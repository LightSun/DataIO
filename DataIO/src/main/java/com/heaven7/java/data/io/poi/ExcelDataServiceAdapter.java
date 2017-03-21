package com.heaven7.java.data.io.poi;

import java.util.List;

public  abstract class ExcelDataServiceAdapter implements ExcelDataService{

	@Override
	public boolean insert(ExcelRow t) {
		return false;
	}

	@Override
	public int insertBatch(List<ExcelRow> t) {
		return 0;
	}

	@Override
	public boolean update(ExcelRow t) {
		return false;
	}

	@Override
	public boolean delete(ExcelRow t) {
		return false;
	}

	@Override
	public boolean delete(Object priKey) {
		return false;
	}

	@Override
	public List<ExcelRow> query() {
		return null;
	}

	@Override
	public List<ExcelRow> query(Object[] params) {
		return null;
	}

}
