/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.enums;

import static com.shoptell.backoffice.enums.ThirdpartyEnum.AUTOMATIC;
import static com.shoptell.backoffice.enums.ThirdpartyEnum.CUELINKS;
import static com.shoptell.backoffice.enums.ThirdpartyEnum.PAYOOM;
import static com.shoptell.backoffice.enums.ThirdpartyEnum.VCOMMISSION;

import org.apache.commons.lang.StringUtils;

/**
 * @author abhishekagarwal
 *
 */
public enum HomeEnum {
	//Automatic
	AMAZON(3, "amazon.in", "12"),
	FLIPKART(1, "flipkart.com", "15"),
	SNAPDEAL(2, "snapdeal.com", "15"),
	EXAMPER(4, "examper.com", "15"),
	
	//Payoom
	AMERICANSWAN("americanswan.com", "28","20"),
	ARCHIES("archiesonline.com", "241","7"),
	BOOKMYFLOWERS("bookmyflowers.com", "110","12"),
	CANDERE("candere.com", "442","10"),
	CROMA("cromaretail.com", "540","1"),
	EBAY("ebay.in", "373","8.4"),
	FABINDIA("fabindia.com", "518","7"),
	FLABERRY("flaberry.com", "78","15"),
	FNP("fnp.com", "52","13"),
	HEALTHGENIE("healthgenie.in", "604","7"),
	HEALTHKART("healthkart.com", "578","3"),
	INDIATIMES("shopping.indiatimes.com", "381","10.5"),
	JABONG("jabong.com", "62","8"),
	JOCKEY("jockeyindia.com", "832","10"),
	MCD("mcdelivery.co.in", "512","14",1),
	NETMEDS("netmeds.com", "766","25"),
	NYKAA("nykaa.com",  "421","7"),
	PURPLLE("purplle.com", "522","3.5"),
	PEPPERFRY("pepperfry.com", "86","8.5"),
	RANGRITI("rangriti.com", "662","18"),
	SHOPCLUES("shopclues.com", "646", "7"),
	SHOPNINETEEN("shopnineteen.com", "630","11"),
	TRENDIN("trendin.com", "30","7"),
	YEPME("yepme.com", "628","7"),
	ZIVAME("zivame.com", "116","20"),
	
	//Payoom In Rs.
	ASKMEBAZAAR("askmebazaar.com", "570","45",1),
	ASKMEGROCERY("askmegrocery.com", "806","70",1),
	BABYOYE("babyoye.com", "8","315",1),
	BIBA("biba.in", "660","260",1),
	BLUESTONE("bluestone.com", "480","1000",1),
	CHUMBAK("chumbak.com", "265","80",1),
	DOMINOS("dominos.co.in", "217","20",1),
	LENSKART("lenskart.com", "32","300",1),
	LIMEROAD("limeroad.com", "74","250",1),
	PRINTVENUE("printvenue.com", "126","150",1),
	VOXPOP("voxpop.com", "10","210",1),
	
	//Payoom default Url
	AIRTEL("airtel.in", "1027","3.15",true),
	
	//Payoom New
	BIGBASKET("bigbasket.com", "1045","3.50","utm_campaign=payoomapr-20182&utm_medium=cps&utm_source=payoom"),
	COOLWINKS("coolwinks.com", "1053","350",1,"utm_source=payoom&utm_medium=cps&utm_campaign=20160513_unisex_cool50"),
	CRAFTSVILLA("craftsvilla.com", "1059","7","utm_source=payoom&utm_medium=affiliate&utm_campaign=publisher_id"),
	FLOWERAURA("floweraura.com","610","12","utm_source=mooya&utm_medium=affiliate&utm_campaign=cps"),
	HAPPILYUNMARRIED("happilyunmarried.com", "474", "14", "utm_campaign=payoomoffer&utm_source=affiliate&utm_medium=cpa&utm_content=payoomoffer"),
	HOPSCOTCH("hopscotch.in", "1061","7","utm_source=affiliate&utm_medium=payoom&utm_campaign=payoom"),
	INDIACIRCUS("indiacircus.com","506","250",1,"utm_source=Payoom&utm_medium=Banners&utm_campaign=Special25"),
	INFIBEAM("infibeam.com", "812", "7", "trackId=payo"),
	JOYBYNATURE("joybynature.com","796","140",1,"utm_source=PayOOM&utm_medium=affiliate&utm_campaign=PayOOM"),
	LANDMARK("landmarkshops.in","1013","175",1,"utm_source=Affiliate&utm_medium=PAYOOM&utm_campaign=PAYOOMAP500"),
	LENSCLUES("lensclues.com","866","175",1,"utm_source=payoom&utm_medium=CPS&utm_campaign=LensClues&utm_content=FF"),
	MEBELKART("mebelkart.com","552","7","utm_source=affiliate&utm_medium=PAYOOM&utm_campaign=CPS"),
	MYFLOWERTREE("myflowertree.com","656","12","utm_source=payoom"),
	ORDERVENUE("ordervenue.com","671","8","utm_source=Payoom&utm_medium=CPS&utm_campaign=Payoom"),
	PUMA("puma.com", "1021","250",1,"utm_source=Payoom&utm_medium=Affiliates&utm_campaign=PumaPromotion"),
	ROYZEZ("royzez.com","991","140",1,"utm_source=Payoom&utm_medium=Holizez&utm_term=22March&utm_content=Cpc&utm_campaign=KakaOverseas"),
	SHOPCJ("shopcj.com","961","120",1,"utm_source=affiliates&utm_medium=payoom&utm_campaign=homepage"),
	SHOPPERSSTOP("shoppersstop.com","846","80",1,"utm_source=affiliate&utm_medium=ban&utm_campaign=ss_payoom20182&utm_content=homepage"),
	SHUDHBUY("shudhbuy.com","983","3", "utm_source=Affiliate&utm_medium=Payoom&utm_term=Aff_id_01&utm_campaign=Traffic_11_02_2016"),
	TATASKY("tatasky.com", "926","200",1,"utm_source=payoom&utm_medium=banner&utm_campaign=acquationPlan"),
	VIOLETSTREET("violetstreet.com","762","14","utm_source=payoom&utm_medium=affiliate&utm_campaign=violetstreet"),
	ZOTEZO("zotezo.com","753","9","utm_source=Payoom&utm_medium=CPS&utm_content=Beat-The-Deal&utm_campaign=CPS-Payoom-08-09-2015"),
	STALKBUYLOVE("stalkbuylove.com", "100", "180", 1, "utm_source=Payoom&utm_medium={affiliate_id}&utm_term=General&utm_content=None&utm_campaign=Janweek2"),
	SPORTS365("sports365.in","594","100",1,"utm_source=pym&utm_medium=cps&utm_campaign=aff"),
	WYO("wyo.in", "878", "14", "utm_source=Payoomads&utm_medium=CPS&utm_campaign=Discounts-All-Dec"),
	
	//Vcommission New
	INDIAREADS("indiareads.com",VCOMMISSION,"919","10","utm_medium=affiliate&utm_source=vcommission&utm_campaign=vcommission_india&utm_content=homepage"),
	FABALLEY("faballey.com",VCOMMISSION,"28","350",1,"utm_source=vcommission&utm_medium=banner&utm_campaign=VCOM"),
	FLYINGMACHINE("flyingmachine.co.in",VCOMMISSION,"2141","210",1,"utm_source=vcommission&utm_medium=display&utm_campaign=CPS"),
	KAPKIDS("kapkids.in",VCOMMISSION,"1078","280",1,"utm_source=vcommission&utm_medium=display&utm_campaign=CPS"),
	KAZO("kazo.com", VCOMMISSION, "1792","315",1,"utm_medium=Affiliate&utm_source=vcommission"),
	MONTECARLO("montecarlo.in",VCOMMISSION,"1868","7","utm_source=VComm&utm_medium=cps&utm_campaign=VCommission%20"),
	NATURESBASKET("naturesbasket.co.in", VCOMMISSION, "2371", "105", 1, "utm_source=vcomm&utm_medium=cps&utm_campaign=vcommron"),
	PRINTLAND("printland.in",VCOMMISSION,"90","175",1,"utm_source=vcom&utm_medium=affiliate&utm_campaign=product"),
	PAYTM("paytm.com",VCOMMISSION, "1022",".7","utm_source=Affiliates&utm_medium=VCOMM&utm_campaign=VCOMM-generic&utm_term={affiliate_id}_{aff_sub}"),
	MYNTRA("myntra.com", VCOMMISSION, "22", "6", "utm_source=vcommission_utm_medium=affiliate"),
	PRETTYSECRETS("prettysecrets.com", VCOMMISSION, "186","21", "utm_source=vcommission&utm_medium=cps&utm_campaign=affiliate"),
	ABOF("abof.com",VCOMMISSION,"2361","7","utm_source=aff&utm_medium=vcom"),
	APLAVA("aplava.com",VCOMMISSION, "1764","10","utm_source=vcom&utm_medium=cps&utm_campaign=vcomtest"),
	NEWU("newu.in",VCOMMISSION,"2247","7","utm_source=vcommission&utm_medium=banner&utm_campaign=sale"),
	TATACLIQ("tatacliq.com", VCOMMISSION, "2792", "7", "cid=af:vcommission:{affiliate_id}"),
	
	//Cuelinks
	MOBIKWIK("mobikwik.com", CUELINKS, "","2.25"),
	
	//Vcommission
	FREECHARGE("freecharge.in", VCOMMISSION, "1622","2.1",true),
	CLOVIA("clovia.com", VCOMMISSION, "146","24"),
	FABFURNISH("fabfurnish.com", VCOMMISSION, "1070","7"),
	FIRSTCRY("firstcry.com", VCOMMISSION, "2031","35",1),
	HOMESHOP18("homeshop18.com", VCOMMISSION, "402","8.4"),
	KOOVS("koovs.com", VCOMMISSION, "318","10.5"),
	NAAPTOL("naaptol.com", VCOMMISSION, "446","245",1),
	KFC("kfc.co.in", VCOMMISSION ,"749","30",1,true),
	ZOVI("zovi.com", VCOMMISSION, "480","10.5");
	 
	private int priority = 5;

	private String url;

	private ThirdpartyEnum thirdparty = PAYOOM;

	private String offerId;

	private String cashback;

	private int inRs;

	private boolean defaultUrl;

	private String params;
	
	private boolean goHttps;

	public static HomeEnum fromName(String name) {
		return HomeEnum.valueOf(name);
	}

	private HomeEnum(String url, ThirdpartyEnum thirdparty, String offerId, String cashback, int inRs) {
		this.url = url;
		this.thirdparty = thirdparty;
		this.offerId = offerId;
		this.cashback = cashback;
		this.setInRs(inRs);
	}

	private HomeEnum(String url, String offerId, String cashback, int inRs) {
		this.url = url;
		this.offerId = offerId;
		this.cashback = cashback;
		this.setInRs(inRs);
	}

	private HomeEnum(String url, String offerId, String cashback, int inRs, String params) {
		this.url = url;
		this.offerId = offerId;
		this.cashback = cashback;
		this.setInRs(inRs);
		this.setParams(params);
	}
	
	private HomeEnum(String url, ThirdpartyEnum thirdparty, String offerId, String cashback, int inRs, String params) {
		this.url = url;
		this.offerId = offerId;
		this.cashback = cashback;
		this.setInRs(inRs);
		this.setParams(params);
		this.setThirdparty(thirdparty);
	}
	
	private HomeEnum(String url, ThirdpartyEnum thirdparty, String offerId, String cashback, int inRs, boolean defaultUrl) {
		this.url = url;
		this.offerId = offerId;
		this.cashback = cashback;
		this.setInRs(inRs);
		this.setDefaultUrl(defaultUrl);
		this.setThirdparty(thirdparty);
	}

	private HomeEnum(String url, String offerId, String cashback, boolean defaultUrl) {
		this.url = url;
		this.setOfferId(offerId);
		this.setCashback(cashback);
		this.setDefaultUrl(defaultUrl);
	}
	
	private HomeEnum(String url, String offerId, String cashback, int inRs, boolean defaultUrl) {
		this.url = url;
		this.setOfferId(offerId);
		this.setCashback(cashback);
		this.setDefaultUrl(defaultUrl);
		this.setInRs(inRs);
	}
	
	private HomeEnum(String url, ThirdpartyEnum thirdparty, String offerId, String cashback, boolean defaultUrl) {
		this.url = url;
		this.setOfferId(offerId);
		this.setCashback(cashback);
		this.setDefaultUrl(defaultUrl);
		this.setThirdparty(thirdparty);
	}

	private HomeEnum(int priority, String url, String cashback) {
		this.priority = priority;
		this.url = url;
		this.setThirdparty(AUTOMATIC);
		this.setCashback(cashback);
	}

	private HomeEnum(String url, String offerId, String cashback) {
		this.url = url;
		this.setOfferId(offerId);
		this.setCashback(cashback);
	}

	private HomeEnum(String url, ThirdpartyEnum thirdparty, String offerId, String cashback) {
		this.url = url;
		this.setThirdparty(thirdparty);
		this.setOfferId(offerId);
		this.setCashback(cashback);
	}
	
	private HomeEnum(String url, ThirdpartyEnum thirdparty, String offerId, String cashback, String params) {
		this.url = url;
		this.setThirdparty(thirdparty);
		this.setOfferId(offerId);
		this.setCashback(cashback);
		this.setParams(params);
	}

	private HomeEnum(String url, String offerId, String cashback, String params) {
		this.url = url;
		this.setOfferId(offerId);
		this.setCashback(cashback);
		this.setParams(params);
	}
	
	private HomeEnum(String url, String offerId, String cashback, String params, boolean goHttps) {
		this.url = url;
		this.setOfferId(offerId);
		this.setCashback(cashback);
		this.setParams(params);
		this.goHttps = goHttps;
	}
	
	private HomeEnum(String url, ThirdpartyEnum thirdparty, String offerId, String cashback, String params, boolean goHttps) {
		this.url = url;
		this.setThirdparty(thirdparty);
		this.setOfferId(offerId);
		this.setCashback(cashback);
		this.setParams(params);
		this.goHttps = goHttps;
	}

	public static HomeEnum getHome(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		for (HomeEnum tmp : HomeEnum.values()) {
			if (tmp.name().equalsIgnoreCase(name)) {
				return tmp;
			}
		}
		return null;
	}

	public static HomeEnum determineHome(String url) {
		for (HomeEnum tmp : HomeEnum.values()) {
			String key = tmp.getUrl();
			if (url.contains(key)) {
				return tmp;
			}
		}
		return null;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the thirdparty
	 */
	public ThirdpartyEnum getThirdparty() {
		return thirdparty;
	}

	/**
	 * @param thirdparty
	 *            the thirdparty to set
	 */
	public void setThirdparty(ThirdpartyEnum thirdparty) {
		this.thirdparty = thirdparty;
	}

	/**
	 * @return the offerId
	 */
	public String getOfferId() {
		return offerId;
	}

	/**
	 * @param offerId
	 *            the offerId to set
	 */
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	/**
	 * @return the cashback
	 */
	public String getCashback() {
		return cashback;
	}

	/**
	 * @param cashback
	 *            the cashback to set
	 */
	public void setCashback(String cashback) {
		this.cashback = cashback;
	}

	/**
	 * @return the inRs
	 */
	public int getInRs() {
		return inRs;
	}

	/**
	 * @param inRs
	 *            the inRs to set
	 */
	public void setInRs(int inRs) {
		this.inRs = inRs;
	}

	/**
	 * @return the defaultUrl
	 */
	public boolean isDefaultUrl() {
		return defaultUrl;
	}

	/**
	 * @param defaultUrl
	 *            the defaultUrl to set
	 */
	public void setDefaultUrl(boolean defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

	/**
	 * @return the params
	 */
	public String getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(String params) {
		this.params = params;
	}

	/**
	 * @return the goHttps
	 */
	public boolean isGoHttps() {
		return goHttps;
	}

	/**
	 * @param goHttps the goHttps to set
	 */
	public void setGoHttps(boolean goHttps) {
		this.goHttps = goHttps;
	}
}
