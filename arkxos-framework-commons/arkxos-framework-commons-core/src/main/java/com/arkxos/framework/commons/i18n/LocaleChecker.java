package com.arkxos.framework.commons.i18n;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * A tool which checks locale files for consistency. This tool compares a reference
 * locale file against another local file. It reports if any text translations
 * are missing, or are superflous (unused). This tool can be run from the build file by
 * typing: <code>ant locale</code>.
 * <p>
 * Symbols that are reported <b>MISSING</b> should be added, symbols that are reported
 * <b>UNUSED</b> should be removed or renamed.
 *
 * @author Andrew Leppard
 */
public class LocaleChecker {

    /** This class cannot be instantiated. */
    private LocaleChecker() {
        assert false;
    }

    /**
     * Compare a reference locale file against another locale file.
     * <p>
     * Run:
     * <code>java Locale &lt;reference locale&gt; &lt;check locale&gt;</code>
     *
     * @param args first argument is the reference locale file, the second argument
     *             is the locale to check
     */
    public static void main(String[] args) {

        // Extract user arguments from the command line
        if(args.length != 2) {
            System.out.println("Use: locale <reference locale> <check locale>");
            System.exit(1);
        }
        
        // Load locale files
        String referenceLocale = args[0];
        String checkLocale = args[1];

        Properties referenceProperties = loadProperties(referenceLocale);
        Properties checkProperties = loadProperties(checkLocale);

        if(referenceProperties == null || checkProperties == null)
            System.exit(1);

        // Output symbols that are missing or unused
        Set<Object> referenceKeys = referenceProperties.keySet();
        ArrayList<Object> checkKeys = new ArrayList<>(checkProperties.keySet());
        int errors = 0;

        // Output symbols that are missing
        for(Iterator<Object> iterator = referenceKeys.iterator(); iterator.hasNext();) {
            String key = (String)iterator.next();

            // Is the locale we are checking not contain this key?
            if(!checkKeys.contains(key)) {
                System.out.println("MISSING " + key);
                errors++;
            }
            checkKeys.remove(key);
        }

        // Output symbols that are unused
        for(Iterator<Object> iterator = checkKeys.iterator(); iterator.hasNext();) {
            String key = (String)iterator.next();

            System.out.println("UNUSED " + key);
        }

        errors += checkKeys.size();

        if(errors == 0)
            System.out.println("Locale OK");
        
        System.exit(0);
    }

    /**
     * Load the given properties file.
     *
     * @param fileName the name of the properties file
     * @return the properties file or <code>null</code> if there was an error
     */
    private static Properties loadProperties(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            Properties properties = new Properties();
            properties.load(fileInputStream);
            return properties;
        }
        catch(FileNotFoundException e) {
            System.out.println("Error loading " + fileName + ".");
        }
        catch(IOException e) {
            System.out.println("Error reading " + fileName + ".");
        }

        return null;
    }

}