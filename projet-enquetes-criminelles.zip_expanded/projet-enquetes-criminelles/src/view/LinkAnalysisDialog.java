package view;

import dao.EvidenceDAO;
import dao.PersonDAO;
import dao.LinkDAO;
import model.Evidence;
import model.Link;
import model.Person;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class LinkAnalysisDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Person ID", "Name", "Evidence", "Case ID"}, 0
    );
    private final JTable table = new JTable(model);

    public LinkAnalysisDialog(JFrame parent) {
        super(parent, "Analyse des Liens", true);
        setSize(750, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(0xFDFEFE));

        loadLinks();
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Links Between People and Evidences"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadLinks() {
        List<Link> links = dao.LinkDAO.readAll();
        PersonDAO personDAO = new PersonDAO();
        EvidenceDAO evidenceDAO = new EvidenceDAO();

        model.setRowCount(0);
        for (Link link : links) {
            String personId = link.personId;
            String evidenceId = link.evidenceId;

            Optional<Person> personOpt = personDAO.find(p -> p.getId().equals(personId));
            Optional<Evidence> evidenceOpt = evidenceDAO.find(e -> e.getId().equals(evidenceId));

            String personName = personOpt.map(p -> p.getFirstName() + " " + p.getLastName()).orElse("?");
            String evDesc = evidenceOpt.map(Evidence::getDescription).orElse("?");
            String caseId = evidenceOpt.map(Evidence::getCaseId).orElse("?");

            model.addRow(new Object[]{personId, personName, evDesc, caseId});
        }
    }
}
