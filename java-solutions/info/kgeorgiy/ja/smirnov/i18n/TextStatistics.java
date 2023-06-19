package info.kgeorgiy.ja.smirnov.i18n;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IllformedLocaleException;
import java.util.Locale;

public class TextStatistics {

    public static void main(String[] args) throws Exception {
        if (args == null) {
            System.out.println("Args is null");
            return;
        } else if (args.length != 4) {
            System.out.println("Wrong number of arguments (" + args.length + "), " +
                    " expected: [input locale, output locale, input file name, output file name]");
            return;
        }
        try {
            final Locale inputLocale = parseLocale(args[0]);
            final Locale outputLocale = parseLocale(args[1]);
            final String inputFileName = args[2];
            final String outputFileName = args[3];
            try {
                getStatistics(inputLocale, outputLocale, inputFileName, outputFileName);
            } catch (final RuntimeException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        } catch (final IllformedLocaleException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets text statistic of input file.
     * @param inputLocale locale of input file.
     * @param outputLocale output file locale.
     * @param inputFilePath path to input file.
     * @param outputFilePath path to output file.
     */
    public static void getStatistics(
            final Locale inputLocale,
            final Locale outputLocale,
            final String inputFilePath,
            final String outputFilePath
    ) {
        Locale.setDefault(inputLocale);
        final String input;
        try {
            input = Files.readString(Path.of(inputFilePath));
        } catch (final IOException e) {
            System.out.println("Can't read data from input file, " + inputFilePath);
            throw new UncheckedIOException(e);
        }
        final ParseResult result = new TextParser(input).parse();
        Locale.setDefault(outputLocale);
        new ParseResultHandler().save(result, inputFilePath, outputFilePath);
    }
    private static Locale parseLocale(final String locale) {
        final String[] fullLocale = locale.split("_");
        final Locale.Builder builder = new Locale.Builder();
        if (fullLocale.length > 0) {
            builder.setLanguage(fullLocale[0]);
            if (fullLocale.length > 1) {
                builder.setRegion(fullLocale[1]);
                if (fullLocale.length > 2) {
                    builder.setVariant(fullLocale[2]);
                }
            }
        }
        return builder.build();
    }

}
