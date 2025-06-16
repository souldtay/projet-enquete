// src/controller/MainController.java
package controller;

import dao.*;
import model.*;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class MainController {

    private final CaseDAO    caseDAO    = new CaseDAO();
    private final PersonDAO  personDAO  = new PersonDAO();
    private final CommentDAO commentDAO = new CommentDAO();

    /* ---------- getters used by the views ---------- */
    public List<CrimeCase> allCases()     { return caseDAO.findAll(); }
    public List<Person>    allPersons()   { return personDAO.findAll(); }

    /** <- NEW: lets the UI retrieve every comment */
    public List<Comment>   allComments()  { return commentDAO.findAll(); }

    /* ---------- mutators ---------- */
    public void addCase(CrimeCase c)      { caseDAO.save(c); }
    public void addComment(Comment c)     { commentDAO.save(c); }

    /* ---------- search helper ---------- */
    public List<CrimeCase> filter(CaseStatus status,
                                  LocalDate from,
                                  LocalDate to) {
        return new SearchController(caseDAO).filter(status, from, to);
    }

    /* ---------- entry point ---------- */
    public static void main(String[] args) {

        // DEBUG: print actual runtime classpath & jar origin
        System.out.println("RUN CLASSPATH = " + System.getProperty("java.class.path"));
        System.out.println("BeanUtils from  = " +
                org.apache.commons.beanutils.ConversionException.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation());

        // Launch GUI
        SwingUtilities.invokeLater(() ->
            new view.MainFrame(new MainController()).setVisible(true));
    }
}
