/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.recaptcha;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Scope("session")
@Named (value = "ReCaptchaImpl")
public class ReCaptchaImpl implements ReCaptcha {

    protected static Logger log = LoggerFactory.getLogger(ReCaptchaImpl.class);
    private static final String CIPHER_INSTANCE_NAME = "AES/ECB/PKCS5Padding";
    public static final String SCRIPT_URL = "//www.google.com/recaptcha/api.js";
    public static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private String siteKey;
    private String secret;
    
    @Inject
    private Environment env;
    
    @PostConstruct
    public void start(){
    	String siteKey = env.getProperty("captcha.site.key");
    	String secret = env.getProperty("captcha.secret.key");
    	
        if (siteKey == null || siteKey.isEmpty()){
            throw new IllegalArgumentException("Invalid siteKey");
        }
        if (secret == null || secret.isEmpty()){
            throw new IllegalArgumentException("Invalid secret");
        }
        this.siteKey = siteKey;
        this.secret = secret;
    }
    
    public String createSToken() {
		String sessionId = UUID.randomUUID().toString();
		String jsonToken = createJsonToken(sessionId);
		return encryptAes(jsonToken, secret);
	}
    
    private String createJsonToken(String sessionId) {
		JsonObject obj = new JsonObject();
		obj.addProperty("session_id", sessionId);
		obj.addProperty("ts_ms", System.currentTimeMillis());
		return new Gson().toJson(obj);
	}
    
    private String encryptAes(String input, String siteSecret) {
		try {
			SecretKeySpec secretKey = getKey(siteSecret);
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return BaseEncoding.base64Url().omitPadding().encode(cipher.doFinal(input.getBytes("UTF-8")));
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}
    
    private static SecretKeySpec getKey(String siteSecret) {
		try {
			byte[] key = siteSecret.getBytes("UTF-8");
			key = Arrays.copyOf(MessageDigest.getInstance("SHA").digest(key), 16);
			return new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			log.error("", e);
		}
		return null;
	}
    
    @SuppressWarnings("unused")
	private static String decryptAes(String input, String key) throws Exception {
		SecretKeySpec secretKey = getKey(key);
		Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return new String(cipher.doFinal(BaseEncoding.base64Url().omitPadding().decode(input)), "UTF-8");
	}

    @Override
    public String createScriptResource(Map<String, String> parameters) {
        String url = SCRIPT_URL;
        List<String> queryList = new ArrayList<String>();
        for(String key : parameters.keySet()){
            try {
                queryList.add(String.format("%s=%s",key, URLEncoder.encode(parameters.get(key),"UTF-8")));
            } catch (UnsupportedEncodingException e) {
                //should never happen
            }
        }
        if (!queryList.isEmpty()){
            url = url + "?" + StringUtils.join(queryList,"&");
        }
        return "<script src=\"" + url + "\" async defer></script>";
    }

    @Override
    public String createReCaptchaTag(Map<String, String> parameters) {
        String attrs = String.format("data-sitekey=%s",siteKey);
        List<String> attrList = new ArrayList<String>();
        for(String key : parameters.keySet()){
            attrList.add(String.format("data-%s=\"%s\"",key,parameters.get(key)));
        }
        attrList.add(String.format("data-%s=\"%s\"","stoken",createSToken()));
        if (!attrList.isEmpty()){
            attrs = attrs + " " + StringUtils.join(attrList," ");
        }
        return "<div id=\"g-recaptcha\" class=\"g-recaptcha\" " + attrs + "></div>";
    }

    @Override
    public String getSiteKey() {
        return this.siteKey;
    }

    @SuppressWarnings("deprecation")
	@Override
    public ReCaptchaResponse verifyResponse(String response, String remoteIp) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = null;
        HttpEntity entity = null;
        CloseableHttpResponse httpResponse = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(VERIFY_URL).addParameter("secret",secret);
            if (response != null){
                uriBuilder.addParameter("response", response);
            }
            if (remoteIp != null){
                uriBuilder.addParameter("remoteip",remoteIp);
            }
            httpGet = new HttpGet(uriBuilder.build());
            httpResponse = httpClient.execute(httpGet);
            log.debug("status: {}", httpResponse.getStatusLine());
            entity = httpResponse.getEntity();
            if (entity != null){
                JSONObject json = new JSONObject(EntityUtils.toString(entity,"UTF-8"));
                Boolean success = json.getBoolean("success");
                JSONArray errorCodes = json.optJSONArray("error-codes");
                String errorCode = (errorCodes != null && errorCodes.length() > 0) ? errorCodes.optString(0, null) : null;
                
                EntityUtils.consume(entity); //to close entity
                
                return new ReCaptchaResponse(success, errorCode);
            }
        } catch (UnsupportedEncodingException e) {
            //bypass
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(),e);
        } catch (ParseException e) {
			log.error("", e);
		} catch (JSONException e) {
			log.error("", e);
		} finally {
			if (entity != null){
				try {
					entity.consumeContent();
				} catch (IOException e) {
				}
			}
			if (httpResponse != null){
				try {
					httpResponse.close();
				} catch (IOException e) {
				}
			}
			if (httpGet != null){
				httpGet.abort();
			}
            if (httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    //bypass
                }
            }
        }
        return new ReCaptchaResponse(false,"not-reachable");
    }
}
