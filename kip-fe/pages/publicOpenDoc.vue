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
const dialog = ref(false);
const viewer = ref();
const upLinkId = ref();


const color = useColor();
const documentList = useDocumentList();

await documentList.$reset();
await documentList.setPublicDocumentList();
await documentList.setFirstPublicDocumentDetails();

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
  dialog.value = false;
  await documentList.$reset();
  await documentList.setPublicDocumentList();
  await selectDocument(temp.documentId);
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
              <span>{{ doc.title }} / {{ doc.documentId }}</span>

            </v-tab>
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

      <v-divider class="divider-container" vertical/>

      <!-- ☝️☝️☝️☝️☝️☝️☝️ 가운데 문서제목 부분 -->
      <v-col cols="7" >
        <v-list class="pa-4 mb-4">
          <v-card flat>
            <v-card-title class="headline text-center" >
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

        <div class="pa-4">
          <v-responsive>
            <v-chip-group column>
              <v-chip
                  v-for="(hashTag, index) in documentList.selectedDocumentDetails.hashTags"
                  :key="index"
                  @click="documentList.filterPublicDocByHashTag(hashTag['hashTagId'])"
                  prepend-icon="mdi-pound"
                  v-if="documentList.selectedDocumentDetails && documentList.selectedDocumentDetails.hashTags.length > 0">
                {{ hashTag.tagName }} ({{ hashTag['docsCounts'] }}) {{hashTag['hashTagId']}}
              </v-chip>
              <div v-else>해시태그가 없습니다.</div>
            </v-chip-group>
          </v-responsive>
        </div>

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