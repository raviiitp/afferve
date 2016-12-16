/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *			Vinay Kumar Chaudhary -- vkc932@gmail.com
 ****************************************************************************/
package com.shoptell.backoffice.processor.impl;

import static com.shoptell.backoffice.BackofficeUtil.flipkartUpdateFeatures;
import static com.shoptell.backoffice.BackofficeUtil.jsonToMap;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

@Named
public class SunglassesProcessor extends DataProcessor {

	@Override
	public ReviewedProductInfoDTO shopcluesProcess(HomeProductInfoDTO prod) {
		return null;
	}

	@Override
	public ReviewedProductInfoDTO ebayProcess(HomeProductInfoDTO prod) {
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
			boolean woman = temp.contains("GIRL") || temp.contains("WOMAN") || temp.contains("WOMEN") || temp.contains("LADIES");
			temp = temp.replaceAll("(WOMEN)|(WOMAN)", "");
			boolean man = temp.contains("BOY") || temp.contains("MAN") || temp.contains("MEN");
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
		String frameShapeRegex = "(CAT(\\-|\\s)?EYE)|(AVIATOR)|(OVAL)|(OVER(\\-|\\s)?SIZED)|(WRAP(\\-|\\s)?ARROUND)|(RECTANGLE)|RECTANGULAR|(ROUND)|(SPORT)|(WAYFARER)|(SQUARE)";

		Pattern p = Pattern.compile(frameShapeRegex);
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

		String model = null;
		Pattern p1 = Pattern.compile("(?:[0-9]+[A-Z]|[A-Z]+[0-9_/.-]|[0-9][0-9][0-9][0-9]+)[A-Z0-9/_-]*");
		Matcher m1 = p1.matcher(title);
		if (m1.find()) {
			model = m1.group(0);
		}

		if (StringUtils.isNotBlank(model)) {
			title = title.replaceAll(model, "");
		}

		String color = prod.getColor();

		if (StringUtils.isBlank(color)) {
			color = retriveColor(prod);
		}
		if ("JOE BLACK".equalsIgnoreCase(productBrand)) {
			color = null;
			String COLOR_REGEX = "("
			/* 10 */+ "BLACK ONYX|BLACK TITAN|CHAMPANGNE|ONYX BLACK|"
			/* 9 */+ "ALABASTER|BALLISTIC|CHAMPAGNE|CHAMPANGE|CHOCOLATE|SANDSTONE|TURQUOISE|"
			/* 8 */+ "CHARCOAL|DAZZLING|GRAPHITE|GUNMETAL|MAGNETIC|METALLIC|MIDNIGHT|MILKYWAY|MOONDUST|SANTRONI|SHIMMERY|TITANIUM|"
			/* 7 */+ "CERAMIC|CHESTNUT|CLASSIC|FROSTED|LEATHER|MAGENTA|"
			/* 6 */+ "ARCTIC|BRIGHT|BRONZE|BRUSHED|BUFFED|CARBON|CHROME|COFFEE|COPPER|FERVOR|GLOSSY|GOLDEN|MARBLE|ORANGE|PEBBLE|PURPLE|SILVER|YELLOW|"
			/* 5 */+ "BIRCH|BLACK|BLUSH|BROWN|CORAL|FROST|GREEN|METAL|MILKY|PEARL|ROAST|ROYAL|SLEEK|SLATE|SPACE|STARRY|STEEL|WHITE|"
			/* 4 */+ "BLUE|CHIC|CYAN|DARK|DEEP|GOLD|GRAY|GREY|MINT|MIST|PINK|PURE|ROSE|SNOW|WINE|"
			/* 3 */+ "GUN|ICE|ICY|JET|RED|GUN|DEMI|(MULTI(\\s|\\-)?COLOR))";
			// AQUA|

			p = Pattern.compile(COLOR_REGEX);
			title = title.replaceAll("JOEBLACK", ""); // Special Case just for
														// joe black;
			m = p.matcher(title);
			if (m.find()) {
				color = m.group(0);
			}
		}

		if (StringUtils.isNotBlank(color)) {
			title = title.replaceAll(color, "");
		}

		// Extracting-incomplete properties using description---START
		String description = prod.getDescription();
		if (StringUtils.isNotBlank(description)) {
			description = description.toUpperCase();
			// idealFor--START
			if (StringUtils.isBlank(idealfor)) {
				temp = description;

				boolean kid = temp.contains("KID");
				boolean unisex = temp.contains("UNISEX");
				boolean woman = temp.contains("GIRL") || temp.contains("WOMAN") || temp.contains("LADIES") || temp.contains("FEMALE") || temp.contains("WOMEN");
				temp = temp.replaceAll("(WOMEN)|(WOMAN)|(FEMALE)", "");
				boolean man = temp.contains("BOY") || temp.contains("MALE") || temp.contains("MAN") || temp.contains("GENT") || temp.contains("MEN");
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

				// System.out.println("des--> "+idealfor);
			}
			// idealFor-END

			// color-Start
			if (StringUtils.isBlank(color)) {
				String colorregex = "FRAME\\s?COLOU?R\\s?:\\s?([A-Z]+(\\sBLACK)?)";
				p = Pattern.compile(colorregex);

				m = p.matcher(description);
				if (m.find() && isMatchPresent(m, 1)) {
					color = m.group(1);
				}
				// System.out.println("color des "+color);
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
			}

			// FrameSHAPE-END

			// model--START
			if (StringUtils.isBlank(model)) {
				String modelregex = "((PRODUCT CODE)|(MODEL NUMBER))\\s?:\\s?([A-Z0-9_.-]+)";
				p = Pattern.compile(modelregex);
				m = p.matcher(description);
				if (m.find()) {
					model = m.group(4);
				}
			}
			// model-END
		}

		// Extracting-incomplete properties using description---END

		// System.out.println(header+" --S!--> "+title+" | "+productBrand+" | "+model+" | "+frameShape+" | "+idealfor+" | "+color);
		ReviewedProductInfoDTO info = populateDto(productBrand, model, idealfor, frameShape, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		if (StringUtils.isNotBlank(title)) {
			title = title.toUpperCase().trim();
		}
		else {
			return null;
		}

		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replaceAll(productBrand + " ", "");
		}
		else {
			return null;
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
		String frameShapeRegex = "(CAT(\\-|\\s)?EYE)|(AVIATOR)|(OVAL)|(OVER(\\s|\\-)?SIZED)|(RECTANGLE)|RECTANGULAR|(ROUND)|(SPORT)|(WAYFARER)|(SQUARE)|WRAP(\\s|\\-)?ARROUND";

		Pattern p = Pattern.compile(frameShapeRegex);
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

		String model = prod.getModel();
		// String model=null;
		if (StringUtils.isBlank(model)) {
			Pattern p1 = Pattern.compile("(?:[0-9]+[A-Z]|[A-Z]+[0-9_.-])[A-Z0-9_+-]*");
			Matcher m1 = p1.matcher(title);
			if (m1.find()) {
				model = m1.group(0);
			}
			if (StringUtils.isNotBlank(model)) {
				if (model.contains("+")) {
					model = null;
				}
			}
		}
		else {
			model = model.toUpperCase();
			model = model.replaceAll("(STYLE NO:)|(MODEL NO :)|(MOD.)", "").trim();
		}

		if (StringUtils.isNotBlank(model)) {
			title = title.replaceAll(model, "");
		}

		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase().trim();
		}
		else {
			color = retriveColor(prod);
		}
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase().trim();
			color = color.replaceAll("::", ",");
		}

		// System.out.println(header+" | "+model1+" | "+model);
		// System.out.println(header + "--A-->" + title + " | " +
		// productBrand+" | "+model+" | "+frameShape+" | "+idealfor+" | "+color);
		ReviewedProductInfoDTO info = populateDto(productBrand, model, idealfor, frameShape, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		if (StringUtils.isNotBlank(title)) {
			title = title.toUpperCase().trim();
		}
		else {
			return null;
		}

		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replaceAll(productBrand + " ", "");
		}
		else {
			return null;
		}

		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());

		// FrameShape--FindinBLOCk--START-
		String frameShape = null;
		frameShape = flipkartUpdateFeatures(map, "General", "Style");
		if (StringUtils.isNotBlank(frameShape)) {
			String temp = frameShape.toUpperCase().trim();
			String frameShapeRegex = "(WRAP(\\-|\\s)?AROUND)|(CLUBMASTER)|(CAT(\\-|\\s)?EYE)|(AVIATOR)|(OVAL)|(OVER(\\-|\\s)?SIZED)|(RECTANGLE)|RECTANGULAR|(ROUND)|(SPORT)|(WAYFARER)|(SQUARE)";

			Pattern p = Pattern.compile(frameShapeRegex);
			Matcher m = p.matcher(temp);
			if (m.find()) {
				frameShape = m.group(0);
			}
			if (StringUtils.isNotBlank(frameShape)) {
				frameShape = frameShape.replaceAll("([-])|(\\s)", "");
			}
			if ("RECTANGULAR".equalsIgnoreCase(frameShape)) {
				frameShape = "RECTANGLE";
			}
		}

		String model = null;
		model = flipkartUpdateFeatures(map, "General", "Style Code");
		if (StringUtils.isNotBlank(model)) {
			model = model.toUpperCase().trim();
		}

		String idealfor = null;
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

		String color = flipkartUpdateFeatures(map, "Features & Functions", "Frame Color");
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase().trim();
		}
		else {
			color = flipkartUpdateFeatures(map, "Goggle Features", "Frame Color");
			if (StringUtils.isNotBlank(color)) {
				color = color.toUpperCase().trim();
			}
		}

		// System.out.println(header+" --F--> "+title+" | "+productBrand+" | "+model+" | "+frameShape+" | "+color+" | "+idealfor);
		// System.out.println(header+" | "+title+" | "+productBrand);
		ReviewedProductInfoDTO info = populateDto(productBrand, model, idealfor, frameShape, color, prod);

		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String model, String idealfor, String frameshape, String color, HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
		}
		productBrand = productBrand.replace("-", "");
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, null, null, model, color, new HashMap<String, String>(), prod, null);

		info.getProperties().put("IDEAL FOR", idealfor == null ? "null" : idealfor);
		info.getProperties().put("FRAME SHAPE", frameshape == null ? "null" : frameshape);

		return info;
	}
}
