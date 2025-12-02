package org.ark.framework.jaf.controls;

import java.util.ArrayList;

import org.apache.commons.fileupload2.core.FileItem;

import io.arkx.framework.commons.util.ObjectUtil;

/**
 * @class org.ark.framework.jaf.controls.UploadAction
 *
 * @author Darkness
 * @date 2013-1-31 下午12:45:19
 * @version V1.0
 */
public class UploadAction {

    protected ArrayList<FileItem> items;

    public FileItem getFirstFile() {
        if (ObjectUtil.empty(this.items)) {
            return null;
        }
        return this.items.get(0);
    }

    public ArrayList<FileItem> getAllFiles() {
        return this.items;
    }

    public void setFiles(ArrayList<FileItem> files) {
        this.items = files;
    }
}
