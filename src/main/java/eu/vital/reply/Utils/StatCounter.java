package eu.vital.reply.utils;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by f.deceglia on 23/03/2015.
 */

public class StatCounter {

    private static AtomicInteger requestNumber = new AtomicInteger(0);
    private static AtomicInteger errorNumber = new AtomicInteger(0);

    public static AtomicInteger getRequestNumber() {
        return requestNumber;
    }

    public static AtomicInteger getErrorNumber() {
        return errorNumber;
    }

    public static void setRequestedNumber(int value) {
        requestNumber.set(value);
    }

    public static void incrementErrorNumeber() {

        int currentErrorNumber = errorNumber.get();
        errorNumber.set(currentErrorNumber+1);
    }

}
