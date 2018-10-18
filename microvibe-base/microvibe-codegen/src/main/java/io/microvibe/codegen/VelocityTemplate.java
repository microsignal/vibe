package io.microvibe.codegen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import io.microvibe.util.io.IOUtil;

public class VelocityTemplate {

	private static final String FILE_ENCODE = "UTF8";

	public static void write(Context context, Writer writer, String template) {
		try {
			write(context, writer, getTemplateReader(template));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static BufferedReader getTemplateReader(String template)
			throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedReader(new InputStreamReader(IOUtil.getInputStream(template), FILE_ENCODE));
	}

	public static void write(Context context, Writer writer, Reader reader) {
		try {
			Velocity.evaluate(context, writer, "", reader);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
