(function () {

	pdfViewWin = null;

    // var gaiaCommon = {};
    window.gaiaCommon = window.gaiaCommon || {
        data: {
            commonCode: {},
            menu: {},
        },

        /**
         * 타이틀이 있는 로거입니다. (삭제예정)
         */
        log(title, message) {
            // console.log(`===[${title}]===`);
            // console.log(message);
        },

        /**
         * ajax get
         */
        // [dsjung] 2025-05-21 complete 처리 콜백 추가
        get(url, params, cb, errorCallback,complete) {
            $.ajax({
                url: url,
                method: "GET",
                xhrFields: {
                    withCredentials: true,
                },
                data: params,
                success(result) {
                    if (result?.ok || result.result) {
                        cb(result);
                    } else {
                        gaiaCommon.customAlert(result?.message || "error");
                        if (errorCallback !== null && errorCallback !== undefined) {
                            errorCallback(result);
                        }
                        else{
                            console.log(result)
                        }
                    }
                },
                error(error) {
                    gaiaCommon.LoadingOverlay('body', false);
                    // [dsjung] 2025-05-20 error 핸들링을 위한 콜백 추가
                    if (errorCallback !== null && errorCallback !== undefined) {
                        errorCallback(error);
                    }
                    else{
                        console.log(error)
                    }
                },
                complete(xhr){
                    if(complete){
                        complete(xhr);
                    }
                    else{
                        console.log(`${url} 처리 완료, XHR :`,xhr);
                    }
                }
            });
        },
        // [dsjung] 2025-09-02 이진데이터 처리 함수 추가
        /**
         * 이진데이터 처리하기
         *
         * @param url ({@link String}) 처리를 위한 url
         * @param params ({@link Object}) 요청시 전송할 데이터
         * @param cb ({@link Function}) 성공시 수행할 콜백 데이터 function(data,textStatus,xhr)
         * <ul>
         *     <li>data : 응답된 Blob 데이터</li>
         *     <li>textStatus : 응답된 문자열 메세지</li>
         *     <li>xhr : 응답된 XMLHttpRequest 객체</li>
         * </ul>
         * @param errorCallback ({@link Function}) 실패시 수행할 콜백 데이터
         * @param complete ({@link Function}) ajax 처리 완료시 수행할 콜백 데이터
         */
        getBlob(url, params, cb, errorCallback,complete) {
            $.ajax({
                url: url,
                method: "GET",
                xhrFields: {
                    responseType: 'blob',
                    withCredentials: true,
                },
                data: params,
                success: function(data, textStatus, xhr) {
                    if (xhr.status === 200 && cb) {
                        cb(data, xhr);
                    } else {
                        gaiaCommon.customAlert(textStatus || "error");
                        if (errorCallback !== null && errorCallback !== undefined) {
                            errorCallback(xhr);
                        }
                        else{
                            console.log(xhr)
                        }
                    }
                },
                error: function(error) {
                    console.log(error);
                    if (errorCallback !== null && errorCallback !== undefined) {
                        errorCallback(error);
                    }
                    else{
                        console.log(error)
                    }
                },
                complete(xhr){
                    if(complete){
                        complete(xhr);
                    }
                    else{
                        console.log(`${url} 처리 완료, XHR :`,xhr);
                    }
                }
            });
        },

        /**
         * ajax post
         */
        post(url, params, cb, errorCallback,complete) {
            // [dsjung] 2025-05-21 외부에서 formData를 생성하여 params로 넘긴 경우 처리
            if(params instanceof FormData){
                gaiaCommon.postForm(url,params,cb,errorCallback,complete);
                return;
            }
            $.ajax({
                url: url,
                method: "POST",
                dataType: "json",
                xhrFields: {
                    withCredentials: true,
                },
                contentType: "application/json; charset-utf-8",
                data: JSON.stringify(params),
                success(result,status,xhr) {
                    if (result?.ok && cb) {
                        cb(result);
                    } else {
                        if (errorCallback !== null && errorCallback !== undefined) {
                            errorCallback(result,status,xhr);
                        }
                        else{
                            gaiaCommon.customAlert(result?.message || "error");
                            console.log(result)
                        }
                    }
                },
                error(xhr,status,error) {
                    gaiaCommon.LoadingOverlay('body', false);
                    // [choisr] 2024-12-19 error 핸들링을 위한 콜백 추가
                    if (errorCallback !== null && errorCallback !== undefined) {
                        errorCallback(xhr,status,error);
                    }
                    else{
                        console.log(error)
                    }
                },
                complete(xhr){
                    if(complete){
                        complete(xhr);
                    }
                    else{
                        console.log(`${url} 처리 완료, XHR :`,xhr);
                    }
                }
            });
        },

        /**
         * ajax post
         */
        async postForm(url, params, cb, errorCallback,complete) {
            var formData;
            // [dsjung] 2025-05-21 외부에서 formData를 생성하여 params로 넘긴 경우 처리
            if(params instanceof FormData){
                if(params.keys().next().done){
                    gaiaCommon.customAlert("제출된 데이터 없음.")
                    return;
                }
                formData = params;
            }
            else{
                formData = new FormData();
                $.each(params, function (key, value) {
                    formData.append(key, value);
                });
            }

            console.log('postForm url', url)

            $.ajax({
                url: url,
                method: "POST",
                dataType: "json",
                xhrFields: {
                    withCredentials: true,
                },
                contentType: false,
                processData: false,
                data: formData,
                beforeSend: function(jqXHR, settings) {
                    console.log('jqXHR', jqXHR)
                    console.log('settings', settings)
                //     const headers = {
                //         platform: PLATFORM.CURRENT
                //         , requestUrl: jqXHR.url
                //         , requestId: uuid
                //         , remoteAddr: jqXHR.remoteAddr
                //         , userId: gaiaCommon.me.info ? gaiaCommon.me.info.usrId : ''
                //         , dateTime: dayjs().format('YYYYMMDDHHmmss')
                //         , referer: location.pathname
                //         , commonData: commonData
                //     }
                //
                //     jqXHR.setRequestHeader("X-REQUEST_COMMON_HEADER", JSON.stringify(headers));
                },
                success(result) {
                    if (result?.ok) {
                        cb(result);
                    } else {
                        gaiaCommon.customAlert(result?.message || "error");
                        if (errorCallback !== null && errorCallback !== undefined) {
                            errorCallback(result);
                        }
                        else{
                            console.log(result)
                        }
                    }
                },
                error(error) {
                    gaiaCommon.LoadingOverlay('body', false);
                    // [dsjung] 2025-05-20 error 핸들링을 위한 콜백 추가
                    if (errorCallback !== null && errorCallback !== undefined) {
                        errorCallback(error);
                    }
                    else{
                        console.log(error)
                    }
                },
                 complete(xhr){
                     if(complete){
                         complete(xhr);
                     }
                     else{
                         console.log(`${url} 처리 완료, XHR :`,xhr);
                     }
                 }
            });
        },

        customConfirm(msg1, msg2, msg3, cb, cancel_cb) {
            $('.alertModal.fade').show();
            $('#popBoxAlert').show();

            $('.pop_tit').text(msg1);
            $('.msg_tit').text(msg2);
            $('.msg_tit_dlt').text(msg3);
            $('.msg_tit_dlt').css('white-space', 'pre-line'); //줄바꿈 처리
            $('#popBoxAlert').addClass('on');

            document.body.style.overflow = 'hidden';

            // 공통 취소 처리 함수
            function handleCancel() {
                if (cancel_cb) {
                    cancel_cb();
                }
                $("#popBoxAlert").hide();
                $('.alertModal.fade').hide();
                document.body.style.overflow = 'unset';
            }

            // 기존 클릭 이벤트 제거 (이벤트 핸들러 중복 등록 방지)
            $("#pop_box_cancle").off("click");
            $("#pop_box_confirm").off("click");
            $(".icon_btn.pop_close i").off("click");

            // 취소 관련 버튼과 X 아이콘 공통 처리
            $("#pop_box_cancle").click(handleCancel);
            $(".icon_btn.pop_close i").click(handleCancel);

            $("#pop_box_confirm").click(function () {
                if (cb) {
                    cb();
                }
                $("#popBoxAlert").hide();

                $('.alertModal.fade').hide();
                document.body.style.overflow = 'unset';
            });
        },

        customDateConfirm(msg1, msg2, msg3, cb, cancel_cb) {
            $('.dateConfirmModal.fade').show();
            $('#dateModal').show();

            $('.pop_tit').text(msg1);
            $('.msg_tit').text(msg2);
            $('.msg_tit_dlt').text(msg3);
            $('.msg_tit_dlt').css('white-space', 'pre-line'); //줄바꿈 처리
            $('#dateModal').addClass('on');

            const today = new Date();

            const yyyy = today.getFullYear();
            const mm = String(today.getMonth() + 1).padStart(2, '0');
            const dd = String(today.getDate()).padStart(2, '0');

            const formatted = new Date().toLocaleDateString("en-CA");
            $('#copyDate').val(formatted);
            $('#copyDate').attr('max', formatted);

            document.body.style.overflow = 'hidden';

            // 공통 취소 처리 함수
            function handleCancel() {
                if (cancel_cb) {
                    cancel_cb();
                }
                $("#dateModal").hide();
                $('.dateConfirmModal.fade').hide();
                document.body.style.overflow = 'unset';
            }

            // 기존 클릭 이벤트 제거 (이벤트 핸들러 중복 등록 방지)
            $("#data_pop_box_cancle").off("click");
            $("#date_pop_box_confirm").off("click");
            $(".icon_btn.pop_close i").off("click");

            // 취소 관련 버튼과 X 아이콘 공통 처리
            $("#data_pop_box_cancle").click(handleCancel);
            $(".icon_btn.pop_close i").click(handleCancel);

            $("#date_pop_box_confirm").click(function () {
                if (cb) {
                    cb();
                }
                $("#dateModal").hide();

                $('.dateConfirmModal.fade').hide();
                document.body.style.overflow = 'unset';
            });
        },

        customAlert(msg, cb, options) {
            //[bjkim] options 인자 추가. 기존 preHandler 제거
            options = options || {};

            if (cb) {
                if (options.timeout === undefined) {
                    options.timeout = 800;
                }
            } else {
                options.timeout = 1500;
            }

            $('.pop_box.toast').css({
                "top": ($(window).scrollTop() / $(window).height() * 100) + 10 + "%"
            });

            $('.pop_box.toast').show();

            // 20241125 - throw Message 개행문자 포함을위해 .text() -> .html() 변경
            $('.toast_msg').html(msg);
            $('.pop_box.toast').addClass('on');

            if (options.timeout === 0) {
                setTimeout(function () {
                    $('.pop_box.toast').removeClass('on');
                    $('.pop_box.toast').hide();
                }, 1500); // 1500ms = 1.5초
                if (cb) {
                    cb();
                }
            } else {
                // 일정 시간 후에 'on' 클래스 제거
                setTimeout(function () {
                    $('.pop_box.toast').removeClass('on');
                    $('.pop_box.toast').hide();
                    if (cb) {
                        cb();
                    }
                }, options.timeout); // 1500ms = 1.5초
            }
        },

        // 작업일지 자원 수량 입력하는 팝업
        customNumberEditConfirm(msg1, msg2, cb, cancel_cb) {
            $('.numberEditModal.fade').show();
            $('#numberEditModal').show();

            $('.pop_tit').text(msg1);
            $('.edit_label').text(msg2);
            $('#numberInput').val(''); // 초기화 (열릴 때 항상 비움)
            document.body.style.overflow = 'hidden';

            // 공통 취소 함수
            function handleCancel() {
                if (cancel_cb) cancel_cb();
                $("#numberEditModal").hide();
                $('.numberEditModal.fade').hide();
                document.body.style.overflow = 'unset';
            }

            // 중복 클릭 방지
            $("#edit_pop_box_confirm").off("click");
            $(".icon_btn.pop_close i").off("click");

            // 닫기 버튼 (X)
            $(".icon_btn.pop_close i").click(handleCancel);

            // 저장 버튼
            $("#edit_pop_box_confirm").click(function () {
                const inputVal = parseFloat($('#numberInput').val());
                if (isNaN(inputVal) || inputVal < 0) {
                    gaiaCommon.customAlert("유효한 숫자를 입력하세요.");
                    return;
                }

                if (cb) cb(inputVal);

                $("#numberEditModal").hide();
                $('.numberEditModal.fade').hide();
                document.body.style.overflow = 'unset';
            });
        },

        // 작업일지 보할률 입력하는 팝업
        customBohalEditConfirm(msg1, msg2, cb, cancel_cb) {
            // 팝업 표시
            $('.bohalEditModal.fade').show();
            $('#bohalEditModal').show();

            // 제목/라벨 세팅
            $('.pop_tit').text(msg1);
            $('.edit_label').text(msg2);

            // 입력값 초기화
            $('#bohalInput').val('');
            $('.form_check input[type=checkbox]').prop('checked', false); // 전체적용 해제
            document.body.style.overflow = 'hidden';

            // 취소 함수
            function handleCancel() {
                if (cancel_cb) cancel_cb();
                $('#bohalEditModal').hide();
                $('.bohalEditModal.fade').hide();
                document.body.style.overflow = 'unset';
            }

            // 중복 방지
            $('#bohal_pop_box_confirm').off('click');
            $('.icon_btn.pop_close').off('click');

            // 닫기 버튼 (X)
            $('.icon_btn.pop_close').on('click', handleCancel);

            // 저장 버튼
            $('#bohal_pop_box_confirm').on('click', function () {
                const inputVal = parseFloat($('#bohalInput').val());
                const isAll = $('.form_check input[type=checkbox]').prop('checked'); // 전체적용 여부

                if (isNaN(inputVal) || inputVal < 0 || inputVal > 100) {
                    gaiaCommon.customAlert('0~100 사이의 유효한 숫자를 입력하세요.');
                    return;
                }

                if (cb) cb(inputVal, isAll); // 콜백으로 전체적용 여부 함께 전달

                $('#bohalEditModal').hide();
                $('.bohalEditModal.fade').hide();
                document.body.style.overflow = 'unset';
            });
        },


        /*
            계약 셀렉트박스 생성
            contractCount : 프로젝트의 계약 수
            id : 셀렉트 박스를 만들 상위 요소
            noCntrctCb : 계약이 없을때 콜백함수
            initCb : 초기 콜백 함수
            chgCb :  Change 이벤트 함수
        */
        makeCntrctSelectBox(id, noCntrctCb, initCb, chgCb) {
            // Admin 또는 Gaia 일때
            if(gaiaCommon.me.isAdmin() || isGAIA()){

                if (contractCount === 0){
                    $(id).prepend(
                        "<div class='group'><h3 class='conts_tit'>계약코드</h3>" +
                        "<div class='conts_form'><span class='selectbox'>" +
                        "<select name='cntrctNo' id='cntrctNo' class=''>" +
                        "<option>등록된 계약이 없습니다.</option>" +
                        "</select></span></div></div>"
                    );

                    if (noCntrctCb) {
                        noCntrctCb();
                    }
                }else{
                    var param = { pjtNo: pjtInfo.pjtNo };
                    $.ajax({
                        url: "/api/portal/select-cntrctList",
                        method: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify(param),
                        success: function (data) {
                            var list = data.details.contractList;

                            $(id).prepend(
                                "<div class='group'><h3 class='conts_tit'>계약코드</h3>" +
                                "<div class='conts_form'><span class='selectbox'>" +
                                "<select name='cntrctNo' id='cntrctNo'></select></span></div></div>"
                            );

                            $("#cntrctNo").empty();

                            if (list.length > 0) {
                                list.forEach((contract) => {
                                    $("#cntrctNo").append(
                                        $("<option>")
                                            .val(contract.cntrct_no)
                                            .text(gaiaCommon.decodeSafeText(contract.cntrct_nm))
                                    );
                                });

                                // 초기값
								let cntrctSelectBoxParam = commonJs.getSessionStorage("cntrctSelectBox");
								let initialCntrctNo;

								if(!cntrctSelectBoxParam) {
									cntrctInfo = 	{
														"cntrctNo": list[0].cntrct_no,
														"cntrct_nm": gaiaCommon.decodeSafeText(list[0].cntrct_nm)
													};
									
									commonJs.setSessionStorage("cntrctSelectBox", cntrctInfo);

									initialCntrctNo = list[0].cntrct_no; 
								}else {
									initialCntrctNo = cntrctSelectBoxParam.cntrctNo;
								}

                                $("#cntrctNo").val(initialCntrctNo);

                                // 초기 콜백
                                if (typeof initCb === "function") {
                                    initCb(initialCntrctNo);
                                }

                                // 변경 이벤트
                                if (typeof chgCb === "function") {
                                    $("#cntrctNo").on("change", function (e) {
										cntrctInfo = 	{
															"cntrctNo": e.target.value,
															"cntrct_nm": e.target.options[e.target.selectedIndex].text
														};
										console.log('cntrctInfo :: ', cntrctInfo);
										commonJs.setSessionStorage("cntrctSelectBox", cntrctInfo);
                                        chgCb(e.target.value);
                                    });
                                }
                            }
                        }
                    });
                }

                // CAIROS 일때
            }else if(isCAIROS()){
                $(id).append(
                    `<input type='hidden' id='cntrctNo' value='${pjtInfo.cntrctNo}'/>`
                );

                if (typeof initCb === "function") {
                    initCb(pjtInfo.cntrctNo);
                }
            }
        },

        /*
            계약 셀렉트박스 && 계약변경차수 셀렉트박스 생성
            contractCount : 프로젝트의 계약 수
            cntrctNoContainer : 셀렉트 박스를 만들 상위 요소
            cntrctChgId : 계약 변경 차수 쎌렉트박스 생성 요소
            noCntrctCb : 계약이 없을때 콜백함수
            initCb : 계약이 있고 Admin 또는 Gaia 일 때 콜백함수
            chgCb : 계약이 있고 Admin 또는 Gaia 일 때 Change 이벤트 함수
        */
        makeCntrctAndChgIdSelectBox(cntrctNoContainer,cntrctChgId, noCntrctCb, initCb, chgCb) {
            // Admin 또는 Gaia 일때
            if(gaiaCommon.me.isAdmin() || isGAIA()){
                if (contractCount === 0){
                    $(cntrctNoContainer).prepend(
                        "<div class='group'><h3 class='conts_tit'>계약코드</h3>" +
                        "<div class='conts_form'><span class='selectbox'>" +
                        "<select name='cntrctNo' id='cntrctNo' class=''>" +
                        "<option>등록된 계약이 없습니다.</option>" +
                        "</select></span></div></div>"
                    );

                    $(cntrctChgId).prepend($("<option>").text("등록된 계약이 없습니다."));

                    if (noCntrctCb) {
                        noCntrctCb();
                    }
                }else{
                    var param = { pjtNo: pjtInfo.pjtNo };
                    $.ajax({
                        url: "/api/portal/select-cntrctList",
                        method: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify(param),
                        success: function (data) {
                            var list = data.details.contractList;

                            $(cntrctNoContainer).prepend(
                                "<div class='group'><h3 class='conts_tit'>계약코드</h3>" +
                                "<div class='conts_form'><span class='selectbox'>" +
                                "<select name='cntrctNo' id='cntrctNo'></select></span></div></div>"
                            );

                            $("#cntrctNo").empty();

                            if (list.length > 0) {
                                list.forEach((contract) => {
                                    $("#cntrctNo").append(
                                        $("<option>")
                                            .val(contract.cntrct_no)
                                            .text(gaiaCommon.decodeSafeText(contract.cntrct_nm))
                                    );
                                });

                                // 초기값
                                let cntrctSelectBoxParam = commonJs.getSessionStorage("cntrctSelectBox");
                                let initialCntrctNo;

                                if(!cntrctSelectBoxParam) {
                                    cntrctInfo = 	{
                                        "cntrctNo": list[0].cntrct_no,
                                        "cntrct_nm": gaiaCommon.decodeSafeText(list[0].cntrct_nm)
                                    };

                                    commonJs.setSessionStorage("cntrctSelectBox", cntrctInfo);

                                    initialCntrctNo = list[0].cntrct_no;
                                }else {
                                    initialCntrctNo = cntrctSelectBoxParam.cntrctNo;
                                }

                                $("#cntrctNo").val(initialCntrctNo);

                                gaiaCommon.makeCntrctChgIdSelectBox(initialCntrctNo,cntrctChgId, initCb, chgCb);
                                $("#cntrctNo").on("change", function (e) {
                                    cntrctInfo = 	{
                                        "cntrctNo": e.target.value,
                                        "cntrct_nm": e.target.options[e.target.selectedIndex].text
                                    };
                                    commonJs.setSessionStorage("cntrctSelectBox", cntrctInfo);
                                    gaiaCommon.makeCntrctChgIdSelectBox(e.target.value,cntrctChgId, initCb, chgCb);
                                });

                            }
                        }
                    });
                }

                // CAIROS 일때
            }else if(isCAIROS()){
                $(cntrctNoContainer).append(
                    `<input type='hidden' id='cntrctNo' value='${pjtInfo.cntrctNo}'/>`
                );
                gaiaCommon.makeCntrctChgIdSelectBox(pjtInfo.cntrctNo,cntrctChgId, initCb, chgCb);
                $("#cntrctNo").on("change", function (e) {
                    gaiaCommon.makeCntrctChgIdSelectBox(e.target.value,cntrctChgId, initCb, chgCb);
                });
            }
        },

        // 계약변경차수 셀렉트박스 생성
        makeCntrctChgIdSelectBox (selectCntrctNo,cntrctChgId, initCb, chgCb) {
            var param = {
                cntrctNo: selectCntrctNo,
            };
            $.ajax({
                url: `/api/portal/select-cntrctchgList`,
                method: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(param),
                success: function (data, status, xhr) {
                    var list = data.details.contractChangeList;

                    if (list.length > 0) {
                        $(cntrctChgId).empty();

                        list.forEach((contractchg) => {
                            $(cntrctChgId).append(
                                $("<option>")
                                    .val(contractchg.cntrct_chg_id)
                                    .text(contractchg.cntrct_chg_nm)
                                    .attr("cntrct_amt", contractchg.cntrct_amt) // ✅ forEach 파라미터와 동일하게
                            );
                        });

                        var initialCntrctChgId = $(`${cntrctChgId} option`).first().val() ? $(`${cntrctChgId} option`).first().val() : list[list.length - 1].cntrct_chg_id;
                        $(cntrctChgId).val(initialCntrctChgId);

                        // 초기 콜백
                        if (typeof initCb === "function") {
                            initCb(initialCntrctChgId);
                        }
                        // 변경 이벤트
                        if (typeof chgCb === "function") {
                            $(cntrctChgId).on("change", function (e) {
                                chgCb(e.target.value);
                            });
                        }
                    }
                },
            });
        },

        /**
         * download file
         */
        downloadZip(url, params, type, fileNm) {

            gaiaCommon.LoadingOverlay('body', true); //로딩 바 활성화

            $.ajax({
                url: url,  // 파일 다운로드를 위한 서버 URL
                method: "POST",
                data: JSON.stringify(params),
                contentType: "application/json",
                xhrFields: {
                    withCredentials: true,  // CORS 요청에 대한 인증 정보 포함
                    responseType: 'blob'  // 응답을 Blob으로 처리
                },
                success(response, status, xhr) {
                    // 파일 다운로드를 처리
                    const blob = new Blob([response], { type: 'application/zip' });

                    // Content-Disposition 헤더에서 파일 이름을 가져옴
                    const contentDisposition = xhr.getResponseHeader('Content-Disposition');
                    let fileName = 'document.zip';  // 기본 파일 이름

                    if(type === 'ITEM' && fileNm){
                        fileName = fileNm + '.zip';
                    }
                    else if (contentDisposition) {
                        const matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(contentDisposition);
                        if (matches != null && matches[1]) {
                            fileName = matches[1].replace(/['"]/g, '');  // 파일 이름 설정
                        }
                    }

                    // 현재 날짜와 시간을 가져와서 yyyymmddhhmmss 형식으로 변환
                    const now = new Date();
                    const year = now.getFullYear();
                    const month = String(now.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
                    const day = String(now.getDate()).padStart(2, '0');
                    const hours = String(now.getHours()).padStart(2, '0');
                    const minutes = String(now.getMinutes()).padStart(2, '0');
                    const seconds = String(now.getSeconds()).padStart(2, '0');
                    const timestamp = `${year}${month}${day}${hours}${minutes}${seconds}`;

                    // 파일명에 날짜와 시간 추가
                    const [name, extension] = fileName.split('.');
                    fileName = `${name}_${timestamp}.${extension || 'zip'}`;  // 확장자 유지

                    // Blob을 직접 다운로드
                    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                        // Internet Explorer 전용 처리
                        window.navigator.msSaveOrOpenBlob(blob, fileName);
                    } else {
                        const downloadUrl = window.URL.createObjectURL(blob);
                        const a = document.createElement('a');
                        a.style.display = 'none';
                        a.href = downloadUrl;
                        a.download = fileName;
                        document.body.appendChild(a);
                        a.click();  // 링크 클릭 없이 파일 다운로드 실행
                        document.body.removeChild(a);  // 다운로드 후 a 태그 제거
                        window.URL.revokeObjectURL(downloadUrl);  // 메모리 해제
                    }
                },
                complete() {
                    gaiaCommon.LoadingOverlay('body', false); //로딩 바 비활성화
                },
                error(error) {
                    // gaiaCommon.LoadingOverlay('body', false);
                    gaiaCommon.customAlert(error.responseJSON?.message || '파일 다운로드 중 오류가 발생했습니다.');
                }
            });
        },
        download(url, errCb) {
             // 파일 존재 여부 확인 및 다운로드 요청
            $.ajax({
                url: url,
                method: "GET",
                xhrFields: { responseType: 'blob' }, // 파일 다운로드를 위한 설정
                success: function (blob, status, xhr) {
                    const contentDisposition = xhr.getResponseHeader('Content-Disposition');
                    let fileName = "downloaded_file";

                    if (contentDisposition) {
                        const fileNameMatch = contentDisposition.match(/filename\*?=["']?(UTF-8'')?([^"';]+)["']?/);
                        if (fileNameMatch && fileNameMatch.length > 2) {
                            fileName = decodeURIComponent(fileNameMatch[2]);
                        }
                    }

                    // Blob 데이터를 이용한 파일 다운로드
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = fileName;
                    document.body.appendChild(a);
                    a.click();
                    window.URL.revokeObjectURL(url);
                    a.remove();
                },
                error: function (xhr) {
                    console.error(xhr);
                    if (errCb !== null && errCb !== undefined) {
                        errCb(xhr);
                    }
                    // if (xhr.status === 404) {
                    //     gaiaPortal.customAlert("파일이 존재하지 않아 다운로드할 수 없습니다."); // 파일 없음 알림
                    // } else if (xhr.status === 400) {
                    //     gaiaPortal.customAlert("잘못된 요청입니다."); // 잘못된 요청 처리
                    // } else {
                    //     gaiaPortal.customAlert("서버 오류로 인해 다운로드할 수 없습니다."); // 기타 서버 오류 처리
                    // }
                }
            });

            // /* 기존코드 */
            // // 새 창을 열어 파일을 다운로드합니다.
            // window.open(url, "_blank");
        },

		//통합 문서 관리 PDF 파일 미리보기 공통 기능
		pdfViewUtil(viewType, viewKey, downloadAuthority) {
            const _width = '1000';
			const _height = '1000';

			let _left = Math.ceil((window.screen.width - _width) / 2);
			_left += window.screenLeft; // 듀얼 모니터일 때
			let _top = Math.ceil((window.screen.height - _height) / 2);

			pdfViewParam = {
                "viewType": viewType,
                "viewKey": viewKey,
                "downloadAuthority": downloadAuthority
            };

            commonJs.setSessionStorage("pdfViewParam", pdfViewParam);

			// if (! (!this.pdfViewWin || this.pdfViewWin.closed) ) {        
			// 	this.pdfViewWin.close();      
			// }
			// this.pdfViewWin = window.open(`/util/pdf-file-view`, 'pdf미리보기', 'width=' + _width + ', height=' + _height + ',left=' + _left + ',top=' + _top + ', scrollbars=yes, resizable=yes');

			if(pdfViewParam.viewType === 'GUIDE') {
				window.open(`/util/pdf-file-view`, viewKey, 'width=' + _width + ', height=' + _height + ',left=' + _left + ',top=' + _top + ', scrollbars=yes, resizable=yes');
			}else {
				window.open(`/util/pdf-file-view`, 'pdf미리보기', 'width=' + _width + ', height=' + _height + ',left=' + _left + ',top=' + _top + ', scrollbars=yes, resizable=yes');
			}

			commonJs.delSessionStorage('pdfViewParam');
        },
        openPreview(fileMeta, srcData){
            const _width = '1000';
            const _height = '1000';
            let _left = Math.ceil((window.screen.width - _width) / 2);
            _left += window.screenLeft; // 듀얼 모니터일 때
            let _top = Math.ceil((window.screen.height - _height) / 2);

            window._currentFileMeta = {fileMeta, srcData};

            if(!window._previewWindow || window._previewWindow.closed){
                window._previewWindow = window.open(`/util/file-preview`, "preview", 'width=' + _width + ', height=' + _height + ',left=' + _left + ',top=' + _top + ', scrollbars=yes, resizable=yes');
            }else{
                window._previewWindow.location.reload();
            }

            if (!window._previewMessageListener) {
                window._previewMessageListener = (event) => {
                    if (event.origin !== window.location.origin) return;

                    if (event.data.type === "ready") {
                        const popup = window._previewWindow;
                        if (popup && !popup.closed && window._currentFileMeta) {
                            popup.postMessage({
                                type: "fileInfo",
                                data: window._currentFileMeta
                            }, window.location.origin);
                        }
                    }
                };
                window.addEventListener("message", window._previewMessageListener);
            }

        },
        /**
         * load html
         */
        load(url, params, cb) {
            $.ajax({
                url: url,
                method: "GET",
                data: params,
                xhrFields: {
                    withCredentials: true,
                },
                success(response) {
                    cb(response);
                },
                error(error) {
                    alert(error.responseJSON?.message);
                },
            });
        },

        /**
         * get query params
         */
        getQueryParams() {
            var queryString = window.location.search.substring(1);
            var pairs = queryString.split("&");
            var queryParams = {};
            pairs.forEach((pair) => {
                var parts = pair.split("=");
                var key = decodeURIComponent(parts[0]);
                var value = decodeURIComponent(parts[1] || "");
                if (key && value.includes(",")) {
                    value = value.split(",");
                }
                queryParams[key] = value;
            });
            return queryParams;
        },

        /**
         * set selectbox options
         */
        async setCombo(selector, code, change) {
            $me.changeContract('ADMIN');
            if ($(selector).length && code) {
                if (Array.isArray(code)) {
                    code.forEach(function (c) {
                        $(selector).append($("<option>", { value: c.cmnCd, text: c.cmnCdNmKrn }));
                    });
                } else {
                    await new Promise((resolve, reject) => {
                        this.get("/api/system/common-code/code-combo/" + code, {}, function (result) {
                            if (result.details?.codeCombo) {
                                result.details?.codeCombo.forEach(function (code) {
                                    $(selector).append($("<option>", { value: code.cmnCd, text: code.cmnCdNmKrn }));
                                });
                            }
                            $(selector).each((i, v) => $(v).val($(v).data("comboVal")));
                            resolve();
                        });
                    });
                }
                if (change) {
                    $(selector).change(change);
                }
            }
        },

        /**
         * init 시점에 공통코드 정보를 저장해 두고, 페이지에서 필요할때 가져다 씁니다.
         */
        async setCommonCode(...cmnCdList) {
            await new Promise((resolve, reject) => {
                this.get("/api/system/common-code/code-combo/map", { cmnCdList }, function (result) {
                    if (result.details?.codeComboMap) {
                        gaiaCommon.data.commonCode = result.details?.codeComboMap;
                    }
                    resolve();
                });
            });
        },

        /**
         * 페이지가 초기화 될때 저장된 공통코드 정보를 가져옵니다. (코드 리스트)
         */
        getCommonCode(commonCode) {
            return gaiaCommon.data.commonCode[commonCode];
        },

        /**
         * 페이지가 초기화 될때 저장된 공통코드 정보를 가져옵니다. (코드키로 코드명 조회)
         */
        getCommonCodeName(commonCode, cmnCd) {
            return gaiaCommon.data?.commonCode[commonCode]?.find((obj) => obj.cmnCd === cmnCd)?.cmnCdNmKrn;
        },

        /**
         * 입력폼 검증
         */
        validate(selector) {
            var isValid = true;
            $(selector)
                .find("input, select, textarea")
                .each(function () {
                    if ($(this).attr("required") && !$(this).val()) {
                        alert($(this).attr("placeholder") + "을(를) 입력해주세요.");
                        isValid = false;
                        return false;
                    }
                });
            return isValid;
        },

        /**
         * 팝업
         */
        winPopup(url, specs) {
            this.popup(true, url, specs ? { specs } : {});
        },
        layerPopup(url, layerId) {
            this.popup(false, url, layerId ? { layerId } : {});
        },
        popup(isNew, url, params) {
            $.extend(params, {
                name: "gaia-popup",
                replace: false,
                specs: "width=500, height=400, left=100, top=100, scrollbars=no, resizable=no",
                layerId: "#popup",
            });
            if (isNew) {
                window.open(url, params.name, params.specs, params.replace);
            } else {
                $(params.layerId).load(url);
            }
        },

        /*******************
         * [개인화]
         */
        // my() {
        //     return {
        //         type: "admin",
        //         selected: {
        //             projectId: "G202405006",
        //             contractNo: "P202405008",
        //         },
        //     };
        // },
        me: {
            info: undefined,

            data: {
                contractList: [],
                selected: {},
            },

            async init() {
                $me = this;
                await new Promise((resolve, reject) => {
                    // if (!me) {
                        gaiaCommon.get("/api/me", {}, function (result) {
                            // if (result.details?.contractList) {
                            //     console.log(result.details?.contractList);
                            //     $me.data.contractList = result.details?.contractList;
                            //     gaiaCommon.setCombo(
                            //         "#contractNo",
                            //         result.details?.contractList.map((c) => ({ cmnCd: c.cntrctNo, cmnCdNmKrn: c.name })),
                            //         function () {
                            //             $me.changeContract($(this).val());
                            //         }
                            //     );
                            // }
                            // if (result.details?.me) {
                            //     console.log(result.details?.me);
                            //     $me.data.me = result.details?.me;
                            //     $("span.login-id").html(result.details?.me.loginId);
                            //     $me.selectContract(result.details?.me);
                            // }

                            if (result.details?.me) {
                            }
                            resolve();
                        });
                    // } else {
                    //     Object.freeze(me);
                    //     resolve();
                    // }
                });
            },
            isAdmin() {
                return gaiaCommon.me.info.admin;
            },
            selectContract(me) {
                // console.log(me);
                $("#contractNo").val(me.cntrctNo);
                this.data.selected = { pjtNo: me.pjtNo, cntrctNo: me.cntrctNo };
            },
            changeContract(cntrctNo) {
                //alert('필수요소 생성해야함 강제 생성');
                // console.log('@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@');
                // console.log(cntrctNo);
                // console.log(this.data.selected.pjtNo);
                // console.log(this.data.selected.cntrctNo);
                // console.log('@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@');
                this.selectContract(this.data.contractList.find((c) => c.cntrctNo === cntrctNo));
                gaiaCommon.get("/api/project/select/" + this.data.selected.pjtNo + "/" + this.data.selected.cntrctNo, {}, function (result) {
                    //location.reload();
                });
            },
        },
        LoadingOverlay(pTarget, TfVal) {
            try {
                $(pTarget).LoadingOverlay(TfVal ? "show" : "hide", TfVal);
            } catch (err) {
                return false;
            }
        },

        /**
         * 이미지 경로 변경 함수
         */
        convertAbsoluteToRelativePath(filePath, fileName) {
            if (!filePath) return '';

            // fileName이 있으면 합쳐서 처리
            if (fileName) {
                filePath = filePath + '/' + fileName;
            }

            filePath = filePath.replace(/\\/g, '/'); // 역슬래시 → 슬래시
            return filePath.replace(/^.*\/upload/, '/upload'); // /upload 이하 상대 경로만 추출
        },

        /**
         * 안전한 텍스트 렌더링 함수 (XSS 방지)
         * @param {string} text - 렌더링할 텍스트
         * @returns {string} 이스케이프된 안전한 텍스트
         */
        safeText(text, allowHtml = false) {
            if (!text) return '';

            // 기본 이스케이프 (DOMPurify 없을 때)
            return text
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#x27;')
                .replace(/\//g, '&#x2F;');

        },
        /**
         * 역치환
         * @param {string} text - XSS 방지용 텍스트
         * @returns {string} - 렌더링 할 텍스트
         */
        decodeSafeText(text) {
            if (!text) return '';
            if(typeof text === 'string') {
                return text
                    .replace(/&amp;/g, "&")
                    .replace(/&lt;/g, "<")
                    .replace(/&gt;/g, ">")
                    .replace(/&quot;/g, '\"')
                    .replace(/&#x27;/g, "\'")
                    .replace(/&#x2F;/g,"/" );
            }
            else{
                console.log("Not a valid text!", text,'is',typeof text);
                return text;
            }
        },

        /**
         * 권한 검증 후 콜백 실행 함수
         * @param {string} rescId - 리소스 ID
         * @param {function} successCallback - 권한 있을 때 실행할 콜백
         * @param {function} errorCallback - 에러 발생 시 실행할 콜백 (선택)
         *
         * 사용 예시:
         * gaiaCommon.checkAuth("DT_RESPONSE_CU_01",
         *   function() { $("#popup").load("/defectTracking/responsesForm"); },
         *   function(error) { console.log("권한 없음:", error); }
         * );
         */
        checkAuth(rescId, successCallback, errorCallback) {
            gaiaCommon.LoadingOverlay('body', true);

            $.ajax({
                url: "/api/access-Check",
                method: "GET",
                data: {
                    rescId: rescId
                },
                success: function (data) {
                    gaiaCommon.LoadingOverlay('body', false);

                    if (data && data.details && data.details.authYn === 'Y') {
                        // 권한 있음 - 콜백 실행
                        if (typeof successCallback === 'function') {
                            successCallback();
                        }
                    } else {
                        // 권한 없음 - 알림 표시
                        gaiaCommon.customAlert("권한이 존재하지 않아 접근할 수 없습니다.");
                        if (typeof errorCallback === 'function') {
                            errorCallback('NO_AUTH');
                        }
                    }
                },
                error: function (xhr, status, error) {
                    gaiaCommon.LoadingOverlay('body', false);
                    console.error("권한 확인 중 오류:", error);
                    gaiaCommon.customAlert("서버와의 통신 중 오류가 발생했습니다. 다시 시도해주세요.");
                    if (typeof errorCallback === 'function') {
                        errorCallback('NETWORK_ERROR');
                    }
                }
            });
        }
        , openReportViewer(reportNames, params, options) {
            let reports = [];

            if (!params) {
                params = {}
            }

            if (!options) {
                options = {
                    width: 800
                    , height: 800
                    , left: 0
                    , top: 0
                };
            }

            options.left = Math.round(window.screenX + (window.outerWidth/2) - (options.width/2));
            options.top = Math.round(window.screenY + (window.outerHeight/2) - (options.height/2));

            if (typeof reportNames === 'string') {
                if (reportNames.substring(0, 1) === ',') {
                    alert('올바른 타입이 아닙니다.');
                    return;
                } else if (reportNames.search(',') > -1) {
                    reports.push(reportNames.split(','));
                } else {
                    reports.push(reportNames);
                }
            } else if (typeof reportNames === 'object' && reportNames.hasOwnProperty('length')) {
                if (reportNames.length == 0) {
                    alert('올바른 타입이 아닙니다.');
                    return;
                }

                reports.push(...reportNames);
            } else {
                alert('올바른 타입이 아닙니다.');
                return;
            }

            const windowName = 'reportViewer';
            const windowFeatures = Object.entries(options).map(el => el.join('=')).join(',');

            const form = document.createElement('form');
            form.setAttribute('method', 'POST');
            form.setAttribute('action', REPORT_VIEWER_URL);
            form.setAttribute('target', windowName);

            let hiddenEnvField = document.createElement('input');
            hiddenEnvField.setAttribute('type', 'hidden');
            hiddenEnvField.setAttribute('name', 'envMode');
            hiddenEnvField.setAttribute('value', envMode);
            form.appendChild(hiddenEnvField);

            let hiddenField = document.createElement('input');
            hiddenField.setAttribute('type', 'hidden');
            hiddenField.setAttribute('name', 'file');
            hiddenField.setAttribute('value', reports.join(','));
            form.appendChild(hiddenField);

            const hiddenField2 = document.createElement('input');
            hiddenField2.setAttribute('type', 'hidden');
            hiddenField2.setAttribute('name', 'arg');
            hiddenField2.setAttribute('value', Object.entries(params).map(el => el.join('#')).join('#') + '#');
            form.appendChild(hiddenField2);

            document.body.appendChild(form);

            // Open the new window/tab
            const newWindow = window.open('', windowName, windowFeatures);

            if (newWindow) {
                form.submit(); // Submit the form to the new window
            } else {
                alert('Pop-up blocked! Please allow pop-ups for this site.');
            }

            document.body.removeChild(form); // Clean up the dynamic form
        }
        , exportReport(reportNames, params, options) {
            let reports = [];

            if (!params) {
                params = {}
            }

            if (!options) {
                options = {
                    width: 800
                    , height: 800
                    , left: 0
                    , top: 0
                };
            }

            options.left = Math.round(window.screenX + (window.outerWidth/2) - (options.width/2));
            options.top = Math.round(window.screenY + (window.outerHeight/2) - (options.height/2));

            if (typeof reportNames === 'string') {
                if (reportNames.substring(0, 1) === ',') {
                    alert('올바른 타입이 아닙니다.');
                    return;
                } else if (reportNames.search(',') > -1) {
                    reports.push(reportNames.split(','));
                } else {
                    reports.push(reportNames);
                }
            } else if (typeof reportNames === 'object' && reportNames.hasOwnProperty('length')) {
                if (reportNames.length == 0) {
                    alert('올바른 타입이 아닙니다.');
                    return;
                }

                reports.push(...reportNames);
            } else {
                alert('올바른 타입이 아닙니다.');
                return;
            }

            const windowName = 'reportExport';
            const windowFeatures = Object.entries(options).map(el => el.join('=')).join(',');

            const form = document.createElement('form');
            form.setAttribute('method', 'POST');
            form.setAttribute('action', REPORT_EXPORT_URL);
            form.setAttribute('target', windowName);

            let hiddenField = document.createElement('input');
            hiddenField.setAttribute('type', 'hidden');
            hiddenField.setAttribute('name', 'file');
            hiddenField.setAttribute('value', reports.join(','));
            form.appendChild(hiddenField);

            const hiddenField2 = document.createElement('input');
            hiddenField2.setAttribute('type', 'hidden');
            hiddenField2.setAttribute('name', 'arg');
            hiddenField2.setAttribute('value', Object.entries(params).map(el => el.join('#')).join('#') + '#');
            form.appendChild(hiddenField2);

            document.body.appendChild(form);

            // Open the new window/tab
            const newWindow = window.open('', windowName, windowFeatures);

            if (newWindow) {
                form.submit(); // Submit the form to the new window
            } else {
                alert('Pop-up blocked! Please allow pop-ups for this site.');
            }

            // document.body.removeChild(form); // Clean up the dynamic form
        }
    };  // END gaiaCommon

    // loading 설정 초기화
    setLoadingOverlayDefaultSetting();
})();

/**
 * initPage 함수가 있으면 실행합니다.
 */
$(function () {
    if (!gaiaCommon.flag) {
        //임시 메뉴
        //gaiaCommon.menu.temp();

        // 페이지 최초 실행함수 선언되어있을경우 호출
        try {
            if (commonPage && typeof commonPage.init === "function") {
                commonPage.init();
            }
        } catch (error) {
        }

        gaiaCommon.flag = "I";
    }
});

/**
 * 작업중
 * tui.Grid 리사이즈 Grid refresh call()
 * @param {obejct} tui.Grid
 */
let refreshGrid = function (...args) {
    let timer;
    let _section = document.getElementById("content") ? document.getElementById("content") : document.querySelector(".dashboard_lay");

    const resizeObserver = new ResizeObserver(entries => {
        //console.log("Resize event occured");

        // 이벤트 진행동안 설정된 타이머를 초기화 - debouncing
        clearTimeout(timer);

        // 마지막 변경이 감지된 시점 이후 100ms 동안 변경이 없으면 실행
        timer = setTimeout(() => {
            //console.log("Resize event finished");
            for (elem of args) {
                if (elem.el && elem instanceof tui.Grid) { // tui.Grid 생성자로 만든 객체인지 판별
                    // Section Width - Grid Width
                    let dataWidth = _section.offsetWidth - elem.el.offsetWidth;

                    if (elem.el.getAttribute('data-width') === null) {
                        elem.el.setAttribute('data-width', dataWidth);
                    } else {
                        dataWidth = Number(elem.el.getAttribute('data-width'));
                    }

                    //elem.el.offsetWidth = _section.offsetWidth - dataWidth;
                    elem.el.style.width = `${_section.offsetWidth - dataWidth}px`;
                    elem.refreshLayout();
                }
            }
        }, 10);
    });

    if (_section) {
        // 요소에 대해 사이즈 변화 감지 시작
        resizeObserver.observe(_section);
    }
}

/**
 * Loading 공통 추가
 *
 * @date 2024-11-26
 * @param session
 * @returns {boolean}
 */
function setLoadingOverlayDefaultSetting(session) {
    try {
        //var userGb = session.data.USER_GB;
        //var color = userGb == 3 || userGb == 5 || userGb == 9 ? '#8743ae' : userGb == 1 || userGb == 2 ? '#6954b8' : '#4873ca';

        $.LoadingOverlaySetup({
            background: "rgba(255, 255, 255, 0.0)",
            image: "/assets/images/svg/loading_spin_blue.svg",
            //		    imageAnimation  : "1.5s fadein",
            minSize: 50,
            maxSize: 80,
            //	 	    imageColor      : color,
        });
    } catch (err) {
        return false;
    }
}

/**
 * 주어진 날짜 필드(changedField)의 값을 기준으로,
 * 반대쪽 날짜 필드(otherField)를 자동으로 계산해 채워주는 함수.
 *
 * @param {HTMLInputElement} changedField - 변경된 날짜 input 요소 (기준일)
 * @param {HTMLInputElement} otherField - 자동으로 계산할 대상 날짜 input 요소
 * @param {string} mode - 'start' 또는 'end'
 *                        'start' → 시작일 기준으로 종료일 계산 (+6일)
 *                        'end' → 종료일 기준으로 시작일 계산 (-6일)
 *
 * 사용 예:
 *   syncWeekDates(startInput, endInput, 'start'); // 시작일 변경 시
 *   syncWeekDates(endInput, startInput, 'end');   // 종료일 변경 시
 */
function syncWeekDates(changedField, otherField, mode) {
    if (!changedField.value) return;

    const baseDate = new Date(changedField.value);
    if (isNaN(baseDate)) return;

    let calculatedDate = new Date(baseDate);
    if (mode === 'start') {
        calculatedDate.setDate(baseDate.getDate() + 6);
        otherField.value = calculatedDate.toISOString().split('T')[0];
        otherField.min = changedField.value;
    } else if (mode === 'end') {
        calculatedDate.setDate(baseDate.getDate() - 6);
        otherField.value = calculatedDate.toISOString().split('T')[0];
        otherField.max = changedField.value;
    }
}

// 날짜 관련 함수 객체
const DateUtils = {
    /**
     * parameter 에 따른 Date Format String 을 반환한다.
     * @param param String
     * @param dateObject Date 객체, 기본값 - 현재 시간
     * @returns
     */
    getCurrentDateTime(param, dateObject = new Date()) {

        let day = dateObject.getDate(); // 일
        let month = `${(dateObject.getMonth() + 1)}`.padStart(2, '0');  // 월 (0부터 시작하므로 +1 필요)
        let year = dateObject.getFullYear(); // 연도
        let hours = dateObject.getHours(); // 시간
        let minutes = dateObject.getMinutes(); // 분
        let seconds = dateObject.getSeconds(); // 초

        if (param === 'today') { return `${year}-${month}-${day}`; }
        else if (param === 'month') { return `${year}-${month}`; }
        else if (param === 'today_kor') { return `${year}년 ${month}월 ${day}일`; }
        else if (param === 'month_kor') { return `${year}년 ${month}월`; }
        else if (param === 'time') { return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`; }
        else if (param === 'prev_month') {
            dateObject.setMonth(dateObject.getMonth() - 1);
            month = `${(dateObject.getMonth() + 1)}`.padStart(2, '0');
            year = dateObject.getFullYear();

            return `${year}-${month}`;
        }
        else if (param === 'next_month') {
            dateObject.setMonth(dateObject.getMonth() + 1);
            month = `${(dateObject.getMonth() + 1)}`.padStart(2, '0');
            year = dateObject.getFullYear();

            return `${year}-${month}`;
        }
        else {
            return {
                day: day,
                month: month,
                year: year,
                hours: hours,
                minutes: minutes,
                seconds: seconds
            };
        }
    },

    setCustomDateTime(param) {

    },
    /**
     * 해당 월의 마지막 날짜를 리턴하는 함수
     * @param monthString 'YYYY-MM' 형식
     * @returns {number}
     */
    getLastDayOfMonth(monthString) {
        // 'YYYY-MM' 형식에서 연도와 월을 분리
        let [year, month] = monthString.split('-');

        // 다음 달의 첫째 날을 기준으로 계산 후 하루를 빼서 해당 월의 마지막 날을 구함
        let lastDay = new Date(year, month, 0).getDate();

        return lastDay;
    },

    /**
     * 두 날짜 사이의 모든 월을 가져오는 함수
     * @param startDate
     * @param endDate
     * @returns type: month 'YYYY-MM' 형식의 배열 type: day - 두 날짜 사이의 값
     */
    getMonthsBetweenDates(startDate, endDate, type = 'month') {
        const start = new Date(startDate);
        const end = new Date(endDate);
        if (type === 'month') {
            const months = [];

            while (start <= end) {
                const month = `${start.getFullYear()}-${String(start.getMonth() + 1).padStart(2, '0')}`;
                months.push(month);
                start.setMonth(start.getMonth() + 1);
            }

            return months;
        } else {
            const diffDate = endDate.getTime() - startDate.getTime();
            const oneDay = 1000 * 60 * 60 * 24;// 밀리세컨 * 초 * 분 * 시 = 일
            return Math.abs(diffDate / oneDay);
        }

    },

}
