package io.microvibe.booster.core.lang.freemarker;

import io.microvibe.booster.commons.utils.IOUtils;
import io.microvibe.booster.core.lang.LocalDataBinding;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class FreemarkerTemplate {
	private static final String FILE_ENCODE = "UTF8";

	private static FreemarkerTemplate instance = new FreemarkerTemplate();
	private final Configuration cfg;
	private final StringTemplateLoader stringTemplateLoader;

	private FreemarkerTemplate() {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
		StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
		MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(
			new TemplateLoader[]{
				new ClassTemplateLoader(FreemarkerTemplate.class, ""),
				new ClassTemplateLoader(Thread.currentThread().getContextClassLoader(), ""),
				stringTemplateLoader,
			});
		cfg.setTemplateLoader(multiTemplateLoader);
		cfg.setDefaultEncoding(FILE_ENCODE);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		this.cfg = cfg;
		this.stringTemplateLoader = stringTemplateLoader;
	}

	public static FreemarkerTemplate instance() {
		return instance;
	}


	public String eval(String templateContent) {
		return eval(LocalDataBinding.getBindings(), templateContent);
	}

	public String eval(Map<String, Object> context, String templateContent) {
		log.debug("templateContent: {}", templateContent);
		String id = UUID.randomUUID().toString();
		try {
			stringTemplateLoader.putTemplate(id, templateContent);
			Template temp = cfg.getTemplate(id);
			Writer writer = new StringWriter();
			temp.process(context, writer);
			return writer.toString();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			stringTemplateLoader.removeTemplate(id);
			try {
				cfg.removeTemplateFromCache(id);
			} catch (IOException e) {
			}
		}
	}

	public void write(Map<String, Object> context, Writer writer, Reader reader) {
		String id = UUID.randomUUID().toString();
		try {
			stringTemplateLoader.putTemplate(id, IOUtils.toString(reader));
			Template temp = cfg.getTemplate(id);
			temp.process(context, writer);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			stringTemplateLoader.removeTemplate(id);
			try {
				cfg.removeTemplateFromCache(id);
			} catch (IOException e) {
			}
		}
	}

	public void writeForPath(Map<String, Object> context, Writer writer, String templatePath) {
		try {
			Template temp = cfg.getTemplate(templatePath);
			temp.process(context, writer);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String evalForPath(String templatePath) {
		return evalForPath(LocalDataBinding.getBindings(), templatePath);
	}

	public String evalForPath(Map<String, Object> context, String templatePath) {
		log.debug("templatePath: {}", templatePath);
		Writer writer = new StringWriter();
		writeForPath(context, writer, templatePath);
		return writer.toString();
	}


	public String evalForPath(Object obj, String templatePath) {
		Map<String, Object> map;
		if (obj instanceof Map) {
			map = (Map<String, Object>) obj;
		} else {
			map = new HashMap<String, Object>();
			map.put("one", obj);
		}
		return evalForPath(map, templatePath);
	}

}
