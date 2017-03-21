package com.shinemo.publish.debug;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 
 * @author figo
 * 2017年1月16日
 */
public class ClassFilter {

	private List<String> classFilters;

	public ClassFilter() {

	}

	public ClassFilter(List<String> classFilters) {
		this.classFilters = classFilters;
	}

	public ClassFilter(String classFilter) {
		classFilters = Lists.newArrayList(classFilter);
	}

	public List<String> getClassFilters() {
		return classFilters;
	}

	public void setClassFilters(List<String> classFilters) {
		this.classFilters = classFilters;
	}

}
