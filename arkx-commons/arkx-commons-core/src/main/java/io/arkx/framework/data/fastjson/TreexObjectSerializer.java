package io.arkx.framework.data.fastjson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.collection.tree.TreeNode;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.commons.util.lang.ClassUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

public class TreexObjectSerializer implements ObjectSerializer {

	@Override
	public void write(JSONSerializer serializer, //
			Object object, //
			Object fieldName, //
			Type fieldType, //
			int features) throws IOException {
		SerializeWriter out = serializer.out;

		if (object == null) {
			out.writeNull();
			return;
		}

		Treex<?, ?> tree = (Treex<?, ?>) object;
		Mapx<String, Object> mapedData = convertNodeToMap(tree.getRoot());
		boolean isWarpTreeNode = tree.isWarpTreeNode();
		String jsonString = "";
		if (isWarpTreeNode) {
			jsonString = JSON.toJSONString(mapedData);
		}
		else {
			jsonString = JSON.toJSONString(mapedData.get("children"));
		}
		out.write(jsonString);
	}

	private Mapx<String, Object> convertNodeToMap(TreeNode<?, ?> node) {
		Mapx<String, Object> mapedData = new Mapx<>();
		if (node.getValue() != null) {
			mapedData = ClassUtil.objectToMapx(node.getValue());
		}
		List<Mapx<String, Object>> childMapedDataList = new ArrayList<>();
		for (TreeNode<?, ?> child : node.getChildren()) {
			childMapedDataList.add(convertNodeToMap(child));
		}
		mapedData.put("children", childMapedDataList);
		return mapedData;
	}

}
