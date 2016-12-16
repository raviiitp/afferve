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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;

@Named
public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {

	@Inject
	private AmazonProperties amazonProperties;
	
	public final static String ACCESS_KEY = "AWSAccessKeyId";
	public final static String TIMESTAMP = "Timestamp";
	public final static String SIGNATURE = "Signature";
	public final static String SIGN_ALGORITHM = "HmacSHA256";
	public final static String NAMESPACE = "http://security.amazonaws.com/doc/2007-01-01/";
	
	private String accessKey;
	private SimpleDateFormat dateFormat;
	private SecretKeySpec secretKeySpec;
	
	@PostConstruct
	public void init( ) throws UnsupportedEncodingException {
		accessKey = amazonProperties.getAccessKey();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		secretKeySpec = new SecretKeySpec(amazonProperties.getSecretKey().getBytes("UTF-8"), SIGN_ALGORITHM);
	}
	
	private void addHeader(SOAPHeader header, String name, String value)
			throws SOAPException {
		header.addChildElement(new QName(NAMESPACE, name)).addTextNode(value);
	}
	
	private String getSignature(String operation, String timeStamp)
			throws NoSuchAlgorithmException, UnsupportedEncodingException,
			InvalidKeyException {
		String toSign = operation + timeStamp;
		byte[] toSignBytes = toSign.getBytes("UTF-8");

		Mac signer = Mac.getInstance(SIGN_ALGORITHM);
		signer.init(secretKeySpec);
		signer.update(toSignBytes);
		byte[] signBytes = signer.doFinal();

		return DatatypeConverter.printBase64Binary(signBytes);
	}

	@Override
	public boolean handleMessage(SOAPMessageContext messagecontext) {
		try {
			Boolean outbound = (Boolean) messagecontext
					.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!outbound) {
				return true;

			}

			SOAPMessage soapMessage = messagecontext.getMessage();
			SOAPBody soapBody = soapMessage.getSOAPBody();
			Node operation = soapBody.getFirstChild();

			String timeStamp = dateFormat.format(Calendar.getInstance()
					.getTime());
			String signature = getSignature(operation.getLocalName(), timeStamp);

			// Add the authentication headers
			SOAPEnvelope soapEnv = soapMessage.getSOAPPart().getEnvelope();
			SOAPHeader header = soapEnv.getHeader();
			if (header == null) {
				header = soapEnv.addHeader();

			}
			addHeader(header, ACCESS_KEY, accessKey);
			addHeader(header, TIMESTAMP, timeStamp);
			addHeader(header, SIGNATURE, signature);

			return true;

		} catch (SOAPException e) {
			throw new RuntimeException(e);

		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);

		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);

		}
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

}
