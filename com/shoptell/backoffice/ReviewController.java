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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TypesEnum;
import com.shoptell.backoffice.repository.dto.PartnerCouponsDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.backoffice.thirdparty.PayoomCoupons;
import com.shoptell.frontoffice.service.PartnerCoupons;
import com.shoptell.util.stproperties.STProperties;

@Scope("session")
@Controller(value = "ReviewController")
@RequestMapping("/backoffice")
public class ReviewController {
	@Inject
	private BackofficeProcessor processor;
	
	@Inject
	private PartnerCoupons coupon;
	
	@Inject
	private PayoomCoupons cpn;
	
	@Inject
	private STProperties stprop;

	private static final int pgSize = 20;

	private int review_counter = 0;

	private static final String[] homes = { "AMAZON", "FLIPKART", "SNAPDEAL" };

	private static SortedSet<String> category = new TreeSet<String>();
	
	private static SortedSet<String> keywords = new TreeSet<String>();
	
	private static SortedSet<String> home = new TreeSet<String>();

	private String uri = null;

	private String cat_org;

	private String home_org;

	private boolean ism;

	@PostConstruct
	public void start() {
		if (keywords.size() == 0 || home.size() == 0){
			for (HomeEnum tmp : HomeEnum.values()){
				keywords.add(tmp.name());
				home.add(tmp.name());
			}
			for (TypesEnum tmp : TypesEnum.values()){
				keywords.add(tmp.getName());
			}
		}
		if (category.size() == 0) {
			for (CategoryEnum cat : CategoryEnum.values()) {
				if (CategoryEnum.ALL.equals(cat)) {
					continue;
				}
				category.add(cat.name());
			}
		}
	}

	@RequestMapping(value = "/remerge")
	public String saveItem(@ModelAttribute(value = "prod") ReviewedProductInfoDTO request, @RequestParam(value = "feature", required = false) String features,
			@RequestParam(value = "prop", required = false) String prop[], @RequestParam(value = "item", required = false) String item,
			@RequestParam(value = "delete", required = false) String delete, BindingResult bindingResult, Model model) {

		String msg = processor.saveReviewProducts(request, features, "on".equalsIgnoreCase(delete), prop);
		++review_counter;

		String resp = "redirect:/backoffice/review?rd=true";

		if (StringUtils.isNotBlank(item)) {
			resp = resp + "&pro=" + item;
		}

		if (StringUtils.isNotBlank(msg)) {
			resp = resp + "&msg=" + msg;
		}

		return resp;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/review")
	public String getItems(HttpServletRequest request, Model model, @RequestParam(value = "mergeId", required = false) String mergeId,
			@RequestParam(value = "category", required = false) String subcategory, @RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "home", required = false) String home, @RequestParam(value = "ismerged", defaultValue = "false") boolean ismerged,
			@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "tags", required = false) String tags,
			@RequestParam(value = "pro", required = false) String item, @RequestParam(value = "rd", required = false) boolean rd) {

		Map<String, Object> map = processor.getReviewedProducts(rd, page, pgSize, home, id, tags, ismerged, subcategory, mergeId);

		if (!rd) {
			ism = ismerged;
		}

		model.addAttribute("ismerged", ism);

		if (StringUtils.isNotBlank(subcategory)) {
			cat_org = subcategory;
		}

		if (StringUtils.isNotBlank(home)) {
			home_org = home;
		}

		if (StringUtils.isBlank(cat_org)) {
			cat_org = "SMARTPHONES";
		}

		if (StringUtils.isBlank(home_org)) {
			home_org = "AMAZON";
		}

		model.addAttribute("home", home_org);
		model.addAttribute("subcategory", cat_org);
		model.addAttribute("prop_array", CategoryEnum.getProperties(cat_org));
		model.addAttribute("enum", CategoryEnum.class);

		int lastpg = (int) map.getOrDefault("lastpg", 1);

		int pagenum = (int) map.getOrDefault("pagenum", 1);

		List<ReviewedProductInfoDTO> sublist = (List<ReviewedProductInfoDTO>) map.getOrDefault("sublist", new LinkedList<ReviewedProductInfoDTO>());

		// ismerged = (boolean) map.getOrDefault("ismerged", false);

		String error_msg = (String) map.get("error_msg");

		if (StringUtils.isNotBlank(error_msg)) {
			model.addAttribute("error_msg", error_msg);
		}

		// model.addAttribute("ismerged", ismerged);

		for (ReviewedProductInfoDTO tmp : sublist) {
			String url = tmp.getProductUrl();
			if (StringUtils.isNotBlank(url)) {
				try {
					url = URLDecoder.decode(url, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if ("FLIPKART".equalsIgnoreCase(tmp.getHome())) {
					tmp.setProductUrl(url.replace("&affid=abhishek03", ""));
				}
				else if ("AMAZON".equalsIgnoreCase(tmp.getHome()) || "SNAPDEAL".equalsIgnoreCase(tmp.getHome())) {
					tmp.setProductUrl(url.split("\\?")[0]);
				}
			}
		}

		model.addAttribute("obj", sublist);

		model.addAttribute("lastpg", lastpg);

		model.addAttribute("homes", homes);

		model.addAttribute("page", pagenum);

		model.addAttribute("viewer", "review");

		model.addAttribute("review_counter", review_counter);

		if (!rd) {
			uri = request.getScheme()
					+ "://"
					+ request.getServerName()
					+ ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme())
							&& request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) + request.getRequestURI()
					+ (request.getQueryString() != null ? "?" + request.getQueryString() : "");
		}

		model.addAttribute("uri", uri.replaceAll("&page=\\d+", ""));

		model.addAttribute("category", category);

		if (StringUtils.isNotBlank(item)) {
			model.addAttribute("pro", item.split(",")[0]);
		}

		return "dashboard";
	}

	@RequestMapping(value = "/reset")
	public String resetCounter(HttpServletRequest request, Model model) {
		review_counter = 0;
		return "redirect:/backoffice/review?msg=Reset Success";
	}
	
	
	@RequestMapping(value = "/coupon")
	public String getCoupons(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "pro", required = false) String item,
			HttpServletRequest request, Model model) {
		if (StringUtils.isBlank(keyword)) {
			keyword = TypesEnum.ALL.getName();
		}
		List<PartnerCouponsDTO> coupon_list = coupon.getCoupons(keyword.toUpperCase(), 0, null);
		Collections.sort(coupon_list, BackofficeUtil.compareCoupons);
		model.addAttribute("coupons", coupon_list);
		model.addAttribute("homes", home);
		model.addAttribute("category", category);
		model.addAttribute("viewer", "coupon");
		model.addAttribute("types", TypesEnum.values());
		model.addAttribute("keywords", keywords);
		if (StringUtils.isNotBlank(item)) {
			model.addAttribute("pro", item.split(",")[0]);
		}
		if (StringUtils.isNotBlank(keyword)) {
			model.addAttribute("keyword", keyword);
		}
		return "dashboard";
	}

	@RequestMapping(value = "/addCoupon")
	public String addCoupon(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "category", required = false) String category, @RequestParam(value = "item", required = false) String item,
			@ModelAttribute(value = "prod") PartnerCouponsDTO request, BindingResult bindingResult, Model model,
			@RequestParam(value = "delete", required = false) String delete, @RequestParam(value = "first", required = false) String first,
			@RequestParam(value = "rupee", required = false) String rupee,
			@RequestParam(value = "nocut", required = false) String nocut, HttpServletRequest reqst) {
		if ("on".equalsIgnoreCase(delete)) {
			coupon.deleteCoupon(request);
		}
		else {
			String offset = stprop.getValueOrDefault(BackofficeConstants.IST_TIME_OFFSET, String.valueOf(BackofficeConstants.IST_OFFSET));
			if (request.getExpireDate() != null && StringUtils.isNotBlank(first)) {
				request.setBelongsTo(new LinkedList<String>());
				request.getBelongsTo().add(request.getHome());
				if (StringUtils.isNotBlank(category) && !TypesEnum.ALL.getName().equalsIgnoreCase(category)) {
					request.getBelongsTo().add(category);
				}
				if (request.getExpireDate() != null) {
					long time = request.getExpireDate().getTime() - Long.parseLong(offset);
					request.setExpireDate(new Date(time));
				}
				else {
					request.setExpireDate(new Date(System.currentTimeMillis() + BackofficeConstants.ONE_DAY * 30));
				}
			}
			String cb = request.getAfferve_cb();
			if (StringUtils.isNotBlank(cb)) {
				String tmp = cb.replace("%", "").replace("₹", "").trim();
				if (NumberUtils.isNumber(tmp)) {
					if ("on".equalsIgnoreCase(rupee)) {
						cb = "₹ " + tmp;
					}
					else {
						/*if (!"on".equalsIgnoreCase(nocut)) {
							double p = Double.parseDouble(tmp) * (BackofficeConstants.CONVERSION_RATE + BackofficeConstants.REFFERAL_RATE);
							tmp = roundOff(p);
						}*/
						cb = tmp + "%";
					}
					request.setAfferve_cb(cb);
				}
				else {
					request.setAfferve_cb(cb);
				}
			}
			else {
				String hme = request.getHome();
				HomeEnum home = HomeEnum.getHome(hme);
				if (home != null) {
					cpn.generateCashbackString(home, request);
				}
			}
			if (StringUtils.isNotBlank(category) && !TypesEnum.ALL.getName().equalsIgnoreCase(category) && StringUtils.isBlank(first)) {
				List<String> tmp = request.getBelongsTo();
				if (!tmp.contains(category)){
					tmp.add(category);
				}
			}
			/*else if (StringUtils.isNotBlank(belongsTo)) {
				request.setBelongsTo(new LinkedList<String>());
				String[] tmp = belongsTo.replace("[", "").replace("]", "").split(",");
				for (String e : tmp) {
					request.getBelongsTo().add(e.toUpperCase().trim());
				}

			}*/
			if (StringUtils.isNotBlank(first)) {
				coupon.saveCoupon(request);
			}
			else {
				coupon.save(request);
			}
		}
		String resp = "redirect:/backoffice/coupon";
		if (StringUtils.isNotBlank(keyword)) {
			resp = resp + "?keyword=" + keyword;
		}
		else{
			resp = resp + "?keyword=" + request.getHome();
		}
		if (StringUtils.isNotBlank(item)) {
			resp = resp + "&pro=" + item;
		}
		return resp;
	}

}
