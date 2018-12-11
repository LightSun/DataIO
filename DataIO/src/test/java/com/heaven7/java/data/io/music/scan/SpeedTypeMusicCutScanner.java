package com.heaven7.java.data.io.music.scan;

import com.google.gson.Gson;
import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.data.io.bean.CutConfigBeanV2;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 从csv文件中读出cuts同时读出speed type. often used as v2
 * @author heaven7
 * @since 2
 */
public class SpeedTypeMusicCutScanner extends AbstractMusicCutScanner<CutConfigBeanV2.CutItem> {

    private final List<String> wrongFormatCsvs = new ArrayList<>();
    private final List<String> wrongInfos = new ArrayList<>();
    private int mLastSpeedType = CutConfigBeanV2.SPEED_TYPE_UNKNOWN;

    public SpeedTypeMusicCutScanner(String dir) {
        super(dir);
    }

    @Override
    protected CutConfigBeanV2.CutItem readCutItem(final String csvPath) {
        try {
            String fileName = FileUtils.getFileName(csvPath);
            TextReadHelper<CutConfigBeanV2.CutLine> readHelper = new TextReadHelper<>(
                    new TextReadHelper.Callback<CutConfigBeanV2.CutLine>() {
                @Override
                public CutConfigBeanV2.CutLine parse(String line) {
                   // return Float.valueOf(line.split(",")[0]);
                    int index = line.indexOf(",");
                    final float cut;
                    if(index == -1){
                        cut = Float.valueOf(line.trim());
                    }else{
                        cut = Float.valueOf(line.substring(0, index).trim());
                    }
                    CutConfigBeanV2.CutLine  line1 = new CutConfigBeanV2.CutLine();
                    line1.setCut(cut);
                    if(index >= 0) {
                        String str = line.substring(index + 1).trim();
                        boolean handledSpeed = false;
                        if (str.startsWith("{")) {
                            index = str.indexOf("}");
                            String mark = str.substring(str.indexOf("{") + 1, index);
                            mark = mark.split(",")[0];
                            switch (mark) {
                                case "低":
                                    line1.setSpeedType(CutConfigBeanV2.SPEED_TYPE_SLOW);
                                    handledSpeed = true;
                                    break;

                                case "中":
                                    line1.setSpeedType(CutConfigBeanV2.SPEED_TYPE_MIDDLE);
                                    handledSpeed = true;
                                    break;

                                case "高":
                                    line1.setSpeedType(CutConfigBeanV2.SPEED_TYPE_HIGH);
                                    handledSpeed = true;
                                    break;
                            }
                        }
                        if(handledSpeed){
                            mLastSpeedType = line1.getSpeedType();
                        }else{
                            if (mLastSpeedType == CutConfigBeanV2.SPEED_TYPE_UNKNOWN) {
                                addWrongInfo(csvPath,  "no speed type. at '" + line + "'");
                                return null;
                            }
                            line1.setSpeedType(mLastSpeedType);
                        }
                    }else{
                        if (mLastSpeedType == CutConfigBeanV2.SPEED_TYPE_UNKNOWN) {
                            addWrongInfo(csvPath,  "no speed type. at '" + line + "'");
                            return null;
                        }
                    }
                    return line1;
                }
            });
            List<CutConfigBeanV2.CutLine> lines = readHelper.read(null, csvPath);
            CutConfigBeanV2.CutItem item = new CutConfigBeanV2.CutItem();
            item.setName(fileName);
            item.setCutLines(lines);
            return item;
        }catch (Exception e){
            addWrongInfo(csvPath,  null);
            new RuntimeException(csvPath, e).printStackTrace();
            return null;
        }
    }

    @Override
    protected void writeCutConfig(String targetFilePath, List<CutConfigBeanV2.CutItem> items) {
        if(wrongFormatCsvs.isEmpty()){
            writeToFileImpl(targetFilePath, items);
        }else{
            String fileDir = FileUtils.getLastPath(targetFilePath);
            final StringBuilder sb = new StringBuilder();
            VisitServices.from(wrongInfos).fire(new FireVisitor<String>() {
                @Override
                public Boolean visit(String s, Object param) {
                    sb.append(s).append(Platforms.getNewLine());
                    return null;
                }
            });
            String detail = fileDir + File.separator + "wrong_csv_detail.txt";
            FileUtils.writeTo(detail, sb.toString());
            System.err.println("called [ writeCutConfig() ], all wrongs: \r\n" + sb.toString());

            final StringBuilder sb_wrong_files = new StringBuilder();
            VisitServices.from(wrongFormatCsvs).fire(new FireVisitor<String>() {
                @Override
                public Boolean visit(String s, Object param) {
                    sb_wrong_files.append(s).append(Platforms.getNewLine());
                    return null;
                }
            });
            String txt = fileDir + File.separator + "wrong_csv.txt";
            FileUtils.writeTo(txt, sb_wrong_files.toString());
            //TODO ignore
            writeToFileImpl(targetFilePath, items);
        }
    }

    private void writeToFileImpl(String targetFilePath, List<CutConfigBeanV2.CutItem> items) {
        CutConfigBeanV2 bean = new CutConfigBeanV2();
        bean.setCutItems(items);
        FileUtils.writeTo(targetFilePath, new Gson().toJson(bean));
    }

    private void addWrongInfo(String csvPath, String detail) {
        if(!wrongFormatCsvs.contains(csvPath)) {
            wrongFormatCsvs.add(csvPath);
        }
        wrongInfos.add(csvPath + ", " + detail);
    }

    public static void main(String[] args) {
        String dir = "E:\\tmp\\bugfinds\\music_cuts2\\1208\\60s";
        String configPath = "E:\\tmp\\bugfinds\\music_cuts2\\1208\\cuts.txt";
        new SpeedTypeMusicCutScanner(dir).serialize(configPath);
    }
}
