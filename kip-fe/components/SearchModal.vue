<script setup lang="ts">

const keyword = ref();
const pageNumber = ref(0);
const documentSearch = useDocumentSearch()
const searchedDocs = ref();
const totalPages = ref(0);
import _ from 'lodash';
const emit = defineEmits(['closeModal']);


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

</script>

<template>
  <v-sheet>
    <v-form>
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
      <v-list lines="one">
        <v-list-item
            v-for="searchedDoc in searchedDocs"
            :key="searchedDoc.documentUUID"
            :title="searchedDoc.groupName"
            :subtitle="searchedDoc.title"
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
</template>

<style scoped>

</style>