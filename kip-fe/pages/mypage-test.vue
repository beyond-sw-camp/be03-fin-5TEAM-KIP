<script setup>

const bookmarks = useBookMarks();

await bookmarks.$reset();
await bookmarks.setMyBookMarks();
console.log(bookmarks.getMyBookMarkTitlesAndId,"북마크 리스트")

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
<!--               BookMarks 내용 추가 -->
              <v-list dense v-for="book in bookmarks.getMyBookMarkTitlesAndId" :key="book.bookId">
                <NuxtLink :to="`/user/book/${book.bookId}`">
                  <v-list-item>
                    <v-list-item-content>
                      <v-list-item-title>{{ book.bookTitle }}</v-list-item-title>
                      <v-list-item-title>{{ book.bookTitle }}</v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                </NuxtLink>
              </v-list>
            </v-card>
          </v-col>
        </v-row>
  </v-container>
</template>

<style scoped>
.caption {
  text-align: center;
  color: rgba(0, 0, 0, 0.6);
}

.ma-2 {
  margin: 8px;
}

.v-avatar.my-4 {
  margin-top: 16px;
  margin-bottom: 16px;
}

.v-btn.ma-2 {
  margin-top: 8px;
  margin-bottom: 8px;
}

.v-card-actions.justify-end {
  justify-content: flex-end;
}

.v-card-text.flex-grow-1 {
  flex-grow: 1;
}

.v-container {
  max-width: 960px;
  margin: auto;
}

@media (min-width: 1264px) {
  .v-container {
    max-width: 90%;
  }
}

/* Make sure the buttons are aligned at the bottom of the cards */
.v-card-actions {
  margin-top: auto;
}

/* Adjust the card title size */
.v-card-title {
  font-size: 1.25rem;
  font-weight: bold;
}

/* Additional styles can be placed here */
</style>