package io.arkx.framework.classweaver;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

import io.arkx.framework.preloader.PreClassLoader;

public class ClassWeaver {

	private static final String WEAVER = "L" + Weaver.class.getName().replace('.', '/') + ";";

	private static final String SUPERINVOKE = "L" + SuperInvoke.class.getName().replace('.', '/') + ";";

	private static final String INIT = "<init>";

	private static final String CLINIT = "<clinit>";

	private static final String DEFAULT = "()V";

	private static final Object THIS = "this";

	private static final String OBJECT = "java/lang/Object";

	ClassNode cns;

	HashSet<String> weaverMethods;

	HashMap<String, FieldNode> fields;

	String targetDesc;

	String targetClassName;

	String stubName;

	boolean replaced;

	public ClassWeaver(String name) throws IOException {
		this(new ClassReader(name));
	}

	public ClassWeaver(byte[] bs) throws IOException {
		this(new ClassReader(bs));
	}

	private ClassWeaver(ClassReader cr) throws IOException {
		ClassNode source = new ClassNode();
		cr.accept(source, 15);
		this.cns = source;
		if (this.cns.invisibleAnnotations == null) {
			return;
		}
		for (AnnotationNode an : (List<AnnotationNode>) this.cns.invisibleAnnotations) {
			if (an.desc.equals(WEAVER)) {
				for (int i = 0; i < an.values.size(); i += 2) {
					if (an.values.get(i).equals("value")) {
						this.targetDesc = an.values.get(i + 1).toString();
					}
				}
			}
		}
		if (this.targetDesc == null) {
			return;
		}
		source = new ClassNode();
		cr.accept(source, 0);
		this.cns = source;
		this.replaced = true;

		this.fields = new HashMap();
		this.stubName = ("java/lang/Object".equals(this.cns.superName) ? null : this.cns.superName);
		for (FieldNode field : (List<FieldNode>) this.cns.fields) {
			this.fields.put(field.name, field);
		}
		this.weaverMethods = new HashSet();
		for (MethodNode method : (List<MethodNode>) this.cns.methods) {
			this.weaverMethods.add(method.name + method.desc);
		}
	}

	public ClassNode weave() throws Exception {
		if (!this.replaced) {
			return null;
		}
		byte[] targetData = PreClassLoader.getInstance().loadData(getTargetClassName());
		ClassReader crt = new ClassReader(targetData);

		String o = getTargetClassName().replace('.', '/');
		ClassNode cnt = new ClassNode();
		crt.accept(cnt, 0);
		for (FieldNode field : (List<FieldNode>) cnt.fields) {
			this.fields.remove(field.name);
		}
		for (FieldNode field : this.fields.values()) {
			cnt.fields.add(field);
		}
		HashMap<String, MethodNode> tagMap = new HashMap();
		for (MethodNode method : (List<MethodNode>) cnt.methods) {
			tagMap.put(method.name + method.desc, method);
		}
		for (MethodNode method : (List<MethodNode>) this.cns.methods) {
			String mKey = method.name + method.desc;

			boolean isInit = "<init>".equals(method.name);
			boolean isSuper = false;
			if (method.invisibleAnnotations != null) {
				for (AnnotationNode a : (List<AnnotationNode>) method.invisibleAnnotations) {
					if (SUPERINVOKE.equals(a.desc)) {
						isSuper = true;
						break;
					}
				}
			}
			MethodNode m = (MethodNode) tagMap.get(mKey);
			if (m != null) {
				if (method.invisibleAnnotations == null) {
					method.invisibleAnnotations = m.invisibleAnnotations;
				}
				else {
					mergerAnnotation(method.invisibleAnnotations, m.invisibleAnnotations);
				}
				m.invisibleAnnotations = null;
				if (method.visibleAnnotations == null) {
					method.visibleAnnotations = m.visibleAnnotations;
				}
				else {
					mergerAnnotation(method.visibleAnnotations, m.visibleAnnotations);
				}
				m.visibleAnnotations = null;

				boolean isClinit = "<clinit>".equals(m.name);
				String methodName;
				if ((isInit) || (isClinit)) {
					methodName = "_" + m.name.substring(1, m.name.length() - 1);
					InsnList insnList = m.instructions;
					if (isInit) {
						ListIterator<AbstractInsnNode> ite = insnList.iterator();
						while (ite.hasNext()) {
							AbstractInsnNode insn = (AbstractInsnNode) ite.next();
							insnList.remove(insn);
							if (insn.getOpcode() == 183) {
								break;
							}
						}
					}
				}
				else {
					methodName = method.name;
				}
				for (int i = 0; i < Integer.MAX_VALUE; i++) {
					m.name = (methodName + i);
					mKey = m.name + m.desc;
					if ((!tagMap.containsKey(mKey)) && (!this.weaverMethods.contains(mKey))) {
						break;
					}
				}
				if (isClinit) {
					InsnList insnList = method.instructions;
					AbstractInsnNode first = insnList.getFirst();
					insnList.insertBefore(first, new MethodInsnNode(184, o, m.name, m.desc));
				}
			}
			cnt.methods.add(method);

			InsnList insnList = method.instructions;
			ListIterator<AbstractInsnNode> ite = insnList.iterator();
			boolean isFirst = true;

			while (ite.hasNext()) {
				AbstractInsnNode insn = (AbstractInsnNode) ite.next();
				if ((insn instanceof MethodInsnNode)) {
					MethodInsnNode isn = (MethodInsnNode) insn;

					boolean isStub = (isn.owner.equals(this.stubName)) || (isn.owner.equals(cnt.name));
					MethodNode tisn = isStub ? (MethodNode) tagMap.get(isn.name + isn.desc) : null;
					if ((isInit) && (isFirst) && (insn.getOpcode() == 183)) {
						isFirst = false;

						boolean isDefault = "()V".equals(isn.desc);
						if ((m != null) && (!isStub)) {
							insn = new VarInsnNode(25, 0);
							insnList.insert(isn, insn);
							insnList.insert(insn, new MethodInsnNode(182, o, m.name, m.desc));
						}
						else if ((isStub) && (!isDefault) && (!isn.name.equals(tisn.name))) {
							isn.setOpcode(182);
							AbstractInsnNode first = insnList.getFirst();
							MethodInsnNode constructor = new MethodInsnNode(183, cnt.superName, "<init>", "()V");
							insnList.insertBefore(first, constructor);
							insnList.insertBefore(constructor, new VarInsnNode(25, 0));
						}
						if (isDefault) {
							isn.owner = cnt.superName;
							continue;
						}
					}
					if (isStub) {
						if (((isSuper) || (tisn == null)) && (isn.getOpcode() == 183)) {
							isn.owner = cnt.superName;
							continue;
						}
						if (tisn != null) {
							isn.name = tisn.name;
						}
					}
					if ((isStub) || (isn.owner.equals(this.cns.name))) {
						isn.owner = o;
					}
				}
				else if ((insn instanceof FieldInsnNode)) {
					FieldInsnNode isn = (FieldInsnNode) insn;
					if (isn.owner.equals(this.cns.name)) {
						isn.owner = o;
					}
				}
			}
			if (method.localVariables != null) {
				for (LocalVariableNode var : (List<LocalVariableNode>) method.localVariables) {
					if (var.name.equals(THIS)) {
						var.desc = this.targetDesc;
					}
				}
			}
		}
		return cnt;
	}

	private void mergerAnnotation(List<AnnotationNode> s, List<AnnotationNode> t) {
		if (t == null) {
			return;
		}
		HashMap<String, AnnotationNode> m = new HashMap();
		for (AnnotationNode a : t) {
			m.put(a.desc, a);
		}
		for (AnnotationNode a : s) {
			AnnotationNode v = (AnnotationNode) m.put(a.desc, a);
			t.remove(v);
		}
		s.addAll(t);
	}

	public String getTargetClassName() {
		if (this.targetClassName == null) {
			this.targetClassName = this.targetDesc.substring(1, this.targetDesc.length() - 1);
			this.targetClassName = this.targetClassName.replace('/', '.');
		}
		return this.targetClassName;
	}

	public boolean isReplaced() {
		return this.replaced;
	}

}
