const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useBookMarks = defineStore("bookmarks", {
    state() {
        return {
            myBookMarks: [],
            filteredBookMarks: [],
        };
    },
    getters: {
        // getter를 업데이트하여 모든 정보를 반환
        getMyBookMarksDetails(state){
            return state.filteredBookMarks
        },
    },

    actions: {
        async setMyBookMarks() {
            try {
                const response = await fetch(`${BASE_URL}/user/book/list`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (!response.ok) throw new Error('Failed to fetch bookmarks.');
                this.myBookMarks = await response.json();
                this.filteredBookMarks = this.myBookMarks
            } catch (error) {
                console.error('Error fetching bookmarks:', error.message);
                this.myBookMarks = []; // 에러가 발생한 경우 북마크 목록을 초기화
            }
        },
        // 북마크 생성 및 삭제 함수 정의
        async removeMyBookmark(documentId) {
            try {
                const response = await fetch(`${BASE_URL}/doc/${documentId}/book`, {
                    method: 'POST',
                    headers: { 'Authorization': 'Bearer ' + user.getAccessToken },
                });
                const data = await response.json();
                if (response.ok) {
                    // 서버에서 북마크가 삭제된 후, 프론트엔드 상태도 업데이트
                    this.myBookMarks = this.myBookMarks.filter(book => book.documentId !== documentId);
                } else {
                    console.error('Failed to delete the bookmark:', data.message);
                }
            } catch (error) {
                console.error('Error while deleting the bookmark:', error);
            }
        },
        async filterBookMarkDocByHashTag(hashTagId) {
            this.filteredBookMarks = this.myBookMarks.filter(doc => doc.hashTagIds.includes(hashTagId))
        }
    }
});
