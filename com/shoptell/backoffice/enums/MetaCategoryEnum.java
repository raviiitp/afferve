/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.enums;

public enum MetaCategoryEnum {
	LUGGAGE_BAGS("luggage-bags"),
	CLOTHING_ACCESSORIES("clothing-accessories"),
	ELECTRONICS("electronics"),
	HOME_APPLIANCES("home-appliances"),
	KITCHEN_APPLIANCES("kitchen-appliances"),
	VIDEO_GAMES("video-games"),
	NONE("none");
	
	private String name;

	private MetaCategoryEnum(String name) {
		this.setName(name);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
