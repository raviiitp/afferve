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
public class SpeakerProcessor extends DataProcessor {

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

		String type = null;
		String connectivity = null;
		String color = null;
		String model = "";

		if (title.contains(productBrand)) {
			title = title.replace(productBrand, "");
		}
		if (title.contains("WIRED")) {
			title = title.replace("WIRED", "");
			connectivity = "WIRED";
		}
		if (title.contains("WIRELESS")) {
			title = title.replace("WIRELESS", "");
			connectivity = "BLUETOOTH";
		}
		if (title.contains("BLUETOOTH")) {
			title = title.replace("BLUETOOTH", "");
			connectivity = "BLUETOOTH";
		}
		if (title.matches("(\\s+)\\-(\\s+)")) {
			title = title.replace(" - ", "-");
		}
		title = title.replaceAll("BLUETOOTH", "");

		String c_regex = "((PORTABLE)|(TOWER|FLOORSTANDING)|(DOCK(ING)?)|(BOOKSHELF)|(CEILING|WALL(\\s)?MOUNT)|(SUBWOOFER(S)?)|(\\d+(\\.\\d+)?\\s?(COMPUTER|DESKTOP|MINI))(\\s)?SPEAKER(S)?)";
		Pattern pattern = Pattern.compile(c_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
					tmp = tmp.trim();
				}
				type = tmp;
				if (isMatchPresent(match, 4)) {
					type = "DOCKING";
				}
				if (tmp.equals("FLOORSTANDING")) {
					type = "TOWER";
				}
				if (tmp.equals("WALL MOUNT")) {
					type = "CEILING";
				}
				if (tmp.equals("SUBWOOFERS")) {
					type = "SUBWOOFER";
				}
				if (tmp.contains("DESKTOP")) {
					type = type.replace("DESKTOP", "COMPUTER");
				}
				if (tmp.matches("(\\d)") && (!tmp.contains("COMPUTER") || (!tmp.contains("MINI")))) {
					type = type + " COMPUTER SPEAKERS";
				}
				if (StringUtils.isNotBlank(type)) {
					if (type.contains("COMPUTER")) {
						if (type.contains("2 ")) {
							type = type.replace("2", "2.0");
						}
						if (type.startsWith("1 ")) {
							type = type.replace("1", "1.0");
						}
					}
				}
				if (type.contains("SPEAKER")) {
					type = type.replaceAll("(\\s)SPEAKER(S)?", "");
				}
				if (type.contains("COMPUTER") || type.contains("MINI")) {
					type = type.replaceAll("(COMPUTER|MINI)", "");
				}
				if (!type.contains("SPEAKERS") && !type.equals("SUBWOOFER")) {
					type = type + " SPEAKERS";
				}
				if (StringUtils.isNotBlank(type)) {
					if (type.contains("1.0")) {
						type = "PORTABLE SPEAKERS";
					}
				}
			}
		}
		if (StringUtils.isNotBlank(type)) {
			type = type.replaceAll("\\s+", " ").trim();
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

		if (StringUtils.isNotBlank(title)) {
			if (StringUtils.isNotBlank(color) && title.contains(color)) {
				title = title.replace(color, "");
			}
			title = title
					.replaceAll(
							"(BEIGE|OTHER|TABLET|MOBILE|DESKTOP|QUALITY|GOOD|SPEAKER(S)?|MULTIMEDIA|PLAYER|AUDIO|MACHINE|USB|ACTIVE|SOUND|(\\s)AND(\\s)?|MIC|COMPUTER|PREMIUM|FUNCTION|FM|RADIO|\\-(\\s+)MULTI)",
							"");
			if (title.contains("WITH") && !productBrand.equals("ZEBRONICS")) {
				int indx = title.lastIndexOf("WITH");
				if (indx > -1) {
					title = title.substring(0, indx - 1);

				}
			}
			if (title.contains("FOR")) {
				int indx = title.lastIndexOf("FOR");
				if (indx > -1) {
					title = title.substring(0, indx - 1);

				}
			}
			if (title.endsWith("-")) {
				int indx = title.lastIndexOf("-");
				if (indx > 0) {
					title = title.substring(0, indx - 1);

				}
			}
			if (title.startsWith("-")) {
				int index = title.indexOf("-");
				title = title.substring(0, index) + title.substring(index + 1);
			}
		}
		title = title.replaceAll("[^\\p{L}\\p{Z}\\p{N}\\-\\/]", " ").replaceAll("\\s+", " ").trim();

		String regex1 = "(((([0-9]+[A-Z]*)|([A-Z]+[0-9\\-\\/]+)+)(\\w)+)+)";
		Pattern pattern2 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
		Matcher match2 = pattern2.matcher(title);
		while (match2.find()) {
			if (isMatchPresent(match2, 0)) {
				String tmp = match2.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
				tmp = tmp.replaceAll("\\s+", " ");
				model = tmp;
			}
		}
		if (StringUtils.isBlank(model)) {

			String[] k = title.split("(\\s)");
			if (k.length == 1) {
				model = title;
			}
			else if (k.length > 1) {
				model = k[0] + " " + k[1];
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
		// System.out.println(title+" | "+model);
		// System.out.println(productBrand
		// +" | "+color+" | "+connectivity+" | "+type+" | "+model);
		ReviewedProductInfoDTO info = populateDto(productBrand, null, null, model, type, connectivity, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle().toUpperCase();

		if (title.contains("MOUSE")) {
			return null;
		}

		String productBrand = prod.getProductBrand().toUpperCase();

		String type = null;
		String connectivity = "WIRED";
		String color = null;
		String model = "";

		if (StringUtils.isNotBlank(title)) {
			if (title.contains(productBrand)) {
				title = title.replace(productBrand, "");
			}
			if (title.contains("WIRED")) {
				title = title.replace("WIRED", "");
				connectivity = "WIRED";
			}
			if (title.contains("WIRELESS")) {
				title = title.replace("WIRELESS", "");
				connectivity = "BLUETOOTH";
			}
			if (title.contains("BLUETOOTH")) {
				title = title.replace("BLUETOOTH", "");
				connectivity = "BLUETOOTH";
			}
			if (title.matches("(\\s+)\\-(\\s+)")) {
				title = title.replace(" - ", "-");
			}
			title = title.replace("BLUETOOTH", "");
			title = title.replace("SPEAKERS", "");
		}

		String c_regex = "((PORTABLE)|(TOWER)|(DOCK(ING)?)|(BOOKSHELF)|(CEILING|IN(\\-)WALL)|SHOWER|((\\d+\\.\\d+)\\s?))";
		Pattern pattern = Pattern.compile(c_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "").trim();
				}
				type = tmp;
				if (isMatchPresent(match, 4)) {
					type = "DOCKING";
				}
				if (tmp.equals("IN-WALL")) {
					type = "CEILING";
				}
				if (tmp.equals("SHOWER")) {
					type = "PORTABLE";
				}
				if (tmp.contains("DESKTOP")) {
					type = type.replace("DESKTOP", "COMPUTER");
				}
				if (StringUtils.isNotBlank(type)) {
					if (type.contains("COMPUTER")) {
						if (type.contains("2 ")) {
							type = type.replace("2", "2.0");
						}
						if (type.startsWith("1 ")) {
							type = type.replace("1", "1.0");
						}
					}
				}
				if (type.contains("SPEAKER ")) {
					type = type.replace("SPEAKER", "");
				}
				if (!type.contains("SPEAKERS")) {
					type = type + " SPEAKERS";
				}
				if (type.contains("3.5")) {
					type = "";
				}
				if (StringUtils.isNotBlank(type)) {
					if (type.contains("1.0")) {
						type = "PORTABLE SPEAKERS";
					}
				}
			}
			if (type != null) {
				type = type.trim();
			}
		}

		prod.setTitle(title);
		color = retriveColor(prod);
		title = prod.getTitle();
		if (StringUtils.isBlank(color)) {
			color = prod.getColor();
		}
		if (StringUtils.isNotBlank(color) && title.contains(color)) {
			title = title.replace(color, "");
		}
		model = prod.getModel();

		if (StringUtils.isBlank(model)) {
			String regex1 = "((([0-9]+[A-Z]*)|([A-Z]+[0-9\\-\\/]+)+)(\\w)+)";
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
			if (StringUtils.isNotBlank(color) && model.contains(color)) {
				model = model.replace(color, "");
			}

			if (model.contains(productBrand)) {
				model = model.replace(productBrand, "");
			}
			model = model.replaceAll("(SPEAKER|USB)", "");
		}

		if (StringUtils.isNotBlank(type)) {
			if (type.contains("14.0") || type.contains("15")) {
				type = null;
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

		// System.out.println(productBrand
		// +" | "+color+" | "+connectivity+" | "+type+" | "+model);
		ReviewedProductInfoDTO info = populateDto(productBrand, null, null, model, type, connectivity, color, prod);
		return info;
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		String title = prod.getTitle().toUpperCase();
		String productBrand = prod.getProductBrand().toUpperCase();

		String type = null;
		String connectivity = "WIRED";
		String color = null;
		String model = "";
		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());

		if (title.contains(productBrand)) {
			title = title.replace(productBrand, "");
		}
		if (title.contains("WIRED")) {
			title = title.replace("WIRED", "");
			connectivity = "WIRED";
		}
		if (title.contains("WIRELESS")) {
			title = title.replace("WIRELESS", "");
			connectivity = "BLUETOOTH";
		}
		if (title.contains("BLUETOOTH")) {
			title = title.replace("BLUETOOTH", "");
			connectivity = "BLUETOOTH";
		}
		if (title.matches("(\\s+)\\-(\\s+)")) {
			title = title.replace(" - ", "-");
		}
		title = title.replace("BLUETOOTH", "");
		title = title.replace("SPEAKERS", "");

		String c_regex = "((PORTABLE)|(TOWER)|(DOCK(ING)?)|(BOOKSHELF)|(CEILING|IN(\\-)WALL)|SHOWER|((\\d+\\.\\d+)\\s?))";
		Pattern pattern = Pattern.compile(c_regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(title);
		if (match.find()) {
			if (isMatchPresent(match, 0)) {
				String tmp = match.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
					tmp = tmp.trim();
				}
				type = tmp;
				if (isMatchPresent(match, 4)) {
					type = "DOCKING";
				}
				if (tmp.equals("IN-WALL")) {
					type = "CEILING";
				}
				if (tmp.equals("SHOWER")) {
					type = "PORTABLE";
				}

				if (!type.contains("SPEAKERS")) {
					type = type + " SPEAKERS";
				}
			}
			if (type != null) {
				type = type.trim();
			}
		}

		if (StringUtils.isBlank(type)) {
			type = flipkartUpdateFeatures(map, "General", "Configuration");
		}
		if (StringUtils.isNotBlank(type)) {
			if (type.contains("2 ")) {
				type = type.replace("2", "2.0");
			}
			if (type.startsWith("1 ")) {
				type = type.replace("1", "1.0");
			}
		}
		if (StringUtils.isNotBlank(type)) {
			if (type.contains("CHANNEL")) {
				type = type.replace("CHANNEL", "SPEAKERS");
			}
			if (type.contains("1.0")) {
				type = "PORTABLE SPEAKERS";
			}
		}

		if (StringUtils.isBlank(color)) {
			color = flipkartUpdateFeatures(map, "General", "Color");
		}
		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll("(SPEAKER)|(LAPTOP/DESKTOP)|(MOBILE/TABLET)|(SHAPED)|(AUDIO STAND)|(&)|(MULTIMEDIA PLAYER)|(POWER BANK)|(,)|(HOME AUDIO)",
					"");
		}
		if (color != null && title.contains(color)) {
			title = title.replace(color, "");
		}
		String regex1 = "(((([0-9]+[A-Z]*)|([A-Z]+[0-9\\-\\/]+)+)(\\w)+)+)";
		Pattern pattern2 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
		Matcher match2 = pattern2.matcher(title);
		if (match2.find()) {
			if (isMatchPresent(match2, 0)) {
				String tmp = match2.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
				}
				model = tmp.replaceAll("\\s+", " ");
			}
		}
		if (title.contains("WITH")) {
			int indx = title.lastIndexOf("WITH");
			if (indx > -1) {
				title = title.substring(0, indx - 1);
			}
		}
		title = title.trim();
		if (StringUtils.isBlank(model)) {
			model = title;
		}
		if (StringUtils.isNotBlank(model)) {
			if (model.endsWith("-")) {
				int indx = model.lastIndexOf("-");
				if (indx > -1) {
					model = model.substring(0, indx);

				}
			}
			if (model.endsWith("_")) {
				int indx = model.lastIndexOf("_");
				if (indx > -1) {
					model = model.substring(0, indx - 1);

				}
			}
			if (model.startsWith("-")) {
				int index = model.indexOf("-");
				model = model.substring(0, index) + title.substring(index + 1);
			}
			if (StringUtils.isNotBlank(model)) {
				if (model.contains("-")) {
					model = model.replace("-", "");
				}
				if (model.contains("/")) {
					model = model.replace("/", "");
				}
			}
		}

		ReviewedProductInfoDTO info = populateDto(productBrand, null, null, model, type, connectivity, color, prod);
		return info;
	}

	private ReviewedProductInfoDTO populateDto(String productBrand, String productSubBrand, String productSeries, String model, String type,
			String connectivity, String color, HomeProductInfoDTO prod) {

		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase();
		}
		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(productBrand, productSubBrand, productSeries, model, color, new HashMap<String, String>(),
				prod, null);

		info.getProperties().put("CONNECTIVITY", connectivity == null ? "null" : connectivity);
		info.getProperties().put("TYPE", type == null ? "null" : type);

		return info;
	}
}
