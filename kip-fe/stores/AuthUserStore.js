import {defineStore} from 'pinia';

const BASE_URL = import.meta.env.VITE_BASE_URL;

export default defineStore("AuthUserStore", {
    state() {
        return {
            userInfo: {},
            accessToken: ""
        };
    },
    getters: {
        getAccessToken(state) {
            return state.accessToken;
        },
        getUserInfo(state) {
            return state.userInfo;
        }
    },
    actions: {
        async userLogin(employeeId, password) {
            try {
                // 로그인 하고
                const response =
                    await fetch(`${BASE_URL}/user/login`, {
                        method: 'POST',  //POST 요청은 'Content-Type' 설정
                        body: JSON.stringify({employeeId, password}),
                        headers: {'Content-Type': 'application/json'}
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

                // 피니아 코컬스토리지 정보 비움.
                this.accessToken = "";
                this.userInfo = {};
                if (typeof window !== "undefined")
                    window.localStorage.removeItem('accessToken');

            } catch (e) {
                console.log(e, '로그아웃 실패')
            }
        },


    },
});