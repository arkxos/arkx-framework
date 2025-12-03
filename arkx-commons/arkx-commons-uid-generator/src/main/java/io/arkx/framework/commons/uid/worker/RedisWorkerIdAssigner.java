package io.arkx.framework.commons.uid.worker;

import org.springframework.data.redis.core.RedisTemplate;

import io.arkx.framework.commons.uid.exception.UidGenerateException;
import io.arkx.framework.commons.uid.worker.entity.WorkerNodeEntity;

import lombok.RequiredArgsConstructor;

/**
 * 基于Redis获取workerId（机器节点ID）
 *
 * @author DengJun 2021/5/6
 */
@RequiredArgsConstructor
public class RedisWorkerIdAssigner extends AbstractWorkerAssigner implements WorkerIdAssigner {

    private final RedisTemplate<String, Integer> redisTemplate;

    public String assigner = "gl:uid:assigner";

    public String worker = "gl:uid:worker";

    public void setAssigner(String assigner) {
        this.assigner = assigner;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    /**
     * Redis是单线程的，无需考虑线程安全， 用一个全局key颁发workerId， 每个服务器一个key用于记录服务器信息
     */
    @Override
    public long assignWorkerId() {
        String workerKey = getWorkerKey();
        Integer workerId = redisTemplate.opsForValue().get(workerKey);
        if (workerId != null && workerId != 0) {
            return workerId;
        }
        Long incr = redisTemplate.opsForValue().increment(assigner);
        if (incr == null)
            throw new UidGenerateException();
        int value = incr.intValue();
        redisTemplate.opsForValue().set(workerKey, value);
        return value;
    }

    private String getWorkerKey() {
        WorkerNodeEntity nodeEntity = buildWorkerNode();
        return worker + nodeEntity.getHostName() + "_" + nodeEntity.getPort();
    }

}
