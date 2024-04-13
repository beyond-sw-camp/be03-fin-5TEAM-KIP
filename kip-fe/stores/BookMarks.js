const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useBookMarks = defineStore("bookmarks", {
    state() {
        return {
            accessToken: user.getAccessToken,
            myBookMarks: [],
        };
    },
    getters: {
        // getter를 업데이트하여 모든 정보를 반환
        getMyBookMarksDetails(state){
            return state.myBookMarks.map(book => ({
                documentId: book.documentId,  // 문서 ID
                title: book.title,            // 제목
                groupName: book.groupName     // 그룹 이름
            }))
        },
    },

    actions: {
        async setMyBookMarks() {
            const response = await fetch(`${BASE_URL}/user/book/list`, {
                method: 'GET',
                headers: {'Authorization': 'Bearer ' + this.accessToken},
            });
            this.myBookMarks = await response.json();

        }
    }
});
