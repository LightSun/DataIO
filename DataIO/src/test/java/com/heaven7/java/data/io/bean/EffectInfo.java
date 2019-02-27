package com.heaven7.java.data.io.bean;

/**
 * @author heaven7
 */
public class EffectInfo {

    public static final int NONE = -1;
    public static final int NAME_TYPE_LOW_SCORE     = 1;
    public static final int NAME_TYPE_MIDDLE_SCORE  = 2;
    public static final int NAME_TYPE_HIGH_SCORE    = 3;

    public static final int CATEGORY_LOW_AREA    = 11;
    public static final int CATEGORY_MIDDLE_AREA = 12;
    public static final int CATEGORY_HIGH_AREA   = 13;

    private int nameType;
    private int nameCategory;

    private String effect;

    public int getNameType() {
        return nameType;
    }
    public void setNameType(int nameType) {
        this.nameType = nameType;
    }

    public int getNameCategory() {
        return nameCategory;
    }
    public void setNameCategory(int nameCategory) {
        this.nameCategory = nameCategory;
    }

    public String getEffect() {
        return effect;
    }
    public void setEffect(String effect) {
        this.effect = effect;
    }
}
