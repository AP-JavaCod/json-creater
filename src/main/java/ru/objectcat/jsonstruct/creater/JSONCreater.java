package ru.objectcat.jsonstruct.creater;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import ru.objectcat.jsonstruct.JSONObject;
import ru.objectcat.jsonstruct.creater.annotation.ConstructorJSON;
import ru.objectcat.jsonstruct.creater.annotation.GetMethodJSON;
import ru.objectcat.jsonstruct.creater.annotation.SetMethodJSON;
import ru.objectcat.jsonstruct.creater.annotation.FieldJSON;
import ru.objectcat.jsonstruct.creater.annotation.TypeFieldMethod;

public class JSONCreater {

	public static JSONObject toJSON(Object object) throws IllegalAccessException, InvocationTargetException {
		JSONObject json = new JSONObject();
		Class<?> type = object.getClass();
		for(Method method : type.getDeclaredMethods()) {
			GetMethodJSON met = method.getAnnotation(GetMethodJSON.class);
			if(met != null) {
				method.setAccessible(true);
				String key = met.name();
				Object value = method.invoke(object);
				json.put(key, value);
			}
		}
		for(Field field : type.getDeclaredFields()) {
			FieldJSON ann = field.getAnnotation(FieldJSON.class);
			if(ann != null && (ann.method() == TypeFieldMethod.GET || ann.method() == TypeFieldMethod.ALL)) {
				String key = field.getName();
				field.setAccessible(true);
				Object value = field.get(object);
				json.put(key, value);
			}
		}
		return json;
	}
	
	public static <T> T toObject(JSONObject json, Class<T> type) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		T object = null;  
		for(Constructor<?> constructor : type.getConstructors()) {
			ConstructorJSON constructorJSON = constructor.getAnnotation(ConstructorJSON.class);
			if(constructorJSON != null) {
				Class<?>[] argumentsType = constructorJSON.arguments();
				String[] argumentsName = constructorJSON.names();
				Object[] values = convertArguments(json, argumentsType, argumentsName);
				object = type.getConstructor(argumentsType).newInstance(values);
				break;
			}
		}
		if(object == null) {
			object = type.getConstructor().newInstance();
		}
		for(Method method : type.getDeclaredMethods()) {
			SetMethodJSON add = method.getAnnotation(SetMethodJSON.class);
			if(add != null) {
				Class<?>[] argumentsType = add.arguments();
				String[] argumentsName = add.names();
				Object[] values = convertArguments(json, argumentsType, argumentsName);
				method.setAccessible(true);
				method.invoke(object, values);
			}
		}
		for(Field field : type.getDeclaredFields()) {
			FieldJSON ann = field.getAnnotation(FieldJSON.class);
			if(ann != null && (ann.method() == TypeFieldMethod.SET || ann.method() == TypeFieldMethod.ALL)) {
				String key = field.getName();
				Object value = json.get(key);
				field.setAccessible(true);
				field.set(object, convertJSON(value, field.getType()));
			}
		}
		return object;
	}
	
	private static Object[] convertArguments(JSONObject json, Class<?>[] types, String names[]) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int size = types.length;
		if(size != names.length) {
			throw new RuntimeException();
		}
		Object[] values = new Object[size];
		for(int i = 0; i < size; i++) {
			Class<?> type = types[i];
			String key = names[i];
			Object value = json.get(key);
			values[i] = convertJSON(value, type);
		}
		return values;
	}
	
	private static Object convertJSON(Object value, Class<?> type) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(value == null || type.isAssignableFrom(value.getClass()) || (Boolean.class.isAssignableFrom(value.getClass()) && type == boolean.class)) {
			return value;
		}else if(value instanceof Number n) {
			if(Byte.class.isAssignableFrom(type) || type == byte.class) {
				return n.byteValue();
			}else if(Short.class.isAssignableFrom(type) || type == short.class) {
				return n.shortValue();
			}else if(Integer.class.isAssignableFrom(type) || type == int.class) {
				return n.intValue();
			}else if(Long.class.isAssignableFrom(type) || type == long.class) {
				return n.longValue();
			}else if(Float.class.isAssignableFrom(type) || type == float.class) {
				return n.floatValue();
			}else if(Double.class.isAssignableFrom(type) || type == double.class) {
				return n.doubleValue();
			}
			throw new ClassCastException("Failed to convert JSON to object");
		}else if(value instanceof JSONObject j) {
			return toObject(j, type);
		}
		throw new ClassCastException("Failed to convert JSON to object");
	}
	
}
