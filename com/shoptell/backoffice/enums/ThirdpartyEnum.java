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

import org.apache.commons.lang.StringUtils;

/**
 * @author abhishekagarwal
 *
 */
public enum ThirdpartyEnum {
	AUTOMATIC,
	OFF,
	PAYOOM,
	VCOMMISSION,
	CUELINKS,
	OPTIMISE;
	
	public static ThirdpartyEnum getParty(String name) {
		if (StringUtils.isBlank(name)){
			return null;
		}
		for (ThirdpartyEnum tmp : ThirdpartyEnum.values()) {
			if (tmp.name().equalsIgnoreCase(name.toUpperCase())) {
				return tmp;
			}
		}
		return null;
	}
}
