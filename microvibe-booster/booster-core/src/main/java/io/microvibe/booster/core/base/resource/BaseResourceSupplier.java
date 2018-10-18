package io.microvibe.booster.core.base.resource;

import io.microvibe.booster.core.base.resource.annotation.ResourceIdentity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class BaseResourceSupplier implements ResourceSupplier {

	@Override
	public Collection<ScannedResource> supply() {
		Collection<ScannedResource> colls = new ArrayList<>();
		exec(colls);
		return colls;
	}

	protected void exec(Collection<ScannedResource> colls) {
		try {
			MultiInputStream in = MultiInputStream.openClasspathResources("classpath*:BaseResources.properties");
			Properties prop = new Properties();
			prop.load(in);
			Set<Object> keyset = prop.keySet();
			for (Iterator<Object> iter = keyset.iterator(); iter.hasNext(); ) {
				String name = iter.next().toString();
				String description = prop.getProperty(name);
				int i = name.lastIndexOf(ResourceIdentity.SEPARATOR);
				String parent = "";
				if (i > 0) {
					parent = name.substring(0, i);
				}
				ScannedResource res = ScannedResource.create(name).parentIdentity(parent).description(description);
				colls.add(res);
			}
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}
}
