/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.service;

import static com.shoptell.backoffice.BackofficeConstants.IST_OFFSET;
import static com.shoptell.backoffice.enums.CBStatusEnum.PAID;
import static com.shoptell.backoffice.enums.CBStatusEnum.REQUESTED;
import static com.shoptell.backoffice.enums.CBStatusEnum.TRACKED;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.shoptell.backoffice.repository.dto.CBPaymentDTO;
import com.shoptell.backoffice.repository.dto.CBReportDTO;
import com.shoptell.backoffice.repository.dto.FeedbackDTO;
import com.shoptell.backoffice.repository.dto.SentMailDTO;
import com.shoptell.backoffice.repository.dto.TicketDTO;
import com.shoptell.backoffice.repository.dto.TicketMessageDTO;
import com.shoptell.db.messagelog.MessageLog;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.domain.User;
import com.shoptell.security.EncryptDecryptUtil;
import com.shoptell.util.streport.STReport;

/**
 * Service for sending e-mails.
 * <p/>
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service(value = "MailService")
public class MailService extends Mail {

	private static final String BASE_TEMPLATE = "mailer";

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	@Inject
	private Environment env;

	@Inject
	private JavaMailSenderImpl javaMailSender;

	@Inject
	private MessageSource messageSource;

	@Inject
	private SpringTemplateEngine templateEngine;

	@Inject
	private STReport report;

	/**
	 * System default email address that sends the e-mails.
	 */
	private String from;

	private String name;

	private Locale locale = null;

	@PostConstruct
	public void start() {
		this.from = env.getProperty("mail.from");
		this.name = env.getProperty("mail.name", "AFFERVE");
		locale = Locale.forLanguageTag("en");
	}

	@Async
	public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		// log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
		// isMultipart, isHtml, to, subject, content);

		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
			message.setTo(to);
			message.setFrom(new InternetAddress(from, name));
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			log.debug("Sent e-mail to User '{}'", to);
			saveMail(new SentMailDTO(to, subject, content, "SUCCESS"));
		} catch (Exception e) {
			log.error("", e);
			log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
		}
	}

	@Async
	public void sendActivationEmail(User user, String baseUrl_email) {
		String activationLink = baseUrl_email + "key=" + user.getActivationKey() + "&userId=" + user.getId();
		Context context = new Context(locale);
		context.setVariable("activationLink", activationLink);
		String subject = messageSource.getMessage("email.activation.title", null, locale);
		prepareEmail(context, subject, "activationEmail", user.getId(), null, "User");
	}

	@Async
	public void sendPasswordResetMail(User user, String baseUrl) {
		Context context = new Context(locale);
		String subject = messageSource.getMessage("email.reset.title", null, locale);
		context.setVariable("baseUrl", baseUrl);
		prepareEmail(context, subject, "passwordResetEmail", user.getId(), null, "User");
	}

	@Async
	public void sendWelcomeEmail(String userId) {
		Context context = new Context(locale);
		String subject = messageSource.getMessage("email.welcome.title", null, locale);
		prepareEmail(context, subject, "welcomeEmail", userId, null, null);
	}

	@Scheduled(cron = "${report.mail.cron}")
	public void sendDailyReport() {
		Context context = new Context();
		List<MessageLog> list = report.generateReport();
		context.setVariable("list", list);
		List<FeedbackDTO> feedback = report.getOpenFeedbacks();
		context.setVariable("feedbacks", feedback);
		List<TicketDTO> ticket = report.getOpenTickets();
		context.setVariable("tickets", ticket);
		List<CBPaymentDTO> payment = report.getOpenPayments();
		context.setVariable("payments", payment);
		String content = templateEngine.process("report", context);
		String subject = "AFFERVE SYSTEM REPORT AUTO GENERATED";
		sendEmail("abhishek@afferve.com", subject, content, false, true);
		sendEmail("ravi@afferve.com", subject, content, false, true);
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
		sendEmail(email, "Dear " + firstName + ", " + subject, content, false, true);
	}

	public void sendRequestUrl(String userId, String url) {
		if (StringUtils.isNotBlank(url)) {
			Context context = new Context(locale);
			String subject = messageSource.getMessage("email.request.title", null, locale);
			url = url.replace("http://", "").replace("https://", "").replace("http%3A%2F%2F", "").replace("https%3A%2F%2F", "");
			// url = "http://"+url;
			context.setVariable("url", url);
			try {
				String tmp = URLDecoder.decode(url, "UTF-8");
				context.setVariable("goal", URLEncoder.encode(tmp, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
			}
			prepareEmail(context, subject, "requestUrlEmail", userId, null, null);
		}
	}

	@Inject
	private EncryptDecryptUtil encdec;

	public void transferMoneyToAccount(CBPaymentDTO prod) {
		String subject = null;
		if (REQUESTED.name().equalsIgnoreCase(prod.getStatus())) {
			subject = messageSource.getMessage("cb.payment.title", null, locale);
		}
		else if (PAID.name().equalsIgnoreCase(prod.getStatus())) {
			subject = messageSource.getMessage("cb.credit.title", null, locale);
		}
		Context context = new Context(locale);
		try {
			prod.setAccountNumber(encdec.decryptAndMask(prod.getAccountNumber()));
		} catch (Exception e) {
			log.error("Error while masking account number", e);
		}
		context.setVariable("prod", prod);
		context.setVariable("date", new Date(System.currentTimeMillis() + IST_OFFSET));
		if (StringUtils.isNotBlank(subject)) {
			prepareEmail(context, subject, "paymentdto", prod.getUserId(), null, null);
		}
	}

	public void sendDataUploadCompleteMail() {
		Context context = new Context();

		List<ProcessLog> processLog = report.getProcessLog();
		context.setVariable("processLog", processLog);

		String content = templateEngine.process("report", context);

		String subject = "DATA UPLOAD COMPLETE";
		sendEmail("abhishek@afferve.com", subject, content, false, true);
		sendEmail("ravi@afferve.com", subject, content, false, true);
	}

	@Async
	public void sendTicketMail(String userId, List<TicketMessageDTO> msgs, boolean isUser, boolean isClosed) {
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
		String subject = "[Ticket] " + firstName + " → Afferve Team (www.afferve.com)";

		if (!isUser) {
			subject = "[Ticket] Afferve Team (www.afferve.com) → " + firstName;
		}

		Context context = new Context(locale);
		context.setVariable("messages", msgs);
		context.setVariable("you", firstName);
		context.setVariable("you_image", user.getImageUrl());
		context.setVariable("we", "Afferve Team");
		context.setVariable("we_image", "https://www.afferve.com/assets/images/afferve/logo.png");
		context.setVariable("isClosed", isClosed);
		context.setVariable("isUser", isUser);
		String content = templateEngine.process("transcript", context);

		sendEmail(to, subject, content, false, true);
	}

	@Async
	public void sendCashbackEmail(CBReportDTO prod, boolean bonusMail, String followUserId) {
		if (prod == null || prod.isDoNotMail())
			return;
		Context context = new Context(locale);
		if (StringUtils.isNotBlank(prod.getCashBackAmount()) && NumberUtils.isNumber(prod.getCashBackAmount())) {
			if (bonusMail) {
				switch (prod.getStatus()) {
				case "PENDING":
				case "RECEIVED":
					sendBonusCBMail(prod, context, followUserId);
					break;
				}
			}
			else {
				switch (prod.getStatus()) {
				case "PENDING":
					sendPendingCBMail(prod, context);
					break;
				case "RECEIVED":
					sendReceivedCBMail(prod, context);
					break;
				}
			}
		}
		else if (TRACKED.name().equals(prod.getStatus())) {
			sendTrackCBMail(prod, context);
		}
	}

	private void sendBonusCBMail(CBReportDTO prod, Context context, String followUserId) {
		context.setVariable("amount", "₹" + prod.getCashBackAmount());
		context.setVariable("status", prod.getStatus());
		String subject = null;
		if (prod.isUser()) {
			subject = "Congratulations! Your bonus";
		}
		else {
			subject = "Congratulations! Your referral bonus from a friend";
			// Name of the original buyer captured by followUserId
			if (followUserId != null){
				subject += " ("+batchRepository.name(followUserId)+")";
			}
		}

		if (prod.getStatus().equals("PENDING")) {
			subject += " is confirmed";
		}
		else {
			subject += " is received";
		}
		prepareEmail(context, subject, "bonus", prod.getUserId(), null, null);
	}

	@SuppressWarnings("deprecation")
	private void sendTrackCBMail(CBReportDTO prod, Context context) {
		String home = prod.getHome();
		String subject = null;
		String baseText = null;
		String product = prod.getProduct();
		String userId = prod.getUserId();

		home = home.toLowerCase();
		home = StringUtils.capitaliseAllWords(home);

		if (StringUtils.isNotBlank(product)) {
			product = StringUtils.capitaliseAllWords(product);
			product = "'" + product + "' ";
		}
		else {
			product = "";
		}
		subject = messageSource.getMessage("cb.track.title", null, locale);
		baseText = "Thank you for buying " + product + "at " + home
				+ " via Afferve. Your order has been tracked and cashback status will be updated to your Afferve account once it is confirmed by " + home + ".";
		context.setVariable("baseText", baseText);
		prepareEmail(context, subject, "cbTracked", userId, null, null);
	}

	@SuppressWarnings("deprecation")
	private void sendReceivedCBMail(CBReportDTO prod, Context context) {
		String amount = prod.getCashBackAmount();
		String home = prod.getHome();
		String subject = null;
		String baseText = null;
		String product = prod.getProduct();
		String userId = prod.getUserId();

		home = home.toLowerCase();
		home = StringUtils.capitaliseAllWords(home);

		if (StringUtils.isNotBlank(product)) {
			product = StringUtils.capitaliseAllWords(product);
			product = "'" + product + "' ";
		}
		else {
			product = "";
		}

		subject = messageSource.getMessage("cb.received.title", null, locale) + " " + home;
		baseText = "Thanks for shopping at " + home + " via Afferve. ₹" + amount + " Cashback has been credited to your Afferve 'My Money'.";
		String warnText = "If the sum of confirmed cashback is over ₹100, you can request a payment via NEFT or Paytm Send Money. Please note that we require, either your Bank Account details (for initiating NEFT) or your Mobile Number attached to your Paytm Account (for wallet transfer).";
		context.setVariable("warnText", warnText);
		if (StringUtils.isNotBlank(product)) {
			context.setVariable("product", messageSource.getMessage("cb.product", null, locale) + " " + product);
		}
		context.setVariable("baseText", baseText);
		prepareEmail(context, subject, "cbTracked", userId, null, null);
	}

	@SuppressWarnings("deprecation")
	private void sendPendingCBMail(CBReportDTO prod, Context context) {
		String amount = prod.getCashBackAmount();
		String home = prod.getHome();
		String subject = null;
		String baseText = null;
		String product = prod.getProduct();
		String userId = prod.getUserId();
		String subj_prod = prod.getProduct();

		home = home.toLowerCase();
		home = StringUtils.capitaliseAllWords(home);

		if (StringUtils.isNotBlank(product)) {
			product = StringUtils.capitaliseAllWords(product);
			subj_prod = product;
			product = "'" + product + "' ";
		}
		else {
			product = "";
		}

		if (Double.parseDouble(amount) > 0) {
			subject = messageSource.getMessage("cb.pending.title", null, locale) + " " + home;
			baseText = "Thanks for shopping at "
					+ home
					+ " via Afferve. ₹"
					+ amount
					+ " Cashback has been added to your Afferve account. The status of this Cashback is ‘Pending’. This will get updated to ‘Confirmed’ after we receive payment from "
					+ home
					+ " within 8-12 weeks, though usually sooner. When you have ₹100 Confirmed Cashback, it can be paid to your Bank Account or Paytm Wallet.";
			if (StringUtils.isNotBlank(product)) {
				context.setVariable("product", messageSource.getMessage("cb.product", null, locale) + " " + product);
			}
			String warnText = "Please note that sometimes your cashback may initially track at a lower / higher amount at this stage, however, this will get updated to the correct amount at the time of confirmation. In case you cancel/return your order or if certain T&Cs have not been followed, then your cashback will not be ‘Confirmed’ as retailers will not pay any commission to Afferve. In this case, your cashback would be 'Cancelled'.";
			context.setVariable("warnText", warnText);
		}
		else {
			subject = "No Cashback";
			baseText = "Thanks for shopping at " + home + " via Afferve. Unfortunately, " + home + " is not paying us any commission";
			String nocbText = " to check the cashback rates on products at " + home
					+ ". On the same page, you will also find the products on which Afferve is not getting any commission from " + home + ".";
			context.setVariable("nocbText", nocbText);
			context.setVariable("home", home.toLowerCase());
			if (StringUtils.isNotBlank(product)) {
				subject += " On " + subj_prod;
				baseText += " for " + product;
			}
			else {
				subject += " On Your Recent Purchase";
				baseText += " on your recent order";
			}
			subject += " At " + home;
			baseText += ", so we won't be paying any cashback to you for this product.";
		}
		context.setVariable("baseText", baseText);
		prepareEmail(context, subject, "cbTracked", userId, null, null);
	}
}