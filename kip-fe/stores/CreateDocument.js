const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useCreateDocument = defineStore("createDocument", {
  state() {
    return {
      form: {}
    };
  },
  getters: {

  },

  actions: {
    async createNewDocument(form) {
      try {
        const response = await fetch(`${BASE_URL}/doc`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + user.getAccessToken},
          body: JSON.stringify(form),
        });
        const temp = await response.json();
        return temp;
      } catch (error) {
        console.error('Error fetching bookmarks:', error.message);
      }
    },
  }
});
