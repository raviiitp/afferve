/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.processor.impl;

import javax.inject.Named;

import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

@Named
public class WaterpurifierProcessor extends DataProcessor{

	@Override
	public ReviewedProductInfoDTO shopcluesProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReviewedProductInfoDTO ebayProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReviewedProductInfoDTO snapdealProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

}
