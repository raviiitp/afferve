package com.shoptell.ravi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

@Named
public class DTOFileWriter {

	private FileWriter fw;
	private BufferedWriter bw;
	
	public static final String[] SNAPDEAL_PROPERTY_REGEX = {
		"PRODUCT DIMENSION IN CMS",
		"COMPATIBLE LAPTOP SIZE",
		"DIMENSIONS IN INCHES",
		"NO. OF COMPARTMENTS",
		"NO OF COMPARTMENTS",
		"ADDITIONAL FEATURE",
		"DIMENSION LXHXW CM",
		"LAPTOP COMPARTMENT",
		"CARRYING CAPACITY",
		"DIMENSIONS IN CMS",
		"MATERIAL & CARE",
		"VOLUME CAPACITY",
		"DIMENSION INCH",
		"NO. OF POCKETS",
		"SHOULDER STRAP",
		"OTHER FEATURES",
		"BOTTLE POCKET",
		"NO OF POCKETS",
		"COMPARTMENTS",
		"DIMENSION CM", 
		"EMPTY WEIGHT",
		"HANDLE/STRAP",
		"PRODUCT CODE",
		"PRODUCT TYPE",
		"SUITABLE FOR",
		"COMPARTMENT",
		"LAPTOP SIZE",
		"WATER PROOF",
		"DISCLAIMER",
		"AGE GROUP",
		"COMBO SET",
		"DIMENSION",
		"RAINCOVER",
		"USABILITY",
		"CAPACITY",
		"MATERIAL",
		"EAN CODE",
		"SKU CODE",
		"WARRANTY",
		"CLOSURE",
		"PADDING",
		"POCKETS",
		"GENDER",
		"WEIGHT",
		"BRAND",
		"COLOR",
		"COMBO",
		"MODEL",
		"STRAP",
		"STYLE",
		"SIZE",
		"SUPC",
		"TYPE"
		};
	
	public DTOFileWriter() {
		super();
	}

	public DTOFileWriter(String fileNamePath) {
		File file = new File(fileNamePath);
		
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeStream(String content){
		try {
			bw.write(content);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isMatchPresent(Matcher matcher, int i) {
		int count = matcher.groupCount();
		if (i <= count) {
			String text = matcher.group(i);
			if (StringUtils.isNotBlank(text)) {
				return true;
			}
		}
		return false;
	}
	
	public void SnapdealPropertiesForBags(String id, String brand, String description){
		/*Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("TYPE", null);
		propertyMap.put("FEATURES", null);
		propertyMap.put("GENDER", null);
		propertyMap.put("MATERIAL", null);
		propertyMap.put("SIZE", null);
		propertyMap.put("CAPACITY", null);
		propertyMap.put("WATER PROOF", null);*/

		if(StringUtils.isBlank(description) || StringUtils.isBlank(brand)){
			return;
		}

		writeStream(id);
		writeStream(description);
		
		//product type and type are same
				//remove brand
				//remove (\\w*)
				//remove \\s\\s*
		
		description = description.replaceAll(brand, "");
		if(StringUtils.isNotBlank(description)){
			description = description.replaceAll("\\(\\w*\\)", "");
			description = description.replaceAll("\\s\\s+", " ");
		}
		String[] descritpionByColon = null;
		String nextKey = null, prevKey = null;
		if(StringUtils.isNotBlank(description)){
			descritpionByColon = description.split("\\s?:\\s?");
			if(descritpionByColon != null && descritpionByColon.length > 0){
				prevKey = StringUtils.trim(descritpionByColon[0]);
			}
		}
		
		int ii = 0;
		String value =null;
		
		for(ii = 1; ii < descritpionByColon.length; ii++){
			for(int jj = 0; jj < SNAPDEAL_PROPERTY_REGEX.length; jj++){
				if (descritpionByColon[ii].endsWith(SNAPDEAL_PROPERTY_REGEX[jj])) {
						nextKey = SNAPDEAL_PROPERTY_REGEX[jj];
						value = descritpionByColon[ii].replace(nextKey, "");
						writeStream(prevKey + " : " + value);
						/*if(propertyMap.containsKey(prevKey)){
							propertyMap.put(prevKey, value);
						}*/
						prevKey = nextKey;
						break;
				}
			}
		}
		writeStream(prevKey + " : " + descritpionByColon[ii-1]);
		try {
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
