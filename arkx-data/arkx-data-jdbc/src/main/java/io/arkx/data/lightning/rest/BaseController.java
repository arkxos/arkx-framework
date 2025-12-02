package io.arkx.data.lightning.rest;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import io.arkx.data.lightning.service.BaseService;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.commons.model.PageResult;
import io.arkx.framework.commons.web.ResponseResult;

/**
 * 通用基础控制器 - 支持完整的CRUD、批量操作、状态管理和原生SQL查询
 *
 * @author Nobody
 * @date 2025-08-01
 * @since 1.0
 */
public class BaseController<T, ID extends Serializable, S extends BaseService<T, ID, ?>> {

    protected final S myService;

    public BaseController(S myService) {
        this.myService = myService;
    }

    // ==================== 基本CRUD操作 ====================

    @GetMapping("/{id}")
    public ResponseResult<T> getById(@PathVariable("id") ID id) {
        return ResponseResult.ok(myService.getById(id));
    }

    @GetMapping("/all")
    public ResponseResult<List<T>> getAll() {
        return ResponseResult.ok(myService.findAll());
    }

    @GetMapping("/page")
    public ResponseResult<PageResult<T>> getAllPage(@PageableDefault Pageable pageable) {
        return ResponseResult.ok(myService.findAll(pageable));
    }

    @GetMapping("/sorted")
    public ResponseResult<List<T>> getAllSorted(Sort sort) {
        return ResponseResult.ok(myService.findAll(sort));
    }

    @PostMapping
    public ResponseResult<T> create(@RequestBody T entity) {
        T savedEntity = myService.insert(entity);
        return ResponseResult.ok(savedEntity);
    }

    @PutMapping
    public ResponseResult<T> update(@RequestBody T entity) {
        T updatedEntity = myService.update(entity);
        return ResponseResult.ok(updatedEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable ID id) {
        myService.deleteById(id);
        return ResponseResult.ok();
    }

    @DeleteMapping
    public ResponseResult<Void> deleteAll() {
        myService.deleteAll();
        return ResponseResult.ok();
    }

    // ==================== 批量操作 ====================

    @PostMapping("/batch")
    public ResponseResult<List<T>> createBatch(@RequestBody List<T> entities) {
        List<T> savedEntities = myService.saveAll(entities);
        return ResponseResult.ok(savedEntities);
    }

    @GetMapping("/batch")
    public ResponseResult<List<T>> getBatch(@RequestParam Collection<ID> ids) {
        return ResponseResult.ok(myService.findAllById(ids));
    }

    @DeleteMapping("/batch")
    public ResponseResult<Void> deleteBatch(@RequestParam Collection<ID> ids) {
        myService.deleteAllById(ids);
        return ResponseResult.ok();
    }

    // ==================== 批量Map操作 ====================

    @GetMapping("/mget")
    public ResponseResult<Map<ID, T>> mget(@RequestParam Collection<ID> ids) {
        return ResponseResult.ok(myService.mget(ids));
    }

    @GetMapping("/mget-one-by-one")
    public ResponseResult<Map<ID, T>> mgetOneByOne(@RequestParam Collection<ID> ids) {
        return ResponseResult.ok(myService.mgetOneByOne(ids));
    }

    @GetMapping("/find-all-one-by-one")
    public ResponseResult<List<T>> findAllOneByOne(@RequestParam Collection<ID> ids) {
        return ResponseResult.ok(myService.findAllOneByOne(ids));
    }

    // ==================== 状态管理 ====================

    @PatchMapping("/{id}/toggle-status")
    public ResponseResult<Void> toggleStatus(@PathVariable ID id) {
        myService.toggleStatus(id);
        return ResponseResult.ok();
    }

    @PatchMapping("/fake-delete")
    public ResponseResult<Void> fakeDelete(@RequestParam ID... ids) {
        myService.fakeDelete(ids);
        return ResponseResult.ok();
    }

    // ==================== 示例查询 ====================

    @PostMapping("/by-example")
    public ResponseResult<List<T>> findByExample(@RequestBody T example) {
        return ResponseResult.ok(myService.findAll(Example.of(example)));
    }

    @PostMapping("/by-example/page")
    public ResponseResult<PageResult<T>> findByExamplePage(@RequestBody T example, @PageableDefault Pageable pageable) {
        return ResponseResult.ok(myService.findAll(Example.of(example), pageable));
    }

    @PostMapping("/by-example/sorted")
    public ResponseResult<List<T>> findByExampleSorted(@RequestBody T example, Sort sort) {
        return ResponseResult.ok(myService.findAll(Example.of(example), sort));
    }

    @PostMapping("/by-example/exists")
    public ResponseResult<Boolean> existsByExample(@RequestBody T example) {
        return ResponseResult.ok(myService.exists(Example.of(example)));
    }

    @PostMapping("/by-example/count")
    public ResponseResult<Long> countByExample(@RequestBody T example) {
        return ResponseResult.ok(myService.count(Example.of(example)));
    }

    // ==================== 原生SQL查询 ====================

    @PostMapping("/query/data-table")
    public ResponseResult<DataTable> queryDataTable(@RequestParam String sql,
            @RequestBody(required = false) Object[] params) {
        return ResponseResult.ok(myService.queryDataTable(sql, params));
    }

    @PostMapping("/query/map")
    public ResponseResult<List<Map<String, Object>>> queryMap(@RequestParam String sql,
            @RequestBody(required = false) Object[] params) {
        return ResponseResult.ok(myService.queryMap(sql, params));
    }

    @PostMapping("/query/list")
    public ResponseResult<List<T>> queryList(@RequestParam String sql, @RequestBody(required = false) Object[] params) {
        return ResponseResult.ok(myService.queryList(sql, params));
    }

    @PostMapping("/query/long")
    public ResponseResult<Long> queryForLong(@RequestParam String sql, @RequestBody(required = false) Object[] params) {
        return ResponseResult.ok(myService.queryForLong(sql, params));
    }

    // ==================== 工具方法 ====================

    @GetMapping("/exists/{id}")
    public ResponseResult<Boolean> exists(@PathVariable ID id) {
        return ResponseResult.ok(myService.existsById(id));
    }

    @GetMapping("/count")
    public ResponseResult<Long> count() {
        return ResponseResult.ok(myService.count());
    }

    @GetMapping("/support/{modelType}")
    public ResponseResult<Boolean> support(@PathVariable String modelType) {
        return ResponseResult.ok(myService.support(modelType));
    }

    // ==================== 兼容性方法 ====================

    @GetMapping("/list")
    @Deprecated
    public ResponseResult<List<T>> list() {
        return ResponseResult.ok(myService.list());
    }

    @GetMapping("/get-by-id")
    @Deprecated
    public ResponseResult<T> getByIdOld(@RequestParam ID id) {
        return ResponseResult.ok(myService.getById(id));
    }

    @GetMapping("/findChildrenByParentId")
    public ResponseResult<List<T>> findChildrenByParentId(@RequestParam ID parentId) {
        return ResponseResult.ok(myService.findChildrenByParentId(parentId));
    }

    @GetMapping("/tree")
    public ResponseResult<Treex<String, T>> findAllTree() {
        return ResponseResult.ok(myService.findAllTree());
    }

    /**
     * 导出部门
     *
     * @return
     */
    // @ResponseExcel
    // @GetMapping("/export")
    // public List<DeptExcelVo> export() {
    // return sysOrganizationService.listExcelVo();
    // }
    //
    // /**
    // * 导入部门
    // * @param excelVOList
    // * @param bindingResult
    // * @return
    // */
    // @PostMapping("import")
    // public ResponseResult importDept(@RequestExcel List<DeptExcelVo> excelVOList,
    // BindingResult bindingResult) {
    //
    // return sysOrganizationService.importDept(excelVOList, bindingResult);
    // }

}
