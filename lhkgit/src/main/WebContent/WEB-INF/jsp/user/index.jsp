<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<title>微博通</title>
<meta
	content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no'
	name='viewport'>
<meta name="description" content="Developed By M Abdur Rokib Promy">
<meta name="keywords"
	content="Admin, Bootstrap 3, Template, Theme, Responsive">
<!-- bootstrap 3.0.2 -->
<link href="<%= basePath%>css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<!-- font Awesome -->
<link href="<%= basePath%>css/font-awesome.min.css" rel="stylesheet" type="text/css" />
<!-- Ionicons -->
<link href="<%= basePath%>css/ionicons.min.css" rel="stylesheet" type="text/css" />
<!-- Morris chart -->
<link href="<%= basePath%>css/morris/morris.css" rel="stylesheet" type="text/css" />
<!-- jvectormap -->
<link href="<%= basePath%>css/jvectormap/jquery-jvectormap-1.2.2.css" rel="stylesheet"
	type="text/css" />
<!-- Date Picker -->
<link href="<%= basePath%>css/datepicker/datepicker3.css" rel="stylesheet"
	type="text/css" />
<!-- fullCalendar -->
<!-- <link href="css/fullcalendar/fullcalendar.css" rel="stylesheet" type="text/css" /> -->
<!-- Daterange picker -->
<link href="<%= basePath%>css/daterangepicker/daterangepicker-bs3.css"
	rel="stylesheet" type="text/css" />
<!-- iCheck for checkboxes and radio inputs -->
<link href="<%= basePath%>css/iCheck/all.css" rel="stylesheet" type="text/css" />
<!-- bootstrap wysihtml5 - text editor -->
<!-- <link href="css/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css" rel="stylesheet" type="text/css" /> -->
<link href="<%= basePath%>css/font-default.css" rel='stylesheet' type='text/css'>
<!-- Theme style -->
<link href="<%= basePath%>css/style.css" rel="stylesheet" type="text/css" />
<!-- jQuery 2.0.2 -->
<script src="<%= basePath%>js/jquery.min.js" type="text/javascript"></script>
<!-- jQuery UI 1.10.3 -->
<script src="<%= basePath%>js/jquery-ui-1.10.3.min.js" type="text/javascript"></script>
<!-- Bootstrap -->
<script src="<%= basePath%>js/bootstrap.min.js" type="text/javascript"></script>
<!-- daterangepicker -->
<script src="<%= basePath%>js/plugins/daterangepicker/daterangepicker.js"
	type="text/javascript"></script>

<script src="<%= basePath%>js/plugins/chart.js" type="text/javascript"></script>

<!-- datepicker
    <script src="js/plugins/datepicker/bootstrap-datepicker.js" type="text/javascript"></script>-->
<!-- Bootstrap WYSIHTML5
    <script src="js/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js" type="text/javascript"></script>-->
<!-- iCheck -->
<script src="<%= basePath%>js/plugins/iCheck/icheck.min.js" type="text/javascript"></script>
<!-- calendar -->
<script src="<%= basePath%>js/plugins/fullcalendar/fullcalendar.js"
	type="text/javascript"></script>

<!-- Director App -->
<script src="<%= basePath%>js/Director/app.js" type="text/javascript"></script>

<!-- Director dashboard demo (This is only for demo purposes) -->
<script src="<%= basePath%>js/Director/dashboard.js" type="text/javascript"></script>

<script type="text/javascript">
	var basePath = '<%=basePath%>';
	function initEvent() {
		$(".checkboxAll").click(function() {
			if ($(this).is(':checked')) {
				$("input[name='cbid']").prop("checked", true);
			} else {
				$("input[name='cbid']").prop("checked", false);
			}
		});
	}
	
	function initMain() {
		$.ajax({
			url : basePath + "user/index.do",
			success : function (data){
				$("body").html(data);
			}
		});
	}

	function loginFunction() {
		$("#login").on("click", function() {
			var username = $("input[name='username']").val();
			var pwd = $("input[name='pwd']").val();
			$.ajax({
				url : basePath + "user/login.do",
				type : "post",
				data : {
					"username" : username,
					"pwd" : pwd
				},
				success : function(data) {
					$("body").html(data);
				}
			});
		});

		$("#register").on("click", function() {
			$.ajax({
				url : basePath + "user/register.do",
				success : function(data) {
					$("body").html(data);
				}
			});
		});
	}
	
	function toMainUrl(url,obj) {
		$(obj).siblings().removeClass("active");
		$(obj).addClass("active");
		$.ajax({
			url : basePath + url,
			success : function(data) {
				$(".content").html(data);
				initEvent();
			}
		});
	}
	
	function changePwd() {
		$("#change_pwd_div").show();
	}
	
	function cancelChangePwd() {
		$("#change_pwd_div").hide();
	}
	
	function sureChangePwd() {
		var chPwd = $("input[name='chPwd']").val();
		var chRePwd = $("input[name='chRePwd']").val();
		if (chPwd != chRePwd) {
			$("#error").show();
			$("#error").html("两次输入的密码不一致！");
		} else {
			$.ajax({
				url : basePath + "user/chPwd.do",
				data : {
					"chPwd" : chPwd
				},
				success:function (data) {
					if (data == "") {
						alert("修改成功，请重新登陆！");
						window.location.href = basePath + "user/exit.do";
					} else {
						alert(data);
					}
				}
			});
		}
	}
</script>

<style type="text/css">
#error {
	display: none;
	text-align: center;
	color: red;
}

#main {
	overflow: hidden;
}

#change_pwd_div {
	height: 500px;
    left: 40%;
    position: fixed;
    top: 20%;
    width: 500px;
    z-index: 1000;
    display: none;
}
</style>
</head>
<body class="skin-black">
<!-- header logo: style can be found in header.less -->
<header class="header">
	<a href="#" class="logo"> 微博通 </a>
	<!-- Header Navbar: style can be found in header.less -->
	<nav class="navbar navbar-static-top" role="navigation">
		<!-- Sidebar toggle button-->
		<a href="#" class="navbar-btn sidebar-toggle" data-toggle="offcanvas"
			role="button"> <span class="sr-only">Toggle navigation</span> <span
			class="icon-bar"></span> <span class="icon-bar"></span> <span
			class="icon-bar"></span>
		</a>
		<div class="navbar-right">
			<ul class="nav navbar-nav">
				<!-- Messages: style can be found in dropdown.less-->
				<li class="dropdown user user-menu"><a href="#"
					class="dropdown-toggle" data-toggle="dropdown"> <i
						class="fa fa-user"></i> <span id="fullname">${user_info.fullname }<i
							class="caret"></i></span>
				</a>
					<ul class="dropdown-menu dropdown-custom dropdown-menu-right">
						<li class="dropdown-header text-center">用户登陆管理</li>
						<li class="divider"></li>
						<li onclick="changePwd()">
							<a href="javascript:void(0)" class="text-center">修改密码</a>
						</li>
						<li class="divider"></li>
						<li><a href="<%= basePath%>user/exit.do" class="text-center"></i>退出登陆</a></li>
					</ul></li>
			</ul>
		</div>
	</nav>
</header>
<div class="wrapper row-offcanvas row-offcanvas-left">
	<aside class="left-side sidebar-offcanvas">
		<section class="sidebar">
			<!-- sidebar menu: : style can be found in sidebar.less -->
			<ul class="sidebar-menu">
				<li class="active" onclick="toMainUrl('mymicroblog/index.do',this)"><a href="javascript:void(0)"> <i
						class="fa fa-gavel"></i> <span>我的微博</span>
				</a></li>
				<li onclick="toMainUrl('totalmicroblog/index.do',this)"><a href="javascript:void(0)"> <i
						class="fa fa-dashboard"></i> <span>微博总览</span>
				</a></li>
				<li onclick="toMainUrl('publishmicroblog/index.do',this)"><a href="javascript:void(0)"> <i
						class="fa fa-globe"></i> <span>微博发布</span>
				</a></li>
			</ul>
		</section>
		<!-- /.sidebar -->
	</aside>
	<aside class="right-side">
		<!-- Main content -->
		<section class="content"></section>
	</aside>
</div>
<div id="change_pwd_div">
	<div class="panel">
		<header class="panel-heading">我的微博</header>
		<div class="panel-body form-horizontal">
			<div class="form-group">
				<label class="col-sm-3 control-label">新密码</label>
				<div class="col-sm-9">
				<input type="text" name="chPwd" class="form-control"  placeholder="请输入新的密码">
				</div>
			</div>
			<div class="form-group">
				<label class="col-sm-3 control-label">确认密码</label>
				<div class="col-sm-9">
				<input type="text" name="chRePwd" class="form-control"  placeholder="请输入确认密码">
				</div>
			</div>
			<div class="form-group">
			   	<div class="col-sm-offset-2 col-sm-10">
					<button type="button" class="btn btn-info" onclick="sureChangePwd()">确认</button>
					<button type="button" class="btn btn-info" onclick="cancelChangePwd()">取消</button>
			    </div>
			</div>
			<p id="error"></p>
		</div>
	</div>
</div>
</body>
</html>
