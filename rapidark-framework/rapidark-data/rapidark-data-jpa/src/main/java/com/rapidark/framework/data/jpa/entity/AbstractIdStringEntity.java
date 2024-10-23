package com.rapidark.framework.data.jpa.entity;

import lombok.Data;

import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class AbstractIdStringEntity extends BaseEntity<String> {

}
