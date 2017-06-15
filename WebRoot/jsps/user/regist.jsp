<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title>注册</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<meta http-equiv="content-type" content="text/html;charset=utf-8">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<link rel="stylesheet" type="text/css" href="<c:url value='/css/css.css'/>">
<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/user/regist.css'/>">
<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
<script type="text/javascript" src="<c:url value='/jsps/js/user/regist.js'/>"></script>
</head>

<body>
	<div class="divBody">
		<div class="divTitle">
			<span class="spanTitle">新用户注册</span>
		</div>
		<div class="divCenter">
			<form action="<c:url value='/UserServlet'/>" method="post" id="registForm">
				<input type="hidden" name="method" value="regist" />
				<table>
					<tr>
						<td class="tdLabel">用户名：</td>
						<td class="tdInput"><input type="text" name="loginname"
							id="loginname" class="input" value="" /></td>
						<td class="tdError"><label class="labelError"
							id="loginnameError"></label></td>
					</tr>
					<tr>
						<td class="tdLabel">登录密码：</td>
						<td class="tdInput"><input type="password" name="loginpass"
							id="loginpass" class="input" value="" /></td>
						<td class="tdError"><label class="labelError"
							id="loginpassError"></label></td>
					</tr>
					<tr>
						<td class="tdLabel">确认密码：</td>
						<td class="tdInput"><input type="password" name="reloginpass"
							id="reloginpass" class="input" value="" /></td>
						<td class="tdError"><label class="labelError"
							id="reloginpassError"></label></td>
					</tr>
					<tr>
						<td class="tdLabel">手机号码：</td>
						<td class="tdInput"><input type="text" name="phone"
							id="phone" class="input" value="" /></td>
						<td class="tdError"><label class="labelError" id="phoneError"></label>
						</td>
					</tr>
					<tr>
						<td class="tdLabel">图形验证码：</td>
						<td class="tdInput"><input type="text" name="verifyCode"
							id="verifyCode" class="input" value="" /></td>
						<td class="tdError"><label class="labelError"
							id="verifyCodeError"></label></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><span class="verifyCodeImg"><img id="vCode"
								width="100" src="<c:url value='/VerifyCodeServlet'/>" />
						</span></td>
						<td><a href="javascript:_changeVeriryCodeImg()">换一张</a>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>
							<input type="image" src="<c:url value='/images/regist1.jpg'/>" id="submit" />
						</td>
						<td>&nbsp;</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</body>
</html>
