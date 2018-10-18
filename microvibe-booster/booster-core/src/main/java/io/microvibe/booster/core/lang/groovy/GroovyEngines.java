package io.microvibe.booster.core.lang.groovy;

import io.microvibe.booster.commons.err.MessageException;
import io.microvibe.booster.core.lang.LocalDataBinding;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author Qt
 * @since Jul 21, 2018
 */
public class GroovyEngines {

	static ScriptEngineManager manager = new ScriptEngineManager();
	static ScriptEngine scriptEngine = manager.getEngineByName("groovy");

	public static Object eval(String script) {
		try {
			Bindings bindings = scriptEngine.createBindings();
			bindings.putAll(LocalDataBinding.getBindings());
			return scriptEngine.eval(script, bindings);
		} catch (ScriptException e) {
			throw new MessageException(e, "groovy.error", new Object[]{e.getMessage()});
		}
	}
}
