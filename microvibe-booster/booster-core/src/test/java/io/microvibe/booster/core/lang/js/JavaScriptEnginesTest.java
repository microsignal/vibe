package io.microvibe.booster.core.lang.js;

import io.microvibe.booster.core.lang.LocalDataBinding;
import org.apache.commons.lang.math.RandomUtils;

import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * @author Qt
 * @since Jul 21, 2018
 */
public class JavaScriptEnginesTest {

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 10; i++) {
			new Thread(()->{
				int a = RandomUtils.nextInt(10);
				int b = RandomUtils.nextInt(10);
				String s = "a + b"+" /* "+ a +" + "+b +" */ ";

				LocalDataBinding.put("a", a);
				LocalDataBinding.put("b", b);

				System.out.printf("[%s] %s -> %s\n",Thread.currentThread().getId(),s,JavaScriptEngines.eval(s));
			}).start();
//			Thread.sleep(1000);
		}

	}
}
