package view;

import dao.PersonDAO;
import model.Person;
import model.PersonRole;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SuspectManagerDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final PersonDAO dao = new PersonDAO();
    // table model without the Age column
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Nom", "Antécédents", "Actions"}, 0
    );
    private final JTable table = new JTable(model);

    private final JTextField nameField  = new JTextField();
    private final JTextField notesField = new JTextField();

    public SuspectManagerDialog(JFrame parent) {
        super(parent, "Gestion des Suspects", true);
        setSize(650, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(0xF4F6F7));

        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setBackground(Color.WHITE);
        refreshTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des suspects"));
        add(scrollPane, BorderLayout.CENTER);

        // ----- form panel (Nom + Antécédents) -----
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Ajouter un nouveau suspect"));
        form.setBackground(new Color(0xEAEDED));
        form.add(new JLabel("Nom:"));          form.add(nameField);
        form.add(new JLabel("Antécédents:"));  form.add(notesField);

        JButton addBtn = new JButton("Ajouter Suspect");
        addBtn.setBackground(new Color(0x3498DB));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addBtn.addActionListener(e -> addSuspect());

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(new Color(0xEAEDED));
        south.add(form, BorderLayout.CENTER);
        south.add(addBtn, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
    }

    /** Reloads the JTable from persons.csv → only SUSPECT rows */
    private void refreshTable() {
        System.out.println("[DEBUG] Refreshing suspect table…");
        model.setRowCount(0);
        List<Person> suspects = dao.findAll().stream()
                                   .filter(p -> p.getRole() == PersonRole.SUSPECT)
                                   .toList();
        System.out.println("[DEBUG] Found " + suspects.size() + " suspects.");
        for (Person p : suspects) {
            System.out.println("[DEBUG]  → " + p.getId() + ", notes=" + p.getNotes());
            model.addRow(new Object[]{p.getId(), p.getLastName(), p.getNotes(), "Supprimer"});
        }
    }

    /** Saves a new suspect (role=SUSPECT) and refreshes the table */
    private void addSuspect() {
        System.out.println("[DEBUG] Attempting to add suspect…");
        String name  = nameField.getText().trim();
        String notes = notesField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le champ Nom est requis.");
            return;
        }

        String id = "S" + (System.currentTimeMillis() % 100000);
        Person p  = new Person(id, name, "", PersonRole.SUSPECT, notes);
        System.out.println("[DEBUG] Saving suspect: " + p);
        dao.save(p);

        nameField.setText("");
        notesField.setText("");
        refreshTable();
    }
}
