package io.arkx.framework.commons.simplequeue;

/**
 * Downloader is the part that downloads web pages and store in Page object.
 * <br>
 * Downloader has {@link #setThread(int)} method because downloader is always
 * the bottleneck of a crawler, there are always some mechanisms such as pooling
 * in downloader, and pool size is related to thread numbers.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface ElementProcessor {

    /**
     * Downloads web pages and store in Page object.
     *
     * @param request
     *            request
     * @param task
     *            task
     * @return page
     */
    ExecuteResult execute(ElementWarpper elementWarpper, Task task);

    /**
     * get the site settings
     *
     * @return site
     * @see Config
     */
    Config getConfig();
}
