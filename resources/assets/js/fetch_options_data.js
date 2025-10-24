/**
 * 콤보박스 option 값을 받아오는 js
 * @param {*} pathVariable : 코드 그룹 정보 (cmn_grp_cd)
 * @returns : {value : [코드] , text : [코드이름(한국)] }
 */
async function fetchOptionsData(pathVariable) {
    const url = '/api/system/common-code/code-combo/' + pathVariable;
    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const result = await response.json();
        
        if (result.details?.codeCombo) {
            return result.details.codeCombo;
        } else {
            throw new Error('No data found');
        }
    } catch (error) {
        throw error;
    }
}

async function fetchOptionsDataMap(param) {
    // param을 쿼리 스트링으로 변환
    const queryString = $.param({ cmnGrpCdList: param });
    const url = `/api/system/common-code/code-combo-list?${queryString}`;
    
    try {
        const result = await new Promise((resolve, reject) => {
            $.ajax({
                url: url,
                method: 'GET',
                dataType: 'json',
                success: function(response) {
                    if (response.details && response.details.codeComboMap) {
                        resolve(response.details.codeComboMap);
                    } else {
                        reject(new Error('No data found'));
                    }
                },
                error: function(error) {
                    reject(error);
                }
            });
        });

        
        return result;
    } catch (error) {
        throw error;
    }
}

/**
 * 그리드의 리스트 데이터를 가져오는 js
 * @param {*} url : 요청 url
 * 
 */
async function fetchGridData(u) {
    const url = u;
    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const result = await response.json();
        console.log(result);
        
        if (result.result) {
            return result.data.contents;
        } else {
            throw new Error('No data found');
        }
    } catch (error) {
        throw error;
    }
}

// 전역 객체에 함수 추가
window.fetchOptionsData = fetchOptionsData;
window.fetchGridData = fetchGridData;
