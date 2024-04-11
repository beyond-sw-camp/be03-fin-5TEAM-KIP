const BASE_URL = import.meta.env.VITE_BASE_URL;

export const useBookMarks = defineStore("bookmarks", {
    state: () => ({
        bookmarks: []
    }),

    actions: {
        async list() {
            try {
                const response = await fetch(`${BASE_URL}/user/book/list`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + window.localStorage.getItem('accessToken')},
                });
                if (!response.ok) {
                    throw new Error('Failed to fetch bookmarks');
                }
                const data = await response.json();

                if (data && data.result) {
                    this.bookmarks = data.result; // 상태 업데이트
                } else {
                    throw new Error('Invalid response structure');
                }
            } catch (e) {
                console.error("Bookmarks fetch error:", e.message);
                // 에러 처리 로직 (예: 사용자에게 알림)
            }
        }
    }
});
