package com.arkxos.framework.classweaver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.arkxos.framework.preloader.Util;

public class WeaverScanner {
	static HashSet<String> set = null;

	public static synchronized void scan() {
		if (set != null) {
			return;
		}
		set = new HashSet<>();
		String pluginPath = Util.getPluginPath();
		File weavedDir = new File(pluginPath + "weaved/");
		if (!weavedDir.exists()) {
			weavedDir.mkdirs();
		}
		String classPath = pluginPath + "/classes/";
		loadFromDir(new File(classPath));

		String jarPath = pluginPath + "/lib/";
		File[] arrayOfFile;
		File jarPathFile = new File(jarPath);
		if(jarPathFile.exists()) {
			arrayOfFile = jarPathFile.listFiles();
			if(arrayOfFile != null) {
				int j = arrayOfFile.length;
				for (int i = 0; i < j; i++) {
					File f = arrayOfFile[i];
					if ((f.isFile()) && (f.getName().endsWith(".plugin.jar"))) {
						loadFromJar(f);
					}
				}
			}
		}
	}

	public static boolean isWeaved(String name) {
		return set.contains(name);
	}

	/* Error */
	private static void loadFromDir(File dir) {
		// Byte code:
		// 0: aload_0
		// 1: ifnull +10 -> 11
		// 4: aload_0
		// 5: invokevirtual 54 java/io/File:exists ()Z
		// 8: ifne +4 -> 12
		// 11: return
		// 12: aload_0
		// 13: invokevirtual 69 java/io/File:listFiles ()[Ljava/io/File;
		// 16: dup
		// 17: astore 4
		// 19: arraylength
		// 20: istore_3
		// 21: iconst_0
		// 22: istore_2
		// 23: goto +125 -> 148
		// 26: aload 4
		// 28: iload_2
		// 29: aaload
		// 30: astore_1
		// 31: aload_1
		// 32: invokevirtual 104 java/io/File:isDirectory ()Z
		// 35: ifeq +10 -> 45
		// 38: aload_1
		// 39: invokestatic 63 com/abigdreamer/ark/classweaver/WeaverScanner:loadFromDir
		// (Ljava/io/File;)V
		// 42: goto +103 -> 145
		// 45: aload_1
		// 46: invokevirtual 73 java/io/File:isFile ()Z
		// 49: ifeq +96 -> 145
		// 52: aload_1
		// 53: invokevirtual 76 java/io/File:getName ()Ljava/lang/String;
		// 56: ldc 107
		// 58: invokevirtual 81 java/lang/String:endsWith (Ljava/lang/String;)Z
		// 61: ifeq +84 -> 145
		// 64: aconst_null
		// 65: astore 5
		// 67: new 109 java/io/FileInputStream
		// 70: dup
		// 71: aload_1
		// 72: invokespecial 111 java/io/FileInputStream:<init>
		// (Ljava/io/File;)V
		// 75: astore 5
		// 77: aload 5
		// 79: invokestatic 113 com/abigdreamer/ark/classweaver/WeaverScanner:tryWeave
		// (Ljava/io/InputStream;)V
		// 82: goto +48 -> 130
		// 85: astore 6
		// 87: aload 6
		// 89: invokevirtual 117 java/lang/Exception:printStackTrace ()V
		// 92: aload 5
		// 94: invokevirtual 122 java/io/FileInputStream:close ()V
		// 97: goto +48 -> 145
		// 100: astore 8
		// 102: aload 8
		// 104: invokevirtual 117 java/lang/Exception:printStackTrace ()V
		// 107: goto +38 -> 145
		// 110: astore 7
		// 112: aload 5
		// 114: invokevirtual 122 java/io/FileInputStream:close ()V
		// 117: goto +10 -> 127
		// 120: astore 8
		// 122: aload 8
		// 124: invokevirtual 117 java/lang/Exception:printStackTrace ()V
		// 127: aload 7
		// 129: athrow
		// 130: aload 5
		// 132: invokevirtual 122 java/io/FileInputStream:close ()V
		// 135: goto +10 -> 145
		// 138: astore 8
		// 140: aload 8
		// 142: invokevirtual 117 java/lang/Exception:printStackTrace ()V
		// 145: iinc 2 1
		// 148: iload_2
		// 149: iload_3
		// 150: if_icmplt -124 -> 26
		// 153: return
		// Line number table:
		// Java source line #52 -> byte code offset #0
		// Java source line #53 -> byte code offset #11
		// Java source line #55 -> byte code offset #12
		// Java source line #56 -> byte code offset #31
		// Java source line #57 -> byte code offset #38
		// Java source line #58 -> byte code offset #45
		// Java source line #59 -> byte code offset #64
		// Java source line #61 -> byte code offset #67
		// Java source line #62 -> byte code offset #77
		// Java source line #63 -> byte code offset #85
		// Java source line #64 -> byte code offset #87
		// Java source line #67 -> byte code offset #92
		// Java source line #68 -> byte code offset #100
		// Java source line #69 -> byte code offset #102
		// Java source line #65 -> byte code offset #110
		// Java source line #67 -> byte code offset #112
		// Java source line #68 -> byte code offset #120
		// Java source line #69 -> byte code offset #122
		// Java source line #71 -> byte code offset #127
		// Java source line #67 -> byte code offset #130
		// Java source line #68 -> byte code offset #138
		// Java source line #69 -> byte code offset #140
		// Java source line #55 -> byte code offset #145
		// Java source line #74 -> byte code offset #153
		// Local variable table:
		// start length slot name signature
		// 0 154 0 dir File
		// 30 42 1 f File
		// 22 129 2 i int
		// 20 131 3 j int
		// 17 10 4 arrayOfFile File[]
		// 65 66 5 fis java.io.FileInputStream
		// 85 3 6 e Exception
		// 110 18 7 localObject Object
		// 100 3 8 e Exception
		// 120 3 8 e Exception
		// 138 3 8 e Exception
		// Exception table:
		// from to target type
		// 67 82 85 java/lang/Exception
		// 92 97 100 java/lang/Exception
		// 67 92 110 finally
		// 112 117 120 java/lang/Exception
		// 130 135 138 java/lang/Exception
	}

	/* Error */
	private static void loadFromJar(File f) {
		// Byte code:
		// 0: new 132 java/util/jar/JarFile
		// 3: dup
		// 4: aload_0
		// 5: invokespecial 134 java/util/jar/JarFile:<init> (Ljava/io/File;)V
		// 8: astore_1
		// 9: aload_1
		// 10: invokevirtual 135 java/util/jar/JarFile:entries
		// ()Ljava/util/Enumeration;
		// 13: astore_2
		// 14: goto +107 -> 121
		// 17: aload_2
		// 18: invokeinterface 139 1 0
		// 23: checkcast 145 java/util/jar/JarEntry
		// 26: astore_3
		// 27: aload_3
		// 28: invokevirtual 147 java/util/jar/JarEntry:isDirectory ()Z
		// 31: ifne +90 -> 121
		// 34: aload_3
		// 35: invokevirtual 148 java/util/jar/JarEntry:getName
		// ()Ljava/lang/String;
		// 38: ldc 107
		// 40: invokevirtual 81 java/lang/String:endsWith (Ljava/lang/String;)Z
		// 43: ifeq +78 -> 121
		// 46: aload_1
		// 47: aload_3
		// 48: invokevirtual 149 java/util/jar/JarFile:getInputStream
		// (Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;
		// 51: astore 4
		// 53: aload 4
		// 55: invokestatic 113 com/abigdreamer/ark/classweaver/WeaverScanner:tryWeave
		// (Ljava/io/InputStream;)V
		// 58: goto +48 -> 106
		// 61: astore 5
		// 63: aload 5
		// 65: invokevirtual 117 java/lang/Exception:printStackTrace ()V
		// 68: aload 4
		// 70: ifnull +51 -> 121
		// 73: aload 4
		// 75: invokevirtual 153 java/io/InputStream:close ()V
		// 78: goto +43 -> 121
		// 81: astore 7
		// 83: goto +38 -> 121
		// 86: astore 6
		// 88: aload 4
		// 90: ifnull +13 -> 103
		// 93: aload 4
		// 95: invokevirtual 153 java/io/InputStream:close ()V
		// 98: goto +5 -> 103
		// 101: astore 7
		// 103: aload 6
		// 105: athrow
		// 106: aload 4
		// 108: ifnull +13 -> 121
		// 111: aload 4
		// 113: invokevirtual 153 java/io/InputStream:close ()V
		// 116: goto +5 -> 121
		// 119: astore 7
		// 121: aload_2
		// 122: invokeinterface 156 1 0
		// 127: ifne -110 -> 17
		// 130: goto +8 -> 138
		// 133: astore_1
		// 134: aload_1
		// 135: invokevirtual 117 java/lang/Exception:printStackTrace ()V
		// 138: return
		// Line number table:
		// Java source line #78 -> byte code offset #0
		// Java source line #79 -> byte code offset #9
		// Java source line #80 -> byte code offset #14
		// Java source line #81 -> byte code offset #17
		// Java source line #82 -> byte code offset #27
		// Java source line #83 -> byte code offset #46
		// Java source line #85 -> byte code offset #53
		// Java source line #86 -> byte code offset #61
		// Java source line #87 -> byte code offset #63
		// Java source line #89 -> byte code offset #68
		// Java source line #91 -> byte code offset #73
		// Java source line #92 -> byte code offset #81
		// Java source line #88 -> byte code offset #86
		// Java source line #89 -> byte code offset #88
		// Java source line #91 -> byte code offset #93
		// Java source line #92 -> byte code offset #101
		// Java source line #95 -> byte code offset #103
		// Java source line #89 -> byte code offset #106
		// Java source line #91 -> byte code offset #111
		// Java source line #92 -> byte code offset #119
		// Java source line #80 -> byte code offset #121
		// Java source line #98 -> byte code offset #133
		// Java source line #99 -> byte code offset #134
		// Java source line #101 -> byte code offset #138
		// Local variable table:
		// start length slot name signature
		// 0 139 0 f File
		// 8 39 1 jf java.util.jar.JarFile
		// 133 2 1 e4 Exception
		// 13 109 2 e java.util.Enumeration<java.util.jar.JarEntry>
		// 26 22 3 je java.util.jar.JarEntry
		// 51 61 4 is InputStream
		// 61 3 5 e2 Exception
		// 86 18 6 localObject Object
		// 81 1 7 localException1 Exception
		// 101 1 7 localException2 Exception
		// 119 1 7 localException3 Exception
		// Exception table:
		// from to target type
		// 53 58 61 java/lang/Exception
		// 73 78 81 java/lang/Exception
		// 53 68 86 finally
		// 93 98 101 java/lang/Exception
		// 111 116 119 java/lang/Exception
		// 0 130 133 java/lang/Exception
	}

	private static void tryWeave(InputStream is) throws IOException {
		ClassWeaver weaver = new ClassWeaver(Util.readByte(is));
		if (weaver.isReplaced()) {
			try {
				ClassNode cnt = weaver.weave();
				if (cnt != null) {
					ClassWriter cw = new ClassWriter(0);
					cnt.accept(cw);
					byte[] data = cw.toByteArray();
					String weavedPath = Util.getPluginPath() + "weaved/" + weaver.getTargetClassName() + ".class";
					Util.writeByte(new File(weavedPath), data);
					set.add(weaver.getTargetClassName());
					System.out.println("Class weaving success:" + weaver.getTargetClassName());
				}
			} catch (Exception e) {
				System.err.println("Class weaving failed:" + weaver.getTargetClassName());
				e.printStackTrace();
			}
		}
	}
}
