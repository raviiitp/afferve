/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.amazon;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.core.env.Environment;

@Named
public class AmazonProperties {

	@Inject
	private Environment env;

	/**
	 * Reads amazon.accessKey
	 */
	public String getAccessKey() {
		return env.getProperty("amazon.accessKey");
	}

	/**
	 * Reads amazon.secretKey
	 */
	public String getSecretKey() {
		return env.getProperty("amazon.secretKey");
	}

	/**
	 * Reads amazon.associateTag
	 */
	public String getAssociateTag() {
		return env.getProperty("amazon.associateTag");
	}

}
