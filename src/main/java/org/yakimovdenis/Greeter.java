package org.yakimovdenis;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Greeter {
    private final static Logger slf4jLogger = LoggerFactory.getLogger(Greeter.class);

    public static void main(String[] args) {
        Properties properties = initializeProps();
        if (null == properties) {
            String error = "Internationalization properties file is not found";
            System.err.println(error);
            slf4jLogger.error(error);
            System.exit(1);
        }
        if (args.length == 0) {
            slf4jLogger.error(properties.getProperty("error.emptyArgs"));
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
                    slf4jLogger.error(properties.getProperty("error.wrongTimeZone") + " : " + timeZone);
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
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
            slf4jLogger.error("Internationalization properties file is not found: " + lang);
            System.exit(1);
            try {
                prop.load(reader);
            } catch (IOException ex1) {
                slf4jLogger.error("Something went wrong with properties: " + lang);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioe) {
                        slf4jLogger.error("Something went wrong when closing " + lang);
                    }
                }
            }
        }
        return prop;
    }
}
