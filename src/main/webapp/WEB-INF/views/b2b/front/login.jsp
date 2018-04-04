<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>用户登录</title>
<meta name="decorator" content="default" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${ctxStatic}/font-awesome/dist/css/font-awesome.css" type="text/css" rel="stylesheet" />
<link href="${ctxStatic}/custom/css/b2b-front/login.css" type="text/css" rel="stylesheet" />

<link href="${ctxStatic}/jquery-validation/custom/jquery.validate.css" type="text/css" rel="stylesheet" />
<script src="${ctxStatic}/jquery-validation/dist/jquery.validate.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/jquery-validation/custom/jquery.validate.method.js" type="text/javascript"></script>

<script type="text/javascript">
	$(document).ready(function() {

		<c:if test="${not empty message}">
		$("#messageBox").removeClass("hide");
		</c:if>

		$("#loginName").focus();
		$("#loginForm").validate({
			rules : {
				validateCode : {
					remote : "${pageContext.request.contextPath}/servlet/validateCodeServlet"
				}
			},
			messages : {
				username : {
					required : "请填写用户名."
				},
				password : {
					required : "请填写密码."
				},
				validateCode : {
					remote : "验证码不正确.",
					required : "请填写验证码."
				}
			},
			submitHandler : function(form) {
				$("#username").val($("#username").val().toLowerCase());
				form.submit();
			},
			errorContainer : "#messageBox1",
			errorPlacement : function(error, element) {
				$(function() {
					$(element).tooltip('show');
				});
			}
		});
	});
</script>

</head>
<body>

	<div class="container login-form" style="margin-top: 3em; margin-bottom: 3em;">
		<form id="loginForm" action="${ftx}/login" method="post" class="form-horizontal">
			<input id="refererUrl" name="refererUrl" type="hidden" />

			<div class="alert alert-info center hide" id="messageBox">
				<button class="close" data-dismiss="alert">×</button>${message}</div>

			<div class="form-group">
				<label class="col-md-3 col-sm-3 control-label">
					<b>*</b>登录名
				</label>
				<div class="col-md-5 col-sm-6">
					<input id="username" name="username" type="text" class="form-control required" data-toggle="tooltip" data-trigger="manual" data-original-title="请输入登录名" onmouseover="$(this).tooltip('hide');"
						value="${username}">
				</div>
			</div>
			<div class="form-group">
				<label class="col-md-3 col-sm-3 control-label">
					<b>*</b>密码
				</label>
				<div class="col-md-5 col-sm-6">
					<input id="password" name="password" type="password" class="form-control required" data-toggle="tooltip" data-trigger="manual" data-original-title="请输入密码" onmouseover="$(this).tooltip('hide');">
				</div>
			</div>
			<c:if test="${isValidateCodeLogin}">
				<div class="form-group">
					<label class="col-md-3 col-sm-3 control-label">
						<b>*</b>验证码
					</label>
					<div class="col-md-5 col-sm-6 row">
						<div class="col-xs-7">
							<input type="text" id="validateCode" name="validateCode" class="form-control required" minlength="4" placeholder="请填写图片中的字符，不区分大小写" data-toggle="tooltip" data-trigger="manual"
								data-original-title="请输入正确的验证码" onmouseover="$(this).tooltip('hide');">
						</div>
						<div class="col-xs-5" style="padding-left: 0;">
							<img id="validateCodeImg" src="/servlet/validateCodeServlet" onclick="$(this).attr('src','/servlet/validateCodeServlet?'+new Date().getTime());$('#validateCode').val('');" style="height: 2.3em;">
						</div>
					</div>
				</div>
			</c:if>
			<div class="form-group">
				<div class="col-sm-offset-3 col-sm-6">
					<input class="btn btn-info login-submit" type="submit" value="登   录">
				</div>
			</div>
		</form>
	</div>
</body>
</html>