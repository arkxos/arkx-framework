package com.arkxos.framework.data.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.data.xml.XMLDocument;
import com.arkxos.framework.data.xml.XMLElement;
import com.arkxos.framework.data.xml.XMLMultiLoader;
import com.arkxos.framework.data.xml.XMLParser;

public class XMLTest {

	@Test
	public void load() {
		String path = this.getClass().getClassLoader().getResource("weblogic.xml").getPath();
		String xml = FileUtil.readText(new File(path));
		long t = System.currentTimeMillis();
		XMLParser p = new XMLParser(xml);
		for (int i = 0; i < 100000; i++) {
			p = new XMLParser(xml);
			p.parse();
		}
		//System.out.println(p.getDocument());
		assertTrue((System.currentTimeMillis() - t)<5000);
	}
	
	private void assertXml(XMLMultiLoader xmlMultiLoader) {
		List<XMLElement> persons = xmlMultiLoader.elements("root.persons.person");
		assertEquals(2, persons.size());

		XMLElement person = xmlMultiLoader.elements("root.persons.person", "id", "1");
		assertEquals("darkness", person.element("name").getText());
	}

	@Test
	public void loadXml() {
		String path = this.getClass().getClassLoader().getResource("Person.xml").getPath();
		XMLMultiLoader xmlMultiLoader = new XMLMultiLoader();
		xmlMultiLoader.load(path);
		assertXml(xmlMultiLoader);
	}

	@Test
	public void writeXml() {
		XMLDocument xmlDocument = new XMLDocument();
		XMLElement root = xmlDocument.createRoot("root");
		XMLElement persons = root.addElement("persons");

		XMLElement person1 = persons.addElement("person");
		person1.addAttribute("id", "1");
		XMLElement person1Name = person1.addElement("name");
		person1Name.setText("darkness");

		XMLElement person2 = persons.addElement("person");
		person2.addAttribute("id", "2");
		XMLElement person2Name = person2.addElement("name");
		person2Name.setText("sky");

		XMLMultiLoader xmlMultiLoader = new XMLMultiLoader();
		xmlMultiLoader.load(new ByteArrayInputStream(xmlDocument.asXML().getBytes()));

		assertXml(xmlMultiLoader);
	}
}

