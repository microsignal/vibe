package io.microvibe.booster.system.toolkit;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.ipsearch.DbConfig;
import io.microvibe.booster.ipsearch.DbMakerConfigException;
import io.microvibe.booster.ipsearch.DbSearcher;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Qt
 * @since Aug 20, 2018
 */
@Slf4j
public class IpSearchers {

	private static DbSearcher ipSearcher;

	private static void init() {
		if (ipSearcher == null) {
			synchronized (IpSearchers.class) {
				if (ipSearcher == null) {
					try {
						ipSearcher = ApplicationContextHolder.getBean(DbSearcher.class);
					} catch (BeansException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	public static Region search(String ip) {
		init();
		Region r = new Region();
		try {
			String s = ipSearcher.memorySearch(ip).getRegion();
			String[] regions = StringUtils.split(s, '|');
			r.setCountry(regions[0]);
			r.setRegion(regions[1]);
			r.setProvince(regions[2]);
			r.setCity(regions[3]);
			r.setNetwork(regions[4]);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return r;
	}

	@Data
	public static class Region {
		private String ip; // IP
		private String country; // 国家
		private String region; // 地区
		private String province; // 省
		private String city; // 市
		private String network; // 网络类型/运营商
	}

	public static void main(String[] args) throws DbMakerConfigException, FileNotFoundException, URISyntaxException {
		ipSearcher = new DbSearcher(new DbConfig(), "/config/ipsearch/ip2region.db");
		System.out.println(search("171.120.11.127"));
	}
}
