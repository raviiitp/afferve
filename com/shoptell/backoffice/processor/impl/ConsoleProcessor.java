/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.processor.impl;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

@Named
public class ConsoleProcessor extends DataProcessor {

	private static final String EXTRA_WORDS = "(\\d+\\s*)?GAM(ES?|ING)|CONSOLES?|\\sWITH|\\sIN\\s|MULTI\\s|VIDEO|SYSTEM|FREEBIES|CUSTOM|"
			+ "PAINTED|SPECIAL|EDITION|LIMITED|ULTIMATE|BRAND|NEW|FACTORY|SEALED|201\\d";
	private static final String SPECIAL_SYMBOLS = "[^\\p{L}\\p{Z}\\p{N}&'\\_\\.]";

	@Override
	public ReviewedProductInfoDTO shopcluesProcess(HomeProductInfoDTO prod) {
		return null;
	}

	@Override
	public ReviewedProductInfoDTO ebayProcess(HomeProductInfoDTO prod) {
		return null;
	}

	@Override
	public ReviewedProductInfoDTO snapdealProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle().toUpperCase();
		String productBrand = prod.getProductBrand().toUpperCase();
		String model = "";
		String type = "CONSOLE";

		if (title.contains("NOTEBOOK")) {
			return null;
		}

		if (productBrand.contains("XBOX") || title.contains("XBOX")) {
			productBrand = "MICROSOFT";
		}
		else if (productBrand.contains("NINTENDO") || title.contains("NINTENDO")) {
			productBrand = "NINTENDO";
		}
		else if (productBrand.contains("PLAYSTATION") || title.contains("PLAYSTATION")) {
			productBrand = "SONY";
		}

		if (title.contains(productBrand)) {
			title = title.replace(productBrand, "");
		}
		if (title.contains("HANDHELD") || title.contains("PORTABLE")) {
			type = "PORTABLE";
		}

		String model_regex = "(PLAY\\s?STATION\\s?\\d)|(PS\\s?\\d)|(XBOX\\s?(360|ONE))|((PLAY\\s?STATION\\s?PORTABLE)|(PSP))";
		Pattern pattern = Pattern.compile(model_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			String tmp = null;
			if (isMatchPresent(match, 1)) {
				tmp = match.group(1);
			}
			else if (isMatchPresent(match, 2)) {
				tmp = match.group(2);
			}
			if (StringUtils.isNotBlank(tmp)) {
				tmp = tmp.trim().replace(" ", "");
				char num = tmp.charAt(tmp.length() - 1);
				if (num > '1' && num < '5') {
					model = "PLAYSTATION " + num + " (PS" + num + ")";
					title = title.replaceAll("\\(?(PLAY\\s?STATION\\s?" + num + ")|(PS\\s?" + num + ")\\)?", "");
				}
			}
			else if (isMatchPresent(match, 4)) {
				tmp = match.group(4);
				if (StringUtils.isNotBlank(tmp)) {
					model = "XBOX " + tmp;
					title = title.replaceAll("XBOX\\s?" + tmp, "");
				}
			}
			else if (isMatchPresent(match, 5)) {
				tmp = match.group(5);
				if (StringUtils.isNotBlank(tmp)) {
					model = "PSP";
					if ("SONY".equalsIgnoreCase(productBrand)) {
						model = "PLAYSTATION PORTABLE (PSP)";
					}
					title = title.replaceAll("(PLAY\\s?STATION\\s?PORTABLE)|(PSP)", "");
					type = "PORTABLE";
				}
			}
		}

		String capacity = null;
		String capacity_regex = "(\\d+)(\\s)*(G|M|T)B";
		pattern = Pattern.compile(capacity_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (NumberUtils.isNumber(tmp)) {
					capacity = tmp + match.group(3) + "B";
				}
			}
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
			}
		}

		if (StringUtils.isNotBlank(capacity)) {
			capacity = capacity.replaceAll("\\s", "");
		}

		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll(EXTRA_WORDS, "");
			title = title.replace("+", "&").replace(" AND", "&");
			title = removeDuplicates(title);
			title = title.replaceAll(SPECIAL_SYMBOLS, " ").replaceAll("\\s+", " ").trim();
			if ("CONSOLE".equalsIgnoreCase(type)) {
				if (title.equals("PSVITA") || title.contains("DS")) {
					type = "PORTABLE";
				}
			}
			if (title.endsWith("&")) {
				title = title.substring(0, title.length() - 1).trim();
			}
		}
		if (StringUtils.isNotBlank(title)) {
			if (StringUtils.isNotBlank(model)) {
				model += " " + title;
			}
			else {
				model = title;
			}
		}
		// System.out.println(prod.getOriginalTitle() + " | " + productBrand +
		// " | " + model + " | " + capacity + " | " + type);

		ReviewedProductInfoDTO info = populateDto(productBrand, null, null, model, type, capacity, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle().toUpperCase();
		String productBrand = prod.getProductBrand().toUpperCase();
		String model = "";
		String type = "CONSOLE";

		if (title.contains("NOTEBOOK")) {
			return null;
		}

		if (productBrand.contains("XBOX") || title.contains("XBOX")) {
			productBrand = "MICROSOFT";
		}
		else if (productBrand.contains("NINTENDO") || title.contains("NINTENDO")) {
			productBrand = "NINTENDO";
		}
		else if (productBrand.contains("PLAYSTATION") || title.contains("PLAYSTATION")) {
			productBrand = "SONY";
		}

		if (title.contains(productBrand)) {
			title = title.replace(productBrand, "");
		}
		if (title.contains("HANDHELD") || title.contains("PORTABLE")) {
			type = "PORTABLE";
		}

		String model_regex = "(PLAY\\s?STATION\\s?\\d)|(PS\\s?\\d)|(XBOX\\s?(360|ONE))|((PLAY\\s?STATION\\s?PORTABLE)|(PSP))";
		Pattern pattern = Pattern.compile(model_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			String tmp = null;
			if (isMatchPresent(match, 1)) {
				tmp = match.group(1);
			}
			else if (isMatchPresent(match, 2)) {
				tmp = match.group(2);
			}
			if (StringUtils.isNotBlank(tmp)) {
				tmp = tmp.trim().replace(" ", "");
				char num = tmp.charAt(tmp.length() - 1);
				if (num > '1' && num < '5') {
					model = "PLAYSTATION " + num + " (PS" + num + ")";
					title = title.replaceAll("\\(?(PLAY\\s?STATION\\s?" + num + ")|(PS\\s?" + num + ")\\)?", "");
				}
			}
			else if (isMatchPresent(match, 4)) {
				tmp = match.group(4);
				if (StringUtils.isNotBlank(tmp)) {
					model = "XBOX " + tmp;
					title = title.replaceAll("XBOX\\s?" + tmp, "");
				}
			}
			else if (isMatchPresent(match, 5)) {
				tmp = match.group(5);
				if (StringUtils.isNotBlank(tmp)) {
					model = "PSP";
					if ("SONY".equalsIgnoreCase(productBrand)) {
						model = "PLAYSTATION PORTABLE (PSP)";
					}
					title = title.replaceAll("(PLAY\\s?STATION\\s?PORTABLE)|(PSP)", "");
					type = "PORTABLE";
				}
			}
		}

		String capacity = null;
		String capacity_regex = "(\\d+)(\\s)*(G|M|T)B";
		pattern = Pattern.compile(capacity_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (NumberUtils.isNumber(tmp)) {
					capacity = tmp + match.group(3) + "B";
				}
			}
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
			}
		}

		if (StringUtils.isNotBlank(capacity)) {
			capacity = capacity.replaceAll("\\s", "");
		}

		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll(EXTRA_WORDS, "");
			title = title.replace("+", "&").replace(" AND", "&");
			title = removeDuplicates(title);
			title = title.replaceAll(SPECIAL_SYMBOLS, " ").replaceAll("\\s+", " ").trim();
			if ("CONSOLE".equalsIgnoreCase(type)) {
				if (title.equals("PSVITA") || title.contains("DS")) {
					type = "PORTABLE";
				}
			}
			if (title.endsWith("&")) {
				title = title.substring(0, title.length() - 1).trim();
			}
		}
		if (StringUtils.isNotBlank(title)) {
			if (StringUtils.isNotBlank(model)) {
				model += " " + title;
			}
			else {
				model = title;
			}
		}
		// System.out.println(prod.getOriginalTitle() + " | " + productBrand +
		// " | " + model + " | " + capacity + " | " + type);

		ReviewedProductInfoDTO info = populateDto(productBrand, null, null, model, type, capacity, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle().toUpperCase();
		String productBrand = prod.getProductBrand().toUpperCase();
		String model = "";
		String type = "CONSOLE";

		if (title.contains("NOTEBOOK")) {
			return null;
		}

		if (productBrand.contains("XBOX") || title.contains("XBOX")) {
			productBrand = "MICROSOFT";
		}
		else if (productBrand.contains("NINTENDO") || title.contains("NINTENDO")) {
			productBrand = "NINTENDO";
		}
		else if (productBrand.contains("PLAYSTATION") || title.contains("PLAYSTATION")) {
			productBrand = "SONY";
		}

		if (title.contains(productBrand)) {
			title = title.replace(productBrand, "");
		}
		if (title.contains("HANDHELD") || title.contains("PORTABLE")) {
			type = "PORTABLE";
		}

		String model_regex = "(PLAY\\s?STATION\\s?\\d)|(PS\\s?\\d)|(XBOX\\s?(360|ONE))|((PLAY\\s?STATION\\s?PORTABLE)|(PSP))";
		Pattern pattern = Pattern.compile(model_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			String tmp = null;
			if (isMatchPresent(match, 1)) {
				tmp = match.group(1);
			}
			else if (isMatchPresent(match, 2)) {
				tmp = match.group(2);
			}
			if (StringUtils.isNotBlank(tmp)) {
				tmp = tmp.trim().replace(" ", "");
				char num = tmp.charAt(tmp.length() - 1);
				if (num > '1' && num < '5') {
					model = "PLAYSTATION " + num + " (PS" + num + ")";
					title = title.replaceAll("\\(?(PLAY\\s?STATION\\s?" + num + ")|(PS\\s?" + num + ")\\)?", "");
				}
			}
			else if (isMatchPresent(match, 4)) {
				tmp = match.group(4);
				if (StringUtils.isNotBlank(tmp)) {
					model = "XBOX " + tmp;
					title = title.replaceAll("XBOX\\s?" + tmp, "");
				}
			}
			else if (isMatchPresent(match, 5)) {
				tmp = match.group(5);
				if (StringUtils.isNotBlank(tmp)) {
					model = "PSP";
					if ("SONY".equalsIgnoreCase(productBrand)) {
						model = "PLAYSTATION PORTABLE (PSP)";
					}
					title = title.replaceAll("(PLAY\\s?STATION\\s?PORTABLE)|(PSP)", "");
					type = "PORTABLE";
				}
			}
		}

		String capacity = null;
		String capacity_regex = "(\\d+)(\\s)*(G|M|T)B";
		pattern = Pattern.compile(capacity_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (NumberUtils.isNumber(tmp)) {
					capacity = tmp + match.group(3) + "B";
				}
			}
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
			}
		}

		if (StringUtils.isNotBlank(capacity)) {
			capacity = capacity.replaceAll("\\s", "");
		}

		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll(EXTRA_WORDS, "");
			title = title.replace("+", "&").replace(" AND", "&");
			title = removeDuplicates(title);
			title = title.replaceAll(SPECIAL_SYMBOLS, " ").replaceAll("\\s+", " ").trim();
			if ("CONSOLE".equalsIgnoreCase(type)) {
				if (title.equals("PSVITA") || title.contains("DS")) {
					type = "PORTABLE";
				}
			}
			if (title.endsWith("&")) {
				title = title.substring(0, title.length() - 1).trim();
			}
		}

		if (StringUtils.isNotBlank(title)) {
			if (StringUtils.isNotBlank(model)) {
				model += " " + title;
			}
			else {
				model = title;
			}
		}
		// System.out.println(prod.getOriginalTitle() + " | " + productBrand +
		// " | " + model + " | " + capacity + " | " + type);
		ReviewedProductInfoDTO info = populateDto(productBrand, null, null, model, type, capacity, prod);
		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String productSubBrand, String productSeries, String model, String type, String capacity,
			HomeProductInfoDTO prod) {
		String color = null;
		if (StringUtils.isNotBlank(model)) {
			StringBuilder sb = new StringBuilder();
			model = BackofficeUtil.recursiveRemoveColor(model, sb);
			if (StringUtils.isNotBlank(sb.toString())) {
				color = sb.toString();
			}
		}
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, productSubBrand, productSeries, model, color, new HashMap<String, String>(),
				prod, null);

		info.getProperties().put("CAPACITY", capacity == null ? "null" : capacity);
		info.getProperties().put("TYPE", type == null ? "null" : type);

		return info;
	}
}
