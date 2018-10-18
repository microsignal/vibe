package io.microvibe.booster.core.api.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.*;
import io.microvibe.booster.core.api.tools.DataKeyExtracter;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.controller.bind.DataModelCleaner;
import io.microvibe.booster.core.search.SearchOper;
import org.junit.Test;

import java.util.*;

import static io.microvibe.booster.core.api.support.RequestApiDataParser.current;
import static io.microvibe.booster.core.api.support.RequestApiDataParser.parseJson;

/**
 * @author Qt
 * @since May 28, 2018
 */
public class ApiDataParserTest {
	@Test
	public void test000(){
		RequestApiDataParser parser = null;
		parser = parseJson("{head:{}}");
		System.out.println(parser.getRequestApiData());
		System.out.println(parser.getError());
		System.out.println(parser);

		parser = parseJson("{head:{}}");
		System.out.println(parser.getRequestApiData());
		System.out.println(parser.getError());
		System.out.println(parser);

		Optional<RequestApiDataParser> opt = current();
		opt.orElseThrow(() -> new ApiException(ReplyCode.SysLogicError));
	}

	@Test
	public void test001(){
		JsonData data = JsonData.buildByTxnCode("X0001");
		HeadModel head = data.getHead();
//		head.setCode("X0001");
		head.setMessage("执行成功");
		head.setSuccess(true);

		List<SearchModel> searches = data.getBody().getSearches();
		Map<String, Object> map = new HashMap<>();
		map.put("id__eq",1 );
		searches.add(ModelBuilders.newSearchModel(map));

		System.out.println(data.getBody().getSearch("id", SearchOper.eq));;

		System.out.println(JSONObject.toJSONString(data,true));
	}

	@Test
	public void test002(){
		String paramValue = "{\n" +
//			"    search:[\n" +
//			"        {key:'id',op:'eq',val:'1'},\n" +
//			"        {key:'name',op:'like',val:'xx'},\n" +
//			"    ],\n" +
			"    search:[{" +
			"        id__eq:'1'" +
			"    }],\n" +
			"    sort:[\n" +
			"        {key:'createDate',dir:'asc'},\n" +
			"        {key:'updateDate',dir:'desc'}\n" +
			"    ],\n" +
			"    limit:{\n" +
			"        ps:10,pn:1\n" +
			"    }\n" +
			"}" +
			"";
		JsonData apiData = JsonData.buildByBody(paramValue);
		System.out.println(JSONObject.toJSONString(apiData,true));
		System.out.println(apiData.getBody().getSearches());
		System.out.println(apiData.getBody().getSort());
		System.out.println(apiData.getBody().getSortable());
		System.out.println(apiData.getBody().getSorts());
	}

	@Test
	public void test003() {
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
		System.out.println(JSON.toJSONString(searches,true));
	}


	public static void main(String[] args) {

		DataKit.buildRequest("");
		List list = new ArrayList();
		list.add(JSONObject.parseObject("{id:1}"));
		long total = 100;
		ResponseData responseApiData = DataKit.buildResponse(list, total);
		System.out.println(JSON.toJSONString(responseApiData,true));


	}
}
