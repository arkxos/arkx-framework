package com.xdreamaker.demo.axon.query.entity;


import com.xdreamaker.demo.axon.domain.model.ContractInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Version;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author darkness
 * @date 2021/6/26 22:53
 * @version 1.0
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContractView implements ContractInterface {

    @Id
    @Column(length = 64)
    private Long id;

    private String name;

    private String partyA;

    private String partyB;

    private String industryName;

    private boolean deleted = false;

    @Version
    private Long version;

}
