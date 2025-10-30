(function ($) {
    $.fn.setGrid = function (params) {
        const settings = $.extend(
            {
                grid: null,
                Grid: tui.Grid,
                gridElement: "", // grid를 생성할 div의 id 값
                pagination: null,
                paginationElement: "pagination",
                limit: 10, // 한 페이지에 보여줄 데이터 개수
                totalCnt: 0, // 총 데이터의 개수
                readUrl: null, // 데이터를 로드할 URL, params로부터 받아오며 필수
                columns: [], // 그리드에 사용될 컬럼, params로부터 받아오며 필수
                column: null, // 검색 시, selectbox로 조회할 컬럼명
                keyword: null, // 조회 할 검색어
                data: null,
                initialData: null,
                docNaviType: null,

            },
            params
        );

        const run = function () {
            methods.initGrid.call(this); // 현재 jQuery 객체를 this로 전달
            return {
                methods: methods,
                settings: settings,
                grid: settings.grid,
            };
        };

        const methods = {
            /**
             * 그리드 생성
             */
            initGrid: async function () {
                // 그리드 설정 옵션 세팅
                const gridOptions = {
                    el: document.getElementById(settings.gridElement),
                    scrollX: settings.scrollX,
                    scrollY: settings.scrollY,
                    rowHeaders: settings.rowHeaders,
                    columns: settings.columns,
                    pageOptions: {
                        perPage: settings.limit,
                    },
                    useClientSort: false,
                    width: settings.width,
                    editingEvent: "click", // 셀 클릭 시, 편집모드 즉시 실행
                    contextMenu: settings.contextMenu,
                    minBodyHeight: settings.minBodyHeight,
                    bodyHeight: settings.bodyHeight,
                };

                // 초기 데이터 유무에 따라 data 설정
                if (settings.initialData) {
                    gridOptions.data = {
                        api: {
                            readData: {
                                url: settings.readUrl,
                                method: "GET",
                                initParams: { data: settings.initialData },
                            },
                        },
                    };
                } else {
                    gridOptions.data = {
                        api: {
                            readData: {
                                url: settings.readUrl,
                                method: "GET",
                                initParams: { data: settings.data },
                            },
                        },
                    };
                }

                if (settings.readUrl === null) {
                    gridOptions.data.initialRequest = false;
                }

                settings.grid = new settings.Grid(gridOptions);

                refreshGrid(settings.grid); // 그리드 리사이즈 문제 해결

                settings.pagination = settings.grid.paginationManager.getPagination();

                settings.grid.on("click", function (e) {
                    const rowKey = e.rowKey; // 클릭된 행의 키를 가져옴

                    if (e.columnName == "_checked") {
                        if (e.nativeEvent.target.className == "checkGroup") {
                            let temp = settings.grid.getRow(rowKey);
                            temp._attributes.checked = settings.grid.getRow(rowKey)._attributes.checked ? false : true;
                            settings.grid.setRow(rowKey, temp);
                        }
                    }
                })

                // 'beforeSort' 이벤트 핸들러 설정(정렬 ui 멀티 컬럼 정렬로 나타나는 문제 해결)
                settings.grid.on(
                    "beforeSort",
                    (ev, { columns } = ev.instance.store.data.sortState) =>
                        columns.length && columns.shift()
                );
                // 'beforeRequest' 이벤트 핸들러 설정(검색 후 정렬 시, 검색된 상태 유지 안되는 문제 해결)
                settings.grid.on("beforeRequest", function (ev) {
                    ev.instance.dataProvider.setRequestParams(settings.data);
                });
                // 'response' 이벤트 핸들러 설정: 데이터를 가져온 후 불러온 값을 확인할 수 있음
                settings.grid.on("successResponse", function (ev) {
                    const response = JSON.parse(ev.xhr.response);

                    if (response.data.pagination) {
                        settings.totalCnt = response.data.pagination.totalCount;

                        // 페이지네이션 설정
                        settings.pagination.setItemsPerPage(settings.limit);
                        settings.pagination.setTotalItems(settings.totalCnt);
                        $(".tui-pagination").show();
                        settings.totalCnt = response.data.pagination.totalCount;
                    } else {
                        // totalCount가 없으면 페이지네이션 비활성화
                        settings.pagination = null;
                        settings.grid.paginationManager = null;
                        $(".tui-pagination").hide(); // 페이지네이션 요소를 숨김
                    }
                });
                // errorResponse 이벤트 핸들러 추가
                settings.grid.on("errorResponse", function (ev) {
                    // ev.xhr: XMLHttpRequest 객체를 참조
                    const statusCode = ev.xhr.status;
                    // console.log(statusCode);

                    if (statusCode === 401 || statusCode === 403) {
                        gaiaCommon.customAlert(
                            SESSION_EXPIRED_MSG,
                            () => {
                                // 로그인 페이지로 리다이렉션
                                window.location.href = window.location.pathname;
                            }
                        );
                    } else {
                        console.error(
                            "데이터 요청 중 오류 발생:",
                            ev.xhr.status,
                            ev.xhr.statusText
                        );
                    }
                });
            },

            loadData: function (params, cb) {
                $.ajax({
                    url: settings.readUrl,
                    method: "GET",
                    dataType: "json",
                    data: params,
                })
                    .then(function (response) {
                        if (response.data.pagination) {
                            const options = {
                                pageState: {
                                    page: 1,
                                    totalCount:
                                        response.data.pagination.totalCount,
                                    perPage: settings.limit,
                                },
                            };

                            settings.pagination.setItemsPerPage(settings.limit);
                            settings.pagination.setTotalItems(
                                response.data.pagination.totalCount
                            );
                            settings.pagination.reset(
                                response.data.pagination.totalCount
                            );
                            settings.grid.resetData(
                                response.data.contents,
                                options
                            );
                        } else {
                            settings.grid.resetData(response.data.contents);
                            settings.pagination = null;
                            settings.grid.paginationManager = null;
                        }

                        if (cb) {
                            cb(settings.grid.getData());
                        }
                    })
                    .catch(function (error) {
                        if (error.status === 401 || error.status === 403) {
                            gaiaCommon.customAlert(
                                SESSION_EXPIRED_MSG,
                                () => {
                                    // 로그인 페이지로 리다이렉션
                                    window.location.href = window.location.pathname;
                                }
                            );
                        } else {
                            console.error("Error fetching data:", error);
                            return {
                                contents: [],
                                totalCount: 0,
                            };
                        }
                    });
            },

            /**
             * 데이터 조회 (검색 조회)
             * @param {column, keyword} params
             */
            readData: function (params) {
                settings.data = params;
                settings.grid.readData(1, params);
                // console.log("검색 데이터", params);

                // 전체 아이템 수를 기반으로 페이지네이션 업데이트
                const totalItems = settings.totalCnt; // 전체 행 수를 가져옵니다.

                if (settings.pagination) {
                    settings.pagination.setItemsPerPage(settings.limit); // 페이지 당 아이템 수 설정
                    settings.pagination.setTotalItems(totalItems); // 전체 아이템 수 설정
                    settings.pagination.reset();
                }
            },

            /**
             * 한 페이지 당 보여줄 데이터 개수 설정
             * @param {} size : 한 페이지 당 보여줄 데이터 개수
             */
            itemPerPageChange: function (param, cb) {
                settings.limit = param.size;
                settings.grid.setPerPage(settings.limit, param);

                // 전체 아이템 수를 기반으로 페이지네이션 업데이트
                const totalItems = settings.totalCnt; // 전체 행 수를 가져옵니다.
                // console.log("전체 데이터 행 개수: ", totalItems);

                settings.pagination.setItemsPerPage(settings.limit);  // 페이지 당 아이템 수 설정
                settings.pagination.setTotalItems(totalItems);        // 전체 아이템 수 설정
                settings.pagination.reset();                          // 페이지네이션 리셋

            },

            /**
             * 데이터 삭제
             * @param {*} checkedItemIds : 체크 된 행의 id 값들
             * @param {*} url : 삭제 처리할 url 정보
             * @param {*} listName : 매핑할 list명
             * @param {*} msgData : 삭제메세지
             * @param {*} isHierarchy : 계층구조 여부(문서관리-폴더)
             */
            deleteData: async function (
                checkedItemIds,
                url,
                listName,
                msgData,
                isHierarchy,
            ) {
                // 삭제 여부를 사용자에게 확인
                let data = { [listName]: checkedItemIds };

                if (isHierarchy) {
                    try {
                        let response = await $.ajax({
                            url: "/api/document/file-hierarchy/count",
                            method: "POST",
                            contentType: "application/json",
                            dataType: "json",
                            data: JSON.stringify(data),
                        });

                        if (response.ok) {
                            // 파일, 폴더 개수 확인창에 뿌리고 삭제 여부 확인
                            // console.log(response.details);
                            gaiaCommon.customConfirm(
                                "폴더 삭제",
                                "폴더 삭제",
                                `정말로 삭제하시겠습니까? \n폴더 : ${response.details.fileFolderCount.folders}개, 파일 : ${response.details.fileFolderCount.files}개`,
                                async () => {
                                    try {
                                        await this.performDelete(data, url);
                                    } catch (error) {
                                        console.error("Error during deletion:", error);
                                    }
                                }
                            );
                        }
                    } catch (error) {
                        if (error.status === 401 || error.status === 403) {
                            gaiaCommon.customAlert(
                                SESSION_EXPIRED_MSG,
                                () => {
                                    // 로그인 페이지로 리다이렉션
                                    window.location.href = window.location.pathname;
                                }
                            );
                        } else {
                            console.error("Error fetching data:", error);
                        }
                        return;
                    }
                } else {
                    // 일반 삭제 확인
                    gaiaCommon.customConfirm(
                        msgData.delConfirmTit,
                        msgData.delConfirmTit,
                        msgData.delConfirmMsg,
                        async () => {
                            try {
                                await this.performDelete(data, url, msgData.delCompleteAlert);
                            } catch (error) {
                                console.error("Error during deletion:", error);
                            }
                        }
                    );
                }
            },
            performDelete: async function (data, url, msg) {
                try {
                    let response = await $.ajax({
                        url: url,
                        method: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        data: JSON.stringify(data),
                    });

                    if (response.ok) {
                        gaiaCommon.customAlert(msg, function () {
                            settings.grid.reloadData();
                        }); // 삭제되었습니다.
                    }
                } catch (error) {
                    if (error.status === 401 || error.status === 403) {
                        gaiaCommon.customAlert(
                            SESSION_EXPIRED_MSG,
                            () => {
                                // 로그인 페이지로 리다이렉션
                                window.location.href = window.location.pathname;
                            }
                        );
                    } else {
                        console.error("Error deleting data:", error);
                    }
                }
            },

            /**
             * 데이터 수정
             * @param {*} updateUrl : 수정 처리할 url 정보
             * @param {*} updatedRowData : 수정할 행 데이터
             */
            updateData: function (updateUrl, updatedRowData) {
                // 수정 여부를 사용자에게 확인
                const isConfirmed = confirm("정말로 수정하시겠습니까?");

                if (isConfirmed) {
                    // 서버에서 수정 처리
                    $.ajax({
                        url: updateUrl,
                        method: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        data: JSON.stringify(updatedRowData),
                    })
                        .then(function (response) {
                            if (response.ok) {
                                gaiaCommon.customAlert("수정 처리가 완료되었습니다.");
                                location.reload();
                            }
                        })
                        .catch(function (error) {
                            if (error.status === 401 || error.status === 403) {
                                gaiaCommon.customAlert(
                                    SESSION_EXPIRED_MSG,
                                    () => {
                                        // 로그인 페이지로 리다이렉션
                                        window.location.href = window.location.pathname;
                                    }
                                );
                            } else {
                                console.error("Error updating data:", error);
                            }
                        });
                }
            },

            exportToExcel: function (exportName) {
                // 현재 화면에 보이는 데이터만 가져오기
                const options = {
                    includeHiddenColumns: false,
                    onlySelected: true,
                    fileName: exportName,
                };

                settings.grid.export("xlsx", options);
            },
        };

        return run();
    };
})(jQuery);
