/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.frontoffice.service;

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update.Assignments;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogJobEnum;

@Named(value = "UpdateService")
public class UpdateService extends Service {

	private final static Logger log = LoggerFactory.getLogger(UpdateService.class);

	@Inject
	private ServiceCall urlCall;

	private List<ReviewedProductInfoDTO> rows;
	private String category;

	private static Set<String> flipkartSet = new HashSet<String>(Arrays.asList(
			/*Smartphones*/ "Memory", "Expandable Memory", "SIM Type", "Rear Camera", "Front Facing Camera", "Resolution", "3G", "4G", "Size", "OS", "Type", "Graphics", "Processor",
			/*Air Conditioner*/ "Model Name", "Remote Control", "Star Rating", "Capacity in Tons", "Cooling Capacity", "Dehumidification", "Compressor", "Sleep Mode", "Timer", "Anti-bacteria Filter", "Dust Filter", "Power Requirement",
			/*Camera*/ "Model ID", "Maximum Shutter Speed", "Minimum Shutter Speed", "Shutter Flash Sync Speed", "Total Pixel/Gross Pixel", "Optical Sensor Resolution (in MegaPixel)", "Optical Zoom", "Digital Zoom", "Auto Focus", "Focal Length", "Focus Points", "LCD Screen Size", "Viewfinder Magnification", "LCD Display", "Video Display Resolution", "Flash Modes", "Lens Type", "3D Images", "Power Requirement",
			/*Desktops*/ "Display Resolution", "Display Size", "Touchscreen Support", "3D Support", "Hard Drive", 
			/*Laptops*/ "Part Number", "Lifestyle", "RAM Frequency", "System Memory", "HDD Capacity", "Graphic Processor", "Dedicated Graphics Memory Capacity", "Dedicated Graphics Memory Type", "Cache", "Clock Speed", "USB Port", "HDMI Port", "Resolution", "Screen Size", "Screen Type", "Flash Compensation", "Sensor Type", "Processor Frequency", "Processor Model", "Processor Name",
			/*Microwave Oven*/ "Capacity", "Type", "Cavity Material", "Defrost", "Timer", "Maximum Cooking Time", "Power Output", "Deodorizer", "Child Lock",  
			/*Refrigerator*/ "Defrosting Type", "Refrigerator Type", "Number of Doors", "Stabilizer Required", "Shelf Material",
			/*Television*/  "Display Size", "Operating Modes", "Turbo Mode", "Technology Used", "USB","HD Technology & Resolution", "LED Display Type", "Aspect Ratio", "HDMI", "Smart TV", "3D", "Speaker Output RMS", "Power Requirement",
			/*Washing Machine*/ "Model Name", "Dryer Type", "Auto Power Off", "Wash Program Types", "Lint Filter", "Water Consumption", "Washing Method", "Washing Capacity", "Tub Material",
			/*Watches*/ "Chronograph", "Date Display", "Alarm Clock", "Light", "Occasion", "Mechanism","Strap Type", "Dial Shape", "Water Resistant", "Strap Material",
			/*Eyewear*/ "Body Material", "Temple Material", "Frame Shape", "Frame Style", "Frame Type",
			/*Sunglass*/ "Lens Polarization", "Lens Color", "Lens Gradient", "Lens Material", "UV Protection", "Frame Color", "Frame Type",
			/*Pendrive*/ "Type", "Capacity (GB)", "Transfer Speed", "Interface", "Encryption", "Form Factor", "Case Material",
			/*Tablet*/ "ROM", "RAM", "SIM", "Processor", "4G", "Operating System", "Sensors",
			/*SPEAKERS*/ "Controls", "Remote", "Type", "Configuration", "Subwoofer", "Connector Type", "Headphone Jack", "Power Source",
			/*HEADPHONES*/ "Designed For", "In Sales Package", "Noise Cancellation", "Impedance", "Headphone Driver Units", "Deep Bass", "Bluetooth", "Flatwire", "Foldable/Collapsible", "Connector Size", "Connector Plating", "Cord Type",
			/*GAMINGCONSOLES*/ "Controllers", "Console Type", "Battery", "Hard Disk", "In The Box", "Key Features", "Weight", "AV Multi Output", "AV Digital Output", "Bluetooth", "Ethernet",
			/*Bags*/ "Closure", "Type", "Ideal For", "Number of Pockets", "Pattern", "Shoulder Strap", "Capacity", "Bag Size", "Material", "Occasion", "Luggage Size"));
	
	private static Set<String> snapdealSet = new HashSet<String>(Arrays.asList(
			/*Smartphones*/ "Model", "RAM", "Internal Memory", "Expandable Memory", "SIMs", "Dual SIM", "Rear Camera", "Front Camera", "Display Resolution", "3G/WCDMA", "4G/LTE", "CPU", "Display Size", "Operating System", "OS Version",
			/*Air Conditioner*/ "Model", "Energy Efficiency Ratio", "Type", "Star Rating", "Energy Efficiency", "Capacity", "Anti-bacterial Filter", "Dual Protection Filter", "Anti-Dust Filter", "Auto-Clean", "Panel Display", "Installation Type", "Sleep Mode", "Dehumidifier", "Noise Level - Indoor", "Voltage", "Wattage", "Refrigerant Gas", "Power Supply - Voltage",
			/*Camera*/ "Lens Mount", "Lens 1 Name", "Lens 1: Focal Length", "Resolution", "Sensor Type", "Sensor Size", "Maximum Shutter Speed", "Minimum Shutter Speed", "Minimum ISO", "Maximum ISO", "ISO Rating", "Focus Points", "Video Resolution", "Screen Size (Diagonal)", "Screen Type", "Optical Zoom", "Digital Zoom",
			/*Desktops*/ "Model Name", "Display Size", "Display Type", "USB", "Hard Drive", "System Memory", "Memory Detail", "Integrated Graphic Processor",
			/*Laptops*/ "Series", "Model Number", "RAM", "Expandable Memory", "Storage Capacity", "Screen Size", "Resolution", "Operating System", "Processor Name", "Hard Disk Capacity","Graphic Processor", "USB Port/S",
			/*Microwave Oven*/ "Capacity(Ltrs)", "LED Display", "Power Levels", "Auto Cook Menu", "Timer", "Child Lock", "Defrost", "Cavity Material", 
			/*Refrigerator*/ "Energy Rating", "No. of Doors", "Door Type", "Defrosting Type", "Star Rating", "Interiors", "Refrigerator Shelves", "Refrigeration and Cooling Technology", "Voltage",
			/*Television*/ "Screen Type", "Display Feature", "Screen Resolution", "Aspect Ratio", "HDMI", "USB", "Type", "Speakers", "HDMI", "USB", "Power Supply",
			/*Washing Machine*/ "Model", "Type", "Capacity", "Technology", "Panel Display", "Water Level Setting", "Wash Program", "Exterior - Material", "Warranty Period",
			/*Watches*/ "Strap Type", "Strap material","Wearability", "Dial Shape", "Chronograph", "Date Display", "Power Source",
			/*Eyewear*/ "Frame size", "Frame material", "Frame type", "Frame shape", "Features", "Size", "Lens Technology",
			/*Sunglass*/ "Frame Colour", "Frame Material", "Frame Shape", "Frame Size", "Frame type", "Gradient", "Lens Color", "Lens Material", "Polarized",
			/*Pendrive*/ "Capacity", "Colour", "Interface", "Read Speed", "Write Speed", "Type", "Form Factor",
			/*Tablet*/ "Voice Call", "SIMs", "Colour", "Operating System", "Screen Size (in cm)", "4G", "Expandable Storage Capacity",
			/*SPEAKERS*/ "Subwoofer", "Configuration", "Number of Satellite Speakers", "Connector Type", "Control Buttons", "Remote Control",
			/*HEADPHONES*/ "Batteries Required", "Connector Type", "Bluetooth Range", "Noise Reduction & Cancellations", "Call Controls", "Music Controls",
			/*GAMINGCONSOLES*/ "Box Contents", "Console Type", "Controller Type", "Output", "Ethernet", "WiFi",
			/*Bags*/ "Material", "Laptop Compartment", "Shoulder Strap", "Gender", "Closure", "Type", "Water Proof", "Capacity", "Suitable For", "Raincover", "Wheels", "Shell", "Size"));
	
	private static Set<String> amazonSet = new HashSet<String>(Arrays.asList(
			/*Smartphones*/ "Item model number", "OS", "RAM", "Wireless communication technologies", "Connectivity technologies", "Form factor", "Special features", 
			/*Air Conditioner*/ "Model", "Energy Efficiency", "Capacity", "Noise Level", "Installation Type", "Control Console", "Voltage", "Wattage", "Material",
			/*Camera*/ "Resolution", "Display Technology", "Screen Size", "Optical Zoom", "Digital Zoom", "Video Capture Resolution", "Display Technology", "Display Resolution Maximum", "Max Shutter Speed", "Min Shutter Speed", "Min Focal Length",
			/*Desktops*/ 
			/*Laptops*/ "Series", "Screen Size", "RAM Size", "Memory Technology", "Processor Brand", "Processor Type", "Processor Speed", "Processor Count", "Hard Drive Size", "Operating System", "Graphics Coprocessor", "Graphics Card Ram Size", "Maximum Display Resolution",  "Notebook Display Technology", "Computer Memory Type",
			/*Microwave Oven*/ 
			/*Refrigerator*/ "Defrost System", "Door Orientation", "Number of Shelves", "Freezer Capacity",
			/*Television*/ "Display Resolution Maximum", "Resolution", "Display Technology", "Image Aspect Ratio", "Total Usb Ports","Product Dimensions", "Item Weight", "Image Contrast Ratio",
			/*Washing Machine*/ "Model", "Capacity", "Control Console", "Form Factor", "Door Orientation",
			/*Watches*/
			/*Eyewear*/
			/*Sunglass*/
			/*Pendrive*/"RAM Size","Computer Memory Type","Operating System","Included Components",
			/*Tablet*/
			/*SPEAKERS*/ "Speaker Connectivity", "Audio Wattage", "Wattage", "Batteries Included", "Total Usb Ports", "Connector Type", "Included Components",
			/*HEADPHONES*/ "Connector Type", "Batteries Included", "Headphones Technology", "Headphones Form Factor", "Microphone Form Factor",
			/*GAMINGCONSOLES*/
			/*Bags*/ "Material", "Closure", "Outer-Material", "Shell", "Handle", "Lock"));
	
	private static Set<String> ebaySet = new HashSet<String>(Arrays.asList("Camera", "Sim Type", "Operating System", "Features"));

	public void checkout(List<ProcessLog> list, HomeEnum home) throws InterruptedException {
		process = processUtil.start(home.name(), ProcessLogJobEnum.HOT_UPDATE.name());
		list.add(process);
		if (home.equals(HomeEnum.FLIPKART)) {
			forFlipkart();
		}
		if (home.equals(HomeEnum.SNAPDEAL)) {
			forSnapdeal();
		}
		else if (home.equals(HomeEnum.EBAY)) {
			forEbay();
		}
		else if (home.equals(HomeEnum.AMAZON)) {
			forAmazon();
		}
		list.remove(process);
		processUtil.end(process);
	}

	@Async
	public void init() {
		try {
			forFlipkart();
			forSnapdeal();
			forEbay();
			forAmazon();
		} catch (InterruptedException e) {
			log.error("INTERRUPTED", e);
		}
	}

	public void forFlipkart() throws InterruptedException {
		preprocess(HomeEnum.FLIPKART.name());
	}

	public void forSnapdeal() throws InterruptedException {
		preprocess(HomeEnum.SNAPDEAL.name());
	}

	public void forEbay() throws InterruptedException {
		preprocess(HomeEnum.EBAY.name());
	}

	public void forAmazon() throws InterruptedException {
		preprocess(HomeEnum.AMAZON.name());
	}

	private void preprocess(String home) throws InterruptedException {
		while (true) {
			boolean isBreak = false;
			for (CategoryEnum cat : CategoryEnum.values()) {
				if (cat.equals(CategoryEnum.ALL))
					continue;
				setCategory(cat.name());
				List<ReviewedProductInfoDTO> list = repository.getDataForHotProcessing(home, getCategory());
				if (list != null && list.size() > 0) {
					setRows(list);
					execute();
					isBreak &= false;
				}
				else {
					isBreak = true;
				}
			}
			if (isBreak) {
				break;
			}
		}
	}

	private void execute() throws InterruptedException {
		BatchStatement batch = new BatchStatement();
		for (ReviewedProductInfoDTO row : getRows()) {
			// Stopping running Thread
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("KILL THREAD");
			}

			Assignments stmt = QueryBuilder.update(keyspace, TableEnum.reviewed_product_info.name()).with(QueryBuilder.set("hotUpdate", true));
			if (row.getFeatures() != null && row.getFeatures().size() > 0) {
			}
			else {
				boolean isColorUpdated = getInfo(row);
				if (row.getFeatures() != null && row.getFeatures().size() > 0) {
					stmt = stmt.and(QueryBuilder.set("features", row.getFeatures()));
				}
				if (isColorUpdated) {
					stmt = stmt.and(QueryBuilder.set("color", row.getColor())).and(QueryBuilder.set("ismerged", false));
				}
			}

			Statement statement = stmt.where(QueryBuilder.eq("home", row.getHome())).and(QueryBuilder.eq("subcategoryname", row.getSubCategoryName()))
					.and(QueryBuilder.eq("id", row.getId()));

			batch.add(statement);

			if (batch.size() > BATCHSIZE) {
				session.execute(batch);
				batch.clear();
			}
		}

		session.execute(batch);
		batch.clear();
	}

	/**
	 * @return the rows
	 */
	public List<ReviewedProductInfoDTO> getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(List<ReviewedProductInfoDTO> rows) {
		this.rows = rows;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	public boolean getInfo(ReviewedProductInfoDTO dto) {
		if ("AMAZON".equalsIgnoreCase(dto.getHome())) {
			return getInfoAmazon(dto);
		}
		else if ("FLIPKART".equalsIgnoreCase(dto.getHome())) {
			return getInfoFlipkart(dto);
		}
		else if ("SNAPDEAL".equalsIgnoreCase(dto.getHome())) {
			return getInfoSnapdeal(dto);
		}
		else if ("EBAY".equalsIgnoreCase(dto.getHome())) {
			return getInfoEbay(dto);
		}
		return false;
	}

	public boolean getInfoAmazon(ReviewedProductInfoDTO dto) {
		String url = dto.getProductUrl();
		if (StringUtils.isNotBlank(url)) {
			// log.info("AMAZON : {}", url);
			Document doc = null;
			try {
				doc = urlCall.execute(url, "http://www.amazon.in/");
			} catch (Exception e) {
				msgLog.addError(e);
			}
			if (doc != null) {
				String color = null;
				List<String> features = new LinkedList<String>();

				Elements tmp = doc.select("h1#title > span");

				tmp = doc.select("div.attrG");
				if (tmp != null) {
					for (Element ele : tmp) {
						Elements h1 = ele.select("td.label");
						Elements h2 = ele.select("td.value");
						if (h1 != null && h2 != null) {
							int len = h1.size();
							int len2 = h2.size();
							for (int i = 0; i < len - 1 && i < len2; i++) {
								if (h1.get(i) != null && h2.get(i) != null) {
									String key = h1.get(i).text();
									String val = h2.get(i).text();
									if ("ASIN".equalsIgnoreCase(key)) {
										break;
									}
									if (amazonSet.contains(key)) {
										if ("Special features".equalsIgnoreCase(key)) {
											val = val.replaceAll("\\s?\\d+GB\\sInternal\\sStorage,", "");
										}
										features.add((key + " : " + val).toUpperCase());
									}
									if ("Colour".equalsIgnoreCase(key)) {
										color = val;
									}
									/*
									 * if ("Storage Capacity (Internal):".
									 * equalsIgnoreCase(key)) { size =
									 * val.replaceAll("\\s", ""); }
									 */
								}
							}
						}
					}
				}
				dto.setFeatures(features);
				if (StringUtils.isBlank(dto.getColor()) && StringUtils.isNotBlank(color)) {
					dto.setColor(color.toUpperCase().trim());
					return true;
				}
			}
		}
		return false;
	}

	public boolean getInfoEbay(ReviewedProductInfoDTO dto) {
		String url = dto.getProductUrl();
		if (!StringUtils.isEmpty(url)) {
			// log.info("EBAY : {}", url);
			Document doc = null;
			try {
				doc = urlCall.execute(url, "");
			} catch (Exception e) {
				msgLog.addError(e);
			}
			if (doc != null) {
				String color = null;
				List<String> features = new LinkedList<String>();

				Elements tmp = doc.select("h1.it-ttl");

				tmp = doc.select("div.itemAttr");
				if (tmp != null) {
					for (Element ele : tmp) {
						Elements h1 = ele.select("td.attrLabels");
						Elements h2 = ele.select("td > span");
						if (h1 != null && h2 != null) {
							int len = h1.size();
							int len2 = h2.size();
							for (int i = 0; i < len - 1 && i < len2; i++) {
								if (h1.get(i + 1) != null && h2.get(i) != null) {
									String key = h1.get(i + 1).text();
									String val = h2.get(i).text();
									key = key.replace(":", "");
									if (ebaySet.contains(key)) {
										features.add((key + " : " + val).toUpperCase());
									}
									if ("Colour".equalsIgnoreCase(key)) {
										color = val;
									}
									/*
									 * if ("Storage Capacity (Internal)".
									 * equalsIgnoreCase(key) ||
									 * "Capacity".equalsIgnoreCase(key) ||
									 * "Storage Capacity".equalsIgnoreCase(key))
									 * { size = val.replaceAll("\\s", ""); }
									 */
								}
							}
						}
					}
				}

				dto.setFeatures(features);
				if (StringUtils.isBlank(dto.getColor()) && StringUtils.isNotBlank(color)) {
					dto.setColor(color);
					return true;
				}
			}
		}
		return false;
	}

	public boolean getInfoSnapdeal(ReviewedProductInfoDTO dto) {
		String url = dto.getProductUrl();
		if (!StringUtils.isEmpty(url)) {
			if (url != null && url.lastIndexOf('?') > 0) {
				url = url.substring(0, url.indexOf('?'));
			}
			// log.info("SNAPDEAL : {}", url);

			Document doc = null;
			try {
				if (StringUtils.isNotBlank(url)){
					doc = urlCall.execute(url, "http://www.snapdeal.com/");
				}
			} catch (Exception e) {
				msgLog.addError(e);
			}
			if (doc != null) {
				String color = null;
				String size = null;
				List<String> features = new LinkedList<String>();

				Elements tmp = doc.select("h1.pdp-e-i-head");

				tmp = doc.select("table.product-spec");
				if (tmp != null) {
					for (Element e : tmp) {
						String s = e.text();
						int ind_s = s.indexOf("Colour ");
						if (ind_s != -1) {
							int ind_e = s.indexOf("Other Features", ind_s);
							if (ind_e != -1) {
								color = s.substring(ind_s + 6, ind_e);
								if (!StringUtils.isEmpty(color)) {
									color = color.replaceAll("[^\\p{L}\\p{Z}\\p{N}]", " ");
									color = color.trim();
									break;
								}
							}
						}
					}

					for (Element e : tmp) {
						String s = e.text();
						int ind_s = s.indexOf("Internal Memory ");
						if (ind_s != -1) {
							int ind_e = s.indexOf("GB ", ind_s);
							if (ind_e != -1) {
								size = s.substring(ind_s + 15, ind_e);
								if (!StringUtils.isEmpty(size)) {
									size = size.replaceAll("[^\\p{L}\\p{Z}\\p{N}]", " ");
									size = size.trim() + "GB";
									break;
								}
							}
						}
					}
				}

				tmp = doc.select("table.product-spec");
				if (tmp != null) {
					for (Element ele : tmp) {
						Elements h1 = ele.select("td");
						if (h1 != null) {
							int len = h1.size();
							for (int i = 0; i < len; i = i + 2) {
								if (h1.get(i) != null && h1.get(i + 1) != null) {
									String key = h1.get(i).text();
									String val = h1.get(i + 1).text();
									if (snapdealSet.contains(key)) {
										features.add((key + " : " + val).toUpperCase());
									}
									if ("Colour".equalsIgnoreCase(key)) {
										color = val;
									}
									if ("Internal Memory".equalsIgnoreCase(key)) {
										size = val.replaceAll("\\s", "");
									}
								}
							}
						}
					}
				}
				dto.setFeatures(features);
				if (StringUtils.isNotBlank(size)) {
					Map<String, String> prop = dto.getProperties();
					if (prop == null) {
						dto.setProperties(new HashMap<String, String>());
						prop = dto.getProperties();
					}
					if (dto.getSubCategoryName().equalsIgnoreCase(CategoryEnum.SMARTPHONES.name())) {
						if (!prop.containsKey("SIZE") || StringUtils.isBlank(prop.get("SIZE")) || prop.get("SIZE") == null
								|| prop.get("SIZE").equalsIgnoreCase("null")) {
							String[] array = size.split(",");
							if (array.length > 0) {
								String val = array[0];
								if (StringUtils.isNotBlank(val)) {
									prop.put("SIZE", retrieveSize(val.trim()));
								}
							}
						}
					}
				}
				if (StringUtils.isBlank(dto.getColor()) && StringUtils.isNotBlank(color)) {
					dto.setColor(color.toUpperCase().trim());
					return true;
				}
			}
		}
		return false;
	}

	public boolean getInfoFlipkart(ReviewedProductInfoDTO dto) {
		String url = dto.getProductUrl();
		if (!StringUtils.isEmpty(url)) {
			if (url != null && url.lastIndexOf('&') > 0) {
				url = url.substring(0, url.lastIndexOf('&'));
			}
			// log.info("FLIPKART : {}", url);
			Document doc = null;
			try {
				if (StringUtils.isNotBlank(url)){
					doc = urlCall.execute(url, "http://www.flipkart.com/");
				}
			} catch (Exception e) {
				msgLog.addError(e);
			}
			if (doc != null) {
				String color = null;
				String size = null;
				List<String> features = new LinkedList<String>();

				Elements tmp = doc.select("h1.title");
				Element element = null;
				tmp = doc.select("span.subtitle");
				if (tmp != null && tmp.size() > 0) {
					element = tmp.get(0);
					if (element != null) {
						String subtitle = element.text();
						subtitle = subtitle.replaceAll("\\(|\\)", "");
						String[] prop = subtitle.split(", ");
						if (prop.length > 0) {
							color = prop[0].trim();
						}
						if (prop.length > 1) {
							size = prop[prop.length - 1].trim();
							size = size.replaceAll("\\s", "");
						}
					}
				}

				tmp = doc.select("table.specTable");
				if (tmp != null) {
					for (Element ele : tmp) {
						Elements h1 = ele.select("td.specsKey");
						Elements h2 = ele.select("td.specsValue");
						if (h1 != null && h2 != null) {
							int len = h1.size();
							int len2 = h2.size();
							for (int i = 0; i < len && i < len2; i++) {
								if (h1.get(i) != null && h2.get(i) != null) {
									String key = h1.get(i).text();
									String val = h2.get(i).text();
									if (flipkartSet.contains(key)) {
										if ("Memory".equalsIgnoreCase(key)) {
											if (dto.getSubCategoryName().equalsIgnoreCase(CategoryEnum.SMARTPHONES.name())) {
												key = key.split(",")[0];
											}
										}
										if ("Type".equalsIgnoreCase(key)) {
											if (dto.getSubCategoryName().equalsIgnoreCase(CategoryEnum.SMARTPHONES.name())) {
												key = "Battery";
											}
										}
										features.add((key + " : " + val).toUpperCase());
									}
									if ("Handset Color".equalsIgnoreCase(key) || "Color".equalsIgnoreCase(key)) {
										color = val;
									}
									if ("Internal".equalsIgnoreCase(key)) {
										size = val.replaceAll("\\s", "");
									}
								}
							}
						}
					}
				}

				dto.setFeatures(features);
				if (StringUtils.isNotBlank(size)) {
					Map<String, String> prop = dto.getProperties();
					if (prop == null) {
						dto.setProperties(new HashMap<String, String>());
						prop = dto.getProperties();
					}
					if (dto.getSubCategoryName().equalsIgnoreCase(CategoryEnum.SMARTPHONES.name())) {
						if (!prop.containsKey("SIZE") || StringUtils.isBlank(prop.get("SIZE")) || prop.get("SIZE") == null
								|| prop.get("SIZE").equalsIgnoreCase("null")) {
							String[] array = size.split(",");
							if (array.length > 0) {
								String val = array[0];
								if (StringUtils.isNotBlank(val)) {
									prop.put("SIZE", retrieveSize(val.replace(" ", "").trim()));
								}
							}
						}
					}
				}
				if (StringUtils.isBlank(dto.getColor()) && StringUtils.isNotBlank(color)) {
					dto.setColor(color.toUpperCase().trim());
					return true;
				}
			}
		}
		return false;
	}

	private String retrieveSize(String title) {
		title = title.replace(" ", "");
		int size = 0;
		boolean isMb = false;
		String ext = "GB";
		List<String> allMatches = new LinkedList<String>();
		Pattern pattern = Pattern.compile("\\d+([GM]B)");

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
					if (isMb) {
						ext = "MB";
					}
					else {
						ext = "GB";
					}
				}
			}
		}
		if (size == 0) {
			return null;
		}
		if ("GB".equalsIgnoreCase(ext)) {
			size = size / 1024;
		}
		return String.valueOf(size) + ext;
	}
}
