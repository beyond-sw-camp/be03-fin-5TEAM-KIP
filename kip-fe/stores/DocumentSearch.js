const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useDocumentSearch = defineStore("documentSearch", {
  state() {
    return {
      document: [],
      totalPages : 0,
      canView : String,
      groupId : {}
    };
  },
  getters: {
    getSearchDocument(state){
      // 여기서는 직접적으로 필요한 객체 구조를 반환합니다.
      return {
        document: state.document.map(document => ({
          documentUUID: document.uuid,  // 문서 ID
          groupName: document.groupName,  // 그룹 이름
          title: document.title,  // 제목
        })),
        totalPages: state.totalPages  // 전체 페이지 수
      };
    },
    getAvailable(state){
      // 여기서는 직접적으로 필요한 객체 구조를 반환합니다.
      return {
        canView: state.canView
      };
    },
    getGroupId(state) {
      return {
        groupId: state.groupId
      };
    }
  },

  actions: {
    async setSearchDocument(keyword,pageNumber) {
      try {
        const response = await fetch(`${BASE_URL}/doc/search?keyword=${keyword}&pageNumber=${pageNumber}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
        const data = await response.json()
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
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
        const data = await response.json()
        this.groupId = data.groupId;
        this.canView = data.result;
        console.log(this.canView)
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
  }
});
