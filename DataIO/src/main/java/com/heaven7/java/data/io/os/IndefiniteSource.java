package com.heaven7.java.data.io.os;

/**
 * @author heaven7
 */
public interface IndefiniteSource<T, R> {

    void setSourceContext(SourceContext context);

    /**
     * open the sources with transformer and consumers
     * @param transformer the transformer
     * @param collector the consumers which used to consumers result.
     * @return true if open success.
     */
    boolean open(Transformer<T, R> transformer, Consumer<? super R> collector);

    /**
     * close the sources. this make the consumers end.
     */
    void close();
}
