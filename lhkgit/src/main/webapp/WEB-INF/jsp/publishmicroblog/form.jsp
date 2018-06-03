<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<div class="panel">
	<input type="hidden" name="id" value="${microBolg.id }">
	<header class="panel-heading">微博发布</header>
	<div class="panel-body">
		<form role="form">
			<div class="form-group">
				<label>微博标题</label> <input type="text" class="form-control"
					id="title" name="title" value="${microBlog.title }"
					placeholder="请输入标题">
			</div>
			<div class="form-group">
				<label>微博简介</label> <input type="text" class="form-control"
					id="summary" name="summary" value="${microBlog.summary }"
					placeholder="请输入简介">
			</div>
			<div class="form-group">
				<label>微博内容</label>
				<textarea class="form-control" rows="3" id="content" name="content"
					placeholder="请输入内容">${microBlog.content }</textarea>
			</div>
			<button type="button" class="btn btn-info" id="save">确定</button>
			<button type="button" class="btn btn-info" id="cancel">取消</button>
		</form>
	</div>
</div>
<script type="text/javascript">
	function initTable() {
		$.ajax({
			url : basePath + "publishmicroblog/index.do",
			success: function (data) {
				$(".content").html(data);
				initEvent();
			}
		});
	}
	
	$("#save").on("click",function(){
		var title = $("input[name='title']").val();
		var summary = $("input[name='summary']").val();
		var content = $("textarea[name='content']").val();
		$.ajax({
			url : basePath + "publishmicroblog/save.do",
			data : {
				"title" : title,
				"summary" : summary,
				"content" : content
			},
			success: function (data) {
				if (data == "") {
					alert("保存成功！");
					initTable();
				}
			}
		});
	});
	
	$("#cancel").on("click",function(){
		initTable();
	});
</script>