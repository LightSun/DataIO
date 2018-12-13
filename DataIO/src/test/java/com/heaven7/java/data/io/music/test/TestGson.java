package com.heaven7.java.data.io.music.test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.bind.TypeAdapters;
import org.junit.Test;

/** @author heaven7 */
public class TestGson {

    @Test
    public void testInt() {
        int a = 1;
        System.out.println(new Gson().toJson(a));
        Object b = 2;
        System.out.println(b.getClass().getName());
    }

    @Test
    public void testIntArray() {
        int[] a = {1, 2};
        System.out.println(new Gson().toJson(a));
    }

    @Test
    public void testNull() {
        System.out.println(new Gson().toJson(null).length()); // "null"
        System.out.println((1<<8) + 3);
        System.out.println(259 >> 8);
        System.out.println(Byte.MAX_VALUE); //127. key. value
        // {"":"","":""}

        System.out.println(new JsonParser().parse("1").getClass());
        Integer int1 = new Gson().fromJson("1", int.class);     //ok
        Integer int2 = new Gson().fromJson("1", Integer.class); //ok

    }

    @Test
    public void test3() {
        JsonElement je = TypeAdapters.NUMBER.toJsonTree(1);
        System.out.println(je.getClass());
    }
}
