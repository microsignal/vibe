package io.microvibe.booster.commons.config;

import org.junit.Ignore;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

import java.io.InputStreamReader;

/**
 * @author Qt
 * @since Aug 03, 2018
 */
@Ignore
public class YamlTest {

	public static void main(String[] args) {
		Yaml yaml = new Yaml();
		Node o = yaml.compose(new InputStreamReader(YamlTest.class.getResourceAsStream("test.yaml")));
		System.out.println(o);
		System.out.println(o.getNodeId());
		System.out.println(o.getTag());
		System.out.println(o.getStartMark());
		System.out.println(o.getEndMark());
	}
}
