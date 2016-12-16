/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.W3CDateFormat;
import com.redfin.sitemapgenerator.W3CDateFormat.Pattern;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.MetaCategoryEnum;
import com.shoptell.backoffice.enums.TypesEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.dto.MergedProductInfoDTO;

@Named(value = "SitemapGenerator")
public class SitemapGenerator {
	private static final Logger log = LoggerFactory.getLogger(SitemapGenerator.class);
	private static final String BASE_URL = "https://www.afferve.com";
	private static final String BASE_DIR = "/usr/local/apache-tomcat-8.0.28/webapps/ROOT";
	//private static final String BASE_DIR = "/Users/abhishekagarwal/ok";
	@Inject
	private BatchRepository repository;
	
	@Scheduled(initialDelayString="${sitemap.initial.delay}",fixedDelayString="${sitemap.fixed.delay}")
	public void start(){
		log.info("start() Enter");
		init();
		log.info("start() Exit");
	}

	@Async
	public void init() {
		log.info("init() Enter");
		execute();
		log.info("init() Exit");
	}

	public void execute() {
		log.info("execute() Enter");
		//URI uri;
		try {
			//uri = SitemapGenerator.class.getClassLoader().getResource("sitemap.xml").toURI();
			//File dir = new File(uri).getParentFile();
			File dir = new File(BASE_DIR);
			log.info("Path to sitemap.xml - {}", dir.toString());
			if (dir.isDirectory()) {

				W3CDateFormat dateFormat = new W3CDateFormat(Pattern.DAY);
				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

				WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.afferve.com", dir).dateFormat(dateFormat).autoValidate(true).build();

				wsg.addUrl(new WebSitemapUrl.Options(BASE_URL).lastMod(new Date()).priority(1.0).changeFreq(ChangeFreq.ALWAYS).build());
				
				addBasePages(wsg);

				wsg.write();
				
				wsg.writeSitemapsWithIndex();
			}
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		log.info("execute() Exit");
	}

	private void addBasePages(WebSitemapGenerator wsg) throws MalformedURLException {
		log.info("addBasePages() Enter");
		for (String home : new String[] { "about-us", "contact-us", "how-it-works", "privacy", "tnc" }) {
			wsg.addUrl(generateUrl(0.7, null, "/" + home));
		}

		for (HomeEnum home : HomeEnum.values()) {
			wsg.addUrl(generateUrl(0.0, null, "/partners/coupons/" + home.name().toLowerCase()));
		}

		for (TypesEnum type : TypesEnum.values()) {
			String types = type.getName().toLowerCase();
			if (TypesEnum.ALL.equals(type)){
				types = "";
			}
			wsg.addUrl(generateUrl(0.0, null, "/partners/coupons/" + types));
		}
		
		for (MetaCategoryEnum meta : MetaCategoryEnum.values()) {
			if (MetaCategoryEnum.NONE.equals(meta)) {
				continue;
			}
			String val = meta.getName();
			wsg.addUrl(generateUrl(0.0, null, "/" + val));
		}

		for (CategoryEnum cat : CategoryEnum.values()) {
			if (CategoryEnum.ALL.equals(cat)) {
				continue;
			}
			String val = cat.getSeoName();
			String meta = cat.getMeta().getName();
			wsg.addUrl(generateUrl(0.9, null, "/" + meta + "/" + val));
		}

		Iterator<MergedProductInfoDTO> products = repository.getMergedProductData();
		while (products.hasNext()) {
			MergedProductInfoDTO data = products.next();
			String name = BackofficeUtil.formatName(data.getName(), data.getProductBrand());
			String category = CategoryEnum.valueOf(data.getSubCategoryName()).getSeoName();
			String meta = MetaCategoryEnum.valueOf(data.getMetaCategory()).getName();
			if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(category) && StringUtils.isNotBlank(meta)) {
					wsg.addUrl(generateUrl(0.9, null, "/" + meta + "/" + category + "/"+name));
			}
		}
		log.info("addBasePages() Exit");
	}

	private WebSitemapUrl generateUrl(Double p, ChangeFreq f, String link) throws MalformedURLException {
		if (StringUtils.isBlank(link)) {
			return null;
		}
		else {
			link = link.replace(" ", "%20").replace("+", "%20");
			link = link.replace("&amp;", "&");
			link = link.replace("&", "&amp;");
		}
		if (p == 0.0) {
			p = 1.0;
		}
		if (f == null) {
			f = ChangeFreq.ALWAYS;
		}
		WebSitemapUrl url = new WebSitemapUrl.Options(BASE_URL + link).lastMod(new Date()).priority(p).changeFreq(f).build();
		return url;
	}
}
