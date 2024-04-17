<script setup>

const color = useColor();
const route = useRoute()
const groupId = route.params.groupId;
const groupName = useGroup();
const documentList = useDocumentList();
const firstDocumentTitle = computed(() => documentList.getFirstDocumentTitle);

const selectedDocumentTitle = ref('');

await documentList.$reset();
await documentList.setDocumentList(groupId);
await groupName.setGroupUsersInfo(groupId);

// // 선택된 문서 제목을 첫 번째 문서 제목으로 초기화합니다.
// documentList.selectedDocumentDetails.title = firstDocumentTitle.value;

// 선택된 문서 제목을 첫 번째 문서 제목으로 초기화합니다.
// selectedDocumentTitle.value = firstDocumentTitle.value;

// 문서 선택 시 상세 정보를 가져오는 함수
const selectDocument = async (documentId) => {
  // 문서의 상세 정보를 가져옴
  await documentList.setDocumentDetails(documentId);
  // selectedDocumentTitle.value = documentList.selectedDocumentDetails.value ? documentList.selectedDocumentDetails.value.title : 'Document title unavailable';

};

// documentList.selectedDocumentDetails.title

</script>

<template>
  <v-container fluid>
    <v-row no-gutters>
      <!-- 왼쪽 사이드바 -->
      <v-col cols="3">
        <v-list class="pa-4">
          <v-list-item>
            <v-list-item-content>
              <v-list-item-title class="font-weight-bold headline text-center"
                                 v-for="group in groupName"
                                 :key="group.groupId"
              >
                {{group.groupName}}
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-divider></v-divider>

          <!-- 그룹 문서 title 출력 -->
          <v-tabs
              color="primary"
              direction="vertical"
          >
            <v-tab
                v-for="doc in documentList.getDocumentList"
                :key="doc.documentId"
                @click="selectDocument(doc.documentId)"
            >
              {{ doc.title }}
            </v-tab>
          </v-tabs>
        </v-list>
      </v-col>

      <!-- 세로선 -->
      <v-col cols="1" class="divider-container">
        <v-divider vertical></v-divider>
      </v-col>

      <!-- 가운데 문서내용 부분 -->
      <v-col cols="5" class="text-center">
        <v-card flat>
          <v-card-title class="headline my-2">
            <!-- 초기데이터 세팅 및 사이드바 문서 선택 시 해당문서 title 출력 -->
            {{ documentList.selectedDocumentDetails ? documentList.selectedDocumentDetails.title : firstDocumentTitle }}
          </v-card-title>
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