package com.heaven7.java.data.io.temp;

import com.heaven7.java.base.util.Predicates;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author heaven7
 */
public class Person {

    public List<Number> list1;
    public List<? extends Number> list2;
    public List<Map<String, ? extends Number>> list3;

    public Map<String, Number> map1;
    public Map<String, ? super Number> map2;
    public Map<? extends Number, ? super Number> map3;

    public List<Number>[] array1;
    public List<? super Number>[] array2;

    public static void main(String[] args) throws Exception{

        GenericNode list1 = parseField("list1");
        GenericNode list2 = parseField("list2");
        GenericNode list3 = parseField("list3");
        GenericNode map1 = parseField("map1");
        GenericNode map2 = parseField("map2");
        GenericNode map3 = parseField("map3");
        GenericNode array1 = parseField("array1");
        GenericNode array2 = parseField("array2");
    }

    private static GenericNode parseField(String fieldName) throws Exception{
        Field field = Person.class.getField(fieldName);
        Type type = field.getGenericType();
        GenericNode node = new GenericNode();
        parseNodeImpl(type, node);
        return node;
    }

    private static void parseNodeImpl(Type type, GenericNode parent) {
        if(type instanceof ParameterizedType){
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            List<GenericNode> subs = new ArrayList<>();
            GenericNode node;
            for (Type t : types){
                node = new GenericNode();
                parseNodeImpl(t, node);
                subs.add(node);
            }
            parent.type = (Class<?>) ((ParameterizedType) type).getRawType();
            parent.subType = subs;
        }else if(type instanceof GenericArrayType){
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            parent.isArray = true;
            GenericNode node = new GenericNode();
            parseNodeImpl(componentType, node);
            parent.addNode(node);
        }else if(type instanceof WildcardType){
            Type[] lowerBounds = ((WildcardType) type).getLowerBounds();
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if(Predicates.isEmpty(lowerBounds)){
                parseNodeImpl(upperBounds[0], parent);
            }else {
                parseNodeImpl(lowerBounds[0], parent);
            }
        }else if(type instanceof TypeVariable){
            Type[] types = ((TypeVariable) type).getBounds();
            List<GenericNode> subs = new ArrayList<>();
            GenericNode node;
            for (Type t : types){
                node = new GenericNode();
                parseNodeImpl(t, node);
                subs.add(node);
            }
            parent.subType = subs;
        } else if(type instanceof Class){
            parent.type = (Class<?>) type;
        }else {
            throw new RuntimeException("" + type);
        }
    }
}

//List<Number> -> type = List.class, subType = GenricNode(Number)
class GenericNode{
    Class<?> type;
    List<GenericNode> subType;
    boolean isArray;

    public void addNode(GenericNode node) {
        if(subType == null){
            subType = new ArrayList<>();
        }
        subType.add(node);
    }
}
