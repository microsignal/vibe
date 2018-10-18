package io.microvibe.booster.core.api.controller;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.api.model.*;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.controller.annotation.DataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class HealthControler extends AbstractApiServiceController {

	static final Logger logger = LoggerFactory.getLogger(HealthControler.class);
	@Autowired
	ApplicationContext context;

	@RequestMapping({"/openapi/health", "/openapi/health/json"})
	@ResponseBody
	public void api4json(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doExec(JsonData.buildByTxnCode("health"), request, response);
	}

	@RequestMapping({"/openapi/health/xml"})
	@ResponseBody
	public void api4xml(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doExec(XmlData.buildByTxnCode("health"), request, response);
	}

	// test
	@RequestMapping("/openapi/health/random")
	@ResponseBody
	public void random(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String text = String.valueOf(Math.random());
		PrintWriter writer = resp.getWriter();
		writer.write(text);
		writer.flush();
	}

	@RequestMapping("/openapi/health/test/query")
	@ResponseBody
	public ResponseData testQuery(@DataParam(value = "data", defaultValue = "{}") RequestData reqApiData,
									 HttpServletRequest req,
									 HttpServletResponse resp) throws IOException {
		BodyModel data = reqApiData.getBody();
		List<SearchModel> searches = data.getSearches();
		LimitModel limit = data.getLimit();
		List<SortModel> sorts = data.getSorts();

		logger.info("开始查询...");
		for (SearchModel model : searches) {
			logger.info("查询条件: {} {} {} \n", model.getKey(), model.getOp(), model.getVal());
		}
		if (limit != null) {
			logger.info("分页查询: 当前页码={}, 每页大小={} \n", limit.getPageNumber(), limit.getPageSize());
		}
		for (SortModel sort : sorts) {
			logger.info("按字段排序: order by {} {} \n", sort.getSortKey(),
				Optional.ofNullable(sort.getSortDirection()).orElse(Direction.ASC));
		}
		logger.info("查询结束");

		List<Object> dataList = Arrays.asList(
			JSONObject.parseObject("{id:1,name:'test1',createDate:'2017-10-1'}"), //
			JSONObject.parseObject("{id:2,name:'test2',createDate:'2017-10-1'}"), //
			JSONObject.parseObject("{id:3,name:'test3',createDate:'2017-10-1'}")//
		);
		List<String> sortable = Arrays.asList("id", "name", "createDate");
		int dataTotal = 123;
		ResponseData respApiData = DataKit.buildResponse(dataList, dataTotal, sortable, limit);
        /*
        ResponseData respApiData = DataKit.buildSuccessResponse();
        BodyModel respData = respApiData.getBody();
        respData.setList(dataList);
        respData.setTotal(dataTotal);
        respData.setLimit(limit);
        respData.setSortable(sortable);
        */
		return respApiData;
	}

	@RequestMapping("/openapi/health/test/add")
	@ResponseBody
	public ResponseData testAdd(@DataParam(value = "data", defaultValue = "{}") RequestData reqApiData,
								   HttpServletRequest req,
								   HttpServletResponse resp)
		throws IOException {
		BodyModel data = reqApiData.getBody();
		String name = data.getString("name");
		Integer age = data.getInteger("age");
		String hobby = data.getString("hobby");

		logger.info("创建对象记录: \n\t name: {}\n\t age: {}\n\t hobby: {}\n", name, age, hobby);

		ResponseData respApiData = DataKit.buildSuccessResponse();
		respApiData.setBody("newId", UUID.randomUUID().toString());
		return respApiData;
	}

	@RequestMapping("/openapi/health/test/delete")
	@ResponseBody
	public ResponseData testDelete(@DataParam(value = "data", defaultValue = "{}") RequestData reqApiData,
									  HttpServletRequest req,
									  HttpServletResponse resp)
		throws IOException {
		BodyModel data = reqApiData.getBody();
		String id = data.getString("id");

		logger.info("删除对象记录: id = {}\n", id);
		return DataKit.buildSuccessResponse();
	}

	@RequestMapping("/openapi/health/test/update")
	@ResponseBody
	public ResponseData testUpdate(@DataParam(value = "data", defaultValue = "{}") RequestData reqApiData,
									  HttpServletRequest req,
									  HttpServletResponse resp)
		throws IOException {
		BodyModel data = reqApiData.getBody();
		String id = data.getString("id");
		String name = data.getString("name");
		int age = data.getIntValue("age");
		String hobby = data.getString("hobby");

		logger.info("更新对象记录: id = {}\n", id);
		logger.info("更新对象记录: \n\t name: {}\n\t age: {}\n\t hobby: {}\n", name, age, hobby);

		return DataKit.buildSuccessResponse();
	}

}
