/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shoptell.domain.User;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class AddMailContact {
	private final static Logger log = LoggerFactory.getLogger(AddMailContact.class);
	private static final String apiKey = "20cb44749496e520809c7b87d73e9a1c";
	private static final String apiURL = "http://api2.getresponse.com";
	private static final String CAMPAIGN_TOKEN = "n6n5q";

	@SuppressWarnings("serial")
	public static void addEmail(User user) {
		if (user == null)
			return;
		
		log.info("addEmail() Enter - "+user.toString());
		
		try {
			URL api_url = new URL(apiURL);
			JSONRPC2Session client = new JSONRPC2Session(api_url);
			client.getOptions().setRequestContentType("application/json");
			JSONRPC2Response result = client.send(new JSONRPC2Request("add_contact", Arrays.asList(new Object[] { apiKey, new Hashtable<String, Object>() {
				{
					put("campaign", CAMPAIGN_TOKEN);
					put("name", user.getName());
					put("email", user.getEmail());
				}
			} }), 2));
			log.info("addEmail() Exit - " + result.getResult());
		} catch (MalformedURLException | JSONRPC2SessionException e) {
			log.error("addEmail()", e);
		}
	}

	// public static void execute() throws JSONRPC2SessionException,
	// MalformedURLException {
	// URL api_url = new URL(apiURL);
	// JSONRPC2Session client = new JSONRPC2Session(api_url);
	// client.getOptions().setRequestContentType("application/json");

	// find campaign named 'test'
	// JSONRPC2Response campaigns = client.send(new
	// JSONRPC2Request("get_campaigns", Arrays.asList(new Object[] { api_key,
	// // find by name literally
	// new Hashtable<String, Map>() {
	// {
	// put("name", new Hashtable<String, String>() {
	// {
	// put("EQUALS", "team_970176");
	// }
	// });
	// }
	// } }), 1));

	// uncomment following line to preview Response
	// System.out.println(campaigns.getResult());

	// because there can be only one campaign of this name
	// first key is the CAMPAIGN_ID required by next method
	// (this ID is constant and should be cached for future use)
	//
	// ((HashMap<String, Map>)
	// campaigns.getResult()).keySet().iterator().next();

	// add contact to the campaign
	// JSONRPC2Response result = client.send(new JSONRPC2Request("add_contact",
	// Arrays.asList(new Object[] { apiKey, new Hashtable<String, Object>() {
	// {
	//
	// identifier of 'test' campaign
	// put("campaign", CAMPAIGN_TOKEN);

	// basic info
	// put("name", "Komal Agarwal");
	// put("email", "agarwalabhishek2@yahoo.com");

	// custom fields
	// put("customs", Arrays.asList(new Hashtable[]{
	// new Hashtable<String, String>() {{
	// put("name", "likes_to_drink");
	// put("content", "tea");
	// }},
	// new Hashtable<String, String>() {{
	// put("name", "likes_to_drink");
	// put("content", "tea");
	// }}
	// }));
	// }
	// } }), 2));

	// uncomment following line to preview Response
	// System.out.println(result.getResult());

	// }
}