package io.arkx.data.lightning.sample.repository;

import io.arkx.data.lightning.repository.BaseJdbcRepository;
import io.arkx.data.lightning.sample.model.Menu;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends BaseJdbcRepository<Menu, String> {
}
