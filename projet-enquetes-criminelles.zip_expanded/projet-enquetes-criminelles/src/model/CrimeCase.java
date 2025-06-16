package model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvCustomBindByName;
import java.time.LocalDate;
import java.util.List;

public class CrimeCase {

    @CsvBindByName(column = "id")
    private String id;

    @CsvBindByName(column = "title")
    private String title;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "status")
    private CaseStatus status;

    @CsvDate("M/d/yyyy")
    @CsvBindByName(column = "date")
    private LocalDate date;

    @CsvBindByName(column = "location")
    private String location;

    @CsvCustomBindByName(column = "investigatorIds", converter = model.ListToStringConverter.class)
    private List<String> investigatorIds;

    @CsvBindByName(column = "latitude")
    private double latitude;

    @CsvBindByName(column = "longitude")
    private double longitude;

    public CrimeCase() {}

    public CrimeCase(String id,
                     String title,
                     String description,
                     CaseStatus status,
                     LocalDate date,
                     String location,
                     List<String> investigatorIds,
                     double latitude,
                     double longitude) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = date;
        this.location = location;
        this.investigatorIds = investigatorIds;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<String> getInvestigatorIds() { return investigatorIds; }
    public void setInvestigatorIds(List<String> investigatorIds) { this.investigatorIds = investigatorIds; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    @Override
    public String toString() {
        return String.format("CrimeCase{id='%s', title='%s', status=%s, date=%s, location='%s', investigators=%s, latitude=%f, longitude=%f}",
                id, title, status, date, location, investigatorIds, latitude, longitude);
    }
}
