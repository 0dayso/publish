package com.shinemo.publish.common;

import java.util.List;

public interface Mapper<Q,D> {

    public List<D> find(Q query);

    public D get(Q query);

    public long count(Q query);

    public long insert(D tempDO);

    public int update(D tempDO);

    public int delete(Q query);

    public int deleteAll();

}
