package com.shinemo.publish.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Requests extends RequestUtils {

	public List<String> getParameters(HttpServletRequest request) {
		if (request != null) {
			Enumeration params = request.getParameterNames();
			List<String> l = new ArrayList<String>();
			while (params.hasMoreElements()) {
				String p = (String) params.nextElement();
				l.add(p);
			}
			return l;
		}
		return null;
	}

	public static Boolean getBoolean(final String paramName) {
		return getBoolean(paramName, null);
	}

	public static Boolean getBoolean(final String paramName,
			final Boolean defaultVal) {
		return Requests
				.getBooleanParameter(getRequest(), paramName, defaultVal);
	}

	public static Long getLong(final String paramName) {
		return getLong(paramName, null);
	}

	public static Long getLong(final String paramName, final Long defaultVal) {
		return Requests.getLongParameter(getRequest(), paramName, defaultVal);
	}

	public static Integer getInt(final String paramName) {
		return getInt(paramName, null);
	}

	public static Integer getInt(final String paramName, final Integer defaultVal) {
		return Requests.getIntParameter(getRequest(), paramName, defaultVal);
	}

	public static Float getFloat(final String paramName) {
		return getFloat(paramName, null);
	}

	public static Float getFloat(final String paramName, final Float defaultVal) {
		return Requests.getFloatParameter(getRequest(), paramName, defaultVal);
	}


	public static Double getDouble(final String paramName) {
		return getDouble(paramName, null);
	}

	public static Double getDouble(final String paramName,
			final Double defaultVal) {
		return Requests.getDoubleParameter(getRequest(), paramName, defaultVal);
	}


	public static String getString(final String paramName) {
		return getString(paramName, null);
	}

	public static String getString(final String paramName,
			final String defaultVal) {
		return Requests.getStringParameter(getRequest(), paramName, defaultVal);
	}

	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
	}

}
