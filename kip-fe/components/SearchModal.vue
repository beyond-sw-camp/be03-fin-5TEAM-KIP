<script setup lang="ts">

import {useRequest} from "~/stores/Request";

const keyword = ref();
const pageNumber = ref(0);
const documentSearch = useDocumentSearch()
const searchedDocs = ref();
const totalPages = ref(0);
const sendRequestModal = ref(false);
const requestDays = ref();
const request = useRequest();
const docUUID = ref();
const color = useColor();
import _ from 'lodash';
import {useColor} from "#imports";
const emit = defineEmits(['closeModal']);

const confirmRequest = async () => {
  await request.sendRequest(docUUID.value, requestDays.value);
  sendRequestModal.value = false;
}

const debouncedSearch = _.debounce(async () => {
  if(keyword.value != "") {
    searchedDocs.value = null
    await documentSearch.setSearchDocument(keyword.value, pageNumber.value);
    const data = documentSearch.getSearchDocument
    searchedDocs.value = data.document
    totalPages.value = data.totalPages
  }
  // 여기서 실제 검색 로직을 구현합니다.
}, 500);
const close = () => {
  emit('closeModal');
}

const viewDocument = async (documentUUID) => {
  await documentSearch.viewDocument(documentUUID);
  const canView : any = documentSearch.getAvailable.canView;
  if (canView === `Public Document`) {
    await navigateTo(`/publicOpenDoc`)
    emit('closeModal')
  }
  if (canView === `Group User`) {
    const groupNumber = documentSearch.getGroupId.groupId;
    await navigateTo(`/group/${groupNumber}`)
    emit('closeModal')
  }
  if (canView === `Available User`) {
    await navigateTo('/agree')
    emit('closeModal')
  }
  if (canView === `Unavailable User`) {
    docUUID.value = documentUUID
    sendRequestModal.value = true;
  }
}

</script>

<template>
  <v-sheet>
    <v-form @submit.prevent="">
      <v-container>
        <v-text-field
            clearable
            v-model="keyword"
            @input="debouncedSearch">
          <template v-slot:label>
          <span>
            <strong>검색</strong><v-icon icon="mdi-file-find"></v-icon>
          </span>
          </template>
          <template v-slot:append>
            <v-btn
                :icon="`mdi-close`"
                variant="text"
                density="compact"
                rounded="lg"
                @click="close"
            />
          </template>
        </v-text-field>
      </v-container>
      <v-divider></v-divider>
      <v-list lines="one">
        <v-list-item
            v-for="searchedDoc in searchedDocs"
            :key="searchedDoc.documentUUID"
            :title="searchedDoc.groupName"
            :subtitle="searchedDoc.title"
            @click="viewDocument(searchedDoc.documentUUID)"
        ></v-list-item>
      </v-list>
      <v-divider></v-divider>

      <div class="pa-4 d-flex justify-space-between">
        <v-btn
            v-if="pageNumber > 0"
            @click="pageNumber-- ; debouncedSearch()"
            class="text-none"
            color="medium-emphasis"
            min-width="92"
            variant="outlined"
            rounded
        >
          이전
        </v-btn>
        <v-btn
            v-if="pageNumber < totalPages"
            @click="pageNumber++ ; debouncedSearch()"
            class="text-none"
            color="medium-emphasis"
            min-width="92"
            variant="outlined"
            rounded
        >
          다음
        </v-btn>
      </div>
    </v-form>
  </v-sheet>
  <v-dialog
      class="d-flex"
      width="25vw"
      opacity="10%"
      v-model="sendRequestModal">
    <v-card
        rounded="xl"
        class="pa-5">
      <v-card-title class="headline">권한 요청</v-card-title>
      <v-card-text>해당 글에 권한 요청을 하시겠습니까?
        <v-text-field
            v-model="requestDays"
            label="요청 일수"
            type="number"
            min="1"
            max="30"
            step="1"
        ></v-text-field>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
            variant="elevated"
            :color="color.kipMainColor" @click="confirmRequest">요청
        </v-btn>
        <v-btn color="blue darken-1" @click="sendRequestModal = false">취소</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>

</style>