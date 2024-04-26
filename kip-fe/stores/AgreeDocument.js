const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useAgreeDocument = defineStore("agreeDocument", {
  state() {
    return {
      document: [],
    };
  },
  getters: {
    // getter를 업데이트하여 모든 정보를 반환
    getMyBookMarksDetails(state){
      return state.document.map(document => ({
        documentId: document.documentId,  // 문서 ID
        title: document.title,            // 제목
        groupName: document.groupName     // 그룹 이름
      }))
    },
  },

  actions: {
    async setMyDocument() {
      try {
        const response = await fetch(`${BASE_URL}/user/agree`, {
          method: 'GET',
          headers: {'Authorization': 'Bearer ' + user.getAccessToken},
        });
        if (!response.ok) throw new Error('Failed to fetch documents.');
        this.document = await response.json();
      } catch (error) {
        console.error('Error fetching documents:', error.message);
        this.document = []; // 에러가 발생한 경우 북마크 목록을 초기화
      }
    },
  }
});
