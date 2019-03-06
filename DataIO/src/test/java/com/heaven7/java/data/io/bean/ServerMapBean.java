package com.heaven7.java.data.io.bean;

/**
 * @author heaven7
 */
public class ServerMapBean {

    private String musicId;
    private String name;
    private String md5;
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMusicId() {
        return musicId;
    }
    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
