package io.arkx.data.jdbc.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class FormatOptions {
	private boolean pretty = true;
	private boolean includeComments;
	private String comment;
}