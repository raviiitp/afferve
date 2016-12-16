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
public class WashingmachineProcessor extends DataProcessor {

	private static final String load_type_regex = "(TOP|FRONT)-?\\s*LOAD(ING)?";

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
			title = title.replaceFirst(productBrand, "");
		}

		if (title.contains("WASHING MACHINE")) {
			int indx = title.lastIndexOf("WASHING MACHINE");
			if (indx > -1) {
				title = title.substring(0, indx - 1);
			}
		}
		else {
			return null;
		}

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.trim();
			title = title.replace(color, "");
		}

		String function_type = null;
		String function_type_regex = "(FULLY-?\\s*)?(SEMI-?\\s*)?AUTOMATIC";
		Pattern pattern = Pattern.compile(function_type_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				function_type = match.group(0);
			}

			if (StringUtils.isNotBlank(function_type)) {

				title = title.replace(function_type, "");

				function_type = function_type.replaceAll("\\/|\\(|\\)|\\-|\\,|\\+", " ");
				function_type = function_type.replaceAll("\\s+", " ").trim();
			}
		}

		String load_type = null;
		pattern = Pattern.compile(load_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				load_type = match.group(0);
			}
			if (StringUtils.isNotBlank(load_type)) {
				title = title.replace(load_type, "");
			}
			if (StringUtils.isNotBlank(load_type)) {
				if (load_type.startsWith("T")) {
					load_type = "TOP LOAD";
				}
				else if (load_type.startsWith("F")) {
					load_type = "FRONT LOAD";
				}
			}
		}

		String capacity = null;
		String capacity_type_regex = "(\\d+(.\\d+)?)\\s*KGS?\\.?";
		pattern = Pattern.compile(capacity_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				capacity = match.group(0);
				title = title.replace(capacity, "");
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (NumberUtils.isNumber(tmp)) {
					double val = Double.parseDouble(tmp);
					capacity = val + " KG";
				}
			}
		}

		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll("\\(.*\\)", "").replaceAll("\\s[-]", "").replaceAll("\\s+", " ").trim();
		}

		String model = null;
		if (StringUtils.isNotBlank(title)) {
			model = title;
		}

		// System.out.println(productBrand + " | " + model + " | " + color +
		// " | " + capacity + " | " + function_type + " | " + load_type + " | "
		// + title + " | "+ prod.getTitle());

		// "FUNCTION TYPE","LOAD TYPE", "CAPACITY", "TYPE"
		ReviewedProductInfoDTO info = populateDto(productBrand, model, color, capacity, function_type, load_type, prod);
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
		int int_len = title.length();
		title = title.replaceAll("WASHING\\s+MACHINE|WASHER|WASHER\\s+DRYER", "");
		if (int_len == title.length()) {
			return null;
		}

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.trim();
			title = title.replace(color, "");
		}

		String function_type = null;
		String function_type_regex = "(FULLY-?\\s*)?(SEMI-?\\s*)?AUTOMATIC";
		Pattern pattern = Pattern.compile(function_type_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				function_type = match.group(0);
			}
			if (StringUtils.isNotBlank(function_type)) {

				title = title.replace(function_type, "");

				function_type = function_type.replaceAll("\\/|\\(|\\)|\\-|\\,|\\+", " ");
				function_type = function_type.replaceAll("\\s+", " ").trim();
			}
		}

		String load_type = null;
		pattern = Pattern.compile(load_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				load_type = match.group(0);
			}

			if (StringUtils.isNotBlank(load_type)) {

				title = title.replace(load_type, "");

				if (load_type.startsWith("T")) {
					load_type = "TOP LOAD";
				}
				else if (load_type.startsWith("F")) {
					load_type = "FRONT LOAD";
				}
			}
		}

		String capacity = null;
		String capacity_type_regex = "(\\d+(.\\d+)?)\\s+KGS?";
		pattern = Pattern.compile(capacity_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				capacity = match.group(0);
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (NumberUtils.isNumber(tmp)) {
					double val = Double.parseDouble(tmp);
					capacity = val + " KG";
				}
			}
			title = title.replace(capacity, "");
		}

		String model = prod.getModel();

		if (StringUtils.isNotBlank(model)) {
			model = model.toUpperCase().replace(productBrand, "").replace("/", " ").trim();
			model = model.replaceAll("//s+", " ");
		}

		/*
		 * System.out.println(productBrand + " | " + model + " | " + color +
		 * " | " + capacity + " | " + function_type + " | " + load_type + " | "
		 * + title + " | " + prod.getTitle());
		 */

		// "FUNCTION TYPE","LOAD TYPE", "CAPACITY", "TYPE"
		ReviewedProductInfoDTO info = populateDto(productBrand, model, color, capacity, function_type, load_type, prod);
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
		int int_len = title.length();
		title = title.replaceAll("WASHING\\s+MACHINE|WASHER|WASHER\\s+DRYER", "");
		if (int_len == title.length()) {
			return null;
		}

		String function_type = null;
		String function_type_regex = "(FULLY-?\\s*)?(SEMI-?\\s*)?AUTOMATIC";
		Pattern pattern = Pattern.compile(function_type_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				function_type = match.group(0);
			}
			if (StringUtils.isNotBlank(function_type)) {

				title = title.replace(function_type, "");

				function_type = function_type.replaceAll("\\/|\\(|\\)|\\-|\\,|\\+", " ");
				function_type = function_type.replaceAll("\\s+", " ").trim();
			}
		}

		String load_type = null;
		pattern = Pattern.compile(load_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				load_type = match.group(0);
			}
			if (StringUtils.isNotBlank(load_type)) {

				title = title.replace(load_type, "");

				if (load_type.startsWith("T")) {
					load_type = "TOP LOAD";
				}
				else if (load_type.startsWith("F")) {
					load_type = "FRONT LOAD";
				}
			}
		}

		String capacity = null;
		String capacity_type_regex = "(\\d+(.\\d+)?)\\s+KGS?";
		pattern = Pattern.compile(capacity_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				capacity = match.group(0);
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (NumberUtils.isNumber(tmp)) {
					double val = Double.parseDouble(tmp);
					capacity = val + " KG";
				}
			}
			if (StringUtils.isNotBlank(capacity)) {
				title = title.replace(capacity, "");
			}
		}

		String model = null;
		/*
		 * StringBuilder m = new StringBuilder(); String img =
		 * prod.getImageUrl(); if (StringUtils.isNotBlank(img)) { img =
		 * img.substring(img.lastIndexOf("/") + 1); String[] tmp =
		 * img.split("-"); if (tmp != null && tmp.length > 0) { for (int i = 0;
		 * i < tmp.length - 1; i++) { if (tmp[i].equalsIgnoreCase("200x200")) {
		 * break; } m.append(tmp[i]).append(" "); } } model = m.toString(); }
		 * 
		 * if (StringUtils.isNotBlank(model)) { model =
		 * model.toUpperCase().replace(productBrand, "").trim(); }
		 */

		/*
		 * System.out.println(productBrand + " | " + capacity + " | " +
		 * function_type + " | " + load_type + " | " + model + " | " + title +
		 * " | " + prod.getTitle());
		 */
		// "FUNCTION TYPE","LOAD TYPE", "CAPACITY", "TYPE"

		String color = prod.getColor();

		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());
		if (StringUtils.isBlank(model)) {
			model = flipkartUpdateFeatures(map, "General", "Model Name");
		}
		String tmp = flipkartUpdateFeatures(map, "General", "Shade");
		if (StringUtils.isNotBlank(tmp)) {
			color = tmp;
		}
		if (StringUtils.isBlank(capacity)) {
			capacity = flipkartUpdateFeatures(map, "General", "Washing Capacity");
		}
		if (StringUtils.isBlank(function_type)) {
			tmp = flipkartUpdateFeatures(map, "General", "Function Type");
			if (tmp != null) {
				function_type_regex = "(FULLY-?\\s*)?(SEMI-?\\s*)?AUTOMATIC";
				pattern = Pattern.compile(function_type_regex, Pattern.CASE_INSENSITIVE);
				match = pattern.matcher(tmp);
				if (match.find()) {
					if (isMatchPresent(match, 0)) {
						function_type = match.group(0);
					}
				}
			}
		}
		if (StringUtils.isBlank(load_type)) {
			tmp = flipkartUpdateFeatures(map, "General", "Function Type");
			if (tmp != null) {
				pattern = Pattern.compile(load_type_regex, Pattern.CASE_INSENSITIVE);
				match = pattern.matcher(tmp);
				if (match.find()) {
					if (isMatchPresent(match, 0)) {
						load_type = match.group(0);
					}
					if (StringUtils.isNotBlank(load_type)) {
						if (load_type.startsWith("T")) {
							load_type = "TOP LOAD";
						}
						else if (load_type.startsWith("F")) {
							load_type = "FRONT LOAD";
						}
					}
				}
			}
		}

		ReviewedProductInfoDTO info = populateDto(productBrand, model, color, capacity, function_type, load_type, prod);
		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String model, String color, String capacity, String function_type, String load_type,
			HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
		}
		
		if (StringUtils.isNotBlank(model)){
			model = model.replaceAll("[^\\p{L}\\p{Z}\\p{N}\\.]", " ").replaceAll("\\s+", " ").trim();
		}
		
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, null, null, model, color, new HashMap<String, String>(), prod, null);

		// "FUNCTION TYPE","LOAD TYPE", "CAPACITY", "TYPE"

		info.getProperties().put("FUNCTION TYPE", function_type == null ? "null" : function_type);
		info.getProperties().put("LOAD TYPE", load_type == null ? "null" : load_type);
		info.getProperties().put("CAPACITY", capacity == null ? "null" : capacity);

		return info;
	}

	/*
	 * public static void main(String[] args) { WashingmachineProcessor p = new
	 * WashingmachineProcessor(); HomeProductInfoDTO prod = new
	 * HomeProductInfoDTO();
	 * 
	 * prod.setTitle(
	 * "SAMSUNG WT655QPNDRP/XTL SEMI AUTOMATIC WASHING MACHINE (6.5 KG)  ");
	 * prod.setProductBrand("SAMSUNG"); prod.setModel("WT655QPNDRP/XTL/");
	 * 
	 * p.amazonProcess(prod); }
	 */
}
