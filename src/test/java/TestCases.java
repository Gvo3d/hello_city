import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yakimovdenis.Clock;
import org.yakimovdenis.Greeter;
import org.yakimovdenis.IClock;
import org.yakimovdenis.Main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
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
        InputStream reader = Greeter.class.getClassLoader().getResourceAsStream("properties_en.properties");
        try {
            props.load(reader);
        } catch (IOException e) {
            System.out.println("ERROR with opening props!");
        }
    }

    private void executeTestAtHour(int hour) {
        String[] args = new String[1];
        args[0] = ODESSA;
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, Calendar.SEPTEMBER, 19, hour, 01, 01);
        TestClock clock = new TestClock(calendar.getTimeInMillis());
        Greeter greeter = new Greeter(clock);
        greeter.setWriteToLog(true);
        greeter.calculateAndWrite(args);

        Assert.assertEquals(getCompareString(null, ODESSA, clock) + ", Odessa!" + NEWLINE, byteArrayOutputStream.toString());
        System.err.println("This is not a error. We remind you - that you can check manually logging.log for Greeter function results, because for that test property setWriteToLog was true.\nTime that was tested: "+hour+" hour.");
    }

    private String getCompareString(String tz, String cityName, IClock clock) {
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

        if (null == clock) {
            clock = new Clock();
        }

        int resultHours = Integer.parseInt(sdf.format(new Date(clock.getCurrentTime())));

        if (tzTime != 0) {
            resultHours = resultHours + tzTime - Math.toIntExact(TimeUnit.MILLISECONDS.toHours(TimeZone.getDefault().getRawOffset()));
        }

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
        String[] args = new String[1];
        args[0] = ODESSA;
        Main.main(args);
        Assert.assertEquals(getCompareString(null, ODESSA, null) + ", Odessa!" + NEWLINE, byteArrayOutputStream.toString());
    }

    @Test
    public void simpleTestWithWrongCityname() {
        String[] args = new String[1];
        args[0] = ODESSA2;
        Main.main(args);
        Assert.assertEquals(getCompareString(null, ODESSA, null) + ", Odessa!" + NEWLINE, byteArrayOutputStream.toString());
    }

    @Test
    public void simpleTestWithOtherTimezone() {
        String[] args = new String[2];
        args[0] = ODESSA2;
        args[1] = TIMEZONE;
        IClock clock = new Clock();
        Greeter greeter = new Greeter(clock);
        greeter.setWriteToLog(true);
        greeter.calculateAndWrite(args);
        Assert.assertEquals(getCompareString(TIMEZONE, ODESSA, null) + ", Odessa!" + NEWLINE, byteArrayOutputStream.toString());
    }

    @Test
    public void simpleTestWithTwoZonesNotEquals() {
        String[] args = new String[1];
        args[0] = ODESSA;
        Main.main(args);
        String odessianHello = byteArrayOutputStream.toString().split("\\,")[0];
        byteArrayOutputStream = new ByteArrayOutputStream();

        args[0] = LOSANGELES;
        Main.main(args);
        String losangeleHello = byteArrayOutputStream.toString().split("\\,")[0];
        Assert.assertNotEquals(odessianHello, losangeleHello);
    }

    @Test
    public void simpleTestWithTimeChange() {
        executeTestAtHour(1);
    }

    @Test
    public void simpleTestWithTimeChange2() {
        executeTestAtHour(7);
    }

    @Test
    public void simpleTestWithTimeChange3() {
        executeTestAtHour(9);
    }

    @Test
    public void simpleTestWithTimeChange4() {
        executeTestAtHour(19);
    }

    @Test
    public void simpleTestWithTimeChange5() {
        executeTestAtHour(23);
    }


    private class TestClock implements IClock {
        private long time;

        public TestClock(long time) {
            this.time = time;
        }

        @Override
        public long getCurrentTime() {
            return time;
        }
    }
}
