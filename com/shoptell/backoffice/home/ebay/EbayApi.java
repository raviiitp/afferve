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

import javax.inject.Inject;
import javax.xml.ws.BindingProvider;

import EbayFindingService.FindingService;
import EbayFindingService.FindingServicePortType;
import EbayShoppingService.Shopping;
import EbayShoppingService.ShoppingInterface;
 

public class EbayApi {

	protected static final String SHOPPINGAPIBASEURL = "http://open.api.ebay.com/shopping?";
	protected static final String FINDINGAPIBASEURL = "http://svcs.ebay.com/services/search/FindingService/v1?";

	protected static final String versionHandling = "LatestEnumValues";

	protected static final int shoppingApi = 0x1;
	protected static final int findingApi = 0x2;

	@Inject
	protected EbayProperties ebayProperties;
	protected BindingProvider bp;

	protected Shopping sService;
	protected ShoppingInterface siPort;

	protected FindingService fService;
	protected FindingServicePortType fPort;
	


	public EbayApi(int coreApi) {
		if (coreApi == shoppingApi) {
			sService = new Shopping();
			siPort = sService.getShopping();
		} else if (coreApi == findingApi) {
			fService = new FindingService();
			fPort = fService.getFindingServiceSOAPPort();
		}
	}
}
