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

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class ServiceCall extends Service {

	private final Logger log = LoggerFactory.getLogger(ServiceCall.class);

	// private String host;
	// private int port;

	// @PostConstruct
	// public void start() {
		// setHost(stprop.getValueOrDefault(SCRAP_SERVER_HOST,
		// "54.169.211.79"));
		// setPort(Integer.parseInt(stprop.getValueOrDefault(SCRAP_SERVER_PORT,
		// "8080")));
	// }

	public Document execute(String query, String baseURI) throws Exception {
		// String params = "q=" + URLEncoder.encode(query, FORMAT);
		// String sURL = "http://" + host + ":" + port + "?" + params;
		// URL url = new URL(sURL);
		// HttpURLConnection request = (HttpURLConnection) url.openConnection();
		// request.setRequestMethod("POST");
		// request.setReadTimeout(20000);
		// request.connect();
		// String result = IOUtils.toString((InputStream) request.getContent(),
		// StandardCharsets.UTF_8);
		String result = call(query);
		if (StringUtils.isBlank(baseURI)) {
			return Jsoup.parse(result);
		}
		return Jsoup.parse(result, baseURI);
	}

	/**
	 * Added this method on removal of aws server. Now all scrap requests will
	 * go through Afferve Prod server itself.
	 * 
	 * @param query
	 * @return
	 */
	private String call(String query) {
		log.info("call() Enter Query - " + query);
		try {
			Response resp = Jsoup.connect(query)
					.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36")
					.timeout(15000).method(Method.GET).execute();
			Document doc = resp.parse();
			return doc.toString();
		} catch (IOException e) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
			}
		}
		return "";
	}

	// /**
	// * @return the host
	// */
	// public String getHost() {
	// return host;
	// }
	//
	// /**
	// * @param host the host to set
	// */
	// public void setHost(String host) {
	// this.host = host;
	// }
	//
	// /**
	// * @return the port
	// */
	// public int getPort() {
	// return port;
	// }
	//
	// /**
	// * @param port the port to set
	// */
	// public void setPort(int port) {
	// this.port = port;
	// }
}
