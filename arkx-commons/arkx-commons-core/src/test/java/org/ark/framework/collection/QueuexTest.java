package org.ark.framework.collection;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang.math.RandomUtils;
import org.ark.common.Person;
import org.junit.jupiter.api.Test;

import com.arkxos.framework.commons.collection.Queuex;

/**   
 * 
 * @author Darkness
 * @date 2012-11-29 下午05:41:02 
 * @version V1.0   
 */
public class QueuexTest {
	
	@Test
	public void testCompare() {
		
		Queuex<Person> queuex = new Queuex<Person>(100);
		for (int i = 0; i < 5; i++) {
			queuex.push(new Person(RandomUtils.nextInt(100)));
		}
		//System.out.println("before=============");
//		for (int i = 0; i < queuex.size(); i++) {
//			Person p = (Person) queuex.get(i);
//		}
//		queuex.sort(new Comparator<Person>() {
//
//			public int compare(Person o1, Person o2) {
//				if (o1.getAge() > o2.getAge()) {
//					return 1;
//				}
//				return -1;
//			}
//		});
		int min = 0;
		//System.out.println("sort after=============");
		for (int i = 0; i < queuex.size(); i++) {
			Person p = (Person) queuex.get(i);
			
			assertTrue(min <= p.getAge());
			min = p.getAge();
		}
	}


}
