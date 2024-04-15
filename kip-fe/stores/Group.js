const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useGroup = defineStore("group", {
    state() {
        return {
            myGroupsInfo: [],
            HierarchyInfo: [],
            GroupUsersInfo: {},
        };
    },
    getters: {
        getMyGroupNamesAndId(state) {
            return state.myGroupsInfo.map(group => ({
                groupId: group.groupId,
                groupName: group.groupName,
                groupType: group.groupType,
            }))
        },
        getNameOfSuperGroups(state){
            // 소속된 그룹들 중에 "DEPARTMENT" 부서를 찾음
            const findMyDepartmentGroup =
                state.myGroupsInfo.find(group => group.groupType === "DEPARTMENT")
            // 1개의 부서도 소속이 안된 사원은 소속 부서 없음 나옴.
            if (findMyDepartmentGroup === undefined)
                return ["소속된 부서없음"];
            // 찾은 부서의 상위 부서들을 리스트로 출력
            const superDepartment = Object.values(findMyDepartmentGroup.nameOfSuperGroups)
            // 소속된 그릅 이름을 리스트 마지막에 삽입
            superDepartment.push(findMyDepartmentGroup.groupName)
            return superDepartment;  // 최종 리스티를 반환.
        },
        getHierarchyInfo(state){
            return state.HierarchyInfo;
        }
    },
    actions: {
        async setMyGroupsInfo() {
            const response =
                await fetch(`${BASE_URL}/group/mygroups`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
            this.myGroupsInfo = await response.json();
        },
        async setHierarchyInfo() {
            const response =
                await fetch(`${BASE_URL}/group/hierarchy/1`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });

            // 객체를 리스트로 한 번 감쌈 (뷰티파이 규칙)
            const listInfo = [];
            const objectInfo = await response.json();
            listInfo.push(objectInfo);

            this.HierarchyInfo = listInfo;
        },
        async setGroupUsersInfo(groupId) {
            const response =
                await fetch(`${BASE_URL}/group/${groupId}/users`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });

            this.GroupUsersInfo = await response.json();
        }
    }
});