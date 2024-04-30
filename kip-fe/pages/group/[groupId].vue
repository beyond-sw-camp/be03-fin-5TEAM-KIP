<script setup>
import {ref} from "vue";
import {toastViewerInstance} from "~/useToastViewer";
import draggable from "vuedraggable";

const color = useColor();
const route = useRoute();
const groupId = route.params.groupId;
const groupName = useGroup();
const documentList = useDocumentList();
const attachedFile = useAttachedFile();
const createDocument = useCreateDocument();
const postForm = ref();
const updateContent = ref();

const dialog = ref(false);
const upLinkId = ref();
const viewer = ref();
const titleEditing = ref(false);
const newTitle = ref();
const updateContentModal = ref(false);
const versionHistoryModal = ref(false);

// 드레그 관련 함수
const moveDocumentReq = ref({
  startDocId: "",
  endDocId: "",
})
const handleChange = async (event) => {
  if (event.moved.newIndex === 0 || event.moved.oldIndex === 0) {
    alert("최상단 문서의 순서는 변경할 수 없습니다.")
    await documentList.setDocumentList(groupId);
  } else {
    moveDocumentReq.value.startDocId = event.moved.element.documentId
    moveDocumentReq.value.endDocId = documentList.fillteredDocList[event.moved.newIndex - 1].documentId
    await documentList.moveDocumentToTargetDoc(moveDocumentReq.value)
  }
}

// 문서 타입을 드래그로 변경
const changeDocumentTypeByScroll = async (documentId) => {
  await documentList.ChangeDocumentType(documentId)
  await documentList.setDocumentList(groupId);
}

// 첨부파일 관련
const files = ref([]);
const fileHover = ref(null);
const fileDialog = ref(false);
const fileLoading = ref(false);

// 북마크 관련
const selection = ref([]);
const bookmarks = useBookMarks();

await documentList.setDocumentList(groupId);
await groupName.setSelectedGroupInfo(groupId)
await documentList.setFirstDocumentDetails()

await attachedFile.setAttachedFileList(documentList.getFirstDocId);

groupName.setTopNaviGroupList(groupId);

const openCreateNewDocument = (docId) => {
  upLinkId.value = docId;
  dialog.value = true;
  console.log(upLinkId.value)
};

const handleData = async (form) => {
  form.groupId = groupId;
  form.upLinkId = upLinkId.value

  const temp = await createDocument.createNewDocument(form)
  await documentList.setDocumentList(groupName.getSelectedGroupInfo[0].groupId);
  await selectDocument(temp.documentId);
  dialog.value = false;
};
const createNewVersion = async (form) => {
  await documentList.updateVersion(documentList.selectedDocumentDetails.documentId, form.value.content, form.value.message);
  viewer.value = toastViewerInstance(
      viewer.value,
      documentList.selectedDocumentDetails.content
  );
  updateContentModal.value = false;
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
    bookmarks.myBookMarks.some(book => book.documentId === documentList.selectedDocumentDetails.documentId)
);

// 북마크 버튼 클릭 핸들러
const handleBookmarkClick = async () => {
  // 만약 현재 문서가 북마크되어 있다면, 북마크를 제거하는 액션을 실행합니다.
  if (isBookmarked.value) {
    await bookmarks.removeMyBookmark(documentList.selectedDocumentDetails.documentId);
  } else {
    await bookmarks.removeMyBookmark(documentList.selectedDocumentDetails.documentId);
  }

  // 북마크 상태를 갱신합니다.
  await bookmarks.setMyBookMarks();
};

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

const updateDocumentTitle = async () => {
  titleEditing.value = false
  documentList.selectedDocumentDetails.title = newTitle.value
  await documentList.updateDocumentTitle(documentList.selectedDocumentDetails.documentId, documentList.selectedDocumentDetails.title)
  await documentList.setDocumentList(groupName.getSelectedGroupInfo[0].groupId);
  newTitle.value =  ""
}
const closeVersionHistory = async () => {
  await documentList.setDocumentDetails(documentList.selectedDocumentDetails.documentId);
  viewer.value = toastViewerInstance(
      viewer.value,
      documentList.selectedDocumentDetails.content
  );
  versionHistoryModal.value = false
}


</script>

<template>
  <v-container fluid>
    <v-row no-gutters>


      <!-- 👈👈👈👈👈👈👈👈 왼쪽 사이드바 -->
      <v-col cols="3">
        <v-list class="pa-4">
          <v-list-item>
            <v-list-item-title class="font-weight-bold headline text-center mb-4 pa-2">
              {{ groupName.getSelectedGroupInfo[0].groupName }}
              {{ `${groupName.getSelectedGroupInfo[0].groupType === 'DEPARTMENT' ? '🏢' : '🚀'}` }}
            </v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>

          <!-- 그룹 문서 title 출력 -->
          <v-tabs color="primary" direction="vertical" class="mt-4">

            <draggable
                v-model="documentList.fillteredDocList"
                group="groupDocs"
                :animation="1000"
                item-key="documentId"
                @change="handleChange"
            >

              <template #item="{ element: doc }">
                <v-card variant="text">
                  <v-row>
                    <v-col cols="10" >
                      <v-tab
                          width="100%"
                          @click="selectDocument(doc.documentId)"
                          :value="doc.documentId"
                          @contextmenu.prevent="changeDocumentTypeByScroll(doc.documentId)">
                        <h3
                            v-if="doc.docType === 'SECTION'"
                            class="ellipsis"
                            style="width:17vw; text-align: start"
                        > 🔹️ {{ doc.title }}</h3>
                        <div
                            v-else
                            class="ellipsis"
                            style="width:17vw; text-align: start"
                        >
                          {{ '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' }} {{ doc.title }} {{ doc.documentId }}
                        </div>
                      </v-tab>
                    </v-col>
                    <v-col cols="2" class="d-flex align-center px-0">
                      <v-hover v-slot="{ isHovering, props }">
                        <v-btn
                            icon="mdi-plus"
                            v-bind="props"
                            :class="{
                            'on-hover': isHovering,
                            'show-btns': isHovering
                          }"
                            color="rgba(255, 255, 255, 0)"
                            variant="tonal"
                            @click.stop="openCreateNewDocument(doc.documentId)"
                        />
                      </v-hover>
                    </v-col>
                  </v-row>
                </v-card>
              </template>
            </draggable>
          </v-tabs>

        </v-list>
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

      <!-- 세로선 -->
      <v-divider class="divider-container" vertical></v-divider>

      <!-- ☝️☝️☝️☝️☝️☝️☝️ 가운데 문서제목 부분 -->
      <v-col cols="7" class="position-relative">
        <v-list class="pa-4">
          <v-card flat>
            <div class="d-flex justify-center">
              <v-card-title v-if="titleEditing" class="headline text-center">
                <v-text-field
                    v-model="newTitle"
                    @blur="titleEditing = false"
                    @keyup.enter="updateDocumentTitle"
                    autofocus
                    persistent-placeholder
                    persistent-hint
                    append-inner-icon="mdi-keyboard-return"
                    hint="변경할 제목을 입력하시고 엔터를 입력하세요."
                    placeholder="변경할 제목을 입력하세요."
                    style="min-width: 300px;"
                    variant="underlined"
                ></v-text-field>
              </v-card-title>

              <!-- 제목 표시 -->
              <v-card-title v-else class="headline text-centermb-4 pa-2 mb-4">
                {{ documentList.selectedDocumentDetails.title }}
              </v-card-title>

              <v-item-group v-model="selection">
                <v-item>
                  <v-btn
                      density="comfortable"
                      @click="handleBookmarkClick"
                      :icon="isBookmarked ? 'mdi-star' : 'mdi-star-outline'"
                  ></v-btn>
                </v-item>
              </v-item-group>
            </div>
          </v-card>
          <!-- 가로 선 추가 -->
          <v-divider></v-divider>
        </v-list>

        <v-card flat class="px-6 mt-4 mx-auto">
          <div ref="viewer">{{ documentList.selectedDocumentDetails.content }}</div>
        </v-card>
        <div class="fab_div">
          <v-container class="d-flex justify-end" style="margin: 30px;">
            <v-speed-dial location="top center" transition="fade-transition">
              <template v-slot:activator="{ props: activatorProps }">
                <v-btn
                    rounded="circle"
                    v-bind="activatorProps"
                    size="large"
                    stacked
                >
                  <v-img
                      width="36px"
                      src="/images/logos/kiplogo.svg"/>
                </v-btn>
              </template>

              <v-btn key="1" size="large" prepend-icon="mdi-format-title" @click="titleEditing = true">제목 수정</v-btn>
              <v-btn key="2" size="large" prepend-icon="mdi-pencil" @click="updateContentModal=true">내용 수정</v-btn>
              <v-btn key="3" size="large" prepend-icon="mdi-history" @click="versionHistoryModal=true">수정 이력</v-btn>
              <v-btn key="4" size="large" v-if="isBookmarked" prepend-icon="mdi-star" @click="handleBookmarkClick">북마크 해제</v-btn>
              <v-btn key="5" size="large" v-else prepend-icon="mdi-star-outline" @click="handleBookmarkClick">북마크 추가</v-btn>

            </v-speed-dial>
          </v-container>
        </div>
        <v-dialog v-model="updateContentModal" fullscreen>
          <v-card>
            <UpdateContent ref="updateContent" @submit="createNewVersion" :dataToPass="documentList.selectedDocumentDetails.content"></UpdateContent>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn  @click=updateContent.submit()>작성 완료</v-btn>
              <v-btn  @click="updateContentModal = false">닫기</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-dialog v-model="versionHistoryModal">
          <v-card>
            <VersionHistory :selectDocumentId="documentList.selectedDocumentDetails.documentId"></VersionHistory>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn  @click="closeVersionHistory">닫기</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-col>

      <!-- 👉👉👉👉👉👉👉👉👉 오른쪽 영역 -->
      <v-divider class="divider-container" vertical></v-divider>

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
                  @click=documentList.setDocumentList(groupName.getSelectedGroupInfo[0].groupId)> 초기화
          </v-chip>
          <v-chip
              v-for="(hashTag, index) in documentList.selectedDocumentDetails.hashTags"
              :key="index"
              prepend-icon="mdi-pound"
              @click="documentList.filterGroupDocByHashTag(hashTag['hashTagId'])">
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

.fab_div {
  justify-content: flex-end;
  display: flex;
  align-items: flex-end;
  bottom: 0px;
  z-index: 1004;
  transform: translateY(0%);
  position: fixed;
  height: 80px;
  left: 0px;
  width: calc(100% + 0px);
}

.show-btns {
  color: var(--primary-color) !important;
}

.sortable-ghost {
  background-color: rgba(0, 51, 255, 0.27);
}


</style>