package io.microvibe.booster.core.base;

import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlContext;
import org.apache.ibatis.ognl.OgnlException;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnoTest {


	@AttributeOverrides(
		@AttributeOverride(name = "id",column = @Column(name = "o_id"))
	)
	public static class A {

	}

	public static void main(String[] args) throws OgnlException {
		System.out.println(AnnotationUtils.findAnnotation(A.class, AttributeOverride.class));

		OgnlContext context = new OgnlContext();

		Map example = new HashMap();
		List criteria = new ArrayList();
		criteria.add("123");
		example.put("criteria",criteria);


		context.setRoot(example);


		Object ognl = Ognl.parseExpression("['criteria']['size1']");
		Object value = Ognl.getValue(ognl, context, context.getRoot());
		System.out.println(value.getClass());
		System.out.println(value);

	}
}
