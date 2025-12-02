package io.arkx.framework.commons.simplequeue;

/**
 * Listener of Spider on page processing. Used for monitor and such on.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface QueueListener {

	public void onSuccess(ElementWarpper request);

	public void onError(ElementWarpper request);

}
