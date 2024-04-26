// import this after install `@mdi/font` package
import '@mdi/font/css/materialdesignicons.css'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import {VSpeedDial} from "vuetify/lib/labs/VSpeedDial";


export default defineNuxtPlugin((app) => {
    const vuetify = createVuetify({
        components: {
            VSpeedDial,
        },
        // ... your configuration
    })
    app.vueApp.use(vuetify)
})