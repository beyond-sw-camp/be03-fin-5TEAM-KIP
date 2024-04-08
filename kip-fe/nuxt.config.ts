import vuetify, { transformAssetUrls } from 'vite-plugin-vuetify'
// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    devtools: {enabled: true}, // https://devtools.nuxt.com/

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
        '@pinia/nuxt',
    ],
    vite: {
        vue: {
            template: {
                transformAssetUrls,
            },
        }
    }

})
