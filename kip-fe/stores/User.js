const BASE_URL = import.meta.env.VITE_BASE_URL;

export const useUser = defineStore("user", {
    state() {
        return {
            accessToken: "",
            userInfo: {},
            isLoggedIn: false,
        };
    },
    getters: {
        getAccessToken(state) {
            return state.accessToken;
        },
        getUserInfo(state) {
            return state.userInfo;
        },
        getIsLoggedIn(state){
            return state.isLoggedIn;
        }
    },
    actions: {
        async userLogin(employeeId, password) {
            try {
                // 로그인 하고
                const response =
                    await fetch(`${BASE_URL}/user/login`, {
                        method: 'POST',  //POST 요청은 'Content-Type' 설정
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({employeeId, password}),
                    });
                const tokenRes = await response.json()
                const tokenData = tokenRes.result.access_token;

                // 유저정보 가지고 와서
                const userInfoRes =
                    await fetch(`${BASE_URL}/user/mypage`, {
                        method: 'GET',
                        headers: {'Authorization': 'Bearer ' + tokenData}
                    })
                const userData = await userInfoRes.json();

                // 피니아 스토리지에 저장.
                this.accessToken = tokenData;
                this.userInfo = userData.result;

                // 로컬 스토리지에 저장.
                if (typeof window !== "undefined")  // CSR 인경우만 동작함.
                    window.localStorage.setItem('accessToken', tokenData);

                // 로그인 표시
                this.isLoggedIn = true;

            } catch (e) {
                console.log(e, '로그인 실패')
            }
        },

        // 로그아웃 안하고 새로고침 했을때 사용자 정보 유지 하는 함수.
        async setUserInfoAndTokenToPiniaFromLocalStorage() {
            if (typeof window !== "undefined") {
                this.accessToken = window.localStorage.getItem('accessToken');
                if (this.accessToken)
                    try {
                        const response =
                            await fetch(`${BASE_URL}/user/mypage`, {
                                method: 'GET',
                                headers: {'Authorization': 'Bearer ' + this.accessToken}
                            });
                        const userInfoRes = await response.json();
                        this.userInfo = userInfoRes.result;
                    } catch (e) {
                        console.log(e, '유저정보 가져오기 실패')
                    }
            }
        },

        async userLogOut() {
            try {
                const response =
                    await fetch(`${BASE_URL}/user/logout`, {
                        method: 'DELETE',
                        headers: {'Authorization': 'Bearer ' + this.accessToken}
                    })
                const deleteRes = response.json();
                console.log(deleteRes.result)

                this.$reset(); // 유져 정보 리셋
                CartStore().$reset() // 장바구니 리셋

                if (typeof window !== "undefined"){ // 로컬스토리지 리셋
                    window.localStorage.removeItem('accessToken');
                    window.localStorage.removeItem('CartStore:items');
                }

            } catch (e) {
                console.log(e, '로그아웃 실패')
            }
        },
    },
});