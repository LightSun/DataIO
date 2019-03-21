package com.heaven7.java.data.io.os.sources;

import com.heaven7.java.data.io.os.Producer;
import com.heaven7.java.data.io.os.Transformers;

/**
 * @author heaven7
 */
public class DirectIndefiniteSource<T> extends SimpleIndefiniteSource<T,T> {

    public DirectIndefiniteSource(Producer<T> producer) {
        super(producer);
        setTransformer(Transformers.<T>unchangeTransformer());
    }
}
