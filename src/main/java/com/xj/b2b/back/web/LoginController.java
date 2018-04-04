package com.xj.b2b.back.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xj.b2b.back.security.FormAuthenticationFilter;
import com.xj.b2b.back.security.SystemAuthorizingRealm.Principal;
import com.xj.b2b.back.utils.UserUtils;
import com.xj.common.config.Global;
import com.xj.common.security.shiro.session.SessionDAO;
import com.xj.common.servlet.ValidateCodeServlet;
import com.xj.common.utils.CookieUtils;
import com.xj.common.utils.IdGen;
import com.xj.common.utils.StringUtils;
import com.xj.common.web.BaseController;

/**
 * 后台Controller
 * 
 * @author yangb
 * @version 2016年4月1日
 */
@Controller
public class LoginController extends BaseController {

	@Autowired
	private SessionDAO sessionDAO;

	// ========== 后台管理系统登录 >>>>>>>>>>

	/**
	 * 管理登录
	 */
	@RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET)
	public String backLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		return loginCheck(adminPath, "b2b/back/sysLogin", response);
	}

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	@RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
	public String backLoginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
		return loginFail(adminPath, "b2b/back/sysLogin", request, response, model);
	}

	/**
	 * 登录成功，进入管理首页
	 */
	@RequiresPermissions("sys:back:login")
	@RequestMapping(value = "${adminPath}")
	public String backIndex(HttpServletRequest request, HttpServletResponse response) {
		return index(adminPath, "b2b/back/sysIndex", request, response);
	}

	/**
	 * 获取主题方案
	 */
	@RequiresPermissions("sys:back:login")
	@RequestMapping(value = "/theme/{theme}")
	public String getThemeInCookie(@PathVariable String theme, HttpServletRequest request,
			HttpServletResponse response) {
		if (StringUtils.isNotBlank(theme)) {
			CookieUtils.setCookie(response, "theme", theme);
		} else {
			theme = CookieUtils.getCookie(request, "theme");
		}
		return "redirect:" + request.getParameter("url");
	}

	// ========== 前台系统登录 >>>>>>>>>>
	/**
	 * 用户登录
	 */
	@RequestMapping(value = "${frontPath}/login", method = RequestMethod.GET)
	public String frontLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("loginPath", frontPath);
		return loginCheck(frontPath, "b2b/front/login", response);
	}

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	@RequestMapping(value = "${frontPath}/login", method = RequestMethod.POST)
	public String frontLoginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
		return loginFail(frontPath, "b2b/front/login", request, response, model);
	}

	/**
	 * 登录成功，进入前台首页
	 */
	@RequestMapping(value = "${frontPath}")
	public String frontIndex(HttpServletRequest request, HttpServletResponse response, Model model) {
		return index(frontPath, "b2b/front/index", request, response);
	}

	// ========== 登录相关方法 >>>>>>>>>>

	private String loginCheck(String redirectPath, String url, HttpServletResponse response) {
		Principal principal = UserUtils.getPrincipal();

		// // 默认页签模式，后台登录用
		// String tabmode = CookieUtils.getCookie(request, "tabmode");
		// if (tabmode == null){
		// CookieUtils.setCookie(response, "tabmode", "1");
		// }

		if (logger.isDebugEnabled()) {
			logger.debug("login, active session size: {}", sessionDAO.getActiveSessions(false).size());
		}

		// 如果已登录，再次访问主页，则退出原账号。
		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))) {
			CookieUtils.setCookie(response, "LOGINED", "false");
		}

		// 如果已经登录，则跳转到管理首页
		if (principal != null && !principal.isMobileLogin()) {
			return "redirect:" + redirectPath;
		}

		return url;
	}

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	private String loginFail(String redirectPath, String url, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Principal principal = UserUtils.getPrincipal();

		// 如果已经登录，则跳转到首页
		if (principal != null) {
			return "redirect:" + redirectPath;
		}

		String username = WebUtils.getCleanParam(request, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
		boolean rememberMe = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
		boolean mobile = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
		String exception = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
		String message = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);

		if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")) {
			message = "用户或密码错误, 请重试.";
		}

		model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_MOBILE_PARAM, mobile);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, exception);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);

		if (logger.isDebugEnabled()) {
			logger.debug("login fail, active session size: {}, message: {}, exception: {}",
					sessionDAO.getActiveSessions(false).size(), message, exception);
		}

		// 非授权异常，登录失败，验证码加1。
		if (!UnauthorizedException.class.getName().equals(exception)) {
			model.addAttribute("isValidateCodeLogin", UserUtils.isValidateCodeLogin(username, true, false));
		}

		// 验证失败清空验证码
		request.getSession().setAttribute(ValidateCodeServlet.VALIDATE_CODE, IdGen.uuid());

		// 如果是手机登录，则返回JSON字符串
		if (mobile) {
			return renderString(response, model);
		}

		return url;
	}

	/**
	 * 登录成功，进入首页
	 */
	private String index(String redirectPath, String url, HttpServletRequest request, HttpServletResponse response) {
		Principal principal = UserUtils.getPrincipal();

		// 非后台管理时，凭证为空，跳转到首页
		if (!redirectPath.equals(adminPath)) {
			if (principal == null) {
				return url;
			}
		}

		// 登录成功后，验证码计算器清零
		UserUtils.isValidateCodeLogin(principal.getLoginName(), false, true);

		if (logger.isDebugEnabled()) {
			logger.debug("show index, active session size: {}", sessionDAO.getActiveSessions(false).size());
		}

		// 如果已登录，再次访问主页，则退出原账号。
		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))) {
			String logined = CookieUtils.getCookie(request, "LOGINED");
			if (StringUtils.isBlank(logined) || "false".equals(logined)) {
				CookieUtils.setCookie(response, "LOGINED", "true");
			} else if (StringUtils.equals(logined, "true")) {
				UserUtils.getSubject().logout();
				return "redirect:" + redirectPath + "/login";
			}
		}

		// 如果是手机登录，则返回JSON字符串
		if (principal.isMobileLogin()) {
			if (request.getParameter("login") != null) {
				return renderString(response, principal);
			}
			if (request.getParameter("index") != null) {
				return url;
			}
			return "redirect:" + redirectPath + "/login";
		}

		return url;
	}

}
