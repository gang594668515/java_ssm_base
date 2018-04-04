
package com.xj.b2b.back.dao;

import com.xj.b2b.back.entity.User;
import com.xj.common.persistence.CrudDao;
import com.xj.common.persistence.annotation.MyBatisDao;

/**
 * 用户DAO接口
 * 
 * @version 2014-05-16
 */
@MyBatisDao
public interface UserDao extends CrudDao<User> {

	/**
	 * 根据登录名称查询用户
	 */
	public User getByLoginName(User user);

	/**
	 * 查询全部用户数目
	 */
	public long findAllCount(User user);

	/**
	 * 用户注册
	 */
	public int insertReg(User user);

	/**
	 * 更新角色字符串
	 */
	public int updateRoleNames(User user);

	/**
	 * 更新账户余额
	 */
	public int updateBalance(User user);

	/**
	 * 更新用户信息
	 */
	public int updateUserInfo(User user);

	/**
	 * 删除用户角色关联数据
	 */
	public int deleteUserRole(User user);

	/**
	 * 插入用户角色关联数据
	 */
	public int insertUserRole(User user);

	/**
	 * 更新用户密码
	 */
	public int updatePasswordById(User user);

	/**
	 * 更新登录信息，如：登录IP、登录时间
	 */
	public int updateLoginInfo(User user);

	/**
	 * 还原用户
	 */
	public int revertUser(User user);

	/**
	 * 更新用户找回密码时的验证码
	 */
	public int updateValidCode(User user);

	/**
	 * 重置用户密码
	 */
	public int resetUserPwd(User user);

}
