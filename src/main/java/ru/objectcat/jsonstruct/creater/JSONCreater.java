package ru.objectcat.jsonstruct.creater;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import ru.objectcat.jsonstruct.JSONObject;

public class JSONCreater {

	public static JSONObject toJSON(Object object) throws IllegalAccessException {
		JSONObject json = new JSONObject();
		Class<?> type = object.getClass();
		for(Field field : type.getDeclaredFields()) {
			FieldJSON ann = field.getAnnotation(FieldJSON.class);
			if(ann != null) {
				String key = field.getName();
				field.setAccessible(true);
				Object value = field.get(object);
				json.put(key, value);
			}
		}
		return json;
	}
	
	public static <T> T toObject(JSONObject json, Class<T> type) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		T object = type.getDeclaredConstructor().newInstance();
		for(Field field : type.getDeclaredFields()) {
			FieldJSON ann = field.getAnnotation(FieldJSON.class);
			if(ann != null) {
				String key = field.getName();
				Class<?> typeField = field.getType();
				Object value = json.get(key);
				field.setAccessible(true);
				if(typeField.isAssignableFrom(value.getClass()) || typeField == boolean.class) {
					field.set(object, value);
				}else if(value instanceof Number n) {
					if(Byte.class.isAssignableFrom(typeField) || typeField == byte.class) {
						field.set(object, n.byteValue());
					}else if(Short.class.isAssignableFrom(typeField) || typeField == short.class) {
						field.set(object, n.shortValue());
					}else if(Integer.class.isAssignableFrom(typeField) || typeField == int.class) {
						field.set(object, n.intValue());
					}else if(Long.class.isAssignableFrom(typeField) || typeField == long.class) {
						field.set(object, n.longValue());
					}else if(Float.class.isAssignableFrom(typeField) || typeField == float.class) {
						field.set(object, n.floatValue());
					}else if(Double.class.isAssignableFrom(typeField) || typeField == double.class) {
						field.set(object, n.doubleValue());
					}else {
						throw new ClassCastException("Failed to convert JSON to object");
					}
				}else if(value instanceof JSONObject j){
					Object val = toObject(j, typeField);
					field.set(object, val);
				}else {
					throw new ClassCastException("Failed to convert JSON to object");
				}
			}
		}
		return object;
	}
	
}
