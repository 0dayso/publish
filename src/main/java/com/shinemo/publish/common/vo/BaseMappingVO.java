package com.shinemo.publish.common.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.shinemo.publish.common.BaseDO;

/**
 * Created by wug on 2015/12/1 0001 12:02.
 * email wug@shinemo.com
 */
public class BaseMappingVO extends BaseDO {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4668860515576667958L;

	public static <DO extends BaseDO, T extends BaseMappingVO, B extends BaseMapBuilder> List<T> build(List<DO> listDO, B builder, Class<T> clazz) {
        List<T> list = new ArrayList<T>(listDO.size());
        for (DO dio : listDO) {
            list.add(build(dio, builder, clazz));
        }
        return list;
    }

    public static <DO extends BaseDO, T extends BaseMappingVO> T build(DO baseDO, BaseMapBuilder builder, Class<T> clazz) {
        T temp = null;
        try {
            temp = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (BaseMapBuilder.Mapping map : builder.getMappingList()) {
            Object value;
            try {
                value = PropertyUtils.getProperty(baseDO, map.getPropertyName());
                BeanUtils.setProperty(temp, map.getName(), value);
            } catch (Exception e) {
            }
        }
        return temp;
    }
}
