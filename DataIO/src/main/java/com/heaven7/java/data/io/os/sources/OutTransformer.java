package com.heaven7.java.data.io.os.sources;

public interface OutTransformer<T> {

   OutTransformer<Object> TO_STRING = new OutTransformer<Object>() {
        @Override
        public String transform(Object s) {
            return s.toString();
        }
    };
    OutTransformer<String> STRING = new OutTransformer<String>() {
        @Override
        public String transform(String s) {
            return s;
        }
    };
    String transform(T t);


}