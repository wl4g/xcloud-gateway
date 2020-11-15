package com.wl4g.gateway.dao;

import org.apache.ibatis.annotations.Param;

import com.wl4g.gateway.bean.GWUpstream;

import java.util.List;

public interface GWUpstreamDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWUpstream record);

    int insertSelective(GWUpstream record);

    GWUpstream selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWUpstream record);

    int updateByPrimaryKey(GWUpstream record);

    List<GWUpstream> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name);
}