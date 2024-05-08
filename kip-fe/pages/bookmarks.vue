<script setup>
import {toastViewerInstance} from "~/useToastViewer";
import {ref} from "vue";

const route = useRoute();
const groupId = route.params.groupId;
const groupName = useGroup();
groupName.TopNaviGroupList = ["Knowledge is Power", "즐겨찾기 ⭐"];

const documentList = useDocumentList();
const attachedFile = useAttachedFile();
const toastViewer = ref();
const bookmarks = useBookMarks();


const UpdateToastViewer = async () => {
  toastViewerInstance(
      toastViewer.value,
      documentList.getSelectedDocContent
  );
}

onMounted(async () => {
  await bookmarks.setMyBookMarks();
  await documentList.setFirstBookDetails()
  await UpdateToastViewer();
  if (bookmarks.myBookMarks.length > 0)
    await attachedFile.setAttachedFileList(bookmarks.myBookMarks[0].documentId);
  groupName.setTopNaviGroupList(groupId);

})

groupName.setTopNaviGroupList(groupId);

// 문서 선택 시 상세 정보를 가져오는 함수
const selectDocument = async (documentId) => {
  await documentList.setDocumentDetails(documentId);
  await attachedFile.setAttachedFileList(documentId);
  await UpdateToastViewer();
};

const ResetHasTagAddAndFiltering = async () => {
  await bookmarks.setMyBookMarks();
}

// 파일 클릭 핸들러
const handleFileClick = (url) => {
  window.open(url, '_blank');
};

// 선택한 문서 ID가 북마크 목록에 있는지 확인
const isBookmarked = computed(() =>
    bookmarks.myBookMarks.some(book => book.documentId === documentList.selectedDocumentDetails.documentId)
);

const handleBookmarkClick = async () => {
  // 만약 현재 문서가 북마크되어 있다면, 북마크를 제거하는 액션을 실행합니다.
  if (isBookmarked.value) {
    await bookmarks.removeMyBookmark(documentList.selectedDocumentDetails.documentId);
  } else {
    await bookmarks.removeMyBookmark(documentList.selectedDocumentDetails.documentId);
  }

  // 북마크 상태를 갱신합니다.
  await bookmarks.setMyBookMarks();
};


// 단축키
import {onKeyStroke} from '@vueuse/core'
import {useKeyModifier} from '@vueuse/core'
const alt = useKeyModifier('Alt')
onKeyStroke(['M', 'm'], () => {
  if (alt.value) handleBookmarkClick()
})
onKeyStroke(['R', 'r'], () => {
  if (alt.value) ResetHasTagAddAndFiltering();
})



</script>

<template>
  <v-container fluid>
    <v-row no-gutters>
      <!-- 👈👈👈👈👈👈👈👈 왼쪽 사이드바 -->
      <v-col cols="3">
        <v-list class="pa-4">
          <v-list-item>
            <v-list-item-title class="font-weight-bold headline text-center mb-4 pa-2">
              북마크 ⭐
            </v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>
          <!-- 그룹 문서 title 출력 -->
          <v-tabs color="primary" direction="vertical" class="mt-4">
            <v-tab
                v-for="book in bookmarks.getMyBookMarksDetails"
                :key="book.title"
                @click="selectDocument(book.documentId)">
              ⭐ &nbsp; <strong> [{{ book.groupName }}] </strong> &nbsp; {{ book.title }}
            </v-tab>
          </v-tabs>
        </v-list>
      </v-col>

      <!-- 세로선 -->
      <v-divider class="divider-container" vertical></v-divider>

      <!-- ☝️☝️☝️☝️☝️☝️☝️ 가운데 문서제목 부분 -->
      <v-col cols="7" class="position-relative">
        <v-list class="pa-4">
          <v-card flat>
            <div class="d-flex justify-center">
              <!-- 제목 표시 -->
              <v-card-title
                  v-if="bookmarks.myBookMarks.length > 0"
                  class="headline text-center mb-4 pa-2">
                {{ documentList.getSelectedDocTitle }}
              </v-card-title>

              <v-card-title v-else class="headline text-center pa-2 mb-4">
                북마크를 한 문서가 없습니다.
              </v-card-title>
              <v-btn
                  class=" pt-1 pb-6 px-0"
                  variant="text"
                  rounded="xl"
                  @click="handleBookmarkClick">
                <v-icon
                    color="yellow-darken-2"
                    size="xx-large"
                    :icon="isBookmarked ? 'mdi-star' : 'mdi-star-outline'"
                />
                <v-tooltip
                    activator="parent"
                    location="end"
                >ALT + M
                </v-tooltip>
              </v-btn>
            </div>
          </v-card>

          <!-- 가로 선 추가 -->
          <v-divider></v-divider>
        </v-list>
        <v-card v-if="bookmarks.myBookMarks.length > 0" flat class="px-6 mt-4 mx-auto">
          <div ref="toastViewer">{{ documentList.getSelectedDocContent }}</div>
        </v-card>

      </v-col>

      <v-divider class="divider-container" vertical></v-divider>

      <!-- 👉👉👉👉👉👉👉👉👉 오른쪽 영역 -->
      <v-col cols="2">
        <!-- 첨부 파일 섹션 -->
        <div class="attached-files">
          <v-card flat>
            <v-card-title class="headline text-center">첨부 파일 ⭐
                                                       <!-- 첨부파일 업로드 로직 부분 -->
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
                  class="mt-3"
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
                  @click="ResetHasTagAddAndFiltering"> 새로고침
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