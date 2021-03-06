/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.util;

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.shoptell.backoffice.repository.BatchRepository;

/**
 * @author abhishekagarwal
 *
 */
//@Named(value = "BankDetailsUtil")
public class BankDetailsUtil {

	private static final Logger log = LoggerFactory.getLogger(BankDetailsUtil.class);

	private String FILENAME;

	@Inject
	private BatchRepository repo;

	List<BankDetailDTO> list = new LinkedList<BankDetailDTO>();

	@PostConstruct
	public void start() {
		//FILENAME = getClass().getResource("/bank/details.xls").getFile();
	}

	@Async
	public void init() {
		preprocess();
		execute();
		postprocess();
	}

	private void postprocess() {
		repo.batchSave(list);
		list.clear();
		log.debug("End");
	}

	private void execute() {
		FileInputStream file = null;
		try {
			file = new FileInputStream(new File(FILENAME));
			@SuppressWarnings("resource")
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			int sheets = workbook.getNumberOfSheets();
			for (int indx = 0; indx < sheets; indx++) {
				HSSFSheet sheet = workbook.getSheetAt(indx);
				Iterator<Row> rowIterator = sheet.iterator();
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					BankDetailDTO dto = new BankDetailDTO();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						cell.setCellType(Cell.CELL_TYPE_STRING);
						int key = cell.getColumnIndex()+1;
						String value = cell.getStringCellValue();
						switch (key) {
						case 1:
							dto.setBank(value);
							break;
						case 2:
							dto.setIfsc(value);
							break;
						case 3:
							dto.setMicr(value);
							break;
						case 4:
							dto.setBranch(value);
							break;
						case 5:
							dto.setAddress(value);
							break;
						case 6:
							dto.setContact(value);
							break;
						case 7:
							dto.setCity(value);
							break;
						case 8:
							dto.setDistrict(value);
							break;
						case 9:
							dto.setState(value);
							break;

						default:
							break;
						}
					}
					list.add(dto);
					if (list.size() > BATCHSIZE) {
						repo.batchSave(list);
						list.clear();
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				if (file != null)
					file.close();
			} catch (IOException e) {
				log.error("", e);
			}
		}

	}

	private void preprocess() {
		log.debug("Start");
	}
}
