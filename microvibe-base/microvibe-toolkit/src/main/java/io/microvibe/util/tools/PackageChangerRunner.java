package io.microvibe.util.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class PackageChangerRunner {

	public static void main(String[] args) throws IOException, DocumentException {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}

		File file = new File(args[0]);
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		Element root = doc.getRootElement();
		List<Element> changes = root.elements("change");

		Charset charset;
		try {
			charset = Charset.forName(root.elementTextTrim("charset"));
		} catch (Exception e) {
			charset = Charset.defaultCharset();
		}

		for (Element change : changes) {
			Element dir = change.element("dir");
			String src = dir.attributeValue("src");
			String target = dir.attributeValue("target");
			PackageChanger pc = new PackageChanger(new File(src), new File(target));

			pc.setCharset(charset);

			boolean copyForcedly = Boolean.valueOf(change.elementTextTrim("copyForcedly"));
			pc.setCopyForcedly(copyForcedly);

			String fileExtensions = change.elementTextTrim("fileExtensions");
			if (fileExtensions != null && !fileExtensions.equals("")) {
				String[] arr = fileExtensions.split("[,;|\\s]+");
				pc.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						String name = pathname.getName();
						for (String s : arr) {
							if (name.endsWith(s)) {
								return true;
							}
						}
						return false;
					}
				});
			}

			String charsetName = change.elementTextTrim("charset");
			if (charsetName != null && !charsetName.equals("")) {
				pc.setCharset(Charset.forName(charsetName));
			}

			List<Element> pkgs = change.elements("package");
			for (Element pkg : pkgs) {
				pc.addPackageChangeMapping(new PackageChangeMapping(
						pkg.attributeValue("name"),
						pkg.attributeValue("mapping")));
			}
			pc.doChange();
		}

	}
}
