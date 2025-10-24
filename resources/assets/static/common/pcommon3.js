// window._load_pcommon3 확인
function doNothing(){};

var PykUtil = function(){
var commsg = {};
var isKo = true; //한국어 체크
/* confirm : 사용자에게 확인을 요청하는 메시지들 */
commsg.confirm = {};
commsg.confirm.search = isKo?'조회 하시겠습니까?':'Search?';
commsg.confirm.save = isKo?'저장 하시겠습니까?':'Save?';
commsg.confirm.applreq = isKo?'승인요청 하시겠습니까?':'Request for approval?';
commsg.confirm.appl = isKo?'승인 하시겠습니까?':'Approve?';
commsg.confirm.rtn = isKo?'재검토처리 하시겠습니까?':'Return?';
commsg.confirm.insert = isKo?'추가 하시겠습니까?':'Insert?';
commsg.confirm.update = isKo?'수정 하시겠습니까?':'Update?';
commsg.confirm.remove = isKo?'삭제 하시겠습니까?':'Delete?';// 일부러 delete 로 안함
commsg.confirm.execute = isKo?'작업을 실행 하시겠습니까?':'Execute?';
commsg.confirm.select = isKo?'선택 하시겠습니까?':'Choose?';
commsg.confirm.register = isKo?'등록 하시겠습니까?':'Register?';
/* alert : 사용자에게 알림을 표시하는 메시지들 */
commsg.alert = {};
commsg.alert.applreqd = isKo?'요청되었습니다.':'Requested';
commsg.alert.executed = isKo?'정상적으로 실행되었습니다.':'Executed';
commsg.alert.searched = isKo?'정상적으로 조회되었습니다.':'Searched.';
commsg.alert.saved = isKo?'정상적으로 저장되었습니다.':'Saved.';
commsg.alert.inserted = isKo?'정상적으로 추가되었습니다.':'Inserted.';
commsg.alert.updated = isKo?'정상적으로 수정되었습니다.':'Updated.';
commsg.alert.deleted = isKo?'정상적으로 삭제되었습니다.':'Deleted.';
commsg.alert.jobExecuted = isKo?'작업이 정상적으로 실행 되었습니다.':'Job Executed.';
commsg.alert.error = isKo?'오류가 발생하였습니다.':'Error.';
commsg.alert.searchError = isKo?'조회 오류가 발생하였습니다.':'Search Error.';
commsg.alert.saveError = isKo?'저장 오류가 발생하였습니다.':'Save Error.';
commsg.alert.insertError = isKo?'추가 오류가 발생하였습니다.':'Insert Error.';
commsg.alert.updateError = isKo?'수정 오류가 발생하였습니다.':'Update Error.';
commsg.alert.deleteError = isKo?'삭제 오류가 발생하였습니다.':'Delete Error.';
commsg.alert.jobError = isKo?'작업 실행 오류가 발생하였습니다.':'Job Error.';
commsg.alert.loginFail = isKo?'로그인에 실패하였습니다.':'Login Fail.';
commsg.alert.noSearchResult = isKo?'조회 결과가 없습니다.':'No Data.';
commsg.alert.notModified = isKo?'변경된 사항이 없습니다.':'No Data Changed.';
commsg.alert.dupError = isKo?'중복 데이터가 존재합니다.':'Duplicated Data.';
commsg.alert.cantDelete0 = isKo?'삭제 안된 하위 로우가 있어 삭제할 수 없습니다.':'Cant Delete.';
commsg.alert.cantInsert0 = isKo?'삭제된 상위 로우가 있어 추가할 수 없습니다.':'Cant Insert';
commsg.alert.listDataModified = isKo?'변경된 데이터가 있습니다.':'List Data Changed.';
commsg.alert.executeAfterSaving = isKo?'저장 후 실행하십시오.':'Execute After Saving.';
commsg.alert.chooseItem = isKo?'항목을 선택해주세요.':'Choose Item.';
commsg.alert.noSaveData = isKo?'저장할 데이터가 없습니다.':'No Save Data.';
commsg.alert.noData = isKo?'데이터가 없습니다.':'No Data.';
commsg.alert.registered = isKo?'등록 되었습니다.':'Registered';
commsg.alert.noSelItems = isKo?'선택된 항목이 없습니다.':'No Selected Items.';
commsg.alert.noSelectedItem = isKo?'선택된 항목이 없습니다.':'No selected item.';
/**
 * 숫자의 소수점 자릿수를 계산하는 함수
 * 
 * @param {number} n - 소수점 자릿수를 계산할 숫자
 * @returns {number} - 숫자의 소수점 자릿수
 */
var getScale = function(n){
	var str1 = Big(n).toString();
	if(str1.indexOf('.') < 0){
		return 0;
	}else{
		return str1.split('.')[1].length;
	}
};
/**
 * 객체 obj1이 객체 obj2의 모든 속성을 포함하고 있는지 확인하는 함수
 * 
 * @param {Object} obj1 - 비교할 첫 번째 객체
 * @param {Object} obj2 - 비교할 두 번째 객체
 * @returns {boolean} - obj1이 obj2의 모든 속성을 포함하면 true, 그렇지 않으면 false
 */
var objectContains = function(obj1, obj2){
	for(var k in obj2){
		if(obj2.hasOwnProperty(k)){
			if(obj2[k] != obj1[k])return false;
		}
	}
	return true;
};

/* 화면 전체 커버 + 프로그래스 바 표시 */
var progressCover = (function(){
	var makePrgrCoverHtml = function(s){
		//<i class="spinner-border text-dark"></i> 이거 쓰면 여러번 날릴때 이상하게 보인다.
		// HTML 코드 생성
		return ('\
			<div class="uu-overlay">\
				<button class="uu-btn-naked uu-overlay__loader" ></button>\
				<div class="uu-overlay__time"><div class="uu-overlay__timetext"></div></div>\
				<div class="uu-overlay__box" >\
					<div><div class="uu-overlay__info" ></div></div>\
					<div class="progress" style="height: auto;background-color:#e9ecef;"><div class="progress-bar" style="height: 0;background-color: #ffc107;"></div></div>\
				</div>\
			</div>\
		');
	};
	var count = 0; // 프로그레스 커버 표시 횟수
	var timeShowTimeoutId; // 경과 시간 표시 타이머 ID
	// var plainOverlay = new PlainOverlay({
	// 	face: $(makePrgrCoverHtml())[0]
	// });
	var _block = function(s){
		// 화면 가리기와 프로그레스 커버 표시
		$.blockUI({
			forceIframe: false,
			// fadeIn: 0,
			fadeOut: 0,
			css: {
				border: 'none',
				width: '32px',
				top: '45%',
				left: '45%',
				//padding: '15px',
				backgroundColor: 'transparent'
			},
			message: makePrgrCoverHtml(s),
			overlayCSS: {
				backgroundColor: 'transparent',
				opacity: 0.2,
				//cursor: 'default',
				zIndex: 99999
			},
			baseZ: 99998,
			fadeIn: 0
		});
		// plainOverlay.show();
	};
	var _unblock = function(){
		// 프로그레스 커버 숨기기
		$.unblockUI({fadeOut: 200});
		// plainOverlay.hide();
	};
	return function(isShow, s){
		s = s || {};
		if(isShow == 'count') return count;// 프로그레스 커버 표시 횟수 반환
		if(isShow){
			if(count == 0){
				_block(s);
			}
			if(! s.noLoseFocus)_loseCurrentFocus();// 현재 포커스 잃기
			count++; // 프로그레스 커버 표시 횟수 증가

			// 경과시간(초) 표시
			var st_time = new Date().getTime();
			if(timeShowTimeoutId){
				clearInterval(timeShowTimeoutId);
				timeShowTimeoutId = null;
			}
			timeShowTimeoutId = setInterval(function(){
				var text1 = ''+formatNumber((new Date().getTime() - st_time)/1000, 2)+' sec';
				$(PRGR_TIME_TEXT_SEL).text(text1);
			}, 500);
		}else{
			// 경과시간 보여주기 표시 중지
			if(timeShowTimeoutId){
				clearInterval(timeShowTimeoutId);
				timeShowTimeoutId = null;
			}
			count--; // 프로그레스 커버 표시 횟수 감소
			if(count < 0){
				var errorMessage = 'progressCover(): count < 0';
				alert(errorMessage);
				throw errorMessage;
				// count == 0;
			}else if(count == 0){
				_unblock();// 여기서 timeout 쓰면 안됨
			}
		}
	}
})();

/* json 응답 체크 */
function isValidJsonResponse(data){
	if(data.errorCode || data.errorMessage){
		return false;
	}else{
		return true;
	}
}

/* id 또는 name 으로 element 찾기 */
function $ge(parent, e, noCheck){
	parent = parent || document;
	var $e = $(e, parent).eq(0);
	if($e.length) return $e;
	var $e = $('#'+e, parent).eq(0);
	if($e.length) return $e;
	var $e = $('[name='+e+']', parent).eq(0);
	if($e.length) return $e;
	if(! noCheck){
		var message = 'ge() Error: '+ e;
		alert(message);
		throw message;
	}
}
/* tag(node, element) 객체 가져오기 */
function ge(parent, e, noCheck){
	return $ge(parent, e, noCheck)[0];
}

// function hasElem(parent, e){
// 	parent = parent || document;
// 	var $e = $(e, parent).eq(0);
// 	if($e.length){return true}
// 	var $e = $('#'+e, parent).eq(0);
// 	if($e.length){return true}
// 	var $e = $('[name='+e+']', parent).eq(0);
// 	if($e.length){return true}
// 	else{return false;}
// }

// 폼 체크, noAlert true 이면 alert 창 없이 true, false 만
/* form 요소 */
function checkForm(form, noAlert, op){
	var form = ge(null, form);
	var check = true;
	var elements = $($.makeArray(form.elements)).filter(function(idx){
		var node = this;
		return node.name && (node.checked || rselectTextarea.test(node.nodeName) || rinput.test(node.type));
	});
	var i, len, el;
	for(i = 0, len = elements.length; i < len; i+=1){
		el = elements[i];
		check = checkField(form, el, null, noAlert, op);
		if(! check)break;
	}
	return check;
}

//
function _getDispName($el){
	var dispname =
		$el.data('dispname') ||
		$el.attr('placeholder') || $el.attr('title') ||
		// $el.parent().find('.field_label').text() ||
		$('[for='+$el.attr('id')+']').text() ||
		$('[for='+$el.attr('name')+']').text() ||
		$el.attr('name') ||
		$el.attr('id');
	return dispname;
}

// required|format=number|scale=2|min=3|max=1000|minsize=3|validator=isFormatNumber
function checkFieldValue(val, rules, ops){
	var message;
	var dispname = ops.dispname;
	if(rules.required && ! $.trim(val)){
		message = isKo?(dispname + ' 은(는) 필수 항목입니다.'):(dispname + ' is required.');
		return {valid: false, message: message};
	}
	if(! val) return {valid: true};
	if(rules.maxsize != null && val.length > rules.maxsize){
		message = isKo?
			(dispname + ' 을(를) '+rules.maxsize+' 자 이하로 입력해주세요.(현재 '+val.length+' 자)'):
			('['+dispname+'] Please enter '+rules.maxsize+' characters or less. Current is '+val.length+' characters');
		return {valid: false, message: message};
	}
	if(rules.minsize != null && val.length < rules.minsize){
		message = isKo?
			(dispname + ' 을(를) '+rules.minsize+' 자 이상으로 입력해주세요.(현재 '+val.length+' 자)'):
			('['+dispname+'] Please enter more than '+rules.minsize+' characters. Current is '+val.length+' characters');
		return {valid: false, message: message};
	}
	if(rules.exactsize != null && val.length != rules.exactsize){
		message = (dispname + ' 을(를) '+rules.exactsize+' 자 로 입력해주세요.(현재 '+val.length+' 자)');
		return {valid: false, message: message};
	}
	if(rules.nowhitechar && val.match(/\s/)){
		message = isKo?
			(dispname + ' 에는 공백 문자가 없어야 합니다.'):
			('['+dispname+'] There should be no whitespace characters.');
		return {valid: false, message: message};
	}
	if(rules.nolrwhite && val != _.trim(val)){
		message = (dispname + ' 을(를) 좌우 공백 없이 입력해주세요.');
		return {valid: false, message: message};
	}
	// if(rules.rexpRule && ! rules.rexpRule.rexp.test(val)){
	// 	message = rules.rexpRule.msg ? (rules.reRule.msg+' ['+dispname+']') : (dispname + ' 을(를) 형식에 맞게 입력해주세요.');
	// 	return {valid: false, message: message};
	// }
	if(rules.rexp && ! rules.rexp.test(val)){
		message = rules.rexpMsg ? (rules.rexpMsg+' ['+dispname+']') : (dispname + ' 을(를) 형식에 맞게 입력해주세요.');
		return {valid: false, message: message};
	}

	var formatOp = _formatOption[rules.format];
	if(formatOp){
		if(! formatOp.formatCheckFunc(val)){
			var comment = formatOp.formatCheckComment;
			message =
				(isKo ? (dispname + ' 을(를) 형식에 맞게 입력해주세요. ') : ('['+dispname+'] Please enter the items to suit the type.')) + (comment ? '(' + comment + ')' : '');
			return {valid: false, message: message};
		}
	}

	var max = isNullOrEmptyString(rules.max) ? null : rules.format == 'number' ? Number(rules.max) : String(rules.max);
	var min = isNullOrEmptyString(rules.min) ? null : rules.format == 'number' ? Number(rules.min) : String(rules.min);
	var lt =  isNullOrEmptyString(rules.lt ) ? null : rules.format == 'number' ? Number(rules.lt) : String(rules.lt);
	var gt =  isNullOrEmptyString(rules.gt ) ? null : rules.format == 'number' ? Number(rules.gt) : String(rules.gt);
	if( max != null || min != null || lt != null || gt != null){
		var unformatVal = formatOp?.unformatFunc ? formatOp.unformatFunc(val) : val;
		var cmpVal = rules.format == 'number' ? Number(unformatVal) : unformatVal;

		if(max != null && cmpVal > max){
			var str_max = rules.format == 'number' ? formatNumber(max) : max;
			message = isKo?
				(dispname + ' 을(를) '+str_max+' 이하로 입력해주세요.'):
				('['+dispname+'] Please enter less than '+str_max+'.');
			return {valid: false, message: message};
		}
		if(lt != null && cmpVal >= lt){
			var str_lt = rules.format == 'number' ? formatNumber(lt) : lt;
			message = isKo?
				(dispname + ' 을(를) '+str_lt+' 보다 작게 입력해주세요.'):
				('['+dispname+'] Please enter less than '+str_lt+'.');
			return {valid: false, message: message};
		}
		if(min != null && cmpVal < min){
			var str_min = rules.format == 'number' ? formatNumber(min) : min;
			message = isKo?
				(dispname + ' 을(를) '+str_min+' 이상으로 입력해주세요.'):
				('['+dispname+'] Please enter more than '+str_min+'.');
			return {valid: false, message: message};
		}
		if(gt != null && cmpVal <= gt){
			var str_gt = rules.format == 'number' ? formatNumber(gt) : gt;
			message = isKo?
				(dispname + ' 을(를) '+str_gt+' 보다 크게 입력해주세요.'):
				('['+dispname+'] Please enter more than '+str_gt+'.');
			return {valid: false, message: message};
		}
	}
	// 2015-07-07 추가
	var validatefunc = rules.validatefunc;
	if(validatefunc){
		var validResult = validatefunc(val);
		if(! validResult.valid){
			message =
				(isKo ? (dispname + ' 을(를) 바르게 입력해주세요. ') : ('['+dispname+'] Please enter the items to right value.')) + (validResult.message ? '(' + validResult.message + ')' : '');
			return {valid: false, message: message};
		}
	}
	return {valid: true};
}
// 폼 필드 체크, noAlert true 이면 alert 없이 체크만
function checkField(parent, el, customRules, noAlert, op){
	function process_error(message){
		if(op){
			op.el = el;
			op.message = message;
		}
		if(! noAlert){
			showAlert(null, message, selectFocus.bind(null, parent, el), 4000);
		}
	}
	var el = ge(parent, el), $el = $(el), val = $el.val();
	var edata = getSetFieldData($el);
	var dataRules = parseStrToObj($el.attr('data-rules'));
	var rules = $.extend({required: $el.hasClass('required')}, edata, dataRules, customRules);
	var dispname = _getDispName($el), message;
	var checkResult = checkFieldValue(val, rules, {dispname: dispname});
	if(checkResult.valid)return true;
	process_error(checkResult.message);
	return false;
}

function _getFieldFormatFunc(e){
	return _tryThese(function(){
		var $e = $(e);
		var edata = getSetFieldData($e);
		if(edata.format == 'nummask'){
			return function(str){return formatNumMask(str, $e.data('mask'));}
		}else if(edata.format == 'number' && edata.scale != null){
			var op = null;
			if(edata.rstz_yn == 'Y'){
				op = {rstz_yn: edata.rstz_yn};
			}
			return function(str){return formatNumber(str, edata.scale, op);}
		}else{
			return _formatOption[edata.format].formatFunc;
		}
	});
}
function _getFieldUnformatFunc(e){
	return _tryThese(function(){
		return _formatOption[getSetFieldData(e).format].unformatFunc;
	});
}
// function _getFieldFormatCheckFunc(e){
// 	return _tryThese(function(){
// 		return _formatOption[getSetFieldData(e).format].formatCheckFunc;
// 	});
// }
// function _getFieldFormatCheckComment(e){
// 	return _tryThese(function(){
// 		return _formatOption[getSetFieldData(e).format].formatCheckComment;
// 	});
// }
// 고정길이(주민번호 등) 포맷제거
function _unformatFixedNumber(str){
	return str.replace(RegExpMap.nonumreplace, '');
}
// 숫자 포맷제거
function _unformatNumber(str){
	return str.replace(RegExpMap.commareplace, '');
}
// unformat 안함 그대로 리턴.
function _unformatNone(str){
	return str;
}

var formatDate = function(dt1, fmt){
	return fmt.
		replace(/yyyy/g, dt1.getFullYear()).
		replace(/MM/g, _.padStart(dt1.getMonth()+1, 2, '0')).
		replace(/dd/g, _.padStart(dt1.getDate(), 2, '0')).
		replace(/hh/g, _.padStart(dt1.getHours(), 2, '0')).
		replace(/mm/g, _.padStart(dt1.getMinutes(), 2, '0')).
		replace(/ss/g, _.padStart(dt1.getSeconds(), 2, '0')).
		replace(/msc/g, _.padStart(dt1.getMilliseconds(), 3, '0'));
};

// 각 포맷별로 check, format, unformat 함수 설정
var _formatOption = {
	// 년도
	year: {
		unformatFunc: _unformatNone,
		formatCheckFunc: isYearFormat,
		formatFunc: formatYear,
		formatCheckComment: 'ex: '+formatDate(new Date(), 'yyyy')
	},
	// 월
	mm: {
		unformatFunc: _unformatNone,
		formatCheckFunc: isMmFormat,
		formatFunc: formatMm,
		formatCheckComment: 'ex: '+formatDate(new Date(), 'MM')
	},
	// 년월
	ym: {
		unformatFunc: _unformatFixedNumber,
		formatCheckFunc: isYmFormat,
		formatFunc: formatYm,
		formatCheckComment: 'ex: '+formatDate(new Date(), 'yyyy-MM')
	},
	// 년월일
	ymd: {
		unformatFunc: _unformatFixedNumber,
		formatCheckFunc: isYmdFormat,
		formatFunc: formatYmd,
		formatCheckComment: 'ex: '+formatDate(new Date(), 'yyyy-MM-dd')
	},
	// 시분
	hm: {
		unformatFunc: _unformatFixedNumber,
		formatCheckFunc: isHmFormat,
		formatFunc: function(str){return formatNumMask(str, '##:##');},
		formatCheckComment: 'ex: '+formatDate(new Date(), 'hh:mm')
	},
	// 시분
	hms: {
		unformatFunc: _unformatFixedNumber,
		formatCheckFunc: isHmsFormat,
		formatFunc: function(str){return formatNumMask(str, '##:##:##');},
		formatCheckComment: 'ex: '+formatDate(new Date(), 'hh:mm:ss')
	},
	// 시
	hh: {
		unformatFunc: _unformatFixedNumber,
		formatCheckFunc: isHhFormat,
		formatFunc: function(str){ return formatNumMask(str, '##'); },
		formatCheckComment: 'ex: 00 ~ 23 사이의 값'
	},
	// 시분
	ymdhm: {
		unformatFunc: _unformatFixedNumber,
		formatCheckFunc: isYmdhmFormat,
		formatFunc: formatYmdhm,
		formatCheckComment: 'ex: '+formatDate(new Date(), 'yyyy-MM-dd hh:mm')
	},
	// 시분
	ymdhms: {
		unformatFunc: _unformatFixedNumber,
		formatCheckFunc: isYmdhmsFormat,
		formatFunc: formatYmdhms,
		formatCheckComment: 'ex: '+formatDate(new Date(), 'yyyy-MM-dd hh:mm:ss')
	},
	// 숫자
	number: {
		unformatFunc: _unformatNumber,
		formatCheckFunc: isNumberFormat,
		formatFunc: formatNumber,
		formatCheckComment: 'ex: 3,456'
	},
	// digit
	digiit: {
		unformatFunc: _unformatNone,
		formatCheckFunc: isDigitFormat,
		formatFunc: formatDigit,
		formatCheckComment: ''
	},
	// bigalpha
	bigalpha: {
		unformatFunc: _unformatNone,
		formatCheckFunc: function(text){return text.match(RegExpMap.bigalpha);},
		formatFunc: function(text){return text.replace(RegExpMap.notbigalphareplace, '');},
		formatCheckComment: ''
	},
	// bigalphanum
	bigalphanum: {
		unformatFunc: _unformatNone,
		formatCheckFunc: function(text){return text.match(RegExpMap.bigalphanum);},
		formatFunc: function(text){return text.replace(RegExpMap.notbigalphanumreplace, '');},
		formatCheckComment: ''
	},
	// alphanum
	alphanum: {
		unformatFunc: _unformatNone,
		formatCheckFunc: function(text){return text.match(RegExpMap.alphanum);},
		formatFunc: function(text){return text.replace(RegExpMap.notalphanumreplace, '');},
		formatCheckComment: ''
	},
	// 주민번호
	ssn: {
		unformatFunc: _unformatFixedNumber,
		formatCheckFunc: isSsnFormat,
		formatFunc: formatSsn,
		formatCheckComment: ''
	},
	// 사업자번호
	bizno: {
		unformatFunc: _unformatFixedNumber,
		formatCheckFunc: isBizNoFormat,
		formatFunc: formatBizNo,
		formatCheckComment: ''
	},
	// // 우편변호
	// postno: {
	// 	unformatFunc: _unformatFixedNumber,
	// 	formatCheckFunc: isPostNoFormat,
	// 	formatFunc: formatPostNo,
	// 	formatCheckComment: ''
	// },
	// 전화번호
	phone: {
		unformatFunc: _unformatNone,
		formatCheckFunc: isPhoneNoFormat,
		formatCheckComment: '- 기호와 숫자 000-0000-0000'
	},
	// 핸드폰
	cellphone: {
		unformatFunc: _unformatNone,
		formatCheckFunc: isCellPhoneFormat,
		formatCheckComment: '- 기호와 숫자 000-0000-0000'
	},
	// 이메일
	email: {
		unformatFunc: _unformatNone,
		formatCheckFunc: isEmailFormat,
		formatCheckComment: ''
	}
};

// 객체가 비었으면 true
// function isEmpty(o){
// 	return o == null || o.length == 0 || (typeof(o) == 'object' && Object.keys(o).length == 0);
// }
// var isEmpty = _.isEmpty;

// 객체가 비어있지 않으면 true
// function isNotEmpty(o){
// 	return ! isEmpty(o);
// }
var nullToEmpty = function(o){
	return o != null ? o : '';
};

// // null 이면 true
// function isNull(a){
// 	return (a == null || a === '');
// }

// // null 아니며 false
// function isNotNull(a){
// 	return ! (a == null || a === '');
// }

function isNullOrEmptyString(o){
	return o == null || o === '';
}

function ifnull(a, b = ''){
	if(a != null)return a;
	return b;
}
function ifempty(a, b = ''){
	if(! isNullOrEmptyString(a))return a;
	return b;
}

// // a 가 null 이면 b
// function nvl(a, b){
// 	for(var n = 0, len = arguments.length; n < len; n++){
// 		if(isNotNull(arguments[n])) return arguments[n];
// 	}
// 	return '';
// }

// // formList 모두 체크
// function checkFormList(formList){
// 	for(var i = 0, len = formList.length; i < len; i++){
// 		if(! checkForm(formList[i])) return false;
// 	}
// 	return true;
// }

// 휴대전화 번호 포맷 체크
function isCellPhoneFormat(str){
	// var test1 = RegExpMap.cellphone1.test(str);
	// if(test1) return true;
	var test2 = RegExpMap.cellphone2.test(str);
	return test2;
}
// 자주 사용되는 정규식
var RegExpMap = {
	year: /^\d{4}$/,
	mm: /^\d{2}$/,
	ym: /^(\d{4})-(\d{2})$/,
	ymd: /^(\d{4})-(\d{2})-(\d{2})$/,
	hm: /^(\d{2}):(\d{2})$/,
	hms: /^(\d{2}):(\d{2}):(\d{2})$/,
	ymdhm: /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2})$/,
	ymdhms: /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
	number: /(^[+-]?\d+)(\d{3})/,
	bigalpha: /^[A-Z]+$/,
	bigalphanum: /^[A-Z0-9]+$/,
	alphanum: /^[a-zA-Z0-9]+$/,
	digit: /^\d+$/,
	phone: /^\d{2,4}-?\d{3,4}-?\d{4}$/,
	juminno: /^(\d{6})-(\d{7})$/,
	comma: /,/g,
	notnumreplace: /\D/g,
	notnumberreplace: /[^-+.\d]/g,
	number2: /\d(?=(\d{3})+$)/g,
	notbigalphareplace: /[^A-Z]/g,
	bizno: /^(\d{3})-(\d{2})-(\d{5})$/,
	postno: /^(\d{3})-(\d{3})$/,
	numbertest: /(\d+)(\d{3})/,
	htmlamp: /&/g,
	htmllt: /</g,
	htmlgt: />/g,
	htmlspace: / /g,
	numbermatch: /^[-+\d.]*$/,
	numtest: /^\d*$/,
	nonumreplace: /[^\d]/g,
	commareplace: /,/g,
	notbigalphanumreplace: /[^A-Z0-9]/g,
	notalphanumreplace: /[^a-zA-Z0-9]/g,
	//
	// cellphone1: /^01[016789]\d{7,8}$/,
	cellphone2: /^01[016789]-?\d{3,4}-?\d{4}$/,
	email: /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i,
	//
	space: / /g,
	newline: /(\r\n|\n|\r)/g,
	whitespace: /\s/g,
	//
	underNumAlphaHan: /^[_0-9a-zA-Zㄱ-힣]+$/g,
	hangul: /[ㄱ-힣]/,
	//
	x: 1
};

//형식화된 숫자 문자열을 포매팅
function formatNumMask(str, format){
	str = (str == null)?'':str;
	var rv = '',
		numcount = countChr(format, '#');
	str = str.replace(RegExpMap.notnumreplace, '').substr(0, numcount);
	var formatCharAt,
		strCharIndex = 0,
		strLength = str.length;
	for(var n = 0, nlen = format.length; n < nlen; n++){
		formatCharAt = format.charAt(n);
		rv += (formatCharAt == '#') ? str.charAt(strCharIndex++) : formatCharAt;
		if(strCharIndex >= strLength) break;
	}
	return rv;
}
//문자열에서 chr 포함 갯수
function countChr(str, chr){
	var count = 0;
	for(var n = 0, nlen = str.length; n < nlen; n++){
		if(chr == str.charAt(n)) count++;
	}
	return count;
}
// 년도 포맷 체크
function isYearFormat(str){
	if(! RegExpMap.year.test(str)) return false;
	return true;
}
// 년도 포맷
function formatYear(str){
	str = makeDateStrIfNumber(str, 4);
	return formatNumMask(str, '####');
}
// 년월 포맷 체크
function isYmFormat(str){
	if(! RegExpMap.ym.test(str)) return false;
	var year = Number(RegExp.$1);
	var month = Number(RegExp.$2);
	return month >= 1 && month <= 12;
}
// 년월 포맷
function formatYm(str){
	str = makeDateStrIfNumber(str, 6);
	return formatNumMask(str, '####-##');
}
// 월 포맷 체크
function isMmFormat(str){
	if(! RegExpMap.mm.test(str)) return false;
	var month = Number(str);
	return month >= 1 && month <= 12;
}
// 년월 포맷
function formatMm(str){
	return formatNumMask(str, '##');
}
// 일자 포맷 체크
function isYmdFormat(str){
	if(! RegExpMap.ymd.test(str)) return false;
	var year = Number(RegExp.$1);
	var month = Number(RegExp.$2);
	var date = Number(RegExp.$3);
	var dt = new Date(year, month-1, date);
	return dt.getFullYear() == year && dt.getMonth() == month-1 && date == dt.getDate();
}
// 일자 포맷
function formatYmd(str){
	str = makeDateStrIfNumber(str, 8);
	return formatNumMask(str, '####-##-##');
}
// 일시 포맷
function formatYmdhm(str){
	str = makeDateStrIfNumber(str, 12);
	return formatNumMask(str, '####-##-## ##:##');
}
// 일시 포맷
function formatYmdhms(str){
	str = makeDateStrIfNumber(str, 14);
	return formatNumMask(str, '####-##-## ##:##:##');
}
// hm 포맷 체크
function isHmFormat(str){
	if(! RegExpMap.hm.test(str)) return false;
	var h = Number(RegExp.$1);
	var m = Number(RegExp.$2);
	return (h >= 0 && h <= 23) && (m >= 0 && m <= 59);
}
function isHmsFormat(str){
	if(! RegExpMap.hms.test(str)) return false;
	var h = Number(RegExp.$1);
	var m = Number(RegExp.$2);
	var s = Number(RegExp.$3);
	return (h >= 0 && h <= 23) && (m >= 0 && m <= 59) && (s >= 0 && s <= 59);
}
//hh 포맷 체크
function isHhFormat(str){
	return (str >= 0 && str <= 23);
}
// hm 포맷
function formatHm(str){
	return formatNumMask(str, '##:##');
}
function isYmdhmsFormat(str){
	if(! RegExpMap.ymdhms.test(str)) return false;
	var year = Number(RegExp.$1);
	var month = Number(RegExp.$2);
	var date = Number(RegExp.$3);
	var dt = new Date(year, month-1, date);
	var isYmd = dt.getFullYear() == year && dt.getMonth() == month-1 && date == dt.getDate();
	if(! isYmd)return false;
	var h = Number(RegExp.$4);
	var m = Number(RegExp.$5);
	var s = Number(RegExp.$6);
	return (h >= 0 && h <= 23) && (m >= 0 && m <= 59) && (s >= 0 && s <= 59);
}
function isYmdhmFormat(str){
	if(! RegExpMap.ymdhm.test(str)) return false;
	var year = Number(RegExp.$1);
	var month = Number(RegExp.$2);
	var date = Number(RegExp.$3);
	var dt = new Date(year, month-1, date);
	var isYmd = dt.getFullYear() == year && dt.getMonth() == month-1 && date == dt.getDate();
	if(! isYmd)return false;
	var h = Number(RegExp.$4);
	var m = Number(RegExp.$5);
	return (h >= 0 && h <= 23) && (m >= 0 && m <= 59);
}
// 숫자 포맷 체크
function isNumberFormat(str, scale, min, max){
	//var regex = /^[-+]?\d{0,3}(,\d{3})*(\.\d*)?$/;
	var num = Number(str.replace(RegExpMap.commareplace, ''));
	if(_.isNaN(num)) return false;
	var numStr = String(num);
	if(scale != null){
		var dotIndex = numStr.lastIndexOf('.');
		if(dotIndex >=0){
			if(numStr.substring(dotIndex+1).length > scale){
				return false;
			}
		}
	}
	if(min != null && num < min) return false;
	if(max != null && num > max) return false;
	return true;
}

var _formatNumberByNum = (function(){
	var re1 = /\d(?=(\d{3})+$)/g;
	var re2 = /0+$/g;// 끝의 0 제거용
	return function(num, scale, op){
		var strNum = Big(num).toFixed();
		var dotIndex = strNum.indexOf('.');

		var part1, part2;
		if(dotIndex >= 0){
			part1 = strNum.substring(0, dotIndex);
			part2 = strNum.substring(dotIndex + 1);
		}else{
			part1 = strNum;
			part2 = '';
		}

		var dotPart = '';
		if(scale == null){
			// 끝의 0 제거
			part2 = part2.replace(re2, "");
		}else{
			part2 = _.padEnd(part2.substring(0, scale), scale, '0');
			if(op && op.rstz_yn == 'Y'){
				part2 = part2.replace(re2, "");
			}
		}
		if(part2){
			dotPart = '.'+part2;
		}
		return part1.replace(re1, '$&,') + dotPart;
	}
})();
// 숫자 포맷
function formatNumber(str, scale, op){
	if(str === '' || str == null) return '';
	var num;
	if(typeof(str) == 'number'){
		num = str;
	}else{
		num = Number(str.replace(RegExpMap.comma, ''));
	}
	if(_.isNaN(num))return '';
	return _formatNumberByNum(num, scale, op);
}
// digit 포맷 체크
function isDigitFormat(str){
	return RegExpMap.digit.test(str);
}
// digit 포맷
function formatDigit(str){
	return str.replace(RegExpMap.notnumreplace, '');
}
// 전화번호 포맷 체크
function isPhoneNoFormat(str){
	return RegExpMap.phone.test(str);
}
// 주민번호 포맷 체크
function isSsnFormat(str){
	if(! RegExpMap.juminno.test(str)) return false;
	var num = RegExp.$1 + RegExp.$2;
	var sum = 0;
	var last = num.charCodeAt(12) - 0x30;
	var bases = "234567892345";
	for(var i=0; i<12; i++){
		if(_.isNaN(num.substring(i,i+1))) return false;
		sum += (num.charCodeAt(i) - 0x30) * (bases.charCodeAt(i) - 0x30);
	}
	var mod = sum % 11;
	return ((11 - mod) % 10 == last);
}
// 이메일 포맷 체크
function isEmailFormat(str){
	return RegExpMap.email.test(str);
}
// 주민번호 포맷
function formatSsn(str){
	return formatNumMask(str, '######-#######');
}
// 사업자번호 포맷 체크
function isBizNoFormat(str){
	if(! RegExpMap.bizno.test(str)) return false;
	return true;
	// var num = RegExp.$1 + RegExp.$2 + RegExp.$3;
	// var cVal = 0;
	// for(var i=0; i<8; i++){
	// 	var cKeyNum = parseInt(((_tmp = i % 3) == 0) ? 1 : (_tmp  == 1) ? 3 : 7);
	// 	cVal += (parseFloat(num.substring(i,i+1)) * cKeyNum) % 10;
	// }
	// var li_temp = parseFloat(num.substring(i,i+1)) * 5 + '0';
	// cVal += parseFloat(li_temp.substring(0,1)) + parseFloat(li_temp.substring(1,2));
	// return (parseInt(num.substring(9,10)) == 10-(cVal % 10)%10);
}
// 사업자번호 포맷
function formatBizNo(str){
	return formatNumMask(str, '###-##-#####');
}
// // 우편번호 포맷 체크
// function isPostNoFormat(str){
// 	if(! RegExpMap.postno.test(str)) return false;
// 	return true;
// }

// // 우편번호 포맷
// function formatPostNo(str){
// 	return formatNumMask(str, '###-###');
// }

// // node 존재하면 true
// function existsElem(parent, el){
// 	var e = ge(parent, el, true);
// 	return e != null;
// }

// node enable disable
function enableElem(parent, el, isEnable){
	isEnable = !! isEnable;
	var e = ge(parent, el), $e = $(e), tagName = e.tagName.toLowerCase();
	if((tagName == 'input' && _.includes(['text', 'password'], e.type)) || (tagName == 'textarea')){
		$e.attr('readOnly', ! isEnable);
	}else{
		if(isEnable){
			$e.removeAttr('disabled');
		}else{
			$e.attr('disabled', true);
		}
	}
	$e[isEnable?'removeClass':'addClass']('disabled');
}

// function isEnabled(parent, el){
// 	var $el = $(ge(parent, el));
// 	var is_disabled = $el.prop('readOnly') === true || $el.prop('disabled') === true || $el.hasClass('disabled');
// 	return ! is_disabled;
// }

// display element
// function displayElem(parent, el, isDisplay){
// 	$(ge(parent, el))[isDisplay?'show':'hide']();
// }

// element display check
// function isDisplay(parent, el){
// 	return $(ge(parent, el)).css('display') != 'none'
// }

// jquery !this.disabled 제거, unformat 적용
// jquery 에서 form serialize 할 때 적용됨

var rselectTextarea = /^(?:select|textarea)/i;
var rinput = /^(?:color|date|datetime|email|hidden|month|number|password|range|search|tel|text|time|url|week|file)$/i;
(function(){
	var rCRLF = /\r?\n/g;
	$.fn.extend({
		serializeArray2: function(){
			return this.map(function(){
				return this.elements ? $.makeArray(this.elements) : this;
			})
			.filter(function(){
				//return this.name && !this.disabled &&
				return this.name &&
					(this.checked || rselectTextarea.test(this.nodeName) ||
						rinput.test(this.type));
			})
			.map(function(i, elem){
				var val = $(this).val();
				// modified by pyk at 2011-06-03
				var edata = getSetFieldData(elem);
				if(! $(elem).hasClass('dont_unformat')){
					var unformatFunc = _getFieldUnformatFunc(elem);
					if(unformatFunc){
						val = unformatFunc(val);
					}
				}
				if(edata.defaultV != null && (val == null || val === '')){
					val = String(edata.defaultV);
				}
				// end
				return val == null ?
					null :
					$.isArray(val) ?
						$.map(val, function(val, i){
							return { name: elem.name, value: val.replace(rCRLF, "\r\n") };
						}) :
						{ name: elem.name, value: val.replace(rCRLF, "\r\n") };
			}).get();
		}
	});
})();
// 문자열을 html 로 escape 함
function escapeHtml(str){
	return String(str==null?'':str).replace(RegExpMap.htmlamp,'&amp;').replace(RegExpMap.htmllt,'&lt;').replace(RegExpMap.htmlgt,'&gt;').replace(RegExpMap.htmlspace, '&nbsp;');
}
function escapeTextHtml(str){
	return escapeHtml(str).replace(RegExpMap.space, '&nbsp;').replace(RegExpMap.newline, '<br/>').replace()
}

// // 16진수 로 변환
// function hex(num){
// 	return num.toString(16);
// }

// 문자열을 따옴표 안에 넣을 때 변환
var quote = (function(){
	// '(single quoto 추가 pyk)
	//var escapeable = /[\\\"\'\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
	// 다시 제거 함
	var escapeable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
	// table of character substitutions
	var meta = {
	'\b': '\\b',
	'\t': '\\t',
	'\n': '\\n',
	'\f': '\\f',
	'\r': '\\r',
	'"' : '\\"',
	//"'" : "\\'",// 추가. escapeable 에도 추가
	'\\': '\\\\'};
	var c;
	return function(string){
		escapeable.lastIndex = 0;
		return escapeable.test(string) ?
			string.replace(escapeable, function(a){
				c = meta[a];
				if(typeof c === 'string'){
					return c;
				}
				return '\\u' + ('0000' + (+(a.charCodeAt(0))).toString(16)).slice(-4);
			}) : string;
	};
})();
// 이걸로 하면 안된다. encodeJson = JSON.stringify
function encodeJson(o){
	//if(o === null || o === undefined) return '""';
	if(o === null || o === undefined) return 'null';
	switch(o.constructor){
	case Boolean:
	case Number:
		return String(o);
	case String:
		return '"' + quote(o) + '"';
	case Date:
		return String(o.getTime());
	case Array:
		var arr = [];
		for(var i = 0, k = o.length; i < k; i++)
			arr.push(encodeJson(o[i]));
		return '[' + arr.join(',') + ']';
	case Object:
		var arr = [];
		for(var p in o){
			if(o.hasOwnProperty(p))
				arr.push('"'+p+'"' + ':' + encodeJson(o[p]));
		}
		return '{' + arr.join(',') + '}';
	case Function:
		return '"<Function>"';
	default:
		return String(o);
	}
}
function encodeJsonWithObjectKeySort(o){
	if(o === null || o === undefined) return '""';
	switch(o.constructor){
	case Boolean:
	case Number:
		return String(o);
	case String:
		return '"' + quote(o) + '"';
	case Date:
		return String(o.getTime());
	case Array:
		var arr = [];
		for(var i = 0, k = o.length; i < k; i++)
			arr.push(encodeJson(o[i]));
		return '[' + arr.join(',') + ']';
	case Object:
		var arr = [], parr = [];
		for(var p in o){
			if(o.hasOwnProperty(p))parr.push(p);
		}
		parr.sort();
		for(var i = 0, len = parr.length; i < len; i++){
			var p = parr[i];
			arr.push('"'+p+'"' + ':' + encodeJson(o[p]));
		}
		return '{' + arr.join(',') + '}';
	case Function:
		return '"<Function>"';
	default:
		return String(o);
	}
}
function _getCsrfInfo(){
	return _.extend({}, window._csrf);
}
// 폼 만들어 submit
function _freeSubmit(s){
	s = s || {};
	var attr = $.extend({method: 'post'}, s.attr);
	var prop = $.extend({}, s.prop);
	var $form = $('<form>').attr(attr);
	$.extend(true, $form[0], prop);
	if(attr.method == 'post'){
		// for csrf
		var csrfInfo = _getCsrfInfo();
		if(csrfInfo.token){
			s.param = s.param||{};
			s.param[csrfInfo.parameterName] = csrfInfo.token;
		}
	}
	var paramArray = _makeOptionParamArray(s);
	var i, len, param;
	for(i = 0, len = paramArray.length; i < len; i+=1){
		param = paramArray[i];
		$('<input type="hidden" />').attr(param).appendTo($form);
	}
	$form.appendTo(document.body);
	$form[0].submit();
	$form.remove();
}
// function simpleSubmit(s){
// 	var action = _makeGetUrl({url: s.url});
// 	var target = s.target || '_self';
// 	var method = s.method || 'post';
// 	_freeSubmit($.extend(s, {attr: {action: action, target: target, method: method}}));
// }
// 콤보 세팅
function parseStrToObj(str){
	return new Function('return ('+str+');')();
}

function setCombo(parent, e, array, s){
	var combo = ge(parent, e);
	var combo_option = $(combo).attr('data-combo_option');
	s = s || (combo_option ? parseStrToObj(combo_option) : {});
	var temp1 = [];
	if(s.insertAll)temp1.push({code: '', name: '-- '+(s.name||'전체')+' --'});// pyk code
	if(s.insertSel)temp1.push({code: '', name: '-- '+(s.name||'선택')+' --'});// pyk code
	if(s.insertNull)temp1.push({code: '', name: ''});// pyk code
	if(s.insertText)temp1.push({code: '', name: s.insertText});// pyk code
	var comboArray = _makeCodeList(temp1.concat(array));
	$(combo).empty();
	_.each(comboArray, function(it){
		$('<option>').val(it.code).text(it.name).appendTo(combo);// pyk code
	})
	if(s.selectedValue){sev(parent, e, s.selectedValue);}
	if(s.selectedIndex != null){combo.selectedIndex = s.selectedIndex;}
}

// 파라미터용 배열 만들기, form, object, array 모두 가능
function _makeParamArray(){
	var paramArray = [];
	var i, len, arg, args = $.makeArray(arguments), i2, len2;
	for(i = 0, len = args.length; i < len; i+=1){
		arg = args[i];
		if(arg){
			if(arg.tagName && arg.tagName.toUpperCase() == 'FORM'){
				paramArray = paramArray.concat($(arg).serializeArray2());
			}else if($.isArray(arg)){
				paramArray = paramArray.concat(arg);
			}else if($.isPlainObject(arg)){
				for(var k in arg){
					if($.isArray(arg[k])){
						for(i2 = 0, len2 = arg[k].length; i2 < len2; i2+=1){
							paramArray.push({name: k, value: arg[k][i2]});
						}
					}else{
						paramArray.push({name: k, value: arg[k]});
					}
				}
			}
		}
	}
	return paramArray;
}
// get element value, 포매팅 적용된거면 unformat 하여 반환
function gev(parent, e){
	e = ge(parent, e);
	var v;
	if(_.includes(['input', 'select', 'textarea'], e.tagName.toLowerCase())){
		v = e.value;
	}else{
		v = $(e).text();
	}
	if(_.includes(['radio', 'checkbox'], e.type)){
		v = $(':input[name='+e.name+']:checked', parent).val();
	}else{
		var uf = _getFieldUnformatFunc(e);
		if(uf){v = uf(v);}
	}
	return ifnull(v);
}
// set element value, 포매팅 적용된거면 format 하여 세팅
function sev(parent, el, v){
	var e = ge(parent, el);
	if(e == null){
		var message = 'sev(): error. el == '+el;
		alert(message);
		throw message;
	}
	var $e = $(e);
	if(! $e.hasClass('dont_unformat') && v != null){
		var formatFunc = _getFieldFormatFunc(e);
		if(formatFunc){v = formatFunc(v);}
	}
	v = ifnull(v);
	var tagName = e.tagName.toLowerCase();
	// radio, checkbox 처리
	if(_.includes(['radio', 'checkbox'], e.type)){
		$('input[name='+e.name+']', parent).each(function(){
			this.checked = this.value == String(v);
		});
	}else{
		if(_.includes(['input', 'select', 'textarea'], tagName)){
			$e.val(v);
		}else{
			if($e.hasClass('formdata_escapeTextHtml')){
				$e.html(escapeTextHtml(v));
			}else if($e.hasClass('formdata_html')){
				$e.html(v);
			}else{
				$e.text(v);
			}
		}
	}
	// 2015-10-13 bootstrap datepicker 어쩔수 없다.
	if($e.data('datepicker') && $e.data('datepicker').picker){
		$e.bs_datepicker('update', v);
	}
}

// window.open 에 사용할 기본 옵션
function _getWindowOpenFeatures(s){
	var width = ifnull(s.width, ifnull(s.features && s.features.width, 800));
	var height = ifnull(s.height, ifnull(s.features && s.features.height, 600));
	var left = (screen.availWidth - width)/2;
	var top = (screen.availHeight - height)/2;
	var defaultFeatures = {
		channelmode:0,
		directories:0,
		fullscreen:0,
		width:width,
		height:height,
		location:0,
		menubar:0,
		resizable:1,
		scrollbars:1,
		status:1,
		titlebar:1,
		toolbar:0,
		top:top,
		left:left};
	var features = $.extend({}, defaultFeatures, s);
	return _makeMapToFeatures(features, '=', ',');
}
// 팝업 파라미터 만들 때 map 을 features string 으로
function _makeMapToFeatures(map, d1, d2){
	var temparr = [];
	for(var k in map){
		temparr.push(k+d1+map[k]);
	}
	return temparr.join(d2);
}
// // get 방식의 url 생성
// function _makeGetUrl(action, s){
// 	var temp1 = actionToUrl(action);
// 	var paramString = $.param(_makeOptionParamArray(s));
// 	if(paramString){
// 		temp1 = temp1 + (temp1.indexOf('?')>=0?'&':'?') + paramString;
// 	}
// 	return temp1;
// }
// url 로 이동.
// function goLink(s){
// 	var attr, s2;
// 	if($.isPlainObject(s)){
// 		//goLink({action, param, target});
// 		attr = {
// 			// action: _makeGetUrl(s.action),
// 			action: s.url || s.action,
// 			method: s.method || 'get'
// 		};
// 		s2 = s;
// 	}else{
// 		attr = {
// 			action: s,
// 			method: 'get'
// 		};
// 		s2 = arguments[1] || {};
// 	}
// 	if(s2.target)attr.target = s2.target;
// 	var param = s2.param;
// 	var form = s2.form;
// 	_freeSubmit({
// 		attr: attr,
// 		//param: $.extend({_: new Date().getTime()}, param), nginx 에서 .html expires: -1; 처리함
// 		param: param,
// 		form: form
// 	});
// }
// 팝업 또는 iframe 에 window.open 사용하지 말고 openWindow 로 사용해야 권한 파라미터가 전달됨
function openWindow(s){
	s = s || {};
	var target = s.target || 'popup_'+new Date().getTime();
	var method = s.method||'get';
	s.param = $.extend({}, s.param);
	var _isPopup = (! _.includes(['_self', '_parent', '_top'], target) && s.param._isPopup == null && $('iframe[name='+target+']').length == 0);
	if(_isPopup){
		s.param = $.extend({}, s.param);
	}
	// var url = _makeGetUrl(s.action);
	var url = s.url || s.action;
	var sFeatures = '';
	if(_isPopup){
		var temp = {};
		if(s.width != null){temp.width = s.width;}
		if(s.height != null){temp.height = s.height;}
		sFeatures = _getWindowOpenFeatures($.extend(temp, s.features));
	}
	//var isGet = method == 'get';
	//var openUrl = isGet ? _makeGetUrl(s.action) : 'about:blank';
	var popupWindow = window.open('about:blank', target, sFeatures);
	var opts = $.extend(s, {attr: {target: target, action: url, method: method}});
	_freeSubmit(opts);
	if(_isPopup){
		if(popupWindow){
			if(url.substr(0, 4) != 'http'){
				if(s.noCloseWhenParentUnloadYn != 'Y'){
					window.__popupList.push(popupWindow);
				}
				setTimeout(function(){
					try{$(popupWindow).focus();}catch(ignore){doNothing();}/* csap 보완 */
				}, 200);
			}
		}else{
			var msg = isKo?'브라우저 설정으로 인하여 팝업이 차단되었습니다.\n브라우저 설정에서 팝업차단 해제해주시기 바랍니다.':'Pop-up blocked due to browser settings.';
			showAlert(null, msg);
		}
	}
	return popupWindow;
}

// // 윈도우의 모든 프레임을 돌면서 함수 실행
// function doEachFrame(frame, doFunc){
// 	doFunc(frame);
// 	for(var n = 0, nsz = frame.frames.length; n < nsz; n++){
// 		arguments.callee(frame.frames[n], doFunc);
// 	}
// }

// col array 형태를 map array 형태로 변환
function makeMapArray(array){
	if(array.length == 0 || ! $.isArray(array[0])) return array;
	var keys = array[0];
	var keysCnt = keys.length;
	var dataArray = array.slice(1, array.length);
	var newArray = [], map;
	for(var r = 0, rsz = dataArray.length; r < rsz; r++){
		map = {};
		for(var c = 0, csz = keysCnt; c < csz; c++){
			map[keys[c]] = dataArray[r][c];
		}
		newArray.push(map);
	}
	return newArray;
}
// map array 형태를 col array 형태로 변환
function makeColArray(array, ckList){
	if(array.length == 0 || $.isArray(array[0])) return array;
	if(! ckList){
		var ckList = [];
		for(var ck in array[0]){
			ckList.push(ck);
		}
	}
	var dataList = [], rdata;
	for(var r = 0, rsz = array.length; r < rsz; r++){
		rdata = [];
		for(var c = 0, csz = ckList.length; c < csz; c++){
			rdata.push(array[r][ckList[c]]);
		}
		dataList.push(rdata);
	}
	return [ckList].concat(dataList);
}

// // parent window 여부
// function isParentWindow(windowObject){
// 	return windowObject.frames[window.name] == window;
// }
// 에러가 안날 때까지 아규먼트로 전달되는 함수 실행.
function _tryThese(){
	for(var n = 0, nlen = arguments.length; n < nlen; n++){
		try{
			return arguments[n]();
		}catch(ignore){doNothing();}/* csap 보완 */
	}
}

// // 문자열 byte length 구하기
// function getStrByteLength(s,b,i,c){
// 	for(b=i=0;c=s.charCodeAt(i++);b+=c>>11?3:c>>7?2:1);
// 	return b
// }

// 반드시 배열로 만들어 리턴
function _mustArray(a){
	if(a==null)return[];
	if($.isArray(a))return a;
	if(typeof(a)=='string')return a.split(',');
	return [a];
}

function _checkFormsForAjax(s){
	if(! s.disableFormCheck){
		var form = _mustArray(s.form);
		for(var i = 0, len = form.length;i < len; i++){
			if(! checkForm(form[i]))return false;
		}
	}
	if(s.checkFormAfter && s.checkFormAfter(s) !== true)return false;
	return true;
}
function _makeOptionParamArray(s, excludeForm){
	var paramArray = [];
	if(s){
		if(s.param){
			paramArray.push(s.param);
		}
		if(s.form && ! excludeForm){
			paramArray = paramArray.concat(_mustArray(s.form));
		}
	}
	return _makeParamArray.apply(null, paramArray);
}
// // 실제 url 만들기
// function actionToUrl(url){
// 	url = fstr(url);
// 	var result;
// 	if(_.startsWith(url, 'http')){
// 		result = url;
// 	}else if(_.startsWith(url, '/')){
// 		var path = CONTEXTPATH + url;
// 		path = path.replace(/\/\//g, '/');
// 		// result = location.protocol+'//'+location.host+path;
// 		result = path;
// 	}else{
// 		result = url;
// 	}
// 	// port 틀리게 하면 csrf 되는데
// 	// domain 틀리게 하면 csrf 안먹어
// 	//result = result.replace(':8081', ':8082');
// 	return result;
// }
function getCurrFocusElem(){
	return $(':focus:eq(0)', document.body)[0];
}
// 공통 ajax
function ajax(s){
	s = $.extend({
		method: 'POST',// 구 type: 'POST',
		async: true,
		cache: false,
		dataType: 'json',
		contentType: "application/json; charset-utf-8", // 수정한 부분(20240624)
		// traditional: true, 배열 전송할 때 데이터 직렬화를 하는 옵션.
		// 아래거 있어야 ajax 로 다른 포트 보낼때 쿠키 보냄
		// 쿠키를 첨부해서 보내는 요청 헤더에 Authorization 항목이 있는 요청
		// https://junglast.com/blog/http-ajax-withcredential
		// 서버에서도 addCorsMappings 로 allowedOrigin 과 allowCredentials(true) 세팅 해야함.
		xhrFields: {withCredentials : true}
	}, s);
	var isKeepFocus = !! s.isKeepFocus;
	var currFocusElem = getCurrFocusElem();
	var currGrid = window.SlickUtil ? SlickUtil.getGridObjectByElem(currFocusElem) : null;
	if(currGrid != null){
		isKeepFocus = true;
	}
	var focusElem = isKeepFocus ? currFocusElem : null;
	if(_checkFormsForAjax(s)!==true){return;}
	if(s.isSave){
		if(! s.confirmMessage)s.confirmMessage = commsg.confirm.save;
		if(! s.successMessage)s.successMessage = commsg.alert.saved
	}else if(s.isExecute){
		if(! s.confirmMessage)s.confirmMessage = commsg.confirm.execute;
		if(! s.successMessage)s.successMessage = commsg.alert.executed;
	}else if(s.isDelete){
		if(! s.confirmMessage)s.confirmMessage = commsg.confirm.remove;
		if(! s.successMessage)s.successMessage = commsg.alert.deleted;
	}else if(s.isRegister){
		if(! s.confirmMessage)s.confirmMessage = commsg.confirm.register;
		if(! s.successMessage)s.successMessage = commsg.alert.registered;
	}else if(s.isModifiy){
		if(! s.confirmMessage)s.confirmMessage = commsg.confirm.update;
		if(! s.successMessage)s.successMessage = commsg.alert.updated;
	}
	if(s.addMsg && s.confirmMessage)s.confirmMessage += ' ['+s.addMsg+']';
	if(s.addMsg && s.successMessage)s.successMessage += ' ['+s.addMsg+']';
	var dataType = s.dataType;
	function ajaxHandler(){
		var isUpload = !! s.isUpload;
		//var paramArray = _makeOptionParamArray(s, isUpload);
		var saveListJson = s.param?.saveListJson || 'default_value';
		var form = s.form;
		s = $.extend({
			beforeSend: function(xhr){
				if(! (s.isNoProgress || s.noLoseFocus))_loseCurrentFocus();
				if(! s.isNoProgress)progressCover(true, s);
				if(isUpload){
				}else{
					//xhr.setRequestHeader('Accept-Encoding', 'giz');// 이거하면 크롬 콘솔 보면 에러...
					// 아래거 안하는게 낫겠다.
					// if(dataType == 'json'){
					// 	xhr.setRequestHeader("Accept", $.ajaxSettings.accepts.json);
					// }else if(dataType == 'xml'){
					// 	xhr.setRequestHeader("Accept", $.ajaxSettings.accepts.xml);
					// }
				}
				if(s.onBeforeSend){
					s.onBeforeSend(xhr);
				}
				// for csrf
				var csrfInfo = _getCsrfInfo();
				if(csrfInfo.token){
					xhr.setRequestHeader(csrfInfo.headerName, csrfInfo.token);
				}
			},
			//data: paramArray,
			data: saveListJson,
			uploadProgress: function(event, position, total, percentComplete){
				$(PRGR_BAR_INNER_SEL).css({
					height: 15,
					width: percentComplete+'%'
				});
			},
			// dataType: dataType,
			success: function(data, status, xhr){
				// var args = arguments;
				setTimeout(function(){
					// data =
					// 	dataType == 'json' ? data :
					// 	dataType == 'xml' ? $.xml2json(data) :
					// 	data;
					if(dataType == 'html' || typeof data == 'string'){
						data = _.trim(data);
						if(_.startsWith(data, '{"') && _.endsWith(data, '}')){
							data = parseStrToObj(data);
						}
					}else if(dataType == 'json'){
						if(! s.jsonOriginal){
							makeListPropToMapArray(data);
						}
					}
					if(isValidJsonResponse(data)){
						function handleSuccess(){
							if(s.onBeforeSuccess)s.onBeforeSuccess(data, status, xhr);
							if(s.onSuccess)s.onSuccess(data, status, xhr);
						}
						if(s.successMessage){
							showAlert(null, s.successMessage, handleSuccess, s.smacMillis || 4000);
						}else{
							handleSuccess();
						}
					}else{
						if(s.onErrorBefore){
							s.onErrorBefore(data, status, xhr);
						}
						if(s.onError){
							s.onError(data, status, xhr);
						}else{
							ajaxOnErrorDefault(data);
						}
					}
				});
			},
			error: function(xhr, status, errorThrown){
				setTimeout(function(){
					if(s.onErrorBefore){
						s.onErrorBefore(xhr, status, errorThrown);
					}
					if(s.onError){
						s.onError(xhr, status, errorThrown);
					}else{
						ajaxOnErrorDefault(xhr);
					}
				});
			},
			complete: function(xhr, textStatus){
				if(isUpload){
					// for format
					var upload_org_value;
					$(':text', form).each(function(){
						var e = this, $e = $(e);
						var upload_org_value = $e.data('upload_org_value');
						if(upload_org_value){
							$e.val(upload_org_value).removeData('upload_org_value');
						}
					});
				}
				if(! s.isNoProgress){
					progressCover(false);
				}
				if(s.onComplete){
					setTimeout(function(){s.onComplete(xhr, textStatus);});
				}
				if(focusElem){
					setTimeout(function(){selectFocus(null, focusElem);});// setTimeout 필수
				}
			}
		}, s);
		// if(! s.url){
		// 	s.url = actionToUrl(s.action);
		// }
		s.url = s.url || s.action;
		if(s.formdata){
			s.contentType = false;
			s.processData = false;
			s.data = s.formdata;
		}
		// 반드시 여기에 위치해야한다.
		if(isUpload){
			// 업로드 때는 form format 할 수 없으므로
			// 일단 unformat 하고 끝나면 다시 원복하자
			$(':text', form).each(function(){
				var e = this, $e = $(e);
				var uf = _getFieldUnformatFunc(e);
				if(uf){
					$e.data('upload_org_value', $e.val()).val(uf($e.val()));
				}
			});
		}
		return isUpload ? $(form).ajaxSubmit(s) : $.ajax(s);
	}
	if(s.confirmMessage){
		showConfirm(null, s.confirmMessage, function(isYes){if(isYes){ajaxHandler();}});
	}else{
		ajaxHandler();
	}
};
// var axiosInstance = axios.create({
// 	timeout: 60 * 1000
// });
// var ajaxAxios = function(s){
// 	var isKeepFocus = !! s.isKeepFocus;
// 	var currFocusElem = getCurrFocusElem();
// 	var currGrid = window.SlickUtil ? SlickUtil.getGridObjectByElem(currFocusElem) : null;
// 	if(currGrid != null){
// 		isKeepFocus = true;
// 	}
// 	if(_checkFormsForAjax(s)!==true){return;}
// 	if(s.isSave){
// 		if(! s.confirmMessage)s.confirmMessage = commsg.confirm.save;
// 		if(! s.successMessage)s.successMessage = commsg.alert.saved
// 	}else if(s.isExecute){
// 		if(! s.confirmMessage)s.confirmMessage = commsg.confirm.execute;
// 		if(! s.successMessage)s.successMessage = commsg.alert.executed;
// 	}else if(s.isDelete){
// 		if(! s.confirmMessage)s.confirmMessage = commsg.confirm.remove;
// 		if(! s.successMessage)s.successMessage = commsg.alert.deleted;
// 	}else if(s.isRegister){
// 		if(! s.confirmMessage)s.confirmMessage = commsg.confirm.register;
// 		if(! s.successMessage)s.successMessage = commsg.alert.registered;
// 	}else if(s.isModifiy){
// 		if(! s.confirmMessage)s.confirmMessage = commsg.confirm.update;
// 		if(! s.successMessage)s.successMessage = commsg.alert.updated;
// 	}
// 	if(s.addMsg && s.confirmMessage)s.confirmMessage += ' ['+s.addMsg+']';
// 	if(s.addMsg && s.successMessage)s.successMessage += ' ['+s.addMsg+']';
// 	var doAxios = function(){
// 		s = $.extend({
// 			headers: {},
// 			method: 'post',
// 			url: actionToUrl(s.action, 'do'),
// 			onUploadProgress: function(progressEvent){
// 				var percentComplete = progressEvent.loaded * 100 / progressEvent.total;
// 				$(PRGR_BAR_INNER_SEL).css({
// 					height: 15,
// 					width: percentComplete+'%'
// 				});
// 			}
// 		}, s);
// 		// for csrf
// 		var csrfInfo = _getCsrfInfo();
// 		if(csrfInfo.token){
// 			s.headers[csrfInfo.headerName] = csrfInfo.token;
// 		}
// 		var paramArray = _makeOptionParamArray(s);
// 		if(paramArray.length){
// 			s.params = _.reduce(paramArray, function(it, r){
// 				r[it.name] = it.value;
// 				return r;
// 			}, {});
// 		}
// 		var focusElem = isKeepFocus ? currFocusElem : null;
// 		if(! (s.isNoProgress || s.noLoseFocus))_loseCurrentFocus();
// 		if(! s.isNoProgress)progressCover(true, s);
// 		return axiosInstance(s).then(function(response){
// 			var data = response.data;
// 			if(typeof data == 'string'){
// 				data = _.trim(data);
// 				if(_.startsWith(data, '{"') && _.endsWith(data, '}')){
// 					data = parseStrToObj(data);
// 				}
// 			}
// 			if(! s.jsonOriginal){
// 				makeListPropToMapArray(data);
// 			}
// 			if(isValidJsonResponse(data)){
// 				function handleSuccess(){
// 					if(s.onBeforeSuccess)s.onBeforeSuccess(data);
// 					if(s.onSuccess)s.onSuccess(data);
// 				}
// 				if(s.successMessage){
// 					showAlert(null, s.successMessage, handleSuccess, s.smacMillis || 4000);
// 				}else{
// 					handleSuccess();
// 				}
// 			}else{
// 				if(s.onError){
// 					s.onError(data);
// 				}else{
// 					ajaxOnErrorDefault(data);
// 				}
// 			}
// 		}).catch(function(error){
// 			if(s.onError){
// 				s.onError(error);
// 			}else{
// 				ajaxOnErrorDefault(error);
// 			}
// 		}).finally(function(){
// 			if(! s.isNoProgress)progressCover(false);
// 			if(s.onComplete)setTimeout(s.onComplete);
// 			if(focusElem){
// 				setTimeout(function(){selectFocus(null, focusElem);});// setTimeout 필수
// 			}
// 		});
// 	};
// 	if(s.confirmMessage){
// 		showConfirm(null, s.confirmMessage, function(isYes){if(isYes){doAxios();}});
// 	}else{
// 		doAxios();
// 	}
// };
function ajaxOnErrorDefault(data){
	var message;
	if(data.errorCode || data.errorMessage){
		message = data.errorMessage;
	}else if(data.message){// for axios
		message = data.message;
	}else{
		var xhr = data;
		message = (xhr.status+' '+xhr.statusText);
	}
	showAlert(null, message);
}
// grid 하나 조회에 특화된 ajax
function ajaxGridSearch(s){
	s = $.extend({}, s);
	var grid = s.grid;
	s.onSuccess = function(data, status, xhr){
		grid.setItems(data.list1);
	};
	ajax(s);
}
// grid 하나 저장에 특화된 ajax
function ajaxGridSave(s){
	s = s || {};
	s.isSave = true;
	var grid = s.grid;
	s.param = s.param || {};
	if(! validateGrid(grid, s))return;
	s.param.saveListJson = makeSaveListJson(s.items);
	console.log(s.param);
	console.log(s.items);
	console.log(s.param.saveListJson); // json 데이터 확인.
	ajax(s);
}
// 그리드 수정중이면 에러
function _checkGridEditing(grid){
	if(grid.getEditorLock().isActive()){
		if(! grid.getEditorLock().commitCurrentEdit()){
			showAlert(null, '그리드 수정 완료 후 사용해주세요.', null, 1500);
			return false;
		}
	}
	return true;
}
function validateGrid(grid, s){
	if(! _checkGridEditing(grid))return;
	var check = grid.validate(s);
	if(check.valid){
		s.items = check.items;
	}
	return check.valid;
}

function makeSaveListJson(array){
	return encodeJson(makeColArray(array));
}

// 폼에 데이터 세팅
function setFormData(form, item, isOnlyItem){
	if(isOnlyItem){
		var itemKeyList = _.keys(item);
	}
	var i, len, el, elements = $.makeArray(form.elements);
	for(i = 0, len = elements.length; i < len; i+=1){
		el = elements[i];
		if(el.name && el.tagName.toLowerCase() != 'button'){
			if(! isOnlyItem || _.includes(itemKeyList, el.name)){
				sev(form, el, ifnull(item[el.name]));
			}
		}
	}
	$('[data-field]', form).each(function(){
		var el = this, $el = $(el);
		var name = $el.attr('data-field');
		if(name){
			if(! isOnlyItem || _.includes(itemKeyList, name)){
				sev(null, el, item[name]);
			}
		}
	});
	$('[data-fieldfomula]', form).each(function(){
		var el = this, $el = $(el);
		var fieldfomula = $el.attr('data-fieldfomula');
		if(fieldfomula){
			var val = new Function('it', 'return(' + fieldfomula + ');')(item);
			sev(null, el, val);
		}
	});
}
// form element data 를 반환 name 중복 경우 배열형태, 포매팅 제거
function getFormData(form){
	var o = {};
	$('select, input, textarea', form).each(function(){
		var e = this;
		//var jq = $(e);
		var tagName = e.tagName.toLowerCase();
		var name = e.name;
		if(e.name){
			var v = gev(form, e);
			if(_.includes(['checkbox', 'radio'], e.type)){
				if(e.checked){
					_appendObjectValue(o, name, v);
				}
			}else{
				_appendObjectValue(o, name, v);
			}
		}
	});
	return o;
}

// object 에 k, v 를 append, 동일 키 면 배열로 세팅
function _appendObjectValue(o, k, v){
	if($.isArray(o[k])){
		o[k].push(v);
	}else if(o[k] != null){
		o[k] = [o[k], v];
	}else{
		o[k] = v;
	}
}
// 속성이 배열일 경우 makeMapArray 적용
function makeListPropToMapArray(model){
	for(var k in model){
		if($.isArray(model[k])){
			model[k] = makeMapArray(model[k]);
		}
	}
}
// // 좌측 패딩
// function lpad(a, cnt, padder){
// 	a = String(a);
// 	return repeatStr(padder || ' ', cnt - a.length) + a;
// }

// // 우측 패딩
// function rpad(a, cnt, padder){
// 	a = String(a);
// 	return a + repeatStr(padder || ' ', cnt - a.length);
// }

// // 숫자 패딩
// function npad(a, cnt){
// 	return lpad(a, cnt, '0');
// }

function _applyRound(mathFn, number, precision){
	precision = precision || 0;
	if(number == null)number = 0;
	var x = +Big(10).pow(-1 * precision);
	var num = +Big(mathFn(+Big(number).div(x))).times(x);
	return num;
}
function round2(number, precision){
	return _applyRound(Math.round, number, precision);
}
function ceil2(number, precision){
	return _applyRound(Math.ceil, number, precision);
}
function floor2(number, precision){
	return _applyRound(Math.floor, number, precision);
}
function trunc2(number, precision){
	precision = precision || 0;
	if(number == null)number = 0;
	return +Big(number).round(precision, Big.roundDown);
	// return +Big(floor2(Math.abs(number), precision)).times(number >= 0 ? 1 : -1);
}

// // from oracle
// function isBetween(a, b, c){
// 	return a >= b && a <= c;
// }

// // from oracle
// function nvl2(a, b, c){
// 	return isNotNull(a) ? b : c;
// }

// ___regexConstructor = /function([^\(]*)/;
// // 객체의 생성자 명
// function getConstructorName(o){
// 	if(o.constructor){
// 		var code = o.constructor.toString();
// 		match = code.match(___regexConstructor);
// 		return (match && match[1]) || null;
// 	}
// 	return null;
// }

// 문자열 반복
function repeatStr(str, cnt){
	var res = '';
	for(var n = 0; n < cnt; n++){
		res += str;
	}
	return res;
}

// // 폼 필드 맵
// function makeFormParamMap(form){
// 	var paramList = $(form).serializeArray();
// 	var paramMap = {};
// 	var i, len, param;
// 	for(i = 0, len = paramList.length; i < len; i+=1){
// 		param = paramList[i];
// 		paramMap[param.name] = param.value;
// 	}
// 	return paramMap;
// }

// 파일 확장자
function getFileExt(fileName){
	var dotIndex = fileName.lastIndexOf('.');
	var extension = dotIndex >= 0 ? fileName.substr(dotIndex+1):'';
	return extension;
}
// 파일 basename
function getFileBaseName(fileName){
	var dotIndex = fileName.lastIndexOf('.');
	var lastIndex = dotIndex >= 0 ? dotIndex : fileName.length;
	return fileName.substr(0, lastIndex);
}
// element select focus
function selectFocus(p, e){
	if(window.SlickUtil){
		var grid = SlickUtil.getGridObjectByElem(e);
		if(grid != null){
			grid.focus();
			return;
		}
	}
	try{
		$(ge(p, e)).select().focus();
	}catch(ignore){doNothing();}/* csap 보완 */
}

// args 중 가장 큰 값
function greatest(){
	var g = null;
	for(var n = 0, nlen = arguments.length; n < nlen; n++){
		if(arguments[n] != null && arguments[n] != ''){
			if(g == null){
				g = arguments[n];
			}else if(arguments[n] >= g){
				g = arguments[n];
			}
		}
	}
	return g;
}
// args 중 가장 작은 값
function least(){
	var g = null;
	for(var n = 0, nlen = arguments.length; n < nlen; n++){
		if(arguments[n] != null && arguments[n] != ''){
			if(g == null){
				g = arguments[n];
			}else if(arguments[n] <= g){
				g = arguments[n];
			}
		}
	}
	return g;
}
// a 가 min, max 벗어나면 min 이나 max 로 리턴
function getBetweenNum(a, min, max){
	return Math.min(Math.max(a, min), max);
}
// 트리 구조에서 함수 적용
function calcTree(list, levelKey, groupFunction){
	var prevLevel = null;
	var currLevel;
	var subListMap = {};
	var subList = [];
	var i, item;
	for(i = list.length - 1; i >= 0; i-=1){
		item = list[i];
		item.index = i;
		currLevel = item[levelKey];
		// 상위레벨
		if(prevLevel != null && currLevel < prevLevel){
			subList = subListMap[prevLevel];
			subListMap[prevLevel] = null;
		}else{
			subList = [];
		}
		if(subListMap[currLevel] == null) subListMap[currLevel] = [];
		subListMap[currLevel].push(item);
		subList.sort(function(a, b){
			return a.index - b.index;
		});
		groupFunction(item, subList);
		prevLevel = currLevel;
	}
}
// // from oracle
// function decode(x){
// 	var a = arguments;
// 	for(var n = 1, nLen = a.length; n < nLen; n += 2){
// 		if(x == a[n]){
// 			return a[n+1];
// 		}
// 		if(nLen - 1 == n+2){
// 			return a[n+2];
// 		}
// 	}
// 	return x;
// }

// item array 값에 0 이면 빈문자열로 만들기
// function makeEmptyIfZero(arr){
// 	var i, len;
// 	if($.isArray(arr)){
// 		for(i = 0, len = arr.length; i < len; i+=1){
// 			makeEmptyIfZero(arr[i]);
// 		}
// 	}else{
// 		for(var k in arr){
// 			if(x[k] === 0){
// 				x[k] = '';
// 			}
// 		}
// 	}
// }

// a 가 b 를 포함하면 true
// function containsMap(a, b){
// 	for(var k in b){
// 		if(b[k] != a[k]){
// 			return false;
// 		}
// 	}
// 	return true;
// }

// 년월 input 에 월 증가
function addInputMonth(form, name, addCnt){
	sev(form, name, addYm(gev(form, name), 'M', addCnt));
}

function showMessage(title, message, closeMillis){
	if(title == null)title = isKo ? '알림' : 'Message';
	if(closeMillis == null)closeMillis = 3000;
	$.growlUI(title, message, closeMillis);
}

// 강제 숫자 변환
function forceNumber(x){
	x = Number(x);
	return _.isNaN(x) ? 0 : x;
}
function forceUnformatNumber(x){
	return forceNumber(_unformatNumber(x));
}
// 현재 포커스 객체 포커스 잃게 하기
function _loseCurrentFocus(){
	var el = getCurrFocusElem();
	if(el == null)return;
	try{$(el).blur();}catch(skip){doNothing();}/* csap 보완 */
}

// 기본 dialog 띄우기
function openDialog(dForm, s){
	// 모달이 div_app 밖으로 나가니 아래 처리
	if(! $(dForm).hasClass('div_app')){
		var p_div_app = $(dForm).closest('.div_app')[0];
		if(p_div_app){
			var p_div_app_methods = $(p_div_app).data('_div_app_methods');
			var p_div_app_id = $(p_div_app).data('_div_app_id');
			$(dForm).addClass('div_app_sub').addClass(p_div_app_id).data('_div_app_methods', p_div_app_methods);
		}
	}

	// var parentNode = $(dForm).parent()[0];
	s = s || {};
	s = $.extend(true, {}, {
		//show: 'blind',
		//hide: 'blind',
		// width: 'auto',
		// height: 'auto',
		modal: true,
		// appendTo: parentNode,// 이거 하면 dialog 위에 dialog 띄울때 overlay 안먹네
		dialogClass: 'uu-dialog ui-dialog-normal',
		open: function(event, ui){
			if(! s.noCloseFocus){
				setTimeout(function(){
					$(dForm).closest('.ui-dialog').find(':button:eq(0)').focus().blur();
				});
			}
			if(s.onOpen){
				_.bind(s.onOpen, this, event, ui)();
			}
			gridResizeCanvasWithin(dForm);// 안보이는 div 에서 grid 보이게 될때 resizeCanvas 해줘야 함
			if(! $('body').hasClass('uu-scroll-lock')){
				$('body').addClass('uu-scroll-lock');
			}
		},
		close: function(event, ui){
			if(s.onClose){
				_.bind(s.onClose, this, event, ui)();
			}
			if(s.isRemoveWhenClose){
				DivApp.emptyTarget(dForm);// 에 destroyIfDialog 포함됨
				$(dForm).remove();
			}else if(s.isEmptyWhenClose){
				DivApp.emptyTarget(dForm);
				$(dForm).hide();
			}else{
				destroyIfDialog(dForm);
				$(dForm).hide();
			}
			if($('.ui-widget-overlay').length == 0){
				$('body').removeClass('uu-scroll-lock');
			}
		},
		resizeStop: function(event, ui){
			// 이거 안하면 resize 할때 width 줄어든다. css flex 로 바꿔버려 필요는 없지만
			$(event.target).css({width: '', height: ''});
		}
	}, s);

	if(s.width  == null && s.minWidth  == null){s.width  = 'auto';}
	if(s.height == null && s.minHeight == null){s.height = 'auto';}

	$(dForm).dialog(s);
}
// jquery ui dialog 오픈 되어 있는지 검사
function _isDialogOpen(dialog){
	var $d = $(dialog);
	return $d.is(':data(uiDialog)') && $d.dialog('isOpen');
}
// jquery ui dialog 닫기
function closeDialog(dialog){
	if(_isDialogOpen(dialog)){
		$(dialog).dialog('close');
	}
}
function destroyIfDialog(dialog){
	if($(dialog).hasClass('ui-dialog-content')){
		$(dialog).dialog('destroy');
	}
}

// function makeMapByCks(cks, arr){
// 	var map = {};
// 	for(var n = 0, nlen = cks.length; n < nlen; n++){
// 		map[cks[n]] = arr[n];
// 	}
// 	return map;
// }

// function makeArrayByCks(cks, map){
// 	var arr = [];
// 	for(var n = 0, nlen = cks.length; n < nlen; n++){
// 		arr.push(map[cks[n]]);
// 	}
// 	return arr;
// }

// 키로 이루어진 새로운 맵 반환
function _createNewMapByKeys(keys, map){
	var newmap = {};
	for(var i = 0, len = keys.length; i < len; i++){
		newmap[keys[i]] = map[keys[i]];
	}
	return newmap;
}

// // 소계 계산
// function Summary(s){
// 	var aggrs = s.aggrs;
// 	var init = s.init;
// 	var calc = s.calc;
// 	var done = s.done;
// 	var array = s.array;
// 	function calcSub(sub){
// 		var titem = init();
// 		var i, len, item;
// 		for(i = 0, len = sub.items.length; i < len; i+=1){
// 			item = sub.items[i];
// 			calc(titem, item);
// 		}
// 		done(titem, sub.items.length);
// 		titem.startIndex = sub.startIndex;
// 		titem.endIndex = sub.endIndex;
// 		titem.gcount = sub.gcount;
// 		return $.extend(titem, sub.map);
// 	}
// 	function makeSubtotals(){
// 		var i, len, aggr;
// 		for(i = 0, len = aggrs.length; i < len; i+=1){
// 			aggr = aggrs[i];
// 			aggr.subs = [];
// 			if(typeof(aggr.groupby)=='string'){
// 				aggr.groupby = aggr.groupby.split(',');
// 			}
// 			if(typeof(aggr.justsetcks)=='string'){
// 				aggr.justsetcks = aggr.justsetcks.split(',');
// 			}
// 		}
// 		var allsubs = [];
// 		var cursub, changed, item;
// 		var i2, len2;
// 		for(i = 0, len = array.length; i < len; i+=1){
// 			item = array[i];
// 			for(i2 = 0, len2 = aggrs.length; i2 < len2; i2+=1){
// 				aggr = aggrs[i2];
// 				changed = i == 0 || ! equalWithKeys(aggr.cursub.map, item, aggr.groupby);
// 				if(changed){
// 					cursub = {map: _createNewMapByKeys(aggr.groupby.concat(aggr.justsetcks), item), items: [], startIndex: i, endIndex: i, gcount: aggr.groupby.length};
// 					aggr.cursub = cursub;
// 					aggr.subs.push(cursub);
// 					allsubs.push(cursub);
// 				}else{
// 					cursub = aggr.cursub;
// 					cursub.endIndex = index;
// 				}
// 				cursub.items.push(item);
// 			}
// 		}
// 		var totals = [], sub;
// 		for(i = 0, len = allsubs.length; i < len; i+=1){
// 			sub = allsubs[i];
// 			totals.push(calcSub(sub));
// 		}
// 		return totals;
// 	}
// 	return makeSubtotals();
// }

// // 피봇 만들기
// function Pivot(s){
// 	var list = s.list;
// 	var rowfields = s.rowfields;
// 	var colfields = s.colfields;
// 	var sumfields = s.sumfields;
// 	var colprefix = s.colprefix || '$';
// 	var coldelim = s.coldelim || '.';
// 	var initFuncBySFs = s.initFuncBySFs;
// 	var calcFuncBySFs = s.calcFuncBySFs;
// 	function make(){
// 		var olist = [], oit, changed, temp, colfieldsarr = [];
// 		var pivotcolfieldMap = {}, pivotcolfield;
// 		var i, len, it, i2, len2, sf;
// 		for(i = 0, len = list.length; i < len; i+=1){
// 			it = list[i];
// 			oit = _.find(olist, function(oit){
// 				var it2 = _.map(rowfields, function(field){
// 					return it[field];
// 				});
// 				var oit2 = _.map(rowfields, function(field){
// 					return oit[field];
// 				});
// 				return _.isEqual(it2, oit2);
// 			});
// 			if(oit == null){
// 				oit = _createNewMapByKeys(rowfields, it);
// 				if(initFuncBySFs){
// 					for(i2 = 0, len2 = sumfields.length; i2 < len2; i2+=1){
// 						sf = sumfields[i2];
// 						initFuncBySFs(sf, oit, it);
// 					}
// 				}
// 				olist.push(oit);
// 			}
// 			for(i2 = 0, len2 = sumfields.length; i2 < len2; i2+=1){
// 				sf = sumfields[i2];
// 				temp = _.map(colfields, function(cf){
// 					return it[cf];
// 				});
// 				if(sumfields.length > 1) temp.push(sf);
// 				pivotcolfield = colprefix + temp.join(coldelim);
// 				if(! pivotcolfieldMap[pivotcolfield]){
// 					colfieldsarr.push(temp);
// 					pivotcolfieldMap[pivotcolfield] = 1;
// 				}
// 				oit[pivotcolfield] = it[sf];
// 				if(calcFuncBySFs){
// 					calcFuncBySFs(sf, oit, it);
// 				}
// 			}
// 		}
// 		var arrayItemSortFunc = function(a, b){
// 			var cp;
// 			for(var i = 0, l = a.length; i < l; i++){
// 				cp = a[i] == b[i] ? 0 : a[i] > b[i] ? 1 : -1;
// 				if(cp != 0){
// 					return cp;
// 				}
// 			}
// 			return 0;
// 		};
// 		colfieldsarr.sort(arrayItemSortFunc);
// 		var pivotcolfields = _.map(colfieldsarr, function(it){
// 			return colprefix + it.join(coldelim);
// 		});
// 		return {
// 			//pivotcolfields: getMapKeys(pivotcolfieldMap).sort(),
// 			pivotcolfields: pivotcolfields,
// 			list: olist
// 		};
// 	}
// 	$.extend(this, {
// 		make: make
// 	});
// }

function makeSumArray(s){
	var calcCols = s.calcCols;
	s = $.extend({
		init: function(){
			var o = {};
			var i, len, c;
			for(i = 0, len = calcCols.length; i < len; i+=1){
				c = calcCols[i];
				o[c.field] = 0;
			}
			if(s.init2){
				s.init2(o);
			}
			return o;
		},
		calc: function(total, item){
			var i, len, c;
			for(i = 0, len = calcCols.length; i < len; i+=1){
				c = calcCols[i];
				if(c.calc == 'sum'){
					total[c.field] += item[c.field] || 0;
				}else if(c.calc == 'avg'){
					total[c.field] += item[c.field] || 0;
				}else if(c.calc == 'max'){
					total[c.field] = Math.max(total[c.field], item[c.field] || 0);
				}else if(c.calc == 'min'){
					total[c.field] = Math.min(total[c.field], item[c.field] || 0);
				}else if($.isFunction(c.calc)){
					c.calc(total, item);
				}else{
					total[c.field] = item[c.field];
				}
			}
			if(s.calc2){
				s.calc2(total, item)
			}
		},
		done: function(total, count){
			var i, len, c;
			for(i = 0, len = calcCols.length; i < len; i+=1){
				c = calcCols[i];
				if(c.calc == 'avg'){
					total[c.field] = total[c.field] / count;
				}else if($.isFunction(c.done)){
					c.done(total, count);
				}
			}
			if(s.done2){
				s.done2(total, item)
			}
		}
	}, s);
	var sumArray = Summary(s);
	return sumArray;
}

var DEFAULT_AUTOCOMPLETE_SETTINS = {
	minLength: 1,
	focus: function(event, ui){return false;},
	position: {my: "left top", at: "left bottom", collision: "flip"}
};

var DEFAULT_AUTOCOMPLETE_RENDER_ITEM_FUNCTION = function(ul, item){
	return $("<li>").append( $("<div>").text(item.label || item.name || 'None')).appendTo( ul );
};

function setInputAutocomplete(el, s){
	var acop1 = $.extend({}, DEFAULT_AUTOCOMPLETE_SETTINS, s);
	var $el = $(el);
	$el.autocomplete(acop1).on('dblclick', function(){
		var self = this;
		$(self).autocomplete('search');
	});
	$el.data("ui-autocomplete")._renderItem = (s._renderItem || DEFAULT_AUTOCOMPLETE_RENDER_ITEM_FUNCTION);
}

// autocomplete 소스 만들기
function makeAutocompleteSource(s){
	var cache = s.cache || {};
	return function(request, response){
		var term = request.term;
		if(term in cache){
			response(cache[term]);
			return;
		}
		s.param = $.extend({}, s.param);
		s.param[s.termKey || 'keyword'] = term;
		ajax($.extend({
			isNoProgress: true,
			noLoseFocus: true,
			onSuccess: function(data, status, xhr){
				if(s.nameFunc){
					s.makeArrayFunc = function(data){
						var list1 = data.list1;
						_.each(list1, function(it){
							it.name = s.nameFunc(it);
						});
						return list1;
					};
				}
				var array = s.makeArrayFunc(data);
				array = [{code: '', name: '', label: ''}].concat(array);// pyk code
				var array100 = array.slice(0, 100);
				cache[term] = array100;
				response(array100);
			},
			onError: function(){
				response([]);
			}
		}, s));
	}
}

// 필드 포매팅
function formatField(e){
	var ff = _getFieldFormatFunc(e);
	if(ff){$(e).val(ff($(e).val()));}
}
function unformatField(e){
	var ff = _getFieldUnformatFunc(e);
	if(ff){$(e).val(ff($(e).val()));}
}
// input 필드에 필수 포맷 등 check 추가
function getSetFieldData(e){
	//<input type="text" name="xxx" class="required format_ymd" data-max="2" data-dispname="xxx" />
	var $e = $(e);
	var edata = $e.data();
	// check 적용되있으면 return;
	if(edata._checkapplied){
		return edata;
	}
	var formatList = ['year', 'ym', 'ymd', 'hm', 'hms', 'ymdhm', 'ymdhms', 'number', 'postno', 'ssn', 'bizno', 'nummask', 'phone', 'cellphone', 'email'];
	for(var i = 0, len = formatList.length; i < len; i++){
		var format = formatList[i];
		if($e.hasClass('format_'+format)){
			edata.format = format;
			break;
		}
	}
	var isTextField = $e.is('input:text') || $e.is('textarea');
	if(isTextField){
		var maxlength, size, textAlignClass, datapickFormat;
					if(edata.format == 'year'  ){maxlength = 4;   size = maxlength + 2;	textAlignClass = 'center';datapickFormat = 'year';
		}else if(edata.format == 'ym'    ){maxlength = 7;   size = maxlength + 2;	textAlignClass = 'center';datapickFormat = 'ym';
		}else if(edata.format == 'ymd'   ){maxlength = 10;  size = maxlength + 2;	textAlignClass = 'center';datapickFormat = 'ymd';
		}else if(edata.format == 'hm'    ){maxlength = 5;   size = maxlength + 2;	textAlignClass = 'center';
		}else if(edata.format == 'hms'   ){maxlength = 8;   size = maxlength + 2;	textAlignClass = 'center';
		}else if(edata.format == 'ymdhm' ){maxlength = 16;  size = maxlength + 2;	textAlignClass = 'center';
		}else if(edata.format == 'ymdhms'){maxlength = 19;  size = maxlength + 2;	textAlignClass = 'center';
		}else if(edata.format == 'postno'){maxlength = 7;   size = maxlength + 2;	textAlignClass = 'center';
		}else if(edata.format == 'ssn'   ){maxlength = 14;  size = maxlength + 2;	textAlignClass = 'center';
		}else if(edata.format == 'bizno' ){maxlength = 12;  size = maxlength + 2;	textAlignClass = 'center';
		}else if(edata.format == 'phone' ){maxlength = 14;  size = maxlength + 2;	textAlignClass = 'center';
		}else if(edata.format == 'number'){maxlength = 20;                        textAlignClass = 'right';
		}
		if(! $e.attr('maxlength') && maxlength){
			$e.attr('maxlength', maxlength);
		}
		if(! $e.attr('size') && size){
			$e.attr('size', size);
		}
		if(datapickFormat){
			setDatepicker($e, datapickFormat);
		}
		// maxlength 없을 경우 50 으로 해서 maxlength 를 꼭 입력하게끔 하자
		if(! $e.attr('maxlength')){
			var maxlength = $e.is('textarea') ? 20000 : $e.hasClass('uu-field') ? 100 : 1000;
			$e.attr('maxlength', maxlength);
		}
	}
	edata._checkapplied = true;
	return edata;
}
// 그리드 셀 콤보 등에서 사용하기 위한 클래스
function Coder(array){
	var array = _makeCodeList(array);
	var self = this;
	var itemByValue = {};
	var itemByText = {};
	function init(){
		var item;
		for(var i = 0, len = array.length; i < len; i++){
			item = array[i];
			item.label = item.name+'('+item.code+')';// pyk code
			itemByValue[item.code] = item;// pyk code
			itemByText[item.name] = item;// pyk code
		}
	}
	$.extend(self, {
		itemByValue: itemByValue,
		itemByText: itemByText,
		array: array
	});
	init();
}
// 배열 아이템에 코드성 ui 에서 사용할 value, text 추가
function _makeCodeList(array, vk, tk){
	vk = vk || 'code';
	tk = tk || 'name';
	return _.map(array, function(it){
		it = $.extend({}, it);
		it.code = it[vk];
		it.name = it[tk];
		return it;
	});
}
// 그룹 트리 만들기. array 반드시 정렬 후 사용
function makeLevelArrayByGroup(array, ops){
	var ks = _mustArray(ops.ks), it, pitem, pMakeLevel, item;
	var getnamefunc = ops.getnamefunc || function(a, l, k){return a[k];}
	var hirachy = [{level: 1, name: 'root'}];
	//var hirachy = [];
	for(var i = 0, len = array.length; i < len; i++){
		item = array[i];
		if(ks.length){
			for(var k = 0, klen = ks.length; k < klen; k++){
				if(pitem == null || k > pMakeLevel || item[ks[k]] != pitem[ks[k]]){
					it = {level: k + 2, name: getnamefunc(item, k, ks[k])};
					if(k == klen - 1){
						it = $.extend(it, item);
					}
					hirachy.push(it);
					pMakeLevel = k;
				}
			}
		}else{
			hirachy.push($.extend({level: 2, name: 'sub'}, item));
		}
		pitem = item;
	}
	return hirachy;
}

function gridResizeCanvasWithin(parent){
	if(! window.SlickUtil)return;
	if(gridResizeCanvasWithin.debounce == null){
		gridResizeCanvasWithin.debounce = _.debounce(function(){
			SlickUtil.resizeCanvasWithin(document.body);
		}, 100);
	}
	parent = parent || document.body;
	if(parent == document.body){
		gridResizeCanvasWithin.debounce();
	}else{
		SlickUtil.resizeCanvasWithin(parent);
	}
}

// hirachy 구조에서 item 마다 함수 실행
function eachArrayChildren(array, func, childrenKey){
	childrenKey = childrenKey || 'children';
	_.each(array, function(it, i){
		func(it, i);
		var children = it[childrenKey];
		if(children && children.length){
			eachArrayChildren(children, func, childrenKey);
		}
	});
}

// // guid 생성
// function guid(delim){
// 	function s4(){
// 		return Math.floor((1 + Math.random()) * 0x10000)
// 			.toString(16)
// 			.substring(1);
// 	}
// 	delim = delim || '-';
// 	return s4() + s4() + delim + s4() + delim + s4() + delim +
// 		s4() + delim + s4() + s4() + s4();
// }

// jquery ui dialog 이용한 alert
var _getAlertTmplFn = function(){
	var _method = _getAlertTmplFn;
	return _method.tmplFn ? _method.tmplFn : _method.tmplFn = (function(){
		return _.template('<div class="uu-box-dialog-alert {%- it.styleClass %}"> {%= it.message %} {% if(it.tipMessage){ %}<br/><br/><span style="font-size: 0.9rem;">{%- it.tipMessage %}</span> {% } %}</div>');
	})();
};
var showAlert = function(title, message, closeHandler, autoCloseMillis, is_html, styleClass){
	// var _method = showAlert;
	// _method.timeoutid = null;
	// var $div1 = _method.$div1 ? _method.$div1 : _method.$div1 = $('<div></div>');
	if($.isPlainObject(title)){
		var s = title;
		message = s.message;
		closeHandler = s.closeHandler;
		autoCloseMillis = s.autoCloseMillis;
		is_html = s.is_html;
		styleClass = s.styleClass;
	}
	var tipMessage = '';
	if(autoCloseMillis != null){
		tipMessage = isKo ? '(자동으로 닫힙니다.)' : '(Closes automatically)';
	}
	// if(_method.timeoutid){
	// 	clearTimeout(_method.timeoutid);
	// 	_method.timeoutid = null;
	// }
	if(is_html){
		null;
	}else{
		message = escapeTextHtml(message);
	}
	var tmplFn1 = _getAlertTmplFn();
	var $div1 = $('<div></div>');
	$div1.empty().append(makeTmplNodeList(tmplFn1, {message: message, tipMessage: tipMessage, styleClass: styleClass}));
	var timeoutid = null;
	openDialog($div1, {
		// minWidth: 400,
		maxWidth: 1000,
		// minHeight: 200,
		maxHeight: 600,
		width: 'auto',
		height: 'auto',
		modal: true,
		resize: 'auto',
		//dialogClass: 'ui-dialog-noheader',
		dialogClass: 'uu-dialog ui-dialog-alert',
		title: title || (isKo ? '알림' : 'Message'),
		noCloseFocus: true,
		isRemoveWhenClose: true,
		onOpen: function(event, ui){
			var node = this;
			setTimeout(function(){
				$(node).closest('.ui-dialog').find('.ui-dialog-buttonset button:eq(0)').focus();
			}, 10);
		},
		//hide: 'blind',
		onClose: function(){
			if(timeoutid){
				clearTimeout(timeoutid);
				timeoutid = null;
			}
			if(closeHandler){
				setTimeout(closeHandler);
			}
		},
		buttons: [{
			html: '<i class="fa fa-times"></i> 닫기',
			'class': 'uu-btn',
			click: function(){
				$(this).dialog('close');
			}
		}],
		position: {
			my: 'center',
			at: 'center',
			of: window
		}
	});
	if(autoCloseMillis){
		timeoutid = setTimeout(function(){
			closeDialog($div1);
		}, autoCloseMillis);
	}
};
//jquery ui dialog 이용한 confirm
var showConfirm = function(title, message, okHandler, noHandler, is_html, styleClass){
	//var _method = showConfirm;
	//var $div1 = _method.$div1 ? _method.$div1 : _method.$div1 = $('<div></div>');
	if($.isPlainObject(title)){
		var s = title;
		message = s.message;
		okHandler = s.okHandler;
		noHandler = s.noHandler;
		is_html = s.is_html;
		styleClass = s.styleClass;
	}
	if(! message){
		setTimeout(function(){okHandler(true);});
		return;
	}
	if(is_html){
		null;
	}else{
		message = escapeTextHtml(message);
	}
	var tmplFn1 = _getAlertTmplFn();
	var $div1 = $('<div></div>');
	$div1.empty().append(makeTmplNodeList(tmplFn1, {message: message, styleClass: styleClass}));
	var isYes = false;
	openDialog($div1, {
		// minWidth: 400,
		maxWidth: 1000,
		// minHeight: 200,
		maxHeight: 600,
		width: 'auto',
		height: 'auto',
		modal: true,
		dialogClass: 'uu-dialog ui-dialog-confirm',
		title: title || (isKo ? '확인' : 'Confirm'),
		noCloseFocus: true,
		isRemoveWhenClose: true,
		onOpen: function(event, ui){
			var node = this;
			setTimeout(function(){
				$(node).closest('.ui-dialog').find('.ui-dialog-buttonset button:eq(0)').focus();
			}, 10);
		},
		//hide: 'blind',
		onClose: function(){
			setTimeout(function(){
				if(isYes){
					okHandler && okHandler(isYes);
				}else{
					noHandler && noHandler(isYes);
				}
			});
		},
		buttons: [{
			html: '<i class="fa fa-check"></i> 확인',
			'class': 'uu-btn',
			click: function(){
				isYes = true;
				$(this).dialog('close');
			}
		}, {
			html: '<i class="fa fa-times"></i> 취소',
			'class': 'uu-btn',
			click: function(){
				isYes = false;
				$(this).dialog('close');
			}
		}],
		position: {
			my: 'center',
			at: 'center',
			of: window
		}
	});
};

// // 2015-07-07 추가
// function validatePlusInteger(val){
// 	val = Number(val);
// 	var valid = val > 0 && val == Math.round(val);
// 	var message;
// 	if(! valid)message = "0 보다 큰 정수";
// 	return {valid: valid, message: message};
// }
// 2015-07-17
function setDatepicker(node, format, s){
	s = s || {};
	var is_slickgrid = !! s.is_slickgrid;
	// format: 'year', 'ym', 'ymd'
	// bootstrap 우선
	var $node = $(node);
	if($node.bs_datepicker){
//	if(false){
		var o = {
			forceParse: false,// 이거 반드시 필요
			//immediateUpdates: true,
			enableOnReadonly: false,
			autoclose: true,
			//todayBtn: true,
			//clearBtn: true,
			todayHighlight: true
		};
		if(is_slickgrid){
			// - 같은 구분자 없으면 키보드 입력시 바로 처리 안되네
			o.format = format == 'year' ? 'yyyy' : format == 'ym' ? 'yyyymm' : 'yyyymmdd';
		}else{
			o.format = format == 'year' ? 'yyyy' : format == 'ym' ? 'yyyy-mm' : 'yyyy-mm-dd';
		}
		if(format == 'year'){
			o.viewMode = o.minViewMode = 'years';
		}else if(format == 'ym'){
			o.viewMode = o.minViewMode = 'months';
		}
		o = $.extend(o, s);
		if(is_slickgrid){
			o.keyboardNavigation = false;
			$node.bs_datepicker(o);
			if(s.value){
				$node.bs_datepicker('setDate', dateut.fromYmd(s.value+'0101'));
			}
			$node.bs_datepicker('show');
		}else{
			$node.bs_datepicker(o);
		}
	}else if($node.datepicker){
		if(format == 'ymd'){
			if(is_slickgrid){
				s.dateFormat = 'yymmdd';
			}else{
				s.dateFormat = 'yy-mm-dd';
			}
			$node.datepicker(s);
			if(is_slickgrid){
				$node.datepicker('show');
			}
		}
	}
}
function destroyDatepicker(node){
	var $node = $(node);
	// 순서 중요
	if($node.hasClass('hasDatepicker')){
		$node.datepicker('hide');
		$node.datepicker('destroy');
	}else if($node.data('datepicker') && $node.bs_datepicker){
		$node.bs_datepicker('hide');
		$node.bs_datepicker('destroy');
	}
}
// 그리드 셀 클릭시 다른 곳에 있는 datepicker 안사라지는 문제 때문에
var debounceHideDatepickerAll = (function(){
	return _.debounce(function(){
		$.fn.bs_datepicker && $(':data(datepicker)').bs_datepicker('hide');// bootstrap 용
		$.fn.datepicker && $('.hasDatepicker').datepicker('hide');// jquery ui 용
	}, 100);
})();
/////////////////////////// div_app /////////////////////////
//url 로드하고 div_app 에 추가(없으면 hidden new div 에 추가)
var loadPage = function(s){
	var action = s.action;
	var target = s.target;
	var callback = s.callback;
	ajax({
		dataType: 'html',
		headers: {format: 'json'},
		action: action,
		form: s.form,
		method: s.method || 'get',
		param: s.param,
		onSuccess: function(htmlText){
			// loadPage 를 동시에 여러개 호출할 수 있으므로 변수값이 바뀌지 않게 window.tempInitDivApp 를 유니크하게 replace 하자
			var uid = _.uniqueId();
			var tempKey1 = 'tempInitDivApp_'+uid;
			htmlText = htmlText.replace('window.tempInitDivApp', 'window.'+tempKey1);
			var $div_app = $('<div class="div_app" style="display: none;" />').append(htmlText);
			if(target){
				DivApp.emptyTarget(target);
				$(target).append($div_app);
				$div_app.show();
			}else{
				$(document.body).append($div_app);
			}
			var div_app = $div_app[0];
			initNewHtml(div_app);
			if(window[tempKey1]){
				var initDivApp = window[tempKey1];
				delete window[tempKey1];
				initDivApp(div_app);
			}
			if(callback){
				callback({
					div_app: div_app,
					div_app_methods: $div_app.data('_div_app_methods')
				});
			}
			// var temp1, temp2;
			// //parseHTML 해버리면 <script> 태그 없어지더라. temp1 = $('<div>').append($.parseHTML(htmlText));
			// temp1 = $('<div>').append(htmlText);
			// $('script:not([src])', temp1).remove();
			// $('meta[charset]', temp1).remove();
			// var htmlText = temp1.html();// script 제거된
			// temp1.remove();
			// // template script 필요하므로 script[type="text/javascript"] 로 제한해야
			// temp1 = $('<div>').append(htmlText);
			// var temp2 = temp1.find('script:not([src]):first');
			// // ie8 에서 script 가져올때 text() 하니 안되더라
			// var scriptText = temp2.text() || temp2.html();
			// temp1.remove();
			// handleHtmlScript(htmlText, scriptText);
		}
	});
};

var loadDialog = function(s1){
	loadPage({
		param: s1.param,
		action: s1.action,
		callback: function(s2){
			openDialog(
				s2.div_app,
				_.extend({isRemoveWhenClose: true}, s1)
			);
		}
	});
};

/////////////////////
function tinyFlashMessage(ops1){
	function _removeTinyFlashMessage(ops2){
		var $of = $(ops2.of);
		if($of.length){
			var tinyflashMessageNode = $of.data('_tinyflashMessageNode');
			if(tinyflashMessageNode){
				$(tinyflashMessageNode).remove();
				$of.data('_tinyflashMessageNode', null);
			}
		}
	}
	var options = $.extend({
		message: 'No message',
		of: null,
		my: 'left top',
		at: 'left bottom',
		delay: 1000,
		collision: 'flipfit'
	}, ops1);
	var $msg = $('<div class="uu-text-flash uu-shadow px-2" style="position: absolute;display: inline-block;" ></div>');
	if(ops1.cssClass){
		$msg.addClass(ops1.cssClass);
	}
	if(ops1.css){
		$msg.css(ops1.css);
	}
	var $of = $(options.of);
	_removeTinyFlashMessage(ops1);
	$of.data('_tinyflashMessageNode', $msg[0]);
	var chain1 = $msg.
		text(options.message).hide().
		appendTo(document.body);
	var isNoFade = !! ops1.isNoFade;
	if(isNoFade){
		chain1 = chain1.show();
	}else{
		chain1 = chain1.fadeIn('fast');
	}
	chain1 = chain1.position({
		my: options.my,
		at: options.at,
		of: $of,
		collision: options.collision
	}).delay(options.delay);
	var removeFn = function(){
		_removeTinyFlashMessage(ops1);
	};
	if(isNoFade){
		chain1 = chain1.hide(0, removeFn);
	}else{
		chain1 = chain1.fadeOut('normal', removeFn);
	}
}
function getCodeName(codeList, code){
	if(code == null){
		code = '';
	}
	var it = _.find(codeList, {code: code});// pyk code
	return it != null ? it.name : '';// pyk code
}

// function findGetValue(list, searchItem, getKeyName){
// 	return _.get(_.find(list, searchItem), getKeyName);
// }

function getCodeName2(coder, code){
	if(code == null){
		code = '';
	}
	var item = coder.itemByValue[code];
	if(item){
		return item.name || '';// pyk code
	}
	return '';
}

function checkAlert(checkValue, message, callback){
	if(! checkValue){
		showAlert(null, message, callback);
		return false;
	}else{
		return true;
	}
}
function checkFlash(checkValue, message, op){
	if(! checkValue){
		flashMessageWarn(message, op);
		return false;
	}else{
		return true;
	}
}

function downloadFile(ops1){
	if(_checkFormsForAjax(ops1)!==true){return;}
	// var url = actionToUrl(ops1.action);
	var url = ops1.url || ops1.action;
	// 에러 응답 json 처리 위해
	ops1.param = $.extend({format: 'json'}, ops1.param);
	var csrfInfo = _getCsrfInfo();
	if(csrfInfo.token){
		ops1.param[csrfInfo.parameterName] = csrfInfo.token;
	}
	var paramArray = _makeOptionParamArray(ops1);
	_loseCurrentFocus();
	$.fileDownload(url, {
		//preparingMessageHtml: 'Please wait...',
		httpMethod: 'POST',
		data: paramArray,
		// not working cookie ?? successCallback:
		failCallback: function(responseHtml, url){
			var data, jsonStr;
			if(_.startsWith(responseHtml, '<') && responseHtml.indexOf('{') > 0){
				jsonStr = $(responseHtml).text();
			}else if(_.startsWith(responseHtml, '{')){
				jsonStr = responseHtml;
			}else{
				showAlert(null, responseHtml);
				return;
			}
			if(jsonStr){
				data = JSON.parse(jsonStr);
			}
			if(data && data.errorMessage){
				showAlert(null, data.errorMessage);
			}else{
				showAlert(null, responseHtml);
			}
			if(ops1.onError)ops1.onError(responseHtml, url);
		}
	});
}

// function convertNumToHan(num){
// 	if(! num)return '';
// 	var money = Math.round(forceNumber(num)).toString();
// 	var han1 = ['', '일', '이', '삼', '사', '오', '육', '칠', '팔', '구'];
// 	var han2 = ['', '십', '백', '천'];
// 	var han3 = ['', '만', '억', '조', '경', '해'];
// 	var result = '';
// 	var len = money.length;
// 	for(var i = len - 1; i >= 0; i--){
// 		var dan = han1[+(money.substring(len - i - 1, len - i))];
// 		result += dan;
// 		if(dan){
// 			result += han2[i % 4];
// 		}
// 		if(i % 4 == 0){
// 			var temp = han3[i / 4];
// 			if(temp){
// 				result += '('+temp+')' + ' ';
// 			}
// 		}
// 	}
// 	return result;
// }

var _getValueArrayByKs = function(obj, ks){
	var arr = [];
	return _.map(ks, function(k){
		return obj[k];
	});
};

function hasDupByKeys(arr, ks){
	if(arr == null || arr.length == 0)return false;
	var uniq_arr = _.uniq(_.map(arr, function(it){
		return JSON.stringify(_getValueArrayByKs(it, ks));
	}));
	return arr.length != uniq_arr.length;
}

// function join2(arr, d, s, e){
// 	if(s == null)s = '';
// 	if(e == null)e = '';
// 	if(d == null)d = ',';
// 	return _(arr).map(function(it){
// 		if(it == null)it = '';
// 		return s + it + e;
// 	}).join(d);
// }

function fieldAddDate(p, e, add){
	if(! checkField(p, e))return;
	var val = gev(p, e);
	var date1 = dateut.fromYmd(val);
	var val_len = val.length;
	if(val_len == 4){
		dateut.addYear(date1, add);
	}else if(val_len == 6){
		dateut.addMonth(date1, add);
	}else{
		dateut.addDate(date1, add);
	}
	sev(p, e, dateut.getYmd(date1).substr(0, val_len));
}

function getKeysFromList(list1, omitNodataColumn){
	var ks = [], km = {};
	for(var i = 0, len = list1.length; i < len; i++){
		var item = list1[i];
		for(var k in item){
			if(km[k] == null){
				if(omitNodataColumn && ! item[k]){
					continue;
				}
				ks.push(k);
				km[k] = 1;
			}
		}
	}
	return ks;
}

function getPct(a, b){
	return +Big(getRatio(a, b)||0).times(100);
}
function getRatio(a, b){
	if(! b)return null;
	return +Big(a||0).div(b);
}

// function disableBackspaceForUserMistake(){
// 	$(document).keydown(function(e){
// 		if(e.keyCode == 8){
// 			var is_ok = e.target.tagName == 'INPUT' || e.target.tagName == 'TEXTAREA';
// 			if(is_ok){
// 				null;
// 			}else{
// 				e.preventDefault();
// 				//Blocking backspace due to user error
// 				tinyFlashMessage({of: document.body, message: '사용자 실수로 인한 백스페이스 차단중입니다.', delay: 1000});
// 			}
// 		}
// 	});
// }

// function blink(s){
// 	var count = s.count || 10;
// 	var toggleClass = s.toggleClass || 'uu-flashing';
// 	$('.blink', s.parent).each(function(){
// 		var el = this, $el = $(el);
// 		for(var i = 0, len = count; i < len; i++){
// 			$el.toggleClass(toggleClass, 500);
// 			$el.toggleClass(toggleClass, 500);
// 		}
// 	});
// }

// function blink2(s){
// 	var count = s.count || 2;
// 	var toggleClass = s.toggleClass || 'uu-flashing';
// 	for(var i = 0, len = count; i < len; i++){
// 		$(s.search).toggleClass(toggleClass, 500);
// 		$(s.search).toggleClass(toggleClass, 500);
// 	}
// }

// // 값는 있는 첫번째 콤보값 selected
// function setComboValueFirst(parent, e){
// 	var combo = ge(parent, e);
// 	var options = $('option', combo);
// 	for(var i = 0, len = options.length; i < len; i++){
// 		if(options[i].value){
// 			options[i].selected = true;
// 			break;
// 		}
// 	}
// }

// 콤보변경시 자동 조회
function setComboAutoSearchEvent(parent){
	$('select.auto-search[name]', parent).on('change', function(){
		var self = this;
		var form = $(self).closest('form')[0];
		if(form && $('div.buttons button[data-action]', form).length == 1){
			$('div.buttons button[data-action]', form).trigger('click');
		}
	});
}

// var makeItemTrHtml = function(it){
// 	var tmplFn1 = _.template('<tr><th>{%- it.key %}</th><td>{%- it.val %}</td></tr>');
// 	var list1 = [];
// 	for(var key in it){
// 		var val = it[key];
// 		list1.push({key: key, val: val});
// 	}
// 	return _.map(list1, function(it){
// 		return tmplFn1(it);
// 	}).join('');
// };

function checkMobile(){
	var check = false;
	(function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino|android|ipad|playbook|silk/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4))) check = true;})(navigator.userAgent||navigator.vendor||window.opera);
	return check;
}
//for big.js xx.round(dp, rm);

// lodash templdate
function makeTmplNodeList(tmplFn, listOrMap){
	if(typeof tmplFn == 'string' || tmplFn.html){
		tmplFn = makeLTFn(tmplFn);
	}
	var result = [];
	var list1 = _.isArray(listOrMap) ? listOrMap : [listOrMap];
	_.each(list1, function(it){
		var $node = $(tmplFn(it));
		$node.data('tmplNodeItem', it);
		result.push($node);
	});
	return result;
}
var makeLTFn = function(strOr, s){
	if(strOr.html){
		strOr = strOr.html();
	}
	return _.template(strOr, s);
};
function getTmplNodeData(el){
	return $(el).closest('.tnode').data('tmplNodeItem');
}

// function showOrHideBySelector(parent, selector, isShow){
// 	$(selector, parent)[isShow ? 'show' : 'hide']();
// 	gridResizeCanvasWithin(parent);
// }

function enableElemBySelector(parent, selector, isEnable){
	$(selector, parent).each(function(){enableElem(null, this, isEnable)});
}

// function setCookie(cname, cvalue, exdays){
// 	var d = new Date();
// 	d.setTime(d.getTime() + (exdays*24*60*60*1000));
// 	var expires = "expires="+ d.toUTCString();
// 	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
// }

// function getCookie(cname){
// 	var name = cname + "=";
// 	var decodedCookie = decodeURIComponent(document.cookie);
// 	var ca = decodedCookie.split(';');
// 	for(var i = 0; i <ca.length; i++){
// 		var c = ca[i];
// 		while (c.charAt(0) == ' '){
// 			c = c.substring(1);
// 		}
// 		if(c.indexOf(name) == 0){
// 			return c.substring(name.length, c.length);
// 		}
// 	}
// 	return "";
// }

function randomString(length, chars){
	if(! chars)chars = 'a#';
	var mask = '';
	if(chars.indexOf('a') > -1) mask += 'abcdefghijklmnopqrstuvwxyz';
	if(chars.indexOf('A') > -1) mask += 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
	if(chars.indexOf('#') > -1) mask += '0123456789';
	if(chars.indexOf('!') > -1) mask += '~`!@#$%^&*()_+-={}[]:";\'<>?,./|\\';
	var result = '';
	for(var i = length; i > 0; --i) result += mask[_.random(mask.length - 1)];
	return result;
}

// function replaceYmdTool(){
// 	$('.replaceymdtool[data-ymd1nm]').each(function(){
// 		var el = this, $el = $(el), ymd1nm = ifnull($el.attr('data-ymd1nm')), ymd2nm = ifnull($el.attr('data-ymd2nm'));
// 		$el.replaceWith('\
// 			<div class="uu-tooltip">\
// 				<span class="uu-text-link">설정</span>\
// 				<div class="uu-tooltip-content uu-nowrap" style="" data-ymd1nm="'+ymd1nm+'" data-ymd2nm="'+ymd2nm+'" >\
// 			<span class="uu-btn py-0 ytbtn">오늘</span> <span class="uu-btn py-0 ytbtn">-일</span> <span class="uu-btn py-0 ytbtn">+일</span> <span class="uu-btn py-0 ytbtn">현재월</span><br/>\
// 			<span class="uu-btn py-0 ytbtn">3개월</span> <span class="uu-btn py-0 ytbtn">6개월</span> <span class="uu-btn py-0 ytbtn">9개월</span> <span class="uu-btn py-0 ytbtn">12개월</span><br/>\
// 			<span class="uu-btn py-0 ytbtn">월초</span> <span class="uu-btn py-0 ytbtn">월말</span> <span class="uu-btn py-0 ytbtn">-월</span> <span class="uu-btn py-0 ytbtn">+월</span><br/>\
// 			<span class="uu-btn py-0 ytbtn">연초</span> <span class="uu-btn py-0 ytbtn">연말</span> <span class="uu-btn py-0 ytbtn">-년</span> <span class="uu-btn py-0 ytbtn">+년</span><br/>\
// 			<span class="uu-btn py-0 ytbtn">연초~오늘</span><br/>\
// 				</div>\
// 			</div>'
// 		);
// 	});
// 	if(! window.__ymdtooleventset__){
// 		window.__ymdtooleventset__ = true;
// 		$(document.body).on('click', '.ytbtn', function(){
// 			var el = this, $el = $(el), text1 = $el.text(),
// 				ymd1nm = $el.closest('[data-ymd1nm]').attr('data-ymd1nm'),
// 				ymd2nm = $el.closest('[data-ymd2nm]').attr('data-ymd2nm');
// 			var fm = $el.closest('form');
// 			var e1 = ge(fm, ymd1nm);
// 			var e2 = ymd2nm ? ge(fm, ymd2nm) : null;
// 			var ymd1 = gev(fm, ymd1nm);
// 			var ymd2 = e2 ? gev(fm, ymd2nm) : null;
// 			ymd1 = ymd1 && checkField(fm, ymd1nm, null, true) ? ymd1 : dateut.getYmd(new Date());
// 			ymd2 = ymd2 && checkField(fm, ymd2nm, null, true) ? ymd2 : dateut.getYmd(new Date());
// 			var dt1 = dateut.fromYmd(ymd1), dt2 = dateut.fromYmd(ymd2);
// 			if(text1 == '오늘'){
// 				dt1 = new Date();dt2 = new Date();
// 			}else if(text1 == '-일'){
// 				dateut.addDate(dt1, -1);dt2 = dt1;
// 			}else if(text1 == '+일'){
// 				dateut.addDate(dt1, 1);dt2 = dt1;
// 			}else if(text1 == '3개월'){
// 				dt2 = new Date();dt1 = new Date(dt2.getTime());dateut.addMonth(dt1, -3);
// 			}else if(text1 == '6개월'){
// 				dt2 = new Date();dt1 = new Date(dt2.getTime());dateut.addMonth(dt1, -6);
// 			}else if(text1 == '9개월'){
// 				dt2 = new Date();dt1 = new Date(dt2.getTime());dateut.addMonth(dt1, -9);
// 			}else if(text1 == '12개월'){
// 				dt2 = new Date();dt1 = new Date(dt2.getTime());dateut.addMonth(dt1, -12);
// 			}else if(text1 == '현재월'){
// 				dt1 = new Date();dt1 = dateut.getFirstDateOfMonth(dt1, 'date');dt2 = dateut.getLastDateOfMonth(dt1, 'date');
// 			}else if(text1 == '월초'){
// 				dt1 = dateut.getFirstDateOfMonth(dt1, 'date');dt2 = dt1;
// 			}else if(text1 == '월말'){
// 				dt1 = dateut.getLastDateOfMonth(dt1, 'date');dt2 = dt1;
// 			}else if(text1 == '-월'){
// 				if(e2){dt1.setDate(1);}
// 				dateut.addMonth(dt1, -1);dt2 = dateut.getLastDateOfMonth(dt1, 'date');
// 			}else if(text1 == '+월'){
// 				if(e2){dt1.setDate(1);}
// 				dateut.addMonth(dt1, 1);dt2 = dateut.getLastDateOfMonth(dt1, 'date');
// 			}else if(text1 == '연초'){
// 				dt1 = dateut.fromYmd(ymd1.substr(0, 4)+'0101');
// 				dt2 = dt1;
// 			}else if(text1 == '연말'){
// 				dt1 = dateut.fromYmd(ymd1.substr(0, 4)+'1231');
// 				dt2 = dt1;
// 			}else if(text1 == '-년'){
// 				if(e2){dt1.setMonth(0);dt1.setDate(1);}
// 				dateut.addYear(dt1, -1);dt2 = dateut.fromYmd(dateut.getYmd(dt1).substr(0, 4)+'1231');
// 			}else if(text1 == '+년'){
// 				if(e2){dt1.setMonth(0);dt1.setDate(1);}
// 				dateut.addYear(dt1, 1);dt2 = dateut.fromYmd(dateut.getYmd(dt1).substr(0, 4)+'1231');
// 			}else if(text1 == '연초~오늘'){
// 				dt1 = dateut.fromYmd(ymd1.substr(0, 4)+'0101');
// 				dt2 = new Date();
// 			}else return;
// 			var ymd1 = dateut.getYmd(dt1);
// 			var ymd2 = dateut.getYmd(dt2);
// 			sev(fm, ymd1nm, ymd1);
// 			if(e2)sev(fm, ymd2nm, ymd2);
// 		});
// 	}
// }

// var PRGR_INFO_TEXT_SEL = '.uu-overlay .uu-overlay__info';
var PRGR_BAR_INNER_SEL = '.uu-overlay .progress-bar';
var PRGR_TIME_TEXT_SEL = '.uu-overlay .uu-overlay__timetext';

// function inputTextPlusNum(p, e, num){
// 	sev(p, e, +gev(p, e)+num)
// }

// function setMultiCodeSelector(s){
// 	var checkboxName = s.checkboxName || 'check_'+s.rpstName;
// 	var codesName = s.codesName || 'array_'+s.rpstName;
// 	var namesName = s.namesName || 'array_name_'+s.rpstName;
// 	var selector = $(s.selector)[0];
// 	var $selector = $(selector);
// 	var cssClass1 = s.cssClass1 || 'uu-tooltip';
// 	var cssClass2 = s.cssClass2 || 'uu-tooltip-content';
// 	$selector.html('\
// 		<span class="'+cssClass1+'">\
// 			<input type="hidden" name="'+codesName+'"/>\
// 			<input type="text" class="uu-field uu-field-caret" name="'+namesName+'" readonly="readonly" style="width: '+Math.max($selector.width(), s.width||150)+'px;"/>\
// 			<span class="'+cssClass2+' uu-nowrap checkbox_cont"></span>\
// 		</span>\
// 	');
// 	var attr_title = $selector.attr('title');
// 	var tmplCheckbox = _.template('<label class="tnode uu-hover-black" style="display: block;"><input type="checkbox" name="'+checkboxName+'" value="{%- it.code %}"/>{%- it.name %}</label>');
// 	var nodeList = makeTmplNodeList(tmplCheckbox, s.codes);
// 	$('.checkbox_cont', selector).empty().append(nodeList);
// 	if(attr_title){
// 		sev(selector, namesName, '--'+attr_title+'--');
// 	}
// 	var setOIC = function(){
// 		var checkedItemList = _.reduce($('[name='+checkboxName+']:checked', selector), function(r, node){
// 			r.push(getTmplNodeData(node));
// 			return r;
// 		}, []);
// 		sev(selector, codesName, _.map(checkedItemList, 'code').join(','));
// 		var text = '';
// 		if(checkedItemList.length){
// 			text = checkedItemList[0].name;
// 			if(checkedItemList.length > 1){
// 				text += ' 외 '+(checkedItemList.length-1)+'건';
// 			}
// 		}else{
// 			if(attr_title){
// 				text = '--'+attr_title+'--';
// 			}
// 		}
// 		sev(selector, namesName, text);
// 	};
// 	$selector.on('click', ':checkbox[name='+checkboxName+']', setOIC);
// 	if(s.initVal){
// 		$(':checkbox[name='+checkboxName+'][value='+s.initVal+']', selector).trigger('click');
// 	}
// }

// var loadScripts = function(scripts){
// 	if(loadScripts.loaded == null)loadScripts.loaded = [];
// 	var loadables = _.filter(scripts, function(js){
// 		return !_.includes(loadScripts.loaded, js);
// 	});
// 	var _arr = _.map(loadables, function(js){
// 		return $.getScript(js).done(function(){
// 			if(! _.includes(loadScripts.loaded, js)){
// 				loadScripts.loaded.push(js);
// 			}
// 		});
// 	});
// 	_arr.push($.Deferred(function(deferred){
// 		deferred.resolve();
// 	}));
// 	return $.when.apply($, _arr);
// };

function flashMessage(message, s){
	s = $.extend({preserveMillis: 2000, target: window}, s);
	var target = $(s.of||s.target)[0];
	var mcEl = _.find($('.uu-mq-container'), function(it){
		return $(it).data('mq_target') == target;
	});
	// var isOnlyOne = s.isOnlyOne;
	var $mc;
	if(mcEl){
		$mc = $(mcEl);
	}else{
		$mc = $('<div class="uu-mq-container" style="position:'+(target == window ? 'fixed':'absolute')+';max-width: 500px;"/>').data('mq_target', target).appendTo(document.body);
	}
	if(message === false){
		$mc.remove();
	}
	if(! s.isHtml){
		message = escapeTextHtml(message);
	}
	// if(isOnlyOne){
	// 	$mc.empty();
	// }
	if(s.group){
		$('.uu-mq-message[data-group='+s.group+']', $mc).remove();
	}
	var $msg = $('<div class="uu-mq-message shadow py-2 px-3 '+(s.cssClass||'alert alert-success')+'" data-group="'+(s.group||'')+'" />').html(message).appendTo($mc);
	$mc.show();
	var defaultMy = 'center';
	var defaultAt = 'center';
	if(target.tagName && target.tagName.toLowerCase() == 'input'){
		defaultMy = 'left top';
		defaultAt = 'left bottom';
	}
	$mc.position({
		my: s.my || defaultMy,
		at: s.at || defaultAt,
		of: target,
		collision: 'flipfit'
	});
	setTimeout(function(){
		$msg.fadeOut("normal", function(){
			$msg.remove();
			if($mc.is(':empty')){
				$mc.remove();
			}
		});
	}, s.preserveMillis);

}
function flashMessageWarn(msg, s){
	flashMessage(msg, $.extend({cssClass: 'alert alert-warning'}, s));
}
var gridDestroyWithin = function(dCont){
	window.SlickUtil && SlickUtil.destroyGridWithin(dCont);
};
function openDialogGrid(dataArr, op){
	var _method = openDialogGrid;
	var dCont = $('<div/>');
	$(dCont).appendTo(document.body);// 안보이는 상태에서 생성된 그리드는 깨지더라
	var setGridData = function(){
		_.each(dataArr, function(m, n){
			var gCont = $('<div class="div_c"><div class="uu-text-title"></div><div class="slickgrid-resizer" style="height: 100px;"><div class="slickgrid" ></div></div></div>');
			dCont.append(gCont);
			$('.uu-text-title', gCont).text(m.name);
			$('.slickgrid-resizer', gCont).css('height', m.height || 150);
			var columns = (m.op && m.op.forceColumns) ? m.op.forceColumns : [
				$.extend({}, SlickColumn.itemnoImpl)
			];
			var grid = Slick.makeGrid(null, $('div.slickgrid', gCont), columns);
			SlickUtil.setItemsWithColumn(grid, m.list, m.op);
		});
		window.SlickUtil && SlickUtil.initGridResizer(dCont);
	};
	setGridData();
	openDialog(dCont, $.extend({
		title: '정보',
		width: 1000,
		isRemoveWhenClose: true
	}, op));
}

// // 산식에서 변수 추출
// function extractReVarArr(reCode, sansik){
// 	//var regex = /[A-Z][A-Z0-9]+/g;
// 	var match;
// 	var arr1 = [];
// 	while (match = reCode.exec(sansik)){
// 		arr1.push(match[0]);
// 	}
// 	return _.uniq(_.compact(arr1));
// }

// function plusToMapKey(map, k, v){
// 	map[k] = (map[k]||0)+(v||0);
// }

// function arrayPushIfNot(arr, val){
// 	if(_.includes(arr, val)){
// 		arr.push(val);
// 		return 1;
// 	}else return 0;
// }

// var countUp = (function(){
// 	var easings = ['linear', 'swing'];
// 	var gbs1 = 'Quad,Cubic,Quart,Quint,Expo,Sine,Circ,Bounce'.split(',');//,Elastic,Back
// 	var gbs2 = 'In,Out,InOut'.split(',');
// 	for(var i1 = 0, len1 = gbs1.length; i1 < len1; i1++){
// 		for(var i2 = 0, len2 = gbs2.length; i2 < len2; i2++){
// 			easings.push('ease'+gbs2[i2]+gbs1[i1]);
// 		}
// 	}
// 	return function(callback, duration, easing){
// 		return jQuery({Counter: 0}).animate({Counter: 100}, {
// 			duration: duration || 1000,
// 			easing: easing || easings[_.random(easings.length - 1)],
// 			step: function(){callback(this.Counter);},
// 			complete: function(){callback(100);}
// 		});
// 	};
// })();

// function drawGoogleChartWrapper(wp, callback){
// 	var drawChart1 = function(){
// 		drawGoogleChartWrapper._is_google_chart_ready = true;
// 		var wrapper1 = new google.visualization.ChartWrapper(wp);
// 		wrapper1.draw();
// 		if(callback){
// 			callback({wrapper: wrapper1});
// 		}
// 	};
// 	if(drawGoogleChartWrapper._is_google_chart_ready){
// 		drawChart1();
// 	}else{
// 		google.charts.load('current');
// 		google.charts.setOnLoadCallback(drawChart1);
// 	}
// };
/* 페이징 처리하는 함수 - start */
var makePager = function(rcpp, rcount, pageno){
	var pager = {
		rcpp: rcpp || 10,
		pageno: pageno || 0,
		rcount: rcount || 1,
		/*코드 주석 : 기존 코드 */
		// get s_num(){return this.rcpp * (this.pageno - 1) + 1;},
		// get e_num(){return this.rcpp * this.pageno;},
		get s_num(){return this.rcpp * this.pageno + 1;},
		get e_num(){return this.rcpp * (this.pageno + 1);},
		get spage(){return 1;},
		//get epage(){return Math.floor(((this.rcount - 1)/this.rcpp) + 1);},
		get epage(){return Math.floor((this.rcount - 1)/this.rcpp);},
		makePageList: function(list1){
			if(! (list1 && list1.length > 0))return [];
			return _.slice(list1, this.s_num - 1, this.e_num);
			//return _.slice(list1, this.s_num, this.e_num);
		},
		setPagination: function(el_page1, pageFunctionName, linkAttrText){
			var page_info = new _PaginationInfo({
				currentPageNo: this.pageno + 1, //0부터 시작을 1로 바꿈.
				recordCountPerPage: this.rcpp,
				totalRecordCount: this.rcount
				//totalRecordCount: 14 //totalCount는 서버에서 받아와야함.
			});
			setPagination(el_page1, page_info, pageFunctionName, linkAttrText);
		}
	};
	return pager;
};

// 페이지 뿌리기 위한 정보
function _PaginationInfo(s){
	//var _currentPageNo = 1;//현재 페이지 번호
	//var _recordCountPerPage = 10;//한 페이지에 보여지는 레코드 수
	//var _pageSize = 10;//페이지 리스트에 게시되는 페이지 건수
	//var _totalRecordCount;//전체 게시물 건 수
	var self = this;
	$.extend(self, {
		//currentPageNo: 1,
		recordCountPerPage: 10,
		pageSize: 5//,
		//totalRecordCount: ??
	}, s);
	function getTotalPageCount(){
		return Math.floor((self.totalRecordCount - 1)/self.recordCountPerPage) + 1;
	}
	function getFirstPageNo(){
		return 1;
	}
	function getLastPageNo(){
		return getTotalPageCount();
	}
	function getFirstPageNoOnPageList(){
		var temp = Math.floor((self.currentPageNo-1)/self.pageSize)*self.pageSize + 1;
		return Math.max(temp, 1);
	}
	function getLastPageNoOnPageList(){
		var lastPageNoOnPageList = getFirstPageNoOnPageList() + self.pageSize - 1;
		if(lastPageNoOnPageList > getTotalPageCount()){
			lastPageNoOnPageList = getTotalPageCount();
		}
		return lastPageNoOnPageList;
	}
	function getFirstRownum(){
		return (self.currentPageNo - 1) * self.recordCountPerPage + 1;
	}
	function getLastRownum(){
		return self.currentPageNo * self.recordCountPerPage;
	}
	$.extend(this, {
		getTotalPageCount: getTotalPageCount,
		getFirstPageNo: getFirstPageNo,
		getLastPageNo: getLastPageNo,
		getFirstPageNoOnPageList: getFirstPageNoOnPageList,
		getLastPageNoOnPageList: getLastPageNoOnPageList,
		getFirstRownum: getFirstRownum,
		getLastRownum: getLastRownum
	});
}
// node 에 페이징 뿌리기
function setPagination(node, pinfo, pageFunctionName, linkAttrText){
	var $node = $(node).empty();
	var firstPageNoOnPageList = pinfo.firstPageNoOnPageList != null ? pinfo.firstPageNoOnPageList : pinfo.getFirstPageNoOnPageList();
	var lastPageNoOnPageList = pinfo.lastPageNoOnPageList != null ? pinfo.lastPageNoOnPageList : pinfo.getLastPageNoOnPageList();
	var lastPageNo = pinfo.lastPageNo != null ? pinfo.lastPageNo : pinfo.getLastPageNo();
	var totalRecordCount = pinfo.totalRecordCount;
	var currentPageNo = pinfo.currentPageNo;

	console.log(firstPageNoOnPageList); // 페이지 리스트의 첫 페이지 번호
	console.log(lastPageNoOnPageList);  // 페이지 리스트의 마지막 페이지 번호
	console.log(lastPageNo);			// 마지막 페이지 번호
	console.log(totalRecordCount);		// 총 레코드 수
	console.log(currentPageNo);			// 현재 페이지 번호

	if(totalRecordCount <= 0){
		return;
	}
	var makePageItemHtml = function(pageNo, pageText, isActive, isEnabled){
		//var scriptStr = isEnabled ? pageFunctionName+'('+pageNo+')' : ''; // 페이지 이동 스크립트
		var scriptStr = isEnabled ? pageFunctionName+'('+(pageNo-1)+')' : ''; // 페이지 이동 스크립트
		var classes = [];
		if(!isEnabled)classes.push('disabled');
		if(isActive)classes.push('active');
		var temp = '<a class="page-link" href="javascript:'+scriptStr+';" data-pageno="'+(pageNo-1)+'" '+(linkAttrText||'')+' title="'+pageNo+' page">'+pageText+'</a>';
		return ' <li class="page-item '+classes.join(' ')+'">'+temp+'</li>';
	};
	var isEnabled, pageNo, pageText;
	var tempArr1 = [];
	// 처음
	isEnabled = firstPageNoOnPageList > 1;
	pageNo = 1, pageText = isKo ? '<i class="fa fa-angle-double-left" aria-hidden="true"><span class="d-none">First</span></i>':'First';
	tempArr1.push(makePageItemHtml(pageNo, pageText, false, isEnabled));
	// 이전
	isEnabled = firstPageNoOnPageList > 1;
	pageNo = (firstPageNoOnPageList - 1), pageText = isKo ? '<i class="fa fa-angle-left" aria-hidden="true"><span class="d-none">Prev</span></i>':'Prev';
	tempArr1.push(makePageItemHtml(pageNo, pageText, false, isEnabled));
	//
	for(var pageNo = firstPageNoOnPageList; pageNo <= lastPageNoOnPageList; pageNo++){
		//var pageIdx= pageNo -1
		//tempArr1.push(makePageItemHtml(pageIdx, pageNo, pageIdx == currentPageNo, true));
		tempArr1.push(makePageItemHtml(pageNo, pageNo, pageNo == currentPageNo, true));
	}
	// 다음
	isEnabled = lastPageNoOnPageList < lastPageNo;
	pageNo = lastPageNoOnPageList + 1, pageText = isKo?'<i class="fa fa-angle-right" aria-hidden="true"><span class="d-none">Next</span></i>':'Next';
	tempArr1.push(makePageItemHtml(pageNo, pageText, false, isEnabled));
	// 끝
	isEnabled = lastPageNoOnPageList < lastPageNo;
	pageNo = lastPageNo, pageText = isKo?'<i class="fa fa-angle-double-right" aria-hidden="true"><span class="d-none">Last</span></i>':'Last';
	tempArr1.push(makePageItemHtml(pageNo, pageText, false, isEnabled));
	//
	$node.html('<ul class="pagination uu-custom" title="total '+totalRecordCount+'">'+tempArr1.join('')+'</ul>');
}
/* 페이징 처리하는 함수 - end */

var setComboAll = function(div_app, selector, codeList){
	$(selector, div_app).each(function(){
		var el = this;
		setCombo(null, el, codeList);
	});
};

var doInProgress = function(callback){
	progressCover(true);
	try{
		return callback();
	}finally{
		progressCover(false);
	}
};

var fstr = function(val){
	if(val == null)return '';
	return String(val);
};

var fnum = function(val){
	val = Number(val||0);
	return _.isNaN(val) ? 0 : val;
};

// var stringFormat = function(str){
// 	var args = Array.prototype.slice.call(arguments, 1);
// 	return str.replace(/{(\d+)}/g, function(match, index){
// 		return args[index] || match;
// 	});
// 	//ex) console.log(stringFormat("{0} + {1} = {2} {3}", 4, 5, 9))
// };

var _StringReplacer = function(regex, substringIndex, isEmptyWhenNull){
	var regex = regex || /\{\{[^{}]+\}\}/g;
	var substringIndex = substringIndex || 2;
	var isEmptyWhenNull = isEmptyWhenNull || true;
	var replace = function(str, map){
		var replaced = str.replace(regex, function(value){
			var key = value.substring(substringIndex, value.length - substringIndex);
			key = _.trim(key);
			var repl = map[key];
			if(repl == null && isEmptyWhenNull){
				repl = '';
			}
			return repl;
		});
		return replaced;
	};
	this.replace = replace;
};

var _makeReplaceRegex = function(chr1, chr2, cnt){
	var t1 = '\\'+chr1
	var regex = new RegExp(_.repeat('\\'+chr1, cnt)+'[^'+chr1+''+chr2+']+'+_.repeat('\\'+chr2, cnt), 'g');
	return regex;
};

// var replace3 = new _StringReplacer(_makeReplaceRegex('{', '}', 4), 4);

// var replaceStrByMap = function(str, map){
// 	if(replaceStrByMap.replacer == null){
// 		replaceStrByMap.replacer = new _StringReplacer(_makeReplaceRegex('{', '}', 2), 2);
// 	}
// 	var replacer = replaceStrByMap.replacer;
// 	return replacer.replace(str, map);
// };

// var flashElem = function(parent, e, speed){
// 	speed = speed || 100;
// 	var $e = $ge(parent, e);
// 	var toggleCellClass = function(times){
// 		if(! times)return;
// 		setTimeout(function(){
// 			$e.queue(function(){
// 				$e.toggleClass('uu-flashing').dequeue();
// 				toggleCellClass(times - 1);
// 			});
// 		},
// 		speed);
// 	};
// 	toggleCellClass(4);
// };

// jQuery.cachedScript = function(url, options){
// 	options = $.extend({
// 		type: 'GET',
// 		dataType: 'script',
// 		cache: true,
// 		url: url
// 	}, options);
// 	return jQuery.ajax(options);
// };

function saveAsBlob(blob, filename){
	if(typeof saveAs == 'function'){
		saveAs(blob, filename);
	}else{
		var link = document.createElement('a');
		link.style.display = 'none';
		document.body.appendChild(link);
		var ourl = URL.createObjectURL(blob);
		link.href = ourl;
		link.download = filename;
		link.click();
		setTimeout(function(){
			URL.revokeObjectURL(ourl);
			$(link).remove();
		}, 5000);
	}
}

// function bytesToSize(bytes){
// 	var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
// 	if (bytes == 0) return '0 Byte';
// 	var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
// 	return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
// }

var createPdf = function(dd){
	if(! pdfMake.fonts){
		pdfMake.fonts = {
			NanumBarunGothicWeb: {
				normal: 'https://cdn.jsdelivr.net/font-nanumlight/1.0/NanumBarunGothicWeb.ttf',
				bold: 'https://cdn.jsdelivr.net/font-nanumlight/1.0/NanumBarunGothicWebBold.ttf'
			}
		};
	}
	var docDefinition = $.extend({
		defaultStyle: {
			font: 'NanumBarunGothicWeb',
			fontSize: 8
		},
		content: [
		]
	}, dd);
	var tableLayouts = {
		layout1: {
			hLineWidth: function(i, node){return 0.1;},
			vLineWidth: function(i){return 0.1;}
			//hLineColor: (i) => xx
			// paddingLeft: (i) => xx
			// paddingRight: (i, node) => xx
		}
	};
	return pdfMake.createPdf(docDefinition, tableLayouts);//.download(pdf_name);
};

var openDialogText = function(title1, text1){
	var $el1 = $('\
		<div>\
			<form>\
				<textarea wrap="off" class="disabled overflow-auto" readonly="readonly" style="display: block;width: 800px;height: 500px;"></textarea>\
			</form>\
			<button class="uu-btn mt-1">복사</button>\
		</div>\
	');
	var $ta1 = $el1.find('textarea:first');
	var $btn1 = $el1.find('button:first');
	$ta1.val(text1);
	$ta1.focus();//
	$el1.on('click', 'button:first', function(){
		copyToClipboard($ta1.val(), function(){
			flashMessage('copied');
		});
	});
	openDialog($el1, {
		title: title1,
		isRemoveWhenClose: true
	});
};

function copyToClipboard(text, cbOk, cbFail){
	if(navigator.clipboard){
		navigator.clipboard.writeText(text).then(cbOk, cbFail);
	}else{
		_unsecuredCopyToClipboard(text, cbOk, cbFail);
	}
};

var copyTextToClipboard = function(text){
	copyToClipboard(text, function(){
		flashMessage('copied');
	}, function(){
		flashMessageWarn('not copied');
	});
};

function _unsecuredCopyToClipboard(text, cbOk, cbFail){
	var ta1 = document.createElement('textarea');
	ta1.textContent = text;
	document.body.appendChild(ta1);
	ta1.style.position = 'fixed';
	ta1.style.left = '-9999px';
	var selection = document.getSelection();
	selection.removeAllRanges();
	var range = document.createRange();
	range.selectNode(ta1);
	selection.addRange(range);
	try{
		var isOk = document.execCommand('copy');
		if(isOk){
			cbOk && cbOk();
		}else{
			cbFail && cbFail();
		}
	}catch(err){
		cbFail && cbFail();
	}
	selection.removeAllRanges();
	document.body.removeChild(ta1);
};

var asyncAjax = function(s){
	return new Promise((resolve, reject) => {
		s.onSuccess = function(data){
			resolve(data);
		};
		s.onError = function(data){
			ajaxOnErrorDefault(data);
			reject(data);
		}
		ajax(s);
	});
};

var setErrorMessageToDivApp = function(div_app, err){
	$(div_app).text(err.errorMessage || err.statusText);
};

var getSearchParam = function(name){
	return new URLSearchParams(window.location.search).get(name);
};

var initNewHtml = function(div_app){
	div_app = div_app || document.body;
	$('form:not([autocomplete])', div_app).attr('autocomplete', 'off');
	$.fn.tooltip && $('[data-bs-toggle="tooltip"]', div_app).tooltip();
};


var dateut = (function(){
	// 분기 배열 만들기
	var makeQtList = function(stYmd, edYmd){
		var stYm = stYmd.substr(0, 6);
		var edYm = edYmd.substr(0, 6);
		var stDate = fromYmd(stYm + '01');
		stDate.setMonth(Math.ceil((stDate.getMonth() + 1) / 3) * 3 - 2 - 1);
		var edDate = fromYmd(edYm + '01');
		edDate.setMonth(Math.ceil((edDate.getMonth() + 1) / 3) * 3 - 2 - 1);
		var list = [];
		for(var date = stDate; date <= edDate; dateut.addMonth(date, 3)){
			list.push(date.getFullYear() + 'Q' + dateut.getQuarter(date));
		}
		return list;
	};
	// 년월 배열 만들기
	var makeYmList = function(stYmd, edYmd){
		var stYm = stYmd.substr(0, 6);
		var edYm = edYmd.substr(0, 6);
		var ymList = [];
		for(var date = fromYmd(stYm+'01'), ym = dateut.getYm(date); ym <= edYm; dateut.addMonth(date, 1), ym = dateut.getYm(date)){
			ymList.push(ym);
		}
		return ymList;
	};
	// 년도 배열 만들기
	var makeYyList = function(stYmd, edYmd){
		var stYy = Number(stYmd.substr(0, 4));
		var edYy = Number(edYmd.substr(0, 4));
		var yyList = [];
		for(var yy = stYy; yy <= edYy; yy++){
			yyList.push(String(yy));
		}
		return yyList;
	};
	// 일자 배열 만들기
	var makeYmdList = function(stYmd, edYmd){
		var ymdList = [];
		for(var date = fromYmd(stYmd), ymd = dateut.getYmd(date); ymd <= edYmd; dateut.addDate(date, 1), ymd = dateut.getYmd(date)){
			ymdList.push(ymd);
		}
		return ymdList;
	};
	// ymd 의 year, month, date 를 inc 만큼 증가시켜 반환
	var addYmd = function(ymd, gbn, inc){
		var dt = new Date(Number(ymd.substr(0, 4)), Number(ymd.substr(4, 2))-1, Number(ymd.substr(6, 2)));
		switch (gbn){
		case "Y": dateut.addYear(dt, inc);break;
		case "M": dateut.addMonth(dt, inc);break;
		case "D": dateut.addDate(dt, inc);break;
		// last day of month
		case "LOM":
			dt.setDate(1);
			dt.setMonth(dt.getMonth() + 1);
			dt.setDate(dt.getDate() - 1);
			break;
		}
		return dateut.getYmd(dt);
	};
	// ym 의 year, month 를 inc 만큼 증가시켜 반환
	var addYm = function(ym, gbn, inc){
		var dt = new Date(Number(ym.substr(0, 4)), Number(ym.substr(4, 2))-1, 1);
		switch (gbn){
		case "Y": dt.setFullYear(dt.getFullYear() + inc); break;
		case "M": dt.setMonth(dt.getMonth() + inc); break;
		}
		return _.padStart(String(dt.getFullYear()), 4, '0') + _.padStart(String(dt.getMonth() + 1), 2, '0');
	};
	// 두 시간 사이의 개월수 계산
	var getMonthsBetween = function(d1, d2){
		var mcnt = 0;
		mcnt += (d2.getFullYear() - d1.getFullYear()) * 12;
		mcnt += d2.getMonth() - d1.getMonth();
		return mcnt;
	};
	// 두 시간 사이의 일수 계산
	var getDaysBetween = function(d1, d2){
		return Math.floor((d2 - d1)/(24 * 60 * 60 * 1000));
	};
	var fromYmd = function(ymd){
		var len1 = ymd.length;
		var yy = Number(ymd.substr(0, 4));
		var MM = len1 >= 6 ? Number(ymd.substr(4, 2))-1 : 0;
		var dd = len1 >= 8 ? Number(ymd.substr(6, 2)) : 1;
		var hh = len1 >= 10 ? Number(ymd.substr(8, 2)) : 0;
		var mm = len1 >= 12 ? Number(ymd.substr(10, 2)) : 0;
		var ss = len1 >= 14 ? Number(ymd.substr(12, 2)) : 0;
		var SSS = len1 >= 17 ? Number(ymd.substr(14, 3)) : 0;
		var date1 = new Date(yy, MM, dd, hh, mm, ss, SSS);
		return date1;
	};
	var fromYmdhms = function(ymdhms){
		return fromYmd(ymdhms);
	};
	var dayNames = {
		en: ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'],
		ko: ['일','월','화','수','목','금','토']
	};
	var getBetweenDays = function(d1, d2){
		return (d2.getTime() - d1.getTime())/ DAYTIME;
	};
	var DAYTIME = 1000 * 60 * 60 * 24;
	var HOURTIME = 1000 * 60 * 60;
	var MINUTETIME = 1000 * 60;
	var SECONDTIME = 1000;
	var calcElapsed = function(a, b){
		var elapsed = b.getTime() - a.getTime();
		return {
			days: Math.floor(elapsed / DAYTIME),
			hours: Math.floor((elapsed % DAYTIME) / HOURTIME),
			minutes: Math.floor((elapsed % HOURTIME) / MINUTETIME),
			seconds: Math.floor((elapsed % MINUTETIME) / SECONDTIME)
		};
	};
	var fromTime = function(time){
		var d1 = new Date();
		d1.setTime(time);
		return d1;
	};

	////////////////////////// dateobject //////////////////////////
	var getYmd = function(mydate){
		return String(mydate.getFullYear())+_.padStart(mydate.getMonth()+1, 2, '0')+_.padStart(mydate.getDate(), 2, '0');
	};
	var getHms = function(mydate){
		return _.padStart(mydate.getHours(), 2, '0')+_.padStart(mydate.getMinutes(), 2, '0')+_.padStart(mydate.getSeconds(), 2, '0');
	};
	var getYmdhms = function(mydate){
		return getYmd(mydate) + getHms(mydate);
	};
	var getYm = function(mydate){
		return String(mydate.getFullYear())+_.padStart(mydate.getMonth()+1, 2, '0');
	};
	var getCalDateList = function(mydate){
		var LOM = dateut.getLastDateOfMonth(mydate);
		var thisYm = dateut.getYm(mydate);
		var date = new Date(mydate);
		date.setDate(1);
		dateut.addDate(date, -1 * date.getDay());
		var dateList = [];
		for(;true; dateut.addDate(date, 1)){
			dateList.push(new Date(date));
			if(date.getDay() == 6 && (date.getDate() == LOM || dateut.getYm(date) > thisYm)){
				break;
			}
		}
		return dateList;
	};
	// 월 달력
	var getMonthCalendarList = function(mydate){
		var LOM = dateut.getLastDateOfMonth(mydate);// 30

		// 달력만들 시작일자, 종료일자 만들기
		var first_date = new Date(mydate);
		first_date.setDate(1);
		if(first_date.getDay() > 0){
			dateut.addDate(first_date, -1 * first_date.getDay());
		}
		var last_date = new Date(mydate);
		last_date.setDate(LOM);
		if(last_date.getDay() < 6){
			dateut.addDate(last_date, 6 - last_date.getDay());
		}
		var week_arr = [], week_date_arr;
		var date2 = new Date(first_date);
		while(date2 <= last_date){
			var date2_day = date2.getDay();
			if(date2_day == 0){
				week_date_arr = [];
			}
			week_date_arr.push(date2);
			if(date2_day == 6){
				week_arr.push(week_date_arr);
			}

			date2 = new Date(date2);
			dateut.addDate(date2, 1);
		}
		return week_arr;
	};
	var getFirstDateOfMonth = function(mydate, gbn){
		var date = new Date(mydate);
		date.setDate(1);
		return gbn == 'date'?date:date.getDate();
	};
	// 말일
	var getLastDateOfMonth = function(mydate, gbn){
		var date = new Date(mydate);
		date.setDate(1);
		date.setMonth(date.getMonth()+1);
		date.setDate(date.getDate()-1);
		return gbn == 'date'?date:date.getDate();
	};
	// 월의 몇번째 요일
	var getDayOrder = function(mydate){
		return Math.ceil(mydate.getDate()/7);
	};
	// 월의 첫번째 요일
	var getFirstDay = function(mydate){
		var date = new Date(mydate);
		date.setDate(1);
		return date.getDay();
	};
	// 월의 마지막 요일 여부
	var isLastDay = function(mydate){
		return mydate.getDate() + 7 > dateut.getLastDateOfMonth(mydate);
	};
	var getDD = function(mydate){
		return _.padStart(mydate.getDate(), 2, '0');
	};
	var getMM = function(mydate){
		return _.padStart(mydate.getMonth()+1, 2, '0');
	};
	var addYear = function(mydate, n){
		mydate.setFullYear(mydate.getFullYear()+n);
		return mydate;
	};
	var addMonth = function(mydate, n){
		var tmpDate = mydate.getDate();
		mydate.setMonth(mydate.getMonth()+n);
		if(mydate.getDate() != tmpDate){
			mydate.setMonth(mydate.getMonth() - 1);
			mydate.setDate(dateut.getLastDateOfMonth(mydate));
		}
		return mydate;
	};
	var addDate = function(mydate, n){
		mydate.setDate(mydate.getDate()+n);
		return mydate;
	};
	// 토, 일 여부
	var isHolyDay = function(mydate){
		return mydate.getDay() == 0 || mydate.getDay() == 6;
	};
	// 월 마지막 여부
	var isLastDate = function(mydate){
		return mydate.getDate() == dateut.getLastDateOfMonth(mydate);
	};
	var toLastDate = function(mydate){
		mydate.setDate(dateut.getLastDateOfMonth(mydate));
	};
	var getDayName = function(mydate, lang){
		return dateut.dayNames[lang || 'ko'][mydate.getDay()];
	};
	var getWeekStartEnd = function(mydate){
		var mydate = new Date(mydate);
		var day = mydate.getDay();
		var addCnt = day == 0 ? -6 : - (day - 1);// 일요일이면 6을 나머지는 ...
		dateut.addDate(mydate, addCnt);
		var startDate = new Date(mydate);
		dateut.addDate(mydate, 6);
		var endDate = new Date(mydate);
		return {startDate: startDate, endDate: endDate};
	};
	var getQuarter = function(mydate){
		return Math.ceil((mydate.getMonth() + 1)/3);
	};
	return {
		makeQtList,
		makeYmList,
		makeYyList,
		makeYmdList,
		addYmd,
		addYm,
		getMonthsBetween,
		getDaysBetween,
		fromYmd,
		fromYmdhms,
		dayNames,
		getBetweenDays,
		calcElapsed,
		fromTime,
		//
		getYm,
		getYmd,
		getHms,
		getYmdhms,
		getCalDateList,
		getMonthCalendarList,
		getFirstDateOfMonth,
		getLastDateOfMonth,
		getDayOrder,
		getFirstDay,
		isLastDay,
		getDD,
		getMM,
		addYear,
		addMonth,
		addDate,
		isHolyDay,
		isLastDate,
		toLastDate,
		getDayName,
		getWeekStartEnd,
		getQuarter
	};
})();//dateut

var arrayut = (function(){
	var move = function(arr, old_index, new_index){
		while(old_index < 0)old_index += arr.length;
		while(new_index < 0)new_index += arr.length;
		if(new_index >= arr.length){
			var k = new_index - arr.length;
			while((k--) + 1){
				arr.push(undefined);
			}
		}
		arr.splice(new_index, 0, arr.splice(old_index, 1)[0]);
		return arr;
	};
	var getValidIndex = function(len1, index1){
		while(index1 < 0)index1 += len1;
		return index1 % len1;
	};
	var sumItemFn = function(r, it, props){
		_.each(props, function(prop){
			r[prop] = +Big(r[prop]||0).plus(it[prop]||0);
		});
	};
	var sumListFn = function(r, list1, props){
		_.each(list1, function(it){
			sumItemFn(r, it, props);
		});
	};
	var mapPick = function(list1, props){
		return _.map(list1, _.bind(_.pick, null, _, props));
	};
	var makeArrayGroupBy = function(list1, gbFn){
		var gb = _.groupBy(list1, gbFn);
		var arr = [];
		for(var k in gb){
			arr.push(gb[k]);
		}
		return arr;
	};
	var getCodeList = function(gbn){
		if(gbn == 'yn'){
			return makeMapArray([['code', 'name'], ['Y', 'Y'], ['N', 'N']]);
		}else if(gbn == 'mmList'){
			return '01 02 03 04 05 06 07 08 09 10 11 12'.split(' ');
		}
	};
	var makeFormHtml = function(rowList, type){
		var wrap = $('<div><table class="uu-table"><caption>정보</caption><tbody></tbody></table></div>');
		var tbody = $('tbody', wrap);
		_.each(rowList, function(row){
			var tr = tbody.append('<tr></tr>');
			_.each(row, function(nmVal){
				var nm = nmVal.nm;
				var val = nmVal.val;
				if(_.isNumber(val))val = formatNumber(val);
				var colspan = nmVal.colspan || 1;
				tr.append('<th scope="row">'+escapeTextHtml(nm)+'</th><td colspan="'+colspan+'" >'+escapeTextHtml(val)+'</td>');
			});
			tbody.append(tr);
		});
		return wrap.html();
	};
	var sumList = function(arr1){
		var r = 0;
		_.each(arr1, function(num){
			r = +Big(r).plus(num||0);
		});
		return r;
	};
	var makeCondSumItem = function(list1, condFn, sumPropList){
		return _.reduce(list1, function(r, it){
			if(condFn == null || condFn(it)){
				sumItemFn(r, it, sumPropList);
			}
			return r;
		}, {});
	};
	// 목록 속성값으로 맵 만들기
	var makeListToMap = function(list1, keyProp, valueProp){
		return _.reduce(list1, function(r, it){
			var key = it[keyProp];
			r[key] = valueProp ? it[valueProp] : it;
			return r;
		}, {});
	};
	var getPropsFromObject = function(obj1){
		var arr1 = [];
		for(var prop in obj1){
			if(obj1.hasOwnProperty(prop)){
				arr1.push(prop);
			}
		}
		return arr1;
	};
	// 목록에서 사용자id 중복되지 않게 추출
	var getDistinctPropValueFromList = function(list1, propList){
		var map1 = {};
		_.each(list1, function(it){
			_.each(propList, function(prop){
				var val = it[prop];
				if(val) map1[val] = 1;
			});
		});
		return getPropsFromObject(map1);
	};
	return {
		move,
		getValidIndex,
		sumItemFn,
		sumListFn,
		mapPick,
		makeArrayGroupBy,
		getCodeList,
		makeFormHtml,
		sumList,
		makeCondSumItem,
		makeListToMap,
		getPropsFromObject,
		getDistinctPropValueFromList
	};
})();//arrayut

var treeut = function(ss){
	var ss = _.extend({
		propLevel: 'level',
		propId: 'id',
		propPid: 'pid',
		propChildren: 'children',
		limitLevel: 5,
		rootCondFunc: function(it){
			return ! it.pid;
		},
		isSetHelperProp: false
	}, ss)
	var {propLevel, propId, propPid, propChildren, limitLevel, rootCondFunc, isSetHelperProp} = ss;

	// var setArrayPidByLevel = function(levelArray){
	// 	var pids = [];
	// 	_.each(levelArray, function(it, idx){
	// 		it[propId] = idx;
	// 		var level = it[propLevel];
	// 		if(pids[level - 1] != null)it[propPid] = pids[level - 1];
	// 		pids[level] = it[propId];
	// 		it[propLevel] = level;
	// 	});
	// };

	// id pid 로 레벨 찾기
	var findLevel = function(idArray, it){
		var checkIt = it;
		var level = 1;
		var limitLevel = limitLevel || 5;
		var rootCondFunc = rootCondFunc || function(it){
			return ! it[propPid];
		};
		for(;true;){
			if(rootCondFunc && rootCondFunc(checkIt)){
				return {valid: true, level: level};
			}
			var pid = checkIt[propPid];
			if(! pid)return {valid: true, level: level};
			var searchParentMap = {};
			searchParentMap[propId] = pid;
			var parentItem = _.find(idArray, searchParentMap);// searchParentMap 예 = {id: pid}
			if(parentItem == null)return {valid: false, msg: '레벨을 찾을 수 없습니다.'};
			level++;
			if(level > limitLevel){
				return {valid: false, msg: '레벨이 '+limitLevel+'가 초과합니다.'};
			}
			checkIt = parentItem;
		}
	};
	// id pid 로 level 셋
	var setLevelByIdPid = function(idArray){
		_.each(idArray, function(it){
			var result = findLevel(idArray, it);
			it[propLevel] = result.valid && result.level;
		});
	};
	var makeTreeByIdPid = function(idArray){
		var idArray = _.cloneDeep(idArray);// 복사해서 사용하자
		// 먼저 _level 넣고
		setLevelByIdPid(idArray);
		var gbPid = _.groupBy(idArray, propPid);// group by pid
		var treeArray = [];
		_.each(idArray, function(it){
			var id = it[propId];
			var children = gbPid[id];
			if(children)it[propChildren] = children;
			// 루트에 1레벨 item 만 넣기
			var myLevel = it[propLevel];
			if(myLevel == 1 || myLevel == null)treeArray.push(it);
		});
		return treeArray;
	};
	var makeTreeToLevelArray = function(treeArray){
		var levelArray = [];
		var _id = 0;
		var pushToArray = function(treeArray, level, parentIt){
			_.each(treeArray, function(it){
				var children = it[propChildren];
				// var levelIt = $.extend({_level: level, _id: _id++, _pid: parentIt ? parentIt._id : null}, it);
				var levelIt = $.extend({}, it);
				levelIt[propLevel] = level;
				delete levelIt[propChildren];
				levelArray.push(levelIt);
				if(children && children.length){
					pushToArray(children, level + 1, levelIt);
				}
			});
		};
		pushToArray(treeArray, 1, null);
		return levelArray;
	};
	// level array to hierachy
	var makeLevelToTreeArray = function(levelArray){
		var pitems = [{[propChildren]: []}];
		_.each(levelArray, function(it, idx){
			it = $.extend({}, it);
			// if(isSetHelperProp)it._id = idx;
			var level = it[propLevel];
			// if(isSetHelperProp)it._level = level;
			if(pitems[level - 1][propChildren] == null){
				pitems[level - 1][propChildren] = [];
			}
			pitems[level - 1][propChildren].push(it);
			pitems[level] = it;
			// if(isSetHelperProp)it._pid = pitems[level - 1]._id;
		});
		return pitems[0][propChildren];
	};

	return {
		makeTreeByIdPid,
		makeTreeToLevelArray,
		makeLevelToTreeArray
	};

};//treeut

var maskEmail = function(str){
	if(! str)return str;
	if(! _.includes(str, '@'))return str;
	var index1 = str.indexOf('@');
	var part1 = str.substr(0, index1);
	var maskCnt = Math.trunc(part1.length/2);
	part1 = _.map(part1, function(c, i){
		if(i >= maskCnt)return '*';
		return c;
	}).join('');
	return part1 + str.substr(index1);
};

var maskStr = function(str){
	if(! str)return '';
	str = String(str);
	var noMaskLen = Math.ceil(str.length / 2);
	return str.substr(0, noMaskLen) + repeatStr('*', str.length - noMaskLen);
};

// date 가 숫자로 넘어올때 포매팅 할려고
var makeDateStrIfNumber = function(val, len){
	if(typeof val == 'number'){
		var ymdhms = uu.dateut.getYmdhms(new Date(val));
		if(len == null)return ymdhms;
		return ymdhms.substr(0, len);
	}else return val;
};

var _event = function(that){
	return event || that.event;
};

var postRedirect = function(action, data) {
	const form = document.createElement('form');
	form.method = 'post';
	form.action = action;
	for(const key in data) {
		if(data.hasOwnProperty(key)) {
			const input = document.createElement('input');
			input.type = 'hidden';
			input.name = key;
			input.value = data[key];
			form.appendChild(input);
		}
	}
	document.body.appendChild(form);
	form.submit();
	document.body.removeChild(form);
};

return {
	isKo,
	commsg,
	getScale,
	objectContains,
	progressCover,
	$ge,
	ge,
	checkForm,
	checkField,
	formatDate,
	// 널 체크
	// isEmpty,
	// isNotEmpty,
	nullToEmpty,
	// isNull,
	// isNotNull,
	// nvl,
	isNullOrEmptyString,
	ifnull,
	ifempty,
	RegExpMap,
	// 포맷
	formatNumMask,
	countChr,
	isYearFormat,
	formatYear,
	isYmFormat,
	formatYm,
	isMmFormat,
	formatMm,
	isYmdFormat,
	formatYmd,
	formatYmdhm,
	formatYmdhms,
	isHmFormat,
	isHmsFormat,
	isHhFormat,
	formatHm,
	isYmdhmsFormat,
	isYmdhmFormat,
	isNumberFormat,
	formatNumber,
	isDigitFormat,
	formatDigit,
	isPhoneNoFormat,
	isSsnFormat,
	isEmailFormat,
	formatSsn,
	isBizNoFormat,
	formatBizNo,
	enableElem,
	escapeHtml,
	escapeTextHtml,
	encodeJson,
	encodeJsonWithObjectKeySort,
	parseStrToObj,
	//
	setCombo,
	gev,
	sev,
	// goLink,
	openWindow,
	makeMapArray,
	makeColArray,
	getCurrFocusElem,
	ajax,
	ajaxOnErrorDefault,
	ajaxGridSearch,
	ajaxGridSave,
	validateGrid,
	makeSaveListJson,
	setFormData,
	getFormData,
	makeListPropToMapArray,
	// lpad,
	// rpad,
	// npad,
	round2,
	ceil2,
	floor2,
	trunc2,
	repeatStr,
	getFileExt,
	getFileBaseName,
	selectFocus,
	greatest,
	least,
	getBetweenNum,
	calcTree,
	addInputMonth,
	showMessage,
	forceNumber,
	forceUnformatNumber,
	openDialog,
	closeDialog,
	destroyIfDialog,
	makeSumArray,
	setInputAutocomplete,
	makeAutocompleteSource,
	formatField,
	unformatField,
	getSetFieldData,
	Coder,
	makeLevelArrayByGroup,
	gridResizeCanvasWithin,
	eachArrayChildren,
	showAlert,
	showConfirm,
	setPagination,
	setDatepicker,
	destroyDatepicker,
	debounceHideDatepickerAll,
	loadPage,
	loadDialog,
	tinyFlashMessage,
	getCodeName,
	getCodeName2,
	checkAlert,
	checkFlash,
	downloadFile,
	hasDupByKeys,
	getKeysFromList,
	getPct,
	getRatio,
	setComboAutoSearchEvent,
	checkMobile,
	makeTmplNodeList,
	makeLTFn,
	getTmplNodeData,
	enableElemBySelector,
	randomString,
	flashMessage,
	flashMessageWarn,
	gridDestroyWithin,
	openDialogGrid,
	makePager,
	setComboAll,
	doInProgress,
	fstr,
	fnum,
	// replaceStrByMap,
	saveAsBlob,
	createPdf,
	openDialogText,
	copyToClipboard,
	copyTextToClipboard,
	asyncAjax,
	setErrorMessageToDivApp,
	getSearchParam,
	initNewHtml,
	dateut,
	arrayut,
	treeut,
	maskEmail,
	maskStr,
	// actionToUrl,
	makeDateStrIfNumber,
	_event,
	postRedirect,
	// ajaxAxios,
	dummy: 1
};

};//PykUtil
window.uu = new PykUtil();

var DivAppUtil = function(){
	//div_app 에 methods 객체 set
	var setDivAppMethodObj = function(div_app, methods){
		div_app = $(div_app)[0];
		var _div_app_id = 'div_app-'+_.uniqueId();
		$(div_app).addClass(_div_app_id).data('_div_app_methods', methods).data('_div_app_id', _div_app_id);
		// 모달로 띄울 경우 아예 밖으로 나가버려서 모달 div 에다가도 methods set 하자
		// -> openDialog 에서 자동으로 하는 걸로 수정
		// $('.div_app_sub', div_app).each(function(){
		// 	var el = this, $el = $(el);
		// 	if($el.closest('.div_app')[0] == div_app){
		// 		$el.data('_parentDivApp', div_app);
		// 	}
		// });
		window.SlickUtil && SlickUtil.initGridResizer(div_app);
	};
	// div_app 에서 methods 객체 찾기
	var findDivAppMethodObj = function(el){
		var temp1 = $(el).closest('.div_app_sub')[0];
		if(! temp1){
			temp1 = $(el).closest('.div_app')[0];
		}
		return $(temp1).data('_div_app_methods');
	};

	// tag 변수 일일히 지정하기 귀찮아서
	// var re_var = /^[_a-zA-Z][_a-zA-Z0-9]*$/;
	var getRefs = function(div_app){
		div_app = $(div_app)[0];
		var map1 = {};
		$('form[name],[data-ename]', div_app).each(function(){
			var el = this, $el = $(el);
			var name = $el.attr('name') || $el.attr('data-ename');// el.name 하면 안되네
			// 가장 가까운 .div_app 가 나 일때만
			//if(name.match(re_var) && $el.closest('.div_app')[0] == div_app){
			if(name && $el.closest('.div_app')[0] == div_app){
				map1[name] = el;
			}
		});
		return map1;
	};
	var emptyTarget = function(target){
		var callDestroy = function(target){
			var methodObj = $(target).data('_div_app_methods');
			if(methodObj)DivApp.callAction(methodObj, 'destroy', null);
		};
		uu.gridDestroyWithin(target);
		if($(target).is('.div_app')){
			callDestroy(target);
		}
		_.each($('.div_app', target), function(subDiv){
			callDestroy(subDiv);
		});
		uu.destroyIfDialog(target);
		$(target).empty();
	};

	return {
		setDivAppMethodObj: setDivAppMethodObj,
		findDivAppMethodObj: findDivAppMethodObj,
		getRefs: getRefs,
		getModel: function(fn){
			if(fn == null)return {};
			var map1 = fn();
			uu.makeListPropToMapArray(map1);
			return map1;
		},
		emptyTarget: emptyTarget,
		callAction: function(methodObj, actionNm, event){

			// var pIndex = actionNm.indexOf('(');
			// var pLastIndex = actionNm.lastIndexOf(')');
			// if(pIndex > 0 && pLastIndex > 0){
			// 	var actualActionNm = actionNm.substr(0, pIndex);
			// 	var func = methodObj[actualActionNm];
			// 	if(! func)return;
			// 	var argsStr = actionNm.substring(pIndex+1, pLastIndex);
			// 	new Function('func, thisObj', 'func.call(thisObj, '+argsStr+')')(func, el);
			// 	return;
			// }
			var pIndex = actionNm.indexOf('(');
			if(pIndex > 0){
				new Function('m, event', actionNm)(methodObj, event);
				// var actualActionNm = actionNm.substr(0, pIndex);
				// var func = methodObj[actualActionNm];
				// if(! func)return;
				// var argsStr = actionNm.substring(pIndex+1, pLastIndex);
				// new Function('func, thisObj', 'func.call(thisObj, '+argsStr+')')(func, el);
				return;
			}

			var fn1 = methodObj[actionNm];
			if(fn1)fn1.call({event});
			// else if(_.isFunction(methodObj)){
			// 	methodObj.call(el, actionNm);
			// }
		}
		// makeRefFn: function(div_app){
		// 	div_app = $(div_app)[0];
		// 	return function(name){
		// 		var _div_app_id = $(div_app).data('_div_app_id');
		// 		var findSelector = 'form[name='+name+'],[data-ename='+name+']';
		// 		var temp1 = $('.div_app.'+_div_app_id+' '+findSelector)[0];
		// 		if(temp1)return temp1;
		// 		temp1 = $('.div_app_sub.'+_div_app_id+findSelector)[0];
		// 		if(temp1)return temp1;
		// 		temp1 = $('.div_app_sub.'+_div_app_id+' '+findSelector)[0];
		// 		return temp1;
		// 	};
		// }
		// ,loadDivAppAll: function(parent){
		// 	if(parent == null)parent = document.body;
		// 	_.each($('.div_app', parent), function(divAppNode){
		// 		if(divAppNode._initDivApp){
		// 			divAppNode._initDivApp(divAppNode);
		// 			divAppNode._initDivApp = null;
		// 		}
		// 	});
		// }
	};
};//DivAppUtil
window.DivApp = new DivAppUtil();

var NL = '\r\n';
var TAB = '\t';

$(function(){
	if(! window._load_pcommon3)return;

	if(_.templateSettings){
		$.extend(_.templateSettings, {
			// escape: /\(@-([\s\S]+?)@\)/g,// /<%-([\s\S]+?)%>/g,
			// evaluate: /\(@([\s\S]+?)@\)/g,// /<%([\s\S]+?)%>/g,
			// interpolate: /\(@=([\s\S]+?)@\)/g,// /<%=([\s\S]+?)%>/g,
			escape: /{%-([\s\S]+?)%}/g,
			evaluate: /{%([\s\S]+?)%}/g,
			interpolate: /{%=([\s\S]+?)%}/g,
			variable: 'it'
		});
	}

	// 이거 하면 summernote 에러나네. $.fn.bs_tooltip = $.fn.tooltip.noConflict();
	if($.fn.datepicker && $.fn.datepicker.noConflict){
		var datepicker = $.fn.datepicker.noConflict();
		$.fn.bs_datepicker = datepicker;
	}

	if($.datepicker){
		// datepicker default
		$.datepicker.setDefaults({
			//monthNames: ["1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"], // Names of months for drop-down and formatting
			monthNamesShort: ["1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"], // For formatting
			dayNamesMin: ["일","월","화","수","목","금","토"], // Column headings for days starting at Sunday
			weekHeader: "주", // Column header for week of the year
			showMonthAfterYear: true,
			dateFormat: 'yy-mm-dd',
			changeMonth: true,
			changeYear: true,
			duration: 0,
			numberOfMonths: 1,
			beforeShow: function(input, inst){
				if($(input).attr('readonly')){
					return false;
				}
			}//,
			//onClose: function(dateText, inst){}
		});
	}

	if($.fn.bs_datepicker && $.fn.bs_datepicker.dates){
		var days = '일,월,화,수,목,금,토,일'.split(',');
		var months = '1월,2월,3월,4월,5월,6월,7월,8월,9월,10월,11월,12월'.split(',');
		$.fn.bs_datepicker.dates['en'] = $.fn.bs_datepicker.dates['kr'] = {
			clear: '지우기',
			days: days, daysShort: days, daysMin: days,
			months: months, monthsShort: months,
			today: '오늘'
		};
	}
});

$(function(){
	if(! window._load_pcommon3)return;

	var inputSelector = ':text';
	$(document).on('keyup', inputSelector, function(event){
		var e = this, $e = $(this);
		if(e.disabled || e.readOnly)return;
		if(_.includes([37,38,39,40], event.keyCode))return;
		var edata = uu.getSetFieldData($e);
		if(edata.format == 'number'){
			if(! uu.RegExpMap.numbermatch.test($e.val())){
				var temp = $e.val().replace(uu.RegExpMap.notnumberreplace, '');
				$e.val(temp);
			}
			// if($e.hasClass('num_han')){
			// 	var num = forceNumber($e.val());
			// 	var han = convertNumToHan(num);
			//uu.tinyFlashMessagege({
			// 		message: han,
			// 		of: $e,
			// 		delay: 3000,
			// 		css: {fontSize: '0.9em'}
			// 	});
			// }
		}else{
			uu.formatField(e);
		}
	}).on('focus', inputSelector, function(event){
		var e = this, $e = $(this);
		if(e.disabled || e.readOnly)return;
		var edata = uu.getSetFieldData($e);
		// focusout 할때 변경 감지 하기 위해(순서중요)
		$e.data('focus_org_value', $e.val());
		if(edata.format == 'number'){uu.unformatField(e);}
		// if(window._text_input_select_timeout){
		// 	clearTimeout(window._text_input_select_timeout);
		// }
		// if($e.hasClass('uu-field')){
		// 	window._text_input_select_timeout = setTimeout(_.bind($e.select, $e));
		// }
	}).on('focusout', inputSelector, function(event){
		var e = this, $e = $(this);
		if(e.disabled || e.readOnly)return;
		uu.formatField(e);
		// focus 될때랑 나갈때랑 달라지면 change 호출하자
		if($e.data('focus_org_value') != $e.val()){
			$e.trigger('change');//$e.change();
		}
		$e.removeData('focus_org_value');
	});
	// action 없는 form은 submit 방지
	$(document).on('submit', 'form:not([action])', function(){return false;});
	window.__popupList = [];
	$(window).on('beforeunload', function(){
		var i, len, popup, array = window.__popupList;
		for(i = 0, len = array.length; i < len; i+=1){
			popup = array[i];
			if(popup && ! popup.closed){
				popup.close();
			}
		}
	});
	$(window).on('resize', function(event){
		uu.gridResizeCanvasWithin();// 인자 없으면 debouce 됨
	});

	// 공통 [data-action] 있는 거 클릭했을 때
	$(document.body).on('click', '.div_app [data-action], .div_app_sub [data-action]', function(event){
		var el = this, $el = $(el);
		var methodObj = DivApp.findDivAppMethodObj(el);
		var actionNm = $el.attr('data-action');
		DivApp.callAction(methodObj, actionNm, event);
	});

	// 아래거 하면 링크 href 가 안먹음 onclick 은 먹는데
	// if($.fn.dropdown){
	// 	$(document.body)
	// 	.on('mouseenter', '.dropdown.uu-bs-dropdown-hovershow', function(){
	// 		var node = this;
	// 		node._bs_dropdown_timeout && clearTimeout(node._bs_dropdown_timeout);
	// 		node._bs_dropdown_timeout = setTimeout(function(){
	// 			$(node).dropdown('show');
	// 		}, 300);
	// 	})
	// 	.on('mouseleave', '.dropdown.uu-bs-dropdown-hovershow', function(){
	// 		var node = this;
	// 		node._bs_dropdown_timeout && clearTimeout(node._bs_dropdown_timeout);
	// 		node._bs_dropdown_timeout = setTimeout(function(){
	// 			$(node).dropdown('hide');
	// 		}, 500);
	// 	})
	// 	;
	// }
});

// 그리드 멀티 코드 선택
window.GridMultiCodeSel = function(s){
	var codeList = s.codeList;
	var dataAction = s.dataAction;
	var valColName = s.valColName;
	var dispColName = s.dispColName
	var tmpl1 = _.template('<label><input type="checkbox" name="{%- it.code %}"/> {%- it.name %}</label>');
	var checkboxListHtml = _.map(codeList, function(it){
		return tmpl1(it)
	}).join('');
	// 그리드 셀 클릭했을때 창 띄우기
	var gridItemClick = function(item, div1){
		$(div1).html(
			`
			<form class="uu-form">
				`+checkboxListHtml+`
				<br/>
				<br/>
				<div><button class="uu-btn" data-action="`+dataAction+`">적용</button></div>
			</form>
			`
		);
		var strCdArr = item[valColName];
		var arrCd = strCdArr ? strCdArr.split(',') : [];
		var fm1 = $('form', div1)[0];
		$(':checkbox', fm1).each(function(){
			var chk = this;
			var chkName = $(chk).attr('name');
			$(chk).prop('checked', arrCd.indexOf(chkName) >= 0);
		});
		uu.openDialog(div1, {title: '선택'});
	};
	// 그리드에 적용하기
	var applyToGrid = function(grid, div1){
		var fm1 = $('form', div1)[0];
		var cdArr = [];
		$(':checkbox', fm1).each(function(){
			var chk = this;
			if(chk.checked){
				var chkName = $(chk).attr('name');
				cdArr.push(chkName);
			}
		});
		var strCdArr = cdArr.join(',');
		var it1 = grid.getActiveItem();
		it1[valColName] = strCdArr;
		it1[dispColName] = getDisp_cd_list(strCdArr);
		grid.updateItem(it1);
		uu.closeDialog(div1);
	};
	// 권한 명칭 표시
	var getDisp_cd_list = function(strCdArr){
		if(! strCdArr)return '';
		var cdArr = strCdArr.split(',');
		return _.map(cdArr, function(cd){
			return uu.getCodeName(codeList, cd);
		}).join(',');
	};
	return {
		gridItemClick,
		applyToGrid,
		getDisp_cd_list
	};
};

// var XmlMaker = function(doc, root){
// 	var self = this;
// 	self.context = root;
// 	var nbody = function(name, body){
// 		var node = doc.createElement(name);
// 		var parent = self.context;
// 		parent.appendChild(node);
// 		self.context = node;
// 		body();
// 		self.context = parent;
// 	};
// 	var ntext = function(name, val){
// 		if(val == null)val = '';
// 		var node = doc.createElement(name);
// 		node.appendChild(doc.createTextNode(val));
// 		self.context.appendChild(node);
// 	};
// 	var cdata = function(name, val){
// 		if(val == null)val = '';
// 		var node = doc.createElement(name);
// 		node.appendChild(doc.createCDATASection(val));
// 		self.context.appendChild(node);
// 	};
// 	self.nbody = nbody;
// 	self.ntext = ntext;
// 	self.cdata = cdata;
// };

// XmlUtil = (function(){
// 	var makeXmlFromData = function(rootName, data){
// 		var rootName = rootName||'data';
// 		var doc = new DOMParser().parseFromString('<data></data>', 'text/xml'); //important to use "text/xml"
// 		var root = doc.children[0];
// 		var xml = new XmlMaker(doc, root);
// 		for(var dataname in data){
// 			var value = data[dataname];
// 			if(! value)continue;
// 			if(_.isArray(value)){// 배열이면
// 				var list1 = value;
// 				if(list1.length == 0)continue;
// 				var it0 = list1[0];
// 				var keys = _.keys(it0);
// 				keys = _.filter(keys, function(k){
// 					return ! _.isFunction(it0[k]);
// 				});
// 				xml.nbody(dataname, function(){
// 					_.each(list1, function(row){
// 						xml.nbody('row', function(){
// 							_.each(keys, function(k){
// 								xml.ntext(k, row[k]);
// 							});
// 						});
// 					});
// 				});
// 			}else{// 맵이면
// 				var keys = _.keys(value);
// 				keys = _.filter(keys, function(k){
// 					return ! _.isFunction(value[k]);
// 				});
// 				xml.nbody(dataname, function(){
// 					_.each(keys, function(k){
// 						xml.ntext(k, value[k]);
// 					});
// 				});
// 			}
// 		}
// 		var xmlStr = new XMLSerializer().serializeToString(doc);
// 		return xmlStr;
// 	};
// 	return {
// 		makeXmlFromData: makeXmlFromData
// 	};
// })();

// var AutoScroller1 = function(container, childSelector, animateTM, scrollTM){
// 	var self = this;
// 	var $cont1 = $(container);
// 	var animateTM = animateTM || 1000;
// 	var scrollTM = scrollTM || 2000;
// 	var isScrolling = true;
// 	var stopScrolling = false;
// 	var calcScrollLeft = function(times){
// 		var boxLen = $(childSelector, $cont1).length;
// 		var contScrollWidth = $cont1[0].scrollWidth;
// 		var contWidth = $cont1.width();
// 		if(! (contScrollWidth > contWidth))return 0;
// 		var oneScrollWith = trunc2(contScrollWidth / boxLen);
// 		var temp1 = $cont1[0].scrollLeft;
// 		var firstScrollLeft = trunc2(temp1/oneScrollWith) * oneScrollWith;// 강제로 스크롤했을때 보정 위해
// 		var scrLeft = firstScrollLeft + ((oneScrollWith * Math.ceil(contWidth/oneScrollWith)) * times);
// 		if(scrLeft + 10 > contScrollWidth){
// 			scrLeft = 0;// 넘어가면 처음으로 이동하자
// 		}
// 		return scrLeft;
// 	};
// 	var setAutoScroll = function(){
// 		setTimeout(function(){
// 			if(isScrolling){
// 				var scrLeft = isScrolling ? calcScrollLeft(1) : 0;
// 				$cont1.animate({scrollLeft: scrLeft}, animateTM, setAutoScroll);
// 			}else{
// 				setAutoScroll();
// 			}
// 		}, scrollTM);
// 	};
// 	// 마우스 오버 중일때는 자동 스크롤 안하게
// 	$cont1.hover(function(){
// 		isScrolling = false;
// 	}, function(){
// 		if(! stopScrolling){
// 			isScrolling = true;
// 		}
// 	});
// 	var doScroll = function(times){
// 		stopScrolling = true;
// 		isScrolling = false;
// 		var scrLeft = calcScrollLeft(times);
// 		$cont1.animate({scrollLeft: scrLeft}, animateTM);
// 	};
// 	self.doScroll = doScroll;
// 	setAutoScroll();
// };

// window.UiUtil = (function(){
// 	var setResizable = function(node, ops){
// 		// helper 안주면 이거 할 필요 없다.
// 		//ops = ops || {};
// 		//var debounceHeightAuto = _.debounce(function(){
// 		//	if(ops.resetHeight)$(node).css('height', '');
// 		//	if(ops.resetWidth)$(node).css('width', '');
// 		//}, 400);// 200 으로 하면 안먹음
// 		//ops.resize = function(){
// 		//	debounceHeightAuto();
// 		//	uu.gridResizeCanvasWithin();
// 		//};
// 		var _ops = $.extend(ops, {grid: 50, helper: null});//ops 가 null 이어도 되네
// 		$(node).resizable(_ops);
// 	};
// 	var toggle = function(node, isShow){
// 		if(arguments.length >= 2){
// 			$(node).toggle(!! isShow);
// 		}else{
// 			$(node).toggle();
// 		}
// 		uu.gridResizeCanvasWithin();
// 	};
// 	var setNodeHeightCompact = function(height, node){
// 		var p = $(node).parent();
// 		var children = p.children();
// 		var height2 = _.reduce(children, function(r, child){
// 			if($(child).css('position') != 'absolute' && ! $(child).is(node)){
// 				r += $(child).outerHeight();
// 			}
// 			return r;
// 		}, 0);
// 		$(node).outerHeight(Math.max(20, height - height2));
// 	};
// 	var calcParentCompactHeight = function(node){
// 		var p = $(node).parent();
// 		var children = p.children();
// 		return _.reduce(children, function(r, child){
// 			if($(child).css('position') != 'absolute'){
// 				r += $(child).outerHeight();
// 			}
// 			return r;
// 		}, 0);
// 	};
// 	var syncHeightOnReisze = function(nodes){// resize 할때 높이 맞추기
// 		var resizeFn = function(){
// 			var me = this;
// 			var height = calcParentCompactHeight(me);// 내 parent compact height 계산
// 			_.each(nodes, function(node){
// 				if(node != me){
// 					setNodeHeightCompact(height, node);
// 				}
// 			});
// 		};
// 		_.each(nodes, function(node){
// 			$(node).resizable('option', 'resize', resizeFn);
// 		})
// 	};
// 	var setResizableEast = function(node){
// 		// helper: 'ui-resizable-helper' 쓰면 resize 후 height 가 고정되 버리네
// 		UiUtil.setResizable(node, {handles: 'e', grid: 50});//, resetHeight: true
// 	};
// 	var setResizableSouth = function(node){
// 		UiUtil.setResizable(node, {handles: 's', grid: 50});//, resetWidth: true
// 	};
// 	var applyMyUi = function(div_app){
// 		_.each($('.uu-ui-resizable_e', div_app), UiUtil.setResizableEast);
// 		_.each($('.uu-ui-resizable_s', div_app), UiUtil.setResizableSouth);
// 		_.each($('.uu-ui-sync_h_cont', div_app), function(contNode){
// 			UiUtil.syncHeightOnReisze($('.uu-ui-sync_h_obj', contNode));
// 		});
// 	};
// 	var getClassList = function(node){
// 		return $(node).attr('class').split(/\s+/);
// 	};
// 	var removeClassByPrefix = function(node, prefix){
// 		var cList = getClassList(node);
// 		_.each(cList, function(cName){
// 			if(_.startsWith(cName, prefix)){
// 				$(node).removeClass(cName);
// 			}
// 		});
// 	};
// 	return {
// 		syncHeightOnReisze: syncHeightOnReisze,
// 		setResizable: setResizable,
// 		setResizableEast: setResizableEast,
// 		setResizableSouth: setResizableSouth,
// 		toggle: toggle,
// 		applyMyUi: applyMyUi,
// 		getClassList: getClassList,
// 		removeClassByPrefix: removeClassByPrefix
// 	};
// })();

// window.WebStorageUtil = {
// 	getItem: function(storage, key){
// 		var json = storage.getItem(key);
// 		try{
// 			return JSON.parse(json);
// 		}catch(err){
// 			return null;
// 		}
// 	},
// 	setItem: function(storage, key, val){
// 		var json = JSON.stringify(val);
// 		storage.setItem(key, json)
// 	},
// 	clear: function(storage){
// 		storage.clear();
// 	}
// };//WebStorageUtil
