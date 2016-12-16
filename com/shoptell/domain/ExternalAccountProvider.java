/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.domain;

import org.apache.commons.lang.StringUtils;

/**
 * Supported social providers.  Many other providers are supported, see http://projects.spring.io/spring-social/.
 */
public enum ExternalAccountProvider {
    FACEBOOK,
    GOOGLE,
    LINKEDIN,
    TWITTER;

    public static ExternalAccountProvider caseInsensitiveValueOf(String value) {
        if (StringUtils.isNotBlank(value))
            return ExternalAccountProvider.valueOf(value.toUpperCase());
        else
            return null;
    }
}