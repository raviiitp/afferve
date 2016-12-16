/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.frontoffice.service;

import static com.shoptell.backoffice.BackofficeConstants.ENABLE_ES_RANK_UPDATE;
import static com.shoptell.backoffice.BackofficeConstants.FETCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.HOMES;
import static com.shoptell.backoffice.BackofficeConstants.IST_OFFSET;
import static com.shoptell.backoffice.BackofficeConstants.IST_TIME_OFFSET;
import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;
import static com.shoptell.backoffice.BackofficeConstants.MAX_ITEM_SIZE;
import static com.shoptell.backoffice.BackofficeConstants.ONE_HOUR;
import static com.shoptell.backoffice.BackofficeConstants.SP_CHAR_REMOVE_REGEX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.home.amazon.ItemLookupApi;
import com.shoptell.backoffice.home.flipkart.FlipkartShoppingApi;
import com.shoptell.backoffice.home.snapdeal.SnapdealShoppingApi;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.QueryMapper;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.backoffice.repository.UpdateQuery;
import com.shoptell.backoffice.repository.dto.BankDiscountDTO;
import com.shoptell.backoffice.repository.dto.ColorMapDTO;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.MergeProductPropertiesDTO;
import com.shoptell.backoffice.repository.dto.MergedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.UserTransactionDTO;
import com.shoptell.backoffice.score.MatchScore;
import com.shoptell.config.elasticsearch.ElasticSearchUtil;

/**
 * @author abhishekagarwal
 *
 */
@Scope("session")
@Named(value = "SearchService")
public class SearchService extends Service {
	private static final Logger log = LoggerFactory.getLogger(SearchService.class);

	public static final String COLOR_REGEX = "(STAINLESS|CHAMPAGNE|TITANIUM|PLATINUM|GRAPHITE|VOILET|ORANGE|SILVER|YELLOW|PURPLE|BLACK|BROWN|CHROME|STEEL|GREEN|WHITE|BLUE|GOLD|GRAY|GREY|MINT|PINK|ROSE|RED)";

	private static final String COLOR_SPLIT = "\\s|,|;|\\/|-|\\+|&|and|AND|_|!";

	public static final String DO_NOT_UPDATE = "DO_NOT_UPDATE";

	public static final String DO_ALL_UPDATE = "DO_ALL_UPDATE";

	public static final String BEST_PRICE_UPDATE = "BEST_PRICE_UPDATE";

	@Inject
	private MatchScore match;

	@Inject
	private SelectQuery selectQuery;

	@Inject
	private UpdateQuery updateQuery;

	@Inject
	private URLRedirector urlRedirect;

	@Inject
	private BatchRepository repo;

	@Inject
	private ElasticSearchUtil es;

	@Inject
	private FlipkartShoppingApi flipkart;

	@Inject
	private SnapdealShoppingApi snapdeal;

	@Inject
	private ItemLookupApi amazon;
	
	private ExecutorService pools[] = new ExecutorService[3];

	@PostConstruct
	public void start() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		tableName = TableEnum.merged_product_info.name();
	}
	
	public List<MergedProductInfoDTO> search(String search_key, String id, String category, boolean brand, boolean allowUpdate) {
		CategoryEnum subCategory = CategoryEnum.getCategory(category);
		CategoryEnum srchCategory = CategoryEnum.getCategory(search_key);
		if (subCategory != null && subCategory.name().equalsIgnoreCase(search_key)) {
			return popularProducts(subCategory, allowUpdate);
		}
		else if (srchCategory != null && !CategoryEnum.ALL.equals(srchCategory)) {
			return popularProducts(srchCategory, allowUpdate);
		}
		try {
			Set<UUID> uniqueItems = new HashSet<UUID>(); // Set to contain only
															// unique
															// mergedItems

			TreeMap<Float, MergedProductInfoDTO> map = new TreeMap<Float, MergedProductInfoDTO>();

			boolean maxLimit = true;
			String productBrand = null;

			if (StringUtils.isNotBlank(id)) {
				// Increased Views Count
				es.updateViews(id);
				// Selected Item
				Map<String, Object> where = new HashMap<String, Object>();
				where.put("id", UUID.fromString(id));
				ResultSet rs = selectQuery.selectAll(TableEnum.merged_product_info, where);
				maxLimit = addSearchElements(0f, rs, map, uniqueItems, search_key);
				if (map.size() > 0) {
					Iterator<Entry<Float, MergedProductInfoDTO>> it = map.entrySet().iterator();
					if (it.hasNext()) {
						Entry<Float, MergedProductInfoDTO> element = it.next();
						productBrand = element.getValue().getProductBrand();
					}
				}
			}

			if (maxLimit) {
				search_key = search_key.replaceAll(SP_CHAR_REMOVE_REGEX, "").replaceAll("\\s+", " ").trim();
				List<String> list = Arrays.asList(search_key.toLowerCase().split(" "));

				Map<String, Object> values = new HashMap<String, Object>();
				Map<String, Object> contains = new HashMap<String, Object>();

				if (subCategory != null) {
					values.put("subcategoryname", subCategory.name());
				}

				values.put("productbrand", search_key.toUpperCase());
				ResultSet rs = selectQuery.selectAll(TableEnum.merged_product_info, values, contains, true);
				boolean noBrandElement = addBrandElements(1f, rs, map, search_key);
				
				if (noBrandElement && (list.size() > 0)) {
					values.remove("productbrand");
					if (StringUtils.isNotBlank(productBrand)) {
						values.put("productbrand", productBrand.toUpperCase());
					}
					contains.put("tags", list);
					ResultSet ress = selectQuery.selectAll(TableEnum.merged_product_info, values, contains, true);
					maxLimit = addSearchElements(1f, ress, map, uniqueItems, search_key);

					// One Token Removed
					if (list.size() > 2) {
						for (int i = list.size() - 1; i >= 0; i--) {
							if (maxLimit) {
								ArrayList<String> newList = new ArrayList<String>(list);
								newList.remove(i);
								contains.clear();
								contains.put("tags", newList);
								ResultSet res = selectQuery.selectAll(TableEnum.merged_product_info, values, contains, true);
								maxLimit = addSearchElements(2f, res, map, uniqueItems, search_key);
							}
							else {
								break;
							}
						}
					}

					// One Token Check
					if (list.size() > 1) {
						for (int i = 0; i < list.size(); i++) {
							if (maxLimit) {
								contains.clear();
								contains.put("tags", list.get(i));
								ResultSet res = selectQuery.selectAll(TableEnum.merged_product_info, values, contains, true);
								maxLimit = addSearchElements(3f, res, map, uniqueItems, search_key);
							}
							else {
								break;
							}
						}
					}
				}
			}

			List<MergedProductInfoDTO> outcome = processOutcome(map, allowUpdate);
			// log.info("Size of Search Map - {}", map.toString());
			if (outcome != null && outcome.size() > 0) {
				if (Boolean.valueOf(stprop.getValueOrDefault(ENABLE_ES_RANK_UPDATE, "false"))) {
					es.updateRank(outcome);
				}
			}
			return outcome;
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("SEARCH ERROR", e);
		}
		return null;
	}

	private boolean addBrandElements(float computeScore, ResultSet rs, TreeMap<Float, MergedProductInfoDTO> map, String search_key) {
		Iterator<MergedProductInfoDTO> results = QueryMapper.mergedProductInfoDTO().map(rs).iterator();
		if (!results.hasNext()) {
			return true; // no element
		}
		SortedSet<MergedProductInfoDTO> set = new TreeSet<MergedProductInfoDTO>(BackofficeUtil.compareMergeProductInfoOnPrice);
		while (results.hasNext()) {
			MergedProductInfoDTO selectedItem = results.next();
			if (selectedItem != null && !selectedItem.isDisabled()) {
				set.add(selectedItem);
			}
		}
		if (set.size() > MAX_ITEM_SIZE) {
			for (int i = 0; i < set.size() - MAX_ITEM_SIZE; i++) {
				set.remove(set.last());
			}
		}
		List<MergedProductInfoDTO> list = new LinkedList<MergedProductInfoDTO>(set);
		Collections.sort(list, BackofficeUtil.compareMergeProductInfo);
		for (MergedProductInfoDTO value : list) {
			computeScore += 0.000001;
			map.put(computeScore, value);
		}
		return false;
	}

	private boolean addSearchElements(float computeScore, ResultSet rs, TreeMap<Float, MergedProductInfoDTO> map, Set<UUID> uniqueItems, String search_key) {
		Iterator<MergedProductInfoDTO> results = QueryMapper.mergedProductInfoDTO().map(rs).iterator();
		if (results != null) {
			while (results.hasNext()) {
				MergedProductInfoDTO selectedItem = results.next();
				if (selectedItem != null && !selectedItem.isDisabled() && !uniqueItems.contains(selectedItem.getId())) {
					uniqueItems.add(selectedItem.getId());
					String name = selectedItem.getName();
					if (StringUtils.isNotBlank(name)) {
						float score = match.getScore(name, search_key);
						if (score > 0) {
							float finalScore = computeScore + score;
							while (map.containsKey(finalScore)) {
								finalScore += 0.000001;
							}
							selectedItem.setRelevanceRank(finalScore);
							map.put(finalScore, selectedItem);
							//TODO Remove in future on pagination
							if (map.size() > MAX_ITEM_SIZE){
								break;
							}
						}
					}
				}
			}
		}
		return map.size() < MAX_ITEM_SIZE;
	}

	public List<ReviewedProductInfoDTO> homePageProducts(CategoryEnum subCategory, int count) {
		List<ReviewedProductInfoDTO> data = new LinkedList<ReviewedProductInfoDTO>();
		for (String home : HOMES) {
			HashSet<ReviewedProductInfoDTO> list = new HashSet<ReviewedProductInfoDTO>();
			Set<String> set = repo.getPopularProducts(subCategory, home, true);
			if (set != null && set.size() > 0) {
				for (String id : set) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("home", home);
					map.put("subcategoryname", subCategory.name());
					map.put("id", id);
					ResultSet rs = selectQuery.selectAll(TableEnum.reviewed_product_info, map, 1);
					Iterator<ReviewedProductInfoDTO> rows = QueryMapper.reviewedProductInfoDTO().map(rs).iterator();
					if (rows != null && rows.hasNext()){
						ReviewedProductInfoDTO tmp = rows.next();
						if (tmp != null && !tmp.isDisabled()){
							list.add(tmp);
						}
					}
				}
			}
			//updatePricesOfReviewData(list);
			data.addAll(list);
		}
		if (data.size() > 0) {
			Set<String> set = new HashSet<String>();
			Iterator<ReviewedProductInfoDTO> itr = data.iterator();

			while (itr.hasNext()) {
				ReviewedProductInfoDTO tmp = itr.next();
				String name = tmp.getName();

				if (set.contains(name)) {
					itr.remove();
				}
				else {
					set.add(name);
				}
			}
		}
		if (data != null && count > 0 && count < data.size()) {
			return data.subList(0, count);
		}
		return data;
	}

	private List<MergedProductInfoDTO> popularProducts(CategoryEnum subCategory, boolean allowUpdate) {
		Set<UUID> ids = new HashSet<UUID>();
		for (String home : HOMES) {
			Set<String> set = repo.getPopularProducts(subCategory, home, false);
			if (set != null && set.size() > 0) {
				Statement statement = QueryBuilder.select("mergeprodinfoid").from(keyspace, TableEnum.reviewed_product_info.name())
						.where(QueryBuilder.eq("home", home)).and(QueryBuilder.eq("subcategoryname", subCategory.name()))
						.and(QueryBuilder.in("id", new LinkedList<String>(set)));
				statement = statement.setFetchSize(FETCHSIZE);
				ResultSet rs = session.execute(statement);
				Iterator<Row> rows = rs.iterator();
				if (rows != null) {
					while (rows.hasNext()) {
						Row tmp = rows.next();
						UUID mid = tmp.getUUID("mergeprodinfoid");
						if (mid != null) {
							ids.add(mid);
						}
					}
				}
			}
		}
		if (ids.size() > 0) {
			Statement statement = QueryBuilder.select("mergeprodid").from(keyspace, TableEnum.merged_product_properties.name()).allowFiltering()
					.where(QueryBuilder.in("id", new LinkedList<UUID>(ids)));
			statement = statement.setFetchSize(FETCHSIZE);
			ResultSet rs = session.execute(statement);
			List<Row> rows = rs.all();
			if (rows != null && rows.size() > 0) {
				ids.clear();
				for (int i = 0; i < rows.size(); i++) {
					Row tmp = rows.get(i);
					UUID mid = tmp.getUUID("mergeprodid");
					if (mid != null) {
						ids.add(mid);
					}
				}
				if (ids.size() > 0) {
					TreeMap<Float, MergedProductInfoDTO> map = new TreeMap<Float, MergedProductInfoDTO>();
					float f = 1f;
					
					statement = QueryBuilder.select().all().from(keyspace, tableName).where(QueryBuilder.in("id", new LinkedList<UUID>(ids)));
					statement = statement.setFetchSize(FETCHSIZE);
					List<MergedProductInfoDTO> results = QueryMapper.mergedProductInfoDTO().map(session.execute(statement)).all();
					
					Collections.sort(results, BackofficeUtil.compareMergeProductInfo);
					
					for (MergedProductInfoDTO row : results) {
						if (row.isDisabled()) {
							continue;
						}
						map.put(f++, row);
					}

					List<MergedProductInfoDTO> outcome = processOutcome(map, allowUpdate);
					return outcome;
				}
			}
		}
		return null;
	}

	public List<MergedProductInfoDTO> findById(String id, boolean allowUpdate) {
		try {
			TreeMap<Float, MergedProductInfoDTO> map = new TreeMap<Float, MergedProductInfoDTO>();
			MergedProductInfoDTO row = QueryMapper.mergedProductInfoDTO().get(UUID.fromString(id));
			if (row != null) {
				map.put(1.0f, row);
				List<MergedProductInfoDTO> response = processOutcome(map, allowUpdate);
				return response;
			}
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("SEARCH ERROR", e);
		}
		return null;
	}

	public String getRedirectedUrl(Map<String, String> map) {
		String url = urlRedirect.redirectHomeUrl(map);
		if (url == null) {
			url = "/";
		}
		return url;
	}

	public String generateUrl(Map<String, String> map) {
		String url = urlRedirect.generateUrl(map);
		if (url == null) {
			url = "/";
		}
		return url;
	}

	@SuppressWarnings("unchecked")
	private List<MergedProductInfoDTO> processOutcome(TreeMap<Float, MergedProductInfoDTO> map, boolean allowUpdate) {
		try {
			if (map != null && map.size() > 0) {
				long time = System.currentTimeMillis();
				HashSet<ReviewedProductInfoDTO> updateList = new HashSet<ReviewedProductInfoDTO>();
				for (Iterator<Entry<Float, MergedProductInfoDTO>> itm = map.entrySet().iterator(); itm.hasNext();) {
					Entry<Float, MergedProductInfoDTO> mapEntry = itm.next();
					MergedProductInfoDTO element = mapEntry.getValue();

					boolean valueExists = processElement(element, updateList);
					if (!valueExists) {
						itm.remove();
					}
				}
				if (allowUpdate){
					updatePricesOfReviewData(updateList);
				}
				
				ArrayList<MergedProductInfoDTO> outcome = new ArrayList<MergedProductInfoDTO>(map.values());
				log.info("Time Take - {} MS", System.currentTimeMillis() - time);
				
				if (allowUpdate && outcome.size() > 0) {
					updateMergedData((ArrayList<MergedProductInfoDTO>) SerializationUtils.clone(outcome));
				}
				return outcome;
			}
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("SEARCH PROCESSING ERROR", e);
		}
		return null;
	}

	@Async
	private void updateMergedData(ArrayList<MergedProductInfoDTO> clone) {
		BatchStatement batch = new BatchStatement();
		for (MergedProductInfoDTO data : clone) {
			data.update();
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("modifiedon", new Date(System.currentTimeMillis()));
			values.put("salesrank", data.getSalesRank());
			values.put("bestprice", data.getBestPrice());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", data.getId());
			Statement statement = updateQuery.updateQueryStatement(TableEnum.merged_product_info, values, map);
			batch.add(statement);
		}
		session.execute(batch);
	}

	public boolean ourPartnerLogoClicked(Map<String, String> map) {
		UserTransactionDTO txn = new UserTransactionDTO(map.get("userId"), map.get("home"));
		txn.setUrl(map.get("productUrl"));
		txn.setTrackingId(null);
		txn.setTrackingSubId(null);
		try {
			repo.save(txn);
			return true;
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("ourPartnerLogoClicked ERROR", e);
		}
		return false;
	}

	private boolean processElement(MergedProductInfoDTO element, HashSet<ReviewedProductInfoDTO> updateList) throws InterruptedException {
		// To get all properties set associated with the merge row
		Iterator<MergeProductPropertiesDTO> propItr = repo.getMergeProductProperties(element.getId());
		if (propItr != null && propItr.hasNext()) {
			int imagePriority = 6;
			Map<UUID, ColorMapDTO> dataMap = new HashMap<UUID, ColorMapDTO>();
			while (propItr.hasNext()) {
				MergeProductPropertiesDTO dto = propItr.next();
				Set<ReviewedProductInfoDTO> itemSet = getReviewDataForId(dto.getId(), DO_NOT_UPDATE, updateList);
				if (itemSet != null && itemSet.size() > 0) {
					dataMap.put(dto.getId(), new ColorMapDTO(dto, itemSet));
					for (ReviewedProductInfoDTO item : itemSet) {
						HomeEnum home = HomeEnum.fromName(item.getHome());
						if (home.getPriority() < imagePriority) {
							imagePriority = home.getPriority();
							element.setImageUrlMap(item.getImageUrls());
						}
						if (item.getFeatures() != null && item.getFeatures().size() > 0 && (element.getFeatures() == null || element.getFeatures().size() < 1)) {
							element.setFeatures(item.getFeatures());
						}
					}
				}
			}
			if (dataMap.size() > 0) {
				element.setData(dataMap);
				return true;
			}
		}
		return false;
	}

	@Async
	private void updateReviewData(Set<ReviewedProductInfoDTO> clone) {
		if (clone != null) {
			BatchStatement batch = new BatchStatement();
			for (ReviewedProductInfoDTO productInfo : clone) {
				if (productInfo.isUpdated()) {
					Map<String, Object> values = new HashMap<String, Object>();
					values.put("sellingPrice", productInfo.getSellingPrice());
					values.put("inStock", productInfo.isInStock());
					values.put("salesRank", productInfo.getSalesRank());
					values.put("mrp", productInfo.getMrp());
					values.put("modifiedOn", new Date(System.currentTimeMillis()));

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("home", productInfo.getHome());
					map.put("subCategoryName", productInfo.getSubCategoryName());
					map.put("id", productInfo.getId());

					Statement statement = updateQuery.updateQueryStatement(TableEnum.reviewed_product_info, values, map);
					batch.add(statement);
				}
			}
			session.execute(batch);
			batch.clear();
		}
	}

	// Call Without Update
	public Set<ReviewedProductInfoDTO> getReviewDataForId(UUID id, String updateStatus, HashSet<ReviewedProductInfoDTO> updateList) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mergeProdInfoId", id);
		ResultSet rs = selectQuery.selectAll(TableEnum.reviewed_product_info, map);
		Iterator<ReviewedProductInfoDTO> itr = QueryMapper.reviewedProductInfoDTO().map(rs).iterator();
		return getDataToUpdate(itr,updateStatus,updateList);
	}

	private Set<ReviewedProductInfoDTO> getDataToUpdate(Iterator<ReviewedProductInfoDTO> itr, String updateStatus, HashSet<ReviewedProductInfoDTO> updateList) {
		Set<ReviewedProductInfoDTO> data = null;
		double bestDeal = Double.MAX_VALUE;
		HashSet<ReviewedProductInfoDTO> bestDealList = new HashSet<ReviewedProductInfoDTO>();
		if (itr != null && itr.hasNext()) {
			data = new HashSet<ReviewedProductInfoDTO>();
			while (itr.hasNext()) {
				ReviewedProductInfoDTO tmp = itr.next();
				//log.info(tmp.toString());
				if (tmp.isDisabled()) {
					continue;
				}

				tmp.setProductUrl(null);
				processColor(tmp);

				setDynamicProperties(tmp, tmp.getHome(), tmp.getSubCategoryName());
				data.add(tmp);

				if (!DO_NOT_UPDATE.equals(updateStatus)) {
					boolean shouldUpdate = (tmp.getModifiedOn() == null || tmp.getModifiedOn().before(
							new Date(System.currentTimeMillis() - ONE_HOUR)));

					if (shouldUpdate) {
						if (DO_ALL_UPDATE.equals(updateStatus)) {
							bestDealList.add(tmp);
						}
						else {
							double netAmount = tmp.getSellingPrice() - tmp.getMaxCBAmount();
							if (Math.abs(bestDeal - netAmount) < 0.0000001) {
								bestDealList.add(tmp);
							}
							else if (bestDeal > netAmount) {
								bestDealList.clear();
								bestDealList.add(tmp);
								bestDeal = netAmount;
							}
						}
					}
				}
			}
		}
		if (bestDealList.size() > 0) {
			updateList.addAll(bestDealList);
		}
		return data;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updatePricesOfReviewData(HashSet<ReviewedProductInfoDTO> data) {
		if (data == null || data.size() == 0) {
			return;
		}
		
		List<Future<Runnable>> list = new LinkedList<Future<Runnable>>();
		for (ReviewedProductInfoDTO tmp : data) {
			if (HomeEnum.FLIPKART.name().equalsIgnoreCase(tmp.getHome())) {
				if (pools[0] == null) {
					pools[0] = Executors.newFixedThreadPool(10);
				}
				Future thread = pools[0].submit(new Runnable() {
					@Override
					public void run() {
						HomeProductInfoDTO element = null;
						try {
							element = flipkart.priceCall(tmp.getId(), tmp.getSubCategoryName());
							if (element != null) {
								tmp.setSellingPrice(element.getSellingPrice());
								tmp.setInStock(element.isInStock());
								tmp.setSalesRank(element.getSalesRank());
								tmp.setMrp(element.getMrp());
								tmp.setUpdated(true);
							}
						} catch (InterruptedException e) {
						}
					}
				});
				list.add(thread);
			}
			else if (HomeEnum.AMAZON.name().equalsIgnoreCase(tmp.getHome())) {
				if (pools[1] == null) {
					pools[1] = Executors.newFixedThreadPool(10);
				}
				Future thread = pools[1].submit(new Runnable() {
					@Override
					public void run() {
						HomeProductInfoDTO element = null;
						try {
							element = amazon.priceCall(tmp.getId(), tmp.getSubCategoryName());
							if (element != null) {
								tmp.setSellingPrice(element.getSellingPrice());
								tmp.setInStock(element.isInStock());
								tmp.setSalesRank(element.getSalesRank());
								tmp.setMrp(element.getMrp());
								tmp.setUpdated(true);
							}
						} catch (InterruptedException e) {
						}
					}
				});
				list.add(thread);
			}
			else if (HomeEnum.SNAPDEAL.name().equalsIgnoreCase(tmp.getHome())) {
				if (pools[2] == null) {
					pools[2] = Executors.newFixedThreadPool(10);
				}
				Future thread = pools[2].submit(new Runnable() {
					@Override
					public void run() {
						HomeProductInfoDTO element = null;
						try {
							element = snapdeal.priceCall(tmp.getId(), tmp.getSubCategoryName());
							if (element != null) {
								tmp.setSellingPrice(element.getSellingPrice());
								tmp.setInStock(element.isInStock());
								tmp.setSalesRank(element.getSalesRank());
								tmp.setMrp(element.getMrp());
								tmp.setUpdated(true);
							}
						} catch (InterruptedException e) {
						}
					}
				});
				list.add(thread);
			}

		}
		for (Future<Runnable> runn : list) {
			try {
				runn.get(10, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				log.info("Cancel Update");
				runn.cancel(true);
			} catch (Exception e) {
				msgLog.addError(e);
			}
		}

		for (ReviewedProductInfoDTO tmp : data) {
			setDynamicProperties(tmp, tmp.getHome(), tmp.getSubCategoryName());
		}

		updateReviewData((HashSet<ReviewedProductInfoDTO>) SerializationUtils.clone(data));
	}

	@PreDestroy
	private void poolShutDown() {
		if (pools != null) {
			for (ExecutorService pool : pools) {
				if (pool != null) {
					pool.shutdownNow();
				}
			}
		}
	}

	private void setDynamicProperties(ReviewedProductInfoDTO set, String home, String categoryName) {
		set.setCbRate(CBRateService.getRate(home, categoryName));
		set.setMaxCBAmount(CBRateService.getMaxAmount(home, categoryName, set.getSellingPrice()));
		verifyAndAddDiscounts(home, categoryName, set);
	}

	private void processColor(ReviewedProductInfoDTO tmp) {
		String color = tmp.getColor();
		if (StringUtils.isNotBlank(color) && !color.equalsIgnoreCase("null")) {
			Pattern p = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = p.matcher(color);
			while (matcher.find()) {
				if (StringUtils.isNotBlank(matcher.group(0))) {
					color = matcher.group(0).trim().toUpperCase();
					break;
				}
			}
		}
		if (StringUtils.isNotBlank(color)) {
			color = color.replaceAll(COLOR_SPLIT, " ").replaceAll("\\s+", " ").trim();
			tmp.setColor(color);
		}
		else {
			tmp.setColor(null);
		}
	}

	private void verifyAndAddDiscounts(String home, String categoryName, ReviewedProductInfoDTO set) {
		try {
			List<BankDiscountDTO> list = BankDiscountsService.getDiscount(home, categoryName);
			String offset = stprop.getValueOrDefault(IST_TIME_OFFSET, String.valueOf(IST_OFFSET));
			Date currDate = new Date(System.currentTimeMillis() + Long.parseLong(offset));
			if (list != null) {
				for (BankDiscountDTO dis : list) {
					if (!dis.isActive() || dis.getEndDate().before(currDate) || dis.getStartDate().after(currDate) /*
																													 * ||
																													 * dis
																													 * .
																													 * isAppOnly
																													 * (
																													 * )
																													 */
							|| dis.getMinBuyAmount() > set.getSellingPrice()) {
						continue;
					}
					set.getDiscounts().add(dis);
				}
			}
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("BANK DISCOUNT ERROR", e);
		}
	}
	
	public HashSet<ReviewedProductInfoDTO> updateBestPriceOfEachBlock (List<Map<String, String>> list){
		Map<String, Map<String, Set<String>>> data = new HashMap<String, Map<String,Set<String>>>();
		if (list != null && list.size() > 0){
			for (Map<String, String> map : list){
				if (map != null && map.size() > 0){
					String id = map.get("id");
					String subCategory = map.get("subCategoryName");
					String home = map.get("home");
					if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(subCategory) && StringUtils.isNotBlank(home)){
						if (!data.containsKey(home)){
							data.put(home, new HashMap<String, Set<String>>());
						}
						Map<String, Set<String>> categoryMap = data.get(home);
						if (!categoryMap.containsKey(subCategory)){
							categoryMap.put(subCategory, new HashSet<String>());
						}
						Set<String> idList = categoryMap.get(subCategory);
						idList.add(id);
					}
				}
			}
			HashSet<ReviewedProductInfoDTO> updateList = new HashSet<ReviewedProductInfoDTO>();
			for (Entry<String, Map<String, Set<String>>> hMap : data.entrySet()) {
				String home = hMap.getKey();
				for (Entry<String, Set<String>> cMap : hMap.getValue().entrySet()) {
					String subCategory = cMap.getKey();
					Statement statement = QueryBuilder
							.select("home", "subcategoryname", "id", "createdon", "instock", "modifiedon", "mrp", "salesrank", "sellingprice")
							.from(keyspace, TableEnum.reviewed_product_info.name()).where(QueryBuilder.eq("home", home))
							.and(QueryBuilder.in("id", new LinkedList<String>(cMap.getValue()))).and(QueryBuilder.eq("subcategoryname", subCategory));
					statement.setFetchSize(FETCHSIZE);
					Iterator<ReviewedProductInfoDTO> itr = QueryMapper.reviewedProductInfoDTO().map(session.execute(statement)).iterator();
					getDataToUpdate(itr, DO_ALL_UPDATE, updateList);
				}
			}
			updatePricesOfReviewData(updateList);
			return updateList;
		}
		return null;
	}
	
	public HashSet<ReviewedProductInfoDTO> updatedReviewedProductInfoDTOOnly (HashSet<ReviewedProductInfoDTO> data){
		
		HashSet<ReviewedProductInfoDTO> updateList = new HashSet<ReviewedProductInfoDTO>();
		
		if(data != null){
			for (ReviewedProductInfoDTO tmp : data) {
				if(tmp.isUpdated()){
					updateList.add(tmp);
				}
			}
			data.clear();
		}
		return updateList;
	}
}
