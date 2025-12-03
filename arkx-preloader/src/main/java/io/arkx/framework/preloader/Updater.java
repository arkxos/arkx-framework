package io.arkx.framework.preloader;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// fix

public class Updater {

    public static class UpdateCommand {

        public String Command;

        public String Type;

        public UpdateCommand() {
        }

    }

    public static class UpdateCommandList {

        public void parse(String commands) {
            String lines[] = commands.split("\\n");
            String as[];
            int j = (as = lines).length;
            for (int i = 0; i < j; i++) {
                String line = as[i];
                line = line.trim();
                UpdateCommand uc = null;
                if (line.equals("#[DB]")) {
                    uc = new UpdateCommand();
                    uc.Type = "#[DB]";
                } else if (line.equals("#[REVISION]")) {
                    uc = new UpdateCommand();
                    uc.Type = "#[REVISION]";
                } else if (line.equals("#[DELETE]")) {
                    uc = new UpdateCommand();
                    uc.Type = "#[DELETE]";
                } else {
                    if (!line.equals("#[JAVA]"))
                        continue;
                    uc = new UpdateCommand();
                    uc.Type = "#[JAVA]";
                }
                uc.Command = line.substring(uc.Type.length());
                list.add(uc);
            }

        }

        public List getAll() {
            return list;
        }

        List list;

        public UpdateCommandList() {
            list = new ArrayList();
        }

    }

    public Updater() {
    }

    public static void update() {
        StringBuilder sb;
        String pluginPath;
        String UpdateTime;
        sb = new StringBuilder();
        pluginPath = Util.getPluginPath();
        UpdateTime = "0";
        String path;
        path = (new StringBuilder(String.valueOf(pluginPath))).append("update/current/").toString();
        normalize(path);
        if (!(new File(path)).exists()) {
            try {
                if (sb.length() != 0)
                    writeText((new StringBuilder(String.valueOf(pluginPath))).append("update/Updater_")
                            .append(UpdateTime).append(".log").toString(), sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        File pluginPathFile = new File(pluginPath);
        if (!pluginPathFile.exists()) {
            try {
                if (sb.length() != 0)
                    writeText((new StringBuilder(String.valueOf(pluginPath))).append("update/Updater_")
                            .append(UpdateTime).append(".log").toString(), sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            String uiPath = pluginPath.substring(0, pluginPath.lastIndexOf("WEB-INF"));
            String commandFile = (new StringBuilder(String.valueOf(path))).append("commands.txt").toString();
            if ((new File(commandFile)).exists()) {
                UpdateCommandList list = new UpdateCommandList();
                list.parse(readText(commandFile, "UTF-8"));
                for (Iterator iterator = list.getAll().iterator(); iterator.hasNext();) {
                    UpdateCommand uc = (UpdateCommand) iterator.next();
                    if (uc.Type.equals("#[REVISION]")) {
                        UpdateTime = uc.Command.trim();
                        break;
                    }
                }

                for (Iterator iterator1 = list.getAll().iterator(); iterator1.hasNext();) {
                    UpdateCommand uc = (UpdateCommand) iterator1.next();
                    if (uc.Type.equals("#[DELETE]")) {
                        String f = uc.Command;
                        if (f.startsWith("UI/")) {
                            f = f.substring(3);
                            f = (new StringBuilder(String.valueOf(uiPath))).append(f.trim()).toString();
                            f = normalize(f);
                            if ((new File(f)).exists()) {
                                if ((new File(f)).delete())
                                    sb.append((new StringBuilder("Delete ")).append(f).append(" success").toString());
                                else
                                    sb.append((new StringBuilder("Delete ")).append(f).append(" failed").toString());
                            } else {
                                sb.append((new StringBuilder("Delete ")).append(f).append(" failed,file not found")
                                        .toString());
                            }
                        }
                    }
                }

            }
            copy(new File(path), uiPath, sb);
            delete(new File(path));
        } catch (Throwable t) {
            sb.append(t.getMessage());
            StackTraceElement stack[] = t.getStackTrace();
            for (int i = 0; i < stack.length; i++) {
                StackTraceElement ste = stack[i];
                sb.append("\t");
                sb.append(ste.getClassName());
                sb.append(".");
                sb.append(ste.getMethodName());
                sb.append("(),LineNo.:");
                sb.append(ste.getLineNumber());
                sb.append("\n");
            }

        }
        try {
            if (sb.length() != 0)
                writeText((new StringBuilder(String.valueOf(pluginPath))).append("update/Updater_").append(UpdateTime)
                        .append(".log").toString(), sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void delete(File parent) {
        if (parent.isFile()) {
            parent.delete();
            return;
        }
        File afile[];
        int k = (afile = parent.listFiles()).length;
        for (int i = 0; i < k; i++) {
            File f = afile[i];
            if (f.isDirectory())
                delete(f);
        }

        k = (afile = parent.listFiles()).length;
        for (int j = 0; j < k; j++) {
            File f = afile[j];
            f.delete();
        }

    }

    private static void copy(File path, String parentPath, StringBuilder sb) throws Exception {
        int i;
        int j;
        File afile[];
        j = (afile = path.listFiles()).length;
        i = 0;
        if (i < j) {
            File f;
            InputStream is;
            f = afile[i];
            if (f.isDirectory()) {
                copy(f, (new StringBuilder(String.valueOf(parentPath))).append("/").append(f.getName()).toString(), sb);
                // continue; /* Loop/switch isn't completed */
            }
            is = null;
            is = new FileInputStream(f);
            byte bs[] = readByte(is);
            if (!(new File(parentPath)).exists())
                (new File(parentPath)).mkdirs();
            writeByte((new StringBuilder(String.valueOf(parentPath))).append("/").append(f.getName()).toString(), bs);
            if (is != null)
                is.close();
            i++;
        } else {
        }
    }

    public static String normalize(String path) {
        path = path.replace('\\', '/');
        path = path.replaceAll("\\.\\.\\/", "/");
        path = path.replaceAll("\\.\\/", "/");
        path = path.replaceAll("/+", "/");
        return path;
    }

    public static String readText(String f, String encoding) throws IOException {
        InputStream is = null;
        String s;
        is = new FileInputStream(f);
        byte bs[] = readByte(is);
        s = new String(bs, encoding);
        if (is != null)
            is.close();
        return s;
    }

    public static byte[] readByte(InputStream is) throws IOException {
        byte buffer[] = new byte[8192];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        do {
            int bytesRead = -1;
            bytesRead = is.read(buffer);
            if (bytesRead != -1)
                os.write(buffer, 0, bytesRead);
            else
                return os.toByteArray();
        } while (true);
    }

    public static boolean writeByte(String fileName, byte b[]) throws IOException {
        fileName = normalize(fileName);
        BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fileName));
        fos.write(b);
        fos.close();
        return true;
    }

    public static boolean writeText(String fileName, String content) throws IOException {
        fileName = normalize(fileName);
        byte bs[] = content.getBytes("UTF-8");
        writeByte(fileName, bs);
        return true;
    }

    public static final String COMMAND_REVISION = "#[REVISION]";

    public static final String COMMAND_DELETE = "#[DELETE]";

    public static final String COMMAND_DB = "#[DB]";

    public static final String COMMAND_JAVA = "#[JAVA]";

}
