/********************************************************************************************
 * vue 관련 공통 설정
 ********************************************************************************************/
Vue.config.productionTip = false;
var staticVue = new Vue();
var globalSession = getSession();
var globalDefaultInfo = getDefaultInfo(); 
var vm = new Vue({});

/********************************************************************************************
 * vue 객체에 Dataset Type 선언
 ********************************************************************************************/
Vue.prototype.ROWTYPE_EMPTY = 0;
Vue.prototype.ROWTYPE_NORMAL = 1;
Vue.prototype.ROWTYPE_INSERT = 2;
Vue.prototype.ROWTYPE_UPDATE = 4;
Vue.prototype.ROWTYPE_DELETE = 8;
Vue.prototype.ROWTYPE_GROUP = 16;


/********************************************************************************************
 * 푸시 수신 전역 변수 선언
 ********************************************************************************************/
Vue.prototype.pushAlarmCount = self == top ? {'mailCnt':0 ,'notyCnt':0 ,'msgCnt':0 } : parent.staticVue.pushAlarmCount;

/********************************************************************************************
 * vue 객체에 공통코드 선언
 ********************************************************************************************/
// 통합 공통코드 유동처리 wjjoo 2022.09.21
axios.get('/system-manager/service/codeUseList', {}).then(function (response) {
	var data = response.data;
	for(var i = 0 ; i < data.length ; i++){
		if(data[i].COMMCODE_USE_YN == 'Y'){
			Vue.prototype[data[i].SERVICE_CD.toLowerCase() + 'Commcode'] = self == top ? new CommcodeSet(data[i].SERVICE_CD.toUpperCase(), true) : parent.staticVue[data[i].SERVICE_CD.toLowerCase() + 'Commcode'];
		}
	}
});

/********************************************************************************************
 * vue 객체에 서비스코드 선언
 ********************************************************************************************/
Vue.prototype.serviceCode = self == top ? new CustomcodeSet('/system-manager/service/initList') : parent.staticVue.serviceCodeSet;

/********************************************************************************************
 * vue 객체에 global Param 변수 선언
 ********************************************************************************************/
Vue.prototype.vueGlobalParam = '';

/********************************************************************************************
 * vue 객체에 Loading Overlay 함수 선언
 ********************************************************************************************/
Vue.prototype.LoadingOverlay = function(pTarget, TfVal){
	try{
		$(pTarget).LoadingOverlay(TfVal ? "show" : "hide", TfVal);
	}catch(err){
		return false;
	}
}

/********************************************************************************************
 * vue 객체에 mask 함수 선언
 ********************************************************************************************/
Vue.prototype.mask = function(pType, pVal){
	try{
		if(pType == 'email'){
			const len = pVal.split('@')[0].length - 3;
			
			return pVal.replace(new RegExp('.(?=.{0,' + len + '}@)', 'g'), '*');
		}else if(pType == 'tel*'){
			var x = pVal.replace(/\D/g, '').match(/(\d{3})(\d{4})(\d{4})/);
			
			return '(' + x[1] + ') ' + '****' + '-' + x[3];
		}else if(pType == 'tel'){
			var x = pVal.replace(/(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})/,"$1-$2-$3");
			
			return x;
		}else if(pType == 'YYYY-MM-DD'){
			var returnVal = moment(pVal, 'YYYYMMDD').format(pType);
			
			if(returnVal == 'Invalid date') return '';	
			else return returnVal;
		}else if(pType == 'HH:mm'){
			var returnVal = moment(pVal, 'HH:mm').format(pType);
			
			if(returnVal == 'Invalid date') return '';	
			else return returnVal;
			return returnVal;
		}
		
	}catch(err){
		return false;
	}
}

/********************************************************************************************
 * vue 객체에 filter 선언
 ********************************************************************************************/
Vue.filter('numberFormat', function (value) {
    if(!value) return ''
    if(typeof value == 'number') value = String(value);

    return value.toFixed(0).replace(/(\d)(?=(\d{3})+(?:\.\d+)?$)/g, "$1,");
    
//    return value.split('').reverse().reduce((acc, digit, i) => {
//        if (i > 0 && i % 3 === 0) acc.push(',')
//        return [...acc, digit]
//    }, []).reverse().join('')
})

/********************************************************************************************
 * vue 객체에 logout 함수 선언
 ********************************************************************************************/
Vue.prototype.logout = function(){
	try{
		var returnVal = axios.post('/logout');
		
		returnVal.then(function(response) {
			if(self == top){
				window.location.href = window.location.origin;
			}else{
				parent.window.location.href = window.location.origin;
			}
		});	
	}catch(err){
		return false;
	}
}

/********************************************************************************************
 * vue 객체에 Loading Overlay 함수 선언
 ********************************************************************************************/
Vue.prototype.vueFleUpload = function(pTarget, pUploadPath){
	try{
		var files = pTarget.files;
		var formData = new FormData();
		
		formData.append('upload_path', pUploadPath);
		for(var i=0 ; i < files.length ; i++){
			var index = i+1;
			formData.append('file'+index, files[i]);
		}
		
		return axios.post('/commons/file/file-upload',
			formData,
	        {
	            headers: {
	                'Content-Type': 'multipart/form-data'
	            },
	        }
	    ).catch(function(error) {
			return false;
		});
	}catch(err){
		return false;
	}
}

/********************************************************************************************
 * 페이지 오픈
 ********************************************************************************************/
$(document).ready(function() {
	try{
		if(typeof VeeValidate == 'function') Vue.use(VeeValidate,{locale: 'ko'});
		if(typeof VCalendar == 'object') Vue.use(VCalendar);
		if(typeof Vue2PerfectScrollbar == 'object') Vue.use(Vue2PerfectScrollbar);
		if(typeof VueToast == 'object') Vue.use(VueToast);
		//VueClipboard.config.autoSetContainer = true;
		//Vue.use(VueClipboard);
		if(typeof VueMask == 'object') Vue.directive('mask', VueMask.VueMaskDirective);
		if(typeof tui.Grid == 'function') tuiGridInit();
		
		document.addEventListener('keydown', function(event) {
			var noeExistsEl = ['TEXTAREA','DIV'];
			
			if(noeExistsEl.indexOf(event.target.tagName) == -1 && event.keyCode === 13){
				event.preventDefault();
			}
		}, true);
		
		globalSession = getSession();
		globalSession.then(function(response) {
			//header.html mounted 로 이동
			//staticVue.webSockConn(); 
		}).catch(function(error) {
			return null;
		});
		
		globalDefaultInfo = getDefaultInfo();
		globalDefaultInfo.then(function(response) {
			if(self == top){
				document.title = response.data.SITE_NM;
				
				if(isPc() && !isChrome()){
					//notifySubmit('info', '알림', response.data.SITE_NM+'은 크롬(Chrome)에서 최적화되어있습니다. 크롬 사용을 권장합니다', 'icon-caution', 150000);
				}else if(isPc() && !isChrome() && !isIE11OrMore()){
					alert(response.data.SITE_NM+'은 크롬(Chrome) 및 IE11이상\n에서 사용가능합니다.\n크롬 다운로드 페이지로 이동합니다.');
					location.href= "https://www.google.com/intl/ko_ALL/chrome/";
				}
			}else{
				document.title = response.data.SITE_NM;
				parent.document.title = response.data.SITE_NM;
			}

			globalDefaultInfo = response.data;
		}).catch(function(error) {
			return null;
		});
		
		setLoadingOverlayDefaultSetting();
	}catch(err){
		return false;
	}
});

//vue 객체에 사용자 세션정보 선언
function getSession(){
	try{
		var returnVal = axios.get('/session-info');
		
		returnVal.then(function(response) {
			Vue.prototype.session = response.data;
		}).catch(function(error) {
			return null;
		});
		
		return returnVal;
	}catch(err){
		return false;
	}
}

//vue 객체에 고객 기본정보 설정
function getDefaultInfo(){
	try{
		var returnVal = axios.get('/system-manager/default-info/representative');
		
		returnVal.then(function(response) {
			Vue.prototype.defaultInfo = response.data;
		}).catch(function(error) {
			return null;
		});
		
		return returnVal;
	}catch(err){
		return false;
	}
}

function setLoadingOverlayDefaultSetting(session){
	try{
		//var userGb = session.data.USER_GB;
		//var color = userGb == 3 || userGb == 5 || userGb == 9 ? '#8743ae' : userGb == 1 || userGb == 2 ? '#6954b8' : '#4873ca';
		
		$.LoadingOverlaySetup({
		    background      : "rgba(255, 255, 255, 0.0)",
		    image           : "/images/common/loading_spin_blue.svg",
//		    imageAnimation  : "1.5s fadein",
		    minSize			: 50,
		    maxSize			: 80,
//	 	    imageColor      : color,
		});
	}catch(err){
		return false;
	}
}

//input file 이미지 미리보기
function getThumbnailPrivew(html, $target) {
	try{
	    if (html.files && html.files[0]) {
	        var reader = new FileReader();
	        reader.onload = function(e) {
	            $target.css('display', 'block');
	            $target.html('<img  id="single_img_box" src="' + e.target.result + '" border="0" alt="" />');
	        }
	        reader.readAsDataURL(html.files[0]);
	    }
	}catch(err){
		return false;
	}
}

function modal_open(el) {
	try{
	    var temp = $('#' + el);
	
        temp.fadeIn();
	
	    if (temp.outerWidth() < $(document).width()) temp.css('margin-left', '-' + temp.outerWidth() / 2 + 'px');
	    else temp.css('left', '0px');
	
	    temp.find('a.cbtn, a.layer_close, .cbtn2').off('click');
	    temp.find('a.cbtn, a.layer_close, .cbtn2').click(function(e) {
	    	if($(this).parents('.modal-container').length <= 1){
	    		temp.fadeOut();
		        $("html").css("overflow", "auto");
		        $(this).parents('.modal-layer-wrap').eq(0).removeClass('on');
	            e.preventDefault();
	    	}
	    });
	    
	    $("html").css("overflow", "hidden");
	    temp.parents('.modal-layer-wrap').eq(0).find(temp).parent().addClass('on');
	    
	    
	    /*var temp = $('#' + el);
	    var bg = temp.prev().hasClass('bg'); 
	
	    if (bg) { 
	    	$('.layer').fadeIn();
	    } else {
	        temp.fadeIn();
	    }
	
	    if (temp.outerWidth() < $(document).width()) temp.css('margin-left', '-' + temp.outerWidth() / 2 + 'px');
	    else temp.css('left', '0px');
	
	    temp.find('a.cbtn, a.layer_close, .cbtn2').click(function(e) {
	        if (bg) {
	            $('.layer').fadeOut();
	        } else {
	            temp.fadeOut();
	            e.preventDefault();
	        }
	    });
	    
	    $('.layer .bg').click(function(e) {
	        $('.layer').fadeOut();
	        e.preventDefault();
	    });
	
	    $("html").css("overflow", "hidden");
	
	    temp.find('.cbtn, .layer_close, .cbtn2').fadeIn().click(function() {
	        $(this).fadeOut()
	        $("html").css("overflow", "auto");
	        $(this).parents('.modal-layer-wrap').eq(0).removeClass('on');
	    });
	
	    //modal background dark
	    temp.parents('.modal-layer-wrap').eq(0).find(temp).parent().addClass('on');*/
	    return true;
	}catch(err){
		return false;
	}
}

function modal_close(el) {
	try{
		var temp = $('#'+el); //레이어의 id를 temp변수에 저장var temp = $('#'+el); //레이어의 id를 temp변수에 저장
		$("html").css("overflow", "auto");
	    temp.hide();
	    $('#' + el).parents('.modal-layer-wrap').eq(0).removeClass('on');
	    return true;
	}catch(err){
		return false;
	}
}

function pad(n, width) {
	  n = n + '';
	  return n.length >= width ? n : new Array(width - n.length + 1).join('0') + n;
}

function tuiGridInit() {
	tui.Grid.setLanguage('ko'); // set Korean
	tui.Grid.applyTheme('striped', {
   		outline: {
   			border: '#dcdff1',
   			showVerticalBorder: true,
   		},
   		area: {
   			header: {
   				background: '#4f5685',
   			}
   		},
   		row: {
   			even: {
   				background: '#f7f8fb',
   				text: '#555',
   			},
   			hover: {
   				background: '#e2e2e3',
   			}
   		},
   		cell: {
   			normal: {
   		      background: '#fff',
   		      border: '#dcdff1',
   		      text: '#555',
   		    },
   			header: {
   				background: '#4f5685',
   				border: '#9a9dac',
   				text: '#fff',
   				showVerticalBorder: true,
   			},
   			rowHeader: {
   				background: '#f9f9f9',
   				text: '#555',
   			},
   			selectedHeader: {
   				background: '#383d61',
   			},
   			normal: {
   				background: '#fff',
   				border: '#dcdff1',
   				text: '#555',
   				showVerticalBorder: true,
   			},
   			summary: {
   				background: '#eaecf1',
   				border: '#dcdff1',
   				text: '#555',
   				showVerticalBorder: true,
   				//showHorizontalBorder: false,
   			}
   		},
   		frozenBorder: {
   			border: '#eaecf1',
   		}
   	    /* grid: {
   	        border: '#aaa',
   	        text: '#333'
   	    },
   	    cell: {
   	        disabled: {
   	            text: '#999'
   	        }
   	    } */
   	});
}
