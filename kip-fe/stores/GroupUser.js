const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useGroupuser = defineStore("groupuser", {
    state() {
        return {
            groupName: "",
            usersInfoInGroup: [],
        };
    },
    getters: {
        getGroupName(state) {
            return state.groupName;
        },
        getUsersInfoInGroup(state) {
            // 역할순으로 정렬하고 역할이 같으면 이름순으로 정렬
            return state.usersInfoInGroup
                .sort((a, b) => {
                    const roleCompare = b.groupRole.localeCompare(a.groupRole)
                    if (roleCompare === 0)
                        return a.name.localeCompare(b.name);
                    return roleCompare;
                });
        },

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
                this.usersInfoInGroup = temp.userList;
                this.groupName = temp.groupName;
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
                this.usersInfoInGroup = this.getUsersInfoInGroup
                    .map(user => user.userId === updatedUserRole.userId
                        ? {...user, groupRole: updatedUserRole.groupRole}
                        : user)
                    .sort((a, b) => {
                        const roleCompare = b.groupRole.localeCompare(a.groupRole)
                        if (roleCompare === 0)
                            return a.name.localeCompare(b.name);
                        return roleCompare;
                    });
            } catch (e) {
                console.log(e, "그룹에 속한 유저 역할 수정 실패")
            }
        },
        async deleteUserFromGroup(gruopId, userId) {
            try {
                await fetch(`${BASE_URL}/group/${gruopId}/${userId}/delete`, {
                    method: 'DELETE',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                this.usersInfoInGroup = this.usersInfoInGroup
                    .filter(user => user.userId !== userId);
            } catch (e) {
                console.log(e, "그룹에 소속된 유저 그룹에서 제외 실패")
            }
        }
    }
});