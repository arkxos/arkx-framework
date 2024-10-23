package com.rapidark.framework.data.jpa.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class IdLongEntity extends AbstractIdLongEntity {

    @Id
//    @Column(length = 22)
    // @GeneratedValue(generator  = "myIdStrategy")
    // @GenericGenerator(name = "myIdStrategy", strategy = "assigned")
    protected Long id;

}
