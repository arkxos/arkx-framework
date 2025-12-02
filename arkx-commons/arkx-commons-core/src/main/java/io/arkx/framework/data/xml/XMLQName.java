package io.arkx.framework.data.xml;

/**
 * 表示一个XML中的QName
 *
 */
public class XMLQName {

	String name;

	XMLNamespace namespace;

	public XMLQName(String name, XMLNamespace ns) {
		namespace = ns;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public XMLNamespace getNamespace() {
		return namespace;
	}

	public String getQualifiedName() {
		if (namespace == null) {
			return name;
		}
		else {
			return namespace.getPrefix() + ":" + name;
		}
	}

	@Override
	public String toString() {
		return getQualifiedName();
	}

}
