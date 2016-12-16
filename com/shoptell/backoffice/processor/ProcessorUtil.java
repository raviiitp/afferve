/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.processor;

import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.inject.Named;

/**
 * @author abhishekagarwal
 *
 */
@Named
public class ProcessorUtil {

	public static String removeDuplicates(String title) {
		return new LinkedHashSet<String>(Arrays.asList(title.split(" "))).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ");
	}
}
