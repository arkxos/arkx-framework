package io.arkx.framework.data.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseStringIdTreeEntity extends StringIdTreeEntity {

	private String name;

}
