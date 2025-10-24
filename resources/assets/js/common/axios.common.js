axios.defaults.baseURL = `${location.protocol}//${location.host}`;
axios.defaults.timeout = 5000;
axios.defaults.headers.post['Content-Type'] = 'application/json; charset=utf-8';

axios.interceptors.request.use(
    function (config) {
        // console.log('axios config', config);
        return config;
    },
    function (error) {
        // console.log('axios request error', error);\
        return Promise.reject(error);
    }
);

axios.interceptors.response.use(
    function (response) {
        // console.log('axios response', response);
        return response;
    },

    function (error) {
        // console.log('axios response error', error.response);
        return Promise.reject(error);
    }
);

const UNAUTHORIZED = 401

const onUnauthroized = () => {
    location.href = '/login';
}

const setAuthInHeader = token => {
    axios.defaults.headers.common['Authorization'] = token ? `Bearer ${token}` : null;
}

const request = (method, url, data, isSelf) => {
    setAuthInHeader( sessionStorage.getItem('accessToken') );

    if (!isSelf) {
        isSelf = true;
    }

    if (method === 'get' && data) {
        const params = [];
        for (var key in data) {
            params.push(key + '=' + data[key]);
        }

        if (url.indexOf('?') < 0) {
            url += '?' + params.join('&');
        }
    }

    return axios ({
        method,
        url,
        data
    })
        .then(result => result.data)
        .catch(result => {
            console.log('axios catch', result.response);
            const {status} = result.response;
            if (status === UNAUTHORIZED) {
                return onUnauthroized();
            }
            throw Error(result);
        });
}