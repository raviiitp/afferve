/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.security.EncryptDecryptUtil;

@Scope("session")
@Controller
@RequestMapping("/backoffice")
public class BackofficeController {

	@Inject
	private BackofficeProcessor processor;
	@Inject
	private EncryptDecryptUtil encDecUtil;

	private List<String> homes = new LinkedList<String>();
	private List<String> category = new LinkedList<String>();

	@PostConstruct
	public void start() {
		for (HomeEnum home : HomeEnum.values()) {
			homes.add(home.name());
		}
		for (CategoryEnum cat : CategoryEnum.values()) {
			category.add(cat.name());
		}
	}

	@RequestMapping(value = "/dashboard")
	public String dashboard(@RequestParam(value = "q", required = false) String query,  @RequestParam(value = "v", required = false) String view, Model model) {
		model.addAttribute("homes", homes);
		model.addAttribute("category", category);

		if (StringUtils.isNotBlank(query)) {
			model.addAttribute("viewer", query);
		}
		else {
			model.addAttribute("viewer", "dashboard");

			long totalRegisteredUsers = processor.getTotalRegisteredUsers(model,view);
			model.addAttribute("totalRegisteredUsers", totalRegisteredUsers);

			long totalTransactionsYesterday = processor.getTotalTransactionsYesterday(model,view);
			model.addAttribute("totalTransactionsYesterday", totalTransactionsYesterday);

			long totalTransactionsToday = processor.getTotalTransactionsToday(model,view);
			model.addAttribute("totalTransactionsToday", totalTransactionsToday);

			long totalConversionThisWeek = processor.getTotalConversionThisWeek(model,view);
			model.addAttribute("totalConversionThisWeek", totalConversionThisWeek);

			long totalReviewedProducts = processor.getTotalReviewedProducts(model,view);
			model.addAttribute("totalReviewedProducts", totalReviewedProducts);

			long totalProducts = processor.getTotalProducts(model,view);
			model.addAttribute("totalProducts", totalProducts);

			long totalLiveCoupons = processor.getTotalLiveCoupons(model,view);
			model.addAttribute("totalLiveCoupons", totalLiveCoupons);

			long totalFeedbackToday = processor.getTotalFeedbackToday(model,view);
			model.addAttribute("totalFeedbackToday", totalFeedbackToday);

			long totalTicketsOpen = processor.getTotalTicketsOpen(model,view);
			model.addAttribute("totalTicketsOpen", totalTicketsOpen);

			long totalLiveOffers = processor.getTotalLiveOffers(model,view);
			model.addAttribute("totalLiveOffers", totalLiveOffers);
			
			long totalPendingPayments = processor.getTotalPendingPayments(model,view);
			model.addAttribute("totalPendingPayments", totalPendingPayments);
		}
		return "dashboard";
	}

	@RequestMapping(value = "/decrypt")
	public String decrypt(@RequestParam(value = "encrypted", required = false) String encrypted,
			@RequestParam(value = "decrypted", required = false) String decrypted, Model model) {
		try {
			if (StringUtils.isNotBlank(encrypted)) {
				decrypted = encDecUtil.decrypt(encrypted);
			}
			else if (StringUtils.isNotBlank(decrypted)) {
				encrypted = encDecUtil.encrypt(decrypted);
			}
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}
		model.addAttribute("encrypted", encrypted);
		model.addAttribute("decrypted", decrypted);
		model.addAttribute("viewer", "debug");
		return "dashboard";
	}

}
