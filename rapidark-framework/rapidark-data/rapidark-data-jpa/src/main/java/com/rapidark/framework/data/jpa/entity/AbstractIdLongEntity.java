package com.rapidark.framework.data.jpa.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class AbstractIdLongEntity extends BaseEntity<Long> {

}
