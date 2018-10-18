package io.microvibe.booster.core.lang.groovy;

import io.microvibe.booster.core.lang.LocalDataBinding;
import io.microvibe.booster.core.lang.js.JavaScriptEngines;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

/**
 * @author Qt
 * @since Jul 21, 2018
 */
public class GroovyEnginesTest {

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				int a = 1 + RandomUtils.nextInt(10);
				int b = 1 + RandomUtils.nextInt(10);
				String s = "a / b" + " /* " + a + " / " + b + " */ ";

				LocalDataBinding.put("a", a);
				LocalDataBinding.put("b", b);

				System.out.printf("[%s] %s -> %s [%s]\n", Thread.currentThread().getId(),
					s, GroovyEngines.eval(s), GroovyEngines.eval(s).getClass());
			}).start();
//			Thread.sleep(1000);
		}
	}


	@Test
	public void testGroovy() {
		for (int i = 0; i < 1000; i++) {
			int a = 1 + RandomUtils.nextInt(10);
			int b = 1 + RandomUtils.nextInt(10);
			String s = "a / b" + " /* " + a + " / " + b + " */ ";
			LocalDataBinding.put("a", a);
			LocalDataBinding.put("b", b);
			GroovyEngines.eval(s);
		}
	}

	@Test
	public void testJavascript() {
		for (int i = 0; i < 1000; i++) {
			int a = 1 + RandomUtils.nextInt(10);
			int b = 1 + RandomUtils.nextInt(10);
			String s = "a / b" + " /* " + a + " / " + b + " */ ";
			LocalDataBinding.put("a", a);
			LocalDataBinding.put("b", b);
			JavaScriptEngines.eval(s);
		}
	}
}
