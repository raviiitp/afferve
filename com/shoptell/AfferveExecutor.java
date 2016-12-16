/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell;

import static com.shoptell.backoffice.BackofficeConstants.CATEGORY_UPDATE_TIME_GAP;
import static com.shoptell.backoffice.BackofficeConstants.DISABLE_DATA_DOWNLOAD;
import static com.shoptell.backoffice.BackofficeConstants.DISABLE_DATA_UPDATER;
import static com.shoptell.backoffice.BackofficeConstants.ENABLE_AUTOMATIC_MERGING;
import static com.shoptell.backoffice.BackofficeConstants.ENABLE_AUTOMATIC_REVIEW;
import static com.shoptell.backoffice.BackofficeConstants.ENABLE_HOME_PRODUCT_MERGE;
import static com.shoptell.backoffice.BackofficeConstants.ENABLE_PRODUCT_RANK_UPDATE;
import static com.shoptell.backoffice.BackofficeConstants.ENABLE_PRODUCT_SCRAPING;
import static com.shoptell.backoffice.BackofficeConstants.FORCED_CATEGORY_UPDATE;
import static com.shoptell.backoffice.BackofficeConstants.SLEEP_DATA_GATHER;
import static com.shoptell.db.messagelog.MessageEnum.INFO;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.home.CategoryInfo;
import com.shoptell.backoffice.home.ProductInfo;
import com.shoptell.backoffice.home.amazon.AmazonPopularProducts;
import com.shoptell.backoffice.home.amazon.ItemSearchApi;
import com.shoptell.backoffice.home.flipkart.FlipkartCategoryInfo;
import com.shoptell.backoffice.home.flipkart.FlipkartShoppingApi;
import com.shoptell.backoffice.home.snapdeal.SnapdealCategoryInfo;
import com.shoptell.backoffice.home.snapdeal.SnapdealShoppingApi;
import com.shoptell.backoffice.repository.util.MergeHomeProductInfoUtil;
import com.shoptell.backoffice.repository.util.ReviewedProductInfoDTOUtil;
import com.shoptell.db.messagelog.MessageLogUtil;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogUtil;
import com.shoptell.frontoffice.service.HomeProductSyncService;
import com.shoptell.frontoffice.service.RankUpdateService;
import com.shoptell.frontoffice.service.UpdateService;
import com.shoptell.service.MailService;
import com.shoptell.util.stproperties.STProperties;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "AfferveExecutor")
public class AfferveExecutor {
	private static final Logger log = LoggerFactory.getLogger(AfferveExecutor.class);
	private static final int MAX_THREAD_COUNT = 6;

	@Inject
	private FlipkartCategoryInfo flipkart_category_info;
	@Inject
	private SnapdealCategoryInfo snapdeal_category_info;
	@Inject
	private ItemSearchApi amazon_product_info;
	/*
	 * @Inject private FindItemsByCategory ebay_product_info;
	 */
	@Inject
	private FlipkartShoppingApi flipkart_product_info;
	@Inject
	private SnapdealShoppingApi snapdeal_product_info;
	/*
	 * @Inject private ShopcluesApi shopclues_product_info;
	 */
	@Inject
	private AmazonPopularProducts amazon_popular_product_info;
	@Inject
	private MergeHomeProductInfoUtil mergeEngine;
	@Inject
	private ProcessLogUtil processUtil;
	@Inject
	private STProperties stprop;
	@Inject
	private ReviewedProductInfoDTOUtil reviewUtil;
	@Inject
	protected MessageLogUtil msgLog;
	@Inject
	protected RankUpdateService rankService;
	@Inject
	protected UpdateService updateService;
	@Inject
	protected HomeProductSyncService productMergeService;
	@Inject
	protected MailService mail;

	private static ExecutorService pool;
	private static List<Future<Runnable>> futures;

	private static List<ProcessLog> processList = new LinkedList<ProcessLog>();

	private static final HomeEnum allHomes[] = { HomeEnum.AMAZON,/*
																 * HomeEnum.EBAY,
																 */HomeEnum.FLIPKART,/*
																					 * HomeEnum
																					 * .
																					 * SHOPCLUES
																					 * ,
																					 */HomeEnum.SNAPDEAL };

	public static boolean isRunning = false;

	private static Date nextCategoryUpdateTime;

	@Scheduled(cron = "${afferve.executor.data.cron}")
	public void startApplicationDataInput() {
		log.info("startApplicationDataInput() Enter");
		if (Boolean.valueOf(stprop.getValueOrDefault(DISABLE_DATA_DOWNLOAD, "true"))) {
			return;
		}
		try {
			dataInput();
		} catch (Exception e) {
			log.error("DATA INPUT ERROR", e);
			msgLog.addError(e);
		}
		log.info("startApplicationDataInput() Exit");
	}

	public void startApplicationDataUpdater() {
		log.info("startApplicationDataUpdater() Enter");
		if (Boolean.valueOf(stprop.getValueOrDefault(DISABLE_DATA_UPDATER, "true"))) {
			return;
		}
		if (isRunning)
			return;
		try {
			updater();
		} catch (Exception e) {
			log.error("DATA UPDATE ERROR", e);
			msgLog.addError(e);
		}
		log.info("startApplicationDataUpdater() Exit");
	}

	@Scheduled(cron = "${afferve.executor.merge.cron}")
	public void singleThreadDataMerge() {
		log.info("singleThreadDataMerge() Enter");
		if (Boolean.valueOf(stprop.getValueOrDefault(ENABLE_HOME_PRODUCT_MERGE, "true"))) {
			log.info("HOME_PRODUCT_MERGE - START");
			for (HomeEnum home : allHomes) {
				try {
					productMergeService.checkout(processList, home);
				} catch (InterruptedException e) {
					msgLog.addError(e);
				}
			}
			log.info("HOME_PRODUCT_MERGE - END");
		}
		if (Boolean.valueOf(stprop.getValueOrDefault(ENABLE_AUTOMATIC_REVIEW, "true"))) {
			log.info("AUTOMATIC_REVIEW - START");
			for (HomeEnum home : allHomes) {
				try {
					reviewUtil.checkout(processList, home);
				} catch (InterruptedException e) {
					msgLog.addError(e);
				}
			}
			log.info("AUTOMATIC_REVIEW - END");
		}
		if (Boolean.valueOf(stprop.getValueOrDefault(ENABLE_AUTOMATIC_MERGING, "true"))) {
			for (HomeEnum home : allHomes) {
				try {
					mergeEngine.checkout(processList, home);
				} catch (InterruptedException e) {
					msgLog.addError(e);
				}
			}
		}
		isRunning = false;
		mail.sendDataUploadCompleteMail();
		log.info("singleThreadDataMerge() Exit");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Async
	public void updater() throws InterruptedException {
		log.info("updater() Enter");
		pool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
		futures = new LinkedList<Future<Runnable>>();
		Future handler = null;

		ProductInfo product[] = { /*
								 * ebay_product_info, shopclues_product_info,
								 * amazon_product_info, flipkart_product_info,
								 */snapdeal_product_info };
		CategoryInfo category[] = { /* flipkart_category_info, */snapdeal_category_info };

		boolean forcedCategoryUpdate = Boolean.valueOf(stprop.getValueOrDefault(FORCED_CATEGORY_UPDATE, "false"));
		if (forcedCategoryUpdate || nextCategoryUpdateTime == null || (new Date(System.currentTimeMillis()).after(nextCategoryUpdateTime))) {
			int timeInHours = Integer.parseInt(stprop.getValueOrDefault(CATEGORY_UPDATE_TIME_GAP, "4"));
			nextCategoryUpdateTime = new Date(System.currentTimeMillis() + timeInHours * 60 * 60 * 1000);
			for (CategoryInfo cat : category) {
				handler = pool.submit(new Runnable() {
					@Override
					public void run() {
						try {
							cat.checkout(processList);
						} catch (InterruptedException e) {
							msgLog.add(INFO, "KILLED CATEGORY THREAD", "KILLED CATEGORY THREAD");
						}

					}
				});
				futures.add(handler);
			}
			Thread.sleep(2 * 60 * 1000);// 2 minutes
		}
		for (ProductInfo prod : product) {
			handler = pool.submit(new Runnable() {
				@Override
				public void run() {
					prod.updater(processList);
				}
			});
			futures.add(handler);
		}

		Thread.sleep(1000 * 60 * Integer.parseInt(stprop.getValueOrDefault(SLEEP_DATA_GATHER, "30"))); // 1/2
		stopExecutionThread();
		log.info("updater() Exit");
	}

	@SuppressWarnings("unchecked")
	@Async
	public void dataInput() throws InterruptedException {
		log.info("dataInput() Enter");
		isRunning = true;
		pool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
		futures = new LinkedList<Future<Runnable>>();

		// Amazon and Ebay Category Info is already in table
		// Step 1: Flipkart Category Info
		// Step 2: Snapdeal Category Info
		// Step 3: Amazon Product Checkout
		// Step 4: Ebay Product Checkout
		// Step 5: Shopclues Product Checkout
		// Step 6: Flipkart Product Checkout
		// Step 7: Snapdeal Product Checkout

		ProductInfo product[] = { /* ebay_product_info, shopclues_product_info, */amazon_product_info, flipkart_product_info, snapdeal_product_info };
		CategoryInfo category[] = { flipkart_category_info, snapdeal_category_info };

		for (CategoryInfo cat : category) {
			futures.add((Future<Runnable>) pool.submit(new Runnable() {
				@Override
				public void run() {
					try {
						cat.checkout(processList);
					} catch (InterruptedException e) {
						msgLog.add(INFO, "KILLED CATEGORY THREAD", "KILLED CATEGORY THREAD");
					}

				}
			}));
		}

		for (Future<Runnable> tmp : futures) {
			try {
				tmp.get();
			} catch (InterruptedException e) {
				msgLog.addError(e);
			} catch (ExecutionException e) {
				msgLog.addError(e);
			}
		}

		for (ProductInfo prod : product) {
			futures.add((Future<Runnable>) pool.submit(new Runnable() {
				@Override
				public void run() {
					prod.checkout(processList);
				}
			}));
		}

		futures.add((Future<Runnable>) pool.submit(new Runnable() {
			@Override
			public void run() {
				amazon_popular_product_info.checkout(processList);

			}
		}));

		for (Future<Runnable> tmp : futures) {
			try {
				tmp.get();
			} catch (InterruptedException e) {
				msgLog.addError(e);
			} catch (ExecutionException e) {
				msgLog.addError(e);
			}
		}

		if (Boolean.valueOf(stprop.getValueOrDefault(ENABLE_PRODUCT_RANK_UPDATE, "false"))) {
			log.info("PRODUCT_RANK_UPDATE - START");
			for (HomeEnum home : allHomes) {
				if (home.equals(HomeEnum.FLIPKART) || home.equals(HomeEnum.SNAPDEAL)) {
					futures.add((Future<Runnable>) pool.submit(new Runnable() {
						@Override
						public void run() {
							try {
								rankService.checkout(processList, home);
							} catch (InterruptedException e) {
								msgLog.addError(e);
							}
						}
					}));
				}
			}
			log.info("PRODUCT_RANK_UPDATE - END");
		}

		if (Boolean.valueOf(stprop.getValueOrDefault(ENABLE_PRODUCT_SCRAPING, "false"))) {
			log.info("PRODUCT_SCRAPING - START");
			for (HomeEnum home : HomeEnum.values()) {
				if (home.equals(HomeEnum.FLIPKART) || home.equals(HomeEnum.SNAPDEAL) || home.equals(HomeEnum.AMAZON)) {
					futures.add((Future<Runnable>) pool.submit(new Runnable() {
						@Override
						public void run() {
							try {
								updateService.checkout(processList, home);
							} catch (InterruptedException e) {
								msgLog.addError(e);
							}
						}
					}));
				}
			}
			log.info("PRODUCT_SCRAPING - END");
		}

		log.info("dataInput() Exit");
	}

	private void stopExecutionThread() {
		for (Future<?> future : futures) {
			if (future.isDone() || future.isCancelled()) {
			}
			else {
				future.cancel(true);
			}
		}

		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					log.error("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}

		futures.clear();

		if (processList.size() > 0) {
			markProcessesIncomplete();
		}
		try {
			Thread.sleep(2 * 60 * 1000);
		} catch (InterruptedException e) {
			msgLog.addError(e);
		}
	}

	private void markProcessesIncomplete() {
		try {
			processUtil.kill(processList);
			processList.clear();
		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return isRunning;
	}
}
