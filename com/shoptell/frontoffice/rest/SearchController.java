/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.frontoffice.rest;

import static com.shoptell.backoffice.BackofficeConstants.SP_CHAR_REMOVE_REGEX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.repository.dto.MergedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.PartnerCouponsDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.config.elasticsearch.ElasticSearchResponse;
import com.shoptell.config.elasticsearch.ElasticSearchUtil;
import com.shoptell.frontoffice.service.PartnerCoupons;
import com.shoptell.frontoffice.service.SearchService;
import com.shoptell.service.UserService;

@Scope("session")
@RestController
public class SearchController {

	@Inject
	private SearchService searchService;

	@Inject
	private ElasticSearchUtil elasticSearchUtil;

	@Inject
	private UserService userService;

	@Inject
	private PartnerCoupons partnerCoupons;

	private static final Logger log = LoggerFactory.getLogger(SearchController.class);

	/**
	 * GET http://localhost:8080/searchctrl?q=searchKeywords&id=id
	 */
	//update=false for bots
	@RequestMapping(value = "/searchctrl", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getProductsWithId(@RequestParam("q") String q, @RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "brand", required = false) boolean brand, 
			@RequestParam(value = "update", defaultValue="true") boolean allowUpdate, HttpServletRequest request) {
		
		if (!allowUpdate){
			log.info("getProductsWithId() Enters - BOT DETECTED");
		}
		
		if (StringUtils.isNotBlank(category)){
			category = category.replace(" ", "");
		}
		q = formatQuery(q);
		List<MergedProductInfoDTO> map = null;
		if (StringUtils.isNotBlank(q)) {
			map = searchService.search(q, id, category, brand, allowUpdate);
		}
		
		if (map == null){
			map = new LinkedList<MergedProductInfoDTO>();
		}
		
		if (!allowUpdate){
			log.info("getProductsWithId() Exit - BOT DETECTED");
		}
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/searchListCtrl", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getProductsList(@RequestParam("qList") List<String> qList, @RequestParam("count") int count, HttpServletRequest request) {
		List<List<?>> mapList = null;
		for (String q : qList) {
			q =  formatQuery(q);
			List<ReviewedProductInfoDTO> map = null;
			if (StringUtils.isNotBlank(q)) {
				CategoryEnum subCategory = CategoryEnum.getCategory(q);
				if (subCategory != null) {
					map = searchService.homePageProducts(subCategory, count);
				}
			}
			if (map != null) {
				if (mapList == null) {
					mapList = new ArrayList<List<?>>();
				}
				mapList.add(map);
			}
		}
		
		if(mapList == null){
			mapList = new LinkedList<List<?>>();
		}
		
		return new ResponseEntity<>(mapList, HttpStatus.OK);
	}
	
	private String formatQuery(String q) {
		if (StringUtils.isNotBlank(q)) {
			q = q.replaceAll(SP_CHAR_REMOVE_REGEX, " ").trim();
			q = q.replaceAll("\\s+", " ");
		}
		return q;
	}

	@RequestMapping(value = "/updateItemBlock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> updateItemBlock(@RequestParam("dataID") String dataID, HttpServletRequest request) {
		
		HashSet<ReviewedProductInfoDTO> updateList = new HashSet<ReviewedProductInfoDTO>();
		searchService.getReviewDataForId(UUID.fromString(dataID), SearchService.DO_ALL_UPDATE, updateList);
		searchService.updatePricesOfReviewData(updateList);
		HashSet<ReviewedProductInfoDTO> NewUpdateList = searchService.updatedReviewedProductInfoDTOOnly(updateList);
		return new ResponseEntity<>(NewUpdateList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/updateBestPriceOfEachBlock", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> updateBestPriceOfEachBlock(@RequestBody List<Map<String, String>> toUpdate, HttpServletRequest request) {
		
		HashSet<ReviewedProductInfoDTO> updateList = searchService.updateBestPriceOfEachBlock(toUpdate);
		HashSet<ReviewedProductInfoDTO> NewUpdateList = searchService.updatedReviewedProductInfoDTOOnly(updateList);
		return new ResponseEntity<>(NewUpdateList, HttpStatus.OK);
	}
	
	/**
	 * Get http://localhost:8080/productDescCtrl?pid=pid where pid is id of row
	 * from merged table that will be displayed on productDescription page.
	 */
	@RequestMapping(value = "/productDescCtrl", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getProductDesc(@RequestParam String pid, HttpServletRequest request) {

		List<MergedProductInfoDTO> map = null;
		if (StringUtils.isNotBlank(pid)) {
			map = searchService.findById(pid, true); // update is allowed
		}
		if (map == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(map, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/searchSuggestion", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> searchSuggestion(@RequestParam("searchKeyword") String searchKeyword, @RequestParam(value = "category", required = false) String category) {

		List<ElasticSearchResponse> resp = null;
		if (StringUtils.isNotBlank(searchKeyword)) {
			searchKeyword = searchKeyword.replaceAll(SP_CHAR_REMOVE_REGEX, " ").trim();
		}
		if (StringUtils.isNotBlank(category)){
			category = category.replace(" ", "");
		}
		if (StringUtils.isNotBlank(searchKeyword)) {
			resp = elasticSearchUtil.Search(searchKeyword, CategoryEnum.getCategory(category));
		}
		if (resp == null) {
			resp = new LinkedList<ElasticSearchResponse>();
		}
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	/**
	 * generate product url for Amazon, Ebay site
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/redirectToHomeUrlCtrl", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> redirectHome(@RequestBody Map<String, String> map, HttpServletRequest request) {
		log.debug("{} {} {} {}", map.get("userId"), map.get("home"), map.get("subCategoryName"), map.get("id"));
		String url = searchService.getRedirectedUrl(map);
		UrlClass urlClass = new UrlClass(url);
		return new ResponseEntity<>(urlClass, HttpStatus.OK);
	}

	@RequestMapping(value = "/generateUrl", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> generateUrl(@RequestBody Map<String, String> map, HttpServletRequest request) {
		log.debug("{} {}", map.get("userId"), map.get("productUrl"));
		String url = searchService.generateUrl(map);
		UrlClass urlClass = new UrlClass(url);
		return new ResponseEntity<>(urlClass, HttpStatus.OK);
	}

	@RequestMapping(value = "/ourPartnerLogoClicked", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> ourPartnerLogoClicked(@RequestBody Map<String, String> map, HttpServletRequest request) {
		log.debug("{} {}", map.get("userId"), map.get("productUrl"));
		if (searchService.ourPartnerLogoClicked(map)) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}

	@RequestMapping(value = "/signin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void signin(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.sendRedirect("/");
		} catch (IOException e) {
			log.error("", e);
		}
	}

	@RequestMapping(value = "/mailToAdmin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> mailToAdmin(@RequestBody String content, HttpServletRequest request) {
		// mailService.sendEmail("ravikumar.iitp@gmail.com", content +
		// " color constant missing", content, false, true);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/activateEmail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> activateEmail(@RequestParam("key") String key, @RequestParam("userId") String userId, HttpServletRequest request) {

		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		key = StringUtils.trim(key);
		if (StringUtils.isBlank(key)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		if (userService.activateEmail(userId, key)) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getCoupons", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getCoupons(@RequestParam("partnerName") String partnerName, @RequestParam("count") int count, HttpServletRequest request) {
		List<PartnerCouponsDTO> coupons = null;

		if (StringUtils.isNotBlank(partnerName)) {
			partnerName = partnerName.trim();
			coupons = partnerCoupons.getCoupons(partnerName, count, null);
		}

		if (coupons == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(coupons, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getRechargeCouponsWithCouponCode", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getRechargeCouponsWithCouponCode(@RequestParam("partnerName") String partnerName, @RequestParam("count") int count,
			HttpServletRequest request) {
		List<List<PartnerCouponsDTO>> result = new ArrayList<List<PartnerCouponsDTO>>();
		
		List<PartnerCouponsDTO> coupons = null;

		if (StringUtils.isNotBlank(partnerName)) {
			partnerName = partnerName.trim();
			coupons = partnerCoupons.getCoupons(partnerName, count, null);
		}
		if (coupons == null) {
			coupons = new ArrayList<>();
		}

		result.add(coupons);
		
		List<PartnerCouponsDTO> couponCodes = null;

		if (StringUtils.isNotBlank(partnerName)) {
			partnerName = partnerName.trim();
			couponCodes = partnerCoupons.getCouponCodes();
		}
		if (couponCodes == null) {
			couponCodes = new ArrayList<>();
		}

		result.add(couponCodes);
		

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
