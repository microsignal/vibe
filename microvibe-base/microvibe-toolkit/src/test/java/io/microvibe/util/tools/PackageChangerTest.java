package io.microvibe.util.tools;

import java.io.File;
import java.io.IOException;

public class PackageChangerTest {

	public static void main(String[] args) throws IOException {
		PackageChanger pc = new PackageChanger(
				new File("./src/main/java"),
				new File("./target/java"));
		pc.setCopyForcedly(true);
		pc.addPackageChangeMapping(new PackageChangeMapping("io.github", "io.gitee"));
		pc.doChange();
	}
}
