package io.arkx.framework.core.scanner;

import org.objectweb.asm.tree.ClassNode;

import io.arkx.framework.extend.IExtendItem;

/**
 * 编译后资源遍历器
 */
public interface IBuiltResourceVisitor extends IExtendItem {

	/**
	 * @param br 编译后资源实例
	 * @return 本遍历器是否处理br对应的资源
	 */
	boolean match(BuiltResource br);

	/**
	 * 遍历类型
	 * @param br 编译后资源实例
	 * @param cn 类型节点
	 */
	void visitClass(BuiltResource br, ClassNode cn);

	/**
	 * 遍历内部类
	 * @param br 编译后资源实例
	 * @param outerClass 外部类节点
	 * @param innerClass 内部类节点
	 */
	void visitInnerClass(BuiltResource br, ClassNode outerClass, ClassNode innerClass);

	/**
	 * 遍历非class资源
	 * @param br 编译后资源实例
	 */
	void visitResource(BuiltResource br);

}
