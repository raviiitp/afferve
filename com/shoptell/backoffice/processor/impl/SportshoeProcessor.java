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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

/**
 * @author abhishekagarwal
 *
 */
@Named
public class SportshoeProcessor extends DataProcessor {

	public static final String SEX_MATCH_REGEX = "\\s(((CHILDREN)|(UNISEX)|(WOMEN)|(BABY)|(GIRL)|(TEEN)|(BOY)|(KID)|(MAN)|(MEN))\\/?('?S'?)?\\s?)+";

	public static final String BRACKET_REMOVE_REGEX = "(\\(.*\\))";

	public static final String COLOR_REGEX = "\\s((ULTRABERRY|CORAL|ALABASTER|CHAMPAGNE|CHOCOLATE|SANDSTONE|CHARCOAL|DAZZLING|MAGNETIC|MIDNIGHT|"
			+ "MOONDUST|TITANIUM|CERAMIC|CRIMSON|FROSTED|PANTHER|BRIGHT|BUFFED|ENERGY|MARBLE|ORANGE|SILVER|YELLOW|BLACK|BROWN|GREEN|OLIVE|SLEEK|SPACE|STEEL|WHITE|"
			+ "BLUE|CHIC|DASH|DARK|DEEP|GOLD|GRAY|GREY|MIST|PINK|ROSE|WINE|RED)\\s?(AND\\s)?\\/?,?-?\\s?)+";

	public static final String WORD_REMOVE_REGEX = "(((INTERNATIONAL)|(FEATUREPHONE)|(MANUFACTURER)|(MARTIAL\\sARTS)|(MOBILEPHONE)|"
			+ "(REFURBISHED)|(BASKETBALL)|(UNACTIVATED)|(CUSTOMIZED)|(GYMNASTICS)|(MULTIMEDIA)|(REGISTERED)|(SMARTPHONE)|(VOLLEYBALL)|(201[0-9]?)|(AVAILABLE)|(BADMINTON)|"
			+ "(BLUETOOTH)|(CONDITION)|(DISCOUNTS)|(EXCLUSIVE)|(POLYESTER)|(SMATPHONE)|(SYNTHETIC)|(UNIVERSAL)|(ATHLETIC)|(FOOTBALL)|"
			+ "(FREEBIES)|(IMPORTED)|(ORIGINAL)|(PORTABLE)|(RELIANCE)|(ROTATING)|(SHIPPING)|(SNEAKERS)|(SUPPORTS)|(TRAINING)|(UNLOCKED)|"
			+ "(VODAFONE)|(WARRANTY)|(ANDROID)|(BATTERY)|(DETAILS)|(EDITION)|(FACTORY)|(FASHION)|(FEATURE)|(GENUINE)|(LEATHER)|(LIMITED)|"
			+ "(OUTDOOR)|(PHABLET)|(REPLACE)|(RUNNING)|(SANDALS)|(SERVICE)|(SHIPING)|(SNEAKER)|(SPECIAL)|(SUPPORT)|(VERSION)|(WITHOUT)|([0-9]+%)|"
			+ "(AIRTEL)|(CAMERA)|(COLOUR)|(DESIGN)|(JUNIOR)|(KITKAT)|(LAUNCH)|(LOCKED)|(MOBILE)|(MONTHS)|(ORDERS)|(PACKED)|(RETINA)|(RUBBER)|"
			+ "(SCREEN)|(SEALED)|(SEASON)|(SELLER)|(SINGLE)|(SPORTS)|(SQUASH)|(TABLET)|(TENNIS)|(UNLOCK)|(WEIGHT)|(ABOUT)|(BOOTS)|(BRAND)|"
			+ "(CABLE)|(COLOR)|(COMBO)|(COVER)|(FLASH)|(FRONT)|(INDIA)|(JELLY)|(LIGHT)|(LOWER)|(MONEY)|(MONTH)|(NIGHT)|(OFFER)|(PRICE)|(RADIO)|"
			+ "(SHOES)|(SMART)|(SPORT)|(TOUCH)|(ULTRA)|(WHATS)|(WORTH)|(BEAN)|(BILL)|(CDMA)|(CELL)|(CORE)|(DATA)|(DEAL)|(DUAL)|(EMIS)|(FLAT)|(FLIP)|(FREE)|(FULL)|"
			+ "(GOOD)|(HDMI)|(HUGE)|(IIFA)|(INCH)|(LIKE)|(LOCK)|(MESH)|(ONLY)|(PACK)|(QUAD)|(RARE)|(SALE)|(SEAL)|(SHIP)|(SHOE)|(SIZE)|(UNIT)|"
			+ "(USED)|(WIFI)|(WITH)|(YEAR))(S?)\\s?)+";

	public static final String WORD_REMOVE_REGEX01 = "\\s(((ALL)|(AND)|(APP)|(BLK)|(BOX)|(COD)|(FOR)|(GET)|(GPS)|(GSM)|(GYM)|(INF)|(JNR)|(LED)|(MAH)|(MFG)|(MGF)|"
			+ "(MP3)|(NEW)|(NOW)|(OFF)|(PRE)|(RAM)|(ROM)|(SIM)|(TFT)|(VAT)|(FM)|(IN)|(JR)|(OF)|(OR)|(RS)|(UK)|(XT)|(YR)|(&))(S?)\\s?)+";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shoptell.backoffice.processor.DataProcessor#shopcluesProcess(com.
	 * shoptell.backoffice.repository.dto.HomeProductInfoDTO)
	 */
	@Override
	public ReviewedProductInfoDTO shopcluesProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle().trim().toUpperCase().replaceAll(WORD_REMOVE_REGEX, " ").replaceAll(WORD_REMOVE_REGEX01, " ");
		title = removeDuplicates(title).replaceAll("\\+", " PLUS");

		String subCategoryName = "SPORTSHOES";

		String product_brand = prod.getProductBrand();
		String product_sub_brand = null;
		String model = null;
		String series = null;
		String color = prod.getColor();
		String sex = null;
		String size = prod.getSize();

		Set<String> sexCat = prod.getCategoryPaths();

		if (sexCat != null) {
			if (sexCat.contains("Women's Footwear")) {
				sex = "WOMEN";
			}
			else if (sexCat.contains("Men's Footwear")) {
				sex = "MEN";
			}
			else if (sexCat.contains("Kid's Footwear")) {
				sex = "KIDS";
			}
			else {
				sex = "UNISEX";
			}
		}

		Pattern pattern = Pattern.compile(SEX_MATCH_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replaceAll(tmp, " ");
				// sex = sex.trim();
				break;
			}
		}

		boolean brandFound = false;

		if (StringUtils.isNotBlank(product_brand)) {
			product_brand = product_brand.toUpperCase();
			if (title.contains(product_brand)) {
				brandFound = true;
				title = title.replaceAll(product_brand, " ");
			}
			else {
				int counter = 0;
				StringBuilder tmp = new StringBuilder();
				for (int i = 0; i < product_brand.length(); i++) {
					char c = product_brand.charAt(i);
					int num = c - 40;
					if (num > 0 && num < 27) {
						counter++;
						tmp.append(c);
					}
				}
				StringBuilder sb = new StringBuilder();
				int brkIndx = 0;
				for (int i = 0; i < title.length(); i++) {
					char c = title.charAt(i);
					int num = c - 40;
					if (num > 0 && num < 27) {
						counter--;
						if (counter > 0) {
							sb.append(c);
						}
						else {
							brkIndx = i;
							break;
						}
					}
				}

				if (tmp.toString().equalsIgnoreCase(sb.toString())) {
					brandFound = true;
					title = title.substring(brkIndx);
				}
			}
		}

		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
			title = title.replaceAll(color, " ");
		}

		pattern = Pattern.compile(BRACKET_REMOVE_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replace(tmp, "");
				break;
			}
		}

		pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replaceAll(tmp, " ");
				if (StringUtils.isBlank(color)) {
					color = tmp.trim();
				}
				break;
			}
		}

		title = title.replaceAll("\\s+", " ").trim();

		Set<String> set = new LinkedHashSet<String>(Arrays.asList(title.split(" ")));

		for (String element : set) {
			if (StringUtils.isNotBlank(element)) {
				if (element.contains("-")) {
					break;
				}
				if (!brandFound) {
					if (!BackofficeUtil.shoeBrandSet.contains(element)) {
						continue;
					}
					product_brand = element;
					brandFound = true;
					continue;
				}
				else if (StringUtils.isBlank(product_sub_brand)) {
					product_sub_brand = element;
					continue;
				}
				else if (StringUtils.isBlank(series)) {
					series = element;
					continue;
				}
				else if (StringUtils.isBlank(model)) {
					model = element;
				}
				else {
					if (element.length() < 4) {
						model += " " + element;
					}
				}
			}
		}

		ReviewedProductInfoDTO info = populateDto(product_brand, product_sub_brand, series, model, color, size, sex, prod);

		return info;
	}

	private ReviewedProductInfoDTO populateDto(String product_brand, String product_sub_brand, String series, String model, String color, String size,
			String sex, HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)){
			color = color.toUpperCase();
		}
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(product_brand, product_sub_brand, series, model, color, new HashMap<String, String>(), prod,
				null);
		info.getProperties().put("SIZE", size == null ? "null" : size);
		info.getProperties().put("SEX", sex == null ? "null" : sex);
		return info;
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
		String title = prod.getTitle().trim().toUpperCase().replaceAll(WORD_REMOVE_REGEX, " ").replaceAll(WORD_REMOVE_REGEX01, " ");
		title = removeDuplicates(title).replaceAll("\\+", " PLUS");

		String subCategoryName = "SPORTSHOES";

		String product_brand = prod.getProductBrand();
		String product_sub_brand = null;
		String model = null;
		String series = null;
		String color = prod.getColor();
		String sex = null;
		String size = prod.getSize();

		String sexCat = (String) prod.getCategoryPaths().toArray()[0];

		if ("Women's Shoes".equalsIgnoreCase(sexCat)) {
			sex = "WOMEN";
		}
		else if ("Men's Shoes".equalsIgnoreCase(sexCat)) {
			sex = "MEN";
		}
		else if ("Kid's Shoes".equalsIgnoreCase(sexCat)) {
			sex = "KIDS";
		}
		else {
			sex = "UNISEX";
		}

		Pattern pattern = Pattern.compile(SEX_MATCH_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replaceAll(tmp, " ");
				// sex = sex.trim();
				break;
			}
		}

		boolean brandFound = false;

		if (StringUtils.isNotBlank(product_brand)) {
			product_brand = product_brand.toUpperCase();
			if (title.contains(product_brand)) {
				brandFound = true;
				title = title.replaceAll(product_brand, " ");
			}
			else {
				int counter = 0;
				StringBuilder tmp = new StringBuilder();
				for (int i = 0; i < product_brand.length(); i++) {
					char c = product_brand.charAt(i);
					int num = c - 40;
					if (num > 0 && num < 27) {
						counter++;
						tmp.append(c);
					}
				}
				StringBuilder sb = new StringBuilder();
				int brkIndx = 0;
				for (int i = 0; i < title.length(); i++) {
					char c = title.charAt(i);
					int num = c - 40;
					if (num > 0 && num < 27) {
						counter--;
						if (counter > 0) {
							sb.append(c);
						}
						else {
							brkIndx = i;
							break;
						}
					}
				}

				if (tmp.toString().equalsIgnoreCase(sb.toString())) {
					brandFound = true;
					title = title.substring(brkIndx);
				}
			}
		}

		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
			title = title.replaceAll(color, " ");
		}

		pattern = Pattern.compile(BRACKET_REMOVE_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replace(tmp, "");
				break;
			}
		}

		pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replaceAll(tmp, " ");
				if (StringUtils.isBlank(color)) {
					color = tmp.trim();
				}
				break;
			}
		}

		title = title.replaceAll("\\s+", " ").trim();

		Set<String> set = new LinkedHashSet<String>(Arrays.asList(title.split(" ")));

		for (String element : set) {
			if (StringUtils.isNotBlank(element)) {
				if (element.contains("-")) {
					break;
				}
				if (!brandFound) {
					if (!BackofficeUtil.shoeBrandSet.contains(element)) {
						continue;
					}
					product_brand = element;
					brandFound = true;
					continue;
				}
				else if (StringUtils.isBlank(product_sub_brand)) {
					product_sub_brand = element;
					continue;
				}
				else if (StringUtils.isBlank(series)) {
					series = element;
					continue;
				}
				else if (StringUtils.isBlank(model)) {
					model = element;
				}
				else {
					if (element.length() < 4) {
						model += " " + element;
					}
				}
			}
		}

		ReviewedProductInfoDTO info = populateDto(product_brand, product_sub_brand, series, model, color, size, sex, prod);

		return info;
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
		String title = prod.getTitle().trim().toUpperCase().replaceAll(WORD_REMOVE_REGEX, " ").replaceAll(WORD_REMOVE_REGEX01, " ");
		title = removeDuplicates(title).replaceAll("\\+", " PLUS");

		String subCategoryName = "SPORTSHOES";

		String product_brand = prod.getProductBrand();
		String product_sub_brand = null;
		String model = null;
		String series = null;
		String color = prod.getColor();
		String sex = null;
		String size = prod.getSize();

		if ("Womens_Footwear".equalsIgnoreCase(prod.getCategoryName())) {
			sex = "WOMEN";
		}
		else if ("Mens_Footwear".equalsIgnoreCase(prod.getCategoryName())) {
			sex = "MEN";
		}
		else if ("Kids_Footwear".equalsIgnoreCase(prod.getCategoryName())) {
			sex = "KIDS";
		}
		else {
			sex = "UNISEX";
		}

		Pattern pattern = Pattern.compile(SEX_MATCH_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replaceAll(tmp, " ");
				// sex = sex.trim();
				break;
			}
		}

		boolean brandFound = false;

		if (StringUtils.isNotBlank(product_brand)) {
			product_brand = product_brand.toUpperCase();
			if (title.contains(product_brand)) {
				brandFound = true;
				title = title.replaceAll(product_brand, " ");
			}
			else {
				int counter = 0;
				StringBuilder tmp = new StringBuilder();
				for (int i = 0; i < product_brand.length(); i++) {
					char c = product_brand.charAt(i);
					int num = c - 40;
					if (num > 0 && num < 27) {
						counter++;
						tmp.append(c);
					}
				}
				StringBuilder sb = new StringBuilder();
				int brkIndx = 0;
				for (int i = 0; i < title.length(); i++) {
					char c = title.charAt(i);
					int num = c - 40;
					if (num > 0 && num < 27) {
						counter--;
						if (counter > 0) {
							sb.append(c);
						}
						else {
							brkIndx = i;
							break;
						}
					}
				}

				if (tmp.toString().equalsIgnoreCase(sb.toString())) {
					brandFound = true;
					title = title.substring(brkIndx);
				}
			}
		}

		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
			title = title.replaceAll(color, " ");
		}

		pattern = Pattern.compile(BRACKET_REMOVE_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replace(tmp, "");
				break;
			}
		}

		pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replaceAll(tmp, " ");
				if (StringUtils.isBlank(color)) {
					color = tmp.trim();
				}
				break;
			}
		}

		title = title.replaceAll("\\s+", " ").trim();

		Set<String> set = new LinkedHashSet<String>(Arrays.asList(title.split(" ")));

		for (String element : set) {
			if (StringUtils.isNotBlank(element)) {
				if (element.contains("-")) {
					break;
				}
				if (!brandFound) {
					if (!BackofficeUtil.shoeBrandSet.contains(element)) {
						continue;
					}
					product_brand = element;
					brandFound = true;
					continue;
				}
				else if (StringUtils.isBlank(product_sub_brand)) {
					product_sub_brand = element;
					continue;
				}
				else if (StringUtils.isBlank(series)) {
					series = element;
					continue;
				}
				else if (StringUtils.isBlank(model)) {
					model = element;
				}
				else {
					if (element.length() < 4) {
						model += " " + element;
					}
				}
			}
		}

		ReviewedProductInfoDTO info = populateDto(product_brand, product_sub_brand, series, model, color, size, sex, prod);

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
		String title = prod.getTitle().trim().toUpperCase().replaceAll(WORD_REMOVE_REGEX, " ").replaceAll(WORD_REMOVE_REGEX01, " ");
		title = removeDuplicates(title).replaceAll("\\+", " PLUS");

		String subCategoryName = "SPORTSHOES";

		String product_brand = prod.getProductBrand();
		String product_sub_brand = null;
		String model = null;
		String series = null;
		String color = prod.getColor();
		String sex = null;
		String size = prod.getSize();

		String sexCat = (String) prod.getCategoryPaths().toArray()[0];

		if ("Women's Shoes".equalsIgnoreCase(sexCat)) {
			sex = "WOMEN";
		}
		else if ("Boys' Shoes".equalsIgnoreCase(sexCat)) {
			sex = "MEN";
		}
		else if ("Girls' Shoes".equalsIgnoreCase(sexCat)) {
			sex = "GIRLS";
		}
		else if ("kids_footwear".equalsIgnoreCase(sexCat)) {
			sex = "KIDS";
		}
		else {
			sex = "UNISEX";
		}

		Pattern pattern = Pattern.compile(SEX_MATCH_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replaceAll(tmp, " ");
				// sex = sex.trim();
				break;
			}
		}

		boolean brandFound = false;

		if (StringUtils.isNotBlank(product_brand)) {
			product_brand = product_brand.toUpperCase();
			if (title.contains(product_brand)) {
				brandFound = true;
				title = title.replaceAll(product_brand, " ");
			}
			else {
				int counter = 0;
				StringBuilder tmp = new StringBuilder();
				for (int i = 0; i < product_brand.length(); i++) {
					char c = product_brand.charAt(i);
					int num = c - 40;
					if (num > 0 && num < 27) {
						counter++;
						tmp.append(c);
					}
				}
				StringBuilder sb = new StringBuilder();
				int brkIndx = 0;
				for (int i = 0; i < title.length(); i++) {
					char c = title.charAt(i);
					int num = c - 40;
					if (num > 0 && num < 27) {
						counter--;
						if (counter > 0) {
							sb.append(c);
						}
						else {
							brkIndx = i;
							break;
						}
					}
				}

				if (tmp.toString().equalsIgnoreCase(sb.toString())) {
					brandFound = true;
					title = title.substring(brkIndx);
				}
			}
		}

		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
			title = title.replaceAll(color, " ");
		}

		pattern = Pattern.compile(BRACKET_REMOVE_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replace(tmp, "");
				break;
			}
		}

		pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replaceAll(tmp, " ");
				if (StringUtils.isBlank(color)) {
					color = tmp.trim();
				}
				break;
			}
		}

		title = title.replaceAll("\\s+", " ").trim();

		Set<String> set = new LinkedHashSet<String>(Arrays.asList(title.split(" ")));

		for (String element : set) {
			if (StringUtils.isNotBlank(element)) {
				if (element.contains("-")) {
					break;
				}
				if (!brandFound) {
					if (!BackofficeUtil.shoeBrandSet.contains(element)) {
						continue;
					}
					product_brand = element;
					brandFound = true;
					continue;
				}
				else if (StringUtils.isBlank(product_sub_brand)) {
					product_sub_brand = element;
					continue;
				}
				else if (StringUtils.isBlank(series)) {
					series = element;
					continue;
				}
				else if (StringUtils.isBlank(model)) {
					model = element;
				}
				else {
					if (element.length() < 4) {
						model += " " + element;
					}
				}
			}
		}

		ReviewedProductInfoDTO info = populateDto(product_brand, product_sub_brand, series, model, color, size, sex, prod);

		/*
		 * if (prod.getVariations() != null) { Set<String> variants = new
		 * HashSet<String>(prod.getVariations()); info.setVariants(variants); }
		 */

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

		String title = prod.getTitle().trim().toUpperCase().replaceAll(WORD_REMOVE_REGEX, " ").replaceAll(WORD_REMOVE_REGEX01, " ");
		title = removeDuplicates(title).replaceAll("\\+", " PLUS");

		String subCategoryName = "SPORTSHOES";

		String product_brand = prod.getProductBrand();
		String product_sub_brand = null;
		String model = null;
		String series = null;
		String color = prod.getColor();
		String sex = null;
		String size = prod.getSize();

		if ("womens_footwear".equalsIgnoreCase(prod.getCategoryName())) {
			sex = "WOMEN";
		}
		else if ("mens_footwear".equalsIgnoreCase(prod.getCategoryName())) {
			sex = "MEN";
		}
		else if ("kids_footwear".equalsIgnoreCase(prod.getCategoryName())) {
			sex = "KIDS";
		}
		else {
			sex = "UNISEX";
		}

		Pattern pattern = Pattern.compile(SEX_MATCH_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replaceAll(tmp, " ");
				// sex = sex.trim();
				break;
			}
		}

		boolean brandFound = false;

		if (StringUtils.isNotBlank(product_brand)) {
			product_brand = product_brand.toUpperCase();
			if (title.contains(product_brand)) {
				brandFound = true;
				title = title.replaceAll(product_brand, " ");
			}
			else {
				int counter = 0;
				StringBuilder tmp = new StringBuilder();
				for (int i = 0; i < product_brand.length(); i++) {
					char c = product_brand.charAt(i);
					int num = c - 40;
					if (num > 0 && num < 27) {
						counter++;
						tmp.append(c);
					}
				}
				StringBuilder sb = new StringBuilder();
				int brkIndx = 0;
				for (int i = 0; i < title.length(); i++) {
					char c = title.charAt(i);
					int num = c - 40;
					if (num > 0 && num < 27) {
						counter--;
						if (counter > 0) {
							sb.append(c);
						}
						else {
							brkIndx = i;
							break;
						}
					}
				}

				if (tmp.toString().equalsIgnoreCase(sb.toString())) {
					brandFound = true;
					title = title.substring(brkIndx);
				}
			}
		}

		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
			title = title.replaceAll(color, " ");
		}

		pattern = Pattern.compile(BRACKET_REMOVE_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replace(tmp, "");
				break;
			}
		}

		pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(title);
		while (matcher.find()) {
			if (!StringUtils.isEmpty(matcher.group(0))) {
				String tmp = matcher.group(0);
				title = title.replaceAll(tmp, " ");
				if (StringUtils.isBlank(color)) {
					color = tmp.trim();
				}
				break;
			}
		}

		title = title.replaceAll("\\s+", " ").trim();

		Set<String> set = new LinkedHashSet<String>(Arrays.asList(title.split(" ")));

		for (String element : set) {
			if (StringUtils.isNotBlank(element)) {
				if (!brandFound) {
					if (!BackofficeUtil.shoeBrandSet.contains(element)) {
						continue;
					}
					product_brand = element;
					brandFound = true;
					continue;
				}
				else if (StringUtils.isBlank(product_sub_brand)) {
					product_sub_brand = element;
					continue;
				}
				else if (StringUtils.isBlank(series)) {
					series = element;
					continue;
				}
				else if (StringUtils.isBlank(model)) {
					model = element;
				}
				else {
					if (element.length() < 4) {
						model += " " + element;
					}
				}
			}
		}

		ReviewedProductInfoDTO info = populateDto(product_brand, product_sub_brand, series, model, color, size, sex, prod);

		/*
		 * if (prod.getSizeVariants() != null) { String[] array =
		 * prod.getSizeVariants().split(",\\s"); if (array != null &&
		 * array.length > 0) { Set<String> variants = new
		 * HashSet<String>(Arrays.asList(array)); info.setVariants(variants); }
		 * }
		 */

		return info;
	}

}
