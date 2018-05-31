<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<script type="text/javascript">
	function publishComment() {
		var microBlogId = $("input[name='id']").val();
		var fullname = $("#fullname").text();
		var content = $("textarea[name='content']").val();
		if (content == "" || content == null) {
			alert("您还没有输入任何信息，无法发布!");
			return false;
		}
		$.ajax({
			url : basePath + "comment/save.do",
			data : {
				"content" : content,
				"micro_blog_id" : microBlogId
			},
			success : function(data) {
				if (data == "") {
					var newCommentHtml = "<li class='media'><div class='media-body'>"
						+ "<span class='text-muted pull-right'><small><em>1 minute ago</em></small></span>"
						+ "<strong>" + fullname + "</strong><p class='con'>" + content + "</p></div><hr></li>";
					$("#newComment").before(newCommentHtml);
					$("textarea[name='content']").val("");
					alert("发表评论成功");
				} else {
					alert(data);
				}
			}
		});
	}

	function transmit() {
		var parentMicroBlogId = $("input[name='id']").val();
		$.ajax({
			url : basePath + "totalmicroblog/transmit.do",
			data : {
				"parentMicroBlogId" : parentMicroBlogId
			},
			success : function(data) {
				alert(data);
				if (data == "") {
					alert("转发成功，是否跳转至转发界面!");
				} else {
					alert("转发失败，请重试!");
				}
			}
		});
	}

	function detailMicroBlog(id) {
		$.ajax({
			url : basePath + "totalmicroblog/detailMicroBlog.do",
			data : {
				"id" : id
			},
			success : function(data) {
				$(".content").html(data);
			}
		})
	}
</script>
<div class="panel">
	<input type="hidden" name="id" value="${microBlog.id }">
	<header class="panel-heading">微博详情</header>
	<div class="panel-body">
		<span class="text-muted pull-right"> <small><em>${microBlog.publish_date }</em></small></span>
		<h2 class="text-center">${microBlog.title }</h2>
		<h4 class="text-center">作者：${microBlog.fullname }</h4>
		<p class="lead">简介：${microBlog.summary }</p>
		<c:if
			test="${microBlog.parent_fullname != null && microBlog.parent_fullname != '' }">
			<a href="javascript:void(0)" onclick=" detailMicroBlog(${microBlog.parent_id})">@转发源：${microBlog.parent_fullname }</a>
		</c:if>
		<hr>
		<p>${microBlog.content }</p>
		<div class="panel panel-success">
			<header class="panel-heading">评论区</header>
			<div class="panel-body">
				<ul class="media-list">
					<c:forEach items="${commentList }" var="comment">
						<li class="media">
							<div class="media-body">
								<span class="text-muted pull-right"> <small><em>${comment.publish_date }</em></small></span>
								<strong>${comment.fullname }</strong>
								<p class="con">${comment.content }</p>
							</div>
							<hr>
						</li>
					</c:forEach>
					<li class="media" id="newComment">
						<div class="media-body">
							<textarea class="form-control" rows="3" id="content"
								name="content" placeholder="请输入内容"></textarea>
							<a href="javascript:void(0)" onclick="publishComment()">发表</a>
							<c:if test="${user_info.id != microBlog.user_id }">
								<a href="javascript:void(0)" onclick="transmit()">转发</a>
							</c:if>
						</div>
					</li>
				</ul>
			</div>
		</div>
	</div>
</div>