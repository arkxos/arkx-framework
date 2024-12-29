package com.arkxos.framework.cosyui.control;

import java.io.OutputStream;
import java.util.Map;

import com.arkxos.framework.annotation.Priv;
import com.arkxos.framework.annotation.Verify;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.DateUtil;
import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.ServletUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.core.method.IMethodLocator;
import com.arkxos.framework.core.method.MethodLocatorUtil;
import com.arkxos.framework.cosyui.control.grid.DataGridBodyManager;
import com.arkxos.framework.cosyui.html.HtmlElement;
import com.arkxos.framework.cosyui.html.HtmlParser;
import com.arkxos.framework.cosyui.html.HtmlTR;
import com.arkxos.framework.cosyui.web.UIFacade;
import com.arkxos.framework.cosyui.web.mvc.handler.ZAction;
import com.arkxos.framework.data.db.DataTableUtil;
import com.arkxos.framework.json.JSON;
import com.arkxos.framework.json.JSONObject;
import com.arkxos.framework.security.PrivCheck;
import com.arkxos.framework.security.VerifyCheck;
import com.rapidark.framework.Config;
import com.rapidark.framework.Constant;
import com.rapidark.framework.Current;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DataGridUI extends UIFacade {
	public static final int MaxPageSize = 10000;

	/**
	 * 方法本身不需要检查权限，但其method属性调用的方法需要检查权限
	 */
	@Verify(ignoreAll = true)
	@Priv(login = false)
	public void doWork() {
		DataGridAction dga = new DataGridAction();
		String method = $V(Constant.Method);
		dga.setMethod(method);

		dga.setID($V(Constant.ID));
		dga.setAjaxRequest(true);
		dga.setPageEnabled($B(Constant.Page));
		dga.setMultiSelect(!"false".equalsIgnoreCase($V(Constant.DataGridMultiSelect)));
		dga.setAutoFill(!"false".equalsIgnoreCase($V(Constant.DataGridAutoFill)));
		dga.setScroll($B(Constant.DataGridScroll));
		dga.setLazy($B(Constant.Lazy));
		dga.setCacheSize($I(Constant.CacheSize));
		dga.setParams(Current.getRequest());
		if (dga.isPageEnabled()) {
			dga.setPageIndex(0);
			dga.setPageIndex($I(Constant.DataGridPageIndex));
			if (dga.getPageIndex() < 0) {
				dga.setPageIndex(0);
			}
			if (dga.getPageIndex() != 0) {
				dga.setTotal($I(Constant.DataGridPageTotal));
			}
			dga.setPageSize($I(Constant.Size));
			if (dga.getPageSize() > MaxPageSize) {// 每页最大条数为10000
				dga.setPageSize(MaxPageSize);
			}
		}
		dga.setTagBody(DataGridBodyManager.get(this.Request.getString(Constant.TagBody)));

		// 响应DataGrid.insertRow
		String strInsertRowIndex = this.Request.getString(Constant.DataGridInsertRow);
		if (StringUtil.isNotEmpty(strInsertRowIndex)) {
			DataTable dt = this.Request.getDataTable(Constant.DataTable);
			this.Request.remove(Constant.DataTable);
			this.Request.remove(Constant.DataGridInsertRow);
			dga.bindData(dt);

			HtmlParser parser = new HtmlParser(dga.getResult());
			parser.parse();
			HtmlTR tr = new HtmlTR((HtmlElement) parser.getDocument().getTopElementsByTagName("tr").get(1));
			$S("TRAttr", tr.getAttributes());
			for (int i = 0; i < tr.elements().size(); i++) {
				$S("TDAttr" + i, tr.getTD(i).getAttributes());
				$S("TDHtml" + i, tr.getTD(i).getInnerHTML());
			}
		} else {
			IMethodLocator m = MethodLocatorUtil.find(method);
			PrivCheck.check(m);
			
			// 参数检查
			if (!VerifyCheck.check(m)) {
				String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
				LogUtil.warn(message);
				Current.getResponse().setFailedMessage(message);
				return;
			}
			m.execute(new Object[] { dga });
			$S("HTML", dga.getResult());
		}
	}

	@Priv(login = false)
	public void toExcel(ZAction za) throws Exception {
		HttpServletRequest request = za.getRequest();
		HttpServletResponse response = za.getResponse();
		request.setCharacterEncoding(Config.getGlobalCharset());
		response.reset();
		response.setContentType("application/octet-stream");
		String suffix = ".xls";
		if ("2007".equals(Config.getExcelVersion())) {
			suffix = ".xlsx";
		}
		response.setHeader("Content-Disposition",
				"attachment; filename=Excel_" + DateUtil.getCurrentDateTime("yyyyMMddhhmmss") + suffix);
		try {
			String xls = "_Excel_";
			String selectDataJSON = $V(xls + "_ARK_SELECT_Data");
			JSONObject dataSource = StringUtil.isEmpty(selectDataJSON) ? null : JSON.parseJSONObject(selectDataJSON);
			Mapx<String, String> params = ServletUtil.getParameterMap(request);
			String ID = params.getString(xls + Constant.ID);
			String tagBody = params.getString(xls + Constant.TagBody);
			String pageIndex = params.getString(xls + Constant.DataGridPageIndex);
			String pageSize = params.getString(xls + Constant.Size);
			String rowTotal = params.getString(xls +  Constant.DataGridPageTotal);
			String method = params.getString(xls +  Constant.Method);
			String pageFlag = params.getString(xls + Constant.Page);
			String excelPageFlag = params.getString(xls + "_ARK_ToExcelPageFlag");
			String strWidths = params.getString(xls +  "_ARK_Widths");
			String strIndexes = params.getString(xls +  "_ARK_Indexes");
			String strRows = params.getString(xls +  "_ARK_Rows");
			int pageLimit = params.getInt(xls + "_ARK_PageLimit");

			OutputStream os = response.getOutputStream();

			DataGridAction dga = new DataGridAction();
			dga.getParams().putAll(params);
			dga.setTagBody(DataGridBodyManager.get(tagBody));
			dga.setID(ID);
			dga.setAjaxRequest(true);
			if (ObjectUtil.notEmpty(dataSource)) {
				DataTable dt = (DataTable) JSON.tryReverse(dataSource);
				IMethodLocator m = MethodLocatorUtil.find(method);
				PrivCheck.check(m);
				Mapx<String, Object> map = Current.getRequest();
				for (Map.Entry<String, Object> e : map.entrySet()) {
					String k = e.getKey();
					if (k.startsWith(xls)) {
						Object v = e.getValue();
						map.remove(k);
						map.put(k.substring(xls.length()), v);
					}
				}
				m.execute(new Object[] { dga });
				try {
//					Class<?> clazz = Class.forName(DataTableUtil.class.getName());
//					Method htmlTableToExcel = clazz.getMethod("prepareHtmlTableToExcel",
//							new Class[] { OutputStream.class, DataGridAction.class, DataTable.class, String.class,
//									String.class, String.class, String.class });
//					htmlTableToExcel.invoke(null, new Object[] {  });
					
					DataTableUtil.prepareHtmlTableToExcel(os, dga, dt, xls, strIndexes, strRows, strWidths);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				dga.setMethod(method);
				try {
//					OutputStream os, DataGridAction dga, String rowTotal,
//					String excelPageFlag, String pageIndex, String pageSize, 
//					String pageFlag, String method, String rest, String xls,
//					String strIndexes, String strRows, String strWidths, int pageLimit
					DataTableUtil.prepareHtmlTableToExcel( os, dga, rowTotal, excelPageFlag, pageIndex, pageSize,
							pageFlag, method, "", xls, strIndexes, strRows, strWidths, Integer.valueOf(pageLimit) );
//					Class<?> clazz = Class.forName(DataTableUtil.class.getName());
//					Method htmlTableToExcel = clazz.getMethod("prepareHtmlTableToExcel",
//							new Class[] { OutputStream.class, DataGridAction.class, String.class, String.class,
//									String.class, String.class, String.class, String.class, String.class, String.class,
//									String.class, String.class, Integer.TYPE });
//					htmlTableToExcel.invoke(null, new Object[] {});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			os.flush();
			os.close();
			os = null;
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
