// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    devtools: {enabled: true}, // https://devtools.nuxt.com/
    modules: ['@nuxt/ui'], // Nuxt 불러오는 설정
    ui: {
        global: true, // 컴포넌트 전역적으로 사용
    },
})
