import "@toast-ui/editor/dist/toastui-editor.css";
import Editor from "@toast-ui/editor";
const BASE_URL = import.meta.env.VITE_BASE_URL;

export const toastEditorInstance = (
    divId: HTMLElement,
    initialEditType: string,
    hideModeSwitch: boolean,
    autofocus: boolean,
    height: string,
    placeholder: string,
    previewStyle: string,
    initialValue: string,
) => {
  return new Editor({
    el: divId,
    initialEditType: initialEditType,
    hideModeSwitch: hideModeSwitch,
    language: "ko-KR",
    autofocus: autofocus,
    height: height,
    placeholder: placeholder,
    previewStyle: previewStyle,
    initialValue : initialValue,
    hooks: {
      addImageBlobHook: async ({blob, callback}: { blob: any, callback: any }) => {

        const formData = new FormData();
        formData.append('image', blob);

        // 2. FileApiController - uploadEditorImage 메서드 호출
        const response = await fetch(`${BASE_URL}/doc/image`, {
          method : 'POST',
          body : formData,
        })
        const imageUrl = await response.text()
        callback(imageUrl, 'image alt attribute');
      }
    },
    linkAttributes: {
      target: '_blank',
    },
  });
};