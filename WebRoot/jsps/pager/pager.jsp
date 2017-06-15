<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
	function _go() {
		var currentPage = $("#pageCode").val();//获取文本框中的当前页码
		if(!/^[1-9]\d*$/.test(currentPage)) {//对当前页码进行整数校验
			alert('请输入正确的页码！');
			return;
		}
		if(currentPage > ${pb.getPageCount()}) {//判断当前页码是否大于最大页
			alert('请输入正确的页码！');
			return;
		}
		location = "${pb.url }&currentPage="+currentPage;
	}
</script>


<div class="divBody">
	<div class="divContent">
		<%--上一页 --%>
		<c:choose>
			<c:when test="${pb.currentPage eq 1 }">
				<span class="spanBtnDisabled">上一页</span>
			</c:when>
			<c:otherwise>
				<a href="${pb.url }&currentPage=${pb.currentPage-1}"
					class="aBtn bold">上一页</a>
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test="${pb.getPageCount() <= 6 }">
				<c:set var="begin" value="1" />
				<c:set var="end" value="${pb.getPageCount()}" />
			</c:when>
			<c:otherwise>
				<c:set var="begin" value="${pb.currentPage-2 }" />
				<c:set var="end" value="${pb.currentPage + 3}" />
				<c:if test="${begin < 1 }">
					<c:set var="begin" value="1" />
					<c:set var="end" value="6" />
				</c:if>
				<c:if test="${end > pb.getPageCount() }">
					<c:set var="begin" value="${pb.getPageCount()-5 }" />
					<c:set var="end" value="${pb.getPageCount() }" />
				</c:if>
			</c:otherwise>
		</c:choose>

		<c:forEach begin="${begin}" end="${end}" var="i">
			<c:choose>
				<c:when test="${i eq pb.currentPage}">
					<span class="spanBtnSelect">${i}</span>
				</c:when>
				<c:otherwise>
					<a href="${pb.url }&currentPage=${i}" class="aBtn">${i}</a>
				</c:otherwise>
			</c:choose>


		</c:forEach>

		<%-- 显示点点点 --%>
		<c:if test="${end < pb.getPageCount()}">
		<span class="spanApostrophe">...</span>
		</c:if>
		<%--下一页 --%>
		<c:choose>
			<c:when test="${pb.currentPage eq pb.getPageCount()}">
				<span class="spanBtnDisabled">下一页</span>
			</c:when>
			<c:otherwise>
				<a href="${pb.url}&currentPage=${pb.currentPage+1}"
					class="aBtn bold">下一页</a>
			</c:otherwise>
		</c:choose>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

		<%-- 共N页 到M页 --%>
		<span>共${pb.getPageCount()}页</span> <span>到</span> <input type="text"
			class="inputPageCode" id="pageCode" value="${pb.currentPage}" /> <span>页</span>
		<a href="javascript:_go();" class="aSubmit">确定</a>
	</div>
</div>