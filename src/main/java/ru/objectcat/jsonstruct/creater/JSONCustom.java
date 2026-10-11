package ru.objectcat.jsonstruct.creater;

import ru.objectcat.jsonstruct.JSONObject;
import ru.objectcat.jsonstruct.JSONAdapter;

public class JSONCustom implements JSONAdapter{

	@Override
	public JSONObject toJSON() {
		try {
			return JSONCreater.toJSON(this);
		}catch(IllegalAccessException e) {
			throw new ClassCastException("Failed to convert object to JSON");
		}
	}
	
}
