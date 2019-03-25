package com.heaven7.java.data.io.os.sources;

import com.heaven7.java.data.io.os.Producer;
import com.heaven7.java.data.io.os.Transformers;

/**
 * @author heaven7
 */
public class DirectProductManager<T> extends SimpleProductManager<T,T> {

    public DirectProductManager(Producer<T> producer) {
        super(producer);
        setTransformer(Transformers.<T>unchangeTransformer());
    }
}
