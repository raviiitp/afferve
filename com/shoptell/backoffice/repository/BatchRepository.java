/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository;

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.DATA_EXPIRY_DAY_COUNT;
import static com.shoptell.backoffice.BackofficeConstants.FETCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;
import static com.shoptell.backoffice.BackofficeConstants.ONE_DAY;
import static com.shoptell.backoffice.BackofficeConstants.POPULAR_ITEMS_HOMEPAGE_LIMIT;
import static com.shoptell.backoffice.BackofficeConstants.POPULAR_ITEMS_MAX_LIMIT;
import static com.shoptell.backoffice.enums.TableEnum.home_product_info;
import static com.shoptell.backoffice.enums.TableEnum.merged_product_info;
import static com.shoptell.backoffice.enums.TableEnum.merged_product_properties;
import static com.shoptell.backoffice.enums.TableEnum.person_in_contact;
import static com.shoptell.backoffice.enums.TableEnum.popularproduct;
import static com.shoptell.backoffice.enums.TableEnum.reviewed_product_info;
import static com.shoptell.backoffice.enums.TableEnum.user;
import static com.shoptell.backoffice.enums.TableEnum.usertransactions;
import static com.shoptell.backoffice.repository.QueryMapper.personInContactDTO;
import static org.apache.commons.lang.StringUtils.capitaliseAllWords;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Calendar;
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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.UDTMapper;
import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.PopularProductEnum;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.MergeProductPropertiesDTO;
import com.shoptell.backoffice.repository.dto.MergedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.PopularProductDTO;
import com.shoptell.backoffice.repository.dto.ProductOfferDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.UserTransactionDTO;
import com.shoptell.db.messagelog.MessageLogUtil;
import com.shoptell.domain.User;
import com.shoptell.domain.UserNotificationDTO;
import com.shoptell.social.contact.PersonInContactDTO;
import com.shoptell.util.stproperties.STProperties;

/**
 * Cassandra repository for the ProductInfo entity.
 */
@Named
public class BatchRepository {

	private static final Logger log = LoggerFactory.getLogger(BatchRepository.class);

	@Inject
	private SelectQuery selectQuery;

	@Inject
	private Session session;

	@Inject
	private Environment env;

	@Inject
	protected STProperties stprop;

	@Inject
	private MessageLogUtil msgLog;

	@SuppressWarnings("rawtypes")
	private Mapper mapper; // DO NOT TOUCH
	private MappingManager mappingManager;
	private UDTMapper<ProductOfferDTO> udtProductOfferMapper;

	private String keyspace;

	@PostConstruct
	public void init() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		mappingManager = new MappingManager(session);
		udtProductOfferMapper = mappingManager.udtMapper(ProductOfferDTO.class);
	}

	/**
	 * 
	 * @param rows
	 *            : rows is list of row/entity to be saved in DB. Use row to get
	 *            class type
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean batchSave(List<?> rows) {
		boolean safeRun = true;
		if (rows.size() > 0) {
			Class<?> class_type = rows.get(0).getClass();
			Mapper mapper = mappingManager.mapper(class_type);
			BatchStatement batch = new BatchStatement();

			int fromIndex = 0, toIndex = BATCHSIZE;
			while (fromIndex < rows.size()) {
				if (toIndex > rows.size()) {
					toIndex = rows.size();
				}

				try {
					Iterator<?> itr = rows.subList(fromIndex, toIndex).iterator();
					while (itr.hasNext()) {
						try {
							batch.add(mapper.saveQuery(itr.next()));
						} catch (Exception ex) {
							msgLog.addError(ex);
							log.error("", ex);
						}
					}
					session.execute(batch);
					batch.clear();
				} catch (Exception e) {
					msgLog.addError(e);
					log.error("", e);
					safeRun = false;
				}

				fromIndex = toIndex;
				toIndex += BATCHSIZE;
			}
		}
		return safeRun;
	}

	@SuppressWarnings("unchecked")
	public void batchDelete(List<?> rows) {
		if (rows.size() > 0) {
			Class<?> class_type = rows.get(0).getClass();
			mapper = mappingManager.mapper(class_type);
			BatchStatement batch = new BatchStatement();

			int fromIndex = 0, toIndex = BATCHSIZE;
			while (fromIndex < rows.size()) {
				if (toIndex > rows.size()) {
					toIndex = rows.size();
				}

				try {
					Iterator<?> itr = rows.subList(fromIndex, toIndex).iterator();
					while (itr.hasNext()) {
						batch.add(mapper.deleteQuery(itr.next()));
					}
					session.execute(batch);
					batch.clear();
				} catch (Exception e) {
					msgLog.addError(e);
					log.error("", e);
				}

				fromIndex = toIndex;
				toIndex += BATCHSIZE;
			}
		}

	}

	/**
	 * 
	 * @param pinfo
	 *            is the new product info If a pinfo with pinfo.getId() is
	 *            already present in DB and both are not equal then delete old
	 *            one i.e. stored one. Then insert new pinfo. Else nothing to do
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean save(Object pinfo) {
		boolean safeRun = true;
		Class<?> class_type = pinfo.getClass();
		mapper = mappingManager.mapper(class_type);
		try {
			mapper.save(pinfo);
		} catch (Exception e) {
			log.error("", e);
			msgLog.addError(e);
			safeRun = false;
		}
		return safeRun;
	}

	@SuppressWarnings("unchecked")
	public void delete(Object pinfo) {
		Class<?> class_type = pinfo.getClass();
		mapper = mappingManager.mapper(class_type);
		try {
			mapper.delete(pinfo);
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public List<?> selectAll(String table, Class<?> class_type, String pageNumber, Map<String, Object> map, String limit) {
		mapper = mappingManager.mapper(class_type);
		try {
			Statement selectAll = null;
			boolean first = true;
			if (map == null || map.size() == 0) {
				Select select = QueryBuilder.select().all().from(keyspace, table);
				if (isNotBlank(limit)) {
					selectAll = select.limit(Integer.parseInt(limit));
				}
				else {
					selectAll = select;
				}
			}
			else {
				Where select = null;
				for (Entry<String, Object> en : map.entrySet()) {
					if (first) {
						first = false;
						Select qb = QueryBuilder.select().all().from(keyspace, table);
						if (home_product_info.name().equalsIgnoreCase(table)) {
							qb = qb.allowFiltering();
						}
						select = qb.where(QueryBuilder.eq(en.getKey(), en.getValue()));
					}
					else {
						select = select.and(QueryBuilder.eq(en.getKey(), en.getValue()));
					}
				}
				if (isNotBlank(limit)) {
					selectAll = select.limit(Integer.parseInt(limit));
				}
				else {
					selectAll = select;
				}
			}

			selectAll.setFetchSize(FETCHSIZE);

			String requestedPage = pageNumber;
			// This will be absent for the first page
			if (requestedPage != null && !pageNumber.equalsIgnoreCase("1")) {
				selectAll.setPagingState(PagingState.fromString(requestedPage));
			}

			ResultSet rs = session.execute(selectAll);
			return mapper.map(rs).all();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	public Iterator<?> selectBySubCategoryName(String table, Class<?> class_type, String home, String subCategoryName, boolean reviewAll) {
		mapper = mappingManager.mapper(class_type);
		Where getInfoByCategoryName = null;
		Statement stmt = null;
		try {
			if (home == null) {
				getInfoByCategoryName = QueryBuilder.select().all().from(keyspace, table).where(QueryBuilder.eq("subCategoryName", subCategoryName));
			}
			else {
				getInfoByCategoryName = QueryBuilder.select().all().from(keyspace, table).where(QueryBuilder.eq("home", home))
						.and(QueryBuilder.eq("subCategoryName", subCategoryName));
			}
			if (table.equalsIgnoreCase(home_product_info.name()) && !reviewAll) {
				stmt = getInfoByCategoryName.and(QueryBuilder.eq("ismerged", false)).limit(FETCHSIZE);
			}
			else {
				stmt = getInfoByCategoryName;
			}
			stmt.setFetchSize(FETCHSIZE);
			ResultSet rs = session.execute(stmt);
			return mapper.map(rs).iterator();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * update isMerged boolean to true after merging HomeProductInfo rows
	 */
	public void batchUpdateIsMerged(String table, List<ReviewedProductInfoDTO> fieldsList) {

		if (fieldsList.size() > 0) {

			BatchStatement batch = new BatchStatement();

			int fromIndex = 0, toIndex = BATCHSIZE;
			while (fromIndex < fieldsList.size()) {
				if (toIndex > fieldsList.size()) {
					toIndex = fieldsList.size();
				}

				try {

					for (ReviewedProductInfoDTO fields : fieldsList.subList(fromIndex, toIndex)) {
						batch.add(QueryBuilder.update(keyspace, table).with(QueryBuilder.set("isMerged", true))
								.and(QueryBuilder.set("mergeProdInfoId", fields.getMergeProdInfoId())).where(QueryBuilder.eq("home", fields.getHome()))
								.and(QueryBuilder.eq("subCategoryName", fields.getSubCategoryName())).and(QueryBuilder.eq("id", fields.getId())));
					}
					session.execute(batch);
					batch.clear();
				} catch (Exception e) {
					log.error("", e);
				}

				fromIndex = toIndex;
				toIndex += BATCHSIZE;
			}
		}
	}

	/**
	 * flip isMerged to !isMerged
	 */
	public void flipIsMerge(String table, Class<?> class_type, String home, boolean isMerged) {
		List<ReviewedProductInfoDTO> homeProductInfoList = flipIsMergeUtil(table, class_type, home, isMerged, null);
		int fromIndex = 0, toIndex = BATCHSIZE;
		if (homeProductInfoList != null && homeProductInfoList.size() > 0) {
			try {
				BatchStatement batch = new BatchStatement();
				while (homeProductInfoList != null && homeProductInfoList.size() > 0) {
					if (toIndex > homeProductInfoList.size()) {
						toIndex = homeProductInfoList.size();
					}
					for (ReviewedProductInfoDTO fieldsToUpdate : homeProductInfoList.subList(fromIndex, toIndex)) {
						batch.add(QueryBuilder.update(keyspace, table).with(QueryBuilder.set("isMerged", !isMerged))
								.where(QueryBuilder.eq("home", fieldsToUpdate.getHome()))
								.and(QueryBuilder.eq("subCategoryName", fieldsToUpdate.getSubCategoryName()))
								.and(QueryBuilder.eq("id", fieldsToUpdate.getId())));
					}
					session.execute(batch);
					batch.clear();
					homeProductInfoList.subList(fromIndex, toIndex).clear();
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<ReviewedProductInfoDTO> flipIsMergeUtil(String table, Class<?> class_type, String home, boolean isMerged, String pageNumber) {

		mapper = mappingManager.mapper(class_type);
		try {
			Statement selectAllByIsMerge = null;
			if (home == null) {
				selectAllByIsMerge = QueryBuilder.select().all().from(keyspace, table).where(QueryBuilder.eq("ismerged", isMerged));
			}
			else {
				selectAllByIsMerge = QueryBuilder.select().all().from(keyspace, table).where(QueryBuilder.eq("home", home))
						.and(QueryBuilder.eq("ismerged", isMerged));
			}

			selectAllByIsMerge.setFetchSize(FETCHSIZE);

			String requestedPage = pageNumber;
			// This will be absent for the first page
			if (requestedPage != null) {
				selectAllByIsMerge.setPagingState(PagingState.fromString(requestedPage));
			}

			ResultSet rs = session.execute(selectAllByIsMerge);
			return mapper.map(rs).all();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	public void batchUpdateHomeTable(String table, List<HomeProductInfoDTO> rows) {
		if (rows.size() > 0) {
			BatchStatement batch = new BatchStatement();
			int fromIndex = 0, toIndex = BATCHSIZE;
			while (fromIndex < rows.size()) {
				if (toIndex > rows.size()) {
					toIndex = rows.size();
				}

				try {
					List<HomeProductInfoDTO> _tmpHomeList = rows.subList(fromIndex, toIndex);
					for (HomeProductInfoDTO fieldsToUpdate : _tmpHomeList) {
						batch.add(QueryBuilder.update(keyspace, table).with(QueryBuilder.set("mrp", fieldsToUpdate.getMrp()))
								.and(QueryBuilder.set("sellingPrice", fieldsToUpdate.getSellingPrice()))
								.and(QueryBuilder.set("salesRank", fieldsToUpdate.getSalesRank()))
								.and(QueryBuilder.set("discountPercentage", fieldsToUpdate.getDiscountPercentage()))
								.and(QueryBuilder.set("inStock", fieldsToUpdate.isInStock()))
								.and(QueryBuilder.set("modifiedOn", fieldsToUpdate.getModifiedOn()))
								.and(QueryBuilder.set("modifiedBy", fieldsToUpdate.getModifiedBy()))
								.and(QueryBuilder.set("cashBack", fieldsToUpdate.getCashBack()))
								.and(QueryBuilder.set("condition", fieldsToUpdate.getCondition())).where(QueryBuilder.eq("home", fieldsToUpdate.getHome()))
								.and(QueryBuilder.eq("subCategoryName", fieldsToUpdate.getSubCategoryName()))
								.and(QueryBuilder.eq("id", fieldsToUpdate.getId())));
					}
					session.execute(batch);
					batch.clear();
				} catch (Exception e) {
					log.error("", e);
				}

				fromIndex = toIndex;
				toIndex += BATCHSIZE;
			}
		}
	}

	public void batchSaveAndUpdate(String table, List<HomeProductInfoDTO> rows) {
		batchInsert(table, rows);
		// batchUpdateHomeTable(table, rows);
	}

	public void batchInsert(String table, List<HomeProductInfoDTO> rows) {
		if (rows.size() > 0) {
			BatchStatement batch = new BatchStatement();

			int fromIndex = 0, toIndex = BATCHSIZE;
			while (fromIndex < rows.size()) {
				if (toIndex > rows.size()) {
					toIndex = rows.size();
				}

				try {
					Iterator<HomeProductInfoDTO> itr = rows.subList(fromIndex, toIndex).iterator();
					while (itr.hasNext()) {
						HomeProductInfoDTO fieldsToUpdate = itr.next();
						if (fieldsToUpdate != null) {
							if (!fieldsToUpdate.isInStock()) {
								// continue; // do not insert out of stock
								// products
							}

							List<UDTValue> udtValueList = null;
							if (fieldsToUpdate.getOfferList() != null) {
								udtValueList = new ArrayList<>();
								for (ProductOfferDTO pOffer : fieldsToUpdate.getOfferList()) {
									udtValueList.add(udtProductOfferMapper.toUDT(pOffer));
								}
							}

							Insert insert = QueryBuilder
									.insertInto(keyspace, table)
									/* .ifNotExists() */
									.value("originalTitle", fieldsToUpdate.getOriginalTitle())
									.value("salesRank", fieldsToUpdate.getSalesRank())
									.value("categoryId", fieldsToUpdate.getCategoryId())
									.value("tags", fieldsToUpdate.getTags())
									.value("title", fieldsToUpdate.getTitle())
									.value("subTitle", fieldsToUpdate.getSubTitle())
									.value("description", fieldsToUpdate.getDescription())
									.value("productUrl", fieldsToUpdate.getProductUrl())
									.value("imageUrl", fieldsToUpdate.getImageUrl())
									.value("imageUrlXL", fieldsToUpdate.getImageUrlXL())
									.value("imageUrlLarge", fieldsToUpdate.getImageUrlLarge())
									.value("imageUrlMedium", fieldsToUpdate.getImageUrlMedium())
									.value("imageUrlSmall", fieldsToUpdate.getImageUrlSmall())
									.value("mrp", fieldsToUpdate.getMrp())
									.value("sellingPrice", fieldsToUpdate.getSellingPrice())
									.value("discountPercentage", fieldsToUpdate.getDiscountPercentage())
									.value("inStock", fieldsToUpdate.isInStock())
									.value("emiAvailable", fieldsToUpdate.isEmiAvailable())
									.value("codAvailable", fieldsToUpdate.isCodAvailable())
									.value("productBrand", fieldsToUpdate.getProductBrand())
									.value("color", fieldsToUpdate.getColor())
									.value("size", fieldsToUpdate.getSize())
									.value("sizeUnit", fieldsToUpdate.getSizeUnit())
									.value("categoryPaths", fieldsToUpdate.getCategoryPaths())
									.value("createdOn", fieldsToUpdate.getCreatedOn())
									.value("modifiedOn", fieldsToUpdate.getModifiedOn())
									.value("createdBy", fieldsToUpdate.getCreatedBy())
									.value("modifiedBy", fieldsToUpdate.getModifiedBy())
									.value("styleCode", fieldsToUpdate.getStyleCode())
									.value("offers", fieldsToUpdate.getOffers())
									.value("imageUrls", fieldsToUpdate.getImageUrls())
									.value("colorVariants", fieldsToUpdate.getColorVariants())
									.value("cashBack", fieldsToUpdate.getCashBack())
									.value("type", fieldsToUpdate.getType())
									/*
									 * .value("competitorFields",
									 * udtValueCompetitorFields)
									 */
									.value("shippingOptions", fieldsToUpdate.getShippingOptions())
									.value("sizeVariants", fieldsToUpdate.getSizeVariants())
									.value("offset", fieldsToUpdate.getOffset())
									.value("galleryPlusPictureUrl", fieldsToUpdate.getGalleryPlusPictureUrl())
									.value("paymentMethods", fieldsToUpdate.getPaymentMethods())
									.value("productIdType", fieldsToUpdate.getProductIdType())
									.value("productIdValue", fieldsToUpdate.getProductIdValue())
									.value("attributeMap", fieldsToUpdate.getAttributeMap())
									.value("isAutoPay", fieldsToUpdate.isAutoPay())
									.value("postalCode", fieldsToUpdate.getPostalCode())
									.value("location", fieldsToUpdate.getLocation())
									.value("country", fieldsToUpdate.getCountry())
									.value("sellingState", fieldsToUpdate.getSellingState())
									.value("timeLeft", fieldsToUpdate.getTimeLeft())
									.value("condition", fieldsToUpdate.getCondition())
									.value("isMultiVariationListing", fieldsToUpdate.isMultiVariationListing())
									.value("isTopRatedListing", fieldsToUpdate.isTopRatedListing())
									.value("isReturnAccepted", fieldsToUpdate.isReturnAccepted())
									.value("shippingServiceCost", fieldsToUpdate.getShippingServiceCost())
									.value("shippingType", fieldsToUpdate.getShippingType())
									.value("shipToLocations", fieldsToUpdate.getShipToLocations())
									.value("galleryInfoGalleryUrl", fieldsToUpdate.getGalleryInfoGalleryUrl())
									.value("accessories", fieldsToUpdate.getAccessories())
									.value("alternateVersions", fieldsToUpdate.getAlternateVersions())
									.value("collections", fieldsToUpdate.getCollections())
									.value("imagesets", fieldsToUpdate.getImageSets())
									.value("itemLinks", fieldsToUpdate.getItemLinks())
									.value("offerTotalOffers", fieldsToUpdate.getOfferTotalOffers())
									.value("offerTotalOfferPages", fieldsToUpdate.getOfferTotalOfferPages())
									.value("offerlist", udtValueList)
									// UDT map of productOffers
									.value("offerSummaryTotalCollectible", fieldsToUpdate.getOfferSummaryTotalCollectible())
									.value("offerSummaryLowestCollectiblePrice", fieldsToUpdate.getOfferSummaryLowestCollectiblePrice())
									.value("offerSummaryTotalNew", fieldsToUpdate.getOfferSummaryTotalNew())
									.value("offerSummaryLowestNewPrice", fieldsToUpdate.getOfferSummaryLowestNewPrice())
									.value("variations", fieldsToUpdate.getVariations()).value("variationAttributes", fieldsToUpdate.getVariationAttributes())
									.value("variationSummaryHighestPrice", fieldsToUpdate.getVariationSummaryHighestPrice())
									.value("variationSummaryLowestPrice", fieldsToUpdate.getVariationSummaryLowestPrice())
									.value("variationSummaryHighestSalesPrice", fieldsToUpdate.getVariationSummaryHighestSalesPrice())
									.value("variationSummaryLowestSalesPrice", fieldsToUpdate.getVariationSummaryLowestSalesPrice())
									.value("actors", fieldsToUpdate.getActors()).value("artists", fieldsToUpdate.getArtists())
									.value("aspectRatio", fieldsToUpdate.getAspectRatio()).value("audienceRating", fieldsToUpdate.getAudienceRating())
									.value("binding", fieldsToUpdate.getBinding()).value("ceroAgeRating", fieldsToUpdate.getCeroAgeRating())
									.value("clothingSize", fieldsToUpdate.getClothingSize()).value("department", fieldsToUpdate.getDepartment())
									.value("ean", fieldsToUpdate.getEan()).value("edition", fieldsToUpdate.getEdition())
									.value("episodeSequence", fieldsToUpdate.getEpisodeSequence()).value("esrbAgeRating", fieldsToUpdate.getEsrbAgeRating())
									.value("features", fieldsToUpdate.getFeatures()).value("format", fieldsToUpdate.getFormat())
									.value("genre", fieldsToUpdate.getGenre()).value("hardwarePlatform", fieldsToUpdate.getHardwarePlatform())
									.value("hazardousMaterialType", fieldsToUpdate.getHazardousMaterialType()).value("isbn", fieldsToUpdate.getIsbn())
									.value("issuePerYear", fieldsToUpdate.getIssuePerYear()).value("itemPartNumber", fieldsToUpdate.getItemPartNumber())
									.value("label", fieldsToUpdate.getLabel()).value("languages", fieldsToUpdate.getLanguages())
									.value("legalDisclaimer", fieldsToUpdate.getLegalDisclaimer()).value("magazineType", fieldsToUpdate.getMagazineType())
									.value("manufacturer", fieldsToUpdate.getManufacturer())
									.value("manufacturerPartsWarrantyDescription", fieldsToUpdate.getManufacturerPartsWarrantyDescription())
									.value("mediaType", fieldsToUpdate.getMediaType()).value("model", fieldsToUpdate.getModel())
									.value("mpn", fieldsToUpdate.getMpn()).value("manufacturerMaximumAgeUnit", fieldsToUpdate.getManufacturerMaximumAgeUnit())
									.value("manufacturerMaximumAgeValue", fieldsToUpdate.getManufacturerMaximumAgeValue())
									.value("manufacturerMinimumAgeUnit", fieldsToUpdate.getManufacturerMinimumAgeUnit())
									.value("manufacturerMinimumAgeValue", fieldsToUpdate.getManufacturerMinimumAgeValue())
									.value("modelYear", fieldsToUpdate.getModelYear()).value("numberOfDiscs", fieldsToUpdate.getNumberOfDiscs())
									.value("numberOfIssues", fieldsToUpdate.getNumberOfIssues()).value("numberOfItems", fieldsToUpdate.getNumberOfItems())
									.value("numberOfTracks", fieldsToUpdate.getNumberOfTracks()).value("numberOfPages", fieldsToUpdate.getNumberOfPages())
									.value("operatingSystem", fieldsToUpdate.getOperatingSystem()).value("partNumber", fieldsToUpdate.getPartNumber())
									.value("productGroup", fieldsToUpdate.getProductGroup()).value("productTypeName", fieldsToUpdate.getProductTypeName())
									.value("productTypeSubcategory", fieldsToUpdate.getProductTypeSubcategory())
									.value("publicationDate", fieldsToUpdate.getPublicationDate()).value("publisher", fieldsToUpdate.getPublisher())
									.value("packageQuantity", fieldsToUpdate.getPackageQuantity())
									.value("packageDimensionsHeight", fieldsToUpdate.getPackageDimensionsHeight())
									.value("packageDimensionsLength", fieldsToUpdate.getPackageDimensionsLength())
									.value("packageDimensionsWidth", fieldsToUpdate.getPackageDimensionsWidth())
									.value("packageDimensionsWeight", fieldsToUpdate.getPackageDimensionsWeight())
									.value("packageDimensionsHeightUnit", fieldsToUpdate.getPackageDimensionsHeightUnit())
									.value("packageDimensionsLengthUnit", fieldsToUpdate.getPackageDimensionsLengthUnit())
									.value("packageDimensionsWidthUnit", fieldsToUpdate.getPackageDimensionsWidthUnit())
									.value("packageDimensionsWeightUnit", fieldsToUpdate.getPackageDimensionsWeightUnit())
									.value("pictureFormats", fieldsToUpdate.getPictureFormats()).value("regionCode", fieldsToUpdate.getRegionCode())
									.value("releaseDate", fieldsToUpdate.getReleaseDate()).value("seikodoProductCode", fieldsToUpdate.getSeikodoProductCode())
									.value("sku", fieldsToUpdate.getSku()).value("studio", fieldsToUpdate.getStudio())
									.value("trackSequence", fieldsToUpdate.getTrackSequence()).value("upc", fieldsToUpdate.getUpc())
									.value("specificationJson", fieldsToUpdate.getSpecificationJson()).value("warranty", fieldsToUpdate.getWarranty())
									.value("weeeTaxValue", fieldsToUpdate.getWeeeTaxValue()).value("paymentMethod", fieldsToUpdate.getPaymentMethod())
									.value("categoryName", fieldsToUpdate.getCategoryName()).value("isMerged", false).value("home", fieldsToUpdate.getHome())
									.value("subCategoryName", fieldsToUpdate.getSubCategoryName()).value("id", fieldsToUpdate.getId());
							batch.add(insert);
						}
					}
					session.execute(batch);
					batch.clear();
				} catch (Exception e) {
					log.error("", e);
				}

				fromIndex = toIndex;
				toIndex += BATCHSIZE;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<ReviewedProductInfoDTO> getDataForHotProcessing(String home, String category) {
		Statement statement = QueryBuilder.select().all().from(keyspace, reviewed_product_info.name()).where(QueryBuilder.eq("home", home))
				.and(QueryBuilder.eq("subcategoryname", category)).and(QueryBuilder.eq("hotUpdate", false)).limit(10);
		ResultSet rs = session.execute(statement);
		mapper = mappingManager.mapper(ReviewedProductInfoDTO.class);
		return mapper.map(rs).all();
	}

	public void updateProductRank(String home, Map<String, Integer> map, String table, String category) {
		BatchStatement batch = new BatchStatement();
		List<PopularProductDTO> prodList = new LinkedList<PopularProductDTO>();

		long base_rank = ((Long.MAX_VALUE - System.currentTimeMillis()) / 1000000) % 1000000; // Ever
																								// decreasing
																								// value
		for (Entry<String, Integer> entry : map.entrySet()) {
			Statement statement = QueryBuilder.update(keyspace, table).with(QueryBuilder.set("salesrank", entry.getValue() + base_rank))
					.where(QueryBuilder.eq("home", home)).and(QueryBuilder.eq("subcategoryname", category)).and(QueryBuilder.eq("id", entry.getKey()));
			batch.add(statement);
			prodList.add(new PopularProductDTO(category, home, entry.getKey(), "", PopularProductEnum.TOP.name(), entry.getValue()));
		}
		batchSave(prodList);
		session.execute(batch);
		batch.clear();
	}

	@SuppressWarnings("unchecked")
	public Iterator<MergedProductInfoDTO> getMergedProductData() {
		Statement statement = QueryBuilder.select().all().from(keyspace, merged_product_info.name());
		statement.setFetchSize(FETCHSIZE);
		ResultSet rs = session.execute(statement);
		mapper = mappingManager.mapper(MergedProductInfoDTO.class);
		return mapper.map(rs).iterator();
	}

	public void updateReviewFromHome(ReviewedProductInfoDTO tmp, Row row) {
		tmp.setSellingPrice(row.getDouble("sellingprice"));
		tmp.setMrp(row.getDouble("mrp"));
		if (tmp.getMrp() == 0 || tmp.getSellingPrice() > tmp.getMrp()) {
			tmp.setMrp(tmp.getSellingPrice());
		}

		if (row.getDouble("salesrank") > 0) {
			tmp.setSalesRank(row.getDouble("salesrank"));
		}

		boolean isInStock = row.getBool("instock");
		Date date = row.getDate("modifiedOn");
		String days = stprop.getValueOrDefault(DATA_EXPIRY_DAY_COUNT, "5");
		if (!(isNotBlank(days) && NumberUtils.isDigits(days))) {
			days = "5";
		}
		long day = Long.parseLong(days);

		if (date != null && date.before(new Date(System.currentTimeMillis() - (day * 24 * 3600 * 1000)))) { // 5
																											// days
																											// before
			isInStock = false;
		}
		tmp.setInStock(isInStock);
		tmp.setModifiedOn(date);
	}

	public Iterator<Row> retrieveDataFromHomeTable(Set<String> ids, String home, String category) {
		Statement select = QueryBuilder.select("subcategoryname", "home", "id", "instock", "sellingprice", "salesrank", "mrp", "modifiedOn")
				.from(keyspace, home_product_info.name()).where(QueryBuilder.eq("home", home)).and(QueryBuilder.in("id", new LinkedList<String>(ids)))
				.and(QueryBuilder.eq("subcategoryname", category));
		select.setFetchSize(FETCHSIZE);
		return session.execute(select).iterator();
	}

	@SuppressWarnings("unchecked")
	public User getUser(String userId) {
		if (isBlank(userId)) {
			return null;
		}
		Where statement = QueryBuilder.select().all().from(keyspace, user.name()).where(QueryBuilder.eq("id", userId));
		statement.setFetchSize(FETCHSIZE);
		ResultSet rs = session.execute(statement);
		mapper = mappingManager.mapper(User.class);

		Iterator<User> users = mapper.map(rs).iterator();
		if (users != null && users.hasNext()) {
			return users.next();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public String name(String userId) {
		User user = getUser(userId);
		String name = "a friend";
		if (user != null && isNotBlank(user.getFirstName())) {
			name = capitaliseAllWords(user.getFirstName().toLowerCase());
			if (isNotBlank(user.getLastName())) {
				name += " " + capitaliseAllWords(user.getLastName().toLowerCase());
			}
		}
		return name;
	}

	public UserNotificationDTO getUserNotification(String userId) {
		UserNotificationDTO userNotification = QueryMapper.userNotificationDTO().get(userId);
		return userNotification;
	}

	public UUID getMergePropRow(UUID id, String subCategoryName, Map<String, String> properties) {
		Where statement = QueryBuilder.select("id").from(keyspace, merged_product_properties.name()).allowFiltering().where(QueryBuilder.eq("mergeprodid", id))
				.and(QueryBuilder.eq("subcategoryname", subCategoryName));

		String[] propKeys = CategoryEnum.getProperties(subCategoryName);
		if (propKeys != null) {
			for (int i = 0; i < propKeys.length; i++) {
				String value = properties.get(propKeys[i]);
				if (isBlank(value)) {
					value = "";
				}
				statement = statement.and(QueryBuilder.eq("property" + i, value));
				if (i == 9)
					break;
			}
		}
		statement.setFetchSize(FETCHSIZE);
		ResultSet rs = session.execute(statement);
		if (rs != null) {
			List<Row> row = rs.all();
			if (row != null && row.size() > 0) {
				Row rw = row.get(0);
				return rw.getUUID("id");
			}
		}
		UUID uuid = UUIDs.random();
		MergeProductPropertiesDTO mergeProperties = new MergeProductPropertiesDTO(uuid, id, subCategoryName, properties);
		save(mergeProperties);
		return uuid;
	}

	public Iterator<MergeProductPropertiesDTO> getMergeProductProperties(UUID id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mergeprodid", id);
		ResultSet rs = selectQuery.selectAll(merged_product_properties, map);
		Iterator<MergeProductPropertiesDTO> props = QueryMapper.mergeProductPropertiesDTO().map(rs).iterator();
		if (props != null && props.hasNext()) {
			return props;
		}
		return null;
	}

	public Set<String> getPopularProducts(CategoryEnum subCategory, String home, boolean doNotUpdate) {
		Set<String> set = new HashSet<String>();
		if (subCategory == null) {
			return null;
		}
		Date date = BackofficeUtil.getStartOfDay(new Date(System.currentTimeMillis()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int limit = POPULAR_ITEMS_MAX_LIMIT;
		if (doNotUpdate) {
			limit = POPULAR_ITEMS_HOMEPAGE_LIMIT;
		}

		while (true) {
			Statement statement = QueryBuilder.select("id", "rank").from(keyspace, popularproduct.name()).allowFiltering()
					.where(QueryBuilder.gte("createdon", cal.getTime())).and(QueryBuilder.eq("category", subCategory.name()))
					.and(QueryBuilder.eq("home", home)).and(QueryBuilder.in("type", PopularProductEnum.NEW.name(), PopularProductEnum.TOP.name()));

			statement = statement.setFetchSize(FETCHSIZE);
			ResultSet rs = session.execute(statement);
			Iterator<Row> rows = rs.iterator();

			if (rows != null && rows.hasNext()) {
				while (rows.hasNext()) {
					Row tmp = rows.next();
					int rank = tmp.getInt("rank");
					if (rank <= limit) {
						set.add(tmp.getString("id"));
					}
				}
				break;
			}
			else {
				cal.add(Calendar.DATE, -1);
				if (cal.getTime().before(new Date(System.currentTimeMillis() - 10 * ONE_DAY))) {
					return null;
				}
			}
		}
		return set;
	}

	public List<UserTransactionDTO> userTransactions(UUID low, UUID high) {
		String[] keys = { "time", "time" };
		QueryOperations[] operations = { QueryOperations.GT, QueryOperations.LT };
		Object[] values = { low, high };
		ResultSet rs = selectQuery.selectWithOperations(usertransactions, true, keys, operations, values);
		return QueryMapper.userTransactionDTO().map(rs).all();
	}

	@SuppressWarnings("unchecked")
	public String getLastTransactionId(UUID low, String home) {
		String id = null;
		Where statement = QueryBuilder.select().all().from(keyspace, usertransactions.name()).allowFiltering().where(QueryBuilder.gt("time", low))
				.and(QueryBuilder.eq("home", home));
		statement.setFetchSize(FETCHSIZE);
		ResultSet rs = session.execute(statement);
		mapper = mappingManager.mapper(UserTransactionDTO.class);
		List<UserTransactionDTO> list = mapper.map(rs).all();
		if (list == null || list.size() == 0) {
			return null;
		}
		if (list.size() > 1) {
			Collections.sort(list, BackofficeUtil.compareUserTransaction);
			UserTransactionDTO prev = list.get(0);
			if (prev != null) {
				int j = 0;
				for (int i = 1; i < list.size(); i++) {
					UserTransactionDTO curr = list.get(i);
					if (curr != null) {
						if (curr.date().equals(prev.date())) {
							j = i;
						}
						else {
							break;
						}
					}
				}
				if (j == 0) {
					id = prev.getTrackingId();
				}
				else {
					int max = -1;
					for (int i = 0; i <= j; i++) {
						UserTransactionDTO dto = list.get(i);
						if (dto != null) {
							Pattern pattern = Pattern.compile("afferve-0(\\d+)-21", Pattern.CASE_INSENSITIVE);
							Matcher matcher = pattern.matcher(id);
							while (matcher.find()) {
								if (BackofficeUtil.isMatchPresent(matcher, 1)) {
									id = matcher.group(1);
									if (NumberUtils.isNumber(id)) {
										int tmp = Integer.parseInt(id);
										if (tmp > max) {
											max = tmp;
										}
									}
									break;
								}
							}
						}
					}
					if (max == -1) {
						return null;
					}
					return "afferve-0" + max + "-21";
				}
			}
		}
		else {
			UserTransactionDTO tmp = list.get(0);
			if (tmp != null) {
				id = tmp.getTrackingId();
			}
		}
		return id;
	}

	/**
	 * get list of competitorFields by subCategoryName of size fetchsize
	 */
	public Iterator<ReviewedProductInfoDTO> getDataForReview(String home, String subCategoryName, boolean allReview) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("home", home);
			map.put("subCategoryName", subCategoryName);
			if (!allReview) {
				map.put("isMerged", false);
			}
			ResultSet result = selectQuery.selectAll(reviewed_product_info, map, FETCHSIZE);
			if (result == null || result.isExhausted()){
				return null;
			}
			Iterator<ReviewedProductInfoDTO> rows = QueryMapper.reviewedProductInfoDTO().map(result).iterator();
			return rows;
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	public PersonInContactDTO getPersonInContact(String email) {
		PersonInContactDTO personInContactDTO = null;
		try {
			personInContactDTO = personInContactDTO().get(email);
		} catch (Exception e) {
			log.error("", e);
		}
		return personInContactDTO;
	}

	public void savePersonContact(PersonInContactDTO person) {
		if (person != null) {
			Insert statement = QueryBuilder.insertInto(keyspace, person_in_contact.name()).ifNotExists().value("email", person.getEmail())
					.value("birthday", person.getBirthday()).value("firstName", person.getFirstName()).value("fullName", person.getFullName())
					.value("gender", person.getGender()).value("lastName", person.getLastName()).value("phoneNumber", person.getPhoneNumber())
					.value("notInContact", person.isNotInContact());

			session.execute(statement);
		}
	}
}
