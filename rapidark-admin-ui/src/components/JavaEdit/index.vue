<template>
  <div class="json-editor">
    <textarea ref="textarea" />
  </div>
</template>

<script>
import CodeMirror from 'codemirror'
import 'codemirror/lib/codemirror.css'
// 替换主题这里需修改名称
import 'codemirror/theme/idea.css'
import 'codemirror/mode/clike/clike'
export default {
  props: {
    value: {
      type: String,
      required: true
    },
    height: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      editor: false
    }
  },
  watch: {
    value(value) {
      const editorValue = this.editor.getValue()
      if (value !== editorValue) {
        this.editor.setValue(this.value)
      }
    },
    height(value) {
      this.editor.setSize('auto', this.height)
    }
  },
  mounted() {
    this.editor = CodeMirror.fromTextArea(this.$refs.textarea, {
      mode: 'text/x-java',
      lineNumbers: true,
      lint: true,
      lineWrapping: true,
      tabSize: 2,
      cursorHeight: 0.9,
      // 替换主题这里需修改名称
      theme: 'idea',
      readOnly: true
    })
    this.editor.setSize('auto', this.height)
    this.editor.setValue(this.escapeStringHTML(this.value))
  },
  methods: {
    getValue() {
      return this.editor.getValue()
    },
    //把HTML格式的字符串转义成实体格式字符串
    escapeHTMLString: function (str) {
      str = str.replace(/</g,'&lt;');
      str = str.replace(/>/g,'&gt;');
      return str;
    },
    //把实体格式字符串转义成HTML格式的字符串
    escapeStringHTML: function (str) {
      str = str.replace(/&lt;/g,'<');
      str = str.replace(/&gt;/g,'>');
      str = str.replace(/&quot;/g,'"');

      return str;
    }
  }
}
</script>

<style scoped>
  .json-editor{
    height: 100%;
    margin-bottom: 10px;
  }
  .json-editor >>> .CodeMirror {
    font-size: 14px;
    overflow-y:auto;
    font-weight:normal
  }
  .json-editor >>> .CodeMirror-scroll{
  }
  .json-editor >>> .cm-s-rubyblue span.cm-string {
    color: #F08047;
  }
</style>
