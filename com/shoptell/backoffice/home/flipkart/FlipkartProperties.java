/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.flipkart;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.core.env.Environment;

@Named
public class FlipkartProperties {

	@Inject
	private Environment env;

	/**
	 * Reads amazon.accessKey
	 */
	public String getTrackingID() {
		return env.getProperty("flipkart.trackingID");
	}

	/**
	 * Reads amazon.secretKey
	 */
	public String getToken() {
		return env.getProperty("flipkart.token");
	}
}
