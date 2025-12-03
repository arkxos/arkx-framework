package io.arkx.framework.data.fasttable;

/**
 * @author Darkness
 * @date 2016年12月6日 下午1:01:42
 * @version V1.0
 */
public abstract class SingleSearcher<T, K extends Comparable<? super K>> implements ISearcher<T, K, T> {

    @Override
    public T search(T foundRecord, long foundRecordIndex, long low, long high, FastTable recordFile,
            RecordFunction<T> converter, int recordLength) {
        return foundRecord;
    }

}
