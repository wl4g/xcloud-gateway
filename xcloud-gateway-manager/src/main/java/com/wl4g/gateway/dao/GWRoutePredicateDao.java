package com.wl4g.gateway.dao;

import com.wl4g.gateway.bean.GWRoutePredicate;

public interface GWRoutePredicateDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWRoutePredicate record);

    int insertSelective(GWRoutePredicate record);

    GWRoutePredicate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWRoutePredicate record);

    int updateByPrimaryKey(GWRoutePredicate record);
}