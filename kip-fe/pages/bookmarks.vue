<script setup>

const bookmarks = useBookMarks();
const headers = ref([
  { text: '문서 제목', value: 'title' },
  { text: '그룹 이름', value: 'groupName' },
  { text: '작업', value: 'actions', sortable: false }
]);

await bookmarks.$reset();
await bookmarks.setMyBookMarks();

// 북마크 삭제 함수 구현
async function removeBookmark(documentId) {
  // 사용자 확인을 위한 추가적인 단계가 필요하면 여기에 추가
  const confirmed = window.confirm('북마크를 삭제하시겠습니까?');
  if (confirmed) {
    await bookmarks.removeMyBookmark(documentId);
  }
}
</script>

<template>
  <v-container fluid>
    <!-- 상단 탭 네비게이션 -->
    <v-row>
      <v-col>
        <v-tabs centered>
          <v-tab>Account</v-tab>
          <v-tab>BookMark</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-card outlined class="pa-4">
          <v-card-title class="text-h5">Your BookMarks</v-card-title>
          <!-- BookMarks 내용 추가, 스크롤 및 페이지네이션 유지 -->
          <!-- height : 스크롤을 위한 테이블 높이 설정 -->
          <v-data-table
              :headers="headers"
              :items="bookmarks.getMyBookMarksDetails"
              item-key="documentId"
              class="elevation-1"
              :height="500"
          >
            <template v-slot:item.title="{ item }">
              <NuxtLink :to="`/group/4`">{{ item.title }}</NuxtLink>
            </template>
            <template v-slot:item.groupName="{ item }">
              {{ item.groupName }}
            </template>
            <template v-slot:item.actions="{ item }">
              <v-icon small @click="removeBookmark(item.documentId)">
                mdi-delete
              </v-icon>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>


<style scoped>
.table-container {
  height: calc(100vh - 200px); /* 화면의 높이에서 200px을 제외한 높이로 설정 */
  overflow-y: auto; /* y축에 대해서만 스크롤 허용 */
}
</style>

