package io.microvibe.booster.core.lang.velocity;


import io.microvibe.booster.commons.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class VelocityTemplate {

	private static final String FILE_ENCODE = "UTF8";

	public static String eval(String templateContent) {
		return eval(VelocityContextLocal.getContext(), templateContent);
	}

	public static String eval(Context context, String templateContent) {
		log.debug("templateContent: {}", templateContent);
		Writer writer = new StringWriter();
		Reader reader = new StringReader(templateContent);
		write(context, writer, reader);
		return writer.toString();
	}

	public static String eval(Map<String, Object> map, String templateContent) {
		Context context = new VelocityContext(map);
		return eval(context, templateContent);
	}

	public static void write(Context context, Writer writer, Reader reader) {
		try {
			Velocity.evaluate(context, writer, "", reader);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public static void writeForPath(Context context, Writer writer, String templatePath) {
		try {
			log.debug("templatePath: {}", templatePath);
			write(context, writer, getTemplateReader(templatePath));
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public static String evalForPath(String templatePath) {
		return evalForPath(VelocityContextLocal.getContext(), templatePath);
	}

	public static String evalForPath(Context context, String templatePath) {
		try {
			log.debug("templatePath: {}", templatePath);
			Writer writer = new StringWriter();
			write(context, writer, getTemplateReader(templatePath));
			return writer.toString();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}


	public static String evalForPath(Map<String, Object> map, String templatePath) {
		Context context = new VelocityContext(map);
		return evalForPath(context, templatePath);
	}

	public static String evalForPath(Object obj, String templatePath) {
		Map<String, Object> map;
		if (obj instanceof Map) {
			map = (Map<String, Object>) obj;
		} else {
			map = new HashMap<String, Object>();
			map.put("one", obj);
		}
		return evalForPath(map, templatePath);
	}

	public static BufferedReader getTemplateReader(String templatePath)
		throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedReader(new InputStreamReader(IOUtils.getInputStream(templatePath), FILE_ENCODE));
	}
}
