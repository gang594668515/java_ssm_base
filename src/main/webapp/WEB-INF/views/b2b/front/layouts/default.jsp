<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><sitemesh:title /> - 项目名称</title>
<link href="${ctxStatic}/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="${ctxStatic}/custom/css/include/global.css" rel="stylesheet" type="text/css" />
<link href="${ctxStatic}/custom/css/b2b-front/include/header.css" rel="stylesheet" type="text/css" />
<link href="${ctxStatic}/custom/css/b2b-front/include/footer.css" rel="stylesheet" type="text/css" />

<script src="${ctxStatic}/jquery/dist/jquery.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/bootstrap/dist/js/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/third-party/browser-info.js" type="text/javascript"></script>


<script type="text/javascript">
	$(document).ready(function() {
		//检测新消息
		var principal = "${fns:getPrincipal()}";
		if (principal != null && principal != "") {
			checkNewMsg(); //首次立即加载  
			window.setInterval(checkNewMsg, 60000); //循环执行！！
		}

		// 检测浏览器兼容性
		checkBrowser();
	});

	function checkNewMsg() {
		$.ajax({
			type : "POST",
			dataType : "json",
			url : "${ftx}/newMsg",
			timeout : 80000,
			success : function(data) {
				$(".header-msg-count").text(data.newMsg);
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				if (textStatus == "timeout") {
					checkNewMsg();
				}
			}
		});
	}
</script>
<sitemesh:head />
</head>
<body>
	<%@ include file="../include/header.jsp"%>
	<%-- 中间body --%>
	<sitemesh:body />
	<%@ include file="../include/footer.jsp"%>
</body>
</html>