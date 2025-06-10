package io.arkx.framework.performance.monitor2;

import java.util.List;

import io.arkx.framework.commons.model.PageResult;
import io.arkx.framework.commons.web.ResponseResult;
import io.arkx.framework.performance.monitor2.model.LogMethodPerformance;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**   
 * 
 * @author Darkness
 * @date 2013-7-22 下午08:18:06 
 * @version V1.0   
 */
@Controller
@RequestMapping("monitor/performance")
public class PerformanceController {
	
	@Resource
	private LogMethodPerformanceRepository logMethodPerformanceRepository;
	
	/**
	 * 列出所有数据
	 * 
	 * @author Darkness
	 * @date 2013-5-14 上午10:26:45
	 * @version V1.0
	 */
	@RequestMapping("/list.do")
	@ResponseBody
	public ResponseResult<PageResult<LogMethodPerformance>> listAll(Pageable pageInfo) {

		Page<LogMethodPerformance> datasList = logMethodPerformanceRepository.findAll(pageInfo);
		 
		return ResponseResult.ok(datasList);
	}

}
