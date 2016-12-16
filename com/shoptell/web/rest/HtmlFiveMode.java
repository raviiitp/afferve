/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.web.rest;

import static com.shoptell.backoffice.BackofficeConstants.SP_CHAR_REMOVE_REGEX;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.CategoryEnum;

/**
 * 
 * @author ravi
 *
 *         this class is to support html5mode = true how it works? : Actually
 *         main aim is to forward "/" for each and every url (related to states
 *         in angularjs) it does not reload url, so Angularjs MVC takes care of
 *         which state to go based on url.
 */

// no @RestController
@Scope("session")
@Controller
public class HtmlFiveMode {

	@PostConstruct
	public void init() {
	}

	/**
	 * for each url forward to /
	 * 
	 * @return
	 */
	@RequestMapping({ "/about-us/**", "/activate-email/**", "/clothing-accessories/**", "/contact-us/**", "/coupons/**", "/diwali-offers/**", "/electronics/**", "/faq/**", "/home-appliances/**",
			"/how-it-works/**", "/kitchen-appliances/**", "/luggage-bags/**", "/partners/**", "/pdesc/**", "/privacy/**", "/redirect/**", "/referral/**", "/setting/**",
			"/sitemap/**", "/tnc/**", "/ucsearch/**", "/valentine/**", "/video-games/**"})
	public String index() {
		return "forward:/";
	}
	
	@RequestMapping({ "/search/**" })
	public void searchURLRedirect(@RequestParam("q") String q, @RequestParam(value = "cat", required = false) String category, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String newUrl = null;

		if (StringUtils.isNotBlank(q)) {
			q = q.replaceAll(SP_CHAR_REMOVE_REGEX, "").trim();
			if (StringUtils.isNotBlank(category)) {
				category = category.replace(" ", "");
				CategoryEnum subCategory = CategoryEnum.getCategory(category);
				if (subCategory != null) {
					newUrl = BackofficeUtil.getBaseUrl(request) + "/" + subCategory.getMeta().getName() + "/" + subCategory.getSeoName() + "/" + q;
				}
			}
			if(StringUtils.isBlank(newUrl)){
				newUrl = BackofficeUtil.getBaseUrl(request) + "/ucsearch?q=" + q;
			}
			response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		} else {
			newUrl = BackofficeUtil.getBaseUrl(request);
			response.setStatus(HttpServletResponse.SC_OK);
		}
		response.setHeader("Location", newUrl);
	}
}
