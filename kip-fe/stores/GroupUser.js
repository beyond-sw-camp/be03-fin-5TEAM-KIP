const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useGroupuser = defineStore("groupuser", {
    state() {
        return {
            groupName: "",
            groupType: "",
            usersInfoInGroup: [],
            allUserInfo: [],
        };
    },
    getters: {
        getGroupName(state) {
            return state.groupName;
        },
        getGroupType(state) {
            return state.groupType;
        },
        getUsersInfoInGroup(state) {
            return state.usersInfoInGroup
        },
        getAllUserInfoInKip(state) {
            // 그룹에 포함되지 않는 맴버들만 보여주기.
            return state.allUserInfo
        }

    },
    actions: {
        async setUsersInfoInGroup(gruopId) {
            try {
                const response =
                    await fetch(`${BASE_URL}/group/${gruopId}/users`, {
                        method: 'GET',
                        headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                    });
                const temp = await response.json();
                this.groupName = temp.groupName;
                this.groupType = temp.groupType;

                // 역할순으로 정렬하고 역할이 같으면 이름순으로 정렬
                this.usersInfoInGroup = temp.userList
                    .sort((a, b) => {
                        const roleCompare = b.groupRole.localeCompare(a.groupRole)
                        if (roleCompare === 0)
                            return a.name.localeCompare(b.name);
                        return roleCompare;
                    });
            } catch (e) {
                console.log(e, "그룹에 속한 유저 정보 로드 실패")
            }
        },
        async updateUserRoleInGroup(gruopId, userId) {
            try {
                const response =
                    await fetch(`${BASE_URL}/group/${gruopId}/${userId}/role`, {
                        method: 'PATCH',
                        headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                    });
                const updatedUserRole = await response.json();

                //  기존 정보에서 변경된 값만 갈아 끼워넣음.
                if (response.ok)
                    this.usersInfoInGroup = this.getUsersInfoInGroup
                        .map(user => user.userId === updatedUserRole.userId
                            ? {...user, groupRole: updatedUserRole.groupRole}
                            : user)
            } catch (e) {
                console.log(e, "그룹에 속한 유저 역할 수정 실패")
            }
        },
        async deleteUserFromGroup(gruopId, userId) {
            try {
                const response = await fetch(`${BASE_URL}/group/${gruopId}/${userId}/delete`, {
                    method: 'DELETE',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (response.ok)
                    this.usersInfoInGroup = this.usersInfoInGroup
                        .filter(user => user.userId !== userId);
            } catch (e) {
                console.log(e, "그룹에 소속된 유저 그룹에서 제외 실패")
            }
        },

        deletUserFromAllUserInfoInKip(employeeId) {
            this.allUserInfo = this.allUserInfo.filter(user => user.employeeId !== employeeId);
        },


        async setAllUserInfoInKip() {
            try {
                const respons = await fetch(`${BASE_URL}/user`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken}
                });
                let temp = await respons.json();

                // 그룹에 포함된 사용자는 저장하지 않는 로직
                for (let userInGroup of this.usersInfoInGroup)
                    temp = temp.filter(user => user.userId !== userInGroup.userId)
                        .sort((a, b) => a.name.localeCompare(b.name))
                this.allUserInfo = temp
            } catch (e) {
                console.log(e, "모든 유저 정보 가지고오기 실패");
            }
        },

        async addUserToGroup(gruopId, userId) {

            // 객체 조림
            let group = {};
            group.groupId = gruopId;
            group.groupUsers = [];

            let userTemp = {};
            userTemp.userId = userId;
            userTemp.groupRole = 'NORMAL';
            group.groupUsers.push(userTemp);

            try {
                const response = await fetch(`${BASE_URL}/group/addusers`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                    body: JSON.stringify(group),
                });
                if (response.ok)
                    this.allUserInfo = this.allUserInfo
                        .filter(user => user.userId !== userId);
            } catch (e) {
                console.log(e, "그룹에 새로운 사용자 추가 완료")
            }
        },
        async addCreatedUserToAllUsers(user){
            // 기존 유저정보 맨 앞에 추가.
            this.allUserInfo.unshift(user);
        }
    }
});