package com.heaven7.java.data.io.os;

/**
 * @author heaven7
 */
public interface IndefiniteSource<T, R> {

    void setSourceContext(SourceContext context);
    SourceContext getSourceContext();

    void setScheduler(Scheduler scheduler);
    Scheduler getScheduler();

    void setTransformer(Transformer<? super T, R> transformer);
    Transformer<? super T, R> getTransformer();

    /**
     * open the sources with transformer and consumers
     * @param collector the consumers which used to consumer result.
     * @return true if open success.
     */
    boolean open(Consumer<? super R> collector);

    /**
     * close the sources. this make the consumers end.
     */
    void close();
}
