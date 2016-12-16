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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AWSECommerce.ItemLookupRequest;
import AWSECommerce.Items;

import com.shoptell.backoffice.home.ProductException;
import com.shoptell.backoffice.home.ProductInfo;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;


@Named(value="ItemLookupApi")
public class ItemLookupApi extends ProductInfo{
	
	private static final Logger log = LoggerFactory.getLogger(ItemLookupApi.class);

	@Inject
	private AmazonApi amazonApi;
	@Inject
	private ItemSearchUtil itemSearchUtil;
	
	public void init() throws RemoteException{
		String id = "B00KGZZ824";
		List<ItemLookupRequest> requests = new LinkedList<ItemLookupRequest>();
		ItemLookupRequest request = new ItemLookupRequest();
		request.getItemId().add(id);
		request.getResponseGroup().addAll(Arrays.asList(new String[] {"BrowseNodes","OfferSummary"}));
		requests.add(request);
		List<Items> response = amazonApi.itemLookupApiCall(requests);
		System.out.println(response);
	}
	
	public HomeProductInfoDTO priceCall(String id, String category) throws InterruptedException {
		//log.info("priceCall() Enter");
		try {

			List<ItemLookupRequest> requests = new LinkedList<ItemLookupRequest>();
			ItemLookupRequest request = new ItemLookupRequest();
			request.getItemId().add(id);
			request.getResponseGroup().addAll(Arrays.asList(new String[] {"BrowseNodes","OfferSummary"}));
			requests.add(request);
			List<Items> response = amazonApi.itemLookupApiCall(requests);

			if (response != null) {
				for (Items items : response) {
					for (AWSECommerce.Item item : items.getItem()) {
						HomeProductInfoDTO prod = itemSearchUtil.processItem(item, null, true, category);
						if (prod != null && StringUtils.isNotBlank(prod.getId()) && prod.getSellingPrice() > 0) {
							return prod;
						}
						if (prod == null){
							throw new ProductException("Bad Request");
						}
					}
				}
			}
		}catch (ProductException e){
			log.info("Error Price Call For Amazon - {},{}",id,category);
			msgLog.addError(e);
		}
		catch (Exception e) {
			msgLog.addError(e);
			log.error("EXECUTION ERROR", e);
		} finally {
		//	log.info("priceCall() Exit");
		}
		return null;
	}
	

	@Override
	protected void preprocess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void execute() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void priceUpdater() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postprocess() {
		// TODO Auto-generated method stub
		
	}
}
