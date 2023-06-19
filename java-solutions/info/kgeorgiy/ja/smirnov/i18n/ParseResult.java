package info.kgeorgiy.ja.smirnov.i18n;

import info.kgeorgiy.ja.smirnov.i18n.statistics.DateStatistics;
import info.kgeorgiy.ja.smirnov.i18n.statistics.DoubleStatistics;
import info.kgeorgiy.ja.smirnov.i18n.statistics.StringStatistics;

public record ParseResult(
        StringStatistics sentenceStatistics,
        StringStatistics wordStatistics,
        DoubleStatistics numberStatistics,
        DoubleStatistics currencyStatistics,
        DateStatistics dateStatics) {
}