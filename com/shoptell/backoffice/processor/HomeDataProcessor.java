/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.processor;

import javax.inject.Named;

import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

@Named
public class HomeDataProcessor extends Processor{
	public ReviewedProductInfoDTO process(HomeProductInfoDTO prod) {
		return processData(prod, HomeEnum.fromName(prod.getHome()));
	}
}
