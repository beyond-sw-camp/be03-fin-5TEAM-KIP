import {defineStore} from "pinia";

export default defineStore("ProductStore", {
    state: () => {
        return {
            products: []
        }
    },

    getters: {
        getProducts: state => state.products,
    },

    actions: {
        async fill() {
            this.products = (await import("@/data/products.json")).default;
        }
    }
})