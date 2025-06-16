package dao;

import model.Evidence;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EvidenceDAO extends AbstractDAO<Evidence> {

    @Override protected String file() { return "data/evidence.csv"; }
    @Override protected Class<Evidence> type() { return Evidence.class; }

    /** rewrite file so header is guaranteed, append never fails */
    @Override
    public void save(Evidence ev) {
        try {
            List<Evidence> all = Files.exists(Path.of(file()))
                                  ? findAll()
                                  : new java.util.ArrayList<>();
            all.add(ev);                    // add the new evidence
            overwriteAll(all);              // header + data
            System.out.println("[DEBUG] evidence.csv now has " + all.size() + " rows");
        } catch (Exception ex) {
            System.err.println("[ERROR] writing evidence.csv failed");
            ex.printStackTrace();
        }
    }
}
