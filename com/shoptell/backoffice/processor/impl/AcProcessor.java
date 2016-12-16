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

import static com.shoptell.backoffice.BackofficeUtil.flipkartUpdateFeatures;
import static com.shoptell.backoffice.BackofficeUtil.jsonToMap;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

@Named
public class AcProcessor extends DataProcessor {

	@Override
	public ReviewedProductInfoDTO shopcluesProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReviewedProductInfoDTO ebayProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReviewedProductInfoDTO snapdealProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replace(productBrand, "");
		}

		if (title.contains("REMOTE")) {
			return null;
		}

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase().trim();
			title = title.replace(color, "");
		}

		String type = prod.getType();

		if (StringUtils.isNotBlank(type)) {
			if ("AIR CONDITIONERS SPLIT AC".equalsIgnoreCase(type)) {
				type = "SPLIT AC";
			}
			else if ("AIR CONDITIONERS WINDOW AC".equalsIgnoreCase(type)) {
				type = "WINDOW AC";
			}
		}

		int int_len = title.length();
		title = title.replaceAll("(WINDOW AIR CONDITIONER|SPLIT AIR CONDITIONER)", "");
		if (int_len == title.length()) {
			return null;
		}

		/*
		 * if (title.contains("INVERTER")) { type = "INVERTER " + type; title =
		 * title.replace("INVERTER", ""); }
		 */

		String capacity = null;
		String capacity_regex = "(\\d*(\\.\\d+)?) TONS?";
		Pattern pattern = Pattern.compile(capacity_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				capacity = match.group(0);
			}
			if (StringUtils.isNotBlank(capacity)) {
				title = title.replace(capacity, "");
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				double val = Double.parseDouble(tmp);
				capacity = val + " TON";
			}
		}

		String star = null;
		String star_regex = "(\\d+(\\.\\d+)?) STARS?";
		pattern = Pattern.compile(star_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				star = match.group(0);
			}
			if (StringUtils.isNotBlank(star)) {
				title = title.replace(star, "");
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				double val = Double.parseDouble(tmp);
				star = val + " STAR";
			}
		}

		if (title.contains("NON INVERTER")) {
			title = title.replace("NON INVERTER", "");
		}
		else if (title.contains("INVERTER")) {
			title = title.replace("INVERTER", "");
			if (StringUtils.isBlank(star)) {
				star = "INVERTER";
			}
		}

		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll("(WINDOW)|(SPLIT)|(AC)|(AIR)|(CONDITIONER)", "");
			title = title.replaceAll("(\\d(\\.\\d)?-)|(\\s\\d(\\.\\d)?\\s)|(\\(.*(\\))?)|(\\s-)", "");
			title = title.replaceAll("\\s+", " ").trim();
		}

		String model = null;
		if (StringUtils.isNotBlank(title)) {
			model = title;
		}

		// System.out.println(prod.getTitle()+" | "+productBrand+" | "+capacity+" | "+star+" | "+type+" | "+title+" | "+color);

		ReviewedProductInfoDTO info = populateDto(productBrand, model, capacity, star, type, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replace(productBrand, "");
		}

		String model = prod.getModel();
		if (StringUtils.isNotBlank(model)) {
			model = model.toUpperCase().trim();
			title = title.replace(model, "");
		}

		String capacity = null;
		String star = null;
		String color = prod.getColor();
		String regex = "\\(((\\d+(\\.\\d+)?)\\s+TONS?,\\s+)?((\\d+)\\s+STAR\\s+RATING,\\s+)?(.*?)\\)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		String cut = null;
		while (match.find()) {
			if (isMatchPresent(match, 0)) {
				cut = match.group(0);
			}
			if (isMatchPresent(match, 2)) {
				String tmp = match.group(2);
				double val = Double.parseDouble(tmp);
				capacity = val + " TON";
			}
			if (isMatchPresent(match, 5)) {
				String tmp = match.group(5);
				double val = Double.parseDouble(tmp);
				star = val + " STAR";
			}
			if (StringUtils.isNotBlank(color)) {
				if (isMatchPresent(match, 6)) {
					color = match.group(6);
				}
			}
		}

		if (StringUtils.isNotBlank(cut)) {
			title = title.split(cut)[0];
		}

		if (title.contains("NON INVERTER")) {
			title = title.replace("NON INVERTER", "");
		}
		else if (title.contains("INVERTER")) {
			title = title.replace("INVERTER", "");
			if (StringUtils.isBlank(star)) {
				star = "INVERTER";
			}
		}

		String type = null;
		String type_regex = "(SPLIT|WINDOW)\\s+AC";
		pattern = Pattern.compile(type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				type = match.group(0);
			}
			if (StringUtils.isNotBlank(type)) {
				title = title.replace(type, "");
			}
		}

		/*
		 * System.out.println(prod.getTitle() + " | " + productBrand + " | " +
		 * capacity + " | " + star + " | " + type + " | " + model + " | " +
		 * title + " | " + color);
		 */
		ReviewedProductInfoDTO info = populateDto(productBrand, model, capacity, star, type, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replace(productBrand, "");
		}
		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.trim();
			title = title.replace(color, "");
		}
		String capacity = null;
		String capacity_regex = "(\\d+(\\.\\d+)?) TONS?";
		Pattern pattern = Pattern.compile(capacity_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				capacity = match.group(0);
			}
			if (StringUtils.isNotBlank(capacity)) {
				title = title.replace(capacity, "");
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				double val = Double.parseDouble(tmp);
				capacity = val + " TON";
			}
		}

		String star = null;
		String star_regex = "(\\d+(\\.\\d+)?) STARS?";
		pattern = Pattern.compile(star_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				star = match.group(0);
			}
			if (StringUtils.isNotBlank(star)) {
				title = title.replace(star, "");
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				double val = Double.parseDouble(tmp);
				star = val + " STAR";
			}
		}
		if (title.contains("INVERTER")) {
			title = title.replace("INVERTER", "");
			if (StringUtils.isBlank(star)) {
				star = "INVERTER";
			}
		}

		String type = null;
		String type_regex = "(SPLIT|WINDOW) AC";
		pattern = Pattern.compile(type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				type = match.group(0);
			}
			if (StringUtils.isNotBlank(type)) {
				title = title.replace(type, "");
			}
		}

		String model = null;
		String m1 = null;
		String img = prod.getImageUrl();
		if (StringUtils.isNotBlank(img)) {
			img = img.substring(img.lastIndexOf("/") + 1);
			String[] tmp = img.split("-");
			if (tmp != null && tmp.length > 0) {
				for (int i = 0; i < tmp.length - 1; i++) {
					if (tmp[i].equalsIgnoreCase("SPLIT") || tmp[i].equalsIgnoreCase("WINDOW")) {
						model = tmp[i + 1];
						if (i + 1 < tmp.length)
							m1 = tmp[i + 2];
						break;
					}
				}
			}
		}

		if (StringUtils.isNotBlank(m1) && !"200x200".equalsIgnoreCase(m1)) {
			if (m1.length() > 1 && !NumberUtils.isNumber(m1)) {
				model += " " + m1;
			}
		}

		title = title.replaceAll("\\s+", " ").trim();

		if (StringUtils.isNotBlank(model)) {
			model = model.toUpperCase();
		}

		/*
		 * System.out.println(prod.getTitle() + " | " + productBrand + " | " +
		 * capacity + " | " + star + " | " + type + " | " + model + " | " +
		 * title);
		 */

		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());
		if (StringUtils.isBlank(model)) {
			model = flipkartUpdateFeatures(map, "General", "Model Name");
		}
		if (StringUtils.isBlank(capacity)) {
			capacity = flipkartUpdateFeatures(map, "General", "Capacity in Tons");
		}
		if (StringUtils.isBlank(star)) {
			star = flipkartUpdateFeatures(map, "General", "Star Rating");
			if (star != null) {
				star += " STAR";
			}
		}
		if (StringUtils.isBlank(type)) {
			type = flipkartUpdateFeatures(map, "General", "Type");
			if (type != null) {
				type += " AC";
			}
		}
		if (StringUtils.isBlank(color)) {
			color = flipkartUpdateFeatures(map, "General", "Color");
		}

		ReviewedProductInfoDTO info = populateDto(productBrand, model, capacity, star, type, color, prod);
		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String model, String capacity, String star, String type, String color,
			HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
		}
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, null, null, model, color, new HashMap<String, String>(), prod, null);

		info.getProperties().put("ENERGY RATING", star == null ? "null" : star);
		info.getProperties().put("CAPACITY", capacity == null ? "null" : capacity);
		info.getProperties().put("TYPE", type == null ? "null" : type);

		return info;
	}

}
