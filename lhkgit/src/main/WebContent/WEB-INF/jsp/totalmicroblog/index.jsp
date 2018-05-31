<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<script type="text/javascript">
	function detailMicroBlog(id) {
		$.ajax({
			url : basePath + "totalmicroblog/detailMicroBlog.do",
			data : {"id" : id },
			success : function (data) {
				$(".content").html(data);
			}
		})
	}
</script>

<div class="panel">
	<header class="panel-heading">微博总览</header>
	<div class="panel-body">
		<ul class="media-list">
			<c:forEach items="${microBlogList }" var="microBlog">
				<li class="media" style="cursor: pointer;" onclick="detailMicroBlog(${microBlog.id})">
					<div class="media-body">
						<span class="text-muted pull-right"> <small><em>${microBlog.publish_date }</em></small>
						</span>
						<strong>标题：${microBlog.title }&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;作者：${microBlog.fullname }</strong>
						<p>简介：${microBlog.summary }</p>
						<c:if test="${microBlog.parent_user_id != null }">
							<p>@转发源：${microBlog.parent_fullname }</p>
						</c:if>
					</div>
				</li>
				<hr>
			</c:forEach>
		</ul>
	</div>
</div>