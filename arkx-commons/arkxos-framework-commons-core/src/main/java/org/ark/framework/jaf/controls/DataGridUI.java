//package org.ark.framework.jaf.controls;
//
//import java.io.OutputStream;
//import java.lang.reflect.Method;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.ark.framework.Config;
//import org.ark.framework.jaf.Current;
//import org.ark.framework.jaf.ZAction;
//import org.ark.framework.jaf.html.HtmlTR;
//import org.ark.framework.jaf.html.HtmlTable;
//import org.ark.framework.orm.query.QueryBuilder;
//import org.ark.framework.security.PrivCheck;
//import org.ark.framework.security.VerifyCheck;
//import org.ark.framework.utility.DateUtil;
//import org.ark.framework.utility.HtmlUtil;
//import org.ark.framework.utility.LogUtil;
//import org.ark.framework.utility.ServletUtil;
//import com.arkxos.framework.commons.util.StringUtil;
//
//import com.arkxos.framework.framework.RequestData;
//import com.arkxos.framework.cosyuiFacade;
//import com.arkxos.framework.framework.annotation.Priv;
//import com.arkxos.framework.framework.annotation.Verify;
//import com.arkxos.framework.framework.collection.DataTable;
//import com.arkxos.framework.framework.collection.Mapx;
//import com.arkxos.framework.cosyui.control.DataGridAction;
//
//
///**
// * @class org.ark.framework.jaf.controls.DataGridUI
// * 
// * @author Darkness
// * @date 2013-1-31 下午12:40:41 
// * @version V1.0
// */
//public class DataGridUI extends UIFacade {
//	
//	public static final int MaxPageSize = 10000;
//
//	@Verify(ignoreAll = true)
//	@Priv(login = false)
//	public void doWork() {
//		try {
//			DataGridAction dga = new DataGridAction();
//
//			dga.setTagBody(StringUtil.htmlDecode($V("_ARK_TAGBODY")));
//			String method = $V("_ARK_METHOD");
//			dga.setMethod(method);
//
//			dga.setID($V("_ARK_ID"));
//			dga.setPageFlag("true".equalsIgnoreCase($V("_ARK_PAGE")));
//			dga.setMultiSelect(!"false".equalsIgnoreCase($V("_ARK_MULTISELECT")));
//			dga.setAutoFill(!"false".equalsIgnoreCase($V("_ARK_AUTOFILL")));
//			dga.setScroll("true".equalsIgnoreCase($V("_ARK_SCROLL")));
//			dga.setLazy("true".equalsIgnoreCase($V("_ARK_LAZY")));
//			if (StringUtil.isNotEmpty($V("_ARK_CACHESIZE"))) {
//				dga.setCacheSize(Integer.parseInt($V("_ARK_CACHESIZE")));
//			}
//			dga.setParams(Current.getRequest());
//			dga.Response = Current.getResponse();
//
//			if (dga.isPageFlag()) {
//				dga.setPageIndex(0);
//				if ((this.Request.get("_ARK_PAGEINDEX") != null) && (!this.Request.get("_ARK_PAGEINDEX").equals(""))) {
//					dga.setPageIndex(Integer.parseInt(this.Request.getString("_ARK_PAGEINDEX")));
//				}
//				if (dga.getPageIndex() < 0) {
//					dga.setPageIndex(0);
//				}
//				if (dga.getPageIndex() != 0) {
//					dga.setTotal(Integer.parseInt(this.Request.getString("_ARK_PAGETOTAL")));
//				}
//				dga.setPageSize(Integer.parseInt($V("_ARK_SIZE")));
//				if (dga.getPageSize() > 10000) {
//					dga.setPageSize(10000);
//				}
//			}
//
//			HtmlTable table = new HtmlTable();
//			table.parseHtml(dga.getTagBody());
//			dga.setTemplate(table);
//			dga.parse();
//
//			String strInsertRowIndex = this.Request.getString("_ARK_INSERTROW");
//			if (StringUtil.isNotEmpty(strInsertRowIndex)) {
//				DataTable dt = (DataTable) this.Request.get("_ARK_DATATABLE");
//				this.Request.remove("_ARK_DATATABLE");
//				this.Request.remove("_ARK_INSERTROW");
//				dga.bindData(dt);
//
//				HtmlTR tr = dga.getTable().getTR(1);
//				$S("TRAttr", tr.getAttributes());
//				for (int i = 0; i < tr.Children.size(); i++) {
//					$S("TDAttr" + i, tr.getTD(i).getAttributes());
//					$S("TDHtml" + i, tr.getTD(i).getInnerHTML());
//				}
//			} else {
//				Method m = Current.findMethod(method, new Class[] { DataGridAction.class });
//				if (!PrivCheck.check(m, this.Request, this.Response)) {
//					return;
//				}
//
//				if (!VerifyCheck.check(m)) {
//					String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
//					LogUtil.warn(message);
//					Current.getResponse().setFailedMessage(message);
//					return;
//				}
//				Current.invokeMethod(m, new Object[] { dga });
//
//				$S("BodyHTML", dga.getBodyHtml());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Priv(login = false)
//	public void toExcel(ZAction za) throws Exception {
//		HttpServletRequest request = za.getRequest();
//		HttpServletResponse response = za.getResponse();
//		request.setCharacterEncoding(Config.getGlobalCharset());
//		response.reset();
//		response.setContentType("application/octet-stream");
//		response.setHeader("Content-Disposition", "attachment; filename=Excel_" + DateUtil.getCurrentDateTime("yyyyMMddhhmmss") + ".xls");
//		try {
//			String xls = "_Excel_";
//			Mapx params = ServletUtil.getParameterMap(request);
//			String ID = params.getString(xls + "_ARK_ID");
//			String tagBody = params.getString(xls + "_ARK_TAGBODY");
//			String pageIndex = params.getString(xls + "_ARK_PAGEINDEX");
//			String pageSize = params.getString(xls + "_ARK_SIZE");
//			String pageTotal = params.getString(xls + "_ARK_PAGETOTAL");
//			String method = params.getString(xls + "_ARK_METHOD");
//			String pageFlag = params.getString(xls + "_ARK_PAGE");
//			String excelPageFlag = params.getString(xls + "_ARK_ToExcelPageFlag");
//			String strWidths = params.getString(xls + "_ARK_Widths");
//			String strIndexes = params.getString(xls + "_ARK_Indexes");
//			String strRows = params.getString(xls + "_ARK_Rows");
//
//			if ((tagBody != null) && (!tagBody.equals(""))) {
//				tagBody = StringUtil.htmlDecode(tagBody);
//			}
//			DataGridAction dga = new DataGridAction();
//			HtmlTable table = new HtmlTable();
//			dga.setMethod(method);
//			dga.setID(ID);
//			dga.setTagBody(tagBody);
//			if ("1".equals(excelPageFlag)) {
//				if ("true".equals(pageFlag)) {
//					dga.setPageFlag(true);
//					dga.setPageIndex(0);
//					dga.setPageSize(Integer.parseInt(pageTotal));
//				}
//
//			} else if ("true".equals(pageFlag)) {
//				dga.setPageFlag(true);
//				dga.setPageIndex(StringUtil.isEmpty(pageIndex) ? 0 : Integer.parseInt(pageIndex));
//				dga.setPageSize(StringUtil.isEmpty(pageSize) ? 0 : Integer.parseInt(pageSize));
//			}
//
//			table.parseHtml(dga.getTagBody());
//			dga.setTemplate(table);
//			dga.parse();
//
//			OutputStream os = response.getOutputStream();
//
//			Method m = Current.prepareMethod(request, response, method, new Class[] { DataGridAction.class });
//			if (!PrivCheck.check(m, request, response)) {
//				return;
//			}
//
//			RequestData map = Current.getRequest();
//			for (String k : map.keyArray()) {
//				if (k.startsWith(xls)) {
//					Object v = map.get(k);
//					map.remove(k);
//					map.put(k.substring(xls.length()), v);
//				}
//			}
//			dga.setParams(map);
//			dga.Response = Current.getResponse();
//			Current.invokeMethod(m, new Object[] { dga });
//
//			String[] rows = (String[]) null;
//			if (StringUtil.isNotEmpty(strRows)) {
//				rows = strRows.split(",");
//			}
//
//			HtmlTable ht = dga.getTable();
//			if ((ht.getChildren().size() > 0) && ("blank".equalsIgnoreCase(ht.getTR(ht.getChildren().size() - 1).getAttribute("ztype")))) {
//				ht.removeTR(ht.getChildren().size() - 1);
//			}
//			HtmlUtil.htmlTableToExcel(os, ht, strWidths.split(","), strIndexes.split(","), rows);
//
//			os.flush();
//			os.close();
//
//			os = null;
//			response.flushBuffer();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void sqlBind(DataGridAction dgp) {
//		dgp.bindData(new QueryBuilder((String) dgp.getParams().get("_ARK_DATAGRID_SQL"), new Object[0]));
//	}
//}