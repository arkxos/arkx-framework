import io.arkx.framework.commons.util.FileUtil;

/**
 * @author Darkness
 * @date 2020-08-26 18:02:49
 * @version V1.0
 */
public class testa {

    public static void main(String[] args) throws ClassNotFoundException {
        String textString = FileUtil.readText("C:\\Users\\Administrator\\Desktop\\test.txt");
        String[] lines = textString.split("\r\n");
        for (String string : lines) {
            System.out.println("\"" + string + "\",");
        }
    }

}
