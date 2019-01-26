import java.util.Calendar;

class TimeConvert {
    private static TimeConvert instance;

    private TimeConvert() {
        instance = this;
    }

    static TimeConvert getInstance() {
        if (instance == null) {
            instance = new TimeConvert();
        }
        return instance;
    }

    static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    static int getMin() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    static int getSec() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }
}
