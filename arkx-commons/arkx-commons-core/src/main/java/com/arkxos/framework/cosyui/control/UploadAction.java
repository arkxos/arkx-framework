package com.arkxos.framework.cosyui.control;

import java.util.ArrayList;

import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.cosyui.web.mvc.handler.ZAction;
import com.arkxos.framework.thirdparty.commons.fileupload.FileItem;

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
