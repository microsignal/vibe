package io.microvibe.util.tools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.mockito.internal.util.collections.Sets;

public class ChPkg4Dzwl {
	/*
	public static void main(String[] args) throws IOException {
		PackageChanger pc = new PackageChanger(
				new File("D:\\xcode\\co\\dzwl\\code\\booster-test\\src\\main\\java"),
				new File("D:\\xcode\\co\\dzwl\\code\\booster-test\\src\\main\\java"));
		pc.setCopyForcedly(true);
		pc.addPackageChangeMapping(new PackageChangeMapping("com.transnal", "com.jxwy.dzwl"));
		pc.doChange();
	}
	*/

	public static void main(String[] args) {
		// @formatter:off
		String [] projs = {
				"booster-app-jar              ",
				"booster-commons               ",
				"booster-config                ",
				"booster-core                  ",
				"booster-core-autoconfigure    ",
				"booster-sys                   ",
				"booster-test                  ",
		};
		Arrays.asList(projs).forEach(s->{
			try {
				s = s.trim();
				PackageChanger pc = new PackageChanger(
						new File("D:\\xcode\\ae\\ae-booster\\"+s+"\\src\\main\\java"),
						new File("D:\\xcode\\ae\\ae-booster\\"+s+"\\src\\main\\java"));
				pc.setCopyForcedly(true);
				pc.addPackageChangeMapping(new PackageChangeMapping("com.jxwy.dzwl", "com.antengine"));
				pc.doChange();
			} catch (IOException e) {
			}
		});
		// @formatter:on
	}

}
