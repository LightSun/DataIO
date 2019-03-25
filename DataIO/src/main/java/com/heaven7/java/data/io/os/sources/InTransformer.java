package com.heaven7.java.data.io.os.sources;

public interface InTransformer<T>{
        T transform(String line);
    }