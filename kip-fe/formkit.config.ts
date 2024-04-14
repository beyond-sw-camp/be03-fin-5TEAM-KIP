import type {DefaultConfigOptions} from "@formkit/vue";
import {createAutoAnimatePlugin} from "@formkit/addons"

const BASE_URL = import.meta.env.VITE_BASE_URL;

const config: DefaultConfigOptions = {
    theme: "genesis",
    plugins: [createAutoAnimatePlugin()], // 입력 폼 애니메이션 플러그인.
    messages: {
        en: {
            validation: {
                id_check: '존재하지 않는 사번 입니다.',
                matches: '사번은 k-(10자리 숫자) 형식 입니다.'
            }
        }
    },
}
export default config;
