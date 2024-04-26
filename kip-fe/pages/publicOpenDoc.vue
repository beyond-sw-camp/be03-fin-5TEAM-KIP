<script setup>

import {ref} from "vue";
import {toastViewerInstance} from "~/useToastViewer";
import postForm from "~/components/PostForm.vue";
import {VTreeview} from 'vuetify/labs/VTreeview'

// 상단 네비 제목 설정
const group = useGroup();
group.TopNaviGroupList = ["Knowledge is Power", "전체공개문서", "해시태그로 검색해 주세요.🏷️"];

// 피니아.
const createDocument = useCreateDocument();
const documentList = useDocumentList();
const color = useColor();
const attachedFile = useAttachedFile();

const loading = ref(false);
const titleLoding = ref(false)
const snackbar = ref(false);
const dialog = ref(false);
const viewer = ref();
const upLinkId = ref();

// 첨부파일 관련
const files = ref([]);
const fileHover = ref(null);
const fileDialog = ref(false);
const fileLoading = ref(false);

// 북마크 관련
const selection = ref([]);
const bookmarks = useBookMarks();

await bookmarks.setMyBookMarks();

// 초기 문서 세팅
await documentList.setPublicDocumentList();
await documentList.setFirstPublicDocumentDetails();

// 첨부 파일
await attachedFile.setAttachedFileList(documentList.getFirstPublicDocId);

// 해시태그 업데이트 관련
const hashTagUpdateModal = ref(false);
const hashTagsUpdateReqDto = ref({
  documentId: "",
  hashTags: []
});
const hashTagUpdateModalOpen = () => {
  hashTagUpdateModal.value = true
  hashTagsUpdateReqDto.value.documentId = documentList.getSelectedDocId
  hashTagsUpdateReqDto.value.hashTags = documentList.getHashTagsInSelectedDoc
}
const hashTagUpdateReq = () => {
  documentList.updateHashTags(hashTagsUpdateReqDto.value)
  hashTagUpdateModal.value = false;
}

await documentList.setPublicDocumentList();
await documentList.setFirstPublicDocumentDetails();

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
const handlerForUpdateModal = ref(false);
const updateDocumentTitleReq = ref({
  targetDocumentId: "",
  newTitle: ""
})
const OpenTitleUpdateModal = () => {
  handlerForUpdateModal.value = true
  updateDocumentTitleReq.value.targetDocumentId = documentList.getSelectedDocId
  updateDocumentTitleReq.value.newTitle = documentList.getSelectedDocTitle
}
const realUpdateDocumentTitle = async (event) => {
  titleLoding.value = true
  const results = await event
  await wait(500); // 0.5초 대기

  if (results.valid) {
    await documentList.updateDocumentTitle(updateDocumentTitleReq.value)
    await documentList.setPublicDocumentList();
    await documentList.setDocumentDetails(
        updateDocumentTitleReq.value.targetDocumentId)
    handlerForUpdateModal.value = false
  }
  titleLoding.value = false
}

// 문서 선택 시 상세 정보를 가져오는 함수
const selectDocument = async (documentId) => {
  // 문서의 상세 정보를 가져옴
  await documentList.setDocumentDetails(documentId);
  await attachedFile.setAttachedFileList(documentId);
  viewer.value = toastViewerInstance(
      viewer.value,
      documentList.selectedDocumentDetails.content
  );
};

// 에디터 관련 코드.
const openCreateNewDocument = () => {
  upLinkId.value = null;
  dialog.value = true;
}
const handleData = async (form) => {
  form.groupId = null;
  form.upLinkId = null;
  const temp = await createDocument.createNewDocument(form)
  await documentList.setPublicDocumentList();
  await selectDocument(temp.documentId);
  dialog.value = false;
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

// 파일 업로드 핸들러
const handleFileUpload = async () => {
  fileLoading.value = true; // 빙글이 시작
  await wait(1200); // 1.2초 대기

  // 각 파일에 대해 업로드 로직 실행
  for (let file of files.value) {
    console.log(file)
    await attachedFile.setAttachedFileUpload(documentList.selectedDocumentDetails.documentId, file);
  }
  files.value = []; // 파일 목록 초기화

  // 파일 업로드 후 첨부파일 목록 다시 불러오기
  await attachedFile.setAttachedFileList(documentList.selectedDocumentDetails.documentId);

  fileLoading.value = false; // 빙글이 끝내기
  fileDialog.value = false; // 다이얼로그 닫기
};

// 파일 클릭 핸들러
const handleFileClick = (url) => {
  window.open(url, '_blank');
};

// 첨부파일 삭제 로직
const AttachedFileDelete = async (fileId) => {
  await attachedFile.setAttachedFileDelete(fileId);
  await wait(2000); // 1.2초 대기
  // 첨부파일 삭제 후 첨부파일 목록 다시 불러오기
  await attachedFile.setAttachedFileList(documentList.selectedDocumentDetails.documentId);
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

</script>

<template>
  <v-container fluid>
    <v-row no-gutters>

      <!-- 👈👈👈👈👈👈👈👈 왼쪽 사이드바 -->
      <v-col cols="3">
        <v-list class="pa-4">
          <v-list-item>
            <v-list-item-title class="font-weight-bold headline text-center mb-4 mb-6">
              전체공개문서
              <v-btn
                  :icon="`mdi-plus`"
                  variant="elevated"
                  rounded="lg"
                  class="mb-2 ml-2"
                  @click.stop="openCreateNewDocument"
              />
            </v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>

          <!-- 전체공개 문서 title 출력 -->
          <v-tabs color="primary" direction="vertical">
            <v-tab
                v-for="doc in documentList.getPublicDocumentList"
                :key="doc.documentId"
                @click="selectDocument(doc.documentId)">

              <div>{{ doc.title }} / {{ doc.documentId }}</div>
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

        <v-dialog v-model="dialog" fullscreen>
          <v-card>
            <PostForm ref="postForm" @submit="handleData"></PostForm>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn @click=postForm.submit()>작성 완료</v-btn>
              <v-btn @click="dialog = false">닫기</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-col>

      <v-divider class="divider-container" vertical/>

      <!-- ☝️☝️☝️☝️☝️☝️☝️ 가운데 문서제목 부분 -->
      <v-col cols="7">

        <v-list class="pa-4 mb-4">
          <v-card flat>
            <v-row>
              <v-col cols="8" offset="2">
                <v-card-title class="headline d-flex justify-center">
                  {{ documentList.selectedDocumentDetails.title }}
                  <v-item-group v-model="selection">
                    <v-item>
                      <v-btn
                          density="comfortable"
                          @click="handleBookmarkClick"
                          :icon="isBookmarked ? 'mdi-star' : 'mdi-star-outline'"
                      ></v-btn>
                    </v-item>
                  </v-item-group>
                </v-card-title>


              </v-col>
              <v-col cols="2">
                <v-btn
                    :icon="`mdi-pencil`"
                    variant="elevated"
                    rounded="lg"
                    class="mb-2 ml-2"
                    @click.stop="OpenTitleUpdateModal"
                />
              </v-col>
            </v-row>
          </v-card>

          <!--           📜 문서 제목수정을 위한 모달. -->
          <v-dialog
              class="d-flex justify-center"
              width="40vw"
              opacity="50%"
              v-model="handlerForUpdateModal">
            <v-sheet
                rounded="xl"
                class="d-flex justify-center flex-wrap pa-10">

              <v-form ref="form" style="width: 50vw" @submit.prevent="realUpdateDocumentTitle">
                <v-row>
                  <v-col>

                    <v-text-field
                        label="문서 제목 입력"
                        placeholder="변경할 문서명을 적어주세요."
                        v-model="updateDocumentTitleReq.newTitle"
                        :rules="[value => !!value || '이름 입력이 필요합니다.']"
                        clearable
                        required
                    />

                    <v-btn
                        class="mt-7"
                        :color="color.kipMainColor"
                        :loading="titleLoding"
                        text="문서 제목 변경"
                        type="submit"
                        block
                    />
                  </v-col>
                </v-row>
              </v-form>
            </v-sheet>
          </v-dialog>
          <!-- 가로 선 추가 -->
          <v-divider></v-divider>
        </v-list>

        <v-card flat class="px-6 mt-4 mx-auto">
          <div ref="viewer">{{ documentList.selectedDocumentDetails.content }}</div>
        </v-card>

      </v-col>

      <v-divider class="divider-container" vertical/>

      <!-- 👉👉👉👉👉👉👉👉👉 오른쪽 영역 -->
      <v-col cols="2">
        <!-- 첨부 파일 섹션 -->
        <div class="attached-files">
          <v-card flat>
            <v-card-title class="headline text-center">첨부 파일

              <!-- 첨부파일 업로드 로직 부분 -->
              <v-dialog
                  class="d-flex justify-center"
                  width="40vw"
                  opacity="50%"
                  v-model="fileDialog">
                <template v-slot:activator="{ props: activatorProps }">
                  <v-btn
                      class="mb-2 ml-2"
                      v-bind="activatorProps"
                      density="compact"
                      variant="flat"
                      icon="mdi-plus"
                  >
                  </v-btn>
                </template>

                <v-sheet
                    rounded="xl"
                    class="d-flex justify-center flex-wrap pa-10">

                  <v-form ref="form" style="width: 50vw">
                    <v-file-input
                        v-model="files"
                        label="Select files"
                        placeholder="Upload your documents"
                        prepend-icon="mdi-paperclip"
                        multiple
                    >
                      <template v-slot:selection="{ fileNames }">
                        <template v-for="fileName in fileNames" :key="fileName">
                          <v-chip class="me-2" color="primary" size="small" label>
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
            </v-card-title>


            <!-- 첨부파일 목록 -->
            <v-card-text>
              <div v-if="attachedFile.getAttachedFileList.length > 0">
                <v-btn text color="primary"
                       v-for="file in attachedFile.getAttachedFileList"
                       :key="file.fileName"
                       @click="handleFileClick(file.fileUrl)"
                       @mouseenter="fileHover = file.fileName"
                       @mouseleave="fileHover = null">

                  {{ file.fileName }}

                  <v-dialog max-width="500">
                    <template v-slot:activator="{ props: activatorProps }" v-if="fileHover === file.fileName">
                      <v-btn
                          v-bind="activatorProps"
                          :icon="`mdi-minus`"
                          variant="text"
                          density="compact"
                          rounded="lg"
                      />
                    </template>

                    <template v-slot:default="{ isActive }">
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
                              @click="isActive.value = false"
                          ></v-btn>
                        </v-card-actions>
                      </v-card>
                    </template>

                  </v-dialog>
                </v-btn>
              </div>
              <div v-else>첨부파일이 없습니다.</div>
            </v-card-text>
          </v-card>
        </div>

        <!--    ⏩⏩⏩⏩⏩  해시태그 -->
        <v-chip prepend-icon="mdi-pencil"
                color="blue"
                class="mx-4 mb-0 mt-5"
                @click="hashTagUpdateModalOpen"> 해시 태그 수정
        </v-chip>

        <v-chip-group column class="px-4"
                      v-if="documentList.selectedDocumentDetails
                      && documentList.selectedDocumentDetails.hashTags.length > 0">
          <v-chip prepend-icon="mdi-refresh"
                  @click=documentList.setPublicDocumentList> 초기화
          </v-chip>
          <v-chip
              v-for="(hashTag, index) in documentList.selectedDocumentDetails.hashTags"
              :key="index"
              prepend-icon="mdi-pound"
              @click="documentList.filterPublicDocByHashTag(hashTag['hashTagId'])">
            {{ hashTag.tagName }} ({{ hashTag['docsCounts'] }})
          </v-chip>
        </v-chip-group>
        <div v-else class="pa-4">해시태그가 없습니다.</div>

      </v-col>

      <!--           ❤️ 해시태그 수정을 위한 모달-->
      <v-dialog
          class="d-flex justify-center"
          width="40vw"
          opacity="40%"
          v-model="hashTagUpdateModal">
        <v-sheet
            rounded="xl"
            class="d-flex justify-center flex-wrap pa-10">
          <v-combobox
              variant="underlined"
              v-model="hashTagsUpdateReqDto.hashTags"
              multiple
              chips
              placeholder="태그를 입력하세요."
              persistent-placeholder
              hint="여러 태그를 엔터로 구분하여 입력하세요."/>
          <v-btn
              class="mt-4"
              :color="color.kipMainColor"
              text="해시태그 수정하기"
              @click="hashTagUpdateReq"
              block
          />

        </v-sheet>
      </v-dialog>
    </v-row>
  </v-container>
</template>
<style scoped>
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
</style>