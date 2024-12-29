package com.arkxit.data.jpa.entity;

import lombok.Data;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class IdLongEntity extends AbstractIdLongEntity {

    @Id
//    @Column(length = 22)
    // @GeneratedValue(generator  = "myIdStrategy")
    // @GenericGenerator(name = "myIdStrategy", strategy = "assigned")
    protected Long id;

}
