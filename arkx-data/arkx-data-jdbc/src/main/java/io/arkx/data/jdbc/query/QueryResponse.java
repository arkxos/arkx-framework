package io.arkx.data.jdbc.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class QueryResponse {
	private boolean success;
	private String message;
	private List<QueryResult> results;
	private QueryStats stats;
}