/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.FrozenValue;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "afferve", name = "home_product_info")
public class HomeProductInfoDTO implements Serializable {
	/**
	 * PRIMARY KEY (( home), categoryName, id);
	 */
	private static final long serialVersionUID = 1L;
	@PartitionKey
	private String home;
	@ClusteringColumn(value = 0)
	private String subCategoryName;
	@ClusteringColumn(value = 1)
	private String id; // Amazon : ASIN, Flipkart : id and Ebay : id
	@Column(name = "isMerged")
	private boolean merged; // merged means reviewed
	@Column(name = "isAutoPay")
	private boolean autoPay;
	@Column(name = "isMultiVariationListing")
	private boolean multiVariationListing;
	@Column(name = "isTopRatedListing")
	private boolean topRatedListing;
	@Column(name = "isReturnAccepted")
	private boolean returnAccepted;
	@Frozen
	private ShippingOptionsDTO shippingOptions;
	@FrozenValue
	private List<ProductOfferDTO> offerList;
	@FrozenValue
	private Map<String, Map<String, String>> imageSets;
	@FrozenValue
	private Map<String, List<String>> variationAttributes;
	@FrozenValue
	private List<Map<String, String>> languages;
	private boolean codAvailable;
	private boolean emiAvailable;
	private boolean inStock;
	private boolean markForDelete;
	private Date createdOn;
	private Date modifiedOn;
	private int offerTotalOfferPages;
	private int offerTotalOffers;
	private List<String> actors;
	private List<String> artists;
	private List<String> features;
	private List<String> format;
	private List<String> galleryInfoGalleryUrl;
	private List<String> galleryPlusPictureUrl;
	private List<String> itemLinks;
	private List<String> paymentMethods;
	private List<String> pictureFormats;
	private List<String> shipToLocations;
	private List<String> variations;
	private Map<String, String> accessories;
	private Map<String, String> alternateVersions;
	private Map<String, String> attributeMap;
	private Map<String, String> collections;
	private Map<String, String> imageUrls;
	private Set<String> categoryPaths;
	private Set<String> offers;
	private Set<String> tags;
	private String aspectRatio;
	private String audienceRating;
	private String binding;
	private String cashBack;
	private String categoryId;
	private String categoryName;
	private String ceroAgeRating;
	private String clothingSize;
	private String color;
	private String colorVariants;
	private String condition;
	private String country;
	private String createdBy;
	private String department;
	private String description;
	private String discountPercentage;
	private String ean;
	private String edition;
	private String episodeSequence;
	private String esrbAgeRating;
	private String genre;
	private String hardwarePlatform;
	private String hazardousMaterialType;
	private String imageUrl;
	private String imageUrlLarge;
	private String imageUrlMedium;
	private String imageUrlSmall;
	private String imageUrlXL;
	private String isbn;
	private String issuePerYear;
	private String itemPartNumber;
	private String label;
	private String type;
	private String legalDisclaimer;
	private String location;
	private String magazineType;
	private String manufacturer;
	private String manufacturerMaximumAgeUnit;
	private String manufacturerMaximumAgeValue;
	private String manufacturerMinimumAgeUnit;
	private String manufacturerMinimumAgeValue;
	private String manufacturerPartsWarrantyDescription;
	private String mediaType;
	private String model;
	private String modelYear;
	private String modifiedBy;
	private String mpn;
	private double mrp;
	private String numberOfDiscs;
	private String numberOfIssues;
	private String numberOfItems;
	private String numberOfPages;
	private String numberOfTracks;
	private String offerSummaryLowestCollectiblePrice;
	private String offerSummaryLowestNewPrice;
	private String offerSummaryTotalCollectible;
	private String offerSummaryTotalNew;
	private String offset;
	private String operatingSystem;
	private String packageDimensionsHeight;
	private String packageDimensionsHeightUnit;
	private String packageDimensionsLength;
	private String packageDimensionsLengthUnit;
	private String packageDimensionsWeight;
	private String packageDimensionsWeightUnit;
	private String packageDimensionsWidth;
	private String packageDimensionsWidthUnit;
	private String packageQuantity;
	private String partNumber;
	private String paymentMethod;
	private String postalCode;
	private String productBrand;
	private String productGroup;
	private String productIdType;
	private String productIdValue;
	private String productTypeName;
	private String productTypeSubcategory;
	private String productUrl;
	private String publicationDate;
	private String publisher;
	private String regionCode;
	private String releaseDate;
	private double salesRank;
	private String seikodoProductCode;
	private double sellingPrice;
	private String sellingState;
	private String shippingServiceCost;
	private String shippingType;
	private String size;
	private String sizeUnit;
	private String sizeVariants;
	private String sku;
	private String studio;
	private String styleCode;
	private String subTitle;
	private String timeLeft;
	private String title;
	private String originalTitle;
	private String trackSequence;
	private String upc;
	private String variationSummaryHighestPrice;
	private String variationSummaryHighestSalesPrice;
	private String variationSummaryLowestPrice;
	private String variationSummaryLowestSalesPrice;
	private String warranty;
	private String weeeTaxValue;
	private String specificationJson;
	public HomeProductInfoDTO() {
		super();
		this.markForDelete = false;
		this.home = null;
		this.subCategoryName = null;
		this.id = null;
		this.merged = false;
		this.autoPay = false;
		this.multiVariationListing = false;
		this.topRatedListing = false;
		this.returnAccepted = false;
		this.shippingOptions = null;
		this.offerList = null;
		this.imageSets = null;
		this.variationAttributes = null;
		this.languages = null;
		this.codAvailable = false;
		this.emiAvailable = false;
		this.inStock = false;
		this.createdOn = new Date(System.currentTimeMillis());
		this.modifiedOn = new Date(System.currentTimeMillis());
		this.offerTotalOfferPages = 0;
		this.offerTotalOffers = 0;
		this.actors = null;
		this.artists = null;
		this.features = null;
		this.format = null;
		this.galleryInfoGalleryUrl = null;
		this.galleryPlusPictureUrl = null;
		this.itemLinks = null;
		this.paymentMethods = null;
		this.pictureFormats = null;
		this.shipToLocations = null;
		this.variations = null;
		this.accessories = null;
		this.alternateVersions = null;
		this.attributeMap = null;
		this.collections = null;
		this.imageUrls = null;
		this.categoryPaths = null;
		this.offers = null;
		this.tags = null;
		this.aspectRatio = null;
		this.audienceRating = null;
		this.binding = null;
		this.cashBack = null;
		this.categoryId = null;
		this.categoryName = null;
		this.ceroAgeRating = null;
		this.clothingSize = null;
		this.color = null;
		this.colorVariants = null;
		this.condition = null;
		this.country = null;
		this.createdBy = null;
		this.department = null;
		this.description = null;
		this.discountPercentage = null;
		this.ean = null;
		this.edition = null;
		this.episodeSequence = null;
		this.esrbAgeRating = null;
		this.genre = null;
		this.hardwarePlatform = null;
		this.hazardousMaterialType = null;
		this.imageUrl = null;
		this.imageUrlLarge = null;
		this.imageUrlMedium = null;
		this.imageUrlSmall = null;
		this.imageUrlXL = null;
		this.isbn = null;
		this.issuePerYear = null;
		this.itemPartNumber = null;
		this.label = null;
		this.legalDisclaimer = null;
		this.location = null;
		this.magazineType = null;
		this.manufacturer = null;
		this.manufacturerMaximumAgeUnit = null;
		this.manufacturerMaximumAgeValue = null;
		this.manufacturerMinimumAgeUnit = null;
		this.manufacturerMinimumAgeValue = null;
		this.manufacturerPartsWarrantyDescription = null;
		this.mediaType = null;
		this.model = null;
		this.modelYear = null;
		this.modifiedBy = null;
		this.mpn = null;
		this.mrp = 0;
		this.numberOfDiscs = null;
		this.numberOfIssues = null;
		this.numberOfItems = null;
		this.numberOfPages = null;
		this.numberOfTracks = null;
		this.offerSummaryLowestCollectiblePrice = null;
		this.offerSummaryLowestNewPrice = null;
		this.offerSummaryTotalCollectible = null;
		this.offerSummaryTotalNew = null;
		this.offset = null;
		this.operatingSystem = null;
		this.packageDimensionsHeight = null;
		this.packageDimensionsHeightUnit = null;
		this.packageDimensionsLength = null;
		this.packageDimensionsLengthUnit = null;
		this.packageDimensionsWeight = null;
		this.packageDimensionsWeightUnit = null;
		this.packageDimensionsWidth = null;
		this.packageDimensionsWidthUnit = null;
		this.packageQuantity = null;
		this.partNumber = null;
		this.paymentMethod = null;
		this.postalCode = null;
		this.productBrand = null;
		this.productGroup = null;
		this.productIdType = null;
		this.productIdValue = null;
		this.productTypeName = null;
		this.productTypeSubcategory = null;
		this.productUrl = null;
		this.publicationDate = null;
		this.publisher = null;
		this.regionCode = null;
		this.releaseDate = null;
		this.salesRank = 0;
		this.seikodoProductCode = null;
		this.sellingPrice = 0;
		this.sellingState = null;
		this.shippingServiceCost = null;
		this.shippingType = null;
		this.size = null;
		this.sizeUnit = null;
		this.sizeVariants = null;
		this.sku = null;
		this.studio = null;
		this.styleCode = null;
		this.subTitle = null;
		this.timeLeft = null;
		this.title = null;
		this.originalTitle = null;
		this.trackSequence = null;
		this.upc = null;
		this.variationSummaryHighestPrice = null;
		this.variationSummaryHighestSalesPrice = null;
		this.variationSummaryLowestPrice = null;
		this.variationSummaryLowestSalesPrice = null;
		this.warranty = null;
		this.weeeTaxValue = null;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessories == null) ? 0 : accessories.hashCode());
		result = prime * result + ((actors == null) ? 0 : actors.hashCode());
		result = prime * result + ((alternateVersions == null) ? 0 : alternateVersions.hashCode());
		result = prime * result + ((artists == null) ? 0 : artists.hashCode());
		result = prime * result + ((aspectRatio == null) ? 0 : aspectRatio.hashCode());
		result = prime * result + ((attributeMap == null) ? 0 : attributeMap.hashCode());
		result = prime * result + ((audienceRating == null) ? 0 : audienceRating.hashCode());
		result = prime * result + (autoPay ? 1231 : 1237);
		result = prime * result + ((binding == null) ? 0 : binding.hashCode());
		result = prime * result + ((cashBack == null) ? 0 : cashBack.hashCode());
		result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
		result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
		result = prime * result + ((categoryPaths == null) ? 0 : categoryPaths.hashCode());
		result = prime * result + ((ceroAgeRating == null) ? 0 : ceroAgeRating.hashCode());
		result = prime * result + ((clothingSize == null) ? 0 : clothingSize.hashCode());
		result = prime * result + (codAvailable ? 1231 : 1237);
		result = prime * result + ((collections == null) ? 0 : collections.hashCode());
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((colorVariants == null) ? 0 : colorVariants.hashCode());
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((department == null) ? 0 : department.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((discountPercentage == null) ? 0 : discountPercentage.hashCode());
		result = prime * result + ((ean == null) ? 0 : ean.hashCode());
		result = prime * result + ((edition == null) ? 0 : edition.hashCode());
		result = prime * result + (emiAvailable ? 1231 : 1237);
		result = prime * result + ((episodeSequence == null) ? 0 : episodeSequence.hashCode());
		result = prime * result + ((esrbAgeRating == null) ? 0 : esrbAgeRating.hashCode());
		result = prime * result + ((features == null) ? 0 : features.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((galleryInfoGalleryUrl == null) ? 0 : galleryInfoGalleryUrl.hashCode());
		result = prime * result + ((galleryPlusPictureUrl == null) ? 0 : galleryPlusPictureUrl.hashCode());
		result = prime * result + ((genre == null) ? 0 : genre.hashCode());
		result = prime * result + ((hardwarePlatform == null) ? 0 : hardwarePlatform.hashCode());
		result = prime * result + ((hazardousMaterialType == null) ? 0 : hazardousMaterialType.hashCode());
		result = prime * result + ((home == null) ? 0 : home.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imageSets == null) ? 0 : imageSets.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((imageUrlLarge == null) ? 0 : imageUrlLarge.hashCode());
		result = prime * result + ((imageUrlMedium == null) ? 0 : imageUrlMedium.hashCode());
		result = prime * result + ((imageUrlSmall == null) ? 0 : imageUrlSmall.hashCode());
		result = prime * result + ((imageUrlXL == null) ? 0 : imageUrlXL.hashCode());
		result = prime * result + ((imageUrls == null) ? 0 : imageUrls.hashCode());
		result = prime * result + (inStock ? 1231 : 1237);
		result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
		result = prime * result + ((issuePerYear == null) ? 0 : issuePerYear.hashCode());
		result = prime * result + ((itemLinks == null) ? 0 : itemLinks.hashCode());
		result = prime * result + ((itemPartNumber == null) ? 0 : itemPartNumber.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((languages == null) ? 0 : languages.hashCode());
		result = prime * result + ((legalDisclaimer == null) ? 0 : legalDisclaimer.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((magazineType == null) ? 0 : magazineType.hashCode());
		result = prime * result + ((manufacturer == null) ? 0 : manufacturer.hashCode());
		result = prime * result + ((manufacturerMaximumAgeUnit == null) ? 0 : manufacturerMaximumAgeUnit.hashCode());
		result = prime * result + ((manufacturerMaximumAgeValue == null) ? 0 : manufacturerMaximumAgeValue.hashCode());
		result = prime * result + ((manufacturerMinimumAgeUnit == null) ? 0 : manufacturerMinimumAgeUnit.hashCode());
		result = prime * result + ((manufacturerMinimumAgeValue == null) ? 0 : manufacturerMinimumAgeValue.hashCode());
		result = prime * result + ((manufacturerPartsWarrantyDescription == null) ? 0 : manufacturerPartsWarrantyDescription.hashCode());
		result = prime * result + ((mediaType == null) ? 0 : mediaType.hashCode());
		result = prime * result + (merged ? 1231 : 1237);
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((modelYear == null) ? 0 : modelYear.hashCode());
		result = prime * result + ((modifiedBy == null) ? 0 : modifiedBy.hashCode());
		result = prime * result + ((modifiedOn == null) ? 0 : modifiedOn.hashCode());
		result = prime * result + ((mpn == null) ? 0 : mpn.hashCode());
		long temp;
		temp = Double.doubleToLongBits(mrp);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (multiVariationListing ? 1231 : 1237);
		result = prime * result + ((numberOfDiscs == null) ? 0 : numberOfDiscs.hashCode());
		result = prime * result + ((numberOfIssues == null) ? 0 : numberOfIssues.hashCode());
		result = prime * result + ((numberOfItems == null) ? 0 : numberOfItems.hashCode());
		result = prime * result + ((numberOfPages == null) ? 0 : numberOfPages.hashCode());
		result = prime * result + ((numberOfTracks == null) ? 0 : numberOfTracks.hashCode());
		result = prime * result + ((offerList == null) ? 0 : offerList.hashCode());
		result = prime * result + ((offerSummaryLowestCollectiblePrice == null) ? 0 : offerSummaryLowestCollectiblePrice.hashCode());
		result = prime * result + ((offerSummaryLowestNewPrice == null) ? 0 : offerSummaryLowestNewPrice.hashCode());
		result = prime * result + ((offerSummaryTotalCollectible == null) ? 0 : offerSummaryTotalCollectible.hashCode());
		result = prime * result + ((offerSummaryTotalNew == null) ? 0 : offerSummaryTotalNew.hashCode());
		result = prime * result + offerTotalOfferPages;
		result = prime * result + offerTotalOffers;
		result = prime * result + ((offers == null) ? 0 : offers.hashCode());
		result = prime * result + ((offset == null) ? 0 : offset.hashCode());
		result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
		result = prime * result + ((originalTitle == null) ? 0 : originalTitle.hashCode());
		result = prime * result + ((packageDimensionsHeight == null) ? 0 : packageDimensionsHeight.hashCode());
		result = prime * result + ((packageDimensionsHeightUnit == null) ? 0 : packageDimensionsHeightUnit.hashCode());
		result = prime * result + ((packageDimensionsLength == null) ? 0 : packageDimensionsLength.hashCode());
		result = prime * result + ((packageDimensionsLengthUnit == null) ? 0 : packageDimensionsLengthUnit.hashCode());
		result = prime * result + ((packageDimensionsWeight == null) ? 0 : packageDimensionsWeight.hashCode());
		result = prime * result + ((packageDimensionsWeightUnit == null) ? 0 : packageDimensionsWeightUnit.hashCode());
		result = prime * result + ((packageDimensionsWidth == null) ? 0 : packageDimensionsWidth.hashCode());
		result = prime * result + ((packageDimensionsWidthUnit == null) ? 0 : packageDimensionsWidthUnit.hashCode());
		result = prime * result + ((packageQuantity == null) ? 0 : packageQuantity.hashCode());
		result = prime * result + ((partNumber == null) ? 0 : partNumber.hashCode());
		result = prime * result + ((paymentMethod == null) ? 0 : paymentMethod.hashCode());
		result = prime * result + ((paymentMethods == null) ? 0 : paymentMethods.hashCode());
		result = prime * result + ((pictureFormats == null) ? 0 : pictureFormats.hashCode());
		result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
		result = prime * result + ((productBrand == null) ? 0 : productBrand.hashCode());
		result = prime * result + ((productGroup == null) ? 0 : productGroup.hashCode());
		result = prime * result + ((productIdType == null) ? 0 : productIdType.hashCode());
		result = prime * result + ((productIdValue == null) ? 0 : productIdValue.hashCode());
		result = prime * result + ((productTypeName == null) ? 0 : productTypeName.hashCode());
		result = prime * result + ((productTypeSubcategory == null) ? 0 : productTypeSubcategory.hashCode());
		result = prime * result + ((productUrl == null) ? 0 : productUrl.hashCode());
		result = prime * result + ((publicationDate == null) ? 0 : publicationDate.hashCode());
		result = prime * result + ((publisher == null) ? 0 : publisher.hashCode());
		result = prime * result + ((regionCode == null) ? 0 : regionCode.hashCode());
		result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
		result = prime * result + (returnAccepted ? 1231 : 1237);
		temp = Double.doubleToLongBits(salesRank);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((seikodoProductCode == null) ? 0 : seikodoProductCode.hashCode());
		temp = Double.doubleToLongBits(sellingPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((sellingState == null) ? 0 : sellingState.hashCode());
		result = prime * result + ((shipToLocations == null) ? 0 : shipToLocations.hashCode());
		result = prime * result + ((shippingOptions == null) ? 0 : shippingOptions.hashCode());
		result = prime * result + ((shippingServiceCost == null) ? 0 : shippingServiceCost.hashCode());
		result = prime * result + ((shippingType == null) ? 0 : shippingType.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((sizeUnit == null) ? 0 : sizeUnit.hashCode());
		result = prime * result + ((sizeVariants == null) ? 0 : sizeVariants.hashCode());
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		result = prime * result + ((studio == null) ? 0 : studio.hashCode());
		result = prime * result + ((styleCode == null) ? 0 : styleCode.hashCode());
		result = prime * result + ((subCategoryName == null) ? 0 : subCategoryName.hashCode());
		result = prime * result + ((subTitle == null) ? 0 : subTitle.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((timeLeft == null) ? 0 : timeLeft.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + (topRatedListing ? 1231 : 1237);
		result = prime * result + ((trackSequence == null) ? 0 : trackSequence.hashCode());
		result = prime * result + ((upc == null) ? 0 : upc.hashCode());
		result = prime * result + ((variationAttributes == null) ? 0 : variationAttributes.hashCode());
		result = prime * result + ((variationSummaryHighestPrice == null) ? 0 : variationSummaryHighestPrice.hashCode());
		result = prime * result + ((variationSummaryHighestSalesPrice == null) ? 0 : variationSummaryHighestSalesPrice.hashCode());
		result = prime * result + ((variationSummaryLowestPrice == null) ? 0 : variationSummaryLowestPrice.hashCode());
		result = prime * result + ((variationSummaryLowestSalesPrice == null) ? 0 : variationSummaryLowestSalesPrice.hashCode());
		result = prime * result + ((variations == null) ? 0 : variations.hashCode());
		result = prime * result + ((warranty == null) ? 0 : warranty.hashCode());
		result = prime * result + ((weeeTaxValue == null) ? 0 : weeeTaxValue.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HomeProductInfoDTO other = (HomeProductInfoDTO) obj;
		if (accessories == null) {
			if (other.accessories != null)
				return false;
		}
		else if (!accessories.equals(other.accessories))
			return false;
		if (actors == null) {
			if (other.actors != null)
				return false;
		}
		else if (!actors.equals(other.actors))
			return false;
		if (alternateVersions == null) {
			if (other.alternateVersions != null)
				return false;
		}
		else if (!alternateVersions.equals(other.alternateVersions))
			return false;
		if (artists == null) {
			if (other.artists != null)
				return false;
		}
		else if (!artists.equals(other.artists))
			return false;
		if (aspectRatio == null) {
			if (other.aspectRatio != null)
				return false;
		}
		else if (!aspectRatio.equals(other.aspectRatio))
			return false;
		if (attributeMap == null) {
			if (other.attributeMap != null)
				return false;
		}
		else if (!attributeMap.equals(other.attributeMap))
			return false;
		if (audienceRating == null) {
			if (other.audienceRating != null)
				return false;
		}
		else if (!audienceRating.equals(other.audienceRating))
			return false;
		if (autoPay != other.autoPay)
			return false;
		if (binding == null) {
			if (other.binding != null)
				return false;
		}
		else if (!binding.equals(other.binding))
			return false;
		if (cashBack == null) {
			if (other.cashBack != null)
				return false;
		}
		else if (!cashBack.equals(other.cashBack))
			return false;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		}
		else if (!categoryId.equals(other.categoryId))
			return false;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		}
		else if (!categoryName.equals(other.categoryName))
			return false;
		if (categoryPaths == null) {
			if (other.categoryPaths != null)
				return false;
		}
		else if (!categoryPaths.equals(other.categoryPaths))
			return false;
		if (ceroAgeRating == null) {
			if (other.ceroAgeRating != null)
				return false;
		}
		else if (!ceroAgeRating.equals(other.ceroAgeRating))
			return false;
		if (clothingSize == null) {
			if (other.clothingSize != null)
				return false;
		}
		else if (!clothingSize.equals(other.clothingSize))
			return false;
		if (codAvailable != other.codAvailable)
			return false;
		if (collections == null) {
			if (other.collections != null)
				return false;
		}
		else if (!collections.equals(other.collections))
			return false;
		if (color == null) {
			if (other.color != null)
				return false;
		}
		else if (!color.equals(other.color))
			return false;
		if (colorVariants == null) {
			if (other.colorVariants != null)
				return false;
		}
		else if (!colorVariants.equals(other.colorVariants))
			return false;
		if (condition == null) {
			if (other.condition != null)
				return false;
		}
		else if (!condition.equals(other.condition))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		}
		else if (!country.equals(other.country))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		}
		else if (!createdBy.equals(other.createdBy))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		}
		else if (!createdOn.equals(other.createdOn))
			return false;
		if (department == null) {
			if (other.department != null)
				return false;
		}
		else if (!department.equals(other.department))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (discountPercentage == null) {
			if (other.discountPercentage != null)
				return false;
		}
		else if (!discountPercentage.equals(other.discountPercentage))
			return false;
		if (ean == null) {
			if (other.ean != null)
				return false;
		}
		else if (!ean.equals(other.ean))
			return false;
		if (edition == null) {
			if (other.edition != null)
				return false;
		}
		else if (!edition.equals(other.edition))
			return false;
		if (emiAvailable != other.emiAvailable)
			return false;
		if (episodeSequence == null) {
			if (other.episodeSequence != null)
				return false;
		}
		else if (!episodeSequence.equals(other.episodeSequence))
			return false;
		if (esrbAgeRating == null) {
			if (other.esrbAgeRating != null)
				return false;
		}
		else if (!esrbAgeRating.equals(other.esrbAgeRating))
			return false;
		if (features == null) {
			if (other.features != null)
				return false;
		}
		else if (!features.equals(other.features))
			return false;
		if (format == null) {
			if (other.format != null)
				return false;
		}
		else if (!format.equals(other.format))
			return false;
		if (galleryInfoGalleryUrl == null) {
			if (other.galleryInfoGalleryUrl != null)
				return false;
		}
		else if (!galleryInfoGalleryUrl.equals(other.galleryInfoGalleryUrl))
			return false;
		if (galleryPlusPictureUrl == null) {
			if (other.galleryPlusPictureUrl != null)
				return false;
		}
		else if (!galleryPlusPictureUrl.equals(other.galleryPlusPictureUrl))
			return false;
		if (genre == null) {
			if (other.genre != null)
				return false;
		}
		else if (!genre.equals(other.genre))
			return false;
		if (hardwarePlatform == null) {
			if (other.hardwarePlatform != null)
				return false;
		}
		else if (!hardwarePlatform.equals(other.hardwarePlatform))
			return false;
		if (hazardousMaterialType == null) {
			if (other.hazardousMaterialType != null)
				return false;
		}
		else if (!hazardousMaterialType.equals(other.hazardousMaterialType))
			return false;
		if (home == null) {
			if (other.home != null)
				return false;
		}
		else if (!home.equals(other.home))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (imageSets == null) {
			if (other.imageSets != null)
				return false;
		}
		else if (!imageSets.equals(other.imageSets))
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		}
		else if (!imageUrl.equals(other.imageUrl))
			return false;
		if (imageUrlLarge == null) {
			if (other.imageUrlLarge != null)
				return false;
		}
		else if (!imageUrlLarge.equals(other.imageUrlLarge))
			return false;
		if (imageUrlMedium == null) {
			if (other.imageUrlMedium != null)
				return false;
		}
		else if (!imageUrlMedium.equals(other.imageUrlMedium))
			return false;
		if (imageUrlSmall == null) {
			if (other.imageUrlSmall != null)
				return false;
		}
		else if (!imageUrlSmall.equals(other.imageUrlSmall))
			return false;
		if (imageUrlXL == null) {
			if (other.imageUrlXL != null)
				return false;
		}
		else if (!imageUrlXL.equals(other.imageUrlXL))
			return false;
		if (imageUrls == null) {
			if (other.imageUrls != null)
				return false;
		}
		else if (!imageUrls.equals(other.imageUrls))
			return false;
		if (inStock != other.inStock)
			return false;
		if (isbn == null) {
			if (other.isbn != null)
				return false;
		}
		else if (!isbn.equals(other.isbn))
			return false;
		if (issuePerYear == null) {
			if (other.issuePerYear != null)
				return false;
		}
		else if (!issuePerYear.equals(other.issuePerYear))
			return false;
		if (itemLinks == null) {
			if (other.itemLinks != null)
				return false;
		}
		else if (!itemLinks.equals(other.itemLinks))
			return false;
		if (itemPartNumber == null) {
			if (other.itemPartNumber != null)
				return false;
		}
		else if (!itemPartNumber.equals(other.itemPartNumber))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		}
		else if (!label.equals(other.label))
			return false;
		if (languages == null) {
			if (other.languages != null)
				return false;
		}
		else if (!languages.equals(other.languages))
			return false;
		if (legalDisclaimer == null) {
			if (other.legalDisclaimer != null)
				return false;
		}
		else if (!legalDisclaimer.equals(other.legalDisclaimer))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		}
		else if (!location.equals(other.location))
			return false;
		if (magazineType == null) {
			if (other.magazineType != null)
				return false;
		}
		else if (!magazineType.equals(other.magazineType))
			return false;
		if (manufacturer == null) {
			if (other.manufacturer != null)
				return false;
		}
		else if (!manufacturer.equals(other.manufacturer))
			return false;
		if (manufacturerMaximumAgeUnit == null) {
			if (other.manufacturerMaximumAgeUnit != null)
				return false;
		}
		else if (!manufacturerMaximumAgeUnit.equals(other.manufacturerMaximumAgeUnit))
			return false;
		if (manufacturerMaximumAgeValue == null) {
			if (other.manufacturerMaximumAgeValue != null)
				return false;
		}
		else if (!manufacturerMaximumAgeValue.equals(other.manufacturerMaximumAgeValue))
			return false;
		if (manufacturerMinimumAgeUnit == null) {
			if (other.manufacturerMinimumAgeUnit != null)
				return false;
		}
		else if (!manufacturerMinimumAgeUnit.equals(other.manufacturerMinimumAgeUnit))
			return false;
		if (manufacturerMinimumAgeValue == null) {
			if (other.manufacturerMinimumAgeValue != null)
				return false;
		}
		else if (!manufacturerMinimumAgeValue.equals(other.manufacturerMinimumAgeValue))
			return false;
		if (manufacturerPartsWarrantyDescription == null) {
			if (other.manufacturerPartsWarrantyDescription != null)
				return false;
		}
		else if (!manufacturerPartsWarrantyDescription.equals(other.manufacturerPartsWarrantyDescription))
			return false;
		if (mediaType == null) {
			if (other.mediaType != null)
				return false;
		}
		else if (!mediaType.equals(other.mediaType))
			return false;
		if (merged != other.merged)
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		}
		else if (!model.equals(other.model))
			return false;
		if (modelYear == null) {
			if (other.modelYear != null)
				return false;
		}
		else if (!modelYear.equals(other.modelYear))
			return false;
		if (modifiedBy == null) {
			if (other.modifiedBy != null)
				return false;
		}
		else if (!modifiedBy.equals(other.modifiedBy))
			return false;
		if (modifiedOn == null) {
			if (other.modifiedOn != null)
				return false;
		}
		else if (!modifiedOn.equals(other.modifiedOn))
			return false;
		if (mpn == null) {
			if (other.mpn != null)
				return false;
		}
		else if (!mpn.equals(other.mpn))
			return false;
		if (Double.doubleToLongBits(mrp) != Double.doubleToLongBits(other.mrp))
			return false;
		if (multiVariationListing != other.multiVariationListing)
			return false;
		if (numberOfDiscs == null) {
			if (other.numberOfDiscs != null)
				return false;
		}
		else if (!numberOfDiscs.equals(other.numberOfDiscs))
			return false;
		if (numberOfIssues == null) {
			if (other.numberOfIssues != null)
				return false;
		}
		else if (!numberOfIssues.equals(other.numberOfIssues))
			return false;
		if (numberOfItems == null) {
			if (other.numberOfItems != null)
				return false;
		}
		else if (!numberOfItems.equals(other.numberOfItems))
			return false;
		if (numberOfPages == null) {
			if (other.numberOfPages != null)
				return false;
		}
		else if (!numberOfPages.equals(other.numberOfPages))
			return false;
		if (numberOfTracks == null) {
			if (other.numberOfTracks != null)
				return false;
		}
		else if (!numberOfTracks.equals(other.numberOfTracks))
			return false;
		if (offerList == null) {
			if (other.offerList != null)
				return false;
		}
		else if (!offerList.equals(other.offerList))
			return false;
		if (offerSummaryLowestCollectiblePrice == null) {
			if (other.offerSummaryLowestCollectiblePrice != null)
				return false;
		}
		else if (!offerSummaryLowestCollectiblePrice.equals(other.offerSummaryLowestCollectiblePrice))
			return false;
		if (offerSummaryLowestNewPrice == null) {
			if (other.offerSummaryLowestNewPrice != null)
				return false;
		}
		else if (!offerSummaryLowestNewPrice.equals(other.offerSummaryLowestNewPrice))
			return false;
		if (offerSummaryTotalCollectible == null) {
			if (other.offerSummaryTotalCollectible != null)
				return false;
		}
		else if (!offerSummaryTotalCollectible.equals(other.offerSummaryTotalCollectible))
			return false;
		if (offerSummaryTotalNew == null) {
			if (other.offerSummaryTotalNew != null)
				return false;
		}
		else if (!offerSummaryTotalNew.equals(other.offerSummaryTotalNew))
			return false;
		if (offerTotalOfferPages != other.offerTotalOfferPages)
			return false;
		if (offerTotalOffers != other.offerTotalOffers)
			return false;
		if (offers == null) {
			if (other.offers != null)
				return false;
		}
		else if (!offers.equals(other.offers))
			return false;
		if (offset == null) {
			if (other.offset != null)
				return false;
		}
		else if (!offset.equals(other.offset))
			return false;
		if (operatingSystem == null) {
			if (other.operatingSystem != null)
				return false;
		}
		else if (!operatingSystem.equals(other.operatingSystem))
			return false;
		if (originalTitle == null) {
			if (other.originalTitle != null)
				return false;
		}
		else if (!originalTitle.equals(other.originalTitle))
			return false;
		if (packageDimensionsHeight == null) {
			if (other.packageDimensionsHeight != null)
				return false;
		}
		else if (!packageDimensionsHeight.equals(other.packageDimensionsHeight))
			return false;
		if (packageDimensionsHeightUnit == null) {
			if (other.packageDimensionsHeightUnit != null)
				return false;
		}
		else if (!packageDimensionsHeightUnit.equals(other.packageDimensionsHeightUnit))
			return false;
		if (packageDimensionsLength == null) {
			if (other.packageDimensionsLength != null)
				return false;
		}
		else if (!packageDimensionsLength.equals(other.packageDimensionsLength))
			return false;
		if (packageDimensionsLengthUnit == null) {
			if (other.packageDimensionsLengthUnit != null)
				return false;
		}
		else if (!packageDimensionsLengthUnit.equals(other.packageDimensionsLengthUnit))
			return false;
		if (packageDimensionsWeight == null) {
			if (other.packageDimensionsWeight != null)
				return false;
		}
		else if (!packageDimensionsWeight.equals(other.packageDimensionsWeight))
			return false;
		if (packageDimensionsWeightUnit == null) {
			if (other.packageDimensionsWeightUnit != null)
				return false;
		}
		else if (!packageDimensionsWeightUnit.equals(other.packageDimensionsWeightUnit))
			return false;
		if (packageDimensionsWidth == null) {
			if (other.packageDimensionsWidth != null)
				return false;
		}
		else if (!packageDimensionsWidth.equals(other.packageDimensionsWidth))
			return false;
		if (packageDimensionsWidthUnit == null) {
			if (other.packageDimensionsWidthUnit != null)
				return false;
		}
		else if (!packageDimensionsWidthUnit.equals(other.packageDimensionsWidthUnit))
			return false;
		if (packageQuantity == null) {
			if (other.packageQuantity != null)
				return false;
		}
		else if (!packageQuantity.equals(other.packageQuantity))
			return false;
		if (partNumber == null) {
			if (other.partNumber != null)
				return false;
		}
		else if (!partNumber.equals(other.partNumber))
			return false;
		if (paymentMethod == null) {
			if (other.paymentMethod != null)
				return false;
		}
		else if (!paymentMethod.equals(other.paymentMethod))
			return false;
		if (paymentMethods == null) {
			if (other.paymentMethods != null)
				return false;
		}
		else if (!paymentMethods.equals(other.paymentMethods))
			return false;
		if (pictureFormats == null) {
			if (other.pictureFormats != null)
				return false;
		}
		else if (!pictureFormats.equals(other.pictureFormats))
			return false;
		if (postalCode == null) {
			if (other.postalCode != null)
				return false;
		}
		else if (!postalCode.equals(other.postalCode))
			return false;
		if (productBrand == null) {
			if (other.productBrand != null)
				return false;
		}
		else if (!productBrand.equals(other.productBrand))
			return false;
		if (productGroup == null) {
			if (other.productGroup != null)
				return false;
		}
		else if (!productGroup.equals(other.productGroup))
			return false;
		if (productIdType == null) {
			if (other.productIdType != null)
				return false;
		}
		else if (!productIdType.equals(other.productIdType))
			return false;
		if (productIdValue == null) {
			if (other.productIdValue != null)
				return false;
		}
		else if (!productIdValue.equals(other.productIdValue))
			return false;
		if (productTypeName == null) {
			if (other.productTypeName != null)
				return false;
		}
		else if (!productTypeName.equals(other.productTypeName))
			return false;
		if (productTypeSubcategory == null) {
			if (other.productTypeSubcategory != null)
				return false;
		}
		else if (!productTypeSubcategory.equals(other.productTypeSubcategory))
			return false;
		if (productUrl == null) {
			if (other.productUrl != null)
				return false;
		}
		else if (!productUrl.equals(other.productUrl))
			return false;
		if (publicationDate == null) {
			if (other.publicationDate != null)
				return false;
		}
		else if (!publicationDate.equals(other.publicationDate))
			return false;
		if (publisher == null) {
			if (other.publisher != null)
				return false;
		}
		else if (!publisher.equals(other.publisher))
			return false;
		if (regionCode == null) {
			if (other.regionCode != null)
				return false;
		}
		else if (!regionCode.equals(other.regionCode))
			return false;
		if (releaseDate == null) {
			if (other.releaseDate != null)
				return false;
		}
		else if (!releaseDate.equals(other.releaseDate))
			return false;
		if (returnAccepted != other.returnAccepted)
			return false;
		if (Double.doubleToLongBits(salesRank) != Double.doubleToLongBits(other.salesRank))
			return false;
		if (seikodoProductCode == null) {
			if (other.seikodoProductCode != null)
				return false;
		}
		else if (!seikodoProductCode.equals(other.seikodoProductCode))
			return false;
		if (Double.doubleToLongBits(sellingPrice) != Double.doubleToLongBits(other.sellingPrice))
			return false;
		if (sellingState == null) {
			if (other.sellingState != null)
				return false;
		}
		else if (!sellingState.equals(other.sellingState))
			return false;
		if (shipToLocations == null) {
			if (other.shipToLocations != null)
				return false;
		}
		else if (!shipToLocations.equals(other.shipToLocations))
			return false;
		if (shippingOptions == null) {
			if (other.shippingOptions != null)
				return false;
		}
		else if (!shippingOptions.equals(other.shippingOptions))
			return false;
		if (shippingServiceCost == null) {
			if (other.shippingServiceCost != null)
				return false;
		}
		else if (!shippingServiceCost.equals(other.shippingServiceCost))
			return false;
		if (shippingType == null) {
			if (other.shippingType != null)
				return false;
		}
		else if (!shippingType.equals(other.shippingType))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		}
		else if (!size.equals(other.size))
			return false;
		if (sizeUnit == null) {
			if (other.sizeUnit != null)
				return false;
		}
		else if (!sizeUnit.equals(other.sizeUnit))
			return false;
		if (sizeVariants == null) {
			if (other.sizeVariants != null)
				return false;
		}
		else if (!sizeVariants.equals(other.sizeVariants))
			return false;
		if (sku == null) {
			if (other.sku != null)
				return false;
		}
		else if (!sku.equals(other.sku))
			return false;
		if (studio == null) {
			if (other.studio != null)
				return false;
		}
		else if (!studio.equals(other.studio))
			return false;
		if (styleCode == null) {
			if (other.styleCode != null)
				return false;
		}
		else if (!styleCode.equals(other.styleCode))
			return false;
		if (subCategoryName == null) {
			if (other.subCategoryName != null)
				return false;
		}
		else if (!subCategoryName.equals(other.subCategoryName))
			return false;
		if (subTitle == null) {
			if (other.subTitle != null)
				return false;
		}
		else if (!subTitle.equals(other.subTitle))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		}
		else if (!tags.equals(other.tags))
			return false;
		if (timeLeft == null) {
			if (other.timeLeft != null)
				return false;
		}
		else if (!timeLeft.equals(other.timeLeft))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
			return false;
		if (topRatedListing != other.topRatedListing)
			return false;
		if (trackSequence == null) {
			if (other.trackSequence != null)
				return false;
		}
		else if (!trackSequence.equals(other.trackSequence))
			return false;
		if (upc == null) {
			if (other.upc != null)
				return false;
		}
		else if (!upc.equals(other.upc))
			return false;
		if (variationAttributes == null) {
			if (other.variationAttributes != null)
				return false;
		}
		else if (!variationAttributes.equals(other.variationAttributes))
			return false;
		if (variationSummaryHighestPrice == null) {
			if (other.variationSummaryHighestPrice != null)
				return false;
		}
		else if (!variationSummaryHighestPrice.equals(other.variationSummaryHighestPrice))
			return false;
		if (variationSummaryHighestSalesPrice == null) {
			if (other.variationSummaryHighestSalesPrice != null)
				return false;
		}
		else if (!variationSummaryHighestSalesPrice.equals(other.variationSummaryHighestSalesPrice))
			return false;
		if (variationSummaryLowestPrice == null) {
			if (other.variationSummaryLowestPrice != null)
				return false;
		}
		else if (!variationSummaryLowestPrice.equals(other.variationSummaryLowestPrice))
			return false;
		if (variationSummaryLowestSalesPrice == null) {
			if (other.variationSummaryLowestSalesPrice != null)
				return false;
		}
		else if (!variationSummaryLowestSalesPrice.equals(other.variationSummaryLowestSalesPrice))
			return false;
		if (variations == null) {
			if (other.variations != null)
				return false;
		}
		else if (!variations.equals(other.variations))
			return false;
		if (warranty == null) {
			if (other.warranty != null)
				return false;
		}
		else if (!warranty.equals(other.warranty))
			return false;
		if (weeeTaxValue == null) {
			if (other.weeeTaxValue != null)
				return false;
		}
		else if (!weeeTaxValue.equals(other.weeeTaxValue))
			return false;
		return true;
	}
	/**
	 * @return the home
	 */
	public String getHome() {
		return home;
	}
	/**
	 * @param home the home to set
	 */
	public void setHome(String home) {
		this.home = home;
	}
	/**
	 * @return the subCategoryName
	 */
	public String getSubCategoryName() {
		return subCategoryName;
	}
	/**
	 * @param subCategoryName the subCategoryName to set
	 */
	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the merged
	 */
	public boolean isMerged() {
		return merged;
	}
	/**
	 * @param merged the merged to set
	 */
	public void setMerged(boolean merged) {
		this.merged = merged;
	}
	/**
	 * @return the autoPay
	 */
	public boolean isAutoPay() {
		return autoPay;
	}
	/**
	 * @param autoPay the autoPay to set
	 */
	public void setAutoPay(boolean autoPay) {
		this.autoPay = autoPay;
	}
	/**
	 * @return the multiVariationListing
	 */
	public boolean isMultiVariationListing() {
		return multiVariationListing;
	}
	/**
	 * @param multiVariationListing the multiVariationListing to set
	 */
	public void setMultiVariationListing(boolean multiVariationListing) {
		this.multiVariationListing = multiVariationListing;
	}
	/**
	 * @return the topRatedListing
	 */
	public boolean isTopRatedListing() {
		return topRatedListing;
	}
	/**
	 * @param topRatedListing the topRatedListing to set
	 */
	public void setTopRatedListing(boolean topRatedListing) {
		this.topRatedListing = topRatedListing;
	}
	/**
	 * @return the returnAccepted
	 */
	public boolean isReturnAccepted() {
		return returnAccepted;
	}
	/**
	 * @param returnAccepted the returnAccepted to set
	 */
	public void setReturnAccepted(boolean returnAccepted) {
		this.returnAccepted = returnAccepted;
	}
	/**
	 * @return the shippingOptions
	 */
	public ShippingOptionsDTO getShippingOptions() {
		return shippingOptions;
	}
	/**
	 * @param shippingOptions the shippingOptions to set
	 */
	public void setShippingOptions(ShippingOptionsDTO shippingOptions) {
		this.shippingOptions = shippingOptions;
	}
	/**
	 * @return the offerList
	 */
	public List<ProductOfferDTO> getOfferList() {
		return offerList;
	}
	/**
	 * @param offerList the offerList to set
	 */
	public void setOfferList(List<ProductOfferDTO> offerList) {
		this.offerList = offerList;
	}
	/**
	 * @return the imageSets
	 */
	public Map<String, Map<String, String>> getImageSets() {
		return imageSets;
	}
	/**
	 * @param imageSets the imageSets to set
	 */
	public void setImageSets(Map<String, Map<String, String>> imageSets) {
		this.imageSets = imageSets;
	}
	/**
	 * @return the variationAttributes
	 */
	public Map<String, List<String>> getVariationAttributes() {
		return variationAttributes;
	}
	/**
	 * @param variationAttributes the variationAttributes to set
	 */
	public void setVariationAttributes(Map<String, List<String>> variationAttributes) {
		this.variationAttributes = variationAttributes;
	}
	/**
	 * @return the languages
	 */
	public List<Map<String, String>> getLanguages() {
		return languages;
	}
	/**
	 * @param languages the languages to set
	 */
	public void setLanguages(List<Map<String, String>> languages) {
		this.languages = languages;
	}
	/**
	 * @return the codAvailable
	 */
	public boolean isCodAvailable() {
		return codAvailable;
	}
	/**
	 * @param codAvailable the codAvailable to set
	 */
	public void setCodAvailable(boolean codAvailable) {
		this.codAvailable = codAvailable;
	}
	/**
	 * @return the emiAvailable
	 */
	public boolean isEmiAvailable() {
		return emiAvailable;
	}
	/**
	 * @param emiAvailable the emiAvailable to set
	 */
	public void setEmiAvailable(boolean emiAvailable) {
		this.emiAvailable = emiAvailable;
	}
	/**
	 * @return the inStock
	 */
	public boolean isInStock() {
		return inStock;
	}
	/**
	 * @param inStock the inStock to set
	 */
	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}
	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}
	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	/**
	 * @return the modifiedOn
	 */
	public Date getModifiedOn() {
		return modifiedOn;
	}
	/**
	 * @param modifiedOn the modifiedOn to set
	 */
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	/**
	 * @return the offerTotalOfferPages
	 */
	public int getOfferTotalOfferPages() {
		return offerTotalOfferPages;
	}
	/**
	 * @param offerTotalOfferPages the offerTotalOfferPages to set
	 */
	public void setOfferTotalOfferPages(int offerTotalOfferPages) {
		this.offerTotalOfferPages = offerTotalOfferPages;
	}
	/**
	 * @return the offerTotalOffers
	 */
	public int getOfferTotalOffers() {
		return offerTotalOffers;
	}
	/**
	 * @param offerTotalOffers the offerTotalOffers to set
	 */
	public void setOfferTotalOffers(int offerTotalOffers) {
		this.offerTotalOffers = offerTotalOffers;
	}
	/**
	 * @return the actors
	 */
	public List<String> getActors() {
		return actors;
	}
	/**
	 * @param actors the actors to set
	 */
	public void setActors(List<String> actors) {
		this.actors = actors;
	}
	/**
	 * @return the artists
	 */
	public List<String> getArtists() {
		return artists;
	}
	/**
	 * @param artists the artists to set
	 */
	public void setArtists(List<String> artists) {
		this.artists = artists;
	}
	/**
	 * @return the features
	 */
	public List<String> getFeatures() {
		return features;
	}
	/**
	 * @param features the features to set
	 */
	public void setFeatures(List<String> features) {
		this.features = features;
	}
	/**
	 * @return the format
	 */
	public List<String> getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(List<String> format) {
		this.format = format;
	}
	/**
	 * @return the galleryInfoGalleryUrl
	 */
	public List<String> getGalleryInfoGalleryUrl() {
		return galleryInfoGalleryUrl;
	}
	/**
	 * @param galleryInfoGalleryUrl the galleryInfoGalleryUrl to set
	 */
	public void setGalleryInfoGalleryUrl(List<String> galleryInfoGalleryUrl) {
		this.galleryInfoGalleryUrl = galleryInfoGalleryUrl;
	}
	/**
	 * @return the galleryPlusPictureUrl
	 */
	public List<String> getGalleryPlusPictureUrl() {
		return galleryPlusPictureUrl;
	}
	/**
	 * @param galleryPlusPictureUrl the galleryPlusPictureUrl to set
	 */
	public void setGalleryPlusPictureUrl(List<String> galleryPlusPictureUrl) {
		this.galleryPlusPictureUrl = galleryPlusPictureUrl;
	}
	/**
	 * @return the itemLinks
	 */
	public List<String> getItemLinks() {
		return itemLinks;
	}
	/**
	 * @param itemLinks the itemLinks to set
	 */
	public void setItemLinks(List<String> itemLinks) {
		this.itemLinks = itemLinks;
	}
	/**
	 * @return the paymentMethods
	 */
	public List<String> getPaymentMethods() {
		return paymentMethods;
	}
	/**
	 * @param paymentMethods the paymentMethods to set
	 */
	public void setPaymentMethods(List<String> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}
	/**
	 * @return the pictureFormats
	 */
	public List<String> getPictureFormats() {
		return pictureFormats;
	}
	/**
	 * @param pictureFormats the pictureFormats to set
	 */
	public void setPictureFormats(List<String> pictureFormats) {
		this.pictureFormats = pictureFormats;
	}
	/**
	 * @return the shipToLocations
	 */
	public List<String> getShipToLocations() {
		return shipToLocations;
	}
	/**
	 * @param shipToLocations the shipToLocations to set
	 */
	public void setShipToLocations(List<String> shipToLocations) {
		this.shipToLocations = shipToLocations;
	}
	/**
	 * @return the variations
	 */
	public List<String> getVariations() {
		return variations;
	}
	/**
	 * @param variations the variations to set
	 */
	public void setVariations(List<String> variations) {
		this.variations = variations;
	}
	/**
	 * @return the accessories
	 */
	public Map<String, String> getAccessories() {
		return accessories;
	}
	/**
	 * @param accessories the accessories to set
	 */
	public void setAccessories(Map<String, String> accessories) {
		this.accessories = accessories;
	}
	/**
	 * @return the alternateVersions
	 */
	public Map<String, String> getAlternateVersions() {
		return alternateVersions;
	}
	/**
	 * @param alternateVersions the alternateVersions to set
	 */
	public void setAlternateVersions(Map<String, String> alternateVersions) {
		this.alternateVersions = alternateVersions;
	}
	/**
	 * @return the attributeMap
	 */
	public Map<String, String> getAttributeMap() {
		return attributeMap;
	}
	/**
	 * @param attributeMap the attributeMap to set
	 */
	public void setAttributeMap(Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}
	/**
	 * @return the collections
	 */
	public Map<String, String> getCollections() {
		return collections;
	}
	/**
	 * @param collections the collections to set
	 */
	public void setCollections(Map<String, String> collections) {
		this.collections = collections;
	}
	/**
	 * @return the imageUrls
	 */
	public Map<String, String> getImageUrls() {
		return imageUrls;
	}
	/**
	 * @param imageUrls the imageUrls to set
	 */
	public void setImageUrls(Map<String, String> imageUrls) {
		this.imageUrls = imageUrls;
	}
	/**
	 * @return the categoryPaths
	 */
	public Set<String> getCategoryPaths() {
		return categoryPaths;
	}
	/**
	 * @param categoryPaths the categoryPaths to set
	 */
	public void setCategoryPaths(Set<String> categoryPaths) {
		this.categoryPaths = categoryPaths;
	}
	/**
	 * @return the offers
	 */
	public Set<String> getOffers() {
		return offers;
	}
	/**
	 * @param offers the offers to set
	 */
	public void setOffers(Set<String> offers) {
		this.offers = offers;
	}
	/**
	 * @return the tags
	 */
	public Set<String> getTags() {
		return tags;
	}
	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
	/**
	 * @return the aspectRatio
	 */
	public String getAspectRatio() {
		return aspectRatio;
	}
	/**
	 * @param aspectRatio the aspectRatio to set
	 */
	public void setAspectRatio(String aspectRatio) {
		this.aspectRatio = aspectRatio;
	}
	/**
	 * @return the audienceRating
	 */
	public String getAudienceRating() {
		return audienceRating;
	}
	/**
	 * @param audienceRating the audienceRating to set
	 */
	public void setAudienceRating(String audienceRating) {
		this.audienceRating = audienceRating;
	}
	/**
	 * @return the binding
	 */
	public String getBinding() {
		return binding;
	}
	/**
	 * @param binding the binding to set
	 */
	public void setBinding(String binding) {
		this.binding = binding;
	}
	/**
	 * @return the cashBack
	 */
	public String getCashBack() {
		return cashBack;
	}
	/**
	 * @param cashBack the cashBack to set
	 */
	public void setCashBack(String cashBack) {
		this.cashBack = cashBack;
	}
	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}
	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}
	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	/**
	 * @return the ceroAgeRating
	 */
	public String getCeroAgeRating() {
		return ceroAgeRating;
	}
	/**
	 * @param ceroAgeRating the ceroAgeRating to set
	 */
	public void setCeroAgeRating(String ceroAgeRating) {
		this.ceroAgeRating = ceroAgeRating;
	}
	/**
	 * @return the clothingSize
	 */
	public String getClothingSize() {
		return clothingSize;
	}
	/**
	 * @param clothingSize the clothingSize to set
	 */
	public void setClothingSize(String clothingSize) {
		this.clothingSize = clothingSize;
	}
	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
	/**
	 * @return the colorVariants
	 */
	public String getColorVariants() {
		return colorVariants;
	}
	/**
	 * @param colorVariants the colorVariants to set
	 */
	public void setColorVariants(String colorVariants) {
		this.colorVariants = colorVariants;
	}
	/**
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}
	/**
	 * @param condition the condition to set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}
	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the discountPercentage
	 */
	public String getDiscountPercentage() {
		return discountPercentage;
	}
	/**
	 * @param discountPercentage the discountPercentage to set
	 */
	public void setDiscountPercentage(String discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	/**
	 * @return the ean
	 */
	public String getEan() {
		return ean;
	}
	/**
	 * @param ean the ean to set
	 */
	public void setEan(String ean) {
		this.ean = ean;
	}
	/**
	 * @return the edition
	 */
	public String getEdition() {
		return edition;
	}
	/**
	 * @param edition the edition to set
	 */
	public void setEdition(String edition) {
		this.edition = edition;
	}
	/**
	 * @return the episodeSequence
	 */
	public String getEpisodeSequence() {
		return episodeSequence;
	}
	/**
	 * @param episodeSequence the episodeSequence to set
	 */
	public void setEpisodeSequence(String episodeSequence) {
		this.episodeSequence = episodeSequence;
	}
	/**
	 * @return the esrbAgeRating
	 */
	public String getEsrbAgeRating() {
		return esrbAgeRating;
	}
	/**
	 * @param esrbAgeRating the esrbAgeRating to set
	 */
	public void setEsrbAgeRating(String esrbAgeRating) {
		this.esrbAgeRating = esrbAgeRating;
	}
	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}
	/**
	 * @return the hardwarePlatform
	 */
	public String getHardwarePlatform() {
		return hardwarePlatform;
	}
	/**
	 * @param hardwarePlatform the hardwarePlatform to set
	 */
	public void setHardwarePlatform(String hardwarePlatform) {
		this.hardwarePlatform = hardwarePlatform;
	}
	/**
	 * @return the hazardousMaterialType
	 */
	public String getHazardousMaterialType() {
		return hazardousMaterialType;
	}
	/**
	 * @param hazardousMaterialType the hazardousMaterialType to set
	 */
	public void setHazardousMaterialType(String hazardousMaterialType) {
		this.hazardousMaterialType = hazardousMaterialType;
	}
	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	/**
	 * @return the imageUrlLarge
	 */
	public String getImageUrlLarge() {
		return imageUrlLarge;
	}
	/**
	 * @param imageUrlLarge the imageUrlLarge to set
	 */
	public void setImageUrlLarge(String imageUrlLarge) {
		this.imageUrlLarge = imageUrlLarge;
	}
	/**
	 * @return the imageUrlMedium
	 */
	public String getImageUrlMedium() {
		return imageUrlMedium;
	}
	/**
	 * @param imageUrlMedium the imageUrlMedium to set
	 */
	public void setImageUrlMedium(String imageUrlMedium) {
		this.imageUrlMedium = imageUrlMedium;
	}
	/**
	 * @return the imageUrlSmall
	 */
	public String getImageUrlSmall() {
		return imageUrlSmall;
	}
	/**
	 * @param imageUrlSmall the imageUrlSmall to set
	 */
	public void setImageUrlSmall(String imageUrlSmall) {
		this.imageUrlSmall = imageUrlSmall;
	}
	/**
	 * @return the imageUrlXL
	 */
	public String getImageUrlXL() {
		return imageUrlXL;
	}
	/**
	 * @param imageUrlXL the imageUrlXL to set
	 */
	public void setImageUrlXL(String imageUrlXL) {
		this.imageUrlXL = imageUrlXL;
	}
	/**
	 * @return the isbn
	 */
	public String getIsbn() {
		return isbn;
	}
	/**
	 * @param isbn the isbn to set
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	/**
	 * @return the issuePerYear
	 */
	public String getIssuePerYear() {
		return issuePerYear;
	}
	/**
	 * @param issuePerYear the issuePerYear to set
	 */
	public void setIssuePerYear(String issuePerYear) {
		this.issuePerYear = issuePerYear;
	}
	/**
	 * @return the itemPartNumber
	 */
	public String getItemPartNumber() {
		return itemPartNumber;
	}
	/**
	 * @param itemPartNumber the itemPartNumber to set
	 */
	public void setItemPartNumber(String itemPartNumber) {
		this.itemPartNumber = itemPartNumber;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the legalDisclaimer
	 */
	public String getLegalDisclaimer() {
		return legalDisclaimer;
	}
	/**
	 * @param legalDisclaimer the legalDisclaimer to set
	 */
	public void setLegalDisclaimer(String legalDisclaimer) {
		this.legalDisclaimer = legalDisclaimer;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the magazineType
	 */
	public String getMagazineType() {
		return magazineType;
	}
	/**
	 * @param magazineType the magazineType to set
	 */
	public void setMagazineType(String magazineType) {
		this.magazineType = magazineType;
	}
	/**
	 * @return the manufacturer
	 */
	public String getManufacturer() {
		return manufacturer;
	}
	/**
	 * @param manufacturer the manufacturer to set
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	/**
	 * @return the manufacturerMaximumAgeUnit
	 */
	public String getManufacturerMaximumAgeUnit() {
		return manufacturerMaximumAgeUnit;
	}
	/**
	 * @param manufacturerMaximumAgeUnit the manufacturerMaximumAgeUnit to set
	 */
	public void setManufacturerMaximumAgeUnit(String manufacturerMaximumAgeUnit) {
		this.manufacturerMaximumAgeUnit = manufacturerMaximumAgeUnit;
	}
	/**
	 * @return the manufacturerMaximumAgeValue
	 */
	public String getManufacturerMaximumAgeValue() {
		return manufacturerMaximumAgeValue;
	}
	/**
	 * @param manufacturerMaximumAgeValue the manufacturerMaximumAgeValue to set
	 */
	public void setManufacturerMaximumAgeValue(String manufacturerMaximumAgeValue) {
		this.manufacturerMaximumAgeValue = manufacturerMaximumAgeValue;
	}
	/**
	 * @return the manufacturerMinimumAgeUnit
	 */
	public String getManufacturerMinimumAgeUnit() {
		return manufacturerMinimumAgeUnit;
	}
	/**
	 * @param manufacturerMinimumAgeUnit the manufacturerMinimumAgeUnit to set
	 */
	public void setManufacturerMinimumAgeUnit(String manufacturerMinimumAgeUnit) {
		this.manufacturerMinimumAgeUnit = manufacturerMinimumAgeUnit;
	}
	/**
	 * @return the manufacturerMinimumAgeValue
	 */
	public String getManufacturerMinimumAgeValue() {
		return manufacturerMinimumAgeValue;
	}
	/**
	 * @param manufacturerMinimumAgeValue the manufacturerMinimumAgeValue to set
	 */
	public void setManufacturerMinimumAgeValue(String manufacturerMinimumAgeValue) {
		this.manufacturerMinimumAgeValue = manufacturerMinimumAgeValue;
	}
	/**
	 * @return the manufacturerPartsWarrantyDescription
	 */
	public String getManufacturerPartsWarrantyDescription() {
		return manufacturerPartsWarrantyDescription;
	}
	/**
	 * @param manufacturerPartsWarrantyDescription the manufacturerPartsWarrantyDescription to set
	 */
	public void setManufacturerPartsWarrantyDescription(String manufacturerPartsWarrantyDescription) {
		this.manufacturerPartsWarrantyDescription = manufacturerPartsWarrantyDescription;
	}
	/**
	 * @return the mediaType
	 */
	public String getMediaType() {
		return mediaType;
	}
	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @return the modelYear
	 */
	public String getModelYear() {
		return modelYear;
	}
	/**
	 * @param modelYear the modelYear to set
	 */
	public void setModelYear(String modelYear) {
		this.modelYear = modelYear;
	}
	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}
	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	/**
	 * @return the mpn
	 */
	public String getMpn() {
		return mpn;
	}
	/**
	 * @param mpn the mpn to set
	 */
	public void setMpn(String mpn) {
		this.mpn = mpn;
	}
	/**
	 * @return the mrp
	 */
	public double getMrp() {
		return mrp;
	}
	/**
	 * @param mrp the mrp to set
	 */
	public void setMrp(double mrp) {
		this.mrp = mrp;
	}
	/**
	 * @return the numberOfDiscs
	 */
	public String getNumberOfDiscs() {
		return numberOfDiscs;
	}
	/**
	 * @param numberOfDiscs the numberOfDiscs to set
	 */
	public void setNumberOfDiscs(String numberOfDiscs) {
		this.numberOfDiscs = numberOfDiscs;
	}
	/**
	 * @return the numberOfIssues
	 */
	public String getNumberOfIssues() {
		return numberOfIssues;
	}
	/**
	 * @param numberOfIssues the numberOfIssues to set
	 */
	public void setNumberOfIssues(String numberOfIssues) {
		this.numberOfIssues = numberOfIssues;
	}
	/**
	 * @return the numberOfItems
	 */
	public String getNumberOfItems() {
		return numberOfItems;
	}
	/**
	 * @param numberOfItems the numberOfItems to set
	 */
	public void setNumberOfItems(String numberOfItems) {
		this.numberOfItems = numberOfItems;
	}
	/**
	 * @return the numberOfPages
	 */
	public String getNumberOfPages() {
		return numberOfPages;
	}
	/**
	 * @param numberOfPages the numberOfPages to set
	 */
	public void setNumberOfPages(String numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
	/**
	 * @return the numberOfTracks
	 */
	public String getNumberOfTracks() {
		return numberOfTracks;
	}
	/**
	 * @param numberOfTracks the numberOfTracks to set
	 */
	public void setNumberOfTracks(String numberOfTracks) {
		this.numberOfTracks = numberOfTracks;
	}
	/**
	 * @return the offerSummaryLowestCollectiblePrice
	 */
	public String getOfferSummaryLowestCollectiblePrice() {
		return offerSummaryLowestCollectiblePrice;
	}
	/**
	 * @param offerSummaryLowestCollectiblePrice the offerSummaryLowestCollectiblePrice to set
	 */
	public void setOfferSummaryLowestCollectiblePrice(String offerSummaryLowestCollectiblePrice) {
		this.offerSummaryLowestCollectiblePrice = offerSummaryLowestCollectiblePrice;
	}
	/**
	 * @return the offerSummaryLowestNewPrice
	 */
	public String getOfferSummaryLowestNewPrice() {
		return offerSummaryLowestNewPrice;
	}
	/**
	 * @param offerSummaryLowestNewPrice the offerSummaryLowestNewPrice to set
	 */
	public void setOfferSummaryLowestNewPrice(String offerSummaryLowestNewPrice) {
		this.offerSummaryLowestNewPrice = offerSummaryLowestNewPrice;
	}
	/**
	 * @return the offerSummaryTotalCollectible
	 */
	public String getOfferSummaryTotalCollectible() {
		return offerSummaryTotalCollectible;
	}
	/**
	 * @param offerSummaryTotalCollectible the offerSummaryTotalCollectible to set
	 */
	public void setOfferSummaryTotalCollectible(String offerSummaryTotalCollectible) {
		this.offerSummaryTotalCollectible = offerSummaryTotalCollectible;
	}
	/**
	 * @return the offerSummaryTotalNew
	 */
	public String getOfferSummaryTotalNew() {
		return offerSummaryTotalNew;
	}
	/**
	 * @param offerSummaryTotalNew the offerSummaryTotalNew to set
	 */
	public void setOfferSummaryTotalNew(String offerSummaryTotalNew) {
		this.offerSummaryTotalNew = offerSummaryTotalNew;
	}
	/**
	 * @return the offset
	 */
	public String getOffset() {
		return offset;
	}
	/**
	 * @param offset the offset to set
	 */
	public void setOffset(String offset) {
		this.offset = offset;
	}
	/**
	 * @return the operatingSystem
	 */
	public String getOperatingSystem() {
		return operatingSystem;
	}
	/**
	 * @param operatingSystem the operatingSystem to set
	 */
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	/**
	 * @return the packageDimensionsHeight
	 */
	public String getPackageDimensionsHeight() {
		return packageDimensionsHeight;
	}
	/**
	 * @param packageDimensionsHeight the packageDimensionsHeight to set
	 */
	public void setPackageDimensionsHeight(String packageDimensionsHeight) {
		this.packageDimensionsHeight = packageDimensionsHeight;
	}
	/**
	 * @return the packageDimensionsHeightUnit
	 */
	public String getPackageDimensionsHeightUnit() {
		return packageDimensionsHeightUnit;
	}
	/**
	 * @param packageDimensionsHeightUnit the packageDimensionsHeightUnit to set
	 */
	public void setPackageDimensionsHeightUnit(String packageDimensionsHeightUnit) {
		this.packageDimensionsHeightUnit = packageDimensionsHeightUnit;
	}
	/**
	 * @return the packageDimensionsLength
	 */
	public String getPackageDimensionsLength() {
		return packageDimensionsLength;
	}
	/**
	 * @param packageDimensionsLength the packageDimensionsLength to set
	 */
	public void setPackageDimensionsLength(String packageDimensionsLength) {
		this.packageDimensionsLength = packageDimensionsLength;
	}
	/**
	 * @return the packageDimensionsLengthUnit
	 */
	public String getPackageDimensionsLengthUnit() {
		return packageDimensionsLengthUnit;
	}
	/**
	 * @param packageDimensionsLengthUnit the packageDimensionsLengthUnit to set
	 */
	public void setPackageDimensionsLengthUnit(String packageDimensionsLengthUnit) {
		this.packageDimensionsLengthUnit = packageDimensionsLengthUnit;
	}
	/**
	 * @return the packageDimensionsWeight
	 */
	public String getPackageDimensionsWeight() {
		return packageDimensionsWeight;
	}
	/**
	 * @param packageDimensionsWeight the packageDimensionsWeight to set
	 */
	public void setPackageDimensionsWeight(String packageDimensionsWeight) {
		this.packageDimensionsWeight = packageDimensionsWeight;
	}
	/**
	 * @return the packageDimensionsWeightUnit
	 */
	public String getPackageDimensionsWeightUnit() {
		return packageDimensionsWeightUnit;
	}
	/**
	 * @param packageDimensionsWeightUnit the packageDimensionsWeightUnit to set
	 */
	public void setPackageDimensionsWeightUnit(String packageDimensionsWeightUnit) {
		this.packageDimensionsWeightUnit = packageDimensionsWeightUnit;
	}
	/**
	 * @return the packageDimensionsWidth
	 */
	public String getPackageDimensionsWidth() {
		return packageDimensionsWidth;
	}
	/**
	 * @param packageDimensionsWidth the packageDimensionsWidth to set
	 */
	public void setPackageDimensionsWidth(String packageDimensionsWidth) {
		this.packageDimensionsWidth = packageDimensionsWidth;
	}
	/**
	 * @return the packageDimensionsWidthUnit
	 */
	public String getPackageDimensionsWidthUnit() {
		return packageDimensionsWidthUnit;
	}
	/**
	 * @param packageDimensionsWidthUnit the packageDimensionsWidthUnit to set
	 */
	public void setPackageDimensionsWidthUnit(String packageDimensionsWidthUnit) {
		this.packageDimensionsWidthUnit = packageDimensionsWidthUnit;
	}
	/**
	 * @return the packageQuantity
	 */
	public String getPackageQuantity() {
		return packageQuantity;
	}
	/**
	 * @param packageQuantity the packageQuantity to set
	 */
	public void setPackageQuantity(String packageQuantity) {
		this.packageQuantity = packageQuantity;
	}
	/**
	 * @return the partNumber
	 */
	public String getPartNumber() {
		return partNumber;
	}
	/**
	 * @param partNumber the partNumber to set
	 */
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	/**
	 * @return the paymentMethod
	 */
	public String getPaymentMethod() {
		return paymentMethod;
	}
	/**
	 * @param paymentMethod the paymentMethod to set
	 */
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}
	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	/**
	 * @return the productBrand
	 */
	public String getProductBrand() {
		return productBrand;
	}
	/**
	 * @param productBrand the productBrand to set
	 */
	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}
	/**
	 * @return the productGroup
	 */
	public String getProductGroup() {
		return productGroup;
	}
	/**
	 * @param productGroup the productGroup to set
	 */
	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}
	/**
	 * @return the productIdType
	 */
	public String getProductIdType() {
		return productIdType;
	}
	/**
	 * @param productIdType the productIdType to set
	 */
	public void setProductIdType(String productIdType) {
		this.productIdType = productIdType;
	}
	/**
	 * @return the productIdValue
	 */
	public String getProductIdValue() {
		return productIdValue;
	}
	/**
	 * @param productIdValue the productIdValue to set
	 */
	public void setProductIdValue(String productIdValue) {
		this.productIdValue = productIdValue;
	}
	/**
	 * @return the productTypeName
	 */
	public String getProductTypeName() {
		return productTypeName;
	}
	/**
	 * @param productTypeName the productTypeName to set
	 */
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	/**
	 * @return the productTypeSubcategory
	 */
	public String getProductTypeSubcategory() {
		return productTypeSubcategory;
	}
	/**
	 * @param productTypeSubcategory the productTypeSubcategory to set
	 */
	public void setProductTypeSubcategory(String productTypeSubcategory) {
		this.productTypeSubcategory = productTypeSubcategory;
	}
	/**
	 * @return the productUrl
	 */
	public String getProductUrl() {
		return productUrl;
	}
	/**
	 * @param productUrl the productUrl to set
	 */
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
	/**
	 * @return the publicationDate
	 */
	public String getPublicationDate() {
		return publicationDate;
	}
	/**
	 * @param publicationDate the publicationDate to set
	 */
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
	/**
	 * @return the publisher
	 */
	public String getPublisher() {
		return publisher;
	}
	/**
	 * @param publisher the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	/**
	 * @return the regionCode
	 */
	public String getRegionCode() {
		return regionCode;
	}
	/**
	 * @param regionCode the regionCode to set
	 */
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
	/**
	 * @return the releaseDate
	 */
	public String getReleaseDate() {
		return releaseDate;
	}
	/**
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	/**
	 * @return the salesRank
	 */
	public double getSalesRank() {
		return salesRank;
	}
	/**
	 * @param salesRank the salesRank to set
	 */
	public void setSalesRank(double salesRank) {
		this.salesRank = salesRank;
	}
	/**
	 * @return the seikodoProductCode
	 */
	public String getSeikodoProductCode() {
		return seikodoProductCode;
	}
	/**
	 * @param seikodoProductCode the seikodoProductCode to set
	 */
	public void setSeikodoProductCode(String seikodoProductCode) {
		this.seikodoProductCode = seikodoProductCode;
	}
	/**
	 * @return the sellingPrice
	 */
	public double getSellingPrice() {
		return sellingPrice;
	}
	/**
	 * @param sellingPrice the sellingPrice to set
	 */
	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	/**
	 * @return the sellingState
	 */
	public String getSellingState() {
		return sellingState;
	}
	/**
	 * @param sellingState the sellingState to set
	 */
	public void setSellingState(String sellingState) {
		this.sellingState = sellingState;
	}
	/**
	 * @return the shippingServiceCost
	 */
	public String getShippingServiceCost() {
		return shippingServiceCost;
	}
	/**
	 * @param shippingServiceCost the shippingServiceCost to set
	 */
	public void setShippingServiceCost(String shippingServiceCost) {
		this.shippingServiceCost = shippingServiceCost;
	}
	/**
	 * @return the shippingType
	 */
	public String getShippingType() {
		return shippingType;
	}
	/**
	 * @param shippingType the shippingType to set
	 */
	public void setShippingType(String shippingType) {
		this.shippingType = shippingType;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}
	/**
	 * @return the sizeUnit
	 */
	public String getSizeUnit() {
		return sizeUnit;
	}
	/**
	 * @param sizeUnit the sizeUnit to set
	 */
	public void setSizeUnit(String sizeUnit) {
		this.sizeUnit = sizeUnit;
	}
	/**
	 * @return the sizeVariants
	 */
	public String getSizeVariants() {
		return sizeVariants;
	}
	/**
	 * @param sizeVariants the sizeVariants to set
	 */
	public void setSizeVariants(String sizeVariants) {
		this.sizeVariants = sizeVariants;
	}
	/**
	 * @return the sku
	 */
	public String getSku() {
		return sku;
	}
	/**
	 * @param sku the sku to set
	 */
	public void setSku(String sku) {
		this.sku = sku;
	}
	/**
	 * @return the studio
	 */
	public String getStudio() {
		return studio;
	}
	/**
	 * @param studio the studio to set
	 */
	public void setStudio(String studio) {
		this.studio = studio;
	}
	/**
	 * @return the styleCode
	 */
	public String getStyleCode() {
		return styleCode;
	}
	/**
	 * @param styleCode the styleCode to set
	 */
	public void setStyleCode(String styleCode) {
		this.styleCode = styleCode;
	}
	/**
	 * @return the subTitle
	 */
	public String getSubTitle() {
		return subTitle;
	}
	/**
	 * @param subTitle the subTitle to set
	 */
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	/**
	 * @return the timeLeft
	 */
	public String getTimeLeft() {
		return timeLeft;
	}
	/**
	 * @param timeLeft the timeLeft to set
	 */
	public void setTimeLeft(String timeLeft) {
		this.timeLeft = timeLeft;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the originalTitle
	 */
	public String getOriginalTitle() {
		return originalTitle;
	}
	/**
	 * @param originalTitle the originalTitle to set
	 */
	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}
	/**
	 * @return the trackSequence
	 */
	public String getTrackSequence() {
		return trackSequence;
	}
	/**
	 * @param trackSequence the trackSequence to set
	 */
	public void setTrackSequence(String trackSequence) {
		this.trackSequence = trackSequence;
	}
	/**
	 * @return the upc
	 */
	public String getUpc() {
		return upc;
	}
	/**
	 * @param upc the upc to set
	 */
	public void setUpc(String upc) {
		this.upc = upc;
	}
	/**
	 * @return the variationSummaryHighestPrice
	 */
	public String getVariationSummaryHighestPrice() {
		return variationSummaryHighestPrice;
	}
	/**
	 * @param variationSummaryHighestPrice the variationSummaryHighestPrice to set
	 */
	public void setVariationSummaryHighestPrice(String variationSummaryHighestPrice) {
		this.variationSummaryHighestPrice = variationSummaryHighestPrice;
	}
	/**
	 * @return the variationSummaryHighestSalesPrice
	 */
	public String getVariationSummaryHighestSalesPrice() {
		return variationSummaryHighestSalesPrice;
	}
	/**
	 * @param variationSummaryHighestSalesPrice the variationSummaryHighestSalesPrice to set
	 */
	public void setVariationSummaryHighestSalesPrice(String variationSummaryHighestSalesPrice) {
		this.variationSummaryHighestSalesPrice = variationSummaryHighestSalesPrice;
	}
	/**
	 * @return the variationSummaryLowestPrice
	 */
	public String getVariationSummaryLowestPrice() {
		return variationSummaryLowestPrice;
	}
	/**
	 * @param variationSummaryLowestPrice the variationSummaryLowestPrice to set
	 */
	public void setVariationSummaryLowestPrice(String variationSummaryLowestPrice) {
		this.variationSummaryLowestPrice = variationSummaryLowestPrice;
	}
	/**
	 * @return the variationSummaryLowestSalesPrice
	 */
	public String getVariationSummaryLowestSalesPrice() {
		return variationSummaryLowestSalesPrice;
	}
	/**
	 * @param variationSummaryLowestSalesPrice the variationSummaryLowestSalesPrice to set
	 */
	public void setVariationSummaryLowestSalesPrice(String variationSummaryLowestSalesPrice) {
		this.variationSummaryLowestSalesPrice = variationSummaryLowestSalesPrice;
	}
	/**
	 * @return the warranty
	 */
	public String getWarranty() {
		return warranty;
	}
	/**
	 * @param warranty the warranty to set
	 */
	public void setWarranty(String warranty) {
		this.warranty = warranty;
	}
	/**
	 * @return the weeeTaxValue
	 */
	public String getWeeeTaxValue() {
		return weeeTaxValue;
	}
	/**
	 * @param weeeTaxValue the weeeTaxValue to set
	 */
	public void setWeeeTaxValue(String weeeTaxValue) {
		this.weeeTaxValue = weeeTaxValue;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the markForDelete
	 */
	public boolean isMarkForDelete() {
		return markForDelete;
	}
	/**
	 * @param markForDelete the markForDelete to set
	 */
	public void setMarkForDelete(boolean markForDelete) {
		this.markForDelete = markForDelete;
	}
	/**
	 * @return the specificationJson
	 */
	public String getSpecificationJson() {
		return specificationJson;
	}
	/**
	 * @param specificationJson the specificationJson to set
	 */
	public void setSpecificationJson(String specificationJson) {
		this.specificationJson = specificationJson;
	}
	@Override
	public String toString() {
		return "HomeProductInfoDTO [home=" + home + ", subCategoryName=" + subCategoryName + ", id=" + id + ", merged=" + merged + ", autoPay=" + autoPay
				+ ", multiVariationListing=" + multiVariationListing + ", topRatedListing=" + topRatedListing + ", returnAccepted=" + returnAccepted
				+ ", shippingOptions=" + shippingOptions + ", offerList=" + offerList + ", imageSets=" + imageSets + ", variationAttributes="
				+ variationAttributes + ", languages=" + languages + ", codAvailable=" + codAvailable + ", emiAvailable=" + emiAvailable + ", inStock="
				+ inStock + ", markForDelete=" + markForDelete + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", offerTotalOfferPages="
				+ offerTotalOfferPages + ", offerTotalOffers=" + offerTotalOffers + ", actors=" + actors + ", artists=" + artists + ", features=" + features
				+ ", format=" + format + ", galleryInfoGalleryUrl=" + galleryInfoGalleryUrl + ", galleryPlusPictureUrl=" + galleryPlusPictureUrl
				+ ", itemLinks=" + itemLinks + ", paymentMethods=" + paymentMethods + ", pictureFormats=" + pictureFormats + ", shipToLocations="
				+ shipToLocations + ", variations=" + variations + ", accessories=" + accessories + ", alternateVersions=" + alternateVersions
				+ ", attributeMap=" + attributeMap + ", collections=" + collections + ", imageUrls=" + imageUrls + ", categoryPaths=" + categoryPaths
				+ ", offers=" + offers + ", tags=" + tags + ", aspectRatio=" + aspectRatio + ", audienceRating=" + audienceRating + ", binding=" + binding
				+ ", cashBack=" + cashBack + ", categoryId=" + categoryId + ", categoryName=" + categoryName + ", ceroAgeRating=" + ceroAgeRating
				+ ", clothingSize=" + clothingSize + ", color=" + color + ", colorVariants=" + colorVariants + ", condition=" + condition + ", country="
				+ country + ", createdBy=" + createdBy + ", department=" + department + ", description=" + description + ", discountPercentage="
				+ discountPercentage + ", ean=" + ean + ", edition=" + edition + ", episodeSequence=" + episodeSequence + ", esrbAgeRating=" + esrbAgeRating
				+ ", genre=" + genre + ", hardwarePlatform=" + hardwarePlatform + ", hazardousMaterialType=" + hazardousMaterialType + ", imageUrl=" + imageUrl
				+ ", imageUrlLarge=" + imageUrlLarge + ", imageUrlMedium=" + imageUrlMedium + ", imageUrlSmall=" + imageUrlSmall + ", imageUrlXL=" + imageUrlXL
				+ ", isbn=" + isbn + ", issuePerYear=" + issuePerYear + ", itemPartNumber=" + itemPartNumber + ", label=" + label + ", type=" + type
				+ ", legalDisclaimer=" + legalDisclaimer + ", location=" + location + ", magazineType=" + magazineType + ", manufacturer=" + manufacturer
				+ ", manufacturerMaximumAgeUnit=" + manufacturerMaximumAgeUnit + ", manufacturerMaximumAgeValue=" + manufacturerMaximumAgeValue
				+ ", manufacturerMinimumAgeUnit=" + manufacturerMinimumAgeUnit + ", manufacturerMinimumAgeValue=" + manufacturerMinimumAgeValue
				+ ", manufacturerPartsWarrantyDescription=" + manufacturerPartsWarrantyDescription + ", mediaType=" + mediaType + ", model=" + model
				+ ", modelYear=" + modelYear + ", modifiedBy=" + modifiedBy + ", mpn=" + mpn + ", mrp=" + mrp + ", numberOfDiscs=" + numberOfDiscs
				+ ", numberOfIssues=" + numberOfIssues + ", numberOfItems=" + numberOfItems + ", numberOfPages=" + numberOfPages + ", numberOfTracks="
				+ numberOfTracks + ", offerSummaryLowestCollectiblePrice=" + offerSummaryLowestCollectiblePrice + ", offerSummaryLowestNewPrice="
				+ offerSummaryLowestNewPrice + ", offerSummaryTotalCollectible=" + offerSummaryTotalCollectible + ", offerSummaryTotalNew="
				+ offerSummaryTotalNew + ", offset=" + offset + ", operatingSystem=" + operatingSystem + ", packageDimensionsHeight=" + packageDimensionsHeight
				+ ", packageDimensionsHeightUnit=" + packageDimensionsHeightUnit + ", packageDimensionsLength=" + packageDimensionsLength
				+ ", packageDimensionsLengthUnit=" + packageDimensionsLengthUnit + ", packageDimensionsWeight=" + packageDimensionsWeight
				+ ", packageDimensionsWeightUnit=" + packageDimensionsWeightUnit + ", packageDimensionsWidth=" + packageDimensionsWidth
				+ ", packageDimensionsWidthUnit=" + packageDimensionsWidthUnit + ", packageQuantity=" + packageQuantity + ", partNumber=" + partNumber
				+ ", paymentMethod=" + paymentMethod + ", postalCode=" + postalCode + ", productBrand=" + productBrand + ", productGroup=" + productGroup
				+ ", productIdType=" + productIdType + ", productIdValue=" + productIdValue + ", productTypeName=" + productTypeName
				+ ", productTypeSubcategory=" + productTypeSubcategory + ", productUrl=" + productUrl + ", publicationDate=" + publicationDate + ", publisher="
				+ publisher + ", regionCode=" + regionCode + ", releaseDate=" + releaseDate + ", salesRank=" + salesRank + ", seikodoProductCode="
				+ seikodoProductCode + ", sellingPrice=" + sellingPrice + ", sellingState=" + sellingState + ", shippingServiceCost=" + shippingServiceCost
				+ ", shippingType=" + shippingType + ", size=" + size + ", sizeUnit=" + sizeUnit + ", sizeVariants=" + sizeVariants + ", sku=" + sku
				+ ", studio=" + studio + ", styleCode=" + styleCode + ", subTitle=" + subTitle + ", timeLeft=" + timeLeft + ", title=" + title
				+ ", originalTitle=" + originalTitle + ", trackSequence=" + trackSequence + ", upc=" + upc + ", variationSummaryHighestPrice="
				+ variationSummaryHighestPrice + ", variationSummaryHighestSalesPrice=" + variationSummaryHighestSalesPrice + ", variationSummaryLowestPrice="
				+ variationSummaryLowestPrice + ", variationSummaryLowestSalesPrice=" + variationSummaryLowestSalesPrice + ", warranty=" + warranty
				+ ", weeeTaxValue=" + weeeTaxValue + ", specificationJson=" + specificationJson + "]";
	}
	
}
