package io.microvibe.booster.system.restful;


import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.entity.Entryable;
import io.microvibe.booster.core.env.BootConstants;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 供前端接口调用的通用的数据字典接口
 *
 * @author Qt
 * @version 1.0.1
 * @since Feb 11, 2018
 */
@RestController
@RequestMapping(value = "/openapi/entry", method = RequestMethod.GET)
public class OpenEntryableController {


	public OpenEntryableController() {
		scanEntryable();
	}

	@GetMapping
	public ResponseData list(@RequestParam(name = "type", required = false) String type,
		@RequestParam(name = "types", required = false) String[] types) {
		ResponseData responseApiData = DataKit.buildSuccessResponse();
		if (type != null) {
			Map<String, String> map = Entryable.entries(type);
			List<Payload> list = new ArrayList<Payload>(map.size());
			if (map != null) {
				Set<Map.Entry<String, String>> entries = map.entrySet();
				for (Map.Entry<String, String> entry : entries) {
					list.add(new Payload(entry.getKey(), entry.getValue()));
				}
			}
			responseApiData.setBody("list", list);
		} else if (types != null) {
			for (int i = 0; i < types.length; i++) {
				String iType = types[i];
				Map<String, String> map = Entryable.entries(iType);
				if (map != null) {
					List<Payload> list = new ArrayList<Payload>(map.size());
					Set<Map.Entry<String, String>> entries = map.entrySet();
					for (Map.Entry<String, String> entry : entries) {
						list.add(new Payload(entry.getKey(), entry.getValue()));
					}
					responseApiData.setBody(iType, list);
				}
			}
		}
		return responseApiData;
	}

	@GetMapping("/map")
	public ResponseData map(@RequestParam(name = "type", required = false) String type,
		@RequestParam(name = "types", required = false) String[] types) {
		ResponseData responseApiData = DataKit.buildSuccessResponse();
		if (type != null) {
			Map<String, String> map = Entryable.entries(type);
			responseApiData.setBody("map", map);
		} else if (types != null) {
			for (int i = 0; i < types.length; i++) {
				String iType = types[i];
				Map<String, String> map = Entryable.entries(iType);
				if (map != null) {
					responseApiData.setBody(iType, map);
				}
			}
		}
		return responseApiData;
	}

	private void scanEntryable() {
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
				+ ClassUtils.convertClassNameToResourcePath(BootConstants.BASE_PACKAGE) + "/**/enums/*.class";
			Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

			AssignableTypeFilter typeFilter = new AssignableTypeFilter(Entryable.class);

			for (Resource resource : resources) {
				if (resource.isReadable()) {
					MetadataReader reader = readerFactory.getMetadataReader(resource);
					String className = reader.getClassMetadata().getClassName();

					readClass(readerFactory, typeFilter, reader, className);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readClass(MetadataReaderFactory readerFactory, AssignableTypeFilter typeFilter, MetadataReader reader, String className) throws IOException {
		try {
			if (typeFilter.match(reader, readerFactory)) {
				Class<?> clazz = Class.forName(className);
				if (clazz.isEnum()) {
					Method method = clazz.getMethod("values");
					Object values = method.invoke(null);
					int len = Array.getLength(values);
					for (int i = 0; i < len; i++) {
						Entryable entryable = (Entryable) Array.get(values, i);
						entryable.register();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Data
	@RequiredArgsConstructor
	public static class Payload {
		final String name;
		final String label;
	}
}
