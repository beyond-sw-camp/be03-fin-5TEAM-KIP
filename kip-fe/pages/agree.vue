<script setup>
import {toastViewerInstance} from "~/useToastViewer";
import {useAgreeDocument} from "~/stores/AgreeDocument.js";

const color = useColor();
const groupName = useGroup();
groupName.TopNaviGroupList = ["Knowledge is Power","허용된 문서"];

const documentList = useDocumentList();
const attachedFile = useAttachedFile();
const createDocument = useCreateDocument();
const hover = ref(null);
const toastViewer = ref();

// 첨부파일 관련
const fileHover = ref(null);

// 북마크 관련
const agreeDocuments = useAgreeDocument();

const UpdateToastViewer = async () => {
  toastViewerInstance(
      toastViewer.value,
      documentList.getSelectedDocContent
  );
}

onMounted(async () => {
  await agreeDocuments.setMyDocument();
  await documentList.setAgreeDocumentDetails()
  await UpdateToastViewer();
  if (agreeDocuments.document.length > 0) {
    await attachedFile.setAttachedFileList(agreeDocuments.document[0].documentId);
  }
})



// 문서 선택 시 상세 정보를 가져오는 함수
const selectDocument = async (documentId) => {
  // 문서의 상세 정보를 가져옴
  await documentList.setDocumentDetails(documentId);
  await attachedFile.setAttachedFileList(documentId);
  await UpdateToastViewer();
};

// 파일 클릭 핸들러
const handleFileClick = (url) => {
  window.open(url, '_blank');
};

</script>

<template>
  <v-container fluid>
    <v-row no-gutters>
      <!-- 👈👈👈👈👈👈👈👈 왼쪽 사이드바 -->
      <v-col cols="3">
        <v-list class="pa-4">
          <v-list-item>
            <v-list-item-title class="font-weight-bold headline text-center">
              문서 목록
            </v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>
          <!-- 그룹 문서 title 출력 -->
          <v-tabs color="primary" direction="vertical">
            <v-tab
                v-for="document in agreeDocuments.getMyBookMarksDetails"
                :key="document.title"
                @click="selectDocument(document.documentId)"
                @mouseenter="hover = document.documentId"
                @mouseleave="hover = null"
            >
              {{document.groupName}}.{{ document.title }}
            </v-tab>
          </v-tabs>
        </v-list>
      </v-col>

      <!-- 세로선 -->
      <v-divider class="divider-container" vertical></v-divider>

      <!-- ☝️☝️☝️☝️☝️☝️☝️ 가운데 문서제목 부분 -->
      <v-col cols="7">
        <v-list class="pa-4">
          <v-card flat>
            <div class="d-flex justify-center">
              <v-card-title v-if="agreeDocuments.document.length > 0" class="headline text-center">
                {{ documentList.getSelectedDocTitle }}
              </v-card-title>
              <v-card-title v-else class="headline text-center">
                허용된 문서가 존재 하지 않습니다.
              </v-card-title>
            </div>
          </v-card>
          <!-- 가로 선 추가 -->
          <v-divider></v-divider>
        </v-list>
        <v-card v-if="agreeDocuments.document.length > 0" flat class="mt-4 mx-auto" width="800">
          <div ref="toastViewer">{{ documentList.getSelectedDocContent }}</div>
        </v-card>
      </v-col>
      <v-divider class="divider-container" vertical></v-divider>
      <!-- 👉👉👉👉👉👉👉👉👉 오른쪽 영역 -->
      <v-col cols="2">
        <!-- 첨부 파일 섹션 -->
        <div class="attached-files">
          <v-card flat>
            <v-card-title class="headline text-center">첨부 파일
              <v-divider></v-divider>
            </v-card-title>
            <!-- 첨부파일 목록 -->
            <v-card-text>
              <v-btn text color="primary"
                     v-for="file in attachedFile.getAttachedFileList"
                     :key="file.fileName"
                     @click="handleFileClick(file.fileUrl)"
                     @mouseenter="fileHover = file.fileName"
                     @mouseleave="fileHover = null">
                {{ file.fileName }}
              </v-btn>
            </v-card-text>
          </v-card>
        </div>
        <v-divider></v-divider>
        <!--    ⏩⏩⏩⏩⏩  해시태그 -->
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