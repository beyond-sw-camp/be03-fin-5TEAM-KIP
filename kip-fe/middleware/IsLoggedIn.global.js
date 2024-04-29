export default defineNuxtRouteMiddleware((to, from) => {
    // 로그인 된상태가 아니고, 경로가 "/" 가 아니면 "/" 으로 보냄
    if (to.path !== '/' && !useUser().getIsLoggedIn) {
        useUser().isLoggedIn = true;
        return navigateTo({path: '/'});
    }
})