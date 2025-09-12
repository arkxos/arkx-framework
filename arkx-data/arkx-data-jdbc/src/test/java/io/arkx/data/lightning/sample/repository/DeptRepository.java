package io.arkx.data.lightning.sample.repository;

import io.arkx.data.lightning.repository.BaseJdbcRepository;
import io.arkx.data.lightning.sample.model.Dept;
import org.springframework.stereotype.Repository;

@Repository
public interface DeptRepository extends BaseJdbcRepository<Dept, String> {
}
