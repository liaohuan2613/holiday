<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<div class="panel">
	<div class="panel-body table-responsive">
		<div class="box-tools m-b-15">
			<div class="panel-body btn-gap">
				<button class="btn btn-info" type="button">新建</button>
				<button class="btn btn-info" type="button">修改</button>
				<button class="btn btn-info" type="button">删除</button>
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
					<th style="width: 10%">Task</th>
					<th style="width: 20%">Progress</th>
					<th style="width: 20%">Label</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td><input type="checkbox" name="cbid"></td>
					<td>Update software</td>
					<td>
						<div class="progress xs">
							<div class="progress-bar progress-bar-danger" style="width: 55%"></div>
						</div>
					</td>
					<td><span class="badge bg-red">55%</span></td>
				</tr>
			</tbody>
		</table>
	</div>
</div>