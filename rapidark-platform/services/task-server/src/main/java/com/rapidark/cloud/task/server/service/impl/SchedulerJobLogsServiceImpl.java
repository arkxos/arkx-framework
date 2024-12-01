package com.rapidark.cloud.task.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rapidark.framework.data.mybatis.model.PageParams;
import com.rapidark.cloud.task.client.model.entity.SchedulerJobLogs;
import com.rapidark.cloud.task.server.mapper.SchedulerJobLogsMapper;
import com.rapidark.cloud.task.server.service.SchedulerJobLogsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liuyadu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SchedulerJobLogsServiceImpl implements SchedulerJobLogsService {
    @Autowired
    private SchedulerJobLogsMapper schedulerJobLogsMapper;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<SchedulerJobLogs> findListPage(PageParams pageParams) {
        SchedulerJobLogs query = pageParams.mapToObject(SchedulerJobLogs.class);
        QueryWrapper<SchedulerJobLogs> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getJobName()), SchedulerJobLogs::getJobName, query.getJobName());
        queryWrapper.orderByDesc("create_time");
        return schedulerJobLogsMapper.selectPage(new Page(pageParams.getPage(), pageParams.getLimit()), queryWrapper);
    }

    /**
     * 添加日志
     *
     * @param log
     */
    @Override
    public void addLog(SchedulerJobLogs log) {
        schedulerJobLogsMapper.insert(log);
    }

    /**
     * 更细日志
     *
     * @param log
     */
    @Override
    public void modifyLog(SchedulerJobLogs log) {
        schedulerJobLogsMapper.updateById(log);
    }

    /**
     * 根据主键获取日志
     *
     * @param logId
     * @return
     */
    @Override
    public SchedulerJobLogs getLog(String logId) {
        return schedulerJobLogsMapper.selectById(logId);
    }
}
