package info.kgeorgiy.ja.smirnov.i18n.statistics;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

abstract class AbstractStatistics<T> implements Consumer<T>, Statistics<T> {

    protected Set<T> uniques = new HashSet<>();
    protected long count = 0;
    protected T max;
    protected T min;
    protected Comparator<? super T> comparator;

    public AbstractStatistics(final Comparator<? super T> comparator) {
        this.comparator = comparator;
    }


    public void accept(final T t) {
        ++count;
        uniques.add(t);
        if (max == null) {
            max = t;
        } else {
            max = comparator.compare(max, t) > 0 ? max : t;
        }
        if (min == null) {
            min = t;
        } else {
            min = comparator.compare(min, t) < 0 ? min : t;
        }
    }

    public long getCount() {
        return count;
    }

    public T getMax() {
        return max;
    }

    public T getMin() {
        return min;
    }

    public long getUniqueCount() {
        return uniques.size();
    }

}
