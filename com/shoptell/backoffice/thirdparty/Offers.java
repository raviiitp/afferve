/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.thirdparty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.google.gson.Gson;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.ThirdpartyEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.dto.OffersDTO;

@Named(value = "Offers")
public class Offers {
	private static final Logger log = LoggerFactory.getLogger(Offers.class);

	@Inject
	private BatchRepository batchRepository;

	private String thirdparty;

	@Async
	public void init() {
		preprocess();
		execute();
		postprocess();
	}

	private void postprocess() {
		// TODO Auto-generated method stub
	}
	
	public void forPayoom(){
		log.info("forPayoom() Enter");
		setThirdparty(ThirdpartyEnum.PAYOOM.name());
		execute();
		log.info("forPayoom() Exit");
	}
	
	public void forVcom(){
		log.info("forVcom() Enter");
		setThirdparty(ThirdpartyEnum.VCOMMISSION.name());
		execute();
		log.info("forVcom() Exit");
	}

	private void execute() {
		log.info("execute() Enter");
		List<OffersDTO> list = new LinkedList<OffersDTO>();
		try {
			String jsonData = queryService();
			JSONObject obj = new JSONObject(jsonData);
			if (obj != null) {
				JSONObject response = obj.getJSONObject("response");
				if (response != null) {
					JSONObject data = response.getJSONObject("data");
					Iterator<?> keyset = data.keys();
					while (keyset.hasNext()) {
						String key = (String) keyset.next();
						Object value = data.get(key);
						if (value instanceof JSONObject) {
							Object tmp = ((JSONObject) value).get("Offer");
							OffersDTO offer = new Gson().fromJson(tmp.toString(), OffersDTO.class);
							offer.setThirdparty(getThirdparty());
							HomeEnum home = HomeEnum.determineHome(offer.getPreview_url());
							if (home != null){
								offer.setHome(home.name());
								offer.setPublished(true);
								if (StringUtils.isNotBlank(offer.getDescription())){
									String desc = offer.getDescription();
									desc = desc.trim().replaceAll("\\s+", " ");
									offer.setDescription(desc);
								}
								if (StringUtils.isNotBlank(offer.getName())){
									String desc = offer.getName();
									desc = desc.trim().replaceAll("\\s+", " ");
									offer.setName(desc);
								}
							}
							else {
								offer.setPublished(false);
							}
							list.add(offer);
						}
					}
					if (list.size() > 0) {
						batchRepository.batchSave(list);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("execute() Exit");
	}

	private void preprocess() {
		// TODO Auto-generated method stub

	}

	public String queryService() throws Exception {
		HttpURLConnection con = null;
		BufferedReader in = null;
		try {
			String urlString = null;
			switch (ThirdpartyEnum.getParty(getThirdparty())) {
			case PAYOOM:
				urlString = "https://api.hasoffers.com/Apiv3/json?NetworkId=payoom&Target=Affiliate_Offer&Method=findAll&api_key=ee050f32cf142ef47294db7b8f23a2c430c0966bb2dbf1b241b53ff0cf292fe9";
				break;
			case VCOMMISSION:
				urlString = "https://api.hasoffers.com/Apiv3/json?NetworkId=vcm&Target=Affiliate_Offer&Method=findAll&api_key=13254cfccd3e88ec1b1d8f7f4d189c5dea1840c8ade4487a0f62d01cabbb5747";
				break;

			default:
				break;
			}
			if (StringUtils.isBlank(urlString)){
				return null;
			}
			URL url = new URL(urlString);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			// con.setRequestProperty("api_key", prop.getToken());

			int status = con.getResponseCode();

			switch (status) {

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
		} catch (MalformedURLException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			if (in != null) {
				in.close();
			}
			if (con != null) {
				// con.disconnect();
			}
		}
		return null;
	}

	/**
	 * @return the thirdParty
	 */
	public String getThirdparty() {
		return thirdparty;
	}

	/**
	 * @param thirdParty the thirdParty to set
	 */
	public void setThirdparty(String thirdParty) {
		this.thirdparty = thirdParty;
	}
}
