package com.heaven7.java.data.io.bean;

import com.google.gson.annotations.Expose;

/**
 * @author heaven7
 */
public class MusicMappingItem {

    @Expose
    private String filename;
    @Expose
    private String musicName;
    @Expose
    private String fullId;
    @Expose
    private String id;

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getFullId() {
        return fullId;
    }

    public void setFullId(String fullId) {
        this.fullId = fullId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
