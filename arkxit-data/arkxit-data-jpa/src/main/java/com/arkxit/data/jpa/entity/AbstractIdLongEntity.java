package com.arkxit.data.jpa.entity;

import lombok.Data;

import jakarta.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class AbstractIdLongEntity extends BaseEntity<Long> {

}
