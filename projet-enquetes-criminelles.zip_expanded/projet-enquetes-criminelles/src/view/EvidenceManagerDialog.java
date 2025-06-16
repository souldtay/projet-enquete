package view;

import dao.EvidenceDAO;
import model.Evidence;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EvidenceManagerDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final EvidenceDAO dao = new EvidenceDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Case ID", "Type", "Description"}, 0
    );
    private final JTable table = new JTable(model);

    private final JTextField caseIdField = new JTextField();
    private final JTextField typeField = new JTextField();
    private final JTextField descField = new JTextField();

    public EvidenceManagerDialog(JFrame parent) {
        super(parent, "Gestion des Preuves", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        table.setRowHeight(25);
        refreshTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("Case ID:")); form.add(caseIdField);
        form.add(new JLabel("Type:")); form.add(typeField);
        form.add(new JLabel("Description:")); form.add(descField);

        JButton addBtn = new JButton("Ajouter Preuve");
        addBtn.addActionListener(e -> addEvidence());

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(addBtn, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Evidence> all = dao.findAll();
        for (Evidence e : all) {
            model.addRow(new Object[]{e.getId(), e.getCaseId(), e.getType(), e.getDescription()});
        }
    }

    private void addEvidence() {
        String caseId = caseIdField.getText().trim();
        String type = typeField.getText().trim();
        String desc = descField.getText().trim();

        if (caseId.isEmpty() || type.isEmpty()) return;

        String id = "E" + (System.currentTimeMillis() % 10000);
        Evidence e = new Evidence(id, caseId, type, desc);
        dao.save(e);

        caseIdField.setText("");
        typeField.setText("");
        descField.setText("");
        refreshTable();
    }
}
