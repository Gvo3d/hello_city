package org.yakimovdenis;


import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Locale;
import java.util.Properties;

public class Greeter {
    public static void main(String[] args) {
        Properties properties = initializeProps();
        if (null == properties) {
            System.out.println("Internationalization properties file is not found");
            System.exit(1);
        }
        if (args.length == 0) {
            System.out.println(properties.getProperty("error.emptyArgs"));
        }
    }

    private static Properties initializeProps() {
        Locale currentLocale = Locale.getDefault();
        Properties prop = new Properties();
        String lang = "properties_" + currentLocale.getLanguage() + ".properties";
        InputStream reader;
        reader = Greeter.class.getClassLoader().getResourceAsStream(lang);
        try {
            prop.load(reader);
        } catch (IOException ex) {
            lang = "properties_en.properties";
            reader = Greeter.class.getClassLoader().getResourceAsStream(lang);
            System.out.println("Internationalization properties file is not found: " + lang);
            System.exit(1);
            try {
                prop.load(reader);
            } catch (IOException ex1) {
                System.out.println("Something went wrong with properties: " + lang);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioe) {
                        System.out.println("Something went wrong when closing " + lang);
                    }
                }
            }
        }
        return prop;
    }
}
