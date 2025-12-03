package io.arkx.framework.data.excel;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;

/**
 * @author Darkness
 * @date 2020年9月25日 下午3:58:42
 * @version V1.0
 */
public class JxlsUtils {

    public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> model) throws IOException {
        Context context = PoiTransformer.createInitialContext();
        if (model != null) {
            for (String key : model.keySet()) {
                context.putVar(key, model.get(key));
            }
        }
        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        Transformer transformer = jxlsHelper.createTransformer(is, os);
        // 获得配置
        JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig()
                .getExpressionEvaluator();
        // 设置静默模式，不报警告
        evaluator.getJexlEngine().setSilent(true);
        // 函数强制，自定义功能
        Map<String, Object> funcs = new HashMap<>();
        funcs.put("utils", new JxlsUtils()); // 添加自定义功能
        evaluator.getJexlEngine().setFunctions(funcs);
        // 必须要这个，否者表格函数统计会错乱
        jxlsHelper.setUseFastFormulaProcessor(false).processTemplate(context, transformer);
    }

    public static void exportExcel(File xls, File out, Map<String, Object> model)
            throws FileNotFoundException, IOException {
        exportExcel(new FileInputStream(xls), new FileOutputStream(out), model);
    }

    public static void exportExcel(String templatePath, OutputStream os, Map<String, Object> model) throws Exception {
        InputStream template = getTemplate(templatePath);
        if (template != null) {
            exportExcel(template, os, model);
        } else {
            throw new Exception("Excel 模板未找到。");
        }
        if (template != null) {
            template.close();
        }
    }

    private static String getConfigPath() {
        String pluginPath = JxlsUtils.class.getResource("/arkx.license").getPath();
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            if (pluginPath.startsWith("/")) {
                pluginPath = pluginPath.substring(1);
            }
        }
        if (pluginPath.startsWith("file:/")) {
            pluginPath = pluginPath.substring(6);
        } else if (pluginPath.startsWith("jar:file:/")) {
            pluginPath = pluginPath.substring(10);
        }
        String jarPath = "";
        if (pluginPath.indexOf(".jar!") > 0) {// 获取jar包中的schema
            jarPath = pluginPath.substring(0, pluginPath.indexOf(".jar!") + ".jar!".length() - 1);
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") < 0) {
                if (!jarPath.startsWith("/")) {
                    jarPath = "/" + jarPath;
                }
            }
        }
        String jarFolder = jarPath.substring(0, jarPath.lastIndexOf("/"));
        System.out.println("=================jarFolder: " + jarFolder);
        String configFolder = jarFolder + File.separator + "config";
        System.out.println("=================configFolder: " + configFolder);
        return configFolder;
    }

    // 获取jxls模版文件
    public static InputStream getTemplate(String path) {
        return JxlsUtils.class.getResourceAsStream(path);
    }

    // 日期格式化
    public String dateFmt(Date date, String fmt) {
        if (date == null) {
            return "";
        }
        try {
            SimpleDateFormat dateFmt = new SimpleDateFormat(fmt);
            return dateFmt.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // if判断
    public Object ifelse(boolean b, Object o1, Object o2) {
        return b ? o1 : o2;
    }

}
