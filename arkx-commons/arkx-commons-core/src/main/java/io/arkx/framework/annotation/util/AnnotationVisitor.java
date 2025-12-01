package io.arkx.framework.annotation.util;

import io.arkx.framework.Config;
import io.arkx.framework.annotation.Alias;
import io.arkx.framework.annotation.Priv;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.core.scanner.AsmUtil;
import io.arkx.framework.core.scanner.BuiltResource;
import io.arkx.framework.core.scanner.BuiltResourceScanner;
import io.arkx.framework.core.scanner.IBuiltResourceVisitor;
import io.arkx.framework.data.xml.XMLElement;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 遍历所有类中ARK相关的注解
 */
@Slf4j
public class AnnotationVisitor implements IBuiltResourceVisitor {
	
	protected static long lastTime = 0;
	private static Lock lock = new ReentrantLock();
	private static String UIFACADE;
	private static String BaseUIFacade;
	private static Map<String, String> BaseUIFacadeMethods;
	private static String ALIAS;
	private static String PRIV;
	private static String UIMETHOD;
	private static final String VALUE = "value";
	private static final String ALONE = "alone";
	private static final String SCHEMA = "com/arkxos/schema/";
	private static final String FRAMEWORK = "com/arkxos/framework/";
	private static BuiltResourceScanner scanner;

	private static void init() {
//		UIFACADE = UIFacade.class.getName().replace('.', '/');
//		BaseUIFacade = BaseUIFacade.class.getName().replace('.', '/');
		
		ALIAS = Alias.class.getName().replace('.', '/');
		PRIV = Priv.class.getName().replace('.', '/');
//		UIMETHOD = UIMethod.class.getName().replace('.', '/');
		scanner = new BuiltResourceScanner(new AnnotationVisitor(), null);
	}

	private static boolean isNeedLoad() {
		return lastTime == 0 || Config.isDebugMode() && System.currentTimeMillis() - lastTime > 3000;
	}
	
	public static void load() {
		if (isNeedLoad()) {// 开发模式下3秒扫描一次
			log.info("[start]load rapidark Alias Priv Class");
			lock.lock();
			try {
				if (isNeedLoad()) {
					if (scanner == null) {
						init();
						scanner.scan(lastTime);
					}
					if (lastTime == 0) {
						// 如果有配置文件，则从配置文件中读取信息并替换注解中的信息
						// 读取配置文件中的方法别名
						List<XMLElement> nds = Config.getElements("*.mapping.method");
						for (int i = 0; i < nds.size(); i++) {
							XMLElement data = nds.get(i);
							String id = data.getAttributes().get("id");
							String value = data.getAttributes().get(VALUE);
							AliasMapping.put(id, value);
						}
					}
					lastTime = System.currentTimeMillis();
				}
			} finally {
				lock.unlock();

				log.info("[end]load rapidark Alias Priv Class");
			}
		}
	}

	@Override
	public String getExtendItemID() {
		return "com.arkxos.framework.annotation.AnnotationVisitor";
	}

	@Override
	public String getExtendItemName() {
		return "Annotation Visitor";
	}

	@Override
	public boolean match(BuiltResource br) {
		String fullName = br.getFullName();
		if ((!fullName.endsWith("BaseUIFacade.class")  && fullName.indexOf(FRAMEWORK) >= 0) || fullName.indexOf(SCHEMA) >= 0) {
			return false;
		}
		return fullName.endsWith("BaseUIFacade.class") || fullName.endsWith("UI.class") || fullName.endsWith("Method.class") || fullName.indexOf("UI$") > 0;
	}

	
	private Map<String, String> extraMethods(ClassNode classNode) {
		Map<String, String> result = new HashMap<>();
		if(classNode.name.endsWith("DataGridUI")) {
			System.out.println("===");
		}
		String classAlias = (String) AsmUtil.getAnnotationValue(classNode, ALIAS, VALUE);
		for (int i = 0; i < classNode.methods.size(); i++) {
			MethodNode methodNode = (MethodNode)classNode.methods.get(i);

			if (methodNode.name == null || methodNode.name.startsWith("<")) {
				continue;
			}
			if ((methodNode.access & Opcodes.ACC_PUBLIC) == 0) {// 必须是public方法
				continue;
			}
			if (!AsmUtil.isAnnotationPresent(methodNode, PRIV)) {// 未用@Priv标明的方法不允许外部访问
				continue;
			}
			boolean hasMethodAlias = AsmUtil.isAnnotationPresent(methodNode, ALIAS);
			boolean hasClassAlias = classAlias != null;
			
			if(!hasClassAlias) {
				classAlias = classNode.name.replaceAll("/", ".");
			}
			
			String alias = "";
			
			String methodAlias = "";
			if(hasMethodAlias) {
				methodAlias = (String) AsmUtil.getAnnotationValue(methodNode, ALIAS, VALUE);
			} else {
				methodAlias = methodNode.name;
			}
			
			alias = classAlias + "." + methodAlias;
			if (hasMethodAlias) {
				Boolean alone = (Boolean) AsmUtil.getAnnotationValue(methodNode, ALIAS, ALONE);
				if(alone != null && alone) {
					alias = methodAlias;
				}
			}
			
			String params = StringUtil.join(getParamNames(methodNode));
			result.put(alias, classNode.name + "#" + methodNode.name + "(" + StringUtil.noNull(params) + ")");
		}
		
		return result;
	}
	
	@Override
	public void visitClass(BuiltResource br, ClassNode classNode) {
		if(classNode.name.indexOf("org/ark") != -1) {
			return;
		}
		
		if(classNode.name.indexOf("Operating") != -1) {
//			System.out.println("loading os");
		}
//		System.out.println("load " + classNode.name);
		if (BaseUIFacade.equals(classNode.name)) {
			Map<String, String> extraMethods = extraMethods(classNode);
			BaseUIFacadeMethods = extraMethods;
		} else if ((classNode.access & Opcodes.ACC_ABSTRACT) != 0) {
			return;// 不能是虚拟类
		}
		
		if (BaseUIFacade.equals(classNode.superName)) {
			String classAlias = (String) AsmUtil.getAnnotationValue(classNode, ALIAS, VALUE);
			for (Map.Entry<String, String> entry : BaseUIFacadeMethods.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue();
				name = name.replace("BaseUIFacade", classAlias);
				value = value.replace(BaseUIFacade, classNode.name);
				AliasMapping.put(name, value);
			}
			
			Map<String, String> extraMethods = extraMethods(classNode);
			for (Map.Entry<String,String> method : extraMethods.entrySet()) {
				AliasMapping.put(method.getKey(), method.getValue());
			}
		} else if (UIFACADE.equals(classNode.superName)) {
			Map<String, String> extraMethods = extraMethods(classNode);
			for (Map.Entry<String,String> method : extraMethods.entrySet()) {
				AliasMapping.put(method.getKey(), method.getValue());
			}
		} else if (classNode.superName.equals(UIMETHOD)) {
			if (AsmUtil.isAnnotationPresent(classNode, ALIAS) && AsmUtil.isAnnotationPresent(classNode, PRIV)) {
				AliasMapping.put(AsmUtil.getAnnotationValue(classNode, ALIAS, VALUE).toString(), classNode.name);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String[] getParamNames(MethodNode node) {
		if (node == null || ObjectUtil.isEmpty(node.localVariables))
			return null;
		int size = Type.getArgumentTypes(node.desc).length;
		if (size == 0)
			return null;
		if ((node.access & 8) != 0)
			size--;
		String names[] = new String[Type.getArgumentTypes(node.desc).length];
		int i = 0;
		Iterator<LocalVariableNode> iterator = node.localVariables.iterator();
		while (iterator.hasNext()) {
			LocalVariableNode a = iterator.next();
			if (a.index == 0 && a.name.equals("this"))
				continue;
			if (i >= names.length)
				break;
			names[i++] = a.name;
		}
		if (node.visibleParameterAnnotations != null) {
			String paramAnnoation = "Lcom/arkxos/framework/annotation/Param;";
			for (i = 0; i < node.visibleParameterAnnotations.length; i++) {
				List<AnnotationNode> list = node.visibleParameterAnnotations[i];
				if (list != null) {
					for (Iterator<AnnotationNode> iterator1 = list.iterator(); iterator1.hasNext();) {
						AnnotationNode an = iterator1.next();
						if (an != null && an.desc != null && an.desc.equals(paramAnnoation) && an.values != null && an.values.size() > 1) {
							names[i] = an.values.get(1).toString();
						}
					}

				}
			}

		}
		return names;
	}

	@Override
	public void visitResource(BuiltResource br) {

	}

	@Override
	public void visitInnerClass(BuiltResource br, ClassNode cn, ClassNode icn) {
		if ((icn.access & Opcodes.ACC_PUBLIC) == 0) {
			return;
		}
		if (!icn.name.startsWith(cn.name)) {
			return;
		}
		if (!AsmUtil.isAnnotationPresent(icn, PRIV)) {// 未用@Priv标明的方法不允许外部访问
			return;
		}
		if (!icn.superName.equals(UIMETHOD)) {
			return;
		}
		boolean flag = AsmUtil.isAnnotationPresent(icn, ALIAS);
		String classAlias = (String) AsmUtil.getAnnotationValue(cn, ALIAS, VALUE);
		if(classAlias.indexOf("org/ark") != -1) {
			return;
		}
		if (classAlias != null && !flag) {// 说明方法没有别名但类有别名
			String name = icn.name;
			int index = name.indexOf('/');
			if (index > 0) {
				name = name.substring(index + 1);
			}
			AliasMapping.put(classAlias + "." + name, icn.name);
		}
		if (flag) {
			String innerAlias = (String) AsmUtil.getAnnotationValue(icn, ALIAS, VALUE);
			Boolean innerAlone = (Boolean) AsmUtil.getAnnotationValue(icn, ALIAS, ALONE);
			if ((innerAlone == null || !innerAlone) && classAlias != null) {
				innerAlias = classAlias + "." + innerAlias;
			}
			AliasMapping.put(innerAlias, icn.name);
		}
	}

}
