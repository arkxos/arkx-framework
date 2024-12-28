package com.rapidark.framework.data.fasttable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.rapidark.framework.commons.util.SystemInfo;
import com.rapidark.framework.commons.util.TimeWatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDbTest {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		int recordSize = 100*10000;
		List<Person> dataList = new ArrayList<>();
		for (int i = 0; i < recordSize; i++) {
			Person person = new Person();
			person.setName("fastdb" + i);
			person.setAge(i);
			person.setMoney(100);
			person.setSalary(99);
			person.setMarried(false);
			
			dataList.add(person);
		}
		
		String recordFilePath = SystemInfo.userDir() + File.separator + "temp" + File.separator;
		FastDatabase database = new FastDatabase(recordFilePath);
		database.clear();
		
		TimeWatch timeWatch = new TimeWatch();
		timeWatch.startWithTaskName("save object");
		
		database.registerConverter(new PersonRecordConverter());
		
		database.save("person",  dataList);
		
		timeWatch.stopAndPrint();
		
		
		timeWatch = new TimeWatch();
		timeWatch.startWithTaskName("read object");
		
		List<Person> recordsFromFile = database.queryAll("person", Person.class);

		assertEquals(recordSize, recordsFromFile.size());
		System.out.println(recordsFromFile.size());
		System.out.println(recordsFromFile.get(0));
		// System.out.println(recordsFromFile.get(recordsFromFile.size()-10));
		System.out.println(recordsFromFile.get(recordsFromFile.size() - 1));
		
		timeWatch.stopAndPrint();
	}
}
