<script setup>

import {ref} from "vue";
import {toastViewerInstance} from "~/useToastViewer";
import postForm from "~/components/PostForm.vue";

const group = useGroup();
// 상단 네비 제목 설정
group.TopNaviGroupList = ["Knowledge is Power", "전체공개문서", "해시태그로 검색해 주세요.🏷️"];

// 피니아.
const createDocument = useCreateDocument();


const hover = ref(null);
const loading = ref(false);
const snackbar = ref(false);
const dialog = ref(false);
const viewer = ref();
const upLinkId = ref();


// 해시태그 관련
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

const color = useColor();
const documentList = useDocumentList();

await documentList.$reset();
await documentList.setPublicDocumentList();
await documentList.setFirstPublicDocumentDetails();

// 문서 삭제 관련 코드.
const deleteDocModalOpen = ref();
const selectedDeleteDocTitle = ref();
const selectedDeleteDocId = ref();
const OpendeleteDocumentModal = async (documenetTitle, documentId) => {
  loading.value = false;
  deleteDocModalOpen.value = true;
  selectedDeleteDocTitle.value = documenetTitle;
  selectedDeleteDocId.value = documentId;
}
const realDeleteSelectedDoc = async () => {
  loading.value = true;
  await documentList.deleteDocument(selectedDeleteDocId.value)
  await documentList.setPublicDocumentList();
  await wait(800);
  deleteDocModalOpen.value = false;
  snackbar.value = true;
}


// 문서 선택 시 상세 정보를 가져오는 함수
const selectDocument = async (documentId) => {
  // 문서의 상세 정보를 가져옴
  await documentList.setDocumentDetails(documentId);
  viewer.value = toastViewerInstance(
      viewer.value,
      documentList.selectedDocumentDetails.content
  );
};

const openCreateNewDocument = () => {
  upLinkId.value = null;
  dialog.value = true;
}
const handleData = async (form) => {
  form.groupId = null;
  form.upLinkId = null;
  const temp = await createDocument.createNewDocument(form)
  await documentList.$reset();
  await documentList.setPublicDocumentList();
  dialog.value = false;
}
</script>

<template>
  <v-container fluid>
    <v-row no-gutters>

      <!-- 👈👈👈👈👈👈👈👈 왼쪽 사이드바 -->
      <v-col cols="3">
        <v-list class="pa-4">
          <v-list-item>
            <v-list-item-title class="font-weight-bold headline text-center mb-4">
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

                <!--                   ✏️ 삭제버튼 -->
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
                    @click="OpendeleteDocumentModal(doc.title, doc.documentId)"
                />
                <!--                  ➕ 생성버튼 -->
                <v-btn
                    icon="mdi-location-exit"
                    v-bind="props"
                    :class="{
                            'on-hover': isHovering,
                            'show-btns': isHovering
                          }"
                    color="rgba(255, 255, 255, 0)"
                    variant="plain"
                    @click="OpenCrateModal(item)"
                />
              </v-hover>
            </v-tab>
          </v-tabs>
        </v-list>

<!--        모달 세팅 -->
        <v-dialog
            v-model="deleteDocModalOpen"
            max-width="500">
          <v-card title="공개 문서 삭제">
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
            <v-card-title class="headline text-center">
              {{ documentList.selectedDocumentDetails.title }}
            </v-card-title>
          </v-card>

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
        <!-- 'On This Page' 섹션 -->
        <v-card flat>
          <v-card-title class="headline text-center">On This Page</v-card-title>
          <v-card-text>
            <v-list dense>
              <v-list-item v-for="item in rightSideItems" :key="item">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
                <v-list-item-subtitle v-if="item.subtitle">{{ item.subtitle }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>

        <!-- 첨부 파일 섹션 -->
        <v-card flat>
          <v-card-title class="headline text-center">첨부 파일</v-card-title>
          <v-card-text>
            <v-btn color="primary">service-task.pdf</v-btn>
            <v-btn color="primary">work-project.zip</v-btn>
            <!-- 더 많은 파일들... -->
          </v-card-text>
        </v-card>

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
              color="success"
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