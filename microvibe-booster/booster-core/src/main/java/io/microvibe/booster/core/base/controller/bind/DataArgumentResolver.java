package io.microvibe.booster.core.base.controller.bind;

import com.alibaba.fastjson.util.TypeUtils;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.*;
import io.microvibe.booster.core.api.tools.DataKeyExtracter;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.controller.annotation.DataKey;
import io.microvibe.booster.core.base.controller.annotation.DataKeys;
import io.microvibe.booster.core.base.controller.annotation.DataParam;
import io.microvibe.booster.core.base.controller.annotation.DataValid;
import io.microvibe.booster.core.validation.Validations;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorChain;
import com.baidu.unbiz.fluentvalidator.registry.Registry;
import com.baidu.unbiz.fluentvalidator.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

@Slf4j
public class DataArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		return Data.class.isAssignableFrom(type) || BodyModel.class.isAssignableFrom(type);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Class<?> type = parameter.getParameterType();
		DataParam dataParam = parameter.getParameterAnnotation(DataParam.class);
		String paramValue = fetchParamValue(webRequest, parameter.getParameterName(), dataParam);

		if (XmlData.class.isAssignableFrom(parameter.getParameterType())) {
			ApiDataType.current(ApiDataType.XML);
		} else {
			ApiDataType.current(ApiDataType.JSON);
		}

		Data data = DataKit.buildRequestBody(paramValue);

		BodyModel body = data.getBody();

		handlePageParam(body, dataParam);

		// 清理无效查询条件
		cleanUnnecessaryParam(body, parameter);

		boolean cleanEmptyValues = dataParam != null && dataParam.cleanEmptyValues();
		if (cleanEmptyValues) {
			DataModelCleaner.cleanEmptyValues(body);
		}

		// 参数校验
		doValidateParam(body, parameter);

		if (BodyModel.class.isAssignableFrom(type)) {
			return body;
		}
		return data;
	}

	private String fetchParamValue(NativeWebRequest webRequest, String parameterName, DataParam requestParam) {
		boolean required = false;
		String paramName;
		String defaultValue = null;
		if (requestParam != null) {
			paramName = requestParam.value();
			defaultValue = requestParam.defaultValue();
			required = requestParam.required();
		} else {
			paramName = parameterName;
		}
		HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		String paramValue = DataKit.fetchRequestData(servletRequest, paramName, DataParam.DEFAULT_NAME, defaultValue);

		if (required && paramValue == null) {
			throw new ApiException(ReplyCode.RequestBodyEmpty);
		}
		return paramValue;
		/*
		String defaultValue = null;
		boolean required = false;
		String paramValue = null;
		if (requestParam != null) {
			String paramName = requestParam.value();
			defaultValue = requestParam.defaultValue();
			required = requestParam.required();
			paramValue = StringUtils.trimToNull(webRequest.getParameter(paramName));
			log.debug("paramName={}, paramValue={}", paramName, paramValue);
		} else {
			paramValue = StringUtils.trimToNull(webRequest.getParameter(parameterName));
			if (paramValue == null) {
				paramValue = StringUtils.trimToNull(webRequest.getParameter(DataParam.DEFAULT_NAME));
				log.debug("paramName={}, paramValue={}", DataParam.DEFAULT_NAME, paramValue);
			} else {
				log.debug("paramName={}, paramValue={}", parameterName, paramValue);
			}
			defaultValue = DataParam.DEFAULT_VALUE;
		}

		if (paramValue == null) {
			try {
				HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
				ServletInputStream in = servletRequest.getInputStream();
				if (in != null) {
					String content = IOUtils.toString(in, servletRequest.getCharacterEncoding());
					paramValue = StringUtils.trimToNull(content);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		if (paramValue == null) {
			Map<String, String[]> parameterMap = webRequest.getParameterMap();
			if (parameterMap.size() == 1) {
				Map.Entry<String, String[]> uniqueEntry = parameterMap.entrySet().iterator().next();
				String[] uniqueVal = uniqueEntry.getValue();
				if (uniqueVal == null || uniqueVal.length == 0 ||
					uniqueVal.length == 1 && StringUtils.isBlank(uniqueVal[0])) {
					paramValue = StringUtils.trimToNull(uniqueEntry.getKey());
				}
			}
		}

		if (paramValue == null) {
			paramValue = StringUtils.trimToNull(defaultValue);
		}
		if (required && paramValue == null) {
			throw new ApiException(ReplyCode.RequestBodyEmpty);
		}
		return paramValue;
		*/
	}

	private void handlePageParam(BodyModel body, DataParam dataParam) {
		if (dataParam != null && dataParam.pageRequired()) {
			LimitModel limitModel = body.getLimit();
			if (limitModel == null) {
				if (dataParam.defaultPageNumber() >= 0 && dataParam.defaultPageSize() >= 0) {
					limitModel = ModelBuilders.newLimitModel();
					body.setLimit(limitModel);
				} else {
					throw new ApiException(ReplyCode.RequestPageParamEmpty);
				}
			}
			if (!limitModel.hasPageNumber()) {
				limitModel.setPageNumber(dataParam.defaultPageNumber());
			}
			if (!limitModel.hasPageSize()) {
				limitModel.setPageSize(dataParam.defaultPageSize());
			}
		}
	}

	// 清理无效查询条件
	private void cleanUnnecessaryParam(BodyModel body, MethodParameter parameter) {
		// clean unnecessary keys
		DataModelCleaner.cleanKeys(body, getKeyExtracter(parameter));
	}

	private DataKeyExtracter<?> getKeyExtracter(MethodParameter parameter) {
		DataKeyExtracter<?> keyExtracter = null;
		DataKeys dataKeys = parameter.getMethodAnnotation(DataKeys.class);
		if (dataKeys != null) {
			keyExtracter = DataKeyExtracter.config();
			for (DataKey dataKey : dataKeys.value()) {
				keyExtracter.include(dataKey.include());
				for (Class<?> clazz : dataKey.includeAll()) {
					keyExtracter.includeAll(clazz);
				}
				for (Class<?> clazz : dataKey.includeAllUnderlineFormat()) {
					keyExtracter.includeAllUnderlineFormat(clazz);
				}
				keyExtracter.exclude(dataKey.exclude());
				for (Class<?> clazz : dataKey.excludeAll()) {
					keyExtracter.excludeAll(clazz);
				}
				for (Class<?> clazz : dataKey.excludeAllUnderlineFormat()) {
					keyExtracter.excludeAllUnderlineFormat(clazz);
				}
				String[] mappingFrom = dataKey.mappingFrom();
				String[] mappingTo = dataKey.mappingTo();
				int length = Math.min(mappingFrom.length, mappingTo.length);
				for (int i = 0; i < length; i++) {
					keyExtracter.mapping(mappingFrom[i], mappingTo[i]);
				}
				for (Class<?> clazz : dataKey.mappingAllCamelToUnderline()) {
					keyExtracter.mappingAllCamelToUnderline(clazz);
				}
				for (Class<?> clazz : dataKey.mappingAllUnderlineToCamel()) {
					keyExtracter.mappingAllUnderlineToCamel(clazz);
				}
			}
		}
		return keyExtracter;
	}

	private void doValidateParam(BodyModel body, MethodParameter parameter) {
		Set<DataValid> dataValids = AnnotationUtils.getRepeatableAnnotations(parameter.getMethod(), DataValid.class);
		for (DataValid dataValid : dataValids) {
			dataValid.args();
			doValidate(body, dataValid.key(), dataValid.isFailFast(), dataValid.args(), dataValid.message(), dataValid.value());
		}
		/*DataValids dataValids = parameter.getMethodAnnotation(DataValids.class);
		if (dataValids != null) {
			for (DataValid dataValid : dataValids.value()) {
				doValidate(body,dataValid.key() ,dataValid.isFailFast() ,dataValid.value());
			}
		}*/
	}

	private void doValidate(BodyModel body, String key, boolean failFast, String[] args, String message,
		Class<? extends Validator> validatorClass) {
		List<Validator> addOnValidators = CollectionUtil.createArrayList();
		Registry registry = Validations.getRegistry();
		if (args != null && args.length > 0) {
			for (Constructor<?> constructor : validatorClass.getConstructors()) {
				try {
					Class<?>[] parameterTypes = constructor.getParameterTypes();
					if (parameterTypes.length == args.length) {
						Object[] parameters = new Object[parameterTypes.length];
						for (int i = 0; i < parameterTypes.length; i++) {
							parameters[i] = TypeUtils.castToJavaBean(args[i], parameterTypes[i]);
						}
						Object o = constructor.newInstance(parameters);
						addOnValidators.add((Validator) o);
						break;
					}
				} catch (Exception e) {
					log.trace(e.getMessage(), e);
				}
			}
		} else {
			List<?> beans = registry.findByType(validatorClass);
			if (!CollectionUtil.isEmpty(beans)) {
				addOnValidators.add((Validator) beans.get(0));
			}
		}
		ValidatorChain chain = new ValidatorChain();
		chain.setValidators(addOnValidators);

		Object target = body;
		if (StringUtils.isNotBlank(key)) {
			target = body.getByKeyPath(key);
		}
		log.debug("BodyModel: check for key [ {} ]", key);

		Validations.raiseWhenError(
			FluentValidator.checkAll()
				.configure(registry)
				.setIsFailFast(failFast)
				.on(target, chain)
				.doValidate()
				.result(ResultCollectors.toComplex()),
			message
		);
	}

}
