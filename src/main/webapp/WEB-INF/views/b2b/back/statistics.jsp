<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>统计信息</title>
<meta name="decorator" content="back_default" />

<style type="text/css">
table.statistics {
	border-collapse: separate;
	border-spacing: 8px 15px
}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/statistics/info">统计信息</a></li>
	</ul>

	<div class="breadcrumb" style="height: 100%;">
		<table class="statistics">
			<colgroup>
				<col width="100">
				<col width="150">
				<col width="100">
				<col width="150">
				<col width="100">
				<col width="150">
			</colgroup>
			<tbody>
				<tr>
					<td class="text-right">注册用户：</td>
					<td>${val1}</td>
				</tr>
			</tbody>
		</table>
	</div>


</body>
</html>