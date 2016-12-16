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

import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.utils.UUIDs;
import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.dto.UserTransactionDTO;

/**
 * @author abhishekagarwal
 *
 */

@Named
public class URLRedirector {

	private static final Logger log = LoggerFactory.getLogger(URLRedirector.class);

	private static AtomicInteger counter = new AtomicInteger(1);

	private static int amazon_track_size = 100;

	@Inject
	private URLShortener urlShort;

	@Inject
	private ActivityService activity;

	@Inject
	private Session session;

	@Inject
	private Environment env;

	@Inject
	private BatchRepository repo;

	private String keyspace;

	private String FLIPKART_TRACK_ID;

	private String SNAPDEAL_TRACK_ID;

	private String AMAZON_TRACK_ID;
	
	private static final String EXAMPER_TRACK_ID = "afferve";

	//private String INFIBEAM_TRACK_ID;

	private String PAYOOM_TRACK_ID;
	private String PAYOOM_BASE_URL;

	private String VCOMMISSION_TRACK_ID;
	private String VCOMMISSION_BASE_URL;

	private String AFFERVE_SRC;

	private static final String[] DEFAULT_PARAMS = { "utm_source", "utm_medium", "utm_campaign", "utm_content", "utm_term", "offer_id", "aff_id" };

	@PostConstruct
	public void start() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		String size = env.getProperty("amazon.track.ids.count");
		if (StringUtils.isNotBlank(size) && StringUtils.isNumeric(size)) {
			amazon_track_size = Integer.parseInt(size);
		}
		AMAZON_TRACK_ID = env.getProperty("amazon.associateTag", "simplecode-21");
		FLIPKART_TRACK_ID = env.getProperty("flipkart.trackingID", "abhishek03");
		SNAPDEAL_TRACK_ID = env.getProperty("snapdeal.trackingID", "71015");
		//INFIBEAM_TRACK_ID = env.getProperty("infibeam.trackingID", "afferve");
		PAYOOM_TRACK_ID = env.getProperty("payoom.trackingID", "20182");
		PAYOOM_BASE_URL = env.getProperty("payoom.baseurl", "http://tracking.payoom.com/aff_c?offer_id=");
		VCOMMISSION_TRACK_ID = env.getProperty("vcommission.trackingID", "46147");
		VCOMMISSION_BASE_URL = env.getProperty("vcommission.baseurl", "http://tracking.vcommission.com/aff_c?offer_id=");
		AFFERVE_SRC = env.getProperty("general.source", "www_afferve_com");

		int tmp = setNextAmazonTrackingId();
		if (tmp > 1 && tmp < amazon_track_size) {
			counter = new AtomicInteger(tmp);
		}
	}

	private int setNextAmazonTrackingId() {
		log.info("setNextAmazonTrackingId() Enter");
		Date date = BackofficeUtil.getStartOfDay(new Date(System.currentTimeMillis()));
		UUID low = UUIDs.startOf(date.getTime());
		int tmp = 0;
		String id = repo.getLastTransactionId(low, HomeEnum.AMAZON.name());
		if (id != null) {
			Pattern pattern = Pattern.compile("afferve-0(\\d+)-21", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(id);
			while (matcher.find()) {
				if (BackofficeUtil.isMatchPresent(matcher, 1)) {
					id = matcher.group(1);
					if (NumberUtils.isNumber(id)) {
						tmp = Integer.parseInt(id);
					}
					break;
				}
			}
		}
		log.info("setNextAmazonTrackingId() Exit");
		return ++tmp;
	}

	private String getNextAmazonTrackingId() {
		int indx = counter.getAndIncrement();
		counter.compareAndSet(amazon_track_size, 1);

		return "afferve-0" + indx + "-21";
	}

	public String redirectHomeUrl(Map<String, String> map) {
		// String keys[] = {"home","id","subcategoryname","userid"};
		String home = "default";
		String userId = "default";

		if (map.containsKey("home")) {
			home = map.get("home");
		}

		if (map.containsKey("userId")) {
			userId = map.get("userId");
		}

		Statement select = QueryBuilder.select("producturl").from(keyspace, TableEnum.home_product_info.name()).where(QueryBuilder.eq("home", map.get("home")))
				.and(QueryBuilder.eq("id", map.get("id"))).and(QueryBuilder.eq("subcategoryname", map.get("subCategoryName")));

		List<Row> rs = session.execute(select).all();
		HomeEnum homeImp = HomeEnum.getHome(home);
		if (rs != null && rs.size() > 0 && homeImp != null) {
			activity.addProductActivity(userId, homeImp, map.get("subCategoryName"), map.get("id"));
			return createTxn(homeImp, userId, rs.get(0).getString("producturl"));
		}
		return null;
	}

	private String determineUrl(HomeEnum home, String url) {
		boolean slash = url.endsWith("/");
		if (slash) {
			url = url.substring(0, url.length() - 1);
		}

		if ("www.flipkart.com".equals(url)) {
			url = url + "/";
		}

		try {
			url = URLDecoder.decode(url, "UTF-8");
			

			if (url.startsWith("https://")) {
				url = url.replace("https://", "http://");
			}
			if (!(url.startsWith("http://"))) {
				url = "http://" + url;
			}
			if (home.isGoHttps()){
				url = url.replace("http://", "https://");
			}
		} catch (UnsupportedEncodingException e2) {
		}

		boolean encode = false;
		switch (home) {
		case AMAZON:
			url = trimUrl(url, "tag=");
			url = url + "tag=" + AMAZON_TRACK_ID;
			break;
		case EXAMPER:
			url = trimUrl(url, "ref=");
			url = url + "ref=" + EXAMPER_TRACK_ID;
			break;
		case AMERICANSWAN:
			url = trimUrl(url, "utm_source=");
			url = url + "utm_source=" + "payoom-cps";
			encode = true;
			break;
		case ARCHIES:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=alliances" + "&utm_medium=discountcoupon" + "&utm_campaign=payoom";
			encode = true;
			break;
		case ASKMEBAZAAR:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=affiliate" + "&utm_medium=payoom" + "&utm_campaign=pay_oom";
			encode = true;
			break;
		case ASKMEGROCERY:
			url = trimUrl(url, "utm_source=", "utm_medium=");
			url = url + "utm_source=affiliate" + "&utm_medium=payoom";
			encode = true;
			break;
		case BABYOYE:
			url = trimUrl(url, "aff_id=", "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "aff_id=321&utm_source=Payoom&utm_medium=babyoyeaffiliate&utm_campaign=regular";
			encode = true;
			break;
		case BIBA:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=", "utm_content=");
			url = url + "utm_source=payoom&utm_medium=banner&utm_content=newarrivals&utm_campaign=june_newarrivals";
			encode = true;
			break;
		case BLUESTONE:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=Payoom&utm_medium=cps&utm_campaign=DiamondEarrings";
			encode = true;
			break;
		case BOOKMYFLOWERS:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=affiliate&utm_medium=Banner&utm_campaign=payoom";
			encode = true;
			break;
		case CANDERE:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=affiliate&utm_medium=PM&utm_campaign=PM-11-03";
			encode = true;
			break;
		case CHUMBAK:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_campaign=Payoom-Affiliate&utm_medium=Affiliate&utm_source=cps";
			encode = true;
			break;
		case CLOVIA:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=10014" + "&utm_medium=20004" + "&utm_campaign=70043";
			encode = true;
			break;
		case CROMA:
			url = trimUrl(url, "cm_mmc=");
			url = url + "cm_mmc=Payoom-_-Affiliate-_-Feb2015-_-AffiliateOffers";
			encode = true;
			break;
		case DOMINOS:
			url = trimUrl(url, "src=");
			url = url + "src=" + "payoom";
			encode = true;
			break;
		case EBAY:
			url = trimUrl(url, "aff_source=");
			url = url + "aff_source=" + "payoom";
			encode = true;
			break;
		case FABFURNISH:
			url = trimUrl(url, "wt_af=", "utm_source=", "utm_medium=", "utm_term=", "utm_content=", "utm_campaign=");
			url = url
					+ "wt_af=in.affiliate.Vcommission.Vcommission_Cps.af.1&utm_source=Vcommission&utm_medium=affiliate&utm_term=General&utm_content=Banners&utm_campaign=Vcommission_home_Oct27";
			encode = true;
			break;
		case FABINDIA:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=payoom" + "&utm_medium=cps" + "&utm_campaign=women-kurtas";
			encode = true;
			break;
		case FIRSTCRY:
			url = trimUrl(url, "ref=", "utm_source=", "utm_medium=", "utm_content=");
			url = url + "ref=vcomm&utm_source=vcomm&utm_medium=aff&utm_content=vcomm";
			encode = true;
			break;
		case FLABERRY:
			url = trimUrl(url, "utm_source=");
			url = url + "utm_source=payoom";
			encode = true;
			break;
		case FNP:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=affiliate" + "&utm_medium=Banner" + "&utm_campaign=payoom";
			encode = true;
			break;
		case FLIPKART:
			url = trimUrl(url, "affid=");
			url = url + "affid=" + FLIPKART_TRACK_ID;
			url = url.replace("www.", "").replace("dl.", "").replace("/dl", "");
			url = url.replace("flipkart.com", "dl.flipkart.com/dl");
			break;
		case HEALTHGENIE:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=payoom" + "&utm_medium=banner" + "&utm_campaign=payoomcps";
			encode = true;
			break;
		case HEALTHKART:
			url = trimUrl(url, "utm_source=", "utm_medium=");
			url = url + "utm_source=PayOOM" + "&utm_medium=hk_affiliate";
			encode = true;
			break;
		case HOMESHOP18:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=affiliate&utm_medium=VCM&utm_campaign=coupon";
			encode = true;
			break;
		/*case INFIBEAM:
			url = trimUrl(url, "trackId=");
			url = url + "trackId=" + INFIBEAM_TRACK_ID;
			url = url.replaceFirst("/*\\?", "/?");
			break;*/
		case INDIATIMES:
			url = trimUrl(url, "utm_source=", "utm_medium=");
			url = url + "utm_source=payoom" + "&utm_medium=affiliate";
			encode = true;
			break;
		case JABONG:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=cps_payoom" + "&utm_medium=dc-clicktracker" + "&utm_campaign=20182";
			encode = true;
			break;
		case JOCKEY:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=", "utm_content=", "utm_term=");
			url = url + "utm_source=Payoom" + "&utm_content=CPS" + "&utm_term=Banners" + "&utm_campaign=JockeyIndiaCPSNov15";
			encode = true;
			break;
		case KOOVS:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=vcommission&utm_medium=cpa&utm_campaign=premium_20140326";
			encode = true;
			break;
		case LENSKART:
			url = trimUrl(url, "utm_source=");
			url = url + "utm_source=payoom";
			encode = true;
			break;
		case LIMEROAD:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=affiliates&utm_medium=payoom_paid&utm_campaign=default";
			encode = true;
			break;
		case MCD:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=payoom&utm_medium=cpo&utm_campaign=payoom";
			encode = true;
			break;
		case MOBIKWIK:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=payoom&utm_medium=cps&utm_campaign=sale";
			encode = true;
			break;
		case NAAPTOL:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=", "utm_code=", "ntpromoid=");
			url = url + "ntpromoid=18562&utm_source=Vcommission&utm_medium=Banner&utm_campaign=VCOM&utm_code=Vcommission-Banner";
			encode = true;
			break;
		case NETMEDS:
			url = trimUrl(url, "source_attribution=", "utm_source=", "utm_medium=", "utm_campaign=", "utm_content=");
			url = url + "source_attribution=Payoom-CPS&utm_source=Payoom-CPS&utm_medium=CPS-Banner&utm_campaign=Display&utm_content=CPS-Cent";
			encode = true;
			break;
		case NYKAA:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=Payoom&utm_medium=Affiliate&utm_campaign=Nykaa-Payoom";
			encode = true;
			break;
//		case PAYTM:
//			url = trimUrl(url, "utm_term=", "utm_source=", "utm_medium=", "utm_campaign=");
//			url = url + "utm_source=Affiliates&utm_medium=Payoom&utm_term=20182{transaction_id}&utm_campaign=Payoom";
//			encode = true;
//			break;
		case PEPPERFRY:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=aff&utm_medium=payoom&utm_campaign=payoom";
			encode = true;
			break;
		case PRINTVENUE:
			url = trimUrl(url, "utm_source=", "utm_medium=");
			url = url + "utm_source=PayOOM&utm_medium=CPS";
			encode = true;
			break;
		case PURPLLE:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=payoom&utm_medium=affiliate&utm_campaign=payoom";
			encode = true;
			break;
		case RANGRITI:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_content=", "utm_campaign=");
			url = url + "utm_source=payoom&utm_medium=banner&utm_content=new_arrivals&utm_campaign=june_newarrivals";
			encode = true;
			break;
		case SHOPCLUES:
			url ="http://affiliateshopclues.com/?a=10&c=19&p=r&s1={affiliate_id}&s2={transaction_id}&ckmrdr="+url; 
			encode = true;
			break;
		case SHOPNINETEEN:
			url = trimUrl(url, "utm_source=", "utm_medium=", "nineaflt=");
			url = url + "utm_source=Payoom&utm_medium=Affiliate&nineaflt=POM";
			encode = true;
			break;
		case SNAPDEAL:
			url = trimUrl(url, "utm_source=", "utm_campaign=", "offer_id=", "aff_id=");
			String src = "aff_prog";
			String cmp = "afts";
			String ofr = "17";
			url = url + "utm_source=" + src + "&utm_campaign=" + cmp + "&offer_id=" + ofr + "&aff_id=" + SNAPDEAL_TRACK_ID;
			break;
		case TRENDIN:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "utm_source=payoom&utm_medium=Affiliate&utm_campaign=payoom";
			encode = true;
			break;
		case VOXPOP:
			url = trimUrl(url, "cnvcd=", "utm_source=", "utm_medium=", "utm_campaign=");
			url = url + "cnvcd=PM1&utm_source=PayOOM_Network&utm_medium=CPA&utm_campaign=30_Offer";
			encode = true;
			break;
		case YEPME:
			url = trimUrl(url, "offer_id=", "aff_id=");
			url = url + "offer_id=6&aff_id=1004";
			encode = true;
			break;
		case ZIVAME:
			url = trimUrl(url, "utm_medium=", "utm_source=");
			url = url + "utm_source=Affiliates" + "&utm_medium=" + "payoom-20182";
			encode = true;
			break;
		case ZOVI:
			url = trimUrl(url, "utm_source=", "utm_medium=", "utm_campaign=", "utm_content=", "ccode=");
			url = url + "ccode=nvc&utm_source=nvc&utm_medium=AFF&utm_content=offer&utm_campaign=VCOM";
			encode = true;
			break;
		default:
			String param = home.getParams();
			if (StringUtils.isNotBlank(param)) {
				url = trimUrlByParam(url, param);
				url = url + param;
			}
			encode = true;
			break;
		}

		if (encode) {
			try {
				url = URLEncoder.encode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return url;
	}

	private String trimUrlByParam(String url, String param) {
		String tmp[] = param.trim().split("&");
		List<String> params = new LinkedList<String>();
		if (tmp.length > 0) {
			for (String element : tmp) {
				int index = element.indexOf("=");
				if (index > 0) {
					params.add(element.substring(0, index));
				}
			}
		}

		return trimUrl(url, params);
	}

	private String createTxn(HomeEnum home, String userId, String preUrl) {

		UserTransactionDTO txn = new UserTransactionDTO(userId, home.name());

		StringBuilder sb = new StringBuilder();

		String transactionId = txn.getTransactionId();
		log.info("Start Transaction - {}", txn.toString());

		String offerId = null;

		switch (home) {
		case AMAZON:
			String amazonTrackingId = getNextAmazonTrackingId();
			sb.append(preUrl.replace("simplecode-21", amazonTrackingId));
			txn.setTrackingId(amazonTrackingId);
			break;
		case FLIPKART:
			sb.append(preUrl).append("&affExtParam1=").append(userId).append("&affExtParam2=").append(transactionId);
			break;
		case SNAPDEAL:
			sb.append(preUrl).append("&aff_sub=").append(userId).append("&aff_sub2=").append(transactionId);
			break;
		case EXAMPER:
			sb.append(preUrl);//.append("&affExtParam1=").append(userId);
			break;
//		case SHOPCLUES:
//			String id = env.getProperty("shopclues.affiliateID", "1007");
//			String source = env.getProperty("shopclues.source", "www_afferve_com");
//			sb.append("http://affiliateshopclues.com/?a=" + id + "&c=19&p=r&s1=shopclues&ckmrdr=").append(preUrl)
//					.append("?utm_source=" + source + "&utm_medium=CPS");
//			sb.append("&s2=").append(userId).append("&s3=").append(transactionId);
//			break;
//		case INFIBEAM:
//			sb.append(preUrl);
//			break;
		
		default:
			String key = home.name() + "." + home.getThirdparty().name() + "." + "ID";
			offerId = env.getProperty(key.toLowerCase(), home.getOfferId());
			switch (home.getThirdparty()) {
			case OFF:
				sb.append(preUrl);
				break;
			case PAYOOM:
				if (home.isDefaultUrl()) {
					sb.append(getPayoomURLDefault(offerId, userId, transactionId));
				}
				else {
					sb.append(getPayoomURL(preUrl, offerId, userId, transactionId));
				}
				break;
			case VCOMMISSION:
				if (home.isDefaultUrl()) {
					sb.append(getVcommissionURLDefault(offerId, userId, transactionId));
				}
				else {
					sb.append(getVcommissionURL(preUrl, offerId, userId, transactionId));
				}
				break;
			default:
				break;
			}
			break;
		}
		String url = sb.toString();
		txn.setUrl(url);
		repo.save(txn);
		// String shortUrl = urlShort.shortenUrl(url);
		// log.info("Transaction - {} , Short URL - {}", txn.toString(),
		// shortUrl);
		return url;
	}

//	private String getCuelinksURL(String userId, String url, boolean isHttps) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("https://linksredirect.com/?pub_id=9791CL9012");
//		if (StringUtils.isNotBlank(userId)) {
//			sb.append("&subid=").append(userId);
//		}
//		if (isHttps) {
//			sb.append("&url=https%3A//").append(url).append("/");
//		}
//		else {
//			sb.append("&url=http%3A//").append(url).append("/");
//		}
//		return sb.toString();
//	}

	private StringBuilder getPayoomURLDefault(String offerId, String userId, String transactionId) {
		StringBuilder sb = new StringBuilder();
		sb.append(PAYOOM_BASE_URL).append(offerId).append("&aff_id=").append(PAYOOM_TRACK_ID).append("&source=" + AFFERVE_SRC);
		if (StringUtils.isNotBlank(userId)) {
			sb.append("&aff_sub=").append(userId);
		}
		sb.append("&aff_sub2=").append(transactionId);
		return sb;
	}

	private StringBuilder getPayoomURL(String preUrl, String offerId, String userId, String transactionId) {
		StringBuilder sb = new StringBuilder();
		sb.append(PAYOOM_BASE_URL).append(offerId).append("&aff_id=").append(PAYOOM_TRACK_ID).append("&source=" + AFFERVE_SRC);
		if (StringUtils.isNotBlank(userId)) {
			sb.append("&aff_sub=").append(userId);
		}
		sb.append("&aff_sub2=").append(transactionId);
		sb.append("&url=").append(preUrl);
		return sb;
	}

	private StringBuilder getVcommissionURLDefault(String offerId, String userId, String transactionId) {
		StringBuilder sb = new StringBuilder();
		sb.append(VCOMMISSION_BASE_URL).append(offerId).append("&aff_id=").append(VCOMMISSION_TRACK_ID).append("&source=" + AFFERVE_SRC);
		if (StringUtils.isNotBlank(userId)) {
			sb.append("&aff_sub=").append(userId);
		}
		sb.append("&aff_sub2=").append(transactionId);
		return sb;
	}

	private StringBuilder getVcommissionURL(String preUrl, String offerId, String userId, String transactionId) {
		StringBuilder sb = new StringBuilder();
		sb.append(VCOMMISSION_BASE_URL).append(offerId).append("&aff_id=").append(VCOMMISSION_TRACK_ID).append("&source=" + AFFERVE_SRC);
		if (StringUtils.isNotBlank(userId)) {
			sb.append("&aff_sub=").append(userId);
		}
		sb.append("&aff_sub2=").append(transactionId);
		sb.append("&url=").append(preUrl);
		return sb;
	}

	public String generateUrl(Map<String, String> map) {
		// String keys[] = {"home","id","subcategoryname","userid","searchurl"};
		String userId = "default";
		if (map.containsKey("userId")) {
			userId = map.get("userId");
		}

		String preUrl = map.get("productUrl");
		if (StringUtils.isNotBlank(preUrl)) {
			HomeEnum home = HomeEnum.determineHome(preUrl);
			if (home != null) {
				String url = determineUrl(home, preUrl);
				if (StringUtils.isNotBlank(url)) {
					activity.addURLActivity(userId, preUrl, home);
					return createTxn(home, userId, url);
				}
			}
		}
		return null;
	}

	private String trimUrl(String url, String... params) {
		for (String param : params) {
			if (url.contains(param)) {
				if (!param.endsWith("=")) {
					param = param + "=";
				}
				int start = url.indexOf(param);
				if (start != -1) {
					String sub = url.substring(start);
					String values[] = sub.split("&");
					String val = values[0];
					url = url.replaceFirst("(&)?" + val, "");
				}
			}
		}
		url = trimUrlDefault(url);
		return url;
	}

	private String trimUrl(String url, List<String> params) {
		for (String param : params) {
			if (url.contains(param)) {
				if (!param.endsWith("=")) {
					param = param + "=";
				}
				int start = url.indexOf(param);
				if (start != -1) {
					String sub = url.substring(start);
					String values[] = sub.split("&");
					String val = values[0];
					url = url.replaceFirst("(&)?" + val, "");
				}
			}
		}
		url = trimUrlDefault(url);
		return url;
	}

	private String trimUrlDefault(String url) {
		for (String param : DEFAULT_PARAMS) {
			if (url.contains(param)) {
				if (!param.endsWith("=")) {
					param = param + "=";
				}
				int start = url.indexOf(param);
				if (start != -1) {
					String sub = url.substring(start);
					String values[] = sub.split("&");
					String val = values[0];
					url = url.replaceFirst("(&)?" + val, "");
				}
			}
		}

		url = url.replace("&(&+)", "&");
		url = url.replace("?(&+)", "?");
		url = url.replaceAll("&$", "");
		url = url.replaceAll("\\?$", "");

		if (!url.contains("?")) {
			url = url + "?";
		}
		else {
			url = url + "&";
		}
		return url;
	}

	/*public static void main(String[] args) {
		URLRedirector r = new URLRedirector();
		System.out.println(r.determineUrl(HomeEnum.BIGBASKET, "http://www.bigbasket.com/pc/beverages/energy-health-drinks/?nc=nb&utm_campaign=11-20182&utm_medium=12&utm_source=12"));
	}*/

}