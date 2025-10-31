String.prototype.replaceAll = function (org, dest) {
    return this.split(org).join(dest);
}

// let param = gaiaCommon.getQueryParams();
param = tware.utils.parseQuery();
const path = location.hash;


const gaia = {
    loaded: false,
    complete: function() {
        console.log('complete')
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


        /* 기존코드 */
        // var d = new Date().getTime();
        // var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        //     var r = (d + Math.random() * 16) % 16 | 0;
        //     d = Math.floor(d / 16);
        //     return (c == 'x' ? r : (r & 0x7 | 0x8)).toString(16);
        // });
        // return uuid;
    }
    ,
    loadView(view) {
        if (gaia.utils.isSampleView(view)) {
            const main = tware.utils.handlebarCompile($(`#mobile-view-${view}-template`).html());
            $('.main_nav').empty();
            $('#main').html(main);
            window.scrollTo({top: 0, behavior:'smooth'})
            return;
        }

        $('#main').load(`/assets/mo/views/${view}.html`, async (res) => {
            console.log('view loaded completed.');

            const pjtNo = sessionStorage.getItem('pjtNo');
            const cntrctNo = sessionStorage.getItem('cntrctNo');

            if (view !== 'home' && view.indexOf('sample') == -1) {
                $('.main_nav').html(tware.utils.handlebarCompile($('#mobile-layout-sub-menu-template').html(), {
                    pjtNo
                    , cntrctNo
                }));
            }

            if (loaded) {
                loaded(pjtNo, cntrctNo);
            }

            window.scrollTo({top: 0, behavior:'smooth'})

            ensureIndicator();
            setActiveByHash();
        });
    }
    , utils: {
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
        , parseParams : function(path) {
            const pos = path.indexOf('?');
            const values = {};

            if (pos > -1) {
                const query = path.substring(pos + 1);

                query.split("&").forEach((v) => {
                    keyValue = v.split("=");
                    values[keyValue[0]] = keyValue[1];
                });
            }

            return values;
        }
        , getView(path) {
            const pos = path.indexOf('?');
            const view = path.substring(1, pos > -1 ? pos : path.length);

            params = {}
            params = gaia.utils.parseParams(path);

            return view;
        }
        , isSampleView(view) {
            return view.indexOf('sample') > -1;
        }
    }
};

gaia.init = async function (args, createdFn) {
    sessionStorage.clear();

    gaia.constants = $.extend({}, tware.constants, gaia.constants);

    var regexp = /\/adm\/.*\/(.*)\.do/;
    var viewId;

    args = args || {};
    args.path = path;
    args.data = {};
    args.query = tware.utils.parseQuery();
    args.search = {};

    if (args.query._condition === 'init') {
        // args.search = tware.utils.parseSerializeValues(sessionStorage.getItem(path));
        args.search = JSON.parse(sessionStorage.getItem(path));
    } else {
        delete args.search;
        sessionStorage.removeItem(path);
    }

    if (regexp.test(path)) {
        viewId = path.match(regexp)[1];
        args.viewId = viewId;
    }

    $('#preloader').load('/assets/templates/handlebars-templates.html', async (res) => {
        console.log('template loaded completed.');
        await preProcess();

        if (createdFn && typeof createdFn === 'function') {
            console.log('preloader loaded.');
            createdFn.call(this, args);
        }

        await postProcess();
    });

    function bindEvent() {
        window.addEventListener('hashchange', handleHashChange);

        function handleHashChange() {
            // 변경 후의 해시값
            const hash = location.hash;

            if (hash) {
                const view = gaia.utils.getView(hash);

                if (!view) return;
                if (view === 'side_nav') return;

                gaia.loadView(view);
            } else {
                location.href = '/';
            }
        }
    }

    async function preProcess() {
        Handlebars.registerPartial('header', Handlebars.compile($('#mobile-layout-header-template').html(), 'utf-8'));
        Handlebars.registerPartial('menu', Handlebars.compile($('#mobile-layout-menu-template').html(), 'utf-8'));
        Handlebars.registerPartial('sidebar', Handlebars.compile($('#mobile-layout-sidebar-template').html(), 'utf-8'));
        Handlebars.registerPartial('footer', Handlebars.compile($('#mobile-layout-footer-template').html(), 'utf-8'));

        if (path) {
            console.log('path', path);
            args.data = gaia.utils.parseParams(path);

            if (!gaia.utils.isSampleView(gaia.utils.getView(path))) {
                if (!args.data.pjtNo || !args.data.cntrctNo) {
                    location.href = '/';
                }

                sessionStorage.setItem('pjtNo', args.data.pjtNo);
                sessionStorage.setItem('cntrctNo', args.data.cntrctNo);
            }
        }

        $('#wrapper').html(tware.utils.handlebarCompile($('#mobile-layout-template').html(), {
            title: 'CaiROS'
            , path: path
        }));
    }

    async function postProcess() {
        bindEvent();

        let view = 'home';

        if (location.hash) {
            view = gaia.utils.getView(location.hash);
        }

        gaia.loadView(view);

        $("body").fadeIn(600);

        // const content = document.getElementById("content");
        // if (content) {
        //     // DOM 변화 감지
        //     const observer = new MutationObserver(toggleUpButton);
        //     observer.observe(content, {
        //         childList: true,
        //         subtree: true
        //     });
        // }
        //
        // window.addEventListener("resize", toggleUpButton);
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





// 해시 변경 시 활성화
function setActiveByHash() {
    const hash = location.hash;
    const target = document.querySelector(`.nav_link[href^="${hash.split('-')[0]}"]`);
    if (target) setActiveTab(target);
}

// 활성 탭 설정
function setActiveTab(linkEl) {
    document.querySelectorAll('.nav_item.is-active')
        .forEach(li => li.classList.remove('is-active'));

    const li = linkEl.closest('.nav_item');
    if (li) li.classList.add('is-active');
    moveIndicator(linkEl);
}

// 인디케이터 이동 계산
function moveIndicator(linkEl) {
    const wrap = document.querySelector('.main_nav .nav_list_wrap');
    const bar  = ensureIndicator();
    if (!wrap || !bar || !linkEl) return;

    const wrapRect = wrap.getBoundingClientRect();
    const linkRect = linkEl.getBoundingClientRect();

    const scrollLeft = document.getElementById('NAV_LIST')?.scrollLeft || 0;
    const left = (linkRect.left - wrapRect.left) + scrollLeft;
    const width = linkRect.width;

    bar.style.width = width + 'px';
    bar.style.transform = `translateX(${left}px)`;
}

// 네비게이션 인디케이터 (활성 탭 밑줄)
// 인디케이터가 없으면 생성
function ensureIndicator() {
    const wrap = document.querySelector('.main_nav .nav_list_wrap');
    if (!wrap) return null;
    let bar = wrap.querySelector('.nav_indicator');
    if (!bar) {
        bar = document.createElement('span');
        bar.className = 'nav_indicator';
        wrap.appendChild(bar);
    }
    return bar;
}

// 클릭 이벤트로 활성화
// $(document).off('click.gaiaNavIndicator');
// $(document).on('click.gaiaNavIndicator', '.nav_link[href^="#"]', function (e) {
//     const link = this;
//     // 메뉴 닫기
//     $('body').removeClass('side-menu-open');
//     // 인디케이터 이동
//     setActiveTab(link);
// });
//
// // 해시 변경, 리사이즈, 스크롤 시 다시 계산
// window.addEventListener('hashchange', setActiveByHash);
// window.addEventListener('resize', setActiveByHash);
// document.getElementById('NAV_LIST')?.addEventListener('scroll', () => {
//     const active = document.querySelector('.nav_item.is-active .nav_link');
//     if (active) moveIndicator(active);
// });

// 페이지 첫 로드 시 1회 실행
// ensureIndicator();
// setActiveByHash();
