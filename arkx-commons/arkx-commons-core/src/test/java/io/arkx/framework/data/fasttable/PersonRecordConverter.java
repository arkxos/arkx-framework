package io.arkx.framework.data.fasttable;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class PersonRecordConverter extends RecordConverter<Person> {

    FastColumn nameColumn = new FastColumn("name", FastColumnType.String, 10);

    FastColumn ageColumn = new FastColumn("age", FastColumnType.Int);

    FastColumn moneyColumn = new FastColumn("money", FastColumnType.Double);

    FastColumn salaryColumn = new FastColumn("salary", FastColumnType.Float);

    FastColumn isMarriedColumn = new FastColumn("isMarried", FastColumnType.Boolean);

    FastColumn[] columns = new FastColumn[]{nameColumn, ageColumn, moneyColumn, salaryColumn, isMarriedColumn};

    @Override
    public Class<Person> acceptEntityClass() {
        return Person.class;
    }

    @Override
    public List<FastColumn> getColumns() {
        return Arrays.asList(columns);
    }

    @Override
    public void writeEntity2Buffer(Person entity, ByteBuffer recordBuffer) {
        String name = entity.getName();
        writeString(recordBuffer, name, nameColumn.getLength());
        recordBuffer.putInt(entity.getAge());// int
        recordBuffer.putDouble(entity.getMoney());// double
        recordBuffer.putFloat(entity.getSalary());// float
        recordBuffer.put((byte) (entity.isMarried() ? 1 : 0));// boolean
    }

    @Override
    public Person builderObject(ByteBuffer recordBuffer) {
        Person entity = new Person();

        String name = readString(recordBuffer, nameColumn.getLength());
        entity.setName(name);

        int age = recordBuffer.getInt();
        entity.setAge(age);

        double money = recordBuffer.getDouble();
        entity.setMoney(money);

        float salary = recordBuffer.getFloat();
        entity.setSalary(salary);

        boolean isMarried = recordBuffer.get() == 1;
        entity.setMarried(isMarried);

        return entity;
    }

}
