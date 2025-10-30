var __PcesUtil = function(){
	var getCodeListOf = function(colNm){
		if(colNm == 'eval_pstats_cd'){
			return uu.makeMapArray([
				['code', 'name'],
				['0', '등록전'],
				['1', '등록중'],
				//['2', '심사의뢰'],// 이제 사용 안함
				['3', '심사중'],
				['9', '심사완료']
			]);
		}else if(colNm == 'sucsfbid_mthd_dtl_cd'){
			return uu.makeMapArray([
				['code', 'name'],
				['0', '적격공사'],
				['5', '일반공사'],
				['6', '고난이도공사'],
				['13', '간이형종심제']
			]);
		}else if(colNm == 'violt_cd'){
			return uu.makeMapArray([
				['code', 'name'],
				//['0', '정상'],
				['1', '무효입찰'],
				['2', '예정가격위반'],
				//3
				['4', '음(-)의입찰'],
				['5', '지정금액비율위반'],//7번도 있던데 지금 사용안함
				['6', '표준시장단가위반'],
				//9 물량사유서 미제출 인듯
				['10', '직접노무비위반'],
				['11', '설계단가위반'],//설계대비100분의 50미만이거나 100분의 150초과
				['13', '부계약자비율위반']
			]);
		}else if(colNm == 'cst_bill_lct_cd'){
			return uu.makeMapArray([
				['code', 'name'],
				['1', '순공사원가/재'],
				['2', '순공사원가/노'],
				['3', '순공사원가/경'],
				['4', '순공사원가이후'],
				// ['5', 'PS공종/제요율적용제외공종'],
				['6', '총합계이후']
			]);
		}else if(colNm == 'expnss_kind_cd'){//SGC
			return uu.makeMapArray([
				['code', 'name'],
				['A', '순공사비계'],
				['B', '간접노무비'],
				['C', '경비등합계'],
				['D', '일반관리비'],
				['E', '법정경비'],
				['F', '이윤'],
				['G', '단가등지정항목(PS)'],
				['H', '부가가치세'],
				['J', '매입부가가치세']
			]);
		}else if(colNm == 'expnss_calcfrmla_cd'){//CAL
			return uu.makeMapArray([
				['code', 'name'],
				['1', '노무비의'],
				['2', '직접노무비의'],
				['3', '산업안전보건관리비 계상기준'],
				['4', '직접공사비의'],
				['5', '총합계의'],
				['6', '재료비의 '],
				['7', '고정금액'],
				['8', '재료비 + 노무비의'],
				['9', '순공사원가의'],
				['11', '공사이행보증서발급수수료 계상기준']
			]);
		}else if(colNm == 'cnstty_dtls_div_cd'){
			return uu.makeMapArray([
				['code', 'name'],
				['G', '공종'],
				['S', '내역']
			]);
		}else if(colNm == 'blnce_prce_incl_div_cd'){
			return uu.makeMapArray([
				['code', 'name'],
				['0', '균형포함'],
				['1', '상위제외'],
				['2', '하위제외'],
				['3', '동가제외'],// - 동가제외 기관만 해당
				['4', '설계단가 65%, 110% 제외'],// - 한국도로공사만 해당
				['5', '예가88%초과제외']
			]);
		}else if(colNm == 'co_corp_var.var_cd'){
			return uu.makeMapArray([
				['code', 'name'],
				['0001', '단위공사엑셀등록YN'],
				['0002', '비드등록옵션YN']
			]);
		}else if(colNm == 'co_syst_log.log_ty_cd'){
			return uu.makeMapArray([
				['code', 'name'],
				['0001', '에러']
			]);
		}
	};

	var setJudgeConstInfo = function(form, judgeConstInfo){
		if(form == null)form = $('.el_judgeConstInfo:first')[0];

		window._judgeParam = {
			cnstwk_mng_no: judgeConstInfo.cnstwk_mng_no
		};

		if(form && judgeConstInfo){
			var judgeConstInfo = $.extend({}, judgeConstInfo);
			judgeConstInfo.eval_pstats_cd_nm = uu.getCodeName(getCodeListOf('eval_pstats_cd'), String(judgeConstInfo.eval_pstats_cd));
			$(form).css('visibility', 'visible');
			uu.setFormData(form, $.extend({}, judgeConstInfo));
		}
	};

	var gridDownloadExcel = function(grid, title, judgeConstInfo, compInfo, s){
		var beforeHeaderRows = [];
		if(judgeConstInfo){
			beforeHeaderRows.push(['['+uu.getCodeName(getCodeListOf('sucsfbid_mthd_dtl_cd'), judgeConstInfo.sucsfbid_mthd_dtl_cd)+']'+judgeConstInfo.cnstwk_nm]);
		}
		if(compInfo){
			beforeHeaderRows.push([compInfo.corp_nm]);
		}
		grid.downloadExcel($.extend({
			downloadName: title,
			cellFontSize: 10,
			title: title,
			beforeHeaderRows: beforeHeaderRows,
			is_check_only_y: true}, s));
	};

	var setEmConstInfo = function(form, info1){
		var info1 = $.extend({}, info1);
		info1.sucsfbid_mthd_dtl_cd_nm = uu.getCodeName(getCodeListOf('sucsfbid_mthd_dtl_cd'), String(info1.sucsfbid_mthd_dtl_cd));
		uu.setFormData(form, info1);
	};

	var get내역구분명 = function(codeList_KND, it){
		var arr1 = [];
		if(it.cnstty_dtls_div_cd == 'G'){
			arr1.push('공종');
		}if(it.cnstty_dtls_div_cd == 'S'){
			if(it.dcnstty_amt_ty_cd == '0' && it.std_mrkt_uprc_cd){
				arr1.push('(표준시장단가)');
			}else if(it.dcnstty_amt_ty_cd){
				arr1.push(uu.getCodeName(codeList_KND, it.dcnstty_amt_ty_cd));
			}
		}
		return arr1.join(', ');
	};
	var openDialogEmConst = function(){
		uu.loadDialog({
			// 부모의 파라미터를 쓰기 때문에 필요없다.
			action: '/html/pces/empty/empty0020_part_emConstInfoForm.html',
			title: '공사정보',
			width: 600
		});
	};
	var openDialogBidMake = function(emParam){
		emParam = emParam || {cnstwk_mng_no: uu.getSearchParam('cnstwk_mng_no')};
		uu.loadPage({
			action: '/html/pces/empty/empty0020_part_makeBidFileForm.html',
			callback: function(s){
				s.div_app_methods.setAppData(emParam);
				uu.openDialog(s.div_app, {//loadDialog 불가
					title: 'BID파일생성',
					width: 900,
					isRemoveWhenClose: true
				});
			}
		});
	};
	var openDialogJudgeConst = function(){
		uu.loadDialog({
			// 부모의 파라미터를 쓰기 때문에 필요없다.
			action: '/html/pces/judge/judge0010_part_judgeConstInfoForm.html',
			title: '공사정보',
			minWidth: 600
		});
	};

	return {
		getCodeListOf: getCodeListOf,
		setJudgeConstInfo: setJudgeConstInfo,
		gridDownloadExcel: gridDownloadExcel,
		setEmConstInfo: setEmConstInfo,
		get내역구분명: get내역구분명,
		openDialogEmConst: openDialogEmConst,
		openDialogBidMake: openDialogBidMake,
		openDialogJudgeConst: openDialogJudgeConst,
		// 목록에서 사용자id 보내서 사용자명 가져오기
		asyncGetUserIdMapFromList: async function(list1, userIdPropList){
			var users;
			var userIdNmMap = {};
			if(list1.length){
				var userIdList = uu.arrayut.getDistinctPropValueFromList(list1, userIdPropList);
				if(userIdList && userIdList.length){
					//var userIdList = _(list1).map('cnstwk_ofcl_id').uniq().join(',');
					var data = await uu.asyncAjax({
						param: {userIdList: userIdList},
						action: '/api-pces/common/bizcomm/getUserListByIdList.do'
					});
					users = data.list1;
					if(users && users.length){
						userIdNmMap = uu.arrayut.makeListToMap(users, 'usr_id', 'usr_nm');
					}
				}
			}
			return Promise.resolve(userIdNmMap);
		},
		asyncGetCommCodeList: async function(arrClcode){
			return await commUt.asyncGetCommCodeListImpl('/api-pces/pub/common/getCommCodeList.do', arrClcode);
		},
		asyncGetCommCodeListAdmin: async function(arrClcode){
			return await commUt.asyncGetCommCodeListImpl('/api-admin/pub/common/getCommCodeList.do', arrClcode);
		},
		// // 권한
		// makeRghtGridMultiCodeSel: function(s){
		// 	s = s||{};

		// 	var codeList = [
		// 		{code: '0020', name: '공내역'},
		// 		{code: '0030', name: '심사'},
		// 		{code: '0040', name: '기관관리'}
		// 	];
		// 	if(s.isAdmin){
		// 		codeList.push({code: '1010', name: '관리자'});
		// 	}

		// 	s = $.extend({
		// 		codeList: codeList,
		// 		dataAction: 'set_rght_cd_list',
		// 		valColName: 'pces_rght_cd_list',
		// 		dispColName: 'disp_rght_cd_list'
		// 	}, s);
		// 	return new GridMultiCodeSel(s);
		// },

		showProcessLog: function(processKey, callback1){
			var func1 = function(){
				uu.ajax({
					isNoProgress: true,
					param: {processKey: processKey},
					action: '/api-pces/util/getProcessLog.do',
					onSuccess: function(data){
						var info1 = data.info1;
						if(info1.isFileExists){
							setTimeout(func1, 1000);
						}
						callback1(data);
					}
				});
			};
			setTimeout(func1, 3000);// 반드시 3초
		},
		x: 1
	};
};//__PcesUtil
window.pcesUt = __PcesUtil();
