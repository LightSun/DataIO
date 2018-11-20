package com.heaven7.java.data.io.music;

/**
 * @author heaven7
 */
public interface MusicCutProvider {

    /**
     * the row name of first column
     * @param rowName the row name
     * @return the cuts . like '1,2,4,6,8,9.9'
     */
    String getCuts(String rowName);
}
