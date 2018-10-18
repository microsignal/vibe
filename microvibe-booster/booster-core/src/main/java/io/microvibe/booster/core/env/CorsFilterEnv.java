package io.microvibe.booster.core.env;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CorsFilterEnv {
	@Value("${cors.allowed.origins:*}")
	String allowOrigin;
	@Value("${cors.allowed.methods:GET,POST,PUT,DELETE_BY_ID}")
	String allowMethod;
	@Value("${cors.allowed.headers:Origin,Accept,X-Requested-With,Content-Type,Authorization,Access-Token,Access-Ajax,Referer,Set-Cookie,Cookie}")
	String allowHeaders;
	@Value("${cors.exposed.headers:Access-Token, Access-Ajax}")
	String exposedHeaders;
	@Value("${cors.support.credentials:true}")
	boolean supportCredentials;
	@Value("${cors.preflight.maxage:7200}")
	int preflightMaxage;

	@Value("${cors.request.decorate:false}")
	boolean requestDecorate;
}
