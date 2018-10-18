package io.microvibe.booster.core.api;

import com.alibaba.fastjson.JSON;
import io.microvibe.booster.core.api.model.JsonData;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.SearchModel;
import io.microvibe.booster.core.api.model.XmlData;
import io.microvibe.booster.core.api.tools.DataKeyExtracter;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.controller.bind.DataModelCleaner;
import io.microvibe.booster.core.base.search.builder.QL;
import io.microvibe.booster.core.base.search.builder.QLStatement;
import lombok.Data;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;

import java.util.List;

public class ApiTest {

	@Test
	public void test000() {
		SQL sql = new SQL() {{
			SELECT("id,name").FROM("tab")
				.AND().OR()
				.WHERE("id = 1")
				.WHERE("id = 1")
				.OR()
				.WHERE("name like xx")
				.WHERE("name like xx")
				.AND()
				.WHERE("name like xx")
			;
		}};
		System.out.println(sql.toString());

		QL.FROM("table", "t");
		QL.WHERE("name like 'xxx%'");
		QL.AND().OR().WHERE("id = 1").WHERE("id = 2").WHERE("id = 3").END().OR().WHERE("id = 4").END()
			.END();
		QLStatement ql = QL.build();
		System.out.println(ql.statement());
		System.out.println(ql.bindedValues());
	}

	@Test
	public void test001() {
		JsonData apiData = JsonData.buildByBody("{id:1,name:'test'," +
			"mainAddr:{addr:'xx-xx-xx'}," +
			"addrList:[" +
			"{addr:'001'}," +
			"{addr:'002'}" +
			"]" +
			"}");

		UserModel model = apiData.getBody(UserModel.class);
		System.out.println(model);
		System.out.println(JSON.toJSONString(model, true));
		System.out.println(apiData);
		System.out.println(XmlData.copy(apiData));
	}

	@Test
	public void test002() {
		JsonData apiData = JsonData.buildByBody("{" +
			"search: [" +
			"{'id__eq':'001'}," +
			"{key:'name',op:'eq',val:'jack'}," +
			"{" +
			"  conj:'or'," +
			"  search:[" +
			"    {key:'addr',op:'eq',val:'001'}," +
			"    {key:'addr',op:'eq',val:'002'}," +
			"    {" +
			"       conj:'and'," +
			"       search:[" +
			"         {age__lt:'50'}," +
			"         {age__gt:'20'}" +
			"       ]" +
			"    }" +
			"  ]" +
			"}" +
			"]" +
			"}");

		System.out.println(JSON.toJSONString(apiData.getBody(), true));
		List<SearchModel> searches = apiData.getBody().getSearches();
		for (SearchModel search : searches) {
			System.out.println(JSON.toJSONString(search, true));
		}
		System.out.println(JSON.toJSONString(apiData.getBody(), true));

		DataKeyExtracter<?> config = DataKeyExtracter.config()
			.exclude("age");
		DataModelCleaner.cleanUnnecessarySearchKeys(config, searches);
		System.out.println(JSON.toJSONString(searches, true));
	}

	@Test
	public void test003() {
		String json = "{" +
			"search:[" +
			"{age__eq:123}" +
			"{id__eq:123}" +
			"{user_name:'admin'}" +
			"]," +
			"sort:[" +
			"{id:'asc'}" +
			"{age:'asc'}" +
			"{user_name:'asc'}" +
			"]" +
			"}";
		RequestData requestData = DataKit.buildRequestBody(json);

		System.out.println(requestData.toPrettyString());
		DataKeyExtracter<?> config = DataKeyExtracter.config()
			.exclude("age")
			.mapping("user_name", "name");
		DataModelCleaner.cleanKeys(requestData.getBody(), config);
		System.out.println(requestData.toPrettyString());
	}

	@Data
	public static class UserModel {
		private String id;
		private String name;
		private UserAddrModel mainAddr;
		private List<UserAddrModel> addrList;
	}

	@Data
	public static class UserAddrModel {
		private String addr;
	}
}
