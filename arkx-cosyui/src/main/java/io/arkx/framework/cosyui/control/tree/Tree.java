package io.arkx.framework.cosyui.control.tree;

import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.json.JSON;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Darkness
 * @date 2016年12月23日 下午2:27:45
 * @version V1.0
 */
public class Tree {

    private String rootText;

    private String rootIcon;

    private String identifierColumnName;

    private String parentIdentifierColumnName;

    private String leafIcon = "icons/extra/icon_tree09.gif";

    private String branchIcon = "icons/extra/icon_tree09.gif";

    private String data;

    public Tree() {
    }

    public Tree(DataTable dataTable) {
        this.data = JSON.toJSONString(dataTable);
    }

    public String getData() {
        return data;
    }

    @JSONField(serialize = false)
    public DataTable getDataTable() {
        DataTable dataTable = JSON.parseBean(data, DataTable.class);
        return dataTable;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRootText() {
        return rootText;
    }

    public void setRootText(String rootText) {
        this.rootText = rootText;
    }

    public String getRootIcon() {
        return rootIcon;
    }

    public void setRootIcon(String rootIcon) {
        this.rootIcon = rootIcon;
    }

    public String getIdentifierColumnName() {
        return identifierColumnName;
    }

    public void setIdentifierColumnName(String identifierColumnName) {
        this.identifierColumnName = identifierColumnName;
    }

    public String getLeafIcon() {
        return leafIcon;
    }

    public void setLeafIcon(String leafIcon) {
        this.leafIcon = leafIcon;
    }

    public String getBranchIcon() {
        return branchIcon;
    }

    public void setBranchIcon(String branchIcon) {
        this.branchIcon = branchIcon;
    }

    public String getParentIdentifierColumnName() {
        return parentIdentifierColumnName;
    }

    public void setParentIdentifierColumnName(String parentIdentifierColumnName) {
        this.parentIdentifierColumnName = parentIdentifierColumnName;
    }

}
