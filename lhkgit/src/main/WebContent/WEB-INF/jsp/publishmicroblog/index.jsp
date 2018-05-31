<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
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

	function add() {
		$.ajax({
			url : basePath + "publishmicroblog/addOrEdit.do",
			success : function(data) {
				$(".content").html(data);
			}
		});
	}

	function edit() {
		var length = $("input[name='cbid']:checked").size();
		if (length != 1) {
			alert("请选择一条数据进行操作！");
		} else {
			var id = $("input[name='cbid']:checked").val();
			$.ajax({
				url : basePath + "publishmicroblog/addOrEdit.do",
				data : {
					"id" : id
				},
				success : function(data) {
					$(".content").html(data);
				}
			});
		}
	}

	function remove() {
		var length = $("input[name='cbid']:checked").size();
		if (length < 1) {
			alert("请选择至少一条数据进行操作！");
		} else {
			var ids = "";
			for (var i = 0; i < length; i++) {
				var id = $("input[name='cbid']:checked").eq(i).val();
				if (i == 0) {
					ids += id;
				} else {
					ids += "," + id;
				}
			}
			$.ajax({
				url : basePath + "publishmicroblog/delete.do",
				data : {
					"ids" : ids
				},
				success : function(data) {
					if (data == "") {
						alert("删除成功！");
						initTable();
					}
				}
			});
		}
	}
</script>
<div class="panel">
	<div class="panel-body table-responsive">
		<div class="box-tools m-b-15">
			<div class="panel-body btn-gap">
				<button class="btn btn-info" type="button" onclick="add()">新建</button>
				<button class="btn btn-info" type="button" onclick="edit()">修改</button>
				<button class="btn btn-info" type="button" onclick="remove()">删除</button>
			</div>

			<div class="input-group">
				<!-- <input type="text" name="table_search" class="form-control input-sm pull-right" style="width: 150px;" placeholder="Search">
                                <div class="input-group-btn">
                                    <button class="btn btn-sm btn-default"><i class="fa fa-search"></i></button>
                                </div> -->
			</div>
		</div>
		<table class="table table-bordered">
			<thead>
				<tr>
					<th style="width: 2%"><input type="checkbox"
						name="checkboxAll" class="checkboxAll"></th>
					<th style="width: 10%">用户名</th>
					<th style="width: 20%">作者姓名</th>
					<th style="width: 20%">微博标题</th>
					<th style="width: 20%">来源作者</th>
					<th style="width: 20%">发布时间</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${microBlogList }" var="microBlog">
					<tr>
						<td><input type="checkbox" name="cbid"
							value="${microBlog.id }"></td>
						<td>${microBlog.username }</td>
						<td>${microBlog.fullname }</td>
						<td>${microBlog.title }</td>
						<td>${microBlog.parent_fullname }</td>
						<td>${microBlog.publish_date }</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>