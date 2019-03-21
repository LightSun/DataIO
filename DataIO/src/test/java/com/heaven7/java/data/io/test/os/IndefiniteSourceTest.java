package com.heaven7.java.data.io.test.os;

import com.heaven7.java.data.io.os.Consumer;
import com.heaven7.java.data.io.os.Transformers;
import com.heaven7.java.data.io.os.producers.CollectionProducer;
import com.heaven7.java.data.io.os.sources.SimpleProductSource;
import com.heaven7.java.data.io.test.Schedulers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class IndefiniteSourceTest {

    @Test
    public void test1(){
        List<String> list = Arrays.asList("heaven7_1", "heaven7_2", "heaven7_3");
        SimpleProductSource<String,String> source = new SimpleProductSource<>(
                new CollectionProducer<String>(list));
        source.setScheduler(Schedulers.DEFAULT);
        source.setTransformer(Transformers.<String>unchangeTransformer());

        source.open(new TestConsumer<String>());
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static class TestConsumer<T> implements Consumer<T>{
        @Override
        public void onStart() {
            System.out.println("start -------");
        }
        @Override
        public void onConsume(T obj) {
            System.out.println("onConsume: " + obj);
        }

        @Override
        public void onEnd() {
            System.out.println("end -------");
        }
    }
}
