package com.rapidark.framework.commons.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * An immutaable representation of Currency. This class represents a currency,
 * such as the Australian Dollar or the British Pound, rather than an acutal amount
 * of money. We use our own representation because the Java API's Currency
 * class has no way of returning a NumberFormat object. So we could not
 * simply pass a Currency object to our Money object and have the Money object
 * be able to print properly. We would have to pass both the locale and the
 * currency around, which is heavy handed.
 *
 * @author Andrew Leppard
 * @see Money
 */
public class Currency implements Cloneable, Comparable<Currency> {

    // Default currency, i.e. the currency for default locale
    private static Currency defaultCurrency = new Currency(java.util.Locale.getDefault());

    // A list of all the known currencies
    private static List<Currency> currencyList = null;

    /**
     * Return a list of all the available currencies.
     *
     * @return list of available currencies.
     */
    public static synchronized List<Currency> getAvailableCurrencies() {
        if(currencyList == null) {
            HashMap<String,Currency> availableCurrencies = new HashMap<String,Currency>();
            
            // Create a list of unique currencies.
            java.util.Locale[] locales = java.util.Locale.getAvailableLocales();
            
            for(int i = 0; i < locales.length; i++) {
                try {
                    Currency currency = new Currency(locales[i]);
                    availableCurrencies.put(currency.toString(), currency);
                }
                catch (IllegalArgumentException e) {
                    // Skip locales without a currency. E.g. "en_" (English) has no currency,
                    // while "en_AU" (English/Australia) has the Australian Dollar.
                }
            }
            
            // Extract and sort the list of unique currencies from the hash map.
            currencyList = new ArrayList<Currency>(availableCurrencies.values());
            Collections.sort(currencyList);
        }

        return currencyList;
    }

    /**
     * Return the currency associated with the default locale. If there is no currency
     * associated with the default locale, it will use the US Dollar.
     *
     * @return currency associated with the default locale.
     */
    public static Currency getDefaultCurrency() {
        return defaultCurrency;
    }
    
    // A locale that uses this currency
    private java.util.Locale locale;

    // The Java currency object
    private java.util.Currency currency;

    /**
     * Create an instance of the currency associated with the given locale.
     * If the given locale has no associated currency, e.g. Antartica,
     * this function will default to the US dollar.
     *
     * @param locale use the currency associated with this locale
     */
    public Currency(java.util.Locale locale) {
        this.locale = locale;
        this.currency = java.util.Currency.getInstance(locale);

        // If the locale has no currency, then default to the US dollar.
        if(this.currency == null) {
            this.currency = java.util.Currency.getInstance(java.util.Locale.US);
        }
    }

    /**
     * Create an an instance of the currency using the currency described by
     * the given ISO 4217 currency code.
     *
     * @param currencyCode the ISO 4217 currency code (3 characters).
     * @exception UnknownCurrencyCodeException if the currency code is not known.
     */
    public Currency(String currencyCode) throws UnknownCurrencyCodeException {
        // I see no way in Java to map a currency back to a locale. This
        // makes sense as there is a more to one mapping between locales and
        // currencies, however, you need the locale to display the currency!
        List<Currency> currencyList = getAvailableCurrencies();

        for(Currency currency : currencyList) {
            if(currency.getCurrencyCode().equals(currencyCode)) {
                this.locale = currency.locale;
                this.currency = currency.currency;
                return;
            }
        }

        // If we got here, the currency code is unrecognised.
        throw new UnknownCurrencyCodeException(currencyCode);
    }

    /**
     * Return the ISO 4217 currency code of this currency.
     *
     * @return ISO 4217 currency code.
     */
    public String getCurrencyCode() {
        return currency.getCurrencyCode();
    }

    /**
     * Return a string representation of this currency.
     *
     * @return string representation of this currency.
     */
    @Override
    public String toString() {
        return getCurrencyCode();
    }

    /**
     * Return the number of fraction digits used to display this currency. For examle,
     * the Australian Dollar has 2 fraction digits (e.g. $12.95) while the Japanese
     * Yen has zero (e.g. 100 yen).
     *
     * @return number of fraction digits.
     */
    public int getDefaultFractionDigits() {
        return currency.getDefaultFractionDigits();
    }

    /**
     * Return the number format which can be used to form a number into the appropriate
     * locale representation of this currency.
     *
     * @return the number format.
     */
    public NumberFormat getNumberFormat() {
        return NumberFormat.getCurrencyInstance(locale);
    }

    /**
     * Compare this currency to the given object. If the given object is not a
     * currency, it will throw a <code>ClassCastException</code>. Currencies
     * are ordered alphabetically by their ISO 4217 currency codes.
     *
     * @param object currency object to be compared
     * @return 0 if the two objects are identical, -1 if the argument currency comes
     *         alphabetically after this currency, 1 if the argument currency comes
     *         alphabetically before this currency.
     * @exception ClassCastException if the argument is not a <code>Currency</code>.
     */
    @Override
    public int compareTo(Currency currency) {
        // Sort currencies by their currency code
        return getCurrencyCode().compareTo(currency.getCurrencyCode());
    }

    /**
     * Compare this currency to the given object. If the given object is not a
     * currency, it will throw a <code>ClassCastException</code>.
     *
     * @param object currency object to be compared
     * @return <code>true</code> if the currencies are the same; <code>false</code> otherwise.
     * @exception ClassCastException if the argument is not a <code>Currency</code>.
     */
    @Override
    public boolean equals(Object object) {
        Currency otherCurrency = (Currency)object;
        
        return currency.equals(otherCurrency.currency);
    }

    /**
     * Create and return a compare of this currency. Since currencies are immutable, this
     * simply returns this object.
     *
     * @return this object.
     */
    @Override
    public Currency clone() {
        // Since the currency object is immutable, we can just return this object.
        return this;
    }
    
}