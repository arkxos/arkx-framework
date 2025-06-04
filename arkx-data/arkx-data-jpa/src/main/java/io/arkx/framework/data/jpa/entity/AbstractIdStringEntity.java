package io.arkx.framework.data.jpa.entity;

import lombok.Data;

import jakarta.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class AbstractIdStringEntity extends BaseEntity<String> {

}
