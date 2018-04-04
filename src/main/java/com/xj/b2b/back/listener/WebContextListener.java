package com.xj.b2b.back.listener;

import javax.servlet.ServletContextEvent;

import com.xj.common.config.Global;

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		printKeyLoadMessage();

		super.contextInitialized(event);
	}

	/**
	 * 获取Key加载信息
	 */
	public void printKeyLoadMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n======================================================================\r\n");
		sb.append("\r\n    欢迎使用 " + Global.getConfig("productName") + "  - Powered By " + Global.getConfig("poweredBy")
				+ "\r\n");
		sb.append("\r\n======================================================================\r\n");
		System.out.println(sb.toString());
	}

}
