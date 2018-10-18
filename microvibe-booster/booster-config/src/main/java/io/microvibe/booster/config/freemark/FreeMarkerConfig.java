package io.microvibe.booster.config.freemark;

import io.microvibe.booster.commons.spring.AfterApplicationContextHolder;
import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.commons.spring.MessageResources;
import com.jagregory.shiro.freemarker.ShiroTags;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("ALL")
@Configuration
@Component
@ConditionalOnClass(freemarker.template.Configuration.class)
@ConditionalOnBean(freemarker.template.Configuration.class)
@AfterApplicationContextHolder
public class FreeMarkerConfig implements InitializingBean {

	@Autowired
	private ApplicationContext context;
	@Autowired
	private freemarker.template.Configuration configuration;

	@Override
	public void afterPropertiesSet() throws Exception {
		configuration.setSharedVariable("shiro", new ShiroTags());
		configuration.setSharedVariable("env", new EnvironmentFetcher());
		configuration.setSharedVariable("msg", new MessageFetcher());
	}

	public static class EnvironmentFetcher implements TemplateMethodModelEx {

		@Override
		public Object exec(List arguments) throws TemplateModelException {
			Iterator iter = arguments.iterator();
			String key = iter.next().toString();
			String defVal = key;
			if (iter.hasNext()) {
				defVal = iter.next().toString();
			}
			return ApplicationContextHolder.getApplicationContext().getEnvironment().getProperty(key, defVal);
		}
	}

	public static class MessageFetcher implements TemplateMethodModelEx {

		@Override
		public Object exec(List arguments) throws TemplateModelException {
			Iterator iter = arguments.iterator();
			String code = iter.next().toString();
			Object[] args = new Object[arguments.size() - 1];
			for (int i = 0; i < args.length; i++) {
				args[i] = iter.next();
			}
			return MessageResources.getMessage(code, args);
		}
	}
}
