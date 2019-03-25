package com.heaven7.java.data.io.os.sources;

import java.util.List;

public class DirectListSource<T> implements ListSource<T>{
        private List<T> list;
        public DirectListSource(List<T> list) {
            this.list = list;
        }
        @Override
        public List<T> getList() {
            return list;
        }
    }