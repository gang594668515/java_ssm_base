
package com.xj.b2b.back.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.xj.b2b.back.dao.MenuDao;
import com.xj.b2b.back.dao.RoleDao;
import com.xj.b2b.back.dao.UserDao;
import com.xj.b2b.back.entity.Menu;
import com.xj.b2b.back.entity.Role;
import com.xj.b2b.back.entity.User;
import com.xj.b2b.back.security.SystemAuthorizingRealm.Principal;
import com.xj.b2b.back.utils.LogUtils;
import com.xj.b2b.back.utils.UserUtils;
import com.xj.b2b.front.utils.EmailUtils;
import com.xj.common.config.Global;
import com.xj.common.persistence.Page;
import com.xj.common.security.Digests;
import com.xj.common.security.shiro.session.CacheSessionDAO;
import com.xj.common.service.BaseService;
import com.xj.common.service.ServiceException;
import com.xj.common.utils.CacheUtils;
import com.xj.common.utils.Encodes;
import com.xj.common.utils.FileUtils;
import com.xj.common.utils.StringUtils;
import com.xj.common.web.Servlets;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 * 
 * @author yangb
 * @version 2016年4月2日
 */
@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService implements InitializingBean {

	public static final String HASH_ALGORITHM = Digests.SHA256;
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;

	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;

	@Autowired
	private CacheSessionDAO cacheSessionDAO;

	public CacheSessionDAO getSessionDao() {
		return cacheSessionDAO;
	}

	/**
	 * 获取所有Session
	 * 
	 * @param includeLeave
	 *            是否包括离线（最后访问时间大于3分钟为离线会话）
	 */
	public Collection<Session> getAllSession(boolean includeLeave) {
		return getSessionDao().getActiveSessions(includeLeave);
	}

	/**
	 * 获取指定用户Session
	 * 
	 * @param user
	 */
	public Collection<Session> getUserSession(User user) {
		Principal principal = new Principal(user, false);
		return getSessionDao().getActiveSessions(true, principal, null);
	}

	/**
	 * 踢出指定用户Session
	 * 
	 * @param user
	 * @param mobileLogin
	 */
	public void delUserSession(User user) {
		Principal principal = new Principal(user, false);
		Collection<Session> sessions = getSessionDao().getActiveSessions(true, principal, UserUtils.getSession());

		for (Session session : sessions) {
			logger.debug("<踢出用户> ID: {}, lastAccessTime: {}",
					session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY), session.getLastAccessTime());
			getSessionDao().delete(session);
		}
	}

	// @Autowired
	// private IdentityService identityService;

	// -- User Service --//
	/**
	 * 更新用户账户余额
	 * 
	 * @param user
	 */
	@Transactional(readOnly = false)
	public void updateUserBalance(User user) {
		user.preUpdate();
		userDao.updateBalance(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
	}

	/**
	 * 根据ID获取用户
	 * 
	 * @param id
	 * @return
	 */
	public User getUser(String id) {
		return UserUtils.get(id);
	}

	/**
	 * 用户登录名校验
	 */
	public String checkLoginName(String oldLoginName, String loginName) {
		if (loginName != null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName != null && UserUtils.getByLoginName(loginName) == null) {
			return "true";
		}
		return "false";
	}

	public Page<User> findUser(Page<User> page, User user) {
		// 设置分页参数
		user.setPage(page);
		// 执行分页查询
		page.setList(userDao.findList(user));
		return page;
	}

	/**
	 * 重置用户密码
	 * 
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = false)
	public int resetUserPwd(User user) {
		int n = userDao.resetUserPwd(user);
		UserUtils.clearCache(user);
		return n;
	}

	/**
	 * 修改用户验证码
	 * 
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = false)
	public int updateValidCode(User user) {
		int n = userDao.updateValidCode(user);
		UserUtils.clearCache(user);
		return n;
	}

	/**
	 * 无分页查询人员列表
	 * 
	 * @param user
	 * @return
	 */
	public List<User> findUser(User user) {
		List<User> list = userDao.findList(user);
		return list;
	}

	@Transactional(readOnly = false)
	public void saveUser(User user) {
		if (StringUtils.isBlank(user.getId())) {
			user.setLoginName(user.getLoginName().toLowerCase()); // 登录名小写
			// 校验登录名
			if (!UserUtils.verifyLoginName(user.getLoginName())) {
				throw new ServiceException("登录名 [" + user.getLoginName() + "] 校验失败！");
			}
			user.setIsNewRecord(true); // 使用自定义ID
			user.preInsert();
			userDao.insert(user);
		} else {
			// 更新用户数据
			user.preUpdate();
			userDao.update(user);
		}
		if (StringUtils.isNotBlank(user.getId())) {
			// 更新用户与角色关联
			userDao.deleteUserRole(user);
			if (user.getRoleList() != null && user.getRoleList().size() > 0) {
				userDao.insertUserRole(user);
			} else {
				throw new ServiceException(user.getLoginName() + "未设置有效角色！");
			}
			// // 将当前用户同步到Activiti
			// saveActivitiUser(user);
			// 清除用户缓存
			UserUtils.clearCache(user);
		}
	}

	@Transactional(readOnly = false)
	public void regUser(User user) {
		user.setLoginName(user.getLoginName().toLowerCase()); // 登录名小写
		// 校验登录名
		if (!UserUtils.verifyLoginName(user.getLoginName())) {
			throw new ServiceException("登录名 [" + user.getLoginName() + "] 校验失败！");
		}
		// 设置默认角色为“注册用户”
		List<Role> roleList = Lists.newArrayList();
		Role role = getRoleByEnname("login");
		roleList.add(role);
		user.setRoleList(roleList);
		user.setIsNewRecord(true); // 使用自定义ID
		user.preInsert();
		userDao.insertReg(user);
		userDao.insertUserRole(user);
	}

	@Transactional(readOnly = false)
	public void updateUserInfo(User user) {
		user.preUpdate();
		userDao.updateUserInfo(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
	}

	@Transactional(readOnly = false)
	public void deleteUser(User user) {
		userDao.delete(user);
		// 清除用户缓存
		UserUtils.clearCache(user);

		// 踢出Session
		delUserSession(user);
	}

	@Transactional(readOnly = false)
	public void revertUser(User user) {
		userDao.revertUser(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
	}

	@Transactional(readOnly = false)
	public void updatePasswordById(String id, String loginName, String newPassword) {
		User user = new User(id);
		user.setPassword(entryptPassword(newPassword));
		userDao.updatePasswordById(user);
		// 清除用户缓存
		user.setLoginName(loginName);
		UserUtils.clearCache(user);
	}

	@Transactional(readOnly = false)
	public void updateUserLoginInfo(User user) {
		// 保存上次登录信息
		user.setOldLoginIp(user.getLoginIp());
		user.setOldLoginDate(user.getLoginDate());
		// 更新本次登录信息
		user.setLoginIp(StringUtils.getRemoteAddr(Servlets.getRequest()));
		user.setLoginDate(new Date());
		userDao.updateLoginInfo(user);
	}

	/**
	 * 生成安全的密码，生成随机的 SALT_SIZE * 2 位salt并经过 HASH_INTERATIONS 次 HASH_ALGORITHM
	 */
	public static String entryptPassword(String plainPassword) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha(plain.getBytes(), HASH_ALGORITHM, salt, HASH_INTERATIONS);
		return Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword);
	}

	/**
	 * 验证密码
	 * 
	 * @param plainPassword
	 *            明文密码
	 * @param password
	 *            密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Encodes.decodeHex(password.substring(0, SALT_SIZE * 2));
		byte[] hashPassword = Digests.sha(plain.getBytes(), HASH_ALGORITHM, salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword));
	}

	/**
	 * 获得活动会话
	 * 
	 * @return
	 */
	public Collection<Session> getActiveSessions() {
		return cacheSessionDAO.getActiveSessions(false);
	}

	// -- Role Service --//

	public Role getRole(String id) {
		return UserUtils.getRole(id);
	}

	public Role getRoleByName(String name) {
		return UserUtils.getRoleByName(name);
	}

	public Role getRoleByEnname(String enname) {
		return UserUtils.getRoleByEnname(enname);
	}

	public List<Role> findRole(Role role) {
		return roleDao.findList(role);
	}

	public List<Role> findAllRole() {
		return UserUtils.getRoleList();
	}

	@Transactional(readOnly = false)
	public void saveRole(Role role) {
		if (StringUtils.isBlank(role.getId())) {
			role.setIsNewRecord(true); // 使用自定义ID
			role.preInsert();
			roleDao.insert(role);
		} else {
			role.preUpdate();
			roleDao.update(role);
		}
		// 更新角色与菜单关联
		roleDao.deleteRoleMenu(role);
		if (role.getMenuList().size() > 0) {
			roleDao.insertRoleMenu(role);
		}

		// 清除用户角色缓存
		UserUtils.clearCache(role);
	}

	@Transactional(readOnly = false)
	public void deleteRole(Role role) {
		roleDao.delete(role);

		// 清除用户角色缓存
		UserUtils.clearCache(role);
	}

	@Transactional(readOnly = false)
	public Boolean outUserInRole(Role role, User user) {
		List<Role> roles = user.getRoleList();
		for (Role e : roles) {
			if (e.getId().equals(role.getId())) {
				roles.remove(e);
				saveUser(user);
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = false)
	public User assignUserToRole(Role role, User user) {
		if (user == null) {
			return null;
		}
		List<Role> roleList = user.getRoleList();
		if (roleList.contains(role)) {
			return null;
		}
		roleList.add(role);
		user.setRoleList(roleList);
		saveUser(user);
		return user;
	}

	// -- Menu Service --//

	public Menu getMenu(String id) {
		return menuDao.get(id);
	}

	public List<Menu> findAllMenu() {
		return UserUtils.getMenuList();
	}

	@Transactional(readOnly = false)
	public void saveMenu(Menu menu) {

		// 获取父节点实体
		menu.setParent(this.getMenu(menu.getParent().getId()));

		// 获取修改前的parentIds，用于更新子节点的parentIds
		String oldParentIds = menu.getParentIds();

		// 设置新的父节点串
		menu.setParentIds(menu.getParent().getParentIds() + menu.getParent().getId() + ",");

		// 保存或更新实体
		if (StringUtils.isBlank(menu.getId())) {
			menu.setIsNewRecord(true); // 使用自定义ID
			menu.preInsert();
			menuDao.insert(menu);
		} else {
			menu.preUpdate();
			menuDao.update(menu);
		}

		// 更新子节点 parentIds
		Menu m = new Menu();
		m.setParentIds("%," + menu.getId() + ",%");
		List<Menu> list = menuDao.findByParentIdsLike(m);
		for (Menu e : list) {
			e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
			menuDao.updateParentIds(e);
		}
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}

	@Transactional(readOnly = false)
	public void updateMenuSort(Menu menu) {
		menuDao.updateSort(menu);
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}

	@Transactional(readOnly = false)
	public void deleteMenu(Menu menu) {
		menuDao.delete(menu);
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}

	public void sendMail(String subject, String sendHtml, String receiveUser) {
		try {
			EmailUtils.sendMail(subject, sendHtml, receiveUser);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void afterPropertiesSet() throws Exception {
		// 上传文件路径下子目录初始化
		String basePath = System.getProperty("web.root") + Global.USERFILES_BASE_URL;
		String[] subdir = StringUtils.split(Global.getConfig("userfiles.subdir.init"), "|");
		for (int i = 0; i < subdir.length; i++) {
			FileUtils.createDirectory(FileUtils.path(basePath + subdir[i]));
		}
	}

}
