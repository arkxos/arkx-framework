package org.ark.framework.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.ark.framework.infrastructure.repositories.Person;
import org.ark.framework.infrastructure.repositories.PersonRepository;
import org.ark.framework.infrastructure.repositoryframework.RepositoryFactory;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.Test;

import io.arkx.framework.data.jdbc.Criteria;
import io.arkx.framework.data.jdbc.Entity;
import io.arkx.framework.data.jdbc.Restrictions;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;
import com.arkxos.framework.XTest;

/**   
 * 
 * @author Darkness
 * @date 2012-11-23 上午10:49:38 
 * @version V1.0   
 */
public class CriteriaTest extends XTest {

	PersonRepository personRepository;
	
	@Override
	public void init() {
		personRepository = RepositoryFactory.getRepository(PersonRepository.class);
		
		Person person = new Person();
		person.setId("3");
		person.setName("darkness1");
		person.setSex("male1");
		person.setAge(28);

		personRepository.save(person);

		assertNotNull(person.getId());
	}
	
	@Test
	public void tst() {
		Criteria criteria = getSession().createCriteria(Person.class);
		criteria.add(Restrictions.in(Entity.Id, "'3', 'b', 'c'"));
		
		List<Person> paramValues = criteria.findEntities();
		assertEquals(1, paramValues.size());
	}
	
	@After("")
    public void delete() {

		assertEquals(personRepository.delete("3"), 1);
	}

	public Session getSession() {
		return SessionFactory.currentSession();
	}
}
