package view;

import controller.AnalysisController;
import controller.MainController;
import dao.PersonDAO;
import dao.EvidenceDAO;
import dao.CaseDAO;
import model.CrimeCase;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final MainController mc;
    private final AnalysisController ac;
    private final DefaultTableModel tm =
        new DefaultTableModel(new String[]{"ID","Title","Status","Date"}, 0);

    private final CaseDetailsPanel details;
    private final CarteAlbiPanel graphPanel;

    private final JButton btnDummy = new JButton("Add dummy case");
    private CrimeCase currentCase;

    public DashboardPanel(MainController mc, JFrame parent) {
        super(new BorderLayout());
        this.mc = mc;
        this.ac = new AnalysisController(
            new PersonDAO(), new EvidenceDAO(), new CaseDAO()
        );

        this.graphPanel = new CarteAlbiPanel();
        graphPanel.setPreferredSize(new Dimension(400, 0));

        this.details = new CaseDetailsPanel(mc, ac, graphPanel, parent);

        JTable table = new JTable(tm);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setRowHeight(24);
        table.setSelectionBackground(new Color(0x3F51B5));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setBackground(new Color(0xFAFAFA));

        // IMPORTANT : Listener pour charger les détails quand on sélectionne une affaire
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            openDetails(table.getSelectedRow(), parent);
        });

        JPanel southBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnDummy.setBackground(new Color(0x4CAF50));
        btnDummy.setForeground(Color.WHITE);
        btnDummy.setFocusPainted(false);
        btnDummy.setFont(new Font("SansSerif", Font.BOLD, 12));
        southBtns.add(btnDummy);
        add(southBtns, BorderLayout.SOUTH);

        btnDummy.addActionListener(e -> {
        	CrimeCase c = new CrimeCase(
        		    "C" + (System.currentTimeMillis() % 100000),
        		    "Short desc",
        		    "Test case – student click",
        		    model.CaseStatus.OPEN,
        		    java.time.LocalDate.now(),
        		    "Albi",            // Modifie "Paris" par "Albi"
        		    List.of("INV01"),
        		    43.9290,           // latitude
        		    2.1480             // longitude
        		);

            mc.addCase(c);
            refresh();
        });

        JSplitPane vertical = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(table),
            details
        );
        vertical.setResizeWeight(0.60);
        vertical.setDividerLocation(0.60);
        vertical.setDividerSize(6);

        JSplitPane horizontal = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            vertical,
            graphPanel
        );
        horizontal.setResizeWeight(0.65);
        horizontal.setDividerLocation(0.65);
        horizontal.setDividerSize(6);
        horizontal.setBorder(null);

        add(horizontal, BorderLayout.CENTER);

        // Charge la liste des affaires au démarrage
        refresh();
    }

    public void refresh() {
        tm.setRowCount(0);
        List<CrimeCase> cases = mc.allCases();
        cases.forEach(c ->
            tm.addRow(new Object[]{c.getId(), c.getTitle(), c.getStatus(), c.getDate()})
        );

        // Mets à jour la carte avec toutes les affaires
        updateMapWithCases(cases);
        
    }

    private void openDetails(int row, JFrame parent) {
        if (row < 0) return;

        String caseId = (String) tm.getValueAt(row, 0);
        currentCase = mc.allCases().stream()
                .filter(c -> c.getId().equals(caseId))
                .findFirst()
                .orElse(null);

        if (currentCase == null) return;

        details.load(
        	    currentCase,
        	    mc.allComments().stream()
        	        .filter(comm -> comm.getCaseId().equals(caseId))
        	        .toList()
        	);

        	// Met à jour la carte avec mise en évidence de l'affaire sélectionnée
        	if (graphPanel instanceof CarteAlbiPanel panel) {
        	    panel.highlightAndShowCases(mc.allCases(), currentCase);
        	}

        // Si tu veux, tu peux aussi faire un rafraîchissement carte ici si nécessaire
    }

    private void updateMapWithCases(List<CrimeCase> cases) {
        if (graphPanel instanceof CarteAlbiPanel panel) {
            panel.showCases(cases); // méthode à créer dans CarteAlbiPanel
        }
    }
    
}
