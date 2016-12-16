/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.snapdeal;

import static com.shoptell.backoffice.enums.CategoryEnum.AIRCONDITIONER;
import static com.shoptell.backoffice.enums.CategoryEnum.BAGS;
import static com.shoptell.backoffice.enums.CategoryEnum.CAMERA;
import static com.shoptell.backoffice.enums.CategoryEnum.DESKTOPS;
import static com.shoptell.backoffice.enums.CategoryEnum.EYEWEARS;
import static com.shoptell.backoffice.enums.CategoryEnum.GAMINGCONSOLES;
import static com.shoptell.backoffice.enums.CategoryEnum.HEADPHONES;
import static com.shoptell.backoffice.enums.CategoryEnum.LAPTOPS;
import static com.shoptell.backoffice.enums.CategoryEnum.LUGGAGEBAGS;
import static com.shoptell.backoffice.enums.CategoryEnum.MICROWAVEOVEN;
import static com.shoptell.backoffice.enums.CategoryEnum.REFRIGERATOR;
import static com.shoptell.backoffice.enums.CategoryEnum.SMARTPHONES;
import static com.shoptell.backoffice.enums.CategoryEnum.SPEAKERS;
import static com.shoptell.backoffice.enums.CategoryEnum.SUNGLASSES;
import static com.shoptell.backoffice.enums.CategoryEnum.TABLET;
import static com.shoptell.backoffice.enums.CategoryEnum.TELEVISION;
import static com.shoptell.backoffice.enums.CategoryEnum.WASHINGMACHINE;
import static com.shoptell.backoffice.enums.CategoryEnum.WATCHES;

import org.apache.commons.lang.StringUtils;

import com.shoptell.backoffice.enums.CategoryEnum;

public enum SnapdealCategoryEnum {
	// add new categories here
	Gaming (GAMINGCONSOLES),
	Bags_Luggage (BAGS, LUGGAGEBAGS),
	Handbags_Clutches (BAGS, LUGGAGEBAGS),
	Mobiles_Tablets (SMARTPHONES, TABLET),
	TVs_Audio_Video (TELEVISION, HEADPHONES, SPEAKERS),
	Eyewear (EYEWEARS, SUNGLASSES),
	Watches (WATCHES),
	Cameras_Accessories (CAMERA),
	Computers_Peripherals (LAPTOPS, DESKTOPS),
	Appliances (AIRCONDITIONER, REFRIGERATOR, WASHINGMACHINE, MICROWAVEOVEN),
	Beauty_Personal_Care,
	Real_Estate,
	Jewellery,
	Furniture,
	Tweens_Boys,
	Automobiles,
	Home_Improvement,
	Womens_Accessories,
	The_Designer_Studio,
	Kitchen_Appliances,
	Fashion_Jewellery,
	Office_Equipment,
	Kids_Decor,
	Hobbies,
	Mens_Clothing,
	Automotive,
	Womens_Ethnic_Wear,
	Infant_Wear,
	Boys_Clothing_2_8_Yrs,
	Gourmet,
	Refurbished_Products,
	Books,
	Girls_Clothing_2_8_Yrs,
	Nutrition_Supplements,
	Mens_Footwear,
	Stationery,
	Click_and_Collect,
	Household_Essentials,
	Online_Education,
	Chocolates_Snacks,
	Home_Furnishing,
	TV_Shop,
	Girls_Clothing_8_14_Yrs,
	Home_Services,
	Fusion_Wear,
	Kids_Eyewear,
	Services,
	Musical_Instruments,
	Hardware_Sanitary_Fittings,
	Kitchenware,
	Precious_Jewellery,
	Home_Decoratives,
	Womens_Footwear,
	Sports_Fitness,
	Gifting_Events,
	Health_Wellness_Medicine,
	Digital_Entertainment,
	Toys_Games,
	Kids_Footwear,
	Baby_Care,
	Fashion_Accessories,
	World_Food__Indian_Food,
	Womens_Clothing,
	Movies_Music,
	Fragrances,
	Snapdeal_Select;
	
	private CategoryEnum[] subCategory;

	private SnapdealCategoryEnum(CategoryEnum... categoryEnums) {
		if (categoryEnums != null && categoryEnums.length > 0) {
			this.setSubCategory(categoryEnums);
		}
	}

	public static SnapdealCategoryEnum getCategory(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		for (SnapdealCategoryEnum tmp : SnapdealCategoryEnum.values()) {
			if (tmp.name().equalsIgnoreCase(name)) {
				return tmp;
			}
		}
		return null;
	}

	/**
	 * @return the subCategory
	 */
	public CategoryEnum[] getSubCategory() {
		return subCategory;
	}

	/**
	 * @param subCategory
	 *            the subCategory to set
	 */
	public void setSubCategory(CategoryEnum[] subCategory) {
		this.subCategory = subCategory;
	}
}
