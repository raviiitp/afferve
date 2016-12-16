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
public class CameraProcessor extends DataProcessor {

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
		String SP_CHAR_REMOVE_REGEX = "[^\\p{L}\\p{Z}\\p{N}&\\.\\(\\)\\/-]";
		String title = prod.getTitle();
		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replace(productBrand, "");
			title = title.replaceAll(SP_CHAR_REMOVE_REGEX, " ");
		}

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.trim();
			title = title.split("(\\()?" + color)[0];
		}
		else {
			Pattern pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(title);
			String match = null;
			while (matcher.find()) {
				if (isMatchPresent(matcher, 0)) {
					match = matcher.group(0);
				}
			}

			if (StringUtils.isNotBlank(match)) {
				color = match.trim();
				int index = title.indexOf(match);
				if (index != -1) {
					title = title.substring(0, index).trim();
				}
			}
			if (StringUtils.isNotBlank(color)) {
				color = color.trim().replace("(", "");
			}
		}

		title = title.replaceAll("\\(.*\\)", "");

		String type = null;
		if ("POINT & SHOOT DIGITAL CAMERAS".equalsIgnoreCase(prod.getType()) || "DIGITAL CAMERAS".equalsIgnoreCase(prod.getType())) {
			type = "POINT & SHOOT";
		}
		else if ("SPY CAMERAS".equalsIgnoreCase(prod.getType())) {
			type = "SPY CAMERA";
		}
		else if ("MIRRORLESS SYSTEM CAMERAS".equalsIgnoreCase(prod.getType())) {
			type = "MIRRORLESS";
		}
		else if ("INSTANT CAMERAS".equalsIgnoreCase(prod.getType())) {
			type = "INSTANT CAMERA";
		}
		else if ("DIGITAL SLRS".equalsIgnoreCase(prod.getType())) {
			type = "DSLR CAMERA";
		}
		else if ("SLRS".equalsIgnoreCase(prod.getType())) {
			type = "SLR CAMERA";
		}
		
		String type_regex = "((POINT & SHOOT|CAMCOR?DERS?|DSLR|MIRRORLESS|INSTANT|SPY|SLR)\\s*(DIGITAL\\s*)?(CAMERA(S)?)?)|(UV|POLARIZING)\\s+FILTER";
		Pattern pattern = Pattern.compile(type_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				type = match.group(0);
			}
			if (StringUtils.isNotBlank(type)) {
				type = type.trim();
				title = title.replace(type, "");
				if ("POINT & SHOOT CAMERA".equalsIgnoreCase(type) || type.contains("POINT & SHOOT")) {
					type = "POINT & SHOOT";
				}
				else if ("MIRRORLESS CAMERA".equalsIgnoreCase(type)) {
					type = "MIRRORLESS";
				}
				else if ("DIGITAL SLRS CAMERA".equalsIgnoreCase(type) || type.contains("DSLR")) {
					type = "DSLR CAMERA";
				}
				else if (type.contains("CAMCORDER") || type.contains("CAMCODER")) {
					type = "CAMCORDER";
				}
				else if (type.contains("INSTANT")) {
					type = "INSTANT CAMERA";
				}
				else if ("SLR".equalsIgnoreCase(type)) {
					type = "SLR CAMERA";
				}
			}
		}
		
		String mp = null;
		String mp_regex = "(\\d+(\\.\\d+)?)\\s*((MP)|(MEGA\\s*PIXEL))";
		pattern = Pattern.compile(mp_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				mp = match.group(0);
			}
			if (StringUtils.isNotBlank(mp)){
				title = title.split(mp)[0];
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				double val = Double.parseDouble(tmp);
				mp = val + " MP";
			}
		}
		
		if (StringUtils.isNotBlank(title)){
			title = title.split("(WITH|\\()")[0];
		}
		if (StringUtils.isNotBlank(title)){
			title = removeDuplicates(title).replace("CAMERA", "").replaceAll("-|\\s(DIGITAL|COMBO|WITH|ADVANCED|BODY|ONLY|D?SLRS?)", "");
			title = title.replaceAll("\\s+", " ").trim();
		}

		if (StringUtils.isNotBlank(color)) {
			color = color.replaceAll("\\W", " ").replaceAll("\\s+", " ").trim();
		}
		
		//System.out.println(title+" | "+productBrand+" | "+type+" | "+color+" | "+mp+" | "+prod.getTitle());
		String model = null;
		if (StringUtils.isNotBlank(title)) {
			model = title;
		}

		/*System.out.println(productBrand + " | " + color + " | " + type + " | " + title + " | " + prod.getTitle());*/
		// "MEGA PIXEL","TYPE","COLOR"
		
		ReviewedProductInfoDTO info = populateDto(productBrand, model, mp, type, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String SP_CHAR_REMOVE_REGEX = "[^\\p{L}\\p{Z}\\p{N}&\\.\\(\\)\\/-]";
		String title = prod.getTitle();
		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replace(productBrand, "");
			title = title.replaceAll(SP_CHAR_REMOVE_REGEX, " ");
		}

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.trim();
			title = title.split("(\\()?" + color)[0];
		}
		else {
			Pattern pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(title);
			String match = null;
			while (matcher.find()) {
				if (isMatchPresent(matcher, 0)) {
					match = matcher.group(0);
				}
			}

			if (StringUtils.isNotBlank(match)) {
				color = match.trim();
				int index = title.indexOf(match);
				if (index != -1) {
					title = title.substring(0, index).trim();
				}
			}
			if (StringUtils.isNotBlank(color)) {
				color = color.trim().replace("(", "");
			}
		}

		title = title.replaceAll("\\(.*\\)", "");

		String type = null;
		if ("POINT & SHOOT DIGITAL CAMERAS".equalsIgnoreCase(prod.getType())) {
			type = "POINT & SHOOT";
		}
		else if ("SPY CAMERAS".equalsIgnoreCase(prod.getType())) {
			type = "SPY CAMERA";
		}
		else if ("MIRRORLESS SYSTEM CAMERAS".equalsIgnoreCase(prod.getType())) {
			type = "MIRRORLESS";
		}
		else if ("INSTANT CAMERAS".equalsIgnoreCase(prod.getType())) {
			type = "INSTANT CAMERA";
		}
		else if ("DIGITAL SLRS".equalsIgnoreCase(prod.getType())) {
			type = "DSLR CAMERA";
		}
		else if ("SLRS".equalsIgnoreCase(prod.getType())) {
			type = "SLR CAMERA";
		}

		String mp = null;
		String mp_regex = "(\\d+(\\.\\d+)?)\\s*MP";
		Pattern pattern = Pattern.compile(mp_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				mp = match.group(0);
			}
			if (StringUtils.isNotBlank(mp)){
				title = title.split(mp)[0];
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				double val = Double.parseDouble(tmp);
				mp = val + " MP";
			}
		}

		title = title.replaceAll("\\s+", " ").trim();
		String model = title;
		if (StringUtils.isNotBlank(model)) {
			String[] ele = model.split("\\s|,");
			if (ele.length > 2) {
				StringBuilder sb = new StringBuilder(ele[0]);
				for (int i = 1; i < 2; i++) {
					sb.append(" ").append(ele[i]);
				}
				model = sb.toString();
			}
		}

		/*System.out.println(productBrand + " | " + color + " | " + type + " | " + mp + " | " + model + " | " + prod.getTitle());*/
		ReviewedProductInfoDTO info = populateDto(productBrand, model, mp, type, color, prod);

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

		String type = "POINT & SHOOT";
		String type_regex = "((POINT & SHOOT|CAMCORDER|DSLR|MIRRORLESS|INSTANT|SPY)\\s+CAMERA)|(UV|POLARIZING)\\s+FILTER";
		Pattern pattern = Pattern.compile(type_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				type = match.group(0);
			}
			title = title.replace(type, "");
			if ("POINT & SHOOT CAMERA".equalsIgnoreCase(type)) {
				type = "POINT & SHOOT";
			}
			else if ("MIRRORLESS CAMERA".equalsIgnoreCase(type)) {
				type = "MIRRORLESS";
			}
			else if ("DIGITAL SLRS CAMERA".equalsIgnoreCase(type)) {
				type = "DSLR CAMERA";
			}
			else if ("CAMCORDER CAMERA".equalsIgnoreCase(type)) {
				type = "CAMCORDER";
			}
		}

		title = title.replaceAll("\\(.*\\)", "");
		title = removeDuplicates(title).replace("CAMERA", "").replaceAll("\\s(WITH|ADVANCED|BODY|ONLY)", "");
		title = title.replaceAll("\\s+", " ").trim();

		if (StringUtils.isNotBlank(color)) {
			color = color.replaceAll("\\W", " ").replaceAll("\\s+", " ").trim();
		}

		String model = null;
		if (StringUtils.isNotBlank(title)) {
			model = title;
		}
		
		String mp = null;
		
		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());
		
		String tmp = flipkartUpdateFeatures(map, "General", "Model Number");
		if (StringUtils.isNotBlank(tmp)){
			model = tmp;
		}
		if (StringUtils.isBlank(mp)) {
			String mpix = flipkartUpdateFeatures(map, "Pixels", "Optical Sensor Resolution (in MegaPixel)");
			if (StringUtils.isNotBlank(mpix)) {
				String mp_regex = "(\\d+(\\.\\d+)?)\\s*((MP)|(MEGA\\s*PIXEL))";
				pattern = Pattern.compile(mp_regex, Pattern.CASE_INSENSITIVE);
				match = pattern.matcher(mpix);
				if (match.find()) {
					if (isMatchPresent(match, 0)) {
						mp = match.group(0);
					}
					if (isMatchPresent(match, 1)) {
						String temp = match.group(1);
						double val = Double.parseDouble(temp);
						mp = val + " MP";
					}
				}
			}
		}
		if (StringUtils.isBlank(type)){
			type = flipkartUpdateFeatures(map, "General", "Type");
		}
		if (StringUtils.isBlank(color)){
			color = flipkartUpdateFeatures(map, "General", "Color");
		}

		/*System.out.println(productBrand + " | " + color + " | " + type + " | " + title + " | " + prod.getTitle());*/
		// "MEGA PIXEL","TYPE","COLOR"
		ReviewedProductInfoDTO info = populateDto(productBrand, model, mp, type, color, prod);

		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String model, String mp, String type, String color, HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)){
			color = color.toUpperCase();
		}
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, null, null, model, color, new HashMap<String, String>(), prod, null);

		info.getProperties().put("CAMERA TYPE", type == null ? "null" : type);
		info.getProperties().put("MEGA PIXEL", mp == null ? "null" : mp);

		return info;
	}

}
