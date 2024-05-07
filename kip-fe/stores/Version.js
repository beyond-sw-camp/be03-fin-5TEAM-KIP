const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useVersion = defineStore("version", {
  state() {
    return {
      versions:[],
      versionDetail:{}
    };
  },
  getters: {
    getVersions(state){
      // 여기서는 직접적으로 필요한 객체 구조를 반환합니다.
      return state.versions.map(version => ({
        versionId : version.versionId,
        writer : version.writer,
        createdTime : version.createdTime,
        message : version.message,
        isShow : version.isShow
      }));
    },
    getDetail(state){
      return state.versionDetail
    }
  },

  actions: {
    async setVersions(documentId) {
      try {
        const response = await fetch(`${BASE_URL}/doc/${documentId}/version`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
        this.versions = await response.json();
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
    async setVersionDetail(versionId) {
      try {
        const response = await fetch(`${BASE_URL}/version/${versionId}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
        const data = await response.json()
        this.versionDetail = data.content;
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
    async changeVersion(versionId) {
      try {
        const response = await fetch(`${BASE_URL}/doc/${versionId}`, {
          method: 'PATCH',
          headers: {
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
        const data = await response.json()
        this.versionDetail = data.content;
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
  }
});
