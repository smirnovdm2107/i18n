package info.kgeorgiy.ja.smirnov.i18n.statistics;

import java.util.Calendar;
import java.util.Date;

public class DateStatistics extends SummarizeStatistics<Date, Date, Date> {

    public DateStatistics() {
        super((t1, t2) -> {
            if (t1 == null) {
                return t2;
            }
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(t1.getTime() + t2.getTime());
            return new Date(calendar.getTimeInMillis());
        }, Date::compareTo);
    }

    @Override
    public Date getAverageImpl() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getSum().getTime() / getCount());
        return calendar.getTime();
    }
}
