package info.kgeorgiy.ja.smirnov.i18n.statistics;

public interface SummableStatistics<T, U, R> extends Statistics<T> {
    U getSum();

    R getAverage();
}
