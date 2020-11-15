package com.wl4g.gateway.dao;

import org.apache.ibatis.annotations.Param;

import com.wl4g.gateway.bean.GWCluster;

import java.util.List;

public interface GWClusterDao {
	int deleteByPrimaryKey(Integer id);

	int insert(GWCluster record);

	int insertSelective(GWCluster record);

	GWCluster selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(GWCluster record);

	int updateByPrimaryKey(GWCluster record);

	List<GWCluster> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name);
}