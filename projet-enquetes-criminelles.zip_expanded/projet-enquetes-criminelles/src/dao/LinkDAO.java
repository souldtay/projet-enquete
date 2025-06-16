package dao;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import model.Link;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

public class LinkDAO {
    private static final Path path = Path.of("data/links.csv");

    public static void save(Link link) {
        try (Writer w = Files.newBufferedWriter(
                path,
                Charset.defaultCharset(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND))
        {
            StatefulBeanToCsv<Link> btc = new StatefulBeanToCsvBuilder<Link>(w)
                    .withApplyQuotesToAll(false)
                    .build();
            btc.write(Collections.singletonList(link));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ✅ NEW METHOD: read all links from CSV
    public static List<Link> readAll() {
        try (Reader reader = Files.newBufferedReader(path, Charset.defaultCharset())) {
            return new CsvToBeanBuilder<Link>(reader)
                    .withType(Link.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
        } catch (Exception ex) {
            ex.printStackTrace();
            return List.of();  // fallback: empty list
        }
    }
}
