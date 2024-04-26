<script setup>
import {ref} from "vue";
import {toastViewerInstance} from "~/useToastViewer";

const color = useColor();
const route = useRoute();
const groupId = route.params.groupId;
const groupName = useGroup();
const documentList = useDocumentList();
const attachedFile = useAttachedFile();
const createDocument = useCreateDocument();
const postForm = ref();
const hover = ref(null);
const dialog = ref(false);
const upLinkId = ref();
const viewer = ref();

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
// 문서 제목 업데이트 관련
const titleLoding = ref(false)
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
    await documentList.setDocumentList(groupName.getSelectedGroupInfo[0].groupId);
    await documentList.setDocumentDetails(
        updateDocumentTitleReq.value.targetDocumentId)
    handlerForUpdateModal.value = false
  }
  titleLoding.value = false
}

</script>

<template>
  <v-container fluid>
    <v-row no-gutters>

      <!-- 👈👈👈👈👈👈👈👈 왼쪽 사이드바 -->
      <v-col cols="3">
        <v-list class="pa-4">
          <v-list-item>
            <v-list-item-title class="font-weight-bold headline text-center mt-2 mb-6">
              {{ groupName.getSelectedGroupInfo[0].groupName }}
              {{ `${groupName.getSelectedGroupInfo[0].groupType === 'DEPARTMENT' ? '🏢' : '🚀'}` }}
            </v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>
          <!-- 그룹 문서 title 출력 -->

          <v-tabs color="primary" direction="vertical" class="mt-4">
            <v-tab
                v-for="doc in documentList.getDocumentList"
                :key="doc.documentId"
                @click="selectDocument(doc.documentId)"
                @mouseenter="hover = doc.documentId"
                @mouseleave="hover = null"
            >

              <h3 v-if="doc.docType === 'SECTION'">🔹️ {{ doc.title }} </h3>
              <div v-else>
                  {{ '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' }} {{ doc.title }}
              </div>

              <template v-if="hover === doc.documentId" v-slot:append>
                <v-btn
                    :icon="`mdi-plus`"
                    variant="text"
                    density="compact"
                    rounded="lg"
                    @click.stop="openCreateNewDocument(doc.documentId)"
                />
              </template>
            </v-tab>
          </v-tabs>
        </v-list>
        <v-dialog v-model="dialog" fullscreen>
          <v-card>
            <PostForm ref="postForm" @submit="handleData"></PostForm>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn  @click=postForm.submit()>작성 완료</v-btn>
              <v-btn  @click="dialog = false">닫기</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-col>

      <!-- 세로선 -->
      <v-divider class="divider-container" vertical></v-divider>

      <!-- ☝️☝️☝️☝️☝️☝️☝️ 가운데 문서제목 부분 -->
      <v-col cols="7">

        <v-list class="pa-4 mb-4" >
          <v-card flat>
            <v-row>
              <v-col cols="8" offset="2">
                <div class="d-flex justify-center">
                  <v-card-title class="headline text-center mb-4">
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
</style>