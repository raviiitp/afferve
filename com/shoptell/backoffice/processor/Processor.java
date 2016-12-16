/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.processor;

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
import static com.shoptell.backoffice.enums.CategoryEnum.PENDRIVE;
import static com.shoptell.backoffice.enums.CategoryEnum.REFRIGERATOR;
import static com.shoptell.backoffice.enums.CategoryEnum.SMARTPHONES;
import static com.shoptell.backoffice.enums.CategoryEnum.SPEAKERS;
import static com.shoptell.backoffice.enums.CategoryEnum.SUNGLASSES;
import static com.shoptell.backoffice.enums.CategoryEnum.TABLET;
import static com.shoptell.backoffice.enums.CategoryEnum.TELEVISION;
import static com.shoptell.backoffice.enums.CategoryEnum.WASHINGMACHINE;
import static com.shoptell.backoffice.enums.CategoryEnum.WATCHES;
import static com.shoptell.backoffice.enums.CategoryEnum.getCategory;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.processor.impl.AcProcessor;
import com.shoptell.backoffice.processor.impl.BagsProcessor;
import com.shoptell.backoffice.processor.impl.BookProcessor;
import com.shoptell.backoffice.processor.impl.CameraProcessor;
import com.shoptell.backoffice.processor.impl.ConsoleProcessor;
import com.shoptell.backoffice.processor.impl.DesktopProcessor;
import com.shoptell.backoffice.processor.impl.EyewearProcessor;
import com.shoptell.backoffice.processor.impl.HeadphoneProcessor;
import com.shoptell.backoffice.processor.impl.LaptopProcessor;
import com.shoptell.backoffice.processor.impl.MicrowaveovenProcessor;
import com.shoptell.backoffice.processor.impl.MonitorProcessor;
import com.shoptell.backoffice.processor.impl.PendriveProcessor;
import com.shoptell.backoffice.processor.impl.RefrigeratorProcessor;
import com.shoptell.backoffice.processor.impl.SmartphoneProcessor;
import com.shoptell.backoffice.processor.impl.SpeakerProcessor;
import com.shoptell.backoffice.processor.impl.SportshoeProcessor;
import com.shoptell.backoffice.processor.impl.SunglassesProcessor;
import com.shoptell.backoffice.processor.impl.TabletProcessor;
import com.shoptell.backoffice.processor.impl.TvProcessor;
import com.shoptell.backoffice.processor.impl.WashingmachineProcessor;
import com.shoptell.backoffice.processor.impl.WatchesProcessor;
import com.shoptell.backoffice.processor.impl.WaterpurifierProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.db.messagelog.MessageLogUtil;

/**
 * @author abhishekagarwal
 *
 */

public abstract class Processor {
	@Inject
	private MessageLogUtil msgLog;
	@Inject
	public SmartphoneProcessor smartphone;
	@Inject
	public SportshoeProcessor shoe;
	@Inject
	public BookProcessor book;
	@Inject
	public LaptopProcessor laptop;
	@Inject
	public DesktopProcessor desktop;
	@Inject
	public MonitorProcessor monitor;
	@Inject
	public AcProcessor ac; 					
	@Inject
	public RefrigeratorProcessor frige; 	
	@Inject
	public TvProcessor tv; 					
	@Inject
	public WashingmachineProcessor wm; 
	@Inject
	public WaterpurifierProcessor wp;
	@Inject
	public MicrowaveovenProcessor oven;
	@Inject
	public CameraProcessor camera;
	@Inject
	public PendriveProcessor pendrive;
	@Inject
	public WatchesProcessor watches;
	@Inject
	public EyewearProcessor eyewear;
	@Inject
	public SunglassesProcessor sunglass;
	@Inject
	public TabletProcessor tablet;
	@Inject
	public BagsProcessor bags;
	@Inject
	public HeadphoneProcessor headphone;
	@Inject
	public SpeakerProcessor speaker;
	@Inject
	public ConsoleProcessor console;
//	@Inject
//	public NailpolishProcessor nailpolish;
//	@Inject
//	public ShampooProcessor shampoo;

	public ReviewedProductInfoDTO processData(HomeProductInfoDTO prod, HomeEnum home) {
		try {
			if (SMARTPHONES.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return smartphone.process(prod, home);
			}
			else if (HEADPHONES.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return headphone.process(prod, home);
			}
			else if (SPEAKERS.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return speaker.process(prod, home);
			}
			else if (GAMINGCONSOLES.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return console.process(prod, home);
			}
//			else if (NAILPOLISH.name().equalsIgnoreCase(prod.getSubCategoryName())) {
//				return nailpolish.process(prod, home);
//			}
//			else if (SHAMPOOS.name().equalsIgnoreCase(prod.getSubCategoryName())) {
//				return shampoo.process(prod, home);
//			}
			else if (LAPTOPS.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return laptop.process(prod, home);
			}
			else if (DESKTOPS.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return desktop.process(prod, home);
			}
			else if (WASHINGMACHINE.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return wm.process(prod, home);
			}
			else if (TELEVISION.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return tv.process(prod, home);
			}
			else if (REFRIGERATOR.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return frige.process(prod, home);
			}
			else if (AIRCONDITIONER.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return ac.process(prod, home);
			}
			else if (MICROWAVEOVEN.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return oven.process(prod, home);
			}
			else if (CAMERA.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return camera.process(prod, home);
			}
			else if (WATCHES.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return watches.process(prod, home);
			}
			else if (EYEWEARS.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return eyewear.process(prod, home);
			}
			else if (SUNGLASSES.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return sunglass.process(prod, home);
			}
			else if (TABLET.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return tablet.process(prod, home);
			}
			else if (PENDRIVE.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return pendrive.process(prod, home);
			}
			else if (BAGS.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return bags.process(prod, home);
			}
			else if (LUGGAGEBAGS.name().equalsIgnoreCase(prod.getSubCategoryName())) {
				return bags.process(prod, home);
			}
		} catch (Exception e) {
			if (msgLog != null)
				msgLog.addError(e);
		}
		return null;
	}

	public String getSubCategory(String category) {
		if (StringUtils.isNotBlank(category)) {
			CategoryEnum subCategory = getCategory(category);
			if (subCategory != null) {
				return subCategory.name();
			}
		}
		return null;
	}
}
