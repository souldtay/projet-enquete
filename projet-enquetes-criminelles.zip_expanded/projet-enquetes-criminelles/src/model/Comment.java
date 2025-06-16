// src/model/Comment.java
package model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import util.LocalDateTimeConverter;

import java.time.LocalDateTime;

public class Comment {

    @CsvBindByName(column = "caseId")
    private String caseId;

    @CsvBindByName(column = "author")
    private String author;

    @CsvCustomBindByName(column = "timestamp",
                         converter = LocalDateTimeConverter.class)
    private LocalDateTime timestamp;

    @CsvBindByName(column = "message")
    private String message;

    /* ---------- ctors ---------- */
    public Comment() {}                     // for OpenCSV

    public Comment(String caseId, String author,
                   LocalDateTime ts, String msg) {
        this.caseId   = caseId;
        this.author   = author;
        this.timestamp= ts;
        this.message  = msg;
    }

    /* ---------- getters & setters ---------- */
    public String        getCaseId()   { return caseId; }
    public void          setCaseId(String caseId) { this.caseId = caseId; }

    public String        getAuthor()   { return author; }
    public void          setAuthor(String author) { this.author = author; }

    public LocalDateTime getTimestamp(){ return timestamp; }
    public void          setTimestamp(LocalDateTime t) { this.timestamp = t; }

    public String        getMessage()  { return message; }
    public void          setMessage(String message) { this.message = message; }

    @Override public String toString() {
        return "[%s] %s: %s".formatted(timestamp.toLocalDate(), author, message);
    }
}
