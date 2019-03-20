package com.heaven7.java.data.io.os;

/**
 * @author heaven7
 */
public interface Transformer<T,R> {

    /**
     * onConsume the product
     * @param context the source context
     * @param t the product
     * @return the result of onConsume
     */
    R consume(SourceContext context, T t);
}
