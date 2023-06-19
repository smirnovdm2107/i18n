package info.kgeorgiy.ja.smirnov.i18n.statistics;

public class DoubleStatistics extends SummarizeStatistics<Double, Double, Double> {

    public DoubleStatistics() {
        super((s, d) -> {
            if (s == null) {
                return d;
            }
            return s + d;
        }, Double::compare);
    }

    @Override
    public Double getAverageImpl() {
        return getSum() / getCount();
    }
}
