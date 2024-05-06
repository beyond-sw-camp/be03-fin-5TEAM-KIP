<script setup>
import {ref} from "vue";
import {toastViewerInstance} from "~/useToastViewer";
import {VTreeview} from 'vuetify/labs/VTreeview'


// 피니아.
const documentList = useDocumentList();
const createDocument = useCreateDocument();
const bookmarks = useBookMarks();
const attachedFile = useAttachedFile();
const color = useColor();
const group = useGroup();

// 상단 네비 제목 설정
group.TopNaviGroupList = ["Knowledge is Power", "전체공개문서", "태그 검색  (ALT + H) 🏷️"];

// 기본 변수들
const loading = ref(false);
const titleLoding = ref(false)
const snackbar = ref(false);
const dialog = ref(false);
const viewer = ref();
const upLinkId = ref();

// 첨부파일 관련
const files = ref([]);
const fileDialog = ref(false);
const fileLoading = ref(false);
const attachedFileModal = ref(false);


// 초기 문서 세팅
await bookmarks.setMyBookMarks();
await documentList.setPublicDocumentList();          // 문서리스트 세팅
await documentList.setFirstPublicDocumentDetails();  // 최상단 문서 정보 세팅
await attachedFile.setAttachedFileList(documentList.getFirstPublicDocId);
const UpdateToastViewer = async () => {
  viewer.value = await toastViewerInstance(
      viewer.value,
      documentList.getSelectedDocContent
  );
}
onMounted(async () => {
  await UpdateToastViewer()
})

// 해시태그 업데이트 관련
const hashTagUpdateModal = ref(false);
const hashTagsUpdateReqDto = ref({
  documentId: "",
  hashTags: []
});
const hashTagUpdateModalOpen = async () => {
  await documentList.setHashTagsForTop100List();
  hashTagsUpdateReqDto.value.documentId = documentList.getSelectedDocId
  hashTagsUpdateReqDto.value.hashTags = documentList.returnHashTagsForTop100List()
  hashTagUpdateModal.value = !hashTagUpdateModal.value
}
const hashTagUpdateReq = () => {
  documentList.updateHashTags(hashTagsUpdateReqDto.value)
  hashTagUpdateModal.value = false;
}
const Top100HashTagAddAndFiltering = (hashTagId, hashTageName) => {
  documentList.filterPublicDocByHashTag(hashTagId)
  if (!hashTagsUpdateReqDto.value.hashTags.includes(hashTageName))
    hashTagsUpdateReqDto.value.hashTags.push(hashTageName)
  else alert(`${hashTageName}은 이미 추가된 해새태그 입니다.`)
}
const ResetHasTagAddAndFiltering = async () => {
  await documentList.setHashTagsForTop100List()
  await documentList.setPublicDocumentList()
}

// 문서 삭제 관련 코드.
const deleteDocModalOpen = ref();
const selectedDeleteDocTitle = ref();
const selectedDeleteDocId = ref();
const OpenDeleteDocumentModal = async (documenetTitle, documentId) => {
  loading.value = false;
  if (documentList.getPublicDocumentList.length > 1 && documentId !== 2) {
    deleteDocModalOpen.value = true;
    selectedDeleteDocTitle.value = documenetTitle;
    selectedDeleteDocId.value = documentId;
  } else if (documentId === 2) alert("공지사항 문서는 삭제할 수 없습니다.")
  else alert("전체공개문서는 1개이상 있어야 합니다.");
}
const realDeleteSelectedDoc = async () => {
  loading.value = true;
  await documentList.deleteDocument(selectedDeleteDocId.value)
  await documentList.setPublicDocumentList();
  await wait(800);
  deleteDocModalOpen.value = false;
  snackbar.value = true;
}

// 문서 제목 업데이트 관련
const titleEditing = ref(false);
const newTitle = ref();
const showTitleEditor = () => {
  titleEditing.value = !titleEditing.value
  newTitle.value = documentList.getSelectedDocTitle;

}
const updateDocumentTitle = async () => {
  titleEditing.value = false
  await documentList.updateDocumentTitle(
      documentList.getSelectedDocId,
      newTitle.value)
  await documentList.setPublicDocumentList()
  newTitle.value = ""
}

// 문서 선택 시 상세 정보를 가져오는 함수
const selectDocument = async (documentId) => {
  // 문서의 상세 정보를 가져옴
  await documentList.setDocumentDetails(documentId);
  await attachedFile.setAttachedFileList(documentId);
  await UpdateToastViewer();
};

// 문서작성 관련코드
const postForm = ref();
const openCreateNewDocument = () => {
  upLinkId.value = null;
  dialog.value = !dialog.value;
}
const handleData = async (form) => {
  form.groupId = null;
  form.upLinkId = null;
  const temp = await createDocument.createNewDocument(form)
  await documentList.setPublicDocumentList();
  await selectDocument(temp.documentId);
  dialog.value = false;
}

//  문서 수정
const createNewVersionModal = ref(false);
const versionHistoryModal = ref(false);
const updateContent = ref();
const createNewVersion = async (form) => {
  await documentList.updateVersion(documentList.getSelectedDocId, form.value.content, form.value.message);
  await UpdateToastViewer()
  createNewVersionModal.value = false;
}


// 전체공개문서 기존그룹으로 이동
const handlerMoveDocToGroup = ref(false)
const selectedTargetGroupName = ref("한화시스템")
const selectedTargetDocumentTitle = ref("")
const moveDocToGroupReq = ref({
  targetDocumentId: "",
  targetGroupId: "1"
})
const realShowGroupModalForSelect = async (documenetTitle, documentId) => {
  if (documentList.getPublicDocumentList.length > 1 && documentId !== 2) {
    await group.setHierarchyInfo();
    selectedTargetGroupName.value = "한화시스템"
    handlerMoveDocToGroup.value = true
    moveDocToGroupReq.value.targetDocumentId = documentId
    selectedTargetDocumentTitle.value = documenetTitle
  } else if (documentId === 2) alert("공지사항 문서는 이동할 수 없습니다.")
  else alert("전체공개문서는 1개이상 있어야 합니다.");
}
const SetTargetGroupIdAndName = (selectedGroupInfo) => {
  moveDocToGroupReq.value.targetGroupId = selectedGroupInfo.id
  selectedTargetGroupName.value = selectedGroupInfo.title
}
const RealMoveDocToTargetGroup = async () => {
  if (confirm(`${selectedTargetDocumentTitle.value} 문서를 이동하시겠습니까?`)) {
    handlerMoveDocToGroup.value = false
    await documentList.moveDocumentToTargetGroup(moveDocToGroupReq.value)
    await documentList.setPublicDocumentList();
    alert("문서가 정상적으로 이동하였습니다.")
  }
}

// 첨부파일
const fileDialogOpen = () => {
  files.value = []; // 파일 목록 초기화
  fileLoading.value = false;
  fileDialog.value = !fileDialog.value;
}
const handleFileUpload = async () => {
  fileLoading.value = true; // 빙글이 시작
  await wait(1200); // 1.2초 대기
  // 각 파일에 대해 업로드 로직 실행
  for (let file of files.value) {
    await attachedFile.setAttachedFileUpload(documentList.selectedDocumentDetails.documentId, file);
  }
  // 파일 업로드 후 첨부파일 목록 다시 불러오기
  await attachedFile.setAttachedFileList(documentList.selectedDocumentDetails.documentId);
  fileLoading.value = false; // 빙글이 끝내기
  fileDialog.value = false; // 다이얼로그 닫기
};
const handleFileClick = (url) => {
  window.open(url, '_blank');
};
const AttachedFileDelete = async (fileId) => {
  await attachedFile.setAttachedFileDelete(fileId);
  await wait(2000); // 1.2초 대기
  // 첨부파일 삭제 후 첨부파일 목록 다시 불러오기
  await attachedFile.setAttachedFileList(documentList.selectedDocumentDetails.documentId);
  attachedFileModal.value = false
};

// 선택한 문서 ID가 북마크 목록에 있는지 확인
const isBookmarked = computed(() =>
    bookmarks.myBookMarks.some(book => book.documentId === documentList.getSelectedDocId)
);

// 북마크 버튼 클릭 핸들러
const handleBookmarkClick = async () => {
  // 만약 현재 문서가 북마크되어 있다면, 북마크를 제거하는 액션을 실행합니다.
  if (isBookmarked.value) {
    await bookmarks.removeMyBookmark(documentList.getSelectedDocId);
  } else {
    await bookmarks.removeMyBookmark(documentList.getSelectedDocId);
  }

  // 북마크 상태를 갱신합니다.
  await bookmarks.setMyBookMarks();
};

// 단축키
import {onKeyStroke} from '@vueuse/core'
import {useKeyModifier} from '@vueuse/core'

const KipButton = ref(false)
const alt = useKeyModifier('Alt')
onKeyStroke(['Q', 'q'], () => {
  if (alt.value) KipButton.value = !KipButton.value;
})
onKeyStroke(['M', 'm'], () => {
  if (alt.value) handleBookmarkClick()
})
onKeyStroke(['N', 'n'], () => {
  if (alt.value) openCreateNewDocument()
})
onKeyStroke(['T', 't'], () => {
  if (alt.value) showTitleEditor()
})
onKeyStroke(['H', 'h'], () => {
  if (alt.value) hashTagUpdateModalOpen()
})
onKeyStroke(['A', 'a'], () => {
  if (alt.value) fileDialogOpen()
})
onKeyStroke(['R', 'r'], () => {
  if (alt.value) ResetHasTagAddAndFiltering();
})
onKeyStroke(['U', 'u'], () => {
  if (alt.value) createNewVersionModal.value = !createNewVersionModal.value;
})
onKeyStroke(['Y', 'y'], () => {
  if (alt.value) versionHistoryModal.value = !versionHistoryModal.value
})
onKeyStroke(['Enter'], () => {
  if (alt.value)
    if (dialog.value) postForm.value.submit();
    else if (createNewVersionModal.value) updateContent.value.submit();
})

</script>
<template>
  <v-container fluid>
    <v-row no-gutters>
      <!-- 👈👈👈👈👈👈👈👈 왼쪽 사이드바 -->
      <v-col cols="3">
        <v-list class="pa-4">
          <v-list-item>
            <v-list-item-title class="font-weight-bold headline text-center mb-4 pa-2">
              전체공개문서 🌐
            </v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>

          <!-- 전체공개 문서 title 출력 -->
          <v-tabs color="primary" direction="vertical" class="mt-4">
            <v-tab
                v-for="doc in documentList.getPublicDocumentList"
                :key="doc.documentId"
                @click="selectDocument(doc.documentId)">

              <div class="ellipsis" style="width:11vw; text-align: start">{{ doc.title }}</div>
              <v-spacer></v-spacer>
              <v-hover v-slot="{ isHovering, props }">

                <!--                  ❌️ 삭제버튼 -->
                <v-btn
                    icon="mdi-trash-can"
                    v-bind="props"
                    class="ml-5"
                    :class="{
                            'on-hover': isHovering,
                            'show-btns': isHovering
                          }"
                    color="rgba(255, 255, 255, 0)"
                    variant="plain"
                    @click="OpenDeleteDocumentModal(doc.title, doc.documentId)"
                />


                <!--                 ⏩⏩ 그룹으로 이동 버튼  -->
                <v-btn
                    icon="mdi-location-exit"
                    v-bind="props"
                    :class="{
                            'on-hover': isHovering,
                            'show-btns': isHovering
                          }"
                    color="rgba(255, 255, 255, 0)"
                    variant="plain"
                    @click="realShowGroupModalForSelect(doc.title, doc.documentId)"
                />
              </v-hover>
            </v-tab>
          </v-tabs>
        </v-list>
        <!--            ⏩⏩  그룹 이동을 위한 모달 -->
        <v-dialog
            class="d-flex"
            width="45vw"
            opacity="10%"
            v-model="handlerMoveDocToGroup">
          <v-sheet
              rounded="xl"
              class="pa-10">
            <div class="d-flex justify-space-between">
              <h2 class="mb-4 text-center">
                {{ `${selectedTargetDocumentTitle} 문서 👉 ${selectedTargetGroupName} 그룹으로` }}</h2>
              <v-btn
                  color="info"
                  :loading="titleLoding"
                  text="이동하기 🚀"
                  type="submit"
                  @click="RealMoveDocToTargetGroup"
              />
            </div>
            <v-treeview
                :items="group.getHierarchyInfo"
                color="blue">
              <template v-slot:prepend="{ item }">
                <v-icon
                    v-if="item.children"
                    :icon="`mdi-${item.children.length === 0
                                ? 'account-group-outline' : 'folder-network'}`"
                    @click="SetTargetGroupIdAndName(item)"
                />
              </template>
              <template v-slot:title="{ item }">
                <div @click="SetTargetGroupIdAndName(item)">
                  {{ item.title }} {{ item.groupType === "DEPARTMENT" ? '&nbsp 🏢' : '&nbsp 🚀' }}
                </div>
              </template>
            </v-treeview>
          </v-sheet>
        </v-dialog>

        <!--        ❌삭제 확인 모달 --->
        <v-dialog
            v-model="deleteDocModalOpen"
            max-width="500">
          <v-card title="문서 삭제">
            <v-card-text>
              {{ selectedDeleteDocTitle }} 문서를 삭제하시겠습니까?
            </v-card-text>
            <v-card-actions>
              <v-spacer/>
              <v-btn
                  v-if="!loading"
                  text="Yes"
                  @click="realDeleteSelectedDoc"
              />
              <v-progress-circular
                  class="mr-5"
                  v-if="loading"
                  color="primary"
                  indeterminate
              />
              <v-btn
                  text="No"
                  @click="deleteDocModalOpen = false"/>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-snackbar
            :color="color.kipMainColor"
            rounded="pill"
            elevation="24"
            v-model="snackbar"
            :timeout="3000">
          <div class="text-center">{{ selectedDeleteDocTitle }} 문서가 삭제되었습니다.</div>
        </v-snackbar>

        <!--        문서 작성을 위한 모달 -->
        <v-dialog v-model="dialog"
                  width="95vw"
                  opacity="90%"
        >
          <v-card  rounded="xl"
                   class="pa-8">
            <v-card-actions
                style="
                display: flex;
                justify-content: end;
                position: fixed;
                top: 60px; right: 30px;
                width: 12vw;
                z-index: 9999;">
              <v-btn
                  style="background-color: darkred; color: white"
                  @click=postForm.submit()>저장
                <v-tooltip
                    activator="parent"
                    location="start">ALT + Enter
                </v-tooltip>
              </v-btn>
              <v-btn
                  style="background-color: var(--secondary-color); color: white"
                  @click="dialog = false">닫기
                <v-tooltip
                    activator="parent"
                    location="bottom">ALT + N
                </v-tooltip>
              </v-btn>
            </v-card-actions>
            <PostForm ref="postForm" @submit="handleData"></PostForm>
          </v-card>
        </v-dialog>


        <!--      문서 수정을 위한 모달 -->
        <v-dialog v-model="createNewVersionModal"
                  width="95vw"
                  opacity="90%"
        >
          <v-card  rounded="xl"
                   class="pa-8">
            <v-card-actions
                style="
                display: flex;
                justify-content: end;
                position: fixed;
                top: 50px; right: 35px;
                width: 12vw;
                z-index: 9999;">
              <v-btn
                  style="background-color: darkred; color: white"
                  @click=updateContent.submit()>저장
                <v-tooltip
                    activator="parent"
                    location="start">ALT + Enter
                </v-tooltip>
              </v-btn>
              <v-btn
                  style="background-color: var(--secondary-color); color: white"
                  @click="createNewVersionModal = false">닫기
                <v-tooltip
                    activator="parent"
                    location="bottom">ALT + U
                </v-tooltip>
              </v-btn>
            </v-card-actions>
            <UpdateContent ref="updateContent" @submit="createNewVersion"
                           :dataToPass="documentList.getSelectedDocContent"></UpdateContent>
          </v-card>
        </v-dialog>

        <!--        버전 변경을 위한 모달-->
        <v-dialog
            class="d-flex justify-start ml-12"
            width="60vw"
            opacity="10%"
            v-model="versionHistoryModal">
          <v-card
              rounded="xl"
              class="pa-4">
            <VersionHistory
                @version-changed="UpdateToastViewer"
                :selectDocumentId="documentList.getSelectedDocId"></VersionHistory>
          </v-card>
        </v-dialog>

      </v-col>
      <v-divider class="divider-container" vertical/>

      <!-- ☝️☝️☝️☝️☝️☝️☝️ 가운데 문서제목 부분 -->
      <v-col cols="7" class="position-relative">
        <v-list class="pa-4">
          <v-card flat>
            <div class="d-flex justify-center">
              <v-card-title v-if="titleEditing" class="headline text-center">
                <v-text-field
                    class="title__update"
                    v-model="newTitle"
                    @blur="titleEditing = false"
                    @keyup.enter="updateDocumentTitle"
                    autofocus
                    persistent-placeholder
                    persistent-hint
                    append-inner-icon="mdi-keyboard-return"
                    hint="변경할 제목을 입력하시고 엔터를 입력하세요."
                    placeholder="변경할 제목을 입력하세요."
                    style="min-width: 40vw;"
                    variant="underlined"
                ></v-text-field>
              </v-card-title>

              <!-- 제목 표시 -->
              <v-card-title
                  @click="showTitleEditor"
                  v-else class="headline text-center mb-4 pa-2">
                {{ documentList.selectedDocumentDetails.title }}
              </v-card-title>
              <v-btn
                  class=" pt-0 pb-12 px-0"
                  variant="text"
                  rounded="xl"
                  @click="handleBookmarkClick">
                <v-icon
                    color="yellow-darken-2"
                    size="xxx-large"
                    :icon="isBookmarked ? 'mdi-star' : 'mdi-star-outline'"
                />
                <v-tooltip
                    activator="parent"
                    location="end"
                >ALT + M
                </v-tooltip>
              </v-btn>
            </div>
          </v-card>

          <!-- 가로 선 추가 -->
          <v-divider></v-divider>
        </v-list>
        <v-card flat class="px-6 mt-4 mx-auto">
          <div ref="viewer">{{ documentList.getSelectedDocContent }}</div>
        </v-card>

        <!--        스피드 모달 아이콘-->
        <div class="fab_div">
          <v-container class="d-flex justify-end" style="margin: 30px;">
            <v-speed-dial v-model="KipButton" location="top center" transition="scale-transition">
              <template v-slot:activator="{ props: activatorProps }">
                <v-btn
                    rounded="circle"
                    elevation="4"
                    v-bind="activatorProps"
                    size="large"
                    stacked>
                  <v-tooltip
                      activator="parent"
                      location="start">ALT + Q
                  </v-tooltip>

                  <v-img
                      width="36px"
                      src="/images/logos/kiplogo.svg"/>
                </v-btn>
              </template>
              <v-btn
                  color="deep-orange-lighten-1"
                  class="mb-3"
                  key="4"
                  size="large"
                  rounded="xl"
                  prepend-icon="mdi-plus"
                  @click="openCreateNewDocument"> 새글쓰기
                <v-tooltip
                    activator="parent"
                    location="start">ALT + N
                </v-tooltip>
              </v-btn>
              <v-btn
                  color="light-green-darken-2"
                  style="width:200px !important;"
                  class="mb-3"
                  text="제목수정"
                  key="1"
                  size="large"
                  rounded="xl"
                  prepend-icon="mdi-format-title"
                  @click="showTitleEditor">제목수정
                <v-tooltip
                    activator="parent"
                    location="start">ALT + T
                </v-tooltip>
              </v-btn>
              <v-btn
                  :color="color.kipMainColor"
                  class="mb-3"
                  key="2"
                  size="large"
                  rounded="xl"
                  prepend-icon="mdi-pencil"
                  @click="createNewVersionModal=true"> 내용수정
                <v-tooltip
                    activator="parent"
                    location="start">ALT + U
                </v-tooltip>

              </v-btn>
              <v-btn
                  :color="color.kipMainColor"
                  class="mb-3"
                  key="3"
                  size="large"
                  rounded="xl"
                  prepend-icon="mdi-history"
                  @click="versionHistoryModal=true">수정이력
                <v-tooltip
                    activator="parent"
                    location="start">ALT + Y
                </v-tooltip>
              </v-btn>
            </v-speed-dial>
          </v-container>
        </div>

      </v-col>
      <v-divider class="divider-container" vertical/>

      <!-- 👉👉👉👉👉👉👉👉👉 오른쪽 영역 -->
      <v-col cols="2">
        <!-- 첨부 파일 섹션 -->
        <div class="attached-files">
          <v-card flat>
            <v-card-title class="headline text-center">첨부 파일
            </v-card-title>
            <!-- 첨부파일 업로드 로직 부분 -->
            <v-dialog
                class="d-flex justify-center"
                width="45vw"
                opacity="50%"
                v-model="fileDialog">

              <v-sheet
                  rounded="xl"
                  class="d-flex justify-center flex-wrap pa-10">

                <v-form ref="form" style="width: 50vw">
                  <v-file-input
                      v-model="files"
                      :color="color.kipMainColor"
                      label="업로드할 파일을 선택해 주세요"
                      placeholder="업로드할 파일을 선택해 주세요"
                      prepend-icon="mdi-paperclip"
                      counter
                      :show-size="1000"
                      multiple
                  >
                    <template v-slot:selection="{ fileNames }">
                      <template v-for="fileName in fileNames" :key="fileName">
                        <v-chip
                            class="ma-1 pa-5"
                            :color="color.kipMainColor"
                        >
                          {{ fileName }}
                        </v-chip>
                      </template>
                    </template>
                  </v-file-input>

                  <v-btn
                      class="mt-7"
                      :color="color.kipMainColor"
                      :loading="fileLoading"
                      text="업로드 완료"
                      @click="handleFileUpload"
                      block
                  />
                </v-form>
              </v-sheet>
            </v-dialog>


            <!-- 첨부파일 목록 -->
            <v-card-text>
              <v-card
                  v-for="file in attachedFile.getAttachedFileList"
                  :key="file.fileName"
                  class="my-3"
                  color="blue-lighten-1"
                  variant="outlined"
                  rounded="xl">

                <v-row>
                  <v-col cols="3" class="d-flex justify-center align-center py-2">
                    <v-btn
                        class="ml-4"
                        @click="handleFileClick(file.fileUrl)"
                        :icon="file.fileType.includes('image') ? 'mdi-image-outline'
                          :`${file.fileType.includes('compressed') ? 'mdi-folder-zip'
                          :`${file.fileType.includes('application/pdf') ? 'mdi-file-pdf-box':'mdi-file-document-outline' }`}`"
                        variant="text"
                    />

                  </v-col>
                  <v-col cols="6" class="d-flex justify-start align-center py-2" style="width: 70%">
                    <div
                        @click="handleFileClick(file.fileUrl)"
                        class="cursor-pointer ellipsis" style="width:100%">
                      {{ file.fileName }}
                      <v-tooltip
                          activator="parent"
                          location="start"
                      >{{ file.fileName }}
                      </v-tooltip>
                    </div>
                  </v-col>

                  <v-col cols="3" class="d-flex justify-start align-center py-2">
                    <v-btn
                        class="mr-4"
                        @click="attachedFileModal=true"
                        icon="mdi-close"
                        variant="text"
                        rounded="xl"
                        size="sm"
                    />
                  </v-col>
                </v-row>
                <!--  첨부파일 삭제를 위한 모달-->
                <v-dialog
                    v-model="attachedFileModal"
                    max-width="500">

                  <v-card title="첨부파일 삭제">
                    <v-card-text>
                      첨부파일을 삭제하시겠습니까?
                    </v-card-text>
                    <v-card-actions>
                      <v-spacer></v-spacer>

                      <v-snackbar
                          :timeout="2000"
                      >
                        <template v-slot:activator="{ props }">
                          <v-btn
                              v-bind="props"
                              @click="AttachedFileDelete(file.id)"
                          >Yes
                          </v-btn>
                        </template>
                        첨부파일이 삭제되었습니다.
                      </v-snackbar>
                      <v-btn
                          text="No"
                          @click="attachedFileModal = false"
                      ></v-btn>
                    </v-card-actions>
                  </v-card>
                </v-dialog>
              </v-card>
              <v-btn
                  block
                  rounded="xl"
                  color="blue-lighten-1"
                  @click="fileDialogOpen"
                  variant="tonal">
                <v-icon
                    icon="mdi-plus"
                    size="x-large"
                ></v-icon>
                <v-tooltip
                    activator="parent"
                    location="start"
                >ALT + A
                </v-tooltip>
              </v-btn>
            </v-card-text>
          </v-card>
        </div>

        <!--    ⏩⏩⏩⏩⏩  해시태그 -->
        <v-chip prepend-icon="mdi-pencil"
                color="blue"
                class="mx-4 mb-0 mt-5"
                @click="hashTagUpdateModalOpen"> 해시 태그 수정
          <v-tooltip
              activator="parent"
              location="start"
          > ALT + H
          </v-tooltip>
        </v-chip>
        <v-chip-group column class="px-4">
          <v-chip prepend-icon="mdi-refresh"
                  style="color: #4CAF50"
                  @click=documentList.setPublicDocumentList> 초기화
            <v-tooltip
                activator="parent"
                location="start"
            > ALT + R
            </v-tooltip>
          </v-chip>
          <v-chip
              v-for="(hashTag, index) in documentList.selectedDocumentDetails.hashTags"
              style="color: #546E7A"
              :key="index"
              @click="documentList.filterPublicDocByHashTag(hashTag['hashTagId'])">
            {{ hashTag.tagName }} ({{ hashTag['docsCounts'] }})
            <v-tooltip
                activator="parent"
                location="top"
            > 태그필터링
            </v-tooltip>
          </v-chip>
        </v-chip-group>

        <!--           ❤️ 해시태그 수정을 위한 모달-->
        <v-dialog
            class="d-flex justify-center"
            width="60vw"
            opacity="10%"
            v-model="hashTagUpdateModal">
          <v-sheet
              rounded="xl"
              class="d-flex justify-center flex-wrap pa-10">
            <v-combobox
                variant="underlined"
                v-model="hashTagsUpdateReqDto.hashTags"
                multiple
                placeholder="태그를 입력하세요."
                persistent-placeholder
                hint="여러 태그를 엔터로 구분하여 입력하세요.">
              <template v-slot:selection="data">
                <v-chip
                    class="pa-4 mr-1"
                    style="color: #FF5722"
                    :key="JSON.stringify(data.item)"
                    v-bind="data.attrs"
                    :disabled="data.disabled"
                    :model-value="data.selected"
                    size="large"
                    @click="documentList.filterTop100HashTagsByClick(data.item.title)">
                  {{ data.item.title }}
                  <v-tooltip
                      activator="parent"
                      location="top"
                  > 태그 검색
                  </v-tooltip>
                </v-chip>
              </template>
            </v-combobox>
            <h2 class="mt-5 mb-3" style="width:100%; display: flex; justify-content: center"> 🗼 Top 100 해시태그 🗼</h2>
            <v-chip-group column class="px-4 d-flex flex-wrap">
              <v-chip
                  prepend-icon="mdi-refresh"
                  style="color: #4CAF50"
                  @click="ResetHasTagAddAndFiltering"
              >
                초기화
                <v-tooltip
                    activator="parent"
                    location="start"
                > ALT + R
                </v-tooltip>
              </v-chip>
              <v-chip
                  v-for="(hashTag, index) in documentList.fillteredTop100HaahTag"
                  style="color: #546E7A"
                  :key="index"
                  @click="Top100HashTagAddAndFiltering(hashTag['hashTagId'], hashTag.tagName)">
                {{ hashTag.tagName }}
                <v-tooltip
                    activator="parent"
                    location="top"
                > 태그 추가
                </v-tooltip>
              </v-chip>
            </v-chip-group>
            <v-btn
                class="mt-6"
                :color="color.kipMainColor"
                text="수정 하기"
                @click="hashTagUpdateReq"
                block
            />
          </v-sheet>
        </v-dialog>
      </v-col>
    </v-row>
  </v-container>
</template>
<style>
.font-weight-bold {
  font-weight: bold;
}

.headline {
  font-size: 1.5rem;
  font-weight: bold;
}

.divider-container {
  min-height: calc(97vh - 1.6vw - 90px);
}

.show-btns {
  color: var(--primary-color) !important;
}

.fab_div {
  justify-content: flex-end;
  display: flex;
  align-items: flex-end;
  bottom: 4vh;
  z-index: 1004;
  transform: translateY(0%);
  position: fixed;
  height: 80px;
  left: -4vw;
  width: calc(100% + 0px);
}

.title__update input:focus {
  font-size: 30px;
  font-weight: bold;
  text-align: center;
  padding: 20px 20px 20px 30px;
  margin-bottom: 2px;
  color: white;
  background-color: var(--primary-color);
  border-radius: 25px;
}
</style>