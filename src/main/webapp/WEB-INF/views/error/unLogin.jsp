
<%
	response.setStatus(500);

	// 获取异常类
	Throwable ex = Exceptions.getThrowable(request);

	// 编译错误信息
	StringBuilder sb = new StringBuilder("身份验证异常，可能原因是您未登录系统. 错误信息：\n");
	if (ex != null) {
		sb.append(Exceptions.getStackTraceAsString(ex));
	} else {
		sb.append("未知错误.\n\n");
	}

	// 如果是异步请求或是手机端，则直接返回信息
	if (Servlets.isAjaxRequest(request)) {
		out.print(sb);
	}

	// 输出异常信息页面
	else {
%>
<%@page import="org.slf4j.Logger,org.slf4j.LoggerFactory"%>
<%@page import="com.xj.common.web.Servlets"%>
<%@page import="com.xj.common.utils.Exceptions"%>
<%@page import="com.xj.common.utils.StringUtils"%>
<%@page contentType="text/html;charset=UTF-8" isErrorPage="true"%>
<%@include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>身份验证异常</title>
<%@include file="/WEB-INF/views/include/head.jsp"%>
<style type="text/css">
.page-header>h1 {
	color: #c71c22;
}
</style>
</head>
<body>
	<div class="container-fluid">
		<div class="page-header">
			<h1>身份验证异常，可能原因是您未登录系统.</h1>
		</div>
		<div class="errorMessage">
			错误信息：<%=ex == null ? "未知错误." : StringUtils.toHtml(ex.getMessage())%>
			<br>
			<br>
			<a href="javascript:" onclick="history.go(-1);" class="btn">返回上一页</a>
			&nbsp;
			<a href="/" target="_top" style="cursor: pointer;" class="btn btn-inverse">返回首页</a>
			&nbsp;
			<a href="${ftx}/login" target="_top" style="cursor: pointer;" class="btn btn-success">登录系统</a>
		</div>
		<script>
			try {
				top.$.jBox.closeTip();
			} catch (e) {
			}
		</script>
	</div>
</body>
</html>
<%
	}
	out = pageContext.pushBody();
%>