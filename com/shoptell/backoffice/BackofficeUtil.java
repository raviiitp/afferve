/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shoptell.backoffice.repository.dto.ActivityDTO;
import com.shoptell.backoffice.repository.dto.CBReportDTO;
import com.shoptell.backoffice.repository.dto.MergedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.PartnerCouponsDTO;
import com.shoptell.backoffice.repository.dto.TicketMessageDTO;
import com.shoptell.backoffice.repository.dto.UserTransactionDTO;

@Named
public class BackofficeUtil {
	private static final Logger log = LoggerFactory.getLogger(BackofficeUtil.class);

	public static final Set<String> mobileBrandSet = new HashSet<String>(Arrays.asList(BackofficeConstants.MOBILE_BRANDS.split(",")));
	public static final Set<String> shoeBrandSet = new HashSet<String>(Arrays.asList(BackofficeConstants.SHOE_BRANDS.split(",")));

	public static DecimalFormat df = new DecimalFormat(BackofficeConstants.DECIMAL_FORMAT);

	public static final String COLOR_REGEX = "(\\s|\\(|-)(("
	/* 10 */+ "BLACK ONYX|BLACK TITAN|CHAMPANGNE|ONYX BLACK|"
	/* 9 */+ "ALABASTER|BALLISTIC|CHAMPAGNE|CHAMPANGE|CHOCOLATE|SANDSTONE|"
	/* 8 */+ "CHARCOAL|DAZZLING|GRAPHITE|GUNMETAL|MAGNETIC|METALLIC|MIDNIGHT|MILKYWAY|MOONDUST|SANTRONI|SHIMMERY|TITANIUM|"
	/* 7 */+ "CERAMIC|CHESTNUT|CLASSIC|ELEGANT|FROSTED|LEATHER|MAGENTA|"
	/* 6 */+ "ARCTIC|BRIGHT|BRONZE|BRUSHED|BUFFED|CARBON|CHROME|COFFEE|COPPER|FERVOR|GLOSSY|GOLDEN|MARBLE|MAROON|ORANGE|PEBBLE|PURPLE|SILVER|YELLOW|"
	/* 5 */+ "BIRCH|BLACK|BLUSH|BROWN|CORAL|FROST|GREEN|METAL|MILKY|PEARL|ROAST|ROYAL|SLEEK|SLATE|SPACE|STARRY|STEEL|WHITE|"
	/* 4 */+ "BLUE|CHIC|CYAN|DARK|DEEP|GOLD|GRAY|GREY|INOX|MINT|MIST|PINK|PURE|ROSE|SNOW|WINE|"
	/* 3 */+ "GUN|ICE|ICY|JET|RED)\\s?(,|;|\\/|-|\\+|&|AND|_|!)?\\s?){1,3}";

	public static String recursiveRemoveColor(String title, StringBuilder color) {
		String tmp = null;
		if (StringUtils.isNotBlank(title)) {
			Pattern pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(title);
			while (matcher.find()) {
				if (isMatchPresent(matcher, 0)) {
					tmp = matcher.group(0);
					title = title.replace(tmp, " ");
				}
			}
			if (StringUtils.isNotBlank(tmp)) {
				tmp = tmp.replaceAll("[^\\p{L}\\p{Z}\\p{N}]", "").trim();
			}
			title = title.replaceAll("\\s+", " ");
		}

		if (StringUtils.isNotBlank(tmp)) {
			color.append(tmp);
		}
		return title;
	}

	@PostConstruct
	public void start() {
		df.setRoundingMode(RoundingMode.FLOOR);
	}

	public static String roundOff(double number) {
		return df.format(number);
	}

	public static Date getStartOfPrevMonth() {
		Calendar c = oneMonthBack();
		if (c == null) {
			return null;
		}
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		return DateUtils.truncate(c.getTime(), Calendar.DATE);
	}

	public static Date getEndOfPrevMonth() {
		Calendar c = oneMonthBack();
		if (c == null) {
			return null;
		}
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return DateUtils.addMilliseconds(DateUtils.ceiling(c.getTime(), Calendar.DATE), -1);
	}

	private static Calendar oneMonthBack() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(System.currentTimeMillis()));

		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		if (month == 0) {
			month = 11;
			--year;

		}
		else if (month > 0 && month < 12) {
			--month;
		}
		else {
			return null;
		}
		c.set(Calendar.MONTH, month);
		c.set(Calendar.YEAR, year);

		return c;
	}

	public static String getPrevMonth() {
		Calendar c = oneMonthBack();
		if (c == null)
			return null;

		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();
		return months[c.get(Calendar.MONTH)] + " " + c.get(Calendar.YEAR);
	}

	public static Date getEndOfDay(Date date) {
		return DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);
	}

	public static Date getStartOfDay(Date date) {
		return DateUtils.truncate(date, Calendar.DATE);
	}

	public static LocalDate asLocalDate(java.util.Date date) {
		return asLocalDate(date, ZoneId.systemDefault());
	}

	/**
	 * Creates {@link LocalDate} from {@code java.util.Date} or it's subclasses.
	 * Null-safe.
	 */
	public static LocalDate asLocalDate(java.util.Date date, ZoneId zone) {
		if (date == null)
			return null;

		if (date instanceof java.sql.Date)
			return ((java.sql.Date) date).toLocalDate();
		else
			return Instant.ofEpochMilli(date.getTime()).atZone(zone).toLocalDate();
	}

	// returns the complete url hitting to any controller method
	public static String getHittingUrl(HttpServletRequest reqst) {
		return reqst.getScheme()
				+ "://"
				+ reqst.getServerName()
				+ ("http".equals(reqst.getScheme()) && reqst.getServerPort() == 80 || "https".equals(reqst.getScheme()) && reqst.getServerPort() == 443 ? ""
						: ":" + reqst.getServerPort()) + reqst.getRequestURI() + (reqst.getQueryString() != null ? "?" + reqst.getQueryString() : "");
	}

	public static String getBaseUrl(HttpServletRequest request) {
		String scheme = request.getScheme();
		String name = request.getServerName();
		int port = request.getServerPort();

		if (port == 80 || port == 443) {
			return scheme + "://" + name;
		}

		return scheme + "://" + name + ":" + port;
	}

	public static String retrieveSize(String title) {
		title = title.replace(" ", "");
		int size = 0;
		boolean isMb = false;
		String ext = "GB";
		List<String> allMatches = new LinkedList<String>();
		Pattern pattern = Pattern.compile("\\d+([GM]B)");

		Matcher matcher = pattern.matcher(title);
		while (matcher.find()) {
			allMatches.add(matcher.group());
		}

		for (String match : allMatches) {
			title = title.replace(match, "");
			String tmp = match.substring(0, match.length() - 2);

			if (match.endsWith("MB")) {
				isMb = true;
			}
			else {
				isMb = false;
			}

			if (StringUtils.isNumeric(tmp)) {
				int num = Integer.parseInt(tmp);
				if (!isMb) {
					num *= 1024;
				}
				if (num > size) {
					size = num;
					if (isMb && size < 1024) {
						ext = "MB";
					}
					else {
						ext = "GB";
					}
				}
			}
		}
		if (size == 0) {
			return null;
		}
		if ("GB".equalsIgnoreCase(ext)) {
			size = size / 1024;
		}
		return String.valueOf(size) + ext;
	}

	public static boolean isMatchPresent(Matcher matcher, int i) {
		int count = matcher.groupCount();
		if (i <= count) {
			String text = matcher.group(i);
			if (StringUtils.isNotBlank(text)) {
				return true;
			}
		}
		return false;
	}

	public static Comparator<TicketMessageDTO> compareTicket = new Comparator<TicketMessageDTO>() {
		@Override
		public int compare(TicketMessageDTO o1, TicketMessageDTO o2) {
			if (o1.getDate().after(o2.getDate())) {
				return 1;
			}
			else if (o2.getDate().after(o1.getDate())) {
				return -1;
			}
			return 0;
		}
	};

	public static Comparator<UserTransactionDTO> compareUserTransaction = new Comparator<UserTransactionDTO>() {
		@Override
		public int compare(UserTransactionDTO o1, UserTransactionDTO o2) {
			if (o1.date().before(o2.date()))
				return 1;
			if (o2.date().before(o1.date()))
				return -1;
			return 0;
		}
	};

	public static Comparator<CBReportDTO> compareCBReport = new Comparator<CBReportDTO>() {
		@Override
		public int compare(CBReportDTO o1, CBReportDTO o2) {
			if (o1.getDate().before(o2.getDate()))
				return 1;
			if (o2.getDate().before(o1.getDate()))
				return -1;
			return 0;
		}
	};

	public static Comparator<ActivityDTO> compareActivity = new Comparator<ActivityDTO>() {
		@Override
		public int compare(ActivityDTO o1, ActivityDTO o2) {
			if (o1.getDate().before(o2.getDate()))
				return 1;
			if (o2.getDate().before(o1.getDate()))
				return -1;
			return 0;
		}
	};

	public static Comparator<PartnerCouponsDTO> compareCoupons = new Comparator<PartnerCouponsDTO>() {
		@Override
		public int compare(PartnerCouponsDTO o1, PartnerCouponsDTO o2) {
			if (o1.getCreatedOn().after(o2.getCreatedOn()))
				return -1;
			else if (o2.getCreatedOn().after(o1.getCreatedOn()))
				return 1;
			return 0;
		}
	};

	public static Comparator<MergedProductInfoDTO> compareMergeProductInfoOnPrice = new Comparator<MergedProductInfoDTO>() {
		@Override
		public int compare(MergedProductInfoDTO o1, MergedProductInfoDTO o2) {
			if (o1.getBestPrice() > o2.getBestPrice())
				return -1;
			else if (o1.getBestPrice() < o2.getBestPrice())
				return 1;
			return 0;
		}
	};

	public static Comparator<MergedProductInfoDTO> compareMergeProductInfo = new Comparator<MergedProductInfoDTO>() {
		@Override
		public int compare(MergedProductInfoDTO o1, MergedProductInfoDTO o2) {
			if (o1.getCreatedOn().after(o2.getCreatedOn()))
				return -1;
			else if (o2.getCreatedOn().after(o1.getCreatedOn()))
				return 1;
			return 0;
		}
	};

	public final static boolean containsDigit(String s) {
		boolean containsDigit = false;

		if (s != null && !s.isEmpty()) {
			for (char c : s.toCharArray()) {
				if (containsDigit = Character.isDigit(c)) {
					break;
				}
			}
		}

		return containsDigit;
	}

	public static String formatName(String name, String brand) {
		if (StringUtils.isBlank(name) || StringUtils.isBlank(brand)) {
			return null;
		}
		else {
			name = name.toLowerCase().replace("/", "%2F");
			brand = brand.toLowerCase().replace("/", "%2F");
		}
		try {
			name = URLEncoder.encode(name.toLowerCase(), "UTF-8");
			brand = URLEncoder.encode(brand.toLowerCase(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return brand + "/" + name;
	}

	public static String getName(String productBrand, String productSubBrand, String series, String model) {
		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isEmpty(productBrand)) {
			sb.append(productBrand.trim()).append(" ");
		}
		if (!StringUtils.isEmpty(productSubBrand)) {
			sb.append(productSubBrand.trim()).append(" ");
		}
		if (!StringUtils.isEmpty(series)) {
			sb.append(series.trim()).append(" ");
		}
		if (!StringUtils.isEmpty(model)) {
			sb.append(model.trim()).append(" ");
		}
		if (sb.length() > 0) {
			return sb.toString().trim();
		}
		return null;
	}

	public static Map<String, Map<String, String>> jsonToMap(String json) {
		try {
			if (StringUtils.isNotBlank(json)) {
				JSONArray array = new JSONArray(json);
				if (array != null && array.length() > 0) {
					Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						String key = obj.get("key").toString();
						if (!map.containsKey(key)) {
							map.put(key, new HashMap<String, String>());
						}
						Map<String, String> inMap = map.get(key);
						JSONArray values = obj.getJSONArray("values");
						if (values != null && values.length() > 0) {
							for (int j = 0; j < values.length(); j++) {
								JSONObject inObj = values.getJSONObject(j);
								String inKey = inObj.get("key").toString();
								JSONArray inValue = inObj.getJSONArray("value");
								if (inValue != null && inValue.length() > 0) {
									inMap.put(inKey, inValue.getString(0).toString());
								}
							}
						}
					}
					return map;
				}
			}

		} catch (JSONException e) {
			log.error("", e);
		}
		return null;
	}

	public static String flipkartUpdateFeatures(Map<String, Map<String, String>> map, String extKey, String inKey) {
		if (map != null && map.size() > 0) {
			Map<String, String> gf = map.get(extKey);
			if (gf != null && gf.size() > 0) {
				String hc = gf.get(inKey);
				if (StringUtils.isNotBlank(hc)) {
					return hc.trim().toUpperCase().replaceAll("[^\\p{L}\\p{Z}\\p{N}\\.]", " ").replaceAll("\\s+", " ");
				}
			}
		}
		return null;
	}

	public static void listUpperCase(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i).toUpperCase());
		}
	}

	public static Date getExpectedCBDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(System.currentTimeMillis()));

		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		if (month > 9) {
			++year;
		}
		month = (month+2)%12;
		c.set(Calendar.MONTH, month);
		c.set(Calendar.YEAR, year);

		return DateUtils.truncate(c.getTime(), Calendar.MONTH);
	}
}
