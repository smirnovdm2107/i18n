package info.kgeorgiy.ja.smirnov.i18n;

import info.kgeorgiy.ja.smirnov.i18n.statistics.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ParseResultHandler {
    private final List<String> lines = new ArrayList<>();
    private final ResourceBundle bundle =
            ResourceBundle.getBundle("StatisticsResourceBundle");
    private static final String COMMON_NUMBER_OF = "   {0}: {1, number, integer}";
    private static final String NUMBER_OF_PATTERN = COMMON_NUMBER_OF + ".";
    private static final String NUMBER_OF_WITH_UNIQUE =
            COMMON_NUMBER_OF + "{1, choice, 0#|0< ({2, number, integer} {3})}.";

    public void save(final ParseResult result, final String inputFileName, final String outputFileName) {
        withPattern("{0} \"{1}\"", bundle.getString("AnalyzedFile"), inputFileName);
        handleSummary(result);
        handleSentences(result.sentenceStatistics());
        handleWords(result.wordStatistics());
        handleNumbers(result.numberStatistics());
        handleCurrency(result.currencyStatistics());
        handleDates(result.dateStatics());
        save(outputFileName);
    }


    private void handleNumberOf(final String name, final Statistics<?> statistics) {
        final long count = statistics.getCount();
        withPattern(NUMBER_OF_PATTERN,
                bundle.getString("Number-of-" + name), count);
    }

    private void handleNumberOfWithUnique(final String name, final Statistics<?> statistics) {
        final long count = statistics.getCount();
        final long unique = statistics.getUniqueCount();
        final String different;
        if (unique == 1) {
            different = bundle.getString("one-different-" + name);
        } else {
            different = bundle.getString("many-different");
        }
        withPattern(NUMBER_OF_WITH_UNIQUE,
                bundle.getString("Number-of-" + name), count, unique, different);
    }

    private void withNoneValue(final String key) {
        withPattern("   {0}: {1}.", bundle.getString(key), bundle.getString("NoneValue"));
    }

    private void withMaybeNoneValue(final String key, final Object value, final Runnable runnable) {
        if (value == null) {
            withNoneValue(key);
            return;
        }
        runnable.run();
    }

    private String replaceLineSeparator(final String string) {
        return string.replace(System.lineSeparator(), bundle.getString("NewLine"));
    }

    private void handleKeyValueLength(final String key, final int length, final String value) {
        withMaybeNoneValue(
                key,
                value,
                () -> withPattern("   {0}: {1, number, integer} (\"{2}\").", bundle.getString(key), length,
                        replaceLineSeparator(value)
                )
        );
    }

    private void handleKeyValue(final String key, final Object value, final String type, final String style) {
        withMaybeNoneValue(
                key,
                value,
                () -> withPattern("   {0}: {1, " + type + ", " + style + "}.", bundle.getString(key), value)
        );
    }

    private void handleKeyValueString(final String key, final String value) {
        withMaybeNoneValue(key, value, () -> handleKeyValue(key, "\"" + replaceLineSeparator(value) + "\"", "", ""));
    }

    private void withStatisticHeader(final String name) {
        withPattern("{0}", bundle.getString("Statistics-by-" + name));
    }


    private void withPattern(final String pattern, Object... args) {
        final String message = MessageFormat.format(pattern, args);
        lines.add(message);
    }

    private void handleSummary(final ParseResult result) {
        withPattern("{0}", bundle.getString("SummaryStatistics"));
        handleNumberOf("sentences", result.sentenceStatistics());
        handleNumberOf("words", result.wordStatistics());
        handleNumberOf("numbers", result.numberStatistics());
        handleNumberOf("amounts", result.currencyStatistics());
        handleNumberOf("dates", result.dateStatics());
    }


    private void handleStatistics(final String header, final Statistics<?> statistics) {
        withStatisticHeader(header);
        handleNumberOfWithUnique(header, statistics);
    }

    private void handleStringStatistics(final String header, final StringStatistics statistics) {
        handleStatistics(header, statistics);
        handleKeyValueString("Min-" + header, statistics.getMin());
        handleKeyValueString("Max-" + header, statistics.getMax());
        handleKeyValueLength("Max-length-" + header, statistics.getMaxLength(), statistics.getMaxLengthString());
        handleKeyValueLength("Min-length-" + header, statistics.getMinLength(), statistics.getMinLengthString());
        handleKeyValue("Average-" + header, statistics.getAverageLength(), "number", "0.##");
    }

    private void handleSummable(final String header, final SummableStatistics<?, ?, ?> statistics, String type, String style) {
        handleStatistics(header, statistics);
        handleKeyValue("Min-" + header, statistics.getMin(), type, style);
        handleKeyValue("Max-" + header, statistics.getMax(), type, style);
        handleKeyValue("Average-" + header, statistics.getAverage(), type, style);
    }

    private void handleSentences(final StringStatistics statistics) {
        handleStringStatistics("sentences", statistics);
    }

    private void handleWords(final StringStatistics statistics) {
        handleStringStatistics("words", statistics);
    }

    private void handleCurrency(final DoubleStatistics statistics) {
        handleSummable("amounts", statistics, "number", "currency");
    }

    private void handleDates(final DateStatistics statistics) {
        handleSummable("dates", statistics, "date", "full");
    }

    private void handleNumbers(final DoubleStatistics statistics) {
        handleSummable("numbers", statistics, "number", "0.##");
    }


    private void save(final String path) {
        try (final BufferedWriter outputStream = Files.newBufferedWriter(Path.of(path))) {
            for (final String line : lines) {
                outputStream.write(line);
                outputStream.newLine();
            }
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
