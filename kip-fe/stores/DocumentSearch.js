const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useDocumentSearch = defineStore("documentSearch", {
    state() {
        return {
            rowData: {},
            document: [],
            totalPages: 0,
            canView: String,
            groupId: {}
        };
    },
    getters: {
        getSearchDocument(state) {
            // 여기서는 직접적으로 필요한 객체 구조를 반환합니다.
            return state.document;
        },
        getCurrentPages(state) {
            return state.rowData.number;
        },
        getTotalPages(state) {
            return state.rowData.totalPages;
        },
        getAvailable(state) {
            // 여기서는 직접적으로 필요한 객체 구조를 반환합니다.
            return state.canView
        },
        getTotalPagesCount(state) {
            return state.rowData.totalElements;
        },
        getGroupId(state) {
            return {
                groupId: state.groupId
            };
        },
    },

    actions: {
        async setSearchDocument(keyword, pageNumber) {
            try {
                const response = await fetch(`${BASE_URL}/doc/search?keyword=${keyword}&pageNumber=${pageNumber}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                });
                const data = await response.json()
                this.rowData = data
                this.document = data.content;
                this.totalPages = data.totalPages - 1;
            } catch (error) {
                console.error('Error fetching search:', error.message);
            }
        },
        async viewDocument(documentUUID) {
            try {
                const response = await fetch(`${BASE_URL}/doc/search/${documentUUID}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                });
                const data = await response.json()
                this.groupId = data.groupId;
                this.canView = data.result;
            } catch (error) {
                console.error('Error fetching search:', error.message);
            }
        },
    }
});
