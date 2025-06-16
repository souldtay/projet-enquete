package dao;

import com.opencsv.bean.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * Utility for reading and writing CSV files via OpenCSV.
 */
public final class CSVUtil {
    private CSVUtil() {}

    /**
     * Read all beans of type T from the given CSV file.
     */
    public static <T> List<T> read(String file, Class<T> type) {
        Path path = Path.of(file);
        try (var reader = Files.newBufferedReader(path, Charset.defaultCharset())) {
            // parse CSV (header → bean mapping)
            return new CsvToBeanBuilder<T>(reader)
                .withType(type)
                .withIgnoreLeadingWhiteSpace(true)
                .withThrowExceptions(false)
                .build()
                .parse();
        } catch (IOException e) {
            System.err.println("❌ Failed reading CSV " + file);
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Write the given beans to the CSV file.
     * If append=true, we only add data lines; otherwise we overwrite and write header+data.
     */
    public static <T> void write(String file,
                                 Collection<T> beans,
                                 Class<T> type,
                                 boolean append) {
        Path path = Path.of(file);
        try {
            boolean exists = Files.exists(path);
            boolean headerPresent = exists &&
                Files.lines(path, Charset.defaultCharset())
                     .anyMatch(l -> l.toLowerCase().startsWith("id,"));

            boolean writeHeader = !append || !headerPresent;
            List<T> data = beans instanceof List<?> list ? (List<T>)list : new ArrayList<>(beans);

            try (Writer w = Files.newBufferedWriter(
                    path,
                    Charset.defaultCharset(),
                    StandardOpenOption.CREATE,
                    append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING)) {

                HeaderColumnNameMappingStrategy<T> strat = new HeaderColumnNameMappingStrategy<>();
                strat.setType(type);

                StatefulBeanToCsvBuilder<T> builder = new StatefulBeanToCsvBuilder<T>(w)
                    .withApplyQuotesToAll(false)
                    .withMappingStrategy(strat);

                if (!writeHeader) {
                    builder.withMappingStrategy(new NoHeaderStrategy<>(strat));
                }

                builder.build().write(data);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed writing CSV " + file);
            e.printStackTrace();
        }
    }

    /** little wrapper that never emits a header row */
    private static class NoHeaderStrategy<T> extends HeaderColumnNameMappingStrategy<T> {
        public NoHeaderStrategy(HeaderColumnNameMappingStrategy<T> delegate) {
            setType(delegate.getType());
        }
        @Override public String[] generateHeader(T bean) {
            return new String[0];
        }
    }
}
