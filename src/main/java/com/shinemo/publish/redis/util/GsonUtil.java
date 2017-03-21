package com.shinemo.publish.redis.util;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {
	
	private static final Logger log = LoggerFactory.getLogger(GsonUtil.class);
	
	private final static Gson gson       = new GsonBuilder().create();
	
    public static <T> String fromObj2Gson(T obj, Class<T> clazz) {
        if (null == obj)
            return null;
        return gson.toJson(obj, clazz);
    }

    @SuppressWarnings("unchecked")
	public static <T> T fromGson2Obj(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json))
            return null;
        String className = clazz.getSimpleName();
        String stringName = String.class.getSimpleName();
        if(className.equals(stringName)){
        	return (T) String.valueOf(json);
        }
        return gson.fromJson(json, clazz);
    }

    public static <T> String fromObj2Gson(T obj, Type t) {
        if (null == obj)
            return null;
        return gson.toJson(obj, t);
    }
    
    public static <T> List<T> fromJsonToList(String json, Class<T[]> type) {
        try {
            T[] list = gson.fromJson(json, type);
            if (list == null) return null;
            return Arrays.asList(list);
        } catch (Exception e) {
            log.error("Jsons.fromJsonToList ex, json=" + json + ", type=" + type, e);
        }
        return null;
    }
    

    public static <T> T fromGson2Obj(String json, Type type) {
        if (StringUtils.isBlank(json))
            return null;
        return gson.<T> fromJson(json, type);
    }
    
	public static Map<String,Object> getJsonMap(String jsonStr){
		Map<String,Object> paramMap = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(jsonStr)){
			Type typeOfT = new TypeToken<Map<String,Object>>(){}.getType();
			paramMap =gson.fromJson(jsonStr, typeOfT);
		}
		return paramMap;
	}
	
	
	public static String toJson(Object object){
		if(object instanceof String){
			return (String)object;
		}
		return gson.toJson(object);
	}
	
}