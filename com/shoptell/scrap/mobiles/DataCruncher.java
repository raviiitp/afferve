/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.scrap.mobiles;

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.DISABLE_DATA_CRUNCH;
import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;
import static com.shoptell.backoffice.enums.CategoryEnum.SMARTPHONES;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update.Where;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.CountQuery;
import com.shoptell.backoffice.repository.QueryMapper;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogJobEnum;
import com.shoptell.db.processlog.ProcessLogUtil;
import com.shoptell.frontoffice.service.ServiceCall;
import com.shoptell.util.stproperties.STProperties;

@Named(value = "DataCruncher")
public class DataCruncher {
	private static final Logger log = LoggerFactory.getLogger(DataCruncher.class);
	private static final int wait_time = 5000;
	private String keyspace;

	@Inject
	private ProcessLogUtil process_log;
	@Inject
	private STProperties stprop;
	@Inject
	private Environment env;
	@Inject
	private Session session;
	@Inject
	private BatchRepository repository;
	@Inject
	private ServiceCall urlCall;
	@Inject
	private CountQuery countQuery;
	@Inject
	private SelectQuery selectQuery;
	private String subCategoryName = SMARTPHONES.name();

	private ExecutorService pool;
	private static final int MAX_THREAD_COUNT = 3;

	@PostConstruct
	public void start() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		// updateMobileBrands();
		// scrapNewMobiles();
	}

	// @Scheduled(fixedDelayString = "${data.crunch.delay}")
	public void init() {
		if (Boolean.valueOf(stprop.getValueOrDefault(DISABLE_DATA_CRUNCH, "true"))) {
			return;
		}
		ProcessLog plog = process_log.start(ProcessLogJobEnum.ALL.name(), ProcessLogJobEnum.DATA_CRUNCH.name());
		updateMobileBrands();
		scrapNewMobiles();
		process_log.end(plog);
	}

	public void updateMobileBrands() {
		log.debug("updateMobile Start");
		getMobileBrands();
		log.debug("updateMobile End");
	}

	public void scrapNewMobiles() {
		log.debug("scrapNewMobiles Start");
		pool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enable", true);
		ResultSet rows = selectQuery.selectAll(TableEnum.brand_link, map);
		Iterator<BrandLinkDTO> itr = QueryMapper.brandLinkDTO().map(rows).iterator();
		if (itr != null) {
			while (itr.hasNext()) {
				BrandLinkDTO tmp = itr.next();
				if (tmp.getCount() > tmp.getScrapCount()) {
					pool.submit(new Runnable() {
						@Override
						public void run() {
							getMobilePhones(tmp.getLink(), tmp.getBrand(), tmp.getCount() - tmp.getScrapCount(), tmp.getCount());
						}
					});
				}
			}

			log.debug("scrapNewMobiles End");
		}
	}

	public void getMobilePhones(String link, String brand, int count, int total) {
		List<ProductInfoDTO> prod = new LinkedList<ProductInfoDTO>();
		while (true) {
			Document doc = hitURL(link);
			Element body = doc.getElementById("review-body");
			Elements blocks = body.select("li");
			for (Element block : blocks) {
				if (count == 0) {
					break;
				}
				String title = block.select("strong").first().text();
				String url = block.select("a").first().absUrl("href");
				ProductInfoDTO dto = new ProductInfoDTO(brand, title, url, subCategoryName);
				Document phone_body = hitURL(url);
				// if (phone_body != null) {
				Element spec = phone_body.getElementById("specs-list");
				// if (spec != null) {
				Elements list = spec.select("table");
				for (Element table : list) {
					String head = table.select("th").first().text();
					Map<String, String> map = dto.populateMap(head);
					Elements infos = table.select("td");
					for (int i = 0; i < infos.size(); i = i + 2) {
						String key = infos.get(i).text().trim().replace("\u00a0", "");
						String value = infos.get(i + 1).text();
						if (StringUtils.isBlank(key)) {
							key = head + "_" + i;
						}
						if (StringUtils.isNotBlank(value)) {
							map.put(key.toUpperCase(), value.replace("\u00a0", "").toUpperCase().trim());
						}
					}
				}
				// }
				// }
				prod.add(dto);
				--count;

			}

			if (count == 0) {
				break;
			}
			Elements n = doc.getElementsByClass("pages-next-prev");
			if (n == null || n.first() == null)
				break;
			n = n.first().select("a[class=pages-next]");
			if (n == null || n.first() == null)
				break;
			Element next = n.first();
			if (next == null)
				break;
			String nxt = next.attr("class");
			if (nxt.contains("disabled")) {
				break;
			}
			link = next.absUrl("href");
			// System.out.println(link);
		}
		repository.batchSave(prod);
		session.execute(QueryBuilder.update(keyspace, TableEnum.brand_link.name()).with(QueryBuilder.set("scrapCount", total))
				.where(QueryBuilder.eq("subCategoryName", subCategoryName)).and(QueryBuilder.eq("brand", brand)));
	}

	public void getMobileBrands() {
		BatchStatement batch = new BatchStatement();
		List<BrandLinkDTO> list = new LinkedList<BrandLinkDTO>();
		Pattern p = Pattern.compile("((\\w+\\s)+)(phones\\s)\\((\\d+)\\)");
		String link = "http://www.gsmarena.com/makers.php3";
		Document makerDoc = hitURL(link);
		if (makerDoc != null) {
			Element table = makerDoc.select("table").first();
			if (table != null) {
				Elements makers = table.select("td");
				for (int i = 1; i < makers.size(); i = i + 2) {
					Element row = makers.get(i).select("a").first();
					String url = row.absUrl("href");
					String title = row.text();
					Matcher m = p.matcher(title);
					if (m.find()) {
						String name = m.group(1);
						int count = Integer.parseInt(m.group(4));
						// System.out.println(name + ":" + count);
						Where stmt;
						if (StringUtils.isBlank(name)) {
							continue;
						}

						name = name.trim().toUpperCase();
						subCategoryName = subCategoryName.trim();

						if (isExits(subCategoryName, name)) {
							stmt = QueryBuilder.update(keyspace, TableEnum.brand_link.name()).with(QueryBuilder.set("count", count))
									.where(QueryBuilder.eq("subCategoryName", subCategoryName)).and(QueryBuilder.eq("brand", name));
							batch.add(stmt);
							if (batch.size() > BATCHSIZE) {
								session.execute(batch);
								batch.clear();
							}
						}
						else {
							list.add(new BrandLinkDTO(name, subCategoryName, url, count));
						}

					}
				}
			}
		}
		if (batch.size() > 0) {
			session.execute(batch);
			batch.clear();
		}
		if (list.size() > 0) {
			repository.batchSave(list);
			list.clear();
		}
	}

	private boolean isExits(String category, String brand) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("subCategoryName", category);
		map.put("brand", brand);
		long count = countQuery.countAll(TableEnum.brand_link, map);
		if (count == 0) {
			return false;
		}
		return true;
	}

	private Document hitURL(String url) {
		try {
			String baseURI = "http://www.gsmarena.com/";
			Document resp = urlCall.execute(url, baseURI);
			return resp;
		} catch (Exception e) {
			try {
				Thread.sleep(wait_time);
			} catch (InterruptedException e1) {
			}
		}
		return null;
	}
}
