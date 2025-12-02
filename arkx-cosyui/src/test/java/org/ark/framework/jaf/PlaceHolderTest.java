package org.ark.framework.jaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import io.arkx.framework.commons.collection.Mapx;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Darkness
 * @date 2013-3-6 下午03:21:10
 * @version V1.0
 */
public class PlaceHolderTest {

    @Test
    public void placeHolder1() {
        String holder = "user.name";
        PlaceHolder placeHolder = new PlaceHolder(holder);
        assertEquals("user", placeHolder.getPrefix());
        assertEquals("name", placeHolder.getVarName());
    }

    @Test
    public void placeHolder2() {
        String holder = "darkness";
        PlaceHolder placeHolder = new PlaceHolder(holder);
        assertNull(placeHolder.getPrefix());
        assertEquals("darkness", placeHolder.getVarName());
    }

    @Test
    public void eval() {
        String var = "my.name";

        Mapx<String, Object> params = new Mapx<String, Object>();
        params.put("name", "darkness");

        PlaceHolderContext context = PlaceHolderContext.getInstance(null, (HttpServletRequest) null);
        context.addMap(params, "my");
        Object value = context.eval(new PlaceHolder(var));
        assertEquals("darkness", value);
    }
}
