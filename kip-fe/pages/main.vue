<script setup>

const route = useRoute()
const groupId = route.params.groupId;
const groupName = useGroup();
const documentList = useDocumentList();

await documentList.$reset();
await documentList.setDocumentList(groupId);
await groupName.setGroupUsersInfo(groupId);

const selectDocument = (documentId) => {
  // TODO: 선택한 문서 ID를 사용하여 문서의 상세 정보를 가져오고 표시하는 로직을 구현
};

</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="3">
        <v-card flat>
          <v-list-item>
            <v-list-item-content>
              <v-list-item-title class="border-right" variant="tonal"
                                 v-for="group in groupName"
                                 :key="group.groupId"
              > {{group.groupName}}
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>

          <v-list dense class="border-right">
            <v-list-item
                v-for="doc in documentList.getDocumentList"
                :key="doc.documentId"
                @click="selectDocument(doc.documentId)"
            >
              {{ doc.title }}
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>


      <v-col cols="6">
        <v-card flat>
          <v-card-text v-html="documentContent"></v-card-text>
        </v-card>
      </v-col>
      <!--      <v-col cols="3">-->
      <!--        <v-card>-->
      <!--          <v-card-title>목차</v-card-title>-->
      <!--          <v-list dense>-->
      <!--            <v-list-item v-for="toc in documentToc" :key="toc.id">-->
      <!--              {{ toc.title }}-->
      <!--            </v-list-item>-->
      <!--          </v-list>-->
      <!--          <v-card-title>첨부파일</v-card-title>-->
      <!--          <v-list dense>-->
      <!--            <v-list-item-->
      <!--                v-for="attachment in documentAttachments"-->
      <!--                :key="attachment.id"-->
      <!--            >-->
      <!--              {{ attachment.name }}-->
      <!--            </v-list-item>-->
      <!--          </v-list>-->
      <!--          <v-card-title>해시태그</v-card-title>-->
      <!--          <v-chip-group>-->
      <!--            <v-chip v-for="hashtag in documentHashtags" :key="hashtag">-->
      <!--              {{ hashtag }}-->
      <!--            </v-chip>-->
      <!--          </v-chip-group>-->
      <!--        </v-card>-->
      <!--      </v-col>-->
    </v-row>
  </v-container>
</template>

<style scoped>
.border-bottom {
  border-bottom: 1px solid #e0e0e0; /* Change the color to match the design */
}

.border-right {
  border-right: 1px solid #e0e0e0;
}

.list-item:not(:last-child) {
  border-bottom: 1px solid #e0e0e0; /* Add a border to all list items except the last one */
}
</style>