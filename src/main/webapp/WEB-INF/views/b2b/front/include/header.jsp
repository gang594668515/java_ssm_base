<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%-- 网站LOGO及主标题栏 --%>
<header>
	<div class="container-fluid header-div">
		<div class="header-left g-left">
			<c:choose>
				<c:when test="${empty fns:getPrincipal()}">
					<span>您好，欢迎进入Java基础平台</span>&nbsp;
					<a href="${ftx}/login">登录</a>
					&nbsp;
					<a href="${ftx}/signup">注册</a>
				</c:when>
				<c:otherwise>
					<span>您好，${fns:getPrincipal().loginName}，欢迎进入Java基础平台！</span>&nbsp;
					<a href="${ftx}/logout" class="exit_link">[退出]</a>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="hidden-xs header-right g-right">
			<span><a href="${ftx}" target="_self">首页</a></span> <b>|</b> <span><a href="${ftx}/myCenter">我的平台</a></span> <span><a href="${ftx}/myCenter?sublink=myCenter/messageList">
					(<i class="header-msg-count">0</i>)
				</a></span> <b>|</b> <span><a href="${ftx}/help">帮助</a></span>
		</div>
	</div>
</header>