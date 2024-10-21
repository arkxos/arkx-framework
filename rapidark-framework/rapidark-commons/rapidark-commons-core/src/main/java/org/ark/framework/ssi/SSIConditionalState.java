package org.ark.framework.ssi;

/**
 * @class org.ark.framework.ssi.SSIConditionalState
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:34:13 
 * @version V1.0
 */
class SSIConditionalState {
	boolean branchTaken = false;

	int nestingCount = 0;

	boolean processConditionalCommandsOnly = false;
}