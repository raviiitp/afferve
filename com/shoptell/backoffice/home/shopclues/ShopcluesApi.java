/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.shopclues;

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.home.ProductInfo;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.util.HomeProductInfoUtil;

/**
 * @author abhishekagarwal
 *
 */
//@Named(value = "ShopcluesApi")
public class ShopcluesApi extends ProductInfo {
	
	private static final Logger log = LoggerFactory.getLogger(ShopcluesApi.class);

	private static String FILENAMEARCHIVE;

	private static String FILENAME;

	// Create the CSVFormat object
	private CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

	private CSVParser parser;

	private List<HomeProductInfoDTO> productInfoList = new LinkedList<HomeProductInfoDTO>();

	@Inject
	private HomeProductInfoUtil homeProductInfoUtil;

	@PostConstruct
	public void start() {
		home = HomeEnum.SHOPCLUES;
		//FILENAMEARCHIVE = getClass().getResource("/shopclues/Affiliate_feed.csv.gz").getFile();
		//FILENAME = getClass().getResource("/shopclues/Affiliate_feed.csv").getFile();
	}

	@Async
	public void init() {
		preprocess();
		try {
			execute();
		} catch (InterruptedException e) {
			log.error("INIT EXCEPTION", e);
		}
		postprocess();
	}

	@Override
	protected void postprocess() {
		batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), productInfoList);
		productInfoList.clear();
	}

	@Override
	protected void preprocess() {
		productInfoList = new LinkedList<HomeProductInfoDTO>();
		String link = "http://image2.shopclues.com/images/feed/Affiliate_feed.csv.gz";
		InputStream in = null;
		ByteArrayOutputStream out = null;
		FileOutputStream fos = null;
		GZIPInputStream gzipInputStream = null;
		try {
			URL url = new URL(link);

			in = new BufferedInputStream(url.openStream());
			out = new ByteArrayOutputStream();
			fos = new FileOutputStream(FILENAMEARCHIVE);

			log.info("Download Start.");

			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				fos.write(buf, 0, n);
			}

			fos.close();

			gzipInputStream = new GZIPInputStream(new FileInputStream(FILENAMEARCHIVE));
			fos = new FileOutputStream(FILENAME);

			while (-1 != (n = gzipInputStream.read(buf))) {
				fos.write(buf, 0, n);
			}

			log.info("Download Complete.");
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("DOWNLOAD ERROR", e);
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (gzipInputStream != null)
					gzipInputStream.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) {
				log.error("ERROR", e);
			}
		}
	}

	@Override
	public void execute() throws InterruptedException {
		try {
			InputStream is = new FileInputStream(FILENAME);// getClass().getResourceAsStream("/shopclues/Affiliate_feed.csv");
			if (is != null) {
				Reader reader = new InputStreamReader(is);
				parser = new CSVParser(reader, format);

				for (CSVRecord element : parser) {

					// Stopping running Thread
					if (Thread.currentThread().isInterrupted()) {
						throw new InterruptedException("KILL THREAD");
					}

					HomeProductInfoDTO pInfo = new HomeProductInfoDTO();
					pInfo.setSalesRank(0); // TODO Set Rank When Available
					pInfo.setHome(HomeEnum.SHOPCLUES.name());
					pInfo.setCategoryId(element.get("Leaf CategoryId"));
					pInfo.setCategoryName(element.get("Leaf Category"));

					// Ignore other products except Smart Phones
					if (!"Smart Phones".equalsIgnoreCase(pInfo.getCategoryName())) {
						continue;
					}
					// pInfo.setSubCategoryName(CategoryNameMapper.CategoryNameMapper.get(pInfo.getCategoryName()));
					pInfo.setId(element.get("Product ID"));

					Set<String> path = new LinkedHashSet<String>(Arrays.asList(element.get("Category Path").split("///")));
					pInfo.setCategoryPaths(path);

					pInfo.setTitle(element.get("Product Name"));
					pInfo.setOriginalTitle(pInfo.getTitle());
					pInfo.setProductBrand(element.get("Brand"));

					for (int i = 1; i < 5; i++) {
						StringBuilder sb = new StringBuilder();
						String name = "Product Label" + i;
						String desc = element.get(name);
						if (StringUtils.isNotBlank(desc) && !StringUtils.equalsIgnoreCase(desc, "NotAvailable")) {
							sb.append(desc);
						}
						if (StringUtils.isNotBlank(sb.toString())) {
							pInfo.setDescription(sb.toString());
						}
					}

					pInfo.setImageUrl(element.get("image_path"));

					pInfo.setMrp(Double.parseDouble(element.get("MRP")));
					pInfo.setSellingPrice(Double.parseDouble(element.get("Price")));
					pInfo.setDiscountPercentage(element.get("Discount (percentage)"));
					pInfo.setProductUrl(element.get("Product URL"));

					pInfo.setInStock("yes".equalsIgnoreCase(element.get("Stock")));
					
					pInfo.setSubCategoryName(pInfo.getCategoryName());

					// pinfo.setOffset(productArray.getJSONObject(i).getString("offset"));
					// TODO check offset value
					pInfo.setOffset(null);
					pInfo.setTags(homeProductInfoUtil.getTag_asSet(element.get("Product Name"), null, element.get("Brand")));
					// TODO set competitor fields
					if (!StringUtils.isEmpty(pInfo.getTitle())) {
						//CompetitorFieldsDTO compField = homeProductInfoUtil.setCompetitorFields(pInfo);
						//if (compField != null) {
							/*pInfo.setCompetitorFields(compField);
							pInfo.setTitle(compField.getName());
							pInfo.setColor(compField.getProperties().get("COLOR"));
							pInfo.setSize(compField.getProperties().get("SIZE"));*/
							if (!CategoryEnum.SMARTPHONES.name().equalsIgnoreCase(pInfo.getSubCategoryName())) {
								pInfo = null;
							}
							if (pInfo != null) {
								productInfoList.add(pInfo);
							}
							if (productInfoList.size() > BATCHSIZE) {
								batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), productInfoList);
								productInfoList.clear();
							}
						//}
					}
					// productInfoList.add(pInfo);
				}
			}

		} catch (InterruptedException e) {
			throw new InterruptedException("KILL THREAD");
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("EXECUTION ERROR", e);
		} finally {
			try {
				parser.close();
			} catch (IOException e) {
				log.error("ERROR", e);
			}
		}
		log.info("Execution Complete.");
	}

	@Override
	protected void priceUpdater() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}
}
