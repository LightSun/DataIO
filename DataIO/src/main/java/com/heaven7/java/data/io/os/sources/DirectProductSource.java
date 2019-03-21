package com.heaven7.java.data.io.os.sources;

import com.heaven7.java.data.io.os.Producer;
import com.heaven7.java.data.io.os.Transformers;

/**
 * @author heaven7
 */
public class DirectProductSource<T> extends SimpleProductSource<T,T> {

    public DirectProductSource(Producer<T> producer) {
        super(producer);
        setTransformer(Transformers.<T>unchangeTransformer());
    }
}
