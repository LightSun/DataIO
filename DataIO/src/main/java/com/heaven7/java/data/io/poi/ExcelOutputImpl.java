package com.heaven7.java.data.io.poi;

import java.util.List;

/*public*/ class ExcelOutputImpl implements ExcelOutput {

	private static final String TAG = "ExcelOutputImpl";
	private final ExcelDataService mOut;
	
	public ExcelOutputImpl(ExcelDataService mOut) {
		super();
		this.mOut = mOut;
	}


	@Override
	public void writeBatch(List<ExcelRow> rows) {
		
		int changed = mOut.insertBatch(rows);
		System.out.println("[ " + TAG +" ] called writeBatch(): changed count = " + changed);
		
		/*VisitServices.from(rows).fire(null, new FireVisitor<ExcelRow>() {
			@Override
			public Boolean visit(ExcelRow t, Object param) {
				if(!mOut.insert(t)){
					sLOG.debug("insert failed: row index = " + t.getRowIndex() + " , columns = " + t.getColumns());
				}
				return null;
			}
		}, null);*/
	}


	@Override
	public void write(ExcelRow t) {
		if(!mOut.insert(t)){
			System.out.println("[ " + TAG +" ] called write():  insert failed:  row index =  " + t.getRowIndex() + " , columns = " + t.getColumns());
		}
	}

}
