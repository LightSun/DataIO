package com.heaven7.java.data.io.temp;

import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import org.junit.Test;

import java.util.List;

/**
 * @author heaven7
 */
public class ReadTimesFromText {

    @Test
    public void test1(){
        String input = "E:\\test\\times.txt";
        String output = "E:\\test\\times.csv";
        List<Float> list = new TextReadHelper<Float>(new TextReadHelper.Callback<Float>() {
            @Override
            public Float parse(String line) {
                return Float.parseFloat(line);
            }
        }).read(null, input);

        final StringBuilder sb = new StringBuilder();
        VisitServices.from(list).fire(new FireVisitor<Float>() {
            @Override
            public Boolean visit(Float val, Object param) {
                sb.append(val.longValue()).append(",").append(0).append("\r\n");
                return null;
            }
        });
        FileUtils.writeTo(output, sb.toString());

        //24*60*60
    }
}
