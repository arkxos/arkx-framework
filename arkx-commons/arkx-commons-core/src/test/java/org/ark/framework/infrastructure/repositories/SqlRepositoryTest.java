package org.ark.framework.infrastructure.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.ark.framework.infrastructure.repositoryframework.RepositoryFactory;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.Test;

import io.arkx.framework.XTest;

/**
 * @author Darkness
 * @date 2012-9-27 上午9:29:24
 * @version V1.0
 */
public class SqlRepositoryTest extends XTest {

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
    public void findById() {

        String id = "3";

        Person person = personRepository.findById(id);

        assertEquals(person.getId(), id);
    }

    @Test
    public void findAll() {

        List<Person> persons = personRepository.findAll();

        assertTrue(persons.size() > 0);
    }

    @Test
    public void update() {

        String id = "3";

        Person person = personRepository.findById(id);

        person.setName("darkness_update");
        person.setSex("male_update");
        person.setAge(28);

        personRepository.update(person);

        assertNotNull(person.getId());
    }

    @After("")
    public void delete() {

        assertEquals(personRepository.delete("3"), 1);
    }

}
