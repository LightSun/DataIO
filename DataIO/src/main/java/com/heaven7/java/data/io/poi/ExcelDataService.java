package com.heaven7.java.data.io.poi;

import java.util.List;

/**
 * the excel data service . like dao service
 * @author heaven7
 *
 */
public interface ExcelDataService {

	boolean insert(ExcelRow t);
	
	int insertBatch(List<ExcelRow> t);
	
	boolean update(ExcelRow t);
	
	boolean delete(ExcelRow t);
	
	boolean delete(Object priKey);
	
	List<ExcelRow> query();
	
	List<ExcelRow> query(Object[] params);

}
