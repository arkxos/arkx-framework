package org.ark.framework.ui.expression;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.ark.framework.jaf.IExpressionContext;
import org.ark.framework.jaf.PlaceHolder;
import org.ark.framework.jaf.expression.ExpressionParser;
import org.ark.framework.jaf.expression.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Darkness
 * @date 2012-9-8 下午3:00:32
 * @version V1.0
 */
public class ExpressionParserTest {

	@Test
	public void parseTrue() throws ParseException {

		String condition = "true";
		ExpressionParser ep = new ExpressionParser(condition);

		Assertions.assertTrue((Boolean)ep.execute());
	}

	@Test
	public void parse() throws ParseException {

		String condition = "'darkness sky'!=${person.name}";

		ExpressionParser ep = new ExpressionParser(condition);
		IExpressionContext context = new Context();
		ep.setContext(context);
		
		Assertions.assertTrue((Boolean)ep.execute());
	}

	class Context implements IExpressionContext {

		public Object eval(PlaceHolder paramPlaceHolder) {
			if("person".equals(paramPlaceHolder.getPrefix()) && "name".equals(paramPlaceHolder.getVarName())) {
				return "darkness";
			}
			return null;
		}

	}
}
