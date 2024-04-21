const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();

export const useGroup = defineStore("group", {
    state() {
        return {
            myGroupsInfo: [],
            HierarchyInfo: [],
            GroupUsersInfo: {},
            TopNaviGroupList: [],
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
        getTopNaviGroupList(state) {
            return state.TopNaviGroupList;
        },
        getHierarchyInfo(state) {
            return state.HierarchyInfo;
        }
    },
    actions: {

        async createNewGroup(createGroupReq) {
            try {
                const response = await fetch(`${BASE_URL}/group`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                    body: JSON.stringify(createGroupReq),
                });
                if (response.ok) {
                    // 뷰티파이 규칙.
                    const listInfo = [];
                    const objectInfo = await response.json();
                    listInfo.push(objectInfo);
                    this.HierarchyInfo = listInfo;
                }
            } catch (e) {
                console.log(e, "신규 그룹 생성 실패")
            }
        },

        async updateGroupInfo(upadeteGroupReq) {
            try {
                const response = await fetch(`${BASE_URL}/group`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                    body: JSON.stringify(upadeteGroupReq),
                });
                if (response.ok) {
                    // 뷰티파이 규칙.
                    const listInfo = [];
                    const objectInfo = await response.json();
                    listInfo.push(objectInfo);
                    this.HierarchyInfo = listInfo;
                }
            } catch (e) {
                console.log(e, "그룹 정보 업데이트 실패");
            }
        },

        async DeleteGruopFromDataBase (groupId) {
            try {
                const response = await fetch(`${BASE_URL}/group/${groupId}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                });
                if (response.ok) {
                    // 뷰티파이 규칙.
                    const listInfo = [];
                    const objectInfo = await response.json();
                    listInfo.push(objectInfo);
                    this.HierarchyInfo = listInfo;
                }
            } catch (e) {
                console.log(e, "그룹 삭제 실패");
            }
        },

        setTopNaviGroupList(groupId) {
            // 소속된 그룹들 중에 "DEPARTMENT" 부서를 찾음
            let findMyDepartmentGroup = null

            // 최초 그룹 리스트 불러오기 (최상단 조직부서)
            if (groupId === 0)
                findMyDepartmentGroup = this.myGroupsInfo
                    .find(group => group.groupType === "DEPARTMENT")
            else // 그룹아이디가 주어지면 해당 그룹 정보 불러오기
                findMyDepartmentGroup = this.myGroupsInfo
                    .find(group => String(group.groupId) === String(groupId))

            // 1개의 부서도 소속이 안된 사원은 소속 부서 없음 나옴.
            if (findMyDepartmentGroup === undefined) return ["소속된 부서없음"];

            // 찾은 부서의 상위 부서들을 리스트로 출력
            const superDepartment = Object.values(findMyDepartmentGroup.nameOfSuperGroups)

            // 소속된 그릅 이름을 리스트 마지막에 삽입
            superDepartment.push(findMyDepartmentGroup.groupName)
            this.TopNaviGroupList = superDepartment;  // 최종 리스티를 반환.
        },

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

            if (response.ok) {
                // 객체를 리스트로 한 번 감쌈 (뷰티파이 규칙)
                const listInfo = [];
                const objectInfo = await response.json();
                listInfo.push(objectInfo);

                this.HierarchyInfo = listInfo;
            }
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
})
