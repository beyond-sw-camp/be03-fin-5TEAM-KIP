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
            return state.usersInfoInGroup;
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
    }
});