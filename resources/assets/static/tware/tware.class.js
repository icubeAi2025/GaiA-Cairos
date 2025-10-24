var tware = tware || {};
tware.utils = {}

tware.constants = {
    NO_ITEMS : '데이터가 없습니다.'
}

/**
 * javascript 에 클래스 개념 도입 (생성자, 상속)
 *
 * @group tware
 * @namespace tware
 * @param o
 * @param oParent
 * @returns {*}
 * @constructor
 */
tware.Class = function (o, oParent) {
    var $init = null;
    var $params = null;
    var checkDirectCall = function () { return true; };
    var F;

    if ("$init" in o) {
        $init = o.$init;
        delete o.$init;
    }

    if ("$params" in o) {
        $params = o.$params;
        delete o.$params;
    }

    if (typeof oParent === "undefined") {
        F = function () {
            var args = arguments;

            if (!(this instanceof F)) {
                return new F(checkDirectCall, args);
            }

            if (args.length && args[0] === checkDirectCall) {
                args = args[1];
            }

            if ($init !== null) {
                $init.apply(this, args);
            }
        };
    } else {
        F = function () {
            var args = arguments;

            if (!(this instanceof F)) {
                return new F(checkDirectCall, args);
            }

            if (args.length && args[0] === checkDirectCall) {
                args = args[1];
            }

            if ($params) {
                oParent.call(this, $params, $init);
            } else {
                oParent.call(this, {}, $init);
            }
        };

        var Parent = function () {};
        Parent.prototype = oParent.prototype;
        F.$super = oParent.prototype;
        F.prototype = new Parent();
        F.prototype.constructor = F;
    }

    for (var i in o) {
        if (o.hasOwnProperty(i) && i !== "prototype") {
            F.prototype[i] = o[i];
        }
    }

    return F;
};


var regExpByKor = /[ㄱ-ㅎㅏ-ㅣ가-힣]/g;

;(function () {
    tware.utils = tware.Class({

        // if (method === 'get') {
        //     if (data) {
        //         const params = [];
        //         for (var key in data) {
        //             params.push(key + '=' + data[key]);
        //         }
        //
        //         if (url.indexOf('?') < 0) {
        //             url += '?' + params.join('&');
        //         }
        //     }
        // }

        loadScript: function(url, callback) {
            let script = document.createElement("script");
            script.type = "text/javascript";
            script.src = url;

            document.head.appendChild(script);
        }
        , isJsonString: function(str) {
            try {
                var json = JSON.parse(str);
                return (typeof json === 'object');
            } catch (e) {
                return false;
            }
        }
        , hasKor: function(val) {
            return regExpByKor.test(val);
        }
        , removeHangul: function(val) {
            return this.hasKor(val) ? val.replace(regExpByKor, '') : val;
        }
        , isEmptyObject: function(param) {
            return Object.keys(param).length === 0 && param.constructor === Object;
        }

        , hasClass: function(el, clsName) {
            var regex = new RegExp('(^|\\s)' + clsName + '(\\s|$)');
            return regex.test(el.className);
        }

        , addClass: function(el, clsName) {
            if (!hasClass(el, clsName)) {
                el.className += ' ' + clsName;
            }
        }

        , removeClass: function(el, clsName) {
            var regex = new RegExp('(^|\\s)' + clsName + '(\\s|$)');
            el.className = el.className.replace(regex, ' ');
        }
        , parseSerializeValues: function (values) {
            var params = {};

            if (!values) {
                return params;
            }

            var tmpParams = values.split('&');

            tmpParams.map(function(item, idx) {
                var tmp = item.split('=');
                params[tmp[0]] = tmp[1];
            });
            return params;
        }
        , parseQuery: function () {
            var query = location.search.split('?')[1];

            if (!query) {
                return {};
            }

            // var a = window.location.search.substr(1).split('&');
            // if (a == '') return {};
            // var b = {};
            // for (var i = 0; i < a.length; ++i) {
            //     var p = a[i].split('=', 2);
            //     if (p.length == 1)
            //         b[p[0]] = '';
            //     else
            //         b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, ' '));
            // }
            // return b;

            return this.parseSerializeValues(query);
        }
        , handlebarCompile: function (html, value) {
            // console.log('handlebarCompile', value)
            var template = Handlebars.compile(html);

            value = value || {};
            var params = {data: value, user: window.secureUser};
            params.emptyRows = tware.constants.NO_ITEMS || '데이터가 없습니다.';

            return template(params);
        }
        , getPageSize : function(totalRowCount, rowCount){
            return Math.ceil(totalRowCount/rowCount);
        }
        , checkFileExt: function(file) {
            if (!(/(gif|jpe?g|tiff?|png|webp|bmp)$/i.test(file.type))) {
                alert('파일 타입이 png, bmp, webp, jpg, jpeg, 또는 gif가 아닙니다.');
                return false;
            }
            return true;
        }
        , checkFileExtention: function (fileName, allowed, restricted){
            var allowedExtentions = allowed ? allowed.toLowerCase() : "";
            var restrictedExtentions = restricted ? restricted.toLowerCase() : "";
            var basicRestrictedExtentions = "js,exe,bat,sh".toLowerCase();

            var check = false;
            var ext = '';
            if (fileName != '' && fileName !='image.png'){
                ext = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
                if (basicRestrictedExtentions.indexOf(ext) != -1){
                    alert('확장자가 ' + basicRestrictedExtentions + ' 인 파일은 불가능합니다.');
                }
                else if (allowedExtentions != null && allowedExtentions != '' && allowedExtentions.indexOf(ext) == -1){
                    alert('확장자가 ' + allowedExtentions + ' 인 파일만 가능합니다.');
                }
                else if (restrictedExtentions != null && restrictedExtentions != '' && restrictedExtentions.indexOf(ext) != -1){
                    alert('확장자가 ' + restrictedExtentions + ' 인 파일은 불가능합니다.');
                }
                else{
                    check = true;
                }
            }
            return check;
        }
        , calculationByByte: function(byte) {
            var bytes = parseInt(byte, 10);
            var s = ['bytes', 'KB', 'MB', 'GB', 'TG', 'PB'];
            var e = Math.floor(Math.log(bytes) / Math.log(1024));

            if (e == '-Infinity') {
                return '0 ' + s[0];
            } else {
                return ( bytes / Math.pow( 1024, Math.floor(e) ) ).toFixed(2) + ' ' + s[e];
            }
        }
        , getBytes : function(s) {
            var l = 0;
            for (var i=0; i<s.length; i++) l += (s.charCodeAt(i) > 128) ? 2 : 1;
            return l;
        }
        , cutByteLength : function(s, len) {
            var l = 0;
            for (var i=0; i<s.length; i++) {
                l += (s.charCodeAt(i) > 128) ? 2 : 1;
                if (l > len) return s.substring(0,i);
            }
            return s;
        }
        , copyToClipboard: function(options) {
            var textBox = document.getElementsByName(options.targetName)[options.targetIndex];
            textBox.select();
            document.execCommand('copy');
        }
        , hasDuplicates: function(array) {
            return _.uniq(array).length !== array.length;
        }
        , convertTree: function(data, options) {
            var rootNodes = [];

            var traverse = function(nodes, item, index) {
                if (nodes instanceof Array) {
                    return nodes.some(function(node) {
                        if (node[options.id] === item[options.pId]) {
                            node.hasChildren = true;
                            node.children = node.children || [];
                            return node.children.push(data.splice(index, 1)[0]);
                        }

                        return traverse(node.children, item, index);
                    });
                }
            }

            while (data.length > 0) {
                data.some(function(item, index) {
                    if (item[options.pId] === options.rootId) {
                        return rootNodes.push(data.splice(index, 1)[0]);
                    }

                    return traverse(rootNodes, item, index);
                });
            }

            return rootNodes;
        }

        , deviceCheck: function() {
            var mobile = (/iphone|ipad|ipod|android/i.test(navigator.userAgent.toLowerCase()));
            if (mobile) {
                var userAgent = navigator.userAgent.toLowerCase();
                if (userAgent.search('android') > -1) {
                    return 'android';
                } else if ((userAgent.search('iphone') > -1) || (userAgent.search('ipod') > -1) || (userAgent.search('ipad') > -1)) {
                    return 'ios';
                } else {
                    return 'other';
                }
            } else {
                return 'pc';
            }
        }

        , numberWithCommas: function(val) {
            var parts = val.toString().split('.');
            return parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',') + (parts[1] ? '.' + parts[1] : '');
        }

        , isNumberKey: function(evt, options) {
            var charCode = evt.which || evt.keyCode;
            var pattern0 = /^-?\d*[.]\d*$/;
            var patternNum = /^-?\d*\.?\d*$/;

            if ( charCode != 45 && charCode != 46 && charCode > 31 && (charCode < 48 || charCode > 57))
                return false;

            var val = evt.target.value;
            if (pattern0.test(val)) {
                if (charCode == 46) {
                    return false;
                }
            }

            if (val.search('-') > -1 && charCode == 45) {
                return false;
            }
            if (charCode == 45 && !patternNum.test(val)) {
                return false;
            }

            options = options || {};
            if (options.fixed) {
                var pattern2 = new RegExp('\\d*[.]\\d{' + options.fixed + '}', 'gi');
                if (pattern2.test(val)) {
                    console.log('소수점')
                    return false;
                }
            }
            return true;
        }
        , validateEmail: function(email) {
            var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
            return re.test(email);
        }

        , escapeCharRollback: function(val) {
            val = val.replaceAll('&lt;', '<');
            val = val.replaceAll('&gt;', '>');
            val = val.replaceAll('&#40;', '(');
            val = val.replaceAll('&#41;', ')');
            val = val.replaceAll('&#35;', '#');
            val = val.replaceAll('&#38;', '&');
            val = val.replaceAll('&amp;', '&');
            val = val.replaceAll('&quot;', '\"');
            val = val.replaceAll('&#39;', '\'');
            val = val.replaceAll('&nbsp;', ' ');
            val = val.replaceAll('&ldquo;', '“');
            val = val.replaceAll('&rdquo;', '”');
            return val;
        }

        , getDistance: function(point1, point2) {

            var R = 6371;

            var lat1 = point1.lat * Math.PI/180;
            var lat2 = point2.lat * Math.PI/180;
            var lon1 = point1.lng * Math.PI/180;
            var lon2 = point2.lng * Math.PI/180;

            var d = Math.acos(
                Math.sin(lat1) * Math.sin(lat2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.cos(lon2 - lon1)) * R;

            return d;
        }

        /**좌표간 거리구하는 함수
         * num : 거리
         * places : 지수
         */
        , round: function (num, places) {
            var factor = Math.pow(10, places);
            var increment = 5 / (factor * 10);
            return Math.floor((num+increment)*factor)/factor;
        }

        , makeSelectbox: function(options, items, selectedValue) {
            items.forEach(function(item, idx) {
                $('#' + options.id).append('<option value="' + item.value + '" ' + (item.value === selectedValue ? 'selected' : '') +'>' + item.name + '</option>');
            });

            $('#' + options.id).on('change', options.callback);
        }

        , dateDiff: function(date1, date2) {
            if (date1.length < 8) return -1;
            if (date2.length < 8) return -1;
            date1 = date1.substring(0,4) + "/" + date1.substring(4,6) + "/" +date1.substring(6);
            date2 = date2.substring(0,4) + "/" + date2.substring(4,6) + "/" +date2.substring(6);

            var sDate = new Date(date1);
            var eDate = new Date(date2);
            var timeSpan = (eDate-sDate)/86400000;
            var daysApart = Math.abs(Math.round(timeSpan));
            return daysApart;
        }

        , dateAdd: function(opts) {
            var pInterval = opts.ymd;
            var pAddVal = opts.addVal;
            var pYyyymmdd = opts.date;
            var pDelimiter = opts.delimiter;

            var yyyy;
            var mm;
            var dd;
            var cDate;
            var oDate;
            var cYear, cMonth, cDay;

            if (pDelimiter != "") {
                pYyyymmdd = pYyyymmdd.replace(eval("/\\" + pDelimiter + "/g"), "");
            }

            yyyy = pYyyymmdd.substr(0, 4);
            mm  = pYyyymmdd.substr(4, 2);
            dd  = pYyyymmdd.substr(6, 2);

            if (pInterval == "yyyy") {
                yyyy = (yyyy * 1) + (pAddVal * 1);
            } else if (pInterval == "m") {
                mm  = (mm * 1) + (pAddVal * 1);
            } else if (pInterval == "d") {
                dd  = (dd * 1) + (pAddVal * 1);
            }

            cDate = new Date(yyyy, mm - 1, dd);
            cYear = cDate.getFullYear();
            cMonth = cDate.getMonth() + 1;
            cDay = cDate.getDate();

            cMonth = cMonth < 10 ? "0" + cMonth : cMonth;
            cDay = cDay < 10 ? "0" + cDay : cDay;

            if (pDelimiter != "") {
                return cYear + pDelimiter + cMonth + pDelimiter + cDay;
            } else {
                return cYear + cMonth + cDay;
            }

        }

        , getDateNow: function() {
            var date = new Date();

            var year = date.getFullYear();
            var month = (date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
            var day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
            var now = year + "-" + month + "-" + day;

            return now;
        }

        , replaceAll: function(strString, strChar) {
            var strTmp = "";
            for(var i = 0; i < strString.length; i++) {
                if (strString.charAt(i) != strChar)
                    strTmp = strTmp +strString.charAt(i);
            }
            return strTmp;
        }

        , getTotalPage: function(count, row) {
            return  (count % row) > 0 ? parseInt(count / row)+1 : parseInt(count / row);
        }

        , moveOffset: function(obj, ex) {
            var pos = $(obj).offset();
            var extra_space = ex;
            var duration = "slow";
            $('html, body').animate({scrollTop : pos.top - extra_space}, duration);
        }

        , makeDimmed: function(popEl) {
            $("<div class='dimmed'></div>").css('opacity','0.5').appendTo("body").on("click", function(e) {
                removeDimmed();
                $("#popup_btn_confirm").click();

                if(popEl != undefined) {
                    $(popEl).hide();
                }
            });

            $("body").addClass('overH');
        }

        , removeDimmed: function() {
            $("body").removeClass('overH');
            $("div").remove(".dimmed");
        }

        , openPopup: function(url, options) {
            var popupWin;

            if (options.name == null) {
                options.name = '_blank';
            }
            if (!options.method) {
                options.method = 'get';
            }

            if (!options.top || !options.left) {
                options.top = (screen.height / 2) - (options.height / 2);
                options.left = (screen.width / 2) - (options.width /2);
            }

            // if( /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor) ){
            //     popupWin = window.open(url, options.name, `width=${options.width},height=${options.height},top=${top},left=${left},resizable=0,toolbar=0,menubar=0,location=0`);
            // }else{
            //     popupWin = window.open(url, options.name, 'resizable=0,toolbar=0,menubar=0,location=0');
            // }
            //popupWin = window.open(url, name,`toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=no,resizable=0,width=${options.width},height=${options.height},top=${options.top},left=${options.left}`);

            if (options.method === 'post') {
                var form = document.createElement('form');
                form.setAttribute('method', 'post');
                form.setAttribute('action', '/popup' + url);
                form.setAttribute('target', 'popup_id');

                var input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'aa';
                input.value = '변수값1';
                form.appendChild(input);

                input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'callback';
                input.value = 'test';
                form.appendChild(input);

                document.body.appendChild(form);

                form.submit();

                document.body.removeChild(form);
            }

            popupWin.focus();
        }
        , mergeRowSpan: function(className) {
            if (className.substr(0, 1) !== '.') {
                className = '.' + className
            }

            $(className).each(function() {
                var rows = $(className + ':contains("' + $(this).text() + '")');
                if (rows.length > 1) {
                    rows.eq(0).attr('rowspan', rows.length);
                    rows.not(':eq(0)').remove();
                }
            });
        }
        , pivot: function(array) {
            var initArray = [
                {
                    "date":"2017-08-15",
                    "data":[
                        {
                            "color":"orange",
                            "count":100
                        },
                        {
                            "color":"green",
                            "count":101
                        }
                    ]
                },
                {
                    "date":"2017-08-14",
                    "data":[
                        {
                            "color":"orange",
                            "count":102
                        },
                        {
                            "color":"green",
                            "count":103
                        }
                    ]
                }
            ]

            // var result = _(initArray)
            //     //.map('data')
            //     //.flatten()
            //     .flatMap('data') // instead of .map('data').flatten()
            //     .groupBy('color')
            //     .map((item, key) => ({
            //         color: key,
            //         count: _.map(item, 'count')
            //     }))
            //     .value()
            //
            // console.log(result);

            var result = {};
            for(var i = 0; i< array.length; i++) {

                if(!result[array[i][0]]){
                    result[array[i][0]] = {};
                }

                result[array[i][0]][array[i][1]] = array[i][2];
            }

            return result;
        }
        , validateForm: function(form) {
            var requiredEl = $('[required]', form);
            var isValid = true;
            var i = 0;

            while (i < requiredEl.length) {
                var el = requiredEl[i];

                if (el.tagName === 'TEXTAREA' || el.tagName === 'SELECT') {
                    isValid = el.value !== '';
                } else {
                    if (el.type === 'text') {
                        isValid = el.value !== '';
                    } else if (el.type === 'file') {
                        // isValid = el.value !== '';
                    } else {
                        isValid = $('[name="' + el.name + '"]').is(':checked');
                    }
                }

                if (!isValid) {
                    break;
                }

                i++;
            }

            return isValid;
        }
        , resizeImage(imgEl, file, fnCallback) {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = function(event) {
                const checkResizeSize = 1024 * 1024 * 1;
                console.log(`fileSize : ${tware.utils.calculationByByte(file.size)} isResize : ${file.size > checkResizeSize}`);

                if (file.size > checkResizeSize) {
                    const img = new Image();
                    img.src = event.target.result;
                    img.onload = function() {
                        const canvas = document.createElement('canvas');
                        const ctx = canvas.getContext('2d');
                        const scaleFactor = Math.max(1920 / img.width, 1080 / img.height);

                        canvas.width = img.width * scaleFactor;
                        canvas.height = img.height * scaleFactor;

                        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);

                        // 이미지를 JPEG 형식으로 변환
                        const resizedDataURL = canvas.toDataURL('image/jpeg');

                        imgEl.src = resizedDataURL;

                        const resData = tware.utils.createImageFile(file.name, resizedDataURL);

                        if (fnCallback) {
                            fnCallback.call(this, resData)
                        }
                    };
                } else {
                    const result = reader.result;
                    imgEl.style.width = '100%';
                    imgEl.style.height = 'auto';
                    imgEl.src = URL.createObjectURL(file);

                    if (fnCallback) {
                        fnCallback.call(this, {file: file, data: result});
                    }
                }
            };
        }
        , createImageFile(fileName, fileDataUrl) {
            const base64Data = fileDataUrl.split(',')[1];
            const contentType = fileDataUrl.split(';')[0].split(':')[1];
            const byteCharacters = atob(base64Data);
            const byteNumbers = new Array(byteCharacters.length);

            for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }

            const byteArray = new Uint8Array(byteNumbers);
            const blob = new Blob([byteArray], { type: contentType });
            const newFile = new File([blob], fileName, { type: contentType });

            // const url = URL.createObjectURL(blob);
            // const a = document.createElement('a');
            // a.href = url;
            // a.download = filename;
            // document.body.appendChild(a);
            // a.click();
            // document.body.removeChild(a);
            // URL.revokeObjectURL(url); // Clean up the URL object

            console.log(`new file size: ${tware.utils.calculationByByte(newFile.size)}`);

            return {file: newFile, data: base64Data};

        }
        , async uploaderCreate(options) {
            // const observer = new MutationObserver((mutations) => {
            //     mutations.forEach((mutation) => {
            //         // 변경된 내용 처리 로직
            //         if (mutation.type === 'childList') {
            //             console.log('자식 노드가 변경되었습니다.');
            //         } else if (mutation.type === 'attributes') {
            //             console.log('속성이 변경되었습니다.');
            //         }
            //     });
            // });

            if (!options || !options.wrapId || !options.files || (options.mode === "U" && !options.callbackHandler)) {
                return;
            }

            options.limitFileSize = options.limitFileSize || 0;

            let uploader = {
                files: [],
                totalFileSize: 0
            };

            const getExtensionOfFilename = (fileName) => {
                var _fileLen = fileName.length;
                var _lastDot = fileName.lastIndexOf('.');
                var _fileExt = fileName.substring(_lastDot + 1, _fileLen).toLowerCase();
                return _fileExt;
            }

            class UploadFilesService {
                uploadUrl = '';

                constructor(uploadUrl) {
                    this.uploadUrl = uploadUrl;
                }

                upload(index, file, params, onUploadProgress) {
                    let formData = new FormData();
                    formData.append('index', index);
                    formData.append('files', file);

                    if (params) {
                        for (const key in params) {
                            formData.append(key, params[key]);
                        }
                    }

                    return axios.post(this.uploadUrl, formData, {
                        headers: {
                            "Content-Type": "multipart/form-data"
                        },
                        timeout: 60 * 1000,
                        onUploadProgress
                    });
                }

                uploadFiles(files, params, onUploadProgress) {
                    let formData = new FormData();

                    $.each(files, (index, file) => {
                        formData.append('files', file);
                    });

                    if (params) {
                        for (const key in params) {
                            formData.append(key, params[key]);
                        }
                    }

                    return axios.post(this.uploadUrl, formData, {
                        headers: {
                            "Content-Type": "multipart/form-data"
                        },
                        timeout: 60 * 1000,
                        onUploadProgress
                    });
                }
                downloadFiles(fileMeta){

                }

                getFiles() {
                    return axios.get("/files");
                }
            }

            return await new Promise((resolve, reject) => {
                if (document.querySelector('#vueTemplate')) {
                    $(`#${options.wrapId}`).append(document.querySelector('#vueTemplate').innerHTML);

                    // const targetNode = document.querySelector('#vueApp');
                    //
                    // const config = {
                    //     attributes: true, // 속성 변경 감지
                    //     childList: true,  // 자식 노드의 추가/제거 감지
                    //     subtree: true     // 대상 노드의 하위 트리까지 감지
                    // };
                    // observer.observe(targetNode, config);

                    const uploadFilesService = new UploadFilesService(`${options.url}`);

                    const uploaderComponent  = {
                        name: "file-uploader",
                        template: document.querySelector('#vueUploaderTemplate').innerHTML,
                        data() {
                            return {
                                mode : options.mode || 'R',
                                type : options.type || 'multiple',
                                message : '',
                                limitFileSize : options.limitFileSize,
                                totalFileSize : 0,
                                progressInfo: {percentage: 0},
                                uploadedCount : 0,
                                totalFiles : [],
                                totalCount : 0,
                                computedFileInfos : [],
                                selectedFiles : [],
                                selectedFileInfos : [],

                                canvas: null,
                                ctx: null,
                            };
                        },
                        methods: {
                            init() {
                                this.progressInfo.percentage = 0;
                            },
                            onClick () {
                                this.$refs.fileInput.click()
                            },
                            onDragenter (event) {
                                if (this.mode === 'R') {
                                    return;
                                }

                                this.isDragged = true;
                            },
                            onDragleave (event) {
                                if (this.mode === 'R') {
                                    return;
                                }

                                this.isDragged = false;
                            },
                            onDragover (event) {
                                if (this.mode === 'R') {
                                    return;
                                }

                                event.preventDefault();
                            },
                            onDrop (event) {
                                if (this.mode === 'R') {
                                    return;
                                }

                                event.preventDefault();
                                this.isDragged = false;

                                const res = this.validateFiles(event.dataTransfer.files);

                                if (!res.result) {
                                    gaiaCommon.customAlert(
                                        "허용되지 않은 파일 타입입니다. : " + res.name
                                    );
                                    return;
                                }

								if (this.totalFileSize+res.size > this.limitFileSize) {
									console.log('용량초과');
                                    gaiaCommon.customAlert('첨부파일이 제한 용량을 초과하였습니다.');
                                }

                                this.selectedFiles = [...this.selectedFiles, ...event.dataTransfer.files];

                                this.transferSelectedFiles();
                            },
                            onSelectFile(event) {
                                this.init();

                                const res = this.validateFiles(event.target.files);

                                if (!res.result) {
                                    gaiaCommon.customAlert(
                                        "허용되지 않은 파일 타입입니다. : " + res.name
                                    );
                                    return;
                                }

								if (res.size > this.limitFileSize) {
									console.log('용량초과');
                                    gaiaCommon.customAlert('첨부파일이 제한 용량을 초과하였습니다.');
                                }

                                this.selectedFiles = event.target.files;

                                this.transferSelectedFiles();
                            },
                            onRemoveMarked(info) {
                                info.mode = 'D';
                                this.calculationFileSize();
                            },
                            onRemove(idx) {
                                const dataTranster = new DataTransfer();

                                Array.from(this.selectedFiles)
                                    .filter((file, index) => {
                                        return index !== idx;
                                    })
                                    .forEach(file => {
                                        dataTranster.items.add(file);
                                    });

                                this.selectedFiles = dataTranster.files;
                                this.transferSelectedFiles();
                            },
                            onRemoveAll() {
                                this.totalFiles.map((el, idx) => {
                                    if (el.mode !== 'C') {
                                        el.mode = 'D';
                                    }
                                });
                                this.selectedFiles = [];
                                this.transferSelectedFiles();
                            },
                            onUpload() {
                                console.log('params', options.params)
                                if (this.selectedFiles.length > 0) {
                                    if (!this.isValidUploadSize()) {
                                        console.log('용량 초과')
                                        gaiaCommon.customAlert('제한용량을 초과하여 업로드 할 수 없습니다.');
										gaiaCommon.LoadingOverlay('body', false);
                                        return false;
                                    }

                                    this.init();

                                    if (this.type !== 'batch') {
                                        this.uploadedCount = 0;
                                        uploader.files = [];
                                        uploader.files = uploader.files.concat(toRaw(this.totalFiles));

                                        for (let i = 0; i < this.selectedFiles.length; i++) {
                                            this.doUpload(i, this.selectedFiles[i]);
                                        }
                                    } else {
                                        this.doUploadBatch();
                                    }
                                } else {
                                    if (options.callbackHandler) {
                                        options.callbackHandler(uploader.files);
                                    }
                                }
                            },
                            transferSelectedFiles() {
                                this.selectedFileInfos = [];
                                this.totalFileSize = 0;
                                uploader.totalSize = 0;

                                Array.from(this.selectedFiles)
                                    .forEach(async (file) => {
                                        const info = {
                                            fileName: file.name,
                                            size: this.getfileSize(file.size),
                                            orgSize: file.size,
                                            ext: getExtensionOfFilename(file.name),
                                            percentage: 0,
                                            src: ''
                                        }

                                        await this.setImagePath(info, file);

                                        this.selectedFileInfos.push(info);
                                        this.calculationFileSize();
                                    });
                            },
							getfileSize(size) {
								var s = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB'];
								var e = Math.floor(Math.log(size) / Math.log(1024));
								return (size / Math.pow(1024, e)).toFixed(2) + " " + s[e];
							},
                            calculationFileSize() {
                                const totalSize = [...this.computedFileInfos, ...this.selectedFileInfos]
                                    .reduce((sum, f) => sum + (Number(f.orgSize) || 0), 0);

                                this.totalFileSize = totalSize;
                                uploader.totalSize = totalSize;
                            },
                            calculationByByte(size) {
                                return tware.utils.calculationByByte(size);
                            },
                            isImage(name) {
                                return ['jpg', 'gif', 'png'].find( (e) => e === getExtensionOfFilename(name) ) ? true : false;
                            },
                            makeImgPath(path) {
                                if (!path) {
                                    return '';
                                }

                                path = path.replace(/\\/gi, '/');
                                return path.substring(path.indexOf('/upload'))
                            },
                            setImagePath(info, file) {
                                return new Promise(resolve => {
                                    let reader = new FileReader();
                                    reader.onload = e => {
                                        info.src = e.target.result;
                                        resolve(e.target.result);
                                    }
                                    reader.readAsDataURL(file);
                                })
                            },
                            isValidUploadSize() {
                                return this.totalFileSize <= this.limitFileSize;
                            },
                            validateFiles(files) {
                                const allowedExtensions = ['text/plain', 'text/html', 'image/', 'audio/', 'video/', 'application/ogg', 'application/pdf'];
                                let isValid = true;
                                let fileName = '';
                                let fileSize = 0;

                                for (let i = 0; i < files.length; i++) {
                                    const file = files[i];
                                    const fileType = file.type;
									console.log('file :: ',  file);
                                    fileName = file.name;
									fileSize = fileSize+file.size;
                                    // if (!allowedExtensions.includes(fileType)) {
                                    if ( !allowedExtensions.some( (ext) => fileType.startsWith(ext) ) ) {
                                        // isValid = false;
                                        // break;
                                    };
                                }

                                return { result: isValid, name: fileName, size: fileSize };
                            },
                            showPer(per) {
                                this.canvas.style.display = 'block';

                                this.ctx.clearRect(0, 0, 400, 400);
                                this.ctx.strokeStyle = "#f66";
                                this.ctx.lineWidth = 10;
                                this.ctx.beginPath();
                                this.ctx.arc(60, 60, 50, 0, Math.PI * 2 * per / 100);
                                this.ctx.stroke();

                                this.ctx.font = '32px serif';
                                this.ctx.fillStyle = "#000";
                                this.ctx.textAlign = 'center';
                                this.ctx.textBaseline = 'middle';
                                this.ctx.fillText(per + '%', 60, 60);
                            },
                            doUpload(idx, file) {
                                uploadFilesService.upload(idx, file, options.params, (event) => {
                                    this.selectedFileInfos[idx].percentage = Math.round(100 * event.loaded / event.total);
                                    // this.showPer(Math.floor(event.loaded / event.total * 100));
                                })
                                    .then((response) => {
                                        console.log('response', response)
                                        this.canvas.style.display = 'none';

                                        if (!response.data.ok || !response.data.details.result) {
                                            this.selectedFileInfos[idx].message = response.data.details.resultMsg;
                                            return;
                                        }

                                        if (this.selectedFileInfos[idx].percentage >= 100) {
                                            this.uploadedCount++;
                                        }

                                        response.data.details.metas.map((data, index) => {
                                            return data.mode = 'C';
                                        });

                                        uploader.files = uploader.files.concat(toRaw(response.data.details.metas));

                                        return this.uploadedCount == this.selectedFiles.length;
                                    })
                                    .then((isSuccess) => {
                                        if (isSuccess && options.callbackHandler) {
                                            options.callbackHandler(uploader.files);
                                        }
                                    })
                                    .catch((e) => {
                                        console.log('error', e)
                                        this.selectedFileInfos[idx].percentage = 0;
                                        this.message = "Could not upload the file:" + file.name;
                                    });
                            },
                            doUploadBatch() {
                                uploadFilesService.uploadFiles(this.selectedFiles, options.params, (event) => {
                                    this.progressInfo.percentage = Math.round(100 * event.loaded / event.total);
                                    // this.showPer(Math.floor(event.loaded / event.total * 100));
                                })
                                    .then((response) => {
                                        if (!response.data.ok || !response.data.details.result) {
                                            this.message = response.data.details.resultMsg;
                                            return;
                                        }

                                        response.data.details.metas.map((data, index) => {
                                            return data.mode = 'C';
                                        });

                                        uploader.files = uploader.files.concat(toRaw(response.data.details.metas));

                                        return true;
                                    })
                                    .then((isSuccess) => {
                                        if (isSuccess && options.callbackHandler) {
                                            options.callbackHandler(uploader.files);
                                        }
                                    })
                                    .catch((e) => {
                                        console.log('error', e)
                                        this.progressInfo.percentage = 0;
                                        this.message = "Could not upload the file:";
                                    });
                            },
                            uploadCallback(response) {

                            },
                            downloadFile(info){
                                const pureFileMeta = {
                                    dirPath: info.dirPath,
                                    ext: info.ext,
                                    fileName: info.fileName,
                                    filePath: info.filePath,
                                    mode: info.mode,
                                    originalFilename: info.originalFilename,
                                    size: info.size
                                };
                                console.log("FILE REAL PATH :",info.dirPath+"\\"+info.fileName);
                                const link = document.createElement('a');
                                link.href = `/resource/download?filePath=${encodeURIComponent(info.filePath)}&orgName=${encodeURIComponent(info.originalFilename)}`
                                link.download = info.originalFilename;
                                link.click();
                                link.remove();

                                // $.ajax({
                                //     url:'/resource/download',
                                //     method: 'POST',
                                //     data:JSON.stringify(pureFileMeta),
                                //     contentType: "application/json",
                                //     dataType: "json",
                                //     xhrFields: {
                                //         responseType: "blob",
                                //     },
                                //     success:(blob, status, xhr)=>{
                                //         const disposition = xhr.getResponseHeader('Content-Disposition');
                                //         let fileName = info.originalFilename;
                                //         if (disposition && disposition.indexOf('filename=') !== -1) {
                                //             fileName = decodeURIComponent(disposition.split('filename=')[1].replace(/"/g, ''));
                                //         }
                                //
                                //         const link = document.createElement('a');
                                //         const url = window.URL.createObjectURL(blob);
                                //         link.href = url;
                                //         link.download = fileName;
                                //         document.body.appendChild(link);
                                //         link.click();
                                //         link.remove();
                                //         window.URL.revokeObjectURL(url);
                                //     },
                                //     fail:(response)=>{
                                //         console.log("ERROR:",response);
                                //     }
                                // })
                            }
                        },
                        mounted()   {
                            // uploadFilesService.getFiles().then((response) => {
                            //     this.computedFileInfos = response.data;
                            // });

                            this.canvas = document.getElementById("canvas");
                            this.ctx = this.canvas.getContext("2d");

                            this.totalFiles = JSON.parse( JSON.stringify( options.files ) );
                            this.totalFiles.map((el, idx) => {
                                el.ext = getExtensionOfFilename(el.fileName);
                            });

                            this.computedFileInfos = computed(() => {
                                return this.totalFiles.filter((el) => el.mode != 'D');
                            });

                            this.totalCount = computed(() => {
                                return [...this.computedFileInfos, ...this.selectedFileInfos].length;
                            });

                            // this.$refs.fileInput.accept = 'image/*';

                            // // 드래그 앤 드롭 이벤트 처리
                            // dropArea.addEventListener("dragover", (e) => {
                            //     e.preventDefault();
                            //     dropArea.style.backgroundColor = "#eee";
                            // });
                            //
                            // dropArea.addEventListener("dragleave", () => {
                            //     dropArea.style.backgroundColor = "#fff";
                            // });
                            //
                            // dropArea.addEventListener("drop", (e) => {
                            //     e.preventDefault();
                            //     dropArea.style.backgroundColor = "#fff";
                            //     const file = e.dataTransfer.files;
                            //     if (file && file.type.startsWith("image")) {
                            //         displayImage(file);
                            //     }
                            // });
                            //
                            // // 파일 입력 필드 변경 이벤트 처리
                            // fileInput.addEventListener("change", () => {
                            //     const file = fileInput.files[0];
                            //     if (file && file.type.startsWith("image")) {
                            //         displayImage(file);
                            //     }
                            // });
                            //
                            // // 클릭 이벤트 처리
                            // dropArea.addEventListener("click", () => {
                            //     fileInput.click();
                            // });

                            uploader = {
                                files: toRaw(this.totalFiles),
                                totalSize: this.totalFileSize,
                            };

                            this.calculationFileSize();

                            uploader.upload = () => {
                                uploaderComponent.methods.onUpload.call(this);
                            };

                            resolve(uploader);
                        }
                    };

                    const { createApp, computed, ref, toRaw } = Vue;
                    const vueApp = createApp({
                        data() {
                            return {
                                message: ''
                            }
                        }
                    });

                    vueApp.component('file-uploader', uploaderComponent);
                    vueApp.mount(`#${options.wrapId}`);
                }
            });
        }
    })();
})();

class UploadFilesService {
    uploadUrl = '';

    constructor(uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    upload(index, file, onUploadProgress) {
        let formData = new FormData();
        formData.append('index', index);
        formData.append('files', file);

        return axios.post(this.uploadUrl, formData, {
            headers: {
                "Content-Type": "multipart/form-data"
            },
            timeout: 60 * 1000,
            onUploadProgress
        });
    }

    uploadFiles(files, onUploadProgress) {
        let formData = new FormData();

        $.each(files, (index, file) => {
            formData.append('files', file);
        });

        return axios.post(this.uploadUrl, formData, {
            headers: {
                "Content-Type": "multipart/form-data"
            },
            timeout: 60 * 1000,
            onUploadProgress
        });
    }

    getFiles() {
        return axios.get("/files");
    }
}
