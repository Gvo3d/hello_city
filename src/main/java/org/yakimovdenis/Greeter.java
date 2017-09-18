package org.yakimovdenis;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Greeter {
    public static void main(String[] args) {
        Properties properties = initializeProps();
        if (null == properties) {
            System.out.println("Internationalization properties file is not found");
            System.exit(1);
        }
        if (args.length == 0) {
            System.out.println(properties.getProperty("error.emptyArgs"));
            System.exit(1);
        }

        String cityName = StringUtils.capitalize(StringUtils.lowerCase(args[0]));
        String timeZone = null;
        int tzTime = 0;
        if (args.length > 1) {
            timeZone = args[1];
        } else {
            String[] ids = TimeZone.getAvailableIDs();
            String comparsionCity = cityName.replace(" ", "_");
            for (String id : ids) {
                if (id.contains(comparsionCity)) {
                    tzTime = Math.toIntExact(TimeUnit.MILLISECONDS.toHours(TimeZone.getTimeZone(id).getRawOffset()));
                }
            }
        }
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            if (timeZone != null) {
                try {
                    TimeZone tzObject = TimeZone.getTimeZone(timeZone);
                    sdf.setTimeZone(tzObject);
                } catch (Exception e) {
                    System.out.println(properties.getProperty("error.wrongTimeZone") + " : " + timeZone);
                }
            } else {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            }
            StringBuilder builder = new StringBuilder();
            int hours = Integer.parseInt(sdf.format(System.currentTimeMillis()));
            hours = hours +tzTime;
            if (hours >= 6 && hours < 9) {
                builder.append(properties.getProperty("result.morning"));
            } else if (hours >= 9 && hours < 19) {
                builder.append(properties.getProperty("result.day"));
            } else if (hours >= 19 && hours < 23) {
                builder.append(properties.getProperty("result.evening"));
            } else if (hours >= 23 || hours < 6) {
                builder.append(properties.getProperty("result.night"));
            }
            builder.append(", ");
            builder.append(cityName);
            builder.append("!");
        System.out.println(builder.toString());
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
