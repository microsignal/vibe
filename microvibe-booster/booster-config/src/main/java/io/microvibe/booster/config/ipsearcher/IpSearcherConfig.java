package io.microvibe.booster.config.ipsearcher;

import io.microvibe.booster.ipsearch.DbConfig;
import io.microvibe.booster.ipsearch.DbMakerConfigException;
import io.microvibe.booster.ipsearch.DbSearcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

/**
 * @author Qt
 * @since Aug 20, 2018
 */
@Configuration
@Component
public class IpSearcherConfig {
	@Value("${system.ipsearch.dbFile:/config/ipsearch/ip2region.db}")
	private String dbFilePath;

	@Bean
	public DbConfig dbConfig() throws DbMakerConfigException {
		return new DbConfig();
	}

	@Bean
	public DbSearcher dbSearcher() throws FileNotFoundException, URISyntaxException, DbMakerConfigException {
		try {
			return new DbSearcher(dbConfig(), dbFilePath);
		} catch (Exception e) {
			return Mockito.mock(DbSearcher.class);
		}
	}

}
