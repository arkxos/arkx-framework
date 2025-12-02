package org.ark.framework.jaf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Darkness
 * @date 2013-3-6 下午04:00:36
 * @version V1.0
 */
public class ParamManagerTest {

    @Test
    public void extractParam() {
        String content = "index.html?name=${name}&password=${password}";
        List<String> params = ParamManager.extractParam(content);
        assertEquals(2, params.size());
        assertEquals("name", params.get(0));
        assertEquals("password", params.get(1));
    }

    @Test
    public void replaceParam() {
        String content = "index.html?name=${name}&password=${password}";

        Map<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("name", "darkness");
        paramValues.put("password", "123");

        String newContent = ParamManager.replaceParam(content, paramValues);
        assertEquals("index.html?name=darkness&password=123", newContent);
    }

}
