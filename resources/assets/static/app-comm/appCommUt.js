window.SlickDefault = _.extend({}, window.SlickDefault, {
	rowHeight: 27
});

var width_cnstty_nm = 200;//
var width_spec_nm = 150;
var width_unit = 80;
var width_qty = 90;
var width_uprc = 90;
var width_amt = 120;
var width_coname = 150;
var width_constname = 250;
var width_rsce_cd = 150;

var __CommUtil = function(){

	var _commonInfo;
	var _commonCodeMap;

	var asyncGetCommCodeListImpl = async function(actionUrl, arrClcode){
		arrClcode = _.uniq(arrClcode.split(',')).join(',');
		var data = await uu.asyncAjax({
			param: {arrClcode: arrClcode},
			action: actionUrl
		});
		return Promise.resolve(_.groupBy(data.list1, 'clcode'));
	};

	return {
		getCodeListOf(cdGbn){
			if(cdGbn == 'policy_doc_div_cd'){
				return uu.makeMapArray([
					['code', 'name'],
					['0001', '이용약관'],
					['0002', '개인정보처리방침']
				]);
			}else if(cdGbn == 'bsns_div_cd'){
				return uu.makeMapArray([
					['code', 'name'],
					['1000', 'PCES']
				]);
			}else if(cdGbn == 'bsns_auth'){
				return uu.makeMapArray([
					['code', 'name'],
					['1020', 'PCES-공내역'],
					['1030', 'PCES-심사'],
					['1040', 'PCES-기관관리']
					// {code: '0020', name: '공내역'},
					// {code: '0030', name: '심사'},
					// {code: '0040', name: '기관관리'}
				]);
			}else if(cdGbn == 'signup_appl_stats_cd'){//회원가입 신청 상태
				return uu.makeMapArray([
					['code', 'name'],
					['0001', '신청'],
					['0002', '승인'],
					['0003', '반려']
				]);
				//
			}else if(cdGbn == 'cntrct_div_cd'){
				return uu.makeMapArray([
					['code', 'name'],
					['0001', 'PCES'],
					['0002', 'GAIA']
				]);
			}else if(cdGbn == 'dtil_cntrct_div_cd'){
				return uu.makeMapArray([
					['code', 'name'],
					['P001', '종심'],
					['P002', '간이종심'],
					['P003', '종평'],
					['P004', '간이종평'],
					['G001', '엔터프라이즈'],
					['G002', '프리미엄'],
					['G003', '베이직']
				]);
			}else if(cdGbn == 'cntrct_req_dtil_cd'){
				return uu.makeMapArray([
					['code', 'name'],
					['0001', 'PCES'],
					['0002', 'GAIA']
				]);
			}
		},
		getPwGuideMsg() {
			return _.trim('\
				비밀번호 생성 규칙 적용(숫자, 문자, 특수문자 조합(3가지 모두 조합) 최소 9자리 이상)\n\
				연속된 숫자는 불가합니다.\n\
				동일한 문자는 반복 불가합니다.\n\
				키보드 상에서 나란히 있는 문자열은 불가합니다.(예 : qwer)\n\
				신상정보(이름, 아이디)를 활용한 값은 불가합니다.\n\
			');
		},
		setCommInfo: function(obj){
			_commonInfo = _.extend({}, obj);
			window._csrf = _commonInfo.csrf;
		},
		getCommInfo: function(){
			return _.clone(_commonInfo);
		},
		asyncGetCommCodeListImpl: asyncGetCommCodeListImpl,
		refreshOpener: function(){
			if(window.opener && window.opener._refresh){
				window.opener._refresh();
			}
		},

		doLoginPces(){
			location.href = '/api-pces/auth/doLogin.do?'+$.param({origin: location.origin});
		},
		doLogoutPces(){
			location.href = '/api-pces/auth/doLogout.do?'+$.param({origin: location.origin});
			// uu.ajax({
			// 	action: '/api-pces/main/main0010/doLogout.do',
			// 	onSuccess: function(){
			// 		location.href = curl('/');
			// 	}
			// });
		},
		doLogoutAdmin(){
			uu.ajax({
				action: '/api-admin/auth/doLogout.do',
				onSuccess: function(){
					location.href = curl('/');
				}
			});
		},
		doLogoutPortal(){
			uu.ajax({
				action: '/api-portal/auth/doLogout.do',
				onSuccess: function(){
					location.href = curl('/');
				}
			});
		},
		goError: function(err_msg){
			location.href = CONTEXTPATH+'/html/common/error.html?err_msg='+encodeURIComponent(err_msg||'');
			return 'error';
		},
		// makeQuillEditorCustomToolbar(){
		// 	return [
		// 		[
		// 			{header: []},
		// 			{size: []},
		// 			{color: []},
		// 			{background: []},
		// 			{indent: '-1'}, {indent: '+1' },
		// 			'bold', 'italic', 'underline', 'strike',
		// 			{script: 'sub'}, {script: 'super'}
		// 		],
		// 		['link', 'blockquote', 'code', 'code-block', 'image'],
		// 		[{list: 'ordered'}, {list: 'bullet'}, {align: []}]
		// 	];
		// },
		getLoginUser(){
			return commUt.getCommInfo().loginUser;
		},
		hasAuth(bsns_auth){//bsns_div_cd
			var loginUser = commUt.getLoginUser();
			if(loginUser == null)return false;
			var fObj = _.find(loginUser.authList, it => bsns_auth == it.bsns_auth);//bsns_div_cd == it.bsns_div_cd &&
			return fObj != null;
		},
		x: 1
	};
};//__CommUtil
window.commUt = __CommUtil();

// var makeApiPath = function(BASEPATH, action){
// 	return [BASEPATH, action].join('/').replaceAll('//', '/')+'.do';
// };
