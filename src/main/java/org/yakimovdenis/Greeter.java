package org.yakimovdenis;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Greeter {
    private final static Logger LOGGER = LoggerFactory.getLogger(Greeter.class);
    private static final String GMT = "GMT";
    private String basename = "properties";
    private ResourceBundle resources;
    private IClock clock;
    private String insertedTimeZone;
    private boolean writeToLog = false;

    public Greeter(IClock clock) {
        this.clock = clock;
    }

    public void setWriteToLog(boolean writeToLog) {
        this.writeToLog = writeToLog;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public void calculateAndWrite(String[] args) {
        resources = getResourceBundleInstance(basename);
        if (args.length == 0) {
            LOGGER.error(resources.getString("error.emptyArgs"));
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
        StringBuilder builder = new StringBuilder();
        int hours = Integer.parseInt(sdf.format(clock.getCurrentTime()));

        if (timeZoneOffset != 0) {
            hours = hours + timeZoneOffset - Math.toIntExact(TimeUnit.MILLISECONDS.toHours(TimeZone.getDefault().getRawOffset()));
        }

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
        if (writeToLog) {
            StringBuilder logBuilder = new StringBuilder("LOG: City: ").append(cityName).append(". Time from clock: ").append(hours - timeZoneOffset).append(". Local timezone: ").append(TimeZone.getDefault().getID()).append(". Inserted timezone: ").append(insertedTimeZone).append(". Inserted timezone offset: ").append(timeZoneOffset).append('\n').append(builder.toString());
            LOGGER.info(logBuilder.toString());
        }
    }

    private int getTimeZoneDelta(String timeZoneString, String cityNameString) {
        TimeZone timezone = null;
        insertedTimeZone = "NO";
        if (null != timeZoneString) {
            timezone = TimeZone.getTimeZone(timeZoneString);
            if (timezone.equals(TimeZone.getTimeZone(GMT)) && !timeZoneString.equals(GMT)) {
                timezone = null;
                LOGGER.warn(resources.getString("error.wrongTimeZone"));
            } else {
                insertedTimeZone = timeZoneString;
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

    public static ResourceBundle getResourceBundleInstance(String baseName) {
        Locale currLocale = Locale.getDefault();
        ResourceBundle resources = null;
        try {
            resources = ResourceBundle.getBundle(baseName, currLocale);
        } catch (MissingResourceException e) {
            try {
                resources = ResourceBundle.getBundle(baseName, Locale.ENGLISH);
            } catch (MissingResourceException e1) {
                LOGGER.error("Internationalization properties file is not found: " + baseName);
                System.exit(1);
            }
        }
        return resources;
    }
}
