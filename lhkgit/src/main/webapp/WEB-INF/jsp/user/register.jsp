<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<div class="panel center-block" style="width:50%;margin-top: 10%;">
	<header class="panel-heading">微博通</header>
	<div class="panel-body form-horizontal">
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">用户名：</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" placeholder="用户名" name="username">
			</div>
		</div>
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">密码：</label>
			<div class="col-lg-10">
				<input type="password" class="form-control" placeholder="密码" name="pwd">
			</div>
		</div>
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">确认密码：</label>
			<div class="col-lg-10">
				<input type="password" class="form-control" placeholder="确认密码" name="rePwd">
			</div>
		</div>
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">真实姓名：</label>
			<div class="col-lg-10">
				<input type="text" class="form-control" placeholder="真实姓名" name="fullname">
			</div>
		</div>
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">联系电话：</label>
			<div class="col-lg-10">
				<input type="number" class="form-control" placeholder="联系电话" name="phone">
			</div>
		</div>
		<div class="form-group">
			<label class="col-lg-2 col-sm-2 control-label">性别：</label>
			<div class="col-lg-10">
				<select class="form-control m-b-10" name="sex">
					<option value="男">男</option>
					<option value="女">女</option>
				</select>
			</div>
		</div>
		<div class="form-group">
			<div class="col-lg-offset-2 col-lg-10">
				<button id="register" type="button" class="btn btn-danger" onclick="register()">注册</button>
				<button type="button" class="col-lg-offset-5 btn btn-success"
					id="reset">重置</button>
			</div>
		</div>
		<p class="text-center text-danger" id="error">账号或密码错误！</p>
	</div>
</div>
<script type="text/javascript">
$("#register").on("click",function(){
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
	} else {
		var rePwd = $("input[name='rePwd']").val();
		if (rePwd != pwd) {
			$("#error").show();
			$("#error").text("两次输入的密码不一致！");
			return;
		}
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
	if (!sex) {
		$("#error").show();
		$("#error").text("请选择性别！");
		return;
	}
	$.ajax({
		url : basePath + "user/save.do",
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
				alert("用户创建成功！");
				initMain();
			} else {
				$("#error").html(data);
			}
		}
	});
});
</script>