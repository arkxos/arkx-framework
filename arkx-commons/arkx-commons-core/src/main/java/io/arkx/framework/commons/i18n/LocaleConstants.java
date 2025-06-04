package io.arkx.framework.commons.i18n;

public final class LocaleConstants {

	// To be modified if another localization is added. Just add the supported languages here.
	public static final java.util.Locale[] locales = { 
		new java.util.Locale("CA"), // Catalan
		java.util.Locale.ENGLISH, 
		java.util.Locale.FRENCH, 
		java.util.Locale.GERMAN, 
		java.util.Locale.ITALIAN, 
		java.util.Locale.SIMPLIFIED_CHINESE, 
		new java.util.Locale("SV"), // Swedish
		new java.util.Locale("PL") // Polish
	};

	public static final int localeCount = locales.length;
}
