package com.heaven7.java.data.io.os;

/**
 * @author heaven7
 */
public interface IndefiniteSource<T, R> {

    void setSourceContext(SourceContext context);

    /**
     * open the source with transformer and collector
     * @param transformer the transformer
     * @param collector the collector which used to collector result.
     * @return true if open success.
     */
    boolean open(Transformer<T, R> transformer, Consumer<? super R> collector);

    /**
     * close the source. this make the collector end.
     */
    void close();
}
