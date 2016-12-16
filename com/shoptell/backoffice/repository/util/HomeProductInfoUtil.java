/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository.util;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

@Named
public class HomeProductInfoUtil {

	/*
	 * create tags from title, color and productBrand.
	 */
	public Set<String> getTag_asSet(String title, String color, String productBrand) {
		Set<String> tag_asSet = null;
		String regex = "[\\[\\](),]";

		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll(regex, "");
			tag_asSet = new HashSet<String>();
			String[] tokens = title.split(" ");
			for (String token : tokens) {
				if (!token.isEmpty()) {
					tag_asSet.add(token.toLowerCase());
				}
			}
		}
		if (productBrand != null && !productBrand.equals("null")) {
			productBrand = productBrand.replaceAll(regex, "");
			if (tag_asSet == null) {
				tag_asSet = new HashSet<String>();
				tag_asSet.add(productBrand.toLowerCase());
			}
			else if (!tag_asSet.contains(productBrand)) {
				tag_asSet.add(productBrand.toLowerCase());
			}
		}
		if (color != null && !color.equals("null") && !color.equals("0")) {
			color = color.replaceAll(regex, "");
			if (tag_asSet == null) {
				tag_asSet = new HashSet<String>();
			}
			tag_asSet.add(color.toLowerCase());
		}
		return tag_asSet;
	}
}
