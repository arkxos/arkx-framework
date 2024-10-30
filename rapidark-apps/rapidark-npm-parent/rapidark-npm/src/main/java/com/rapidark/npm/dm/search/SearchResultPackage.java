package com.rapidark.npm.dm.search;

import java.util.Arrays;
import java.util.Map;

import com.rapidark.npm.dm.AbstractArtifactInfo;
import com.rapidark.npm.dm.HumanInfo;
import com.rapidark.npm.dm.RepositoryInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

/**
 * Data class to store search result
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SearchResultPackage extends AbstractArtifactInfo {

	private HumanInfo publisher;
	private String npmUrl;

	@JsonProperty("links")
	public void setLinks(Map<String, String> links) {
		for(Map.Entry<String, String> entry : links.entrySet()) {
			switch (entry.getKey()) {
				case "homepage":
					setHomepage(entry.getValue());
					break;
				case "repository":
					RepositoryInfo rep = new RepositoryInfo();
					setRepositories(Arrays.asList(rep));
				case "bugs":
					setBugsUrl(entry.getValue());
					break;
				case "npm":
					setNpmUrl(entry.getValue());
					break;

			}
		}
	}
}
