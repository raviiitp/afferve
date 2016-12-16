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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import EbayShoppingService.*;

import javax.inject.Named;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

@Named
public class EbayShoppingAPI extends EbayApi {

	public EbayShoppingAPI() {
		super(shoppingApi);
	}

	@SuppressWarnings("rawtypes")
	public void initEbayShoppingServiceApi(String apiCallName) {
		bp = (BindingProvider) siPort;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				SHOPPINGAPIBASEURL);

		// Add the logging handler
		List<Handler> handlerList = bp.getBinding().getHandlerChain();
		if (handlerList == null) {
			handlerList = new ArrayList<Handler>();
		}
		// handlerList.add(new EbayLogHandler());
		bp.getBinding().setHandlerChain(handlerList);

		Map<String, Object> requestProperties = bp.getRequestContext();
		Map<String, List<String>> httpHeaders = new HashMap<String, List<String>>();
		requestProperties.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				SHOPPINGAPIBASEURL);
		// set reqired HTTP Headers :
		// http://developer.ebay.com/DevZone/shopping/docs/Concepts/ShoppingAPI_FormatOverview.html#StandardURLParameters
		httpHeaders.put("X-EBAY-API-CALL-NAME",
				Collections.singletonList(apiCallName));
		httpHeaders.put("X-EBAY-API-APP-ID",
				Collections.singletonList(ebayProperties.getAPIID()));
		httpHeaders.put("X-EBAY-API-VERSION",
				Collections.singletonList(ebayProperties.getVersion()));
		httpHeaders.put("X-EBAY-API-VERSIONHANDLING",
				Collections.singletonList(versionHandling));
		httpHeaders.put("X-EBAY-API-SITE-ID",
				Collections.singletonList(ebayProperties.getSiteID()));

		requestProperties.put(MessageContext.HTTP_REQUEST_HEADERS, httpHeaders);

	}

	public GetCategoryInfoResponseType callGetCategoryInfo(
			GetCategoryInfoRequestType req) {
		return siPort.getCategoryInfo(req);

	}

}
