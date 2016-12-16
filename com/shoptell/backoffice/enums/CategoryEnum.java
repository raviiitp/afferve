/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public enum CategoryEnum {
	ALL(MetaCategoryEnum.NONE, "all",new String[]{},""),
	
	TABLET(MetaCategoryEnum.ELECTRONICS, "compare-tablet-prices", new String[]{"SIZE","CONNECTIVITY"},"TABLET","TABLETS"),
	SMARTPHONES (MetaCategoryEnum.ELECTRONICS, "compare-mobile-phone-prices",new String[]{"SIZE"},"SMARTPHONES","HANDSETS", "MOBILE PHONES", "SMART PHONES", "MOBILES"),
	
	LAPTOPS(MetaCategoryEnum.ELECTRONICS, "compare-laptop-prices", new String[]{"INTERNAL MEMORY","RAM","RAM TYPE","MEMORY TYPE","GRAPHIC MEMORY","SCREEN SIZE", "O/S", "PROCESSOR", "GENERATION"},"LAPTOPS"),
	DESKTOPS(MetaCategoryEnum.ELECTRONICS, "desktop-computers-price", new String[]{"INTERNAL MEMORY","RAM","RAM TYPE","MEMORY TYPE","GRAPHIC MEMORY","SCREEN SIZE", "O/S", "PROCESSOR", "GENERATION"},"DESKTOPS"),
	PENDRIVE(MetaCategoryEnum.ELECTRONICS, "compare-pendrive-prices", new String[]{"CAPACITY","INTERFACE", "PACK OF"},"PEN DRIVES"),
	
	WATCHES(MetaCategoryEnum.CLOTHING_ACCESSORIES, "compare-watch-prices", new String[]{"TYPE","IDEAL FOR","DIAL SHAPE","STRAP MATERIAL"},"WATCHES","WRIST WATCHES","WOMEN'S WATCHES","MEN'S WATCHES","KIDS WATCHES"),
	EYEWEARS(MetaCategoryEnum.CLOTHING_ACCESSORIES, "compare-eyewear-prices", new String[]{"FRAME TYPE","IDEAL FOR","FRAME SHAPE"},"EYEWEAR","EYEGLASSES","SPECTACLE FRAMES"),
	SUNGLASSES(MetaCategoryEnum.CLOTHING_ACCESSORIES, "compare-sunglasses-prices", new String[]{"IDEAL FOR","FRAME SHAPE"}, "SUNGLASSES"),
	
	WASHINGMACHINE(MetaCategoryEnum.HOME_APPLIANCES, "compare-washing-machine-prices",new String[]{"FUNCTION TYPE","LOAD TYPE", "CAPACITY", "TYPE"},"WASHINGMACHINE","WASHING_MACHINE","WASHING MACHINES & DRYERS"), 
	TELEVISION(MetaCategoryEnum.HOME_APPLIANCES, "compare-led-tv-prices",new String[]{"SCREEN SIZE", "RESOLUTION", "SCREEN TYPE", "TYPE"},"TELEVISION","TELEVISIONS"), 
	REFRIGERATOR(MetaCategoryEnum.HOME_APPLIANCES, "compare-refrigerator-prices",new String[]{"DOOR TYPE","TYPE","CAPACITY","ENERGY RATING"},"REFRIGERATOR","REFRIGERATORS"),
	AIRCONDITIONER(MetaCategoryEnum.HOME_APPLIANCES, "compare-air-conditioners",new String[]{"ENERGY RATING","CAPACITY","TYPE"},"AIRCONDITIONER","AIR_CONDITIONERS","AIR CONDITIONERS & COOLERS","AIR CONDITIONERS SPLIT AC","AIR CONDITIONERS WINDOW AC"),
	MICROWAVEOVEN(MetaCategoryEnum.HOME_APPLIANCES, "compare-microwave-oven-prices",new String[]{"CAPACITY","TYPE"},"MICROWAVEOVEN","MICROWAVE_OVENS", "MICROWAVE OVENS","MICROWAVE OVENS & OTGS","Microwaves"),
	
	BAGS(MetaCategoryEnum.LUGGAGE_BAGS, "compare-bags-prices",new String[]{"BAG TYPE","FEATURES","IDEAL FOR","MATERIAL","SIZE","CAPACITY","WATER PROOF", "CLOSURE"}, "BAGS", "LAPTOP BAGS", "BACKPACKS", "WOMENS HANDBAGS", "SCHOOL BAGS", "GYM BAGS", "UTILITY BAGS", "CLUTCHES", "MESSENGER BAGS"),
	LUGGAGEBAGS(MetaCategoryEnum.LUGGAGE_BAGS, "compare-luggage-bags-prices",new String[]{"BAG TYPE","FEATURES","IDEAL FOR","MATERIAL","SIZE","CAPACITY","WATER PROOF", "CLOSURE"}, "RUCKSACKS", "DUFFEL BAGS", "SUITCASES", "WAIST BAGS", "LUGGAGE", "TRAVEL BAGS", "HIKING BAGS & RUCKSACKS", "BRIEFCASES", "DUFFLES", "OVERNIGHTERS"),
	
	SPEAKERS (MetaCategoryEnum.ELECTRONICS, "compare-speaker-prices",new String[]{"TYPE","CONNECTIVITY"},"SPEAKERS","PC SPEAKERS"),
	HEADPHONES (MetaCategoryEnum.ELECTRONICS, "compare-headphone-prices",new String[]{"PRODUCT TYPE","TYPE","WIRED/WIRELESS","MIC"},"HEADPHONES & EARPHONES","HEADPHONES","BLUETOOTH HEADPHONES"),
	
	GAMINGCONSOLES(MetaCategoryEnum.VIDEO_GAMES, "compare-gaming-console-prices", new String[]{"CAPACITY", "TYPE"},"GAMING CONSOLES","CONSOLES"),
	
//	DEODORANTS(new String[]{"TYPE","IDEAL FOR", "QUANTITY"}),
//	SHAMPOOS(),
//	KAJAL(),
//	LIPSTICKS(),
//	NAILPOLISH("NAIL POLISHES"),
	
	//FACEWASH("compare-facewash-prices", new String[]{},"FACE WASH"),
	//CHOCOLATES(new String[]{},"Chocolate","Bars","Packets & Boxes","Chocolate Covered Nuts", "Chocolates, Mints & Candies"),
	//MONITORS(new String[]{"INTERNAL MEMORY","RAM","RAM TYPE","MEMORY TYPE","GRAPHIC MEMORY","SCREEN SIZE", "O/S", "PROCESSOR", "GENERATION"/*, "COLOR"*/},"MONITORS"),
	//BOOKS(new String[]{},"BOOKS"), 
	//WATERPURIFIER(new String[]{"PURIFICATION TECHNOLOGY", "CAPACITY", "ELECTRICAL TYPE"},"WATERPURIFIER"), 
	//AIRCOOLER(new String[]{"ENERGY RATING","CAPACITY","TYPE","COLOR"},"AIRCOOLER","AIR COOLERS"),
	
	CAMERA(MetaCategoryEnum.ELECTRONICS, "compare-cameras",new String[]{"MEGA PIXEL","CAMERA TYPE"},"CAMERA","CAMERAS","POINT & SHOOT DIGITAL CAMERAS","SPY CAMERAS","MIRRORLESS SYSTEM CAMERAS","INSTANT CAMERAS","DIGITAL SLRS","SLRS","DIGITAL CAMERAS","CAMCORDERS");
	
	private String[] property;
	private Set<String> match;
	private String seoName;
	private MetaCategoryEnum meta;
	
	private CategoryEnum(String[] property, String... match) {
		this.property = property;
		this.match = new HashSet<String>(Arrays.asList(match));
	}
	
	private CategoryEnum(MetaCategoryEnum meta, String seoName, String[] property, String... match) {
		this.setMeta(meta);
		this.setSeoName(seoName);
		this.property = property;
		this.match = new HashSet<String>(Arrays.asList(match));
	}
	
	public static String[] getProperties(String name) {
		if (StringUtils.isBlank(name)){
			return null;
		}
		for (CategoryEnum tmp : CategoryEnum.values()) {
			if (tmp.name().equalsIgnoreCase(name)){
				String[] array = tmp.getProperty();
				if (array != null && array.length > 0){
					return array;
				}
			}
		}
		return null;
	}
	
	public static CategoryEnum getCategory(String name) {
		if (StringUtils.isBlank(name)){
			return null;
		}
		for (CategoryEnum tmp : CategoryEnum.values()) {
			if (tmp.getMatch().contains(name.toUpperCase()) || tmp.name().equalsIgnoreCase(name.toUpperCase())) {
				return tmp;
			}
		}
		return null;
	}

	/**
	 * @return the property
	 */
	public String[] getProperty() {
		return property;
	}

	/**
	 * @param property the property to set
	 */
	public void setProperty(String[] property) {
		this.property = property;
	}

	/**
	 * @return the match
	 */
	public Set<String> getMatch() {
		return match;
	}

	/**
	 * @param match the match to set
	 */
	public void setMatch(Set<String> match) {
		this.match = match;
	}

	/**
	 * @return the seoName
	 */
	public String getSeoName() {
		return seoName;
	}

	/**
	 * @param seoName the seoName to set
	 */
	public void setSeoName(String seoName) {
		this.seoName = seoName;
	}

	/**
	 * @return the meta
	 */
	public MetaCategoryEnum getMeta() {
		return meta;
	}

	/**
	 * @param meta the meta to set
	 */
	public void setMeta(MetaCategoryEnum meta) {
		this.meta = meta;
	}
}
