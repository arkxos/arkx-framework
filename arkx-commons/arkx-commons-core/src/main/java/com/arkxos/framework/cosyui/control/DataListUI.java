package com.arkxos.framework.cosyui.control;

import com.arkxos.framework.Constant;
import com.arkxos.framework.Current;
import io.arkx.framework.annotation.Priv;
import io.arkx.framework.annotation.Verify;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.core.JsonResult;
import io.arkx.framework.core.method.IMethodLocator;
import io.arkx.framework.core.method.MethodLocatorUtil;
import com.arkxos.framework.cosyui.control.datalist.DataListBodyManager;
import com.arkxos.framework.cosyui.tag.RestUtil;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.arkxos.framework.cosyui.web.RequestData;
import com.arkxos.framework.cosyui.web.UIFacade;
import com.arkxos.framework.security.PrivCheck;
import com.arkxos.framework.security.VerifyCheck;

/**
 * DataList服务器端响应UI类
 * 
 */
public class DataListUI extends UIFacade {

	@Verify(ignoreAll = true)
	@Priv(login = false)
	public void doWork() {
		try {
			DataListAction dla = new DataListAction();
			dla.setPageEnabled($B(Constant.Page));
			String method = $V(Constant.Method);
			String rest = $V(Constant.Rest);
			dla.setMethod(method);
			dla.setRest(rest);
			dla.setID($V(Constant.ID));
			dla.setSortEnd($V(Constant.SortEnd));
			dla.setDragClass($V(Constant.DragClass));
			dla.setParams(Request);
			dla.setPageSize($I(Constant.Size));
			if (dla.getPageSize() > DataGridUI.MaxPageSize) {
				dla.setPageSize(DataGridUI.MaxPageSize);
			}
			if (dla.isPageEnabled()) {
				dla.setPageIndex(0);
				if (Request.get(Constant.DataGridPageIndex) != null && !Request.get(Constant.DataGridPageIndex).equals("")) {
					dla.setPageIndex($I(Constant.DataGridPageIndex));
				}
				if (dla.getPageIndex() < 0) {
					dla.setPageIndex(0);
				}
				if (dla.getPageIndex() != 0) {
					dla.setTotal($I(Constant.DataGridPageTotal));
				}
			}
			dla.setAjaxRequest(true);
			dla.setTagBody(DataListBodyManager.get(Request.getString(Constant.TagBody)));
			
			if(!StringUtil.isEmpty(method)) {
				IMethodLocator m = MethodLocatorUtil.find(method);
				PrivCheck.check(m);
				// 参数检查
				if (!VerifyCheck.check(m)) {
					String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
					LogUtil.warn(message);
					Current.getResponse().setFailedMessage(message);
					return;
				}
				m.execute(dla);
			} else {
				if(dla.isPageEnabled()) {
					RequestData requestData = Current.getRequest();
					requestData.put("pageIndex", dla.getPageIndex());
					requestData.put("pageSize", dla.getPageSize());
					JsonResult jsonResult = RestUtil.post(rest, requestData, PagedData.class);
					if(!jsonResult.isSuccess()) {
						throw new TemplateRuntimeException(jsonResult.getMessage());
					}
					PagedData pagedData = (PagedData)jsonResult.getData();
					dla.setTotal(pagedData.getTotal());
					dla.bindData(pagedData.getDataTable());
				} else {
					RequestData requestData = Current.getRequest();
					JsonResult jsonResult = RestUtil.post(rest, requestData, DataTable.class);
					if(!jsonResult.isSuccess()) {
						throw new TemplateRuntimeException(jsonResult.getMessage());
					}
					DataTable dataTable = (DataTable)jsonResult.getData();
					dla.bindData(dataTable);
				}
			}
			
			
			$S("HTML", dla.getResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
