package com.shinemo.publish.client;

import java.util.Locale;

import com.shinemo.Aace.appcenter.util.Language;

public enum EnvEnum {
	
	XM(-1,"讯盟"),
	UB(0, "优办"),
	CY(1, "彩云"),
	MS(2, "麻绳"),
	XW(3, "小沃");
	
	public static final int TYPE_LENGTH = 1;
	
	private int env;
	private String name;
	
	
	private EnvEnum(int env, String name) {
		this.env = env;
		this.name = name;
	}
	
	public static String getName(int env) {
		for (EnvEnum ae : EnvEnum.values()) {
			if (ae.getEnv() == env) {
				return ae.getName();
			}
		}
		return "";
	}
	

	public int getEnv() {
		return env;
	}
	

	public String getName() {
		return name;
	}
	


}
