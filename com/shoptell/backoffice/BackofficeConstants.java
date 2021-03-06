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

/**
 * @author abhishekagarwal INFIBEAM ?trackId=afferve
 */
public final class BackofficeConstants {

	public static final String SP_CHAR_REMOVE_REGEX = "[^\\p{L}\\p{Z}\\p{N}\\+&-]";

	public static final String FORMAT = "UTF-8";

	public static final String KEYSPACENAME_VAR = "keyspaceName";

	public static final String AMAZON_TRACK_IDS = "amazon.track.ids";

	public static final String ADMIN_ID = "19f795e6-512c-4b78-b2f6-38814ec523a3";

	public static final String NULL_EMAIL = "NULL";

	public static final String RECAPTCHA_COOKIE_NAME = "_af_b_t";

	public static final String ALL_COUPON_CODES = "ALL_COUPON_CODES";

	public static final int RECAPTCHA_MAX_AGE = 15 * 24 * 60 * 60; /*
																	 * 15 days
																	 * as in
																	 * database
																	 * (ttl)
																	 */
	public static final int RECAPTCHA_MAX_AGE_MILLI = RECAPTCHA_MAX_AGE * 1000;

	public static final String BASEURL = "https://www.afferve.com/";
	public static final String BASEURL_EMAIL = "https://www.afferve.com/activate-email?";
	public static final String REFERER = "https://www.afferve.com/search?q";

	/*
	 * public static final String BASEURL = "http://localhost:8080/"; public
	 * static final String BASEURL_EMAIL =
	 * "http://localhost:8080/activate-email?"; public static final String
	 * REFERER = "http://localhost:8080/search?q";
	 */

	public static final int BATCHSIZE = 25;

	public static final int FETCHSIZE = 25; // reduced from 250 to 150 because
											// some queries were getting timed
											// out

	public static final long TIME_DIFFERENCE = 5 * 60 * 60 * 1000L; // 5 hours

	public static final float SCORE = 0.9f;

	public static final String[] AMAZON_SORT_VALUES = { "-price", "date-desc-rank", "price", "relevancerank", "reviewrank", "reviewrank_authority", "salesrank" };

	/**
	 * "ACER,AIRO WIRELESS,ALCATEL,ALTEK,AMAZON,AMOI,APPLE,ARCHOS,ASUS,AT&T,BARNES & NOBLE,BENQ,"
	 * +
	 * "BENQ-SIEMENS,BEST BUY,BIRD,BLACKBERRY,BLU,CASIO,CAT,CELKON,CINGULAR,COOLPAD,COWON,CRICKET,DANGER,DELL,DOPOD,EMPORIA,ERICSSON,"
	 * +
	 * "ETEN,FIREFLY MOBILE,FLY,FUJITSU,FUSION GARAGE,GARMIN-ASUS,GENERANAL MOBILE,GIGABYTE,GIONEE,GOOGLE,HAIER,HANDSPRING,HELIO,HITACHI,"
	 * +
	 * "HP,HTC,HUAWEI,I-MATE,I-MOBILE,ICEMOBILE,INQ,JOLLA,KARBONN,KOGAN,KYOCERA,LATTE,LAVA,LEMON MOBILES,LENOVO,LG,LUMIGON,MAXON,MAXWEST,"
	 * +
	 * "MEIZU,MICROMAX,MICROSOFT,MITAI,MITSUBISHI,MOBIADO,MOTOROLA,NEC,NEONODE,NIU,NOKIA,NOTION INC,NVIDIA,O2,ONEPLUS,OPPO,ORANGE,PALM,"
	 * +
	 * "PANASONIC,PANTECH,PCD,PHILIPS,PLUM,QTEK,SAGEM,SAMSUNG,SANYO,SAYGUS,SENDO,SHARP,SIEMENS,SIERRA WIRELESS,SONIM,SONY,SONY ERICSSON,"
	 * +
	 * "SPICE MOBILE,SPRINT,T-MOBILE,TAG HEUER,TERRESTAR,TOSHIBA,UMX,VELOCITY,VERIZON,VERTU,VERYKOOL,VERZO,VIDEOCON,VIEWSONIC,VIVO,VIZIO,"
	 * + "VKMOBILE,WIND,XIAOMI,XOLO,YEZZ,YOTA,ZEN MOBILE,ZTE"
	 */
	public static final String MOBILE_BRANDS = "AUXUS,AOC,ADCOM,ACER,ALCATEL,AMAZON,APPLE,ASUS,BSNL,BLACKBERRY,BLU,CASIO,CELKON,DELL,ERICSSON,"
			+ "FLY,FORME,GIONEE,GOOGLE,HAIER,HELIO,HP,HTC,HUAWEI,I-MATE,INEW,INFOCUS,INTEX,IBALL,JIVI,KARBONN,LAVA,LEMON,LENOVO,LG,MICROMAX,MIROMAX,"
			+ "MI,MICROSOFT,MOTO,MOTOROLA,MTS,ONIDA,PHICOMM,RELIANCE,SANSUI,SWIPE,VOX,WYNNCOM,GFIVE,"
			+ "NOKIA,NVIDIA,ONE PLUS,ONEPLUS,OPPO,PANASONIC,PHILIPS,SAMSUNG,SHARP,SIEMENS,SONY,SONY ERICSSON,SPICE,T-MOBILE,VERIZON,VIDEOCON,VIVO,"
			+ "XIAOMI,XOLO,YOTA,YU,YKING,ZEN MOBILE,ZEN,ZTE";

	public static final String SHOE_BRANDS = "UNITED COLORS OF BENETTON,AMERICAN DERBY POLO CLUB,BEING HUMAN CLOTHING,HATS OFF ACCESSORIES,"
			+ "ADIDAS ORIGINALS,BATA INDUSTRIALS,MASSIMO ITALIANO,NEZ BY SAMSONITE,ALBERTO TORRESI,CARLTON LONDON,CLAUDE LORRAIN,"
			+ "LOUIS PHILIPPE,TANGERINE TOES,AMERICAN SWAN,BRUNO MANETTI,SALT N PEPPER,SHORTY CAPONE,STYLE CENTRUM,URBAN COUNTRY,"
			+ "WILLY WINKIES,A BY ARDEN,ALPES MARTIN,FRANCO LEONE,HUSH PUPPIES,JACK & JONES,KNOTTY DERBY,RIVER ISLAND,SOLE THREADS,"
			+ "STEVE MADDEN,Z COLLECTION,ADITI WASAN,ALLEN SOLLY,ANDREW HILL,AS ORIGINAL,BREAKBOUNCE,ENROUTE MEN,FIZIK FREAK,HIGH SIERRA,"
			+ "URBAN WOODS,WEINBRENNER,A BY ARDEN,DORRISSINI,DR. SCHOLL,GO BAHAMAS,LEE COOPER,NORTH STAR,NUMERO UNO,PHOSPHORUS,SANFRISSCO,"
			+ "TIMBERLAND,TOM TAILOR,VAN HEUSEN,BATA LITE,BELLFIELD,ESTD.1977,GET GLAMR,GLOBALITE,MOCASSINO,MR BUTTON,RED CHIEF,SLAZENGER,"
			+ "TRED FLEX,VENTOLAND,WAVE WALK,BUCKAROO,BUCKLEUP,COLUMBUS,CONVERSE,DAVINCHI,FLIPSIDE,FREETOES,G SPORTS,NEW LOOK,NICHOLAS,"
			+ "PROVOGUE,RED TAPE,SANZOTTI,SKECHERS,SUNSHINE,SUPERDRY,WOODLAND,BALUJAS,BARRETO,BUGATTI,FOSTELO,FRESTOL,GRIFFON,IMAGICA,LE-FORE,"
			+ "LI-NING,LIBERTY,NAUTICA,PLAYBOY,PROBASE,PROZONE,REDMOND,SALOMON,SCENTRA,STARDOX,ACTION,ADIDAS,ARKOUR,BANISH,BASICS,BENERA,"
			+ "BURNER,BURTON,CLARKS,DELIZE,FAMOZI,FONTAL,FOOTIN,INCULT,ISHOES,KOSHER,LANCER,LEVI'S,LORD'S,PADUKI,PANAHI,REEBOK,SANDAK,SCHOLL,"
			+ "TURTLE,TYCOON,UPANAH,VIRTIS,WILSON,ARDEN,ARROW,ASICS,BROGE,CELIO,CROCS,EGOSS,FIZIK,GUAVA,KELME,KIELZ,KOBOY,LOTTO,METRO,MOCAS,"
			+ "MOCHI,MUFTI,POWER,RUOSH,SPARX,SPINN,SPUNK,TZARO,UMBRO,WOODS,YEPME,YUUKI,ZUICY,ALDO,ARTH,BATA,CYKE,DUKE,DUNE,FILA,FUNK,HITZ,NEXT,"
			+ "NIKE,PUMA,VANS,BCK,FLX,GAS,KIK,MSL,DC,HM,ID";

	public static final String SOFT_TOY_BRANDS = "ARCHIES,ANGRY BIRDS,DREAMWORKS,PILLOW PETS,WARNER BROS.";

	public static final String COLOR_REGEX = "(ORANGE|SILVER|YELLOW|BLACK|BROWN|GREEN|WHITE|BLUE|GOLD|GRAY|GREY|PINK|ROSE|RED)";

	public static final String ENABLE_PRODUCT_SCRAPING = "ENABLE_PRODUCT_SCRAPING";
	public static final String SCRAP_SERVER_HOST = "SCRAP_SERVER_HOST";
	public static final String SCRAP_SERVER_PORT = "SCRAP_SERVER_PORT";
	public static final String ENABLE_AUTOMATIC_MERGING = "ENABLE_AUTOMATIC_MERGING";
	public static final String DISABLE_DATA_DOWNLOAD = "DISABLE_DATA_DOWNLOAD";
	public static final String ENABLE_HOME_PRODUCT_MERGE = "ENABLE_HOME_PRODUCT_MERGE";
	public static final String IST_TIME_OFFSET = "IST_TIME_OFFSET";
	public static final String ENABLE_PRODUCT_RANK_UPDATE = "ENABLE_PRODUCT_RANK_UPDATE";
	public static final String SLEEP_DATA_GATHER = "SLEEP_DATA_GATHER";
	public static final String SLEEP_HOT_DATA_GATHER = "SLEEP_HOT_DATA_GATHER";
	public static final String ENABLE_ES_RANK_UPDATE = "ENABLE_ES_RANK_UPDATE";
	public static final String DISABLE_DATA_MERGE = "DISABLE_DATA_MERGE";
	public static final String ENABLE_AUTOMATIC_REVIEW = "ENABLE_AUTOMATIC_REVIEW";
	public static final String CATEGORY_UPDATE_TIME_GAP = "CATEGORY_UPDATE_TIME_GAP";
	public static final String DISABLE_DATA_CRUNCH = "DISABLE_DATA_CRUNCH";
	public static final String FORCED_CATEGORY_UPDATE = "FORCED_CATEGORY_UPDATE";

	public static final int AMAZON_TRACK_SIZE = 20;
	public static final long IST_OFFSET = 330 * 60 * 1000;
	public static final long ONE_DAY = 24 * 60 * 60 * 1000;
	public static final long ONE_HOUR = 60 * 60 * 1000;

	public static final String DISABLE_DATA_UPDATER = "DISABLE_DATA_UPDATER";
	public static final String DISABLE_INVITE_SEND = "DISABLE_INVITE_SEND";
	public static final String DATA_EXPIRY_DAY_COUNT = "DATA_EXPIRY_DAY_COUNT";

	public static final double CONVERSION_RATE = 0.70;
	public static final double REFFERAL_RATE = 0.05;
	public static final double DEDUCT_FROM_SOURCE = 0.01;

	public static final String DECIMAL_FORMAT = "#.##";

	public static final String MERGE_TIME_LIMIT = "MERGE_TIME_LIMIT";

	public static final int MAX_REQUEST_COUNT = 20;

	public static final double MIN_CUT_AMOUNT = 45;

	public static final int REQUEST_TIMEOUT = 2000;

	public static final String HOMES[] = { "AMAZON", "FLIPKART", "SNAPDEAL" };

	public static final int MAX_ITEM_SIZE = 36;

	public static final int MAX_EMPTY_QUERY_COUNT = 10;

	public static final String DISABLE_DATA_BACKUP = "DISABLE_DATA_BACKUP";

	public static final int POPULAR_ITEMS_MAX_LIMIT = 12;

	public static final int POPULAR_ITEMS_HOMEPAGE_LIMIT = 4;

	public static final String SEND_INVITE_SIZE = "SEND_INVITE_SIZE";

	public static final int MAX_TRANSACTION_LIST = 10;
}
