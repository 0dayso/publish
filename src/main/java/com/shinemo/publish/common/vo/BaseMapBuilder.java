package com.shinemo.publish.common.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wug on 2015/12/1 0001 11:12.
 * email wug@shinemo.com
 */
public class BaseMapBuilder {

    static class Mapping {
        private String name; //展示名字
        private String propertyName; //bean property

        public String getName() {
            return name;
        }
        public String getPropertyName() {
            return propertyName;
        }

    }

    private final List<Mapping> mappingList = new ArrayList<Mapping>();

    public List<Mapping> getMappingList() {
        return mappingList;
    }

    public BaseMapBuilder addMapping(String name, String propertyName) {
        Mapping mapping = new Mapping();
        mapping.name = name;
        mapping.propertyName = propertyName;
        mappingList.add(mapping);
        return this;
    }

}
