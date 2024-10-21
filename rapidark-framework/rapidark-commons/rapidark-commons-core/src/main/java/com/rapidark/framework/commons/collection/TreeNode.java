package com.rapidark.framework.commons.collection;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/7/28 19:42
 */
@Data
public class TreeNode {

    private String id;
    private String name;

    private List<TreeNode> children = new ArrayList<>();

    public TreeNode() {}

    public TreeNode(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addNode(TreeNode node) {
        this.children.add(node);
    }

    public TreeNode findChild(String childNodeId) {
        for(TreeNode childNode : children) {
            if(childNode.getId().equals(childNodeId)) {
                return childNode;
            }
        }
        return null;
    }
}
