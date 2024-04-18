const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useDocumentList = defineStore("documentList", {
    state() {
        return {
            documentList: [],
            publicDocumentList: [],
            selectedDocumentDetails: ref(null), // 선택된 문서의 상세 정보를 저장
        };
    },
    getters: {
        // getter를 업데이트하여 모든 정보를 반환
        // 그룹문서 title 조회
        getDocumentList(state){
            return state.documentList.map(document => ({
                documentId: document.documentId,  // 문서 ID
                doctype: document.doctype,      //문서 타입
                title: document.title     // 제목
            }))
        },

        // 전체공개문서 title 조회
        getPublicDocumentList(state){
            return state.publicDocumentList.map(publicDocument => ({
                documentId: publicDocument.documentId,
                title: publicDocument.title
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

        async setPublicDocumentList(){
            try {
                const response = await fetch(`${BASE_URL}/doc`,{
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (!response.ok) throw new Error('Failed to fetch publicDocumentList');
                this.publicDocumentList = await response.json();
            } catch (error){
                console.error('Error fetching publicDocumentList:', error.message);
                this.publicDocumentList = []; // 에러가 발생한 경우 북마크 목록을 초기화
            }
        },
        async setDocumentDetails(documentId) {
            try {
                const response = await fetch(`${BASE_URL}/doc/${documentId}`,{
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (!response.ok) throw new Error('Failed to fetch document details');
                this.selectedDocumentDetails = await response.json();
            } catch (error){
                console.error('Error fetching document details:', error.message);
            }
        },

        // 첫번째 그룹문서의 상세정보를 가져오기
        async setFirstDocumentDetails(){
            if (this.documentList.length > 0) {
                const firstDocumentId = this.documentList[0].documentId;
                await this.setDocumentDetails(firstDocumentId);
            }
        },

        // 첫번째 전체공개문서의 상세정보를 가져오기
        async setFirstPublicDocumentDetails(){
            if (this.publicDocumentList.length > 0) {
                const firstPublicDocumentId = this.publicDocumentList[0].documentId;
                await this.setDocumentDetails(firstPublicDocumentId);
            }
        },
    }
});