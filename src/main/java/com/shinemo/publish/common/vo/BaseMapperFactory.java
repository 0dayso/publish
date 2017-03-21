package com.shinemo.publish.common.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

import org.apache.commons.lang.StringUtils;

import com.shinemo.publish.common.BaseDO;
import com.shinemo.publish.utils.DateUtils;

public class BaseMapperFactory {

    public static MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    static {
        mapperFactory.getConverterFactory().registerConverter(new String2DateConverter());

    }

    public static <DO extends BaseDO, VO extends BaseDO> List<VO> build(List<DO> listDO, Class<VO> clazz) {
        List<VO> list = new ArrayList<VO>(listDO.size());
        for (DO dio : listDO) {
            list.add(build(dio, clazz));
        }
        return list;
    }

    public static <D, V> List<V> buildBase(List<D> listDO, Class<V> clazz) {
        List<V> list = new ArrayList<V>(listDO.size());
        for (D dio : listDO) {
            list.add(build(dio, clazz));
        }
        return list;
    }

    public static <DO extends BaseDO, VO extends BaseDO> VO build(DO baseDO, Class<VO> clazz) {

        VO vo = getMapperFacade().map(baseDO, clazz);

        return vo;

    }

    public static <D, V> V build(D baseDO, Class<V> clazz) {
        V vo = getMapperFacade().map(baseDO, clazz);
        return vo;
    }

    public static MapperFacade getMapperFacade() {
        MapperFacade mapperFacade = mapperFactory.getMapperFacade();
        return mapperFacade;
    }


    public static class String2DateConverter extends CustomConverter<String, Date> {

        @Override
        public Date convert(String source, Type<? extends Date> destinationType) {

            if (StringUtils.isNotBlank(source)) {

                return DateUtils.format(source);

            }

            return null;
        }

    }

}
