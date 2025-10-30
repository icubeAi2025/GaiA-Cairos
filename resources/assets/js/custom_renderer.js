/**
 * NewWindowViewRenderer 클래스 정의
 * TOAST UI Grid 셀 내에 값과 새창 아이콘을 렌더링합니다.
 * <options 세팅>
 * urlPopupTemplate : 팝업으로 띄울 url 정보
 * urlTemplate : 상세조회 처리를 위한 url 정보
 * idField : 상세조회 시, 필요한 unique 값
 */
class NewWindowViewRenderer {
    // 클래스의 생성자. 셀의 초기 HTML 요소를 생성.
    constructor(props) {
        this.rowKey = props.rowKey;
        this.gridId = props.columnInfo.renderer.options.gridId;
        const el = document.createElement("div"); // 셀의 내용을 포함할 div 요소 생성
        this.el = el; // 클래스 인스턴스의 el 속성으로 최상위 div 요소를 설정
        // console.log(this.gridId);
        el.style.display = "flex";
        el.style.justifyContent =
            props.columnInfo.renderer.options.align || "left";
        el.style.position = "relative"; // position relative 추가

        // 셀의 값을 포함할 span 요소 생성
        this.valueSpan = document.createElement("span");
        // 셀의 값을 갱신
        const fomatter = props.columnInfo.renderer.options.fomatter || "";
        let displayText = props.value + fomatter;


        this.valueSpan.textContent = gaiaCommon.decodeSafeText(displayText);
        this.valueSpan.id = props.className;
        if (el.style.justifyContent == "left") {
            this.valueSpan.style.paddingLeft = "5px";
            this.valueSpan.style.overflow = "hidden";
            this.valueSpan.style.textOverflow = "ellipsis";
        }

        // 새창 아이콘 요소 생성
        this.newWindowIcon = document.createElement("i");
        this.newWindowIcon.className = "ic ic-sent-to-back";
        this.newWindowIcon.style.cursor = "pointer";
        this.newWindowIcon.style.marginLeft = "10px";
        this.newWindowIcon.style.visibility = "hidden";
        this.newWindowIcon.style.right = "10px";

        if (props.columnInfo.renderer.options.popup == "none") {
            this.newWindowIcon.style.display = "none";
        }

        if (props.columnInfo.renderer.options.align !== "left") {
            this.newWindowIcon.style.position = "absolute";
        }

        // span 요소와 아이콘을 최상위 div 요소에 추가
        el.appendChild(this.valueSpan);
        el.appendChild(this.newWindowIcon);

        // 새창 아이콘 클릭 이벤트 핸들러 추가 (회사 정보 조회 - 팝업)
        this.newWindowIcon.addEventListener("click", () => {
            const rowData = props.grid.getRow(props.rowKey);
            let url;
            if (this.gridId === 'phase_grid') {
                url = props.columnInfo.renderer.options.urlPopupTemplate;
            } else {
                url = props.columnInfo.renderer.options.urlTemplate;
            }

            // options에서 사용할 열 이름을 가져와 동적으로 값을 추출
            if (props.columnInfo.renderer.options.idFields) {
                // id가 여러개일 떼
                const idFields = props.columnInfo.renderer.options.idFields;
                const idFieldNames = idFields
                    .split(",")
                    .map((field) => field.trim());

                const idValues = idFieldNames.reduce((acc, field) => {
                    acc[field] = rowData[field];
                    return acc;
                }, {});

                idFieldNames.forEach((field, index) => {
                    // id가 하나일 때
                    const placeholder = `{id${index + 1}}`;
                    if (url.includes(placeholder)) {
                        url = url.replace(placeholder, idValues[field]);
                    }
                });
                //게시판 조회수 증가용
                if (props.columnInfo.renderer.options.viewCount == "true") {

                    let data = {
                        boardCd: rowData["boardCd"],
                        pjtType: rowData["pjtType"],
                        pjtNo: rowData["pjtNo"],
                        cntrctNo: rowData["cntrctNo"],
                        boardDiv: rowData["boardDiv"]
                    }
                    $.ajax({
                        url: "/api/board/updateView",
                        method: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        processData: false,
                        data: JSON.stringify(data),
                        error: function (error) {
                            console.error(error);
                        },
                    });
                }
            } else if (props.columnInfo.renderer.options.idField) {
                const idField = props.columnInfo.renderer.options.idField;
                const idValue = rowData[idField];
                url = url.replace("{id}", idValue);
            } else {
                // console.error("ID 필드가 설정되지 않았습니다.");
                return;
            }

            if (url) {
                if (this.gridId === 'phase_grid') {
                    window.open(url, '_blank', 'width=1200, height=550');
                } else {
                    window.location.href = url;
                }
            } else {
                // console.error("URL이 지정되지 않았습니다.");
            }


        });

        this.addRowHoverEvents();
    }

    // getElement: 그리드가 셀의 요소를 가져갈 때 호출.
    getElement() {
        return this.el;
    }

    // render: 그리드가 셀을 렌더링할 때 호출.
    render(props) {
        this.valueSpan.textContent = gaiaCommon.decodeSafeText(props.value);
    }

    addRowHoverEvents() {
        // 특정 CSS 선택자를 사용하여 행의 DOM 요소를 찾습니다.
        // console.log(props);
        const rowSelector = `#${this.gridId} td[data-row-key="${this.rowKey}"]`; // 예시 CSS 선택자
        // console.log(rowSelector);
        const rowElements = document.querySelectorAll(rowSelector); // NodeList 반환

        if (rowElements.length > 0) {
            // for 루프를 사용하여 NodeList를 순회합니다.
            for (let i = 0; i < rowElements.length; i++) {
                const rowElement = rowElements[i];
                rowElement.addEventListener("mouseenter", () => {
                    this.newWindowIcon.style.visibility = "visible";
                });
                rowElement.addEventListener("mouseleave", () => {
                    this.newWindowIcon.style.visibility = "hidden";
                });
            }
        } else {
            console.error("행 요소를 찾을 수 없습니다:", this.rowKey);
        }
    }
}

/**
 * <h4>그리드의 아이콘 렌더링을 위한 클래스</h4>
 * <h5>[options]</h5>
 * <dl>
 *     <dt>type : 그리고자 하는 아이콘의 타입({@link String})</dt>
 *     <dd>
 *         ex) newWindow, trash, note, copy, arrowUp, arrowDown, checkBox
 *     </dd>
 *     <dt>auth : 렌더링 권한({@link Boolean})</dt>
 *     <dd>ex) true, 'isDelAuth' === 'true'</dd>
 *     <dt>url : 통신이 필요한 경우 ajax URL({@link Boolean})</dt>
 *     <dd>ex) '/renderer/trash</dd>
 *     <dt>idField : 통신에 넘겨줄 데이터 컬럼명({@link String})</dt>
 *     <dd>ex) 'rowNum'</dd>
 *     <dt>idFields : 통신에 넘겨줄 데이터 컬럼명(다중)({@link String})</dt>
 *     <dd>ex) 'rowNum,intData'</dd>
 *     <dt>open : 페이지 이동 / 새 창 열기시 옵션들({@link Object})</dt>
 *     <dd>
 *         {
 *              '/url/{id}'    '/url/{id1}/{id2}'
 *              url:'이동할 url',
 *              mode:'팝업여부', 'p'
 *              width: 팝업 창 너비,
 *              height: 팝업 창 높이,
 *              top: 위치(top),
 *              left: 위치(left),
 *          }
 *     </dd>
 *     <dt>keyName : 백엔드에서 받을 파라미터 명({@link String})</dt>
 *     <dd>ex) 'rowNumList'</dd>
 *     <dt>align : 아이콘의 정렬 방향({@link String})</dt>
 *     <dd>ex) align:"right"; 이후의 모든 아이콘들은 우측정렬</dd>
 *     <dt>absolute : 아이콘의 정렬을 absolute로 할지여부({@link Boolean})</dt>
 *     <dd>ex) absolute:true 로 하면 아이콘의 우측정렬이 absolute로 되므로 value의 정렬이 정 가운데로 됨</dd>
 *     <dt>isHover : 아이콘이 호버상태에 나오게 할 것인지 여부({@link Boolean})</dt>
 *     <dd>ex) isHover:true </dd>
 *     <dt>isBlink : 아이콘이 깜박이게 할 것인지 여부({@link Boolean})</dt>
 *     <dd>ex) isBlink:true </dd>
 *     <dt>textColor : 첫 번째 객체에 설정해주면 텍스트의 색상 조정({@link String})</dt>
 *     <dd>ex) textColor:"red" </dd>
 *     <dt>color : 아이콘의 색상 조정({@link String})</dt>
 *     <dd>ex) color:"red" </dd>
 *     <dt>msgList : 컨펌창이 필요한 경우 메세지 리스트({@link Object})</dt>
 *     <dd>
 *         {
 *             confirmTit:'컨펌창의 타이틀',
 *             confirmSubTit:'컨펌창 바디의 타이틀',
 *             confirmMsg:'컨펌창 바디의 메세지',
 *             completeMsg:'완료 후 알럿 메세지'
 *         }
 *     </dd>
 *     <dt>condition : 사전 조건 검사 함수({@link Function})</dt>
 *     <dd>로직 수행 전 검사해야 하는 콜백함수 function(rowData)</dd>
 *     <dt>success : 통신 성공시 수행할 함수({@link Function})</dt>
 *     <dd>처리 성공시 수행할 콜백함수 function(rowData)</dd>
 *     <dt>fail : 통신 실패시 수행할 함수({@link Function})</dt>
 *     <dd>처리 실패시 수행할 콜백함수 function(rowData)</dd>
 *     <dt>complete : 통신 완료시 수행할 함수({@link Function})</dt>
 *     <dd>통신 완료시 수행할 콜백함수 function(rowData)</dd>
 * </dl>
 */
class IconRenderer {
    constructor(props) {
        //DOM 생성
        const el = document.createElement("div");
        el.style.boxSizing = "border-box";
        el.style.display = "flex";
        el.style.width = "100%";
        el.style.alignItems = "center";
        el.style.padding = "0.75em"
        el.style.justifyContent = "space-between";
        el.style.position = "relative";
        el.className = "custom_renderer";

        this._iconRenderer= {
            newwindow:'<i class="ic ic-sent-to-back" style="cursor:pointer"></i>',
            trash:`<i class='ic ic-delete' style="cursor: pointer;"></i>`,
            note:`<i class='ic ic-note' style="cursor: pointer;"></i>`,
            copy:`<i class='fa fa-copy fa-regular ic-copy' style="cursor: pointer;"></i>`,
            arrowup:`<i class='ic ic-arrow2 up' style="cursor: pointer;"></i>`,
            arrowdown:`<i class='ic ic-arrow2 down' style="cursor: pointer;"></i>`,
            checkbox:`<input class="checkGroup" type="checkbox" name="item-check"></input>`,
            eyes:`<i style="cursor: pointer;" class="ic ic-eyes"></i>`,
            pdf:`<i style="cursor: pointer;" class="fa-regular fa-file-pdf"></i>`,
            printer:`<i class='ic ic-printer' style="cursor: pointer;"></i>`,
            edit:`<i class='ic ic-edit' style="cursor: pointer;"></i>`,
            plus:`<i class='ic ic-plus' style="cursor: pointer;"></i>`,
            setting:`<i class='ic ic-setting' style="cursor: pointer;"></i>`,
            folderclose:`<i class='ic ic-folder-close' style="cursor: pointer;"></i>`,
            folderopen:`<i class='ic ic-folder-open' style="cursor: pointer;"></i>`,
            calendar:`<i class='ic ic-calendar' style="cursor: pointer;"></i>`,
            starempty:`<i class='ic ic-star' style="cursor: pointer;"></i>`,
            starfill:`<i class='ic ic-star-fill' style="cursor: pointer;"></i>`,
        }


        this.el = el;
        this.render(props);
        this.addEventListeners(props);
        this.addHoverListeners(props);
    }
    doAjaxRequest({url, keyName, idValue, success, fail, complete, clickedRowData, completeMsg,grid}) {
        const data = {};
        data[keyName] = [idValue];

        $.ajax({
            url: url,
            method: "POST",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(data),
            success: function (response) {
                if(completeMsg){
                    gaiaCommon.customAlert(completeMsg, function () {
                        if (typeof success === "function") {
                            success(clickedRowData);
                        } else {
                            grid.reloadData();
                        }
                    }, { timeout: 1500 });
                }
                else{
                    if (typeof success === "function") {
                        success(clickedRowData);
                    } else {
                        grid.reloadData();
                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Error do Something:", textStatus, errorThrown);
                console.log("XHR :", jqXHR);
                if (typeof fail === "function") {
                    fail(clickedRowData);
                }
            },
            complete: function (xhr, textStatus) {
                if (typeof complete === "function") {
                    complete(clickedRowData);
                }
            }
        });
    }

    getElement() {
        return this.el;
    }

    render(props) {
        const parser = new DOMParser();
        this.el.replaceChildren();

        let displayText = props.formattedValue ?? props.value ?? '';
        let renderHtml = "";

        const rowData = props.grid.getRow(props.rowKey);

        const centerGroup = document.createElement("div");
        centerGroup.className = 'center_group';
        centerGroup.style = `display:flex; justify-content:${props.columnInfo.align || "center"}; flex:1 1 0; max-width:100%; min-width:0;`

        const valueSpan = document.createElement("span");
        valueSpan.title = displayText;
        valueSpan.classList.add((props?.className || props?.name || props?.columnInfo?.name)+"_"+props.rowKey);
        valueSpan.classList.add('value-span');
        valueSpan.style = `overflow-x: hidden; overflow-y:visible; text-overflow: ellipsis; white-space: nowrap; cursor: pointer; line-height:1.1;`
        valueSpan.textContent = gaiaCommon.decodeSafeText(displayText);



        const optionsList = props.columnInfo.renderer.options;
        let i=0;
        let chk = "";
        if (props.columnInfo.name === "_checked" && Boolean(props.value)) {
            chk = "checked";
        }
        const rightGroup = document.createElement("div");
        rightGroup.className = "right_group";
        rightGroup.style = `display:flex; align-items:center; justify-content: right; flex-shrink:0;`;

        let rightFlag = false;
        for(const options of optionsList){
            if(i === 0){
                const isNoValue = props.columnInfo.name === "_checked" || !displayText || options.isNoValue;
                if(!isNoValue){
                    if(options.textColor){
                        valueSpan.style.color = options.textColor;
                    }
                    centerGroup.appendChild(valueSpan);
                }
            }
            if(typeof options.align == "string" && options.align.toLowerCase() === "right"){
                if(options.absolute){
                    rightGroup.style.position = "absolute";
                    rightGroup.style.right = "0.75em";
                }
                rightFlag = true;
            }
            let iconType = "";
            if(typeof options.type === 'function'){
                iconType = options.type(rowData);
            }
            else{
                iconType = options.type;
            }
            iconType = iconType.toLowerCase();

            const iconHtml = this._iconRenderer[iconType].replace(/>/, ` data-idx="${i}" data-rowkey="${props.rowKey}" data-column="${props.columnInfo.name}" ${iconType === "checkbox" ? chk : ""}>`);
            let iconTag = null;
            if(options.auth !== undefined){
                const auth = options.auth;
                let authCheck = false;
                if(typeof auth === "function"){
                    authCheck = auth(rowData)
                }
                else{
                    authCheck = auth;
                }
                if(iconType === 'checkBox' && !authCheck){
                    props.grid.disableRowCheck(props.rowKey);
                }

                if(authCheck){
                    iconTag = parser.parseFromString(iconHtml,'text/html').body.firstElementChild;
                }
            }
            else{
                iconTag = parser.parseFromString(iconHtml,'text/html').body.firstElementChild;
            }
            if(iconTag != null){
                if(options.color){
                    iconTag.style.color = options.color;
                }
                if(options.isBlink){
                    iconTag.classList.add("blink");
                }
                if(rightFlag){
                    rightGroup.appendChild(iconTag);
                }
                else{
                    centerGroup.appendChild(iconTag);
                }
            }
            i++;
        }
        this.el.appendChild(centerGroup);

        if(rightFlag){
            this.el.appendChild(rightGroup);
        }

        this.addHoverListeners(props);
    }
    addEventListeners(props){
        const options = props.columnInfo.renderer.options;

        this.el.addEventListener("click", (e) => {
            e.stopPropagation();
            const grid = props.grid;

            if (grid) {
                const event = new CustomEvent('click', {
                    bubbles: true,
                    cancelable: true,
                    detail: {
                        rowKey: props.rowKey,
                        columnName: props.columnInfo.name
                    }
                });
                grid.gridEl.dispatchEvent(event);
            }

            // 어떤 아이콘이 클릭되었는지 idx 체크
            const idx = e.target.dataset.idx;

            if(!idx){ return; }

            //해당 idx의 options 확인
            const option = options[idx];

            const clickedRowData = props.grid.getRow(props.rowKey);
            const type = option.type.toLowerCase();
            const url = option?.url;
            const open = option?.open;
            const idField = option?.idField;
            const idFields = option?.idFields;
            const keyName = option?.keyName;
            const condition = option?.condition;

            // 로직 수행 성공 후 수행할 함수
            const success = option.success;
            //조건 체크 함수를 넘겨주지 않은 경우는 기본값으로 true(조건체크를 안 함)
            let precondition = true;
            //넘겨준 조건 체크 함수가 있는 경우에는 통과해야 precondition = true
            if (typeof condition === 'function') {
                precondition = condition(clickedRowData); // 인자로 전달
            }

            if(precondition){
                if(type === "checkbox"){
                    if (clickedRowData._attributes.checked) {
                        grid.uncheck(props.rowKey);  // 체크 해제
                    } else {
                        grid.check(props.rowKey);    // 체크 설정
                    }
                }
                //로직 수행 시도
                if(url || open){
                    //통신이 필요한 렌더러
                    // ajax 통신 실패 시 수행할 함수
                    const fail = option.fail;
                    // ajax 통신 완료 시 수행할 함수
                    const complete = option.complete;
                    let openUrl = '';
                    let idValue;
                    if (idFields) {
                        // idFields가 존재하는 경우, 다중 필드 처리
                        const idFieldNames = idFields
                            .split(",")
                            .map((field) => field.trim()); // ID 필드 이름 배열

                        // idFieldNames을 이용하여 deleteRowData에서 해당 값들을 객체로 변환
                        idValue = idFieldNames.reduce((acc, field) => {
                            acc[field] = clickedRowData[field];
                            return acc;
                        }, {});

                        if(open) {
                            if(!open.url){
                                console.log("Invalide Open Url");
                                return;
                            }
                            openUrl = open.url;
                            idFieldNames.forEach((field, index) => {
                                // id가 하나일 때
                                const placeholder = `{id${index + 1}}`;
                                if (openUrl.includes(placeholder)) {
                                    openUrl = openUrl.replace(placeholder, clickedRowData[field]);
                                }
                            });
                        }
                    } else if (idField) {
                        // idField가 존재하는 경우, 단일 필드 처리
                        idValue = clickedRowData[idField];
                        if(open) {
                            if(!open.url){
                                console.log("Invalid Open Url");
                                return;
                            }
                            openUrl = open.url;
                            openUrl = openUrl.replace("{id}", idValue);
                        }
                    } else if(open && !open.url.includes("{id")) {
                        openUrl = open.url;
                    } else{
                        console.log("No idField / idFields");
                        return; // 필드가 없으면 함수 종료
                    }

                    if(open){
                        const mode = open.mode;
                        //팝업
                        if((mode && mode.toLowerCase() === 'p') || open.target){
                            const target = open.target || 'popup';
                            if(open.align === 'center' && open.height && open.width){
                                open.top = Math.ceil((window.screen.height - open.height) / 2);
                                open.left = Math.ceil((window.screen.width - open.width) / 2);
                            }
                            window.open(
                                openUrl,
                                target,
                                `width=${open.width},height=${open.height},left=${open.left},top=${open.top}`
                            );
                        }
                        //모달
                        else if(mode && mode.toLowerCase() === 'm'){
                            const key = open?.key;
                            $("#popup").load(openUrl, function (response, status, xhr) {
                                if (status === "success") {
                                    // 페이지가 성공적으로 로드된 후에 데이터 저장
                                    $("#popup").data(`${key || row}-info`, clickedRowData);
                                    $("#popup").css({ "display": "flex" });
                                } else {
                                    console.error("page loaded error:", xhr.statusText);
                                }
                            });
                        }
                        else{
                            location.href = openUrl;
                        }
                        return;

                    }

                    const msgList = option?.msgList;
                    if(msgList && (msgList.confirmTit || msgList.confirmSubTit || msgList.confirmMsg)){
                        //컨펌창이 있는 통신
                        const confirmTit = msgList.confirmTit;
                        const confirmSubTit = msgList.confirmSubTit || msgList.confirmTit;
                        const confirmMsg = msgList.confirmMsg || '';
                        const completeMsg = msgList.completeMsg || '완료되었습니다.';

                        gaiaCommon.customConfirm(confirmTit, confirmSubTit, confirmMsg, () => {
                            this.doAjaxRequest({url,keyName,idValue,success,fail,complete,clickedRowData,completeMsg, grid})
                        });
                    }
                    else{
                        //컨펌창이 없는 통신
                        this.doAjaxRequest({url,keyName,idValue,success,fail,complete,clickedRowData,completeMsg:msgList ? msgList?.completeMsg || '완료되었습니다.' : '완료되었습니다.', grid})
                    }
                }
                else{
                    //통신이 필요없는 렌더러
                    if (typeof success === "function") {
                        success(clickedRowData);
                    }
                }

            }
            else{
                //수행 X
                console.log("precondition is false")
            }
        });
    }
    addHoverListeners(props){
        const options = props.columnInfo.renderer.options;

        for(let i = 0;i<options.length;i++){
            const option = options[i]
            if(option.isHover){
                const tdElements = props.grid.el.querySelectorAll(`td[data-row-key="${props.rowKey}"]`);
                const icon = this.el.querySelector(`[data-idx="${i}"][data-rowkey="${props.rowKey}"][data-column="${props.columnInfo.name}"]`)
                icon.style.visibility = "hidden";
                for (let j = 0; j < tdElements.length; j++) {
                    const tdElement = tdElements[j];
                    tdElement.addEventListener("mouseenter", () => {
                        icon.style.visibility = "visible";
                    });
                    tdElement.addEventListener("mouseleave", () => {
                        icon.style.visibility = "hidden";
                    });
                }
            }
        }
    }
}
class CustomColumnCheckbox {
    constructor(props) {
        const el = document.createElement("div");
        el.style.display = "flex";
        el.style.alignItems = "center";
        el.style.justifyContent = "center";
        el.style.gap = "10px";
        el.className = "custom_render";
        this.el = el;
        this.render(props);
        this.addEventListeners(props);
    }

    getElement(props) {
        return this.el;
    }

    render(props) {
        let chk = "";
        if (Boolean(props.value)) {
            chk = "checked";
        }

        // console.log(props.columnInfo.renderer.options);
        // TODO: 추후 삭제 권한 여부로 체크하여 렌더링되도록 변경 예정.
        if (props.columnInfo.renderer.options.isdelAuth != undefined) {
            const isAddAuth = props.columnInfo.renderer.options.isAddAuth;
            const isdelAuth = props.columnInfo.renderer.options.isdelAuth;
            const isEnd = props.columnInfo.renderer.options.isEnd;
            const isdashBoard = props.columnInfo.renderer.options.isdashBoard;
            // console.log("삭제 권한 여부(renderer): ", isdelAuth);
            // isdel 속성에 따라 휴지통 아이콘을 조건부로 렌더링
            const trashIconHtml = isdelAuth && !isEnd
                ? `<i class='ic ic-delete' style="cursor: pointer;"></i>`
                : "";

            const boardIconHtml = isdashBoard
                ? `<i class='ic ic-note' style="cursor: pointer;"></i>`
                : "";
            const copyIconHtml = isAddAuth
                ? `<i class='fa fa-copy fa-regular ic-copy' style="cursor: pointer;"></i>`
                : "";


            this.el.textContent = gaiaCommon.decodeSafeText(`
                        <input class="checkGroup" type="checkbox" ${chk} name="delete-item-check" data-row-key="${props.rowKey}">
                        ${trashIconHtml}
                        ${boardIconHtml}
                        ${copyIconHtml}
                `);
        } else {
            this.el.textContent = gaiaCommon.decodeSafeText(`
            <input class="checkGroup" type="checkbox" ${chk} name="delete-item-check" data-row-key=${props.rowKey}>
            <i class='ic ic-delete' style="cursor: pointer;"></i>
            `);
        }


    }

    addEventListeners(props) {
        // // 체크박스 이벤트 리스너 추가
        // const checkbox = this.el.querySelector(".checkGroup");

        // if (checkbox) {
        //     checkbox.addEventListener("change", () => {
        //         const isChecked = checkbox.checked;
        //         const rowKey = checkbox.getAttribute("data-row-key");

        //         // checked 상태에 따라 props.grid 데이터를 업데이트하거나 다른 처리
        //         if (isChecked) {
        //             // console.log(`Row ${rowKey} checked.`);
        //             // 여기에 체크 상태일 때 처리할 로직 추가
        //         } else {
        //             // console.log(`Row ${rowKey} unchecked.`);
        //             // 여기에 체크 해제 상태일 때 처리할 로직 추가
        //         }

        //         // 체크 상태에 따라 체크박스의 `checked` 속성을 업데이트
        //         checkbox.checked = isChecked;
        //     });
        // }

        // 아이콘에 클릭 이벤트 리스너 추가
        this.el.addEventListener("click", async (e) => {
            //1. 휴지통 아이콘 클릭 이벤트
            if (e.target.classList.contains('ic-delete')) {
                e.stopPropagation();
                // console.log("삭제 실행!!!");
                let deleteRowData = props.grid.getRow(props.rowKey); //rowKey로 해당 행 정보 가져옴.
                const keyName = props.columnInfo.renderer.options.keyName;
                const url = props.columnInfo.renderer.options.url;
                const gridId = props.columnInfo.renderer.options.gridId || props.grid.el.id; // gridId 확인(한 페이지에서 다중 그리도 사용 시)
                const changeNo = props.columnInfo.renderer.options.changeNo;
                const lastChgYn = deleteRowData.lastChgYn;
                const rprsYn = deleteRowData.rprsYn;
                const lastRevisionYn = deleteRowData.lastRevisionYn;
                const apprvlStats = deleteRowData.apprvlStats;
                const docId = deleteRowData.docId;
                const docType = deleteRowData.doc_type;         // 통합문서관리
                const ispApDocId = deleteRowData.isp_ap_doc_id; // 품질검측관리
                const apDocId = deleteRowData.apDocId;
                const dtRplyYn = deleteRowData.rplyYn;          // 결함추적관리
                const dtRplyCd = deleteRowData.rplyCd;          // 결함추적관리
                const dmRplyCd = deleteRowData.rplyStatus;      // 설계검토관리
                const cntrctNo = deleteRowData.cntrctNo;
                const hasCfReport = deleteRowData.hasCfReport;      // 감리일지
                const rgstrId = deleteRowData.inspectionRgstrId;    // 감리일지

                console.log("gridId:", gridId)

                // 삭제 메시지 정보
                const msgList = props.columnInfo.renderer.options.msgList;
                const alertMsg = msgList.deleteCompleteAlert;
                let confirmTit = msgList.deleteConfirmTit;
                let confirmSubTit;
                if (msgList.deleteConfirmSubTit) {
                    confirmSubTit = msgList.deleteConfirmSubTit;
                } else {
                    confirmSubTit = msgList.deleteConfirmTit;
                }
                let confirmMsg = msgList.deleteConfirm;

                // 삭제 후 수행할 함수
                const afterFunction = props.columnInfo.renderer.options.afterFunction;

                let idValue;
                if (props.columnInfo.renderer.options.idFields) {
                    // idFields가 존재하는 경우, 다중 필드 처리
                    const idFields = props.columnInfo.renderer.options.idFields; // ID 필드 배열
                    const idFieldNames = idFields
                        .split(",")
                        .map((field) => field.trim()); // ID 필드 이름 배열

                    // idFieldNames을 이용하여 deleteRowData에서 해당 값들을 객체로 변환
                    idValue = idFieldNames.reduce((acc, field) => {
                        acc[field] = deleteRowData[field];
                        return acc;
                    }, {});
                } else if (props.columnInfo.renderer.options.idField) {
                    // idField가 존재하는 경우, 단일 필드 처리
                    const idField = props.columnInfo.renderer.options.idField;
                    idValue = deleteRowData[idField];
                } else {
                    // console.error("ID 필드가 설정되지 않았습니다.");
                    return; // 필드가 없으면 함수 종료
                }

                // idValues는 이제 단일 필드이거나 여러 필드로 구성된 객체
                let isConfirmed = false;

                gaiaCommon.customConfirm(confirmTit, confirmSubTit, confirmMsg, function () {
                    const data = {};
                    data[keyName] = [idValue];
                    // console.log("삭제 데이터 정보: ", data);

                    $.ajax({
                        url: url,
                        method: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        data: JSON.stringify(data),
                        success: function (response) {
                            gaiaCommon.customAlert(alertMsg, function () {
                                if (typeof afterFunction === "function") {
                                    afterFunction();
                                } else {
                                    props.grid.reloadData();
                                }
                            });
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            console.error("Error deleting data:", textStatus, errorThrown);
                        }
                    });

                });
            };

            //2. 보드 아이콘 클릭 이벤트
            if (e.target.classList.contains('ic-note')) {
                e.stopPropagation();
                let rowData = props.grid.getRow(props.rowKey); //rowKey로 해당 행 정보 가져옴.

                if (!props.columnInfo.renderer.options.idField) {
                    console.error("ID 필드가 설정되지 않았습니다.");
                    return; // 필드가 없으면 함수 종료
                }
				console.log('pjtNo : >>> ', rowData['pjtNo'], 'pjt_nm : >>> ', rowData['pjtNm'], 'cntrctNo : >>> ', rowData['cntrctNo'], 'cntrct_nm : >>> ', rowData['cntrctNm'], 'pjt_div : >>> ', rowData['pjtDiv']);

				let pageCommonData = {
					"pjtNo": rowData['pjtNo'],
					"pjt_nm": gaiaCommon.decodeSafeText(rowData['pjtNm']),
					"cntrctNo": rowData['cntrctNo'],
					"cntrct_nm": gaiaCommon.decodeSafeText(rowData['cntrctNm']),
					"pjt_div": rowData['pjtDiv']
				};

                commonJs.setSessionStorage("pageCommonData", pageCommonData);

                document.getElementById('selectProject').textContent = gaiaCommon.decodeSafeText(rowData['cntrctNm']);

                window.location.href = `/dashboard?pjtNo=${rowData['pjtNo']}&cntrctNo=${rowData['cntrctNo']}`;
            }

            //3. 복사 아이콘 클릭 이벤트
            if (e.target.classList.contains('ic-copy')) {
                e.stopPropagation();
                const rowData = props.grid.getRow(props.rowKey);
                console.log('복사 아이콘 클릭 - RowData:', rowData);
                const onCopy = props.columnInfo.renderer.options.onCopy;

                if (typeof onCopy === 'function') {
                    onCopy(rowData);
                }
            }
        });

        // if (boardIcon) {
        //     // 아이콘에 클릭 이벤트 리스너 추가
        //     boardIcon.addEventListener("click", async () => {
        //         let rowData = props.grid.getRow(props.rowKey); //rowKey로 해당 행 정보 가져옴.

        //         let idValue;
        //         let pjtNm;
        //         if (props.columnInfo.renderer.options.idField) {
        //             const idField = props.columnInfo.renderer.options.idField;
        //             idValue = rowData[idField];
        //             pjtNm = rowData['pjtNm'];
        //         } else {
        //             console.error("ID 필드가 설정되지 않았습니다.");
        //             return; // 필드가 없으면 함수 종료
        //         }

        //         let pageCommonData;
        //         localStorage.removeItem('pageCommonData');

        //         pageCommonData = {
        //             "pjtNo": idValue,
        //             "pjt_nm": pjtNm,
        //             "cntrctNo": idValue,
        //             "cntrct_nm": pjtNm
        //         };
        //         localStorage.setItem("pageCommonData", JSON.stringify(pageCommonData));
        //         document.getElementById('selectProject').innerHTML = pjtNm;

        //         window.location.href = '/dashboard/home';
        //     });
        // }
    }
}

// CommonJS 또는 ES6 모듈 시스템을 통해 TrashCheckboxRenderer 클래스를 내보냄

    // 브라우저 환경에서 전역 객체(window)에 TrashCheckboxRenderer, NewWindowViewRenderer, CustomSelectRenderer 추가
    window.IconRenderer = IconRenderer;

