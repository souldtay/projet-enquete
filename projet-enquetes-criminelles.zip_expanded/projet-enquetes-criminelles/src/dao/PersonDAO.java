package dao;

import model.Person;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PersonDAO extends AbstractDAO<Person> {

    @Override protected String file() {                 // data/persons.csv
        return "data/persons.csv";
    }

    @Override protected Class<Person> type() {
        return Person.class;
    }

    /** FULL-FILE rewrite → never loses the header, never fails silently */
    @Override
    public void save(Person p) {
        System.out.println("[DEBUG] PersonDAO.save → " + p.getId());
        try {
            // 1) load all existing rows (if the file exists)
            List<Person> all = Files.exists(Path.of(file()))
                                   ? findAll()
                                   : new java.util.ArrayList<>();

            // 2) append the new person
            all.add(p);

            // 3) rewrite the file completely (header + data)
            overwriteAll(all);

            System.out.println("[DEBUG]   …wrote " + all.size() + " rows to " + file());
        } catch (Exception ex) {
            System.err.println("[ERROR] Writing persons.csv failed");
            ex.printStackTrace();
        }
    }
}
