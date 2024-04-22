<script setup>
import {ref} from "vue";
import {toastViewerInstance} from "~/useToastViewer";

const color = useColor();
const route = useRoute();
const groupId = route.params.groupId;
const groupName = useGroup();
const documentList = useDocumentList();
const attachedFile = useAttachedFile();
const createDocument = useCreateDocument();
const postForm = ref();
const hover = ref(null);
const dialog = ref(false);
const upLinkId = ref();

const viewer = ref();

const files = ref([]);
const dialog = ref(false);
const hover = ref(null);

await documentList.$reset();
await documentList.setDocumentList(groupId);
await groupName.setGroupUsersInfo(groupId);
await documentList.setFirstDocumentDetails();

const openCreateNewDocument = (docId) => {
  upLinkId.value = docId;
  dialog.value = true;
  console.log(upLinkId.value)
}
const handleData = async (form) => {
  form.groupId = groupId;
  form.upLinkId = upLinkId.value

  const temp = await createDocument.createNewDocument(form)
  dialog.value = false;
  await documentList.$reset();
  await documentList.setDocumentList(groupId);
  await selectDocument(temp.documentId);
}

await attachedFile.$reset();
await attachedFile.setAttachedFileList(documentList.getFirstDocId);

groupName.setTopNaviGroupList(groupId);

// 문서 선택 시 상세 정보를 가져오는 함수
const selectDocument = async (documentId) => {
  // 문서의 상세 정보를 가져옴
  await documentList.setDocumentDetails(documentId);
  await attachedFile.setAttachedFileList(documentId);
  viewer.value = toastViewerInstance(
      viewer.value,
      documentList.selectedDocumentDetails.content
  );
};

// 파일 업로드 핸들러
const handleFileUpload = async () => {
  await wait(1200); // 1.2초 대기
  dialog.value = false; // 다이얼로그 닫기
  // 각 파일에 대해 업로드 로직 실행
  for (let file of files.value) {
    console.log(file)
    await attachedFile.setAttachedFileUpload(documentList.selectedDocumentDetails.documentId, file);
  }
  files.value = []; // 파일 목록 초기화

  // 파일 업로드 후 첨부파일 목록 다시 불러오기
  await attachedFile.setAttachedFileList(documentList.selectedDocumentDetails.documentId);
};

// 파일 클릭 핸들러
const handleFileClick = (url) => {
  window.open(url, '_blank');
};

// 첨부파일 삭제 로직
const AttachedFileDelete = async (fileId) => {
  await attachedFile.setAttachedFileDelete(fileId);
  await wait(2000); // 1.2초 대기
      // 첨부파일 삭제 후 첨부파일 목록 다시 불러오기
      await attachedFile.setAttachedFileList(documentList.selectedDocumentDetails.documentId);
};

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
          <v-tabs color="primary" direction="vertical">
            <v-tab
                v-for="doc in documentList.getDocumentList"
                :key="doc.documentId"
                @click="selectDocument(doc.documentId)"
                @mouseenter="hover = doc.documentId"
                @mouseleave="hover = null"
            >
              {{ doc.title }}
              <template v-if="hover === doc.documentId" v-slot:append>
                <v-btn
                    :icon="`mdi-plus`"
                    variant="text"
                    density="compact"
                    rounded="lg"
                    @click.stop = "openCreateNewDocument(doc.documentId)"
                />
              </template>
            </v-tab>
          </v-tabs>
        </v-list>
        <v-dialog v-model="dialog" fullscreen>
          <v-card>
            <PostForm ref="postForm" @submit="handleData"></PostForm>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn text @click=postForm.submit()>작성 완료</v-btn>
              <v-btn text @click="dialog = false">닫기</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-col>

      <!-- 세로선 -->
      <v-divider class="divider-container" vertical></v-divider>

      <!-- 가운데 문서제목 부분 -->
      <v-col>
        <v-list class="pa-4">
          <v-card flat>
            <v-card-title class="headline text-center">
              {{ documentList.selectedDocumentDetails.title }}
            </v-card-title>
          </v-card>
        <!-- 가로 선 추가 -->
        <v-divider></v-divider>
        </v-list>
        <v-card flat class="mt-4 mx-auto" width="800">
          <div ref="viewer">{{ documentList.selectedDocumentDetails.content }}</div>
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
        <div class="attached-files">
          <v-card flat>
            <v-card-title class="headline text-center">첨부 파일

              <!-- 첨부파일 업로드 로직 부분 -->
              <v-dialog v-model="dialog" max-width="800">
                <template v-slot:activator="{ props: activatorProps }">
                  <v-btn
                      v-bind="activatorProps"
                      density="compact"
                      variant="flat"
                      icon="mdi-plus"
                  >
                  </v-btn>
                </template>

                <v-card>
                  <v-card-title class="headline">첨부파일 업로드</v-card-title>
                  <v-card-text>
                    <v-file-input
                        v-model="files"
                        label="Select files"
                        placeholder="Upload your documents"
                        prepend-icon="mdi-paperclip"
                        multiple
                    >
                      <template v-slot:selection="{ fileNames }">
                        <template v-for="fileName in fileNames" :key="fileName">
                          <v-chip class="me-2" color="primary" size="small" label>
                            {{ fileName }}
                          </v-chip>
                        </template>
                      </template>
                    </v-file-input>
                  </v-card-text>
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="green darken-1" text @click="handleFileUpload">업로드 완료</v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
            </v-card-title>


            <!-- 첨부파일 목록 -->
            <v-card-text>
              <v-btn text color="primary"
                     v-for="file in attachedFile.getAttachedFileList"
                     :key="file.fileName"
                     @click="handleFileClick(file.fileUrl)"
                     @mouseenter="hover = file.fileName"
                     @mouseleave="hover = null">
                {{ file.fileName }}

                <v-dialog max-width="500">
                <template v-slot:activator="{ props: activatorProps }" v-if="hover === file.fileName">
                  <v-btn
                      v-bind="activatorProps"
                      :icon="`mdi-minus`"
                      variant="text"
                      density="compact"
                      rounded="lg"
                  />
                </template>

                  <template v-slot:default="{ isActive }">
                    <v-card title= "첨부파일 삭제">
                      <v-card-text>
                        첨부파일을 삭제하시겠습니까?
                      </v-card-text>

                      <v-card-actions>
                        <v-spacer></v-spacer>

                        <v-snackbar
                            :timeout="2000"
                        >
                          <template v-slot:activator="{ props }">
                        <v-btn
                            v-bind = "props"
                            @click = "AttachedFileDelete(file.id)"

                        >Yes</v-btn>
                          </template>
                          첨부파일이 삭제되었습니다.
                        </v-snackbar>

                        <v-btn
                            text="No"
                            @click="isActive.value = false"
                        ></v-btn>

                      </v-card-actions>
                    </v-card>
                  </template>

                </v-dialog>
              </v-btn>
            </v-card-text>
          </v-card>
        </div>

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