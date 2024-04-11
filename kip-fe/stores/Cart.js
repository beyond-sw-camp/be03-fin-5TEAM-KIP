import {groupBy} from "lodash";

export const useCart = defineStore("cart", {
    state: () => {
        return {
            items: [],
        };
    },

    getters: {
        count: state => state.items.length,
        isEmpty: state => state.count === 0,
        grouped: state => {
            const grouped = groupBy(state.items, item => item.name);
            const sorted = Object.keys(grouped).sort();
            let inOrder = {};
            sorted.forEach((key) => inOrder[key] = grouped[key]);
            return inOrder; // 이름순 정렬을 유지.
        },

        groupCount: state => (name) => state.grouped[name].length,
        totalPrice: state =>
            state.items.reduce((sum, item) => sum + item.price, 0),
    },

    actions: {
        fetchItemsFromLocalStorage() {
            let savedItems = [];
            if (typeof (window) !== 'undefined') {
                const storedItems = window.localStorage.getItem('CartStore:items');
                savedItems = storedItems ? JSON.parse(storedItems) : [];
            }
            this.items = savedItems;
        },
        addItems(count, item) {
            count = parseInt(count);
            for (let i = 0; i < count; i++)
                this.items.push({...item});
            if (typeof (window) !== 'undefined')
                window.localStorage.setItem('CartStore:items', JSON.stringify(this.items));
        },
        deleteItems(name) {
            this.items = this.items.filter(item => item.name !== name);
            if (typeof (window) !== 'undefined')
                window.localStorage.setItem('CartStore:items', JSON.stringify(this.items));
        }
    }
})
