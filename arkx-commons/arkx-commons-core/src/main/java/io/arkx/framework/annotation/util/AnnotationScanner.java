package io.arkx.framework.annotation.util;

import io.arkx.framework.core.scanner.AsmUtil;
import io.arkx.framework.core.scanner.BuiltResource;
import io.arkx.framework.core.scanner.BuiltResourceScanner;
import io.arkx.framework.core.scanner.IBuiltResourceVisitor;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 遍历所有类中的注解
 */
public class AnnotationScanner implements IBuiltResourceVisitor {
	
	private static Lock lock = new ReentrantLock();
	private static Map<String, List<String>> annotationClassesMap = new HashMap<>();
	
	public static List<String> find(Class<?> annotationClass) {
		if(annotationClassesMap.get(annotationClass.getName()) != null) {
			return annotationClassesMap.get(annotationClass.getName());
		}
		
		lock.lock();
		try {
			AnnotationScanner annotationScanner = new AnnotationScanner(annotationClass.getName());
			BuiltResourceScanner scanner = new BuiltResourceScanner(annotationScanner, null);
			scanner.scan(0);

			annotationClassesMap.put(annotationClass.getName(), annotationScanner.classNames);
		} finally {
			lock.unlock();
		}
		
		return annotationClassesMap.get(annotationClass.getName());
	}
	
	private  String ALIAS;
	private List<String> classNames = new ArrayList<>();
	
	public AnnotationScanner(String annotationClassName) {
		ALIAS = annotationClassName.replace('.', '/');
	}

	@Override
	public String getExtendItemID() {
		return "io.arkx.framework.annotation.AnnotationScanner";
	}

	@Override
	public String getExtendItemName() {
		return "AnnotationScanner";
	}

	@Override
	public boolean match(BuiltResource br) {
		String fullName = br.getFullName();
		return fullName.endsWith(".class");
	}

	@Override
	public void visitClass(BuiltResource br, ClassNode classNode) {
		if(AsmUtil.isAnnotationPresent(classNode, ALIAS)) {
			classNames.add(classNode.name);
		}
	}
	
	@Override
	public void visitResource(BuiltResource br) {
	}

	@Override
	public void visitInnerClass(BuiltResource br, ClassNode cn, ClassNode icn) {
	}

}
