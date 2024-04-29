import Viewer from '@toast-ui/editor/dist/toastui-editor-viewer';
import '@toast-ui/editor/dist/toastui-editor-viewer.css';

export const toastViewerInstance = (
    divId: HTMLElement,
    initialValue : string
) => {
  return new Viewer({
    el: divId,
    initialValue : initialValue,
  });
};