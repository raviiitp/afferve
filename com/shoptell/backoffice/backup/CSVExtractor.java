/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.backup;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

/**
 * @author abhishekagarwal
 *
 */
//@Named(value = "CSVExtractor")
public class CSVExtractor{

	private static final Logger log = LoggerFactory.getLogger(CSVExtractor.class);

	private static String FILENAME;

	// Create the CSVFormat object
	private CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter('|');

	private CSVParser parser;

	private List<ReviewedProductInfoDTO> productInfoList = new LinkedList<ReviewedProductInfoDTO>();

	//@PostConstruct
	public void start() {
		FILENAME = "/Users/abhishekagarwal/shoprepoClone/src/main/resources/shopclues/review.csv";//getClass().getResource("/shopclues/review.csv").getFile();
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

	protected void postprocess() {
		//batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), productInfoList);
		productInfoList.clear();
	}

	protected void preprocess() {
		productInfoList = new LinkedList<ReviewedProductInfoDTO>();
		String link = "http://image2.shopclues.com/images/feed/Affiliate_feed.csv.gz";
		InputStream in = null;
		ByteArrayOutputStream out = null;
		FileOutputStream fos = null;
		GZIPInputStream gzipInputStream = null;
		try {
			URL url = new URL(link);

			in = new BufferedInputStream(url.openStream());
			out = new ByteArrayOutputStream();
			fos = new FileOutputStream(FILENAME);
			log.info("Download Start.");

			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				fos.write(buf, 0, n);
			}

			fos.close();

			fos = new FileOutputStream(FILENAME);

			while (-1 != (n = gzipInputStream.read(buf))) {
				fos.write(buf, 0, n);
			}

			log.info("Download Complete.");
		} catch (Exception e) {
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

	public void execute() throws InterruptedException {
		List<String> header = new LinkedList<String>();
		List<String> values = new LinkedList<String>();
		try {
			InputStream is = new FileInputStream(FILENAME);// getClass().getResourceAsStream("/shopclues/Affiliate_feed.csv");
			if (is != null) {
				Reader reader = new InputStreamReader(is);
				parser = new CSVParser(reader, format);
				boolean first = true;
				for (CSVRecord element : parser) {
					int len = element.size();
					if (first){
						first=false;
						for (int i =0 ; i<len ; i++){
							header.add(element.get(i));
							if (compareIt(element.get(i).trim())){
								System.out.println(i+" - "+element.get(i));
							}
						}
					}
					else{
						for (int i =0 ; i<len ; i++){
							values.add(element.get(i));
						}
						appendTofile(getProd1(values));
						//appendTofile(getProd2(values));
						//System.out.println(header.toString());
						//System.out.println(values.toString());
						values.clear();
					}
					
				}
			}

		} catch (Exception e) {
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
	
	private boolean compareIt(String string) {
		return "imageurlsmall".equalsIgnoreCase(string) || "imageurllarge".equalsIgnoreCase(string) || "imageurl".equalsIgnoreCase(string);
	}

	private void appendTofile(String prod) {
		try {
			fos.write(prod.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getProd(List<String> values) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into shoptell.reviewed_product_info (ismerged, home, subcategoryname, id, competitorfields) VALUES "
				+ "(true, '"+values.get(0).trim()+"', '"+values.get(1).trim()+"','"+values.get(2).trim()+"', "+values.get(21).trim()+");");
		sb.append("\n");
		return sb.toString().replace("\"", "");
	}
	
	private String getProd1(List<String> values) {
		StringBuilder sb = new StringBuilder();
		sb.append("update shoptell.reviewed_product_info set ismerged = false where home = '"+values.get(0).trim()+"' and subcategoryname = 'SMARTPHONES' "
				+ "AND id = '"+values.get(2).trim()+"';");
		sb.append("\n");
		/*sb.append("update shoptell.home_product_info set ismerged = true where home = '"+values.get(0).trim()+"' and subcategoryname = 'SMARTPHONES' "
				+ "AND id = '"+values.get(2).trim()+"';");
		sb.append("\n");*/
		return sb.toString().replace("\"", "");
	}
	
	private String getProd2(List<String> values) {
		StringBuilder sb = new StringBuilder();
		String imageurl = values.get(42);
		if (StringUtils.isNotBlank(imageurl)){
			imageurl = imageurl.replaceAll("http(s?):", "").trim();
		}
		String imageurllarge = values.get(43);
		if (StringUtils.isNotBlank(imageurllarge)){
			imageurllarge = imageurllarge.replaceAll("http(s?):", "").trim();
		}
		String imageurlsmall = values.get(46);
		if (StringUtils.isNotBlank(imageurlsmall)){
			imageurlsmall = imageurlsmall.replaceAll("http(s?):", "").trim();
		}
		sb.append("update shoptell.reviewed_product_info set imageurl = '"+imageurl+"', imageurllarge = '"+imageurllarge+"', imageurlsmall = '"+imageurlsmall+"' where home = '"+values.get(0).trim()+"' and subcategoryname = 'SMARTPHONES' "
				+ "AND id = '"+values.get(2).trim()+"';");
		sb.append("\n");
		return sb.toString().replace("\"", "");
	}
	
	static FileOutputStream fos = null;
/*	public static void main(String[] args) throws InterruptedException {
		CSVExtractor ex = new CSVExtractor();
		FILENAME = "/Users/abhishekagarwal/shoprepoClone/src/main/resources/shopclues/review.csv";
		 try {
			fos = new FileOutputStream("/Users/abhishekagarwal/shoprepoClone/src/main/resources/shopclues/review_f.cql");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ex.execute();
	}*/
}

