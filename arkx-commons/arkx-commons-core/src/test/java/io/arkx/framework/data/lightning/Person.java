package io.arkx.framework.data.lightning;

import java.nio.ByteBuffer;
import java.time.LocalDate;

import io.arkx.framework.annotation.fastdb.Column;
import io.arkx.framework.annotation.fastdb.Comment;
import io.arkx.framework.annotation.fastdb.FixedString;
import io.arkx.framework.annotation.fastdb.Table;

@Comment("人员信息")
@Table(namespace = "defaultNamespace")
public class Person {

    @Comment("出生日期")
    private LocalDate birthDate;

    @Comment("姓名")
    @Column(length = 12)
    @FixedString
    private String name;

    @Comment("地址")
    @Column(length = 100)
    private String address;

    @Comment("年龄")
    private int age;

    @Comment("薪水")
    private float salary;

    @Comment("年终奖")
    private double yearEndBonus;

    public LocalDate getBirthDate() {
        if (birthDate == null) {
            return LocalDate.MIN;
        }
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

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

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public double getYearEndBonus() {
        return yearEndBonus;
    }

    public void setYearEndBonus(double yearEndBonus) {
        this.yearEndBonus = yearEndBonus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

class PersonColumnDefine {

    public String getTableName() {
        return "Person";
    }

    public PersonColumn[] getDataColumns() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getColumnCount() {
        // TODO Auto-generated method stub
        return 0;
    }

}

class PersonColumn {

    private String columnName;

    private int columnLength;

    public String getColumnName() {
        // TODO Auto-generated method stub
        return null;
    }

    public ColumnType getColumnType() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getLength() {
        // TODO Auto-generated method stub
        return 0;
    }

}

class PersonRowUnit {

    private ByteBuffer rowUnitBuffer = ByteBuffer.allocate(10);

}

class PersonUnitBuilder {

    public PersonRowUnit toUnit(Person person) {
        return null;
    }

    public Person toPerson(PersonRowUnit unit) {
        return null;
    }

}
