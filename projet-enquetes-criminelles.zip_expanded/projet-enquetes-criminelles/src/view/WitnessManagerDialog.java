package view;

import dao.PersonDAO;
import model.Person;
import model.PersonRole;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WitnessManagerDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final PersonDAO dao = new PersonDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Nom", "Notes"}, 0
    );
    private final JTable table = new JTable(model);

    private final JTextField nameField = new JTextField();
    private final JTextField notesField = new JTextField();

    public WitnessManagerDialog(JFrame parent) {
        super(parent, "Gestion des Témoins", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(0xF4F6F7));

        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setBackground(Color.WHITE);
        refreshTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des témoins"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Ajouter un nouveau témoin"));
        form.setBackground(new Color(0xEAEDED));
        form.add(new JLabel("Nom:")); form.add(nameField);
        form.add(new JLabel("Notes:")); form.add(notesField);

        JButton addBtn = new JButton("Ajouter Témoin");
        addBtn.setBackground(new Color(0x2ECC71));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addBtn.addActionListener(e -> addWitness());

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(new Color(0xEAEDED));
        south.add(form, BorderLayout.CENTER);
        south.add(addBtn, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Person> witnesses = dao.findAll().stream()
                .filter(p -> p.getRole() == PersonRole.WITNESS)
                .toList();
        for (Person p : witnesses) {
            model.addRow(new Object[]{p.getId(), p.getLastName(), p.getNotes()});
        }
    }

    private void addWitness() {
        String name = nameField.getText().trim();
        String notes = notesField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le champ Nom est requis.");
            return;
        }

        String id = "W" + (System.currentTimeMillis() % 10000);
        Person p = new Person(id, name, "", PersonRole.WITNESS, notes);
        dao.save(p);

        nameField.setText("");
        notesField.setText("");
        refreshTable();
    }
}
