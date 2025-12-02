package org.ark.framework.infrastructure.repositories;

import java.util.Date;

import io.arkx.framework.annotation.Entity;
import io.arkx.framework.annotation.Ingore;
import io.arkx.framework.data.jdbc.BaseEntity;

/**
 * @author Darkness
 * @date 2012-9-27 上午9:27:46
 * @version V1.0
 */
@Entity(name = "test__person")
public class Person extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Ingore
	public static final String Birthday = "birthday";

	private String name;

	private String sex;

	private int age;

	private Date birthday;

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
