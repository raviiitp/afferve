/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.frontoffice.service;

import static com.shoptell.backoffice.enums.CategoryEnum.AIRCONDITIONER;
import static com.shoptell.backoffice.enums.CategoryEnum.ALL;
import static com.shoptell.backoffice.enums.CategoryEnum.BAGS;
import static com.shoptell.backoffice.enums.CategoryEnum.CAMERA;
import static com.shoptell.backoffice.enums.CategoryEnum.DESKTOPS;
import static com.shoptell.backoffice.enums.CategoryEnum.EYEWEARS;
import static com.shoptell.backoffice.enums.CategoryEnum.GAMINGCONSOLES;
import static com.shoptell.backoffice.enums.CategoryEnum.HEADPHONES;
import static com.shoptell.backoffice.enums.CategoryEnum.LAPTOPS;
import static com.shoptell.backoffice.enums.CategoryEnum.LUGGAGEBAGS;
import static com.shoptell.backoffice.enums.CategoryEnum.MICROWAVEOVEN;
import static com.shoptell.backoffice.enums.CategoryEnum.PENDRIVE;
import static com.shoptell.backoffice.enums.CategoryEnum.REFRIGERATOR;
import static com.shoptell.backoffice.enums.CategoryEnum.SMARTPHONES;
import static com.shoptell.backoffice.enums.CategoryEnum.SPEAKERS;
import static com.shoptell.backoffice.enums.CategoryEnum.SUNGLASSES;
import static com.shoptell.backoffice.enums.CategoryEnum.TABLET;
import static com.shoptell.backoffice.enums.CategoryEnum.TELEVISION;
import static com.shoptell.backoffice.enums.CategoryEnum.WASHINGMACHINE;
import static com.shoptell.backoffice.enums.CategoryEnum.WATCHES;
import static com.shoptell.backoffice.enums.CategoryEnum.values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogJobEnum;

@Named(value = "RankUpdateService")
public class RankUpdateService extends Service {

	private final static Logger log = LoggerFactory.getLogger(RankUpdateService.class);
	
	@Inject
	private ServiceCall urlCall;

	private int pages = 1;

	private static Map<String, String> flipkartMap = new HashMap<String, String>();
	private static Map<String, String> snapdealMap = new HashMap<String, String>();

	public void checkout(List<ProcessLog> list, HomeEnum home) throws InterruptedException {
		start();
		process = processUtil.start(home.name(), ProcessLogJobEnum.RANK.name());
		list.add(process);
		if (home.equals(HomeEnum.FLIPKART)) {
			forFlipkart();
		}
		else if (home.equals(HomeEnum.SNAPDEAL)) {
			forSnapdeal();
		}
		list.remove(process);
		processUtil.end(process);
	}

	@Async
	public void init() {
		start();
		try {
			forFlipkart();
			forSnapdeal();
		} catch (InterruptedException e) {
			log.error("", e);
		}
	}

	@PostConstruct
	public void start() {
		flipkartMap.put(SMARTPHONES.name(), "http://www.flipkart.com/mobiles/pr?sid=tyy,4io");
		flipkartMap.put(AIRCONDITIONER.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=j9e,abm,c54");
		flipkartMap.put(TELEVISION.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=ckf,czl");
		flipkartMap.put(LAPTOPS.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=6bo,b5g");
		flipkartMap.put(REFRIGERATOR.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=j9e,abm,hzg");
		flipkartMap.put(WASHINGMACHINE.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=j9e,abm,8qx");
		flipkartMap.put(CAMERA.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=jek,p31,nxa"); /*POINT AND SHOOT*/
		flipkartMap.put(DESKTOPS.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=6bo,igk");
		flipkartMap.put(MICROWAVEOVEN.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?p[]=sort=popularity&sid=j9e,m38,o49");
		flipkartMap.put(WATCHES.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?p[]=sort=popularity&sid=r18,f13");
		flipkartMap.put(EYEWEARS.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?p[]=sort=popularity&sid=u73,h4k");
		flipkartMap.put(PENDRIVE.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?p[]=sort=popularity&sid=6bo,jdy,uar");
		flipkartMap.put(TABLET.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?p[]=sort=popularity&sid=tyy,hry");
		flipkartMap.put(SUNGLASSES.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?p[]=sort=popularity&sid=26x");
		flipkartMap.put(BAGS.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=reh,ihu");
		flipkartMap.put(LUGGAGEBAGS.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=reh,plk");
		flipkartMap.put(HEADPHONES.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=tyy,4mr,fp0");
		flipkartMap.put(SPEAKERS.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=tyy,4mr,5ev");
		flipkartMap.put(GAMINGCONSOLES.name(), "http://www.flipkart.com/lc/pr/pv1/spotList1/spot1/productList?sid=4rr,nqk");
		
		snapdealMap.put(SMARTPHONES.name(), "175");
		snapdealMap.put(AIRCONDITIONER.name(), "230");
		snapdealMap.put(TELEVISION.name(), "64");
		snapdealMap.put(LAPTOPS.name(), "57");
		snapdealMap.put(REFRIGERATOR.name(), "245");
		snapdealMap.put(WASHINGMACHINE.name(), "250");
		snapdealMap.put(CAMERA.name(), "4584"); /*Digital Cameras*/
		/*snapdealMap.put(DESKTOPS.name(), "");*/
		snapdealMap.put(MICROWAVEOVEN.name(), "244");
		snapdealMap.put(WATCHES.name(), "476");
		snapdealMap.put(EYEWEARS.name(), "140");
		snapdealMap.put(PENDRIVE.name(), "52");
		snapdealMap.put(TABLET.name(), "133");
		snapdealMap.put(SUNGLASSES.name(), "15");
		snapdealMap.put(BAGS.name(), "1031");
		snapdealMap.put(LUGGAGEBAGS.name(), "3674");
		snapdealMap.put(HEADPHONES.name(), "46102452");
		snapdealMap.put(SPEAKERS.name(), "288");
		snapdealMap.put(GAMINGCONSOLES.name(), "576");
	}

	public void forFlipkart() throws InterruptedException {
		log.info("forFlipkart() Enter");
		execute(HomeEnum.FLIPKART.name());
		log.info("forFlipkart() Exit");
	}

	public void forSnapdeal() throws InterruptedException {
		log.info("forSnapdeal() Enter");
		execute(HomeEnum.SNAPDEAL.name());
		log.info("forSnapdeal() Exit");
	}

	/*public void get1000RanksforFlipkart() throws InterruptedException {
		setPages(10);
		execute(HomeEnum.FLIPKART.name());
		setPages(1);
	}

	public void get1000RanksforSnapdeal() throws InterruptedException {
		setPages(10);
		execute(HomeEnum.SNAPDEAL.name());
		setPages(1);
	}*/

	private void execute(String home) throws InterruptedException {
		for (CategoryEnum category : values()) {
			if (ALL.equals(category)){
				continue;
			}
			for (int i = 0; i < getPages(); i++) {
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException("KILL THREAD");
				}
				try {
					int startprod = (i * 100) + 1;
					int endprod = (i + 1) * 100;
					Map<String, Integer> map = populateProductRank(home, startprod, endprod, category.name());
					if (map == null || map.size() == 0){
						break;
					}
					repository.updateProductRank(home, map, TableEnum.home_product_info.name(), category.name());
				} catch (Exception e) {
					log.error("UPDATE RANK", e);
					msgLog.addError(e);
				}
			}
		}
	}
	
	

	public Map<String, Integer> populateProductRank(String home, int startprod, int endprod, String category) {
		log.info("populateProductRank() Enter");
		Map<String, Integer> map = new HashMap<String, Integer>();
		int start = startprod;
		int end = endprod;
		String lastUrl = null;
		if (StringUtils.isNotBlank(home)) {
			if (home.equalsIgnoreCase("FLIPKART")) {
				try {
					String url = flipkartMap.get(category);
					if (StringUtils.isNotBlank(url)) {
						do {
							// log.info("FLIPKART : {}", url);
							Document doc = urlCall.execute(url, "http://www.flipkart.com/");
							Elements elements = doc.getElementsByClass("product-unit");
							for (Element element : elements) {
								String pid = element.attr("data-pid");
								map.put(pid, start);
								start++;
							}
							lastUrl = url;
							url = flipkartMap.get(category) + "&start=" + start;
							if (url.equalsIgnoreCase(lastUrl) || map.size() == 0) {
								break;
							}

						} while (start < end);
					}
				} catch (Exception e) {
					msgLog.addError(e);
				}
			}
			else if (home.equalsIgnoreCase("SNAPDEAL")) {
				try {
					--start;
					String id = snapdealMap.get(category);
					if (StringUtils.isNotBlank(id)) {
						// url =
						// "http://www.snapdeal.com/products/mobiles-mobile-phones?sort=plrty";
						String url = "http://www.snapdeal.com/acors/json/product/get/search/" + id + "/" + start
								+ "/20?q=&sort=plrty&keyword=&clickSrc=&viewType=List&lang=en&snr=false";
						do {
							// log.info("SNAPDEAL : {}", url);
							Document doc = urlCall.execute(url, "http://www.snapdeal.com/");
							Elements elements = doc.getElementsByClass("product-tuple-listing");
							for (Element element : elements) {
								String pid = element.attr("id");
								start++;
								map.put(pid, start);
							}
							lastUrl = url;
							url = "http://www.snapdeal.com/acors/json/product/get/search/" + id + "/" + start
									+ "/20?q=&sort=plrty&keyword=&clickSrc=&viewType=List&lang=en&snr=false";
							if (url.equalsIgnoreCase(lastUrl) || map.size() == 0) {
								break;
							}
						} while (start < end);
					}
				} catch (Exception e) {
					msgLog.addError(e);
				}
			}
		}
		log.info("populateProductRank() Exit");
		return map;
	}

	/**
	 * @return the pages
	 */
	public int getPages() {
		return pages;
	}

	/**
	 * @param pages
	 *            the pages to set
	 */
	public void setPages(int pages) {
		this.pages = pages;
	}
}
