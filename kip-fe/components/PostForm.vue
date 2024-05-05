<script setup lang="ts">
import {ref} from "vue";
import {toastEditorInstance} from "~/useToastEditor";
import {useColor} from "#imports";

const emit = defineEmits(["submit"]);

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
      "![이미지](https://uicdn.toast.com/toastui/img/tui-editor-bi.png)\n" +
      "\n" +
      "# 에디터 설명\n" +
      "\n" +
      "2018년에 오픈소스로 출시되었으며_, ~~지속적으로~~ **GitHub에서 ⭐️ 10k 스타를 받으며 발전해 왔습니다**.\n" +
      "\n" +
      "## 인스턴스 생성\n" +
      "\n" +
      "다음 코드로 인스턴스를 생성하고 [에디터](https://github.com/nhn/tui.editor)의 `getHtml()`과 `getMarkdown()`을 사용할 수 있습니다.\n" +
      "\n" +
      "```js\n" +
      "const editor = new Editor(options);\n" +
      "```\n" +
      "\n" +
      "> 기본 옵션에 대한 표는 아래를 참조하세요.\n" +
      "> > 더 많은 API 정보는 문서에서 확인할 수 있습니다.\n" +
      "\n" +
      "| 이름 | 유형 | 설명 |\n" +
      "| --- | --- | --- |\n" +
      "| el | `HTMLElement` | 컨테이너 요소 |\n" +
      "\n" +
      "## 기능\n" +
      "\n" +
      "* CommonMark + GFM 사양\n" +
      "   * 실시간 미리보기\n" +
      "   * 스크롤 동기화\n" +
      "   * 자동 들여쓰기\n" +
      "   * 문법 강조\n" +
      "        1. 마크다운\n" +
      "        2. 미리보기\n" +
      "\n" +
      "## 지원하는 Wrapper\n" +
      "\n" +
      "> * Wrappers\n" +
      ">    1. [x] React\n" +
      ">    2. [x] Vue\n" +
      ">    3. [ ] Ember",
  );
});

const submit = () => {
  form.value.content = editor.value.getMarkdown();
  emit('submit', form.value)
}
defineExpose({submit});
</script>
<template>
  <v-row >
    <v-col cols="6" style="height: 40px">
      <v-text-field
          class="input_title"
          variant="solo-inverted"
          v-model="form.title"
          placeholder="제목"
      />
    </v-col>
    <v-col cols="6" style="height: 40px">
      <v-combobox
          cless="hash_title"
          variant="plain"
          v-model="form.hashTags"
          multiple
          placeholder="해시 태그"
          hint="여러 태그를 엔터로 구분하여 입력하세요.">
        <template v-slot:selection="data">
          <v-chip
              class="pa-4 mr-1"
              style="color: #FF5722"
              :key="JSON.stringify(data.item)"
              size="x-large">
            {{ data.item.title }}
            <v-tooltip
                activator="parent"
                location="top"
            > 태그 검색
            </v-tooltip>
          </v-chip>
        </template>
      </v-combobox>
    </v-col>
  </v-row>
  <div ref="editor" class="editor__CSS"></div>

  <!-- 내용 -->
</template>
<style>
.editor__CSS {
  width:100%;
  height: 88vh !important;
}
.input_title input {
  font-size: 25px;
  background-color: #fffac1;
  font-weight: bold;
  color: #00a9ff;
}

.input_title input:focus {
  color: white;
  background-color: var(--primary-color);
}

</style>
