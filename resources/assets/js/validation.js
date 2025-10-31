
$(document).ready(function () {
    $(".number").on("input", function () {
        // 숫자만 허용
        if(/[^0-9]/g.test(this.value)){
            gaiaCommon.customAlert("숫자만 입력 가능합니다.")
            this.value = this.value.replace(/[^0-9]/g, "");
        }
    });

    // 금액-------------------------------------------------------------------
    $(".cost").on("input", function () {
        // 숫자와 콤마만 허용
        this.value = this.value.replace(/[^0-9,]/g, "");
    });

    $(".cost").on("blur", function () {
        let value = $(this).val().replace(/,/g, ""); // 콤마 제거
        if (value) {
            $(this).val(addCommas(value)); // 콤마 추가
        }
    });

    // 소수점 있는 항목들--------------------------------------------------------
    $(".decimal.3").on("input", function () {
        let value = this.value.replace(/[^0-9.]/g, ""); // 숫자와 소수점만 허용
        let parts = value.split(".");
        if (parts.length > 2) {
            value = parts[0] + "." + parts.slice(1).join("");
        }

        // 소수점 앞에는 최대 3자리만 허용
        if (parts[0].length > 3) {
            parts[0] = parts[0].slice(0, 3);
            value = parts.join(".");
        }

        this.value = value;
    });

    $(".decimal.4").on("input", function () {
        let value = this.value.replace(/[^0-9.]/g, ""); // 숫자와 소수점만 허용
        let parts = value.split(".");
        if (parts.length > 2) {
            value = parts[0] + "." + parts.slice(1).join("");
        }

        // 소수점 앞에는 최대 3자리만 허용
        if (parts[0].length > 4) {
            parts[0] = parts[0].slice(0, 4);
            value = parts.join(".");
        }

        this.value = value;
    });

    $(".decimal.7").on("input", function () {
        let value = this.value.replace(/[^0-9.]/g, ""); // 숫자와 소수점만 허용
        let parts = value.split(".");
        if (parts.length > 2) {
            value = parts[0] + "." + parts.slice(1).join("");
        }

        // 소수점 앞에는 최대 3자리만 허용
        if (parts[0].length > 7) {
            parts[0] = parts[0].slice(0, 7);
            value = parts.join(".");
        }

        this.value = value;
    });

    $(".decimal").on("blur", function () {
        let value = parseFloat(this.value);
        if (!isNaN(value)) {
            // 소수점 두 자리로 포맷팅
            this.value = value.toFixed(2);
        } else {
            this.value = ""; // 값이 숫자가 아닌 경우 빈 문자열로 설정
        }
    });

    $(".decimal2.3").on("input", function () {
        let value = this.value.replace(/[^0-9.]/g, ""); // 숫자와 소수점만 허용
        let parts = value.split(".");
        if (parts.length > 2) {
            value = parts[0] + "." + parts.slice(1).join("");
        }

        // 소수점 앞에는 최대 3자리만 허용
        if (parts[0].length > 3) {
            parts[0] = parts[0].slice(0, 3);
            value = parts.join(".");
        }

        this.value = value;
    });

    $(".decimal2").on("blur", function () {
        let value = parseFloat(this.value);
        if (!isNaN(value)) {
            if (Number.isInteger(value)) {
                this.value = Math.round(value).toString(); // 정수일 경우
            } else {
                this.value = (Math.round(value * 1000) / 1000).toFixed(3); // 소수점 세 자리로 포맷팅, 반올림
            }
        } else {
            this.value = "";
        }
    });

    $(".decimal11_4").on("input", function () {
        let value = this.value.replace(/[^0-9.]/g, ""); // 숫자/소수점만 허용
        let parts = value.split(".");
    
        // 소수점이 여러 개일 경우 첫 번째만 유지
        if (parts.length > 2) {
            value = parts[0] + "." + parts.slice(1).join("");
            parts = value.split(".");
        }
    
        // 정수부 7자리 제한
        if (parts[0].length > 7) {
            parts[0] = parts[0].slice(0, 7);
        }
    
        // 소수부 4자리 제한
        if (parts.length === 2 && parts[1].length > 4) {
            parts[1] = parts[1].slice(0, 4);
        }
    
        this.value = parts.join(".");
    });
    

    /**
     * 2025-01-02
     * 경위도 validation 추가
     */
    $('.latitude, .longitude').on('input', function (event) {
        let value = this.value.replace(/[^0-9.]/g, ""); // 숫자와 소수점만 허용
        let parts = value.split(".");
        if (parts.length > 2) {
            value = parts[0] + "." + parts.slice(1).join("");
        }

        // 소수점 앞에는 최대 3자리만 허용
        if (parts[0].length > 3) {
            parts[0] = parts[0].slice(0, 3);
            value = parts.join(".");
        }

        this.value = value;
    });

    $('.latitude, .longitude').on("blur", function (event) {
        let value = parseFloat(this.value);
        if (!isNaN(value)) {
            if (Number.isInteger(value)) {
                this.value = Math.round(value).toString(); // 정수일 경우
            } else {
                this.value = (Math.floor(value * 100000) / 100000); // 소수점 5 자리로 포맷팅
            }
        } else {
            this.value = "";
        }
    });



    // 번호----------------------------------------------------------------------
    //전화번호
    $(".telNo").on("input", function () {
        this.value = this.value
            .replace(/[^0-9]/g, "")
            .slice(0, 11)
            .replace(/^(\d{3})(\d{3,4})(\d{4})$/, `$1-$2-$3`);
    });

    //팩스 번호
    $(".faxNo").on("input", function () {
        // 숫자와 하이픈만 허용
        this.value = this.value
            .replace(/[^0-9-]/g, "")          // 숫자와 하이픈 외의 문자 제거
            .slice(0, 13)                      // 최대 13자리까지만 허용
            .replace(/^(\d{2,3})(\d{3,4})(\d{4})$/, `$1-$2-$3`);
    });

    //사업자번호
    $(".bsnsmnNo").on("input", function () {
        this.value = this.value
            .replace(/[^0-9]/g, "")
            .slice(0, 10)

    });

    // 이메일------------------------------------------------------------------
    $(".email").on("blur", function () {
        const email = $(this).val();
        const pattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if (email.trim() && !pattern.test(email)) {
            gaiaCommon.customAlert("잘못된 이메일 형식입니다.");
        }
    });

    //영어 입력-----------------------------------------------------------------
    $(".inputEng").on("input", function () {
        this.value = this.value
            .replace(/[^a-z|A-Z0-9\s]/, "")
            .slice(0, 10)

    });

    //한글 입력-----------------------------------------------------------------
    $(".inputKrn").on("input", function () {
        this.value = this.value
            .replace(/[^ㄱ-ㅎ|가-힣0-9\s]/, "")
            .slice(0, 10)

    });

    // 글자수 제한--------------------------------------------------------------
    $(document).ready(function () {
        // 모든 .maxlength 요소 처리
        $('.maxlength').each(function () {
            let maxLength = $(this).attr('maxlength'); // maxlength 속성 값 가져오기
            let $this = $(this);

            if ($this.next(`.char-counter`).length == 0 && $(this).is('textarea')) {
                let parentElement = $(this).parent();
                parentElement.css({
                    'display': 'block'
                });

                // textarea의 경우 글자 수 카운터 추가
                let counter = $(`<div class="char-counter" style="display:flex; justify-content: end; padding: 3px; font-size: var(--font-xs); color: var(--color-gray);">
                                <span class="current-count">0</span>/<span class="max-count">${maxLength}</span>
                             </div>`);
                $(this).after(counter); // textarea 뒤에 추가

                setTimeout(function () {
                    let content = $this.val();
                    if (content) {
                        counter.find('.current-count').text(content.length);
                    }
                }, 100);
            }
        });

        // input 이벤트 바인딩 (중복 바인딩 방지)
        $('.maxlength').on('input', function () {
            let content = $(this).val();
            let maxLength = $(this).attr('maxlength');
            let currentLength = content.length;

            // 글자수 제한
            if (currentLength >= maxLength) {
                $(this).val(content.substring(0, maxLength));
                currentLength = maxLength;
                gaiaCommon.customAlert(`글자수는 최대 ${maxLength}자까지 입력 가능합니다.`);

            }

            // textarea의 경우 글자 수 카운터 업데이트
            if ($(this).is('textarea')) {
                $(this).next('.char-counter').find('.current-count').text(currentLength);
            }
        });
    });

    //날짜 검증--------------------------------------------------------------------------------------------
    $("input[type='date']").on("blur", function () {
        if (this.validationMessage != "") {
            gaiaCommon.customAlert(this.validationMessage)
            this.value = ""
        }
    });

});

function addCommas(value) {
    // return value ? value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") : "";
    if (value === null || value === undefined) return "0";
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
// 숫자에서 콤마를 제거하는 함수
function removeCommas(value) {
    if (value === null || value === undefined) return "0";
    return value ? value.toString().replace(/,/g, "") : "";
}

if (typeof window.validateLatLon === "undefined") {
    window.validateLatLon = function (lonInput, latInput) {
        // 위도와 경도에 사용할 정규식 (숫자 형식, 소수점 포함 가능)
        const latRegex = /^-?([1-8]?\d(\.\d+)?|90(\.0+)?)$/;                    // 90 ~ -90
        const lonRegex = /^-?((1[0-7]\d|[1-9]?\d)(\.\d+)?|180(\.0+)?)$/;        // 180 ~ -180

        // 경도 검증
        if (!lonRegex.test(lonInput)) {
            return { valid: false, target: '.longitude', longitude: lonInput, latitude: latInput };
        }

        // 위도 검증
        if (!latRegex.test(latInput)) {
            return { valid: false, target: '.latitude', longitude: lonInput, latitude: latInput };
        }


        return { valid: true, target: null, longitude: lonInput, latitude: latInput };
    }
}