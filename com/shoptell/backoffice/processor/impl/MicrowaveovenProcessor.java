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

import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

@Named
public class MicrowaveovenProcessor extends DataProcessor {

	private static final String type_regex = "GRILL|SOLO|CONVE(C|N)TION|OTG|(OVEN\\s+TOAST\\s+GRILLER)";

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

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.trim();
			title = title.replace(color, "");
		}

		int int_len = title.length();
		title = title.replaceAll("MICROWAVE\\s+OVEN", "");
		if (int_len == title.length()) {
			if (!title.contains("OVEN")) {
				return null;
			}
		}

		String capacity = null;
		String capacity_regex = "(\\d+(\\.\\d+)?)([\\s-])*L(I?TRE?S?(\\.)?)?";
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
				capacity = val + " L";
			}
		}

		String type = null;
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

		if ("CONVENTION".equals(type)) {
			type = "CONVECTION";
		}

		if (StringUtils.isNotBlank(type) && ("OTG".equalsIgnoreCase(type) || type.equalsIgnoreCase("OVEN TOAST GRILLER"))) {
			type = "OTG";
			title = title.replace("OTG", "").replace("OVEN TOAST GRILLER", "");
		}

		if (StringUtils.isNotBlank(title)) {
			pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
			match = pattern.matcher(title);
			while (match.find()) {
				if (isMatchPresent(match, 0)) {
					String tmp = match.group(0);
					if (StringUtils.isNotBlank(tmp) && StringUtils.isBlank(color)) {
						color = tmp;
					}
					title = title.replace(tmp, "");
				}
			}
			title = title.replaceAll("\\s(-|-?\\d{1,2}|[A-Z])\\s", " ")
					.replaceAll("\\s(&|AND|BUILT|IN|OVEN|MICROWAVE|PIZZA|KEBAB|MAKER|TRANSPARENT|SOLID|WITH|HANDLE|DESIGN|BELOW|FULL|SIZE|ROUND|OFF)", " ")
					.replaceAll("(ELECTRIC)\\s", " ").replaceAll("(\\s[-])|([-]\\s)", " ").replaceAll("\\(.*\\)", " ");
		}

		title = title.replaceAll("\\s+", " ");

		// System.out.println(productBrand+" | "+capacity+" | "+type+" | "+color+" | "+title+" | "+prod.getTitle());

		String model = null;

		if (StringUtils.isNotBlank(title)) {
			model = title.trim();
		}

		ReviewedProductInfoDTO info = populateDto(productBrand, model, capacity, type, color, prod);

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

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.trim();
			title = title.replace(color, "");
		}

		String capacity = null;
		String capacity_regex = "(\\d+(\\.\\d+)?)([\\s-])+L(ITRES?)?";
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
				capacity = val + " L";
			}
		}

		String type = null;
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

		if ("CONVENTION".equals(type)) {
			type = "CONVECTION";
		}

		if (StringUtils.isNotBlank(type) && ("OTG".equalsIgnoreCase(type) || type.equalsIgnoreCase("OVEN TOAST GRILLER"))) {
			type = "OTG";
			title = title.replace("OTG", "").replace("OVEN TOAST GRILLER", "");
		}

		String model = prod.getModel();
		if (StringUtils.isNotBlank(model)) {
			model = model.toUpperCase().trim();
		}
		// System.out.println(productBrand+" | "+model+" | "+capacity+" | "+type+" | "+color+" | "+prod.getTitle());
		// "CAPACITY","TYPE","COLOR"

		ReviewedProductInfoDTO info = populateDto(productBrand, model, capacity, type, color, prod);

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
		title = title.replaceAll("MICROWAVE OVEN", "");
		if (int_len == title.length()) {
			return null;
		}

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.trim();
			title = title.replace(color, "");
		}
		String capacity = null;
		String capacity_regex = "(\\d+(\\.\\d+)?)([\\s-])*L(ITE?RE?S?)?";
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
				capacity = val + " L";
			}
		}

		String type = null;
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

		if (StringUtils.isNotBlank(type) && ("OTG".equalsIgnoreCase(type) || type.equalsIgnoreCase("OVEN TOAST GRILLER"))) {
			type = "OTG";
			title = title.replace("OTG", "").replace("OVEN TOAST GRILLER", "");
		}

		String model = null;

		if (StringUtils.isNotBlank(title)) {
			model = title.trim();
		}

		/*
		 * System.out.println(productBrand + " | " + color + " | " + capacity +
		 * " | " + type + " | " + title + " | " + prod.getTitle());
		 */
		
		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());
		
		if (StringUtils.isBlank(model)) {
			model = flipkartUpdateFeatures(map, "General", "Model Name");
		}
		if (StringUtils.isBlank(capacity)) {
			capacity = flipkartUpdateFeatures(map, "General", "Capacity");
		}
		if (StringUtils.isBlank(type)) {
			type = flipkartUpdateFeatures(map, "General", "Type");
		}
		if (StringUtils.isBlank(color)) {
			color = flipkartUpdateFeatures(map, "General", "Shade");
		}
		
		if ("CONVENTION".equals(type)) {
			type = "CONVECTION";
		}

		// "CAPACITY","TYPE","COLOR"
		ReviewedProductInfoDTO info = populateDto(productBrand, model, capacity, type, color, prod);

		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String model, String capacity, String type, String color, HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
		}
		
		if (StringUtils.isNotBlank(model)){
			model = model.replaceAll("[^\\p{L}\\p{Z}\\p{N}\\.]", " ").replaceAll("\\s+", " ").trim();
		}
		
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, null, null, model, color, new HashMap<String, String>(), prod, null);

		info.getProperties().put("CAPACITY", capacity == null ? "null" : capacity);
		info.getProperties().put("TYPE", type == null ? "null" : type);
		info.getProperties().put("COLOR", color == null ? "null" : color);

		return info;
	}

	/*
	 * public static void main(String[] args) { MicrowaveovenProcessor p = new
	 * MicrowaveovenProcessor(); HomeProductInfoDTO prod = new
	 * HomeProductInfoDTO();
	 * prod.setTitle("BAJAJ 35 LTRS 3500 TMCSS OTG MICROWAVE OVENWHITE");
	 * prod.setProductBrand("BAJAJ"); System.out.println(
	 * p.snapdealProcess(prod)); }
	 */
}
