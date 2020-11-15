package com.wl4g.gateway.dao;

import org.apache.ibatis.annotations.Param;

import com.wl4g.gateway.bean.GWCluster;
import com.wl4g.gateway.bean.GWUpstreamGroup;

import java.util.List;

public interface GWUpstreamGroupDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWUpstreamGroup record);

    int insertSelective(GWUpstreamGroup record);

    GWUpstreamGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWUpstreamGroup record);

    int updateByPrimaryKey(GWUpstreamGroup record);

    List<GWUpstreamGroup> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name);
}