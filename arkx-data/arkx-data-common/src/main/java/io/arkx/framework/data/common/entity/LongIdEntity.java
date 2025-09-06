package io.arkx.framework.data.common.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class LongIdEntity extends BaseEntity<Long> implements LongId {

}
