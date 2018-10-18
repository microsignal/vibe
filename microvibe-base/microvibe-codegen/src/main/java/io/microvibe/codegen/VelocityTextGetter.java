package io.microvibe.codegen;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

public class VelocityTextGetter {
	static final ThreadLocal<VelocityEngine> local = new ThreadLocal<VelocityEngine>();

	static void init() throws Exception {
		if (local.get() == null) {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(Velocity.RESOURCE_LOADER, "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init();
			local.set(ve);
		}
	}

	public static String get(String path, VelocityContext context) throws Exception {
		return get(path, "utf8", context);
	}

	public static String get(String path, String encoding, VelocityContext context) throws Exception {
		Writer writer = new StringWriter();
		write(path, encoding, context, writer);
		return writer.toString();
	}

	public static void write(String path, String encoding, VelocityContext context, Writer writer)
			throws Exception {
		init();
		local.get().mergeTemplate(path, encoding, context, writer);
	}
}
