<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>系统消息</title>
<meta name="decorator" content="back_default" />
<script type="text/javascript">
	function page(n, s) {
		if (n)
			$("#pageNo").val(n);
		if (s)
			$("#pageSize").val(s);

		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/user/message">消息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="userMessage" action="${ctx}/sys/user/message" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 3%; text-align: center;">序号</th>
				<th>标题</th>
				<th>相关链接</th>
				<th>类型</th>
				<th class="sort-column send_date">发送时间</th>
				<th>状态</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="item" varStatus="status">
				<tr style="font-weight: ${item.readFlag == 1 ? 'normal' : 'bold'}">
					<td style="text-align: center;">${(page.pageNo - 1) * page.pageSize + status.count}</td>
					<td><a href="${ctx}/sys/user/message/detail?id=${item.id}">${item.title}</a></td>
					<td><c:choose>
							<c:when test="${empty item.href}">
							无
						</c:when>
							<c:otherwise>
								<a href="${item.href}" target="_blank">相关链接</a>
							</c:otherwise>
						</c:choose></td>
					<td>${item.type}</td>
					<td><fmt:formatDate value="${item.sendDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${item.readFlag == 1 ? '已读' : '未读'}</td>
					<td><a href="${ctx}/sys/user/message/detail?id=${item.id}">查看</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>