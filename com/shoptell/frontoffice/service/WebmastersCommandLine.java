/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.frontoffice.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.webmasters.Webmasters;
import com.google.api.services.webmasters.Webmasters.Sitemaps.Submit;
import com.google.api.services.webmasters.WebmastersScopes;

public class WebmastersCommandLine {
	private static final Logger log = LoggerFactory.getLogger(WebmastersCommandLine.class);
	private static final String CLIENT_ID = "556490484158-8r39u8uubh4cf2kfjvnvfhis4ijh44u8.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "IGzDGVxtA0_1ahvzKKxFpyQx";
	private static final String REDIRECT_URI = "https://www.afferve.com/backoffice/google";

	static HttpTransport httpTransport = new NetHttpTransport();
	static JsonFactory jsonFactory = new JacksonFactory();
	static GoogleAuthorizationCodeFlow flow;

	public static String execute() throws IOException {
		flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, WebmastersScopes.all()).setAccessType("online")
				.setApprovalPrompt("auto").build();

		String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
		return url;
	}

	public static void postProcess(String code) {
		GoogleTokenResponse response = null;
		try {
			response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
		} catch (IOException e) {
			log.error("", e);
		}
		GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

		// Create a new authorized API client
		Webmasters service = new Webmasters.Builder(httpTransport, jsonFactory, credential).setApplicationName("Afferve").build();

		Submit request = null;
		try {
			request = service.sitemaps().submit("https://www.afferve.com", "https://www.afferve.com/sitemap.xml");
			request.execute();
		} catch (IOException e) {
			log.error("", e);
		}
	}

}
