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
public class RefrigeratorProcessor extends DataProcessor {

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

		if (title.contains("REFRIGERATOR")) {
			int indx = title.lastIndexOf("REFRIGERATOR");
			if (indx > -1) {
				title = title.substring(0, indx - 1);
			}
		}
		else {
			return null;
		}

		String color = null;
		String desc = prod.getDescription();
		if (StringUtils.isNotBlank(desc)) {
			desc = desc.toUpperCase();
			int indx = desc.lastIndexOf("COLOUR :");
			if (indx > -1) {
				color = desc.substring(indx + 9);
			}
		}
		if (StringUtils.isBlank(color)) {
			color = prod.getColor();
		}
		if (StringUtils.isNotBlank(color)) {
			color = color.trim();
			title = title.replace(color, "");
			StringBuilder sb = new StringBuilder();
			if (color.contains(":")) {
				String[] tmp = color.split("\\s");
				for (int i = 1; i < tmp.length; i++) {
					if (":".equalsIgnoreCase(tmp[i])) {
						break;
					}
					else {
						sb.append(tmp[i - 1]).append(" ");
					}
				}
				color = sb.toString().trim();
			}
		}

		String capacity = null;
		String capacity_regex = "(\\d+)(\\s)*L(I?TRE?)?S?(\\.?)";
		Pattern pattern = Pattern.compile(capacity_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				capacity = match.group(0);
			}
			if (StringUtils.isNotBlank(capacity)){
				title = title.replace(capacity, "");
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				double val = Double.parseDouble(tmp);
				capacity = val + " L";
			}
		}

		String door_type = null;
		String door_type_regex = "((DOUBLE DOOR)|(SINGLE DOOR)|(SIDE BY SIDE)|(TRIPLE DOOR)|(FRENCH DOOR)|(MULTI DOOR))";// \\s+REFRIGERATOR
		pattern = Pattern.compile(door_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				door_type = match.group(0);
			}
			if (StringUtils.isNotBlank(door_type)){
				title = title.replace(door_type, "");
			}
			if (isMatchPresent(match, 1)) {
				door_type = match.group(1);
			}
		}

		String type = null;
		String type_regex = "((FROST FREE)|(DIRECT COOL)|(THERMOELECTRIC COOLING))";
		pattern = Pattern.compile(type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				type = match.group(0);
			}
			if (StringUtils.isNotBlank(type)){
				title = title.replace(type, "");
			}
		}

		String star = null;
		String star_regex = "(\\d)\\s*STAR";
		pattern = Pattern.compile(star_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (NumberUtils.isNumber(tmp)) {
					star = tmp + " STAR";
				}
			}
			if (StringUtils.isNotBlank(star)) {
				title = title.replace(star, "");
			}
		}
		if (StringUtils.isBlank(star)) {
			match = pattern.matcher(desc);
			if (match.find()) {
				if (isMatchPresent(match, 1)) {
					String tmp = match.group(1);
					if (NumberUtils.isNumber(tmp)) {
						star = tmp + " STAR";
					}
				}
				if (StringUtils.isNotBlank(star)) {
					title = title.replace(star, "");
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
		// System.out.println(productBrand+" | "+color+" | "+capacity+" | "+door_type+" | "+type+" | "+star+" | "+title+" | "+prod.getTitle());
		ReviewedProductInfoDTO info = populateDto(productBrand, model, star, capacity, door_type, type, color, prod);

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
		String regex = "\\(((\\d+(\\.\\d+)?)\\s+L(I?TRE?S?)?,\\s*)?((\\d+)\\s+STAR\\s+RATING,\\s*)?(.*?)\\)\\)?";
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
				capacity = val + " L";
			}
			if (isMatchPresent(match, 6)) {
				String tmp = match.group(6);
				double val = Double.parseDouble(tmp);
				star = val + " STAR";
			}
			if (StringUtils.isNotBlank(color)) {
				if (isMatchPresent(match, 7)) {
					color = match.group(7);
					color = color.replace(",", "").trim();
				}
			}
		}

		if (StringUtils.isNotBlank(cut)) {
			title = title.split(cut)[0];
		}

		if (StringUtils.isBlank(capacity)) {
			String capacity_regex = "(\\d+) L(I?TRE?)?S?";
			pattern = Pattern.compile(capacity_regex, Pattern.CASE_INSENSITIVE);
			match = pattern.matcher(title);
			if (match.find()) {
				if (isMatchPresent(match, 0)) {
					capacity = match.group(0);
				}
				title = title.replace(capacity, "");
				if (isMatchPresent(match, 1)) {
					String tmp = match.group(1);
					double val = Double.parseDouble(tmp);
					capacity = val + " L";
				}
			}
		}

		if (StringUtils.isNotBlank(title)) {
			title = title.replace("-", " ");
		}

		String door_type = null;
		String door_type_regex = "((DOUBLE DOOR)|(SINGLE DOOR)|(SIDE BY SIDE)|(TRIPLE DOOR)|(FRENCH DOOR)|(MULTI DOOR))";// \\s+REFRIGERATOR
		pattern = Pattern.compile(door_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				door_type = match.group(0);
			}
			if (StringUtils.isNotBlank(door_type)){
				title = title.replace(door_type, "");
			}
			if (isMatchPresent(match, 1)) {
				door_type = match.group(1);
			}
		}

		String type = null;
		String type_regex = "((FROST FREE)|(DIRECT COOL)|(THERMOELECTRIC COOLING))";
		pattern = Pattern.compile(type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				type = match.group(0);
			}
			if (StringUtils.isNotBlank(type)){
				title = title.replace(type, "");
			}
		}

		/*System.out.println(productBrand + " | " + model + " | " + capacity + " | " + door_type + " | " + type + " | " + color + " | " + star + " | "
				+ prod.getTitle());*/

		ReviewedProductInfoDTO info = populateDto(productBrand, model, star, capacity, door_type, type, color, prod);

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
		String capacity_regex = "(\\d+) L(ITRE)?S?";
		Pattern pattern = Pattern.compile(capacity_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				capacity = match.group(0);
			}
			if (StringUtils.isNotBlank(capacity)){
				title = title.replace(capacity, "");
			}
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				double val = Double.parseDouble(tmp);
				capacity = val + " L";
			}
		}

		String door_type = null;
		String door_type_regex = "((DOUBLE DOOR)|(SINGLE DOOR)|(SIDE BY SIDE)|(TRIPLE DOOR)|(FRENCH DOOR))\\s+REFRIGERATOR"; // FRENCH
																																// MOUNT
		pattern = Pattern.compile(door_type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				door_type = match.group(0);
			}
			if (StringUtils.isNotBlank(door_type)){
				title = title.replace(door_type, "");
			}
			if (isMatchPresent(match, 1)) {
				door_type = match.group(1);
			}
		}

		String type = null;
		String type_regex = "((FROST FREE)|(DIRECT COOL)|(THERMOELECTRIC COOLING))";
		pattern = Pattern.compile(type_regex, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				type = match.group(0);
			}
			if (StringUtils.isNotBlank(type)){
				title = title.replace(type, "");
			}
		}

		String model = null;
//		StringBuilder m = new StringBuilder();
//		String img = prod.getImageUrl();
//		if (StringUtils.isNotBlank(img)) {
//			img = img.substring(img.lastIndexOf("/") + 1);
//			String[] tmp = img.split("-");
//			if (tmp != null && tmp.length > 0) {
//				for (int i = 0; i < tmp.length - 1; i++) {
//					if (tmp[i].equalsIgnoreCase("200x200")) {
//						break;
//					}
//					m.append(tmp[i]).append(" ");
//				}
//			}
//			model = m.toString();
//		}

		/*System.out.println(productBrand + " | " + capacity + " | " + color + " | " + door_type + " | " + type + " | " + model + " | " + title + " | "
				+ prod.getTitle());*/
		
		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());
		if (StringUtils.isBlank(model)) {
			model = flipkartUpdateFeatures(map, "General", "Model Name");
		}
		if (StringUtils.isBlank(capacity)) {
			capacity = flipkartUpdateFeatures(map, "General", "Capacity");
		}
		if (StringUtils.isBlank(door_type)) {
			door_type = flipkartUpdateFeatures(map, "General", "Type");
		}
		if (StringUtils.isBlank(type)) {
			type = flipkartUpdateFeatures(map, "General", "Defrosting Type");
		}
		if (StringUtils.isBlank(color)) {
			color = flipkartUpdateFeatures(map, "General", "Color");
			if (StringUtils.isBlank(color)){
				color = flipkartUpdateFeatures(map, "General", "Shade");
			}
		}

		ReviewedProductInfoDTO info = populateDto(productBrand, model, null, capacity, door_type, type, color, prod);

		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String model, String star, String capacity, String door_type, String type, String color,
			HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)){
			color = color.toUpperCase();
		}
		
		if (StringUtils.isNotBlank(model)){
			model = model.toUpperCase().replace(productBrand, "").replaceAll("[^\\p{L}\\p{Z}\\p{N}\\.]", " ").replaceAll("\\s+", " ").trim();
		}
		
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, null, null, model, color, new HashMap<String, String>(), prod, null);

		// "DOOR TYPE","TYPE","CAPACITY","ENERGY RATING","COLOR"

		info.getProperties().put("CAPACITY", capacity == null ? "null" : capacity);
		info.getProperties().put("TYPE", type == null ? "null" : type);
		info.getProperties().put("DOOR TYPE", door_type == null ? "null" : door_type);
		info.getProperties().put("ENERGY RATING", star == null ? "null" : star);

		return info;
	}

	/*public static void main(String[] args) {
		RefrigeratorProcessor r = new RefrigeratorProcessor();
		HomeProductInfoDTO prod = new HomeProductInfoDTO();
		prod.setTitle("KIEIS METAL GLASS TOP CHEST SHOWCASE (KD215, YELLOW)");
		prod.setProductBrand("KIEIS");
		prod.setModel("KD215");
		r.amazonProcess(prod);
	}*/

}
