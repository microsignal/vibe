package io.microvibe.booster.core.env;

import io.microvibe.booster.core.log.Log;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SystemEnv {
	@Value("${system.developMode:false}")
	private boolean developMode;

	@Value("${system.resubmit.minInterval:500}")
	private long resubmitMinInterval;
	@Value("${system.resubmit.methods:POST,PUT,DELETE_BY_ID}")
	private String resubmitMethods;
	@Value("${system.resubmit.enabled:true}")
	private boolean resubmitEnabled;
	@Value("${system.resubmit.whiteList:/demo/**}")
	private String resubmitWhiteList;
	@Value("${system.resubmit.uriList:/**}")
	private String resubmitUriList;
	@Value(("${system.resources.autoscan:true}"))
	private boolean resourcesAutoScan;

	@Value("#{'${system.methodlog.level:}'?:'INFO'}")
	private Log.Level methodLogLevel = Log.Level.INFO;

	@Value("#{'${system.methodlog.service:}'?:''}")
	private String methodLogService;

}
