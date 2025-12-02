package io.arkx.framework.preloader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import io.arkx.framework.classweaver.WeaverScanner;

public class PreClassLoader extends URLClassLoader {
    private List<JarClassLoader> loaders = null;
    private Vector<Class<?>> loadedClass = new Vector<>();
    private static PreClassLoader instance = null;
    private ClassModifyScanner scanner = null;
    static PrintStream out = System.out;
    static PrintStream err = System.err;

    public static Class<?> load(String className) throws ClassNotFoundException {
        init();
        return instance.loadClass(className);
    }

    public static PreClassLoader getInstance() {
        init();
        return instance;
    }

    public static void destory() {
        if (instance == null) {
            return;
        }
        instance.scanner.destory();
        instance.scanner = null;
        if (instance.loaders != null) {
            for (JarClassLoader jc : instance.loaders) {
                try {
                    jc.jf.close();
                } catch (IOException localIOException) {
                }
            }
            instance.loaders.clear();
            instance.loaders = null;
            instance.loadedClass.clear();
            instance.loadedClass = null;
        }
        instance = null;
    }

    private static void init() {
        if (instance == null) {
            synchronized (PreClassLoader.class) {
                if (instance == null) {
                    Updater.update();
                    WeaverScanner.scan();
                    instance = new PreClassLoader();
                    instance.loadedClass = new Vector<>();

                    System.out.println("Reloader.newPreClassLoader:" + instance);
                    instance.scanner = new ClassModifyScanner();
                    instance.scanner.start();
                }
            }
        }
    }

    protected static void reloadAll() {
        instance = null;
        init();
    }

    private PreClassLoader() {
        super(new URL[0], PreClassLoader.class.getClassLoader());
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    public byte[] loadData(String name) throws ClassNotFoundException {
        if (name.indexOf("AbstractHttpMessageConverter") != -1) {
            System.out.println("ddd");
        }
        String pathName = name.replace('.', '/');
        byte[] data = loadClassData(pathName);
        if (data == null) {
            data = tryLoadFromJar(name);
        }
        if (data == null) {
            throw new ClassNotFoundException(name);
        }
        return data;
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (this.loadedClass == null) {
            return null;
        }
        Class<?> clazz = null;
        byte[] data = loadData(name);
        if (WeaverScanner.isWeaved(name)) {
            String weavedPath = Util.getPluginPath() + "weaved/" + name + ".class";
            if (new File(weavedPath).exists()) {
                data = Util.readByte(weavedPath);
            }
        }
        clazz = defineClass(name, data, 0, data.length);
        this.loadedClass.add(clazz);
        return clazz;
    }

    /* Error */
    private byte[] loadClassData(String name) {
        InputStream fis;
        byte data[];
        fis = null;
        data = (byte[]) null;
        File f = new File((new StringBuilder(String.valueOf(Util.getPluginPath()))).append("classes/").append(name)
                .append(".class").toString());
        if (!f.exists()) {
            try {
                if (fis != null)
                    fis.close();
            } catch (Exception exception1) {
            }
            return null;
        }
        try {
            fis = new FileInputStream(f);
            data = Util.readByte(fis);
            scanner.addClass(f.getAbsolutePath(), f.lastModified());
        } catch (IOException ioexception) {
            try {
                if (fis != null)
                    fis.close();
            } catch (Exception exception2) {
            }
        }
        try {
            if (fis != null)
                fis.close();
        } catch (Exception exception3) {
        }
        return data;
    }

    private void initJarLoaders() {
        if (this.loaders == null) {
            synchronized (this) {
                if (this.loaders == null) {
                    ArrayList<JarClassLoader> tmp = new ArrayList<>();
                    addJarLoadersFromDir(new File(Util.getPluginPath() + "lib/"), tmp);
                    addJarLoadersFromDir(new File(Util.getPluginPath() + "required/"), tmp);
                    this.loaders = tmp;
                }
            }
        }
    }

    private void addJarLoadersFromDir(File f, ArrayList<JarClassLoader> tmp) {
        if (f.exists()) {
            File[] arrayOfFile;
            int j = (arrayOfFile = f.listFiles()).length;
            for (int i = 0; i < j; i++) {
                File f2 = arrayOfFile[i];
                if (f2.isDirectory()) {
                    addJarLoadersFromDir(f2, tmp);
                } else if ((f2.getName().endsWith(".jar")) && (!f2.getName().endsWith(".ui.jar"))
                        && (!f2.getName().endsWith(".resource.jar"))) {
                    try {
                        tmp.add(new JarClassLoader(f2.getAbsolutePath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private byte[] tryLoadFromJar(String name) {
        initJarLoaders();
        if (this.loaders != null) {
            for (JarClassLoader loader : this.loaders) {
                try {
                    byte[] bs = loader.loadClassData(name);
                    if (bs != null) {
                        String packageName = null;
                        int pos = name.lastIndexOf('.');
                        if (pos != -1) {
                            packageName = name.substring(0, pos);
                        }
                        Package pkg = null;
                        if (packageName != null) {
                            pkg = getPackage(packageName);
                            if (pkg == null) {
                                try {
                                    if (loader.jf.getManifest() == null) {
                                        definePackage(packageName, null, null, null, null, null, null, null);
                                    } else {
                                        definePackage(packageName, loader.jf.getManifest(), null);
                                    }
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        return bs;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private URL tryFindResourceFromJar(String name) {
        initJarLoaders();
        if (this.loaders != null) {
            for (JarClassLoader loader : this.loaders) {
                URL url = loader.findResource(name);
                if (url != null) {
                    return url;
                }
            }
        }
        return null;
    }

    private List<URL> tryFindResourcesFromJar(String name) {
        initJarLoaders();
        List<URL> list = new ArrayList();
        if (this.loaders != null) {
            for (JarClassLoader loader : this.loaders) {
                URL url = loader.findResource(name);
                if (url != null) {
                    list.add(url);
                }
            }
        }
        return list;
    }

    public URL findResource(String name) {
        try {
            URL url = super.findResource(name);
            if (url != null) {
                return url;
            }
            String fileName = Util.getPluginPath() + "classes/" + name;
            File f = new File(fileName);
            if (!f.exists()) {
                return tryFindResourceFromJar(name);
            }
            return f.toURI().toURL();
        } catch (MalformedURLException mue) {
        }
        return null;
    }

    public Enumeration<URL> findResources(String name) {
        try {
            Vector<URL> list = new Vector();
            String fileName = Util.getPluginPath() + "classes/" + name;
            File f = new File(fileName);
            if (!f.exists()) {
                list.addAll(tryFindResourcesFromJar(name));
            } else {
                list.add(f.toURI().toURL());
            }
            URL url = super.findResource(name);
            if (url != null) {
                list.add(url);
            }
            return list.elements();
        } catch (MalformedURLException mue) {
        }
        return null;
    }

    public static class JarClassLoader {

        public byte[] loadClassData(String name) throws ClassNotFoundException {
            byte data[];
            InputStream fis = null;
            data = (byte[]) null;
            InputStream is;
            String pathName = name.replace('.', '/') + ".class";
            ZipEntry ze = jf.getEntry(pathName);
            if (ze == null)
                return null;
            try {
                is = jf.getInputStream(ze);
                data = Util.readByte(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fis != null)
                    fis.close();
            } catch (Exception exception2) {
            }
            return data;
        }

        public URL findResource(String name) {
            URL url;
            InputStream fis;
            url = null;
            fis = null;
            try {
                ZipEntry ze = jf.getEntry(name);
                if (ze != null) {
                    url = new URL("jar:file:" + (fileName.startsWith("/") ? "" : "/") + fileName + "!/" + name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception exception1) {
            }
            return url;
        }

        private String fileName;
        private JarFile jf;

        public JarClassLoader(String fileName) throws IOException {
            this.fileName = fileName;
            jf = new JarFile(fileName);
        }

        @Override
        public String toString() {
            return "JarClassLoader:" + this.fileName;
        }
    }

    public Class<?> defineClassEx(String name, byte[] bytes, int offset, int len, ProtectionDomain domain) {
        return defineClass(name, bytes, offset, len, domain);
    }

    public class InternalResource {
        public PreClassLoader.JarClassLoader Loader;
        public long LastModified;

        public InternalResource(PreClassLoader.JarClassLoader Loader, long LastModified) {
            this.LastModified = LastModified;
            this.Loader = Loader;
        }
    }

    protected Vector<Class<?>> getLoadedClasses() {
        return this.loadedClass;
    }

    public void addUrl(URL url) {
        super.addURL(url);
    }
}
