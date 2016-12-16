/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Vinay Kumar Chaudhary --- vkc932@gmail.com
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
public class EyewearProcessor extends DataProcessor {

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
		if (StringUtils.isNotBlank(title)) {
			title = title.toUpperCase().trim();
		}

		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replaceAll(productBrand + " ", "");
		}

		String temp = title;
		String idealfor = null;
		if (StringUtils.isNotBlank(temp)) {
			boolean kid = temp.contains("KID");
			boolean unisex = temp.contains("UNISEX");
			boolean woman = temp.contains("GIRL") || temp.contains("WOMAN") || temp.contains("WOMEN") || temp.contains("FEMALE") || temp.contains("LADIES");
			temp = temp.replaceAll("(WOMEN)|(WOMAN)|(FEMALE)", "");
			boolean man = temp.contains("BOY") || temp.contains("MAN") || temp.contains("MEN") || temp.contains("MALE");
			boolean couple = temp.contains("COUPLE");

			if ((man && woman) || unisex || couple) {
				idealfor = "UNISEX";
			}
			else if (kid) {
				idealfor = "KIDS";
			}
			else if (man) {
				idealfor = "MEN";
			}
			else if (woman) {
				idealfor = "WOMEN";
			}
		}
		title = title.replaceAll(
				"(MEN'S)|(UNISEX)|(LADIES)|(FEMALES?)|(MENS)|(MEN)|(MALES?)|(GIRLS?)|(MAN)|(WOMEN'S)|(WOMENS)|(WOMEN)|(WOMAN)|(KID'S)|(KIDS)|(COUPLE)", "");

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase().trim();
		}
		else {
			color = retriveColor(prod);
			if (StringUtils.isNotBlank(color)) {
				color = color.toUpperCase().trim();
			}
		}
		if (StringUtils.isNotBlank(color)) {
			title = title.replaceAll(color, "");
		}

		String frameType = null;
		if (title.contains("FULL RIM")) {
			frameType = "FULL RIM";
		}
		else if (title.contains("HALF RIM")) {
			frameType = "HALF RIM";
		}
		else if (title.contains("RIMLESS")) {
			frameType = "RIMLESS";
		}
		title = title.replaceAll("(RIMLESS)|(FULL RIM)|(HALF RIM)|( FRAMES?)", "").trim();

		Pattern p = Pattern.compile("(?:[0-9]+[A-Z-]|[A-Z]+[0-9_-])[A-Z0-9_-]*");
		String model = null;
		
		Matcher m = p.matcher(title);
		if (m.find()) {
			model = m.group(0);
			if (StringUtils.isNotBlank(model)){
				title = title.replaceAll(model, "");
			}
		}
		
		p = Pattern.compile("(CAT(\\-|\\s)?EYE)|(AVIATOR)|(OVAL)|(OVER(\\-||\\s)?SIZED)|(RECTANGLE)|RECTANGULAR|(ROUND)|(SPORT)|(WAYFARER)|(SQUARE)|(WRAP(\\s|\\-)?ARROUND)");
		String frameShape = null;
		m = p.matcher(title);
		if (m.find()) {
			frameShape = m.group(0);
		}
		if (StringUtils.isNotBlank(frameShape)) {
			title = title.replaceAll(frameShape, "");
			frameShape = frameShape.replaceAll("([-])|(\\s)", "");
			if ("RECTANGULAR".equalsIgnoreCase(frameShape)) {
				frameShape = "RECTANGLE";
			}
		}

		// Extracting--above incomplete information using --description-START
		String description = prod.getDescription();
		if (StringUtils.isNotBlank(description)) {
			description = description.toUpperCase();
			// color-Start
			if (StringUtils.isBlank(color)) {
				String colorregex = "FRAME\\s?COLOU?R\\s?:\\s?([A-Z][A-Z]+(\\sBLACK)?)";
				p = Pattern.compile(colorregex);

				m = p.matcher(description);
				if (m.find() && isMatchPresent(m, 1)) {
					color = m.group(1);
				}
				// System.out.println("color des " + color);
			}
			// color-end

			// FrameSHAPE-START
			if (StringUtils.isBlank(frameShape)) {
				String shaperegex = "FRAME\\s?(STYLE\\/)?SHAPE\\s?:\\s?([A-Z]+)";
				p = Pattern.compile(shaperegex);

				m = p.matcher(description);
				if (m.find() && isMatchPresent(m, 2)) {
					frameShape = m.group(2);
				}
				if (StringUtils.isNotBlank(frameShape)) {
					if ("WRAP".equalsIgnoreCase(frameShape)) {
						frameShape = "WRAPARROUND";
					}
					frameShape = frameShape.replaceAll("[-]|\\s", "");
					if ("RECTANGULAR".equalsIgnoreCase(frameShape)) {
						frameShape = "RECTANGLE";
					}
					if ("CAT".equalsIgnoreCase(frameShape)) {
						frameShape = "CATEYE";
					}
					if ("BUG".equalsIgnoreCase(frameShape)) {
						frameShape = "BUGEYE";
					}
				}
				// System.out.println("frameShape des " + frameShape);
			}

			// FrameSHAPE-END

			// FrameTYPE-START
			if (StringUtils.isBlank(frameType)) {
				if (description.contains("FULL RIM")) {
					frameType = "FULL RIM";
				}
				else if (description.contains("HALF RIM") || description.contains("SEMI RIM")) {
					frameType = "HALF RIM";
				}
				else if (description.contains("RIMLESS")) {
					frameType = "RIMLESS";
				}

				// System.out.println("frameType des " + frameType);
			}
			// frameTYPE-END

			// model--START
			if (StringUtils.isBlank(model)) {
				String modelregex = "((PRODUCT CODE)|(MODEL NUMBER))\\s?:\\s?([A-Z0-9_.-]+)";
				p = Pattern.compile(modelregex);
				m = p.matcher(description);
				if (m.find() && isMatchPresent(m, 4)) {
					model = m.group(4); // change it to model ( model1)
				}
			}
			// model-END
		}
		// Extracting--above incomplete information using --description-END

		// System.out.println(header + " --S--> " + title + " | " + productBrand
		// + " | " + model + " | " + frameType + " | " + frameShape + " | " +
		// idealfor
		// + " | " + color);
		ReviewedProductInfoDTO info = populateDto(productBrand, model, idealfor, frameType, frameShape, color, prod);
		return info;

	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		if (StringUtils.isNotBlank(title)) {
			title = title.toUpperCase().trim();
		}
		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replaceAll(productBrand + "(\\s|[-])", "");
		}

		String temp = title;
		String idealfor = null;
		if (StringUtils.isNotBlank(temp)) {
			boolean kid = temp.contains("KID");
			boolean unisex = temp.contains("UNISEX");
			boolean woman = temp.contains("GIRL") || temp.contains("WOMAN") || temp.contains("WOMEN") || temp.contains("LADIES") || temp.contains("FEMALE");

			temp = temp.replaceAll("(WOMEN)|(WOMAN)|(FEMALE)", "");
			boolean man = temp.contains("BOY") || temp.contains("MAN") || temp.contains("MALE") || temp.contains("MEN");
			boolean couple = temp.contains("COUPLE");

			if ((man && woman) || unisex || couple) {
				idealfor = "UNISEX";
			}
			else if (kid) {
				idealfor = "KIDS";
			}
			else if (man) {
				idealfor = "MEN";
			}
			else if (woman) {
				idealfor = "WOMEN";
			}
		}
		title = title.replaceAll("(MEN'S)|(UNISEX)|(MENS)|(MEN)|(GIRLS?)|(MAN)|(WOMEN'S)|(WOMENS)|(WOMEN)|(WOMAN)|(KID'S)|(KIDS)|(COUPLE)", "");

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase().trim();

		}
		else {
			color = retriveColor(prod);
			if (StringUtils.isNotBlank(color)) {
				color = color.toUpperCase().trim();
			}

		}
		if (StringUtils.isNotBlank(color)) {
			title = title.replaceAll(color, "");
		}

		String frameType = null;
		if (title.contains("FULL RIM")) {
			frameType = "FULL RIM";
		}
		else if (title.contains("HALF RIM")) {
			frameType = "HALF RIM";
		}
		else if (title.contains("RIMLESS")) {
			frameType = "RIMLESS";
		}
		title = title.replaceAll("(RIMLESS)|(FULL RIM)|(HALF RIM)|( FRAMES?)", "").trim();

		Pattern p = Pattern.compile("(?:[0-9]+[A-Z]|[A-Z]+[0-9])[A-Z0-9_-]*");

		String model = prod.getModel();
		if (StringUtils.isBlank(model)) {
			Matcher m = p.matcher(title);
			if (m.find()) {
				model = m.group(0);
			}
		}

		if (StringUtils.isNotBlank(model)) {
			title = title.replaceAll(model, "");

		}

		String frameShapeRegex = "(CAT(\\-|\\s)?EYE)|(AVIATOR)|(OVAL)|(OVER(\\-|\\s)SIZED)|(RECTANGLE)|RECTANGULAR|(ROUND)|(SPORT)|(WAYFARER)|WRAP(\\s|\\-)ARROUND|(SQUARE)";

		p = Pattern.compile(frameShapeRegex);
		String frameShape = null;
		Matcher m = p.matcher(title);
		if (m.find()) {
			frameShape = m.group(0);
		}
		if (StringUtils.isNotBlank(frameShape)) {
			title = title.replaceAll(frameShape, "");
			frameShape = frameShape.replaceAll("([-])|(\\s)", "");
			if ("RECTANGULAR".equalsIgnoreCase(frameShape)) {
				frameShape = "RECTANGLE";
			}
		}
		// System.out.println(header+" --A--> "+title+" | "+productBrand+" | "+model+" | "+frameType+" | "+frameShape+" | "+idealfor+" | "+color);
		ReviewedProductInfoDTO info = populateDto(productBrand, model, idealfor, frameType, frameShape, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		if (StringUtils.isNotBlank(title)) {
			title = title.toUpperCase().trim();
		}
		String productBrand = prod.getProductBrand();

		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
		}
		title = title.replaceAll(productBrand + " ", "");

		String frameType = null;
		if (title.contains("FULL RIM")) {
			frameType = "FULL RIM";
		}
		else if (title.contains("HALF RIM")) {
			frameType = "HALF RIM";
		}
		else if (title.contains("RIMLESS")) {
			frameType = "RIMLESS";
		}

		// frame Shape--START
		String frameShapeRegex = "(CAT(\\-|\\s)?EYE)|(AVIATOR)|(OVAL)|(OVER(\\-||\\s)?SIZED)|(RECTANGLE)|RECTANGULAR|(ROUND)|(SPORT)|(WAYFARER)|(SQUARE)|(WRAP(\\s|\\-)?ARROUND)";

		Pattern p1 = Pattern.compile(frameShapeRegex);
		String frameShape = null;
		Matcher m1 = p1.matcher(title);
		if (m1.find()) {
			frameShape = m1.group(0);
		}
		if (StringUtils.isNotBlank(frameShape)) {
			title = title.replaceAll(frameShape, "");
			frameShape = frameShape.replaceAll("([-])|(\\s)", "");
			if ("RECTANGULAR".equalsIgnoreCase(frameShape)) {
				frameShape = "RECTANGLE";
			}
		}
		// FRAME SHAPE END

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase().trim();
		}
		else {
			color = retriveColor(prod);
		}

		String idealfor = null;

		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());
		String temp = flipkartUpdateFeatures(map, "General", "Ideal For");
		if (StringUtils.isNotBlank(temp)) {
			temp = temp.toUpperCase();
			boolean kid = temp.contains("KID");
			boolean unisex = temp.contains("UNISEX");
			boolean woman = temp.contains("GIRL") || temp.contains("WOMAN") || temp.contains("WOMEN") || temp.contains("LADIES") || temp.contains("FEMALE");
			temp = temp.replaceAll("(WOMEN)|(WOMAN)|(FEMALE)", "");
			boolean man = temp.contains("BOY") || temp.contains("MAN") || temp.contains("MEN") || temp.contains("MALE");
			boolean couple = temp.contains("COUPLE");

			if ((man && woman) || unisex || couple) {
				idealfor = "UNISEX";
			}
			else if (kid) {
				idealfor = "KIDS";
			}
			else if (man) {
				idealfor = "MEN";
			}
			else if (woman) {
				idealfor = "WOMEN";
			}
		}

		String model = flipkartUpdateFeatures(map, "General", "Style Code");
		if (StringUtils.isNotBlank(model)) {
			model = model.toUpperCase();
		}

		// System.out.println(productBrand+" | "+model+" | "+frameType+" | "+frameShape+" | "+color+" | "+idealfor);
		ReviewedProductInfoDTO info = populateDto(productBrand, model, idealfor, frameType, frameShape, color, prod);
		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String model, String idealfor, String frametype, String frameshape, String color,
			HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
		}
		productBrand = productBrand.replace("-", "");
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, null, null, model, color, new HashMap<String, String>(), prod, null);

		info.getProperties().put("IDEAL FOR", idealfor == null ? "null" : idealfor);
		info.getProperties().put("FRAME TYPE", frametype == null ? "null" : frametype);
		info.getProperties().put("FRAME SHAPE", frameshape == null ? "null" : frameshape);

		return info;
	}

}
