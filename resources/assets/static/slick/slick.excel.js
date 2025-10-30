var GridExcel = (function(){
	var exportExcel = function(s){
		var exportList = s.exportList;
		var downloadName = s.downloadName;
		var excelMaker = new ExcelMaker(s);
		var workbook = excelMaker.makeWorkbook();
		var prevSheetName;
		var title0 = null;
		_.each(exportList, function(exportInfo, idx){//for(var exportInfo of exportList){
			var groupColumns = exportInfo.groupColumns;
			var columns = exportInfo.columns;
			var rows = exportInfo.rows;
			var sheetName = exportInfo.sheetName || 'sheetName';
			var title = exportInfo.title || 'titleName';
			if(idx == 0)title0 = exportInfo.title;
			// 시트명 동일하면 시트 새로 안만들고 기존거에다가 넣는다.
			if(sheetName != prevSheetName){
				excelMaker.makeSheet(sheetName);
			}
			var beforeTitleRows = exportInfo.beforeTitleRows;
			if(beforeTitleRows){
				excelMaker.makeSimpleRows(beforeTitleRows);
			}
			if(! (title == 'noTitle')){
				excelMaker.makeTitleRow(title, columns.length);
			}
			var beforeHeaderRows = exportInfo.beforeHeaderRows;
			if(beforeHeaderRows){
				excelMaker.makeSimpleRows(beforeHeaderRows);
			}
			if(groupColumns){
				excelMaker.makeGroupHeaderRow(groupColumns);
			}
			excelMaker.makeHeaderRow(columns);
			excelMaker.makeDataRows(columns, rows);
			prevSheetName = sheetName;
		});//}//for
		//var s0 = workbook.worksheets[0];
		//s0.getCell('A1').value = {formula: '1*2222'};
		workbook.xlsx.writeBuffer().then(function(buffer) {
			var filename = (downloadName || ((title0||'download')+'_'+new Date().getTime()))+'.xlsx';
			var blob = new Blob([buffer], {type: 'applicationi/xlsx'});
			uu.saveAsBlob(blob, filename);
		});
		return workbook;
	};
	var getArrayFromExcel = function(file, onArray){
		var reader = new FileReader();
		reader.onloadstart = function(){
			uu.progressCover(true);
		};
		reader.onerror = function(event){
			uu.progressCover(false);
			uu.flashMessageWarn(String(reader.error));
		};
		reader.onload = function(event){
			var arrayBuffer = reader.result;
			var workbook = new ExcelJS.Workbook();
			var array = [];
			uu.progressCover(true);
			workbook.xlsx.load(arrayBuffer).then(function(workbook){
				var sheet0 = workbook.worksheets[0];
				// workbook.worksheets.forEach(function(sheet){
				// });
				if(! uu.checkFlash(!! sheet0, '엑셀파일에 시트가 없습니다.'))return;
				var sheet = sheet0;
				sheet.eachRow({includeEmpty: true}, function(row, rowNumber){
					var cells = [];
					row.eachCell({ includeEmpty: true }, function(cell, colNumber) {
						cells.push(cell.value);
					});
					array.push(cells);
				});
				onArray(array);
			}).catch(function(error){
				uu.flashMessageWarn(String(error));
			}).finally(function(){
				uu.progressCover(false);
			});
		};
		reader.onloadend = function(){
			uu.progressCover(false);
		};
		reader.readAsArrayBuffer(file);
	};
	var ExcelMaker = function(s){
		var options = s;
		var workbook;
		var self = this;
		var sheet;
		var row;
		var cell;
		var rnum = 1;
		var cnum = 1;
		var cellFontSize = 10.5;//14px
		var dcStyles = s.dcStyles||{};// data cell style map
		if(s.cellFontSize)cellFontSize = s.cellFontSize;
		var makeWorkbook = function(){
			workbook = new ExcelJS.Workbook();
			return workbook;
		};
		var makeSheet = function(sheetName){
			rnum = 1;
			sheet = workbook.addWorksheet(sheetName);
		};
		var addRow = function(sheet){
			cnum = 1;
			return sheet.getRow(rnum++);
		};
		var addCell = function(row, cellStyle, value, colSpan){
			if(colSpan < 1)return null;
			var cell;
			for(var i = 0; i < colSpan; i++){
				cell = sheet.getRow(rnum-1).getCell(cnum);
				if(cellStyle != null){
					cell.style = cellStyle;
				}
				cell.value = value;
			}
			if(colSpan > 1){
				sheet.mergeCells(rnum-1, cnum, rnum-1, cnum + colSpan - 1);
			}
			cnum += colSpan;
			return cell;
		};
		var makeTitleRow = function(title, colspan){
			var titleCellStyle = makeTitleCellStyle();
			row = addRow(sheet);
			row.height = 30;
			cell = addCell(row, titleCellStyle, title, colspan ? colspan : 1);
		};
		var makeGroupHeaderRow = function(groupColumns){
			var headerCellStyle = makeHeaderCellStyle();
			_.each(groupColumns, function(groupColumns1){
				row = addRow(sheet);
				_.each(groupColumns1, function(groupColumn){
					var name = groupColumn.name;
					var colSpan = groupColumn.colSpan;
					cell = addCell(row, headerCellStyle, name, colSpan);
				});
			});
		};
		var makeHeaderRow = function(columns){
			var headerCellStyle = makeHeaderCellStyle();
			row = addRow(sheet);
			row.height = 30;
			var headerNames = [];
			_.each(columns, function(column){
				headerNames.push(column.name||'');
			});
			_.each(headerNames, function(headerName){
				cell = addCell(row, headerCellStyle, headerName, 1);
			});
		};
		var makeDataRows = function(columns, rows) {
			var numFmt = options.numFmt;
			var dataCellStyles = [];
			_.each(columns, function(column, idx){
				var cnum2 = idx + 1;
				dataCellStyles.push(makeCellStyle(column));
				var cwidth = column.width;
				if(cwidth == null){
					cwidth = 100;
				}
				sheet.getColumn(cnum2).width = cwidth / 8;
			});
			_.each(rows, function(cells){
				row = addRow(sheet);
				_.each(cells, function(cellValue, idx){
					var cellStyle = dataCellStyles[idx];
					if(cellValue == null){
						null;//
					}else if(typeof(cellValue) == 'object') {
						var cellMap = cellValue;
						cellValue = cellMap.v;
						var selfStyle = dcStyles[cellMap.styleName];
						if(selfStyle){
							cellStyle = $.extend({}, cellStyle, selfStyle);
						}
					}
					cell = addCell(row, cellStyle, cellValue, 1);
					// var column = columns[idx + 1];
					// if(numFmt && column && column.datatype == 'number'){
					// 	cell.numFmt = '#,##0.######';
					// }
				});
			});
			if(numFmt){
				_.each(columns, function(column, idx){
					var cnum2 = idx+1;
					if(column.datatype == 'number'){
						sheet.getColumn(cnum2).numFmt = numFmt;
					}
				});
			}
		};
		var makeSimpleRows = function(rows) {
			_.each(rows, function(cells){
				row = addRow(sheet);
				_.each(cells, function(cellValue){
					cell = addCell(row, null, cellValue, 1);
				})
			})
		};
		var makeTitleCellStyle = function(){
			return {
				font: {bold: true, size: 15},
				alignment: {vertical: 'middle', wrapText: false, horizontal: 'center'}
			};
		};
		var makeHeaderCellStyle = function(){
			var etc = {};
			if(cellFontSize > 0) {
				etc.font = {size: cellFontSize};
			}
			return $.extend({
				fill: {
					type: 'pattern',
					pattern: 'solid',
					fgColor: {argb: 'eeeeee'}//FFFF99
				},
				border: {top: {style:'thin'}, left: {style:'thin'}, bottom: {style:'thin'}, right: {style:'thin'}},
				alignment: {horizontal: 'center', vertical: 'middle', wrapText: true}
			}, etc);
		};
		var makeCellStyle = function(column) {
			var etc = {};
			if(column.align && column.align != 'left'){
				etc.alignment = {horizontal: column.align};
			}
			if(cellFontSize > 0) {
				etc.font = {size: cellFontSize};
			}
			return $.extend({
				border: {top: {style:'thin'}, left: {style:'thin'}, bottom: {style:'thin'}, right: {style:'thin'}}
			}, etc);
		};
		self.makeWorkbook = makeWorkbook;
		self.cellFontSize = cellFontSize;
		self.makeSheet = makeSheet;
		self.makeTitleRow = makeTitleRow;
		self.makeHeaderRow = makeHeaderRow;
		self.makeDataRows = makeDataRows;
		self.makeGroupHeaderRow = makeGroupHeaderRow;
		self.makeSimpleRows = makeSimpleRows;
	};
	return {
		exportExcel: exportExcel,
		getArrayFromExcel: getArrayFromExcel
	};
})();

window.ExcelUtil = (function(){

	function getColspan(colspan, columns){
		_.each(columns, function(col){
			if(col.columns){
				getColspan(colspan, col.columns);
			}else{
				colspan[0]++;
			}
		});
	};

	function getColName(name){
		return name.replace(/<br\/>/g, NL);
	};

	function makeExportColumnsInfo(gColumns){
		var groupColumns = [];
		var columns = [];
		function setGroupColumns(gCols, index){
			for(var i = 0, len = gCols.length; i < len; i++){
				var gCol = gCols[i];
				if(gCol.columns){
					if(groupColumns[index] == null){
						groupColumns[index] = [];
					}
					var temp1 = [0];
					getColspan(temp1, gCol.columns);
					var colSpan = temp1[0];
					var gColColumns = gCol.columns;
					groupColumns[index].push({name: getColName(gCol.name), colSpan: colSpan});
					setGroupColumns(gColColumns, index + 1);
				}else{
					columns.push(gCol);
				}
			}
		};
		setGroupColumns(gColumns, 0);
		return {groupColumns: groupColumns, columns: columns};
	};

	function getcelltext(it, column){
		var val = it[column.field || column.id];
		if(column.getvalue){
			if(typeof(column.getvalue) == 'string')return column.getvalue;
			if(_.isFunction(column.getvalue))return column.getvalue(it, column);
			return val;
		}else if(column.coder){
			var coderItem = column.coder.itemByValue[val];
			return coderItem ? coderItem.name : val;// pyk code
		}else return val;
	};

	function makeDataToArray(columns, list1){
		return _.map(list1, function(it){
			return _.map(columns, function(column){
				return getcelltext(it, column);
			});
		});
	};
	// GridExcel.exportExcel 에서 사용할 엑셀 정보 만들기
	var makeExportInfo = function(gColumns, list1){
		var colsInfo = makeExportColumnsInfo(gColumns);
		var groupColumns = colsInfo.groupColumns;
		var realCols = colsInfo.columns;
		var rows = makeDataToArray(realCols, list1);// 컬럼으로 rows 만들고
		var columns = _.map(realCols, function(col){// 엑셀에서 사용할 컬럼은 필요한 것만 모아 별도 생성
			return {
				name: getColName(col.name),
				width: col.width||100,
				align: col.align||'left',
				datatype: col.datatype
			};
		});
		var map1 = {
			columns: columns,
			rows: rows
		};
		if(groupColumns && groupColumns.length){
			map1.groupColumns = groupColumns;
		}
		return map1;
	};
	return {
		makeExportInfo: makeExportInfo
	};
})();
