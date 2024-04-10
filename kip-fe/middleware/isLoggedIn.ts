export default defineNuxtRouteMiddleware((to,from) =>{
    const userIsLoggedIn = false;
    if (!userIsLoggedIn) {
        // return abortNavigation("You're Not Allowed to visit/admin");
        // return navigateTo({name:'login'});
        return navigateTo({path:'/login'});
    }
})