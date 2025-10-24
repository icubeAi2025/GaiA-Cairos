(function ($) {
    $.fn.setGrid = function (params) {
        
        const settings = $.extend({
            grid: null,
            Grid: tui.Grid,
            gridElement: '',
            pagination: null,
            paginationElement: 'pagination',
            limit: 5,  // 한 페이지에 보여줄 데이터 개수
            totalCnt: 0,
            url: '',    // 데이터를 로드할 URL, params로부터 받아오며 필수
            rowHeaders: [],
            columns: [], // 그리드에 사용될 컬럼, params로부터 받아오며 필수
            //status: null, //selectbox로 조회할 컬럼명
            keyword: null, //조회 할 검색어
            data: null,
        }, params);

        const run = function() {
            methods.initGrid.call(this); // 현재 jQuery 객체를 `this`로 전달
            //methods.readData.call(this); // 현재 jQuery 객체를 `this`로 전달
            // methods.readData.call(this, settings.data);
            return {
                methods: methods,
                grid: settings.grid
            }
        };

        const methods = {
            initGrid: function() {
                
                settings.grid = new settings.Grid({
                    el: document.getElementById(settings.gridElement),
                    scrollX: true,
                    scrollY: true,
                    data: {
                        api: {
                            readData: { url: settings.url, method: 'GET', initParams: { data: settings.data } }
                        }
                    },
                    rowHeaders: settings.rowHeaders,
                    pageOptions: {
                        perPage: settings.limit
                    },
                    columns: settings.columns
                });

                settings.pagination = settings.grid.paginationManager.getPagination()

                // 'beforeSort' 이벤트 핸들러 설정(정렬 ui 멀티 컬럼 정렬로 나타나는 문제 해결)
                settings.grid.on('beforeSort', (ev, { columns } = ev.instance.store.data.sortState) => columns.length && columns.shift());
                // 'beforeRequest' 이벤트 핸들러 설정(검색 후 정렬 시, 검색된 상태 유지 안되는 문제 해결)
                settings.grid.on('beforeRequest', function(ev){
                    ev.instance.dataProvider.setRequestParams(settings.data);

                });
            },

            readData: function(params) {
                settings.data = params;
                console.log('검색');
                console.log(settings.data);
                settings.grid.readData(1, params);
            },

            itemPerPageChange: function(size){
                settings.limit = size;
                $.ajax({
                    url: settings.url,
                    method: 'GET',
                    dataType: 'json',
                    data: {
                        page: 1,
                        size: settings.limit || 5,
                        status: settings.status,
                        keyword: settings.keyword
                    }
                }).then(function(response) {
                    const options = {
                        pageState: {
                            page: 1,
                            totalCount: response.data.pagination.totalCount,
                            perPage: settings.limit
                        }
                    }

                    settings.pagination.setItemsPerPage(settings.limit);
                    settings.pagination.setTotalItems(response.data.pagination.totalCount);
                    settings.pagination.reset(response.data.pagination.totalCount);
                    settings.grid.resetData(response.data.contents, options);
                    settings.grid.setPerPage(settings.limit);
                }).catch(function(error) {
                    console.error('Error fetching data:', error);
                    return {
                        contents: [],
                        totalCount: 0
                    };
                });

                
            },

            
        };

        return run();
    };
})(jQuery);
