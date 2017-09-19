package org.yakimovdenis;

public class Clock implements IClock {
    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
