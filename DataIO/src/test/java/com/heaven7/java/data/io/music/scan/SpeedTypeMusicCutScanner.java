package com.heaven7.java.data.io.music.scan;

import com.google.gson.Gson;
import com.heaven7.java.base.util.Logger;
import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.data.io.bean.CutConfigBeanV2;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 从csv文件中读出cuts同时读出speed type. often used as v2
 *
 * @author heaven7
 * @since 2
 */
public class SpeedTypeMusicCutScanner extends AbstractMusicCutScanner<CutConfigBeanV2.CutItem> {

    private static final String TAG = "SpeedMusicCutScanner";
    private final List<String> wrongFormatCsvs = new ArrayList<>();
    private final List<String> wrongInfos = new ArrayList<>();
    private final List<String> tooSmallSpeedTypeCsvs = new ArrayList<>();

    public SpeedTypeMusicCutScanner(String dir) {
        super(dir);
    }

    @Override
    protected List<String> filter(List<String> files) {
        return VisitServices.from(files).filter(new PredicateVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                if (s.endsWith("vamp_bbc-vamp-plugins_bbc-energy_rmsenergy.csv")
                        || s.endsWith("vamp_qm-vamp-plugins_qm-barbeattracker_beatsd.csv")
                        || s.endsWith("vamp_qm-vamp-plugins_qm-segmenter_segmentation.csv")) {
                    return false;
                }
                return true;
            }
        }).getAsList();
    }

    @Override
    protected CutConfigBeanV2.CutItem readCutItem(final String csvPath) {
        String dirName = FileUtils.getFileDir(csvPath, 1, false);
        if (dirName == null || !dirName.endsWith("s")) {
            throw new IllegalStateException("for version 2. csv path must used duration as direct dir. path like '30s', csvPath = " + csvPath);
        }
        final int duration;
        try {
            duration = Integer.valueOf(dirName.substring(0, dirName.length() - 1));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("for version 2. csv path must used duration as direct dir. path like '30s', csvPath = " + csvPath);
        }
        try {
            String fileName = FileUtils.getFileName(csvPath);
            final Set<Integer> speedTypes = new HashSet<>();
            TextReadHelper<CutConfigBeanV2.CutLine> readHelper = new TextReadHelper<>(
                    new TextReadHelper.Callback<CutConfigBeanV2.CutLine>() {
                        int mLastSpeedType = CutConfigBeanV2.SPEED_TYPE_UNKNOWN;
                        @Override
                        public CutConfigBeanV2.CutLine parse(String line) {
                            // return Float.valueOf(line.split(",")[0]);
                            int index = line.indexOf(",");
                            final float cut;
                            if (index == -1) {
                                cut = Float.valueOf(line.trim());
                            } else {
                                cut = Float.valueOf(line.substring(0, index).trim());
                            }
                            CutConfigBeanV2.CutLine line1 = new CutConfigBeanV2.CutLine();
                            line1.setCut(cut);
                            boolean handledSpeed = false;
                            if (index >= 0) {
                                String str = line.substring(index + 1).trim();
                                if (str.startsWith("{")) {
                                    index = str.indexOf("}");
                                    String mark = str.substring(str.indexOf("{") + 1, index);
                                    if (mark.contains(",")) {
                                        mark = mark.split(",")[0];
                                    } else if (mark.contains("-")) {
                                        mark = mark.split("-")[0];
                                    }
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

                                        default:
                                            Logger.w(TAG, "readCutItem", "wrong line in csv (" + csvPath
                                                    + "). line : " + line);
                                    }
                                }
                            }
                            if (handledSpeed) {
                                mLastSpeedType = line1.getSpeedType();
                            } else {
                                if (mLastSpeedType == CutConfigBeanV2.SPEED_TYPE_UNKNOWN) {
                                    addWrongInfo(csvPath, "no speed type. at '" + line + "'");
                                    return null;
                                }
                                line1.setSpeedType(mLastSpeedType);
                            }

                            speedTypes.add(line1.getSpeedType());
                            return line1;
                        }
                    });
            List<CutConfigBeanV2.CutLine> lines = readHelper.read(null, csvPath);
            if(lines.isEmpty()){
                return null;
            }
            CutConfigBeanV2.CutItem item = new CutConfigBeanV2.CutItem();
            item.setName(fileName);
            item.setDuration(duration);
            item.setCutLines(lines);
            if (speedTypes.size() < 2) {
                tooSmallSpeedTypeCsvs.add(csvPath);
            }
            return item;
        } catch (Exception e) {
            addWrongInfo(csvPath, null);
            new RuntimeException(csvPath, e).printStackTrace();
            return null;
        }
    }

    @Override
    protected void writeCutConfig(String targetFilePath, List<CutConfigBeanV2.CutItem> items) {
        if (wrongFormatCsvs.isEmpty()) {
            writeToFileImpl(targetFilePath, items);
        } else {
            String fileDir = FileUtils.getLastPath(targetFilePath);
            String detail = fileDir + File.separator + "wrong_csv_detail.txt";
            StringBuilder sb = listToFile(wrongInfos, detail);
            System.err.println("called [ writeCutConfig() ], all wrongs: \r\n" + sb.toString());
            //wrong csv
            String txt = fileDir + File.separator + "wrong_csv.txt";
            listToFile(wrongFormatCsvs, txt);
            //
            String low_count = fileDir + File.separator + "low_count_speed_types.txt";
            listToFile(tooSmallSpeedTypeCsvs, low_count);
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
        if (!wrongFormatCsvs.contains(csvPath)) {
            wrongFormatCsvs.add(csvPath);
        }
        wrongInfos.add(csvPath + ", " + detail);
    }

    private static StringBuilder listToFile(List<String> list, String targetFile) {
        final StringBuilder sb = new StringBuilder();
        if (!list.isEmpty()) {
            VisitServices.from(list).fire(new FireVisitor<String>() {
                @Override
                public Boolean visit(String s, Object param) {
                    sb.append(s).append(Platforms.getNewLine());
                    return null;
                }
            });
            FileUtils.writeTo(targetFile, sb.toString());
        }
        return sb;
    }

    public static void main(String[] args) {
        // String dir = "E:\\tmp\\bugfinds\\music_cuts2\\1208\\60s";
        String dir = "E:\\tmp\\bugfinds\\music_cuts2\\1212\\60s";
       // String dir = "E:\\tmp\\bugfinds\\music_cuts2\\1212_2\\60s";
        String configPath = new File(dir).getParent() + File.separator + "cuts.txt";
        new SpeedTypeMusicCutScanner(dir).serialize(configPath);
    }
}
