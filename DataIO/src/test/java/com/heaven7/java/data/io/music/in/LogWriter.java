package com.heaven7.java.data.io.music.in;


/**
 * @author heaven7
 */
public interface LogWriter {


    void writeTransferItem(String transferName, String log);

    void writeTransferEffect(String transferName, String log);

    /**
     * write music not exist.
     * @param log the log
     */
    void writeMusicFileNotExist(String log);

}
