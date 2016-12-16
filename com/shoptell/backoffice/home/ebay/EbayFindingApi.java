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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

import EbayFindingService.FindItemsAdvancedRequest;
import EbayFindingService.FindItemsAdvancedResponse;
import EbayFindingService.FindItemsByCategoryRequest;
import EbayFindingService.FindItemsByCategoryResponse;

@Named
public class EbayFindingApi extends EbayApi {

	public EbayFindingApi() {
		super(findingApi);
	}

	@SuppressWarnings("rawtypes")
	public void initEbayFindingServiceApi(String apiCallName) {

		Binding binding = ((BindingProvider) fPort).getBinding();

		List<Handler> chains = binding.getHandlerChain();
		// chains.add(new EbayLogHandler());
		binding.setHandlerChain(chains);

		Map<String, List<String>> httpHeaders = new HashMap<String, List<String>>();
		httpHeaders.put("X-EBAY-SOA-OPERATION-NAME",
				Collections.singletonList(apiCallName));
		httpHeaders.put("X-EBAY-SOA-SECURITY-APPNAME",
				Collections.singletonList(ebayProperties.getAPIID()));
		httpHeaders.put("X-EBAY-SOA-SERVICE-VERSION",
				Collections.singletonList(ebayProperties.getFindingVersion()));
		httpHeaders.put("X-EBAY-SOA-GLOBAL-ID",
				Collections.singletonList(ebayProperties.getGlobalID()));

		BindingProvider bp = (BindingProvider) fPort;
		Map<String, Object> requestProperties = bp.getRequestContext();
		requestProperties.put(MessageContext.HTTP_REQUEST_HEADERS, httpHeaders);
	}

	public FindItemsByCategoryResponse callFindItemsByCategory(
			FindItemsByCategoryRequest req) {
		return fPort.findItemsByCategory(req);
	}

	public FindItemsAdvancedResponse callFindItemsAdvanced(
			FindItemsAdvancedRequest req) {
		return fPort.findItemsAdvanced(req);
	}

}
