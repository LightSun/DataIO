package com.heaven7.java.data.io.music.provider;

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


    class DefaultMusicCutProvider implements MusicCutProvider{

        private final String cuts;

        public DefaultMusicCutProvider(String cuts) {
            this.cuts = cuts;
        }
        @Override
        public String getCuts(String rowName) {
            return cuts;
        }
    }
}
