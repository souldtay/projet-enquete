package controller;

import dao.CaseDAO;
import dao.PersonDAO;
import dao.EvidenceDAO;
import model.*;
import util.*;

import util.MatcherUtil.SuspectScore;

import java.util.*;
import java.util.stream.*;

public class AnalysisController {

    private final PersonDAO personDAO;
    private final EvidenceDAO evidenceDAO;
    private final CaseDAO caseDAO;

    public AnalysisController(PersonDAO personDAO,
                              EvidenceDAO evidenceDAO,
                              CaseDAO caseDAO) {
        this.personDAO   = personDAO;
        this.evidenceDAO = evidenceDAO;
        this.caseDAO     = caseDAO;
    }

    /** Build an undirected graph: person ↔ evidence */
    public Graph buildLinksGraph(String caseId) {
        Graph g = new Graph();

        List<Evidence> evs = evidenceDAO.findAll().stream()
                                        .filter(e -> e.getCaseId().equals(caseId))
                                        .collect(Collectors.toList());

        List<Person> people = personDAO.findAll();

        evs.forEach(e -> {
            g.addNode(e.getId());                       // evidence node
            people.stream()
                  .filter(p -> p.getNotes()
                                .toLowerCase()
                                .contains(e.getDescription().toLowerCase()))
                  .forEach(p -> {
                      g.addNode(p.getId());
                      g.addEdge(p.getId(), e.getId());
                  });
        });
        return g;
    }

    /** Keyword-based matching against this case's evidence */
    public List<SuspectScore> matchSuspects(CrimeCase cc) {
        return MatcherUtil.rank(cc,
                                personDAO.findAll(),
                                evidenceDAO.findAll());
    }

    /**
     * Predict suspects for cc by combining direct keyword matching with
     * historical occurrence in resolved cases.
     */
    public List<SuspectScore> predict(CrimeCase cc) {
        // base scores for this case
        List<SuspectScore> base = matchSuspects(cc);

        // tally occurrences in other resolved cases
        Map<String, Long> historyCount = new HashMap<>();
        caseDAO.findAll().stream()
               .filter(c -> c.getStatus() == CaseStatus.RESOLVED)
               .filter(c -> !c.getId().equals(cc.getId()))
               .forEach(resolvedCase -> {
                   matchSuspects(resolvedCase).stream()
                       .map(s -> s.suspect().getId())
                       .forEach(id -> historyCount.merge(id, 1L, Long::sum));
               });

        // combine: base.score + historyCount
        return base.stream()
            .map(s -> new SuspectScore(
                s.suspect(),
                s.score() + historyCount.getOrDefault(s.suspect().getId(), 0L)
            ))
            .sorted(Comparator.comparingLong(SuspectScore::score).reversed())
            .collect(Collectors.toList());
    }
}