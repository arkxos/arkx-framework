package io.arkx.data.lightning.sample.service;

/**
 * @author Nobody
 * @date 2025-07-28 2:02
 * @since 1.0
 */
import io.arkx.data.lightning.plugin.treetable.closure.service.ClosureTableServiceImpl;
import io.arkx.data.lightning.sample.model.Dept;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DeptService {

	private final ClosureTableServiceImpl closureService;

	public DeptService(ClosureTableServiceImpl closureService) {
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

