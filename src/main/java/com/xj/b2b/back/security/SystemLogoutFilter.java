
package com.xj.b2b.back.security;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Service;

import com.xj.b2b.back.utils.LogUtils;
import com.xj.common.utils.StringUtils;

/**
 * 系统注销 过滤类
 * 
 * @author yangb
 * @version 2016-08-05
 */
@Service
public class SystemLogoutFilter extends org.apache.shiro.web.filter.authc.LogoutFilter {

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		LogUtils.saveLog(httpRequest, "系统注销"); // 记录日志
		return super.preHandle(request, response);
	}

	@Override
	protected void issueRedirect(ServletRequest request, ServletResponse response, String redirectUrl)
			throws Exception {
		// +系统语言参数
		Map<String, String> queryParams = null;
		String lang = request.getParameter("lang");
		if (StringUtils.isNotBlank(lang)) {
			queryParams = new HashMap<String, String>();
			queryParams.put("lang", lang);
		}
		WebUtils.issueRedirect(request, response, redirectUrl, queryParams, true);
	}

}