<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>角色管理</title>
<meta name="decorator" content="back_default" />
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/role/">角色列表</a></li>
		<shiro:hasPermission name="sys:role:edit">
			<li><a href="${ctx}/sys/role/form">角色添加</a></li>
		</shiro:hasPermission>
	</ul>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<tr>
			<th style="width: 3%; text-align: center;">序号</th>
			<th class="sort-column name">角色名称</th>
			<th class="sort-column enname">英文名称</th>
			<th>角色类型</th>
			<th class="sort-column useable">是否可用</th>
			<th>是否系统数据</th>
			<shiro:hasPermission name="sys:role:edit">
				<th>操作</th>
			</shiro:hasPermission>
		</tr>
		<c:forEach items="${list}" var="role" varStatus="status">
			<tr>
				<td style="text-align: center;">${(page.pageNo - 1) * page.pageSize + status.count}</td>
				<td><a href="form?id=${role.id}">${role.name}</a></td>
				<td><a href="form?id=${role.id}">${role.enname}</a></td>
				<td>${fns:getDictLabel(role.roleType, 'role_type', '--')}</td>
				<td>${fns:getDictLabel(role.useable, 'yes_no', '--')}</td>
				<td>${fns:getDictLabel(role.sysData, 'yes_no', '--')}</td>
				<shiro:hasPermission name="sys:role:edit">
					<td><a href="${ctx}/sys/role/assign?id=${role.id}">分配</a>
					    <c:if
							test="${(role.sysData eq fns:getDictValue('是', 'yes_no', '1') && fns:getUser().admin)||!(role.sysData eq fns:getDictValue('是', 'yes_no', '1'))}">
							<a href="${ctx}/sys/role/form?id=${role.id}">修改</a>
						</c:if> <a href="${ctx}/sys/role/delete?id=${role.id}" onclick="return confirmx('确认要删除该角色吗？', this.href)">删除</a></td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
	</table>
</body>
</html>