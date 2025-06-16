package model;

import com.opencsv.bean.CsvBindByName;

public class Evidence {

    @CsvBindByName(column = "id")
    private String id;

    @CsvBindByName(column = "caseId")
    private String caseId;

    @CsvBindByName(column = "type")
    private String type;

    @CsvBindByName(column = "description")
    private String description;

    public Evidence() {}   // OpenCSV no-arg ctor

    public Evidence(String id, String caseId,
                    String type, String description) {
        this.id          = id;
        this.caseId      = caseId;
        this.type        = type;
        this.description = description;
    }

    /* -------- getters & setters -------- */
    public String getId()          { return id; }
    public void   setId(String id) { this.id = id; }

    public String getCaseId()                { return caseId; }
    public void   setCaseId(String caseId)   { this.caseId = caseId; }

    public String getType()        { return type; }
    public void   setType(String t){ this.type = t; }

    public String getDescription()                   { return description; }
    public void   setDescription(String description) { this.description = description; }

    @Override public String toString() { return id + " (" + type + ")"; }
}
