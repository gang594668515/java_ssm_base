<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>日志管理</title>
<meta name="decorator" content="back_default" />
<script src="${ctxStatic}/bootstrap-datetimepicker/dist/js/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/bootstrap-datetimepicker/dist/js/locales/bootstrap-datetimepicker.zh-CN.js" type="text/javascript"></script>
<link href="${ctxStatic}/bootstrap-datetimepicker/dist/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript">
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<!-- 	<ul class="nav nav-tabs"> -->
	<%-- 		<li class="active"><a href="${ctx}/sys/log/">日志列表</a></li> --%>
	<!-- 	</ul> -->
	<form:form id="searchForm" action="${ctx}/sys/log/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<div>
			<label>操作菜单：</label>
			<input id="title" name="title" type="text" maxlength="50" class="input-mini" value="${log.title}" />
			<label>用户ID：</label>
			<input id="createBy.id" name="createBy.id" type="text" maxlength="50" class="input-mini" value="${log.createBy.id}" />
			<label>URI：</label>
			<input id="requestUri" name="requestUri" type="text" maxlength="50" class="input-mini" value="${log.requestUri}" />
		</div>
		<div style="margin-top: 8px;">
			<label>日期范围：&nbsp;</label>
			<input id="beginDate" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-mini form_date" value="<fmt:formatDate value='${log.beginDate}' pattern='yyyy-MM-dd'/>" />
			<label style="margin-left: 5px; margin-right: 5px;">--</label>
			<input id="endDate" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-mini form_date"
				value="<fmt:formatDate value='${log.endDate}' pattern='yyyy-MM-dd'/>" />
			<script type="text/javascript">
				$(".form_date").datetimepicker({
					language : 'zh-CN',
					format : 'yyyy-mm-dd',
					weekStart : 1,
					todayBtn : 1,
					autoclose : 1,
					todayHighlight : 1,
					startView : 2,
					minView : 2,
					forceParse : 0
				});
			</script>
			<label for="exception" style="margin-left: 10px; margin-right: 10px;">
				<input id="exception" name="exception" type="checkbox" ${log.exception eq '1'?' checked':''} value="1" />
				只查询异常信息
			</label>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" />
		</div>
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 3%; text-align: center;">序号</th>
				<th>操作菜单</th>
				<th>操作用户</th>
				<th>所在公司</th>
				<th>URI</th>
				<th>提交方式</th>
				<th>操作者IP</th>
				<th>操作时间</th>
		</thead>
		<tbody>
			<%
				request.setAttribute("strEnter", "\n");
				request.setAttribute("strTab", "\t");
			%>
			<c:forEach items="${page.list}" var="log" varStatus="status">
				<tr>
					<td style="text-align: center;">${(page.pageNo - 1) * page.pageSize + status.count}</td>
					<td>${log.title}</td>
					<td>${log.createBy.name}</td>
					<td>${log.createBy.company}</td>
					<td><strong>${log.requestUri}</strong></td>
					<td>${log.method}</td>
					<td>${log.remoteAddr}</td>
					<td><fmt:formatDate value="${log.createDate}" type="both" /></td>
				</tr>
				<tr>
					<c:choose>
						<c:when test="${not empty log.exception}">
							<td style="background: #ffd4aa">详情</td>
							<td colspan="7" style="word-wrap: break-word; word-break: break-all; background: #ffd4aa">用户代理: ${log.userAgent}<br> 提交参数: ${fns:escapeHtml(log.params)} <br> 异常信息:
								${fn:replace(fn:replace(fns:escapeHtml(log.exception), strEnter, '<br>'), strTab, '&nbsp; &nbsp; ')}
							</td>
						</c:when>
						<c:when test="${not empty log.params}">
							<td style="background: #ffffdd">详情</td>
							<td colspan="7" style="word-wrap: break-word; word-break: break-all; background: #ffffdd">用户代理: ${log.userAgent}<br> 提交参数: ${fns:escapeHtml(log.params)}
							</td>
						</c:when>
						<c:otherwise>
							<td style="background: #eeffdd">详情</td>
							<td colspan="7" style="word-wrap: break-word; word-break: break-all; background: #eeffdd">用户代理: ${log.userAgent}</td>
						</c:otherwise>
					</c:choose>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>