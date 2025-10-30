/*
 * Project Name		: GAIA
 * Author	        : Ji yeong Kim
*/

function setDefaultImage() {
    $('img').each(function (idx, el) {
        $(el).on('error', function(e) {
            $(el).onerror = null;
            $(el).attr('src', '/assets/images/noimg.png');
        });
    });
}

function initUi() {

    // Favorites
    // $('[data-btn-toggle="favorites"]').click(function () {
    //     $(this).toggleClass('favorites');
    //     //e.preventDefault();
    // });

    // Favorites 동적 메뉴에도 적용되도록 수정
    $(document).on("click", '[data-btn-toggle="favorites"]', function () {
        $(this).toggleClass("favorites");
    });

    // Hide LNB
    $('.lnb_expansion').click(function () {
        $('.container').toggleClass('hide_lnb');
        //e.preventDefault();
        //findGridInstance(window, tui.Grid);
    });

    // Show submenu
    var lnbMenu =  $(".lnb_nav a");
    var activeMenu = $(".lnb_nav a.active.on");
    $(activeMenu).siblings('[data-list="submenu-list"]').slideDown();
    $(lnbMenu).on('click', function () {
        $(this).toggleClass('on');
        $(this).next('[data-list="submenu-list"]').slideToggle();
    });

    // collapse
    var cTargetBtn = $(".collapse_btn");
    var cTargetArea = $(".collapse_body");
    $(cTargetBtn).click(function () {
        $(this).toggleClass('collapsed');
        $(this).parent().next(cTargetArea).slideToggle();
    });


    //Header - PJ link
    $('.pj_link').click(function () {
        $(this).toggleClass('show');
        //e.preventDefault();
    });

	$('.pop_close').click(function(e) {
		if($(this).closest('div')[0].id == "popBoxToast"){
			$("#popBoxToast").hide();
		}else if($(this).parents()[1].id == "popBoxAlert"){
			$("#popBoxAlert").hide();

			$('.alertModal.fade').hide();
			document.body.style.overflow = 'unset';
		}
	});

    $('#up-btn').click(function () {
        const content = document.getElementById("content");
        if (content) {
            content.scrollTo({ top: 0, behavior: "smooth" });
        }
    });

    $(window).on('load resize', function () {
        toggleUpButton();
    });
}

function toggleUpButton() {
    const $content = $('#content');
    const $upBtn = $('#up-btn');

    if ($content.length === 0 || $upBtn.length === 0) return;

    const hasScroll = $content[0].scrollHeight > $content[0].clientHeight;
    $upBtn.css('display', hasScroll ? 'block' : 'none');
}

function getCookie(name) {
    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return value ? unescape(value[2]) : null;
};

// 작업중 (사용X)
let findGridInstance = function (obj, constructor) {
    let _instances = [];
    let key_blacklist = ['window', 'self'];

    let recursiveSearch = function(currentObj) {
        // 순환 참조 방지를 위한 예외 처리. Maximum Callstack
        if (!currentObj || typeof currentObj !== 'object') return;

        for (let key in currentObj) {
            if (!key_blacklist.includes(key) && Object.hasOwn(currentObj, key)) {
                let item = currentObj[key];
                if (item instanceof constructor) {
                    _instances.push(item);
                }

                recursiveSearch(item);
            }
        }
    }

    recursiveSearch(obj);
    return _instances;
}