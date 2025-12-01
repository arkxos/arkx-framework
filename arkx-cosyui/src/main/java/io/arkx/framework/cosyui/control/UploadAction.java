package io.arkx.framework.cosyui.control;

import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.cosyui.web.mvc.handler.ZAction;
import io.arkx.framework.thirdparty.commons.fileupload.FileItem;

import java.util.ArrayList;

/**
 * 上传数据绑定行为类
 * 
 */
public class UploadAction extends ZAction {
	ArrayList<FileItem> items;

	public FileItem getFirstFile() {
		if (ObjectUtil.empty(items)) {
			return null;
		}
		return items.get(0);
	}

	public ArrayList<FileItem> getAllFiles() {
		return items;
	}

	public void setItems(ArrayList<FileItem> items) {
		this.items = items;
	}
}
