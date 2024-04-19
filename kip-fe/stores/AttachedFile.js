const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useAttachedFile = defineStore("attachedFile", {
    state() {
        return {
            attachedFile: [],
        };
    },
    getters: {
        // getter를 업데이트하여 모든 정보를 반환
        // 문서 첨부파일 조회
        getAttachedFile(state){
            return state.attachedFile.map(file => ({
                id: file.id,  // 첨부파일 ID
                documentId: file.document,     // 문서 ID
                fileName: file.fileName,      //문서 타입
                fileType: file.fileType,     // 제목
                fileUrl: file.fileUrl     // 제목
            }))
        },
    },

    actions: {
        async setAttachedFile(documentId) {
            try {
                const response = await fetch(`${BASE_URL}/doc/${documentId}/fileList`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (!response.ok) {throw new Error('Failed to fetch attachedFile');}
                this.attachedFile = await response.json();
            } catch (error) {
                console.error('Error fetching attachedFile:', error.message);
                this.attachedFile = []; // 에러가 발생한 경우 첨부파일 목록을 초기화
            }
        },

        async setFirstDocAttachedFile(){
            if (this.attachedFile.length > 0) {
                const firstDocAttachedFile = this.attachedFile[0].documentId;
                await this.setAttachedFile(firstDocAttachedFile);
            }
        }
    }
});