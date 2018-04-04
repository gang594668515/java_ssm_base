
package com.xj.b2b.back.dao;

import com.xj.b2b.back.entity.Role;
import com.xj.common.persistence.CrudDao;
import com.xj.common.persistence.annotation.MyBatisDao;

/**
 * 角色DAO接口
 * 
 * @version 2016-04-06
 */
@MyBatisDao
public interface RoleDao extends CrudDao<Role> {

	/**
	 * 根据角色名称获取角色
	 */
	public Role getByName(Role role);

	/**
	 * 根据角色英文名称获取角色
	 */
	public Role getByEnname(Role role);

	/**
	 * 维护角色与菜单权限关系 - 清空角色对应的菜单
	 */
	public int deleteRoleMenu(Role role);

	/**
	 * 维护角色与菜单权限关系 - 重新插入角色对应的菜单
	 */
	public int insertRoleMenu(Role role);

}
