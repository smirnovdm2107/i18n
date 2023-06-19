package info.kgeorgiy.ja.smirnov.i18n.statistics;

import java.util.Comparator;
import java.util.function.BiFunction;

abstract class SummarizeStatistics<T extends Comparable<? super T>, U, R> extends AbstractStatistics<T> implements SummableStatistics<T, U, R> {
    protected U sum;
    protected final BiFunction<U, T, U> summator;

    public SummarizeStatistics(final BiFunction<U, T, U> summator, final Comparator<? super T> comparator) {
        super(comparator);
        this.summator = summator;
    }

    @Override
    public void accept(final T t) {
        sum = summator.apply(sum, t);
        super.accept(t);
    }

    public U getSum() {
        return sum;
    }

    public R getAverage() {
        if (sum == null) {
            return null;
        }
        return getAverageImpl();
    }

    protected abstract R getAverageImpl();
}
