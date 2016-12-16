/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.processor.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import com.shoptell.backoffice.processor.DataProcessor;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

/**
 * @author abhishekagarwal
 *
 */
@Named
public class ChocolateProcessor extends DataProcessor {
	
	//FileWriter fw = null;
	
	@PostConstruct
	public void start(){
		String filename= "MyFile.txt";
	    /*try {
			//fw = new FileWriter(filename,true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/* (non-Javadoc)
	 * @see com.shoptell.backoffice.processor.DataProcessor#shopcluesProcess(com.shoptell.backoffice.repository.dto.HomeProductInfoDTO)
	 */
	@Override
	public ReviewedProductInfoDTO shopcluesProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.shoptell.backoffice.processor.DataProcessor#ebayProcess(com.shoptell.backoffice.repository.dto.HomeProductInfoDTO)
	 */
	@Override
	public ReviewedProductInfoDTO ebayProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.shoptell.backoffice.processor.DataProcessor#snapdealProcess(com.shoptell.backoffice.repository.dto.HomeProductInfoDTO)
	 */
	@Override
	public ReviewedProductInfoDTO snapdealProcess(HomeProductInfoDTO prod) {
		return amazonProcess(prod);
	}

	/* (non-Javadoc)
	 * @see com.shoptell.backoffice.processor.DataProcessor#amazonProcess(com.shoptell.backoffice.repository.dto.HomeProductInfoDTO)
	 */
	@Override
	public ReviewedProductInfoDTO amazonProcess(HomeProductInfoDTO prod) {
		String product_brand = prod.getProductBrand().toUpperCase().trim();
		String title = prod.getTitle().toUpperCase().trim();
		String original = title;
		String type = prod.getCategoryName().toUpperCase().trim();
		String size = null;
		String sizeScale = null;
		String goodie = null;
		String offer = "";
		
		if (StringUtils.isBlank(title)){
			return null;
		}
		
		if (StringUtils.isNotBlank(title)) {
			String regexs = "\\(.*\\)|PACK\\s+OF\\s+\\d+|\\d+\\s+BARS?";
			Pattern pattern = Pattern.compile(regexs, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(title);
			while (matcher.find()) {
				String tmp = matcher.group(0);
				if (StringUtils.isNotBlank(tmp)) {
					title = title.replace(tmp, "");
					if (StringUtils.isNotBlank(offer)){
						offer += ", ";
					}
					offer = tmp.replaceAll("\\(|\\)", "");
				}
			}
		}
		
		String regex = "PREMIUM\\sQUALITY|IMPORTED|CARTON|PACK";
		title = title.replaceAll(regex, "");
		
		if (StringUtils.isNotBlank(product_brand)) {
			int initSize = title.length();
			title = title.replace(product_brand, "");
			if (title.length() == initSize) {
				String[] split = product_brand.split("\\s|,|\\.");
				if (split.length > 1) {
					for (String tmp : split) {
						if (tmp.endsWith("S")){
							tmp = tmp+"?";
						}
						else {
							tmp = tmp+"'?S?";
						}
						title = title.replace(tmp, "");
					}
				}
			}
		}
		
		if (StringUtils.isNotBlank(type)){
			regex = type;
			if (type.endsWith("S")){
				regex = type+"?";
			}
			else {
				regex = type+"'?S?";
			}
			title = title.replaceAll(regex, "");
		}
		
		String sizeRegex = "(\\d+\\s?[\\*|X]\\s?)?(\\d+\\.?\\d*)\\s*([a-zA-Z]+\\s+)?(PCS?|GRAMS?|GM?S?)";
		
		if (StringUtils.isNotBlank(title)) {
			Pattern pattern = Pattern.compile(sizeRegex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(title);
			while (matcher.find()) {
				if (isMatchPresent(matcher, 2)) {
					size = matcher.group(2);
					if (isMatchPresent(matcher, 4)) {
						sizeScale = matcher.group(4);
						if (StringUtils.isNotBlank(size) && StringUtils.isNotBlank(sizeScale)){
							title = title.replace(matcher.group(0), "");
						}
					}
				}
			}
		}
		
		if (StringUtils.isNotBlank(sizeScale)){
			if (!("PC".equalsIgnoreCase(sizeScale)||"PCS".equalsIgnoreCase(sizeScale))){
				sizeScale = "G";
			}
		}
		
		if (title.contains("WITH")){
			int index = title.lastIndexOf("WITH");
			if (index != -1){
				goodie = title.substring(index);
				title = title.substring(0, index);
			}
		}
		
		regex = "WITH";
		if (StringUtils.isNotBlank(title)){
			title = title.replaceAll(regex, "");
			String SP_CHAR_REMOVE_REGEX = "[^\\p{L}\\p{Z}\\p{N}&%]";
			title = title.replaceAll(SP_CHAR_REMOVE_REGEX, " ");
			title = title.replaceAll("\\s+", " ").trim();
		}
		
		if (StringUtils.isNotBlank(goodie)){
			goodie = goodie.replaceAll(regex, "");
			String[] split = goodie.split("-");
			if (split.length > 1){
				goodie = split[0];
			}
			goodie = goodie.replaceAll("\\s+", " ").trim();
		}
		String[] split = title.split("\\s");
		String line = "";
		Set<String> set = new HashSet<String>(Arrays.asList(new String[]{"S","X"}));
		for (String tmp : split){
			if (StringUtils.isNotBlank(tmp) && !set.contains(tmp)){
				line +=tmp+" ";
			}
		}
		
		if (StringUtils.isNotBlank(line)){
			title = line;
		}
		
		String a = product_brand+" * "+type+" * "+size+" * "+sizeScale+" * "+goodie+" * "+offer+" * "+title+" ["+original+"]";
		/*try {
			//fw.write(a+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return null;
	}

	/* (non-Javadoc)
	 * @see com.shoptell.backoffice.processor.DataProcessor#flipkartProcess(com.shoptell.backoffice.repository.dto.HomeProductInfoDTO)
	 */
	@Override
	public ReviewedProductInfoDTO flipkartProcess(HomeProductInfoDTO prod) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*public static void main(String[] args) {
		DataProcessor smp = new ChocolateProcessor();
		HomeProductInfoDTO prod = new HomeProductInfoDTO();
		prod.setHome("AMAZON");
		prod.setTitle("MALTESERS MILK CHOCOLATE WITH HONEYCOMBED CENTRE 120 GRAMS BOX!");
		prod.setProductBrand("MALTESERS");
		prod.setCategoryName("PACKETS & BOXES");
		smp.amazonProcess(prod);
		 //smp.ebayProcess(prod);
		//smp.snapdealProcess(prod);
		//smp.shopcluesProcess(prod);
		//smp.flipkartProcess(prod);
	}*/

}

