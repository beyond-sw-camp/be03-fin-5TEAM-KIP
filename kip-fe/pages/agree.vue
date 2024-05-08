<script setup>
import {toastViewerInstance} from "~/useToastViewer";
import {useAgreeDocument} from "~/stores/AgreeDocument.js";

const groupName = useGroup();
groupName.TopNaviGroupList = ["Knowledge is Power","접근이 허용된 문서 🔯"];

const documentList = useDocumentList();
const attachedFile = useAttachedFile();
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
  await documentList.setDocumentDetails(documentId);
  await attachedFile.setAttachedFileList(documentId);
  await UpdateToastViewer();
};

const ResetHasTagAddAndFiltering = async () => {
  await agreeDocuments.setMyDocument();
}

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
            <v-list-item-title class="font-weight-bold headline text-center mb-4 pa-2">
              승인문서 🔯
            </v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>
          <!-- 그룹 문서 title 출력 -->
          <v-tabs color="primary" direction="vertical" class="mt-4">
            <v-tab
                v-for="document in agreeDocuments.getMyBookMarksDetails"
                :key="document.title"
                @click="selectDocument(document.documentId)">
              🔯 &nbsp; <strong>[{{document.groupName}}]</strong> &nbsp; {{ document.title }}
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
              <!-- 제목 표시 -->
              <v-card-title
                  v-if="agreeDocuments.document.length > 0"
                  class="headline text-center mb-4 pa-2">
                {{ documentList.getSelectedDocTitle }} 🔯
              </v-card-title>
              <v-card-title v-else class="headline text-center pa-2 mb-4">
                허용된 문서가 없습니다.
              </v-card-title>
            </div>
          </v-card>
          <!-- 가로 선 추가 -->
          <v-divider></v-divider>
        </v-list>
        <v-card v-if="agreeDocuments.document.length > 0" flat class="px-6 mt-4 mx-auto">
          <div ref="toastViewer">{{ documentList.getSelectedDocContent }}</div>
        </v-card>
      </v-col>
      <v-divider class="divider-container" vertical></v-divider>
      <!-- 👉👉👉👉👉👉👉👉👉 오른쪽 영역 -->
      <v-col cols="2">
        <!-- 첨부 파일 섹션 -->
        <div class="attached-files">
          <v-card flat>
            <v-card-title class="headline text-center">첨부 파일 🔯
            </v-card-title>
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
                  <v-col cols="8" class="d-flex justify-start align-center py-2" style="width: 70%">
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
                </v-row>
              </v-card>
              <v-btn
                  block
                  class="mt-4"
                  rounded="xl"
                  color="blue-lighten-1"
                  variant="tonal">
                <v-icon
                    icon="mdi-block-helper"
                    size="x-large"
                ></v-icon>
                <v-tooltip
                    activator="parent"
                    location="start"
                >읽기전용
                </v-tooltip>
              </v-btn>
            </v-card-text>
          </v-card>
        </div>
        <!--    ⏩⏩⏩⏩⏩  해시태그 -->
        <v-chip-group column class="px-4 mt-5">
          <v-chip prepend-icon="mdi-refresh"
                  style="color: #4CAF50"
                  @click=ResetHasTagAddAndFiltering> 새로고침
            <v-tooltip
                activator="parent"
                location="start"
            > ALT + R
            </v-tooltip>
          </v-chip>
          <v-chip
              v-for="(hashTag, index) in documentList.selectedDocumentDetails.hashTags"
              style="color: #546E7A"
              :key="index">
            {{ hashTag.tagName }} ({{ hashTag['docsCounts'] }})
            <v-tooltip
                activator="parent"
                location="start"
            > 읽기전용
            </v-tooltip>
          </v-chip>
        </v-chip-group>
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