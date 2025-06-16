// src/model/Person.java
package model;

import com.opencsv.bean.CsvBindByName;

public class Person {

    @CsvBindByName(column = "id")
    private String id;

    @CsvBindByName(column = "lastName")
    private String lastName;

    @CsvBindByName(column = "firstName")
    private String firstName;

    @CsvBindByName(column = "role")
    private PersonRole role;

    @CsvBindByName(column = "notes")
    private String notes;

    /* ---------- ctors ---------- */
    public Person() {}   // required by OpenCSV

    public Person(String id, String ln, String fn,
                  PersonRole role, String notes) {
        this.id = id;
        this.lastName  = ln;
        this.firstName = fn;
        this.role      = role;
        this.notes     = notes;
    }

    /* ---------- getters & setters ---------- */
    public String      getId()        { return id; }
    public void        setId(String id) { this.id = id; }

    public String      getLastName()  { return lastName; }
    public void        setLastName(String ln) { this.lastName = ln; }

    public String      getFirstName() { return firstName; }
    public void        setFirstName(String fn) { this.firstName = fn; }

    public PersonRole  getRole()      { return role; }
    public void        setRole(PersonRole role) { this.role = role; }

    public String      getNotes()     { return notes; }
    public void        setNotes(String notes) { this.notes = notes; }

    @Override public String toString() { return id + " " + lastName; }
}
