export function clearCache() {
    sessionStorage.removeItem('Authorization');
    sessionStorage.removeItem('userInfo');
    sessionStorage.removeItem('menus');
}