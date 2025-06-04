package org.ark.framework.orm.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;

import org.ark.framework.infrastructure.repositories.Person;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;

import io.arkx.framework.commons.util.DateUtil;
import io.arkx.framework.data.jdbc.Criteria;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Restrictions;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;


/**   
 * @class org.ark.framework.orm.query.CriteriaTest
 * @private
 * @author Darkness
 * @date 2013-2-19 下午01:49:13 
 * @version V1.0   
 */
public class CriteriaTest {

	@Before("")
    public void before() {
		Person person1 = new Person();
		person1.setName("darkness");
		person1.setAge(10);
		person1.setBirthday(DateUtil.parseDateTime("2012-03-25 11:25:14"));
		
		Person person2 = new Person();
		person2.setName("darkness");
		person2.setAge(15);
		person2.setBirthday(DateUtil.parseDateTime("2012-02-25 11:25:14"));
		
		Session session  = SessionFactory.openSession();
		session.beginTransaction();
		
		session.save(person1);
		session.save(person2);
		
		session.commit();
	}
	
	@Test
	public void lt() {
		Criteria criteria = getSession().createCriteria(Person.class);
		
		Date startTime = DateUtil.parseDateTime("2012-10-25 11:25:14");
		criteria.add(Restrictions.lt(Person.Birthday, startTime));
		
		List<Person> persons = criteria.findEntities();
		assertEquals(2, persons.size());
	}
	
	@Test
	public void gt() {
		Criteria criteria = getSession().createCriteria(Person.class);
		
		Date startTime = DateUtil.parseDateTime("2012-02-28 11:25:14");
		criteria.add(Restrictions.gt(Person.Birthday, startTime));
		
		List<Person> persons = criteria.findEntities();
		assertEquals(1, persons.size());
	}
	
	@Test
	public void between() {
		Criteria criteria = getSession().createCriteria(Person.class);
		
		Date startTime = DateUtil.parseDateTime("2012-02-22 11:25:14");
		Date endTime = DateUtil.parseDateTime("2012-02-28 11:25:14");
		criteria.add(Restrictions.gt(Person.Birthday, startTime));
		criteria.add(Restrictions.lt(Person.Birthday, endTime));
		
		List<Person> persons = criteria.findEntities();
		assertEquals(1, persons.size());
	}
	
	@After("")
    public void after() {
		Query queryBuilder = getSession().createQuery("DELETE FROM test__person WHERE NAME='darkness'");
		queryBuilder.executeNoQuery();
	}

	public Session getSession() {
		return SessionFactory.currentSession();
	}
}
