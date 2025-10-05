package io.arkx.framework.commons.uid.component;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import io.arkx.framework.commons.uid.UidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-05 20:49
 * @since 1.0
 */
@Component
public class MybatisPlusIdGenerator implements IdentifierGenerator {

    @Lazy
    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public Number nextId(Object entity) {
        return uidGenerator.getUID();
    }

}
