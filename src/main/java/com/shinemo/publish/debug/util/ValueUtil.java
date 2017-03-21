package com.shinemo.publish.debug.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.Field;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LongValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import com.sun.tools.jdi.ObjectReferenceImpl;

public class ValueUtil {

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String parseValue(Value value) {
		StringBuilder out = new StringBuilder();
		if (value instanceof StringReference || value instanceof IntegerValue
				|| value instanceof BooleanValue || value instanceof ByteValue
				|| value instanceof CharValue || value instanceof ShortValue
				|| value instanceof LongValue || value instanceof FloatValue
				|| value instanceof DoubleValue) {
			out.append(value);
		} else if (value instanceof ObjectReference) {
			ObjectReference obj = (ObjectReference) value;
			String type = obj.referenceType().name();
			if ("java.lang.Integer".equals(type)
					|| "java.lang.Boolean".equals(type)
					|| "java.lang.Float".equals(type)
					|| "java.lang.Double".equals(type)
					|| "java.lang.Long".equals(type)
					|| "java.lang.Byte".equals(type)
					|| "java.lang.Character".equals(type)) {

				Field f = obj.referenceType().fieldByName("value");
				out.append(obj.getValue(f));
			} else if ("java.util.Date".equals(type)) {
				Field field = obj.referenceType().fieldByName("fastTime");
				Date date = new Date(Long.parseLong("" + obj.getValue(field)));
				out.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(date));
			} else if (value instanceof ArrayReference) {
				ArrayReference ar = (ArrayReference) value;
				List<Value> values = ar.getValues();
				out.append("[");
				for (int i = 0; i < values.size(); i++) {
					if (i != 0)
						out.append(" ,");
					out.append(parse(values.get(i)));
				}
				out.append("]");
			} else {
				ObjectReferenceImpl o = ((ObjectReferenceImpl) value);
				List<Field> fields = o.referenceType().allFields();
				Map<String,String> map = new HashMap<String,String>();
				for (Field field : fields) {
					String v = parse(o.getValue(field));
					map.put(field.name(), v);
				}
				if(map.isEmpty()){
					out.append(type);
				}else{
					Map<String,Object> ret = new HashMap<String,Object>();
					ret.put("value", type);
					ret.put("detail", map);
					out.append(ret.toString());
				}
			}
		}
		return out.toString();
	}
	
	private static String parse(Value value) {
		StringBuilder out = new StringBuilder();
		if (value instanceof StringReference || value instanceof IntegerValue
				|| value instanceof BooleanValue || value instanceof ByteValue
				|| value instanceof CharValue || value instanceof ShortValue
				|| value instanceof LongValue || value instanceof FloatValue
				|| value instanceof DoubleValue) {
			out.append(value);
		} else if (value instanceof ObjectReference) {
			ObjectReference obj = (ObjectReference) value;
			String type = obj.referenceType().name();
			if ("java.lang.Integer".equals(type)
					|| "java.lang.Boolean".equals(type)
					|| "java.lang.Float".equals(type)
					|| "java.lang.Double".equals(type)
					|| "java.lang.Long".equals(type)
					|| "java.lang.Byte".equals(type)
					|| "java.lang.Character".equals(type)) {

				Field f = obj.referenceType().fieldByName("value");
				out.append(obj.getValue(f));
			} else if ("java.util.Date".equals(type)) {
				Field field = obj.referenceType().fieldByName("fastTime");
				Date date = new Date(Long.parseLong("" + obj.getValue(field)));
				out.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(date));
			} else if (value instanceof ArrayReference) {
				ArrayReference ar = (ArrayReference) value;
				List<Value> values = ar.getValues();
				out.append("[");
				for (int i = 0; i < values.size(); i++) {
					if (i != 0)
						out.append(" ,");
					out.append(parse(values.get(i)));
				}
				out.append("]");
			} else {
				out.append(type);
			}
		}
		return out.toString();
	}

}
