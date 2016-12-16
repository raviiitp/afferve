/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.ebay;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

@Named
public class EbayProperties {

	@Inject
	private Environment env;

	private static final Logger log = LoggerFactory.getLogger(EbayProperties.class);

	public EbayProperties() {
	}

	@PostConstruct
	public void init() {
		if (env == null) {
			log.debug("env is null");
		}
	}

	public String getAPIID() {
		return env.getProperty("AppID");
	}

	public String getVersion() {
		return env.getProperty("version");
	}

	public String getSiteID() {
		return env.getProperty("siteID");
	}

	public String getFindingVersion() {
		return env.getProperty("Fversion");
	}

	public String getGlobalID() {
		return env.getProperty("globalID");
	}

}
