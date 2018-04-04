
package com.xj.b2b.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.xj.common.service.BaseService;

/**
 * 日志拦截器
 * 
 * @version 2017-09-12
 */
public class UriInterceptor extends BaseService implements HandlerInterceptor {
	private static final ThreadLocal<String> viewNameThreadLocal = new NamedThreadLocal<String>("ThreadLocal ViewName");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			viewNameThreadLocal.set(modelAndView.getViewName());
		} else {
			viewNameThreadLocal.set(null);
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		// 保存日志
		logger.debug("doResolveException, viewNameThreadLocal:{}, 异常： ", viewNameThreadLocal.get(), ex);

	}

}
