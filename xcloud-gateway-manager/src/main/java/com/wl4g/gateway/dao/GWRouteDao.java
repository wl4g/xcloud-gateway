package com.wl4g.gateway.dao;

import com.wl4g.gateway.bean.GWRoute;

public interface GWRouteDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWRoute record);

    int insertSelective(GWRoute record);

    GWRoute selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWRoute record);

    int updateByPrimaryKey(GWRoute record);
}