var BASEPATH = '/api/eapproval';
var params = {
	column: '',
    keyword: ''
};

var page = {
	init(gridConfig){
		this.grid.init(gridConfig);
		page.selectOptions("#apAppDt");
        page.selectOptions("#apCmpltDt");
		page.getFormList();
		// if($('#docStatusType').val() === 'complted') {
        //     $('#modalCmpltDt').attr('visibility', 'visible');
        // }
	},
	
	// 단건 검색
	searchKeyword(){
		event.preventDefault();
		const searchOption = $('#searchOption').val();
		if(!searchOption) {
			gaiaPortal.customAlert("검색 옵션을 선택해주세요.");
			// alert("검색 옵션을 선택해주세요.");
			return
		}

		const keyword = $('input[name="keyword"]').val();
		if(!keyword) {
			gaiaPortal.customAlert("검색어를 입력해주세요.")
			// alert("검색어를 입력해주세요.");
			return
		}
		params.column = searchOption;
		params.keyword = keyword;
		
		searchForm[searchOption] = keyword;

		// page.grid.obj.methods.readData(params);
		page.grid.obj.methods.readData(searchForm);

		page.setSeachKeyword();
	},
	// 상세 검색
	searchDetail(){
        event.preventDefault();
        if(!page.validateForm()){
            return;
        }
		$('.lay_pop').removeClass('on'); 
        var searchFormArray = $('#searchForm').serializeArray();
        
        $.each(searchFormArray, function() {
            searchForm[this.name] = this.value;
        });

        page.getDetailSearch(searchForm);
        page.setSeachKeyword(searchForm);
    },
	// 상세 검색 data 로드
	getDetailSearch(searchForm) {
		params.keyword = '';
		searchForm = searchForm;
		page.grid.obj.methods.readData(searchForm);
	},

	// 검색 keyword 블록 세팅
	setSeachKeyword() {
		$('.selected_list').addClass('on');
		$('.selected_list').children().remove();
		let startAppDt = searchForm.startAppDt || '';  
    	let endAppDt = searchForm.endAppDt || '';
    	let startCmpltDt = searchForm.startCmpltDt || '';
    	let endCmpltDt = searchForm.endCmpltDt || '';

		//기안요청일
		if (startAppDt && endAppDt) {
			let combinedValue = `${startAppDt} ~ ${endAppDt}`;
			let keywordHtml = `
				<span class="selected_item">
					<span class="item keyword">${combinedValue}</span>
					<input type="hidden" name="startAppDt" value="${startAppDt}">
					<input type="hidden" name="endAppDt" value="${endAppDt}">
					<button type="button" class="icon_btn" onclick="page.deleteKeyword(this)">
						<i class="ic ic-close"></i>
						<span class="blind">삭제</span>
					</button>
				</span>
			`;
			$('.selected_list').append(keywordHtml);
			delete searchForm.startAppDt;
        	delete searchForm.endAppDt;
		}
		//결재완료일
		if(startCmpltDt && endCmpltDt) {
			let combinedValue = `${startCmpltDt} ~ ${endCmpltDt}`;
			let keywordHtml = `
				<span class="selected_item">
					<span class="item keyword">${combinedValue}</span>
					<input type="hidden" name="startCmpltDt" value="${startCmpltDt}">
					<input type="hidden" name="endCmpltDt" value="${endCmpltDt}">
					<button type="button" class="icon_btn" onclick="page.deleteKeyword(this)">
						<i class="ic ic-close"></i>
						<span class="blind">삭제</span>
					</button>
				</span>
			`;
			$('.selected_list').append(keywordHtml);
			delete searchForm.startCmpltDt;
        	delete searchForm.endCmpltDt;
		} 
		$.each(searchForm, function(key, value){
				let setValue = '';

				if (value !== null && value !== undefined && value !== '') {
					if(key === 'startAppDt') {
						key = 'apAppDt';
						setValue = value + ' ~ ';
					}
					if (key === 'endAppDt') {
						key = 'apAppDt';
						setValue = ' ~ ' + value;
					}
					if(key === 'startCmpltDt') {
						key = 'apCmpltDt';
						setValue = value + ' ~ ';
					}
					if (key === 'endCmpltDt') {
						key = 'apCmpltDt';
						setValue = ' ~ ' + value;
					}
					if (key === 'selectedForm') {
						setValue = $('#selectedForm option:selected').text();
					}
					if( key === 'selectedStatus') {
						setValue = $('#selectedStatus option:selected').text();
					}
					let keywordHtml = `
									<span class="selected_item">
										<span class="item keyword">${setValue === '' ? value : setValue}</span>
										<input type="hidden" name="${key}" value="${value}">
										<button type="button" class="icon_btn" onclick="page.deleteKeyword(this)">
											<i class="ic ic-close"></i>
											<span class="blind">삭제</span>
										</button>
									</span>
									`;
					$('.selected_list').append(keywordHtml);
				
		}
		})		
	},
	// keyword 삭제
	deleteKeyword(button){
		let selectedItem = $(button).closest('.selected_item');
		let hiddenInputs = selectedItem.find('input[type="hidden"]');
		let key = hiddenInputs.attr('name');

		selectedItem.remove();
		
		searchForm[key] = '';
		
		if($('.selected_list').children().length === 0) {
			$('.selected_list').removeClass('on');
		}
		page.grid.obj.methods.readData(searchForm);
	},
	// 상세검색 서식 리스트 조회
	getFormList(){
		$('#selectedForm').val('');
		$.ajax({
			url:BASEPATH+'/approval/formList',
			data: {
				pjtNo: pjtNo,
				cntrctNo: cntrctNo
			},
			success: function(data){
				var fList = data.details.formList;
				// console.log('fList', fList);
				$.each(fList, function(index, obj){
					$('#selectedForm').append(`<option value="${obj.frmNo}" frm-name="${obj.frmNmKrn}">${obj.frmNmKrn}</option>`)
				})
			}
		})
	},
	// 상세검색 셀렉트 박스 change이벤트(기안요청일, 결재완료일)
	changeHandler(selectboxId, selectedDt){
        if(selectboxId === '#apAppDt') {
            page.dateRange(selectedDt, "#startAppDt", "#endAppDt");
        } else if(selectboxId === '#apCmpltDt') {
            page.dateRange(selectedDt, "#startCmpltDt", "#endCmpltDt");
        }
    },
	// 상세검색 기간 셀렉트 옵션 세팅
	selectOptions(selectId){
        var options = [
            { value: '', text: "직접입력" },
            { value: "1w", text: "1주일" },
            { value: "1", text: "1개월" },
            { value: "3", text: "3개월" },
            { value: "6", text: "6개월" },
            { value: "1y", text: "1년" }
        ];

        $.each(options, function(index, option) {
            $(selectId).append($('<option>', {
                value: option.value,
                text: option.text
            }));
        });
    },
	// 상세검색 기간 범위 설정 이벤트
	dateRange(selectedDt, startInput, endInput){
        var startDate = new Date();
        var endDate = new Date();

        if(selectedDt === '1w') {
            startDate.setDate(startDate.getDate() - 7);
        } else if (selectedDt === '1y') {
            startDate.setFullYear(startDate.getFullYear() - 1);
        } else if(selectedDt === ''){
            startDate = null;
            endDate = null;
        } else {
            startDate.setMonth(startDate.getMonth() - selectedDt);
        }
        if (startDate && endDate) {
            var formattedStartDate = startDate.toISOString().split('T')[0];
            var formattedEndDate = endDate.toISOString().split('T')[0];
            $(startInput).val(formattedStartDate);
            $(endInput).val(formattedEndDate);
        } else {
            $(startInput).val('');
            $(endInput).val('');
        }
    },
	// 상세검색 유효성 검사
	validateForm() {
        var docStatus = $('#selectedStatus').val();
        var formNo = $('#selectedForm').val();
		var title = $('input[name="apDocTitle"]').val();
		var doctxt = $('input[name="apDocTxt"]').val();
		var strdt = $('input[name="startAppDt"]').val();
		var enddt = $('input[name="endAppDt"]').val();
		var strcdt = $('input[name="startCmpltDt"]').val();
		var endcdt = $('input[name="endCmpltDt"]').val();
		// if(!title && !doctxt && !strdt && !enddt && !strcdt && !endcdt) {
		if(!docStatus && !formNo && !title && !doctxt && !strdt && !enddt && !strcdt && !endcdt) {
			gaiaPortal.customAlert('검색어를 입력해주세요');
			return false;
		}
        return true;
    },
	// 상세검색 최소 날짜 지정
	setMinDate(startDtId, endDtId){
        var startDate = new Date($(`#${startDtId}`).val());
        startDate.setDate(startDate.getDate() + 1);
        var minEndDate = startDate.toISOString().split('T')[0];
        $(`#${endDtId}`).attr('min', minEndDate);
    },
	searchInputReset(){
		$("#searchForm")[0].reset();
	},
	// 그리드
	grid: {
		obj: null,
		init(gridConfig){
			if(this.obj) {
				return;
			}
			this.obj = $('#list_grid').setGrid(gridConfig);
			if (this.obj.settings.rowHeaders[0].type === 'checkbox') {
			// if (methods && typeof methods.checkList === 'function' && typeof methods !== 'undefined') {
				this.obj.grid.on('check', methods.checkList.bind(this));
				this.obj.grid.on('uncheck', methods.checkList.bind(this));
				this.obj.grid.on('checkAll', methods.checkList.bind(this));
				this.obj.grid.on('uncheckAll', methods.checkList.bind(this));
			}
		},
	},
	changePerPage(){
        const size = $('select[id="items-per-page"]').val();
		const param = {
			size: size,
			data: docStatus
		}
		page.grid.obj.methods.itemPerPageChange(param);
    },
};

$(document).ready(function () {
    $('.lay_pop').on('click', function (e) {
        e.stopPropagation(); 
    });

    $(document).on('click', function () {
        $('.lay_pop').removeClass('on'); 
    });

    $('#openDetail').on('click', function (e) {
        e.stopPropagation(); 
        $('.lay_pop').addClass('on'); 
    });

	$('#closeDetail').on('click', function () {
        $('.lay_pop').removeClass('on'); 
    });
});

// var approvalCommon = {
// 	init(gridConfig) {
// 		gaiaCommon.setCommonCode("f8a92327-c9ae-4d1e-aa95-76f15c2a9e61");
// 		page.init(gridConfig);
// 	},
// };