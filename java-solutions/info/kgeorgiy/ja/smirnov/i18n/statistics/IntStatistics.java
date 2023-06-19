package info.kgeorgiy.ja.smirnov.i18n.statistics;

public class IntStatistics extends SummarizeStatistics<Integer, Long, Double>{
    public IntStatistics() {
        super((s, t1) -> {
            if (s == null) {
                return t1.longValue();
            }
            return s + t1;
        }, Integer::compare);
    }

    @Override
    protected Double getAverageImpl() {
        return getSum().doubleValue() / getCount();
    }
}
