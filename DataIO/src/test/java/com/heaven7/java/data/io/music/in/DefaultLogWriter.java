package com.heaven7.java.data.io.music.in;

/**
 * @author heaven7
 */
public class DefaultLogWriter implements LogWriter {

    public static final DefaultLogWriter INSTANCE = new DefaultLogWriter();

    @Override
    public void writeTransferItem(String transferName, String log) {

    }

    @Override
    public void writeTransferEffect(String transferName, String log) {

    }

    @Override
    public void writeMusicFileNotExist(String log) {

    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }

    @Override
    public void writeMusicNameFilter(String log) {

    }
}
