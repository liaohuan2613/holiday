<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
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
	
	function initMain() {
		$.ajax({
			url : basePath + "user/index.do",
			success : function (data){
				$("body").html(data);
			}
		});
	}

	function loginFunction() {
		$("#register").on("click", function() {
			$.ajax({
				url : basePath + "user/register.do",
				success : function(data) {
					$("body").html(data);
				}
			});
		});
	}
</script>

<style type="text/css">
#error {
	display: none;
}

#main {
	overflow: hidden;
}
</style>
</head>
<body class="skin-black">
	<div id="main">
		<div class="panel center-block" style="width:40%;margin-top: 10%;">
			<header class="panel-heading">微博通</header>
			<div class="panel-body form-horizontal">
				<form class="form-horizontal" method="post" action="<%= basePath%>user/login.do">
				<div class="form-group">
					<label class="col-lg-3 col-sm-3 control-label">用户名：</label>
					<div class="col-lg-7">
						<input type="text" name="username" class="form-control"
							placeholder="用户名">
					</div>
				</div>
				<div class="form-group">
					<label class="col-lg-3 col-sm-3 control-label">密码：</label>
					<div class="col-lg-7">
						<input type="password" name="pwd" class="form-control"
							placeholder="密码">
					</div>
				</div>
				<div class="form-group">
					<div class="col-lg-offset-2 col-lg-10">
						<button type="submit" class="btn btn-danger" id="login">登陆</button>
						<button type="button" class="col-lg-offset-5 btn btn-success"
							id="register">注册</button>
					</div>
				</div>
				</form>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		loginFunction();
	</script>
</body>
</html>
