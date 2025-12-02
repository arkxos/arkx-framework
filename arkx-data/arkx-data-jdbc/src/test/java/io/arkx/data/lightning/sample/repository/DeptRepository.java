package io.arkx.data.lightning.sample.repository;

import org.springframework.stereotype.Repository;

import io.arkx.data.lightning.repository.BaseJdbcRepository;
import io.arkx.data.lightning.sample.model.Dept;

@Repository
public interface DeptRepository extends BaseJdbcRepository<Dept, String> {

}
