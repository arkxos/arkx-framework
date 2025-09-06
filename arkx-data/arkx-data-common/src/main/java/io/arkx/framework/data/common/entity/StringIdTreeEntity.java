package io.arkx.framework.data.common.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class StringIdTreeEntity extends TreeEntity<String> implements StringId {
}
