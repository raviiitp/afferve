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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

@Named
public class BagsProcessor extends DataProcessor {

	public static final String[] SNAPDEAL_PROPERTY_REGEX = { "PRODUCT DIMENSION IN CMS", "COMPATIBLE LAPTOP SIZE", "DIMENSIONS IN INCHES",
			"NO. OF COMPARTMENTS", "NO OF COMPARTMENTS", "ADDITIONAL FEATURE", "DIMENSION LXHXW CM", "LAPTOP COMPARTMENT", "NO. OF COMPARTMENT",
			"CARRYING CAPACITY", "LAPTOP COMPATIBLE", "DIMENSIONS IN CMS", "LOCK COMBINATION", "SPECIAL FEATURES", "MATERIAL & CARE", "VOLUME CAPACITY",
			"DIMENSION INCH", "INNER MATERIAL", "NO. OF POCKETS", "SHOULDER STRAP", "OTHER FEATURES", "BOTTLE POCKET", "NO OF POCKETS", "NO. OF POCKET",
			"COMPARTMENTS", "DIMENSION CM", "EMPTY WEIGHT", "HANDLE/STRAP", "LUGGAGE TYPE", "PRODUCT CODE", "PRODUCT TYPE", "SIZE IN INCH", "SUITABLE FOR",
			"TROLLEY SIZE", "COMPARTMENT", "LAPTOP SIZE", "SIDE POCKET", "WATER PROOF", "SUBCATEGORY", "BRAND NAME", "DISCLAIMER", "EXPANDABLE", "BRAND NAME",
			"EXACT SIZE", "MODEL NAME", "MODEL CODE", "WATERPROOF", "AGE GROUP", "COMBO SET", "DIMENSION", "IDEAL FOR", "RAINCOVER", "USABILITY", "CAPACITY",
			"FEATURES", "MATERIAL", "OCCASION", "EAN CODE", "EAN NOS.", "SKU CODE", "WARRANTY", "CLOSURE", "CONTENT", "PADDING", "POCKETS", "UTILITY",
			"COLOUR", "GENDER", "WEIGHT", "WHEELS", "VOLUME", "BRAND", "COLOR", "COMBO", "MODEL", "SHELL", "STRAP", "STYLE", "SIZE", "SUPC", "TYPE", "SKU",
			"USP" };

	public static final String SNAPDEAL_UNWANTED_WORDS = "\\s((COLLEGE)|(SCHOOL)|(COLOR)|(TYPE)|(WITH)|(BAG(S)?)|(AND)|(\\-)|(&)|(S\\s))\\s?";
	public static final String AMAZON_UNWANTED_WORDS = "\\s((COLLEGE)|(SCHOOL)|(COLOR)|(TYPE)|(WITH)|(AND)|(\\-)|(&)|(S\\s))|(\\(\\s?\\))|(\\,)|(\\,\\-)\\s?";

	public static final String AMAZON_MATERIAL = "(ABS)|(Canvas)|(Fabric)|(Felt)|(Leather)|(Metal)|(Nylon)|(Polyester)|(Synthetic)";

	@Override
	public ReviewedProductInfoDTO shopcluesProcess(HomeProductInfoDTO prod) {
		return null;
	}

	@Override
	public ReviewedProductInfoDTO ebayProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReviewedProductInfoDTO snapdealProcess(HomeProductInfoDTO prod) {
		String _title = prod.getTitle();
		String brand = prod.getProductBrand();
		if (StringUtils.isBlank(_title) || StringUtils.isBlank(brand)) {
			return null;
		}
		brand = brand.toUpperCase();
		_title = _title.replace(brand, "");

		String color = prod.getColor();
		if (StringUtils.isNotBlank(_title) && StringUtils.isNotBlank(color)) {
			_title = _title.replaceAll("\\s" + color, " ");
		}

		String description = prod.getDescription();

		if (StringUtils.isNotBlank(description)) {
			description = description.replaceAll(brand, "");
			description = description.replaceAll("\\(\\w*\\)", ""); /*
																	 * replace
																	 * (words
																	 * along
																	 * with
																	 * brackets)
																	 */
			description = description.replaceAll("\\s\\s+", " "); /*
																 * replace
																 * double spaces
																 */
		}

		String[] descritpionByColon = null;
		String nextKey = null, prevKey = null;

		int ii = 0;
		String value = null;

		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("BAG TYPE", null);
		propertyMap.put("CAPACITY", null);
		propertyMap.put("CLOSURE", null);
		propertyMap.put("COLOR", null);
		propertyMap.put("FEATURES", null);
		propertyMap.put("IDEAL FOR", null);
		propertyMap.put("MATERIAL", null);
		propertyMap.put("SIZE", null);
		propertyMap.put("WATER PROOF", null);

		if (StringUtils.isNotBlank(description)) {
			descritpionByColon = description.split("\\s?:\\s?");
			if (descritpionByColon != null && descritpionByColon.length > 0) {
				prevKey = StringUtils.trim(descritpionByColon[0]);
				for (ii = 1; ii < descritpionByColon.length; ii++) {
					for (int jj = 0; jj < SNAPDEAL_PROPERTY_REGEX.length; jj++) {
						if (descritpionByColon[ii].endsWith(SNAPDEAL_PROPERTY_REGEX[jj])) {
							nextKey = SNAPDEAL_PROPERTY_REGEX[jj];
							value = descritpionByColon[ii].replace(nextKey, "");
							if (StringUtils.isNotBlank(value)) {
								value = value.trim();
							}
							if ("PRODUCT TYPE".equals(prevKey) || "TYPE".equals(prevKey) || "LUGGAGE TYPE".equals(prevKey)) {
								prevKey = "BAG TYPE";
								if (StringUtils.isNotBlank(value)) {
									value = value.replace("COMPATIBILITY", "BAG");
									value = value.replace("COMPATIBLE", "BAG");
									if (value.endsWith("S")) {
										value = value.substring(0, value.length() - 1);
									}
								}
							}
							else if ("GENDER".equals(prevKey) || "SUITABLE FOR".equals(prevKey)) {
								prevKey = "IDEAL FOR";
							}
							else if ("LAPTOP SIZE".equals(prevKey) || "TROLLEY SIZE".equals(prevKey)) {
								prevKey = "SIZE";
							}
							else if ("COLOUR".equals(prevKey)) {
								prevKey = "COLOR";
							}
							else if ("WATERPROOF".equals(prevKey)) {
								prevKey = "WATER PROOF";
							}
							if (propertyMap.containsKey(prevKey)) {
								propertyMap.put(prevKey, value);
								if (StringUtils.isNotBlank(_title)) {
									_title = _title.replace(" "+value, " ");
								}
							}
							prevKey = nextKey;
							break;
						}
					}
				}

				// for last one
				if (prevKey != null && ii > 1) {
					value = descritpionByColon[ii - 1];
					if (StringUtils.isNotBlank(value)) {
						value = value.trim();
					}
					if ("PRODUCT TYPE".equals(prevKey) || "TYPE".equals(prevKey) || "LUGGAGE TYPE".equals(prevKey)) {
						prevKey = "BAG TYPE";
						if (StringUtils.isNotBlank(value)) {
							value = value.replace("COMPATIBILITY", "BAG");
							value = value.replace("COMPATIBLE", "BAG");
							if (value.endsWith("S")) {
								value = value.substring(0, value.length() - 1);
							}
						}
					}
					else if ("GENDER".equals(prevKey) || "SUITABLE FOR".equals(prevKey)) {
						prevKey = "IDEAL FOR";
					}
					else if ("LAPTOP SIZE".equals(prevKey) || "TROLLEY SIZE".equals(prevKey)) {
						prevKey = "SIZE";
					}
					else if ("COLOUR".equals(prevKey)) {
						prevKey = "COLOR";
					}
					if (propertyMap.containsKey(prevKey)) {
						propertyMap.put(prevKey, value);
						if (StringUtils.isNotBlank(_title)) {
							_title = _title.replaceAll("\\s" + value, " ");
						}
					}
				}
			}
		}

		// product type and type are same
		BagProperties bagProperties = new BagProperties();
		bagProperties.setBagType(propertyMap.get("BAG TYPE"));
		bagProperties.setCapacity(propertyMap.get("CAPACITY"));
		bagProperties.setClosure(propertyMap.get("CLOSURE"));
		if (StringUtils.isNotBlank(color)) {
			bagProperties.setColor(color);
			if (StringUtils.isNotBlank(_title)) {
				_title = _title.replaceAll("\\s" + color, " ");
			}
		}
		else {
			bagProperties.setColor(propertyMap.get("COLOR"));
		}
		bagProperties.setFeatures(propertyMap.get("FEATURES"));
		bagProperties.setIdealFor(propertyMap.get("IDEAL FOR"));
		bagProperties.setMaterial(propertyMap.get("MATERIAL"));
		bagProperties.setSize(propertyMap.get("SIZE"));
		bagProperties.setWaterProof(propertyMap.get("WATER PROOF"));

		if (StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(SNAPDEAL_UNWANTED_WORDS, " ");
		}
		if (StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll("\\s+", " ").trim();
			if (_title.startsWith(" ")) {
				_title = _title.substring(1, _title.length());
			}
		}

		bagProperties.setModel(_title);
		bagProperties.setProduct_brand(brand);

		return populateDto(prod, bagProperties);
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String _title = prod.getTitle();
		String brand = prod.getProductBrand();
		if (StringUtils.isBlank(_title) || StringUtils.isBlank(brand)) {
			return null;
		}

		brand = brand.toUpperCase();

		_title = _title.replace(brand, "");

		String match = null;
		String color = prod.getColor();
		if (StringUtils.isNotBlank(color)) {
			if(StringUtils.isNotBlank(_title)){
				_title = _title.replaceAll("\\s" + color, " ");
			}
		} else {
			Pattern pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(_title);
			while (matcher.find()) {
				if (isMatchPresent(matcher, 0)) {
					match = matcher.group(0);
				}
			}

			if (StringUtils.isNotBlank(match)) {
				color = match.trim();
				/*if (StringUtils.isNotBlank(_title)) {
					int index = _title.indexOf(match);
					if (index != -1) {
						_title = _title.substring(0, index).trim();
					}
				}*/
					color = color.replace("(", "");
			}
			if (StringUtils.isNotBlank(_title)) {
				_title = _title.replaceAll("\\(.*\\)", " ");
			}
		}
		
		String type = prod.getType();
		if (StringUtils.isNotBlank(_title) && StringUtils.isNotBlank(type)) {
			_title = _title.replaceAll(type, "");
			type = type.replace("COMPATIBILITY", "BAG");
			type = type.replace("COMPATIBLE", "BAG");
			if (type.endsWith("S") && !type.endsWith("'S")) {
				type = type.substring(0, type.length() - 1);
			}
		}

		if (StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(AMAZON_UNWANTED_WORDS, " ");
		}
		if (StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll("\\s+", " ").trim();
			if (_title.startsWith(" ")) {
				_title = _title.substring(1, _title.length());
			}
		}

		String material = null;
		Pattern pattern = Pattern.compile(AMAZON_MATERIAL);
		Matcher matcher = pattern.matcher(prod.getTitle());
		match = null;
		while (matcher.find()) {
			if (isMatchPresent(matcher, 0)) {
				match = matcher.group(0);
				break;
			}
		}

		if (StringUtils.isNotBlank(match)) {
			material = match.trim();
			if (StringUtils.isNotBlank(_title)) {
				_title = _title.replaceAll(material, "");
			}
		}
		
		if (StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(COLOR_REGEX, "");
		}

		BagProperties bagProperties = new BagProperties();

		bagProperties.setBagType(type);
		bagProperties.setColor(color);
		bagProperties.setMaterial(material);

		bagProperties.setModel(_title);
		bagProperties.setProduct_brand(brand);

		return populateDto(prod, bagProperties);
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {

		String _title = prod.getTitle();
		String brand = prod.getProductBrand();
		if (StringUtils.isBlank(_title) || StringUtils.isBlank(brand)) {
			return null;
		}

		brand = brand.toUpperCase();

		_title = _title.replace(brand, "");

		String color = prod.getColor();
		if (StringUtils.isNotBlank(_title) && StringUtils.isNotBlank(color)) {
			_title = _title.replaceAll(color, "");
		}

		Map<String, Map<String, String>> map = jsonToMap(prod.getSpecificationJson());

		BagProperties bagProperties = new BagProperties();

		bagProperties.setBagType(flipkartUpdateFeatures(map, "General", "Type"));
		if (StringUtils.isNotBlank(bagProperties.getBagType()) && StringUtils.isNotBlank(_title)) {
			String bagTypeValue = bagProperties.getBagType();
			bagTypeValue = bagTypeValue.replace("COMPATIBILITY", "BAG");
			bagTypeValue = bagTypeValue.replace("COMPATIBLE", "BAG");
			if (bagTypeValue.endsWith("S")) {
				bagTypeValue = bagTypeValue.substring(0, bagTypeValue.length() - 1);
			}
			bagProperties.setBagType(bagTypeValue);
			_title = _title.replaceAll(bagProperties.getBagType(), " ");
		}

		bagProperties.setCapacity(flipkartUpdateFeatures(map, "General", "Capacity"));
		if (StringUtils.isNotBlank(bagProperties.getCapacity()) && StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(bagProperties.getCapacity(), "");
		}

		bagProperties.setClosure(flipkartUpdateFeatures(map, "General", "Closure"));
		if (StringUtils.isNotBlank(bagProperties.getClosure()) && StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(bagProperties.getClosure(), "");
		}

		if (StringUtils.isNotBlank(color)) {
			bagProperties.setColor(color);
		}
		else {
			String tmp = flipkartUpdateFeatures(map, "General", "Color Code");
			if (StringUtils.isNotBlank(tmp) && !NumberUtils.isDigits(tmp)) {
				bagProperties.setColor(tmp);
			}
		}
		if (StringUtils.isNotBlank(bagProperties.getColor()) && StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(bagProperties.getColor(), "");
		}

		bagProperties.setFeatures(null);

		bagProperties.setIdealFor(flipkartUpdateFeatures(map, "General", "Ideal For"));
		if (StringUtils.isNotBlank(bagProperties.getIdealFor()) && StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(bagProperties.getIdealFor(), "");
			if (bagProperties.getIdealFor().contains("MEN") && bagProperties.getIdealFor().contains("WOMEN")) {
				bagProperties.setIdealFor("UNISEX");
			}
		}
		bagProperties.setMaterial(flipkartUpdateFeatures(map, "General", "Material"));
		if (StringUtils.isNotBlank(bagProperties.getMaterial()) && StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(bagProperties.getMaterial(), "");
		}
		bagProperties.setSize(flipkartUpdateFeatures(map, "General", "Bag Size"));
		if (StringUtils.isNotBlank(bagProperties.getSize()) && StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(bagProperties.getSize(), "");
		}
		bagProperties.setWaterProof(flipkartUpdateFeatures(map, "Body Features", "Waterproof"));
		if (StringUtils.isNotBlank(bagProperties.getWaterProof()) && StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(bagProperties.getWaterProof(), "");
		}

		if (StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll(SNAPDEAL_UNWANTED_WORDS, " ");
		}
		if (StringUtils.isNotBlank(_title)) {
			_title = _title.replaceAll("\\s+", " ").trim();
			if (_title.startsWith(" ")) {
				_title = _title.substring(1, _title.length());
			}
		}

		bagProperties.setModel(_title);
		bagProperties.setProduct_brand(brand);

		return populateDto(prod, bagProperties);
	}

	private ReviewedProductInfoDTO populateDto(HomeProductInfoDTO prod, BagProperties bagProperties) {

		String color = bagProperties.getColor();

		if (StringUtils.isNotBlank(color)) {
			color = color.toUpperCase().trim();
		}

		String material = bagProperties.getMaterial();
		if (StringUtils.isNotBlank(material)) {
			material = material.replaceAll("P\\.?\\s?U\\.?", "PU");
			if (material.contains("PU ")) {
				material = "PU";
			}
			else if (material.contains("CLOTH")) {
				material = material.split("\\s")[0];
			}
			else if (material.contains("DENIER")) {
				material = "DENIER DURALITE";
			}
			else if (material.contains("POLYESTER")) {
				material = "POLYESTER";
			}
			bagProperties.setMaterial(material);
		}

		String closure = bagProperties.getClosure();
		if (StringUtils.isNotBlank(closure)) {
			if (closure.contains("ZIP")) {
				closure = "ZIPPER";
			}
			else if (closure.contains("MAGN")) {
				closure = "MAGNETIC BUTTON";
			}
			else if (closure.contains("FLAP")) {
				closure = "FLAP";
			}
			bagProperties.setClosure(closure);
		}

		String model = bagProperties.getModel();
		if (StringUtils.isNotBlank(model)) {
			if (model.endsWith(" S")) {
				model = model.substring(0, model.length() - 2);
			}
			else if ("S".equals(model)) {
				model = null;
			}
			bagProperties.setModel(model);
		}

		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(bagProperties.getProduct_brand(), null, null, bagProperties.getModel(), color,
				new HashMap<String, String>(), prod, null);

		info.getProperties().put("BAG TYPE", StringUtils.isNotBlank(bagProperties.getBagType()) ? bagProperties.getBagType().trim() : "NULL");
		info.getProperties().put("CAPACITY", StringUtils.isNotBlank(bagProperties.getCapacity()) ? bagProperties.getCapacity().trim() : "NULL");
		info.getProperties().put("CLOSURE", StringUtils.isNotBlank(bagProperties.getClosure()) ? bagProperties.getClosure().trim() : "NULL");
		info.getProperties().put("FEATURES", StringUtils.isNotBlank(bagProperties.getFeatures()) ? bagProperties.getFeatures().trim() : "NULL");
		info.getProperties().put("IDEAL FOR", StringUtils.isNotBlank(bagProperties.getIdealFor()) ? bagProperties.getIdealFor().trim() : "NULL");
		info.getProperties().put("MATERIAL", StringUtils.isNotBlank(bagProperties.getMaterial()) ? bagProperties.getMaterial().trim() : "NULL");
		info.getProperties().put("SIZE", StringUtils.isNotBlank(bagProperties.getSize()) ? bagProperties.getSize().trim() : "NULL");
		info.getProperties().put("WATER PROOF", StringUtils.isNotBlank(bagProperties.getWaterProof()) ? bagProperties.getWaterProof().trim() : "NULL");

		return info;
	}

	class BagProperties {
		String product_brand = null;
		String product_sub_brand = null;
		String series = null;
		String model = null;
		String color = null;
		String bagType = null;
		String features = null;
		String idealFor = null;
		String material = null;
		String size = null;
		String capacity = null;
		String waterProof = null;
		String closure = null;

		public String getProduct_brand() {
			return product_brand;
		}

		public void setProduct_brand(String product_brand) {
			this.product_brand = product_brand;
		}

		public String getProduct_sub_brand() {
			return product_sub_brand;
		}

		public void setProduct_sub_brand(String product_sub_brand) {
			this.product_sub_brand = product_sub_brand;
		}

		public String getSeries() {
			return series;
		}

		public void setSeries(String series) {
			this.series = series;
		}

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getBagType() {
			return bagType;
		}

		public void setBagType(String bagType) {
			this.bagType = bagType;
		}

		public String getFeatures() {
			return features;
		}

		public void setFeatures(String features) {
			this.features = features;
		}

		public String getIdealFor() {
			return idealFor;
		}

		public void setIdealFor(String idealFor) {
			this.idealFor = idealFor;
		}

		public String getMaterial() {
			return material;
		}

		public void setMaterial(String material) {
			this.material = material;
		}

		public String getSize() {
			return size;
		}

		public void setSize(String size) {
			this.size = size;
		}

		public String getCapacity() {
			return capacity;
		}

		public void setCapacity(String capacity) {
			this.capacity = capacity;
		}

		public String getWaterProof() {
			return waterProof;
		}

		public void setWaterProof(String waterProof) {
			this.waterProof = waterProof;
		}

		@Override
		public String toString() {
			return "BgPr [br=" + product_brand + ", md=" + model + ", clr=" + color + ", typ=" + bagType + ", fetrs=" + features + ", sex=" + idealFor
					+ ", mtrl=" + material + ", sz=" + size + ", cpcty=" + capacity + ", wtrPrf=" + waterProof + ", clsr=" + closure + "]";
		}

		public String getClosure() {
			return closure;
		}

		public void setClosure(String closure) {
			this.closure = closure;
		}
	}

	
//	public static void main(String[] args) {
//		BagsProcessor bp = new BagsProcessor();
//		HomeProductInfoDTO _hpi = new HomeProductInfoDTO();
//		_hpi.setHome("FLIPKART");
//		_hpi.setTitle("United Colors of Benetton A03-Basic Backpack(902)".toUpperCase());
//		_hpi.setSize(null);
//		_hpi.setType("WOMEN'S");
//		_hpi.setProductBrand("United Colors of Benetton".toUpperCase());
//		_hpi.setDescription("BRAND : MANUFACTURED FOR DELL LAPTOPS MATERIAL : OTHERS DIMENSION (LXHXW) CM : 10X12X5 COLOR : BLACK TYPE : Shoulder Bag BRAND :DELL TYPE:BACKPACKS MATERIAL:CANVAS COLOR:BLACK LAPTOP SIZE:38.1 CM(15.6 INCH) WATER PROOF:YES NO. OF COMPARTMENTS: 2 BOTTLE POCKET: 2 PADDING: YES WARRANTY: 1 YEAR MANUFACTURE WARRANTY COMBO: NO EMPTY WEIGHT: 450GM USABILITY: DIMENSION (LXHXW) CM: 10X12X5 SUPC: DISCLAIMER:PRODUCT COLOR MAY SLIGHTLY VARY DUE TO PHOTOGRAPHIC LIGHTING SOURCES OR YOUR MONITOR SETTINGS"
//				.toUpperCase());
//		ReviewedProductInfoDTO rv = bp.flipkartProcess(_hpi);
//		if (rv != null) {
//			System.out.println(rv.toString());
//		}
//	}
}
