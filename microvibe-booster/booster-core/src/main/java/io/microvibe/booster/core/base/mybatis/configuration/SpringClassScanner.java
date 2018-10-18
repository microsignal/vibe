package io.microvibe.booster.core.base.mybatis.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SpringClassScanner {
	/**
	 * the file type to scan : .class
	 */
	public static final String RESOURCE_PATTERN = "**/*.class";

	/**
	 * packages to scan
	 */
	@Getter
	private Set<String> scanPackages;

	/**
	 * more filters with relation default and
	 */
	@Getter
	private Set<TypeFilter> typeFilters;

	/**
	 * filterAll or filterWhether
	 */
	@Getter
	@Setter
	private boolean filterAll = true;

	private SpringClassScanner() {
		scanPackages = new HashSet<String>();
		typeFilters = new HashSet<TypeFilter>();
	}

	public Set<Class<?>> scan() throws ClassNotFoundException, IOException {
		Set<Class<?>> classSet = new HashSet<>();
		if (!this.scanPackages.isEmpty()) {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
			for (String pkg : this.scanPackages) {
				String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(pkg)) + "/"
					+ RESOURCE_PATTERN;
				Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

				for (Resource resource : resources) {
					if (resource.isReadable()) {
						MetadataReader reader = readerFactory.getMetadataReader(resource);
						String className = reader.getClassMetadata().getClassName();

						if (matched(reader, readerFactory)) {
							classSet.add(Class.forName(className));
						}
					}
				}
			}
		}
		return classSet;
	}

	private boolean matched(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {

		if (filterAll) {
			return filterAll(reader, readerFactory);
		} else {
			return filterWhether(reader, readerFactory);
		}
	}

	/**
	 * must to be every one of filters is matched,return true
	 */
	private boolean filterAll(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
		if (!this.typeFilters.isEmpty()) {
			for (TypeFilter filter : this.typeFilters) {
				if (!filter.match(reader, readerFactory)) {
					return false;
				}
			}
			return true;
		}
		return true;
	}

	/**
	 * if any one of filters is matched,return true
	 */
	private boolean filterWhether(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
		if (!this.typeFilters.isEmpty()) {
			for (TypeFilter filter : this.typeFilters) {
				if (filter.match(reader, readerFactory)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public static class Builder {

		private SpringClassScanner scanner = new SpringClassScanner();

		public Builder scanPackage(String scanPackage) {
			this.scanner.getScanPackages().add(scanPackage);
			return this;
		}

		public Builder typeFilter(TypeFilter typeFilter) {
			this.scanner.getTypeFilters().add(typeFilter);
			return this;
		}

		public Builder filterAll(boolean filterAll) {
			this.scanner.setFilterAll(filterAll);
			return this;
		}

		public SpringClassScanner build() {
			return this.scanner;
		}
	}
}
