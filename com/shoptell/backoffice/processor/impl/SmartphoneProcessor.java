/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.processor.impl;

import static com.shoptell.backoffice.BackofficeUtil.flipkartUpdateFeatures;
import static com.shoptell.backoffice.BackofficeUtil.jsonToMap;
import static com.shoptell.backoffice.enums.CategoryEnum.SMARTPHONES;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import com.datastax.driver.core.ResultSet;
import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.QueryMapper;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.scrap.mobiles.ProductInfoDTO;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "SmartphoneProcessor")
public class SmartphoneProcessor extends DataProcessor {
	@Inject
	private SelectQuery selectQuery;

	public static final String AMAZON_MOBILE_REGEX = "([a-z]+)\\s([a-z]+(\\s[0-9]+)?\\s?)?((\\w*\\s)*)([a-z0-9\\+]*)?(.*)";

	public static final String GB_REGEX = ".*(\\s|\\()(\\d+GB).*";

	public static final String FLIPKART_MOBILE_REGEX = "([a-z]+)\\s([a-z]+(\\s[0-9]+)?\\s)?(\\w+(\\s\\w+)*)?(.*)";

	public static final String SNAPDEAL_COLOR_REGEX01 = "\\(([a-z\\s]*)\\)";

	public static final String subCategoryName = SMARTPHONES.name();

	// BOTH KEY AND VALUE TOGATHER WILL BE IGNORED FROM THE TITLE TRIMMIMG
	public static final Map<String, List<String>> IGNORE_WORDS = new HashMap<>();
	public static final String SEPERATOR = "&&";
	public static final String DOT_SEPERATOR = "ZAFFERVEZ";

	// KEY AND VALUE WILL BE CONCATED IN THE TITLE
	// public static final Map<String, String> CONCAT_WORDS = new
	// HashMap<String, String>();
	public static final Map<String, List<String>> CONCAT_WORDS = new HashMap<>();

	// To Add Product Brand if its missing
	public static final Map<String, String> ADD_WORD = new HashMap<>();

	public static final String MOBILE_WORD_REMOVE_REGEX = "\\s(((PHONE)|(HD)|(SMARTPHONE)|(HANDSET)|(MODEL))\\s?)+";

	public static final String MOBILE_WORD_REMOVE_REGEX2 = "\\s(((RED\\sPLAN)|"
			/* 13 */+ "(INTERNATIONAL)|"
			/* 12 */+ "(CONNECTIVITY)|(FEATUREPHONE)|(MANUFACTURER)|"
			/* 11 */+ "(CONECTIVITY)|(MANUFACTURE)|(MOBILEPHONE)|(REFURBISHED)|(SCREENGUARD)|(UNACTIVATED)|"
			/* 10 */+ "(AFFORDABLE)|(ACCESSORIES)|(CHANGEABLE)|(COFEEBROWN)|(CUSTOMIZED)|(MULTIMEDIA)|(PLUSQWERTY\\S*)|(REFURBISED)|(REGISTERED)|(SMARTPHONE)|(SNAPDRAGON)|(TOUCHSCREEN)|"
			/* 9 */+ "(BACKCOVER)|(BLUETOOTH)|(CONDITION)|(EXCLUSIVE)|(INSURANCE)|(JELLYBEAN)|(LEGENDARY)|(MEGAPIXEL)|(PLUSEXTRA)|(POWER\\s?BANK)|(PROCESSOR)|(SMATPHONE)|(UNDAMAGED)|(UNIVERSAL)|"
			/* 8 */+ "(CELLULAR)|(FREEBIES)|(IMPORTED)|(INCLUDED?)|(INTERNAL)|(KEYCHAINS?)|(LOLLIPOP\\d*)|(LOLLYPOP)|(MULTISIM)|(OCTA\\s?CORE)|(ORIGINAL)|(PORTABLE)|(PRISTINE)|(QUAD\\s?CORE)|(QUALCOMM)|(RECORDER)|(RELIANCE)|(ROTATING)|(SECURITY)|(SHIPPING)|(SUPPORTS)|(UNLOCKED)|(UNSEALED)|(VODAFONE)|(WARRANTY)|(WHATSAPP)|(WIRELESS)|"
			/* 7 */+ "(ADVANCE)|(ANDORID)|(ANDRIOD)|(AND\\s?ROID)|(ASSURED?)|(BATTERY)|(BLOSSOM)|(COMPACT)|(CORNING)|(DETAILS)|(DISPLAY)|(EDITION)|(ENABLED)|(FACTORY)|(FASTEST)|(FEATURE)|(FIREFOX)|(GENUINE)|(GORILLA)|(INBUILT)|(LIMITED)|(MICROSD)|(MOBILES?)|(NETWORK)|(PHABLET)|(PLUSGSM\\S*)|(PLUSLTE\\S*)|(PREMIUM)|(PRIMARY)|(PRIVACY)|(PROCESR\\s)|(REPLACE)|(SERVICE)|(SETTINGS?)|(SHIPING)|(SPECIAL)|(STORAGE)|(SUPPORT)|(VERSION)|(WINDOWS?)|(WITHOUT)|(WORKING)|"
			/* 6 */+ "(ADROID)|(AIRTEL)|(BETTER)|(BUTTONS?)|(CAMERA)|(COLOURS?)|(DEALER)|(DISPLY)|(FINGER)|(GUARDS?)|(INDIAN?)|(KEYPADS?)|(KITKAT)|(LATEST)|(LAUNCH)|(LOCKED)|(LOWEST)|(MEMORY)|(MONTHS)|(MYSTIC)|(OFFERS?)|(OTHERS?)|(ORDERS?)|(PACKED)|(QWERTY)|(RETINA)|(SCREEN)|(SEALED)|(SELLER)|(SENSOR)|(SERENE)|(SINGLE)|(SLIDER)|(SLIVER)|(TABLET)|(UNLOCK)|(UNUSED)|"
			/* 5 */+ "(ABOUT)|(ANGEL)|(BASED)|(BASIC)|(BRAND)|(CABLE)|(CHECK)|(COLOR)|(COMBO)|(COSMIC)|(COVER)|(DREAM)|(FLASH)|(FRESH)|(FRONT)|(GIFTS?)|(JELLY)|(MICRO\\s)|(MONTH)|(MULTI)|(NIGHT)|(PRICE)|(PRINT)|(RADIO)|(SLCD2)|(SLEEK)|(SMART\\s)|(SOUNDS?\\s)|(SUPER)|(TOUCH\\s)|(VOLTE)|(WATER)|(WORTH)|(YAMHA)|"
			/* 4 */+ "(AUTO)|(BACK)|(BANK)|(BEAN)|(BEST)|(BILLS?)|(CALL)|(CAME\\s)|(CARDS?)|(CDMA)|(CELL)|(CORE)|(DATA)|(DDR3)|(DEAL)|(DUAL)|(EVDO)|(FLIP)|(FOUR)|(FREE)|(FULL)|(GOOD)|(HDMI)|(IIFA)|(INCH)|(INTE\\s)|(LIKE)|(LAST)|(LOCK)|(MOST\\s)|(OCTA)|(ONLY)|(OPEN)|(PACK)|(PHON)|(QUAD\\s)|(RARE)|(REAR)|(SILK)|(SEAL)|(TOUR)|(UNIT\\s)|(USED)|(WIFI)|(WITH)|(\\d*\\s?YEAR)|"
			/* 3 */+ "(ALL)|(AND\\s)|(ANY\\s)|(BAR\\s)|(BIG)|(BLK)|(BOX)|(CAM\\s)|(FOR\\s)|(\\d*.?\\d+\\s?GHZ)|(GHZ)|(GPS)|(GSM)|(GUN\\s)|(IPS)|(LED)|(LTE)|(\\d*\\.?\\d*\\s?MAH)|(MFG)|(MGF)|(MID\\s)|(MMC)|(MP3)|(NEW)|(NOT\\s)|(NOW)|(OFF)|(PRE)|(\\d*\\-?\\.?\\d*\\s?GB\\s?RAM)|(\\d*\\-?\\.?\\d*\\s?MB\\s?RAM)|(RAM)|RES\\s|(ROM)|(SIM)|(TLX)|(TFT)|(THE)|(USA)|(VAT\\s)|(WTH\\s)|(WTY)|"
			/* 2 */+ "(3G\\s)|(4G\\s)|(FM)|(IN\\s)|(\\d+\\s?MP)|(OF\\s)|(ON\\s)|(OR\\s)|(\\sI?OS)|(RS)|(TV)|(UK)|(XT\\s)|(\\d*\\s?YR)|(\\s&\\s)|" + ")\\s?)+";

	public static final Set<String> EBAY_EXTRA_WORDS = new HashSet<String>(Arrays.asList(new String[] { "NEW", "BRAND", "DOW", "44", "2014", "2015", "2013",
			"THE", "&" }));

	private static final String SMARTPHONE_NAME_REGEX = "([&.+\\w\\d]+\\s)([&.+\\w\\d]+\\s)?(([&.+\\w\\d]+\\s)*)([&.+\\w\\d]+)(.*)";

	@PostConstruct
	public void start() {
		IGNORE_WORDS.put("9105", Arrays.asList("PEARL"));
		IGNORE_WORDS.put("ANDROID", Arrays.asList("ONE"));// ANDROID ONE ->
															// ANDROID ONE ->
															// ANDROID ONE
		IGNORE_WORDS.put("BLACKBERRY", Arrays.asList("CLASSIC"));
		IGNORE_WORDS.put("COOKIE", Arrays.asList("SMART"));
		IGNORE_WORDS.put("EARTH", Arrays.asList("COLORS"));
		IGNORE_WORDS.put("GALAXY", Arrays.asList("CORE"));
		IGNORE_WORDS.put("PENTA", Arrays.asList("SMART"));
		IGNORE_WORDS.put("KARBONN", Arrays.asList("SMART", "WAVE", "TITANIUM"));
		IGNORE_WORDS.put("SMART", Arrays.asList("FLO"));
		IGNORE_WORDS.put("SPICE", Arrays.asList("SMART"));
		IGNORE_WORDS.put("SOUND", Arrays.asList("WAVE"));
		IGNORE_WORDS.put("SPEED", Arrays.asList("HD"));
		IGNORE_WORDS.put("DESIRE", Arrays.asList("HD"));
		IGNORE_WORDS.put("AQUA", Arrays.asList("4G", "3G", "DREAM"));
		IGNORE_WORDS.put("TURBO", Arrays.asList("4G"));
		IGNORE_WORDS.put("NOVA", Arrays.asList("FM"));

		CONCAT_WORDS.put("CHEERS", Arrays.asList("SLEEK"));
		CONCAT_WORDS.put("I", Arrays.asList("PHONE", "SMART")); // I Phone ->
																// IPHONE
		CONCAT_WORDS.put("K", Arrays.asList("PHONE"));
		CONCAT_WORDS.put("KIT", Arrays.asList("KAT"));
		CONCAT_WORDS.put("NEW", Arrays.asList("YORK"));
		CONCAT_WORDS.put("ONE", Arrays.asList("PLUS"));

		ADD_WORD.put("IPHONE", "APPLE");
		ADD_WORD.put("GALAXY", "SAMSUNG");
	}

	@Override
	public ReviewedProductInfoDTO shopcluesProcess(HomeProductInfoDTO prod) {
		String size = null;
		String color = null;
		String product_brand = null;
		String product_sub_brand = null;
		String series = null;
		String model = null;

		retrieveTitle(prod);
		size = retrieveSize(prod);

		// String title = prod.getTitle();

		Pattern pattern = Pattern.compile(SNAPDEAL_COLOR_REGEX01, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(prod.getTitle());
		while (matcher.find()) {
			if (isMatchPresent(matcher, 1) && StringUtils.isEmpty(color)) {
				color = matcher.group(1).trim();
				prod.setTitle(prod.getTitle().substring(0, prod.getTitle().indexOf(matcher.group(0))).trim());
				break;
			}
		}

		if (StringUtils.isEmpty(color)) {
			color = retriveColor(prod);
		}

		prod.setTitle(processTitle(prod.getTitle()));

		Set<String> set = new LinkedHashSet<String>(Arrays.asList(prod.getTitle().split(" ")));

		StringBuilder tmp = new StringBuilder();

		int index = 1;
		for (String element : set) {
			if (element.contains("(")) {
				break;
			}
			if (index == 1) {
				if (!BackofficeUtil.mobileBrandSet.contains(element)) {
					continue;
				}
				// product_brand = element;
				tmp.append(element);
			}
			if (index == 2) {
				// product_sub_brand = element;
				tmp.append(" ").append(element);
			}
			if (index == 3) {
				// series = element;
				tmp.append(" ").append(element);
			}
			if (index == 4) {
				// model = element;
				tmp.append(" ").append(element);
			}
			if (index == 5) {
				// series += " " + model;
				// model = element;
				tmp.append(" ").append(element);
			}
			if (index == 6) {
				// series += " " + model;
				// model = element;
				tmp.append(" ").append(element);
			}
			if (index == 7) {
				// series += " " + model;
				// model = element;
				tmp.append(" ").append(element);
			}
			index++;
		}

		pattern = Pattern.compile(SMARTPHONE_NAME_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(tmp.toString());
		while (matcher.find()) {
			if (isMatchPresent(matcher, 1)) {
				product_brand = matcher.group(1).trim();
			}

			if (isMatchPresent(matcher, 2)) {
				product_sub_brand = matcher.group(2).trim();
			}

			if (isMatchPresent(matcher, 3)) {
				series = matcher.group(3).trim();
			}
			if (isMatchPresent(matcher, 5)) {
				model = matcher.group(5).trim();
			}
		}

		ReviewedProductInfoDTO info = populateDto(product_brand, product_sub_brand, series, model, color, size, prod);

		return info;
	}

	@Override
	public ReviewedProductInfoDTO snapdealProcess(HomeProductInfoDTO prod) {
		String size = null;
		String color = null;
		String product_brand = null;
		String product_sub_brand = null;
		String series = null;
		String model = null;

		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().replaceAll("\\d*\\-?\\.?\\d*\\s?\"", " ")); // 4.5"
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().replaceAll("\\d*\\-?\\.?\\d*\\s?\'", " ")); // 4.5'
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("\\d*\\-?\\.?\\d+\\s?INCH", " ")); // 4.5INCH
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().replaceAll("\\d*\\-?\\.?\\d*\\s?%", " ")); // 25%
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("UP\\s?TO\\s?\\d+\\s?GB", " ")); // UPTO
																									// 32GB
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("ANDROID\\s?\\d+\\.\\d+", " ")); // ANDROID
																									// 5.0
		}

		retrieveTitle(prod);
		size = retrieveSize(prod);
		color = retriveColor(prod);

		// String title = prod.getTitle();
		Pattern pattern = Pattern.compile(SNAPDEAL_COLOR_REGEX01, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(prod.getTitle());
		while (matcher.find()) {
			if (isMatchPresent(matcher, 1)) {
				if (StringUtils.isEmpty(color)) {
					color = matcher.group(1).trim();
				}
				else {
					color += " " + matcher.group(1).trim();
				}
				prod.setTitle(prod.getTitle().substring(0, prod.getTitle().indexOf(matcher.group(0))).trim());
				break;
			}
		}

		prod.setTitle(processTitle(prod.getTitle()));

		Set<String> set = new LinkedHashSet<String>(Arrays.asList(prod.getTitle().split(" ")));

		StringBuilder tmp = new StringBuilder();

		int index = 1;
		for (String element : set) {
			if (element.contains("(")) {
				break;
			}
			if (index == 1) {
				if (!BackofficeUtil.mobileBrandSet.contains(element)) {
					continue;
				}
				// product_brand = element;
				tmp.append(element);
			}
			if (index == 2) {
				// product_sub_brand = element;
				tmp.append(" ").append(element);
			}
			if (index == 3) {
				// series = element;
				tmp.append(" ").append(element);
			}
			if (index == 4) {
				// model = element;
				tmp.append(" ").append(element);
			}
			if (index == 5) {
				// series += " " + model;
				// model = element;
				tmp.append(" ").append(element);
			}
			if (index == 6) {
				// series += " " + model;
				// model = element;
				tmp.append(" ").append(element);
			}
			if (index == 7) {
				// series += " " + model;
				// model = element;
				tmp.append(" ").append(element);
			}
			index++;
		}

		pattern = Pattern.compile(SMARTPHONE_NAME_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(tmp.toString());
		while (matcher.find()) {
			if (isMatchPresent(matcher, 1)) {
				product_brand = matcher.group(1).trim();
			}

			if (isMatchPresent(matcher, 2)) {
				product_sub_brand = matcher.group(2).trim();
			}

			if (isMatchPresent(matcher, 3)) {
				series = matcher.group(3).trim();
			}
			if (isMatchPresent(matcher, 5)) {
				model = matcher.group(5).trim();
			}
		}

		ReviewedProductInfoDTO info = populateDto(product_brand, product_sub_brand, series, model, color, size, prod);

		return info;
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		String product_brand = null;
		String product_sub_brand = null;
		String series = null;
		String model = null;
		String color = null;
		String size = null;
		
		product_brand = prod.getProductBrand();
		
		retrieveTitle(prod);
		size = retrieveSize(prod);

		// Get Color from Title
		if (StringUtils.isEmpty(color)) {
			color = retriveColor(prod);
		}

		prod.setTitle(processTitle(prod.getTitle()));

		Pattern pattern = Pattern.compile(SMARTPHONE_NAME_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(prod.getTitle());
		while (matcher.find()) {
			if (StringUtils.isBlank(product_brand) && isMatchPresent(matcher, 1)) {
				product_brand = matcher.group(1).trim();
			}

			if (isMatchPresent(matcher, 2)) {
				product_sub_brand = matcher.group(2).trim();
			}

			if (isMatchPresent(matcher, 3)) {
				series = matcher.group(3).trim();
			}
			if (isMatchPresent(matcher, 5)) {
				model = matcher.group(5).trim();
			}
		}
		
		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());
		if (StringUtils.isBlank(model)) {
			model = flipkartUpdateFeatures(map, "GENERAL FEATURES", "Model Name");
		}
		if (StringUtils.isBlank(size)) {
			size = flipkartUpdateFeatures(map, "Memory and Storage", "Internal");
			if (StringUtils.isNotBlank(size)){
				size = size.replace(" ", "");
			}
		}
		if (StringUtils.isBlank(color)) {
			color = flipkartUpdateFeatures(map, "GENERAL FEATURES", "Handset Color");
		}

		// Processing End
		ReviewedProductInfoDTO info = populateDto(product_brand, product_sub_brand, series, model, color, size, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO ebayProcess(HomeProductInfoDTO prod) {
		String size = null;
		String color = null;
		String product_brand = null;
		String product_sub_brand = null;
		String series = null;
		String model = null;

		retrieveTitle(prod);
		size = retrieveSize(prod);

		String[] tokens = prod.getTitle().split("\\s");
		StringBuilder sb = new StringBuilder();
		for (String token : tokens) {
			if (!EBAY_EXTRA_WORDS.contains(token)) {
				sb.append(token).append(" ");
			}
		}
		String tmp = sb.toString();
		if (StringUtils.isNotBlank(tmp)) {
			prod.setTitle(tmp.trim());
		}

		color = retriveColor(prod);

		prod.setTitle(processTitle(prod.getTitle()));

		Set<String> set = new LinkedHashSet<String>(Arrays.asList(prod.getTitle().split(" ")));

		StringBuilder tmpsb = new StringBuilder();

		int index = 1;
		for (String element : set) {
			if (StringUtils.isEmpty(element)) {
				continue;
			}
			if (element.contains("(") || element.contains("|")) {
				break;
			}
			if (index == 1) {
				if (!BackofficeUtil.mobileBrandSet.contains(element)) {
					continue;
				}
				// product_brand = element;
				tmpsb.append(element);
			}
			if (index == 2) {
				// product_sub_brand = element;
				tmpsb.append(" ").append(element);
			}
			if (index == 3) {
				// series = element;
				tmpsb.append(" ").append(element);
			}
			if (index == 4) {
				// model = element;
				tmpsb.append(" ").append(element);
			}
			if (index == 5) {
				// series += " " + model;
				// model = element;
				tmpsb.append(" ").append(element);
			}
			if (index == 6) {
				// series += " " + model;
				// model = element;
				tmpsb.append(" ").append(element);
			}
			if (index == 7) {
				// series += " " + model;
				// model = element;
				tmpsb.append(" ").append(element);
			}
			index++;
		}

		Pattern pattern = Pattern.compile(SMARTPHONE_NAME_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(tmpsb.toString());
		while (matcher.find()) {
			if (isMatchPresent(matcher, 1)) {
				product_brand = matcher.group(1).trim();
			}

			if (isMatchPresent(matcher, 2)) {
				product_sub_brand = matcher.group(2).trim();
			}

			if (isMatchPresent(matcher, 3)) {
				series = matcher.group(3).trim();
			}
			if (isMatchPresent(matcher, 5)) {
				model = matcher.group(5).trim();
			}
		}

		ReviewedProductInfoDTO info = populateDto(product_brand, product_sub_brand, series, model, color, size, prod);

		return info;
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String product_brand = null;
		String product_sub_brand = null;
		String model = null;
		String series = null;
		String color = null;
		String size = null;
		
		product_brand = prod.getProductBrand();

		retrieveTitle(prod);
		// Get Size from Title
		size = retrieveSize(prod);

		// Get Color from Title
		if (StringUtils.isEmpty(color)) {
			Pattern pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(prod.getTitle());
			String match = null;
			while (matcher.find()) {
				if (isMatchPresent(matcher, 0)) {
					match = matcher.group(0);
				}
			}

			if (StringUtils.isNotBlank(match)) {
				color = match.trim();
				int index = prod.getTitle().indexOf(match);
				if (index != -1) {
					prod.setTitle(prod.getTitle().substring(0, index).trim());
				}
			}
		}

		prod.setTitle(processTitle(prod.getTitle()));

		Pattern pattern = Pattern.compile(SMARTPHONE_NAME_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(prod.getTitle());
		while (matcher.find()) {
			if (StringUtils.isBlank(product_brand) && isMatchPresent(matcher, 1)) {
				product_brand = matcher.group(1).trim();
			}

			if (isMatchPresent(matcher, 2)) {
				product_sub_brand = matcher.group(2).trim();
			}

			if (isMatchPresent(matcher, 3)) {
				series = matcher.group(3).trim();
			}
			if (isMatchPresent(matcher, 5)) {
				model = matcher.group(5).trim();
			}
		}

		// Processing End
		ReviewedProductInfoDTO info = populateDto(product_brand, product_sub_brand, series, model, color, size, prod);
		return info;
	}

	private String processTitle(String title) {
		title = title.replace(SEPERATOR, " ").replaceAll("\\s+", " ").trim();
		for (Entry<String, String> tmp : ADD_WORD.entrySet()) {
			if (title.startsWith(tmp.getKey())) {
				title = tmp.getValue() + " " + title;
			}
		}
		return title;
	}

	private String retrieveTitle(HomeProductInfoDTO prod) {
		rectifyTitle(prod);

		String SP_CHAR_REMOVE_REGEX = "[^\\p{L}\\p{Z}\\p{N}&]";

		prod.setTitle(prod.getTitle().toUpperCase().replaceAll("\\s\\+", ",,").replaceAll("\\+", " PLUS").replace(".", DOT_SEPERATOR)
				.replaceAll(SP_CHAR_REMOVE_REGEX, " "));

		String newTitle = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(prod.getTitle())) {

			String[] tokensTitle = prod.getTitle().toUpperCase().split(" ");

			for (Entry<String, List<String>> entry : CONCAT_WORDS.entrySet()) {
				String key = entry.getKey();
				List<String> vals = entry.getValue();
				for (int ii = 0; ii < tokensTitle.length - 1; ii++) {
					for (String val : vals) {
						if (StringUtils.equals(tokensTitle[ii], key) && StringUtils.equals(tokensTitle[ii + 1], val)) {
							tokensTitle[ii] = key + val;
							tokensTitle[ii + 1] = "";
						}
					}
				}
			}

			for (Entry<String, List<String>> entry : IGNORE_WORDS.entrySet()) {
				String key = entry.getKey();
				List<String> vals = entry.getValue();
				for (int ii = 0; ii < tokensTitle.length - 1; ii++) {
					for (String val : vals) {
						if (StringUtils.equals(tokensTitle[ii], key) && StringUtils.equals(tokensTitle[ii + 1], val)) {
							tokensTitle[ii] = key + SEPERATOR + val;
							tokensTitle[ii + 1] = "";
						}
					}
				}
			}

			for (int ii = 0; ii < tokensTitle.length; ii++) {
				newTitle += tokensTitle[ii] + " ";
			}
			prod.setTitle(newTitle.trim());
		}

		prod.setTitle(prod.getTitle().replaceAll("\\s+GB", "GB").replaceAll("\\s+MB", "MB").replaceAll(MOBILE_WORD_REMOVE_REGEX, " "));
		prod.setTitle(removeDuplicates(prod.getTitle()).replaceAll(MOBILE_WORD_REMOVE_REGEX2, " "));
		return prod.getTitle();
	}

	private void rectifyTitle(HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("\\d*\\-?\\.?\\d+\\s?GHZ", " ")); // 1.5GHZ
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("\\d*\\-?\\.?\\d+\\s?MP", " ")); // 40.0
																									// MP
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("U\\.?S\\.?A", " ")); // U.S.A
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("\\d*\\-?\\.?\\d+\\s?MEGA\\s?PIXEL", " ")); // 40.4
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("\\d*\\-?\\.?\\d+\\s?GB\\s?RAM", " ")); // 1.5GB
																											// RAM
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("\\d*\\-?\\.?\\d+\\s?MB\\s?RAM", " ")); // 1.5MB
																											// RAM
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("(\\(|\\s)\\d*\\-?\\.?\\d+\\s?QUAD\\s?CORE", " ")); // 1.5QUAD
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("\\d*\\-?\\.?\\d+\\s?OCTA", " ")); // 1.5OCTA
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("\\d*\\-?\\.?\\d+\\s?YE?A?R", " ")); // 1.5
																										// YR
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().replaceAll("\\d*\\-?\\.?\\d*\\s?\"", " ")); // 4.5"
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().replaceAll("\\d*\\-?\\.?\\d*\\s?\'", " ")); // 4.5'
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("\\d*\\-?\\.?\\d+\\s?INCH", " ")); // 4.5INCH
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().replaceAll("\\d*\\-?\\.?\\d*\\s?%", " ")); // 25%
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("UP\\s?TO\\s?\\d+\\s?GB", " ")); // UPTO
																									// 32GB
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().toUpperCase().replaceAll("ANDROID\\s?\\d+\\.\\d+", " ")); // ANDROID
																									// 5.0
		}
	}

	private ReviewedProductInfoDTO populateDto(String product_brand, String product_sub_brand, String series, String model, String color, String size,
			HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(product_brand)) {
			product_brand = product_brand.replace(DOT_SEPERATOR, ".").toUpperCase();
		}
		if (StringUtils.isNotBlank(product_sub_brand)) {
			product_sub_brand = product_sub_brand.replace(DOT_SEPERATOR, ".").toUpperCase();
		}
		if (StringUtils.isNotBlank(series)) {
			series = series.replace(DOT_SEPERATOR, ".").toUpperCase();
		}
		if (StringUtils.isNotBlank(model)) {
			model = model.replace(DOT_SEPERATOR, ".").toUpperCase();
		}
		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(prod.getTitle().replace(DOT_SEPERATOR, "."));
		}

		ProductInfoDTO prodInfo = verifyProductName(product_brand, product_sub_brand, series, model);

		if (StringUtils.isEmpty(color) && !StringUtils.isEmpty(prod.getColor())) {
			color = prod.getColor();
		}

		if (StringUtils.isEmpty(size) && !StringUtils.isEmpty(prod.getSize()) && !StringUtils.equalsIgnoreCase(prod.getSize(), "null")) {
			size = prod.getSize().trim();
		}

		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
		}
		if (StringUtils.isNotBlank(size)) {
			size = size.replace(" ", "");
		}

		ReviewedProductInfoDTO info = null;
		if (prodInfo == null) {
			info = new ReviewedProductInfoDTO(product_brand, product_sub_brand, series, model, color, new HashMap<String, String>(), prod, null);
		}
		else {
			info = new ReviewedProductInfoDTO(prodInfo.getProductBrand(), prodInfo.getProductSubBrand(), prodInfo.getSeries(), prodInfo.getModel(), color,
					new HashMap<String, String>(), prod, prodInfo.getId());
			List<String> features = new LinkedList<String>();

			for (Entry<String, String> tmp : prodInfo.getAllProperties().entrySet()) {
				String key = tmp.getKey();
				String value = tmp.getValue();
				features.add((key + " : " + value).toUpperCase());
			}

			info.setFeatures(features);
		}

		info.getProperties().put("SIZE", size == null ? "null" : size);

		if (StringUtils.isBlank(info.getName())) {
			return null;
		}
		return info;
	}

	private ProductInfoDTO verifyProductName(String product_brand, String product_sub_brand, String series, String model) {
		if (StringUtils.isBlank(product_brand))
			return null;
		if (StringUtils.isBlank(model))
			return null;
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("subcategoryname", subCategoryName);
		map.put("productbrand", product_brand);
		
		Map<String, Object> contains = new HashMap<String, Object>();
		contains.put("tags", model);
		
		ResultSet rs = selectQuery.selectAll(TableEnum.product_info, map, contains);
		
		Iterator<ProductInfoDTO> list = null;
		if (rs != null)
			list = QueryMapper.productInfoDTO().map(rs).iterator();

		if (list != null && list.hasNext()) {
			StringBuilder sb = new StringBuilder(product_brand);
			if (StringUtils.isNotBlank(product_sub_brand)) {
				sb.append(" ").append(product_sub_brand);
			}
			if (StringUtils.isNotBlank(series)) {
				sb.append(" ").append(series);
			}
			if (StringUtils.isNotBlank(model)) {
				sb.append(" ").append(model);
			}
			int max_count = 0;
			int max_size = 0;

			ProductInfoDTO element = null;
			while (list.hasNext()) {
				ProductInfoDTO tmp = list.next();
				Set<String> tags = tmp.getTags();
				int count = 0;
				String[] keys = sb.toString().split("\\s");
				String[] optKeys = tmp.getTitle().trim().split("\\s");
				if (!keys[keys.length - 1].equalsIgnoreCase(optKeys[optKeys.length - 1])) {
					continue;
				}
				int length = keys.length;
				for (String key : keys) {
					if (tags.contains(key.trim())) {
						count++;
					}
				}

				int size = 0;
				if (tags != null) {
					size = tags.size();
				}

				if (max_count <= count && count == length) { // count >= length
					if ((size > 0 && size < max_size) || max_size == 0) {
						max_count = count;
						element = tmp;
						max_size = size;
					}
				}
			}
			return element;
		}
		return null;
	}
	
//	public static void main(String[] args) {
//		SmartphoneProcessor p = new SmartphoneProcessor();
//		HomeProductInfoDTO prod = new HomeProductInfoDTO();
//		prod.setTitle("NUVO FLASH ");
//		prod.setProductBrand("NUVO");
//		prod.setHome("FLIPKART");
//		p.flipkartProcess(prod);
//	}
}
