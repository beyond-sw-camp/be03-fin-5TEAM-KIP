<script setup>

const group = useGroup();
// 상단 네비 제목 설정
group.TopNaviGroupList = ["Knowledge is Power","전체공개문서","해시태그로 검색해 주세요.🏷️"];

const color = useColor();
const documentList = useDocumentList();

await documentList.$reset();
await documentList.setPublicDocumentList();
await documentList.setFirstPublicDocumentDetails();

// 문서 선택 시 상세 정보를 가져오는 함수
const selectDocument = async (documentId) => {
  // 문서의 상세 정보를 가져옴
  await documentList.setDocumentDetails(documentId);
};
</script>

<template>
  <v-app>
    <v-container fluid>
      <v-row no-gutters>
        <!-- 왼쪽 사이드바 -->
        <v-col cols="3">
          <v-list class="pa-4">
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="font-weight-bold headline text-center">
                  전체공개문서
                </v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-divider></v-divider>

            <!-- 그룹 문서 title 출력 -->
            <v-tabs color="primary" direction="vertical">
              <v-tab
                  v-for="doc in documentList.getPublicDocumentList"
                  :key="doc.documentId"
                  @click="selectDocument(doc.documentId)"
              >
                {{ doc.title }}
              </v-tab>
            </v-tabs>
          </v-list>
        </v-col>

        <!-- 세로선 -->
        <v-divider class="divider-container" vertical></v-divider>

        <!-- 가운데 문서제목 부분 -->
        <v-col class="text-center">
          <v-list class="pa-4">
            <v-card flat>
              <v-card-title class="headline">
                {{ documentList.selectedDocumentDetails.title }}
              </v-card-title>
            </v-card>
          <!-- 가로 선 추가 -->
          <v-divider></v-divider>
          </v-list>

          <v-card flat class="mt-4">
            <v-card-text>
              <!-- 문서의 내용 -->
              1231ed22222222222222222222222222222222
            </v-card-text>
          </v-card>
        </v-col>

        <!-- 오른쪽 영역 -->
        <v-col cols="2">
          <!-- 'On This Page' 섹션 -->
          <v-card flat>
            <v-card-title class="headline text-center">On This Page</v-card-title>
            <v-card-text>
              <v-list dense>
                <v-list-item v-for="item in rightSideItems" :key="item">
                  <v-list-item-content>
                    <v-list-item-title>{{ item.title }}</v-list-item-title>
                    <v-list-item-subtitle v-if="item.subtitle">{{ item.subtitle }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-card-text>
          </v-card>

          <!-- 첨부 파일 섹션 -->
          <v-card flat>
            <v-card-title class="headline text-center">첨부 파일</v-card-title>
            <v-card-text>
              <v-btn text color="primary">service-task.pdf</v-btn>
              <v-btn text color="primary">work-project.zip</v-btn>
              <!-- 더 많은 파일들... -->
            </v-card-text>
          </v-card>

          <div class="pa-4">
            <v-card-title class="headline text-center">해시 태그</v-card-title>
            <v-responsive>
              <v-chip-group column>
                <v-chip
                    v-for="(hashTag, index) in documentList.selectedDocumentDetails.hashTags"
                    :key="index"
                    prepend-icon="mdi mdi-pound"
                    v-if="documentList.selectedDocumentDetails && documentList.selectedDocumentDetails.hashTags.length > 0">
                  {{ hashTag.tagName }}
                </v-chip>
                <div v-else>해시태그가 없습니다.</div>
              </v-chip-group>
            </v-responsive>
          </div>

        </v-col>
      </v-row>
    </v-container>
  </v-app>
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
  height: 80vh; /* 뷰포트의 전체 높이로 설정 */
  min-height: 630px; /* 최소 높이 설정, 필요에 따라 조정 */
}
</style>