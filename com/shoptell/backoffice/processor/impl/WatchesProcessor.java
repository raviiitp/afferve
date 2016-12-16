/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Vinay Kumar Chaudhary 	- vkc932@gmail.com
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
public class WatchesProcessor extends DataProcessor {

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
		String title = prod.getTitle().toUpperCase();
		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
			title = title.replaceAll(productBrand + " ", "");
		}

		// IdealFor Finding BLOCK--START--
		String temp = title;
		String idealfor = null;
		if (StringUtils.isNotBlank(temp)) {
			boolean kid = temp.contains("KID");
			boolean unisex = temp.contains("UNISEX");
			boolean woman = temp.contains("GIRL") || temp.contains("WOMAN") || temp.contains("WOMEN");
			temp = temp.replaceAll("(WOMEN)|(WOMAN)", "");
			boolean man = temp.contains("BOY") || temp.contains("MAN") || temp.contains("MEN");
			boolean couple = temp.contains("COUPLE");

			if (kid) {
				idealfor = "KIDS";
			}
			else if ((man && woman) || unisex || couple) {
				idealfor = "UNISEX";
			}
			else if (man) {
				idealfor = "MEN";
			}
			else {
				idealfor = "WOMEN";
			}
		}
		if (StringUtils.isBlank(idealfor)) {
			temp = prod.getType();
			if (StringUtils.isNotBlank(temp)) {
				boolean kid = temp.contains("KID");
				boolean unisex = temp.contains("UNISEX");
				boolean woman = temp.contains("GIRL") || temp.contains("WOMAN") || temp.contains("WOMEN");
				temp = temp.replaceAll("(WOMEN)|(WOMAN)", "");
				boolean man = temp.contains("BOY") || temp.contains("MAN") || temp.contains("MEN");
				boolean couple = temp.contains("COUPLE");
				if (kid) {
					idealfor = "KIDS";
				}
				else if ((man && woman) || unisex || couple) {
					idealfor = "UNISEX";
				}
				else if (man) {
					idealfor = "MEN";
				}
				else {
					idealfor = "WOMEN";
				}
			}
		}
		// IdealFor Finding BLOCK--END--

		title = title.replaceAll("(MEN'S)|(MENS)|(MEN)|(GIRLS?)|(MAN)|(WOMEN'S)|(WOMENS)|(WOMEN)|(WOMAN)|(KID'S)|(KIDS)|(COUPLE)|(WATCH )", "");

		// Type Finding BLOCK--START--
		String type = null;
		if (StringUtils.isNotBlank(title)) {
			boolean digital = title.contains("DIGITAL") || title.contains("LED");
			boolean analog = title.contains("ANALOG");
			if (analog && digital) {
				type = "ANALOG-DIGITAL";
			}
			else if (digital) {
				type = "DIGITAL";
			}
			else if (analog) {
				type = "ANALOG";
			}

		}
		// Type Finding BLOCK--END--

		title = title.replaceAll("(ANALOG-DIGITAL)|(ANALOG)|(DIGITAL)", "");

		// Color Finding BLOCK--START--
		String color = null;
		color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase().trim();
			title = title.replaceAll(color, "");
		}
		// Color Finding BLOCK--START--

		// Model Finding BLOCK--START--
		Pattern p = Pattern.compile("(?:[0-9]+[A-Z./_-]|[A-Z]+[0-9]|[A-Z]+([_./-])|[0-9][0-9][0-9]+)[A-Z0-9/_.-]*");
		String model = null;
		Matcher m = p.matcher(title);
		if (m.find()) {
			model = m.group(0);
		}
		// Model Finding BLOCK--END--

		String strapMaterial = null;
		String dialshape = null;
		// Extracting above Properties using ---prod.description--START
		String description = prod.getDescription();
		if (StringUtils.isNotBlank(description)) {
			description = description.toUpperCase().trim();

			// type- START-
			if (StringUtils.isBlank(type)) {
				boolean digital = description.contains("DIGITAL") || description.contains("LED");
				boolean analog = description.contains("ANALOG");
				if (analog && digital) {
					type = "ANALOG-DIGITAL";
				}
				else if (digital) {
					type = "DIGITAL";
				}
				else if (analog) {
					type = "ANALOG";
				}
			}
			// type-END

			// Strap-Material--START

			String strapmaterialregex = "STRAP\\s?MATERIAL\\s?:\\s?(([A-Z]+)(\\sSTEEL)?)";
			p = Pattern.compile(strapmaterialregex);
			m = p.matcher(description);
			if (m.find()) {
				strapMaterial = m.group(1);
				if ("OTHER".equalsIgnoreCase(strapMaterial)) {
					strapMaterial = null;
				}
			}
			if (StringUtils.isBlank(strapMaterial)) {
				String strapmaterialregex1 = "STRAP\\s?TYPE\\s?:\\s?(([A-Z]+)(\\sSTEEL)?)";
				p = Pattern.compile(strapmaterialregex1);
				m = p.matcher(description);
				if (m.find()) {
					strapMaterial = m.group(1);
					if ("OTHER".equalsIgnoreCase(strapMaterial)) {
						strapMaterial = null;
					}
				}
			}
			// Strap-Material--END

			// Dial-Shape---START
			String dialshaperegex = "DIAL\\s?SHAPE\\s?:\\s?([A-Z]+)";
			p = Pattern.compile(dialshaperegex);
			m = p.matcher(description);
			if (m.find()) {
				dialshape = m.group(1);
			}
			// Dial-Shape---END

			// Model--START
			String modelregex = "(PRODUC\\?CODE\\s?:(\\s)?)+([A-Z0-9_'-]+)";
			p = Pattern.compile(modelregex);
			m = p.matcher(description);
			if (m.find()) {
				model = m.group(3);
			}
			// Model-END

			// color-start
			if (StringUtils.isBlank(color)) {
				String colorregex = "(STRAP\\s?COLOR\\s?:(\\s)?)+([A-Z0-9_'-]+)";
				p = Pattern.compile(colorregex);
				m = p.matcher(description);
				if (m.find()) {
					color = m.group(3);
				}
			}
			// color-end

		}

		// Extracting above Properties using ---prod.description--END

		// System.out.println(header + " --S--> " + model + " | " +
		// productBrand+ " | " + idealfor + " | " + type + " | " + color +
		// " | "+ strapMaterial+" | "+dialshape);
		ReviewedProductInfoDTO info = populateDto(productBrand, model, idealfor, type, strapMaterial, dialshape, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle().toUpperCase().trim();
		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
		}
		title = title.replaceAll(productBrand + " ", "");

		String idealfor = null;
		String temp = title;
		if (StringUtils.isNotBlank(temp)) {
			boolean kid = temp.contains("KID");
			boolean unisex = temp.contains("UNISEX");
			boolean woman = temp.contains("GIRLS") || temp.contains("WOMAN") || temp.contains("WOMEN");
			temp = temp.replaceAll("(WOMEN)|(WOMAN)", "");
			boolean man = temp.contains("BOY") || temp.contains("MAN") || temp.contains("MEN");
			boolean couple = temp.contains("COUPLE");

			if (kid) {
				idealfor = "KIDS";
			}
			else if ((man && woman) || unisex || couple) {
				idealfor = "UNISEX";
			}
			else if (man) {
				idealfor = "MEN";
			}
			else {
				idealfor = "WOMEN";
			}
		}
		if (StringUtils.isBlank(idealfor)) {
			idealfor = prod.getType().toUpperCase().trim();
		}
		title = title.replaceAll("(MEN'S)|(MENS?)|(MANS?)|(WOMEN'S)|(GIRLS?)|(WOMENS?)|(WOMANS?)|(KID'S)|(KIDS?)|(COUPLES?)|(WATCH )", "");

		String type = null;
		if (StringUtils.isNotBlank(title)) {
			boolean digital = title.contains("DIGITAL");
			boolean analog = title.contains("ANALOG");
			if (analog && digital) {
				type = "ANALOG-DIGITAL";
			}
			else if (digital) {
				type = "DIGITAL";
			}
			else if (analog) {
				type = "ANALOG";
			}
		}

		title = title.replaceAll("(ANALOG-DIGITAL)|(ANALOG)|(DIGITAL)", "");
		String model = prod.getModel();
		if (StringUtils.isBlank(model)) {
			Pattern p = Pattern.compile("(?:[0-9]+[A-Z./_-]|[A-Z]+[0-9]|[A-Z]+([_./-])|[0-9][0-9][0-9]+)[A-Z0-9/_.-]*");
			Matcher m = p.matcher(title);
			if (m.find()) {
				model = m.group(0);
			}
		}
		
		if (StringUtils.isNotBlank(model)){
			model = model.toUpperCase();
		}

		String color = retriveColor(prod);

		String strapMaterial = null;
		// Strap Material FINDING--START
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
			if (color.contains("LEATHER")) {
				strapMaterial = "LEATHER";
				color = color.replaceAll("LEATHER", "");
			}
		}
		if (StringUtils.isBlank(strapMaterial)) {
			if (title.contains("LEATHER")) {
				strapMaterial = "LEATHER";
			}
			else if (title.contains("STEEL")) {
				strapMaterial = "STAINLESS STELL";
			}
		}
		// Strap Material FINDING--END

		// DIal Shape--finding-start
		String dialShape = null;
		// dial SHape=finding--end

		// System.out.println(header+" --A--> "+productBrand+" | "+model+" | "+type+" | "+idealfor+" | "+color);

		ReviewedProductInfoDTO info = populateDto(productBrand, model, idealfor, type, strapMaterial, dialShape, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle().toUpperCase();
		String productBrand = prod.getProductBrand();
		if (StringUtils.isNotBlank(productBrand)) {
			productBrand = productBrand.toUpperCase().trim();
		}
		title = title.replaceAll(productBrand + " ", "");

		// Type Finding BLOCK--START--
		String type = null;
		boolean digital = title.contains("DIGITAL") || title.contains("LED");
		boolean analog = title.contains("ANALOG");
		if (analog && digital) {
			type = "ANALOG-DIGITAL";
		}
		else if (digital) {
			type = "DIGITAL";
		}
		else if (analog) {
			type = "ANALOG";
		}
		// Type Finding BLOCK--END--

		title = title.replaceAll("(ANALOG-DIGITAL)|(ANALOG)|(DIGITAL)", "");

		// IdealFor Finding BLOCK--START--
		String idealfor = null;
		String temp = title;

		boolean kid = temp.contains("KID");
		boolean woman = temp.contains("GIRL") || temp.contains("WOMAN") || temp.contains("WOMEN");
		temp = temp.replaceAll("(WOMEN)|(WOMAN)", "");
		boolean man = temp.contains("BOY") || temp.contains("MAN") || temp.contains("MEN");
		boolean couple = temp.contains("COUPLE");
		if (kid) {
			idealfor = "KIDS";
		}
		else if ((man && woman) || couple) {
			idealfor = "UNISEX";
		}
		else if (man) {
			idealfor = "MEN";
		}
		else {
			idealfor = "WOMEN";
		}

		// IdealFor Finding BLOCK--END--
		int inx = title.indexOf("FOR");
		if (inx > 0) {
			temp = title.substring(inx);
			title = title.replace(temp, "");
		}

		// Model Finding BLOCK--START--
		String model = null;
		Pattern p = Pattern.compile("(?:[0-9]+[A-Z./_-]|[A-Z]+[0-9]|[A-Z]+([_./-])|[0-9][0-9][0-9]+)[A-Z0-9/_.-]*");
		Matcher m = p.matcher(title);
		if (m.find()) {
			model = m.group(0);
		}
		// Model Finding BLOCK--END--

		// Color Finding BLOCK--START--
		String color = null;

		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());

		color = retriveColor(prod);

		if (StringUtils.isBlank(color)) {
			color = flipkartUpdateFeatures(map, "General", "color");
			if (StringUtils.isBlank(color)) {
				color = flipkartUpdateFeatures(map, "General", "Color");
			}
			if (StringUtils.isBlank(color)) {
				color = flipkartUpdateFeatures(map, "Body Features", "Strap Color");
			}
		}
		else {
			color = color.replaceAll("LEATHER ", "");
		}
		// Color Finding BLOCK--END--

		// Strap Material Finding BLOCK--START--
		String strapMaterial = flipkartUpdateFeatures(map, "Body Features", "Strap Material");
		if (StringUtils.isNotBlank(strapMaterial)) {

			strapMaterial = strapMaterial.toUpperCase().replaceAll("STRAP", "").trim();
		}
		if (StringUtils.isBlank(strapMaterial)) {
			if (StringUtils.isNotBlank(title)) {
				if (title.contains("RUBBER")) {
					strapMaterial = "RUBBER";
				}
				else if (title.contains("LEATHER")) {
					strapMaterial = "LEATHER";
				}
				else if (title.contains("STEEL")) {
					strapMaterial = "STAINLESS STEEL";
				}
				else if (title.contains("SILICON")) {
					strapMaterial = "SILICON";
				}
			}

		}
		// Strap Material Finding BLOCK--END--

		// DialShape Finding BLOCK--START--
		String dialShape = flipkartUpdateFeatures(map, "Body Features", "Dial Shape");
		if (StringUtils.isNotBlank(dialShape)) {
			dialShape = dialShape.toUpperCase().trim();
		}
		// DialShape Finding BLOCK--END--

		// System.out.println(header+" --F--> "+model+" | "+productBrand+" | "+idealfor+" | "+type+" | "+color+" | "+strapMaterial+" | "+dialShape);

		ReviewedProductInfoDTO info = populateDto(productBrand, model, idealfor, type, strapMaterial, dialShape, color, prod);
		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String model, String idealfor, String type, String strapmaterial, String dialshape,
			String color, HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase().trim();
		}
		else {
			color = null;
		}
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, null, null, model, color, new HashMap<String, String>(), prod, null);

		info.getProperties().put("IDEAL FOR", idealfor == null ? "null" : idealfor);
		info.getProperties().put("TYPE", type == null ? "null" : type);
		info.getProperties().put("STRAP MATERIAL", strapmaterial == null ? "null" : strapmaterial);
		info.getProperties().put("DIAL SHAPE", dialshape == null ? "null" : dialshape);

		return info;
		// return null;
	}

}
