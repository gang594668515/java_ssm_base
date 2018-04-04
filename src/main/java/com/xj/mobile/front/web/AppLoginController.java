package com.xj.mobile.front.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xj.b2b.back.security.FormAuthenticationFilter;
import com.xj.b2b.back.security.SystemAuthorizingRealm.Principal;
import com.xj.b2b.back.utils.UserUtils;
import com.xj.common.security.shiro.session.SessionDAO;
import com.xj.common.servlet.ValidateCodeServlet;
import com.xj.common.utils.IdGen;
import com.xj.common.utils.StringUtils;
import com.xj.common.web.BaseController;

/**
 * App移动端登录 Controller
 * 
 * @author yangb
 * @version 20180202
 */
@Controller
public class AppLoginController extends BaseController {

	@Autowired
	private SessionDAO sessionDAO;

	// ========== App移动端登录相关方法 >>>>>>>>>>
	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	@RequestMapping(value = "${mobilePath}/login", method = RequestMethod.POST)
	private String loginFail(HttpServletRequest request, HttpServletResponse response) {

		// 如果已经登录，则跳转到首页
		Principal principal = UserUtils.getPrincipal();
		if (principal != null) {
			return "redirect:" + mobilePath;
		}

		String username = WebUtils.getCleanParam(request, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
		boolean rememberMe = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
		boolean mobile = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
		String exception = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
		String message = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);

		if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")) {
			message = "用户或密码错误, 请重试.";
		}

		logger.debug("loginFail, user name: {}, active session size: {}, message: {}, exception: {}", username,
				sessionDAO.getActiveSessions(false).size(), message, exception);

		Map<String, Object> map = new HashMap<>();
		map.put(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
		map.put(FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
		map.put(FormAuthenticationFilter.DEFAULT_MOBILE_PARAM, mobile);
		map.put(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, exception);
		map.put(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);

		// 非授权异常，登录失败，验证码计数加1。
		if (!UnauthorizedException.class.getName().equals(exception)) {
			map.put("isValidateCodeLogin", UserUtils.isValidateCodeLogin(username, true, false));
		}

		// 验证失败清空验证码
		request.getSession().setAttribute(ValidateCodeServlet.VALIDATE_CODE, IdGen.uuid());

		// 手机登录，返回JSON字符串
		return renderString(response, map);
	}

	/**
	 * 登录成功或注销
	 */
	@RequestMapping(value = "${mobilePath}")
	private String mobileIndex(HttpServletRequest request, HttpServletResponse response) {

		// 如果已经登录，返回JSON登录字符串
		Principal principal = UserUtils.getPrincipal();
		if (principal != null) {
			// 登录成功后，验证码计算器清零
			UserUtils.isValidateCodeLogin(principal.getLoginName(), false, true);

			logger.debug("mobileIndex, 登录成功, {}", principal);
			// 手机登录，返回JSON字符串
			return renderString(response, principal);
		} else {
			// 已注销
			Map<String, Boolean> map = new HashMap<>();
			map.put("hasLoggedIn", false);

			logger.debug("mobileIndex, 注销成功。");
			// 手机注销，返回JSON字符串
			return renderString(response, map);
		}

	}

	/**
	 * 判断是否已登录
	 */
	@RequestMapping(value = "${mobilePath}/hasLoggedIn")
	private String hasLoggedIn(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = new HashMap<>();

		// 返回JSON登录结果字符串
		Principal principal = UserUtils.getPrincipal();
		if (principal != null) {
			// 已经登录
			map.put("hasLoggedIn", true);
			map.put("principal", principal);

		} else {
			// 未登录
			map.put("hasLoggedIn", false);
		}

		logger.debug("hasLoggedIn, 登录状态, {}", map);

		return renderString(response, map);
	}

}
