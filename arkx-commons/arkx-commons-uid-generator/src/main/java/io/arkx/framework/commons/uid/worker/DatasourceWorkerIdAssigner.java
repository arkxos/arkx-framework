/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.arkx.framework.commons.uid.worker;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.transaction.annotation.Transactional;

import io.arkx.framework.commons.uid.exception.UidGenerateException;
import io.arkx.framework.commons.uid.worker.dao.WorkerNodeMapper;
import io.arkx.framework.commons.uid.worker.entity.WorkerNodeEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents an implementation of {@link WorkerIdAssigner}, the worker id will be
 * discarded after assigned to the UidGenerator 基于数据库获取 workerId（机器节点ID）
 *
 * @author yutianbao
 */
@Slf4j
@RequiredArgsConstructor
public class DatasourceWorkerIdAssigner extends AbstractWorkerAssigner implements WorkerIdAssigner {

	private final SqlSessionFactory sqlSessionFactory;

	/**
	 * Assign worker id base on database.
	 * <p>
	 * If there is host name & port in the environment, we considered that the node runs
	 * in Docker container<br>
	 * Otherwise, the node runs on an actual machine.
	 * @return assigned worker id
	 */
	@Override
	@Transactional(rollbackFor = UidGenerateException.class)
	public long assignWorkerId() {
		// build worker node entity
		WorkerNodeEntity workerNodeEntity = buildWorkerNode();

		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			WorkerNodeMapper workerNodeMapper = sqlSession.getMapper(WorkerNodeMapper.class);

			// add worker node for new (ignore the same IP + PORT)
			doCheck(workerNodeMapper);

			WorkerNodeEntity node = workerNodeMapper.getWorkerNodeByHostPort(workerNodeEntity.getHostName(),
					workerNodeEntity.getPort());
			long workerId;
			if (node == null) {
				workerNodeMapper.addWorkerNode(workerNodeEntity);
				workerId = workerNodeEntity.getId();
				log.info("Add worker node:" + workerNodeEntity);
			}
			else {
				workerNodeMapper.updateWorkerNode(workerNodeEntity);
				log.info("Update worker node:" + workerNodeEntity);
				workerId = node.getId();
			}

			log.info("Add worker node:" + workerNodeEntity);

			return workerId;
		}
		catch (Exception e) {
			String s = "No qualifying bean of type 'org.apache.ibatis.session.SqlSessionFactory' available";
			log.error(s);
			throw new UidGenerateException(s);
		}
	}

	// 检查数据库和表
	public void doCheck(WorkerNodeMapper workerNodeMapper) {
		// if (workerNodeMapper.queryDatabaseExist() == 0 &&
		// workerNodeMapper.createDatabase() > 0) {
		// log.info("Not found database 'fun_cloud_base',auto created success");
		// if (workerNodeMapper.createTable() > 0) {
		// log.info("Not found table 'ark_uid_worker_node',auto created success");
		// }
		// } else if (workerNodeMapper.queryTableExist() == 0 &&
		// workerNodeMapper.createTable() > 0) {
		// log.info("Not found table 'ark_uid_worker_node',auto created success");
		// }
	}

}
