/**
 * 固定表头与列名
 * @param TableID table表id(注意table下在的thead代码模块默认为横向固定块)
 * @param FixColumnNumber table表列固定,从左往右固定列的总合数
 * @param width table宽度
 * @param height table高度
 */
function FixTable(TableID, FixColumnNumber, width, height) {
	width=$(document).width()-273;
	var h = $("#" + TableID).offset().top;
	height=window.screen.height-h-(150);
	if ($("#" + TableID + "_tableLayout").length != 0) {
		$("#" + TableID + "_tableLayout").before($("#" + TableID));
		$("#" + TableID + "_tableLayout").empty();
	} else {
		$("#" + TableID).after("<div id='" + TableID + "_tableLayout' style='overflow:hidden;height:" + height + "px; width:" + width + "px;'></div>");
	}
	$('<div id="' + TableID + '_tableFix"></div>'
		+ '<div id="' + TableID + '_tableHead"></div>'
		+ '<div id="' + TableID + '_tableColumn"></div>'
		+ '<div id="' + TableID + '_tableData"></div>')
		.appendTo("#" + TableID + "_tableLayout");
	var oldtable = $("#" + TableID);
	var tableFixClone = oldtable.clone(true);
	tableFixClone.attr("id", TableID + "_tableFixClone");
	$("#" + TableID + "_tableFix").append(tableFixClone);
	var tableHeadClone = oldtable.clone(true);
	tableHeadClone.attr("id", TableID + "_tableHeadClone");
	$("#" + TableID + "_tableHead").append(tableHeadClone);
	var tableColumnClone = oldtable.clone(true);
	tableColumnClone.attr("id", TableID + "_tableColumnClone");
	$("#" + TableID + "_tableColumn").append(tableColumnClone);
	$("#" + TableID + "_tableData").append(oldtable);
	$("#" + TableID + "_tableLayout table").each(function() {
		$(this).css("margin", "0");
	});
	var HeadHeight = $("#" + TableID + "_tableHead thead").height();
	HeadHeight += 2;
	$("#" + TableID + "_tableHead").css("height", HeadHeight);
	$("#" + TableID + "_tableFix").css("height", HeadHeight);
	var ColumnsWidth = 0;
	var ColumnsNumber = 0;
	$("#" + TableID + "_tableColumn tr:last td:lt(" + FixColumnNumber + ")")
			.each(function() {
				ColumnsWidth += $(this).outerWidth(true);
				ColumnsNumber++;
			});
	ColumnsWidth += 2;
	if ($.browser.msie) {
		switch ($.browser.version) {
		case "7.0":
			if (ColumnsNumber >= 3)
				ColumnsWidth--;
			break;
		case "8.0":
			if (ColumnsNumber >= 2)
				ColumnsWidth--;
			break;
		}
	}
	$("#" + TableID + "_tableColumn").css("width", ColumnsWidth);
	$("#" + TableID + "_tableFix").css("width", ColumnsWidth);
	$("#" + TableID + "_tableData").scroll(
			function() {
				$("#" + TableID + "_tableHead").scrollLeft(
						$("#" + TableID + "_tableData").scrollLeft());
				$("#" + TableID + "_tableColumn").scrollTop(
						$("#" + TableID + "_tableData").scrollTop());
			});
	$("#" + TableID + "_tableFix").css({
		"overflow" : "hidden",
		"position" : "relative",
		"z-index" : "50",
		"background-color" : "#f9f9f9"
	});
	$("#" + TableID + "_tableHead").css({
		"overflow" : "hidden",
		"width" : width - 17,
		"position" : "relative",
		"z-index" : "45",
		"background-color" : "#f9f9f9"
	});
	$("#" + TableID + "_tableColumn").css({
		"overflow" : "hidden",
		"height" : height - 17,
		"position" : "relative",
		"z-index" : "40",
		"background-color" : "#f9f9f9"
	});
	$("#" + TableID + "_tableData").css({
		"overflow" : "scroll",
		"width" : width,
		"height" : height,
		"position" : "relative",
		"z-index" : "35"
	});
	if ($("#" + TableID + "_tableHead").width() > $("#" + TableID + "_tableFix table").width()) {
		$("#" + TableID + "_tableHead").css("width",$("#" + TableID + "_tableFix table").width());
		$("#" + TableID + "_tableData").css("width",$("#" + TableID + "_tableFix table").width() + 17);
	}
	if ($("#" + TableID + "_tableColumn").height() > $("#" + TableID + "_tableColumn table").height()) {
		$("#" + TableID + "_tableColumn").css("height",$("#" + TableID + "_tableColumn table").height());
		$("#" + TableID + "_tableData").css("height",$("#" + TableID + "_tableColumn table").height() + 17);
	}
	$("#" + TableID + "_tableFix").offset($("#" + TableID + "_tableLayout").offset());
	$("#" + TableID + "_tableHead").offset($("#" + TableID + "_tableLayout").offset());
	$("#" + TableID + "_tableColumn").offset($("#" + TableID + "_tableLayout").offset());
	$("#" + TableID + "_tableData").offset($("#" + TableID + "_tableLayout").offset());
}
/**
 * 合并table列
 * @param _w_table_id table表id
 * @param _w_table_colnum 合并第几列(注意，合并多列调用时，列标识请从大到小依次调用如：TableRowspan(tid,2);TableRowspan(tid,1))
 */
function TableRowspan(_w_table_id,_w_table_colnum){
    _w_table_firsttd = "";//tr列中第个连续相同td对象中的第一个对象
    _w_table_currenttd = "";//当前tr列循环的td对象
    _w_table_SpanNum = 0;//列相同td值循环次数
    _w_table_Obj = $(_w_table_id + " tr td:nth-child(" + _w_table_colnum + ")");
    var i=0;
    _w_table_Obj.each(function(i){
        if(i==0){
            _w_table_firsttd = $(this);
            _w_table_SpanNum = 1;
        }else{
            _w_table_currenttd = $(this);     
            if(_w_table_firsttd.text()==_w_table_currenttd.text()){
                _w_table_SpanNum++;
                //_w_table_currenttd.hide();
                _w_table_currenttd.remove();
                _w_table_firsttd.attr("rowspan",_w_table_SpanNum);
            }else{
                _w_table_firsttd = $(this);
                _w_table_SpanNum = 1;
            }
        }
    });
}

var idTmr;
/**
 * 整个表格拷贝到EXCEL中
 * @param tableid table表id
 */
function exportExcel(tableid) {
	if (getExplorer() == 'ie') {
		var curTbl = document.getElementById(tableid);
		var oXL = new ActiveXObject("Excel.Application");

		// 创建AX对象excel
		var oWB = oXL.Workbooks.Add();
		// 获取workbook对象
		var xlsheet = oWB.Worksheets(1);
		// 激活当前sheet
		var sel = document.body.createTextRange();
		sel.moveToElementText(curTbl);
		// 把表格中的内容移到TextRange中
		sel.select();
		// 全选TextRange中内容
		sel.execCommand("Copy");
		// 复制TextRange中内容
		xlsheet.Paste();
		// 粘贴到活动的EXCEL中
		oXL.Visible = true;
		// 设置excel可见属性

		try {
			var fname = oXL.Application.GetSaveAsFilename("execl.xls","Excel Spreadsheets (*.xls), *.xls");
		} catch (e) {
			print("Nested catch caught " + e);
		} finally {
			oWB.SaveAs(fname);

			oWB.Close(savechanges = false);
			// xls.visible = false;
			oXL.Quit();
			oXL = null;
			// 结束excel进程，退出完成
			// window.setInterval("Cleanup();",1);
			idTmr = window.setInterval("Cleanup();", 1);
		}
	} else {
		tableToExcel(tableid);
	}
}
function getExplorer() {
	var explorer = window.navigator.userAgent;
	// ie
	if (explorer.indexOf("MSIE") >= 0) {
		return 'ie';
	}
	// firefox
	else if (explorer.indexOf("Firefox") >= 0) {
		return 'Firefox';
	}
	// Chrome
	else if (explorer.indexOf("Chrome") >= 0) {
		return 'Chrome';
	}
	// Opera
	else if (explorer.indexOf("Opera") >= 0) {
		return 'Opera';
	}
	// Safari
	else if (explorer.indexOf("Safari") >= 0) {
		return 'Safari';
	}
}
function Cleanup() {
	window.clearInterval(idTmr);
	CollectGarbage();
}
var tableToExcel = (function() {
	var uri = 'data:application/vnd.ms-excel;base64,', 
	template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>', 
	base64 = function(s) {
		return window.btoa(unescape(encodeURIComponent(s)))
	}, format = function(s, c) {
		return s.replace(/{(\w+)}/g, function(m, p) {
			return c[p];
		})
	};
	return function(table, name) {
		if (!table.nodeType)
			table = document.getElementById(table);
		var ctx = {
			worksheet : name || 'Worksheet',
			table : table.innerHTML
		};
		var excelCont = format(template, ctx);
		$.ajax({
	        type: "post",
	        dataType: "html",
	        url: basePath+"core/home/setExcelDownLoadCont.do",
	        data: {
	        	excelCont : excelCont
	        },
	        success: function (data) {
	        	window.location.href = basePath+"core/home/pageDownloadExcel.do";
	        }
	    });
	}
})();