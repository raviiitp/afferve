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

import java.lang.reflect.Type;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.core.env.Environment;

import com.github.scribejava.apis.GoogleApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Named
public class URLShortener {
	
	private OAuthService oAuthService;
	
	@Inject
	private Environment environment;

	@PostConstruct
	public void start(){
		String googleClientId = environment.getProperty("google.client.id");
        String googleClientSecret = environment.getProperty("google.client.secret");
		//String googleClientId = "556490484158-q8s4lkprv7mhspu389jjqd9chgahbuku.apps.googleusercontent.com";
		//String googleClientSecret = "HSVdXFqAnU_-rIrK04ZJ1Utu";
		oAuthService = new ServiceBuilder().provider(GoogleApi.class).apiKey(googleClientId).apiSecret(googleClientSecret)
				.scope("https://www.googleapis.com/auth/urlshortener").build();
	}

	public String shortenUrl(String longUrl) {
		OAuthRequest oAuthRequest = new OAuthRequest(Verb.POST, "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyDd0OFI9nAowSbiAZvQrMmOPO-cLFZAr3w",
				oAuthService);
		oAuthRequest.addHeader("Content-Type", "application/json");
		String json = "{\"longUrl\": \"" + longUrl + "\"}";
		oAuthRequest.addPayload(json);
		Response response = oAuthRequest.send();
		Type typeOfMap = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> responseMap = new GsonBuilder().create().fromJson(response.getBody(), typeOfMap);
		String st = responseMap.get("id");
		return st;
	}
}
