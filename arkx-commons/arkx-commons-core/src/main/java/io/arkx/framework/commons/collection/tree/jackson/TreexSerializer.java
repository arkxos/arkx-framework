package io.arkx.framework.commons.collection.tree.jackson;

import java.io.IOException;
import java.util.Map;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.collection.tree.TreeNode;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.commons.util.lang.ClassUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Treex类的Jackson序列化器
 *
 * @author Nobody
 * @date 2025-09-15
 * @since 1.0
 */
public class TreexSerializer extends JsonSerializer<Treex<?, ?>> {

    @Override
    public void serialize(Treex<?, ?> treex, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (treex.isWarpTreeNode()) {
            // 序列化整个树结构（包含根节点）
            serializeNode(treex.getRoot(), gen);
        } else {
            // 只序列化根节点的子节点（不包含根节点本身）
            gen.writeStartArray();
            for (TreeNode<?, ?> child : treex.getRoot().getChildren()) {
                serializeNode(child, gen);
            }
            gen.writeEndArray();
        }
    }

    /**
     * 递归序列化单个节点
     */
    private void serializeNode(TreeNode<?, ?> node, JsonGenerator gen) throws IOException {
        gen.writeStartObject();

        // 序列化节点的基本属性
        // gen.writeObjectField("id", node.getId());
        // gen.writeStringField("name", node.getName());
        gen.writeStringField("nodeType", node.getNodeType());
        // gen.writeObjectField("parentId", node.getParentId());
        gen.writeBooleanField("isParent", node.isParent());
        gen.writeBooleanField("isLeaf", node.isLeaf());
        gen.writeNumberField("depth", node.getDepth());
        gen.writeNumberField("position", node.getPosition());

        // 序列化节点值中的属性
        if (node.getValue() != null) {
            Mapx<String, Object> mapData = ClassUtil.objectToMapx(node.getValue());
            for (Map.Entry<String, Object> entry : mapData.entrySet()) {
                gen.writeObjectField(entry.getKey(), entry.getValue());
            }
        }

        // 序列化额外数据
        if (node.getExtraDatas() != null && !node.getExtraDatas().isEmpty()) {
            for (Map.Entry<String, Object> entry : node.getExtraDatas().entrySet()) {
                gen.writeObjectField(entry.getKey(), entry.getValue());
            }
        }

        // 递归序列化子节点
        if (node.hasChildren()) {
            gen.writeArrayFieldStart("children");
            for (TreeNode<?, ?> child : node.getChildren()) {
                serializeNode(child, gen);
            }
            gen.writeEndArray();
        } else {
            gen.writeNullField("children");
        }

        gen.writeEndObject();
    }

    @Override
    public Class<Treex<?, ?>> handledType() {
        return (Class<Treex<?, ?>>) (Class<?>) Treex.class;
    }

}
