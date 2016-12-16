/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice;

import static com.shoptell.backoffice.BackofficeUtil.getEndOfDay;
import static com.shoptell.backoffice.BackofficeUtil.getStartOfDay;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.utils.UUIDs;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.QueryMapper;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.backoffice.repository.dto.CBPaymentDTO;
import com.shoptell.backoffice.repository.dto.FeedbackDTO;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.TicketDTO;
import com.shoptell.backoffice.repository.dto.UserTransactionDTO;
import com.shoptell.domain.User;
import com.shoptell.util.streport.STReport;

@Scope("session")
@Named
public class BackofficeProcessor {
	private static final Logger log = LoggerFactory.getLogger(BackofficeProcessor.class);

	@Inject
	private BatchRepository batchRepository;
	
	@Inject
	private SelectQuery selectQuery;
	
	@Inject
	private STReport report;

	private List<HomeProductInfoDTO> prodList = null;
	private List<HomeProductInfoDTO> sublist = null;
	private Map<String, HomeProductInfoDTO> prodMap = new HashMap<String, HomeProductInfoDTO>();
	private int pagenum = 1;

	private List<ReviewedProductInfoDTO> revList = null;
	private List<ReviewedProductInfoDTO> revSublist = null;
	private Map<String, ReviewedProductInfoDTO> revMap = new HashMap<String, ReviewedProductInfoDTO>();
	private int revpagenum = 1;

	@SuppressWarnings("unchecked")
	public Map<String, Object> getProducts(boolean rd, int page, int pgSize, String home, String id, String tags, boolean ismerged) {
		Map<String, Object> respmap = new HashMap<String, Object>();
		if (!rd) {
			if (page > 1) {
				pagenum = page;
				if (prodList.size() > pgSize * page) {
					sublist = prodList.subList((page - 1) * pgSize, page * pgSize);
				}
				else if (prodList.size() > pgSize * (page - 1)) {
					sublist = prodList.subList((page - 1) * pgSize, prodList.size());
				}
				else {
					respmap.put("error_msg", "ALL ENTRIES OVER!!!");
				}
			}
			else {
				pagenum = 1;
				prodMap.clear();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ismerged", ismerged);
				map.put("instock", true);

				respmap.put("ismerged", ismerged);

				if (StringUtils.isNotBlank(home)) {
					map.put("subcategoryname", "SMARTPHONES");
					map.put("home", home);
					if (StringUtils.isNotBlank(id)) {
						map.put("id", id);
					}
				}
				if (StringUtils.isNotBlank(tags)) {
					Map<String, Object> contains = new HashMap<String, Object>();
					contains.put("tags", tags.toLowerCase());
					
					Map<String, Object> where = new HashMap<String, Object>();
					where.put("instock", true);
					
					ResultSet rs = selectQuery.selectAll(TableEnum.home_product_info,where,contains, true);
					prodList = QueryMapper.homeProductInfoDTO().map(rs).all();
				}
				else {
					if (map.size() > 2){
						prodList = (List<HomeProductInfoDTO>) batchRepository.selectAll(TableEnum.home_product_info.name(), HomeProductInfoDTO.class, null, map,
							null);
					}
				}
				if (prodList != null && prodList.size() > 0) {

					sublist = prodList;
					if (prodList.size() > pgSize) {
						sublist = prodList.subList(0, pgSize);
					}
					for (HomeProductInfoDTO item : prodList) {
						String key = item.getHome() + "_" + item.getId();
						prodMap.put(key, item);
					}
				}
			}
		}

		if (prodList != null && prodList.size() > 0) {
			int lastpg = prodList.size() / pgSize;
			respmap.put("lastpg", lastpg + 1);
			respmap.put("pagenum", pagenum);
		}

		if (sublist != null && sublist.size() > 0) {
			respmap.put("sublist", sublist);
		}
		return respmap;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getReviewedProducts(boolean rd, int page, int pgSize, String home, String id, String tags, boolean ismerged, String category, String mergeId) {
		Map<String, Object> respmap = new HashMap<String, Object>();
		if (!rd) {
			if (page > 0) {
				revpagenum = page;
				if (revList.size() > pgSize * page) {
					revSublist = revList.subList((page - 1) * pgSize, page * pgSize);
				}
				else if (revList.size() > pgSize * (page - 1)) {
					revSublist = revList.subList((page - 1) * pgSize, revList.size());
				}
				else {
					respmap.put("error_msg", "ALL ENTRIES OVER!!!");
				}
			}
			else {
				revpagenum = 1;
				revMap.clear();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ismerged", ismerged);

				if (StringUtils.isNotBlank(home)) {
					map.put("subcategoryname", category);
					map.put("home", home);
					if (StringUtils.isNotBlank(id)) {
						map.put("id", id);
					}
				}

				if (StringUtils.isNotBlank(tags)) {
					Map<String, Object> where = new HashMap<String, Object>();
					where.put("subcategoryname", category);
					Map<String, Object> contains = new HashMap<String, Object>();
					contains.put("tags", tags.toLowerCase());
					
					ResultSet rs = selectQuery.selectAll(TableEnum.reviewed_product_info, where, contains, true);
					revList = QueryMapper.reviewedProductInfoDTO().map(rs).all();
				}
				else if (StringUtils.isNotBlank(mergeId)){
					Map<String, Object> mp = new HashMap<String, Object>();
					mp.put("mergeprodinfoid", UUID.fromString(mergeId));
					revList = (List<ReviewedProductInfoDTO>) batchRepository.selectAll(TableEnum.reviewed_product_info.name(), ReviewedProductInfoDTO.class, null, mp, null);
				}
				else {
					revList = (List<ReviewedProductInfoDTO>) batchRepository.selectAll(TableEnum.reviewed_product_info.name(), ReviewedProductInfoDTO.class,
							null, map, null);
				}
				if (revList != null && revList.size() > 0) {

					revSublist = revList;
					if (revList.size() > pgSize) {
						revSublist = revList.subList(0, pgSize);
					}
					for (ReviewedProductInfoDTO item : revList) {
						String key = item.getHome() + "_" + item.getId();
						revMap.put(key, item);
					}
				}
			}
		}

		if (revList != null && revList.size() > 0) {
			int lastpg = revList.size() / pgSize;
			respmap.put("lastpg", lastpg + 1);
			respmap.put("pagenum", revpagenum);
		}

		if (revList != null && revList.size() > 0) {
			respmap.put("sublist", revSublist);
		}
		return respmap;
	}

	@SuppressWarnings("unchecked")
	public long getTotalRegisteredUsers(Model model, String view) {
		List<User> users = (List<User>) batchRepository.selectAll(TableEnum.user.name(), User.class, null, null, null);
		Map<String, User> map = new HashMap<String, User>();
		for (User user : users) {
			String key = user.getEmail();
			if (StringUtils.isNotBlank(key) && !map.containsKey(key)) {
				map.put(key, user);
			}
		}
		return map.size();
	}

	public long getTotalTransactionsYesterday(Model model, String view) {
		Date date = new Date(System.currentTimeMillis()-BackofficeConstants.ONE_DAY);
		UUID low = UUIDs.startOf(getStartOfDay(date).getTime());
		UUID high = UUIDs.endOf(getEndOfDay(date).getTime());
		List<UserTransactionDTO> list = batchRepository.userTransactions(low, high);
		if (list != null){
			return list.size();
		}
		return 0;
	}

	public long getTotalTransactionsToday(Model model, String view) {
		Date date = new Date(System.currentTimeMillis());
		UUID low = UUIDs.startOf(getStartOfDay(date).getTime());
		UUID high = UUIDs.endOf(date.getTime());
		List<UserTransactionDTO> list = batchRepository.userTransactions(low, high);
		if (list != null){
			return list.size();
		}
		return 0;
	}

	public long getTotalConversionThisWeek(Model model, String view) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getTotalReviewedProducts(Model model, String view) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getTotalProducts(Model model, String view) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getTotalLiveCoupons(Model model, String view) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getTotalFeedbackToday(Model model, String view) {
		List<FeedbackDTO> list = report.getOpenFeedbacks();
		if (list != null && list.size() > 0) {
			if (StringUtils.isNotBlank(view) && "FeedbackToday".equals(view)) {
				model.addAttribute("FeedbackToday", list);
			}
			return list.size();
		}
		return 0;
	}

	public long getTotalTicketsOpen(Model model, String view) {
		List<TicketDTO> list = report.getOpenTickets();
		if (list != null && list.size() > 0) {
			if (StringUtils.isNotBlank(view) && "TicketsOpen".equals(view)) {
				model.addAttribute("TicketsOpen", list);
			}
			return list.size();
		}
		return 0;
	}

	public String saveReviewProducts(ReviewedProductInfoDTO request, String features, boolean disable, String[] prop) {
		String key = request.getHome() + "_" + request.getId();
		ReviewedProductInfoDTO item = revMap.get(key);
		if (disable && item != null){
			item.setDisabled(!item.isDisabled());
			item.setReviewed(true);
			item.setMerged(false);
			batchRepository.save(item);
		}
		else if (item != null) {
			try {
				item.setReviewed(true);
				if (!request.isDisabled()){
					item.setMerged(false);
				}

				if (StringUtils.isNotBlank(request.getProductBrand())) {
					item.setProductBrand(request.getProductBrand().toUpperCase().trim());
				}
				else if (StringUtils.isNotBlank(request.getProductBrand()) && StringUtils.isBlank(request.getProductBrand())) {
					item.setProductBrand(null);
				}
				if (StringUtils.isNotBlank(request.getProductSubBrand())) {
					item.setProductSubBrand(request.getProductSubBrand().toUpperCase().trim());
				}
				else if (StringUtils.isNotBlank(item.getProductSubBrand()) && StringUtils.isBlank(request.getProductSubBrand())) {
					item.setProductSubBrand(null);
				}
				if (StringUtils.isNotBlank(request.getSeries())) {
					item.setSeries(request.getSeries().toUpperCase().trim());
				}
				else if (StringUtils.isNotBlank(item.getSeries()) && StringUtils.isBlank(request.getSeries())) {
					item.setSeries(null);
				}
				if (StringUtils.isNotBlank(request.getModel())) {
					item.setModel(request.getModel().toUpperCase().trim());
				}
				else if (StringUtils.isNotBlank(item.getModel()) && StringUtils.isBlank(request.getModel())) {
					item.setModel(null);
				}
				if (StringUtils.isNotBlank(request.getColor())) {
					item.setColor(request.getColor().toUpperCase().trim());
				}
				else if (StringUtils.isNotBlank(item.getColor()) && StringUtils.isBlank(request.getColor())) {
					item.setColor(null);
				}

				if (StringUtils.isNotBlank(features)) {
					item.setFeatures(new LinkedList<String>());
					String[] feature = features.trim().toUpperCase().replace("[", "").replace("]", "").trim().split(";");
					for (String tmp : feature) {
						String[] element = tmp.split(":");
						if (element.length > 1) {
							item.getFeatures().add(element[0].trim() + " : " + element[1].trim());
						}
					}
				}
				String[] array = CategoryEnum.getProperties(request.getSubCategoryName());
				if (array != null && array.length > 0 && prop != null && prop.length > 0){
					int len =  Math.min(array.length, prop.length);
					for (int i=0; i< len; i++){
						String tmp = prop[i];
						String propkey = array[i];
						if (StringUtils.isNotBlank(tmp) && StringUtils.isNotBlank(propkey)){
							tmp = tmp.trim().toUpperCase();
							tmp = tmp.replaceAll("\\s+", " ").replaceAll("\\sGB$", "GB").replaceAll("\\sGB\\s", "GB ");
							item.getProperties().put(propkey, tmp);
						}
					}
				}

				batchRepository.save(item);
			} catch (Exception e) {
				log.error("", e);
				return e.getMessage();
			}
		}
		return null;
	}

	public long getTotalLiveOffers(Model model, String view) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getTotalPendingPayments(Model model, String view) {
		List<CBPaymentDTO> list = report.getOpenPayments();
		if (list != null && list.size() > 0) {
			if (StringUtils.isNotBlank(view) && "PendingPayments".equals(view)) {
				model.addAttribute("PendingPayments", list);
			}
			return list.size();
		}
		return 0;
	}
}