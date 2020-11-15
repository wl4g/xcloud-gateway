package com.wl4g.gateway.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wl4g.gateway.bean.GWUpstreamGroupRef;

public interface GWUpstreamGroupRefDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByUpstreamGroupId(Integer upstreamGroupId);

    int insert(GWUpstreamGroupRef record);

    int insertBatch(@Param("gwUpstreamGroupRefs")List<GWUpstreamGroupRef> gwUpstreamGroupRefs);

    int insertSelective(GWUpstreamGroupRef record);

    GWUpstreamGroupRef selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWUpstreamGroupRef record);

    int updateByPrimaryKey(GWUpstreamGroupRef record);

    List<GWUpstreamGroupRef> getByupstreamGroupId(Integer upstreamGroupId);

}