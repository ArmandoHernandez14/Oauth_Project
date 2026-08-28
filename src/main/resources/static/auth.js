function getAccessToken() {
    return sessionStorage.getItem("accessToken");
}

function getRefreshToken() {
    return sessionStorage.getItem("refreshToken");
}

function isLoggedIn() {
    return !!getAccessToken();
}

function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = "/login.html";
    }
}

function logout() {
    sessionStorage.removeItem("accessToken");
    sessionStorage.removeItem("refreshToken");
    sessionStorage.removeItem("username");

    window.location.href = "/login.html";
}

async function authFetch(url, options = {}) {
    const token = getAccessToken();

    if (!token) {
        window.location.href = "/login.html";
        return;
    }

    const headers = options.headers || {};

    headers["Authorization"] = "Bearer " + token;

    if (options.body && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    options.headers = headers;

    return fetch(url, options);
}