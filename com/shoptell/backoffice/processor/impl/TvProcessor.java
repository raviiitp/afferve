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
public class TvProcessor extends DataProcessor {

	private static final String resolution_regex = "FULL\\s+HD|HD\\s+READY|ULTRA\\s+HD(\\s+\\(4K\\))?|UHD|FHD|HD|WXGA";

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

		int int_len = title.length();
		title = title.replaceAll("TELEVISION|TV", "");
		if (int_len == title.length()) {
			return null;
		}

		String resolution = "HD";
		String resolution_regex = "FULL\\s+HD|HD\\s+READY|(\\(4K\\)\\s+)?ULTRA\\s+HD|UHD|FHD|HD";
		Pattern pattern = Pattern.compile(resolution_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				resolution = match.group(0);
			}
			title = title.replace(resolution, "");
			title = title.replace("4K", "");

			if ("FHD".equalsIgnoreCase(resolution)) {
				resolution = "FULL HD";
			}
			else if ("UHD".equalsIgnoreCase(resolution) || "ULTRA HD".equalsIgnoreCase(resolution) || "4K ULTRA HD".equalsIgnoreCase(resolution)) {
				resolution = "ULTRA HD 4K";
			}
		}

		String screen_type = "LCD";
		String screen_type_regex = "LED|OLED|PLASMA|LCD|CRT";
		pattern = Pattern.compile(screen_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				screen_type = match.group(0);
			}
			title = title.replace(screen_type, "");
		}

		String type = null;
		if (StringUtils.isNotBlank(title)) {
			if (title.contains("SMART")) {
				type = "SMART TV";
			}
			if (title.contains("3D")) {
				type = "3D TV";
			}
			if (title.contains("CURVED")) {
				type = "CURVED TV";
			}
		}

		String screen_size = null;
		String screen_size_regex = "\\(?(\\d+(\\.\\d+)?)([\\s-]*)(INC?H?(ES)?|\")\\)?";
		pattern = Pattern.compile(screen_size_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				screen_size = match.group(0);
				int indx = title.indexOf(screen_size) - 1;
				if (indx > 0) {
					title = title.substring(0, indx);
				}
				else {
					title = "";
				}
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (StringUtils.isNotBlank(tmp) && NumberUtils.isNumber(tmp)) {
					double val = Double.parseDouble(tmp);
					screen_size = val + "\"";
				}
			}
		}

		screen_size_regex = "\\d+(\\.\\d+)?\\s*CMS?\\s*(\\((\\d+(\\.\\d+)?)\\))?";
		pattern = Pattern.compile(screen_size_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				int indx = title.indexOf(match.group(0)) - 1;
				if (indx > 0) {
					title = title.substring(0, indx);
				}
				else {
					title = "";
				}
			}
			if (isMatchPresent(match, 3)) {
				String tmp = match.group(3);
				if (StringUtils.isBlank(screen_size) && StringUtils.isNotBlank(tmp) && NumberUtils.isNumber(tmp)) {
					double val = Double.parseDouble(tmp);
					screen_size = val + "\"";
				}
			}
		}

		String model = null;
		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll("(\\s[-])|(\\(.*\\))", "").replaceAll("\\s+", " ").trim();
			model = title;
		}

		// System.out.println(productBrand + " | "+title+" | " + resolution +
		// " | " + screen_size + " | " + screen_type + " | "+type+" | "+
		// prod.getTitle());

		// "SCREEN SIZE", "RESOLUTION", "SCREEN TYPE", "TYPE"
		ReviewedProductInfoDTO info = populateDto(productBrand, null, model, resolution, screen_size, type, screen_type, prod);
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
		title = title.replaceAll("TELEVISION|TV", "");
		if (int_len == title.length()) {
			return null;
		}
		title = title.replaceAll("\\d+(\\.\\d+)?\\s*CMS?", "");

		String resolution = "HD";
		String resolution_regex = "FULL\\s+HD|HD\\s+READY|(\\(4K\\)\\s+)?ULTRA\\s+HD|UHD|FHD|HD";
		Pattern pattern = Pattern.compile(resolution_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				resolution = match.group(0);
			}
			title = title.replace(resolution, "");
			title = title.replace("4K", "");

			if ("FHD".equalsIgnoreCase(resolution)) {
				resolution = "FULL HD";
			}
			else if ("UHD".equalsIgnoreCase(resolution) || "ULTRA HD".equalsIgnoreCase(resolution) || "4K ULTRA HD".equalsIgnoreCase(resolution)) {
				resolution = "ULTRA HD 4K";
			}
		}

		String screen_type = "LCD";
		String screen_type_regex = "LED|OLED|PLASMA|LCD|CRT";
		pattern = Pattern.compile(screen_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				screen_type = match.group(0);
			}
			title = title.replace(screen_type, "");
		}

		String type = null;
		if (StringUtils.isNotBlank(title)) {
			if (title.contains("SMART")) {
				type = "SMART TV";
			}
			if (title.contains("3D")) {
				type = "3D TV";
			}
			if (title.contains("CURVED")) {
				type = "CURVED TV";
			}
		}

		String screen_size = null;
		String screen_size_regex = "\\(?(\\d+(\\.\\d+)?)([\\s-]*)(INC?H?(ES)?|\")\\)?";
		pattern = Pattern.compile(screen_size_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				screen_size = match.group(0);
				int indx = title.indexOf(screen_size) - 1;
				if (indx > 0) {
					title = title.substring(0, indx);
				}
				else {
					title = "";
				}
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (StringUtils.isNotBlank(tmp) && NumberUtils.isNumber(tmp)) {
					double val = Double.parseDouble(tmp);
					screen_size = val + "\"";
				}
			}
		}
		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll("\\s+", " ").trim();
		}
		String model = prod.getModel();

		if (StringUtils.isNotBlank(model)) {
			model = model.toUpperCase().replace(productBrand, "").trim();
		}

		String productSubBrand = title;
		if (StringUtils.isNotBlank(productSubBrand)) {
			if (StringUtils.isNotBlank(model)) {
				if (model.contains(productSubBrand)) {
					productSubBrand = null;
				}
			}
			if (StringUtils.isNotBlank(productSubBrand)) {
				productSubBrand = productSubBrand.replaceAll("[^\\p{L}\\p{Z}\\p{N}&]", " ");
				if (StringUtils.isNotBlank(model)) {
					productSubBrand = productSubBrand.replace(model, "").trim();
				}
				else {
					model = productSubBrand.trim();
					productSubBrand = null;
				}
			}
		}

		if (StringUtils.isNotBlank(productSubBrand) && StringUtils.isNotBlank(model)) {
			String[] tmp = productSubBrand.split("\\s");
			if (tmp.length > 1) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < tmp.length; i++) {
					if (!model.contains(tmp[i])) {
						sb.append(tmp[i]).append(" ");
					}
				}
				if (sb.length() > 0) {
					productSubBrand = sb.toString().trim();
				}
			}
		}

		/*
		 * System.out.println(productBrand + " | " + productSubBrand + " | " +
		 * model + " | " + resolution + " | " + screen_size + " | " +
		 * screen_type + " | " + prod.getTitle());
		 */

		ReviewedProductInfoDTO info = populateDto(productBrand, null, model, resolution, screen_size, type, screen_type, prod);

		// "SCREEN SIZE", "RESOLUTION", "SCREEN TYPE", "TYPE"
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
		title = title.replaceAll("TELEVISION|TV", "");
		if (int_len == title.length()) {
			return null;
		}
		title = title.replaceAll("\\d+(\\.\\d+)?\\s*CMS?", "");

		String productSubBrand = null;

		String screen_size = null;
		String screen_size_regex = "(\\d+(.\\d+)?)\\s+INCH(ES)?|\\((\\d+(.\\d+)?)\\)";
		Pattern pattern = Pattern.compile(screen_size_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				screen_size = match.group(0);
			}
			if (StringUtils.isNotBlank(screen_size)) {
				String sb = title.substring(0, title.indexOf(screen_size));

				if (StringUtils.isNotBlank(sb)) {
					productSubBrand = sb.trim();
					title = title.replace(sb, "");
				}
			}

			if (StringUtils.isNotBlank(screen_size)) {
				title = title.replace(screen_size, "");
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (StringUtils.isNotBlank(tmp) && NumberUtils.isNumber(tmp)) {
					double val = Double.parseDouble(tmp);
					screen_size = val + "\"";
				}
			}
			if (isMatchPresent(match, 4)) {
				String tmp = match.group(4);
				if (StringUtils.isNotBlank(tmp) && NumberUtils.isNumber(tmp)) {
					double val = Double.parseDouble(tmp);
					screen_size = val + "\"";
				}
			}
		}

		String resolution = "HD";
		pattern = Pattern.compile(resolution_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				resolution = match.group(0);
			}
			title = title.replace(resolution, "");
			title = title.replace("4K", "");
			if ("FHD".equalsIgnoreCase(resolution)) {
				resolution = "FULL HD";
			}
			else if ("ULTRA HD (4K)".equalsIgnoreCase(resolution) || "UHD".equalsIgnoreCase(resolution) || "ULTRA HD".equalsIgnoreCase(resolution)) {
				resolution = "ULTRA HD 4K";
			}
		}

		String screen_type = "LCD";
		String screen_type_regex = "LED|OLED|PLASMA|LCD|CRT";
		pattern = Pattern.compile(screen_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				screen_type = match.group(0);
			}
			title = title.replace(screen_type, "");
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

		String type = null;
		if (StringUtils.isNotBlank(title)) {
			if (title.contains("SMART")) {
				type = "SMART TV";
			}
			if (title.contains("3D")) {
				type = "3D TV";
			}
			if (title.contains("CURVED")) {
				type = "CURVED TV";
			}
		}

		/*
		 * System.out.println(productBrand + " | " + productSubBrand + " | " +
		 * model + " | " + resolution + " | " + screen_size + " | " +
		 * screen_type + " | " + title + " | " + prod.getTitle());
		 */
		// "SCREEN SIZE", "RESOLUTION", "SCREEN TYPE", "TYPE"

		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());
		if (StringUtils.isBlank(model)) {
			model = flipkartUpdateFeatures(map, "General", "Model Name");
		}
		if ("HD".equals(resolution)) {
			String tmp = flipkartUpdateFeatures(map, "General", "HD Technology & Resolution");
			if (StringUtils.isNotBlank(tmp)) {
				pattern = Pattern.compile(resolution_regex, Pattern.CASE_INSENSITIVE);
				match = pattern.matcher(tmp);
				if (match.find()) {
					if (isMatchPresent(match, 0)) {
						resolution = match.group(0);
					}
					if ("FHD".equalsIgnoreCase(resolution)) {
						resolution = "FULL HD";
					}
					else if ("ULTRA HD (4K)".equalsIgnoreCase(resolution) || "UHD".equalsIgnoreCase(resolution) || "ULTRA HD".equalsIgnoreCase(resolution)) {
						resolution = "ULTRA HD 4K";
					}
				}
			}
		}

		if (StringUtils.isBlank(screen_size)) {
			String temp = flipkartUpdateFeatures(map, "General", "Display Size");
			if (StringUtils.isNotBlank(temp)) {
				pattern = Pattern.compile("\\((\\d+(.\\d+)?)\\)", Pattern.CASE_INSENSITIVE);
				match = pattern.matcher(temp);
				if (match.find()) {
					if (isMatchPresent(match, 1)) {
						String tmp = match.group(1);
						if (StringUtils.isNotBlank(tmp) && NumberUtils.isNumber(tmp)) {
							double val = Double.parseDouble(tmp);
							screen_size = val + "\"";
						}
					}
				}
			}
		}
		if (StringUtils.isBlank(type)) {
			if ("yes".equalsIgnoreCase(flipkartUpdateFeatures(map, "General", "3D"))) {
				type = "3D TV";
			}
			else if ("yes".equalsIgnoreCase(flipkartUpdateFeatures(map, "General", "Curve TV"))) {
				type = "CURVED TV";
			}
			else if ("yes".equalsIgnoreCase(flipkartUpdateFeatures(map, "General", "Smart TV"))) {
				type = "SMART TV";
			}
		}
		if ("LCD".equals(screen_type)) {
			screen_type = flipkartUpdateFeatures(map, "General", "Screen Type");
		}

		if (StringUtils.isNotBlank(productSubBrand)) {
			productSubBrand = productSubBrand.replaceAll("[^\\p{L}\\p{Z}\\p{N}\\.]", " ").replaceAll("\\s+", " ");
			if (StringUtils.isNotBlank(model)) {
				productSubBrand = productSubBrand.replace(model, "").trim();
			}
		}

		ReviewedProductInfoDTO info = populateDto(productBrand, productSubBrand, model, resolution, screen_size, type, screen_type, prod);

		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String productSubBrand, String model, String resolution, String screen_size, String type,
			String screen_type, HomeProductInfoDTO prod) {
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, productSubBrand, null, model, null, new HashMap<String, String>(), prod, null);

		// "SCREEN SIZE", "RESOLUTION", "SCREEN TYPE", "TYPE"

		info.getProperties().put("RESOLUTION", resolution == null ? "null" : resolution);
		info.getProperties().put("TYPE", type == null ? "null" : type);
		info.getProperties().put("SCREEN SIZE", screen_size == null ? "null" : screen_size);
		info.getProperties().put("SCREEN TYPE", screen_type == null ? "null" : screen_type);

		return info;
	}

}
