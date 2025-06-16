package view;

import controller.AnalysisController;
import controller.MainController;
import dao.CommentDAO;
import dao.LinkDAO;
import model.*;
import util.Graph;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CaseDetailsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final MainController mc;
    private final AnalysisController ac;
    private final JPanel graphPanel; // <-- Accept any JPanel (like CarteAlbiPanel)
    private final CommentDAO commentDAO = new CommentDAO();
    private CrimeCase current;

    private final JTextArea text = new JTextArea(6, 60);
    private final DefaultListModel<String> commentModel = new DefaultListModel<>();
    private final JList<String> commentList = new JList<>(commentModel);
    private final DefaultListModel<String> suspectModel = new DefaultListModel<>();
    private final JList<String> suspectList = new JList<>(suspectModel);
    private final JTextField txtNewComment = new JTextField();
    private final JButton btnPostComment = new JButton("Post Comment");
    private final JButton btnAddPerson = new JButton("Add Person to Case");
    private final JButton btnPredictSuspects = new JButton("Predict Suspects");

    public CaseDetailsPanel(MainController mc, AnalysisController ac, JPanel graphPanel, JFrame parent) {
        super(new BorderLayout(10, 10));
        this.mc = mc;
        this.ac = ac;
        this.graphPanel = graphPanel;
        setBackground(new Color(0xF9F9F9));

        text.setEditable(false);
        text.setFont(new Font("SansSerif", Font.PLAIN, 13));
        text.setBackground(new Color(0xFDFEFE));
        text.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(text), BorderLayout.NORTH);

        JPanel east = new JPanel(new BorderLayout());
        east.setBackground(new Color(0xF0F3F4));
        JLabel lblMatches = new JLabel("Top suspect matches");
        lblMatches.setFont(new Font("SansSerif", Font.BOLD, 13));
        east.add(lblMatches, BorderLayout.NORTH);
        suspectList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        east.add(new JScrollPane(suspectList), BorderLayout.CENTER);
        add(east, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(10, 10));
        south.setBackground(new Color(0xFBFCFC));
        JLabel lblComments = new JLabel("Comments");
        lblComments.setFont(new Font("SansSerif", Font.BOLD, 13));
        south.add(lblComments, BorderLayout.NORTH);
        commentList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        south.add(new JScrollPane(commentList), BorderLayout.CENTER);

        JPanel entry = new JPanel(new BorderLayout(5, 5));
        entry.add(txtNewComment, BorderLayout.CENTER);
        btnPostComment.setBackground(new Color(0x27AE60));
        btnPostComment.setForeground(Color.WHITE);
        btnPostComment.setFocusPainted(false);
        entry.add(btnPostComment, BorderLayout.EAST);
        south.add(entry, BorderLayout.SOUTH);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(new Color(0xFBFCFC));
        btnAddPerson.setBackground(new Color(0x5DADE2));
        btnAddPerson.setForeground(Color.WHITE);
        btnAddPerson.setFocusPainted(false);
        btnPredictSuspects.setBackground(new Color(0xAF7AC5));
        btnPredictSuspects.setForeground(Color.WHITE);
        btnPredictSuspects.setFocusPainted(false);
        footer.add(btnAddPerson);
        footer.add(btnPredictSuspects);
        south.add(footer, BorderLayout.PAGE_END);
        add(south, BorderLayout.SOUTH);

        btnPostComment.addActionListener(e -> {
            if (current != null && !txtNewComment.getText().isBlank()) {
                String author = JOptionPane.showInputDialog(parent, "Enter your name:", "Comment Author", JOptionPane.PLAIN_MESSAGE);
                if (author == null || author.isBlank()) author = "Anonymous";
                Comment c = new Comment(current.getId(), author, LocalDateTime.now(), txtNewComment.getText().trim());
                commentDAO.save(c);
                load(current, commentDAO.findAll().stream().filter(cm -> cm.getCaseId().equals(current.getId())).toList());
                txtNewComment.setText("");
            }
        });

        btnAddPerson.addActionListener(e -> {
            if (current == null) {
                JOptionPane.showMessageDialog(parent, "Select a case first.", "No Case", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<Person> all = mc.allPersons();
            String[] ids = all.stream().map(Person::getId).toArray(String[]::new);
            String choice = (String) JOptionPane.showInputDialog(parent, "Select person to add to case:", "Add Person", JOptionPane.PLAIN_MESSAGE, null, ids, ids[0]);
            if (choice != null) {
                LinkDAO.save(new Link(current.getId(), choice, null));
                // Optional: if CarteAlbiPanel has a refresh method, call it here
                // Example: if (graphPanel instanceof CarteAlbiPanel panel) panel.refresh();
                load(current, commentDAO.findAll().stream().filter(cm -> cm.getCaseId().equals(current.getId())).toList());
                JOptionPane.showMessageDialog(parent, "Person " + choice + " added to case " + current.getId(), "Person Added", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnPredictSuspects.addActionListener(e -> {
            if (current != null) {
                List<util.MatcherUtil.SuspectScore> preds = ac.predict(current);
                suspectModel.clear();
                preds.stream().limit(5).forEach(s -> suspectModel.addElement(s.suspect().getId() + " (pred " + s.score() + ")"));
            }
        });

        suspectList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (suspectList.getSelectedIndex() >= 0) {
                    String sel = suspectList.getSelectedValue().split(" ")[0];
                    Person p = mc.allPersons().stream().filter(x -> x.getId().equals(sel)).findFirst().orElse(null);
                    if (p != null) new PersonDetailsDialog(parent, p).setVisible(true);
                }
            }
        });
    }

    public void load(CrimeCase cc, List<Comment> comments) {
        this.current = cc;
        text.setText("Title: " + cc.getTitle() + "\n" +
                     "Desc : " + cc.getDescription() + "\n" +
                     "Date : " + cc.getDate().format(DateTimeFormatter.ISO_DATE) + "\n");

        commentModel.clear();
        comments.forEach(c -> commentModel.addElement(String.format("[%s] %s: %s", c.getTimestamp().toLocalDate(), c.getAuthor(), c.getMessage())));

        suspectModel.clear();
        ac.matchSuspects(cc).stream().limit(5).forEach(s -> suspectModel.addElement(s.suspect().getId() + " (score " + s.score() + ")"));
    }
}
