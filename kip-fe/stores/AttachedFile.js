const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useAttachedFile = defineStore("attachedFile", {
    state() {
        return {
            attachedFileList: [],
        };
    },
    getters: {
        // getter를 업데이트하여 모든 정보를 반환
        // 문서 첨부파일 조회
        getAttachedFileList(state) {
            return state.attachedFileList
        },
    },

    actions: {
        async setAttachedFileList(documentId) {
            try {
                const response = await fetch(`${BASE_URL}/doc/${documentId}/fileList`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (response.status === 500) // 첨부파일이 없는 경우.
                    this.attachedFileList = []
                else if (response.ok)
                    this.attachedFileList = await response.json();
                else
                    this.attachedFileList = []
            } catch (error) {
                console.error('첨부 파일을 가져오는데 실패했습니다.:', error.message);
                this.attachedFileList = []; // 에러가 발생한 경우 첨부파일 목록을 초기화
            }
        },

        async setAttachedFileUpload(documentId, fileData) {
            try {
                const formData = new FormData();
                formData.append('file', fileData);

                const response = await fetch(`${BASE_URL}/doc/${documentId}/fileUpload`, {
                    method: 'POST',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                    body: formData,
                });
                if (!response.ok) { // HTTP 상태 코드가 200-299를 벗어났을 때
                    throw new Error(`Failed to upload file: ${response.statusText}`);
                }

                const result = await response.json(); // 응답 본문을 JSON으로 파싱
                console.log('File uploaded successfully:', result);
                return result; // 업로드 성공 결과를 반환

            } catch (error) {
                console.error('Error uploading file:', error.message);
                throw error; // 오류를 상위 호출자에게 전파
            }
        },

        async setAttachedFileDelete(fileId) {
            try {
                const response = await fetch(`${BASE_URL}/doc/file/${fileId}`, {
                    method: 'DELETE',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (!response.ok) {
                    throw new Error('Failed to DELETE attachedFile');
                }
                console.log('File deleted successfully:', fileId);
            } catch (error) {
                console.error('Error DELETE attachedFile:', error.message);
            }
        },

    }
});