package info.kgeorgiy.ja.smirnov.i18n.statistics;

public interface Statistics<T> {

    long getCount();

    long getUniqueCount();

    T getMax();

    T getMin();
}
