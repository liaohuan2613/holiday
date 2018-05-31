<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<div class="panel">
	<header class="panel-heading">我的微博</header>
	<div class="panel-body form-horizontal">
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">用户名：</label>
			<div class="col-lg-10">
				<input type="text" name="username" class="form-control" placeholder="用户名"
					value="${user_info.username }">
			</div>
		</div>
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">真实姓名：</label>
			<div class="col-lg-10">
				<input type="text" name="fullname" class="form-control" placeholder="真实姓名"
					value="${user_info.fullname }">
			</div>
		</div>
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">联系电话：</label>
			<div class="col-lg-10">
				<input type="number" name="phone" class="form-control" placeholder="联系电话"
					value="${user_info.phone }">
			</div>
		</div>
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">性别：</label>
			<div class="col-lg-10">
				<select class="form-control m-b-10" name="sex">
					<c:if test="${user_info.sex != '女' }">
						<option value="男" selected="selected">男</option>
						<option value="女">女</option>
					</c:if>
					<c:if test="${user_info.sex == '女' }">
						<option value="男">男</option>
						<option value="女" selected="selected">女</option>
					</c:if>
				</select>
			</div>
		</div>
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">密码：</label>
			<div class="col-lg-10">
				<input type="password" class="form-control" name="pwd" placeholder="密码" value="${user_info.pwd }">
			</div>
		</div>
		<div class="form-group">
			<div class="col-lg-offset-2 col-lg-10">
				<button type="button" class="btn btn-danger" id="save">保存</button>
				<button type="button" class="col-lg-offset-2 btn btn-success">还原</button>
			</div>
		</div>
	</div>
	<p class="text-center text-danger" id="error">账号或密码错误！</p>
</div>
<script type="text/javascript">
$("#save").on("click", function(){
	var username = $("input[name='username']").val();
	if (!username) {
		$("#error").show();
		$("#error").text("请输入用户名！");
		return;
	}
	
	var pwd = $("input[name='pwd']").val();
	if (!pwd) {
		$("#error").show();
		$("#error").text("请输入密码！");
		return;
	}
	
	var fullname = $("input[name='fullname']").val();
	if (!fullname) {
		$("#error").show();
		$("#error").text("请输入真实姓名！");
		return;
	}
	var phone = $("input[name='phone']").val();
	if (!phone) {
		$("#error").show();
		$("#error").text("请输入联系电话！");
		return;
	}
	var sex = $("select[name='sex']").val();
	$.ajax({
		url : basePath + "mymicroblog/save.do",
		type: "post",
		data : {
			"username" : username,
			"fullname" : fullname,
			"pwd" : pwd,
			"sex" : sex,
			"phone" : phone
		},
		success:function(data) {
			if (data == "") {
				$("#fullname").text(fullname);
				alert("修改成功！");
			} else {
				$("#error").html(data);
			}
		}
	});
});
</script>