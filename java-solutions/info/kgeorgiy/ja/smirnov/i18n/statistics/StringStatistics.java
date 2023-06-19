package info.kgeorgiy.ja.smirnov.i18n.statistics;

import java.util.Comparator;
import java.util.IntSummaryStatistics;

public class StringStatistics extends AbstractStatistics<String> {
    private final IntSummaryStatistics lengthStatistics = new IntSummaryStatistics();
    private String maxLengthString;
    private String minLengthString;

    public StringStatistics(final Comparator<? super String> comparator) {
        super(comparator);
    }


    @Override
    public void accept(final String string) {
        final int length = string.length();
        maxLengthString = length > lengthStatistics.getMax() ? string : maxLengthString;
        minLengthString = length < lengthStatistics.getMin() ? string : minLengthString;
        lengthStatistics.accept(length);
        super.accept(string);
    }

    public String getMaxLengthString() {
        return maxLengthString;
    }

    public String getMinLengthString() {
        return minLengthString;
    }

    public int getMaxLength() {
        return lengthStatistics.getMax();
    }

    public int getMinLength() {
        return lengthStatistics.getMin();
    }

    public Double getAverageLength() {
        if (lengthStatistics.getCount() == 0) {
            return null;
        }
        return lengthStatistics.getAverage();
    }
}
