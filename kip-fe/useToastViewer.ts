import '@toast-ui/editor/dist/toastui-editor-viewer.css';
import Viewer from '@toast-ui/editor/dist/toastui-editor-viewer';


export const toastViewerInstance = (
    divId: HTMLElement,
    initialValue : string
) => {
  return new Viewer({
    el: divId,
    initialValue : initialValue,
    linkAttributes: {
      target: '_blank',
    },
  });
};