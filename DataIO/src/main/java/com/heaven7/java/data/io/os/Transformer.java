package com.heaven7.java.data.io.os;

/**
 * @author heaven7
 */
public interface Transformer<T,R> {

    Transformer<Object, Object> UNCHANGED = new Transformer<Object, Object>() {
        @Override
        public Object consume(SourceContext context, Object t) {
            return t;
        }
    };

    /**
     * onConsume the product
     * @param context the sources context
     * @param t the product
     * @return the result of onConsume
     */
    R consume(SourceContext context, T t);

}
