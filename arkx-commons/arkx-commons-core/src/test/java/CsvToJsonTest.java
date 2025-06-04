import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.arkxos.framework.commons.util.FileUtil;
import com.google.common.base.Joiner;

/**
 * @author Darkness
 * @date 2020-08-04 00:28:44
 * @version V1.0
 */
public class CsvToJsonTest {

	public static void main(String[] args) throws IOException {
		
		List<String> lines = Files.readAllLines(Paths.get("D:\\git\\rapid-ark-v2\\rapid-ark-framework\\src\\test\\java\\test.csv"), StandardCharsets.UTF_8);
		List<String> jsonList = new ArrayList<>();
		String[] headers = null;
		boolean isFirst = true;
		int lineIndex = 0;
		Set<String> idSet = new HashSet<String>();
		for (String line : lines) {
			lineIndex++;
//			System.out.println(lineIndex);
			if (isFirst) {
				headers = line.split(",");
				isFirst = false;
			} else {
				try {
					String[] data = line.split(",");
					Map<String, String> linkedMap = new LinkedHashMap<String, String>();
					for (int i = 0; i < headers.length && i < data.length; i++) {
						linkedMap.put(headers[i], data[i]);
					}
					idSet.add(data[0]);
					jsonList.add(JSON.toJSONString(linkedMap));
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
					
				}
				
			}
		}
		String jsonText = "[\r\n" + Joiner.on(",\r\n").join(jsonList).toString() + "\r\n]";
		FileUtil.writeText("D:\\git\\rapid-ark-v2\\rapid-ark-framework\\src\\test\\java\\test.json", jsonText);
		System.out.println("idSet: " + idSet.size());
	}
}

