const BASE_URL = import.meta.env.VITE_BASE_URL;

export const useUser = defineStore("user", {
    state() {
        return {
            accessToken: "",
            userInfo: {},
            isLoggedIn: false,
            isExistId: false,
            isExistIdForCreate: false,
            isExistPhoneNumber: false,
            isExistEmail: false,
            isCorrectPassword: false,
            justUserName: "",
            createdUserData:{},
        };
    },
    getters: {
        getIsExistId(state) {
            return state.isExistId;
        },
        getIsExistIdForCreate(state) {
            return state.isExistIdForCreate;
        },
        getIsExistPhoneNumber(state) {
            return state.isExistPhoneNumber;
        },
        getIsExistEmail(state) {
            return state.isExistEmail;
        },
        getIsCorrectPassword(state) {
            return state.isCorrectPassword;
        },
        getJustUserName(state) {
            return state.justUserName;
        },
        getAccessToken(state) {
            return state.accessToken;
        },
        getUserInfo(state) {
            return state.userInfo;
        },
        getIsLoggedIn(state) {
            return state.isLoggedIn;
        },
        getProfileImageUrl(state) {
            return state.userInfo.profileImageUrl;
        },
        getCreatedUserData(state){
            return state.createdUserData;
        }
    },
    actions: {

        // DB에 사번이 있는지 검사하는 요청.
        async isExistEmployeeId(employeeId) {
            try {
                const response = await fetch(`${BASE_URL}/user/${employeeId.value}/id`, {
                    method: 'GET',
                });
                this.isExistId = await response.json()
            } catch (e) {
                console.log(e, "아이디 검사 실패")
            }
        },

        async isExistEmployeeIdForCreate(employeeId) {
            try {
                const response = await fetch(`https://server.teamkip.info/user/user/${employeeId}/id`, {
                    method: 'GET',
                });
                this.isExistIdForCreate = await response.json()
            } catch (e) {
                console.log(e, "아이디 검사 실패")
            }
        },
        async isExistPhoneNumberForCreate(phoneNumber) {
            try {
                const response = await fetch(`${BASE_URL}/user/${phoneNumber}/phone`, {
                    method: 'GET',
                });
                this.isExistPhoneNumber = await response.json()
            } catch (e) {
                console.log(e, "폰번호 중복 체크 실패")
            }
        },
        async isExistEmailForCreate(email) {
            try {
                const response = await fetch(`${BASE_URL}/user/${email}/email`, {
                    method: 'GET',
                });
                this.isExistEmail = await response.json()
            } catch (e) {
                console.log(e, "이메일 중복체크 실패")
            }
        },

        // 계정 생성
        async createUserAccount(data) {
            try {
                const response = await fetch(`${BASE_URL}/user`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${this.accessToken}`,
                    },
                    body: JSON.stringify(data),
                });
                this.createdUserData = await response.json();

            } catch (e) {
                console.log(e, "새로운 계정 생성 실패")
            }
        },

        // DB 아이디와 패스워드가 일치하면 이름을 리턴하는 요청
        async isPasswordCorrect(employeeId, password) {
            try {
                const response = await fetch(`${BASE_URL}/user/check`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({employeeId, password}),
                });
                const result = await response.json();
                this.isCorrectPassword = result.isValid;
                this.justUserName = result.userName;
            } catch (e) {
                console.log(e, "패스워드 검사 후 이름 불러오기 실패")
            }
        },

        async login(employeeId, password, token) {
            try {
                // 로그인 하고
                const response =
                    await fetch(`${BASE_URL}/user/login`, {
                        method: 'POST',  // BODY 타입 'Content-Type' 설정
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({employeeId, password, token}),
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
                alert("아이디 또는 비밀번호가 잘못 되었습니다.")
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
                        this.isLoggedIn = true;
                    } catch (e) {
                        console.log(e, '유저정보 가져오기 실패')
                    }
                await useRouter().push('/publicOpenDoc');
            }
        },
        async deleteUser(employeeId, name) {
            try {
                confirm("회원이 영구적으로 삭제됩니다.")
                const response =
                    await fetch(`${BASE_URL}/user/${employeeId}`, {
                        method: 'DELETE',
                        headers: {'Authorization': 'Bearer ' + this.accessToken}
                    })
                if (response.ok)
                    alert(`${name}님의 아이디가 영구 삭제되었습니다.`)
            } catch (e) {
                console.log(e, '사용자 삭제')
            }
        },
        async logout() {
            try {
                const response =
                    await fetch(`${BASE_URL}/user/logout`, {
                        method: 'DELETE',
                        headers: {'Authorization': 'Bearer ' + this.accessToken}
                    })
                const deleteRes = response.json();

                this.$reset(); // 유져 정보 리셋
                useCart().$reset() // 장바구니 리셋

                if (typeof window !== "undefined") { // 로컬스토리지 리셋
                    window.localStorage.removeItem('accessToken');
                    window.localStorage.removeItem('CartStore:items');
                }
                await useRouter().push('/');

            } catch (e) {
                console.log(e, '로그아웃 실패')
            }
        },

        // 개인 페이지에 대한 정보 가져온다
        async fetchUserInfo() {
            try {
                const response = await fetch(`${BASE_URL}/user/mypage`, {
                    headers: {'Authorization': `Bearer ${this.accessToken}`}
                });
                if (!response.ok) throw new Error('Failed to fetch user info');
                this.userInfo = await response.json();
            } catch (error) {
                console.error('Failed to fetch user info:', error);
            }
        },

        // 개인 정보 수정
        async updateUserInfo(updatedInfo) {
            try {
                const response = await fetch(`${BASE_URL}/user`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${this.accessToken}`
                    },
                    body: JSON.stringify(updatedInfo)
                });
                if (!response.ok) throw new Error('Failed to update user info');
                const updatedUserData = await response.json();
                this.userInfo = updatedUserData; // 상태 업데이트
                return updatedUserData; // 업데이트된 사용자 정보 반환
            } catch (error) {
                console.error('Failed to update user info:', error);
                alert('Failed to update user details.');
            }
        },

        async validateCurrentPassword(currentPassword) {
            try {
                const response = await fetch(`${BASE_URL}/user/validate-current-password`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${this.accessToken}`,
                    },
                    body: JSON.stringify({ password: currentPassword })
                });
                const data = await response.json();
                return data; // 서버에서 true/false 값을 직접 반환하도록 수정
            } catch (error) {
                console.error('Failed to validate password:', error);
                return false;
            }
        },

        async changePassword(currentPassword, newPassword) {
            try {
                const response = await fetch(`${BASE_URL}/user/change-password`, {
                    method: 'PATCH',
                    headers: {
                        'Authorization': `Bearer ${this.accessToken}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        findByEmployeeId: this.userInfo.employeeId,
                        currentPassword,
                        newPassword
                    })
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Failed to change password');
                }

                // 비밀번호 변경 성공 시
                return true;
            } catch (error) {
                console.error('Error changing password:', error);
                throw error; // 컴포넌트에서 에러 처리를 할 수 있도록 에러를 다시 던집니다.
            }
        },



    },

    setup() {
        const accessToken = ref('');
        const userInfo = ref({});

        return {
            accessToken,
            userInfo
        };
    }
});