/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.thirdparty;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.repository.dto.PartnerCouponsDTO;

public class coupons {

	public void generateCashbackString(HomeEnum home, PartnerCouponsDTO dto) {
		String cashb = home.getCashback();
		if (StringUtils.isNotBlank(cashb) && NumberUtils.isNumber(cashb)) {
			String cb = "";
			if (home.getInRs() == 1) {
				cb = "₹ " + cashb;
			}
			else {
				// if (!"on".equalsIgnoreCase(nocut)) {
				/*double p = Double.parseDouble(cashb) * (BackofficeConstants.CONVERSION_RATE + BackofficeConstants.REFFERAL_RATE);
				cashb = roundOff(p);*/
				// }
				cb = cashb + "%";
			}
			dto.setAfferve_cb(cb);
		}
	}
}
