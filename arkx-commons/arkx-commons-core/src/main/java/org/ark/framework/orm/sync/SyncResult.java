package org.ark.framework.orm.sync;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:57
 * @since 1.0
 */

import org.ark.framework.orm.sync.metadata.SyncStatus;

import lombok.Data;

/**
 * 同步结果 存储单个表同步的结果信息
 */
@Data
public class SyncResult {

	/**
	 * 表编码
	 */
	private final String tableCode;

	/**
	 * 同步状态
	 */
	private SyncStatus status;

	/**
	 * 发现的记录数
	 */
	private int recordsFound;

	/**
	 * 处理的记录数
	 */
	private int recordsProcessed;

	/**
	 * 同步耗时(毫秒)
	 */
	private long durationMs;

	/**
	 * 发生的错误
	 */
	private Throwable error;

	/**
	 * 开始时间戳
	 */
	private long startTimeMs;

	/**
	 * 结束时间戳
	 */
	private long endTimeMs;

	/**
	 * 构造函数
	 * @param tableCode 表编码
	 */
	public SyncResult(String tableCode) {
		this.tableCode = tableCode;
		this.startTimeMs = System.currentTimeMillis();
		this.status = SyncStatus.IN_PROGRESS;
	}

	/**
	 * 构造函数
	 * @param tableCode 表编码
	 * @param recordsProcessed 处理的记录数
	 * @param status 同步状态
	 */
	public SyncResult(String tableCode, int recordsProcessed, SyncStatus status) {
		this.tableCode = tableCode;
		this.recordsProcessed = recordsProcessed;
		this.status = status;
		this.startTimeMs = System.currentTimeMillis();
		this.endTimeMs = System.currentTimeMillis();
		this.durationMs = this.endTimeMs - this.startTimeMs;
	}

	/**
	 * 标记同步完成
	 */
	public void markCompleted(SyncStatus status) {
		this.endTimeMs = System.currentTimeMillis();
		this.durationMs = this.endTimeMs - this.startTimeMs;
		this.status = status;
	}

	/**
	 * 判断是否成功
	 * @return 是否成功
	 */
	public boolean isSuccess() {
		return status == SyncStatus.SUCCESS;
	}

	/**
	 * 获取简要描述
	 * @return 简要描述
	 */
	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append("表 ").append(tableCode).append(" 同步");

		switch (status) {
			case SUCCESS:
				sb.append("成功，处理 ").append(recordsProcessed).append(" 条记录");
				break;
			case INCONSISTENT:
				sb.append("完成但数据不一致，处理 ").append(recordsProcessed).append(" 条记录");
				break;
			case FAILED:
				sb.append("失败: ").append(error != null ? error.getMessage() : "未知错误");
				break;
			case CANCELLED:
				sb.append("被取消");
				break;
			default:
				sb.append("状态: ").append(status);
		}

		sb.append("，耗时 ").append(durationMs).append(" 毫秒");
		return sb.toString();
	}

}
