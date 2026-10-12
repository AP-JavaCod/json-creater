package ru.objectcat.jsonstruct.creater;

import ru.objectcat.jsonstruct.JSONObject;

import java.lang.reflect.InvocationTargetException;

import ru.objectcat.jsonstruct.JSONAdapter;

public class JSONCustom implements JSONAdapter{

	@Override
	public JSONObject toJSON(){
		try {
			return JSONCreater.toJSON(this);
		}catch(IllegalAccessException | InvocationTargetException e) {
			throw new ClassCastException("Failed to convert object to JSON");
		}
	}
	
}
