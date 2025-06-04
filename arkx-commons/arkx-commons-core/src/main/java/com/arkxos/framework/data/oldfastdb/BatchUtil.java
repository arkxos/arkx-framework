package com.arkxos.framework.data.oldfastdb;

import io.arkx.framework.commons.collection.ThreeTuple;
import io.arkx.framework.commons.collection.TwoTuple;

/**
 *  
 * @author Darkness
 * @date 2016年6月16日 下午2:21:32
 * @version V1.0
 */
public class BatchUtil {
	
	/**
	 * @author Darkness
	 * @date 2016年6月16日 下午2:30:08
	 * @version V1.0
	 */
	public static PageInfo caculateBatchs(int allCount, int preBatchCount) {
		int batchSize = allCount / preBatchCount;
		
		if(allCount % preBatchCount == 0) {
			return new PageInfo(new ThreeTuple<>(batchSize, preBatchCount, preBatchCount));
		} else {
			return new PageInfo(new ThreeTuple<>(batchSize+1, preBatchCount, allCount % preBatchCount));
		}
	}
	
	public static class PageInfo {
		
		private ThreeTuple<Integer, Integer, Integer> info;
		private int currentPageIndex = 1;

		public PageInfo(ThreeTuple<Integer, Integer, Integer> info) {
			this.info = info;
		}
		
		public ThreeTuple<Integer, Integer, Integer> getInfo() {
			return this.info;
		}
		
		public int getPageCount() {
			return this.info.first;
		}
		
		public int getLimit() {
			return this.info.second;
		}
		
		public void reset() {
			this.currentPageIndex = 1;
		}
		
		public boolean hasNext() {
			return this.currentPageIndex <= this.info.first;
		}
		
		public TwoTuple<Integer, Integer> next() {
			int start = (this.currentPageIndex-1) * this.info.second+1;
			int limit = this.getLimit();
			if(this.currentPageIndex == getPageCount()) {
				limit = this.info.third;
			}

			this.currentPageIndex++;
			return new TwoTuple<Integer, Integer>(start, limit);
		}
		
		@Override
		public String toString() {
			return info.toString();
		}
	}
	
	public static void main(String[] args) {
		
		PageInfo pageInfo = caculateBatchs(99, 10);
		while (pageInfo.hasNext()) {
			System.out.println(pageInfo.next());
			
		}
		System.out.println(caculateBatchs(100, 10));
		System.out.println(caculateBatchs(1098, 100));
	}

}
