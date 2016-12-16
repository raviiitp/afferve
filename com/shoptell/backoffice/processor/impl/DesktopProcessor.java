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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

@Named
public class DesktopProcessor extends DataProcessor {

	public static final String COLOR_REGEX = "("
	/* 10 */+ "BLACK ONYX|BLACK TITAN|CHAMPANGNE|ONYX BLACK|"
	/* 9 */+ "ALABASTER|BALLISTIC|CHAMPAGNE|CHAMPANGE|CHOCOLATE|SANDSTONE|"
	/* 8 */+ "CHARCOAL|GRAPHITE|GUNMETAL|MAGNETIC|MILKYWAY|MOONDUST|TITANIUM|"
	/* 7 */+ "CERAMIC|CHESTNUT|LEATHER|MAGENTA|"
	/* 6 */+ "BRONZE|CARBON|CHROME|COFFEE|COPPER|FERVOR|GOLDEN|MARBLE|ORANGE|PEBBLE|PURPLE|SILVER|YELLOW|VIOLET|"
	/* 5 */+ "BLACK|BLUSH|BROWN|CORAL|GREEN|MILKY|PEARL|SLATE|STEEL|WHITE|"
	/* 4 */+ "BLUE|CHIC|CYAN|GOLD|GRAY|GREY|PINK|PURE|ROSE|SNOW|WINE){1,3}";
	// AQUA|

	private static final String INTEL_PROCESSOR_REGEX = "CENTRINO|CELERON|PENTIUM|ATOM|QUAD|DUO|I9|I7|I5|I3";

	private static final String AMD_PROCESSOR_REGEX = "SEMPRON|PHENOM|ATHLON|TURION|APU|FX|A8|A6|A4";
	
	private static final String FLIPKART_MODEL_REGEX = "INTEL|DUAL";

	private ReviewedProductInfoDTO processorUtil(HomeProductInfoDTO prod) {
		
		DesktopProperties desktopProperties = new DesktopProperties();

		String desc = prod.getDescription();

		if (StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(processTitle(prod.getTitle()));
		}

		if (StringUtils.isNotBlank(desc)) {
			prod.setDescription(processDescription(desc));
		}

		if(StringUtils.isNotBlank(prod.getProductBrand())){
			if(StringUtils.isNotBlank(prod.getTitle())){
				prod.setTitle(prod.getTitle().replace(prod.getProductBrand(), ""));
			}
			prod.setProductBrand(prod.getProductBrand().toUpperCase().trim());
			desktopProperties.setProduct_brand(prod.getProductBrand());
		}
		
		retrieveMemory(prod, desktopProperties); /*
												 * internal, ram and graphic
												 * memory; hard disk memory type
												 */
		
		retrieveColor(prod, desktopProperties); /* BLACK, WHITE */

		retrieveScreenSize(prod, desktopProperties); /* 17.3" */

		retrieveGeneration(prod, desktopProperties); /* 3RD GEN */

		retrieveRAMType(prod, desktopProperties); /* DDR3 DDR4 */
		
		retrieveProcessor(prod, desktopProperties); /* processor like i5 */
		
		retrieveOS(prod, desktopProperties); /* operating system */

		prod.setDescription(desc);

		if (StringUtils.isNotBlank(desktopProperties.getInternal_memory())) {
			return populateDto(prod, desktopProperties);
		}
		else {
			return null;
		}
	}

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
		return processorUtil(prod);
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		return processorUtil(prod);
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		return processorUtil(prod);
	}

	private String processTitle(String title) {
		title = title.replaceAll("\\/|\\(|\\)|\\-|\\,|\\+", " ").trim();
		title = title.replaceAll("\\s+", " ").trim();
		return title;
	}

	private String processDescription(String desc) {
		desc = desc.replaceAll("\\/|\\(|\\)|\\-|\\,|\\+", " ").trim();
		desc = desc.replaceAll("\\s+", " ").trim();
		return desc;
	}

	private String retrieveColorUtil(String target, DesktopProperties desktopProperties) {
		String color = null;
		if (StringUtils.isNotBlank(target)) {
			Pattern color_pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = color_pattern.matcher(target);
			while (matcher.find()) {
				if (isMatchPresent(matcher, 0)) {
					color = matcher.group(0);
					int index = target.indexOf(matcher.group(0));
					if (index != -1) {
						target = target.substring(0, index).trim();
					}
					break;
				}
			}
			if (StringUtils.isNotBlank(color)) {
				color = color.trim().replace("(", "");
			}
		}
		if (StringUtils.isNotBlank(color)) {
			desktopProperties.setColor(color.toUpperCase().trim());
		}

		return target;
	}

	private void retrieveColor(HomeProductInfoDTO prod, DesktopProperties desktopProperties) {

		if (StringUtils.isBlank(desktopProperties.getColor())) {
			String color = prod.getColor();
			if (StringUtils.isNotBlank(color)) {
				color = color.replaceAll("\\/|\\(|\\)|\\-|\\,|\\+", " ").trim();
				color = color.replaceAll("\\s+", " ").trim();
				retrieveColorUtil(color, desktopProperties);
			}
		}

		if (StringUtils.isBlank(desktopProperties.getColor())) {
			if (StringUtils.isNotBlank(prod.getTitle())) {
				prod.setTitle(retrieveColorUtil(prod.getTitle(), desktopProperties));
			}
		}

		if (StringUtils.isBlank(desktopProperties.getColor())) {
			if (StringUtils.isNotBlank(prod.getDescription())) {
				prod.setDescription(retrieveColorUtil(prod.getDescription(), desktopProperties));
			}
		}

		return;
	}

	private String retrieveMemoryUtil(String target, String brand, DesktopProperties desktopProperties, boolean fromSize) {
		List<String> allMatches = new LinkedList<String>();
		int[] sizeInNum = null;

		String[] size_as_string = new String[3]; // 0: hdd; 1: ram; 2: graphics
		String size_in_ssd = StringUtils.EMPTY, size_in_sshd = StringUtils.EMPTY, size_in_emmc = StringUtils.EMPTY;

		Pattern hdd_pattern;
		if(fromSize){
			hdd_pattern = Pattern.compile("\\d+([TGM]B\\*?\\d?)");
		}else{
			hdd_pattern = Pattern.compile("\\s+\\d+([TGM]B\\*?\\d?)\\s+");
		}

		Pattern ssd_pattern;
		if(fromSize){
			ssd_pattern = Pattern.compile("\\d+([TGM]BSSD)");
		}else{
			ssd_pattern = Pattern.compile("\\s+\\d+([TGM]BSSD)\\s+");
		}
		
		Pattern sshd_pattern;
		if(fromSize){
			sshd_pattern = Pattern.compile("\\d+([TGM]BSSHD)");
		}else{
			sshd_pattern = Pattern.compile("\\s+\\d+([TGM]BSSHD)\\s+");
		}
		
		Pattern emmc_pattern;
		if(fromSize){
			emmc_pattern = Pattern.compile("\\d+([TGM]BEMMC)");
		}else{
			emmc_pattern = Pattern.compile("\\s+\\d+([TGM]BEMMC)\\s+");
		}

		boolean apple = StringUtils.equalsIgnoreCase(brand, "apple");

		target = target.replaceAll("FLASH", "SSD").replaceAll("\\s+TB", "TB").replaceAll("\\s+GB", "GB").replaceAll("\\s+MB", "MB")
				.replaceAll("\\s+SSD", "SSD").replaceAll("\\s+SSHD", "SSHD").replaceAll("\\s+EMMC", "EMMC").replaceAll("GBRAM", "GB RAM");

		Matcher matcher = null;

		matcher = null;
		matcher = ssd_pattern.matcher(target);
		if (matcher.find()) {
			size_in_ssd = matcher.group().replace(" ", "");
			target = target.replace(size_in_ssd, " ");
			size_in_ssd = size_in_ssd.replace("SSD", " SSD");
		}

		matcher = null;
		matcher = sshd_pattern.matcher(target);
		if (matcher.find()) {
			size_in_sshd = matcher.group().replace(" ", "");
			target = target.replace(size_in_sshd, " ");
			size_in_sshd = size_in_sshd.replace("SSHD", " SSHD");
		}
		
		matcher = null;
		matcher = emmc_pattern.matcher(target);
		if (matcher.find()) {
			size_in_emmc = matcher.group().replace(" ", "");
			target = target.replace(size_in_emmc, " ");
			size_in_emmc = size_in_emmc.replace("EMMC", " EMMC");
		}

		matcher = null;
		matcher = hdd_pattern.matcher(target);
		while (matcher.find()) {
			allMatches.add(matcher.group().replace(" ", ""));
			target = StringUtils.replaceOnce(target, matcher.group(), " ");
			matcher = hdd_pattern.matcher(target);
		}
		sizeInNum = new int[allMatches.size()];

		for (int ii = 0; ii < allMatches.size(); ii++) {
			int multiplier = 1;
			target = target.replace(allMatches.get(ii), " ");

			if (StringUtils.contains(allMatches.get(ii), '*')) {
				String[] _sizeToken = StringUtils.split(allMatches.get(ii), '*');
				if (_sizeToken.length == 2) {
					if (StringUtils.isNumeric(_sizeToken[1])) {
						try {
							multiplier = Integer.parseInt(_sizeToken[1]);
						} catch (Exception e) {
						}
					}
					allMatches.set(ii, _sizeToken[0]);
				}
			}

			String tmp = allMatches.get(ii).substring(0, allMatches.get(ii).length() - 2);
			int _sizeInNum = 0;
			if (StringUtils.isNumeric(tmp)) {
				try {
					_sizeInNum = Integer.parseInt(tmp);
				} catch (Exception e) {
				}

				if (allMatches.get(ii).endsWith("TB")) {
					sizeInNum[ii] = _sizeInNum * multiplier * 1024 * 1024;
				}
				else if (allMatches.get(ii).endsWith("GB")) {
					sizeInNum[ii] = _sizeInNum * multiplier * 1024;
				}
				else if (allMatches.get(ii).endsWith("MB")) {
					sizeInNum[ii] = _sizeInNum * multiplier;
				}
			}
		}
		target = target.trim().replaceAll("\\s+", " ");

		Arrays.sort(sizeInNum);

		int sizeInNum_length = sizeInNum.length;
		if (sizeInNum_length > 2) {

			/* there may be duplicate data, remove those */
			int[] unq_mem = { 0, 0, 0 };
			int prevUnqMem = 0;
			for (int jj = sizeInNum_length - 1, kk = 2; jj >= 0 && kk >= 0;) {
				if (kk == jj) {
					for (int jjj = 0; jjj <= jj; jjj++) {
						unq_mem[jjj] = sizeInNum[jjj];
					}
					break;
				}
				else if (sizeInNum[jj] != prevUnqMem) {
					if(jj != sizeInNum_length - 1 && sizeInNum[jj] > (32 * 1024)){
						if(target.contains("SSD")){
							size_in_ssd = sizeInNum[jj] >= 1048576 ? (sizeInNum[jj] / 1048576) + "TB SSD"
									: (sizeInNum[jj] >= 1024 ? (sizeInNum[jj] / 1024) + "GB SSD" : sizeInNum[jj]
											+ "MB SSD");
						}else if(target.contains("SSHD")){
							size_in_sshd = sizeInNum[jj] >= 1048576 ? (sizeInNum[jj] / 1048576) + "TB SSHD"
									: (sizeInNum[jj] >= 1024 ? (sizeInNum[jj] / 1024) + "GB SSHD" : sizeInNum[jj]
											+ "MB SSHD");
						}
					} else {
						prevUnqMem = sizeInNum[jj];
						unq_mem[kk] = sizeInNum[jj];
						kk--;
					}
					jj--;
				}
				else {
					jj--;
				}
			}
			sizeInNum[0] = unq_mem[0];
			sizeInNum[1] = unq_mem[1];
			sizeInNum[2] = unq_mem[2];

			// 0: hdd; 1: ram; 2: graphics
			sizeInNum_length = 3;
			if (apple && StringUtils.isBlank(size_in_ssd)) {
				size_in_ssd = sizeInNum[sizeInNum_length - 1] >= 1048576 ? (sizeInNum[sizeInNum_length - 1] / 1048576) + "TB SSD"
						: (sizeInNum[sizeInNum_length - 1] >= 1024 ? (sizeInNum[sizeInNum_length - 1] / 1024) + "GB SSD" : sizeInNum[sizeInNum_length - 1]
								+ "MB SSD");
				size_as_string[0] = StringUtils.EMPTY;
			}
			else {
				size_as_string[0] = sizeInNum[sizeInNum_length - 1] >= 1048576 ? (sizeInNum[sizeInNum_length - 1] / 1048576) + "TB"
						: (sizeInNum[sizeInNum_length - 1] >= 1024 ? (sizeInNum[sizeInNum_length - 1] / 1024) + "GB" : sizeInNum[sizeInNum_length - 1] + "MB");
				desktopProperties.setHard_disk_type("HDD");
			}

			size_as_string[1] = sizeInNum[sizeInNum_length - 2] >= 1048576 ? (sizeInNum[sizeInNum_length - 2] / 1048576) + "TB"
					: (sizeInNum[sizeInNum_length - 2] >= 1024 ? (sizeInNum[sizeInNum_length - 2] / 1024) + "GB" : sizeInNum[sizeInNum_length - 2] + "MB");
			size_as_string[2] = sizeInNum[sizeInNum_length - 3] >= 1048576 ? (sizeInNum[sizeInNum_length - 3] / 1048576) + "TB"
					: (sizeInNum[sizeInNum_length - 3] >= 1024 ? (sizeInNum[sizeInNum_length - 3] / 1024) + "GB" : sizeInNum[sizeInNum_length - 3] + "MB");

		}
		else if (sizeInNum_length == 2) {
			boolean hdd = true;
			if (sizeInNum[sizeInNum_length - 1] <= 32768) {
				hdd = false;
			}
			if (hdd) {
				if (apple && StringUtils.isBlank(size_in_ssd)) {
					size_in_ssd = sizeInNum[sizeInNum_length - 1] >= 1048576 ? (sizeInNum[sizeInNum_length - 1] / 1048576) + "TB SSD"
							: (sizeInNum[sizeInNum_length - 1] >= 1024 ? (sizeInNum[sizeInNum_length - 1] / 1024) + "GB SSD" : sizeInNum[sizeInNum_length - 1]
									+ "MB SSD");
					size_as_string[0] = StringUtils.EMPTY;
				}
				else {
					size_as_string[0] = sizeInNum[sizeInNum_length - 1] >= 1048576 ? (sizeInNum[sizeInNum_length - 1] / 1048576) + "TB"
							: (sizeInNum[sizeInNum_length - 1] >= 1024 ? (sizeInNum[sizeInNum_length - 1] / 1024) + "GB" : sizeInNum[sizeInNum_length - 1]
									+ "MB");
					desktopProperties.setHard_disk_type("HDD");
				}
				size_as_string[1] = sizeInNum[sizeInNum_length - 2] >= 1048576 ? (sizeInNum[sizeInNum_length - 2] / 1048576) + "TB"
						: (sizeInNum[sizeInNum_length - 2] >= 1024 ? (sizeInNum[sizeInNum_length - 2] / 1024) + "GB" : sizeInNum[sizeInNum_length - 2] + "MB");
				size_as_string[2] = StringUtils.EMPTY;

			}
			else {
				size_as_string[0] = StringUtils.EMPTY;
				size_as_string[1] = sizeInNum[sizeInNum_length - 1] >= 1048576 ? (sizeInNum[sizeInNum_length - 1] / 1048576) + "TB"
						: (sizeInNum[sizeInNum_length - 1] >= 1024 ? (sizeInNum[sizeInNum_length - 1] / 1024) + "GB" : sizeInNum[sizeInNum_length - 1] + "MB");
				size_as_string[2] = sizeInNum[sizeInNum_length - 2] >= 1048576 ? (sizeInNum[sizeInNum_length - 2] / 1048576) + "TB"
						: (sizeInNum[sizeInNum_length - 2] >= 1024 ? (sizeInNum[sizeInNum_length - 2] / 1024) + "GB" : sizeInNum[sizeInNum_length - 2] + "MB");
			}
		}
		else if (sizeInNum_length == 1) {
			boolean hdd = true;
			if (sizeInNum[sizeInNum_length - 1] < 32768) {
				hdd = false;
			}
			if (hdd) {
				if (apple && StringUtils.isBlank(size_in_ssd)) {
					size_in_ssd = sizeInNum[sizeInNum_length - 1] >= 1048576 ? (sizeInNum[sizeInNum_length - 1] / 1048576) + "TB SSD"
							: (sizeInNum[sizeInNum_length - 1] >= 1024 ? (sizeInNum[sizeInNum_length - 1] / 1024) + "GB SSD" : sizeInNum[sizeInNum_length - 1]
									+ "MB SSD");
					size_as_string[0] = StringUtils.EMPTY;
				}
				else {
					size_as_string[0] = sizeInNum[sizeInNum_length - 1] >= 1048576 ? (sizeInNum[sizeInNum_length - 1] / 1048576) + "TB"
							: (sizeInNum[sizeInNum_length - 1] >= 1024 ? (sizeInNum[sizeInNum_length - 1] / 1024) + "GB" : sizeInNum[sizeInNum_length - 1]
									+ "MB");
					desktopProperties.setHard_disk_type("HDD");
				}
				size_as_string[1] = StringUtils.EMPTY;
				size_as_string[2] = StringUtils.EMPTY;

			}
			else {
				size_as_string[0] = StringUtils.EMPTY;
				size_as_string[1] = sizeInNum[sizeInNum_length - 1] >= 1048576 ? (sizeInNum[sizeInNum_length - 1] / 1048576) + "TB"
						: (sizeInNum[sizeInNum_length - 1] >= 1024 ? (sizeInNum[sizeInNum_length - 1] / 1024) + "GB" : sizeInNum[sizeInNum_length - 1] + "MB");
				size_as_string[2] = StringUtils.EMPTY;
			}
		}
		else {
			size_as_string = null;
		}

		// add ssd, sshd or emmc to hdd
		if (size_as_string != null) {
			if (StringUtils.isNotBlank(size_in_ssd)) {
				size_as_string[0] = (StringUtils.isNotBlank(size_as_string[0]) ? size_as_string[0] + " + " : "") + size_in_ssd;
				desktopProperties.setHard_disk_type(((StringUtils.isNotBlank(desktopProperties.getHard_disk_type()) && !"SSD".equalsIgnoreCase(desktopProperties.getHard_disk_type()))  ? desktopProperties.getHard_disk_type() + " "
						: "") + "SSD");
			}
			if (StringUtils.isNotBlank(size_in_sshd)) {
				size_as_string[0] = (StringUtils.isNotBlank(size_as_string[0]) ? size_as_string[0] + " + " : "") + size_in_sshd;
				desktopProperties.setHard_disk_type(((StringUtils.isNotBlank(desktopProperties.getHard_disk_type()) && !"SSHD".equalsIgnoreCase(desktopProperties.getHard_disk_type()))  ? desktopProperties.getHard_disk_type() + " "
						: "") + "SSHD");
			}
			if (StringUtils.isNotBlank(size_in_emmc)) {
				size_as_string[0] = (StringUtils.isNotBlank(size_as_string[0]) ? size_as_string[0] + " + " : "") + size_in_emmc;
				desktopProperties.setHard_disk_type(((StringUtils.isNotBlank(desktopProperties.getHard_disk_type()) && !"EMMC".equalsIgnoreCase(desktopProperties.getHard_disk_type()))  ? desktopProperties.getHard_disk_type() + " "
						: "") + "EMMC");
			}

			if (StringUtils.isBlank(desktopProperties.getInternal_memory()) && StringUtils.isNotBlank(size_as_string[0])) {
				desktopProperties.setInternal_memory(size_as_string[0].toUpperCase().trim());
			}
			if (StringUtils.isBlank(desktopProperties.getRAM_memory()) && StringUtils.isNotBlank(size_as_string[1])) {
				desktopProperties.setRAM_memory(size_as_string[1].toUpperCase().trim());
			}
			if (StringUtils.isBlank(desktopProperties.getGraphic_memory()) && StringUtils.isNotBlank(size_as_string[2])) {
				desktopProperties.setGraphic_memory(size_as_string[2].toUpperCase().trim());
			}
		} else{
			if (StringUtils.isBlank(desktopProperties.getInternal_memory()) && StringUtils.isNotBlank(size_in_ssd)) {
				desktopProperties.setInternal_memory(size_in_ssd.toUpperCase().trim());
				desktopProperties.setHard_disk_type("SSD");
			}
			if (StringUtils.isBlank(desktopProperties.getInternal_memory()) && StringUtils.isNotBlank(size_in_sshd)) {
				desktopProperties.setInternal_memory(size_in_sshd.toUpperCase().trim());
				desktopProperties.setHard_disk_type("SSHD");
			}
			if (StringUtils.isBlank(desktopProperties.getInternal_memory()) && StringUtils.isNotBlank(size_in_emmc)) {
				desktopProperties.setInternal_memory(size_in_emmc.toUpperCase().trim());
				desktopProperties.setHard_disk_type("EMMC");
			}
		}

		return target;
	}

	private void retrieveMemory(HomeProductInfoDTO prod, DesktopProperties desktopProperties) {
		if (StringUtils.isBlank(desktopProperties.getInternal_memory()) || StringUtils.isBlank(desktopProperties.getRAM_memory())
				|| StringUtils.isBlank(desktopProperties.getGraphic_memory())) {
			String size = prod.getSize();
			if (StringUtils.isNotBlank(size)) {
				size = size.replaceAll("\\/|\\(|\\)|\\-|\\,|\\+", " ").trim();
				size = size.replaceAll("\\s+", " ").trim();
				retrieveMemoryUtil(size, prod.getProductBrand(), desktopProperties, true);
			}
		}

		if (StringUtils.isBlank(desktopProperties.getInternal_memory()) || StringUtils.isBlank(desktopProperties.getRAM_memory())
				|| StringUtils.isBlank(desktopProperties.getGraphic_memory())) {
			if (StringUtils.isNotBlank(prod.getTitle())) {
				prod.setTitle(retrieveMemoryUtil(prod.getTitle(), prod.getProductBrand(), desktopProperties, false));
			}
		}

		/* description messes with graphic memory */
		/*
		 * if (StringUtils.isBlank(desktopProperties.getInternal_memory()) ||
		 * StringUtils.isBlank(desktopProperties.getRAM_memory()) ||
		 * StringUtils.isBlank(desktopProperties.getGraphic_memory())) { if
		 * (StringUtils.isNotBlank(prod.getDescription())) {
		 * prod.setDescription(retrieveMemoryUtil(prod.getDescription(),
		 * prod.getProductBrand(), desktopProperties)); } }
		 */
	}

	private String retrieveOSUtil(String target, String brand, DesktopProperties desktopProperties) {

		Pattern win_pattern = Pattern.compile("WIN(DOWS)?\\s*\\d+\\.?\\d?");
		Pattern ubuntu_pattern = Pattern.compile("(UBUNTU|LINUX)");
		Pattern dos_pattern = Pattern.compile("DOS");

		String os = StringUtils.equalsIgnoreCase(brand, "APPLE") ? "MAC OS" : null;

		Matcher matcher = null;

		if (StringUtils.isBlank(os)) {
			matcher = null;
			matcher = win_pattern.matcher(target);
			if (matcher.find()) {
				os = matcher.group();
				target = target.replace(os, " ");
				os = os.replaceFirst("WIN(DOWS?)?\\s*", "WIN ");
			}
		}

		if (StringUtils.isBlank(os)) {
			matcher = null;
			matcher = ubuntu_pattern.matcher(target);
			if (matcher.find()) {
				os = matcher.group();
				target = target.replace(os, " ");
				os = "UBUNTU";
			}
		}

		if (StringUtils.isBlank(os)) {
			matcher = null;
			matcher = dos_pattern.matcher(target);
			if (matcher.find()) {
				os = matcher.group();
				target = target.replace(os, " ");
			}
		}

		if (StringUtils.isNotBlank(os)) {
			desktopProperties.setOperatingSystem(os.toUpperCase().trim());
		}

		return target;
	}

	private void retrieveOS(HomeProductInfoDTO prod, DesktopProperties desktopProperties) {

		if (StringUtils.isBlank(desktopProperties.getOperatingSystem())) {
			if (StringUtils.isNotBlank(prod.getTitle())) {
				prod.setTitle(retrieveOSUtil(prod.getTitle(), prod.getProductBrand(), desktopProperties));
			}
		}

		if (StringUtils.isBlank(desktopProperties.getOperatingSystem())) {
			if (StringUtils.isNotBlank(prod.getDescription())) {
				prod.setDescription(retrieveOSUtil(prod.getDescription(), prod.getProductBrand(), desktopProperties));
			}
		}

		return;
	}

	private String retrieveProcessorUtil(String target, String regex, DesktopProperties desktopProperties) {

		String processor = null;

		if (StringUtils.isNotBlank(target)) {
			Pattern precessor_pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = precessor_pattern.matcher(target);
			if (matcher.find()) {
				processor = matcher.group();
				target = target.replace(processor, " ");
				//processor += " CORE";
			}
		}
		if (StringUtils.isNotBlank(processor)) {
			desktopProperties.setProcessor(processor.toUpperCase().trim());
		}

		return target;
	}

	private void retrieveProcessor(HomeProductInfoDTO prod, DesktopProperties desktopProperties) {
		if (StringUtils.isBlank(desktopProperties.getProcessor()) && StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(retrieveProcessorUtil(prod.getTitle(), AMD_PROCESSOR_REGEX, desktopProperties));
			if (StringUtils.isNotBlank(desktopProperties.getProcessor())) {
				desktopProperties.setProcessor("AMD " + desktopProperties.getProcessor().toUpperCase().trim());
			}
		}

		if (StringUtils.isBlank(desktopProperties.getProcessor()) && StringUtils.isNotBlank(prod.getTitle())) {
			prod.setTitle(retrieveProcessorUtil(prod.getTitle(), INTEL_PROCESSOR_REGEX, desktopProperties));
			if (StringUtils.isNotBlank(desktopProperties.getProcessor())) {
				desktopProperties.setProcessor("INTEL " + desktopProperties.getProcessor().toUpperCase().trim());
			}
		}

		if (StringUtils.isBlank(desktopProperties.getProcessor()) && StringUtils.isNotBlank(prod.getDescription())) {
			prod.setDescription(retrieveProcessorUtil(prod.getDescription(), AMD_PROCESSOR_REGEX, desktopProperties));
			if (StringUtils.isNotBlank(desktopProperties.getProcessor())) {
				desktopProperties.setProcessor("AMD " + desktopProperties.getProcessor().toUpperCase().trim());
			}
		}

		if (StringUtils.isBlank(desktopProperties.getProcessor()) && StringUtils.isNotBlank(prod.getDescription())) {
			prod.setDescription(retrieveProcessorUtil(prod.getDescription(), INTEL_PROCESSOR_REGEX, desktopProperties));
			if (StringUtils.isNotBlank(desktopProperties.getProcessor())) {
				desktopProperties.setProcessor("INTEL " + desktopProperties.getProcessor().toUpperCase().trim());
			}
		}
	}

	private String retrieveScreenSizeUtil(String target, DesktopProperties desktopProperties) {
		Pattern cm_pattern = Pattern.compile("\\s+\\d+\\.?\\d*\\s*CM\\s+");
		Pattern inch_pattern = Pattern.compile("\\s+\\d+\\.?\\d*\\s*(INCH(ES)?|\")\\s+");

		String screenSize = null;

		Matcher matcher = null;

		matcher = null;
		matcher = cm_pattern.matcher(target);
		if (matcher.find()) {
			screenSize = matcher.group().replace(" ", "");
			target = target.replace(screenSize, " ");
			screenSize = screenSize.replaceFirst("\\s*CM", "CM");
			String tmp = screenSize.substring(0, screenSize.length() - 2);
			try {
				double _screenSize = Double.parseDouble(tmp);
				_screenSize *= 0.393701;
				screenSize = String.valueOf((Math.ceil(_screenSize * 10)) / 10) + "\"";
			} catch (Exception e) {
			}
		}

		matcher = null;
		matcher = inch_pattern.matcher(target);
		if (matcher.find()) {
			screenSize = matcher.group();
			target = target.replace(screenSize, " ");
			screenSize = screenSize.replaceFirst("\\s*INCH(ES)?", "\"");
		}

		if (StringUtils.isNotBlank(screenSize)) {
			desktopProperties.setScreenSize(screenSize.toUpperCase().trim());
		}

		return target;
	}

	private void retrieveScreenSize(HomeProductInfoDTO prod, DesktopProperties desktopProperties) {

		/*if (StringUtils.isBlank(desktopProperties.getScreenSize())) {
			String size = prod.getSize();
			if (StringUtils.isNotBlank(prod.getSize())) {
				size = size.replaceAll("\\/|\\(|\\)|\\-|\\,|\\+", " ").trim();
				size = size.replaceAll("\\s+", " ").trim();
				retrieveScreenSizeUtil(size, desktopProperties);
			}
		}*/

		if (StringUtils.isBlank(desktopProperties.getScreenSize())) {
			if (StringUtils.isNotBlank(prod.getTitle())) {
				prod.setTitle(retrieveScreenSizeUtil(prod.getTitle(), desktopProperties));
			}
		}

		if (StringUtils.isBlank(desktopProperties.getScreenSize())) {
			if (StringUtils.isNotBlank(prod.getDescription())) {
				prod.setDescription(retrieveScreenSizeUtil(prod.getDescription(), desktopProperties));
			}
		}

	}

	private String retrieveGenerationUtil(String target, DesktopProperties desktopProperties) {

		Pattern gen_pattern = Pattern.compile("\\d\\s*(ST|ND|RD|TH)\\s*GEN(ERATION)?");

		String generation = null;

		Matcher matcher = gen_pattern.matcher(target);
		if (matcher.find()) {
			generation = matcher.group();
			target = target.replace(generation, " ");
			generation = generation.replaceAll("\\s", "").replaceFirst("GEN(ERATION)?", " GEN");
		}

		if (StringUtils.isNotBlank(generation)) {
			desktopProperties.setGeneration(generation.toUpperCase().trim());
		}
		return target;
	}

	private void retrieveGeneration(HomeProductInfoDTO prod, DesktopProperties desktopProperties) {
		if (StringUtils.isBlank(desktopProperties.getGeneration())) {
			if (StringUtils.isNotBlank(prod.getTitle())) {
				prod.setTitle(retrieveGenerationUtil(prod.getTitle(), desktopProperties));
			}
		}

		if (StringUtils.isBlank(desktopProperties.getGeneration())) {
			if (StringUtils.isNotBlank(prod.getDescription())) {
				prod.setDescription(retrieveGenerationUtil(prod.getDescription(), desktopProperties));
			}
		}
	}

	private String retrieveRAMTypeUtil(String target, DesktopProperties desktopProperties) {
		Pattern ram_type_pattern = Pattern.compile("\\w?DDR\\d\\w?");

		String ram_type = null;

		Matcher matcher = ram_type_pattern.matcher(target);
		while (matcher.find()) {
			ram_type = matcher.group();
			if (ram_type.startsWith("DD")) {
				target = target.replace(ram_type, " ");
				desktopProperties.setRAM_type(ram_type.toUpperCase().trim());
				break;
			}
		}

		return target;
	}

	private void retrieveRAMType(HomeProductInfoDTO prod, DesktopProperties desktopProperties) {
		if (StringUtils.isBlank(desktopProperties.getRAM_type())) {
			if (StringUtils.isNotBlank(prod.getTitle())) {
				prod.setTitle(retrieveRAMTypeUtil(prod.getTitle(), desktopProperties));
			}
		}

		if (StringUtils.isBlank(desktopProperties.getRAM_type())) {
			if (StringUtils.isNotBlank(prod.getDescription())) {
				prod.setDescription(retrieveRAMTypeUtil(prod.getDescription(), desktopProperties));
			}
		}
	}

	String getModel_amazon (String _title){
		String _model = StringUtils.EMPTY;
		
		Pattern inch_pattern = Pattern.compile("\\d+\\.?\\d*\\s*\\-*\\s*(INCH(ES)?)");
		
		Matcher matcher = null;
		
		if(StringUtils.isNotBlank(_title)){
			
			matcher = null;
			matcher = inch_pattern.matcher(_title);
			if (matcher.find()) {
				String screenSize = matcher.group();
				_model = _title.split(screenSize)[0];
			}
		}
		
		if(StringUtils.isBlank(_model)){
			_model = StringUtils.EMPTY;
		}
		
		return _model;
	}
	
	String getModel_flipkart (String _title){
		String _model = StringUtils.EMPTY;
		
		if(StringUtils.isNotBlank(_title)){
			
			if (_title.contains("CORE")) {
				_model = _title.split("CORE")[0];
				
				if(StringUtils.isNotBlank(_model)){
					_model = _model.replaceAll(AMD_PROCESSOR_REGEX, "").replaceAll(INTEL_PROCESSOR_REGEX, "").replaceAll(FLIPKART_MODEL_REGEX, "");
				}
			}
		}
		
		if(StringUtils.isBlank(_model)){
			_model = StringUtils.EMPTY;
		}
		
		return _model;
	}
	
	String getModel_snapdeal (String _title){
		String _model = StringUtils.EMPTY;
		
		if(StringUtils.isNotBlank(_title)){
			
			if (_title.contains(")")) {
				String firstPart = _title.split("\\)")[0];
				
				if(StringUtils.isNotBlank(firstPart)){
					if(firstPart.contains("(")){
						String[] firstPartArray = firstPart.split("\\(");
						_model = firstPartArray[0];
						if(StringUtils.isNotBlank(firstPartArray[1]) && !firstPartArray[1].contains(" ")){
							_model = _model.concat(" ").concat(firstPartArray[1]);
						}
					}
				}
			}
		}
		
		if(StringUtils.isBlank(_model)){
			_model = StringUtils.EMPTY;
		}
		
		return _model;
	}
	
	private ReviewedProductInfoDTO populateDto(HomeProductInfoDTO prod, DesktopProperties desktopProperties) {
		String _model = StringUtils.EMPTY;
		String _title = prod.getOriginalTitle();

		String[] _titleToken;
		Set<String> modelSet = new HashSet<>();

		if(StringUtils.isNotBlank(_title)){
			
			if(StringUtils.isNotBlank(prod.getProductBrand())){
				_title = _title.replace(prod.getProductBrand(), "");
			}
			
			_title = _title.replace("ALL IN ONE", "ALL-IN-ONE");
			_title = _title.replaceAll("\\(|\\)|\\,", " ").replaceAll("\\s+", " ").trim();
			
			_titleToken = _title.split(" ");
			
			for(int ii = 0, jj = 0; ii < _titleToken.length && jj < 3; ii++){
				if(StringUtils.isNotBlank(_titleToken[ii])){
					if(!modelSet.contains(_titleToken[ii])){
						modelSet.add(_titleToken[ii]);
						_model = _model.concat(_titleToken[ii]).concat(" ");
						jj++;
					}
				}
			}
			_model = _model.toUpperCase().trim();
		}
		if(StringUtils.isBlank(_model)){
			_model = null;
		}else{
			_model = _model.replaceAll("\\(|\\)|\\,", " ").replaceAll("\\s+", " ").trim();
			_model = _model.toUpperCase();
		}

		String color = desktopProperties.getColor();
		if (StringUtils.isNotBlank(color)){
			color = color.toUpperCase().trim();
		}

		ReviewedProductInfoDTO info = new ReviewedProductInfoDTO(prod.getProductBrand(), null, null, _model, color,
				new HashMap<String, String>(), prod, null);

		info.getProperties().put("INTERNAL MEMORY",
				StringUtils.isNotBlank(desktopProperties.getInternal_memory()) ? desktopProperties.getInternal_memory() : "NULL");
		info.getProperties().put("RAM", StringUtils.isNotBlank(desktopProperties.getRAM_memory()) ? desktopProperties.getRAM_memory() : "NULL");
		info.getProperties().put("RAM TYPE", StringUtils.isNotBlank(desktopProperties.getRAM_type()) ? desktopProperties.getRAM_type() : "NULL");
		info.getProperties().put("MEMORY TYPE", StringUtils.isNotBlank(desktopProperties.getHard_disk_type()) ? desktopProperties.getHard_disk_type() : "NULL");
		info.getProperties()
				.put("GRAPHIC MEMORY", StringUtils.isNotBlank(desktopProperties.getGraphic_memory()) ? desktopProperties.getGraphic_memory() : "NULL");
		info.getProperties().put("SCREEN SIZE", StringUtils.isNotBlank(desktopProperties.getScreenSize()) ? desktopProperties.getScreenSize() : "NULL");
		info.getProperties().put("O/S", StringUtils.isNotBlank(desktopProperties.getOperatingSystem()) ? desktopProperties.getOperatingSystem() : "NULL");
		info.getProperties().put("PROCESSOR", StringUtils.isNotBlank(desktopProperties.getProcessor()) ? desktopProperties.getProcessor() : "NULL");
		info.getProperties().put("GENERATION", StringUtils.isNotBlank(desktopProperties.getGeneration()) ? desktopProperties.getGeneration() : "NULL");

		return info;

}

	public class DesktopProperties {
		String product_brand = null;
		String product_sub_brand = null;
		String series = null;
		String model = null;
		String color = null;
		String screenSize = null;
		String RAM_memory = null;
		String RAM_type = null;
		String graphic_memory = null;
		String internal_memory = null;
		String hard_disk_type = null;
		String operatingSystem = null;
		String processor = null;
		String generation = null;

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

		public String getScreenSize() {
			return screenSize;
		}

		public void setScreenSize(String screenSize) {
			this.screenSize = screenSize;
		}

		public String getRAM_memory() {
			return RAM_memory;
		}

		public void setRAM_memory(String rAM_memory) {
			RAM_memory = rAM_memory;
		}

		public String getRAM_type() {
			return RAM_type;
		}

		public void setRAM_type(String rAM_type) {
			RAM_type = rAM_type;
		}

		public String getGraphic_memory() {
			return graphic_memory;
		}

		public void setGraphic_memory(String graphic_memory) {
			this.graphic_memory = graphic_memory;
		}

		public String getInternal_memory() {
			return internal_memory;
		}

		public void setInternal_memory(String internal_memory) {
			this.internal_memory = internal_memory;
		}

		public String getHard_disk_type() {
			return hard_disk_type;
		}

		public void setHard_disk_type(String hard_disk_type) {
			this.hard_disk_type = hard_disk_type;
		}

		public String getOperatingSystem() {
			return operatingSystem;
		}

		public void setOperatingSystem(String operatingSystem) {
			this.operatingSystem = operatingSystem;
		}

		public String getProcessor() {
			return processor;
		}

		public void setProcessor(String processor) {
			this.processor = processor;
		}

		public String getGeneration() {
			return generation;
		}

		public void setGeneration(String generation) {
			this.generation = generation;
		}

		@Override
		public String toString() {/*
								 * + ", s_brnd=" + product_sub_brand + ", srs="
								 * + series + ", mdl=" + model
								 */
			return "brnd=" + product_brand + ", clr=" + color + ", scrnSz=" + screenSize + ", rmMem=" + RAM_memory + ", rmTp=" + RAM_type + ", grpMem="
					+ graphic_memory + ", hdMem=" + internal_memory + ", hdType=" + hard_disk_type + ", os=" + operatingSystem + ", prcsr=" + processor
					+ ", gen=" + generation;
		}
	}
	
	/*public static void main(String[] args) {
		DesktopProcessor lp = new DesktopProcessor();
		HomeProductInfoDTO _hpi = new HomeProductInfoDTO();
		_hpi.setHome("AMAZON");
		_hpi.setTitle("Apple MacBook Air MJVE2HN/A 13-inch Desktop (Core i5/4GB/128GB/OS X Yosemite/Intel HD 6000)".toUpperCase());
		_hpi.setSize(null);
		_hpi.setProductBrand("APPLE".toUpperCase());
		_hpi.setOriginalTitle("Apple MacBook Air MJVE2HN/A 13-inch Desktop (Core i5/4GB/128GB/OS X Yosemite/Intel HD 6000)".toUpperCase());
		ReviewedProductInfoDTO rv =  lp.processorUtil(_hpi);
		
		if(rv != null){System.out.println(rv.toString());}
	}*/
}
