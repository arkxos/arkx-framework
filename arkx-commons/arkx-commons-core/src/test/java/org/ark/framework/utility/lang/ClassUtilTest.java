package org.ark.framework.utility.lang;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javassist.NotFoundException;

import org.ark.common.Person;
import org.junit.jupiter.api.Test;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.DateUtil;
import io.arkx.framework.commons.util.lang.ClassUtil;
import io.arkx.framework.commons.util.lang.ClassUtil.MissingLVException;

/**
 *
 * @author Darkness
 * @date 2012-3-13 下午12:49:46
 * @version V1.0
 */
public class ClassUtilTest {

    @Test
    public void testGetMethodParamNames() throws NotFoundException, MissingLVException {

        String[] names = ClassUtil.getMethodParamNames(MyPerson.class, "getPerson");
        // assertArrayEquals(new String[] { "personName" }, names);

        names = ClassUtil.getMethodParamNames(MyPerson.class, "getPerson", String.class, String.class);
        assertArrayEquals(new String[]{"personName", "personSex"}, names);

        names = ClassUtil.getMethodParamNames(MyPerson.class, "getStaticPerson");
        assertArrayEquals(new String[]{"staticName", "personName", "personSex"}, names);
        for (String string : names) {
            System.out.println(string);
        }
    }

    @Test
    public void testGenericType() {
        assertEquals(Child.class, new ChildClass().getClassGenricType());
    }

    @Test
    public void mapToObject() {

        Map<String, Object> params = new Mapx<String, Object>();
        params.put("id", 1);
        params.put("name", "darkness");
        params.put("age", 28);
        params.put("sex", "male");
        params.put("other", "male");

        params.put("childs(0).id", "2");
        params.put("childs(0).name", "darkness2");
        params.put("childs(0).age", "2");
        params.put("childs(0).sex", "fmale");
        params.put("childs(0).bornTime", "2012-01-22");

        params.put("childs(3).id", "3");
        params.put("childs(3).name", "darkness3");
        params.put("childs(3).age", "3");
        params.put("childs(3).sex", "fmale");
        params.put("childs(3).other", "3");

        List<Person> persons = ClassUtil.mapToObjectList(Person.class, params, "childs");
        assertEquals(persons.size(), 2);

        Person person = ClassUtil.mapToObject(Person.class, params);

        assertEquals("1", person.getId());
        assertEquals("darkness", person.getName());
        assertEquals(28, person.getAge());
        assertEquals("male", person.getSex());
    }

    @Test
    public void objectToObject() {

        Person personOld = new Person();
        personOld.setId("1");
        personOld.setName("darkness");
        personOld.setAge(28);
        personOld.setSex("male");

        Person person = new Person();
        person.setId("1");
        person.setName("darknessNew");
        person.setAge(29);
        person.setSex("");

        ClassUtil.applyToObject(personOld, person);

        assertEquals("1", personOld.getId());
        assertEquals("darknessNew", personOld.getName());
        assertEquals(29, personOld.getAge());
        assertEquals("", personOld.getSex());
    }

    /**
     * 对象转map
     *
     * @author Darkness
     * @date 2012-11-27 下午04:54:13
     * @version V1.0
     */
    @Test
    public void objectToMapx() {

        String dateTime = "2012-10-12 15:29:30";

        Person person = new Person();
        person.setId("1");
        person.setAge(26);
        person.setBornTime(DateUtil.parseDateTime(dateTime));
        person.setName("darkness");
        person.setSex("male");

        Map<String, Object> propValues = ClassUtil.objectToMapx(person);

        assertEquals(propValues.size(), 6);
        assertEquals(propValues.get("name"), "darkness");
        assertEquals(propValues.get("age"), 26);
        assertEquals(dateTime, DateUtil.toDateTimeString((Date) propValues.get("bornTime")));
    }

    /**
     * 对象覆盖
     *
     * @author Darkness
     * @date 2012-11-27 下午04:54:35
     * @version V1.0
     */
    @Test
    public void applyToObject() {

        String dateTime = "2012-10-12 15:29:30";

        Person person = new Person();
        person.setId("1");
        person.setAge(1);
        person.setBornTime(DateUtil.parseDateTime(dateTime));
        person.setName("darkness");
        person.setSex("man");

        Person personNew = new Person();
        personNew.setId("2");
        personNew.setAge(3);
        String dateTimeNew = "2012-10-15 15:29:30";
        personNew.setBornTime(DateUtil.parseDateTime(dateTimeNew));
        personNew.setName("darknessNew");
        personNew.setSex("manNew");

        ClassUtil.applyToObject(person, personNew);

        assertEquals(DateUtil.toDateTimeString(person.getBornTime()), dateTimeNew);
    }

    static class MyPerson {

        private String personName;
        private int age;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getPersonName() {
            return personName;
        }

        public void setPersonName(String personName) {
            this.personName = personName;
        }

        public List<MyPerson> getPerson(String personName) {
            return null;
        }

        public void getPerson(String personName, String personSex) {
        }

        public static void getStaticPerson(String staticName, String personName, String personSex) {
        }
    }

    static class ParentClass<T> {
        public Class<?> getClassGenricType() {
            return ClassUtil.getSuperClassGenricType(getClass());
        }
    }

    static class ChildClass extends ParentClass<Child> {
    }

    static class Child {
    }

}
