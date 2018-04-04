<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>消息详情</title>
<meta name="decorator" content="back_default" />
<script type="text/javascript">
	$(document).ready(function() {
		window.parent.window.checkNewMsg();
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/user/message">消息列表</a></li>
		<li class="active"><a href="${ctx}/sys/user/message/detail?id=${userMessage.id}">消息详情</a></li>
	</ul>
	<form:form id="inputForm" class="form-horizontal">
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label">标题:</label>
			<div class="controls">
				<p class="lbl">${userMessage.title}</p>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内容:</label>
			<div class="controls">
				<p class="lbl">${userMessage.content}</p>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">相关链接:</label>
			<div class="controls">
				<a target="_blank" href="${userMessage.href}" class="lbl">${userMessage.href}</a>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">消息类型:</label>
			<div class="controls">
				<label class="lbl">${userMessage.type}</label>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发送时间:</label>
			<div class="controls">
				<label class="lbl">
					<fmt:formatDate value="${userMessage.sendDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</label>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>