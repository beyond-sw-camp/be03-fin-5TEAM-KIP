import {useBookMarks} from "~/stores/BookMarks.js";
import {useAgreeDocument} from "~/stores/AgreeDocument.js";

const BASE_URL = import.meta.env.VITE_BASE_URL;
const user = useUser();
const bookmarks = useBookMarks();
const agreeDocuments = useAgreeDocument()
const groupUser = useGroupuser();

export const useDocumentList = defineStore("documentList", {
    state() {
        return {
            documentList: [],
            fillteredDocList: [],
            adminDocumentList: [],
            publicDocumentList: [],
            selectedDocumentDetails: ref(null), // 선택된 문서의 상세 정보를 저장
            firstDocumentId: "",
            firstPublicDocumentId: "",
            hashTagsForTop100: [],
            fillteredTop100HaahTag: [],

        };
    },
    getters: {
        // getter를 업데이트하여 모든 정보를 반환
        // 그룹문서 title 조회
        getDocumentList(state) {
            return state.fillteredDocList
        },
        getAdminDocumentList(state) {
            return state.adminDocumentList
        },
        getHashTagsInSelectedDoc(state) {
            return state.selectedDocumentDetails.hashTags.map(tag => tag.tagName);
        },
        getSelectedDocId(state) {
            return state.selectedDocumentDetails.documentId;
        },
        getSelectedDocTitle(state) {
            return state.selectedDocumentDetails.title;
        },
        getSelectedDocContent(state) {
            return state.selectedDocumentDetails.content;
        },
        getPublicDocumentList(state) {
            return state.publicDocumentList
        },
        getFirstDocId(state) {
            return state.firstDocumentId
        },
        getFirstPublicDocId(state) {
            return state.firstPublicDocumentId
        }
    },

    actions: {
        async setDocumentList(groupId) {
            try {
                const response = await fetch(`${BASE_URL}/doc/${groupId}/linked`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (!response.ok) throw new Error('Failed to fetch documentList');
                this.documentList = await response.json();
                this.fillteredDocList = this.documentList

            } catch (error) {
                console.error('Error fetching documentList:', error.message);
                this.documentList = []; // 에러가 발생한 경우 북마크 목록을 초기화
            }
        },

        async setHashTagsForTop100List() {
            try {
                const response = await fetch(`${BASE_URL}/hashtag`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                this.hashTagsForTop100 = await response.json();
                this.fillteredTop100HaahTag = this.hashTagsForTop100.sort((a, b) => b.docsCounts - a.docsCounts).slice(0, 100);

            } catch (error) {
                console.error('해시태그 가져오기 실패 ', error.message);
            }
        },

        returnHashTagsForTop100List() {
            return this.selectedDocumentDetails.hashTags.map(tag => tag.tagName);
        },

        filterTop100HashTagsByClick(HashTagName) {
            this.fillteredTop100HaahTag = this.hashTagsForTop100.filter(tag => tag.tagName.includes(HashTagName)).slice(0, 100)
        },

        filterGroupDocByHashTag(hashTagId) {
            this.fillteredDocList = this.documentList.filter(doc => doc.hashTagIds.includes(hashTagId))
        },

        async setAdminDocumentList(groupId) {
            try {
                const response = await fetch(`${BASE_URL}/doc/${groupId}/linked`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                this.adminDocumentList = await response.json();
            } catch (error) {
                console.error('관리자 문서보기 초기화', error.message);
            }
        },
        async makePublicDocumentFromGroup(documentId) {
            if (user.getLoginUserRole !== 'ADMIN') {
                alert("관리자에게 문의하세요.");
                return;
            }
            try {
                await fetch(`${BASE_URL}/doc/${documentId}/public`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                });
            } catch (e) {
                console.log(e, "문서 전체공개로 변경하기 실패");
            }
        },
        async ChangeDocumentType(documentId) {
            try {
                await fetch(`${BASE_URL}/doc/${documentId}/type`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                });
            } catch (e) {
                console.log(e, "문서 타입 변경 실패");
            }
        },
        async deleteDocument(documentId) {
            try {
                await fetch(`${BASE_URL}/doc/${documentId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                });
            } catch (e) {
                console.log(e, "문서 삭제 실패");
            }
        },
        async filterPublicDocByHashTag(hashTagId) {
            try {
                const response = await fetch(`${BASE_URL}/hashtag/${hashTagId}/docs/public`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (response.ok)
                    this.publicDocumentList = await response.json();

            } catch (e) {
                console.log(e.message, "해시태그 문서 조회 실패 ")
            }
        },
        async updateHashTags(hashTagReq) {
            try {
                const response = await fetch(`${BASE_URL}/hashtag`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                    body: JSON.stringify(hashTagReq)
                });
                if (response.ok)
                    this.selectedDocumentDetails.hashTags = await response.json();

            } catch (e) {
                console.log(e.message, "해시태그 수정 실패")
            }
        },
        async updateDocumentTitle(targetDocumentId, newTitle) {
            try {
                const response = await fetch(`${BASE_URL}/doc/title`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                    body: JSON.stringify({targetDocumentId, newTitle})
                });
                if (response.ok)
                    this.selectedDocumentDetails.title = newTitle;
            } catch (e) {
                console.log(e.message, "문서 제목 수정 실패")
            }
        },

        async moveDocumentToTargetDoc(moveDocumentReq) {
            try {
                await fetch(`${BASE_URL}/doc/move`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                    body: JSON.stringify(moveDocumentReq)
                });
            } catch {
                console.log(e.message, "문서 이동 실패")
            }

        },
        async moveDocumentToTargetGroup(moveDocToGroupReq) {
            if (user.getLoginUserRole !== 'ADMIN') {
                alert("관리자에게 문의하세요.");
                return;
            }
            try {
                await fetch(`${BASE_URL}/doc/group`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                    body: JSON.stringify(moveDocToGroupReq)
                });
            } catch {
                console.log(e.message, "문서를 그룹으로 이동 실패")
            }
        },
        async setPublicDocumentList() {
            try {
                const response = await fetch(`${BASE_URL}/doc/public`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (!response.ok) throw new Error('Failed to fetch publicDocumentList');
                this.publicDocumentList = await response.json();
            } catch (error) {
                console.error('Error fetching publicDocumentList:', error.message);
                this.publicDocumentList = []; // 에러가 발생한 경우 북마크 목록을 초기화
            }
        },
        async setDocumentDetails(documentId) {
            try {
                const response = await fetch(`${BASE_URL}/doc/${documentId}`, {
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + user.getAccessToken},
                });
                if (!response.ok) throw new Error('Failed to fetch document details');
                this.selectedDocumentDetails = await response.json();
            } catch (error) {
                console.error('Error fetching document details:', error.message);
            }
        },

        // 첫번째 그룹문서의 상세정보를 가져오기
        async setFirstDocumentDetails() {
            if (this.documentList.length > 0) {
                const firstDocumentId = this.documentList[0].documentId;
                this.firstDocumentId = firstDocumentId
                await this.setDocumentDetails(firstDocumentId);
            }
        },

        // 첫번째 전체공개문서의 상세정보를 가져오기
        async setFirstPublicDocumentDetails() {
            if (this.publicDocumentList.length > 0) {
                const firstPublicDocumentId = this.publicDocumentList[0].documentId;
                this.firstPublicDocumentId = firstPublicDocumentId
                await this.setDocumentDetails(firstPublicDocumentId);
            }
        },

        // 북마크 첫번째 문서의 상세정보
        async setFirstBookDetails() {
            if (bookmarks.myBookMarks.length > 0) {
                const firstBookId = bookmarks.myBookMarks[0].documentId;
                await this.setDocumentDetails(firstBookId)
            }
        },

        async setAgreeDocumentDetails() {
            if (agreeDocuments.document.length > 0) {
                const firstDocument = agreeDocuments.document[0].documentId;
                await this.setDocumentDetails(firstDocument)
            }
        },
        async updateVersion(documentId, content, message) {
            try {
                const response = await fetch(`${BASE_URL}/version`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + user.getAccessToken
                    },
                    body: JSON.stringify({documentId, content, message})
                });
                this.selectedDocumentDetails.content = content;
            } catch (error) {
                console.error('Error update Version:', error.message);
            }
        },
    }
});