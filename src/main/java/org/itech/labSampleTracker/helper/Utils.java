package org.itech.labSampleTracker.helper;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Utils {
	public static HashMap<String, String> objectToMap(Object object) throws IllegalAccessException {
		HashMap<String, String> map = new HashMap<>();
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			map.put(field.getName(), field.get(object).toString());
		}
		return map;
	}

}
