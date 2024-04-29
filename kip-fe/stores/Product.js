
export const useProduct =  defineStore("product", {
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