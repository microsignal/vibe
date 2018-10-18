package io.microvibe.booster.core.lang.spel;

import io.microvibe.booster.core.lang.LocalDataBinding;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 使用 Spring Expresion Language 计算表达式
 *
 * @author Qt
 * @since Jul 21, 2018
 */
public class SpringSpels {

	public static Object eval(String expr) {
		//创建解析器
		ExpressionParser parser = new SpelExpressionParser();
		//解析表达式
		Expression expression = parser.parseExpression(expr);
		//构造上下文
		EvaluationContext context = new StandardEvaluationContext();
		((StandardEvaluationContext) context).setVariables(LocalDataBinding.getBindings());
		//打印expression表达式的值
		return expression.getValue(context);
	}

	public static <T> T eval(String expr, Class<T> desiredResultType) {
		//创建解析器
		ExpressionParser parser = new SpelExpressionParser();
		//解析表达式
		Expression expression = parser.parseExpression(expr);
		//构造上下文
		EvaluationContext context = new StandardEvaluationContext();
		((StandardEvaluationContext) context).setVariables(LocalDataBinding.getBindings());
		//打印expression表达式的值
		return expression.getValue(context, desiredResultType);
	}
}
