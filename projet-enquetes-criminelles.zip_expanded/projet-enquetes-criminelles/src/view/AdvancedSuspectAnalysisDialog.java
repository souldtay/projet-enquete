package view;

import controller.AnalysisController;
import dao.CaseDAO;
import dao.EvidenceDAO;
import dao.PersonDAO;
import model.CrimeCase;
import model.Person;
import util.MatcherUtil.SuspectScore;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AdvancedSuspectAnalysisDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JTextArea output = new JTextArea();
    private final JComboBox<String> caseBox = new JComboBox<>();
    private final JButton runBtn = new JButton("Comparer les Suspects");

    public AdvancedSuspectAnalysisDialog(JFrame parent) {
        super(parent, "Analyse entre Suspects", true);
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(0xF9F9F9));

        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        output.setBackground(new Color(0xFDFEFE));

        List<CrimeCase> cases = new CaseDAO().findAll();
        for (CrimeCase cc : cases) {
            caseBox.addItem(cc.getId() + " - " + cc.getTitle());
        }

        runBtn.setBackground(new Color(0x2980B9));
        runBtn.setForeground(Color.WHITE);
        runBtn.setFocusPainted(false);
        runBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        runBtn.addActionListener(e -> runComparison());

        JPanel top = new JPanel();
        top.setBackground(new Color(0xEBF5FB));
        top.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Choisir une Affaire:"));
        top.add(caseBox);
        top.add(runBtn);

        JScrollPane scrollPane = new JScrollPane(output);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xD5D8DC)));

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void runComparison() {
        if (caseBox.getSelectedIndex() < 0) return;

        String caseId = ((String) caseBox.getSelectedItem()).split(" ")[0];
        CrimeCase cc = new CaseDAO().findAll().stream()
                .filter(c -> c.getId().equals(caseId))
                .findFirst().orElse(null);
        if (cc == null) return;

        AnalysisController ac = new AnalysisController(
                new PersonDAO(), new EvidenceDAO(), new CaseDAO()
        );
        List<SuspectScore> list = ac.predict(cc);

        output.setText("--- Comparaison de Suspects pour l'affaire: " + cc.getTitle() + " ---\n\n");
        for (SuspectScore s : list.stream().limit(10).collect(Collectors.toList())) {
            Person p = s.suspect();
            output.append(String.format("%-10s %-15s score: %d\n",
                    p.getId(), p.getLastName(), s.score()));
        }
    }
}
