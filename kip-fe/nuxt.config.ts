import vuetify, {transformAssetUrls} from 'vite-plugin-vuetify'
// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    devtools: {enabled: true},

    build: {
        transpile: ['vuetify'],
    },

    modules: [
        (_options, nuxt) => {
            nuxt.hooks.hook('vite:extendConfig', (config) => {
                // @ts-expect-error
                config.plugins.push(vuetify({autoImport: true}))
            })
        }, // Nuxt 전용 뷰티파이 불러오는 설정
        ['@pinia/nuxt', {
            autoImports: ["defineStore", "acceptHMRUpdate"]  // 피니아 생성시 임포트 생략
        }],
    ],
    imports: { // stores 폴더에 있는 것들 모두 임포트
        dirs: ["stores"],
    },
    vite: {
        vue: {
            template: {
                transformAssetUrls,
            },
        }
    }

})
