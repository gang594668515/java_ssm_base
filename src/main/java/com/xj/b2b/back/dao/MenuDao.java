
package com.xj.b2b.back.dao;

import java.util.List;

import com.xj.b2b.back.entity.Menu;
import com.xj.common.persistence.CrudDao;
import com.xj.common.persistence.annotation.MyBatisDao;

/**
 * 菜单DAO接口
 * 
 * @version 2014-05-16
 */
@MyBatisDao
public interface MenuDao extends CrudDao<Menu> {
	/**
	 * 获取指定菜单的所有子孙菜单
	 */
	public List<Menu> findByParentIdsLike(Menu menu);

	/**
	 * 获取当前用户授权菜单
	 */
	public List<Menu> findByUserId(Menu menu);

	/**
	 * 获取指定菜单的父菜单id
	 */
	public int updateParentIds(Menu menu);

	/**
	 * 更新指定菜单排序号
	 */
	public int updateSort(Menu menu);

}
