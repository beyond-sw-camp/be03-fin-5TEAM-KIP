import vuetify, {transformAssetUrls} from 'vite-plugin-vuetify'
// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    devtools: {enabled: true},
    ssr:false, // 서버사이드 렌더링 사용하지 않음, CSR 로 작동하게 하는 코드.
    css: ['~/assets/styles/global.css'],
    imports: { // stores 폴더에 있는 것들 모두 임포트
        dirs: ["stores"],
    },
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
        '@formkit/nuxt',
        '@vueuse/nuxt',
    ],
    vite: {
        vue: {
            template: {
                transformAssetUrls,
            },
        },
        css: { // CSS파일을 빌드할때 추가하는 설정 (로그인에서 CSS가 빌드가 안됨) 효과없음.
            preprocessorOptions: {
                includePaths: ['node_modules']
            }
        }
    },
    nitro: {
        routeRules: {
            // toast-ui editor 가 SSR 을 지원하지 않아 reload시 에러가 나는것을 방지
            "editor/toast": {
                ssr: false,
            },
        },
    },

})
