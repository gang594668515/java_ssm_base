
package com.xj.b2b.back.utils;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Maps;
import com.xj.b2b.back.dao.MenuDao;
import com.xj.b2b.back.dao.RoleDao;
import com.xj.b2b.back.dao.UserDao;
import com.xj.b2b.back.dao.UserMessageDao;
import com.xj.b2b.back.entity.Menu;
import com.xj.b2b.back.entity.Role;
import com.xj.b2b.back.entity.User;
import com.xj.b2b.back.entity.UserMessage;
import com.xj.b2b.back.security.SystemAuthorizingRealm.Principal;
import com.xj.common.utils.CacheUtils;
import com.xj.common.utils.SpringContextHolder;
import com.xj.common.utils.StringUtils;

/**
 * 用户工具类
 * 
 * @version 2013-12-05
 */
public class UserUtils {
	// 日志文件
	private static Logger logger = LoggerFactory.getLogger(UserUtils.class);

	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
	private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);

	public static final String USER_CACHE = "userCache";
	public static final String USER_CACHE_ID_ = "id_";
	public static final String USER_CACHE_LOGIN_NAME_ = "loginName_";

	public static final String ROLE_CACHE = "roleCache";
	public static final String ROLE_CACHE_ID_ = "id_";
	public static final String ROLE_CACHE_NAME_ = "name_";
	public static final String ROLE_CACHE_ENNAME_ = "enname_";

	public static final String CACHE_AUTH_INFO = "authInfo";
	public static final String CACHE_ROLE_LIST = "roleList";
	public static final String CACHE_MENU_LIST = "menuList";
	public static final String CACHE_AREA_LIST = "areaList";
	public static final String CACHE_OFFICE_LIST = "officeList";
	public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";

	// 用户消息
	private static UserMessageDao userMessageDao = SpringContextHolder.getBean(UserMessageDao.class);
	public static final String CACHE_USER_MESSAGE = "userMessageCache";
	public static final String CACHE_USER_ID_ = "uid_";

	/**
	 * 根据ID获取用户
	 * 
	 * @param id
	 * @return 取不到返回null
	 */
	public static User get(String id) {
		User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
		if (user == null) {
			user = userDao.get(id);
			if (user == null) {
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			addCache(user);
		}
		return user;
	}

	/**
	 * 根据登录名获取用户
	 * 
	 * @param loginName
	 * @return 取不到返回null
	 */
	public static User getByLoginName(String loginName) {
		User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
		if (user == null) {
			user = userDao.getByLoginName(new User(null, loginName));
			if (user == null) {
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			addCache(user);
		}
		return user;
	}

	/**
	 * 校验登录名是否有效
	 * 
	 * @param loginName
	 */
	public static boolean verifyLoginName(String loginName) {
		if (StringUtils.isBlank(loginName)) {
			return false;
		}
		return loginName.matches("^[a-z0-9][a-z_0-9]{4,24}");
	}

	/**
	 * 清除当前用户缓存
	 */
	public static void clearCache() {
		removeCache(CACHE_AUTH_INFO);
		removeCache(CACHE_ROLE_LIST);
		removeCache(CACHE_MENU_LIST);
		removeCache(CACHE_AREA_LIST);
		removeCache(CACHE_OFFICE_LIST);
		removeCache(CACHE_OFFICE_ALL_LIST);
		UserUtils.clearCache(getUser());
	}

	/**
	 * 清除指定用户缓存
	 * 
	 * @param user
	 */
	public static void clearCache(User user) {
		CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
	}

	/**
	 * 增加指定用户缓存
	 * 
	 * @param user
	 */
	public static void addCache(User user) {
		CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
		CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
	}

	/**
	 * 获取当前用户
	 * 
	 * @return 取不到返回 new User()
	 */
	public static User getUser() {
		Principal principal = getPrincipal();
		if (principal != null) {
			User user = get(principal.getId());
			if (user != null) {
				return user;
			}
			return new User();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new User();
	}

	// ============== 角色信息 ==============
	/**
	 * 根据ID获取角色信息
	 * 
	 * @param id
	 * @return 取不到返回null
	 */
	public static Role getRole(String id) {
		Role role = (Role) CacheUtils.get(ROLE_CACHE, ROLE_CACHE_ID_ + id);
		if (role == null) {
			role = roleDao.get(id);
			if (role == null) {
				return null;
			}
			addCache(role);
		}
		return role;
	}

	/**
	 * 根据名称获取角色
	 * 
	 * @param name
	 * @return 取不到返回null
	 */
	public static Role getRoleByName(String name) {
		Role role = (Role) CacheUtils.get(ROLE_CACHE, ROLE_CACHE_NAME_ + name);
		if (role == null) {
			Role r = new Role();
			r.setName(name);
			role = roleDao.getByName(r);
			if (role == null) {
				return null;
			}
			addCache(role);
		}
		return role;
	}

	/**
	 * 根据英文名称获取角色
	 * 
	 * @param name
	 * @return 取不到返回null
	 */
	public static Role getRoleByEnname(String enname) {
		Role role = (Role) CacheUtils.get(ROLE_CACHE, ROLE_CACHE_ENNAME_ + enname);
		if (role == null) {
			Role r = new Role();
			r.setEnname(enname);
			role = roleDao.getByEnname(r);
			if (role == null) {
				return null;
			}
			addCache(role);
		}
		return role;
	}

	/**
	 * 获取当前用户角色列表
	 * 
	 * @return
	 */
	public static List<Role> getRoleList() {
		@SuppressWarnings("unchecked")
		List<Role> roleList = (List<Role>) getCache(CACHE_ROLE_LIST);
		if (roleList == null) {
			User user = getUser();
			if (user.isAdmin()) {
				roleList = roleDao.findAllList(new Role());
			} else {
				Role role = new Role();
				roleList = roleDao.findList(role);
			}
			putCache(CACHE_ROLE_LIST, roleList);
		}
		return roleList;
	}

	/**
	 * 清除指定角色缓存
	 * 
	 * @param role
	 */
	public static void clearCache(Role role) {
		CacheUtils.remove(ROLE_CACHE, ROLE_CACHE_ID_ + role.getId());
		CacheUtils.remove(ROLE_CACHE, ROLE_CACHE_NAME_ + role.getName());
		CacheUtils.remove(ROLE_CACHE, ROLE_CACHE_ENNAME_ + role.getEnname());
		removeCache(CACHE_ROLE_LIST);
	}

	/**
	 * 增加指定角色缓存
	 * 
	 * @param role
	 */
	public static void addCache(Role role) {
		CacheUtils.put(ROLE_CACHE, ROLE_CACHE_ID_ + role.getId(), role);
		CacheUtils.put(ROLE_CACHE, ROLE_CACHE_NAME_ + role.getName(), role);
		CacheUtils.put(ROLE_CACHE, ROLE_CACHE_ENNAME_ + role.getEnname(), role);
	}

	// ============== 其他信息 ==============
	/**
	 * 获取当前用户授权菜单
	 * 
	 * @return
	 */
	public static List<Menu> getMenuList() {
		@SuppressWarnings("unchecked")
		List<Menu> menuList = (List<Menu>) getCache(CACHE_MENU_LIST);
		if (menuList == null) {
			User user = getUser();
			if (user.isAdmin()) {
				menuList = menuDao.findAllList(new Menu());
			} else {
				Menu m = new Menu();
				m.setUserId(user.getId());
				menuList = menuDao.findByUserId(m);
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}

	/**
	 * /** 获取当前登录用户授权对象
	 */
	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	/**
	 * 获取当前登录用户凭证
	 */
	public static Principal getPrincipal() {
		try {
			Subject subject = SecurityUtils.getSubject();
			Principal principal = (Principal) subject.getPrincipal();
			if (principal != null) {
				return principal;
			}
			// subject.logout();
		} catch (UnavailableSecurityManagerException e) {
			// logger.error("getPrincipal, 获取当前登录用户凭证失败！", e);
		} catch (InvalidSessionException e) {
			logger.error("getPrincipal, 获取当前登录用户凭证失败！", e);
		}
		return null;
	}

	/**
	 * 获取当前登录用户 Session
	 */
	public static Session getSession() {
		try {
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null) {
				session = subject.getSession();
			}
			if (session != null) {
				return session;
			}
			// subject.logout();
		} catch (InvalidSessionException e) {
			logger.error("getSession, 获取当前登录用户 Session失败！", e);
		}
		return null;
	}

	/**
	 * 是否是验证码登录
	 * 
	 * @param useruame
	 *            用户名
	 * @param isFail
	 *            计数加1
	 * @param clean
	 *            计数清零
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean) {
		Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
		if (loginFailMap == null) {
			loginFailMap = Maps.newHashMap();
			CacheUtils.put("loginFailMap", loginFailMap);
		}
		Integer loginFailNum = loginFailMap.get(useruame);
		if (loginFailNum == null) {
			loginFailNum = 0;
		}
		if (isFail) {
			loginFailNum++;
			loginFailMap.put(useruame, loginFailNum);
		}
		if (clean) {
			loginFailMap.remove(useruame);
		}
		return loginFailNum >= 3;
	}

	// ============== User Cache ==============

	public static Object getCache(String key) {
		return getCache(key, null);
	}

	public static Object getCache(String key, Object defaultValue) {
		// Object obj = getCacheMap().get(key);
		Object obj = getSession().getAttribute(key);
		return obj == null ? defaultValue : obj;
	}

	public static void putCache(String key, Object value) {
		// getCacheMap().put(key, value);
		getSession().setAttribute(key, value);
	}

	public static void removeCache(String key) {
		// getCacheMap().remove(key);
		getSession().removeAttribute(key);
	}

	// ============== User Message ==============
	public static boolean sendUserMessage(String title, String content, String href, String type, User receiveBy) {
		UserMessage userMessage = new UserMessage();
		userMessage.setTitle(title);
		userMessage.setContent(content);
		userMessage.setHref(href);
		userMessage.setType(type);
		userMessage.setReceiveBy(receiveBy);
		return sendUserMessage(userMessage);
	}

	public static boolean sendUserMessage(UserMessage userMessage) {
		if (userMessage.getReceiveBy() == null) {
			return false;
		}
		userMessage.setIsNewRecord(true);
		userMessage.preInsert();
		userMessageDao.insert(userMessage);
		CacheUtils.remove(CACHE_USER_MESSAGE, CACHE_USER_ID_ + userMessage.getReceiveBy().getId());
		return true;
	}

	public static Integer getNewUserMessageCount(UserMessage userMessage) {
		if (userMessage.getReceiveBy() == null) {
			return 0;
		}
		String cacheId = userMessage.getReceiveBy().getId();
		if (StringUtils.isBlank(cacheId)) {
			return 0;
		}

		cacheId = CACHE_USER_ID_ + cacheId;

		Integer count = (Integer) CacheUtils.get(CACHE_USER_MESSAGE, cacheId);
		if (count == null) {
			count = userMessageDao.getNewMessageCount(userMessage);
			if (count == null) {
				count = 0;
			}
			CacheUtils.remove(CACHE_USER_MESSAGE, cacheId);
			CacheUtils.put(CACHE_USER_MESSAGE, cacheId, count);
		}
		return count;
	}

}
