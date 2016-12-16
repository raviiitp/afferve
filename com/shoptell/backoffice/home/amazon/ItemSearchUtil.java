/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.amazon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AWSECommerce.Accessories.Accessory;
import AWSECommerce.BrowseNode;
import AWSECommerce.Collections.Collection;
import AWSECommerce.Collections.Collection.CollectionItem;
import AWSECommerce.Image;
import AWSECommerce.ImageSet;
import AWSECommerce.Item;
import AWSECommerce.Item.AlternateVersions.AlternateVersion;
import AWSECommerce.Item.ImageSets;
import AWSECommerce.ItemAttributes;
import AWSECommerce.ItemAttributes.Languages.Language;
import AWSECommerce.ItemLink;
import AWSECommerce.Offer;
import AWSECommerce.OfferListing;
import AWSECommerce.OfferSummary;
import AWSECommerce.Offers;
import AWSECommerce.Price;
import AWSECommerce.Promotion;
import AWSECommerce.Promotion.Summary;
import AWSECommerce.VariationAttribute;
import AWSECommerce.VariationSummary;

import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ProductOfferDTO;
import com.shoptell.backoffice.repository.util.HomeProductInfoUtil;

@Named
public class ItemSearchUtil {
	
	private static final Logger log = LoggerFactory.getLogger(ItemSearchUtil.class);

	@Inject
	private HomeProductInfoUtil homeProductInfoUtil;
	
	public double formatPrice(String str){
		if (StringUtils.isNotBlank(str)){
			str = str.replaceAll("[^0-9?!\\.]","");
		}
		return Double.parseDouble(str);
	}
	
	public HomeProductInfoDTO processItem(Item responsedItem, String parentName, boolean isUpdate, String categoryName){
		
		HomeProductInfoDTO productInfo = new HomeProductInfoDTO();
		
		String sr = responsedItem.getSalesRank();
		
		if (StringUtils.isNotBlank(sr) && NumberUtils.isNumber(sr)){
			productInfo.setSalesRank(Long.parseLong(sr));
		}
		
		productInfo.setHome(HomeEnum.AMAZON.name());
		
		if (StringUtils.isNotBlank(parentName)){
			productInfo.setCategoryPaths(new HashSet<String>());
			productInfo.getCategoryPaths().add(parentName);
		}
		
		// Accessories
		if(responsedItem.getAccessories() != null){
			Map<String, String> __accessories_asMap = null;
			List<Accessory> accessoryList = responsedItem.getAccessories().getAccessory();
			if(accessoryList.size() > 0){
				__accessories_asMap = new HashMap<String, String>();
			}
			for(Accessory accessory: accessoryList){
				__accessories_asMap.put(accessory.getASIN(), accessory.getTitle());
			}
			productInfo.setAccessories(__accessories_asMap);
		}
		
		// Asin
		productInfo.setId(responsedItem.getASIN());

		// AlternateVersions
		productInfo.setAlternateVersions(null);
		if(responsedItem.getAlternateVersions() != null){
			HashMap<String, String> __alternateVersion_asMap = null;
			List<AlternateVersion> alternateVersionList = responsedItem.getAlternateVersions().getAlternateVersion();
			if(alternateVersionList.size() > 0){
				__alternateVersion_asMap = new HashMap<String, String>();
			}
			for(AlternateVersion alternateVersion: alternateVersionList){
				__alternateVersion_asMap.put(alternateVersion.getASIN(), alternateVersion.getTitle());
			}
			productInfo.setAlternateVersions(__alternateVersion_asMap);
		}
		
		// BrowseNode
		if(responsedItem.getBrowseNodes() != null){
			List<BrowseNode> browseNodes = responsedItem.getBrowseNodes().getBrowseNode();
			productInfo.setCategoryId(browseNodes.get(0).getBrowseNodeId());
			productInfo.setCategoryName(browseNodes.get(0).getName());
			productInfo.setSubCategoryName(browseNodes.get(0).getName());
		}

		// Collections
		if(responsedItem.getCollections() != null){
			Map<String, String> __collectionItems_asMap = null;
			List<Collection> collections = responsedItem.getCollections().getCollection();
			for(Collection collection: collections){
				List<CollectionItem> collectionItems = collection.getCollectionItem();
				if(__collectionItems_asMap == null && collectionItems.size() > 0){
					__collectionItems_asMap = new HashMap<String, String>();
				}
				for(CollectionItem collectionItem : collectionItems){
					__collectionItems_asMap.put(collectionItem.getASIN(), collectionItem.getTitle());
				}
			}
			productInfo.setCollections(__collectionItems_asMap);
		}

		// DetailPageUrl
		productInfo.setProductUrl(responsedItem.getDetailPageURL());

		if(responsedItem.getErrors() != null){
			List<AWSECommerce.Errors.Error> errors = responsedItem.getErrors().getError();
			for(AWSECommerce.Errors.Error error: errors){
				log.error("AMAZON SEARCH API", error);
			}
		}
		
		// ImageSets
		if(responsedItem.getImageSets() != null){
			Map<String, Map<String, String>> __imageSets_asMapofMap = null;
			List<ImageSets> imageSetsList = responsedItem.getImageSets();
			if(imageSetsList.size() > 0){
				__imageSets_asMapofMap = new HashMap<String, Map<String, String>>();
			}
			for(ImageSets imageSets: imageSetsList){
				for( ImageSet imageSet : imageSets.getImageSet()){
					Map<String, String> __tmpImage_asMap = new HashMap<String, String>();
					
					Image img;
					if((img = imageSet.getLargeImage()) != null){
						__tmpImage_asMap.put("LARGE", img.getURL());
					}
					if((img = imageSet.getMediumImage()) != null){
						__tmpImage_asMap.put("MEDIUM",  img.getURL());
					}
					if((img = imageSet.getSmallImage()) != null){
						__tmpImage_asMap.put("SMALL",  img.getURL());
					}
					if((img = imageSet.getSwatchImage()) != null){
						__tmpImage_asMap.put("SWATCH",  img.getURL());
					}
					if((img = imageSet.getThumbnailImage()) != null){
						__tmpImage_asMap.put("THUMBNAIL",  img.getURL());
					}
					if((img = imageSet.getTinyImage()) != null){
						__tmpImage_asMap.put("TINY",  img.getURL());
					}
					__imageSets_asMapofMap.put(imageSet.getCategory(), __tmpImage_asMap);
				}
			}
			productInfo.setImageSets(__imageSets_asMapofMap);
		}
		
		// ItemLinks
		if(responsedItem.getItemLinks() != null){
			List<ItemLink> itemLinks = responsedItem.getItemLinks().getItemLink();
			List<String> __itemLink_asList = null;
			if(itemLinks.size() > 0){
				__itemLink_asList = new ArrayList<String>();
			}
			for(ItemLink itemLink: itemLinks){
				__itemLink_asList.add(itemLink.getURL());
			}
			productInfo.setItemLinks(__itemLink_asList);
		}
		
		// Image
		Image image;
		if((image = responsedItem.getLargeImage()) != null){
			productInfo.setImageUrlLarge(image.getURL());
		}
		if((image = responsedItem.getMediumImage()) != null){
			productInfo.setImageUrlMedium(image.getURL());
			//Image Url
			productInfo.setImageUrl(image.getURL());
		}
		if((image = responsedItem.getSmallImage()) != null){
			productInfo.setImageUrlSmall(image.getURL());
		}
		
		// Offer
		if(responsedItem.getOffers() != null){
			List<ProductOfferDTO> __productOffer_asList = null;
			Offers offers = responsedItem.getOffers();
			List<Offer> offerList = offers.getOffer();
			if(offerList.size() > 0){
				__productOffer_asList = new ArrayList<ProductOfferDTO>();
			}
			for(Offer offer : offerList){
				ProductOfferDTO __tmpOffer = new ProductOfferDTO();
				if(offer.getLoyaltyPoints() != null){
					if(offer.getLoyaltyPoints().getPoints() != null){
						__tmpOffer.setLoyaltyPoints(offer.getLoyaltyPoints().getPoints().toString());
					}
				}
				if(offer.getMerchant() != null){
					__tmpOffer.setMerchantName(offer.getMerchant().getName());
				}
				if(offer.getOfferAttributes() != null){
					__tmpOffer.setCondition(offer.getOfferAttributes().getCondition());
				}
				// OfferListing
				if(offer.getOfferListing() != null){
					List<String> __tmpPrice = new ArrayList<String>();
					List<String> __tmpSalePrice = new ArrayList<String>();
					List<String> __tmpAmountSaved = new ArrayList<String>();
					List<String> __tmpPercentageSaved = new ArrayList<String>();
					for( OfferListing offerListing : offer.getOfferListing()){
						if(offerListing.getAmountSaved() != null){
							__tmpAmountSaved.add(offerListing.getAmountSaved().getFormattedPrice());
						}
						if(offerListing.getPercentageSaved() != null){
							__tmpPercentageSaved.add(offerListing.getPercentageSaved().toString());
						}
						if(offerListing.getPrice() != null){
							__tmpPrice.add(offerListing.getPrice().getFormattedPrice());
							if (productInfo.getMrp() == 0){
								productInfo.setMrp(formatPrice(offerListing.getPrice().getFormattedPrice()));
							}
						}
						if(offerListing.getSalePrice() != null){
							__tmpSalePrice.add(offerListing.getSalePrice().getFormattedPrice());
							if (productInfo.getSellingPrice() == 0){
								productInfo.setSellingPrice(formatPrice(offerListing.getSalePrice().getFormattedPrice()));
							}
						}
					}
					__tmpOffer.setPrice(__tmpPrice);
					__tmpOffer.setSalePrice(__tmpSalePrice);
					__tmpOffer.setAmountSaved(__tmpAmountSaved);
					__tmpOffer.setPercentageSaved(__tmpPercentageSaved);
				}
				// Promotions
				if(offer.getPromotions() != null){
					if(offer.getPromotions().getPromotion() != null){
						List<String> __promotionSummaryBenefitDesc = new ArrayList<String>();
						List<String> __promotionSummaryCategory = new ArrayList<String>();
						List<String> __promotionSummaryStartData = new ArrayList<String>();
						List<String> __promotionSummaryEndData = new ArrayList<String>();
						List<String> __promotionSummaryPromotionId = new ArrayList<String>();
						List<String> __promotionSummaryEligibilityReqDesc = new ArrayList<String>();
						List<String> __promotionSummaryTermsAndConditions = new ArrayList<String>();
						for(Promotion promotion : offer.getPromotions().getPromotion()){
							if(promotion.getSummary() != null){
								Summary promotionSummary = promotion.getSummary();
								__promotionSummaryBenefitDesc.add(promotionSummary.getBenefitDescription());
								__promotionSummaryCategory.add(promotionSummary.getCategory());
								__promotionSummaryEligibilityReqDesc.add(promotionSummary.getEligibilityRequirementDescription());
								__promotionSummaryStartData.add(promotionSummary.getStartDate());
								__promotionSummaryEndData.add(promotionSummary.getEndDate());
								__promotionSummaryPromotionId.add(promotionSummary.getPromotionId());
								__promotionSummaryTermsAndConditions.add(promotionSummary.getTermsAndConditions());
							}
						}
						__tmpOffer.setPromotionSummaryBenefitDesc(__promotionSummaryBenefitDesc);
						__tmpOffer.setPromotionSummaryCategory(__promotionSummaryCategory);
						__tmpOffer.setPromotionSummaryEligibilityReqDesc(__promotionSummaryEligibilityReqDesc);
						__tmpOffer.setPromotionSummaryStartData(__promotionSummaryStartData);
						__tmpOffer.setPromotionSummaryEndData(__promotionSummaryEndData);
						__tmpOffer.setPromotionSummaryPromotionId(__promotionSummaryPromotionId);
						__tmpOffer.setPromotionSummaryTermsAndConditions(__promotionSummaryTermsAndConditions);
					}
				}
				__productOffer_asList.add(__tmpOffer);
			}
			if(offers.getTotalOffers() != null){
				productInfo.setOfferTotalOffers(offers.getTotalOffers().intValue());
			}
			if(offers.getTotalOfferPages() != null){
				productInfo.setOfferTotalOfferPages(offers.getTotalOfferPages().intValue());
			}
			productInfo.setOfferList(__productOffer_asList);
		}
		
		// OfferSummary
		if(responsedItem.getOfferSummary() != null){
			OfferSummary offerSummary = responsedItem.getOfferSummary();
			productInfo.setOfferSummaryTotalCollectible(offerSummary.getTotalCollectible());
			if(offerSummary.getLowestCollectiblePrice() != null){
				productInfo.setOfferSummaryLowestCollectiblePrice(offerSummary.getLowestCollectiblePrice().getFormattedPrice());
			}
			productInfo.setOfferSummaryTotalNew(offerSummary.getTotalNew());
			if(offerSummary.getLowestNewPrice() != null){
				productInfo.setOfferSummaryLowestNewPrice(offerSummary.getLowestNewPrice().getFormattedPrice());
				double price = formatPrice(offerSummary.getLowestNewPrice().getFormattedPrice());
				if (price > 0){
					//minimum selling Price
					productInfo.setSellingPrice(price);
				}
			}
		}
		
		// Variants
		if(responsedItem.getVariations() != null){
			List<Item> variations = responsedItem.getVariations().getItem();
			List<String> __variation_asList = null;
			if(variations.size() > 0){
				__variation_asList = new ArrayList<String>();
			}
			for(Item __tmp_item: variations){
				__variation_asList.add(__tmp_item.getASIN());
			}
			productInfo.setVariations(__variation_asList);
		}
		
		// VariationAttributes
		if(responsedItem.getVariationAttributes() != null){
			List<VariationAttribute> variationAttrList = responsedItem.getVariationAttributes().getVariationAttribute();
			Map<String, List<String>> __variationAttr_asMapofList = null;
			if(variationAttrList.size() > 0){
				__variationAttr_asMapofList = new HashMap<String, List<String>>();
			}
			for(VariationAttribute variationAttr: variationAttrList){
				List<String> __tmpvariationAttrValue = null;
				if(variationAttr.getValue().size() > 0){
					__tmpvariationAttrValue = new ArrayList<String>();
				}
				for(String variationAttrValue : variationAttr.getValue()){
					__tmpvariationAttrValue.add(variationAttrValue);
				}
				__variationAttr_asMapofList.put(variationAttr.getName(), __tmpvariationAttrValue);
			}
			productInfo.setVariationAttributes(__variationAttr_asMapofList);
		}
		
		// VariationSummary
		if(responsedItem.getVariationSummary() != null){
			VariationSummary variationSummary = responsedItem.getVariationSummary();
			Price price;
			if((price = variationSummary.getHighestPrice()) != null){
				productInfo.setVariationSummaryHighestPrice(price.getFormattedPrice());
			}
			if((price = variationSummary.getLowestPrice()) != null){
				productInfo.setVariationSummaryLowestPrice(price.getFormattedPrice());
			}
			if((price = variationSummary.getHighestSalePrice()) != null){
				productInfo.setVariationSummaryHighestSalesPrice(price.getFormattedPrice());
			}
			if((price = variationSummary.getLowestSalePrice()) != null){
				productInfo.setVariationSummaryLowestSalesPrice(price.getFormattedPrice());
			}
		}
		
		
		// ItemAttribute
		if(responsedItem.getItemAttributes() != null){
			ItemAttributes itemAttributes = responsedItem.getItemAttributes();
			
			// Actors
			if(itemAttributes.getActor() != null){
				productInfo.setActors(itemAttributes.getActor());
			}
			
			// Artists
			if(itemAttributes.getArtist() != null){
				productInfo.setActors(itemAttributes.getArtist());
			}

			productInfo.setAspectRatio(itemAttributes.getAspectRatio());

			productInfo.setAudienceRating(itemAttributes.getAudienceRating());

			productInfo.setBinding(itemAttributes.getBinding());

			productInfo.setProductBrand(itemAttributes.getBrand());

			productInfo.setCeroAgeRating(itemAttributes.getCEROAgeRating());

			productInfo.setClothingSize(itemAttributes.getClothingSize());

			if(itemAttributes.getColor() != null){
				productInfo.setColor(itemAttributes.getColor().toUpperCase());
			}

			productInfo.setDepartment(itemAttributes.getDepartment());

			productInfo.setEan(itemAttributes.getEAN());

			productInfo.setEdition(itemAttributes.getEdition());

			productInfo.setEpisodeSequence(itemAttributes.getEpisodeSequence());

			productInfo.setEsrbAgeRating(itemAttributes.getESRBAgeRating());

			// Features
			if(itemAttributes.getFeature() != null){
				List<String> tmp = itemAttributes.getFeature();
				BackofficeUtil.listUpperCase(tmp);
				productInfo.setFeatures(tmp);
			}
			
			// Formats
			if(itemAttributes.getFormat() != null){
				productInfo.setFormat(itemAttributes.getFormat());
			}
			
			productInfo.setGenre(itemAttributes.getGenre());

			productInfo.setHardwarePlatform(itemAttributes.getHardwarePlatform());

			productInfo.setHazardousMaterialType(itemAttributes.getHazardousMaterialType());

			productInfo.setIsbn(itemAttributes.getISBN());

			productInfo.setIssuePerYear(itemAttributes.getIssuesPerYear());

			productInfo.setItemPartNumber(itemAttributes.getItemPartNumber());

			productInfo.setLabel(itemAttributes.getLabel());

			// Languages
			if(itemAttributes.getLanguages() != null && itemAttributes.getLanguages().getLanguage() != null){
				List<Map<String, String>> __languages_asListofMap = null;
				if(itemAttributes.getLanguages().getLanguage().size() > 0){
					__languages_asListofMap = new ArrayList<Map<String, String>>();
				}
				for(Language language : itemAttributes.getLanguages().getLanguage()){
					Map<String, String> __tmpMapofLanguageFields = new HashMap<String, String>();
					if(language.getName() != null){
						__tmpMapofLanguageFields.put("name", language.getName());
					}
					if(language.getAudioFormat() != null){
						__tmpMapofLanguageFields.put("audioformat", language.getAudioFormat());
					}
					if(language.getType() != null){
						__tmpMapofLanguageFields.put("type", language.getType());
					}
					__languages_asListofMap.add(__tmpMapofLanguageFields);
				}
				productInfo.setLanguages(__languages_asListofMap);
			}
			
			productInfo.setLegalDisclaimer(itemAttributes.getLegalDisclaimer());
			
			// ListPrice
			if(itemAttributes.getListPrice() != null){
				Price price = itemAttributes.getListPrice();
				productInfo.setMrp(formatPrice(price.getFormattedPrice()));
			}
			
			productInfo.setMagazineType(itemAttributes.getMagazineType());

			productInfo.setManufacturer(itemAttributes.getManufacturer());

			productInfo.setManufacturerPartsWarrantyDescription(itemAttributes.getManufacturerPartsWarrantyDescription());

			productInfo.setMediaType(itemAttributes.getMediaType());

			productInfo.setModel(itemAttributes.getModel());

			// ModelYear
			if(itemAttributes.getModelYear() != null){
				productInfo.setModelYear(itemAttributes.getModelYear().toString());
			}
			
			productInfo.setMpn(itemAttributes.getMPN());

			if(itemAttributes.getManufacturerMaximumAge()  != null){
				productInfo.setManufacturerMaximumAgeUnit(itemAttributes.getManufacturerMaximumAge().getUnits());
				productInfo.setManufacturerMaximumAgeValue(itemAttributes.getManufacturerMaximumAge().getValue().toString());
			}

			if(itemAttributes.getManufacturerMinimumAge()  != null){
				productInfo.setManufacturerMinimumAgeUnit(itemAttributes.getManufacturerMinimumAge().getUnits());
				productInfo.setManufacturerMinimumAgeValue(itemAttributes.getManufacturerMinimumAge().getValue().toString());
			}

			if(itemAttributes.getNumberOfDiscs() != null){
				productInfo.setNumberOfDiscs(itemAttributes.getNumberOfDiscs().toString());
			}
			
			if(itemAttributes.getNumberOfIssues() != null){
				productInfo.setNumberOfIssues(itemAttributes.getNumberOfIssues().toString());
			}
			
			if(itemAttributes.getNumberOfItems() != null){
				productInfo.setNumberOfItems(itemAttributes.getNumberOfItems().toString());
			}
			
			if(itemAttributes.getNumberOfTracks() != null){
				productInfo.setNumberOfTracks(itemAttributes.getNumberOfTracks().toString());
			}
			
			if(itemAttributes.getNumberOfPages() != null){
				productInfo.setNumberOfPages(itemAttributes.getNumberOfPages().toString());
			}

			productInfo.setOperatingSystem(itemAttributes.getOperatingSystem());

			productInfo.setPartNumber(itemAttributes.getPartNumber());

			productInfo.setProductGroup(itemAttributes.getProductGroup());

			productInfo.setProductTypeName(itemAttributes.getProductTypeName());

			productInfo.setProductTypeSubcategory(itemAttributes.getProductTypeSubcategory());

			productInfo.setPublicationDate(itemAttributes.getPublicationDate());
			

			productInfo.setPublisher(itemAttributes.getPublisher());

			if(itemAttributes.getPackageQuantity() != null){
				productInfo.setPackageQuantity(itemAttributes.getPackageQuantity().toString());
			}
			
			// PakageDimensions
			if (itemAttributes.getPackageDimensions() != null) {
				if (itemAttributes.getPackageDimensions().getHeight() != null) {
					productInfo.setPackageDimensionsHeight(itemAttributes.getPackageDimensions().getHeight().getValue().toString());
					productInfo.setPackageDimensionsHeightUnit(itemAttributes.getPackageDimensions().getHeight().getUnits());
				}
				if (itemAttributes.getPackageDimensions().getLength() != null) {
					productInfo.setPackageDimensionsLength(itemAttributes.getPackageDimensions().getLength().getValue().toString());
					productInfo.setPackageDimensionsLengthUnit(itemAttributes.getPackageDimensions().getLength().getUnits());
				}
				if (itemAttributes.getPackageDimensions().getWidth() != null) {
					productInfo.setPackageDimensionsWidth(itemAttributes.getPackageDimensions().getWidth().getValue().toString());
					productInfo.setPackageDimensionsWidthUnit(itemAttributes.getPackageDimensions().getWidth().getUnits());
				}
				if (itemAttributes.getPackageDimensions().getWeight() != null) {
					productInfo.setPackageDimensionsWeight(itemAttributes.getPackageDimensions().getWeight().getValue().toString());
					productInfo.setPackageDimensionsWeightUnit(itemAttributes.getPackageDimensions().getWeight().getUnits());
				}
			}
			
			// PictureFormat
			if(itemAttributes.getPictureFormat()  != null){
				productInfo.setPictureFormats(itemAttributes.getPictureFormat());
			}

			productInfo.setRegionCode(itemAttributes.getRegionCode());

			productInfo.setReleaseDate(itemAttributes.getReleaseDate());

			productInfo.setSeikodoProductCode(itemAttributes.getSeikodoProductCode());

			productInfo.setSize(itemAttributes.getSize());
			
			if(StringUtils.isNotBlank(productInfo.getSize())){
				productInfo.setSize(productInfo.getSize().toUpperCase());
			}

			productInfo.setSku(itemAttributes.getSKU());
			
			productInfo.setStudio(itemAttributes.getStudio());

			productInfo.setTitle(itemAttributes.getTitle());
			
			if(StringUtils.isNotBlank(productInfo.getTitle())){
				productInfo.setTitle(productInfo.getTitle().toUpperCase());
			}
			
			productInfo.setOriginalTitle(productInfo.getTitle());

			productInfo.setTrackSequence(itemAttributes.getTrackSequence());

			productInfo.setUpc(itemAttributes.getUPC());

			productInfo.setWarranty(itemAttributes.getWarranty());
			
			if(itemAttributes.getWEEETaxValue() != null){
					productInfo.setWeeeTaxValue(itemAttributes.getWEEETaxValue().getFormattedPrice());
			}
		}
		if (StringUtils.isNotBlank(productInfo.getCategoryName())){
			productInfo.setType(productInfo.getCategoryName().toUpperCase());
		}
		productInfo.setSubCategoryName(getSubCategory(productInfo.getCategoryName(), categoryName));
		
		productInfo.setTags(homeProductInfoUtil.getTag_asSet(productInfo.getTitle(), productInfo.getColor(), productInfo.getProductBrand()));
		
		if (StringUtils.isBlank(productInfo.getSubCategoryName())) {
			return null;
		}
		
		if (productInfo.getSellingPrice() > 0){
			productInfo.setInStock(true);
		}
		
		String title = productInfo.getTitle();
		if (!isUpdate && StringUtils.isNotBlank(title) && title.contains("REFURBISHED")){
			productInfo = null;
		}
		return productInfo;
	}

	public String getSubCategory(String category, String categoryName) {
	//	System.out.println(category);
		if (StringUtils.isNotBlank(category)) {
			CategoryEnum subCategory = CategoryEnum.getCategory(category);
			if (subCategory != null) {
				return subCategory.name();
			}
		}
		if (StringUtils.isNotBlank(categoryName)){
			CategoryEnum subCategory = CategoryEnum.getCategory(categoryName);
			if (subCategory != null) {
				return subCategory.name();
			}
		}
		return null;
	}
}

