//= require ./lib/jquery.event.drag-2.2.js
//= require ./lib/jquery.event.drop-2.2.js
//= require ./lib/handlebars-v2.0.0.js
//= require ./slick.core.js
//= require ./slick.formatters.js
//= require ./slick.dataview.js
//= require ./slick.grid.js
//= require ./slick.groupitemmetadataprovider.js
//= require ./plugins/slick.cellrangedecorator.js
//= require ./plugins/slick.cellrangeselector.js
//= require ./plugins/slick.cellselectionmodel.js
//= require ./plugins/slick.cellexternalcopymanager.js
//= require ./plugins/slick.autotooltips.js
//pyk 저장문제 발생 주석 ./controls/slick.columnpicker.js
//= require_self

(function (){
	var isKo = true;
	function getStringWidth(str){
		var width = 0;
		for(var i = 0, len = str.length; i < len; i++){
			width += str.charCodeAt(i) <= 0xff ? 1 : 2;
		}
		return width;
	};
	function getcellvalue(item, column){
		return column.getvalue ? column.getvalue(item, column) : item[column.field];
	};
	function getcellcodertext(item, column){
		if(column.coder){
			var val = item[column.field];
			var coderItem = column.coder.itemByValue[val];
			return coderItem ? coderItem.name : val;// pyk code
		}else return null;
	};
	function getcelltext(item, column){
		var cVal = column.coder ? getcellcodertext(item, column) : getcellvalue(item, column);
		if(cVal == null)cVal = '';
		return String(cVal);
	};
	function makeGrid(globalVarName, divid, firstColumnDefs, gridOps){
		var isMobile = uu.checkMobile();
		if(! globalVarName)globalVarName = _.uniqueId('node_');
		var $element;
		if(typeof(divid) == 'string'){
			if(_.startsWith(divid, '#')){
				$element = $(divid);
			}else{
				$element = $('#'+divid);
			}
			$element = $($element[0]);
		}else{
			$element = $(divid);
		}
		var isGridRecreated = false;// 동일 노드 재생성
		if($element[0].grid){
			$element[0].grid.destroy();
			$element[0].grid = null;
			isGridRecreated = true;
		}

		s = $.extend({
			asyncEditorLoading: true,//asyncEditorLoading	FALSE
			asyncEditorLoadDelay: 100,//asyncEditorLoadDelay	100
			//asyncPostRenderDelay	50
			autoEdit: false,//autoEdit	TRUE
			//이거 쓰면 이상해진다.autoHeight: true,//autoHeight	FALSE
			//cellFlashingCssClass	"flashing"
			//cellHighlightCssClass	"selected"
			//dataItemColumnValueExtractor	null
			//defaultColumnWidth	80
			//defaultFormatter	defaultFormatter
			editable: true,//editable	FALSE
			//editCommandHandler	queueAndExecuteCommand
			//editorFactory	null
			//editorLock	Slick.GlobalEditorLock
			enableAddRow: false,//enableAddRow	FALSE
			//enableAsyncPostRender	FALSE
			//enableCellRangeSelection	null
			enableCellNavigation: true,//enableCellNavigation	TRUE
			enableColumnReorder: true,//enableColumnReorder	TRUE
			//enableRowReordering	null
			//enableTextSelectionOnCells	FALSE
			explicitInitialization: true,//explicitInitialization	FALSE
			//forceFitColumns: false,
			forceSyncScrolling: false,//forceSyncScrolling	FALSE
			//formatterFactory	null
			//fullWidthRows	FALSE
			headerRowHeight: 25,//headerRowHeight	25
			//leaveSpaceForNewRows	FALSE
			multiColumnSort: true,//multiColumnSort	FALSE
			//multiSelect	TRUE
			rowHeight: (window.SlickDefault && window.SlickDefault.rowHeight) || 25,//25,
			//selectedCellCssClass	"selected"
			showHeaderRow: false,//!! enableFilter,//showHeaderRow	FALSE
			syncColumnCellResize: false,//syncColumnCellResize	FALSE
			topPanelHeight: 25,//topPanelHeight	25
			////////////////////////////
			cellStyleFunction: Slick.cellStyleFunction,
			//frozenColumn: 1,
			dummy: 1
		}, gridOps);
		var preserveActiveCellWhenSort = !! s.preserveActiveCellWhenSort;
		var columnMap;
		var defaultColumn = {
			//asyncPostRender	null
			//behavior	null
			//cannotTriggerInsert	null
			//cssClass	""
			//defaultSortAsc	TRUE
			//editor	null
			//field	""
			//focusable	TRUE
			formatter: Slick.DefaultFormatter,
			headerCssClass: null,
			//id	""
			maxWidth: 1000,
			/*minWidth:	30,*/
			minWidth: 10,/* 중요 */
			width: 100,
			//name	""
			//rerenderOnResize	FALSE
			//resizable	TRUE
			//selectable	TRUE
			//sortable: (s.sortable != null ? s.sortable : (! levelField)),//sortable	FALSE
			sortable: (s.sortable != null ? s.sortable : true),
			//toolTip	""
			//width
			/////////////////////////////
			datatype: 'text',
			focusable: true
		};
		//var enableFilter = ! s.disableFilter;

		var levelField = s.levelField;

		var orgColumnDefs;// 나중에 엑셀 저장시 사용할려고
		function setColumnDefault(columnDefs){
			orgColumnDefs = columnDefs;// 20160503
			var oldColumns = grid != null ? grid.getColumns() : null;
			var i, len, column, columnMap, pColNm;
			var maxNameLineCnt = 0;
			uu.eachArrayChildren(columnDefs, function(column, i){
				if(column.name == null)column.name = column.id;
				if(column.columns){
					pColNm = column.name;
				}else{
					column.pColNm = pColNm;
					if(column.required){
						column.name += ' *';
					}
					if(column.checktype != 'radio' && column.allcheck){
						column.name += '<input type="checkbox" name="slick_allcheck" class="slick_allcheck" data-cid="'+column.id+'" title="전체선택"/>';
					}

					if(window.slick_allcheck_event_set == null){
						window.slick_allcheck_event_set = 1;
						$(document.body).on('click', '.slick_allcheck', function(event){
							event.stopImmediatePropagation();
							event.stopPropagation();
							//event.preventDefault();
							var el = this, $el = $(el);
							var div_slickgrid = $el.closest('.slickgrid')[0];
							if(div_slickgrid){
								var grid = div_slickgrid.grid;
								var column_id = $el.data('cid');
								grid.checkAll(column_id, el.checked);
							}
						});
					}

					// 20160429
					if(column.datatype == null && (column.coder != null || column.aco != null)){
						column.datatype = 'autocomplete';
					}
					if(column.field == null){
						column.field = column.id;
					}
					if(column.mask){
						column.align = 'center';
					}
					if(typeof(column.getvalue) == 'string'){
						var str1 = column.getvalue;
						column.getvalue = SlickUtil.makeSimpleGetvalueFunc(null, str1);
					}
					// reset columns 할 때 이전 컬럼 폭 유지 하기 위해
					if(column.id && oldColumns){
						var oldColumn = _.find(oldColumns, {id: column.id});
						if(oldColumn && oldColumn.width){
							column.width = oldColumn.width;
						}
					}
					columnMap = SlickColumn[column.datatype] || SlickColumn[column.format] || SlickColumn.text;
					$.extend(column, $.extend({}, defaultColumn, columnMap, column));
					if(column.mergebyfields != null && typeof(column.mergebyfields) == 'string'){
						column.mergebyfields = column.mergebyfields.split(',');
					}
					if(column.isLevel)column.cssClass = (column.cssClass||'')+' slick-cell-level';
					var nameLineCnt = (column.name||'').split('<br').length;
					maxNameLineCnt = Math.max(nameLineCnt, maxNameLineCnt);
				}
			}, 'columns');
			// if(! ($element.hasClass('slickheader-line1') || $element.hasClass('slickheader-line2') || $element.hasClass('slickheader-line3'))){
			// 	$element.addClass('slickheader-line'+maxNameLineCnt);//2022/05/08
			// }
		}//setColumnDefault
		function resetColumns(reColumns){
			setColumnDefault(reColumns);
			columnFilters = {};// 컬럼 바뀔 경우 에러나서 처리.2018-04-25
			grid.setColumns(reColumns);
		}
		setColumnDefault(firstColumnDefs);
		var pager;
		var dataView = new Slick.Data.DataView({
			groupItemMetadataProvider: s.groupItemMetadataProvider
		});
		var grid = new Slick.Grid($element, dataView, firstColumnDefs, s);
		function getDataView(){
			return dataView;
		}
		var $headerScroller = $('.slick-header', $element),
			$headers = $('.slick-header-columns', $element),
			$headerRowScroller = $('.slick-headerrow', $element),
			$viewport = $('.slick-viewport', $element);
		var toggleFilter = function(){
			var visible = grid.getOptions().showHeaderRow;
			grid.setHeaderRowVisibility(! visible);
			if(visible){
				// 감추기
				$(grid.getContainerNode()).find('.slick-headerrow-column input').val('');
				columnFilters = {};
				dataView.refresh();
			}
		};

		var $bottom = $();
		var setGridToolIf = function(){
			if(! ($bottom && $bottom.length == 1))return;
			if(! $bottom.is(':empty'))return;
			$bottom.append('\
				<span class="span_row"></span> \
				<span class="span_show_sum"></span> \
				<div class="etc_tools d-inline-block">\
				\
					<div class="uu-tooltip">\
						<a href="javascript:void(0);" class="calcRange uu-hover-black" title="click 하면 선택된 cell 의 sum, min, max 계산">sum</a>\
						<div class="uu-tooltip-content uu-nowrap span_sum" style="top: auto;bottom: 80%;"></div>\
					</div>\
					<div class="uu-tooltip">\
						<a href="javascript:void(0);" class="calcDistinct uu-hover-black" title="click 하면 선택된 열의 unique 값 목록 표시">distinct</a>\
						<div class="uu-tooltip-content uu-nowrap span_distinct" style="top: auto;bottom: 80%;max-height: 300px;overflow:auto;"></div>\
					</div>\
					<a href="javascript:void(0);" class="uu-hover-black toggleEdit       " title="수정모드"   >'+(isKo?'수정모드':'autoEdit')+'</a>\
					<a href="javascript:void(0);" class="uu-hover-black dialogSumm       " title="통계"       >'+(isKo?'통계':'Stat')+'</a>\
					<a href="javascript:void(0);" class="uu-hover-black toggleFilter     " title="필터"        style="color:navy;">'+(isKo?'필터':'Filter')+'</a>\
					<a href="javascript:void(0);" class="uu-hover-black dialog_filter    " title="고급필터"   >'+(isKo?'고급필터':'Adv.Filter')+'</a>\
					<a href="javascript:void(0);" class="uu-hover-black reset_filter     " title="필터초기화" >'+(isKo?'필터초기화':'RestFilter')+'</a>\
					<a href="javascript:void(0);" class="uu-hover-black downexcel        " title="전체다운"   >'+(isKo?'전체다운':'DownExcelAll')+'</a>\
					<a href="javascript:void(0);" class="uu-hover-black downexcel_filter " title="필터다운"   >'+(isKo?'필터다운':'DownExcelFilter')+'</a>\
					<a href="javascript:void(0);" class="uu-hover-black openSearchDialog " title="찾기"        style="color:navy;">'+(isKo?'찾기':'Find')+'</a>\
					<a href="javascript:void(0);" class="uu-hover-black select_all_row   " title="행전체선택" >'+(isKo?'행전체선택':'SelectAllRows')+'</a>\
					<a href="javascript:void(0);" class="uu-hover-black setColumnWidths  " title="자동폭"     >'+(isKo?'자동폭':'authColWidth')+'</a>\
					<a href="javascript:void(0);" class="uu-hover-black redraw           " title="다시그리기" >'+(isKo?'다시그리기':'redraw')+'</a>\
					'+(isMobile ? '<form style="display:inline-block" autocomplete="off"><input type="text" class="uu-field" title="입력"/><a href="javascript:void(0);" class="setCellWhenValid" title="setCellWhenValid">set</a></form>':'')+'\
				\
				</div>\
			');
			// <a title="업로드" href="javascript:void(0);" class="uploadExcelOrCsv" style="cursor: default;color: transparent;">_</a>\
			// <a title="값보기" href="javascript:void(0);" class="showItemVal" style="cursor: default;color: transparent;">_</a>\
			$('.toggleEdit', $bottom).click(function(){
				grid.setOptions({autoEdit: ! grid.getOptions().autoEdit});
				grid.invalidate();
			});
			$('.dialogSumm', $bottom).click(function(){
				dialogSummary({});
			});
			$('.toggleFilter', $bottom).click(toggleFilter);
			$('.downexcel', $bottom).click(function(){
				downloadExcel({codetext: true});
			});
			$('.downexcel_filter', $bottom).click(function(){
				downloadExcel({codetext: true, withFilter: true});
			});
			$('.openSearchDialog', $bottom).click(function(){
				openSearchDialog();
			});
			$('.dialog_filter', $bottom).click(function(){
				dialog_filter();
			});
			$('.reset_filter', $bottom).click(function(){
				grid.setOrginalFilter();
			});
			$('.select_all_row', $bottom).click(function(){
				select_all_row();
			});
			$('.uploadExcelOrCsv', $bottom).click(function(){
				var processArray = Slick.createUpdateProcessArrayFunction({
					grid: grid
				});
				grid.uploadExcelOrCsv({processArray: processArray});
			});
			$('.setColumnWidths', $bottom).click(function(){
				grid.setColumnWidths();
			});
			if(isMobile){
				$('.setCellWhenValid', $bottom).click(function(e){
					var self = this, cell = grid.getActiveCell();
					if(cell){
						var item = grid.getItemByRow(cell.row);
						var column = grid.getColumns()[cell.cell];
						var str = $('input:text', $(self).closest('form')).val();
						Slick.setCellWhenValid(item, column, str);
					}
				});
			}
			$('.redraw', $bottom).click(function(){
				console.log(grid.getActiveItem());
				grid.invalidate();
			});
			var debounce_calcRange = _.debounce(function(){
				var ranges = SlickUtil.getSelectedRanges(grid);
				if(! (ranges && ranges.length && $bottom.length))return;
				var sumVal = 0;
				var
					frow = ranges[0].fromRow,
					fcell = ranges[0].fromCell,
					trow = ranges[0].toRow,
					tcell = ranges[0].toCell;
				var columns = grid.getColumns();
				var minVal = null;
				var maxVal = null;
				var selCnt = 0;
				var valCnt = 0;
				for(var r = frow; r <= trow; r++){
					var item = getItemByRow(r);
					for(var c = fcell; c <= tcell; c++){
						selCnt++;
						var column = columns[c];
						var val = getcellvalue(item, column);
						var numVal = +val || 0;
						if(numVal){
							sumVal += numVal;
							minVal = minVal == null ? numVal : Math.min(minVal, numVal);
							maxVal = maxVal == null ? numVal : Math.max(maxVal, numVal);
							valCnt++;
						}
					}
				}
				var avgVal = (sumVal && valCnt) ? +Big(sumVal).div(valCnt) : null;
				var item = getItemByRow(frow);
				var field = columns[fcell].field;
				$('.span_sum', $bottom).html(
					(' sum = ' + (isFinite(sumVal) ? uu.formatNumber(sumVal, 4) : 'Infinity'))+'<br/>'+
					(' avg = ' + (isFinite(avgVal) ? uu.formatNumber(avgVal, 4) : 'Infinity'))+' ('+ valCnt +')<br/>'+
					(' min = ' + (isFinite(minVal) ? uu.formatNumber(minVal, 4) : 'Infinity'))+'<br/>'+
					(' max = ' + (isFinite(maxVal) ? uu.formatNumber(maxVal, 4) : 'Infinity'))+'<br/>'+
					(' selCnt = '+selCnt)+'<br/>'+
					(' val ['+field+'] = ['+item[field]+']')
				);
			}, 300);
			$('.calcRange', $bottom).click(function(){
				debounce_calcRange();
			});
			var debounce_calcDistinct = _.debounce(function(){
				var ranges = SlickUtil.getSelectedRanges(grid);
				if(! (ranges && ranges.length && $bottom.length))return;
				var
					frow = ranges[0].fromRow,
					fcell = ranges[0].fromCell,
					trow = ranges[0].toRow;
				var columns = grid.getColumns();
				var arr1 = [];
				var column = columns[fcell];
				var datatype = column.datatype;
				for(var r = frow; r <= trow; r++){
					var item = getItemByRow(r);
					var c_val = datatype == 'number' ? getcellvalue(item, column) : getcelltext(item, column);
					arr1.push(c_val);
				}
				var uarr = _.uniq(arr1);
				var valTdAttr = '';
				if(datatype == 'number'){
					uarr.sort(function(a, b){return a - b;});
					valTdAttr = 'class="uu-text-end"';
				}else{
					uarr.sort();
				}
				var gb1 = _.groupBy(arr1);
				var tdsFn = _.template('<td class="uu-text-end">{%- it.no %}</td><td class="uu-text-end">{%- it.cnt %}</td><td '+valTdAttr+'>{%- it.val %}</td>');
				var trs = _.map(uarr, function(val, i){
					var tds = tdsFn({no: i+1, cnt: gb1[val].length, val: val});
					return '<tr>'+tds+'</tr>';
				}).join('');
				var html1 = '<table class="uu-table-sm"><tbody>'+trs+'</tbody></table>'
				$('.span_distinct', $bottom).html(html1);
				$('.span_distinct', $bottom).attr('title', uarr.length);
			}, 300);
			$('.calcDistinct', $bottom).click(function(){
				debounce_calcDistinct();
			});
			$('.showItemVal', $bottom).click(function(){
				var aCell = grid.getActiveCell();
				if(aCell && aCell.row != null){
					var item = getItemByRow(aCell.row);
					uu.showAlert(null, JSON.stringify(item, null, '\t'));
				}
			});
			$('.consoleLogActiveItem', $bottom).on('click', function(){
				console.log(grid.getActiveItem());
			});
		};// setGridToolIf

		(function(){// bottom 처리
			var $resizer = $element.parent('.slickgrid-resizer:first');
			if(! ($resizer && $resizer.length == 1))return;
			// 기존 bottom 삭제
			var $slick_bottom = $resizer.next().filter('.slickgrid-bottom:first');
			var isBottomExists = false;
			if($slick_bottom && $slick_bottom.length == 1){
				$slick_bottom.remove();
				isBottomExists = true;
			}
			// 기존 toolbtn 삭제
			$('.slickgrid-resizer-toolbtn', $resizer).remove();// 재생성 할 수 있으니
			if(! s.disableToolBtn){
				// toolbtn 추가
				var $toolbtn = $('<button class="uu-btn-naked xi xi-border-all slickgrid-resizer-toolbtn"></button>');
				$resizer.append($toolbtn);
				$toolbtn.on('click', function(){
					if($bottom && $bottom.length == 1){
						$bottom.toggle();
					}else{
						$bottom = $('<div class="slickgrid-bottom"/>');
						$resizer.after($bottom);
						setGridToolIf();
					}
				});
				if(isBottomExists)$toolbtn.trigger('click');
			}
		})();

		dataView.onRowCountChanged.subscribe(function(e, args){
			//debug.log(dataView.getLength())
			grid.updateRowCount();
			grid.render();
		});
		function trigger(evt, args, e){
			e = e || new Slick.EventData();
			args = args || {};
			args.grid = grid;
			return evt.notify(args, e, grid);
		}
		var hasMergeColumns = (function(){
			var i, len, columns = grid.getColumns();
			for(i = 0, len = columns.length; i < len; i+=1){
				column = columns[i];
				if(column.mergebyfields){
					return true;
				}
			}
			return false;
		})();
		dataView.onRowsChanged.subscribe(function(e, args){
			var inRows = args.rows;
			grid.invalidateRows(inRows);
			grid.render();
		});
		dataView.onRowCountChanged.subscribe(function(e, args){
			if($bottom.length){
				$('.span_row', $bottom).text(' rows: ' + grid.getItems().length +'('+ dataView.getFilteredItems().length+')');
			}
		});
		var prev_getItemMetadata = dataView.getItemMetadata;
		dataView.getItemMetadata = function(row){
			var meta = prev_getItemMetadata(row);
			var it = getItemByRow(row);
			if(it && it._ctrlselect){
				if(meta == null)meta = {};
				meta.cssClasses = (meta.cssClasses||'')+' ctrlselect';
			}
			return meta;
		};
		if(s.selectionModel){
			grid.setSelectionModel(s.selectionModel);
		} else if(s.applyRowSelectionModel){
			grid.setSelectionModel(new Slick.RowSelectionModel({}));
		}else if(! isMobile){
			grid.setSelectionModel(new Slick.CellSelectionModel());
		}

		if(grid.getSelectionModel()){
			var exCopyMngr = new Slick.CellExternalCopyManager({
				dataItemColumnValueSetter: Slick.dataItemColumnValueSetter,
				dataItemColumnValueExtractor: Slick.dataItemColumnValueExtractor
			});
			grid.registerPlugin(exCopyMngr);
			//20170810
			exCopyMngr.onCopyCells.subscribe(function(e, args){
				uu.tinyFlashMessage({message: 'copied', of: $element, at: 'left top'});
				setTimeout(_.bind(grid.focus, grid), 1);// 복사 후 focus 유지 위해
			});
			exCopyMngr.onPasteCells.subscribe(function(e, args){
				if(! s.isNoPaste){
					uu.tinyFlashMessage({message: 'pasted.', of: $element, at: 'left top'});
				}
			});
		}

		//grid.setSelectionModel(new Slick.RowSelectionModel({selectActiveRow: false}));
		//grid.setSelectionModel(new Slick.RowSelectionModel());
		grid.onCellChange.subscribe(function(e, args){
			//var item = args.item;
			var column = grid.getColumns()[args.cell];
			if(column.mergebyfields){
				var row = args.row;
				var inRows = [];
				if(grid.getItemByRow(row - 1))inRows.push(row - 1);
				if(grid.getItemByRow(row + 1))inRows.push(row + 1);
				grid.invalidateRows(inRows);
			}
			grid.updateItem(args.item, column.norschange);
		});
		grid.onSelectedCellsChanged = new Slick.Event()
		grid.onClick.subscribe(function(e, args){
			uu.debounceHideDatepickerAll();// 2022 07 13 셀 클릭시 다른 곳의 datepicker 안사라지는 문제 해결 용

			var row = args.row, cell = args.cell;
			var column = grid.getColumns()[cell];
			if(e.shiftKey && grid.getSelectionModel() != null && grid.getSelectionModel().name == 'CellSelectionModel' ){
				var fItems = grid.getFilteredItems();
				if(fItems.length){
					var frow, trow, fcell, tcell;
					// var aCell = grid.getActiveCell();
					// if(aCell){
					// 	frow = Math.min(row, aCell.row);
					// 	trow = Math.max(row, aCell.row);
					// 	fcell = Math.min(cell, aCell.cell);
					// 	tcell = Math.max(cell, aCell.cell);
					var ranges = SlickUtil.getSelectedRanges(grid);
					if(ranges && ranges.length){
						frow = Math.min(row, ranges[0].fromRow);
						trow = Math.max(row, ranges[0].fromRow);
						fcell = Math.min(cell, ranges[0].fromCell);
						tcell = Math.max(cell, ranges[0].fromCell);
					}else{
						frow = row;
						trow = row
						fcell = cell;
						tcell = cell;
					}
					// setTimeout 안하면 안됨
					setTimeout(function(){
						grid.getSelectionModel().setSelectedRanges([new Slick.Range(frow, fcell, trow, tcell)]);
					});
				}
				e.stopPropagation();
				return;
			}
			if(e.ctrlKey){
				var it = dataView.getItem(row);
				if(it){
					it._ctrlselect = ! it._ctrlselect;
					grid.updateItem(it, true);
				}
				e.stopPropagation();
				return;
			}
			if(column.onClick){
				column.onClick(e, args);
			}else if($(e.target).hasClass("slick-treeico")){
				var item = dataView.getItem(row);
				if (item){
					item._collapsed = ! item._collapsed;
					dataView.updateItem(item.id, item);
				}
				//e.stopImmediatePropagation();
				e.stopPropagation();
			}else{
				if(column.datatype == 'check' && grid.isCellEditable(row, cell)){
					var item = grid.getItemByRow(row);
					toggleCheck(item, column);
				}else if(column.datatype == 'select' && grid.isCellEditable(row, cell)){
					var item = grid.getItemByRow(row);
					item[column.field] = ! item[column.field];
					grid.updateItem(item, column.norschange);
					var selectedItems = _.filter(grid.getItems(), function(it){
						return it[column.field];
					});
					trigger(grid.onSelectedCellsChanged, $.extend({selectedItems: selectedItems}, args), e);
				}
			}
		});
		var debounce_showSum = _.debounce(function(){
			var ranges = SlickUtil.getSelectedRanges(grid);
			if(! (ranges && ranges.length && $bottom.length))return;
			var sumVal = 0;
			var
				frow = ranges[0].fromRow,
				fcell = ranges[0].fromCell,
				trow = ranges[0].toRow,
				tcell = Math.min(ranges[0].toCell, grid.getColumns().length - 1) ;
			var columns = grid.getColumns();
			var selCnt = 0;
			for(var r = frow; r <= trow; r++){
				var item = getItemByRow(r);
				for(var c = fcell; c <= tcell; c++){
					selCnt++;
					var column = columns[c];
					var val = getcellvalue(item, column);
					var numVal = +val || 0;
					if(_.isFinite(numVal)){
						sumVal += numVal;
					}
				}
			}
			var strSumVal = _.isFinite(sumVal) ? uu.formatNumber(sumVal, 2) : 'Infinity';
			$('.span_show_sum', $bottom).text('sum: '+ strSumVal +'('+selCnt+')');
		}, 300);
		grid.onSelectedRowsChanged.subscribe(function(e, args){
			if(! grid.getSelectionModel())return;
			debounce_showSum();
		});
		// 그리드 keydown 수정모드 들어가게
		grid.onKeyDown.subscribe(function(e, args){
			var row = args.row;
			var cell = args.cell;
			//
			var keyCode = e.keyCode;
			var chr = (String.fromCharCode(keyCode)||'').toUpperCase();
			//
			if(row == null || cell == null){
				return false;
			}
			if(! grid.getEditorLock().isActive()){
				var column = grid.getColumns()[cell];
				var datatype = column.datatype;
				var isMetaKey = (e.ctrlKey || e.altKey || e.metaKey);
				if(! isMetaKey && grid.isCellEditable(row, cell)){
					var bEditMode = (
						((keyCode == 21) ||
						 (keyCode == 113) ||// f2
						 (keyCode == 189) ||// -
						 (keyCode == $.ui.keyCode.SPACE) ||
						 (keyCode >= 65 && keyCode <= 90) ||
						 (keyCode >= 96 && keyCode <= 109) ||
						 (chr >= '0' && chr <= '9') ||
						 (chr >= 'A' && chr <= 'Z') ||
						 (keyCode == 229)
						)
					);
					if(keyCode != 113 && Slick.isFunctionKeyCode(keyCode)){
						bEditMode = false;
					}
					if(bEditMode){
						if(datatype == 'check'){
							var item = grid.getItemByRow(row);
							toggleCheck(item, column);
							e.preventDefault();
							return false;
						}else if(datatype == 'text' || datatype == 'longtext' || datatype == 'number'|| datatype == 'autocomplete'){
							//grid.editActiveCell(grid.getCellEditor());
							grid.editActiveCell();// 2022 06 17
							return false;
						}
					}
				}else if(! (e.altKey || e.metaKey) && e.ctrlKey && chr == 'F'){// 찾기
					e.preventDefault();
					openSearchDialog();
					return false;
				}/*else if(e.altKey && e.shiftKey && e.ctrlKey && chr == 'Z'){
					e.preventDefault();
					console.log(grid.getActiveItem());
					return false;
				}*/
			}
			return true;
		});
		grid.onSort.subscribe(function(e, args){
			//if(! grid.getOptions().sortable) return;
			var cols = args.sortCols;
			if(cols.length == 0)return;
			var comparer;
			var temp = 0;
			// active cell 보존용 2022 06 17
			if(preserveActiveCellWhenSort){
				var aCell = grid.getActiveCell();
				var aItem = grid.getActiveItem();
			}
			uu.progressCover(true);

			comparer = function(item1, item2){
				var sign, value1, value2, result, column;
				for (var i = 0, l = cols.length; i < l; i++){
					column = cols[i].sortCol;
					sign = cols[i].sortAsc ? 1 : -1;
					value1 = getcellvalue(item1, column);
					value2 = getcellvalue(item2, column);

					if(value1 == null)value1 = '';
					if(value2 == null)value2 = '';

					if(value1 == ''){
						result = 1;
					}else if(value2 == ''){
						result = -1;
					}else{
						result = (value1 == value2 ? 0 : (value1 > value2 ? 1 : -1)) * sign;
					}

					if(result != 0){
						return result;
					}
				}
				return 0;
			};

			dataView.sort(comparer, args.sortAsc);
			uu.progressCover(false);
			// active cell 복구
			if(preserveActiveCellWhenSort){
				if(aItem && aCell){
					var row = grid.getRowById(aItem.id);
					var cell = aCell.cell;
					grid.setActiveCell(row, cell);
				}
			}else{
				grid.resetActiveCell();
			}
		});
		function notEmptyFilter(i, item){
			return !! item;
		}
		function getDispColumnName(column){
			return _getColumnNameText(column);
		}
		function getDispColumnNameById(column_id){
			return _getColumnNameText(grid.getColumnById(column_id));
		}
		grid.onValidationError.subscribe(function(e, args){
			var message = args.validationResults.msg+' ('+getDispColumnName(args.column)+')';
			if(args.editor.$input){
				uu.tinyFlashMessage({
					message: message,
					of: args.editor.$input
				});
			}else{
				uu.showAlert(null, message, null, 2000);
			}
		});
		grid.onBeforeEditCell.subscribe(function(e,args){
			if (! grid.isCellEditable(args.row, args.cell)){
				return false;
			}
		});
		var filters = [];
		// header row filter start
		var columnFilters = {};
		function commonFilter(item){
			var findText, value;
			for (var column_id in columnFilters){
				if (column_id != null && (findText = columnFilters[column_id]) !== ""){
					var column = grid.getColumnById(column_id);
					var val = getcellvalue(item, column);
					var is_match = String(uu.nullToEmpty(val)).indexOf(findText) >= 0;
					if((! is_match) && column.coder){
						is_match = getcelltext(item, column).indexOf(findText) >= 0;
					}
					if(! is_match){
						return false;
					}
				}
			}
			return true;
		}
		$(grid.getHeaderRow()).delegate(":input", "change keyup", function(e){
			var columnId = $(this).data("columnId");
			if (columnId != null){
				columnFilters[columnId] = $.trim($(this).val());
				dataView.refresh();
			}
		});
		grid.onHeaderRowCellRendered.subscribe(function(e, args){
			$(args.node).empty();
			var $text = $("<input type='text' name='slick_filter' maxlength='50' title='필터'/>");
			$text.data("columnId", args.column.id);
			$text.val(columnFilters[args.column.id]);
			$text.appendTo(args.node);
		});
		filters.push(commonFilter);
		// end
		function hirachyFilter(item){
			if(! levelField)return true;
			if (item.pid != null){
				var parentitem = dataView.getItemById(item.pid);
				while (parentitem){
					if (parentitem._collapsed){
						return false;
					}
					parentitem = dataView.getItemById(parentitem.pid);
				}
			}
			return true;
		}
		filters.push(hirachyFilter);
		function myFilter(item){
			var i, len, filter;
			for(i = 0, len = filters.length; i < len; i+=1){
				filter = filters[i];
				if(! filter(item)) return false;
			}
			return true;
		}
		dataView.setFilter(myFilter);
		if(! s.noautotooltip){
			grid.registerPlugin(new Slick.AutoTooltips({enableForHeaderCells: true}));
		}
		//grid.maxrowid = 0;
		//grid.searchid = 0;
		var deletedItems = [];
		function getItemByRow(row){
			return grid.getDataItem(row);
		}
		function getRowById(id){
			return dataView.getRowById(id);
		}
		function setItems(items, s){
			items = uu.makeMapArray([].concat(items));
			s = s || {};
			deletedItems = [];
			dataitemid = 0;
			var itemid, level;
			var pids = [];
			var i, len, item;
			if(s.levelField){
				if(s.levelField == 'noLevel'){
					levelField = null;
				}else{
					levelField = s.levelField;
				}
			}
			for(i = 0, len = items.length; i < len; i+=1){
				item = items[i];
				itemid = newitemid();
				item.id = itemid;
				if(levelField){
					level = item[levelField];
					if(pids[level - 1] != null)item.pid = pids[level - 1];
					pids[level] = itemid;
					item._level = level;
				}
			}
			dataView.setItems([]);// 이거 필수네. 이걸 이곳에서 해줘야 item id 초기화
			dataView.beginUpdate();
			grid.resetActiveCell();
			if(grid.getSelectionModel()){// 이거 하면 포커스 잃어버려서 cellexternalcopymanager.js 의 66라인 _grid.focus() 주석
				grid.getSelectionModel().setSelectedRanges([]);
			}
			dataView.setItems(items);
			dataView.endUpdate();
		}
		function insertItemAtCurrent(item, status){
			var cell = grid.getActiveCell();
			if(cell){
				var row = cell.row;
				item = item || {};
				grid.insertItems(row, [item], status);
			}
		}
		function insertItem(row, item, status){
			item = item || {};
			grid.insertItems(row, [item], status);
		}
		function appendItem(item, status){
			item = item || {};
			grid.insertItems(null, [item], status);
		}
		function insertItems(row, newitems, status, noActiveCell){
			commitEditIfActive();
			status = (status == 'R')?'':(! status)?'I':status;
			if(row == null || row > dataView.getItems().length){
				row = dataView.getItems().length;
			}
			var defaultItem = {};
			_.each(grid.getColumns(), function(column){
				if(column.datatype == 'check'){
					defaultItem[column.field] = 'N';
				}else if(column.datatype == 'number' && ! column.nullable){
					defaultItem[column.field] = 0;
				}
			});
			dataView.beginUpdate();
			var i, len, item;
			for(i = 0, len = newitems.length; i < len; i+=1){
				item = newitems[i];
				item = $.extend({}, defaultItem, item);
				item.status = status;
				item.id = newitemid();
				dataView.insertItem(row + i, item);
			}
			dataView.endUpdate();
			if(! noActiveCell){
				var cell = grid.getActiveCell();
				var gotoCol = cell ? cell.cell : 0;
				gotoCol = s.frozenCol > -1 ? Math.max(gotoCol, s.frozenCol + 1) : gotoCol;
				var gotoRow = row;
				//grid.scrollCellIntoView(gotoRow, gotoCol);
				grid.setActiveCell(gotoRow, gotoCol);
			}
		}
		function appendItems(items, status, noActiveCell){
			grid.insertItems(null, items, status, noActiveCell);
		}
		function appendItemsForCount(count, item, status, noActiveCell){
			if(item == null)item = {};
			var items = [];
			for(var i = 0; i < count; i++){
				items.push($.extend({}, item));
			}
			appendItems(items, status, noActiveCell);
		}
		function copyItem(item){
			commitEditIfActive();
			var cell = grid.getActiveCell();
			if(cell){
				var item = $.extend({}, grid.getItemByRow(cell.row), item);
				item.status = 'I';
				item.id = newitemid();
				delete item._validated;
				var row = cell.row + 1;
				dataView.insertItem(row, item);
				grid.scrollCellIntoView(row, cell.cell);
				//grid.gotoCell(row, cell.cell, false);
			}
		}
		function removeItemByRow(row){
			var item = grid.getItemByRow(row);
			return removeItemByItem(item);
		}
		function removeItemById(id){
			var item = dataView.getItemById(id);
			if(item){
				removeItemByItem(item);
			}
		}
		function removeItemByItem(item){
			if('pid' in item){
				var items = grid.getItems();
				var index = _.findIndex(items, function(it){
					return it.pid == item.id;
				});
				if(index >= 0){
					var message = '자식 데이터가 존재하여 삭제할 수 없습니다.';
					uu.showAlert(null, message, null, 1500);
					//alert(message);
				}else{
					if((item.status || 'U') == 'U'){
						item.status = 'D';
						deletedItems.push(item);
					}
					dataView.deleteItem(item.id);
				}
			}else{
				if((item.status || 'U') == 'U'){
					item.status = 'D';
					deletedItems.push(item);
				}
				dataView.deleteItem(item.id);
			}
		}
		function removeItems(filter){
			commitEditIfActive();
			var items = dataView.getItems();
			dataView.beginUpdate();
			var i, item;
			for(i = items.length - 1; i >= 0; i-=1){
				item = items[i];
				if(filter(i, item)){
					removeItemByItem(item);
				}
			}
			dataView.endUpdate();
		}
		function removeCheckedItems(){
			grid.removeItems(function(i, item){
				return item.check == 'Y';
			});
		}
		function getItems(){
			return dataView.getItems();
		}
		function getColItemList(filtered){
			var list1 = filtered ? getFilteredItems() : getItems();
			if(list1.length == 0)return [];
			var it0 = list1[0];
			var columns = grid.getColumns();
			return _.map(list1, function(it){
				var it2 = {};
				_.each(columns, function(col){
					it2[col.id] = col.coder ? getcelltext(it, col) : getcellvalue(it, col);
				});
				return it2;
			});
		}
		function getCheckedItems(){
			return _.filter(grid.getItems(), function(it){
				return it.check == 'Y';
			});
		}
		function updateItemByRow(row, item, noStusChange){
			item = $.extend(grid.getItemByRow(row), item);
			grid.updateItem(item, noStusChange);
		}
		function updateItemById(id, item, noStusChange){
			item = $.extend(dataView.getItemById(id), item);
			item.id = id;
			grid.updateItem(item, noStusChange);
		}
		function updateItem(item, noStusChange){
			if(noStusChange){
			}else{
				if(! item.status){
					item.status = 'U';
				}
				if(item._validated != null) delete item._validated;
			}
			dataView.updateItem(item.id, item);
		}
		function isCellEditable(row, cell){
			var ci = getCI(row, cell);
			return Slick.isCellEditableByIC(ci.item, ci.column);
		}
		function setItemDefault(item){
			var columns = grid.getAllColumns();
			var val;
			var i, len, column;
			for(i = 0, len = columns.length; i < len; i+=1){
				column = columns[i];
				switch(column.datatype){
				case 'number':
					val = item[column.field];
					if(column.nullable){
						//skip
					}else if(typeof(val) != 'number'){
						item[column.field] = Number(val) || 0;
					}
					break;
				case 'check':
					val = item[column.field];
					if(!(val == 'Y' || val == 'N')){
						item[column.field] = 'N';
					}
					break;
				}
			}
		}
		function validateItem(item, columns, itemValidator, ignoreValidated){
			if(! ignoreValidated){
				if(item._validated){
					return Slick.validTrue;
				}
			}
			grid.setItemDefault(item);
			var check = Slick.validTrue, val;
			var i, len, column;
			for(i = 0, len = columns.length; i < len; i+=1){
				column = columns[i];
				val = item[column.field];
				if(column.iedit || column.uedit || column.save){
					var cellCheck = Slick.validateCell(val, column, item);
					if(! cellCheck.valid){
						return cellCheck;
					}
				}
			}
			if(! check.valid) return check;
			if(itemValidator){
				check = itemValidator(item, columns);
				if(! check.valid) return check;
			}
			item._validated = true;
			return check;
		}
		function saveItemFilter(it){
			return it.status == 'I' || it.status == 'U';
		}
		function validateItems(s){
			s = s || {};
			var itemFilter = s.itemFilter || saveItemFilter;
			var includeDeletedItems = itemFilter == saveItemFilter;
			var columns = grid.getAllColumns();
			var check;
			var filteredItems = _.filter(dataView.getItems(), itemFilter);
			if(filteredItems.length){
				var i, len, item;
				for(i = 0, len = filteredItems.length; i < len; i+=1){
					item = filteredItems[i];
					check = grid.validateItem(item, columns, s.itemValidator, s.ignoreItemValidated);
					if(! check.valid)break;
				}
				if(! check.valid){
					if(! s.noAlert){
						alertValid(check);
					}
					return check;
				}
			}
			// 서버에서 batch 로 도니까 status 별로 구분하자
			filteredItems = filteredItems.sort(function(a, b){
				return (a.status == b.status) ? 0 : (a.status == 'I') ? 2 : (a.status > b.status) ? 1 : -1;
			});
			if(includeDeletedItems){
				filteredItems = [].concat(deletedItems).concat(filteredItems);
			}
			if(! s.noSizeCheck){
				if(! filteredItems.length){
					check = {valid: false, msg: (s.no_item_msg || '저장할 항목이 없습니다.')};
					if(! s.noAlert){
						alertValid(check);
					}
					return check;
				}
			}
			return {valid: true, items: filteredItems};
		}
		function makeSaveItems(fields, items){
			var newitems = [], newitem;
			var i, len, item, i2, len2, field;
			for(i = 0, len = items.length; i < len; i+=1){
				item = items[i];
				newitem = {};
				for(i2 = 0, len2 = fields.length; i2 < len2; i2+=1){
					field = fields[i2];
					newitem[field] = item[field];
					//if(i2 == 0){
					//	newitem[field] = item[field];
					//}else{
					//	if(item[field] != null){
					//		newitem[field] = item[field];
					//	}
					//}
				}
				newitem.id = item.id;
				newitem.status = item.status;
				newitems.push(newitem);
			}
			return newitems;
		}
		function validate(s){
			s = s || {};
			var valid = validateItems(s);// validateItem
			if(! valid.valid) return valid;
			var items = valid.items;
			var columns = grid.getAllColumns();
			// 저장시 추가할 필드들
			var savefields = [];
			var i, len, column, field;
			// column 정의 안된 필드들 추가
			// 저장할때 정의하거나 그리드 초기화할때 정의하거나
			var addfieldsList = [s.extraSaveFields, grid.getOptions().extraSaveFields];
			for(var j = 0, jlen = addfieldsList.length; j < jlen; j++){
				var addfields = addfieldsList[j];
				if(addfields && addfields.length){
					for(i = 0, len = addfields.length; i < len; i++){
						field = addfields[i];
						if(! _.includes(savefields, field)){
							savefields.push(field);
						}
					}
				}
			}
			// column save 필드들 추가
			for(i = 0, len = columns.length; i < len; i+=1){
				column = columns[i];
				field = column.field;
				if((column.iedit || column.uedit || column.save) && ! _.includes(savefields, field)){
					savefields.push(field);
				}
			}
			return {valid: true, items: makeSaveItems(savefields, items)};
		}
		function alertValid(check){
			var message = check.msg+(check.column ? '\n\n('+getDispColumnName(check.column)+')' : '');
			uu.showAlert(null, message, function(){
				if(check.item){
					var row = dataView.getRowById(check.item.id);
					var cell = check.column ? grid.getColumnIndex(check.column.id) : 0;
					//grid.gotoCell(row, cell, true);
					grid.setActiveCell(row, cell >= 0 ? cell : 0);
				}
			}, 2000);
			//alert(message);
		}
		function resetItemValidated(){
			var items = dataView.getItems();
			var i, len, item;
			for(i = 0, len = items.length; i < len; i+=1){
				item = items[i];
				if(item._validated != null){
					delete item._validated;
				}
			}
		}
		var dataitemid = 0;
		function newitemid(){
			return dataitemid++;
		}
		function toggleFilterRow(){
			grid.setTopPanelVisibility(!grid.getOptions().showTopPanel);
		}
		function getAllColumns(){
			return grid.getColumns();
		}
		function getColumnById(columnid){
			return grid.getColumns()[grid.getColumnIndex(columnid)];
		}
		function getFilteredItems(){
			return dataView.getFilteredItems();
		}
		function checkAll(columnid, checked){
			var items;
			var filteredOnly = true;
			if(filteredOnly){
				items = dataView.getFilteredItems();
			}else{
				items = grid.getItems();
			}
			var column = getColumnById(columnid);
			dataView.beginUpdate();
			var i, len, item;
			for(i = 0, len = items.length; i < len; i+=1){
				item = items[i];
				Slick.setCellWhenValid(item, column, checked ? 'Y' : 'N');
			}
			dataView.endUpdate();
		}
		function makeErrorValid(item, columnid, msg){
			var column = grid.getColumnById(columnid);
			return {valid: false, column: column, item: item, msg: msg};
		}
		function makeDataToArray(s){
			s = s || {};
			var extrafields = s.extrafields;
			var exceptFields = s.exceptFields;
			var columns = getExpColumns(grid.getColumns(), exceptFields);
			var withid = s.withid;
			var array = [], line;
			var i, len, column, item, i2, len2, field;
			var cellValueConverter = s.cellValueConverter;
			if(s.head){
				line = [];
				_.each(columns, function(column){
					line.push(_getColumnNameText(column));
				});
				array.push(line);
			}
			var items = s.itemFilterFunc ? _.filter(grid.getItems(), s.itemFilterFunc) :
				s.withFilter ? dataView.getFilteredItems() : grid.getItems();
			for(i = 0, len = items.length; i < len; i+=1){
				item = items[i];
				line = [];
				_.each(columns, function(column){
					var v = dataItemColumnValueExtractor(item, column, s.codetext, s);
					if(cellValueConverter){
						v = cellValueConverter(item, column, v);
					}
					line.push(v);
				});
				if(extrafields){
					for(i2 = 0, len2 = extrafields.length; i2 < len2; i2+=1){
						field = extrafields[i2];
						line.push(item[field]);
					}
				}
				if(withid){
					line.push(item.id);
				}
				array.push(line);
			}
			return array;
		}
		function getExpColumns(columns, exceptFields){
			return _.filter(columns, function(col){
				return ! (exceptFields && _.includes(exceptFields, col.id));
			});
		}
		function getColspan(colspan, columns, exceptFields){
			_.each(columns, function(col){
				if(col.columns){
					getColspan(colspan, col.columns, exceptFields);
				}else{
					if(! _.includes(exceptFields, col.id)){
						colspan[0]++;
					}
				}
			});
		}
		function makeExportColumnsInfo(s){
			s = s || {};
			var exceptFields = s.exceptFields;
			var groupColumns = [];
			function setGroupColumns(cols, index){
				for(var i = 0, len = cols.length; i < len; i++){
					var col = cols[i];
					if(col.columns){
						if(groupColumns[index] == null){
							groupColumns[index] = [];
						}
						var temp1 = [0];
						getColspan(temp1, col.columns, exceptFields);
						var colSpan = temp1[0];
						var expColumns = getExpColumns(col.columns, exceptFields);
						groupColumns[index].push({name: col.name, colSpan: colSpan});
						setGroupColumns(expColumns, index + 1);
					}
				}
			}
			setGroupColumns(orgColumnDefs, 0);
			var withid = s.withid;
			var columns = getExpColumns(grid.getColumns(), exceptFields);
			var array = [];
			_.each(columns, function(column){
				array.push({
					name: _getColumnNameText2(column, true),
					width: column.width||100,
					align: column.align||'left',
					datatype: column.datatype
				});
			});
			if(withid){
				array.push({name: 'id', width: 1, align: 'left'});
			}
			return {groupColumns: groupColumns, columns: array};
		}
		function getFields(){
			return _.map(grid.getColumns(), function(column){
				return column.field;
			});
		}
		//function appendItemsByArray(array, s){
		//	s = s || {};
		//	var newitems = makeArray2Item(array, s);
		//	appendItems(newitems);
		//}
		//
		//function updateItemsByArray(array, s){
		//	s = s || {};
		//	var newitems = makeArray2Item(array, s);
		//	dataView.beginUpdate();
		//	var i, len, item;
		//	for(i = 0, len = newitems.length; i < len; i+=1){
		//		item = newitems[i];
		//		if(item.id != null && dataView.getIdxById(item.id) != null){
		//			updateItemById(item.id, item);
		//		}
		//	}
		//	dataView.endUpdate();
		//}
		function makeArrayToItems(array, s){
			s = s || {};
			var columns = grid.getColumns();
			var fields = getFields();
			var fieldsLen = fields.length;
			var items = [], item, column, val;
			var status = s.status;
			var i, len, cells, i2, len2, field;
			for(i = 0, len = array.length; i < len; i+=1){
				cells = array[i];
				item = {};
				if(status){
					item.status = status;
				}
				for(i2 = 0, len2 = fields.length; i2 < len2; i2+=1){
					field = fields[i2];
					//column = columns[i2];
					item[field] = cells[i2];
				}
				item.id = cells[fieldsLen];
				items.push(item);
			}
			return items;
		}
		function getGroupHeaderCount(){
			var col_0 = orgColumnDefs[0];
			var ghCnt = 0;
			while(col_0.columns){
				ghCnt++;
				col_0 = col_0.columns[0];
			}
			return ghCnt;
		}
		function uploadExcelOrCsv(s){
			s = s || {};
			if(s.update){
				//alert('이 업로드는 행순서로 화면 리스트에 업데이트 됩니다. 다른 사람 작업한 내용이 업데이트 될 수 도 있으니 자기 작업분만 조회해서 다운로드 하고 작업 하신 후 업로드해주세요.');
			}
			var text1 = '* 반드시 해당 화면 그리드에서 다운로드 받은 엑셀을 수정하여 업로드 하시기 바랍니다. 업로드 후 반드시 저장하시기 바랍니다.';
			var text2 = '* 데이터 각 로우마다 id 를 가지고 있으며 그 로우 id 를 비교하여 그리드 데이터에 반영됩니다.';
			var $form = $('\
				<form method="post" enctype="multipart/form-data" autocomplete="off">\
					<div>xlsx</div>\
					<input type="file" name="file" class="form-control required" title="file" /><br/>\
					<div class="uu-indent">'+text1+'</div>\
					<div class="uu-indent" style="color:red;">'+text2+'</div>\
				</form>');
			var form = $form[0];
			uu.openDialog($form, {
				title: 'Upload',
				width: 500,
				height: 'auto',
				modal: true,
				onOpen: function(event, ui){this.reset();},
				isRemoveWhenClose: true,
				buttons: {
					Upload: function(){
						var isCsv = false, isExcel = false;
						var importArray = function(array){
							var processArray = s.processArray;
							if(processArray == null){
								processArray = Slick.createUpdateProcessArrayFunction({
									grid: grid
								});
							}
							//var array = data.array;
							if(s.isRawArray){
								// 단순 처리인 경우
								if(array && array.length){
									if(processArray){
										processArray(array, s);
									}
								}
							}else{
								// 다운 받은 걸로 한 경우
								if(isCsv){
									array = array.slice(1);// 헤더 제거
								}else{
									var groupHeaderCount = getGroupHeaderCount();
									array = array.slice(1 + 1 + groupHeaderCount);// 타이틀 + 헤더 + 그룹헤더
								}
								if(array.length){
									if(processArray){
										processArray(array, s);
									}/* 위험해서 막음 20160422 else{
										if(s.update){
											grid.updateItemsByArray(array, s);
										}else{
											grid.appendItemsByArray(array, s);
										}
									}*/
								}
							}
							$form.dialog('close');
							if(s.onEnd){
								s.onEnd();
							}
						};
						if(typeof ExcelJS == 'object' && typeof GridExcel == 'object'){
							var inputFile = $('input:file', form)[0];
							var fileName = $(inputFile).val();
							if(! fileName)return uu.flashMessageWarn('파일이 선택되지 않았습니다.');
							var fileName2 = inputFile.files[0].path || fileName;
							if(! uu.checkFlash(_.endsWith(fileName2, '.xlsx'), '.xlsx 파일을 선택해주세요.'))return;

							GridExcel.getArrayFromExcel(inputFile.files[0], function(array){
								importArray(array);
							});
						}else{
							uu.ajax($.extend({
								jsonOriginal: true, isUpload: true, form: form, action: '/util/importGetArray',
								checkFormAfter: function(){
									var filename = uu.gev(form, 'file');
									var ext = uu.getFileExt(filename).toLowerCase();
									if(! _.includes(['xlsx', 'csv'], ext)){
										uu.showAlert(null, 'Not right File');
										//alert('Not right File');
										return false;
									}
									isCsv = ext == 'csv';
									isExcel = ! isCsv;
									return true;
								},
								onSuccess: function(data, status, xhr){
									importArray(data.array);
								}
							}, s));
						}
					}
				}
			});
		}
		function _download(s){
			s = s || {};
			var columnsInfo = makeExportColumnsInfo($.extend({withid: true}, s));
			var groupColumns = columnsInfo.groupColumns;
			var columns = columnsInfo.columns;
			var rows = makeDataToArray($.extend({withid: true}, s));
			var exportList = [];
			exportList.push({
				groupColumns: groupColumns,
				columns: columns,
				rows: rows,
				title : s.title,
				sheetName : s.sheetName,
				beforeHeaderRows: s.beforeHeaderRows
			});
			if(typeof ExcelJS == 'object' && typeof GridExcel == 'object'){
				uu.doInProgress(function(){
					return GridExcel.exportExcel({
						exportList: exportList,
						downloadName: s.downloadName,
						cellFontSize: s.cellFontSize,
						numFmt: '#,##0.##########'
					});
				});
			}else{
				var param = {
					exportListJson: uu.encodeJson(exportList),
					downloadName: s.downloadName,
					cellFontSize: s.cellFontSize
				};
				var action = s.export_gbn;
				uu.downloadFile({
					action: '/util/'+action,
					param: param
				});
				// $.fileDownload(CONTEXTPATH+, {
				// 	httpMethod: 'POST',
				// 	data: $.param(param),
				// 	failMessageHtml: "download fail..."
				// });
			}
		}
		function downloadExcel(s){
			_download($.extend(s, {export_gbn: 'exportExcel', codetext: true}));
		}
		function downloadCsv(s){
			_download($.extend(s, {export_gbn: 'exportCsv', codetext: true}));
		}
		function dialogDownload(s){
			s = s || {codetext: true};
			var $form = $('<form method="post" autocomplete="off">select type: <select name="type" class="required" title="type"><option value="excel">excel</option><option value="csv">csv</option></select></form>');
			var form = $form[0];
			uu.openDialog($form, {
				title: 'Download',
				modal: true,
				onOpen: function(event, ui){this.reset();},
				isRemoveWhenClose: true,
				buttons: {
					Ok: function(){
						var type = uu.gev(form, 'type');
						if(type == 'excel'){
							downloadExcel(s);
						} else {
							downloadCsv(s);
						}
						$form.dialog('close');
					}
				}
			});
		}
		function openSearchDialog(s){
			var $form = $('\
				<form autocomplete="off">\
					<div>* '+(isKo?'선택된 다음 행부터 검색합니다.':'Searches from the next line.')+'</div>\
					<table class="uu-table uu-nowrap">\
						<tbody>\
							<tr>\
								<th>'+(isKo?'검색 옵션':'Option')+'</th>\
								<td>\
									<select name="search_option1" title="검색옵션">\
										<option value="include">'+(isKo?'포함':'include')+'</option>\
										<option value="exact">'+(isKo?'일치':'exact')+'</option>\
									</select>\
									<label><input type="checkbox" name="case_sentitive_yn" value="Y" title="대소문자 구분"/> '+(isKo?'대소문자 구분':'case sensitive')+'</label>\
									<label><input type="checkbox" name="not_yn" value="Y" title="NOT" /> NOT</label>\
									<label><input type="checkbox" name="wrap_sch_yn" value="Y" title="행끝에서 되돌리기"/> 행끝에서 되돌리기</label>\
								</td>\
							</tr>\
							<tr>\
								<th>'+(isKo?'검색 필드':'Field')+'</th>\
								<td><select name="column_id" class="required" title="검색 필드"></select></td>\
							</tr>\
							<tr>\
								<th>'+(isKo?'검색 단어':'Word')+'</th>\
								<td>\
									<input type="text" name="search_txt" class="uu-field required" placeholder="Search word" title="검색 단어" size="30"/>\
									<button class="uu-btn search_in_grid" title="찾기">'+(isKo?'찾기':'Find')+'</button>\
								</td>\
							</tr>\
						</tbody>\
					</table>\
				</form>');
			var form = $form[0];
			$('.search_in_grid', $form).off('click');
			$('.search_in_grid', $form).on('click', function(){
				if(! uu.checkForm(form))return;
				var search_option1 = uu.gev(form, 'search_option1');
				var column_id = uu.gev(form, 'column_id');
				var column = grid.getColumnById(column_id);
				var search_txt = uu.gev(form, 'search_txt');
				var is_case_sentitive = uu.gev(form, 'case_sentitive_yn') == 'Y';
				var is_not_yn = uu.gev(form, 'not_yn') == 'Y';
				var is_wrap_sch = uu.gev(form, 'wrap_sch_yn') == 'Y';
				var items = dataView.getFilteredItems();

				var is_match_func = (
					search_option1 == 'exact' ? (function(c_text, search_txt){
						return c_text == search_txt;
					}) : (function(c_text, search_txt){
						return c_text.indexOf(search_txt) >= 0;
					})
				);
				if(! is_case_sentitive){
					search_txt = search_txt.toUpperCase();
				}
				var loopCnt = 0;
				var cell = grid.getActiveCell();
				var firstSchRow = cell ? cell.row+1 : 0;
				var len = items.length;
				if(firstSchRow > len - 1 && is_wrap_sch){
					firstSchRow = 0;
					loopCnt++;
				}

				for(var i = firstSchRow; i < len; i++){
					var item = items[i];
					if(item == null)break;
					var c_val = getcellvalue(item, column);
					var c_text = String(uu.nullToEmpty(c_val));
					if(! is_case_sentitive){
						c_text = c_text.toUpperCase();
					}
					var is_match = is_match_func(c_text, search_txt);
					if(is_not_yn){
						is_match = ! is_match;
					}
					if((! is_match) && column.coder){
						var c_text = String(uu.nullToEmpty(getcellcodertext(item, column)));
						is_match = is_match_func(c_text, search_txt);
					}
					if(is_match){
						grid.activeFlashCellWithId(item.id, column.id, true);
						return;
					}
					var isEndLine = i == (len - 1);
					if(loopCnt == 0 && isEndLine && is_wrap_sch){
						i = -1;// -1 로 해야 된다.
						loopCnt++;
					}
				}
				uu.showAlert(null, (isKo?'해당하는 단어를 찾을 수 없습니다.':'Can not find'));//
			});
			var form = $form[0];

			var onBeforeOpen = function(){
				var codes_column = [];
				var columns = grid.getColumns();
				for(var i = 0, len = columns.length; i < len; i++){
					var column = columns[i];
					codes_column.push({code: column.id, name: _getColumnNameText(column)});// pyk code
				}
				uu.setCombo(form, 'column_id', codes_column, {insertAll: false});
				var cell = grid.getActiveCell();
				if(cell){
					var column = grid.getColumns()[cell.cell];
					uu.sev(form, 'column_id', column.id);
				}
			};
			onBeforeOpen();

			uu.openDialog($form, {
				title: (isKo?'그리드 찾기':'Find'),
				//width: 400,
				//modal: false,
				isRemoveWhenClose: true,
				onOpen: function(event, ui){
					setTimeout(function(){uu.selectFocus(form, 'search_txt');}, 100);
				}
			});
		}
		function dialog_filter(s){
			var $form = $('\
				<form autocomplete="off">\
					<div class="buttons"><button class="uu-btn add_filter_option">'+(isKo?'필터 옵션 추가':'Add filter options')+'</button></div>\
					<table class="filter_option_table table table-sm table-bordered">\
						<tbody>\
						</tbody>\
					</table>\
					<div class="buttons">\
						<label><input type="checkbox" name="close_after_filter_yn" value="Y" title="필터 적용 후 닫기"/> '+(isKo?'필터 적용 후 닫기':'Close after applying filters')+'</label>\
						<button class="uu-btn do_filter" title="필터 적용">'+(isKo?'필터 적용':'Apply filter')+'</button>\
					</div>\
					<div>* '+(isKo?'필터를 초기화 하시려면 그리드 하단의 [필터초기화]를 클릭하십시오.':'To initialize the filter, click [ResetFilter] at the bottom of the grid.')+'</div>\
					<div>* '+(isKo?'단어를 || 로 구분하여 검색어에 입력하면 다중으로 검색합니다.':'If you enter words separated by ||, you can search multiple.')+'</div>\
				</form>');
			dialog_filter.$form = $form;
			var form = $form[0];
			var filter_option_table = $('.filter_option_table', $form);
			$('.add_filter_option', $form).off('click');
			$('.add_filter_option', $form).on('click', function(){
				var codes_column = [];
				var columns = grid.getColumns();
				for(var i = 0, len = columns.length; i < len; i++){
					var column = columns[i];
					codes_column.push({code: column.id, name: _getColumnNameText(column)});// pyk code
				}
				var $tr;
				$('tbody', filter_option_table).append($tr = $('\
					<tr>\
						<td><select name="column_id" title="컬럼"></select></td>\
						<td>\
							<select name="filter_option" title="필터옵션">\
								<option value="include">'+(isKo?'포함':'include')+'</option>\
								<option value="exact">'+(isKo?'일치':'exact')+'</option>\
								<option value="range">'+(isKo?'범위':'range')+'</option>\
								<option value="regex">'+(isKo?'정규식':'Regular expression')+'</option>\
							</select>\
							<label><input type="checkbox" name="case_sentitive_yn" value="Y" title="대소문자 구분"/> '+(isKo?'대소문자 구분':'case sensitive')+'</label>\
							<label><input type="checkbox" name="ex_yn" value="Y" title="제외"/> '+(isKo?'제외':'except')+'</label>\
						</td>\
						<td>\
							<span class="span_text1" ><input type="text" class="uu-field" name="text1" title="'+(isKo?'필터 단어':'Filter word')+'" maxlength="200"/></span>\
							<span class="span_range" style="display: none;">\
								<input type="text" class="uu-field format_number" name="range1" title="'+(isKo?'범위1':'range1')+'" size="10"/> ~ \
								<input type="text" class="uu-field format_number" name="range2" title="'+(isKo?'범위2':'range2')+'" size="10"/>\
							</span>\
						</td>\
						<td><a href="javascript:void(0);" class="remove_filter_option fa fa-times"></a></td>\
						<td><a href="javascript:void(0);" class="alert_words" title="'+(isKo?'선택한 필드의 단어를 100개까지 중복되지 않게 추출합니다.':'Extracts up to 100 words in the selected field')+'">'+(isKo?'단어추출':'Word extraction')+'</a></td>\
					</tr>\
				'));
				uu.setCombo($tr, 'column_id', codes_column);
				var $tr_list = $('tbody tr', filter_option_table);
				var column_id = null;
				if($tr_list.length == 1){
					var cell = grid.getActiveCell();
					if(cell != null){
						var column = grid.getColumns()[cell.cell];
						column_id = column.id;
					}
				}else{
					var tr = $tr_list[$tr_list.length - 2];
					column_id = uu.gev(tr, 'column_id');
				}
				if(column_id != null){
					uu.sev($tr, 'column_id', column_id);
				}
			});
			$('.do_filter', $form).on('click', function(){
				var btn = this;
				if(! uu.checkForm(form))return;
				var $tr_list = $('tbody tr', filter_option_table);
				if($tr_list.length == 0){
					return uu.showAlert(null, (isKo?'필터 옵션을 하나 이상 추가하십시오.':'Add one or more filter options.'));
				}
				var filters = [];
				for(var i = 0, len = $tr_list.length; i < len; i++){
					var $tr = $($tr_list[i]);
					var filter_option = uu.gev($tr, 'filter_option');
					if(filter_option == 'range'){
						if(! (uu.gev($tr, 'range1') || uu.gev($tr, 'range2'))){
							return uu.showAlert(null, (isKo?'[범위] 옵션은 하나 이상의 값이 입력되어야 합니다.':'The range option requires one or more values to be entered.'));
						}
					}
					var column_id = uu.gev($tr, 'column_id');
					var column = grid.getColumnById(column_id);
					if(column != null){
						var text1 = uu.gev($tr, 'text1');
						var is_case_sentitive = uu.gev($tr, 'case_sentitive_yn') == 'Y';
						var filter = {
							column: column,
							filter_option: filter_option,
							is_ex: uu.gev($tr, 'ex_yn') == 'Y',
							is_case_sentitive: is_case_sentitive
						}
						if(filter_option == 'regex'){
							var regex_option = '';
							if(! is_case_sentitive){
								regex_option += 'i';
							}
							try{
								filter.regex = new RegExp(text1, regex_option);
							}catch(e){
								return uu.showAlert(null, (isKo?'정규식이 올바르지 않습니다.':'The regular expression is invalid.'));
							}
							filter.is_match = function(c_text, regex){
								return regex.test(c_text);
							};
						}else{
							if(! is_case_sentitive){
								text1 = text1.toUpperCase();
							}
							if(filter_option == 'include' || filter_option == 'exact'){
								var text1_arr = null;
								if(text1.indexOf('||') > 0){
									text1_arr = _.uniq(text1.split('||'));
								}
								filter.text1 = text1;
								filter.text1_arr = text1_arr;
								if(filter_option == 'include'){
									filter.is_match = function(c_text, text1, text1_arr){
										if(text1_arr != null){
											for(var i = 0, len = text1_arr.length; i < len; i++){
												if(c_text.indexOf(text1_arr[i]) >= 0)return true;
											}
											return false;
										}else{
											return c_text.indexOf(text1) >= 0;
										}
									};
								}else if(filter_option == 'exact'){
									filter.is_match = function(c_text, text1, text1_arr){
										if(text1_arr != null){
											for(var i = 0, len = text1_arr.length; i < len; i++){
												if(c_text == text1_arr[i])return true;
											}
											return false;
										}else{
											return c_text == text1;
										}
									};
								}
							}else if(filter_option == 'range'){
								var range1 = uu.gev($tr, 'range1');
								var range2 = uu.gev($tr, 'range2');
								filter.num_range1 = range1 ? Number(range1) : null;
								filter.num_range2 = range2 ? Number(range2) : null;
								filter.is_match = function(c_num, num_range1, num_range2){
									return (
										(num_range1 == null || c_num >= num_range1) &&
										(num_range2 == null || c_num <= num_range2)
									);
								};
							}
						}
						filters.push(filter);
					}
				}
				dataView.setFilter(function(item){
					for(var i = 0, len = filters.length; i < len; i++){
						var filter = filters[i];
						var column = filter.column;
						var filter_option = filter.filter_option;
						var c_val = getcellvalue(item, column);
						var is_match = false;
						if(filter_option == 'range'){
							var c_num = c_val == null ? null : Number(c_val);
							is_match = filter.is_match(c_num, filter.num_range1, filter.num_range2);
						}else if(filter_option == 'regex'){
							var c_text = c_val == null ? '' : String(c_val);
							is_match = filter.is_match(c_text, filter.regex);
							if((! is_match) && column.coder){
								c_text = getcellcodertext(item, column);
								if(c_text == null){c_text = '';}
								is_match = filter.is_match(c_text, filter.regex);
							}
						}else{
							var c_text = c_val == null ? '' : String(c_val);
							if(! filter.is_case_sentitive){
								c_text = c_text.toUpperCase();
							}
							is_match = filter.is_match(c_text, filter.text1, filter.text1_arr);
							if((! is_match) && column.coder){
								c_text = getcellcodertext(item, column);
								if(c_text == null){c_text = '';}
								if(! is_case_sentitive){
									c_text = c_text.toUpperCase();
								}
								is_match = filter.is_match(c_text, filter.text1, filter.text1_arr);
							}
						}
						if(filter.is_ex){
							is_match = ! is_match;
						}
						if(! is_match)return false;
					}//for
					return true;
				});
				uu.flashMessage(isKo?'필터가 적용되었습니다.':'Filter applied.');
				if(uu.gev($form, 'close_after_filter_yn') == 'Y'){
					uu.closeDialog($form);
				}
			});
			$($form).on('click', 'a.remove_filter_option', function(){
				var el = this, $el = $(el);
				$el.closest('tr').remove();
			});
			$($form).on('change', 'select[name=filter_option]', function(){
				var el = this, $el = $(el);
				var $tr = $el.closest('tr');
				var filter_option = $el.val();
				$('.span_range', $tr)[filter_option == 'range' ? 'show' : 'hide']();
				$('.span_text1', $tr)[filter_option != 'range' ? 'show' : 'hide']();
				if(filter_option != 'range'){
					uu.sev($tr, 'range1', '');
					uu.sev($tr, 'range2', '');
				}
			});
			$($form).on('click', 'a.alert_words', function(){
				var el = this, $el = $(el);
				var $tr = $el.closest('tr');
				var column_id = uu.gev($tr, 'column_id');
				var column = grid.getColumnById(column_id);
				if(column == null){
					return uu.showAlert(null, isKo?'컬럼이 존재하지 않습니다.':'Column does not exist.');
				}
				var items = grid.getItems();
				var map1 = {};
				var arr1 = [];
				for(var i = 0, len = items.length; i < len; i++){
					var item = items[i];
					var c_text = getcelltext(item, column);
					if(map1[c_text]){
						continue;
					}else{
						map1[c_text] = 1;
						arr1.push(c_text);
						if(arr1.length >= 100)break;
					}
				}
				uu.showAlert(null, arr1.join('||'));
			});
			var form = $form[0];
			uu.openDialog($form, {
				title: (isKo?'그리드 고급필터':'Adv.Filter'),
				width: 800,
				minHeight: 300,
				isRemoveWhenClose: true
			});
		}
		function select_all_row(){
			if(! grid.getSelectionModel())return;
			var f_items = grid.getFilteredItems();
			if(f_items.length){
				var ranges = grid.getSelectionModel().getSelectedRanges();
				if(ranges && ranges.length){
					var
						frow = dataView.getRowById(f_items[0].id),
						trow = dataView.getRowById(f_items[f_items.length - 1].id);
					var
						fcell = ranges[0].fromCell,
						tcell = ranges[0].toCell;
					grid.getSelectionModel().setSelectedRanges([new Slick.Range(frow, fcell, trow, tcell)]);
					grid.focus();
				}
				//grid.setSelectedRows(rows);
			}
		}
		//
		function _getColumnNameText(column){
			return (column.pColNm ? column.pColNm+' ' : '') + _getColumnNameText2(column);
		}
		var re_repl_br = /<br\/>/g;
		function _getColumnNameText2(column, isBrToNl){
			if(column._nameText){
				return column._nameText;
			}else{
				var brRep = isBrToNl ? NL : ' ';
				var nameText = '';
				if(column.name == null){
					nameText = colum.id;
				}else if(column.name.indexOf('<') >= 0){
					nameText = column.name.replace(re_repl_br, brRep);
					if(nameText.indexOf('<') >= 0){
						var wrapped = $("<div>" + nameText + "</div>");
						nameText = wrapped.text();
						wrapped.remove();
					}
				}else{
					nameText = column.name;
				}
				return (column._nameText = nameText);
			}
		}
		function toggleCheck(item, column){
			if(column.checktype == 'radio'){
				var findChecked = {};
				findChecked[column.field] = 'Y';
				_.each(_.filter(grid.getItems(), findChecked), function(it){
					if(it.id != item.id){
						Slick.setCellWhenValid(it, column, 'N');
					}
				});
			}
			var row = dataView.getRowById(item.id);
			var cell = grid.getColumnIndex(column.id);
			Slick.setCellWhenValid(item, column, item[column.field] == 'Y' ? 'N' : 'Y');
		}
		function getRC(column, item){
			var row = dataView.getRowById(item.id);
			var cell = grid.getColumnIndex(column.id);
			return {row: row, cell: cell};
		}
		function getCI(row, cell){
			var column = grid.getColumns()[cell];
			var item = dataView.getItem(row);
			return {column: column, item: item};
		}
		function dialogSummary(s){
			var seq = _.uniqueId();
			var $d = $('\
				<div>\
					<div class="uu-box-title">\
						<div><span class="uu-text-title">[Fields]</span></div>\
					</div>\
					<div><select name="field" style="width:95%" size="5" title="field"></select></div>\
					<div>\
						<button class="uu-btn groupbtn">group</button>\
						<button class="uu-btn sumbtn">sum</button>\
						<button class="uu-btn avgbtn">avg</button>\
						<button class="uu-btn minbtn">min</button>\
						<button class="uu-btn maxbtn">max</button>\
						<button class="uu-btn resetbtn">reset</button>\
						<label> <input type="checkbox" name="use_filered_items_yn" value="Y" title="필터된 목록 사용"/> '+(isKo?'필터된 목록 사용':'Use filtered list')+'</label>\
					</div>\
					<pre class="help"></pre>\
					<button class="uu-btn execute">execute</button>\
					<button class="uu-btn execute2">execute2</button>\
					<div class="slickgrid-resizer" style="height: 250px;"><div class="slickgrid" ></div></div>\
				</div>');
			$d.appendTo(document.body);
			var sgrid = Slick.makeGrid(null, $('.slickgrid', $d), [], {levelField: 'level'});
			var allfields = _.reduce(grid.getColumns(), function(r, column, i){
				r.push({code: column.id, name: _getColumnNameText(column)||column.id});// pyk code
				return r;
			}, []);
			var $sel = $('[name=field]', $d);
			uu.setCombo(null, $sel, allfields, {insertSel: true});
			var so = {grpks: [], calcs: []};
			function setHelp(){
				var groupText = _.map(so.grpks, function(k, i){
					var column = grid.getColumnById(k);
					return (i+1)+'. '+_getColumnNameText(column);
				}).join(', ');
				var calcText = _.map(so.calcs, function(a, i){
					var column = grid.getColumnById(a.groupBy);
					return (i+1)+'. ('+a.calc+')'+_getColumnNameText(column);
				}).join(', ');
				$('.help', $d).empty().text('Level: '+groupText + '\nCalc: ' +calcText);
			}
			function addfield(type, k){
				if(type=='group')so.grpks.push(k);
				else so.calcs.push({groupBy: k, calc: type});
				setHelp();
			}
			$('.groupbtn', $d).click(function(){if($sel.val())addfield('group', $sel.val());});
			$('.sumbtn', $d).click(function(){if($sel.val())addfield('sum', $sel.val());});
			$('.avgbtn', $d).click(function(){if($sel.val())addfield('avg', $sel.val());});
			$('.minbtn', $d).click(function(){if($sel.val())addfield('min', $sel.val());});
			$('.maxbtn', $d).click(function(){if($sel.val())addfield('max', $sel.val());});
			$('.resetbtn', $d).click(function(){so.grpks = [];so.calcs = [];setHelp();});
			$('.execute', $d).click(function(){
				var grpks = so.grpks;
				var use_filered_items_yn = uu.gev($d, 'use_filered_items_yn');
				var array = use_filered_items_yn == 'Y' ? dataView.getFilteredItems() : grid.getItems();
				// get sum group by
				var gb1 = _.groupBy(array, function(it){
					return _.map(grpks, function(grpk){
						return it[grpk]
					}).join('/');
				});
				var temp;
				// 초기화
				array = _.reduce(gb1, function(first, n, key){
					temp = {};
					var i, len, cc, calcs = so.calcs;
					for(i = 0, len = calcs.length; i < len; i+=1){
						temp['calc'+i] = 0;
					}
					var children = gb1[key], child;
					for(i = 0, len = children.length; i < len; i+=1){
						child = children[i];
						for(i2 = 0, len2 = calcs.length; i2 < len2; i2+=1){
							cc = calcs[i2];
							if(_.includes(['sum', 'avg'], cc.calc)){
								temp['calc'+i2] += (child[cc.groupBy] || 0);
							}else if(cc.calc == 'min'){
								temp['calc'+i2] = uu.least(child[cc.groupBy], temp['calc'+i2]);
							}else if(cc.calc == 'max'){
								temp['calc'+i2] = uu.greatest(child[cc.groupBy], temp['calc'+i2]);
							}
						}
					}
					temp.count = children.length;
					for(i = 0, len = calcs.length; i < len; i+=1){
						cc = calcs[i];
						if(cc.calc == 'avg'){
							temp['calc'+i] = uu.getRatio(temp['calc'+i], temp.count);
						}
					}
					first.push($.extend({}, children[0], temp));
					return first;
				}, []);
				array = _.orderBy(array, grpks);
				var levelArray = uu.makeLevelArrayByGroup(array, {
					ks: grpks,
					getnamefunc: s.getnamefunc
				});
				uu.calcTree(levelArray, 'level', function(item, children){
					if(children && children.length){
						temp = {count: 0};
						var i, len, cc, calcs = so.calcs, child;
						for(i = 0, len = calcs.length; i < len; i+=1){
							cc = calcs[i];
							temp['calc'+i] = 0;
						}
						for(i = 0, len = children.length; i < len; i+=1){
							child = children[i];
							temp.count += child.count || 0;
							for(i2 = 0, len2 = calcs.length; i2 < len2; i2+=1){
								cc = calcs[i2];
								if(cc.calc == 'sum'){
									temp['calc'+i2] += (child['calc'+i2] || 0);
								}else if(cc.calc == 'avg'){
									temp['calc'+i2] += (child['calc'+i2] || 0) * child.count;
								}else if(cc.calc == 'min'){
									temp['calc'+i2] = uu.least(child['calc'+i2], temp['calc'+i2]);
								}else if(cc.calc == 'max'){
									temp['calc'+i2] = uu.greatest(child['calc'+i2], temp['calc'+i2]);
								}
							}
						}
						for(i = 0, len = calcs.length; i < len; i+=1){
							cc = calcs[i];
							if(cc.calc == 'avg'){
								temp['calc'+i] = uu.getRatio(temp['calc'+i], temp.count);
							}
						}
						$.extend(item, temp);
					}
				});
				var columns = [
					$.extend({}, SlickColumn.itemnoImpl),
					{id: 'name', name: '구분', isLevel: true, width: 250, ccfn: Slick.levelCssFunc},
					{id: 'count', name: 'count', datatype: 'number', ccfn: Slick.levelCssFunc}
				];
				var gcol;
				var i, len, cc, calcs = so.calcs;
				for(i = 0, len = calcs.length; i < len; i+=1){
					cc = calcs[i];
					gcol = grid.getColumnById(cc.groupBy);
					columns.push({id: 'calc'+i, width: gcol.width, datatype: gcol.datatype, format: gcol.format, scale: gcol.scale, name: '('+cc.calc+')'+_getColumnNameText(gcol), coder: gcol.coder, ccfn: Slick.levelCssFunc});
				}
				sgrid.resetColumns(columns);
				sgrid.setItems(levelArray);
				sgrid.resizeCanvas();
			});
			$('.execute2', $d).click(function(){
				var grpks = so.grpks;
				var calcs = so.calcs;
				var use_filered_items_yn = uu.gev($d, 'use_filered_items_yn');
				var array = use_filered_items_yn == 'Y' ? dataView.getFilteredItems() : grid.getItems();
				var gb1 = _.groupBy(array, function(it){
					var x = {};
					for(var i = 0, len = grpks.length; i < len; i++){
						var k = grpks[i];
						x[k] = it[k];
					}
					return uu.encodeJsonWithObjectKeySort(x);
				});
				var list1 = [];
				for(var gbk in gb1){
					var subarr = gb1[gbk];
					var item1 = {};
					for(var i = 0, len = grpks.length; i < len; i++){
						var grpk = grpks[i];
						item1[grpk] = subarr[0][grpk];
					}
					item1.count = subarr.length;
					for(var i2 = 0, len2 = calcs.length; i2 < len2; i2++){
						var cc = calcs[i2];
						var calcVal;
						if(cc.calc == 'min'){
							//calcVal = _.min(subarr, cc.groupBy)[cc.groupBy];
							calcVal = _.reduce(_.map(subarr, cc.groupBy), function(r, p, i){
								if(r == null)return p;
								return p < r ? p : r;
							}, null);
						}else if(cc.calc == 'max'){
							// calcVal = _.max(subarr, cc.groupBy)[cc.groupBy];
							calcVal = _.reduce(_.map(subarr, cc.groupBy), function(r, p, i){
								if(r == null)return p;
								return p > r ? p : r;
							}, null);
						}else if(cc.calc == 'sum'){
							calcVal = _.sumBy(subarr, cc.groupBy);
						}else if(cc.calc == 'avg'){
							calcVal = _.sumBy(subarr, cc.groupBy)/subarr.length;
						}
						item1['calc'+i2] = calcVal;
					}
					list1.push(item1);
				}
				var columns = [$.extend({}, SlickColumn.itemnoImpl)];
				for(var i = 0, len = grpks.length; i < len; i++){
					var grpk = grpks[i];
					columns.push($.extend({}, grid.getColumnById(grpk), {uedit: false, ccfn: null}));
				}
				columns.push({id: 'count', name: 'count', datatype: 'number', width: 60, sortable: true});
				for(var i2 = 0, len2 = calcs.length; i2 < len2; i2++){
					var cc = calcs[i2];
					var gcol = grid.getColumnById(cc.groupBy);
					columns.push({id: 'calc'+i2, width: gcol.width+60, datatype: gcol.datatype, format: gcol.format, name: '('+cc.calc+')'+_getColumnNameText(gcol), coder: gcol.coder, sortable: true});
				}
				sgrid.setOptions({sortable: true});
				sgrid.resetColumns(columns);
				sgrid.setItems(list1);
				sgrid.resizeCanvas();
			});
			uu.openDialog($d, {
				title: 'Summary',
				width: 800,
				modal: true,
				isRemoveWhenClose: true
			});
		}
		function setSummary(s){
			removeSummary();
			var groupby = $.isArray(s.groupby) ? s.groupby : s.groupby.split(',');
			var aggrs = [{groupby: []}];
			var i, len, field, item, i2, len2, sum;
			for(i = 0, len = groupby.length; i < len; i+=1){
				field = groupby[i];
				aggrs.push({groupby: groupby.slice(0, i + 1)});
			}
			s.aggrs = arrgs;
			s.array = grid.getItems();
			var sumArray = uu.makeSumArray(s);
			var dataArray = [].concat(grid.getItems());
			var finalArray = [], count;
			for(i = 0, len = dataArray.length; i < len; i+=1){
				item = dataArray[i];
				count = 0;
				for(i2 = 0, len2 = sumArray.length; i2 < len2; i2+=1){
					sum = sumArray[i2];
					if(sum.startIndex == i){
						sum.id = newitemid();
						sum.editable = false;
						sum.cssClass = 'slick-cell-sum-'+sum.gcount;
						sum.status = 'S';
						finalArray.push(sum);
						count++;
					}else{
						break;
					}
				}
				sumArray = sumArray.slice(count);
				finalArray.push(item);
			}
			dataView.setItems([]);// 이걸 이곳에서 해줘야 item id 초기화
			dataView.beginUpdate();
			grid.resetActiveCell();
			//grid.getSelectionModel().setSelectedRanges([]);
			dataView.setItems(finalArray);
			dataView.endUpdate();
		}
		function removeSummary(){
			removeItems(function(i, item){
				return item.status == 'S';
			});
		}
		// function makeColumnsForSummary(fields){
		// 	var ncols = [], ncol;
		// 	var i, len, column, columns = grid.getColumns();
		// 	for(i = 0, len = columns.length; i < len; i+=1){
		// 		column = columns[i];
		// 		if(_.includes(fields, column.field)){
		// 			ncol = {
		// 				id: column.id,
		// 				datatype: column.datatype,
		// 				format: column.format,
		// 				name: _getColumnNameText(column),
		// 				coder: column.coder,
		// 				width: column.width
		// 			};
		// 			ncols.push(ncol);
		// 		}
		// 	}
		// 	return ncols;
		// }
		function getViewportItems(){
			var range = grid.getViewport();
			var items = [], item;
			for(var r = range.top, bottom = range.bottom; r <= bottom; r++){
				item = grid.getItemByRow(r);
				if(item)items.push(item);
			}
			return items;
		}
		function getWidestColumnWidth(column){
			var items = getViewportItems();
			var col_index = grid.getColumnIndex(column.id);
			var longestText = column.name;
			var longestTextWidth = getStringWidth(longestText);
			var temp_index = 0;
			for(var i = 0, len = items.length; i < len; i+=1){
				var item = items[i];
				var row_index = grid.getRowById(item.id);
				var cell_node = grid.getCellNode(row_index, col_index);
				var cell_text = $(cell_node).text();
				if(cell_text){
					var textWidth = getStringWidth(cell_text);
					if(textWidth > longestTextWidth){
						longestText = cell_text;
						longestTextWidth = textWidth;
						temp_index++;
					}
				}
			}
			var $slickcell = $('<div class="slick-cell"/>').appendTo('body');
			if(temp_index == 0){
				$slickcell.html(longestText);
			}else{
				$slickcell.html(uu.escapeHtml(longestText));
			}
			var maxWidth = $slickcell.outerWidth() + 15;
			$slickcell.remove();
			return maxWidth;
		}
		function commitEditIfActive(){
			if(grid.getEditorLock().isActive()){
				return grid.getEditorLock().commitCurrentEdit();
			}else{
				return true;
			}
		}
		// 2015-11-05 추가
		if(s.enableScrollEndEvent){
			(function(){
				// 밖에서 미리 정의해야 onScroll 시 빨리 처리할 수 있다더라
				var containerNode = grid.getContainerNode();
				var viewportNode = $('.slick-viewport-top:not(.hideFocus)', containerNode)[0];
				var canvasNode = $('.grid-canvas-top:not(.hideFocus)', containerNode)[0];
				var debounce_trigger_onScrollEnd = _.debounce(function(){
					trigger(grid.onScrollEnd, {});
				}, 200);
				grid.onScroll.subscribe(function(e, args){
					var canvasHeight = $(canvasNode).height();
					var viewportScrollTop = $(viewportNode).scrollTop();
					var scrollPosition = $(viewportNode).height() + viewportScrollTop;
					// 마지막 스크롤 감지...
					if(viewportScrollTop > 0 && scrollPosition >= canvasHeight){
						//alert([$(canvasNode).height(), $(viewportNode).height(), $(viewportNode).scrollTop()]);
						// 두번 호출 될때 한번만 처리하게 하기
						debounce_trigger_onScrollEnd();
					}
				});
			})();
		}
		function setColumnWidths(){
			var columns = grid.getColumns();
			for(var i = 0, len = columns.length; i < len; i++){
				var column = columns[i];
				var width = getWidestColumnWidth(column);
				if(column.minWidth && width < column.minWidth)width = column.minWidth;
				if(column.maxWidth && width > column.maxWidth)width = column.maxWidth;
				column.width = width;
			}
			applyColumnResized();
		}
		function applyColumnResized(){
			grid.applyColumnHeaderWidths();
			grid.applyColumnGroupHeaderWidths();
			trigger(grid.onColumnsResized, {});
			grid.invalidate();
		}
		// function applyResized(){
		// 	grid.resizeCanvas();
		// }
		// end
		$.extend(grid, {
			// events
			onScrollEnd: new Slick.Event(),
			// methods
			getItemByRow: getItemByRow,
			getRowById: getRowById,
			setItems: setItems,
			insertItemAtCurrent: insertItemAtCurrent,
			insertItem: insertItem,
			appendItem: appendItem,
			insertItems: insertItems,
			appendItems: appendItems,
			appendItemsForCount: appendItemsForCount,
			copyItem: copyItem,
			removeItemByRow: removeItemByRow,
			removeItemById: removeItemById,
			removeItemByItem: removeItemByItem,
			removeItems: removeItems,
			removeCheckedItems: removeCheckedItems,
			getItems: getItems,
			getCheckedItems: getCheckedItems,
			updateItemByRow: updateItemByRow,
			updateItemById: updateItemById,
			updateItem: updateItem,
			isCellEditable: isCellEditable,
			setItemDefault: setItemDefault,
			validateItem: validateItem,
			saveItemFilter: saveItemFilter,
			validateItems: validateItems,
			alertValid: alertValid,
			resetItemValidated: resetItemValidated,
			newrowid: newitemid,
			toggleFilterRow: toggleFilterRow,
			getAllColumns: getAllColumns,
			makeSaveItems: makeSaveItems,
			validate: validate,
			getFilteredItems: getFilteredItems,
			checkAll: checkAll,
			getColumnById: getColumnById,
			makeErrorValid: makeErrorValid,
			makeExportColumnsInfo: makeExportColumnsInfo,
			makeDataToArray: makeDataToArray,
			// appendItemsByArray: appendItemsByArray,
			// updateItemsByArray: updateItemsByArray,
			// makeArray2Item: makeArray2Item,
			makeArrayToItems: makeArrayToItems,
			getFields: getFields,
			uploadExcelOrCsv: uploadExcelOrCsv,
			downloadExcel: downloadExcel,
			downloadCsv: downloadCsv,
			dialogDownload: dialogDownload,
			dialogSummary: dialogSummary,
			setSummary: setSummary,
			removeSummary: removeSummary,
			resetColumns: resetColumns,
			getWidestColumnWidth: getWidestColumnWidth,
			commitEditIfActive: commitEditIfActive,
			getDataView: getDataView,
			getcellvalue: getcellvalue,
			getDispColumnName: getDispColumnName,
			getDispColumnNameById: getDispColumnNameById,
			setColumnWidths: setColumnWidths,
			// 20160414
			getItemById: function(id){
				return dataView.getItemById(id);
			},
			getItemByIdx: function(idx){
				return dataView.getItemByIdx(idx);
			},
			getIdxById: function(id){
				return dataView.getIdxById(id);
			},
			beginUpdate: function(){
				return dataView.beginUpdate();
			},
			endUpdate: function(){
				return dataView.endUpdate();
			},
			// 20160427 셀 위치 가져오기
			getCellFromId: function(item_id, column_id){
				var row = dataView.getRowById(item_id);
				var cell = column_id == null ? 0 : grid.getColumnIndex(column_id);
				return {
					row: row, cell: cell
				};
			},
			// 셀 강조 표시 해줄때 쓰자
			activeFlashCellWithId: function(item_id, column_id, noClickEvent){
				var cell = grid.getCellFromId(item_id, column_id);
				grid.flashCell(cell.row, cell.cell);
				if(noClickEvent){
					grid.setActiveCell(cell.row, cell.cell);
				}else{
					grid.clickCell(cell.row, column_id);
				}
			},
			setOrginalFilter: function(){
				dataView.setFilter(myFilter);
			},
			setRowDataWhenValid: function(item, rowdata){
				for(var k in rowdata){
					var column = getColumnById(k);
					if(column){
						var val = rowdata[k];
						var str = val != null ? String(val) : val;
						Slick.setCellWhenValid(item, column, str);
					}
				}
			},
			getGroupHeaderCount: getGroupHeaderCount,//20190327
			applyColumnResized: applyColumnResized,//20190919
			// applyResized: applyResized,
			makeExportColumnsInfo: makeExportColumnsInfo,//20190920
			getcelltext: getcelltext,
			clickCell: function(row, cid){
				var cell = grid.getColumnIndex(cid);
				grid.setActiveCell(row, cell);
				//grid.trigger(grid.onClick, grid.getActiveCell());
				grid.trigger(grid.onItemClick, grid.getActiveCellItem());
				//grid.trigger(grid.onItemChanged, grid.getActiveCellItem());
			},
			// 2022 04 14
			getActiveItem: function(){
				var aCell = grid.getActiveCell();
				return aCell && aCell.row != null && grid.getItemByRow(aCell.row);
			},
			// 2022 11 19
			getActiveCellItem: function(){
				var aCell = grid.getActiveCell();
				if(aCell == null)return null;
				return $.extend(aCell, {
					column: grid.getColumns()[aCell.cell],
					item: grid.getItemByRow(aCell.row)
				});
			},
			// 2022 05 18
			toggleFilter: toggleFilter,
			// 2022 05 31
			getColItemList: getColItemList,
			// 2022 06 10
			hasEditData: function(){// 편집중 여부
				if(grid.getEditorLock().isActive())return true;
				if(deletedItems && deletedItems.length)return true;
				var items = grid.getItems();
				var arr_I_U = ['I', 'U'];
				var fIt = _.find(items, function(it){
					return _.includes(arr_I_U, it.status);
				});
				if(fIt)return true;
				return false;
			},
			openSearchDialog: openSearchDialog,// 2022 06 27
			dummy: 1
		});
		grid.init();
		$element.off('dblclick', '.slick-resizable-handle');
		$element.on('dblclick', '.slick-resizable-handle', function(e){
			e.stopImmediatePropagation();
			var column = $(this).closest('.slick-header-column').data('column');
			var width = getWidestColumnWidth(column);
			if(column.minWidth && width < column.minWidth)width = column.minWidth;
			if(column.maxWidth && width > column.maxWidth)width = column.maxWidth;
			column.width = width;
			applyColumnResized();
		});
		$element[0].grid = grid;
		grid.$element = $element;
		if(globalVarName){
			window[globalVarName] = grid;
		}

		// 안보이는 div 에 그리드 그렸다가 보이게 될때 깨지는 현상 방지
		(function(){
			// var tempid;
			// var setGridVisible = function(){
			// 	if($('.'+grid.uid)[0] == null){
			// 		if(tempid)clearTimeout(tempid);
			// 	}else if($element.is(':visible')){
			// 		grid.resizeCanvas();
			// 		grid.invalidate();
			// 		if(tempid)clearTimeout(tempid);
			// 	}else{
			// 		tempid = setTimeout(setGridVisible, 200);
			// 	}
			// };
			// if(! $element.is(':visible'))setGridVisible();

			// if(window._slickGridObserver == null){
			// 	const options = {
			// 		rootMargin: '0px',
			// 		threshold: 1.0
			// 	};
			// 	const callback = (entries, observer) => {
			// 		entries.forEach(entry => {
			// 			if(entry.isIntersecting && entry.target && entry.target.grid){
			// 				const targetGrid = entry.target.grid;
			// 				targetGrid.resizeCanvas();
			// 				targetGrid.invalidate();
			// 				console.log(entry.target);
			// 				window._slickGridObserver.unobserve(entry.target);
			// 			}
			// 		});
			// 	};
			// 	window._slickGridObserver = new IntersectionObserver(callback, options);
			// }
			// window._slickGridObserver.observe($element[0]);
		})();

		return grid;
	}// makeGrid
	function isCellEditableByIC(item, column){
		if(!(column && item))return false;
		return (
			(column.grid.getOptions().editable) &&
			((item.status == 'I' && column.iedit) || ((item.status || 'U') == 'U' && column.uedit)) &&
			(item.editable !== false) &&
			//(column.edfn == null || column.edfn(column, item))
			(column.edfn == null || column.edfn(item, column))// 2022 04 28
		);
	}
	function _validFalse(msg, column, item){
		return {valid: false, msg: msg, column: column, item: item};
	}
	var _ctypes = 'match,max,min,gt,lt,between,validator,nowhitechar'.split(',');
	var reWhiteSpace = /\s/;
	function validateCell(str, column, item){
		if(column.required && uu.isNullOrEmptyString(str)){
			return _validFalse(isKo?'필수 항목입니다.':'Required.', column, item);
		}
		if(! uu.isNullOrEmptyString(str)){
			if(column.datatype == 'number' || column.format){
				var format = column.datatype == 'number' ? 'number' : column.format;
				var check = SlickFormat.check(str, format);
				if(! check){
					var msg = SlickFormat.getCheckMessage(format);
					return _validFalse(msg, column, item);
				}
			}
			//if(column.datatype == 'number' && typeof str != 'number'){
			//	str = +str;
			//}
			var ctypes = _ctypes, ctype, cval;
			for(var i = 0, len = ctypes.length; i < len; i++){
				ctype = ctypes[i];
				cval = column[ctype];
				if(cval != null){
					switch(ctype){
					case 'match'://match: {regex: /^[A-Z]+$/g, msg: 'xxx'}
						if(! cval.regex.test(str)){
							var msg = cval.msg || (isKo?'형식이 올바르지 않습니다.':'The format is not valid.');
							return _validFalse(msg, column, item);
						}
						break;
					case 'max':// max: 100
						if(! (str <= cval)){
							return _validFalse(isKo?(''+ cval +' 보다 이하이어야 합니다.'):('It must be equal or less than '+cval+'.'), column, item);
						}
						break;
					case 'min':// min: 100
						if(! (str >= cval)){
							return _validFalse(isKo?(''+ cval +' 보다 이상이어야 합니다.'):('It must be equal or more than '+cval+'.'), column, item);
						}
						break;
					case 'gt':// gt: 100
						if(! (str > cval)){
							return _validFalse(isKo?(''+ cval +' 보다 큰 값이어야 합니다.'):('It must be more than '+cval+'.'), column, item);
						}
						break;
					case 'lt':// gt: 100
						if(! (str < cval)){
							return _validFalse(isKo?(''+ cval +' 보다 작은 값이어야 합니다.'):('It must be less than '+cval+'.'), column, item);
						}
						break;
					case 'between':// between: {min: 100, max: 100}
						if(! (str >= cval.min && str <= cval.max)){
							return _validFalse(isKo?(''+ cval.min + ' 이상 '+ cval.max +' 이하 이어야 합니다.'):('It must be between '+cval.min+' and '+cval.max+'.'), column, item);
						}
						break;
					case 'validator':// validator: funciton(str, column, item){...return {valid: true, msg: ...}}
						var validResult = cval(str, item, column);
						if(! validResult.valid){
							return _validFalse(validResult.msg, column, item);
						}
						break;
					case 'nowhitechar':
						if(cval){
							if(str.match(reWhiteSpace)){
								return _validFalse('공백이 있으면 안됩니다. ', column, item);
							}
						}
						break;
					}
				}
			}
		}
		return Slick.validTrue;
	}
	function DefaultFormatter(row, cell, value, column, item){
		if(column.mergebyfields && value != null){
			var uitem = column.grid.getItemByRow(row - 1);
			if(uitem){
				var comp = {};
				var i, len, field, fields = column.mergebyfields;
				for(i = 0, len = fields.length; i < len; i+=1){
					field = fields[i];
					comp[field] = item[field];
				}
				comp[column.field] = value;
				var equal = uu.objectContains(uitem, comp);
				if(equal){
					return '';
				}
			}
		}
		var datatype = column.datatype;
		var getvalue = column.getvalue;
		switch(datatype){
		case 'select':
			var isEdit = Slick.isCellEditableByIC(item, column);
			return '<input type="checkbox" '+(value ? 'checked':'')+' hideFocus '+(isEdit?'':'disabled')+' title="check"/>';
			break;
		}
		var str = getvalue ? getcellvalue(item, column) : value;
		if(str == null) str = '';
		switch(datatype){
		case 'autocomplete':
			if(column.coder){
				var coderItem = column.coder.itemByValue[str];
				str = coderItem ? coderItem.name : str;// pyk code
			}
			break;
		//case 'check':
		//	str = str == 'Y' ? 'V' : str == 'N' ? '' : str;
		//	break;
		default:
			var format = datatype == 'number' ? 'number' : column.format;
			var mask = column.mask;

			if(! uu.isNullOrEmptyString(str)){
				if(format){
					if(! column.dontFormat){
						str = SlickFormat.format(str, format, column.dontFormatScale ? null : column.scale);
					}
				}else if(mask){
					str = uu.formatNumMask(str, mask);
				}
			}
			break;
		}
		var escaped = column.escapeFn ? column.escapeFn(str) : uu.escapeHtml(str);
		if(datatype == 'button'){
			escaped = str ? ('<span class="'+(column.button_class || '')+'" >'+str+'</span>') : '';
		}else if(datatype == 'barchart'){
			var percent = +str;
			if(percent && ! isNaN(percent)){
				percent = Math.min(100, Math.max(0, percent));
				escaped = '<div class="slick-barchart"><div class="slick-barchart-inner" style="width: '+percent+'%">'+uu.formatNumber(percent, 2)+'%'+'</div></div>';
			}else{
				escaped = '';
			}
		//}else if(datatype == 'html'){
		//	return str;
		}else if(datatype == 'check'){
			// jquery.event.drag-2.3.0.js 에서는 class 에서 slick 이 들어가야 되네
			// escaped = '<div class="slick-check-'+(str == 'Y' ? 'checked' : 'unchecked')+'" style="height: 100%;"></div>';
			var checkStr = (str == 'Y' ? '✔' : '');//✓, ✔, ☑ //fa fa-check
			escaped = '<div class="slick-check" style="height: 100%;">'+checkStr+'</div>';
		}else if(datatype == 'autocomplete' && Slick.isCellEditableByIC(item, column)){
			escaped = escaped + ' <span class="combomark">▼</span>';
		}else if(column.isLevel && item._level != null){
			var treeClassNameClosed = (column.grid.getOptions().treeClassNameClosed || 'fa fa-square-plus');//xi xi-plus-square-o
			var treeClassNameOpened = (column.grid.getOptions().treeClassNameOpened || 'fa fa-square-minus');//xi xi-minus-square-o
			var treeClassNameLeaf = (column.grid.getOptions().treeClassNameLeaf || 'fa fa-caret-right fa-fw');
			var spacer = "<span style='display:inline-block;height:1px;width:" + (0.5 * (item._level - 1)) + "rem'></span>";
			var levelHtml = '';
			var dataView = column.grid.getDataView();
			var idx = dataView.getIdxById(item.id);
			var items = dataView.getItems();
			if (items[idx + 1] && items[idx + 1]._level > items[idx]._level){
				if (item._collapsed){
					//levelHtml = " <span class='slick-treeico expand'></span>&nbsp;";
					levelHtml = " <span class='slick-treeico haschild "+treeClassNameClosed+"'></span>&nbsp;";
				} else {
					//levelHtml = " <span class='slick-treeico collapse'></span>&nbsp;";
					levelHtml = " <span class='slick-treeico haschild "+treeClassNameOpened+"'></span>&nbsp;";
				}
			} else {
				//levelHtml = " <span class='slick-treeico'></span>&nbsp;";
				levelHtml = " <span class='slick-treeico "+treeClassNameLeaf+"'></span>&nbsp;";
			}
			escaped = spacer + levelHtml + '<span>'+escaped+'</span>';
		}
		return escaped;
	}
	function cellStyleFunction(row, cell, value, column, item, grid){
		var classNames = [];
		var isEdit = Slick.isCellEditableByIC(item, column);
		if(isEdit) classNames.push('slick-edit');
		if(item.status == 'I') classNames.push('slick-insert');
		else if(item.status == 'U') classNames.push('slick-update');
		if(column.align) classNames.push(column.align);
		if(! isEdit && item.cssClass){
			classNames.push(item.cssClass);
		}
		var datatype = column.datatype;
		if(isEdit && datatype == 'autocomplete'){
			// classNames.push('slick-cbmark');
		}
		var i, len, field, fields = column.mergebyfields;
		if(column.mergebyfields/* && value != null*/){
			var ditem = grid.getItemByRow(row + 1);
			if(ditem){
				var comp = {};
				for(i = 0, len = fields.length; i < len; i+=1){
					field = fields[i];
					comp[field] = ditem[field];
				}
				comp[column.field] = ditem[column.field];
				var equal = uu.objectContains(item, comp);
				if(equal){
					classNames.push('slick-cell-b-b-n');
				}
			}
			var uitem = grid.getItemByRow(row - 1);
			if(uitem){
				var comp = {};
				for(i = 0, len = fields.length; i < len; i+=1){
					field = fields[i];
					comp[field] = item[field];
				}
				comp[column.field] = value;
				var equal = uu.objectContains(uitem, comp);
				if(equal){
					classNames.push('slick-cell-b-t-n');
				}
			}
		}
		if(datatype == 'button'){
			classNames.push('slick-cell-button');
		}
		if(column.ccfn){
			//var classes = column.ccfn(row, cell, value, column, item, grid);
			var classes = column.ccfn(item, column, value, row, cell, grid);
			if(classes && classes.length){
				classNames = classNames.concat(classes);
			}
		}
		return classNames.join(' ');
	}
	function getValidValue(item, column, str){
		if(str == null) str = '';
		var result;
		switch(column.datatype){
		case 'check':
			result = str == 'Y' ? 'Y':'N';
			break;
		case 'select':
			result = (str == 'Y' || str == 'true');
			break;
		case 'autocomplete':
			if(column.aco){
				// skip
			}else{
				var coderItem = column.coder.itemByValue[str] || column.coder.itemByText[str];
				if(coderItem){
					result = coderItem.code;// pyk code
				}else{
					result = '';
				}
			}
			break;
		case 'number':
			if(column.nullable && uu.isNullOrEmptyString(str)){
				result = str;
			}else{
				var num = Number(SlickFormat.unformat(str, column.datatype)) || 0;
				if(column.scale != null){
					//num = uu.round2(num, column.scale);
					num = uu.trunc2(num, column.scale);
				}
				result = num;
			}
			break;
		case 'text':
			var idx = Math.min(str.indexOf('\n'), str.indexOf('\r'));
			if(idx >= 0){
				str = str.substr(0, idx);
			}
			str = SlickFormat.unformat(str, column.datatype == 'number' ? 'number' : column.format);
			if(column.maxlength != null && str.length > column.maxlength){
				str = str.substr(0, column.maxlength);
			}
			result = str;
			break;
		default:
			result = str;
			break;
		}
		var validation = Slick.validateCell(result, column, item);
		return validation.valid ? result : null;
	}
	function setCellWhenValid(item, column, str){
		if(Slick.isCellEditableByIC(item, column)){
			if(str != null && typeof(str) != 'string'){
				str = String(str);
			}
			var vv = getValidValue(item, column, str);
			var cv = item[column.field];
			if(
				(vv == null && column.datatype == 'number' && column.nullable) ||
				(vv != null && (uu.nullToEmpty(cv) != vv || uu.isNullOrEmptyString(vv) != uu.isNullOrEmptyString(cv)))
				){
				var grid = column.grid;
				var row = grid.getRowById(item.id);
				var cell = grid.getColumnIndex(column.id);
				grid.trigger(grid.onBeforeEditCell, {row: row, cell: cell, item: item, column: column});// 2022 06 17
				item[column.field] = vv;
				grid.updateItem(item, column.norschange);//20170703
				grid.trigger(grid.onCellChange, {
					row: row,
					cell: cell,
					item: item
				});
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	function dataItemColumnValueSetter(item, column, str){
		str = str == null ? '' : str;
		// 엑셀에서 복사시 줄바꿈일 경우 처리
		if(str[0] == '"' && str[str.length - 1] == '"'){
			str = str.substring(1, str.length - 1).replace(/""/g, '"');
		}
		setCellWhenValid(item, column, str);
		//}
	}
	function dataItemColumnValueExtractor(item, column, codetext, s){
		var is_check_only_y = false;
		if(s){
			is_check_only_y = !!s.is_check_only_y;
		}
		if(item == null || column == null){
			return '';
		}
		var str = null;
		var val = getcellvalue(item, column);
		if(column.coder && codetext){
			str = getcellcodertext(item, column);
		}else{
			switch(column.datatype){
			case 'check':
				str = val == 'Y' ? 'Y' : (is_check_only_y?'':'N');
				break;
			case 'select':
				str = !! val;
				break;
			case 'number':
				str = column.nullable ? val : (val || 0);
				break;
			default:
				str = val;
				break;
			}
		}
		if(str == null) str = '';
		//return String(str);
		return str;
	}
	function DefaultEditor(args){
		var $input, $wrapper;
		var defaultValue;
		var scope = this;
		var calendarOpen = false;
		var column = args.column;
		var field = column.field;
		var item = args.item;
		var grid = args.grid;
		var is_setdatepicker = _.includes(['year', 'ym', 'ymd'], column.format);
		var is_onlyNum = _.includes(
			['year', 'ym', 'ymd', 'mm', 'hms', 'hm'/*, 'thm'*/, 'hh', 'mi', 'ss', 'ymdhm', 'ymdhms', 'ssn', 'bizno'], column.format);
		this.init = function(){
			$wrapper = $('<div/>');
			$input = $('<input type="text" name="slick-input-'+_.uniqueId()+'" class="editor-text" title=""/>');
			this.$input = $input;//필수
			if(column.align){
				$input.addClass(column.align);
			}
			if(column.maxlength){
				$input.attr('maxlength', column.maxlength);
			}
			$input.appendTo($wrapper);
			$wrapper.appendTo(args.container);
			$input.select().focus();//.select(); 먼저 해야 크롬에서 한글
			$input.bind("keydown.nav", function(e){
				if(e.keyCode == $.ui.keyCode.LEFT || e.keyCode == $.ui.keyCode.RIGHT/* || e.keyCode == $.ui.keyCode.ENTER*/){
					e.stopImmediatePropagation();
					//return false;
				}else if(e.keyCode == $.ui.keyCode.ENTER && is_setdatepicker){
					// 2015-10-13 엔터 이상 동작때문에 이렇게 한다.
					uu.destroyDatepicker($input);
				}
			}).bind('keyup.nav', function(event){
				var el = this, $el = $(el);
				if(column.datatype == 'number'){
					if(! uu.RegExpMap.numbermatch.test($el.val())){
						var temp = $el.val().replace(uu.RegExpMap.notnumberreplace, '');
						$el.val(temp);
					}
				}else if(is_onlyNum){
					if(! uu.RegExpMap.numtest.test($el.val())){
						var temp = $el.val().replace(uu.RegExpMap.nonumreplace, '');
						$el.val(temp);
					}
				}
			});
			//문제가 있네 .bind('focusout', function(){args.cancelChanges();});
			//$input.data('gridArgs', args);
			if(is_setdatepicker){
				var value = item[field];
				uu.setDatepicker($input, column.format, {is_slickgrid: true, value: value});
			}
		};
		this.destroy = function(){
			if(is_setdatepicker){
				uu.destroyDatepicker($input);
			}
			$wrapper.remove();
		};
		this.show = function(){
			//if(is_setdatepicker){
			//	if(calendarOpen){
			//		$.datepicker.dpDiv.stop(true, true).show();
			//	}
			//}
		};
		this.hide = function(){
			//if(is_setdatepicker){
			//	if (calendarOpen){
			//		$.datepicker.dpDiv.stop(true, true).hide();
			//	}
			//}
		};
		this.position = function(position){
			//if(isYmd){
			//	if (!calendarOpen){
			//		return;
			//	}
			//	$.datepicker.dpDiv
			//			.css("top", position.top + 30)
			//			.css("left", position.left);
			//}
		};
		this.focus = function(){
			$input.focus();//.select();//$input.focus();
		};
		this.loadValue = function(item){
			defaultValue = item[field];
			$input[0].defaultValue = uu.nullToEmpty(defaultValue);
			if(! isWordKeyDown(event)){
				$input.val(uu.nullToEmpty(defaultValue));// ie에서는 이거 안하니 한글 먹음
			}
			$input.select();
		};
		this.serializeValue = function(){
			var str = $input.val();
			if(column.datatype == 'number'){
				var value = Slick.getValidValue(item, column, str);
				return value;
			}else{
				return str;
			}
		};
		this.applyValue = function(item, state){
			item[field] = state;
		};
		this.isValueChanged = function(){
			var input_val = $input.val();
			var is_input_empty = uu.isNullOrEmptyString(input_val);
			var is_value_empty = uu.isNullOrEmptyString(defaultValue);
			var changed = (!(is_input_empty && is_value_empty)) &&
			              (input_val != defaultValue || is_input_empty != is_value_empty);
			return changed;
		};
		this.validate = function(){
			return Slick.validateCell($input.val(), column, item);
		};
		this.init();
	}

	// ie 여부
	var isIE = (navigator.userAgent.indexOf("MSIE ") > 0 || !! navigator.userAgent.match(/Trident.*rv\:11\./));

	function AutoCompleteEditor(args){
		var $input;
		var defaultValue;
		var scope = this;
		var column = args.column;
		var field = column.field;
		var item = args.item;
		var coder = column.coder;
		var aco = column.aco;
		var selectedItem;
		var acFilter = column.acFilter;
		var aco_display = column.aco_display;
		//loadValue
		//serializeValue
		//isValueChanged
		//validate
		//serializeValue
		//applyValue
		//loadValue
		this.init = function(){
			$input = $('<input type="text" name="slick-input-'+_.uniqueId()+'" class="editor-text" title="입력"/>');
			this.$input = $input;//필수
			$input.appendTo(args.container);
			$input.autocomplete({
				minLength: 0,
				source: function(request, response){
					if(aco){
						//response([{label: 'loading...'}]);
						aco.source(request, response, column, item);
					}else{
						var filteredArray;
						if(acFilter){
							filteredArray = _.filter(coder.array, function(a){
								return acFilter(a, item, column);
							});
						}else{
							filteredArray = coder.array;
						}
						var temp = [];
						if(! column.required){
							temp.push({code: '', label: ''});
						}
						temp = temp.concat(filteredArray);// pyk code
						var results = $.ui.autocomplete.filter(temp, request.term);
						response(results.slice(0, 100));
					}
				},
				focus: function( event, ui ){
					//하면 안좋다. $input.val(ui.item.name);
					return false;
				},
				select: function( event, ui ){
					selectedItem = ui.item;
					$input.val(ui.item.code);// pyk code
					args.commitChanges();
					event.stopImmediatePropagation();
					return false;
				},
				position: {
					of: args.container
				}
			}).data("ui-autocomplete")._renderItem = function(ul, item){
				var text = (aco_display == 'label' ? item.label : item.name) || 'None';// pyk code
				return $("<li>").append( $("<div>").text(text) ).appendTo( ul );
			};
			$input.bind("keydown.nav", function(e){
				if ((e.keyCode == $.ui.keyCode.DOWN || e.keyCode == $.ui.keyCode.UP/* || e.keyCode == $.ui.keyCode.ENTER*/) && $('ul.ui-autocomplete').is(':visible')){
					e.stopPropagation();
				}
				if (e.keyCode == $.ui.keyCode.LEFT || e.keyCode == $.ui.keyCode.RIGHT/* || e.keyCode == $.ui.keyCode.ENTER*/){
					e.stopImmediatePropagation();
				}
				if(e.keyCode == $.ui.keyCode.DOWN){
					return false;
				}
			});//문제가 있네 .bind('focusout', function(){args.cancelChanges();});
			// 더블클릭하면 전체 조회
			$input.bind('dblclick.nav', function(e){
				$input.autocomplete("search", '');
			});
			$input.select().focus();// 크롬에서 한글. 위치 중요
		};
		this.destroy = function(){
			try{
				$input.autocomplete("destroy");
			}catch(skip){doNothing();}/* csap 보완 */
			$input.remove();
		};
		this.focus = function(){
			$input.focus();
		};
		this.loadValue = function(item){
			defaultValue = item[field];
			var inpVal;
			if(aco){
				inpVal = uu.nullToEmpty(defaultValue);
			}else{
				var coderItem = coder.itemByValue[defaultValue];
				inpVal = uu.nullToEmpty(coderItem ? coderItem.name : null);// pyk code
			}
			$input[0].defaultValue = inpVal;
			if(! isWordKeyDown(event)){
				$input.val(inpVal);
			}else if(! isIE){// 여기서는 한글 땜에 이렇게
				$input.val('');
			}
			$input.select();
			//$input.autocomplete("search", $input.val());
			// 아래처럼 하면 콤보 전체 가져옴.
			$input.autocomplete("search", '');
		};
		this.isValueChanged = function(){
			if(aco){
				if(aco.isValueChanged){
					return aco.isValueChanged(item, selectedItem);
				}else{
					return !! selectedItem;
				}
			}else{
				var inputVal = $input.val();
				var coderItem = coder.itemByValue[inputVal] || coder.itemByText[inputVal];
				var value = coderItem ? coderItem.code : null;// pyk code
				// 2022/03/06 값 있었는데 없게 해버리면 changed=false 로
				if(column.required && defaultValue && value == null){
					return false;
				}
				return (value != defaultValue);
			}
		};
		this.serializeValue = function(){
			if(aco){
				return $input.val();//aco.serializeValue(selectedItem);
			}else{
				var inputVal = $input.val();
				var coderItem = coder.itemByValue[inputVal] || coder.itemByText[inputVal];
				return coderItem ? coderItem.code : '';// pyk code
			}
		};
		this.validate = function(){
			// 필수 인 데서는 안되서 다른 컬럼에 찾기 만들고 셋해야
			return Slick.validateCell($input.val(), column, item);
		};
		this.applyValue = function(item, state){
			if(aco){
				aco.applyValue(item, selectedItem, column);
			}else{
				item[field] = state;
			}
		};
		this.init();
	}
	function CheckboxEditor(args){
		var $check;
		var defaultValue;
		var scope = this;
		var column = args.column;
		var field = column.field;
		var item = args.item;
		this.init = function(){
			$check = $("<INPUT type=checkbox value='true' class='editor-checkbox' hideFocus title='check'/>");
			$check.appendTo(args.container);
			$check.focus();
		};
		this.destroy = function(){
			$check.remove();
		};
		this.focus = function(){
			$check.focus();
		};
		this.loadValue = function(item){
			defaultValue = item[field];
			if (defaultValue == 'Y'){
				$check.attr("checked", "checked");
			} else {
				$check.removeAttr("checked");
			}
		};
		this.serializeValue = function(){
			//return !!$check.attr("checked");
			return $check[0].checked ? 'Y' : 'N';
		};
		this.applyValue = function(item, state){
			item[field] = state;
		};
		this.isValueChanged = function(){
			var changed = (this.serializeValue() !== defaultValue);
			return changed;
		};
		this.validate = function(){
			return Slick.validateCell($check.val(), column, item);
		};
		this.init();
	}
	var isWordKeyDown = function(event){
		if(! (event && event.type == 'keydown'))return false;
		var keyCode = event.which || event.keyCode;
		if(Slick.isFunctionKeyCode(keyCode))return false;//function key
		if(keyCode == 229)return true;// 크롬에서 한글은 229
		var chr = (String.fromCharCode(keyCode)||'').toUpperCase();
		var check = (
			(chr >= '0' && chr <= '9') ||
			(chr >= 'A' && chr <= 'Z')
		);
		return check;
	};
	function LongTextEditor(args){
		var $input, $wrapper;
		var defaultValue;
		var scope = this;
		var column = args.column;
		var field = column.field;
		var item = args.item;
		var grid;
		var containerNode = args.grid.getContainerNode();
		var contNodeOverflow = $(containerNode).css('overflow');
		this.init = function(){
			$(containerNode).css('overflow', 'visible');
			$wrapper = $("<div style='z-index:10000;position:absolute;background:white;padding:5px;border:3px solid gray; color: black'/>")
					.appendTo(containerNode);//-moz-border-radius:10px; border-radius:10px;
			$input = $("<textarea hidefocus rows='5' cols='50' style='backround:white;width:100%;height:80px;border:0;outline:0' title='CTRL + ENTER to save' >")
					.appendTo($wrapper);
			this.$input = $input;
			//$("<DIV style='text-align:left'>&lt; 단축키 1.Save : Ctrl+Enter  2.Cancel : Esc &gt;</DIV>")
			//		.appendTo($wrapper);
			$("<div style='text-align:right'><button>Save(CTRL + ENTER)</button><button>Cancel(ESC)</button></div>")
					.appendTo($wrapper);
			$wrapper.find("button:first").bind("click", this.save);
			$wrapper.find("button:last").bind("click", this.cancel);
			$input.bind("keydown", this.handleKeyDown);
			if(column.maxlength){
				$input.attr('maxlength', column.maxlength);
			}
			grid = args.grid;
			scope.position(args.position);
			$input.select().focus();// 크롬에서 한글
			// 여기서는 하면 안되네 $input.bind('focusout', function(){args.cancelChanges();});
		};
		this.handleKeyDown = function(e){
			if (e.which == $.ui.keyCode.ENTER && e.ctrlKey){
				scope.save();
				e.stopImmediatePropagation();
			} else if (e.which == $.ui.keyCode.ESCAPE){
				e.preventDefault();
				scope.cancel();
				e.stopImmediatePropagation();
			} else if (e.which == $.ui.keyCode.TAB && e.shiftKey){
				e.preventDefault();
				args.grid.navigatePrev();
			} else if (e.which == $.ui.keyCode.TAB){
				e.preventDefault();
				args.grid.navigateNext();
			} else if (e.which == $.ui.keyCode.UP ||
								 e.which == $.ui.keyCode.DOWN ||
								 e.which == $.ui.keyCode.LEFT ||
								 e.which == $.ui.keyCode.RIGHT ||
								 e.which == $.ui.keyCode.ENTER){
				e.stopImmediatePropagation();
			}
		};
		this.save = function(){
			args.commitChanges();
		};
		this.cancel = function(){
			$input.val(defaultValue);
			args.cancelChanges();
		};
		this.hide = function(){
			$wrapper.hide();
		};
		this.show = function(){
			$wrapper.show();
		};
		this.position = function(position){
			var gridPosition = grid.getGridPosition();
			var top = position.top - gridPosition.top - 5;
			var left = position.left - gridPosition.left - 5;
			$wrapper
					.css("top", top)
					.css("left", left)
		};
		this.destroy = function(){
			$(containerNode).css('overflow', contNodeOverflow);
			$wrapper.remove();
		};
		this.focus = function(){
			$input.focus();
		};
		this.loadValue = function(item){
			defaultValue = item[field];
			if(! isWordKeyDown(event)){
				$input.val(defaultValue||'');
			}
			$input.select();
		};
		this.serializeValue = function(){
			return $input.val();
		};
		this.applyValue = function(item, state){
			item[field] = state;
		};
		this.isValueChanged = function(){
			var changed = (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
			return changed;
		};
		this.validate = function(){
			return Slick.validateCell($input.val(), column, item);
		};
		this.init();
	}
	function linkCssFunc(item, column){
		return item.status != 'I' ? ['slick-link'] : null;
	}
	function linkCssFuncAllStatus(item, column){
		return ['slick-link'];
	}
	function levelCssFunc(item, column){
		return 'slick-cb-'+(item._level||0)+'-level';
	}
	// 엑셀업로드 업데이트 콜백 만들기
	function createUpdateProcessArrayFunction(s){
		var grid = s.grid;
		var s_fields = s.fields;// 업뎃할 필드 목록
		var columns = grid.getColumns();
		if(s.fields == null || s.fields.length == 0){
			s_fields = [];
			for(var i = 0, len = columns.length; i < len; i++){
				var column = columns[i];
				if(column.id && (column.iedit || column.uedit)){
					s_fields.push(column.id);
				}
			}
		}
		var chk_columns = [];
		for(var i = 0, len = columns.length; i < len; i++){
			if(_.includes(s_fields, columns[i].id)){
				chk_columns.push(columns[i]);
			}
		}
		var fn_updateItem = s.fn_updateItem;
		return function(array, s){
			// 업로드하여 받은 데이터
			var items = grid.makeArrayToItems(array);
			grid.beginUpdate();
			for(var i = 0, len = items.length; i < len; i++){
				var item1 = items[i];
				if(item1.id != null){
					var item2 = grid.getItemById(item1.id);// 원래 있는
					if(item2 != null){
						var changeCount = 0;
						for(var i2 = 0, len2 = chk_columns.length; i2 < len2; i2++){
							var column = chk_columns[i2];
							var field = column.id;
							// 에딧 가능한지
							var str = item1[field];
							if(setCellWhenValid(item2, column, str)){
								changeCount++;
							}
						}// for fields
						// 바뀌었을 때만
						if(changeCount > 0){
							if(fn_updateItem){
								fn_updateItem(item2);// 금액 업뎃
							}
							grid.updateItem(item2);
						}
					}
				}
			}// for rows
			grid.endUpdate();
		}
	}
	$.extend(Slick, {
		linkCssFunc: linkCssFunc,
		linkCssFuncAllStatus: linkCssFuncAllStatus,
		levelCssFunc: levelCssFunc,
		makeGrid: makeGrid,
		isCellEditableByIC: isCellEditableByIC,
		validateCell: validateCell,
		DefaultFormatter: DefaultFormatter,
		cellStyleFunction: cellStyleFunction,
		setCellWhenValid: setCellWhenValid,
		dataItemColumnValueSetter: dataItemColumnValueSetter,
		dataItemColumnValueExtractor: dataItemColumnValueExtractor,
		DefaultEditor: DefaultEditor,
		AutoCompleteEditor: AutoCompleteEditor,
		CheckboxEditor: CheckboxEditor,
		LongTextEditor: LongTextEditor,
		validTrue: {valid: true, msg: null},
		getValidValue: getValidValue,
		//validateConstraints: validateConstraints,
		checkColumn: {id: "check", name: "", datatype: 'check', iedit: true, uedit: true, width: 18, norschange: true, allcheck: true},
		checkItemFilter: function(it){
			return it.check == 'Y';
		},
		createUpdateProcessArrayFunction: createUpdateProcessArrayFunction,
		getcellvalue: getcellvalue,
		getcellcodertext: getcellcodertext,
		getcelltext: getcelltext,
		isFunctionKeyCode: function(keyCode){
			return keyCode >= 112 && keyCode <= 123;
		},
		width: {
			ymdhms: 150,
			ymdhm: 120,
			desc: 250,
			name: 150,
			adrs: 200,
			usernm: 100
		},
		dummy: 1
	});
})();

(function(){
	window.SlickColumn = window.SlickColumn || {};
	$.extend(SlickColumn, {
		number: {editor: Slick.DefaultEditor, align: 'right', maxlength: 15},
		ymd: {editor: Slick.DefaultEditor, align: 'center', maxlength: 8, width: 100},
		ym: {editor: Slick.DefaultEditor, align: 'center', maxlength: 6, width: 100},
		year: {editor: Slick.DefaultEditor, align: 'center', maxlength: 4},
		mm: {editor: Slick.DefaultEditor, align: 'center', maxlength: 2},
		hms: {editor: Slick.DefaultEditor, align: 'center', maxlength: 6},
		hm: {editor: Slick.DefaultEditor, align: 'center', maxlength: 4},
		thm: {editor: Slick.DefaultEditor, align: 'center', maxlength: 5},
		hh: {editor: Slick.DefaultEditor, align: 'center', maxlength: 2},
		mi: {editor: Slick.DefaultEditor, align: 'center', maxlength: 2},
		ss: {editor: Slick.DefaultEditor, align: 'center', maxlength: 2},
		ymdhm: {editor: Slick.DefaultEditor, align: 'center', maxlength: 12},
		ymdhms: {editor: Slick.DefaultEditor, align: 'center', maxlength: 14},
		ssn: {editor: Slick.DefaultEditor, align: 'center', maxlength: 13},
		bizno: {editor: Slick.DefaultEditor, align: 'center', maxlength: 10},
		text: {editor: Slick.DefaultEditor, maxlength: 50},
		longtext: {editor: Slick.LongTextEditor, maxlength: 1000, width: 200},
		//check: {editor: Slick.CheckboxEditor, align: 'center', cssClass: 'center'},
		check: {editor: null, align: 'center', cssClass: 'text-center'},// editor null 로 하면 autoEdit 에서도 checkbox 안나온다.
		checkImpl: {id: "check", name: "", datatype: 'check', iedit: true, uedit: true, width: 40, norschange: true, allcheck: true},
		select: {align: 'center'},
		itemno: {align: 'right', getvalue: function(item, column){
			return item.id + 1;
		}},
		itemnoImpl: {id: 'itemno', name: 'No', datatype: 'itemno', width: 40},
		ridx: {align: 'right', getvalue: function(item, column){
			return column.grid.getRowById(item.id);
		}},
		ridxImpl: {id: 'ridx', name: 'No', datatype: 'ridx', width: 40},
		autocomplete: {editor: Slick.AutoCompleteEditor},
		//
		cdnm: {align: 'center'},
		code: {align: 'center'},
		pnm: {align: 'center'}
	});
})();

var SlickFormat = {};
(function(){
	var re_ymd = /^(\d{4})(\d{2})(\d{2})$/;
	var re_ym = /^(\d{4})(\d{2})$/;
	var re_year = /^\d{4}$/;
	var re_mm = /^\d{2}$/;
	var re_hms = /^(\d{2})(\d{2})(\d{2})$/;
	var re_hm = /^(\d{2})(\d{2})$/;
	var re_hh = /^\d{2}$/;
	var re_mi = /^\d{2}$/;
	var re_ss = /^\d{2}$/;
	var re_ymdhm = /^(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})$/;
	var re_ymdhms = /^(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})$/;
	var re_ssn = /^(\d{6})(\d{7})$/;
	var re_code = /^[_0-9A-Za-z]+$/;
	var re_bizno = /^(\d{3})(\d{2})(\d{5})$/;
	var re_postno = /^(\d{3})(\d{3})$/;
	$.extend(SlickFormat, {
		check: function(str, format){
			if(uu.isNullOrEmptyString(str)) return true;
			switch(format){
			case 'number':
				var num = Number(str);
				return ! isNaN(num);
			case 'ymd':
				if(! re_ymd.test(str)) return false;
				var year = Number(RegExp.$1);
				var month = Number(RegExp.$2);
				var date = Number(RegExp.$3);
				var dt = new Date(year, month-1, date);
				return dt.getFullYear() == year && dt.getMonth() == month-1 && date == dt.getDate();
			case 'ym':
				if ( ! re_ym.test(str)) return false;
				var year = Number(RegExp.$1);
				var month = Number(RegExp.$2);
				return month >= 1 && month <= 12;
			case 'year':
				return re_year.test(str);
			case 'mm':
				if ( ! re_mm.test(str)) return false;
				var month = Number(str);
				return month >= 1 && month <= 12;
			case 'hms':
				if(! re_hms.test(str)) return false;
				var hh = Number(RegExp.$1);
				var mi = Number(RegExp.$2);
				var ss = Number(RegExp.$3);
				return (hh >= 0 && hh <= 23) && (mi >= 0 && mi <= 59) && (ss >= 0 && ss <= 59);
			case 'hm':
				if(! re_hm.test(str)) return false;
				var hh = Number(RegExp.$1);
				var mi = Number(RegExp.$2);
				return (hh >= 0 && hh <= 23) && (mi >= 0 && mi <= 59);
			case 'thm':
				var is_T = str && _.startsWith(str, 'T');
				var str1 = is_T ? str.substr(1) : str;
				if(! re_hm.test(str1)) return false;
				var hh = Number(RegExp.$1);
				var mi = Number(RegExp.$2);
				return (hh >= 0 && hh <= 23) && (mi >= 0 && mi <= 59);
			case 'hh':
				if(! re_hh.test(str)) return false;
				var hh = Number(str);
				return (hh >= 0 && hh <= 23);
			case 'mi':
				if(! re_mi.test(str)) return false;
				var mi = Number(str);
				return (mi >= 0 && mi <= 59);
			case 'ss':
				if(! re_ss.test(str)) return false;
				var ss = Number(str);
				return (ss >= 0 && ss <= 59);
			case 'ymdhm':
				if(! re_ymdhm.test(str)) return false;
				var year = Number(RegExp.$1);
				var month = Number(RegExp.$2);
				var date = Number(RegExp.$3);
				var dt = new Date(year, month-1, date);
				if(! (dt.getFullYear() == year && dt.getMonth() == month-1 && date == dt.getDate())){
					return false;
				}
				var hh = Number(RegExp.$4);
				var mi = Number(RegExp.$5);
				return (hh >= 0 && hh <= 23) && (mi >= 0 && mi <= 59);
			case 'ymdhms':
				if(! re_ymdhms.test(str)) return false;
				var year = Number(RegExp.$1);
				var month = Number(RegExp.$2);
				var date = Number(RegExp.$3);
				var dt = new Date(year, month-1, date);
				if(! (dt.getFullYear() == year && dt.getMonth() == month-1 && date == dt.getDate())){
					return false;
				}
				var hh = Number(RegExp.$4);
				var mi = Number(RegExp.$5);
				var ss = Number(RegExp.$6);
				return (hh >= 0 && hh <= 23) && (mi >= 0 && mi <= 59) && (ss >= 0 && ss <= 59);
			case 'ssn':
				if(! re_ssn.test(str)) return false;
				var num = RegExp.$1 + RegExp.$2;
				var sum = 0;
				var last = num.charCodeAt(12) - 0x30;
				var bases = "234567892345";
				for (var i=0; i<12; i++){
					if (isNaN(num.substring(i,i+1))) return false;
					sum += (num.charCodeAt(i) - 0x30) * (bases.charCodeAt(i) - 0x30);
				}
				var mod = sum % 11;
				return ((11 - mod) % 10 == last);
			case 'code':
				if(! re_code.test(str)) return false;
				return true;
			case 'bizno':
				if(! re_bizno.test(str))return false;
				return true;
			case 'postno':
				if(! re_postno.test(str))return false;
				return true;
			case 'email':
				if(! uu.isEmailFormat(str))return false;
				return true;
			}
			return true;
		},
		getCheckMessage: function(format){
			var msg1 = '', msg2 = '';
			switch(format){
			case 'number':
				msg1 = '숫자';msg2 = '';
				break;
			case 'ymd':
				msg1 = '일자';msg2 = ' 예: '+uu.dateut.getYmd(new Date());
				break;
			case 'ym':
				msg1 = '년월';msg2 = ' 예: '+uu.formatDate(new Date(), 'yyyyMM');
				break;
			case 'year':
				msg1 = '연도';msg2 = ' 예: '+uu.formatDate(new Date(), 'yyyy');
				break;
			case 'mm':
				msg1 = '월';msg2 = ' 예: '+uu.formatDate(new Date(), 'MM');
				break;
			case 'hms':
				msg1 = '시분초';msg2 = ' 예: '+uu.formatDate(new Date(), 'hhmmss');
				break;
			case 'hm':
				msg1 = '시분';msg2 = ' 예: '+uu.formatDate(new Date(), 'hhmm');
				break;
			case 'thm':
				msg1 = '시분';msg2 = ' 예: T'+uu.formatDate(new Date(), 'hhmm');
				break;
			case 'hh':
				msg1 = '시';msg2 = ' 예: '+uu.formatDate(new Date(), 'hh');
				break;
			case 'mi':
				msg1 = '분';msg2 = ' 예: '+uu.formatDate(new Date(), 'mm');
				break;
			case 'ss':
				msg1 = '초';msg2 = ' 예: '+uu.formatDate(new Date(), 'ss');
				break;
			case 'ymdhm':
				msg1 = '년월일시분';msg2 = ' 예: '+uu.formatDate(new Date(), 'yyyyMMddhhmm');
				break;
			case 'ymdhms':
				msg1 = '년월일시분초';msg2 = ' 예: '+uu.formatDate(new Date(), 'yyyyMMddhhmmss');
				break;
			case 'ssn':
				msg1 = '주민번호';msg2 = ' 예: 8103121234567';
				break;
			case 'code':
				msg1 = '코드';msg2 = ' 예: 1000';
				break;
			case 'bizno':
				msg1 = '사업자번호';msg2 = ' 예: 1234567890';
				break;
			case 'postno':
				msg1 = '우편번호';msg2 = ' 예: 123456';
				break;
			case 'email':
				msg1 = '이메일';msg2 = ' 예: aaaa@bbbb.com';
				break;
			}
			return msg1+' 형식이 아닙니다. '+msg2;
		},
		format: function(str, format, scale){
			if(uu.isNullOrEmptyString(str)) return '';
			switch(format){
			case 'number':
				var num = Number(str);
				return uu.formatNumber(num, scale);
			case 'ymd':
				str = uu.makeDateStrIfNumber(str, 8);
				if(! re_ymd.test(str)) return str;
				return RegExp.$1 + '-' + RegExp.$2 + '-' + RegExp.$3;
			case 'ym':
				str = uu.makeDateStrIfNumber(str, 6);
				if ( ! re_ym.test(str)) return str;
				return RegExp.$1 + '-' + RegExp.$2;
			case 'year':
				str = uu.makeDateStrIfNumber(str, 4);
				return str;
			case 'mm':
				return str;
			case 'hms':
				if(! re_hms.test(str)) return str;
				return RegExp.$1 + ':' + RegExp.$2 + ':' + RegExp.$3;
			case 'hm':
				if(! re_hm.test(str)) return str;
				return RegExp.$1 + ':' + RegExp.$2;
			case 'thm':
				var is_T = str && _.startsWith(str, 'T');
				var str1 = is_T ? str.substr(1) : str;
				if(! re_hm.test(str1)) return str;
				return (is_T ? 'T' : '')+RegExp.$1 + ':' + RegExp.$2;
			case 'hh':
				return str;
			case 'mi':
				return str;
			case 'ss':
				return str;
			case 'ymdhm':
				str = uu.makeDateStrIfNumber(str, 12);
				if(! re_ymdhm.test(str)) return str;
				return RegExp.$1 + '-' + RegExp.$2 + '-' + RegExp.$3 + ' ' + RegExp.$4 + ':' + RegExp.$5;
			case 'ymdhms':
				str = uu.makeDateStrIfNumber(str, 14);
				if(! re_ymdhms.test(str)) return str;
				return RegExp.$1 + '-' + RegExp.$2 + '-' + RegExp.$3 + ' ' + RegExp.$4 + ':' + RegExp.$5 + ':' + RegExp.$6;
			case 'ssn':
				if(! re_ssn.test(str)) return str;
				return RegExp.$1 + '-' + RegExp.$2;
			case 'bizno':
				if(! re_bizno.test(str)) return str;
				return RegExp.$1 + '-' + RegExp.$2 + '-' + RegExp.$3;
			case 'postno':
				if(! re_postno.test(str)) return str;
				return RegExp.$1 + '-' + RegExp.$2;
			}
			return str;
		},
		unformat: function(str, format, scale){
			if(uu.isNullOrEmptyString(str)) return '';
			switch(format){
			case 'number':
				var num = Number(str.replace(/,/g, ''));
				return isNaN(num) ? 0 : num;
			case 'ymd':
				return str.replace(/[^\d]/g, '').substr(0, 8);
			case 'ym':
				return str.replace(/[^\d]/g, '').substr(0, 6);
			case 'year':
				return str.replace(/[^\d]/g, '').substr(0, 4);
			case 'mm':
				return str.replace(/[^\d]/g, '').substr(0, 2);
			case 'hms':
				return str.replace(/[^\d]/g, '').substr(0, 6);
			case 'hm':
				return str.replace(/[^\d]/g, '').substr(0, 4);
			case 'thm':
				var is_T = _.startsWith(str, 'T');
				return (is_T ? 'T':'')+str.replace(/[^\d]/g, '').substr(0, 4);
			case 'hh':
				return str.replace(/[^\d]/g, '').substr(0, 2);
			case 'mi':
				return str.replace(/[^\d]/g, '').substr(0, 2);
			case 'ss':
				return str.replace(/[^\d]/g, '').substr(0, 2);
			case 'ssn':
				return str.replace(/[^\d]/g, '').substr(0, 13);
			case 'bizno':
				return str.replace(/[^\d]/g, '').substr(0, 10);
			case 'postno':
				return str.replace(/[^\d]/g, '').substr(0, 6);
			}
			return str;
		}
	});
})();
window.SlickUtil = {
	initGridResizer: function(div){
		var $slickResizer = $('.slickgrid-resizer', div).not('.no-resize');
		$slickResizer.resizable && $slickResizer.resizable({handles: 's', grid: 50, maxHeight: 1000});
		$slickResizer.on('resize', function(event, ui){
			var temp = $('.slickgrid:eq(0)', this)[0];
			if(temp && temp.grid){
				temp.grid.resizeCanvas();
			}
		});
	},
	// makeContextmenuHandler: function(grid, mItemListFn){
	// 	return function(event){
	// 		if(grid.getEditorLock().isActive())return;
	// 		$('.slick-contextmenu').remove();
	// 		event.preventDefault();
	// 		var rc = grid.getCellFromEvent(event);
	// 		if(! (rc && rc.row >= 0 && rc.cell >= 0))return;
	// 		var column = grid.getColumns()[rc.cell];
	// 		var item = grid.getItemByRow(rc.row);
	// 		var param1 = {grid: grid, column: column, item: item};
	// 		if(event.data == null)event.data = {};
	// 		$.extend(event.data, param1);
	// 		var mItemList = mItemListFn(event);
	// 		if(! (mItemList && mItemList.length)){
	// 			return;
	// 		}
	// 		var $div_cmenu = $('<ul class="slick-contextmenu uu-nowrap"/>');
	// 		_.each(mItemList, function(mItem){
	// 			$('<li><a>'+escapeTextHtml(mItem.text)+'</a></li>').data(mItem).appendTo($div_cmenu);
	// 		});
	// 		$div_cmenu.one('click', 'li', function(event2){
	// 			var el = this, $el = $(el);
	// 			var callback = $el.data('callback');
	// 			if(_.isFunction(callback)){
	// 				if(event2.data == null)event2.data = {};
	// 				$.extend(event2.data, param1, $el.data());
	// 				callback(event2);
	// 			}
	// 		});
	// 		$div_cmenu.appendTo(document.body).css({
	// 			top: event.pageY,
	// 			left: event.pageX
	// 		});
	// 		setTimeout(function(){
	// 			$('body').one('click keydown', function(){
	// 				$div_cmenu.remove();
	// 			});
	// 		});
	// 	};
	// },
	// makeRowMoveKeyDownHandler: function(grid){
	// 	return function(e, args){
	// 		var rc = grid.getActiveCell();
	// 		if(! rc)return;
	// 		var row = rc.row;
	// 		var cell = rc.cell;
	// 		if( ! (row >= 0 && cell >= 0))return;
	// 		if(! (e.ctrlKey && ! e.altKey && ! e.shiftKey &&
	// 			(e.which == $.ui.keyCode.UP || e.which == $.ui.keyCode.DOWN)
	// 		))return;
	// 		if(grid.getEditorLock().isActive())return;
	// 		var items = grid.getItems();
	// 		var newRow = row + (e.which == $.ui.keyCode.UP ? -1 : 1);
	// 		if( ! (newRow >= 0 && newRow < items.length))return;
	// 		e.stopImmediatePropagation();
	// 		Array.move(items, row, newRow);
	// 		grid.getDataView().setItems(items);
	// 		grid.setActiveCell(newRow, cell);
	// 		grid.invalidate();
	// 	};
	// },
	checkRadio: function(grid, item, columnid){
		if(item[columnid] == 'Y'){
			var grid_items = grid.getItems();
			for(var i = 0, len = grid_items.length; i < len; i++){
				var item1 = grid_items[i];
				if(item1 != item){
					if(item1[columnid] == 'Y'){
						item1[columnid] = 'N';
						grid.updateItem(item1);
					}
				}
			}
		}
	},
	/*지정된 열을 기준으로 그리드의 데이터 뷰를 정렬*/
	sortByCols: function(grid, cols){
		var dataView = grid.getDataView();
		dataView.sort(function(item1, item2){
			var sign, value1, value2, result, column;
			for (var i = 0, l = cols.length; i < l; i++){
				column = cols[i].sortCol;
				sign = cols[i].sortAsc ? 1 : -1;
				value1 = getcellvalue(item1, column)||'';
				value2 = getcellvalue(item2, column)||'';
				result = (value1 == value2 ? 0 : (value1 > value2 ? 1 : -1)) * sign;
				if (result != 0){
					return result;
				}
			}
			return 0;
		}, true);
	},
	collapseLevel: function(grid, level, _collapsed){
		var items2 = grid.getItems();
		grid.beginUpdate();
		for(var i = 0, len = items2.length; i < len; i++){
			var item2 = items2[i];
			if(item2._level == level){
				item2._collapsed = _collapsed;
				grid.updateItem(item2, true);
			}
		}
		grid.endUpdate();
	},
	resizeCanvasWithin: function(parent){
		$('.slickgrid', parent).each(function(){
			if(this.grid){
				this.grid.resizeCanvas();
				this.grid.invalidate();// 이것도 해야 되네 2022 05 31
			}
		});
	},
	applyMoveRowsFunction: function(grid, options){
		// handle column 속성에 아래 추가
		// behavior: "selectAndMove",// 이게 중요하네
		// cssClass: "slickgrid-cell-reorder fa fa-bars text-center align-middle"
		var moveRowsPlugin = new Slick.RowMoveManager({
			cancelEditOnDrag: true
		});
		moveRowsPlugin.onBeforeMoveRows.subscribe(function(e, data){
			for(var i = 0; i < data.rows.length; i++){
				// no point in moving before or after itself
				if(data.rows[i] == data.insertBefore || data.rows[i] == data.insertBefore - 1){
					e.stopPropagation();
					return false;
				}
			}
			return true;
		});
		moveRowsPlugin.onMoveRows.subscribe(function(e, args){
			var data = grid.getItems();
			var extractedRows = [], left, right;
			var rows = args.rows;
			var insertBefore = args.insertBefore;
			left = data.slice(0, insertBefore);
			right = data.slice(insertBefore, data.length);
			rows.sort(function(a, b){return a - b;});
			for(var i = 0; i < rows.length; i++){
				extractedRows.push(data[rows[i]]);
			}
			rows.reverse();
			for(var i = 0; i < rows.length; i++){
				var row = rows[i];
				if (row < insertBefore){
					left.splice(row, 1);
				} else {
					right.splice(row - insertBefore, 1);
				}
			}
			data = left.concat(extractedRows.concat(right));
			var selectedRows = [];
			for(var i = 0; i < rows.length; i++)
				selectedRows.push(left.length + i);
			grid.resetActiveCell();
			grid.getDataView().setItems(data);
			grid.setSelectedRows(selectedRows);
			grid.render();
			options && options.callback && setTimeout(options.callback);
		});
		grid.registerPlugin(moveRowsPlugin);
	},
	getSelectedRanges: function(grid){
		var selectionModel = grid.getSelectionModel();
		var ranges;
		if(selectionModel){
			ranges = selectionModel.getSelectedRanges();
		}
		if(ranges && ranges[0]){
			if(selectionModel.constructor != Slick.CellSelectionModel){
				var activeCell = grid.getActiveCell();
				if(activeCell){
					ranges[0].fromCell = ranges[0].toCell = activeCell.cell;
					return ranges;
				}
			}else{
				return ranges;
			}
		}
	},

	applyDragEvent: function(grid, ops){
		// RowSelectionModel 인 경우만 먹네
		grid.onDragStart.subscribe(function(e, dd){
			if(Slick.GlobalEditorLock.isActive()){
				return;
			}
			var cell = grid.getCellFromEvent(e);
			if(! cell){
				return;
			}
			dd.row = cell.row;
			var data = grid.getFilteredItems();
			if(! data[dd.row]){
				return;
			}
			e.stopImmediatePropagation();
			dd.mode = ops.mode;

			var selectedItems;
			if(ops.selectType == 'checked'){
				selectedItems = _.map(grid.getCheckedItems(), function(it){
					return $.extend({}, it);
				});
			}else{
				// selectedRows sort 해야하네
				var selectedRows = grid.getSelectedRows();
				if(! selectedRows.length || $.inArray(dd.row, selectedRows) == -1){
					selectedRows = [dd.row];
					grid.setSelectedRows(selectedRows);
				}
				var selectedItems = _.map(_.sortBy(selectedRows), function(row){
					return $.extend({}, grid.getItemByRow(row));
				});
			}
			if(! (selectedItems && selectedItems.length))return;
			//dd.rows = selectedRows;
			dd.count = selectedItems.length;
			dd.selectedItems = selectedItems;
			var proxy = ops.createProxy ?
				ops.createProxy(dd) :
				$("<span class='slick-dnd-helper uu-nowrap'></span>")
				.text((ops.proxyText||'') + " " + dd.count + " selected row(s)")
				.appendTo("body");
			dd.helper = proxy;
			return proxy;// return false 이면 onDrag 안됨
		});
		grid.onDrag.subscribe(function(e, dd){
			dd.helper && dd.helper.css({top: e.pageY + 5, left: e.pageX + 20});
		});
		// drop 후 실행되네
		grid.onDragEnd.subscribe(function(e, dd){
			dd.helper && dd.helper.remove();
		});
	},

	destroyGridWithin: function(dCont){
		$('.slickgrid', dCont).each(function(){
			var el = this;
			if(el && el.grid){
				el.grid.destroy();
				el.grid = null;
			}
		});
	},
	gridExpandLevel: function(grid, level){
		var items2 = grid.getItems();
		grid.beginUpdate();
		_.each(items2, function(it){
			it._collapsed = !(it._level < level);
			grid.updateItem(it, true);
		});
		grid.endUpdate();
	},
	setEditableWithin: function(cont, editable){
		$('.slickgrid', cont).each(function(){
			var self = this;
			if(self.grid){
				self.grid.setOptions({editable: !!editable});
			}
		});
	},
	getDescendantList: function(grid, it){// 내 자손 목록
		var list1 = grid.getItems();
		var cIt = it;
		var result = [];
		function rec1(cIt){
			var dcList = _.filter(list1, {pid: cIt.id});// 내 바로 밑 자식들
			if(dcList && dcList.length){
				result = result.concat(dcList);
				_.each(dcList, function(it){
					rec1(it);
				});
			}
		};
		rec1(cIt);
		return result;
	},
	getGridObjectByElem: function(e){
		if(! (e && $(e)[0] != null && ! $(e).is('input')))return null;
		if($(e).is('.slickgrid'))return $(e)[0].grid;
		var slickgridElem = $(e).closest('.slickgrid')[0];
		return slickgridElem && slickgridElem.grid;
	},
	getCurrGridObject: function(){
		return SlickUtil.getGridObjectByElem(uu.getCurrFocusElem());
	},
	triggerAtActiveCell: function(grid, eventName){
		grid.trigger(grid[eventName], grid.getActiveCell());
	},
	getCheckActiveItem: function(grid, name){
		var item = grid.getActiveItem();
		if(!uu.checkAlert(!! item, uu.commsg.alert.noSelectedItem+' ['+name+']')) return;
		return item;
	},
	makeRegexValidator: function(pattern, msg){
		return function(str, col, it){
			if(! pattern.test(str)){
				return {valid: false, msg: msg, column: col, item: it};
			}
			return Slick.validTrue;
		};
	},
	setValidActiveCell: function(grid, row, cell){
		var row = Math.min(row, grid.getDataLength());
		setTimeout(function(){grid.setActiveCell(row, cell);});// setTimeout 반드시
	},

	makeSimpleGetvalueFunc: function(itext, utext){
		return function(item, column){
			return item.status == 'I' ? itext : utext;
		}
	},

	makePctGetvalueFunc: function(k1, k2){
		return function(it, column){
			var grid = column.grid;
			var a = grid.getColumnIndex(k1) >= 0 ? +grid.getcellvalue(it, grid.getColumnById(k1)) : it[k1];
			var b = grid.getColumnIndex(k2) >= 0 ? +grid.getcellvalue(it, grid.getColumnById(k2)) : it[k2];
			return (a && b) ? +Big(a).div(b).times(100) : null;
		}
	},

	setItemsWithColumn: function(grid, list, op){
		//isColumnSort, isHeaderInclude, ks, omitNodataColumn
		var op = op||{};
		var isColumnSort = op.isColumnSort;
		var isHeaderInclude = op.isHeaderInclude;
		var ks = op.ks;
		var omitNodataColumn = op.omitNodataColumn;
		var includeCheck = op.includeCheck;
		var prependColumns = op.prependColumns;

		if(ks == null){
			ks = uu.getKeysFromList(list, omitNodataColumn);
		}
		if(isColumnSort){
			ks.sort();
		}
		var columns = [];
		if(op.forceColumns){
			columns = op.forceColumns;
		}else{
			if(includeCheck){
				columns.push($.extend({}, SlickColumn.checkImpl));
			}
			columns.push($.extend({}, SlickColumn.itemnoImpl));
			var prependColIdArr = [];
			if(prependColumns){
				columns = columns.concat(prependColumns);
				prependColIdArr = _.map(prependColumns, 'id');
			}
			var columns2 = _(ks).filter(function(id){
				return ! _.includes(prependColIdArr, id);
			}).map(function(id, i){
				return {id: id, name: id};
			}).value();
			columns = columns.concat(columns2);
		}
		if(op.eachColumnFn){
			_.each(columns, function(col){
				op.eachColumnFn(col);
			});
		}
		grid.resetColumns(columns);
		if(isHeaderInclude){
			var header_data = {};
			for(var i = 0, len = ks.length; i < len; i++){
				header_data[ks[i]] = ks[i];
			}
			list = [header_data].concat(list);
		}
		grid.setItems(list);
	},

	makeGridFalse: function(grid, item, columnId, msg){
		return {
			valid: false,
			msg: msg,
			item: item,
			column: columnId ? grid.getColumnById(columnId) : null
		};
	},

	showAlertActiveFlashCell: function(msg, grid, itemId, cid){
		return showAlert(null, msg, function(){
			grid.activeFlashCellWithId(itemId, cid);
		});
	},

	dummay: 1
};// window.SlickUtil
$(function(){
	SlickUtil.initGridResizer(document.body);
});
//https://github.com/mleibman/SlickGrid/wiki/DataView
//getItems() - Returns the original data array.
//getItem(row) - With a row index of an item in the grid, return the item.
//getItemMetadata(idx) - Returns the item metadata at a given index.
//getLength() - Returns the number of rows in the grid.
//DataView exposes several methods in order to map ids to items to rows in the grid to indices in the original data array:
//
//getItemById(id) - With an item's id, return the item.
//getIdxById(id) - With an item's id, return the index of the item in the original data array.
//getRowById(id) - With an item's id, return the row of the item in the grid.
//getItemByIdx(idx) - With an index of an item in the original data array, return the item. Equivalent to getItems()[index].
//mapIdsToRows(idArray) - Maps an array of item ids into rows in the grid.
//mapRowsToIds(rowArray) - Maps an array of rows in the grid into item ids.
