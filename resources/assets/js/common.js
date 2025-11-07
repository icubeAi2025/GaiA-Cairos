(function () {
	window.commonJs = window.commonJs || {

		// 세션 저장소에 저장하기
		setSessionStorage(name, param) {
            sessionStorage.removeItem(name);

			sessionStorage.setItem(name, JSON.stringify(param));
        },

		// 세션 저장소에 데이터 꺼내기
		getSessionStorage(name) {
			let storageData = JSON.parse(sessionStorage.getItem(name));
			
			return storageData;
        },

		// 세션 저장소에 데이터 삭제
		delSessionStorage(name) {
            sessionStorage.removeItem(name);
        }

	}
})();