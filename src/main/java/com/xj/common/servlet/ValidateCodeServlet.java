
package com.xj.common.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.xj.common.utils.ValidateCodeUtils;

/**
 * 生成随机验证码
 * 
 * @version 2014-7-27
 */
@SuppressWarnings("serial")
public class ValidateCodeServlet extends HttpServlet {

	public static final String VALIDATE_CODE = "validateCode";

	private int w = 120;
	private int h = 48;

	public ValidateCodeServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public static boolean validate(HttpServletRequest request, String validateCode) {
		String code = (String) request.getSession().getAttribute(VALIDATE_CODE);
		return validateCode.toUpperCase().equals(code);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String validateCode = request.getParameter(VALIDATE_CODE); // AJAX验证，成功返回true
		if (StringUtils.isNotBlank(validateCode)) {
			response.getOutputStream().print(validate(request, validateCode) ? "true" : "false");
		} else {
			this.doPost(request, response);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		createImage(request, response);
	}

	private void createImage(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		/*
		 * 得到参数高，宽，都为数字时，则使用设置高宽，否则使用默认值
		 */
		String width = request.getParameter("width");
		String height = request.getParameter("height");
		if (StringUtils.isNumeric(width) && StringUtils.isNumeric(height)) {
			w = NumberUtils.toInt(width);
			h = NumberUtils.toInt(height);
		}

		/*
		 * 生成字符
		 */
		String code = ValidateCodeUtils.generateVerifyCode(4, ValidateCodeUtils.VERIFY_CODES);
		request.getSession().setAttribute(VALIDATE_CODE, code.toUpperCase());
		// 生成验证码图片
		OutputStream out = response.getOutputStream();
		ValidateCodeUtils.outputImage(w, h, code, out);
		out.close();

	}

}
