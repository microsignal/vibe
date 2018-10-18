package io.microvibe.booster.boot;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import io.microvibe.booster.core.env.BootConstants;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.ErrorPageFilter;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Map;
import java.util.Set;


@SpringBootApplication
@ComponentScan(BootConstants.BASE_PACKAGE )
@MapperScan(BootConstants.BASE_PACKAGE_MAPPER)
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableCaching
@Slf4j
public class BootstrapApplication extends SpringBootServletInitializer {
	public BootstrapApplication() {
		super.setRegisterErrorPageFilter(false);
	}

	public static void main(String[] args) throws Exception {
		ParserConfig.getGlobalInstance().setAsmEnable(false);
		SerializeConfig.getGlobalInstance().setAsmEnable(false);
		// SpringApplication.run(BootstrapApplication.class, args);

		SpringApplication app = new SpringApplication(BootstrapApplication.class);
		// app.setAdditionalProfiles("default");
		app.setBannerMode(Banner.Mode.OFF);
		app.getSources().remove(ErrorPageFilter.class);
		app.run(args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(BootstrapApplication.class);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		super.setRegisterErrorPageFilter(false);

		Map<String, ? extends ServletRegistration> servletRegistrations
			= servletContext.getServletRegistrations();
		Set<String> keys = servletRegistrations.keySet();
		for (String key : keys) {
			ServletRegistration servletRegistration = servletRegistrations.get(key);
			log.debug("servletRegistration: class={}, mapping={}", servletRegistration.getClassName(),
				servletRegistration.getMappings());
		}
	}

}
