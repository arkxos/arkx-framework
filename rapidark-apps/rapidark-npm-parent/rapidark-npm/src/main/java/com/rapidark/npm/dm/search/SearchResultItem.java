package com.rapidark.npm.dm.search;

import com.rapidark.npm.dm.AbstractInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

/**
 * Data class to store search result item
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SearchResultItem extends AbstractInfo {

	private float searchScore;
	private SearchScore score;
	@JsonProperty("package")
	private SearchResultPackage searchPackage;
}
