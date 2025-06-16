// src/controller/SearchController.java
package controller;

import dao.CaseDAO;
import model.CaseStatus;
import model.CrimeCase;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

public class SearchController {

    private final CaseDAO dao;

    public SearchController(CaseDAO dao) { this.dao = dao; }

    public List<CrimeCase> filter(CaseStatus status, LocalDate from, LocalDate to) {
        Predicate<CrimeCase> p = c -> true;
        if (status != null) p = p.and(c -> c.getStatus() == status);
        if (from != null)   p = p.and(c -> !c.getDate().isBefore(from));
        if (to != null)     p = p.and(c -> !c.getDate().isAfter(to));
        return dao.findAll().stream().filter(p).toList();
    }
}
