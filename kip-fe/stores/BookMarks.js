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
        // getter를 수정하여 반환 형식을 맞춤
        getMyBookMarkTitlesAndId(state){
            return state.myBookMarks.map(book => ({
                bookId: book[0],       // 첫 번째 요소가 bookId
                bookTitle: book[1]     // 두 번째 요소가 bookTitle
            }))
        },
    },

    actions: {
        async setMyBookMarks() {
            const response = await fetch(`${BASE_URL}/user/book/list`, {
                method: 'GET',
                headers: {'Authorization': 'Bearer ' + this.accessToken},
            });
            const data = await response.json();
            // 서버에서 받은 데이터 구조에 맞추어 myBookMarks를 설정
            if (data.status === "OK") {
                this.myBookMarks = data.result;
            } else {
                console.error('Failed to load bookmarks:', data.message);
                this.myBookMarks = []; // 에러가 발생했을 경우 목록을 비움
            }
        }
    }
});
