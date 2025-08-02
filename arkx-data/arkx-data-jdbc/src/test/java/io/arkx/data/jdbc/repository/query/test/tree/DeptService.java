package io.arkx.data.jdbc.repository.query.test.tree;

/**
 * @author Nobody
 * @date 2025-07-28 2:02
 * @since 1.0
 */
import io.arkx.data.common.treetable.closure.service.SmartClosureTableServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DeptService {

	private final SmartClosureTableServiceImpl closureService;

	public DeptService(SmartClosureTableServiceImpl closureService) {
		this.closureService = closureService;
	}

	@Transactional
	public void addDept(Dept dept) {
//		closureService.insertNode(dept, "dept");
	}

	public List<Dept> getDeptDescendants(Long deptId) {
//		return closureService.findDescendants("dept", deptId, Dept.class);
		return null;
	}
}

