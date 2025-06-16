// replace old SearchPanel
package view;

import controller.MainController;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class SearchPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final MainController mc;
    private final DefaultTableModel tm =
        new DefaultTableModel(new String[]{"ID","Title","Status","Date","City"}, 0);

    public SearchPanel(MainController mc) {
        super(new BorderLayout(10, 10));
        this.mc = mc;
        setBackground(new Color(0xF8F9FA));

        JComboBox<CaseStatus> statusBox = new JComboBox<>(CaseStatus.values());
        statusBox.insertItemAt(null, 0);
        statusBox.setSelectedIndex(0);

        JTextField cityField = new JTextField(8);
        JTextField fromField = new JTextField("2025-01-01", 8);
        JTextField toField   = new JTextField("2025-12-31", 8);

        JButton run = new JButton("Filter");
        run.setBackground(new Color(0x007BFF));
        run.setForeground(Color.WHITE);
        run.setFocusPainted(false);
        run.setFont(new Font("SansSerif", Font.BOLD, 12));
        run.addActionListener(e -> {
            try {
                CaseStatus st = (CaseStatus) statusBox.getSelectedItem();
                LocalDate from = LocalDate.parse(fromField.getText());
                LocalDate to   = LocalDate.parse(toField.getText());
                String city    = cityField.getText().trim().toLowerCase();

                tm.setRowCount(0);
                mc.allCases().stream()
                  .filter(c -> (st == null || c.getStatus() == st))
                  .filter(c -> !c.getDate().isBefore(from) && !c.getDate().isAfter(to))
                  .filter(c -> city.isBlank() || c.getLocation().toLowerCase().contains(city))
                  .forEach(c -> tm.addRow(new Object[]{
                          c.getId(), c.getTitle(), c.getStatus(), c.getDate(), c.getLocation()}));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setBackground(new Color(0xE9ECEF));
        top.setBorder(BorderFactory.createTitledBorder("Search Filters"));
        top.add(new JLabel("Status:")); top.add(statusBox);
        top.add(new JLabel("City:"));   top.add(cityField);
        top.add(new JLabel("From:"));   top.add(fromField);
        top.add(new JLabel("To:"));     top.add(toField);
        top.add(run);

        JTable table = new JTable(tm);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Matching Cases"));

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
}
