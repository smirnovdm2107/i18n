package info.kgeorgiy.ja.smirnov.i18n;

import info.kgeorgiy.ja.smirnov.i18n.statistics.DateStatistics;
import info.kgeorgiy.ja.smirnov.i18n.statistics.DoubleStatistics;
import info.kgeorgiy.ja.smirnov.i18n.statistics.StringStatistics;

import java.text.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextParser {

    private final StringStatistics sentenceStatistics = new StringStatistics(Collator.getInstance());
    private final StringStatistics wordStatistics = new StringStatistics(Collator.getInstance());
    private final DoubleStatistics numberStatistics = new DoubleStatistics();
    private final DoubleStatistics currencyStatistics = new DoubleStatistics();
    private final DateStatistics dateStatistics = new DateStatistics();

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private final DateFormat dateFormat = DateFormat.getDateInstance();
    private final String text;
    private final ParsePosition position = new ParsePosition(0);

    public TextParser(final String text) {
        this.text = text;
    }

    public ParseResult parse() {
        findStatistics();
        return new ParseResult(
                sentenceStatistics,
                wordStatistics,
                numberStatistics,
                currencyStatistics,
                dateStatistics
        );
    }

    public void findStatistics() {
        parseSentences();
        parseWords();
    }

    private void withTextIterator(
            final String text,
            final Supplier<BreakIterator> iteratorSupplier,
            final Consumer<String> consumer
    ) {
        final BreakIterator iterator = iteratorSupplier.get();
        iterator.setText(text);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            position.setIndex(start);
            final String next = text.substring(start, end).trim();
            if (next.isEmpty()) {
                continue;
            }
            consumer.accept(next);
        }
    }


    private <T> boolean handleParse(final BiFunction<String, ParsePosition, T> function, final Consumer<T> consumer) {
        final int index = position.getIndex();
        final T t = function.apply(text, position);
        if (position.getIndex() == index) {
            return false;
        }
        position.setIndex(index);
        consumer.accept(t);
        return true;
    }

    private void handleNumber() {
        handleParse(numberFormat::parse, (n) -> numberStatistics.accept(n.doubleValue()));
    }

    private void handleCurrency() {
        handleParse(currencyFormat::parse, (c) -> currencyStatistics.accept(c.doubleValue()));
    }

    private void handleDate() {
        handleParse(dateFormat::parse, dateStatistics);
    }

    private void handleWord(final String word) {
        if (Character.isLetter(word.codePointAt(0))) {
            wordStatistics.accept(word);
        }
        handleCurrency();
        handleNumber();
        handleDate();
    }

    private void handleSentence(final String sentence) {
        sentenceStatistics.accept(sentence);
    }

    private void parseSentences() {
        withTextIterator(text, BreakIterator::getSentenceInstance, this::handleSentence);
    }

    private void parseWords() {
        withTextIterator(text, BreakIterator::getWordInstance, this::handleWord);
    }
}
