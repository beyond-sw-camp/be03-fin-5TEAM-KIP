<script lang="ts" setup>
import {toastEditorInstance} from "~/useToastEditor";

const form = ref({
  content: "",
});
const emit = defineEmits(["submit"]);
const props = defineProps({
  dataToPass: String
})

const editor = ref();
onMounted(() => {
  editor.value = toastEditorInstance(
      editor.value,
      "markdown",
      false,
      false,
      "80vh",
      "문서의 내용을 여기에 작성해주세요.",
      "vertical",
      <string>props.dataToPass
  );
});
const submit = () => {
  form.value.content = editor.value.getMarkdown();
  emit('submit', form)
}

defineExpose({submit});
</script>
<template>
  <div ref="editor"></div>
</template>
