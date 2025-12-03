package io.arkx.framework.framework.js;

import java.io.File;
import java.io.IOException;

import io.arkx.framework.commons.util.FileUtil;

/**
 * @author Darkness
 * @date 2016年10月11日 下午4:55:59
 * @version V1.0
 */
public class ArkJsGenerator {

    public static void main(String[] args) throws IOException {
        String basePath = "C:\\Users\\Administrator\\git\\rapid-ark-v2\\rapid-ark-web\\src\\main\\webapp\\arkjs3\\framework\\_source\\";
        if (!new File(basePath).exists()) {
            FileUtil.mkdir(basePath);
        }

        String jsFiles = "server2.0.js UIX.js console.js componentManager.js UICompBase.js dialog2.0.js msgPop.js datePicker.js tip.js verify.js dataList2.0.js layer.js webuploader.js uploader2.0.js fieldEditor.js combox.js switchable.js";
        jsFiles += " suggest.js form.js button.js menu.js toolbar.js IEFix.js pageBar.js dataGrid2.1.js dataGrid_plugins.js afloat.js contextMenu.js tree2.1.js tabPage.js panel.js progress.js layout.js splitter.js progressBar.js slider.js scrollBar.js scrollPanel.js numberField.js";
        String[] files = jsFiles.split(" ");
        for (String file : files) {
            if (!new File(basePath + "components\\").exists()) {
                FileUtil.mkdir(basePath + "components\\");
            }
            File file2 = new File(basePath + "components\\" + file);
            file2.createNewFile();
        }

        jsFiles = "core.js lodash.compat.min.js function.js class.js helper.js string.js array.js date.js dateTime.js util.js dataCollection.js JSON.js customEvent.js observable.js prototype.js dom.js event.js eventManager.js $Event.js $Node.js $jqueryUI.min.js url.js page.js ajaxPage.js node.js misc.js resize.js drag.js dragManager.js cookie.js storage.js dataGSetter.js statable.js form.js lang.js";
        files = jsFiles.split(" ");
        for (String file : files) {
            if (!new File(basePath + "core\\").exists()) {
                FileUtil.mkdir(basePath + "core\\");
            }
            File file2 = new File(basePath + "core\\" + file);
            file2.createNewFile();
        }

        jsFiles = "zh-cn.js zh-tw.js en.js";
        files = jsFiles.split(" ");
        for (String file : files) {
            if (!new File(basePath + "lang\\").exists()) {
                FileUtil.mkdir(basePath + "lang\\");
            }
            File file2 = new File(basePath + "lang\\" + file);
            file2.createNewFile();
        }
    }

}
