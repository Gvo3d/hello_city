package org.yakimovdenis;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Greeter {
    private final static Logger slf4jLogger = LoggerFactory.getLogger(Greeter.class);
    private static final String GMT = "GMT";

    public static void main(String[] args) {
        ResourceBundle resources = getResourceBundleInstance();
        if (args.length == 0) {
            slf4jLogger.error(resources.getString("error.emptyArgs"));
            System.exit(1);
        }

        String cityName = StringUtils.capitalize(StringUtils.lowerCase(args[0]));
        int timeZoneOffset;
        if (args.length > 1) {
            timeZoneOffset = getTimeZoneDelta(args[1], cityName);
        } else {
            timeZoneOffset = getTimeZoneDelta(null, cityName);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        sdf.setTimeZone(TimeZone.getTimeZone(GMT));
        StringBuilder builder = new StringBuilder();
        int hours = Integer.parseInt(sdf.format(System.currentTimeMillis()));
        hours = hours + timeZoneOffset;

        if (hours >= 6 && hours < 9) {
            builder.append(resources.getString("result.morning"));
        } else if (hours >= 9 && hours < 19) {
            builder.append(resources.getString("result.day"));
        } else if (hours >= 19 && hours < 23) {
            builder.append(resources.getString("result.evening"));
        } else {
            builder.append(resources.getString("result.night"));
        }
        builder.append(", ");
        builder.append(cityName);
        builder.append("!");
        System.out.println(builder.toString());
    }

    private static int getTimeZoneDelta(String timeZoneString, String cityNameString) {
        TimeZone timezone = null;
        if (null != timeZoneString) {
            timezone = TimeZone.getTimeZone(timeZoneString);
            if (timezone.equals(TimeZone.getTimeZone(GMT)) && !timeZoneString.equals(GMT)) {
                timezone = null;
            }
        }
        if (null == timezone) {
            String[] ids = TimeZone.getAvailableIDs();
            String comparableCityName = cityNameString.replace(" ", "_");
            for (String id : ids) {
                if (id.contains(comparableCityName)) {
                    timezone = TimeZone.getTimeZone(id);
                }
            }
        }
        if (null == timezone) {
            timezone = TimeZone.getTimeZone(GMT);
        }
        return Math.toIntExact(TimeUnit.MILLISECONDS.toHours(timezone.getRawOffset()));
    }

    private static ResourceBundle getResourceBundleInstance() {
        Locale currLocale = Locale.getDefault();
        ResourceBundle resources = null;
        String baseName = "properties";
        try {
            resources = ResourceBundle.getBundle(baseName, currLocale);
        } catch (MissingResourceException e) {
            try {
                resources = ResourceBundle.getBundle(baseName, Locale.ENGLISH);
            } catch (MissingResourceException e1) {
                slf4jLogger.error("Internationalization properties file is not found: " + baseName);
                System.exit(1);
            }
        }
        return resources;
    }
}
