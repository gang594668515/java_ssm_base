package com.xj.b2b.back.interceptor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class GlobalExceptionResolver extends SimpleMappingExceptionResolver {
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {

		String viewName = determineViewName(ex, request);

		// 保存日志
		logger.debug("doResolveException, 异常： ", ex);

		if (viewName != null) {
			String xRequestedWith = request.getHeader("X-Requested-With");
			if (request.getHeader("accept").contains("application/json")
					|| (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest"))) {
				// 异步方式返回
				try {
					if (ex != null && ex.getMessage() != null) {
						PrintWriter writer = response.getWriter();
						writer.write(ex.getMessage());
						writer.flush();
					}
				} catch (IOException e) {
					logger.debug("doResolveException, ajax异常： ", e);
				}
				return null;
			} else {
				// 非异步方式返回
				Integer statusCode = determineStatusCode(request, viewName);
				if (statusCode != null) {
					applyStatusCodeIfPossible(request, response, statusCode);
				}
				return getModelAndView(viewName, ex, request);
			}

		} else {
			return null;
		}
	}
}
