package info.kgeorgiy.ja.smirnov.i18n;

import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StatisticsTest {
    private static final String TEST_DIRECTORY_PREFIX = "__test";
    private static Path testDirectoryPath;
    private static String prefix;
    private static final Locale RUSSIAN_LOCALE = Locale.of("ru", "RU");
    private static final Locale ENGLISH_LOCALE = Locale.of("en", "US");
    private static final String RESOURCE_BUNDLE_BASE_NAME = "StatisticsResourceBundle";
    private static final ResourceBundle RUSSIAN_BUNDLE = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, RUSSIAN_LOCALE);
    private static final ResourceBundle ENGLISH_BUNDLE = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, ENGLISH_LOCALE);

    @BeforeClass
    public static void setUp() throws IOException {
        testDirectoryPath = Files.createTempDirectory(TEST_DIRECTORY_PREFIX);
        prefix = testDirectoryPath.toAbsolutePath().toString();
    }

    public List<String> startProgram(
            final String testName,
            final Locale inputLocale,
            final Locale outputLocale,
            final String text
    ) {
        final String inputFilePath = prefix + File.separator + testName + ".input";
        try (final BufferedWriter writer = Files.newBufferedWriter(Path.of(inputFilePath))) {
            writer.write(text);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        final String outputFilePath = prefix + File.separator + testName + ".output";
        TextStatistics.getStatistics(inputLocale, outputLocale, inputFilePath, outputFilePath);
        final List<String> statistics;
        try (final BufferedReader reader = Files.newBufferedReader(Path.of(outputFilePath))) {
            statistics = reader.lines().toList();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return statistics;
    }


    private List<String> withEnglishEnglishLocale(final String name, final String text) {
        return startProgram(name, ENGLISH_LOCALE, ENGLISH_LOCALE, text);
    }

    private List<String> withEnglishRussianLocale(final String name, final String text) {
        return startProgram(name, ENGLISH_LOCALE, RUSSIAN_LOCALE, text);
    }

    private List<String> withRussianRussianLocale(final String name, final String text) {
        return startProgram(name, RUSSIAN_LOCALE, RUSSIAN_LOCALE, text);
    }

    private List<String> withRussianEnglishLocale(final String name, final String text) {
        return startProgram(name, RUSSIAN_LOCALE, ENGLISH_LOCALE, text);
    }

    private List<Number> getNumbersFromString(final String text, final Locale locale) {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        final ParsePosition position = new ParsePosition(0);
        final List<Number> numbers = new ArrayList<>();
        while (position.getIndex() < text.length()) {
            final int before = position.getIndex();
            final Number number = format.parse(text, position);
            if (before == position.getIndex()) {
                position.setIndex(before + 1);
                continue;
            }
            numbers.add(number);
        }
        return numbers;
    }

    private String fromBundle(final String key, final Locale locale) {
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale).getString(key);
    }

    private String filterOutput(
            final List<String> output,
            final String substring,
            final Locale locale,
            final int skip
    ) {
        return output.stream().filter(it -> it.contains(fromBundle(substring, locale)))
                .skip(skip)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Can't find substring in output: " + substring));

    }

    private void checkMax(
            final List<String> output,
            final String expected,
            final String substring,
            final Locale locale
    ) {
        Assert.assertTrue(filterOutput(output, "Max-" + substring, locale, 0).contains(expected));
    }

    private void checkMin(
            final List<String> output,
            final String expected,
            final String substring,
            final Locale locale
    ) {
        Assert.assertTrue(filterOutput(output, "Min-" + substring, locale, 0).contains(expected));
    }

    private void checkAverage(
            final List<String> output,
            final String expected,
            final String substring,
            final Locale locale
    ) {
        Assert.assertTrue(filterOutput(output, "Average-" + substring, locale, 0).contains(expected));
    }

    private void checkMaxLength(
            final List<String> output,
            final String expected,
            final String substring,
            final Locale locale
    ) {
        Assert.assertTrue(filterOutput(output, "Max-length-" + substring, locale, 0).contains(expected));
    }

    private void checkMinLength(
            final List<String> output,
            final String expected,
            final String substring,
            final Locale locale
    ) {
        Assert.assertTrue(filterOutput(output, "Min-length-" + substring, locale, 0).contains(expected));
    }

    private void checkSummaryCount(
            final List<String> output,
            final int expected,
            final String substring,
            final Locale locale
    ) {
        final String str = filterOutput(output, "Number-of-" + substring, locale, 0);
        final List<Number> numbers = getNumbersFromString(str, locale);
        Assert.assertEquals(1, numbers.size());
        Assert.assertEquals(numbers.get(0).intValue(), expected);

    }

    private void checkCount(final List<String> output, int expected, int unique, final String substring, final Locale locale) {
        final String str = filterOutput(output, "Number-of-" + substring, locale, 1);
        final List<Number> numbers = getNumbersFromString(str, locale);
        Assert.assertTrue(numbers.size() >= 2);
        Assert.assertEquals(numbers.get(0).intValue(), expected);
        Assert.assertEquals(numbers.get(1).intValue(), unique);
    }

    private void checkSummarySentenceCount(
            final List<String> output,
            final int expected,
            final Locale locale
    ) {
        checkSummaryCount(output, expected, "sentences", locale);
    }

    private void checkCommon(
            final List<String> output,
            final String substring,
            final Locale locale,
            final int count,
            final int unique,
            final String max,
            final String min,
            final String average
    ) {
        checkSummaryCount(output, count, substring, locale);
        checkCount(output, count, unique, substring, locale);
        checkMax(output, max, substring, locale);
        checkMin(output, min, substring, locale);
        checkAverage(output, average, substring, locale);
    }

    private void checkCommonString(
            final List<String> output,
            final String substring,
            final Locale locale,
            final String maxLength,
            final String minLength
    ) {
        checkMaxLength(output, maxLength, substring, locale);
        checkMinLength(output, minLength, substring, locale);
    }

    private void checkEnglishInputSentences(final List<String> outputEN, final Locale locale) {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        final double average =
                ((double) ("This is the first sentence.".length() + "This is the second.".length() + "And this is the third".length() * 2)) / 4;

        format.setMaximumFractionDigits(2);

        checkCommon(outputEN, "sentences", locale, 4, 3, "This is the second.",
                "And this is the third", format.format(average)
        );
        checkCommonString(outputEN, "sentences", locale, format.format("This is the first sentence.".length()),
                format.format("This is the second.".length())
        );
    }

    private void checkRussianInputSentences(final List<String> outputRU, final Locale locale) {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        final double average =
                ((double) ("Это первое предложение.".length() + "Это второе.".length() + "А это третье.".length() * 2)) / 4;
        format.setMaximumFractionDigits(2);
        checkCommon(outputRU, "sentences", locale, 4, 3, "Это первое предложение",
                "А это третье", format.format(average)
        );
        checkCommonString(outputRU, "sentences", locale, "Это первое предложение.",
                "Это второе."
        );
    }


    @Test
    public void test1_sentences() {
        final String textRU = "Это первое предложение. Это второе. А это третье. А это третье.";
        final String testName = "test1_sentences";
        checkRussianInputSentences(withRussianRussianLocale(testName, textRU), RUSSIAN_LOCALE);
        checkRussianInputSentences(withRussianEnglishLocale(testName, textRU), ENGLISH_LOCALE);

        final String textEN = "This is the first sentence. This is the second. And this is the third. And this is the third.";
        checkEnglishInputSentences(withEnglishEnglishLocale(testName, textEN), ENGLISH_LOCALE);
        checkEnglishInputSentences(withEnglishRussianLocale(testName, textEN), RUSSIAN_LOCALE);
    }

    private void checkRussianInputWords(final List<String> outputRU, final Locale locale, final double averageRU) {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        format.setMaximumFractionDigits(2);
        checkCommon(outputRU, "words", locale, 5, 4,
                "этом", "В", format.format(averageRU));
        checkCommonString(outputRU, "words", locale, "предложении", "В");
    }

    private void checkEnglishInputWords(final List<String> outputEN, final Locale locale, final double averageEN) {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        format.setMaximumFractionDigits(2);
        checkCommon(outputEN, "words", locale, 6, 6,
                "words", "in", format.format(averageEN));
        checkCommonString(outputEN, "words", locale, "sentence", "is");
    }


    @Test
    public void test2_words() {
        final String textRU = "В этом предложении 5 слов слов";
        double averageRU = Arrays.stream(textRU.split(" ")).filter(it -> Character.isLetter(it.codePointAt(0))).mapToInt(String::length).average().getAsDouble();
        final String testName = "test2_words";
        checkRussianInputWords(withRussianRussianLocale(testName, textRU), RUSSIAN_LOCALE, averageRU);
        checkRussianInputWords(withRussianEnglishLocale(testName, textRU), ENGLISH_LOCALE, averageRU);

        final String textEN = "There is 6 words in this sentence";
        double averageEN = Arrays.stream(textEN.split(" ")).filter(it -> Character.isLetter(it.codePointAt(0))).mapToInt(String::length).average().getAsDouble();
        checkEnglishInputWords(withEnglishRussianLocale(testName, textEN), RUSSIAN_LOCALE, averageEN);
        checkEnglishInputWords(withEnglishEnglishLocale(testName, textEN), ENGLISH_LOCALE, averageEN);
    }

    private void checkNumbers(final List<String> output, final Locale locale, final double average) {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        format.setMaximumFractionDigits(2);
        checkCommon(output, "numbers", locale, 5, 4, "7", "1", format.format(average));
    }

    @Test
    public void test3_numbers() {
        final String textRU = "Предложение 1 с 7 цифрами 1, 2, 3";
        final String testName = "test3_numbers";
        double average = ((double) 1 + 7 + 1 + 2 + 3) / 5;
        checkNumbers(withRussianRussianLocale(testName, textRU), RUSSIAN_LOCALE, average);
        checkNumbers(withRussianEnglishLocale(testName, textRU), ENGLISH_LOCALE, average);

        final String textEN = "Sentence 1 with 7 numbers 1, 2, 3";
        checkNumbers(withEnglishRussianLocale(testName, textEN), RUSSIAN_LOCALE, average);
        checkNumbers(withEnglishEnglishLocale(testName, textEN), ENGLISH_LOCALE, average);

    }

    private void checkAmounts(final List<String> output, final Locale locale, final double average) {
        final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        checkCommon(output, "amounts", locale, 2, 2, format.format(200), format.format(100),  format.format(average));
    }

    @Test
    public void test4_amounts() {
        final String testName = "test4_amount";
        final int maxAmount = 200;
        final int minAmount = 100;
        final double averageAmount = 150;
        final NumberFormat formatRU = NumberFormat.getCurrencyInstance(RUSSIAN_LOCALE);
        final String textRU =  "Валютное предложение " + formatRU.format(maxAmount) + " " + formatRU.format(minAmount);
        checkAmounts(withRussianRussianLocale(testName, textRU), RUSSIAN_LOCALE,  averageAmount);
        checkAmounts(withRussianEnglishLocale(testName, textRU), ENGLISH_LOCALE, averageAmount);
        final NumberFormat formatEN = NumberFormat.getCurrencyInstance(ENGLISH_LOCALE);
        final String textEN = "Currency sentence " + formatEN.format(maxAmount) + " " + formatEN.format(minAmount);
        checkAmounts(withEnglishRussianLocale(testName, textEN), RUSSIAN_LOCALE, averageAmount);
        checkAmounts(withEnglishEnglishLocale(testName, textEN), ENGLISH_LOCALE, averageAmount);
    }

    private void checkDates(final List<String> output, final Locale locale, final Date average) {
        Locale.setDefault(locale);
        final DateFormat format = DateFormat.getDateInstance(DateFormat.FULL);
        checkCommon(output, "dates", locale, 2, 2,
                 format.format(new Date(new Date().getTime() * 3)), format.format(new Date()),  format.format(average));

    }

    @Test
    public void test5_dates() {
        final String testName = "test5_dates";
        final Date current = new Date();
        final Date next = new Date(current.getTime() * 3);
        final Date average = new Date(current.getTime() * 2);
        Locale.setDefault(RUSSIAN_LOCALE);
        final DateFormat formatRU = DateFormat.getDateInstance();

        final String textRU =  "Датовое предложение " + formatRU.format(current) + " " + formatRU.format(next);
        checkDates(withRussianRussianLocale(testName, textRU), RUSSIAN_LOCALE,  average);
        checkDates(withRussianEnglishLocale(testName, textRU), ENGLISH_LOCALE, average);
        Locale.setDefault(ENGLISH_LOCALE);
        final DateFormat formatEN = DateFormat.getDateInstance();
        final String textEN = "Date sentence " + formatEN.format(next) + " " + formatEN.format(current);
        checkDates(withEnglishRussianLocale(testName, textEN), RUSSIAN_LOCALE, average);
        checkDates(withEnglishEnglishLocale(testName, textEN), ENGLISH_LOCALE, average);
    }


    @AfterClass
    public static void setDown() throws IOException {
        Files.walkFileTree(testDirectoryPath, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
