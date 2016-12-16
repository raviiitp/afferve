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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.Recipient;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.shoptell.backoffice.repository.dto.SentMailDTO;
import com.shoptell.domain.User;

@Service(value = "MandrillMailService")
public class MandrillMailService extends Mail{
	private final Logger log = LoggerFactory.getLogger(MandrillMailService.class);

	@Inject
	private Environment env;
	
	@Inject
	private MessageSource messageSource;

	@Inject
	private SpringTemplateEngine templateEngine;
	
	private MandrillApi mandrillApi;
	
	private String from;

	private String name;
	
	private Locale locale = null;
	
	@PostConstruct
	public void start(){
		this.from = env.getProperty("mail.from");
		this.name = env.getProperty("mail.name", "AFFERVE");
		mandrillApi = new MandrillApi(env.getProperty("mandrill.name","852jBOIepPCqKgLqUw_mWQ"));
		locale = Locale.forLanguageTag("en");
	}
	
	@Async
	public void sendEmail(String to, String subject, String content) {
		ArrayList<Recipient> recipients = new ArrayList<Recipient>();
		MandrillMessage message = new MandrillMessage();
		message.setAutoText(true);
		try {
			Recipient recipient = new Recipient();
			recipient.setEmail(to);
			recipients.add(recipient);
			
			message.setFromName(name);
			message.setFromEmail(from);
			message.setSubject(subject);
			message.setHtml(content);
			message.setTo(recipients);
			message.setPreserveRecipients(true);
			
			MandrillMessageStatus[] messageStatusReports = mandrillApi
			        .messages().send(message, true);
			String status = messageStatusReports[0].getStatus();
			
			log.debug("{} e-mail to User '{}'", status,to);
			
			saveMail(new SentMailDTO(to, subject, content, status));
		} catch (Exception e) {
			log.error("", e);
			log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
		}
	}
	
	@Async
	public void sendNewsletter(String userId) {
		Context context = new Context(locale);
		String subject = messageSource.getMessage("newsletter.title", null, locale);
		prepareEmail(context, subject, "newsletter", userId, null, "User");
	}
	
	@Async
	public void sendInvitationToUnknownPerson(String email, String firstName) {
		if (StringUtils.isBlank(email)) {
			return;
		}
		Context context = new Context(locale);
		if (StringUtils.isBlank(firstName)){
			firstName = "Friend";
		}
		String subject = messageSource.getMessage("email.new.title", null, locale);
		prepareEmail(context, subject, "invitationEmail", null, email, firstName);
	}
	
	public void prepareEmail(Context context, String subject, String template, String userId, String emailAddr, String greeting) {
		String email = null;
		String firstName = null;
		String referId = null;

		if (StringUtils.isNotBlank(userId)) {
			User user = batchRepository.getUser(userId);
			if (user == null)
				return;
			email = user.getEmail();
			firstName = user.getFirstName();
			referId = user.getReferId();
		}
		else if (StringUtils.isNotBlank(emailAddr)) {
			email = emailAddr;
		}
		else {
			return;
		}

		log.debug("Sending {} email to '{}'", template, email);

		if (StringUtils.isNotBlank(subject)) {
		}
		else {
			subject = "Extra Cashback On Products From Afferve";
		}
		subject = StringUtils.capitaliseAllWords(subject.toLowerCase());
		
		if (StringUtils.isNotBlank(firstName)) {
		}
		else if (StringUtils.isNotBlank(greeting)){
			firstName = greeting;
		}
		else {
			firstName = "Customer";
		}
		firstName = StringUtils.capitaliseAllWords(firstName.toLowerCase());

		context.setVariable("title", subject);
		context.setVariable("firstName", firstName);
		
		if (StringUtils.isNotBlank(referId)) {
			context.setVariable("referId", referId);
			context.setVariable("uniqid", userId);
			String referralLink = messageSource.getMessage("email.referral.link", null, locale);
			String share = referralLink + referId;
			context.setVariable("link", share);
			try {
				context.setVariable("share", URLEncoder.encode(share, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		
		String content = templateEngine.process(template, context);
		sendEmail(email, "Dear " + firstName + ", " + subject, content);
	}

}
