String.prototype.replaceAll = function (org, dest) {
    return this.split(org).join(dest);
}

let pjtInfo;
let contractCount; //셀렉트 사업의 계약갯수 저장
let param = gaiaCommon.getQueryParams();
let storageParam = commonJs.getSessionStorage("pageCommonData");
let cntrctSelectBoxParam = commonJs.getSessionStorage("cntrctSelectBox");
let normalConn = true;
const path = location.pathname;

const gaia = {
    loaded: false,
    complete: function() {
        console.log('complete')
        setDefaultImage();
    },
    approvalMove: function (moveType) {
		if(gaiaCommon.me.isAdmin()) {
			window.location.href = `/eapproval/approval/waiting?auditType=${moveType}&pjtNo=${pjtInfo.pjtNo}&cntrctNo=${pjtInfo.cntrctNo}`;
		}else {
			gaiaCommon.get("/api/portal/change-pjt/check-authority/" + pjtInfo.pjtNo + "/M060201/" + pjtInfo.cntrctNo, {}, function (result) {
				if (result.details?.checkAuthority === 'Y') { //프로젝트 변경 시 변경 프로젝트의 현재 메뉴에 권한이 있을 경우 현재 페이지 reload
					window.location.href = `/eapproval/approval/waiting?auditType=${moveType}&pjtNo=${pjtInfo.pjtNo}&cntrctNo=${pjtInfo.cntrctNo}`;
				} else { //프로젝트 변경 시 변경 프로젝트의 현재 메뉴에 권한이 없을 경우
					gaiaCommon.customAlert(message.msg064);
				}
			});
		}        
    },
    getSearchData: function () {
        const data = sessionStorage.getItem(path);
        return JSON.parse(data || '{}');
    },
    setSearchData: function (searchData) {
        sessionStorage.setItem(path, JSON.stringify( Object.assign({}, param, searchData )));
    },
    generateUUID: function () {
        // Web Crypto 사용.
        const bytes = new Uint8Array(16);
        window.crypto.getRandomValues(bytes);

        // UUID v4 포맷 설정
        bytes[6] = (bytes[6] & 0x0f) | 0x40; // version 4
        bytes[8] = (bytes[8] & 0x3f) | 0x80; // variant bits

        // 바이트를 16진수 문자열로 변환
        const hex = Array.from(bytes, byte => byte.toString(16).padStart(2, '0')).join('');

        // UUID 포맷으로 조립
        return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}`;
    },
    goBack: function () {
        if (document.referrer.search(location.origin) > -1) {
            if (document.referrer.search('/login') > -1) {
                location.href = '/';
            } else {
                location.href = document.referrer;
            }
        } else {
            location.href = '/';
        }
    },
    utils: {
        createUploader : async (options) => {
            let uploader = {
                files: []
            };

            return await new Promise( async (resolve, reject) => {
                uploader = await tware.utils.uploaderCreate(options);
                resolve(uploader);
            });
        }
        , loadFileUploader() {
            return new Promise((resolve, reject) => {
                $('#hiddenSection').load('/assets/templates/vue-templates.html', (res) => {
                    console.log('template loaded completed.');
                    resolve();
                });
            });
        }
    }
};

gaia.init = async function (params, createdFn) {
    toggleUpButton();
    gaia.constants = $.extend({}, tware.constants, gaia.constants);

    var regexp = /\/adm\/.*\/(.*)\.do/;
    var viewId;

    params = params || {};
    params.path = path;
    params.data = {};
    params.query = tware.utils.parseQuery();
    params.search = {};

    console.log('params.query', params.query)
    if (params.query._condition === 'init') {
        // params.search = tware.utils.parseSerializeValues(sessionStorage.getItem(path));
        params.search = JSON.parse(sessionStorage.getItem(path));
    } else {
        delete params.search;
        sessionStorage.removeItem(path);
    }

    // console.log('params.hash:', params.hash);

    if (regexp.test(path)) {
        viewId = path.match(regexp)[1];
        params.viewId = viewId;
    }

    await preProcess();

    if (createdFn && typeof createdFn === 'function') {
        createdFn.call(this, params);
    }

    await postProcess();

    function bindEvent() {
        // TAB
        if (path !== '/eapproval/draft/create-draft' && path !== '/eapproval/draft/view-tempDraft' && path !== '/eapproval/approval/detail' && path !== '/eapproval/draft/render-draft') {
            var tabItem = $(".tab_item");
            var tabConts = $(".tab_conts");

            $(tabItem).eq(0).addClass('active');
            $(tabConts).eq(0).addClass('active');

            $(tabItem).click(function () {
                $(tabItem).removeClass('active');
                $(tabConts).removeClass('active');

                $(this).addClass('active');
                $("#" + $(this).data('id')).addClass('active');
            });
        }
    }

    async function preProcess() {
        await setUserInfo();
        await setProjectInfo();

        await gaia.utils.loadFileUploader();

        setHeaderLogo();

        // [dsjung] 2025-05-28 헤더가 있는 경우에만 수행하도록 변경
        /** 메인 좌측 메뉴 및 상단 프로젝트 선택 박스 만들기 */

        const header = document.querySelector('header');
        const lnb = document.querySelector('nav');

        console.log('header', header)
        console.log('gaia.isPopup', !$('.modal').hasClass('open'))

        if (header && lnb && !$('.modal').hasClass('open')) {
            if (path !== '/') {
                await makeProjectSelectAndSideMenu();
            	//await makeQuickMenu();
            }
        }

        if (normalConn) {
            console.log("base_content.peb log : " + JSON.stringify(pjtInfo));
            gaiaPortal.init(pjtInfo.pjtNo, pjtInfo.pjt_nm, pjtInfo.cntrctNo, pjtInfo.cntrct_nm, pjtInfo.pjt_div);
        }

        if (!$('.modal').hasClass('open')) {
            initUi();
        }
    }

    async function postProcess() {
        bindEvent();

        if (isGAIA()) {
            if ($('.lnb_nav').length > 0) {
                const selectPjtItem = document.getElementById("selectPjtItem");
                if(selectPjtItem) {
                    selectPjtItem.style.display = 'flex';
                }
            } else {
                const selectDbdItem = document.getElementById("selectDbdItem");
                if(selectDbdItem) {
                    selectDbdItem.style.display = 'flex';
                }
            }
        } else {
            if ($('.lnb_nav').length > 0) {
                const selectPjtItem = document.getElementById("selectPjtItem");
                if(selectPjtItem) {
                    selectPjtItem.style.display = 'flex';
                }
            }
        }

        $('.family_link').show()

        $("body").fadeIn(600);

        const content = document.getElementById("content");
        if (content) {
            // DOM 변화 감지
            const observer = new MutationObserver(toggleUpButton);
            observer.observe(content, {
                childList: true,
                subtree: true
            });
        }

        window.addEventListener("resize", toggleUpButton);
        toggleUpButton();
    }

    function setHeaderLogo() {
        if (isGAIA()) {
            $("body").attr("class", "gaia");
        } else {
            $("body").attr("class", PLATFORM.CURRENT.toLowerCase());
            if (isCAIROS()) {
                $('.brand').css('width', 130);
            }
        }

		let title = (PLATFORM.CURRENT === "CAIROS") ? "CaiROS" : "GaiA";

        $('.brand').children('a').text(title);
    }

    function setUserInfo() {
        return new Promise((resolve, reject) => {
			gaiaCommon.me.info = JSON.parse(decrypt(me));
			
            const info = gaiaCommon.me.info;

            let currentDept = {}

            if (info.admin) {
                $('#goBackoffice').remove();

                $('.login_info').prepend(tware.utils.handlebarCompile($("#layout-header-template").html(), {}));

                $('#goBackoffice').prop('href', `/auth/bo`);
            }

            if (info.departments) {
                for (let i = 0, size = info.departments.length; i < size; i++) {
                    let dept = info.departments[i];

                    if ( info.admin && ( dept.pjtNo === 'ADMIN' && dept.cntrctNo === 'ADMIN' ) ) {
                        currentDept = dept;
                        break;
                    }

                    this.queryString = new URLSearchParams(window.location.search);

                    // cntrctNo : "ADMIN"
                    // corpNo : null
                    // deptDscrpt : "슈퍼관리부서"
                    // deptId : "A01"
                    // deptLvl : 1
                    // deptNm : "슈퍼관리부서"
                    // deptNo : 37
                    // dltYn : "N"
                    // dsplyOrdr : 0
                    // dsplyYn : "Y"
                    // mngNm : "아이디어"
                    // pjtNo : "ADMIN"
                    // pjtType : ""
                    // pstnNm : "부장"
                    // svrType : "09"
                    // upDeptId : "A"
                    // useYn : "Y"
                    if (this.queryString.has('pjtNo') && this.queryString.has('cntrctNo')) {
                        const pjtNo = this.queryString.get('pjtNo');
                        const cntrctNo = this.queryString.get('cntrctNo');

                        if (dept.pjtNo === pjtNo && dept.cntrctNo === cntrctNo) {
                            currentDept = dept;
                            break;
                        }
                    }
                }
            }

            /**
             * 2025-02-04
             * #795 워크샵 피드백 - 포털 상단에 메일이 아닌 이름 노출로 변경
             */
			
            $('#userNm').text( decodeURIComponent(info.name || '') );
			if(currentDept.deptDscrpt) {				
            	$('#userType').text( currentDept.deptDscrpt || '-' )
                                .attr('data-dept-id', currentDept.deptId || ''); // 부서 id data 속성 추가
			}else {
				$('.user_belong').attr('data-after', '');
			}
            resolve();
        });
    }

    function setProjectInfo() { 
        return new Promise((resolve, reject) => {
            if (!storageParam && param.pjtNo && param.cntrctNo) {
                normalConn = false;

                $.ajax({
                    url: "/api/portal/select-pjtNm-cntrctNm/" + param.pjtNo + "/" + param.cntrctNo,
                    method: "GET",
                    contentType: "application/json",
                    dataType: "json",
                    success: function (data, status, xhr) {
                        console.log(data.details?.pjtNmCntrctNm);
                        pjtInfo = {
                            "pjtNo": data.details?.pjtNmCntrctNm.pjt_no,
                            "pjt_nm": data.details?.pjtNmCntrctNm.pjt_nm,
                            "cntrctNo": data.details?.pjtNmCntrctNm.cntrct_no,
                            "cntrct_nm": data.details?.pjtNmCntrctNm.cntrct_nm,
                            "pjt_div": data.details?.pjtNmCntrctNm.pjt_div
                        };

                        commonJs.setSessionStorage("pageCommonData", pjtInfo);

                        gaiaPortal.init(pjtInfo.pjtNo, pjtInfo.pjt_nm, pjtInfo.cntrctNo, pjtInfo.cntrct_nm,pjtInfo.pjt_div);

                        resolve();
                    },
                    error: function (xhr) {
                        console.error(
                            "Error :",
                            xhr.responseText
                        );
                    },
                });
            } else if (storageParam && !cntrctSelectBoxParam) {
                pjtInfo = {
                    "pjtNo": storageParam.pjtNo,
                    "pjt_nm": storageParam.pjt_nm,
                    "cntrctNo": storageParam.cntrctNo,
                    "cntrct_nm": storageParam.cntrct_nm,
                    "pjt_div": storageParam.pjt_div
                };

                resolve();
            }else if (storageParam && cntrctSelectBoxParam) {
                pjtInfo = {
                    "pjtNo": storageParam.pjtNo,
                    "pjt_nm": storageParam.pjt_nm,
                    "cntrctNo": cntrctSelectBoxParam.cntrctNo,
                    "cntrct_nm": cntrctSelectBoxParam.cntrct_nm,
                    "pjt_div": storageParam.pjt_div
                };

                resolve();
            } else {
                const prj = gaiaCommon.me.info.projects[0]

                pjtInfo = {
                    "pjtNo": prj?.pjt_no,
                    "pjt_nm": prj?.pjt_nm,
                    "cntrctNo": prj?.cntrct_no,
                    "cntrct_nm": prj?.cntrct_nm,
                    "pjt_div": prj?.pjt_div
                };

                commonJs.setSessionStorage("pageCommonData", pjtInfo);

                resolve();
            }
        });
    }

    function makeProjectSelectAndSideMenu() {
        return new Promise((resolve, reject) => {
            let params = {
                pjtNo: pjtInfo.pjtNo,
                cntrctNo: pjtInfo.cntrctNo,
            };

            $.ajax({
                url: '/api/portal/left-Menu-userPjt',
                method: 'POST',
                dataType: 'json',
                contentType: 'application/json; charset-utf-8',
                async: false,
                data: JSON.stringify(params),
                success: function (data, status, xhr) {
                    let menuList = data.details.menuList;
                    let projectList = data.details.projectList;
                    let quickMenuList = data.details.quickMenuList;
                    let userInfo = data.details.userInfo.split(':');

                    if ($('.lnb_nav').length > 0) {
                        if (menuList.length) {
                            let setMenuItem = document.getElementById("menuItem");
                            let li;
                            let ul;
                            let subLi;
                            let subUl;
                            let beforeLv = 0;
                            let menuMakeCount = 1;

                            $.each(menuList, function (index, obj) {

                                if (obj.menu_lvl === 1) {
                                    if (beforeLv === 1) {
                                        setMenuItem.appendChild(li);
                                    } else if (beforeLv === 2) {
                                        ul.append(subLi);
                                        li.append(ul);
                                        setMenuItem.appendChild(li);
                                    } else if (beforeLv === 3) {
                                        subLi.append(subUl);
                                        ul.append(subLi);
                                        li.append(ul);
                                        setMenuItem.appendChild(li);
                                    }
                                    li = document.createElement("li");
                                    if (obj.menu_url === "javascript:;") {
                                        li.setAttribute("class", "lnb_item has-sub");
                                    } else {
                                        li.setAttribute("class", "lnb_item");
                                    }

                                    let menuTatle = document.createElement("a");
                                    menuTatle.setAttribute("class", "depth1");
                                    menuTatle.setAttribute("href", obj.menu_url);
                                    $(menuTatle).html(`${gaiaCommon.decodeSafeText(obj.icon_nm)}<span class="m_name" attr1="${obj.menu_nm}">${obj.menu_nm}</span>`)
                                    // menuTatle.innerHTML = ;

                                    li.append(menuTatle);

                                    if (menuMakeCount === menuList.length) {
                                        setMenuItem.appendChild(li);
                                    }

                                } else if (obj.menu_lvl === 2) {
                                    if (obj.menu_lvl > beforeLv) {
                                        ul = document.createElement("ul");
                                        ul.setAttribute("class", "depth2_list");
                                        ul.setAttribute("data-list", "submenu-list");
                                    } else if (beforeLv === 2) {
                                        ul.append(subLi);
                                    } else if (beforeLv === 3) {
                                        subLi.append(subUl);
                                        ul.append(subLi);
                                    }
                                    subLi = document.createElement("li");
                                    if (obj.menu_url === "javascript:;") {
                                        subLi.setAttribute("class", "lnb_item has-sub");
                                    } else {
                                        subLi.setAttribute("class", "lnb_item");
                                    }
                                    $(subLi).html(`<a href="javascript:;" onclick="gaiaPortal.menuMove('${obj.menu_url}', '${obj.menu_api}');" attr2="${obj.menu_nm}">${obj.menu_nm}</a>`)
                                    // subLi.innerHTML = ;

                                    if (menuMakeCount === menuList.length) {
                                        ul.append(subLi);
                                        li.append(ul);
                                        setMenuItem.appendChild(li);
                                    }
                                } else if (obj.menu_lvl === 3) {
                                    if (obj.menu_lvl > beforeLv) {
                                        subUl = document.createElement("ul");
                                        subUl.setAttribute("class", "depth3_list");
                                        subUl.setAttribute("data-list", "submenu-list");
                                    }
                                    var lastLi = document.createElement('li');
                                    lastLi.setAttribute("class", "lnb_item");
                                    if (obj.count) {
                                        $(lastLi).html(`<a href="javascript:;" onclick="gaiaPortal.menuMove('${obj.menu_url}', '${obj.menu_api}');"attr3="${obj.menu_nm}">${obj.menu_nm} ${gaiaCommon.decodeSafeText(obj.count)}</a>`);
                                        // lastLi.innerHTML = ;
                                    } else {
                                        $(lastLi).html(`<a href="javascript:;" onclick="gaiaPortal.menuMove('${obj.menu_url}', '${obj.menu_api}');"attr3="${obj.menu_nm}">${obj.menu_nm}</a>`);
                                        // lastLi.innerHTML = ;
                                    }

                                    subUl.append(lastLi);

                                    if (menuMakeCount === menuList.length) {
                                        subLi.append(subUl);
                                        ul.append(subLi);
                                        li.append(ul);
                                        setMenuItem.appendChild(li);
                                    }

                                }
                                beforeLv = obj.menu_lvl;
                                menuMakeCount++;
                            });
                        }
                    }

                    if (projectList.length) {
                        // mainDiv 및 subDiv 생성 및 추가
                        let mainDiv = $('#pj_list_box');
                        let subDiv = $('#selectList');

                        if (document.getElementById('selectProject')) {
                            if (userInfo[2] === 'PGAIA' || userInfo[2] === 'GAIA') {
                                document.getElementById('selectProject').textContent = pjtInfo.pjt_nm || '데이터 없음';
                            } else {
								if(userInfo[1] === 'ADMIN') {
									document.getElementById('selectProject').textContent = pjtInfo.pjt_nm || '데이터 없음';
									document.getElementById('selectProject').dataset.cntrctNo = pjtInfo.pjtNoNo;
								}else {
									document.getElementById('selectProject').textContent = pjtInfo.cntrct_nm || '데이터 없음';
									document.getElementById('selectProject').dataset.cntrctNo = pjtInfo.cntrctNo;
								}
                            }
                        }

                        if (userInfo[1] !== 'ADMIN' && userInfo[1] !== '관리자' && userInfo[2] !== 'PGAIA' && userInfo[2] !== 'GAIA') {
                            mainDiv.addClass('user_groups2');
                        } else {
                            mainDiv.addClass('user_groups1');
                        }

                        // AJAX 호출 현재 함수 호출 시 셋팅하도록 수정
                        // gaiaCommon.get(`/api/project/select/${pjtInfo.pjtNo}/${pjtInfo.cntrctNo}`, {}, function (result) {
                        //     // 결과 처리 (필요시)
                        //     // console.log(result);
                        // });

                        // subList 생성
						projectList.forEach((obj, index) => {
							let selectItem;
							let str1 = `<span class="td" title="${obj.pjt_nm}">${obj.pjt_nm}</span>`;
							let str2 = `<span class="td" title="${obj.cntrct_nm}">${obj.cntrct_nm}</span>`;

							if(obj.pjt_no === pjtInfo.pjtNo && obj.cntrct_no === pjtInfo.cntrctNo) {
								contractCount = obj.contract_count;
							}

							if (!gaiaCommon.me.isAdmin() && isCAIROS()) {
								selectItem = `<a href="javascript:void(0)" onclick="gaiaPortal.selectProjectItem('${obj.pjt_no}', '${obj.pjt_nm}', '${obj.cntrct_no}', '${obj.cntrct_nm}', '${obj.pjt_div}')">`.concat(str1, str2, '</a>');
							}else {
								selectItem = `<a href="javascript:void(0)" onclick="gaiaPortal.selectProjectItem('${obj.pjt_no}', '${obj.pjt_nm}', '${obj.cntrct_no}', '${obj.cntrct_nm}', '${obj.pjt_div}')">`.concat(str1, '</a>');
							}

							subDiv.append(selectItem);
						});

                        // 이벤트 리스너 추가
                        mainDiv.on("mouseleave", function () {
                            $('.pj_link').toggleClass('show');
                        });
                    } else {
                        document.getElementById('selectProject').textContent = '선택 가능한 프로젝트가 없습니다.';
						$("#menuItem").css('display', 'none');
                    }

	 				makeQuickMenu(quickMenuList);

                    resolve();
                }
            });
        });
    }
}

function makeQuickMenu(quickMenuList) {
	let approval = quickMenuList.approval;

	var auditIcon = ['ic-audit_sch', 'ic-audit', 'ic-audit_etc']
	var auditLabel = [message.itemDash041, message.itemDash042, message.itemDash043]
	var auditCount = [approval.work_count, approval.drat_count, approval.etc_count]
	var auditType = ['02', '01', 'etc']
	auditIcon.forEach((icon, index) => {
		$("#auditList").append(`
				<li>
					<a href="javascript:void(0)" onclick="gaia.approvalMove('${auditType[index]}')"  style='  pointer-events: auto;'>
						<span class="badge_wrap">
							<i class="ic ${icon}"></i>
							<span class="badge">${auditCount[index]}</span>
						</span>
						<span class="audit_label">${auditLabel[index]}</span>
					</a>
				</li>`
		);
	});

	if(!gaiaCommon.me.isAdmin()) {

		let supervision = quickMenuList.supervision;
		if(supervision.checkyn === 'Y') {
			$("#requestSide").css('display', 'block');
			let request = quickMenuList.request;

			request.forEach((item, index) => {
				$("#requestList").append(`
						<li>
							<a href="${item.uri}" style='  pointer-events: auto;'>
								<span class="badge_wrap">
									<i class="ic ic-audit"></i>
									<span class="badge">${item.cnt}</span>
								</span>
								<span class="audit_label">${item.app_nm}</span>
							</a>
						</li>`
				);
			});
		}
	}
  
    // return new Promise((resolve, reject) => {
    //     $.ajax({
    //         url: `/api/dashboard/mainAudit`,
    //         method: "POST",
    //         contentType: "application/json",
    //         data: JSON.stringify({
    //             pjtNo: pjtInfo.pjtNo,
    //             cntrctNo: pjtInfo.cntrctNo
    //         }),
    //         success: function (data) {
    //             //결재
    //             var audit = data.details.audit
    //             var auditIcon = ['ic-audit_sch', 'ic-audit', 'ic-audit_etc']
    //             var auditLabel = [message.itemDash041, message.itemDash042, message.itemDash043]
    //             var auditCount = [audit.workCount, audit.dratCount, audit.etcCount]
    //             var auditType = ['02', '01', 'etc']

    //             if (audit) {
    //                 auditIcon.forEach((icon, index) => {
    //                     $("#auditList").append(`
    //                             <li>
    //                                 <a href="javascript:void(0)" onclick="gaia.approvalMove('${auditType[index]}')"  style='  pointer-events: auto;'>
    //                                     <span class="badge_wrap">
    //                                         <i class="ic ${icon}"></i>
    //                                         <span class="badge">${auditCount[index]}</span>
    //                                     </span>
    //                                     <span class="audit_label">${auditLabel[index]}</span>
    //                                 </a>
    //                             </li>`
    //                     );
    //                 });
    //             }

    //             // 날씨정보 - 20241230 추가 , 20250630 임시주석처리
    //             // let weatherObj = {};
    //             // weatherObj.address_info = data.details.address_info;
    //             // weatherObj.weather = data.details.weather;
    //             // generateWeatherData(weatherObj);

    //             resolve();
    //         }
    //     })
    // });
}

function generateWeatherData (data) {

    /**
     * 날씨 변수
     */
    const sunny = "1";                  // 맑음
    const many_cloudy = "3";            // 구름많음
    const cloudy = "4";                 // 흐림

    const none = "0";                   // 없음
    const rain = "1";                   // 비
    const snow_rain = "2";              // 비/눈
    const snow = "3";                   // 눈
    const shower = "4";                 // 소나기
    const raindrop = "5";               // 빗방울
    const raindrop_blowingsnow = "6";   // 빗방울/눈날림
    const blowingsnow = "7";            // 눈날림
    let tmp = '';    // 기온
    let tmn = '';    // 일 최저기온
    let tmx = '';    // 일 최고기온
    let sky = '';   // 하늘
    let pty = '';   // 강수형태
    let pcp = '';   // 강수량

    let sunrise = null;     // 일출시간
    let sunset = null;      // 일몰시간

    // 일출, 일몰정보 API 호출 함수
    let getSunriseSunsetInfo = function (data) {
        const DESTINATION_URL = new URL("https://api.sunrise-sunset.org/json");

        const addressInfo = data.address_info || {};
        const lat = addressInfo.PLC_LCT_Y;
        const lng = addressInfo.PLC_LCT_X;

        if (lat && lng) {
            const params = {
                lat: lat,
                lng: lng,
                tzid: 'Asia/Seoul'
            };
        DESTINATION_URL.search = new URLSearchParams(params).toString();

			fetch(decodeURI(DESTINATION_URL))
				.then(res => {
					//console.log(res);
					return res.json();
				})
				.then(data => {
					if (data.results !== '') {
						let formatTimeTo24Hour = function (type, inputTime) {
							const now = new Date();

							// 날짜 추출
							const year = now.getFullYear();
							const month = (now.getMonth() + 1).toString().padStart(2, "0"); // 0-based
							const day = now.getDate().toString().padStart(2, "0");

							// 입력 형식: "7:45:06 AM" 또는 "5:24:50 PM"
							// 12시간 형식의 시간을 24시간 형식으로 변환
							const [time, modifier] = inputTime.split(" "); // "7:45:06", "AM" 또는 "5:24:50", "PM"

							// 시각 추출 및 형식화
							let [hours, minutes, seconds] = time.split(":").map(Number); // 시, 분, 초 추출
							if (modifier === "PM" && hours !== 12) {
								hours += 12; // 오후일 때 12를 더함
							}
							if (modifier === "AM" && hours === 12) {
								hours = 0; // 자정(AM 12시)은 0시로 변환
							}

							// 두 자릿수로 변환
							const formattedHours = hours.toString().padStart(2, "0");
							const formattedMinutes = minutes.toString().padStart(2, "0");

							// HH:mm 형태로 반환
							if (type === 'text') return `${formattedHours}:${formattedMinutes}`;
							if (type === 'datetime') return `${year}-${month}-${day} ${formattedHours}:${formattedMinutes}`;
						}
						sunrise = data.results.sunrise;
						sunset = data.results.sunset;

						$('.weather .sunrise')
							.text(`일출 : ${formatTimeTo24Hour('text', sunrise)}`)
							.attr('datetime', formatTimeTo24Hour('datetime', sunrise));
						$('.weather .sunset')
							.text(`일몰 : ${formatTimeTo24Hour('text', sunset)}`)
							.attr('datetime', formatTimeTo24Hour('datetime', sunrise));
					}
				})
				.catch(err => {
					// error 처리
					console.log('Fetch Error', err);
				});
		}
    }

    // 현재시간 포맷팅
    let formatDate = function (type, hours) {
        const date = new Date();
        const days = ["일", "월", "화", "수", "목", "금", "토"];

        // 날짜 요소 추출
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0'); // 0부터 시작하므로 +1
        const day = date.getDate();
        const dayOfWeek = days[date.getDay()]; // 요일 추출
        //const hours = date.getHours();
        const minutes = date.getMinutes();

        // 숫자를 2자리로 맞추기
        const paddedMinutes = minutes.toString().padStart(2, "0");

        // 형식 조합
        if (type === 'text') { return `${month}.${day}(${dayOfWeek}) ${hours}:${paddedMinutes}`; }
        if (type === 'datetime') { return `${year}-${month}-${day} ${hours}:${paddedMinutes}`; }
        if (param === 'today') { return `${year}-${month}-${day}`; }
    }

    // fcstTime 기준으로 데이터 그룹화 함수
    let groupByFcstTime = function (data) {
        return data.reduce((acc, curr) => {
            const key = curr.fcstTime;

            // key가 없으면 새로운 배열로 초기화
            if (!acc[key]) {
                acc[key] = [];
            }

            // 현재 항목을 해당 key에 추가
            acc[key].push(curr);

            return acc;
        }, {});
    }

    if (data.weather === null) {
        $('.weather').hide();

    } else {

        getSunriseSunsetInfo(data);

        let weatherDataList = data.weather;

        // weatherDataList 정보가 없다면 return
        if (weatherDataList === undefined) return false;

        // weatherDataList 정보가 있다면 show
        $('.weather').show();
        const groupedData = groupByFcstTime(weatherDataList);                       // 예보시간 기준으로 그룹핑한다.
        let key_arr = Array.from(Object.keys(groupedData)).sort();
        let fcstTime = new Date().getHours().toString().padStart(2, "0") + '00';    // 기준예보시간

        let fcstData = groupedData[fcstTime];
        tmp = fcstData.filter(item => item.category === 'TMP')[0].fcstValue;

        pcp = fcstData.filter(item => item.category === 'PCP')[0].fcstValue;
        if (pcp === '강수없음') { pcp = '0.0'; }
        else { pcp = pcp.match(/-?\d+(\.\d+)?/g)[0]; }

        let todayTmp = weatherDataList.filter(item => item.category === 'TMP');

        // 기상청 제공 최고기온/최저기온은 실황 값과 괴리가 있어 가공하여 변경사용

        tmn = todayTmp.reduce((min, item) => (Number(item.fcstValue) < Number(min.fcstValue) ? item : min), todayTmp[0]);
        tmx = todayTmp.reduce((max, item) => (Number(item.fcstValue) > Number(max.fcstValue) ? item : max), todayTmp[0]);

        // tmn = todayTmp.reduce((min, item) => (item.fcstValue < min.fcstValue ? min : item), todayTmp[0]);
        // tmx = todayTmp.reduce((max, item) => (item.fcstValue > max.fcstValue ? item : max), todayTmp[0]);
        //let tmn = groupedData['0600'].filter(elem => elem.category === 'TMN')[0].fcstValue;
        //let tmx = groupedData['1500'].filter(elem => elem.category === 'TMX')[0].fcstValue;

        let hours = fcstTime.substring(0, 2);
        $('.weather .updated_at')
            .text(formatDate('text', hours))
            .attr('datetime', formatDate('datetime', hours));

        $('.weather .location').text(data.address_info.PLC_LCT_ADRS); // 주소
        $('.weather .today_tmp').text(tmp);                      // 현재기온
        $('.weather .minmax .min').text(tmn.fcstValue);          // 최저기온
        $('.weather .minmax .max').text(tmx.fcstValue);          // 최고기온
        $('.weather .rainfall').text(pcp);                       // 강수량
    }
}

gaia.create = function(definePage) {
    tware.Class(definePage, gaia.init)();
}

Object.defineProperty(gaia, 'loaded', {
    get: function() {
        return this.value;
    },
    set: function(newValue) {
        this.value = newValue;
        gaia.complete();
    }
});