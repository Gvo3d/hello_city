import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yakimovdenis.Greeter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TestCases {
    private static String ODESSA = "Odessa";
    private static String ODESSA2 = "odEsSa";
    private static String TIMEZONE = "America/Los_Angeles";
    private static final String NEWLINE = System.getProperty("line.separator").toString();
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH");
    private static Locale LOCALE = Locale.ENGLISH;
    private Properties props;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(byteArrayOutputStream));
        Locale.setDefault(LOCALE);
        props = new Properties();
        InputStream reader = Greeter.class.getClassLoader().getResourceAsStream("properties_en.properties");
        try {
            props.load(reader);
        } catch (IOException e) {
            System.out.println("ERROR with opening props!");
        }
    }

    private String getCompareString(String tz, String cityName) {
        int tzTime = 0;
        if (tz != null) {
            TimeZone tzObject = TimeZone.getTimeZone(tz);
            sdf.setTimeZone(tzObject);
        } else {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String[] ids = TimeZone.getAvailableIDs();
            String comparsionCity = cityName.replace(" ", "_");
            for (String id : ids) {
                if (id.contains(comparsionCity)) {
                    tzTime = Math.toIntExact(TimeUnit.MILLISECONDS.toHours(TimeZone.getTimeZone(id).getRawOffset()));
                }
            }
        }
        Date now = new Date(System.currentTimeMillis());
        int resultHours = Integer.parseInt(sdf.format(now));
        resultHours = resultHours + tzTime;
        String comparsionString = null;
        if (resultHours >= 6 && resultHours < 9) {
            comparsionString = props.getProperty("result.morning");
        } else if (resultHours >= 9 && resultHours < 19) {
            comparsionString = props.getProperty("result.day");
        } else if (resultHours >= 19 && resultHours < 23) {
            comparsionString = props.getProperty("result.evening");
        } else if (resultHours >= 23 || resultHours < 6) {
            comparsionString = props.getProperty("result.night");
        }
        return comparsionString;
    }

    @Test
    public void simpleTest() {
        String[] result = new String[1];
        result[0] = ODESSA;
        Greeter.main(result);
        Assert.assertEquals(getCompareString(null, ODESSA)+", Odessa!"+NEWLINE, byteArrayOutputStream.toString());
    }

    @Test
    public void simpleTest2() {
        String[] result = new String[1];
        result[0] = ODESSA2;
        Greeter.main(result);
        Assert.assertEquals(getCompareString(null, ODESSA)+", Odessa!"+NEWLINE, byteArrayOutputStream.toString());
    }

    @Test
    public void simpleTest3() {
        String[] result = new String[2];
        result[0] = ODESSA2;
        result[1] = TIMEZONE;
        Greeter.main(result);
        Assert.assertEquals(getCompareString(TIMEZONE, ODESSA)+", Odessa!"+NEWLINE, byteArrayOutputStream.toString());
    }
}
