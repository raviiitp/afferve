/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.flipkart;

import static com.shoptell.backoffice.BackofficeConstants.REQUEST_TIMEOUT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shoptell.backoffice.home.ProductException;

@Named
public class FlipkartURLCall {

	private static final Logger log = LoggerFactory.getLogger(FlipkartURLCall.class);

	@Inject
	private FlipkartProperties prop;

	public String queryService(String urlString, boolean priceUpdate) throws Exception {
		HttpURLConnection con = null;
		BufferedReader in = null;
		try {
			URL url = new URL(urlString);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Fk-Affiliate-Token", prop.getToken());
			con.setRequestProperty("Fk-Affiliate-Id", prop.getTrackingID());
			if (priceUpdate){
				con.setConnectTimeout(REQUEST_TIMEOUT);
				con.setReadTimeout(REQUEST_TIMEOUT);
			}

			int status = con.getResponseCode();

			switch (status) {
			
			case HttpURLConnection.HTTP_BAD_REQUEST:
				throw new ProductException("Bad Request");

			case HttpURLConnection.HTTP_GONE:
				// The timestamp is expired.
				throw new Exception("URL expired");

			case HttpURLConnection.HTTP_UNAUTHORIZED:
				// The API Token or the Tracking ID is invalid.
				throw new Exception("API Token or Affiliate Tracking ID invalid.");

			case HttpURLConnection.HTTP_FORBIDDEN:
				// Tampered URL, i.e., there is a signature mismatch.
				// The URL contents are modified from the originally returned
				// value.
				throw new Exception("Tampered URL - The URL contents are modified from the originally returned value");

			case HttpURLConnection.HTTP_OK:

				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				return response.toString();

			default:
				throw new Exception("Connection error with the Affiliate API service: HTTP/" + status);
			}
		} catch(SocketTimeoutException e){
			//log.error("Request Timeout");
		}
		catch (MalformedURLException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		finally{
			if (in != null){
				in.close();
			}
			if (con != null){
				//con.disconnect();
			}
		}
		return null;
	}

}
