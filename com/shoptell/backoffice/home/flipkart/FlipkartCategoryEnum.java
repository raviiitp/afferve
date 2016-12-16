/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.flipkart;

import org.apache.commons.lang.StringUtils;

import com.shoptell.backoffice.enums.CategoryEnum;

public enum FlipkartCategoryEnum {
	// add new categories here
	gaming(CategoryEnum.GAMINGCONSOLES),
	luggage_travel(CategoryEnum.LUGGAGEBAGS),
	bags_wallets_belts(CategoryEnum.BAGS),
	televisions(CategoryEnum.TELEVISION),
	mobiles(CategoryEnum.SMARTPHONES),
	watches(CategoryEnum.WATCHES),
	sunglasses(CategoryEnum.SUNGLASSES),
	eyewear(CategoryEnum.EYEWEARS),
	air_conditioners(CategoryEnum.AIRCONDITIONER),
	tablets(CategoryEnum.TABLET),
	refrigerator(CategoryEnum.REFRIGERATOR),
	cameras(CategoryEnum.CAMERA),
	microwave_ovens(CategoryEnum.MICROWAVEOVEN),
	laptops(CategoryEnum.LAPTOPS),
	washing_machine(CategoryEnum.WASHINGMACHINE),
	desktops(CategoryEnum.DESKTOPS),
	computer_storage(CategoryEnum.PENDRIVE),
	mobile_accessories(CategoryEnum.HEADPHONES, CategoryEnum.SPEAKERS),
	food_nutrition,
	landline_phones,
	tv_video_accessories,
	software,
	fragrances,
	network_components,
	e_learning,
	video_players,
	mens_clothing,
	music_movies_posters,
	furniture,
	kids_clothing,
	kids_footwear,
	pet_supplies,
	mens_footwear,
	air_coolers,
	home_entertainment,
	computer_components,
	laptop_accessories,
	womens_clothing,
	camera_accessories,
	automotive,
	home_improvement_tools,
	computer_peripherals,
	stationery_office_supplies,
	sports_fitness,
	baby_care,
	wearable_smart_devices,
	audio_players,
	grooming_beauty_wellness,
	tablet_accessories,
	kitchen_appliances,
	toys,
	home_appliances,
	home_decor_and_festive_needs,
	home_and_kitchen_needs,
	jewellery,
	home_furnishing,
	womens_footwear,
	household_supplies;
	
	private CategoryEnum[] subCategory;

	private FlipkartCategoryEnum(CategoryEnum... categoryEnums) {
		if (categoryEnums != null && categoryEnums.length > 0) {
			this.setSubCategory(categoryEnums);
		}
	}

	public static FlipkartCategoryEnum getCategory(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		for (FlipkartCategoryEnum tmp : FlipkartCategoryEnum.values()) {
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
