package io.arkx.framework.commons.i18n;

/**
 * A selection of functions to perform find/replace on strings.
 *
 * @author Andrew Leppard
 */
public class Find {

	/**
	 * Replace the first occurence of the pattern text with replacement text. This
	 * function treats all arguments as simple strings.
	 * @param source The source string containing the text to replace.
	 * @param pattern The pattern to find and replace.
	 * @param replacement The text to use to replace the pattern.
	 * @return The version of the source string after the find/replace.
	 */
	public static String replace(String source, String pattern, String replacement) {
		// This function used to use Java's regex code, but this was unsatisfactory
		// for a simple find replace call because it did not treat the replacement
		// text as simple text, instead it looked for special symbols to do grouping,
		// referencing etc. This caused exceptions to be thrown when we wanted
		// to replace '%' with $.
		int location = source.indexOf(pattern);
		if (location != -1) {
			return (source.substring(0, location) + replacement + source.substring(location + pattern.length()));
		}

		return source;
	}

	/**
	 * Replace all occurences of the pattern text with replacement text. This function
	 * treats all arguments as simple strings.
	 * @param source The source string containing the text to replace.
	 * @param pattern The pattern to find and replace.
	 * @param replacement The text to use to replace the pattern.
	 * @return The version of the source string after the find/replace.
	 */
	public static String replaceAll(String source, String pattern, String replacement) {
		// This function used to use Java's regex code, but this was unsatisfactory
		// for a simple find replace call because it did not treat the replacement
		// text as simple text, instead it looked for special symbols to do grouping,
		// referencing etc. This caused exceptions to be thrown when we wanted
		// to replace '%' with $.
		int location = source.indexOf(pattern);

		while (location != -1) {
			source = (source.substring(0, location) + replacement + source.substring(location + pattern.length()));
			location = source.indexOf(pattern, location + replacement.length());
		}

		return source;
	}

}
