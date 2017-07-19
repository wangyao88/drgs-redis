package cn.com.cis.module.drgsgroup.dao;

import java.util.List;

import cn.com.cis.module.drgsgroup.entity.TbMainoprFirst;

public interface TbMainoprFirstMapper {

	List<TbMainoprFirst> selectAllTbMainoprFirsts();
	
	List<TbMainoprFirst> selectAllDefaultMainoprFirsts();
}
