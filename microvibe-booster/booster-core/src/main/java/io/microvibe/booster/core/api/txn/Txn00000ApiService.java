package io.microvibe.booster.core.api.txn;

import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ApiService;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import org.springframework.stereotype.Component;

/**
 * 健康测试接口
 *
 * @author Qt
 * @since Oct 09, 2017
 */
@Component
@ApiName({"health"})
public class Txn00000ApiService extends BaseApiService implements ApiService {

	public Txn00000ApiService() {
	}

	@Override
	public ResponseData execute(RequestData request) throws ApiException {
		ResponseData response = request.buildResponse();
		response.setBodyAsString("健康测试通过");
		return response;
	}

}
