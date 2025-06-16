package util;

import model.*;
import java.util.*;
import java.util.stream.*;
import java.util.Arrays;
/**
 * Scores suspects by counting how many keywords from the case's evidence
 * descriptions appear in each suspect's notes.  Higher score ⇒ stronger match.
 */
public final class MatcherUtil {

    private MatcherUtil() { }

    public static List<SuspectScore> rank(CrimeCase cc,
                                          List<Person> people,
                                          List<Evidence> evs) {

        /* ---- 1. extract keywords from THIS case's evidence ---- */
        Set<String> kw = evs.stream()
                .filter(e -> e.getCaseId().equals(cc.getId()))
                /* explicit lambda type eliminates "cannot infer" error */
                .flatMap((Evidence ev) -> Arrays.stream(
                        ev.getDescription()
                          .toLowerCase()
                          .split("\\W+")
                ))
                .filter(s -> s.length() > 3)              // ignore tiny stop-words
                .collect(Collectors.toSet());

        /* ---- 2. score every SUSPECT ---- */
        return people.stream()
                .filter(p -> p.getRole() == PersonRole.SUSPECT)
                .map(p -> new SuspectScore(
                        p,
                        kw.stream()
                          .filter(k -> p.getNotes()
                                        .toLowerCase()
                                        .contains(k))
                          .count()))
                .sorted(Comparator.comparingLong(SuspectScore::score)
                                  .reversed())
                .collect(Collectors.toList());            // Java-8 friendly
    }

    /* simple record to hold results */
    public record SuspectScore(Person suspect, long score) { }
}
