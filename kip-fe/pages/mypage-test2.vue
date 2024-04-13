<script setup>
// import { ref } from 'vue';
// import { useBookMarks } from './bookmarks.js'; // 상대 경로 확인 필요

const bookmarks = useBookMarks();
const headers = ref([
  { text: '문서 제목', value: 'title' },
  { text: '그룹 이름', value: 'groupName' },
  { text: '작업', value: 'actions', sortable: false }
]);

await bookmarks.$reset();
await bookmarks.setMyBookMarks();

// 북마크 삭제 함수 (구현 필요)
function removeBookmark(documentId) {
  // 여기에 북마크 삭제 로직을 구현하십시오.
  console.log('Deleting bookmark with id:', documentId);
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
              <NuxtLink :to="`/user/book/${item.documentId}`">{{ item.title }}</NuxtLink>
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

