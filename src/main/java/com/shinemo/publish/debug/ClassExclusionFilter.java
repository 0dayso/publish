package com.shinemo.publish.debug;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 
 * @author figo
 * 2017年1月16日
 */
public class ClassExclusionFilter {

	private List<String> classExclusionFilters;

	public ClassExclusionFilter() {

	}

	public ClassExclusionFilter(List<String> classExclusionFilters) {
		this.classExclusionFilters = classExclusionFilters;
	}

	public ClassExclusionFilter(String classExclusionFilter) {
		classExclusionFilters = Lists.newArrayList(classExclusionFilter);
	}

	public List<String> getClassExclusionFilters() {
		return classExclusionFilters;
	}

	public void setClassExclusionFilters(List<String> classExclusionFilters) {
		this.classExclusionFilters = classExclusionFilters;
	}

	
}
