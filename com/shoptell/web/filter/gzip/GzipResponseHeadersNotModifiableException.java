/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.web.filter.gzip;

import javax.servlet.ServletException;

public class GzipResponseHeadersNotModifiableException extends ServletException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GzipResponseHeadersNotModifiableException(String message) {
        super(message);
    }
}
