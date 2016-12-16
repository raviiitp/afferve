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
import java.util.List;
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

import com.shoptell.backoffice.repository.dto.CBReportDTO;
import com.shoptell.backoffice.repository.dto.SentMailDTO;
import com.shoptell.domain.User;
import com.sparkpost.Client;
import com.sparkpost.model.AddressAttributes;
import com.sparkpost.model.RecipientAttributes;
import com.sparkpost.model.TemplateContentAttributes;
import com.sparkpost.model.TransmissionWithRecipientArray;
import com.sparkpost.model.responses.Response;
import com.sparkpost.resources.ResourceTransmissions;
import com.sparkpost.transport.RestConnection;

@Service(value = "SparkPostMailService")
public class SparkPostMailService extends Mail {
	private final static Logger log = LoggerFactory.getLogger(SparkPostMailService.class);
	
	private static final String BASE_TEMPLATE = "mailer";

	@Inject
	private Environment env;

	@Inject
	private MessageSource messageSource;

	@Inject
	private SpringTemplateEngine templateEngine;

	private Client client;

	private String from;

	private String name;

	private Locale locale = null;

	@PostConstruct
	public void start() {
		this.from = env.getProperty("mail.from");
		this.name = env.getProperty("mail.name", "AFFERVE");
		client = new Client(env.getProperty("sparkpost.name", "a457220d7a62c52b40c428c38a40a07f00a116a4"));
		locale = Locale.forLanguageTag("en");
		//sendMissYouEmail("d0aedd9a-98c7-44fb-8b0a-f410467b74e9");
		//sendInvitationToUnknownPerson("abhishek0agarwal@gmail.com", "Abhishek");
		//sendInvitationToUnknownPerson("abhishek0agarwal@gmail.com", "abhishek");
	}

	@Async
	public void sendEmail(String to, String subject, String content) {
		try {
			TransmissionWithRecipientArray transmission = new TransmissionWithRecipientArray();

			List<RecipientAttributes> recipientArray = new ArrayList<RecipientAttributes>();

			RecipientAttributes recipientAttribs = new RecipientAttributes();
			recipientAttribs.setAddress(new AddressAttributes(to));
			recipientArray.add(recipientAttribs);

			transmission.setRecipientArray(recipientArray);

			TemplateContentAttributes contentAttributes = new TemplateContentAttributes();
			AddressAttributes addr = new AddressAttributes(from);
			addr.setName(name);
			contentAttributes.setFrom(addr);

			contentAttributes.setSubject(subject);
			contentAttributes.setHtml(content);
			transmission.setContentAttributes(contentAttributes);

			RestConnection connection = new RestConnection(client);
			Response response = ResourceTransmissions.create(connection, 0, transmission);
			String status = response.getResponseMessage();
			log.debug("{} e-mail to User '{}'", status, to);

			saveMail(new SentMailDTO(to, subject, content, status));
		} catch (Exception e) {
			log.error("", e);
			log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
		}
	}

	/*@Async
	public void sendNewsletter(String userId) {
		Context context = new Context(locale);
		String subject = messageSource.getMessage("newsletter.title", null, locale);
		prepareEmail(context, subject, "newsletter", userId, null, "User");
	}*/

	@Async
	public void sendInvitationToUnknownPerson(String email, String firstName) {
		if (StringUtils.isBlank(email)) {
			return;
		}
		Context context = new Context(locale);
		if (StringUtils.isBlank(firstName)) {
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
		context.setVariable("template", template);

		if (StringUtils.isNotBlank(subject)) {
		}
		else {
			subject = "Extra Cashback On Products From Afferve";
		}
		subject = StringUtils.capitaliseAllWords(subject.toLowerCase());

		if (StringUtils.isNotBlank(firstName)) {
		}
		else if (StringUtils.isNotBlank(greeting)) {
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

		String content = templateEngine.process(BASE_TEMPLATE, context);
		sendEmail(email, "Dear " + firstName + ", " + subject, content);
	}

	@Async
	public void sendMonthSummary(String userId, double pending, double received, double paid, List<CBReportDTO> list, String month) {
		User user = batchRepository.getUser(userId);
		if (user == null)
			return;
		String to = user.getEmail();
		String firstName = user.getFirstName();
		if (StringUtils.isNotBlank(firstName)) {
			firstName = StringUtils.capitaliseAllWords(firstName.toLowerCase());
		}
		else {
			firstName = "You";
		}
		String subject = "Afferve Money Summary For " + month;
		
		Context context = new Context(locale);
		context.setVariable("you", firstName);
		context.setVariable("you_image", user.getImageUrl());
		context.setVariable("month", month);
		context.setVariable("reports", list);
		context.setVariable("pending", pending);
		context.setVariable("received", received);
		context.setVariable("paid", paid);
		context.setVariable("saving", pending+received+paid);
		String content = templateEngine.process("monthlysummary", context);
		sendEmail(to, subject, content);
	}

	@Async
	public void sendMissYouEmail(String userId) {
		User user = batchRepository.getUser(userId);
		if (user == null)
			return;
		String to = user.getEmail();
		String firstName = user.getFirstName();
		String subject = null;
		
		if (StringUtils.isNotBlank(firstName)) {
			firstName = StringUtils.capitaliseAllWords(firstName.toLowerCase());
			subject = firstName+", ";
		}
		else {
			firstName = "";
		}
		 subject += "We Want You Back";
		
		Context context = new Context(locale);
		
		context.setVariable("you", firstName);
		context.setVariable("you_image", user.getImageUrl());
		
		String content = templateEngine.process("missyouemail", context);
		sendEmail(to, subject, content);
	}
}
