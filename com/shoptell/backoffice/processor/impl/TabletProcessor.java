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

import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

@Named
public class TabletProcessor extends DataProcessor {

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
		title = removeDuplicates(title);
		// System.out.println(prod.getTitle());
		String productBrand = null;
		String color = null;
		String capacity = null;
		String model = null;
		String productSubBrand = null;
		String productSeries = null;

		productBrand = prod.getProductBrand();
		productBrand = productBrand.toUpperCase().trim();
		title = title.replace(productBrand, "");

		if (StringUtils.isNotBlank(prod.getColor())) {
			color = prod.getColor().toUpperCase();
			if (title.contains(color)) {
				if (title.contains("SPACE")) {
					color = "SPACE " + color;
				}
				title = title.replace(color, "");
			}
		}

		if (title.contains("TABLET")) {
			int indx = title.lastIndexOf("TABLET");
			if (indx > -1) {
				title = title.substring(0, indx - 1);
			}
		}

		if (StringUtils.isNotBlank(prod.getSize())) {
			capacity = prod.getSize();

			if (!capacity.contains("GB")) {
				capacity = capacity + " GB";
			}

			String capacity2 = capacity.replace(" ", "");

			if (title.contains(capacity)) {
				title = title.replace(capacity, "");
			}
			if (title.contains(capacity2)) {
				title = title.replace(capacity2, "");
			}
		}

		if (StringUtils.isNotBlank(title)) {

			title = title.replaceAll("(WINDOWS)|(MODEL)|(SERIES)|(FAMILY)|(SMOKY)|((\\s)HD)|(EDITION)|(PROCESSOR)|(FOR)|(KIDS)|(CAPACITIVE)|(QUAD)|"
					+ "(OCTA)|(SECOND)|(GRAY)|(WHITE)|(CAMERA)|(NEW)|(GREY)|(METALLIC)|(BLACK)|(BLUE)|(SILVER)|(GOLD)|(SPACE GRAY)|(I KALL)|"
					+ "(TMOBILE)|(VOICE)|(INTERACTIVE)|(INTERNAL)|(STORAGE)|(PENTOUCH)|((\\s)?CELL(ULAR)?)|(MIGADGETS)|(WRITING)|(TABLET)|"
					+ "(RETINA)|(DISPLAY)|((\\s)AND(\\s))|(ANDROID)|(CALLING)|((\\d+)?RAM)|(ROM)|(PC)|(OS)|(LOLLIPOP)|"
					+ "(((DUAL|SINGLE)(\\s)?)?(SIM|CORE))|((\\d+)(\\s*)(INCH(ES)?|CM))|(MIDNIGHT)|(ONLY)|((\\d+)(\\s*)(G|M)B)|(QUAD CORE)", "");
			title = title.trim();
		}

		String connectivity = "WI-FI";

		if (title.contains("WI-FI")) {
			int indx = title.lastIndexOf("WI-FI");
			if (indx > -1) {
				connectivity = title.substring(indx, title.length());
				title = title.substring(0, indx - 1);

			}
		}

		if (title.contains("WIFI")) {
			int indx = title.lastIndexOf("WIFI");
			if (indx > -1) {
				connectivity = title.substring(indx, title.length());
				title = title.substring(0, indx - 1);

			}
		}
		if (connectivity.contains("WITH")) {
			int indx = connectivity.lastIndexOf("WITH");
			if (indx > -1) {
				connectivity = connectivity.substring(0, indx - 1);

			}
		}

		if (title.contains("WITH")) {
			int indx = title.lastIndexOf("WITH");
			if (indx > -1) {
				title = title.substring(0, indx - 1);

			}
		}
		if (title.contains("SINGLE")) {
			int indx = title.lastIndexOf("SINGLE");
			if (indx > -1) {
				title = title.substring(0, indx - 1);

			}
		}

		title = removeDuplicates(title);

		String c = null;
		String c_regex = "(\\s)(\\d)G";
		Pattern pattern = Pattern.compile(c_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 2)) {
				String tmp = match.group(2);
				if (NumberUtils.isNumber(tmp)) {
					c = tmp + "G";
				}
			}
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
			}
		}
		if (StringUtils.isNotBlank(c)) {
			connectivity = connectivity + " " + c;
		}

		if (title.startsWith("3G")) {
			connectivity += " 3G";
		}

		if (title.contains("VIA DONGLE")) {
			connectivity += " " + "VIA DONGLE";
			title = title.replace("VIA DONGLE", "");
		}
		if (title.contains("LTE")) {
			connectivity += " " + "LTE";
			title = title.replace("LTE", "");
		}
		title = title.replace(" LTE", "");

		if (productBrand.equals("IBALL")) {
			if (title.contains("3G")) {
				title = title.replaceAll("3G", "");
				connectivity += " 3G";
			}
			if (title.contains("2G")) {
				title = title.replaceAll("2G", "");
				connectivity += " 2G";
			}
		}

		if (title.contains("CDMA")) {
			title = title.replace("CDMA", "");
			connectivity = connectivity + " CDMA";
		}
		connectivity = connectivity.replaceAll("([^\\p{L}\\p{Z}\\p{N}])|(DATA)", "").replaceAll("\\s+", " ").trim();

		if (connectivity.contains("WIFI")) {
			connectivity = connectivity.replace("WIFI", "WI-FI");
		}

		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll("[^\\p{L}\\p{Z}\\p{N}]", "").replaceAll("\\(|\\)", "").trim();
		}
		String[] array = title.split("\\s");
		int length = array.length;

		if (length == 1) {
			model = title;
		}
		else if (length == 2) {
			productSubBrand = array[0];
			model = array[length - 1];
		}
		else if (length > 2) {
			productSubBrand = array[0];
			model = array[length - 1];
			productSeries = "";
			for (int i = 1; i < length - 1; i++) {
				productSeries += array[i] + " ";
			}
			productSeries = productSeries.trim();
		}

		// System.out.println(title);

		/*
		 * System.out.println(productBrand+ " | " + productSubBrand+ " | " +
		 * productSeries+ " | " + model +" | " + capacity+ " | " + color + " | "
		 * + connectivity);
		 */

		ReviewedProductInfoDTO info = populateDto(productBrand, productSubBrand, productSeries, model, capacity, color, connectivity, prod);

		return info;
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		title = removeDuplicates(title);

		String productBrand = null;
		String color = null;
		String model = null;
		String productSubBrand = null;
		String productSeries = null;

		productBrand = prod.getProductBrand().toUpperCase();
		if (productBrand.contains("APPLE")) {
			productBrand = "APPLE";
		}
		if (title.contains(productBrand)) {
			title = title.replace(productBrand, "");
		}

		if (StringUtils.isNotBlank(prod.getColor())) {
			color = prod.getColor().toUpperCase();
		}
		else {
			color = retriveColor(prod);
		}

		if (StringUtils.isNotBlank(color)) {
			if (title.contains(color)) {
				title = title.replace(color, "");
			}
		}

		String capacity = prod.getSize();
		if (StringUtils.isBlank(capacity) || !capacity.contains("GB")) {
			capacity = retrieveSize(prod);
		}

		if (StringUtils.isNotBlank(title)) {
			title = title
					.replaceAll("[^\\p{L}\\p{Z}\\p{N}]", "")
					.replaceAll(
							"(PURITAN)|(\\s)(BY)|(WINDOWS)|(MODEL)|(SERIES)|(FAMILY)|(SMOKY)|((\\s)HD)|(EDITION)|(PROCESSOR)|((\\s)FOR)|(PERFORMANCE)|(KIDS)|(CAPACITIVE)|(QUAD)|(OCTA)|"
									+ "(SECOND)|(GENERATION)|(GRAY)|(WHITE)|(CAMERA)|((\\s)NOT)|(NEW)|(GREY)|(METALLIC)|(BLACK)|(BLUE)|(SILVER)|(GOLD)|"
									+ "(SPACE GRAY)|(I KALL)|(TMOBILE)|(VOICE)|(INTERACTIVE)|(SUPPORTED)|(INTERNAL)|(STORAGE)|(PENTOUCH)|((\\s)CELL(ULAR)?)|"
									+ "(MIGADGETS)|(WRITING)|(TABLET)|(RETINA)|(DISPLAY)|((\\s)AND(\\s))|(ANDROID)|(CALLING)|((\\d+)?RAM)|(ROM)|(PC)|"
									+ "(OS)|(LOLLIPOP)|(((DUAL|SINGLE)(\\s)?)?(SIM|CORE))|((\\d+)(\\s*)CM)|((\\d+)(\\s*)INCH)|(MIDNIGHT)|(ONLY)|"
									+ "((\\d+)(\\s*)GB)|((\\d+)(\\s*)MB)|(QUAD CORE)", "").trim();
		}
		String connectivity = "WI-FI";
		if (title.contains("WI-FI")) {
			title = title.replace("WI-FI", "");
		}
		if (title.contains("WIFI")) {
			title = title.replace("WIFI", "");
		}
		title = title.replaceAll("\\(|\\)", "").trim();

		if (productBrand.equals("APPLE")) {
			if (title.contains("WITH")) {
				title = title.replace("WITH", "");
			}
		}

		if (title.contains("WITH")) {
			int indx = title.lastIndexOf("WITH");
			if (indx > 0) {
				title = title.substring(0, indx - 1);
			}
		}

		String c = null;
		String c_regex = "(\\s)(\\d)G";
		Pattern pattern = Pattern.compile(c_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 2)) {
				String tmp = match.group(2);
				if (NumberUtils.isNumber(tmp)) {
					c = tmp + "G";
				}
			}
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
			}
		}
		if (StringUtils.isNotBlank(c)) {
			connectivity = connectivity + " " + c;
		}
		if (title.startsWith("3G")) {
			connectivity += " 3G";
		}

		if (title.contains("VIA DONGLE")) {
			connectivity += " " + "VIA DONGLE";
			title = title.replace("VIA DONGLE", "");
		}
		if (title.contains("LTE")) {
			connectivity += " " + "LTE";
			title = title.replace("LTE", "");
		}
		title = title.replace(" LTE", "");

		if (productBrand.equals("APPLE")) {
			if (title.contains("97")) {
				title = title.replaceAll("97", "");
			}
		}
		if (productBrand.equals("IBALL")) {
			if (title.contains("3G")) {
				title = title.replaceAll("3G", "");
				connectivity += " 3G";
			}
			if (title.contains("2G")) {
				title = title.replaceAll("2G", "");
				connectivity += " 2G";
			}
		}

		if (StringUtils.isNotBlank(title)) {
			title = title.trim();
		}

		if (title.contains("I PAD")) {
			title = title.replaceAll("I(\\s+)PAD", "IPAD");
		}

		String[] array = title.split("\\s");
		int length = array.length;

		if (productBrand.equals("TAGITAL")) {
			model = "T7X";
		}
		else {

			if (length == 1) {
				model = title;
			}
			else if (length == 2) {
				productSubBrand = array[0];
				model = array[length - 1];
			}
			else if (length > 2) {
				productSubBrand = array[0];
				model = array[length - 1];
				productSeries = "";
				for (int i = 1; i < length - 1; i++) {
					productSeries += array[i] + " ";
				}
				productSeries = productSeries.trim();
			}
		}
		/*
		 * if(productBrand.equals("APPLE")){ if(productSeries.contains("PAD")){
		 * productSeries=productSeries.replace("PAD", "");
		 * productSubBrand=productSubBrand.concat("PAD"); productSeries =
		 * productSeries.trim(); } }
		 */
		/*
		 * System.out.println(productBrand+ " | " + productSubBrand+ " | " +
		 * productSeries+ " | " + model +" | " + capacity+ " | " + color + " | "
		 * + connectivity);
		 */
		ReviewedProductInfoDTO info = populateDto(productBrand, productSubBrand, productSeries, model, capacity, color, connectivity, prod);

		return info;
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		title = removeDuplicates(title);

		String productBrand = null;
		String color = null;

		productBrand = prod.getProductBrand();
		productBrand = productBrand.toUpperCase().trim();
		title = title.replace(productBrand, "");

		if (StringUtils.isNotBlank(prod.getColor())) {
			color = prod.getColor().toUpperCase();
			if (title.contains(color)) {
				if (title.contains("SPACE")) {
					color = "SPACE " + color;
				}
				title = title.replace(color, "");
			}
		}

		if (title.contains("TABLET")) {
			int indx = title.lastIndexOf("TABLET");
			if (indx > -1) {
				title = title.substring(0, indx - 1);
			}
		}

		String capacity = null;
		String capacity2 = null;
		if (StringUtils.isNotBlank(prod.getSize())) {
			capacity = prod.getSize();

			if (!capacity.contains("GB")) {
				capacity = capacity + " GB";
			}

			capacity2 = capacity.replace(" ", "");

			if (title.contains(capacity) || title.contains(capacity2)) {
				if (title.contains(capacity)) {
					title = title.replace(capacity, "");
				}
				if (title.contains(capacity2)) {
					title = title.replace(capacity2, "");
				}
			}
		}

		// System.out.println(title);
		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll("(WINDOWS)|(MODEL)|(SERIES)|(FAMILY)|((\\s)NON)|(SMOKY)|((\\s)HD)|(EDITION)|(PROCESSOR)|(FOR)|(KIDS)|(CAPACITIVE)|(QUAD)|"
					+ "(OCTA)|(SECOND)|(GRAY)|(WHITE)|(CAMERA)|(NEW)|(GREY)|(METALLIC)|(BLACK)|(BLUE)|(SILVER)|(GOLD)|(SPACE GRAY)|(I KALL)|"
					+ "(TMOBILE)|(VOICE)|(INTERACTIVE)|(INTERNAL)|(STORAGE)|(PENTOUCH)|((\\s)?CELL(ULAR)?)|(MIGADGETS)|(WRITING)|(TABLET)|"
					+ "(RETINA)|(DISPLAY)|((\\s)AND(\\s))|(ANDROID)|(CALLING)|((\\d+)?RAM)|(ROM)|(PC)|(OS)|(LOLLIPOP)|"
					+ "(((DUAL|SINGLE)(\\s)?)?(SIM|CORE))|((\\d+)(\\s*)(INCH(ES)?|CM))|(MIDNIGHT)|(ONLY)|((\\d+)(\\s*)(G|M)B)|(QUAD CORE)", "");
			title = title.trim();
		}

		String connectivity = "WI-FI";
		if (title.contains("WI-FI")) {
			int indx = title.lastIndexOf("WI-FI");
			if (indx > -1) {
				connectivity = title.substring(indx, title.length());
				title = title.substring(0, indx - 1);
			}
		}

		if (title.contains("WIFI")) {
			int indx = title.lastIndexOf("WIFI");
			if (indx > -1) {
				connectivity = title.substring(indx, title.length());
				title = title.substring(0, indx - 1);
			}
		}

		if (title.contains("WITH")) {
			int indx = title.lastIndexOf("WITH");
			if (indx > -1) {
				title = title.substring(0, indx - 1);
			}
		}
		if (title.contains("SINGLE")) {
			int indx = title.lastIndexOf("SINGLE");
			if (indx > -1) {
				title = title.substring(0, indx - 1);
			}
		}

		String c = null;
		String c_regex = "(\\s)(\\d)G";
		Pattern pattern = Pattern.compile(c_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 2)) {
				String tmp = match.group(2);
				if (NumberUtils.isNumber(tmp)) {
					c = tmp + "G";
				}
			}
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
			}
		}
		if (StringUtils.isNotBlank(c)) {
			connectivity = connectivity + " " + c;
		}
		if (title.startsWith("3G")) {
			connectivity += " 3G";
		}

		if (title.contains("VIA DONGLE")) {
			connectivity += " " + "VIA DONGLE";
			title = title.replace("VIA DONGLE", "");
		}
		if (title.contains("LTE")) {
			connectivity += " " + "LTE";
			title = title.replace("LTE", "");
		}
		title = title.replace(" LTE", "");

		if (productBrand.equals("IBALL")) {
			if (title.contains("3G")) {
				title = title.replaceAll("3G", "");
				connectivity += " 3G";
			}
			if (title.contains("2G")) {
				title = title.replaceAll("2G", "");
				connectivity += " 2G";
			}

		}

		if (connectivity.contains("+")) {
			connectivity = connectivity.replace("+", "");
		}
		if (StringUtils.isNotBlank(connectivity)) {
			connectivity = connectivity.replaceAll("\\s+", " ").trim();
		}

		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll("[^\\p{L}\\p{Z}\\p{N}]", "").replaceAll("\\(|\\)", "").trim();
		}

		String model = null;
		String productSubBrand = null;
		String productSeries = null;

		String[] array = title.split("\\s");
		int length = array.length;

		if (length == 1) {
			model = title;
		}
		else if (length == 2) {
			productSubBrand = array[0];
			model = array[length - 1];
		}
		else if (length > 2) {
			productSubBrand = array[0];
			model = array[length - 1];
			productSeries = "";
			for (int i = 1; i < length - 1; i++) {
				productSeries += array[i] + " ";
			}
			productSeries = productSeries.trim();
		}

		// System.out.println(title);

		/*
		 * System.out.println(productBrand+ " | " + productSubBrand+ " | " +
		 * productSeries+ " | " + model +" | " + capacity2+ " | " + color +
		 * " | " + connectivity);
		 */
		ReviewedProductInfoDTO info = populateDto(productBrand, productSubBrand, productSeries, model, capacity2, color, connectivity, prod);
		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String productSubBrand, String productSeries, String model, String capacity, String color,
			String connectivity, HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
		}

		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, productSubBrand, productSeries, model, color, new HashMap<String, String>(),
				prod, null);

		info.getProperties().put("SIZE", capacity == null ? "null" : capacity);
		info.getProperties().put("CONNECTIVITY", connectivity == null ? "null" : connectivity);

		return info;
	}

	/*
	 * public static void main(String[] args) { TabletProcessor p = new
	 * TabletProcessor(); HomeProductInfoDTO prod = new HomeProductInfoDTO();
	 * prod.setTitle("SAMSUNG GALAXY TAB E WITH WIFI + 3G");
	 * prod.setProductBrand("SAMSUNG"); p.snapdealProcess(prod); }
	 */

}