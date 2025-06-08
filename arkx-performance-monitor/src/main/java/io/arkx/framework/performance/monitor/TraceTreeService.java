package io.arkx.framework.performance.monitor;

/**
 * @author Nobody
 * @date 2025-06-06 17:21
 * @since 1.0
 */
import io.arkx.framework.performance.monitor.model.TraceNode;
import io.arkx.framework.performance.monitor.repository.TraceRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service
public class TraceTreeService {

	private final TraceRepository traceRepository;

	public TraceTreeService(TraceRepository traceRepository) {
		this.traceRepository = traceRepository;
	}

	public String buildTraceTree(String requestId) {
		try {
			List<TraceNode> nodes = traceRepository.findByRequestId(requestId);
			if (nodes.isEmpty()) {
				return "No trace data available for request: " + requestId;
			}

			// 构建树结构
			TraceNode root = buildTreeStructure(nodes);

			// 转换为树状文本
			return root.toTreeString();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to build trace tree", e);
		}
	}

	private TraceNode buildTreeStructure(List<TraceNode> nodes) {
		Map<String, TraceNode> nodeMap = new HashMap<>();
		Set<String> rootCandidates = new HashSet<>();

		// 创建所有节点并添加为根候选
		for (TraceNode node : nodes) {
			nodeMap.put(node.getTraceId(), node);
			rootCandidates.add(node.getTraceId());
		}

		// 构建父子关系
		for (TraceNode node : nodes) {
			if (node.getParentId() != null) {
				TraceNode parent = nodeMap.get(node.getParentId());
				if (parent != null) {
					parent.addChild(node);
					rootCandidates.remove(node.getTraceId());
				}
			}
		}

		// 获取根节点
		if (!rootCandidates.isEmpty()) {
			String rootId = rootCandidates.iterator().next();
			return nodeMap.get(rootId);
		}

		// 如果未找到根节点，回退到最早节点
		return nodes.stream()
				.min(Comparator.comparingLong(TraceNode::getStartTime))
				.orElse(nodes.get(0));
	}
}
