<script setup>

const route = useRoute()
const groupId = route.params.groupId;
const documentList = useDocumentList();
const firstPublicDocumentTitle = computed(() => documentList.getFirstPublicDocumentTitle);

await documentList.$reset();
await documentList.setPublicDocumentList();

const selectDocument = (documentId) => {
  // TODO: 선택한 문서 ID를 사용하여 문서의 상세 정보를 가져오고 표시하는 로직을 구현
};
</script>

<template>
  <v-app>
    <v-container fluid>
      <v-row no-gutters>
        <!-- 왼쪽 사이드바 -->
        <v-col cols="3">
          <v-list dense class="pa-4">
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="font-weight-bold headline text-center">
                  전체공개문서
                </v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-divider></v-divider>

            <v-list-item
                v-for="doc in documentList.getPublicDocumentList"
                :key="doc.documentId"
                @click="selectDocument(doc.documentId)">
              <v-list-item-content>
                <v-list-item-title> {{ doc.title }} </v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-col>

        <!-- 세로선 -->
        <v-col cols="1" class="divider-container">
          <v-divider vertical></v-divider>
        </v-col>

        <!-- 가운데 문서내용 부분 -->
        <v-col cols="5" class="text-center">
          <v-card flat>
            <v-card-title class="headline my-2">{{ firstPublicDocumentTitle }}</v-card-title>
            <!-- 그룹 첫번째 문서의 제목 추가 -->
          </v-card>
          <v-divider></v-divider> <!-- 가로 선 추가 -->
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
            <v-card-title class="headline my-2 text-right">On This Page</v-card-title>
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
            <v-card-title class="headline my-2">첨부 파일</v-card-title>
            <v-card-text>
              <v-btn text color="primary">service-task.pdf</v-btn>
              <v-btn text color="primary">work-project.zip</v-btn>
              <!-- 더 많은 파일들... -->
            </v-card-text>
          </v-card>
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
  height: 100vh; /* 뷰포트의 전체 높이로 설정 */
  min-height: 600px; /* 최소 높이 설정, 필요에 따라 조정 */
}
</style>