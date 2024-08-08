import axios from "axios";
import { ElMessage } from 'element-plus';

const authItemName = "access_token";

// 預設的失敗處理函數
const defaultFailure = (message, code, url) => {
    console.warn(`請求地址: ${url}, 狀態碼: ${code}, 錯誤訊息: ${message}`);
    ElMessage.warning({
        message: `錯誤: ${message}`, // 提示用戶錯誤訊息
        type: 'warning',
    });
};

// 預設的錯誤處理函數
const defaultError = (err) => {
    console.error('發生錯誤, 請聯絡管理員', err);
    ElMessage.error('發生錯誤, 請聯絡管理員');
};

function takeAccessToken(){
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName);
    if (!str) { return null; }
    const authObj = JSON.parse(str);
    if (authObj.expire <= new Date()) {
        deleteAccessToken();
        ElMessage.warning('登入狀態已過期，請重新登入');
    }
    return authObj.token;
}

function storeAccessToken(remember, token, expire){
    const authObj = { token: token, expire: expire };
    const str = JSON.stringify(authObj);
    if (remember) {
        localStorage.setItem(authItemName, str);
    } else {
        sessionStorage.setItem(authItemName, str);
    }
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName);
    sessionStorage.removeItem(authItemName);
}

function accessHeader() {
    const token = takeAccessToken();
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

function internalPost(url, data, headers, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, { headers: headers })
        .then(({ data }) => {
            if (data.code === 200) {
                success(data.data);
            } else {
                const errorMessage = data.msg || '未知錯誤'; // 使用 `msg` 而不是 `message`
                failure(errorMessage, data.code, url);
            }
        })
        .catch(err => {
            if (err.response && err.response.data) {
                // 使用 `msg` 而不是 `message`
                const errorMessage = err.response.data.msg || '發生未知錯誤';
                const errorCode = err.response.status;
                const requestUrl = err.config.url;
                failure(errorMessage, errorCode, requestUrl);
            } else {
                error('發生未預期的錯誤，請聯絡管理員');
            }
        });
}

function internalGet(url, headers, success, failure = defaultFailure, error = defaultError) {
    axios.get(url, { headers: headers })
        .then(({ data }) => {
            if (data.code === 200) {
                success(data.data);
            } else {
                const errorMessage = data.msg || '未知錯誤'; // 使用 `msg`
                failure(errorMessage, data.code, url);
            }
        })
        .catch(err => {
            if (err.response && err.response.data) {
                const errorMessage = err.response.data.msg || '發生未知錯誤';
                const errorCode = err.response.status;
                const requestUrl = err.config.url;
                failure(errorMessage, errorCode, requestUrl);
            } else {
                error('發生未預期的錯誤，請聯絡管理員');
            }
        });
}

function get(url, success, failure = defaultFailure) {
    internalGet(url, accessHeader(), success, failure);
}

function post(url, data, success, failure = defaultFailure){
    internalPost(url, data, accessHeader(), success, failure)
}

function login(username, password, remember, success, failure = defaultFailure){
    internalPost('/api/auth/login', {
        username: username,
        password: password
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {
        storeAccessToken(remember, data.token, data.expire);
        ElMessage.success(`登入成功，歡迎 ${data.username} 來到我們的網頁`);
        success(data);
    }, failure);
}

function logout(success, failure = defaultFailure) {
    get('/api/auth/logout', () => {
        deleteAccessToken();
        ElMessage.success('您已成功登出');
        success();
    }, failure);
}

function unauthorized() {
    return !takeAccessToken()
}

export { login, logout, get, post, unauthorized};
