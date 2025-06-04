package com.arkxos.framework.commons.collection.tree.jackson;

import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.collection.tree.TreeNode;
import com.arkxos.framework.commons.util.lang.ClassUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

/**
 * @author Nobody
 * @date 2025-05-19 17:40
 * @since 1.0
 */

public class TreeNodeSerializer extends JsonSerializer<TreeNode<?, ?>> {

	@Override
	public void serialize(TreeNode<?, ?> node, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();

		// 序列化节点的基本属性
		gen.writeObjectField("id", node.getId());
		gen.writeStringField("name", node.getName());
		gen.writeStringField("nodeType", node.getNodeType());
		gen.writeObjectField("value", node.getValue());
		gen.writeObjectField("parentId", node.getParentId());
		gen.writeStringField("path", node.getPath());
		gen.writeBooleanField("isParent", node.isParent());
		gen.writeBooleanField("isLeaf", node.isLeaf());
		gen.writeNumberField("depth", node.getDepth());

		// 序列化额外数据（如果需要）
		if (node.getValue() != null) {
			Mapx<String, Object> mapData = ClassUtil.objectToMapx(node.getValue());
			for (Map.Entry<String, Object> stringObjectEntry : mapData.entrySet()) {
				gen.writeObjectField(stringObjectEntry.getKey(), stringObjectEntry.getValue());
			}
		}

		// 序列化额外数据（如果需要）
		if (node.getExtraDatas() != null && !node.getExtraDatas().isEmpty()) {
			for (Map.Entry<String, Object> stringObjectEntry : node.getExtraDatas().entrySet()) {
				gen.writeObjectField(stringObjectEntry.getKey(), stringObjectEntry.getValue());
			}
		}

		// 递归序列化子节点
		if (node.hasChildren()) {
			gen.writeArrayFieldStart("children");
			for (TreeNode<?, ?> child : node.getChildren()) {
				this.serialize(child, gen, serializers);
			}
			gen.writeEndArray();
		} else {
			gen.writeNullField("children");
		}

		gen.writeEndObject();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<TreeNode<?, ?>> handledType() {
		return (Class<TreeNode<?, ?>>) (Class<?>) TreeNode.class; // 允许所有 TreeNode 子类型
	}
}