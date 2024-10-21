package com.rapidark.framework.framework.common;

import java.util.Date;
import java.util.List;


/**
 * 
 * @author Darkness
 * @date 2012-9-27 上午9:27:46
 * @version V1.0
 */
public class Person extends Entity {

	public static final String BornTime = "BornTime";
	
	private String name;
	private String sex;
	private Date bornTime;
	private int age;
	
	private List<Person> childs;

	public Person(String name, int age, Date bornTime) {
		this.name = name;
		this.age = age;
		this.bornTime = bornTime;
	}

	public Person() {
	}

	public Person(int age) {
		this.age = age;
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

	public Date getBornTime() {
		return bornTime;
	}

	public void setBornTime(Date bornTime) {
		this.bornTime = bornTime;
	}

	public List<Person> getChilds() {
		return childs;
	}

	public void setChilds(List<Person> childs) {
		this.childs = childs;
	}
	
	@Override
	public String toString() {
		return "name: " + name + ", birthday:" + bornTime + ", sex: " + sex + ", age:" + age;
	}
	
}
