package io.arkx.framework.data.fasttable;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Darkness
 * @date 2017年7月12日 下午3:05:39
 * @version 1.0
 * @since 1.0 
 */
public class FastTableHeaderTest {
	
	String TEST_TABLE_NAME = "testPerson";
	
	private FastTableHeader preparedHeader() {
		FastTableHeader header = new FastTableHeader();
		header.setTableName(TEST_TABLE_NAME);
		
		header.addColumn(FastColumn.dateColumn("date"));
		header.addColumn(FastColumn.stringColumn("string",20));
		header.addColumn(FastColumn.stringColumn("fixedstring",6));
		header.addColumn(FastColumn.intColumn("int"));
		header.addColumn(FastColumn.floatColumn("float"));
		header.addColumn(FastColumn.doubleColumn("double"));
		header.addColumn(FastColumn.longColumn("long"));
		
		header.setRowSize(0);
		
		return header;
	}

	@Test
	public void encodeAndDecode() {
		FastTableHeader header = preparedHeader();
		
		ByteBuffer headerBuffer = FastTableHeaderEncoder.encode(header);
		int headerLength = headerBuffer.limit();
		assertEquals(254, headerLength);
		FastTableHeader decodedHeader = FastTableHeaderDecoder.decode(headerBuffer);

		assertEquals(TEST_TABLE_NAME, decodedHeader.getTableName());
		assertEquals(0, decodedHeader.getRowSize());
		assertEquals(7, decodedHeader.columnSize());
		
//		assertEquals("string", decodedHeader.getColumns().get(1).getName());
//		assertEquals("float", decodedHeader.getColumns().get(3).getName());
		
		//assertEquals(2, decodedHeader.getIndexColumns().size());
		//assertEquals("date", decodedHeader.getIndexColumns().get(0).getName());
		//assertEquals("fixedstring", decodedHeader.getIndexColumns().get(1).getName());
	}
}
