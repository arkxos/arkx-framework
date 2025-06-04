package com.arkxos.framework.data.jpa.entity;

import lombok.Data;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class IdStringEntity extends AbstractIdStringEntity {

    @Id
//    @Column(length = 22)
    // @GeneratedValue(generator  = "myIdStrategy")
    // @GenericGenerator(name = "myIdStrategy", strategy = "assigned")
    protected String id;

}
