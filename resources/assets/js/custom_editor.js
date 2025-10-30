/**
 * 콤보 박스를 통해 셀 값을 수정하는 커스텀 에디터
 * <options 세팅>
 * optionsData : select의 선택 값들에 대한 정보
 * columnName : 행 데이터를 가져오기 위한 컬럼명
 */
class CustomSelectEditor {
    constructor(props) {
        this.el = document.createElement('select');
        this.props = props;
        this.init(props);
    }

    // 초기화 메서드: select 요소를 초기화
    async init(props) {
        const el = this.el;

        let rowData = props.grid.getRow(props.rowKey); //rowKey로 해당 행 정보를 가져옴.

        const optionsData = props.columnInfo.editor.options.optionsData;
        const columnName = props.columnInfo.editor.options.columnName;
        const rowDataValue = rowData[columnName]; // 해당 행의 컬럼 값

        let selectedValue = null;

        try {
            // 가져온 데이터로 옵션 요소를 생성하여 select 요소에 추가
            optionsData.forEach(option => {
                const optionElement = document.createElement('option');
                optionElement.value = option.value;
                optionElement.text = option.text;

                if (optionElement.value === rowDataValue) {
                    selectedValue = optionElement.value;
                }
  
                el.appendChild(optionElement);
            });

            el.value = selectedValue; // select 요소의 값 설정
        } catch (error) {
            console.error('Failed to fetch options data:', error);
        }
    }

    getElement() {
        return this.el;
    }

    getValue() {
        return this.el.value;
    }

    mounted() {
        this.el.focus();
    }

}

class CustomDateInput {
    constructor(props) {
        // input 요소 생성
        const el = document.createElement('input');
        el.type = 'date'; // HTML5 date input 사용
        el.style.width = '100%'; // 필요한 경우 너비 조정
        el.style.padding = '5px'; // 스타일 추가

        // 오늘 날짜를 구해 'YYYY-MM-DD' 형식으로 변환
        const today = new Date().toISOString().split('T')[0];

        el.value = props.value ? props.value : ''; // props.value가 있으면 값을 설정
        el.min = today; // 오늘 날짜부터 선택 가능

        this.el = el;
    }

    // 에디터의 DOM 요소를 반환
    getElement() {
        return this.el;
    }

    // 에디터의 값을 반환
    getValue() {
        return this.el.value;
    }

    // input 필드가 마운트될 때 호출
    mounted() {
        this.el.focus(); // input 필드에 포커스 설정
    }

    // 에디터가 파기될 때 호출 (필요시 cleanup)
    destroy() {
        // 특별한 정리 작업 필요 없음 (필요시 구현)
    }
}


// 브라우저 환경에서 전역 객체(window)에 추가
window.CustomSelectEditor = CustomSelectEditor;
window.CustomDateInput = CustomDateInput;
  
