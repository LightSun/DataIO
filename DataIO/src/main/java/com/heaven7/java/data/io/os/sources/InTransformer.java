package com.heaven7.java.data.io.os.sources;

public interface InTransformer<T> {

    InTransformer<String> STRING = new InTransformer<String>() {
        @Override
        public String transform(String str) {
            return str;
        }
    };

    T transform(String str);
}