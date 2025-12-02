package io.arkx.framework.commons.util;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 16:10
 * @since 1.0
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库导出进度跟踪器 用于跟踪数据库导出过程的进度
 */
public class ExportProgressTracker {

	private static final ExportProgressTracker INSTANCE = new ExportProgressTracker();

	// 存储每个任务的进度信息
	private final Map<String, TaskProgress> taskProgressMap = new ConcurrentHashMap<>();

	// 任务阶段常量
	public static final int STAGE_INIT = 0; // 初始化

	public static final int STAGE_SCHEMA_GENERATION = 1; // Schema生成

	public static final int STAGE_COMPILATION = 2; // 编译

	public static final int STAGE_JAR_CREATION = 3; // JAR创建

	public static final int STAGE_DATA_EXPORT = 4; // 数据导出

	public static final int STAGE_COMPLETE = 5; // 完成

	// 总步骤数（不包括COMPLETE状态）
	public static final int TOTAL_STEPS = 5;

	// 各阶段权重（百分比）
	private static final int[] STAGE_WEIGHTS = { 5, 40, 15, 10, 30, 0 };

	// 各阶段名称
	private static final String[] STAGE_NAMES = { "初始化", "生成Schema文件", "编译Schema文件", "创建JAR包", "导出数据", "完成" };

	private ExportProgressTracker() {
		// 私有构造函数
	}

	public static ExportProgressTracker getInstance() {
		return INSTANCE;
	}

	/**
	 * 开始一个新的导出任务
	 * @param taskId 任务ID
	 * @param totalTables 总表数（用于计算schema生成阶段的进度）
	 */
	public void startTask(String taskId, int totalTables) {
		TaskProgress progress = new TaskProgress(totalTables);
		taskProgressMap.put(taskId, progress);
	}

	/**
	 * 更新任务阶段
	 * @param taskId 任务ID
	 * @param stage 新阶段
	 */
	public void updateStage(String taskId, int stage) {
		TaskProgress progress = taskProgressMap.get(taskId);
		if (progress != null) {
			progress.currentStage = stage;
			progress.stageProgress.set(0);
			progress.currentTableName = ""; // 清空当前表名
		}
	}

	/**
	 * 更新当前阶段的进度
	 * @param taskId 任务ID
	 * @param stageProgress 阶段内进度（0-100）
	 */
	public void updateStageProgress(String taskId, int stageProgress) {
		TaskProgress progress = taskProgressMap.get(taskId);
		if (progress != null) {
			progress.stageProgress.set(Math.min(100, stageProgress));
		}
	}

	/**
	 * 增加表生成计数（用于Schema生成阶段）
	 * @param taskId 任务ID
	 */
	public void incrementTableCount(String taskId) {
		TaskProgress progress = taskProgressMap.get(taskId);
		if (progress != null) {
			int completed = progress.completedTables.incrementAndGet();
			if (progress.totalTables > 0) {
				int percent = (completed * 100) / progress.totalTables;
				progress.stageProgress.set(percent);
			}
		}
	}

	/**
	 * 更新当前正在处理的表名
	 * @param taskId 任务ID
	 * @param tableName 表名
	 */
	public void updateCurrentTable(String taskId, String tableName) {
		TaskProgress progress = taskProgressMap.get(taskId);
		if (progress != null) {
			progress.currentTableName = tableName;
		}
	}

	/**
	 * 获取任务总进度（百分比）
	 * @param taskId 任务ID
	 * @return 总进度（0-100）
	 */
	public int getProgress(String taskId) {
		TaskProgress progress = taskProgressMap.get(taskId);
		if (progress == null) {
			return 0;
		}

		// 计算之前阶段的权重总和
		int previousStagesProgress = 0;
		for (int i = 0; i < progress.currentStage; i++) {
			previousStagesProgress += STAGE_WEIGHTS[i];
		}

		// 当前阶段的进度贡献
		int currentStageContribution = (progress.stageProgress.get() * STAGE_WEIGHTS[progress.currentStage]) / 100;

		// 总进度
		return previousStagesProgress + currentStageContribution;
	}

	/**
	 * 获取当前任务状态信息
	 * @param taskId 任务ID
	 * @return 状态描述
	 */
	public String getStatusDescription(String taskId) {
		TaskProgress progress = taskProgressMap.get(taskId);
		if (progress == null) {
			return "任务不存在";
		}

		// 如果有错误信息，优先返回错误信息
		if (progress.error != null) {
			return "错误: " + progress.error;
		}

		// 当前步骤信息（从1开始计数，但完成阶段特殊处理）
		String stepInfo;
		if (progress.currentStage == STAGE_COMPLETE) {
			// 完成阶段不显示步骤信息
			stepInfo = "";
		}
		else {
			int currentStep = progress.currentStage + 1;
			stepInfo = " [第" + currentStep + "步/共" + TOTAL_STEPS + "步]";
		}

		String tableInfo = progress.currentTableName != null && !progress.currentTableName.isEmpty()
				? " [当前表: " + progress.currentTableName + "]" : "";

		String stageName = STAGE_NAMES[progress.currentStage];
		String stageDetails = "";

		// 添加阶段特定详情
		switch (progress.currentStage) {
			case STAGE_SCHEMA_GENERATION:
				stageDetails = " (" + progress.completedTables.get() + "/" + progress.totalTables + ")";
				break;
		}

		return "正在" + stageName + stageDetails + tableInfo + stepInfo + " - 总进度: " + getProgress(taskId) + "%";
	}

	/**
	 * 完成任务并清理资源
	 * @param taskId 任务ID
	 */
	public void completeTask(String taskId) {
		TaskProgress progress = taskProgressMap.get(taskId);
		if (progress != null) {
			progress.currentStage = STAGE_COMPLETE;
			progress.stageProgress.set(100);

			// 可以设置一个定时器在一段时间后清除此任务
			// 暂时不清除，让前端可以查询最终结果
		}
	}

	/**
	 * 清除任务记录
	 * @param taskId 任务ID
	 */
	public void removeTask(String taskId) {
		taskProgressMap.remove(taskId);
	}

	/**
	 * 获取任务当前阶段
	 * @param taskId 任务ID
	 * @return 当前阶段
	 */
	public int getCurrentStage(String taskId) {
		TaskProgress progress = taskProgressMap.get(taskId);
		return progress != null ? progress.currentStage : -1;
	}

	/**
	 * 设置任务错误信息
	 * @param taskId 任务ID
	 * @param errorMessage 错误信息
	 */
	public void setError(String taskId, String errorMessage) {
		TaskProgress progress = taskProgressMap.get(taskId);
		if (progress != null) {
			progress.error = errorMessage;
		}
	}

	/**
	 * 任务进度信息
	 */
	private static class TaskProgress {

		int currentStage = STAGE_INIT;

		AtomicInteger stageProgress = new AtomicInteger(0);

		final int totalTables;

		final AtomicInteger completedTables = new AtomicInteger(0);

		String currentTableName = ""; // 当前正在处理的表名

		String error = null; // 错误信息

		TaskProgress(int totalTables) {
			this.totalTables = totalTables;
		}

	}

}
