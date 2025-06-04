package com.arkxos.framework.data.jpa.repository;
//package com.arkxos.framework.jpa.repository;
//
//import java.beans.PropertyDescriptor;
//import java.io.Serializable;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import jakarta.persistence.EntityManager;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.data.jpa.repository.support.JpaEntityInformation;
//import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.ReflectionUtils;
//
//import com.arkxos.framework.jpa.domain.ext.Status;
//
///**
// * <p>通用Jpa仓库实现</p>
// *
// * @author <a href="mailto:stormning@163.com">stormning</a>
// * @version V1.0, 2015/8/7
// */
//public class GenericJpaRepositoryImpl<T, ID extends Serializable>
//        extends SimpleJpaRepository<T, ID> implements GenericJpaRepository<T, ID>, Serializable {
//
//	private static final long serialVersionUID = 1L;
//
//	private JpaEntityInformation<T, ID> eif;
//
//    private boolean isStatusAble = false; // 实体类是否弃用状态字段
//    private Method statusReadMethod;// 状态字段读方法
//    private Method statusWriteMethod;// 状态字段写方法
//
//    public GenericJpaRepositoryImpl(JpaEntityInformation<T, ID> eif, EntityManager em) {
//        super(eif, em);
//        this.eif = eif;
//        this.initStatusInfo();
//    }
//    
//    /**
//          * 初始化状态信息
//     */
//    private void initStatusInfo() {
//    	PropertyDescriptor descriptor = findFieldPropertyDescriptor(eif.getJavaType(), Status.class);
//        isStatusAble = descriptor != null;
//        if (isStatusAble) {
//            statusReadMethod = descriptor.getReadMethod();
//            statusWriteMethod = descriptor.getWriteMethod();
//        }
//    }
//
//    /**
//     * 根据id查询map结构数据
//     */
//    @Override
//    public Map<ID, T> mget(Collection<ID> ids) {
//        return toMap(findAllById(ids));
//    }
//
//    @Override
//    public Map<ID, T> mgetOneByOne(Collection<ID> ids) {
//        return toMap(findAllOneByOne(ids));
//    }
//
//    @Override
//    public List<T> findAllOneByOne(Collection<ID> ids) {
//        List<T> results = new ArrayList<>();
//        for (ID id : ids) {
//            findById(id).ifPresent(results::add);
//        }
//        return results;
//    }
//
//    private Map<ID, T> toMap(List<T> list) {
//        Map<ID, T> result = new LinkedHashMap<>();
//        for (T t : list) {
//            if (t != null) {
//                result.put(eif.getId(t), t);
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 启用、禁用对象
//     */
//    @Override
//    @Transactional
//    public void toggleStatus(ID id) {
//        if (isStatusAble && id != null) {
//            Optional<T> target = findById(id);
//            if (target.isPresent()) {
//                Status status = (Status) ReflectionUtils.invokeMethod(statusReadMethod, target);
//                if (status == Status.ENABLED || status == Status.DISABLED) {
//                    ReflectionUtils.invokeMethod(statusWriteMethod, target,
//                            status == Status.DISABLED ? Status.ENABLED : Status.DISABLED);
//                    save(target.get());
//                }
//            }
//        }
//    }
//
//    /**
//     * 逻辑删除
//     */
//    @SafeVarargs
//    @Override
//    @Transactional
//    public final void fakeDelete(ID... ids) {
//        for (ID id : ids) {
//            changeStatus(id, Status.DELETED);
//        }
//    }
//
//    /**
//     * 改变实体状态
//     * @param id
//     * @param status
//     */
//    private void changeStatus(ID id, Status status) {
//        if (isStatusAble && id != null) {
//            Optional<T> target = findById(id);
//            if (target.isPresent()) {
//                Status oldStatus = (Status) ReflectionUtils.invokeMethod(statusReadMethod, target);
//                if (oldStatus != status) {
//                    ReflectionUtils.invokeMethod(statusWriteMethod, target, status);
//                    save(target.get());
//                }
//            }
//        }
//    }
//
//    /**
//     * 获取对象属性描述
//     * @param target
//     * @param fieldClass
//     * @return
//     */
//    private PropertyDescriptor findFieldPropertyDescriptor(Class<?> target, Class<?> fieldClass) {
//        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(target);
//        for (PropertyDescriptor pd : propertyDescriptors) {
//            if (pd.getPropertyType() == fieldClass) {
//                return pd;
//            }
//        }
//        return null;
//    }
//}
