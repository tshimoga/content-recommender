package com.recommender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

public class DataLoader {
	
private static String data = "/resources/data.xlsx";
	
	
	 List<Post> loadData() throws IOException {
			
			List<Post> dataArray = new ArrayList<Post>();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			File file = new File(classLoader.getResource(data).getPath());
			FileInputStream is = new FileInputStream(file);
			XSSFWorkbook myWorkBook = new XSSFWorkbook (is);
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			Iterator<Row> rowIterator = mySheet.iterator();
			rowIterator.next();
			 while (rowIterator.hasNext()) {
	             Row row = rowIterator.next();
	             Post post;
	             if(row.getCell(2)!=null) {
	             post = new Post(row.getCell(0).getStringCellValue(),row.getCell(1).getStringCellValue(),row.getCell(2).getStringCellValue());
	             } else {
	            	 post = new Post(row.getCell(0).getStringCellValue(),row.getCell(1).getStringCellValue(),"");
	             }
	             dataArray.add(post);
			 }
			 
			 myWorkBook.close();
			return dataArray;
			
		}

}
