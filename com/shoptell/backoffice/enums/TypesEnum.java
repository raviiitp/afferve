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

public enum TypesEnum {

	ALL("ALL"), AUTOMOBILE("AUTOMOBILE"), BOOKS("BOOKS"), ELECTRONICS("ELECTRONICS"), FASHION("FASHION"), FOODRETAIL("FOOD-RETAIL"), FURNITURE("FURNITURE"), GIFTS(
			"GIFTS"), GROCERY("GROCERY"), HEALTHBEAUTY("HEALTH-BEAUTY"), HOMEAPPLIANCE("HOME-APPLIANCE"), JEWELLERY("JEWELLERY"), KITCHENAPPLIANCE(
			"KITCHEN-APPLIANCE"), RECHARGE("RECHARGE"), SPORTS("SPORTS");

	private String name;

	TypesEnum(String name) {
		this.setName(name);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
