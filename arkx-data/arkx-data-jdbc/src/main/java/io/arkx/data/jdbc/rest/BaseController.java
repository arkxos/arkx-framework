package io.arkx.data.jdbc.rest;

import io.arkx.data.jdbc.service.BaseService;
import io.arkx.framework.commons.collection.DataTable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 通用基础控制器 - 支持完整的CRUD、批量操作、状态管理和原生SQL查询
 *
 * @author Nobody
 * @date 2025-08-01
 * @since 1.0
 */
public class BaseController<T, ID extends Serializable, S extends BaseService<T, ID, ?>> {

	protected final S service;

	public BaseController(S service) {
		this.service = service;
	}

	// ==================== 基本CRUD操作 ====================

	@GetMapping("/{id}")
	public ResponseEntity<T> getById(@PathVariable("id") ID id) {
		return ResponseEntity.of(service.findById(id));
	}

	@GetMapping
	public ResponseEntity<List<T>> getAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/page")
	public ResponseEntity<Page<T>> getAllPage(@PageableDefault Pageable pageable) {
		return ResponseEntity.ok(service.findAll(pageable));
	}

	@GetMapping("/sorted")
	public ResponseEntity<List<T>> getAllSorted(Sort sort) {
		return ResponseEntity.ok(service.findAll(sort));
	}

	@PostMapping
	public ResponseEntity<T> create(@RequestBody T entity) {
		T savedEntity = service.insert(entity);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedEntity);
	}

	@PutMapping("/{id}")
	public ResponseEntity<T> update(@PathVariable ID id, @RequestBody T entity) {
		T updatedEntity = service.update(entity);
		return ResponseEntity.ok(updatedEntity);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable ID id) {
		service.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteAll() {
		service.deleteAll();
		return ResponseEntity.noContent().build();
	}

	// ==================== 批量操作 ====================

	@PostMapping("/batch")
	public ResponseEntity<List<T>> createBatch(@RequestBody List<T> entities) {
		List<T> savedEntities = service.saveAll(entities);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedEntities);
	}

	@GetMapping("/batch")
	public ResponseEntity<List<T>> getBatch(@RequestParam Collection<ID> ids) {
		return ResponseEntity.ok(service.findAllById(ids));
	}

	@DeleteMapping("/batch")
	public ResponseEntity<Void> deleteBatch(@RequestParam Collection<ID> ids) {
		service.deleteAllById(ids);
		return ResponseEntity.noContent().build();
	}

	// ==================== 批量Map操作 ====================

	@GetMapping("/mget")
	public ResponseEntity<Map<ID, T>> mget(@RequestParam Collection<ID> ids) {
		return ResponseEntity.ok(service.mget(ids));
	}

	@GetMapping("/mget-one-by-one")
	public ResponseEntity<Map<ID, T>> mgetOneByOne(@RequestParam Collection<ID> ids) {
		return ResponseEntity.ok(service.mgetOneByOne(ids));
	}

	@GetMapping("/find-all-one-by-one")
	public ResponseEntity<List<T>> findAllOneByOne(@RequestParam Collection<ID> ids) {
		return ResponseEntity.ok(service.findAllOneByOne(ids));
	}

	// ==================== 状态管理 ====================

	@PatchMapping("/{id}/toggle-status")
	public ResponseEntity<Void> toggleStatus(@PathVariable ID id) {
		service.toggleStatus(id);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/fake-delete")
	public ResponseEntity<Void> fakeDelete(@RequestParam ID... ids) {
		service.fakeDelete(ids);
		return ResponseEntity.ok().build();
	}

	// ==================== 示例查询 ====================

	@PostMapping("/by-example")
	public ResponseEntity<List<T>> findByExample(@RequestBody T example) {
		return ResponseEntity.ok(service.findAll(Example.of(example)));
	}

	@PostMapping("/by-example/page")
	public ResponseEntity<Page<T>> findByExamplePage(@RequestBody T example,
													 @PageableDefault Pageable pageable) {
		return ResponseEntity.ok(service.findAll(Example.of(example), pageable));
	}

	@PostMapping("/by-example/sorted")
	public ResponseEntity<List<T>> findByExampleSorted(@RequestBody T example, Sort sort) {
		return ResponseEntity.ok(service.findAll(Example.of(example), sort));
	}

	@PostMapping("/by-example/exists")
	public ResponseEntity<Boolean> existsByExample(@RequestBody T example) {
		return ResponseEntity.ok(service.exists(Example.of(example)));
	}

	@PostMapping("/by-example/count")
	public ResponseEntity<Long> countByExample(@RequestBody T example) {
		return ResponseEntity.ok(service.count(Example.of(example)));
	}

	// ==================== 原生SQL查询 ====================

	@PostMapping("/query/data-table")
	public ResponseEntity<DataTable> queryDataTable(@RequestParam String sql,
													@RequestBody(required = false) Object[] params) {
		return ResponseEntity.ok(service.queryDataTable(sql, params));
	}

	@PostMapping("/query/map")
	public ResponseEntity<List<Map<String, Object>>> queryMap(@RequestParam String sql,
															  @RequestBody(required = false) Object[] params) {
		return ResponseEntity.ok(service.queryMap(sql, params));
	}

	@PostMapping("/query/list")
	public ResponseEntity<List<T>> queryList(@RequestParam String sql,
											 @RequestBody(required = false) Object[] params) {
		return ResponseEntity.ok(service.queryList(sql, params));
	}

	@PostMapping("/query/long")
	public ResponseEntity<Long> queryForLong(@RequestParam String sql,
											 @RequestBody(required = false) Object[] params) {
		return ResponseEntity.ok(service.queryForLong(sql, params));
	}

	// ==================== 工具方法 ====================

	@GetMapping("/exists/{id}")
	public ResponseEntity<Boolean> exists(@PathVariable ID id) {
		return ResponseEntity.ok(service.existsById(id));
	}

	@GetMapping("/count")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(service.count());
	}

	@GetMapping("/support/{modelType}")
	public ResponseEntity<Boolean> support(@PathVariable String modelType) {
		return ResponseEntity.ok(service.support(modelType));
	}

	// ==================== 兼容性方法 ====================

	@GetMapping("/list")
	@Deprecated
	public ResponseEntity<List<T>> list() {
		return ResponseEntity.ok(service.list());
	}

	@GetMapping("/get-by-id")
	@Deprecated
	public ResponseEntity<T> getByIdOld(@RequestParam ID id) {
		return ResponseEntity.of(Optional.ofNullable(service.getById(id)));
	}
}
