package org.ark.framework.jaf.tag;

import java.io.IOException;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.StringUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import jakarta.servlet.jsp.tagext.Tag;

/**
 * @class org.ark.framework.jaf.tag.UploaderTag
 *        <h2>文件上控件</h2> <br>
 *        function importDB(){ <br>
 *        var diag = new Dialog("DBUpload"); <br>
 *        diag.title = "<ark:lang id='SysInfo.ImportDB'>导入数据库</ark:lang>"; <br>
 *        diag.url = "DBUpload.zhtml"; <br>
 *        diag.width = 500; <br>
 *        diag.height = 100; <br>
 *        diag.onOk = function(){ <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; $DW.doUpload(); <br>
 *        } <br>
 *        diag.show(); <br>
 *        } <br>
 *        <br>
 *        &lt;ark:uploader id="DBFile" width="300" allowType="zdt"
 *        fileCount="1"/> <br>
 *        <br>
 *        function doUpload(){ <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; var u = new Uploader("DBFile"); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; if(u.hasFile()&&!u.hasError()){ <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        u.sendRequest("SystemInfo.uploadDB",null,function(response){ <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        var taskID = response.get("TaskID"); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        var p = new Progress(taskID,"<ark:lang id=
 *        "SysInfo.DBImporting">数据导入中</ark:lang>...",500,150); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        p.show(function(){ <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        $D.close(); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        window.dialogOpener.Dialog.alert("<ark:lang id=
 *        'SysInfo.ImportSuccess'>数据导入成功</ark:lang>!"); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        window.dialogOpener.$D.close(); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        }); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        Node.hide(p.Dialog.okButton); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        Node.hide(p.Dialog.cancelButton); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        p.Dialog.cancelButton.onclick = function(){} <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        Node.disable(window.dialogOpener.$D.CancelButton); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        Node.disable(window.dialogOpener.$D.OKButton); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; return; <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; } <br>
 *        } <br>
 *        <br>
 *        public void uploadDB(UploadAction ua) { <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; final String FileName =
 *        AppDataPath.getValue() + "backup/DBUpload_" +
 *        DateUtil.getCurrentDate("yyyyMMddHHmmss") + ".zdt"; <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; try { <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        ua.getFirstFile().write(new File(FileName)); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; } catch (Exception e) { <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; e.printStackTrace();
 *        <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; return; <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; } <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; LongTimeTask ltt =
 *        LongTimeTask.getInstanceByType("Install"); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; if (ltt != null) { <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        this.Response.setFailedMessage(LangMapping.get("SysInfo.Installing"));
 *        <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; return; <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; } <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; ltt = new LongTimeTask() { <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public void execute()
 *        { <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        DBImporter di = new DBImporter(); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        di.setTask(this); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        di.importDB(FileName); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        setPercent(100); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *        InstallUI.reload(); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; } <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; }; <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; ltt.setType("Install"); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; ltt.setUser(Account.getCurrent()); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; ltt.start(); <br>
 *        &nbsp;&nbsp;&nbsp;&nbsp; $S("TaskID", ltt.getTaskID()); <br>
 *        }
 * @author Darkness
 * @date 2013-1-24 上午10:03:36
 * @version V1.0
 */
public class UploaderTag extends BodyTagSupport {

    private static final long serialVersionUID = 1L;

    /**
     * id标识
     *
     * @property id
     * @type {String}
     */
    private String id;

    /**
     * 名称
     *
     * @property name
     * @type {String}
     */
    private String name;

    /**
     * 滚动条颜色
     *
     * @property barColor
     * @type {String}
     */
    private String barColor;

    /**
     * 宽度
     *
     * @property width
     * @type {int}
     */
    private String width;

    /**
     * 高度
     *
     * @property height
     * @type {int}
     */
    private String height;

    /**
     * 允许的文件类型
     *
     * @property allowType
     * @type {String}
     */
    private String allowType;

    /**
     * 上传文件个数,*代表无限制
     *
     * @property fileCount
     * @type {int}
     */
    private String fileCount;

    /**
     * 文件最大大小
     *
     * @property fileMaxSize
     * @type {int}
     */
    private String fileMaxSize;

    /**
     * 文件名
     *
     * @property fileName
     * @type {String}
     */
    private String fileName;

    public void setPageContext(PageContext pc) {
        super.setPageContext(pc);
        this.id = null;
        this.name = null;
        this.barColor = null;
        this.width = null;
        this.height = null;
        this.allowType = null;
        this.fileCount = null;
        this.fileMaxSize = null;
    }

    public int doAfterBody() throws JspException {
        String content = getBodyContent().getString();
        try {
            getPreviousOut().print(getHtml(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Tag.EVAL_PAGE;
    }

    public int doEndTag() throws JspException {
        if ((this.bodyContent == null) || (StringUtil.isEmpty(this.bodyContent.getString()))) {
            try {
                this.pageContext.getOut().print(getHtml(""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Tag.EVAL_PAGE;
    }

    public String getHtml(String content) {
        String flashVars = "";
        String srcSWF = Config.getContextPath() + "Framework/Components/ZUploader2.swf";
        if (StringUtil.isEmpty(this.id)) {
            this.id = "_ARK_NOID_";
        }
        if (StringUtil.isEmpty(this.name)) {
            this.name = this.id;
        }
        if (StringUtil.isNotEmpty(this.allowType)) {
            this.allowType = "" + SelectTag.getRealValue(this.allowType, this, this.pageContext);
            flashVars = flashVars + "fileType=" + this.allowType;
        }
        this.fileName = ((String) SelectTag.getRealValue(this.fileName, this, this.pageContext));
        if (StringUtil.isNotEmpty(this.fileName)) {
            flashVars = flashVars + "&fileName=" + StringUtil.htmlEncode(this.fileName);
        }
        if (StringUtil.isNotEmpty(this.barColor)) {
            flashVars = flashVars + "&barColor=" + this.barColor;
        }
        if ((StringUtil.isNotEmpty(this.fileCount)) && (this.fileCount != "0")) {
            flashVars = flashVars + "&fileCount=" + this.fileCount;
        }
        if ((StringUtil.isNotEmpty(this.fileMaxSize)) && (this.fileMaxSize != "0")) {
            flashVars = flashVars + "&fileMaxSize=" + this.fileMaxSize;
        }
        if (StringUtil.isEmpty(this.width)) {
            this.width = "250";
        }
        if (StringUtil.isEmpty(this.height)) {
            this.height = "25";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<object id='" + this.id + "' classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' width='"
                + this.width + "' height='" + this.height + "' style='vertical-align:middle;'>\n");
        sb.append("<param name='movie' value='" + srcSWF + "'>\n");
        sb.append("<param name='quality' value='high'>\n");
        sb.append("<param name='wmode' value='transparent'>\n");
        sb.append("<param name='FlashVars' value='" + flashVars + "'>\n");
        sb.append(
                "<embed name='" + this.name + "' src='" + srcSWF + "' type='application/x-shockwave-flash' FlashVars='"
                        + flashVars + "' quality='high' wmode='transparent' width='" + this.width + "' height='"
                        + this.height + "'></embed>\n");
        sb.append("</object>\n");

        sb.append("<script type='text/javascript'>");
        sb.append("var _ZUploaderSessionID='" + this.pageContext.getSession().getId()
                + "';Ark.Uploader.checkVersion();</script>\n");
        return sb.toString();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarColor() {
        return this.barColor;
    }

    public void setBarColor(String barColor) {
        this.barColor = barColor;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAllowType() {
        return this.allowType;
    }

    public void setAllowType(String allowType) {
        this.allowType = allowType;
    }

    public String getFileCount() {
        return this.fileCount;
    }

    public void setFileCount(String fileCount) {
        this.fileCount = fileCount;
    }

    public String getFileMaxSize() {
        return this.fileMaxSize;
    }

    public void setFileMaxSize(String fileMaxSize) {
        this.fileMaxSize = fileMaxSize;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
