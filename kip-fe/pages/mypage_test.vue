<template>
  <v-row v-if="selectedTab === 1">
    <v-col cols="12">
      <v-card outlined class="pa-4">
        <v-card-title class="text-h5">Your BookMarks</v-card-title>
        <v-list>
          <v-list-item v-for="bookmark in bookmarks" :key="bookmark.id">
            <v-list-item-content>
              <v-list-item-title>{{ bookmark.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup>
import { onMounted, computed } from 'vue';
import { useBookMarks } from "~/stores/BookMarks.js"; // 스토어 파일 경로는 프로젝트 구성에 따라 다를 수 있습니다.

const store = useBookMarks();

// 컴포넌트가 마운트될 때 북마크 목록을 가져옴
onMounted(async () => {
  await store.list();
});

// 스토어에서 북마크 상태를 가져옴
const bookmarks = computed(() => store.bookmarks);
</script>
