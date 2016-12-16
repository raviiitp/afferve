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
public class HeadphoneProcessor extends DataProcessor {

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
		String title = prod.getTitle().toUpperCase();
		String productBrand = prod.getProductBrand().toUpperCase();

		String productType = null;
		String type = null;
		String mic = null;
		String wiredOrWireless = "WIRED";
		String color = null;
		String model = null;

		if (title.contains(productBrand)) {
			title = title.replace(productBrand, "");
		}
		if (title.contains("HEADPHONE") || title.contains("HEAD PHONE")) {
			title = title.replaceAll("HEAD(\\s)?PHONE(S)?", "");
			productType = "HEADPHONE";
		}
		if (title.contains("EARPHONE") || title.contains("EAR PHONE")) {
			title = title.replaceAll("EAR(\\s)?PHONE(S)?", "");
			productType = "EARPHONE";
		}
		if (title.contains("WIRED")) {
			title = title.replace("WIRED", "");
		}
		if (title.contains("WIRELESS")) {
			title = title.replace("WIRELESS", "");
			wiredOrWireless = "WIRELESS";
		}
		if (title.contains("BLUETOOTH")) {
			title = title.replace("BLUETOOTH", "");
			wiredOrWireless = "WIRELESS";
		}

		String custom_color_regex = "("
		/* 10 */+ "BLACK ONYX|BLACK TITAN|CHAMPANGNE|ONYX BLACK|"
		/* 9 */+ "ALABASTER|BALLISTIC|CHAMPAGNE|CHAMPANGE|CHOCOLATE|SANDSTONE|TURQUOISE|"
		/* 8 */+ "CHARCOAL|DAZZLING|GRAPHITE|GUNMETAL|MAGNETIC|METALLIC|MIDNIGHT|MILKYWAY|MOONDUST|SANTRONI|SHIMMERY|TITANIUM|"
		/* 7 */+ "CERAMIC|CHESTNUT|CLASSIC|ELEGANT|FROSTED|LEATHER|MAGENTA|"
		/* 6 */+ "ARCTIC|BRIGHT|BRONZE|BRUSHED|BUFFED|CARBON|CHROME|COFFEE|COPPER|FERVOR|GLOSSY|GOLDEN|MARBLE|ORANGE|PEBBLE|PURPLE|SILVER|YELLOW|"
		/* 5 */+ "BIRCH|BLACK|BLUSH|BROWN|CORAL|FROST|GREEN|METAL|MILKY|PEARL|ROAST|ROYAL|SLEEK|SLATE|SPACE|STARRY|STEEL|WHITE|BEIGE|"
		/* 4 */+ "BLUE|CHIC|CYAN|DARK|DEEP|GOLD|GRAY|GREY|INOX|MINT|MIST|PINK|PURE|ROSE|SNOW|WINE|"
		/* 3 */+ "GUN|ICE|ICY|JET|RED)(\\s|\\Z|\\W)";
		// AQUA|

		Pattern patternx = Pattern.compile(custom_color_regex, Pattern.CASE_INSENSITIVE);
		Matcher matchx = patternx.matcher(title);
		if (matchx.find()) {
			if (isMatchPresent(matchx, 1)) {
				String tmp = matchx.group(1);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
					tmp = tmp.trim();
				}
				color = tmp;
			}

		}
		if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(color)) {
			if (title.contains(color)) {
				title = title.replace(color, "");
			}
		}

		String c_regex = "((IN|ON|OVER)(\\-)?(\\s)?(THE)?(\\-)?(\\s)?(EAR))";
		Pattern pattern = Pattern.compile(c_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
					tmp = tmp.replace("THE", "");
					tmp = tmp.replace("-", " ");
					tmp = tmp.trim();
				}
				tmp = tmp.replaceAll("\\s+", " ");
				type = tmp;
			}

		}

		String regex = "(W(ITH)?(OUT)?(\\/)?(O)?(\\s)?(MIC))";
		Pattern pattern1 = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher match1 = pattern1.matcher(title);
		if (match1.find()) {
			if (isMatchPresent(match1, 0)) {
				String tmp = match1.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
				mic = tmp;
			}
		}
		if (StringUtils.isNotBlank(mic)) {
			if (mic.equals("W/MIC")) {
				mic = "WITH MIC";
			}
		}

		if (StringUtils.isNotBlank(title) && title.contains("HEADSET")) {
			title = title.replace("HEADSET", "");
			if (StringUtils.isBlank(type)) {
				type = "ON EAR";
			}
		}
		if (StringUtils.isNotBlank(type)) {
			if (type.contains("(s)")) {
				type = type.replace("(s)", " ");
			}
		}

		if (StringUtils.isNotBlank(title) && title.contains("+")) {
			int indx = title.lastIndexOf("+");
			if (indx > -1) {
				title = title.substring(0, indx - 1);
			}
		}
		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll("(AUDIO-TECHNICA)|((\\d+)(\\s)?MM)", "");
		}

		String regex1 = "((([0-9]+[A-Z]*(\\.)?)|([A-Z]+[0-9\\-\\/]+)+)(\\w)+)";
		Pattern pattern2 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
		Matcher match2 = pattern2.matcher(title);
		if (match2.find()) {
			if (isMatchPresent(match2, 0)) {
				String tmp = match2.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
				model = tmp;
			}
		}
		if (StringUtils.isNotBlank(color) && StringUtils.isNotBlank(title)) {
			if (color.equals("PEARL")) {

				if (title.contains("RED")) {
					color = "RED";
				}
				if (title.contains("BLACK")) {
					color = "BLACK";
				}
				if (title.contains("WHITE")) {
					color = "WHITE";
				}
				title = title.replaceAll("(RED)|(BLACK)|(WHITE)", "PEARL");
			}
		}

		if (StringUtils.isNotBlank(title)) {
			title = title
					.replaceAll(
							"(MULTIMEDIA)|(BEYERDYNAMIC)|(CREAM)|(JACK)|(FORAL)|(TEAL)|((OR)?(\\s)?(ORANGE))|(EA(R\\s)?PHONES)|(SOLID)|(P(I)?NK)|((\\s)AND)|(CYAN)|(RED)|(WHITE)|(SILVER)|(PACK(\\s)?OF(\\s)?(\\d))|(SKULCANDY)|(BLACK)|(BLUE)|(GREEN)|(GR(A|E)Y)|(PORTABLE)|(ISOLATING)|(ARMY)|(AQUA)|(ROPHONE)|(EAR(\\s)?BUDS)|(SERIES)|(POWERFUL)|(BASS)|(STEREO)|(SOUND)|(MONITORING)|(HANDS(FREE|ET))",
							"");
			title = title.replaceAll("[^\\p{L}\\p{Z}\\p{N}]", " ").replaceAll("\\s+", " ").trim();
		}

		if (StringUtils.isNotBlank(title)) {
			String s[] = title.split("(\\s)");
			if (s.length > 0) {
				if (productBrand.equals("BEATS")) {
					model = s[0];
				}
				if (StringUtils.isBlank(model) && StringUtils.isNotBlank(title) && !title.contains("WITH")) {
					if (s.length == 1)
						model = s[0];
					else if (s.length > 1)
						model = s[0] + " " + s[1];

				}
				else if (StringUtils.isNotBlank(model) && StringUtils.isNotBlank(title) && !title.contains("WITH")) {
					if (!model.contains("-") && !model.contains("/")) {
						if (s.length == 1)
							model = s[0] + " " + model;
					}
				}
			}
		}

		if (StringUtils.isNotBlank(model)) {
			if (model.contains("-")) {
				model = model.replace("-", "");
			}
			if (model.contains("/")) {
				model = model.replace("/", "");
			}
		}

		// System.out.println(productBrand+" | " + color + " | "+model+
		// " | "+wiredOrWireless+ " | "+type);
		ReviewedProductInfoDTO info = populateDto(productBrand, null, null, model, productType, type, mic, wiredOrWireless, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle().toUpperCase();
		String productBrand = prod.getProductBrand().toUpperCase();

		String productType = null;
		String type = null;
		String mic = null;
		String wiredOrWireless = "WIRED";
		String color = null;
		String model = null;

		if (title.contains(productBrand)) {
			title = title.replace(productBrand, "");
		}
		if (title.contains("HEADPHONE") || title.contains("HEAD PHONE")) {
			title = title.replaceAll("HEAD(\\s)?PHONE(S)?", "");
			productType = "HEADPHONE";
		}
		if (title.contains("EARPHONE") || title.contains("EAR PHONE")) {
			title = title.replaceAll("EAR(\\s)?PHONE(S)?", "");
			productType = "EARPHONE";
		}
		if (title.contains("WIRED")) {
			title = title.replace("WIRED", "");
		}
		if (title.contains("WIRELESS")) {
			title = title.replace("WIRELESS", "");
			wiredOrWireless = "WIRELESS";
		}
		if (title.contains("BLUETOOTH")) {
			title = title.replace("BLUETOOTH", "");
			wiredOrWireless = "WIRELESS";
		}

		String c_regex = "((IN|ON|OVER)(\\-)?(\\s)?(THE)?(\\-)?(\\s)?(EAR))";
		Pattern pattern = Pattern.compile(c_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
					tmp = tmp.replace("THE", "");
					tmp = tmp.replace("-", " ");
					tmp = tmp.trim();
				}
				tmp = tmp.replaceAll("\\s+", " ");
				type = tmp;
			}

		}

		if (StringUtils.isNotBlank(type)) {
			if (type.contains("(s)")) {
				type = type.replace("(s)", " ");
			}
		}

		color = prod.getColor();

		if (StringUtils.isBlank(color)) {
			color = retriveColor(prod);
		}

		if (StringUtils.isNotBlank(color)) {
			if (color.contains("AND")) {
				color = color.replace("AND", "");
			}
			if (color.contains("(")) {
				int indx = title.lastIndexOf("+");
				if (indx > -1) {
					title = title.substring(0, indx - 1);
				}
			}
		}

		String regex = "(W(ITH)?(OUT)?(\\/)?(O)?(\\s)?(MIC))";
		Pattern pattern1 = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher match1 = pattern1.matcher(title);
		if (match1.find()) {
			if (isMatchPresent(match1, 0)) {
				String tmp = match1.group(0);
				title = title.replace(tmp, "");
				mic = tmp;
			}
		}
		if (StringUtils.isNotBlank(mic)) {
			if (mic.equals("W/MIC")) {
				mic = "WITH MIC";
			}
		}

		model = prod.getModel();

		if (StringUtils.isBlank(model)) {
			String regex1 = "((([0-9]+[A-Z]*(\\.)?)|([A-Z]+[0-9\\-\\/]+)+)(\\w)+)";
			Pattern pattern2 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
			Matcher match2 = pattern2.matcher(title);
			if (match2.find()) {
				if (isMatchPresent(match2, 0)) {
					String tmp = match2.group(0);
					if (StringUtils.isNotBlank(tmp)) {
						title = title.replace(tmp, "");
					}
					model = tmp;
				}
			}
		}

		if (StringUtils.isNotBlank(model)) {
			model = model.toUpperCase();
			model = model.replaceAll("((EA(R)?|HEAD)(\\s)?PHONES?)", "");

			if (model.contains("-" + color) || model.contains("_" + color)) {
				if (model.contains("-" + color)) {
					model = model.replace("-" + color, "");
				}
				if (model.contains("_" + color)) {
					model = model.replace("_" + color, "");
				}
			}
			if (StringUtils.isNotBlank(color) && model.contains(color)) {
				model = model.replace(color, "");
			}
			if (model.contains("W/O MIC")) {
				mic = "WITHOUT MIC";
				model = model.replace("W/O MIC", "");
			}

			if (model.contains("+")) {
				model = model.replace("+", "");
			}

			model = model
					.replaceAll(
							"(IN(\\-)EAR)|(WITH(\\s)MIC)|((\\s)?(\\-)(\\s))|(GR(E|A)Y)|(\\s)AND|(DIGITAL)|(DOLBY)|(METALLIC(\\s)RED)|(JACK)|(SPLITTER)|(CABLE)|(STEREO)|((\\-)(GREEN|RED|LHT|BLK|BLU|MIC))|((\\s)(RD|BK|BLU|BLK|RGR))|((\\d+)(\\s)?OHM)",
							"");
			if (StringUtils.isNotBlank(model)) {
				model = model.trim();
			}
		}
		if (StringUtils.isNotBlank(model)) {
			if (model.contains("-")) {
				model = model.replace("-", "");
			}
			if (model.contains("/")) {
				model = model.replace("/", "");
			}
		}
		// System.out.println(productBrand+" | " + color+" | " + model);
		// System.out.println(productBrand+" | " + color + " | "+model + " | " +
		// productType+ " | " + type + " | " + wiredOrWireless + " | " + mic);
		ReviewedProductInfoDTO info = populateDto(productBrand, null, null, model, productType, type, mic, wiredOrWireless, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle().toUpperCase();
		String productBrand = prod.getProductBrand().toUpperCase();
		String productType = null;
		String type = null;
		String mic = null;
		String wiredOrWireless = "WIRED";
		String color = null;
		String model = null;
		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());

		if (title.contains(productBrand)) {
			title = title.replace(productBrand, "");
		}
		if (title.contains("HEADPHONE") || title.contains("HEAD PHONE")) {
			title = title.replaceAll("HEAD(\\s)?PHONE(S)?", "");
			productType = "HEADPHONE";
		}
		if (title.contains("EARPHONE") || title.contains("EAR PHONE")) {
			title = title.replaceAll("EAR(\\s)?PHONE(S)?", "");
			productType = "EARPHONE";
		}
		if (title.contains("WIRED")) {
			title = title.replace("WIRED", "");
		}
		if (title.contains("WIRELESS")) {
			title = title.replace("WIRELESS", "");
			wiredOrWireless = "WIRELESS";
		}
		if (title.contains("BLUETOOTH")) {
			title = title.replace("BLUETOOTH", "");
			wiredOrWireless = "WIRELESS";
		}

		if (StringUtils.isBlank(wiredOrWireless)) {
			wiredOrWireless = flipkartUpdateFeatures(map, "General", "Wired/Wireless");
			if (StringUtils.isNotBlank(wiredOrWireless)) {
				wiredOrWireless = wiredOrWireless.toUpperCase();
			}
		}

		if (StringUtils.isBlank(color)) {
			color = flipkartUpdateFeatures(map, "General", "Color");
		}

		if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(color)) {
			if (title.contains(color)) {
				title = title.replace(color, "");
			}
		}

		if (StringUtils.isNotBlank(title)) {
			String c_regex = "((IN|ON|OVER)(\\-)?(\\s)?(THE)?(\\-)?(\\s)?(EAR))";
			Pattern pattern = Pattern.compile(c_regex, Pattern.CASE_INSENSITIVE);
			Matcher match = pattern.matcher(title);
			if (match.find()) {
				if (isMatchPresent(match, 0)) {
					String tmp = match.group(0);
					if (StringUtils.isNotBlank(tmp)) {
						title = title.replace(tmp, "");
						tmp = tmp.replace("THE", "");
						tmp = tmp.replace("-", " ");
						tmp = tmp.trim();
					}
					tmp = tmp.replaceAll("\\s+", " ");
					type = tmp;
				}

			}

			if (StringUtils.isBlank(type)) {
				type = flipkartUpdateFeatures(map, "General", "Headphone Type");
				if (StringUtils.isNotBlank(type)) {
					type = type.toUpperCase();
					if (type.contains("ON")) {
						type = "ON EAR";
					}
					if (type.contains("OVER")) {
						type = "OVER EAR";
					}
					if (type.contains("IN")) {
						type = "IN EAR";
					}
					if (type.contains("THE")) {
						type = type.replace("THE", "");
						type = type.replaceAll("\\s+", " ");
					}
				}
			}

			if (StringUtils.isNotBlank(type)) {
				if (type.contains("(s)")) {
					type = type.replace("(s)", " ");
				}
			}

			String regex = "(W(ITH)?(OUT)?(\\/)?(O)?(\\s)?(MIC))";
			Pattern pattern1 = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher match1 = pattern1.matcher(title);
			if (match1.find()) {
				if (isMatchPresent(match1, 0)) {
					String tmp = match1.group(0);
					title = title.replace(tmp, "");
					mic = tmp;
				}
			}
			if (title.contains("HEADSET")) {
				title = title.replace("HEADSET", "");
				if (StringUtils.isBlank(type)) {
					type = "ON EAR";
				}
			}

			if (title.contains("+")) {
				int indx = title.lastIndexOf("+");
				if (indx > -1) {
					title = title.substring(0, indx - 1);
				}
			}

		}

		String regex1 = "((([0-9]+[A-Z]*(\\.)?)|([A-Z]+[0-9\\-\\/]+)+)(\\w)+)";
		Pattern pattern2 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
		Matcher match2 = pattern2.matcher(title);
		if (match2.find()) {
			if (isMatchPresent(match2, 0)) {
				String tmp = match2.group(0);
				title = title.replace(tmp, "");
				model = tmp;
			}
		}

		title = title
				.replaceAll(
						"(COLLECTION)|(MUSIC)|(MULTIMEDIA)|(BEOPLAY)|((OR)?(\\s)?(ORANGE))|(EA(R\\s)?PHONES)|(SOLID)|(P(I)?NK)|((\\s)AND)|(NEW)|(WATER)|(RESISTA(N)?T)|(PACK(\\s)?OF(\\s)?(\\d))|(SKULCANDY)|(PORTABLE)|(ISOLATING)|(ARMY)|(AQUA)|(ROPHONE)|(EAR(\\s)?BUDS)|(SERIES)|(POWERFUL)|(BASS)|(STEREO)|(SOUND)|(MONITORING)|(HANDS(FREE|ET))",
						"");
		title = title.trim();

		String s[] = title.split("\\s");
		if (s.length > 0 && StringUtils.isBlank(model) && StringUtils.isNotBlank(title) && !title.contains("WITH")) {
			if (s.length == 1)
				model = s[0];
			else if (s.length > 1)
				model = s[0] + " " + s[1];

		}
		else if (StringUtils.isNotBlank(model) && StringUtils.isNotBlank(title) && !title.contains("WITH")) {
			if (!model.contains("-") && !model.contains("/")) {
				if (s.length == 1)
					model = s[0] + " " + model;
			}
		}

		if (StringUtils.isNotBlank(model)) {
			if (model.contains("-")) {
				model = model.replace("-", "");
			}
			if (model.contains("/")) {
				model = model.replace("/", "");
			}
		}
		// System.out.println(productBrand+" | "+color+" | "+model);
		// System.out.println(title);
		System.out.println(productBrand + " | " + color + " | " + type + " | " + productType);
		ReviewedProductInfoDTO info = populateDto(productBrand, null, null, model, productType, type, mic, wiredOrWireless, color, prod);
		return info;

	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String productSubBrand, String productSeries, String model, String productType,
			String type, String mic, String wiredOrWireless, String color, HomeProductInfoDTO prod) {
		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
		}

		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, productSubBrand, productSeries, model, color, new HashMap<String, String>(),
				prod, null);

		info.getProperties().put("PRODUCT TYPE", productType == null ? "null" : productType);
		info.getProperties().put("TYPE", type == null ? "null" : type);
		info.getProperties().put("WIRED/WIRELESS", wiredOrWireless == null ? "null" : wiredOrWireless);
		info.getProperties().put("MIC", mic == null ? "null" : mic);

		return info;
	}
}
