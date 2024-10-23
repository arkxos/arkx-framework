package com.rapidark.framework.data.jpa.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class IdStringEntity extends AbstractIdStringEntity {

    @Id
//    @Column(length = 22)
    // @GeneratedValue(generator  = "myIdStrategy")
    // @GenericGenerator(name = "myIdStrategy", strategy = "assigned")
    protected String id;

}
