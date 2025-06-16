package model;
import com.opencsv.bean.CsvBindByName;
public class Link {
    @CsvBindByName(column="caseId") public String caseId;
    @CsvBindByName(column="personId") public String personId;
    @CsvBindByName(column="evidenceId") public String evidenceId;
    public Link(){}
    public Link(String c, String p, String e) { caseId=c; personId=p; evidenceId=e; }
}

// Note: Prediction system and full search panels would be added similarly
// with new controller methods and UI components.
