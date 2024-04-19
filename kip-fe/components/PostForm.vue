<script setup lang="ts">
import {ref} from "vue";
import {toastEditorInstance} from "~/useToastEditor";
import {useColor} from "#imports";

const emit = defineEmits(["submit"]);
const color = useColor();

const form = ref({
  title: "",
  hashTags: [],
  content: "",
  image: [],
  upLinkId: "",
  groupId: "",
});

const editor = ref();
onMounted(() => {
  editor.value = toastEditorInstance(
      editor.value,
      "markdown",
      false,
      false,
      "75vh",
      "문서의 내용을 여기에 작성해주세요.",
      "vertical",
  );
});

const submit = () => {
  form.value.content = editor.value.getMarkdown();
  emit('submit', form.value)
}

defineExpose({submit});
</script>
<template>
    <v-row>
      <v-col cols="7" >
      <v-text-field
          style="margin-left: 10px;"
          class="input_title"
          variant="underlined"
          v-model="form.title"
          placeholder="제목을 입력하세요."
          persistent-placeholder
          clearable
      />
      </v-col>
      <v-col cols="7">
        <v-combobox
            style="margin-left: 10px"
            variant="underlined"
            v-model="form.hashTags"
            multiple
            chips
            placeholder="태그를 입력하세요."
            persistent-placeholder
            hint="여러 태그를 엔터로 구분하여 입력하세요."
        />
      </v-col>
    </v-row>
    <v-spacer></v-spacer>

    <!-- 내용 -->
    <div ref="editor"></div>
</template>
<style>
.input_title input {
  font-size: 50px;
}
</style>
