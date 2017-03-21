/**
 * 
 */
package com.shinemo.publish.api.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shinemo.publish.utils.ThreadLocalDateUtil;

import net.sf.json.JSONArray;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * @author david
 *
 */
public class JacksonUtil {


	private static ObjectMapper mapper = new ObjectMapper();
	
	private static ObjectMapper mapperForDateYMD = new ObjectMapper();

	private static ObjectMapper mapperForDateYMDMS = new ObjectMapper();


	/**
	 * Object to string
	 * 
	 * @param obj
	 * @return
	 */
	public static String convertFrom(Object obj) throws Exception {
		if (obj == null) {
			return "";
		}

		try {
			//mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY.NON_NULL);
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			// LOG.warn("Failed to parse object to JSON. ", e);
			throw e;
		}
	}


	/**
	 * @param jsonStr
	 * @return
	 */
	public static <T> T convertFrom(String jsonStr, Class<T> clazz) throws Exception {
		if (jsonStr == null || jsonStr.isEmpty()) {
			jsonStr = "{}"; // to avoid JSON error
		}

		try {
			return mapper.readValue(jsonStr, clazz);
		} catch (Exception e) {
			// LOG.warn("Failed to parse JSON to object", e);
			throw e;
		}
	}
	
	public static String convertFromDateEndofDay(Object obj) throws Exception {
        if (obj == null) {
            return "";
        }
        try {
            mapperForDateYMD.setDateFormat(ThreadLocalDateUtil.getDateFormatForYMD());
            return mapperForDateYMD.writeValueAsString(obj);
        } catch (Exception e){
            // LOG.warn("Failed to parse object to JSON. ", e);
            throw e;
        }
    }

	public static String convertFromDateEndofSecond(Object obj) throws Exception {
		if (obj == null) {
			return "";
		}
		try {
			mapperForDateYMDMS.setDateFormat(ThreadLocalDateUtil.getDateFormat());
			return mapperForDateYMDMS.writeValueAsString(obj);
		} catch (Exception e){
			// LOG.warn("Failed to parse object to JSON. ", e);
			throw e;
		}
	}

	public static <T> List<T> convertToList(String jsonStr, Class<T> clazz) throws Exception {
		List<T> list = new ArrayList<T>();
		JSONArray array = JSONArray.fromObject(jsonStr);
		for (int i = 0; i < array.size(); i++) {
			if (clazz == String.class) {
				list.add((T) array.getString(i));
			} else {
				list.add((T) mapper.readValue(array.getString(i), clazz));
			}
		}

		return list;
	}


}
