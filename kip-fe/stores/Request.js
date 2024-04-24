const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useRequest = defineStore("request", {
  state() {
    return {
      myRequest:[],
      receivedRequest:[]
    };
  },
  getters: {
    getMyRequest(state){
      // 여기서는 직접적으로 필요한 객체 구조를 반환합니다.
      return state.myRequest.map(request => ({
          requestId : request.requestId,
          documentName : request.documentName,
          groupName : request.groupName,
          days : request.days,
          isOk : request.isOk,
          dueDate : request.dueDate
        }));
    },
    getReceivedRequest(state){
      // 여기서는 직접적으로 필요한 객체 구조를 반환합니다.
      return state.receivedRequest.map(request => ({
          requestId : request.requestId,
          documentName : request.documentName,
          requesterName : request.requesterName,
          days : request.days,
          isOk : request.isOk,
          dueDate : request.dueDate
        }));
    },
  },

  actions: {
    async setMyRequest() {
      try {
        const response = await fetch(`${BASE_URL}/request`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
        const data = await response.json()
        this.myRequest = data;
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
    async setReceivedRequest() {
      try {
        const response = await fetch(`${BASE_URL}/request/receive`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
        const data = await response.json()
        this.receivedRequest = data;
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
    async agreeRequest(requestId) {
      try {
        await fetch(`${BASE_URL}/request/agree/${requestId}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
    async rejectRequest(requestId) {
      try {
        await fetch(`${BASE_URL}/request/refuse/${requestId}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
    async removeMyRequest(requestId) {
      try {
        await fetch(`${BASE_URL}/request/my/${requestId}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
    async removeReceivedRequest(requestId) {
      try {
        await fetch(`${BASE_URL}/request/receive/${requestId}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken},
        });
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
    async sendRequest(docId, days) {
      try {
        const response = await fetch(`${BASE_URL}/doc/request`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken },
          body: JSON.stringify({docId, days})
            });
        const result = await response.json();
        console.log(result)
      } catch (error) {
        console.error('Error fetching search:', error.message);
      }
    },
  }
});
