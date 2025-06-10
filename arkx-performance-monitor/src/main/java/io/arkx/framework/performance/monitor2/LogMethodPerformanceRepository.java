package io.arkx.framework.performance.monitor2;

import io.arkx.framework.data.jpa.BaseRepository;
import io.arkx.framework.performance.monitor2.model.LogMethodPerformance;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**   
 * 
 * @author Darkness
 * @date 2013-7-22 下午08:49:31 
 * @version V1.0   
 */
@Repository
public interface LogMethodPerformanceRepository extends BaseRepository<LogMethodPerformance, String> {

	Optional<LogMethodPerformance> findOneByClassNameAndMethodName(String className, String methodName);

}
