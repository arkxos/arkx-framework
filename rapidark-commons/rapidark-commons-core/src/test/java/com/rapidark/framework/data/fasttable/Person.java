package com.rapidark.framework.data.fasttable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.rapidark.framework.data.fasttable.annotation.FastColumn;
import com.rapidark.framework.data.fasttable.annotation.Serialize;
import com.rapidark.framework.data.fasttable.util.RecordConverterGenerator;

/**
 * 
 * @author Darkness
 * @date 2016年11月8日 下午6:07:40
 * @version V1.0
 */
@Serialize
public class Person {

	@FastColumn(length = 10)
	private String name;
	@FastColumn
	private int age;
	@FastColumn
	private double money;
	@FastColumn
	private float salary;
	@FastColumn
	private boolean isMarried;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public float getSalary() {
		return salary;
	}

	public void setSalary(float salary) {
		this.salary = salary;
	}

	public boolean isMarried() {
		return isMarried;
	}

	public void setMarried(boolean isMarried) {
		this.isMarried = isMarried;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static void main(String[] args) {

		String text = RecordConverterGenerator.generate(Person.class);
		System.out.println(text);
	}
}
