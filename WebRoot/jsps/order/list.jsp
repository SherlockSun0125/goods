<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>订单列表</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/pager/pager.css'/>" />
    <script type="text/javascript" src="<c:url value='/jsps/pager/pager.js'/>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/order/list.css'/>" />
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/css.css'/>" />

  </head>
  
  <body>
<p class="pLink" style="margin-left:0px;">
  <a href="<c:url value='/OrderServlet?method=findByStatus&status=123'/>">未完成订单</a>  | 
  <a href="<c:url value='/OrderServlet?method=findByStatus&status=4'/>">已完成订单</a>  | 
  <a href="<c:url value='/OrderServlet?method=findByStatus&status=5'/>">已取消订单</a>
</p>
<div class="divMain" style="margin-left:0px;margin-right:20px;width:100%;" >
	<div class="title" style="margin-left:0px;margin-right:0px;">
		<div style="margin-top:7px;">
			<span style="margin-left: 10px;margin-right: 320px;">商品信息</span>
			<span style="margin-left: 150px;margin-right: 100px;">金额</span>
			<span style="margin-left: 40px;">操作</span>
		</div>
	</div>
	<br/>
	<table align="center" border="0" width="100%" cellpadding="0" cellspacing="0">
<c:forEach items="${pb.beanList}" var="order">	
	
		<tr class="tt">
			<td width="320px">订单号：<a  href="<c:url value='/OrderServlet?method=loadOrder&oid=${order.oid }'/>">${order.oid }</a></td>
			<td width="200px">下单时间：${order.orderTime }</td>
			<td width="178px">&nbsp;</td>
			<td width="205px">&nbsp;</td>
			<td>&nbsp;</td>
		</tr>	

		<tr style="padding-top: 10px; padding-bottom: 10px;">
			<td colspan="2">


  <c:forEach items="${order.orderItemList}" var="orderItem">
	    <img border="0" width="70" src="<c:url value='/${orderItem.book.image_b }'/>"/>
  </c:forEach>

			</td>
			<td style="padding-left: 0">
				<span class="price_t">&yen;${order.total}</span>
				&nbsp;&nbsp;&nbsp;
				<c:choose>
				<c:when test="${order.status eq 1}">(等待付款)</c:when>
				<c:when test="${order.status eq 2}">(准备发货)</c:when>
				<c:when test="${order.status eq 3}">(等待确认)</c:when>
				<c:when test="${order.status eq 4}">(交易成功)</c:when>
				<c:when test="${order.status eq 5}">(已取消)</c:when>
			</c:choose>
			</td>

			<td>
				<a href="<c:url value='/OrderServlet?method=loadOrder&oid=${order.oid}'/>">查看</a><br/>
				<c:if test="${order.status eq 1 }">
					<a href="<c:url value='/OrderServlet?method=paymentPre&oid=${order.oid }'/>">支付</a><br/>
					<a href="<c:url value='/OrderServlet?method=loadOrder&oid=${order.oid}&btn=cancel'/>">取消</a><br/>	
				</c:if>	
				<c:if test="${order.status eq 3}">			
					<a href="<c:url value='/OrderServlet?method=loadOrder&oid=${order.oid}&btn=confirm'/>">确认收货</a><br/>
				</c:if>			
				<c:if test="${order.status eq 5}">			
					<a href="<c:url value='/OrderServlet?method=loadOrder&oid=${order.oid}&btn=recovery'/>">恢复至购物车</a><br/>
				</c:if>	
			</td>
		</tr>
</c:forEach>

	</table>
	<br/>
	<%@include file="/jsps/pager/pager.jsp"%>
</div>
  </body>
</html>
