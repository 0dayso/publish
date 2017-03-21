package com.shinemo.publish.common.vo;

/**
 * Created by wug on 2015/12/1 0001 21:18.
 * email wug@shinemo.com
 */
public class BaseLastMappingVO extends BaseMappingVO {
    private Long lastModified;

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
}
