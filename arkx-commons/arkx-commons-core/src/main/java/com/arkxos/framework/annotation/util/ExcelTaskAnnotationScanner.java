package com.arkxos.framework.annotation.util;

import java.lang.annotation.Annotation;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.objectweb.asm.tree.ClassNode;

import com.arkxos.framework.core.scanner.AsmUtil;
import com.arkxos.framework.core.scanner.BuiltResource;
import com.arkxos.framework.core.scanner.BuiltResourceScanner;
import com.arkxos.framework.core.scanner.IBuiltResourceVisitor;

/**
 * 遍历所有类中的注解
 */
public class ExcelTaskAnnotationScanner implements IBuiltResourceVisitor {
	
	private static Lock lock = new ReentrantLock();
	private static final String VALUE = "value";
	
	public static String find(Class<? extends Annotation> annotationClass, String taskType) {
		try {
			ExcelTaskAnnotationScanner annotationScanner = new ExcelTaskAnnotationScanner(annotationClass.getName(), taskType);
			BuiltResourceScanner scanner = new BuiltResourceScanner(annotationScanner, null);
			scanner.scan(0, annotationClass);
			
			return annotationScanner.getClassName();
		} finally {
		}
	}
	
	private  String ALIAS;
	private String taskType;
	private String className;
	
	public ExcelTaskAnnotationScanner(String annotationClassName, String taskType) {
		ALIAS = annotationClassName.replace('.', '/');
		this.taskType = taskType;
	}

	@Override
	public String getExtendItemID() {
		return "com.arkxos.framework.annotation.ExcelTaskAnnotationScanner";
	}

	@Override
	public String getExtendItemName() {
		return "AnnotationScanner";
	}
	
	public String getClassName() {
		return className;
	}

	@Override
	public boolean match(BuiltResource br) {
		String fullName = br.getFullName();
		return fullName.endsWith(".class");
	}

	@Override
	public void visitClass(BuiltResource br, ClassNode classNode) {
		if(AsmUtil.isAnnotationPresent(classNode, ALIAS)) {
			String taskType = (String) AsmUtil.getAnnotationValue(classNode, ALIAS, VALUE);
			if(this.taskType.equals(taskType)) {
				this.className = classNode.name.replace("/", ".");
			}
		}
	}
	
	@Override
	public void visitResource(BuiltResource br) {
	}

	@Override
	public void visitInnerClass(BuiltResource br, ClassNode cn, ClassNode icn) {
	}

}
