/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.shopclues;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.core.env.Environment;

@Named
public class ShopcluesProperties {

	@Inject
	private Environment env;

	public String getTrackingID() {
		return env.getProperty("shopclues.affiliateID");
	}


	public String getToken() {
		return env.getProperty("shopclues.apiKey");
	}
}
