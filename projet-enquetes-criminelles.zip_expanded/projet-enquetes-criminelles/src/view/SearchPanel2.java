package view;

import dao.PersonDAO;
import dao.EvidenceDAO;
import model.Person;
import model.Evidence;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SearchPanel2: separate search UI for Persons and Evidence.
 */
public class SearchPanel2 extends JPanel {
    private static final long serialVersionUID = 1L;
    private final PersonDAO personDAO = new PersonDAO();
    private final EvidenceDAO evidenceDAO = new EvidenceDAO();

    private final DefaultTableModel personModel = new DefaultTableModel(
        new String[]{"ID", "Last Name", "First Name", "Role", "Notes"}, 0
    );
    private final JTable personTable = new JTable(personModel);

    private final DefaultTableModel evidenceModel = new DefaultTableModel(
        new String[]{"ID", "Case ID", "Type", "Description"}, 0
    );
    private final JTable evidenceTable = new JTable(evidenceModel);

    public SearchPanel2() {
        super(new GridLayout(2, 1, 10, 10));
        setBackground(new Color(0xF8F9FA));

        // Person search sub-panel
        JPanel personPanel = new JPanel(new BorderLayout(10, 10));
        personPanel.setBackground(new Color(0xE3F2FD));
        personPanel.setBorder(BorderFactory.createTitledBorder("Search Persons"));
        JTextField personSearchField = new JTextField();
        personSearchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        personPanel.add(new JLabel("Search (name/notes):"), BorderLayout.NORTH);
        personPanel.add(personSearchField, BorderLayout.NORTH);

        personTable.setFillsViewportHeight(true);
        personTable.setRowHeight(22);
        personTable.setShowGrid(false);
        personTable.setIntercellSpacing(new Dimension(0, 0));
        personTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        personTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        personPanel.add(new JScrollPane(personTable), BorderLayout.CENTER);

        // Evidence search sub-panel
        JPanel evidencePanel = new JPanel(new BorderLayout(10, 10));
        evidencePanel.setBackground(new Color(0xFFF3E0));
        evidencePanel.setBorder(BorderFactory.createTitledBorder("Search Evidence"));
        JTextField evidenceSearchField = new JTextField();
        evidenceSearchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        evidencePanel.add(new JLabel("Search (description/type):"), BorderLayout.NORTH);
        evidencePanel.add(evidenceSearchField, BorderLayout.NORTH);

        evidenceTable.setFillsViewportHeight(true);
        evidenceTable.setRowHeight(22);
        evidenceTable.setShowGrid(false);
        evidenceTable.setIntercellSpacing(new Dimension(0, 0));
        evidenceTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        evidenceTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        evidencePanel.add(new JScrollPane(evidenceTable), BorderLayout.CENTER);

        // Add sub-panels
        add(personPanel);
        add(evidencePanel);

        // Populate initial data
        refreshPersons(personDAO.findAll());
        refreshEvidence(evidenceDAO.findAll());

        // Add filtering listeners
        personSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterPersons(personSearchField.getText()); }
            public void removeUpdate(DocumentEvent e) { filterPersons(personSearchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filterPersons(personSearchField.getText()); }
        });

        evidenceSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterEvidence(evidenceSearchField.getText()); }
            public void removeUpdate(DocumentEvent e) { filterEvidence(evidenceSearchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filterEvidence(evidenceSearchField.getText()); }
        });
    }

    private void refreshPersons(List<Person> persons) {
        personModel.setRowCount(0);
        for (Person p : persons) {
            personModel.addRow(new Object[]{
                p.getId(), p.getLastName(), p.getFirstName(), p.getRole(), p.getNotes()
            });
        }
    }

    private void refreshEvidence(List<Evidence> evidences) {
        evidenceModel.setRowCount(0);
        for (Evidence e : evidences) {
            evidenceModel.addRow(new Object[]{
                e.getId(), e.getCaseId(), e.getType(), e.getDescription()
            });
        }
    }

    private void filterPersons(String keyword) {
        String text = keyword.trim().toLowerCase();
        List<Person> filtered = personDAO.findAll().stream()
            .filter(p -> p.getLastName().toLowerCase().contains(text)
                      || p.getFirstName().toLowerCase().contains(text)
                      || p.getNotes().toLowerCase().contains(text))
            .collect(Collectors.toList());
        refreshPersons(filtered);
    }

    private void filterEvidence(String keyword) {
        String text = keyword.trim().toLowerCase();
        List<Evidence> filtered = evidenceDAO.findAll().stream()
            .filter(e -> e.getDescription().toLowerCase().contains(text)
                      || e.getType().toLowerCase().contains(text))
            .collect(Collectors.toList());
        refreshEvidence(filtered);
    }
}
