const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useDocumentList = defineStore("documentList", {
    state() {
        return {
            documentList: [],
        };
    },
    getters: {
        // getter를 업데이트하여 모든 정보를 반환
        getDocumentList(state){
            return state.documentList.map(document => ({
                documentId: document.documentId,  // 문서 ID
                doctype: document.doctype,            // 제목
                title: document.title     // 그룹 이름
            }))
        },
    },

    actions: {
        async setDocumentList(groupId) {
            try {
                const response = await fetch(`${BASE_URL}/doc/${groupId}/linked`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (!response.ok) throw new Error('Failed to fetch documentList');
                this.documentList = await response.json();
            } catch (error) {
                console.error('Error fetching documentList:', error.message);
                this.documentList = []; // 에러가 발생한 경우 북마크 목록을 초기화
            }
        },

        // 북마크 삭제 함수 정의
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
    }
});
