package com.heaven7.java.data.io.music.in;


/**
 * @author heaven7
 */
public interface LogWriter {


    /**
     * write transfer item log. often transfer excel row to item.
     * @param transferName the transfer name
     * @param log the log
     */
    void writeTransferItem(String transferName, String log);

    /**
     * write transfer effect log. like effects-filter-transition.
     * @param transferName the transfer name
     * @param log the log
     */
    void writeTransferEffect(String transferName, String log);

    /**
     * write music not exist.
     * @param log the log
     */
    void writeMusicFileNotExist(String log);

    /**
     * start write log
     */
    void start();

    /**
     * end write log
     */
    void end();

    /**
     *  write the music name filter log
     * @param log the log
     */
    void writeMusicNameFilter(String log);
}
