(function () {
    let pjtMainInfo;
    let pageCommonData;
    let nowMenuId;
    let boardWindow = null;

    window.gaiaPortal = window.gaiaPortal || {
        /** 초기화 함수 */
        async init(pjtNo, pjt_nm, cntrctNo, cntrct_nm, pjt_div) {
            pjtMainInfo = {
                "pjtNo": pjtNo,
                "pjt_nm": pjt_nm,
                "cntrctNo": cntrctNo,
                "cntrct_nm": cntrct_nm,
                "pjt_div": pjt_div
            };
        },

        /** 초기화 함수 */
         async mainInit() {
			commonJs.delSessionStorage("pageCommonData");
            await gaiaPortal.mainMakeHome();
        },

        /** 초기화 함수 */
        async navMenuInit(menu_id, menu_title) {
            nowMenuId = menu_id;
            await gaiaPortal.makeNavMenu(menu_id, menu_title);
            let menuNameList = document.getElementById("menuDepth").children;
            // console.log("menuNameList: ", menuNameList[1].innerText);
            // console.log("menuNameList개수: ", menuNameList.length);
            /**
             * 2025-09-01 사이드 메뉴 active 범위 제한 (불필요한 메뉴까지 active 되던 문제 수정)
             * 전체 a 태그에서 검색하던 로직을 컨테이너 하위 검색으로 변경
             */
            let $menuContainer;
            for (let i = 1; i < menuNameList.length; i++) {
                let menuNm = menuNameList[i].innerText;
                let sideMenuItem;

                if (i === 1) {
                    // i가 1일 때는 span 태그를 찾고, 가까운 a 태그를 선택
                    sideMenuItem = $(`span[attr${i}='${menuNm}']`).closest('a');
                    sideMenuItem.addClass('active');
                    $menuContainer = sideMenuItem.siblings('ul.depth2_list');
                } else {
                    // 그 외의 경우에는 a 태그를 직접 찾음
                    sideMenuItem = $menuContainer.find(`a[attr${i}='${menuNm}']`);
                    sideMenuItem.addClass('active');
                }
                // console.log("sideMenuItem: ", sideMenuItem);
                // 사이드 메뉴에서 menuName과 일치하는 항목을 찾기
                sideMenuItem.toggleClass('on');
                sideMenuItem.css('opacity', '1');
                sideMenuItem.next('[data-list="submenu-list"]').slideToggle();
                // console.log("현재 메뉴: ",sideMenuItem);
                // console.log("현재 메뉴 부모: ",sideMenuItem.parent());
            }
        },

        /** WBSGEN 상단메뉴네비 함수 */
        async navWbsGenMenuInit(menu_name, menu_title) {

            let navMenuItem = `
            <h2 class="page_tit" id="menuName">${menu_title}</h2>
            <nav class="breadcrumb_nav">
                <ol class="breadcrumb" id="menuDepth">
                    <li class="breadcrumb_item"><a href="/">홈</a></li>
                   <li class="breadcrumb_item">${menu_name}</li>
                   <li class="breadcrumb_item">${menu_title}</li>
                </ol>
            </nav>
           `
            document.getElementById('pageNav').innerHTML = navMenuItem;

        },

        getQueryParams() {
            var params = {};
            var queryString = window.location.search.substring(1);
            var regex = /([^&=]+)=([^&]*)/g;
            var match;

            while ((match = regex.exec(queryString))) {
                var key = decodeURIComponent(match[1]);
                var value = decodeURIComponent(match[2]);
                params[key] = value;
            }
            return params;
        },
        /** 최초 메인 화면 구성(GAIA : 프로젝트 기준. CAIROS : 계약 기준) */
        async mainMakeHome() {
			commonJs.delSessionStorage("cntrctSelectBox");
            return new Promise((resolve, reject) => {

                $.ajax({
                    url: '/api/portal/main-home',
                    method: 'POST',
                    dataType: 'json',
                    contentType: 'application/json; charset-utf-8',
                    async: false,
                    success: function (data, status, xhr) {
                        gaiaPortal.mainMakeHomeHtml(data.details.userInfo, data.details.projectList);

                        let userInfo = data.details.userInfo.split(':');

                        if (data.details.pjt_add === "Y" && PLATFORM.CURRENT === "GAIA") {
                            document.getElementById("pjtInstall").style.display = 'block';
                        } 

                        var faqCategory = data.details.faqCategory;
                        faqCategory.forEach((faqCategory, index) => {
                            var categoryIndex = index + 1
                            $("#category").append(
                                $("<li>")
                                    .addClass("tab_item")
                                    .attr("data-id", "tabs0" + (categoryIndex))
                                    .attr("data-category", "F0" + (categoryIndex))
                                    .text(faqCategory.cmnCdNm)
                            )

                            $("#boardList").append(
                                $("<div>")
                                    .addClass("tab_conts")
                                    .attr("id", `tabs0${categoryIndex}`)
                                    .append(
                                        $("<p>")
                                            .addClass("faq_list")
                                            .attr("id", `list${categoryIndex}`)
                                    )
                            )
                            gaiaPortal.makeFaqBoard(faqCategory.cmnCd, categoryIndex)
                        });

                        var boardList = data.details.noticeList;

                        boardList.forEach((boardList, index) => {
                            const boardTitle = boardList.boardTitle;
                            const boardNo = boardList.boardNo;
                            const boardType = boardList.boardType;

                            var noticeList = `
                                <div class="notice_item">
                                    <span class="notice_label">공지</span>
                                    <a href="#"
                                       onclick="gaiaPortal.boardClick('${boardNo}', '${boardType}'); return false;"
                                       class="notice_tit">${boardTitle}</a>
                                </div>`;

                            $("#noticeList").append(noticeList);
                        });

                        gaiaPortal.makeSlid();

                        gaiaPortal.openPopup(data.details.popupMsgList);
                    }
                });

                resolve();
				$("#loadingMag").css('display', 'none');
				gaiaCommon.LoadingOverlay('body', false);
            });
        },

        //게시판 리스트 생성
        makeFaqBoard: function (boardCategory, categoryIndex) {
            var param = $.param({
                boardType: "2",
                boardCategory: boardCategory,
            });

            $.ajax({
                url: "/api/board/mainFaqList?" + param,
                method: "GET",
                dataType: "json",
                success: function (data) {
                    var list = data.details.boardList;
                    if (list.length != 0) {
                        //console.log(list)
                        list.forEach((boardList, index) => {
                            $(`#list${categoryIndex}`).append(
                                $("<a>")
                                    .attr("href", "#")
                                    .addClass("faq_item")
                                    .data("boardNo", boardList.boardNo)
                                    .data("boardType", boardList.boardType)
                                    .data("boardCategory", boardList.boardCategory)
                                    .text(boardList.boardTitle)
                                    .on("click", function (event) {
                                        event.preventDefault();
                                        const boardNo = $(this).data("boardNo");
                                        const boardType = $(this).data("boardType");
                                        const boardCategory = $(this).data("boardCategory");
                                        gaiaPortal.boardClick(boardNo, boardType, boardCategory);
                                    })
                            );
                        });
                    } else {
                        $(`#list${categoryIndex}`).append(
                            $("<p>")
                                .text(boardList.boardTitle || "데이터가 없습니다.")
                                .css({
                                    height: "150px",
                                    width: "100%",
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "center",
                                    textAlign: "center",
                                    color: "#999",
                                })
                        );

                    }

                },
                error: function (error) {
                    console.error("Error loading contract list:", error);
                },
            });
        },

        boardClick: function (boardNo, boardType, boardCategory) {
            const width = 1100;
            const height = 700;
            let left = Math.ceil((window.screen.width - width) / 2);
            left += window.screenLeft; // 듀얼 모니터일 때
            const top = Math.ceil((window.screen.height - height) / 2);

            const url = `/board/readMain?boardType=${boardType}&boardNo=${boardNo}&boardCategory=${boardCategory}`;

            if (boardWindow && !boardWindow.closed) {
                boardWindow.focus();
                boardWindow.location.href = url;
            } else {
                boardWindow = window.open(url, '_blank', `width=${width},height=${height},left=${left},top=${top}`);
            }

            window.addEventListener('beforeunload', () => {
                if (boardWindow && !boardWindow.closed) {
                    boardWindow.close();
                }
            });
        },

        makeSlid: function () {
            // Slick slide
            var $slideNoticeList = $('.notice_list');
            var $slideNoticeitem = $('.notice_item ');
            var enableNav = true;
            var speed = 1000;

            $slideNoticeitem.css({
                whiteSpace: "nowrap",
                overflow: "hidden",
                textOverflow: "ellipsis",
                paddingRight: "150px"
            });

            $slideNoticeList.on('init reInit afterChange', function (event, slick, currentSlide, nextSlide) {
                var currentSlide = slick.currentSlide + 1;
                var totalSlide = slick.$dots[0].children.length;
                $(".notice .slick_paging").html('<b class="page">' + currentSlide + '</b> / ' + totalSlide);
            });

            $slideNoticeList.slick({
                arrows: true,
                prevArrow: $('.notice .prev'),
                nextArrow: $('.notice .next'),
                vertical: true,
                dots: true,
                infinite: true,
                slidesToShow: 1,
                slidesToScroll: 1,
                autoplay: true,
                speed: 500,
                draggable: false
            });

        },


        /** 최초 메인 화면 구성(GAIA : 프로젝트 기준. CAIROS : 계약 기준) */
        mainMakeHomeHtml(user, projectList) {

            let userInfo = user.split(':');


            // Slick slide
            var $slidePjList = $('#pjtContent');

            if ($slidePjList.hasClass("slick-initialized")) {
                $slidePjList.slick("unslick");
            }

            // Project List
            $slidePjList.on(
                "init reInit afterChange",
                function (event, slick, currentSlide) {
                    var currentSlideIndex = slick.currentSlide + 1;
                    var totalSlide = slick.$dots[0].children.length;
                    $(".project .slick_paging").html(
                        '<b class="page">' +
                        Math.ceil(currentSlideIndex / 4) +
                        "</b> / " +
                        totalSlide
                    );
                }
            );

            if (projectList.length) {
                const setProjecItem = document.getElementById("pjtContent");

                // 자식 노드가 있는지 판별
                if (setProjecItem && setProjecItem.hasChildNodes()) {
                    // 모든 자식 노드를 삭제한다.
                    setProjecItem.replaceChildren();
                }

                $.each(projectList, function (index, obj) {

                    let view_name = (userInfo[2] === "PGAIA" || userInfo[2] === 'GAIA') ? obj.pjt_nm : obj.cntrct_nm;

                    let lv1_div = document.createElement('div');
                    lv1_div.setAttribute("class", "slick_item");

                    let lv2_div = document.createElement('div');
                    lv2_div.setAttribute("class", "box card");

                    let lv3_first_div = document.createElement('div');
                    lv3_first_div.setAttribute("class", "btn_area");

                    let lv3_second_div = document.createElement('div');
                    lv3_second_div.setAttribute("class", "pj_info");

                    let btn = document.createElement('button');
                    btn.setAttribute("type", "button");
                    btn.setAttribute("class", obj.favorites);
                    btn.setAttribute("data-btn-toggle", "favorites");
                    btn.setAttribute("onclick", "gaiaPortal.setFavorites('" + obj.pjt_no + "', '" + obj.cntrct_no + "', '" + userInfo[2] + "', '" + userInfo[0] + "', event)");
                    btn.innerHTML = `   <i class="ic ic-star-fill"></i>
                                        <span class="tooltip">즐겨찾기</span>`;

                    lv3_first_div.append(btn);
                    lv3_second_div.innerHTML = `<span class="label _ing">${obj.con_pstats_nm}</span>
                                                <strong class="pj_name"><a style="overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; line-height: unset;"
                                                href="javascript:;" onclick="gaiaPortal.homeMove('${obj.pjt_no}', '${obj.pjt_nm}', '${obj.cntrct_no}', '${obj.cntrct_nm}','${obj.pjt_div}');">${view_name}</a></strong>
                                                <p class="pj_etc_info">
                                                    <span class="pj_etc_item" style="height: 38px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis; ">${obj.plc_lct_adrs}</span>
                                                    <span class="pj_etc_item">${obj.pjt_period}</span>
                                                </p>`;

                    let lv3_first_ul = document.createElement('ul');
                    lv3_first_ul.setAttribute("class", "list_sty pj_status");
                    lv3_first_ul.style.height = "95px";
                    lv3_first_ul.style.display = "flex";
                    lv3_first_ul.style.justifyContent = "center";

                    let lv3_second_ul = document.createElement('ul');
                    lv3_second_ul.setAttribute("class", "list_sty audit_list");

                    lv3_first_ul.innerHTML = `  <li style="width: 76.5px; display: flex; flex-direction: column; align-items: center;">
                                                    <b class="status_tit" style="width:100%; text-align:center;">계획</b>
                                                    <span class="put_txt _per" style="width: 100%;">${obj.pjt_plan}</span>
                                                </li>
                                                <li style="width: 76.5px; display: flex; flex-direction: column; align-items: center;">
                                                    <b class="status_tit">실적</b>
                                                    <span class="put_txt _per" style="width: 100%;" >${obj.pjt_results}</span>
                                                </li>

                                                <!-- 공정률 범위에 따라 lv1 / lv2 / lv3 -->
                                                <li style="width: 76.5px; display: flex; flex-direction: column; align-items: center;">
                                                    <b class="${obj.pjt_completed_lv}">공정률</b>
                                                    <span class="put_txt _per" style="width: 100%;">${obj.pjt_completed}</span>
                                                </li>`;

                    lv3_second_ul.innerHTML = ` <li>
                                                    <span class="badge_wrap">
                                                        <i class="ic ic-audit_sch" style="cursor:pointer;" onClick="gaiaPortal.approvalMove('${obj.pjt_no}', '${obj.pjt_nm}', '${obj.cntrct_no}', '${obj.cntrct_nm}', '${obj.pjt_div}', '02');"></i>
                                                        <span class="badge">${obj.job_count}"</span>
                                                    </span>
                                                    <span class="audit_label">작업일보</span>
                                                </li>
                                                <li>
                                                    <span class="badge_wrap">
                                                        <i class="ic ic-audit" style="cursor:pointer;" onClick="gaiaPortal.approvalMove('${obj.pjt_no}', '${obj.pjt_nm}', '${obj.cntrct_no}', '${obj.cntrct_nm}', '${obj.pjt_div}', '01');"></i>
                                                        <span class="badge">${obj.draft_count}"</span>
                                                    </span>
                                                    <span class="audit_label">전자결재</span>
                                                </li>
                                                <li>
                                                    <span class="badge_wrap">
                                                        <i class="ic ic-audit_etc" style="cursor:pointer;" onClick="gaiaPortal.approvalMove('${obj.pjt_no}', '${obj.pjt_nm}', '${obj.cntrct_no}', '${obj.cntrct_nm}', '${obj.pjt_div}', 'etc');"></i>
                                                        <span class="badge">${obj.etc_count}"</span>
                                                    </span>
                                                    <span class="audit_label">기타</span>
                                                </li>`;

                    lv2_div.append(lv3_first_div);
                    lv2_div.append(lv3_second_div);
                    lv2_div.append(lv3_first_ul);
                    lv2_div.append(lv3_second_ul);

                    lv1_div.append(lv2_div);

                    if (setProjecItem) {
                        setProjecItem.appendChild(lv1_div);
                    }
                });

                $slidePjList.slick({
                    arrows: true,
                    prevArrow: $('.project .prev'),
                    nextArrow: $('.project .next'),
                    dots: true,
                    infinite: false,
                    slidesToShow: 4,
                    slidesToScroll: 4,
                    autoplay: false,
                    speed: 1000,
                    draggable: true
                });
            }
        },

        /** 홈 화면의 상단 네비게이션 박스 만들기 */
        async makeNavMenu(menu_id, menu_title) {

            return new Promise((resolve, reject) => {
                let param = {
                    menu_id: menu_id
                };

                $.ajax({
                    url: '/api/portal/nav-menu',
                    method: 'POST',
                    dataType: 'json',
                    contentType: 'application/json; charset-utf-8',
                    data: JSON.stringify(param),
                    async: false,
                    success: function (data, status, xhr) {
                        let navMenu = data.details.navMenu;

                        if (navMenu.length) {
                            let navMenuItem = `<h2 class="page_tit" id="menuName">${menu_title}</h2>`;
                            $.each(navMenu, function (index, obj) {
                                navMenuItem = navMenuItem + obj.menu_nm;
                            });
                            document.getElementById('pageNav').innerHTML = navMenuItem;
                        }
                    }
                });

                resolve();
            });
        },
        /** 최초 메인 종합프로젝트 화면에서 프로젝트 선택 시 이동 함수 */
        approvalMove(pjt_no, pjt_nm, cntrct_no, cntrct_nm, pjt_div, moveType) {

            pageCommonData = {
                "pjtNo": pjt_no,
                "pjt_nm": pjt_nm,
                "cntrctNo": cntrct_no,
                "cntrct_nm": cntrct_nm,
                "pjt_div": pjt_div
            };

            commonJs.setSessionStorage("pageCommonData", pageCommonData);

			if(gaiaCommon.me.isAdmin()) {
				window.location.href = `/eapproval/approval/waiting?auditType=${moveType}&pjtNo=${pjt_no}&cntrctNo=${cntrct_no}`;
			}else {
				gaiaCommon.get("/api/portal/change-pjt/check-authority/" + pjt_no + "/M060201/" + cntrct_no, {}, function (result) {
					if (result.details?.checkAuthority === 'Y') { //프로젝트 변경 시 변경 프로젝트의 현재 메뉴에 권한이 있을 경우 현재 페이지 reload
						window.location.href = `/eapproval/approval/waiting?auditType=${moveType}&pjtNo=${pjt_no}&cntrctNo=${cntrct_no}`;
					} else { //프로젝트 변경 시 변경 프로젝트의 현재 메뉴에 권한이 없을 경우 대시보드 메뉴로 이동
						gaiaCommon.customAlert('현재 메뉴에 대한 권한이 없습니다. 대시보드 메뉴로 이동합니다.');
						gaiaPortal.homeMove(pjt_no, pjt_nm, cntrct_no, cntrct_nm, pjt_div);
					}
				});
			}        
        },
        /** 최초 메인 종합프로젝트 화면에서 프로젝트 선택 시 이동 함수 */
        homeMove(pjt_no, pjt_nm, cntrct_no, cntrct_nm, pjt_div, documentType) {

            pageCommonData = {
                "pjtNo": pjt_no,
                "pjt_nm": pjt_nm,
                "cntrctNo": cntrct_no,
                "cntrct_nm": cntrct_nm,
                "pjt_div": pjt_div
            };

            commonJs.setSessionStorage("pageCommonData", pageCommonData);

            // 문서 타입이 있을경우 착공계 문서관리로 이동 없을경우 대쉬보드화면으로 이동
            if (documentType) {
                window.location.href = `/document?div=${documentType}&pjtNo=${pjt_no}&cntrctNo=${cntrct_no}`;
            } else {
                window.location.href = `/dashboard?pjtNo=${pjt_no}&cntrctNo=${cntrct_no}`;
            }
        },
        /** 좌측 메뉴 선택 시 이동 함수 */
        menuMove(url, useApi) {
			commonJs.setSessionStorage("moveUrl", url);
            //console.log("pjtMainInfo.pjtNo : >> " + pjtMainInfo.pjtNo);
            //console.log("pjtMainInfo.cntrctNo : >> " + pjtMainInfo.cntrctNo);

            if (url !== 'javascript:;') {
                gaiaPortal.resetItem();
                window.location.href = `${url}${url.search("\\?") > -1 ? "&" : "?"}pjtNo=${pjtMainInfo.pjtNo}&cntrctNo=${pjtMainInfo.cntrctNo}&useApi=${useApi}`;
            }
        },
        /** 상단 셀렉트 박스의 프로젝트 선택 함수 */
        selectProjectItem(pjt_no, pjt_nm, cntrct_no, cntrct_nm, pjt_div) {
            // 로그아웃 또는 초기 메인화면 생성시 로컬 스토리지 삭제 로직 추가
            gaiaPortal.resetItem();

            pageCommonData = {
                "pjtNo": pjt_no,
                "pjt_nm": pjt_nm,
                "cntrctNo": cntrct_no,
                "cntrct_nm": cntrct_nm,
                "pjt_div": pjt_div
            };
            commonJs.setSessionStorage("pageCommonData", pageCommonData);
			commonJs.delSessionStorage("cntrctSelectBox");

            // 현재 URL에서 '/form'이 있을 때만 제거
            let baseUrl = window.location.href; // 현재 URL을 기본으로 설정
            if (baseUrl.includes('/form')) {
                baseUrl = baseUrl.replace(/\/form(.*)$/, ''); // '/form' 이후의 모든 쿼리 제거
            }
            // 주소창의 URL을 수정
            window.history.replaceState(null, '', baseUrl);

			moveUrl = commonJs.getSessionStorage("moveUrl");

            if (nowMenuId === 'MAIN') {
                window.location.href = `${location.pathname}?pjtNo=${pjt_no}&cntrctNo=${cntrct_no}`;
            } else {
				if(gaiaCommon.me.isAdmin()) {
					if(gaiaPortal.getQueryParams().div) {
						window.location.href = `${location.pathname}?pjtNo=${pjt_no}&cntrctNo=${cntrct_no}&div=${gaiaPortal.getQueryParams().div}`;
					} else if(moveUrl) {
						window.location.href = `${moveUrl}?pjtNo=${pjt_no}&cntrctNo=${cntrct_no}`;
					} else {
                        gaiaPortal.homeMove(pjt_no, pjt_nm, cntrct_no, cntrct_nm, pjt_div);
					}
				}else {
					gaiaCommon.get("/api/portal/change-pjt/check-authority/" + pjt_no + "/" + (nowMenuId ? nowMenuId + "/" : "") + cntrct_no, {}, function (result) {
						if (result.details?.checkAuthority === 'Y') { //프로젝트 변경 시 변경 프로젝트의 현재 메뉴에 권한이 있을 경우 현재 페이지 reload
							
							if(gaiaPortal.getQueryParams().div) {
								window.location.href = `${location.pathname}?pjtNo=${pjt_no}&cntrctNo=${cntrct_no}&div=${gaiaPortal.getQueryParams().div}`;
							}else {
								window.location.href = `${moveUrl}?pjtNo=${pjt_no}&cntrctNo=${cntrct_no}`;
							}
						} else { //프로젝트 변경 시 변경 프로젝트의 현재 메뉴에 권한이 없을 경우 대시보드 메뉴로 이동
							gaiaCommon.customAlert('현재 메뉴에 대한 권한이 없습니다. 대시보드 메뉴로 이동합니다.');
							gaiaPortal.homeMove(pjt_no, pjt_nm, cntrct_no, cntrct_nm, pjt_div);
						}
					});
				}
            }
        },
        /** 최초 메인 종합프로젝트 화면에서 프로젝트 프로젝트 즐겨찾기 추가 함수 */
        setFavorites(pjt_no, cntrct_no, pjt_type, login_id, event) {
            let getClassNm = event.currentTarget;

            let param = {
                pjtNo: pjt_no,
                cntrctNo: cntrct_no,
                pjtType: pjt_type,
                loginId: login_id,
                favoritesYN: getClassNm.className
            };

            $.ajax({
                url: '/api/portal/set-favorites',
                method: 'POST',
                dataType: 'json',
                contentType: 'application/json; charset-utf-8',
                data: JSON.stringify(param),
                async: false,
                success: function (data, status, xhr) {
                    let msg = data.details.msg;
                    gaiaCommon.customAlert(msg);
                }
            });
        },
        /** 최초 메인 종합프로젝트 화면의 검색 함수 */
        totalPjtSearch(searchType, checkMsg1, checkMsg2, checkMsg3) {
            let searchItem = document.getElementById('searchItem');
            let searchText = document.getElementById('searchText');
            let favoritesSearch = document.getElementById('FavoritesSearch').className;

            if (searchType === 'TextSearch') {
                if (!searchItem.value) {
                    gaiaCommon.customAlert(checkMsg1);
                    document.getElementById('searchItem').focus();
                    return;
                }
                if (!searchText.value) {
                    gaiaCommon.customAlert(checkMsg2);
                    document.getElementById('searchText').focus();
                    return;
                }

                if (favoritesSearch === 'icon_btn _outline') {
                    favoritesSearch = 'icon_btn _outline favorites';
                } else {
                    favoritesSearch = 'icon_btn _outline';
                }
            } else {
                if (!searchItem.value || !searchText.value) {
                    searchItem.selectedIndex = '0';
                    searchText.value = null;
                }
            }
            // console.log(favoritesSearch);

            let param = {
                searchItem: searchItem.value,
                searchText: searchText.value,
                favoritesSearch: favoritesSearch
            };

            $.ajax({
                url: '/api/portal/main-home-search',
                method: 'POST',
                dataType: 'json',
                contentType: 'application/json; charset-utf-8',
                data: JSON.stringify(param),
                async: false,
                success: function (data, status, xhr) {
                    if (data.details.projectList.length) {
                        gaiaPortal.mainMakeHomeHtml(data.details.userInfo, data.details.projectList);
                    } else {
                        gaiaCommon.customAlert(checkMsg3);
                    }

                }
            });

        },

        customConfirm(msg1, msg2, msg3, cb, cancel_cb) {
            if (envMode === 'local' || envMode === 'dev') {
                gaiaCommon.customConfirm("[gaiaCommon.customConfirm()으로 변경하세요] " + msg1, msg2, msg3, cb, cancel_cb);
            } else {
                gaiaCommon.customConfirm(msg1, msg2, msg3, cb, cancel_cb);
            }
        },

        customAlert(msg, cb, options) {
            if (envMode === 'local' || envMode === 'dev') {
                gaiaCommon.customAlert("[gaiaCommon.customAlert()으로 변경하세요] " + msg, cb, options);
            } else {
                gaiaCommon.customAlert(msg, cb, options);
            }
        },

        resetItem() {
            sessionStorage.removeItem('pCntrctNo');
            sessionStorage.removeItem('dCntrctNo');
            sessionStorage.removeItem('drCntrctNo');
            sessionStorage.removeItem('cCntrctNo');
            sessionStorage.removeItem('cCntrctId');
        },
        openPopup(popupMsgList) {
            const wrapper = document.getElementById("wrapper_popup");
            const overlay = document.getElementById("popup_overlay");
            let activePopup = null;
            let offsetX = 0;
            let offsetY = 0;

            const onMouseMove = (e) => {
                if (!activePopup) return;
                activePopup.style.top = `${e.clientY - offsetY}px`;
                activePopup.style.left = `${e.clientX - offsetX}px`;
            };

            const onMouseUp = () => {
                activePopup = null;
                document.removeEventListener('mousemove', onMouseMove);
                document.removeEventListener('mouseup', onMouseUp);
            };

            let hasPopup = false;

            let coorNo = 1;
            popupMsgList.forEach((obj, index) => {
                const cookieCheck = gaiaPortal.getTodayCloseCookie(`hd_pops_${obj.pop_msg_cd}`);
                if (cookieCheck !== "N") {
                    hasPopup = true;

                    const popupDiv = document.createElement('div');
                    popupDiv.className = "popup-box";
                    popupDiv.id = `hd_pops_${obj.pop_msg_cd}`;
                    popupDiv.style.maxHeight = "calc(100vh - 100px)";
                    popupDiv.innerHTML = `<h2>${obj.pop_title}</h2>
					<p>${obj.pop_content}</p>`;

                    popupDiv.innerHTML = `
                            <div class="resize-handle-tr"></div>
                            <div class="resize-handle-tl"></div>
                            <div class="header-popup" style="color : ${isCAIROS() ? 'var(--color-white)' : 'var(--color-default)'};">
                                <div style="user-select: none;">공지팝업</div>
                                <button type="button" id="close-btn" onclick="gaiaPortal.closePopup('hd_pops_${obj.pop_msg_cd}', 'I')">
                                        <i class="ic ic-close" style="color : ${isCAIROS() ? 'var(--color-white)' : 'var(--color-default)'};"></i>
                                </button>
                            </div>
                            <div class="body-popup">
                                <div class="title-popup" style="display:flex;">
                                    <b style="min-width: 40px;">[알림]</b><b>${obj.pop_title}</b>
                                </div>
                                <div class="content-popup">
                                    <p>${obj.pop_content}</p>
                                    ${obj.link_nm ? `
                                    <div class="link-box">
                                        <button type="button" style="color:${isCAIROS() ? 'var(--color-white)' : 'var(--color-default)'}" onclick="gaiaPortal.homeMove(${obj.link_url})">${obj.link_nm}</button>
                                    </div>` : ``}
                                </div>
                            </div>
                            <div class="footer-popup">
                                <button onclick="gaiaPortal.closePopup('hd_pops_${obj.pop_msg_cd}', 'D')" class="hour-close">24시간동안 열지 않음</button>
                                <button onclick="gaiaPortal.closePopup('hd_pops_${obj.pop_msg_cd}', 'I')">닫기</button>
                            </div>
                            <div class="resize-handle-br"></div>
                            <div class="resize-handle-bl"></div>
                            `;



                    const header = popupDiv.querySelector('.header-popup');
                    header.addEventListener('mousedown', (e) => {
                        activePopup = popupDiv;
                        offsetX = e.clientX - popupDiv.offsetLeft;
                        offsetY = e.clientY - popupDiv.offsetTop;
                        document.addEventListener('mousemove', onMouseMove);
                        document.addEventListener('mouseup', onMouseUp);
                    });

                    popupDiv.style.position = "fixed";
                    popupDiv.style.visibility = "hidden";
                    popupDiv.style.display = "block";
                    wrapper.appendChild(popupDiv);

                    // 팝업 크기 측정
                    const popupWidth = popupDiv.offsetWidth;
                    const popupHeight = popupDiv.offsetHeight;

                    // 정중앙 위치 계산
                    popupDiv.style.left = `${(window.innerWidth - popupWidth) / 2}px`;
                    popupDiv.style.top = `${(window.innerHeight - popupHeight) / 2}px`;
                    popupDiv.style.visibility = "visible";

                    gaiaPortal.attachResizeHandlers(popupDiv);

                    if (wrapper) {
                        wrapper.appendChild(popupDiv);

                        popupDiv.addEventListener("mousedown", () => {
                            const allPopups = document.querySelectorAll(".popup-box");
                            let maxZ = 11;

                            allPopups.forEach(p => {
                                const z = parseInt(window.getComputedStyle(p).zIndex || 0);
                                if (z > maxZ) maxZ = z;
                            });
                            popupDiv.style.zIndex = maxZ + 1;
                        });
                    }

                    if (coorNo < 5) {
                        coorNo++;
                    } else {
                        coorNo = 1;
                    }
                }
            });

            if (hasPopup) {
                overlay.style.display = "block";

            }
        },

        // 팝업 닫기
        closePopup(id, type) {
            const overlay = document.getElementById("popup_overlay");
            if (type === 'D') {
                gaiaPortal.setTodayCloseCookie(id, "N", 1);
            }
            const el = document.getElementById(id);
            if (el) el.style.display = "none";

            const remaining = [...document.querySelectorAll("#wrapper_popup > .popup-box")]
                .filter(p => p.style.display !== "none");

            if (remaining.length === 0) {
                overlay.style.display = "none";
            }
        },

        setTodayCloseCookie(id, value, days) {
            const date = new Date();
            date.setDate(date.getDate() + days);
            document.cookie = `${escape(id)}=${escape(value)}; expires=${date.toUTCString()}; path=/`;
        },

        //오늘 그만보기 쿠키 가져오기
        getTodayCloseCookie(IdName) {
            var cookie = document.cookie;
            if (document.cookie != "") {
                var cookie_array = cookie.split("; ");
                for (var index in cookie_array) {
                    var cookie_name = cookie_array[index].split("=");
                    if (cookie_name[0] == IdName) {
                        return cookie_name[1];
                    }
                }
            }
            return;
        },

        // 리사이즈 핸들 이벤트 등록 함수
        attachResizeHandlers(popupDiv) {
            const MIN_WIDTH = 500;
            const MIN_HEIGHT = 189;
            const popupWidth = popupDiv.offsetWidth;
            const popupHeight = popupDiv.offsetHeight;

            const startResize = (e, direction) => {
                e.preventDefault();

                const startX = e.clientX;
                const startY = e.clientY;
                const startWidth = popupDiv.offsetWidth;
                const startHeight = popupDiv.offsetHeight;
                const startTop = popupDiv.offsetTop;
                const startLeft = popupDiv.offsetLeft;

                const onMouseMove = (e) => {
                    popupDiv.style.maxHeight = "";

                    const dx = e.clientX - startX;
                    const dy = e.clientY - startY;

                    let newWidth = startWidth;
                    let newHeight = startHeight;
                    let newLeft = startLeft;
                    let newTop = startTop;

                    if (direction.includes("r")) {
                        newWidth = Math.max(MIN_WIDTH, startWidth + dx);
                    }

                    if (direction.includes("b")) {
                        newHeight = Math.max(MIN_HEIGHT, startHeight + dy);
                    }

                    if (direction.includes("l")) {
                        const attemptedWidth = startWidth - dx;
                        if (attemptedWidth >= MIN_WIDTH) {
                            newWidth = attemptedWidth;
                            newLeft = startLeft + dx;
                        } else {
                            newWidth = `${(window.innerWidth - popupWidth) / 2}px`;
                            newLeft = `${(window.innerWidth - popupWidth) / 2}px`;
                        }
                    }

                    if (direction.includes("t")) {
                        const attemptedHeight = startHeight - dy;
                        if (attemptedHeight >= MIN_HEIGHT) {
                            newHeight = attemptedHeight;
                            newTop = startTop + dy;
                        } else {
                            newHeight = `${(window.innerHeight - popupHeight) / 2}px`
                            newTop = `${(window.innerHeight - popupHeight) / 2}px`
                        }
                    }

                    popupDiv.style.width = newWidth + "px";
                    popupDiv.style.height = newHeight + "px";
                    popupDiv.style.left = newLeft + "px";
                    popupDiv.style.top = newTop + "px";
                    popupDiv.style.transform = "";
                };

                const onMouseUp = () => {
                    document.removeEventListener("mousemove", onMouseMove);
                    document.removeEventListener("mouseup", onMouseUp);
                };

                document.addEventListener("mousemove", onMouseMove);
                document.addEventListener("mouseup", onMouseUp);
            };

            const handles = [
                { selector: '.resize-handle-tr', direction: 'tr' },
                { selector: '.resize-handle-tl', direction: 'tl' },
                { selector: '.resize-handle-br', direction: 'br' },
                { selector: '.resize-handle-bl', direction: 'bl' },
            ];

            handles.forEach(({ selector, direction }) => {
                const handle = popupDiv.querySelector(selector);
                if (handle) {
                    handle.addEventListener('mousedown', (e) => startResize(e, direction));
                }
            });
        }


    };
})();