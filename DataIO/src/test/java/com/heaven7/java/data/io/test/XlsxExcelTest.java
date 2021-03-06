package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.music.adapter.ExcelToJsonAdapterV1;
import com.heaven7.java.data.io.poi.ExcelDataServiceAdapter;
import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelRow;
import org.junit.Test;

import java.util.List;

import static com.heaven7.java.data.io.test.ExcelTest.fire;

/**
 * @author heaven7
 */
public class XlsxExcelTest {

    // private static final String PATH =
    // "E:\\study\\github\\research-institute\\python\\Py_work\\Lib\\site-packages\\pandas\\tests\\io\\data\\test1.xlsx";
    private static final String PATH = "E:\\tmp\\bugfinds\\music2.xlsx";

@Test
public void testRead(){
        new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setSheetIndex(0)
                .setExcelPath(PATH)
                .setSkipToRowIndex(2)
                .build()
                .readAndWrite(new ExcelDataServiceAdapter() {
            @Override
            public int insertBatch(List<ExcelRow> t) {
                fire(t);
                return t.size();
            }
        });
}

@Test
@SuppressWarnings({"deprecation"})
public void testReadAndWriteToJson(){
        String outDir = "E:\\tmp\\bugfinds";
        String warnFile = "E:\\tmp\\bugfinds\\music2_warn.txt";
        String cuts =
                "0.000000,"
                        + "4.992000,"
                        + "7.454000,"
                        + "9.880000,"
                        + "12.330000,"
                        + "14.780000,"
                        + "17.229000,"
                        + "19.679000,"
                        + "23.975000,"
                        + "25.797000,"
                        + "28.247000,"
                        + "30.697000,"
                        + "33.146000,"
                        + "35.596000,"
                        + "40.496000,"
                        + "43.549000,"
                        + "45.395000,"
                        + "48.448000,"
                        + "50.294000,"
                        + "53.348000,"
                        + "55.182000,"
                        + "57.632000,"
                        + "60.093000,"
                        + "64.981000,"
                        + "67.431000,"
                        + "70.519000,"
                        + "73.549000,"
                        + "77.845000,"
                        + "79.679000,"
                        + "83.963000,"
                        + "87.028000,"
                        + "89.478000,"
                        + "92.532000,"
                        + "94.366000,"
                        + "96.816000,"
                        + "100.496000,"
                        + "104.165000,"
                        + "106.022000,"
                        + "109.076000,"
                        + "110.922000,"
                        + "113.975000,"
                        + "116.413000,"
                        + "118.875000,"
                        + "121.313000,"
                        + "124.006000,"
                        + "126.015000,"
                        + "128.662000,"
                        + "131.111000,"
                        + "133.561000,"
                        + "136.011000,"
                        + "140.899000,"
                        + "143.348000,"
                        + "145.798000,"
                        + "148.863000,"
                        + "150.698000,"
                        + "152.532000,"
                        + "155.597000,"
                        + "158.047000,"
                        + "160.496000,"
                        + "162.331000,"
                        + "165.384000,"
                        + "167.845000,"
                        + "170.295000,"
                        + "175.183000,"
                        + "177.644000,"
                        + "180.082000,"
                        + "182.567000,"
                        + "187.954000,"
                        + "190.369000,"
                        + "193.387000,"
                        + "195.600000";
    ExcelToJsonAdapterV1 adapter = new ExcelToJsonAdapterV1(outDir, "music2",cuts);
    new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setSheetIndex(0)
                .setExcelPath(PATH)
                .setSkipToRowIndex(2)
                .build()
                .readAndWrite(adapter);
}
}
