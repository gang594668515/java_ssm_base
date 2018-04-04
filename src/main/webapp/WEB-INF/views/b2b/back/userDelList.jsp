<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>用户回收站</title>
<meta name="decorator" content="back_default" />
<script type="text/javascript">
	function page(n, s) {
		if (n)
			$("#pageNo").val(n);
		if (s)
			$("#pageSize").val(s);
		$("#searchForm").attr("action", "${ctx}/sys/user/delList");
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active">
			<a href="${ctx}/sys/user/list">已删除用户列表</a>
		</li>
	</ul>
	<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/delList" method="post" class="breadcrumb form-search ">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();" />
		<ul class="ul-form">
			<li>
				<label>登录名：</label>
				<form:input path="loginName" htmlEscape="false" maxlength="50" class="input-medium" />
			</li>
			<!-- <li class="clearfix"></li> -->
			<li>
				<label>姓名：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium" />
			</li>
			<li>
				<label>企业名称：</label>
				<form:input path="company" htmlEscape="false" maxlength="100" class="input-medium" />
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();" />
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 3%; text-align: center;">序号</th>
				<th class="sort-column login_name">登录名</th>
				<th class="sort-column name">姓名</th>
				<th class="sort-column company">企业名称</th>
				<th>邮箱</th>
				<th>手机</th>
				<th>账户余额</th>
				<th>角色</th>
				<shiro:hasPermission name="sys:user:revert">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="user" varStatus="status">
				<tr>
					<td style="text-align: center;">${(page.pageNo - 1) * page.pageSize + status.count}</td>
					<td><a href="${ctx}/sys/user/form?id=${user.id}">${user.loginName}</a></td>
					<td>${user.name}</td>
					<td>${user.company}</td>
					<td>${user.email}</td>
					<td>${user.mobile}</td>
					<td>${user.accountBalance}</td>
					<td>${user.roleNames}</td>
					<shiro:hasPermission name="sys:user:revert">
						<td><a href="${ctx}/sys/user/revert?id=${user.id}" onclick="return confirmx('确认要恢复该用户吗？', this.href)">恢复</a></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>