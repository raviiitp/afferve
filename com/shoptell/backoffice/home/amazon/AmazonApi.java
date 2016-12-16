/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.amazon;

import java.rmi.RemoteException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AWSECommerce.AWSECommerceService;
import AWSECommerce.AWSECommerceServicePortType;
import AWSECommerce.BrowseNodeLookup;
import AWSECommerce.BrowseNodeLookupRequest;
import AWSECommerce.BrowseNodes;
import AWSECommerce.ItemLookup;
import AWSECommerce.ItemLookupRequest;
import AWSECommerce.ItemSearch;
import AWSECommerce.ItemSearchRequest;
import AWSECommerce.Items;
import AWSECommerce.OperationRequest;

import com.shoptell.db.messagelog.MessageLogUtil;

@Named
public class AmazonApi {

	private static final Logger log = LoggerFactory.getLogger(AmazonApi.class);

	@Inject
	private AmazonProperties amazonProperties;
	@Inject
	private SignatureHandler signatureHandler;
	@Inject
	private MessageLogUtil msgLog;

	private static final String XMLEscaping = null;
	private String AWSAccessKeyId = null;
	private String associateTag = null;

	private AWSECommerceService service = new AWSECommerceService();
	private AWSECommerceServicePortType port = null;

	@SuppressWarnings("rawtypes")
	@PostConstruct
	public void init() {
		try {
			AWSAccessKeyId = amazonProperties.getAccessKey();
			associateTag = amazonProperties.getAssociateTag();
			port = service.getAWSECommerceServicePortIN();

			Binding binding = ((BindingProvider) port).getBinding();

			List<Handler> chains = binding.getHandlerChain();
			chains.add(signatureHandler);
			binding.setHandlerChain(chains);

		} catch (Exception e) {
			msgLog.addError(e);
			log.error("", e);
		}
	}

	public List<BrowseNodes> browseNodeLookupApiCall(List<BrowseNodeLookupRequest> request) {
		BrowseNodeLookup search = new BrowseNodeLookup();

		String marketplaceDomain = search.getMarketplaceDomain();
		String validate = search.getValidate();

		BrowseNodeLookupRequest shared = null;
		Holder<OperationRequest> operationRequest = new Holder<OperationRequest>();
		Holder<List<BrowseNodes>> items = new Holder<List<BrowseNodes>>();

		port.browseNodeLookup(marketplaceDomain, AWSAccessKeyId, associateTag, XMLEscaping, validate, shared, request, operationRequest, items);

		return items.value;
	}

	public List<Items> itemSearchApiCall(List<ItemSearchRequest> request) throws RemoteException {
		ItemSearch search = new ItemSearch();

		String marketplaceDomain = search.getMarketplaceDomain();
		String validate = search.getValidate();

		ItemSearchRequest shared = null;// request.get(0);

		Holder<OperationRequest> operationRequest = new Holder<OperationRequest>();
		Holder<List<Items>> items = new Holder<List<Items>>();

		try {
			port.itemSearch(marketplaceDomain, AWSAccessKeyId, associateTag, XMLEscaping, validate, shared, request, operationRequest, items);
		} catch (Exception e) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e1) {
			} finally {
				try {
					port.itemSearch(marketplaceDomain, AWSAccessKeyId, associateTag, XMLEscaping, validate, shared, request, operationRequest, items);
				} catch (Exception e2) {
					//msgLog.addError(e2);
					//e2.printStackTrace();
				}
			}
		}

		return items.value;
	}

	public List<Items> itemLookupApiCall(List<ItemLookupRequest> request) throws RemoteException {
		ItemLookup search = new ItemLookup();
		String marketplaceDomain = search.getMarketplaceDomain();
		String validate = search.getValidate();
		ItemLookupRequest shared = null;

		Holder<OperationRequest> operationRequest = new Holder<OperationRequest>();
		Holder<List<Items>> items = new Holder<List<Items>>();

		try {
			port.itemLookup(marketplaceDomain, AWSAccessKeyId, associateTag, XMLEscaping, validate, shared, request, operationRequest, items);
		} catch (Exception e) {
			/*try {
				Thread.sleep(500);
				port.itemLookup(marketplaceDomain, AWSAccessKeyId, associateTag, XMLEscaping, validate, shared, request, operationRequest, items);
			} catch (Exception e1) {
			}*/
		}
		return items.value;
	}

}
