package io.arkx.framework.data.common.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class StringIdEntity extends BaseEntity<String> implements StringId {

}
