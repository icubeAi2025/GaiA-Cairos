window.CONTEXTPATH = '';
window._load_pcommon3 = true;
window.curl = function(...paths){
	if(paths.length == 0)return CONTEXTPATH;
	return [CONTEXTPATH].concat(paths).join('');
};
//url 경로 생성 메서드
window.apiurl = function(...paths){
	return curl(...paths); // url 컨벤션에 맞게 세팅
};
