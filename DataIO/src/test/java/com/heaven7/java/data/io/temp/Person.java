package com.heaven7.java.data.io.temp;

import com.heaven7.java.base.util.Predicates;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author heaven7
 */
public class Person<E> {

    public List<Number> list1;
    public List<? extends Number> list2;
    public List<Map<String, ? extends Number>> list3;

    public Map<String, Number> map1;
    public Map<String, ? super Number> map2;
    public Map<? extends Number, ? super Number> map3;

    public List<Number>[] array1;
    public List<? super Number>[] array2;

    public List<E> params; //类上的泛型只支持单泛型

    public static void main(String[] args) throws Exception{

        GenericNode list1 = parseField("list1");
        GenericNode list2 = parseField("list2");
        GenericNode list3 = parseField("list3");
        GenericNode map1 = parseField("map1");
        GenericNode map2 = parseField("map2");
        GenericNode map3 = parseField("map3");
        GenericNode array1 = parseField("array1");
        GenericNode array2 = parseField("array2");

        Student person = new Student();
        //
        GenericNode node = parseField(person, "params");
    }

    private static GenericNode parseField(String fieldName) throws Exception{
        Field field = Person.class.getField(fieldName);
        Type type = field.getGenericType();
        GenericNode node = new GenericNode();
        parseNodeImpl(null, type, node);
        return node;
    }
    private static GenericNode parseField(Person<?> p, String fieldName) throws Exception{
        Field field = p.getClass().getField(fieldName);
        Type type = field.getGenericType();
        GenericNode node = new GenericNode();
        parseNodeImpl(p.getClass(), type, node);
        return node;
    }

    private static void parseNodeImpl(Class clazz,Type type, GenericNode parent) {
        if(type instanceof ParameterizedType){
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            List<GenericNode> subs = new ArrayList<>();
            GenericNode node;
            for (Type t : types){
                node = new GenericNode();
                parseNodeImpl(clazz, t, node);
                subs.add(node);
            }
            parent.type = (Class<?>) ((ParameterizedType) type).getRawType();
            parent.subType = subs;
        }else if(type instanceof GenericArrayType){
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            parent.isArray = true;
            GenericNode node = new GenericNode();
            parseNodeImpl(clazz, componentType, node);
            parent.addNode(node);
        }else if(type instanceof WildcardType){
            Type[] lowerBounds = ((WildcardType) type).getLowerBounds();
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if(Predicates.isEmpty(lowerBounds)){
                parseNodeImpl(clazz, upperBounds[0], parent);
            }else {
                parseNodeImpl(clazz, lowerBounds[0], parent);
            }
        }else if(type instanceof TypeVariable){
            //indicates  Wildcard from object. that means only can be known as runtime.
            //TypeVariable<?>[] types = ((TypeVariable) type).getGenericDeclaration().getTypeParameters();
            ParameterizedType pt = (ParameterizedType) clazz.getGenericSuperclass();
            if(pt == null){
                throw new UnsupportedOperationException("must extend the generic super class");
            }
            if(pt.getActualTypeArguments().length > 1){
                //or else can't map them.
                throw new UnsupportedOperationException("generic class. can only have one.");
            }
            parent.declareName = type.toString();
            parseNodeImpl(clazz, pt.getActualTypeArguments()[0], parent);
        } else if(type instanceof Class){
            parent.type =(Class<?>) type;
        }else {
            throw new RuntimeException("" + type);
        }
    }
}

//List<Number> -> type = List.class, subType = GenericNode(Number)
class GenericNode{
    Class<?> type;
    String declareName;
    List<GenericNode> subType;
    boolean isArray;

    public void addNode(GenericNode node) {
        if(subType == null){
            subType = new ArrayList<>();
        }
        subType.add(node);
    }
}
//Person<String, Integer>
class Student extends Person<String>{

}

