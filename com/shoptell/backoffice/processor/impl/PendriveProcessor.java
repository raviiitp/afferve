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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

/**
 * @author abhishekagarwal
 *
 */
@Named
public class PendriveProcessor extends DataProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shoptell.backoffice.processor.DataProcessor#shopcluesProcess(com.
	 * shoptell.backoffice.repository.dto.HomeProductInfoDTO)
	 */

	@Override
	public ReviewedProductInfoDTO shopcluesProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shoptell.backoffice.processor.DataProcessor#ebayProcess(com.shoptell
	 * .backoffice.repository.dto.HomeProductInfoDTO)
	 */
	@Override
	public ReviewedProductInfoDTO ebayProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shoptell.backoffice.processor.DataProcessor#snapdealProcess(com.shoptell
	 * .backoffice.repository.dto.HomeProductInfoDTO)
	 */
	@Override
	public ReviewedProductInfoDTO snapdealProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		title = removeDuplicates(title);
		String productBrand = null;
		String color = null;

		if (StringUtils.isNotBlank(prod.getProductBrand())) {
			productBrand = prod.getProductBrand();
		}

		if (StringUtils.isNotBlank(prod.getColor())) {
			color = prod.getColor().toUpperCase();
		}
		if (StringUtils.isBlank(color)) {
			color = retriveColor(prod);
		}

		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replace(productBrand, "");
		}

		if (StringUtils.isNotBlank(color) && title.contains(color)) {
			title = title.replace(color, "");
		}

		if (title.contains("MEMORY CARD")) {
			return null;
		}

		String capacity = null;
		String capacity_regex = "(\\d+)(\\s)*GB";
		Pattern pattern = Pattern.compile(capacity_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (NumberUtils.isNumber(tmp)) {
					capacity = Integer.parseInt(tmp) + "GB";
				}
			}
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
			}
		}

		String interfac = "USB 2.0";
		title = title.replace("2.0", "");
		if (title.contains("3.0")) {
			title = title.replace("3.0", "");
			interfac = "USB 3.0";
		}
		String pack_of = null;
		if (title.contains("PACK ") || title.contains("COMBO OF") || title.contains("PCS") || title.contains("BULK") || title.contains("PACK")
				|| title.contains("PIECES") || title.contains("")) {
			String pack_of_regex = "((\\d+)?\\s*)?((PACK OF)|(COMBO OF)|(PCS)|(PACK)|(PIECES)|(BULK))(\\s*(\\d+)?)?";
			pattern = Pattern.compile(pack_of_regex, Pattern.CASE_INSENSITIVE);
			match = pattern.matcher(title);
			if (match.find()) {
				if (isMatchPresent(match, 2)) {
					String tmp = match.group(2);
					if (NumberUtils.isNumber(tmp)) {
						pack_of = tmp;
					}
				}
				if (isMatchPresent(match, 11)) {
					String tmp = match.group(11);
					if (NumberUtils.isNumber(tmp)) {
						pack_of = tmp;
					}
				}

			}
		}
		if (StringUtils.isNotBlank(pack_of)) {
			title = title.replace(" " + pack_of, "");
		}
		title = removeDuplicates(title);
		title = title
				.replaceAll(
						"((\\d+)(\\s*)GB)|(BODY)|(SILVER)|(WHITE)|(PURPLE)|(STEEL)|(BLACK)|(BLUE)|(RED)|(MOSER(\\s)BAER)|(MULTICOLO(U)?R)|(METT?ALL?IC)|(SHAPED?)|(MICRODUO)|(((PEN|FLASH)(\\s)?)?DRIVES?)|(LIGHT)|(USB)|(UTILITY)|(DESIGNER)|(FANCY)|(2-IN-1)|(MULTI(\\s)?COLOR)|(MEDIA)|((\\s)AND)|(MICRO(\\s)?SD)|(PACK)|(PCS)|(PIECES)|(OF)|(BULK)|(COMBO)|FREE T(-)?SERIES MP3",
						"");

		String model = null;

		if (StringUtils.isNotBlank(title)) {
			model = title;
			if (model.contains("_") && !model.contains("GB")) {
				model = model + "GB";
				model = model.replace(" ", "");
			}
			model = model.replaceAll("[^\\p{L}\\p{Z}\\p{N}\\_]", " ").replaceAll("\\s+", " ").trim();
			model = removeDuplicates(model);
		}

		if (StringUtils.isNotBlank(model)){
			model = model.toUpperCase();
		}

		if (productBrand.equals("HP")) {
			model = model.replaceAll(" ", "");
		}

		ReviewedProductInfoDTO info = populateDto(productBrand, model, capacity, color, interfac, pack_of, prod);
		// System.out.println(productBrand + " | "+model+ " | "+capacity+
		// " | "+color+ " | "+interfac+ " | "+pack_of );
		return info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shoptell.backoffice.processor.DataProcessor#amazonProcess(com.shoptell
	 * .backoffice.repository.dto.HomeProductInfoDTO)
	 */
	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		String productBrand = prod.getProductBrand();

		if (StringUtils.isBlank(title) || StringUtils.isBlank(productBrand)) {
			return null;
		}

		if (title.contains("HUB") || title.contains("TEST") || title.contains("MICROSD") || title.contains("MICRO SD") || title.contains("MEMORYCARD")
				|| title.contains("POUCH") || title.contains("CABLE") || title.contains("RECORDER") || title.contains("EPAYISTA") || title.contains("ENCLOTH")
				|| title.contains("INTELLITOUCH") || title.contains("DATACARD") || title.contains("VIRUS") || title.contains(" VPN")
				|| title.contains("HARD DRIVE") || title.contains("TOOL") || title.contains("DEVICE") || title.contains("G-RAID") || title.contains("RECORDER")) {
			return null;
		}

		productBrand = productBrand.toUpperCase().trim();
		prod.setTitle(title.replace(productBrand, "").toUpperCase());
		String color = retriveColor(prod);

		title = prod.getTitle();
		if (StringUtils.isBlank(color) && StringUtils.isNotBlank(prod.getColor())) {
			color = prod.getColor().trim().toUpperCase();
			title = title.replace(color, "");
		}

		String pd_regex = "((FLASH|PEN)?(\\s)?(DRIVE))";
		Pattern pattern = Pattern.compile(pd_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {

			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
			}
			else {
				return null;
			}
		}

		String interfac = "USB 2.0";
		title = title.replace("2.0", "");
		if (title.contains("3.0")) {
			title = title.replace("3.0", "");
			interfac = "USB 3.0";
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

		String pack_of = null;
		title = title.replaceAll("[^\\p{L}\\p{Z}\\p{N}]", "").trim();

		if (title.contains("PACK ") || title.contains("COMBO") || title.contains("PCS") || title.contains("BULK") || title.contains("PACK")
				|| title.contains("PIECES")) {
			String pack_of_regex = "((\\d+)?\\s*)?((PACK OF)|(COMBO(\\s)?(OF)?)|(PCS)|(PACK)|(PIECES)|(BULK))(\\s*(\\d+)?)?";
			pattern = Pattern.compile(pack_of_regex, Pattern.CASE_INSENSITIVE);
			match = pattern.matcher(title);
			if (match.find()) {
				if (isMatchPresent(match, 2)) {
					String tmp = match.group(2);
					if (NumberUtils.isNumber(tmp)) {
						pack_of = tmp;
					}
				}
				else if (isMatchPresent(match, 11)) {
					String tmp = match.group(11);
					if (NumberUtils.isNumber(tmp)) {
						pack_of = tmp;
					}
				}
			}
		}

		title = title.replaceAll(
				"(MULTICOLOR)|((FLASH)?(\\s)?(PEN|MEMORY)?(\\s)?DRIVE)|(FLASH)|(USB)|(100 % ORIGINAL HIGHSPEED )|(PEN)|((\\s)(\\d+)(\\s*)(G|M)B(Z)?(IN)?)"
						+ "|(UTILITY)|(NEW)|(FANCY)|(DESIGNER)|(MULTI)|(COLOR)|((3D)?(\\s)?CARTOON (CHARACTER)?)|(SILICON)"
						+ "|(STYLISH FASHION)|(CREDIT CARDD?)|(HOT PLUG AND PLAY)|(SHAPED?)|(PACK)|(PCS)|(PIECES)|(OF)|(BULK)|(COMBO)|(OTG)", "");

		title = title.trim();
		String model = prod.getModel();

		if (StringUtils.isNotBlank(model)) {
			model = model.toUpperCase().replaceAll(
					"(MULTICOLOR)|((FLASH)?(\\s)?(PEN|MEMORY)?(\\s)?DRIVE)|(FLASH)|(USB)|(100 % ORIGINAL HIGHSPEED )|(PEN)|((\\s)(\\d+)(\\s*)(G|M)B(Z)?(IN)?)"
							+ "|(UTILITY)|(NEW)|(FANCY)|(DESIGNER)|(MULTI)|(COLOR)|((3D)?(\\s)?CARTOON (CHARACTER)?)|(SILICON)"
							+ "|(STYLISH FASHION)|(CREDIT CARDD?)|(HOT PLUG AND PLAY)|(SHAPED?)|(PACK)|(PCS)|(PIECES)|(OF)|(BULK)|(COMBO)", "");
			model = model.replace("ON-THE-GO (OTG)", "OTG");

			if (StringUtils.isNotBlank(productBrand) && model.contains(productBrand)) {
				model = model.replace(productBrand, "");
			}
			if (StringUtils.isNotBlank(capacity) && model.contains(capacity)) {
				model = model.replace(capacity, "");
			}
			if (StringUtils.isNotBlank(color) && model.contains(color)) {
				model = model.replace(color, "");
			}
			if (productBrand.equals("PRINTLAND")) {
				if (title.contains("[A-Z0-9]")) {
					model = title;
				}
				else {
					model = title + " " + model;
				}
			}
			model = removeDuplicates(model);
			model = model.replaceAll("[^\\p{L}\\p{Z}\\p{N}]", " ").replaceAll("\\s+", " ").trim();
		}
		if (productBrand.equals("DV TECH.")) {
			model = "OTG";
		}

		ReviewedProductInfoDTO info = populateDto(productBrand, model, capacity, color, interfac, pack_of, prod);
		// System.out.println( productBrand +" | "+model+ " | " + color +" | " +
		// capacity + " | " + interfac + " | " + pack_of);
		return info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shoptell.backoffice.processor.DataProcessor#flipkartProcess(com.shoptell
	 * .backoffice.repository.dto.HomeProductInfoDTO)
	 */
	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		title = removeDuplicates(title);
		String productBrand = null;
		String color = null;
		String interfac = "USB 2.0";
		String capacity = null;
		String pack_of = null;

		if (StringUtils.isNotBlank(prod.getProductBrand())) {
			productBrand = prod.getProductBrand();
		}

		if (StringUtils.isBlank(color)) {
			color = retriveColor(prod);
		}

		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replace(productBrand, "");
		}

		if (StringUtils.isNotBlank(color) && title.contains(color)) {
			title = title.replace(color, "");
		}

		String capacity_regex = "(\\d+)(\\s)+GB";
		Pattern pattern = Pattern.compile(capacity_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 1)) {
				String tmp = match.group(1);
				if (NumberUtils.isNumber(tmp)) {
					capacity = tmp + " GB";
					title = title.replace(capacity, "");
				}
			}
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
			}

		}

		if (StringUtils.isNotBlank(title)) {
			if (title.contains("PACK ") || title.contains("COMBO OF") || title.contains("PCS") || title.contains("BULK") || title.contains("PIECES")) {
				String pack_of_regex = "((\\d+)?\\s*)?((PACK OF)|(COMBO OF)|(PCS)|(PACK)|(PIECES)|(BULK))(\\s*(\\d+)?)?";
				pattern = Pattern.compile(pack_of_regex, Pattern.CASE_INSENSITIVE);
				match = pattern.matcher(title);
				if (match.find()) {
					if (isMatchPresent(match, 2)) {
						String tmp = match.group(2);
						if (NumberUtils.isNumber(tmp)) {
							pack_of = tmp;
						}
					}
					else if (isMatchPresent(match, 11)) {
						String tmp = match.group(11);
						if (NumberUtils.isNumber(tmp)) {
							pack_of = tmp;
						}
					}
				}
			}

			interfac = "USB 2.0";
			title = title.replace("2.0", "");
			if (title.contains("3.0")) {
				title = title.replace("3.0", "");
				interfac = "USB 3.0";
			}

			if (!title.contains("_")) {
				title = title.replaceAll("(\\d+)(\\s*)GB", "");
			}

			title = title.replaceAll("(MULTICOLOR)|((PEN|MEMORY)(\\s)?DRIVE)|(FLASH)|(USB)|(100 % ORIGINAL HIGHSPEED )|(PEN)|((\\s)(\\d+)(G|M)B)"
					+ "|(UTILITY)|(NEW)|(FANCY)|(DESIGNER)|(MULTI)|(COLOR)|((3D)?(\\s)?CARTOON (CHARACTER)?)|(SILICON)"
					+ "|(STYLISH FASHION)|(CREDIT CARDD?)|(HOT PLUG AND PLAY)|(SHAPED?)|(PACK)|(PCS)|(PIECES)|(OF)|(BULK)|(COMBO)", "");
			title = title.replace("ON-THE-GO (OTG)", "OTG");
		}
		String model = null;

		if (StringUtils.isNotBlank(title)) {

			model = title.replaceAll("[^\\p{L}\\p{Z}\\p{N}\\_]", " ").replaceAll("\\s+", " ").toUpperCase().trim();

		}
		
		if (StringUtils.isNotBlank(capacity)){
			capacity = capacity.replace(" ", "");
		}
		
		ReviewedProductInfoDTO info = populateDto(productBrand, model, capacity, color, interfac, pack_of, prod);
		// System.out.println("---->" +productBrand + " | "+model+
		// " | "+capacity+ " | "+color+ " | "+interfac+ " | "+pack_of );
		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String model, String capacity, String color, String interfac, String pack_of,
			HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
		}
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, null, null, model, color, new HashMap<String, String>(), prod, null);

		info.getProperties().put("CAPACITY", capacity == null ? "null" : capacity);
		info.getProperties().put("INTERFACE", interfac == null ? "null" : interfac);
		info.getProperties().put("PACK OF", pack_of == null ? "null" : pack_of);
		info.getProperties().put("COLOR", color == null ? "null" : color);

		return info;
	}
}