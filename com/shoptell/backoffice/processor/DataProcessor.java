/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.processor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

public abstract class DataProcessor {
	
	public static final String COLOR_REGEX = "(\\s|\\(|-)(("
			/* 10 */+ "BLACK ONYX|BLACK TITAN|CHAMPANGNE|ONYX BLACK|"
			/* 9 */+ "ALABASTER|BALLISTIC|CHAMPAGNE|CHAMPANGE|CHOCOLATE|SANDSTONE|"
			/* 8 */+ "CHARCOAL|DAZZLING|GRAPHITE|GUNMETAL|MAGNETIC|METALLIC|MIDNIGHT|MILKYWAY|MOONDUST|SANTRONI|SHIMMERY|TITANIUM|"
			/* 7 */+ "CERAMIC|CHESTNUT|CLASSIC|ELEGANT|FROSTED|LEATHER|MAGENTA|"
			/* 6 */+ "ARCTIC|BRIGHT|BRONZE|BRUSHED|BUFFED|CARBON|CHROME|COFFEE|COPPER|FERVOR|GLOSSY|GOLDEN|MARBLE|MAROON|ORANGE|PEBBLE|PURPLE|SILVER|YELLOW|"
			/* 5 */+ "BIRCH|BLACK|BLUSH|BROWN|CORAL|FROST|GREEN|METAL|MILKY|PEARL|ROAST|ROYAL|SLEEK|SLATE|SPACE|STARRY|STEEL|WHITE|"
			/* 4 */+ "BLUE|CHIC|CYAN|DARK|DEEP|GOLD|GRAY|GREY|INOX|MINT|MIST|PINK|PURE|ROSE|SNOW|WINE|"
			/* 3 */+ "GUN|ICE|ICY|JET|RED)\\s?(,|;|\\/|-|\\+|&|AND|_|!)?){1,3}";
			// AQUA|

	public ReviewedProductInfoDTO process(HomeProductInfoDTO prod, HomeEnum home) {
		if (StringUtils.isBlank(prod.getTitle()) || StringUtils.isBlank(prod.getProductBrand())){
			return null;
		}
		switch (home) {
		case FLIPKART:
			return flipkartProcess(prod);
		case AMAZON:
			return amazonProcess(prod);
		case SNAPDEAL:
			return snapdealProcess(prod);
		case EBAY:
			return ebayProcess(prod);
		case SHOPCLUES:
			return shopcluesProcess(prod);
		default:
			break;
		}
		return null;
	}

	public abstract ReviewedProductInfoDTO shopcluesProcess(HomeProductInfoDTO prod);

	public abstract ReviewedProductInfoDTO ebayProcess(HomeProductInfoDTO prod);

	public abstract ReviewedProductInfoDTO snapdealProcess(HomeProductInfoDTO prod);

	public abstract ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod);

	public abstract ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod);
	
	public static String removeDuplicates(String title) {
		return new LinkedHashSet<String>(Arrays.asList(title.split(" "))).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ");
	}
	
	public boolean isMatchPresent(Matcher matcher, int i) {
		int count = matcher.groupCount();
		if (i <= count) {
			String text = matcher.group(i);
			if (StringUtils.isNotBlank(text)) {
				return true;
			}
		}
		return false;
	}
	
	public String retriveColor(HomeProductInfoDTO prod, String title) {
		prod.setTitle(title);
		return retriveColor(prod);
	}
	
	public String retriveColor(HomeProductInfoDTO prod) {
		String title = prod.getTitle();
		String color = null;
		if (StringUtils.isNotBlank(title)) {
			Pattern pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(title);
			while (matcher.find()) {
				if (isMatchPresent(matcher, 0)) {
					color = matcher.group(0);
					int index = title.indexOf(matcher.group(0));
					if (index != -1) {
						title = title.substring(0, index).trim();
					}
					break;
				}
			}
			if (StringUtils.isNotBlank(color)) {
				color = color.trim().replace("(", "").replace("-", "");
			}
			prod.setTitle(title);
		}
		return color;
	}
	
	public String retrieveSize(HomeProductInfoDTO prod) {
		int size = 0;
		boolean isMb = false;
		String ext = "GB";
		List<String> allMatches = new LinkedList<String>();
		Pattern pattern = Pattern.compile("\\d+([GM]B)");

		String title = prod.getTitle();

		Matcher matcher = pattern.matcher(title);
		while (matcher.find()) {
			allMatches.add(matcher.group());
		}

		for (String match : allMatches) {
			title = title.replace(match, "");
			String tmp = match.substring(0, match.length() - 2);

			if (match.endsWith("MB")) {
				isMb = true;
			}
			else {
				isMb = false;
			}

			if (StringUtils.isNumeric(tmp)) {
				int num = Integer.parseInt(tmp);
				if (!isMb) {
					num *= 1024;
				}
				if (num > size) {
					size = num;
					if (isMb && size < 1024) {
						ext = "MB";
					}
					else {
						ext = "GB";
					}
				}
			}
		}
		title = title.trim().replaceAll("\\s+", " ");
		prod.setTitle(title);
		if (size == 0) {
			return null;
		}
		if ("GB".equalsIgnoreCase(ext)) {
			size = size / 1024;
		}
		return String.valueOf(size) + ext;
	}
}
