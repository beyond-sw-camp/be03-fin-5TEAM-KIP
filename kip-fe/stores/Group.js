const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useGroup = defineStore("group", {
    state() {
        return {
            accessToken: user.getAccessToken,
            myGroupsInfo: [],
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
        }
    },
    actions: {
        async setMyGroupsInfo() {
            const response =
                await fetch(`${BASE_URL}/group/mygroups`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + this.accessToken},
                });
            this.myGroupsInfo = await response.json();
        }
    }
});