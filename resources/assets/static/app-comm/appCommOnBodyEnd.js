(function(){
	const commInfo = commUt.getCommInfo();
	const isLogin = commInfo.loginUser != null;
	const pathname = location.pathname;

	try{

		if(! isLogin){
			let noLoginPathList = ['/main/', 'adminmain'];
			let isNoLoginPage = false;
			for(let path of noLoginPathList){
				if(_.includes(pathname, path)){
					isNoLoginPage = true;
					return;
				}
			}
			if(! isNoLoginPage){
				throw '로그인이 필요한 페이지 입니다.';
			}
		}

		var isDevPage = _.includes(pathname, '/test/') || _.includes(pathname, '/sample/') || _.includes(pathname, '/dev/');
		if(isDevPage && ! commInfo.isDevMode){
			throw '권한이 없는 페이지입니다.';
		}
	}catch(msg){
		$(document.body).hide(0, function(){
			alert(msg);
			if(window.opener){
				window.close();
			}else{
				location.href = curl('/');
			}
		});
		throw msg;// 이후 스크립트 실행 못하게 할려고
	}
})();

$(function(){
	var commInfo = commUt.getCommInfo();
	var loginUser = commInfo.loginUser;
	$('.show_when_login_y').toggle(loginUser != null);
	$('.show_when_login_n').toggle(loginUser == null);
	$('.show_when_admin_y').toggle(loginUser != null && loginUser.admin_yn == 'Y');
	if(loginUser != null){
		$('[data-login-val]').each(function(i, el){
			var prop1 = $(el).attr('data-login-val');
			$(el).text(loginUser[prop1]);
		});
	}
});
