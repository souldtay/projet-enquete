package dao;

import model.CrimeCase;
import java.util.List;

public class CaseDAO extends AbstractDAO<CrimeCase> {

    @Override
    protected String file() {
        return "data/cases.csv";
    }

    @Override
    protected Class<CrimeCase> type() {
        return CrimeCase.class;
    }

    @Override
    public void save(CrimeCase c) {
        System.out.println("Saving case: " + c);
        // 1) Load everything
        List<CrimeCase> all = findAll();
        // 2) Add the new one
        all.add(c);
        // 3) Rewrite the file from scratch (header + all rows)
        CSVUtil.write(file(), all, type(), /*append=*/false);
    }

}
