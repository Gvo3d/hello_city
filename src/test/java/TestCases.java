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
    private static String LOSANGELES = "Los_Angeles";
    private static String TIMEZONE = "America/Los_Angeles";
    private static final String NEWLINE = System.getProperty("line.separator");
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private SimpleDateFormat sdf;
    private static Locale LOCALE = Locale.ENGLISH;
    private Properties props;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(byteArrayOutputStream));
        Locale.setDefault(LOCALE);
        props = new Properties();
        sdf = new SimpleDateFormat("HH");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        InputStream reader = Greeter.class.getClassLoader().getResourceAsStream("properties_en.properties");
        try {
            props.load(reader);
        } catch (IOException e) {
            System.out.println("ERROR with opening props!");
        }
    }

    private String getCompareString(String tz, String cityName) {
        int tzTime = 0;
        TimeZone tzObject = null;
        if (tz != null) {
            tzObject = TimeZone.getTimeZone(tz);
            if (tzObject.equals(TimeZone.getTimeZone("GMT")) && !tz.equals("GMT")) {
                tzObject = null;
            }
        }
        if (null == tzObject) {
            String[] ids = TimeZone.getAvailableIDs();
            String comparsionCity = cityName.replace(" ", "_");
            for (String id : ids) {
                if (id.contains(comparsionCity)) {
                    tzObject = TimeZone.getTimeZone(id);
                }
            }
        }
        if (null == tzObject) {
            tzObject = TimeZone.getTimeZone("GMT");
        }
        tzTime = Math.toIntExact(TimeUnit.MILLISECONDS.toHours(tzObject.getRawOffset()));

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
        } else {
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

    @Test
    public void simpleTest4() {
        String[] result = new String[1];
        result[0] = ODESSA;
        Greeter.main(result);
        String odessianHello = byteArrayOutputStream.toString().split("\\,")[0];
        byteArrayOutputStream = new ByteArrayOutputStream();

        result[0] = LOSANGELES;
        Greeter.main(result);
        String losangeleHello = byteArrayOutputStream.toString().split("\\,")[0];
        Assert.assertNotEquals(odessianHello,losangeleHello);
    }
}
