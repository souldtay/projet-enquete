package view;

import controller.MainController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final CardLayout cards = new CardLayout();
    private final JPanel root   = new JPanel(cards);

    public MainFrame(MainController mc) {
        super("Criminal Case Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        applyTheme();

        /* centre panels */
        root.add(new DashboardPanel(mc, this), "dash");
        root.add(new SearchPanel(mc),           "search");
        root.add(new SearchPanel2(),            "search2");

        setJMenuBar(buildMenu());
        add(root);
    }

    /* --------------------------------------------------------- */
    /*  MENU BAR                                                 */
    /* --------------------------------------------------------- */
    private JMenuBar buildMenu() {

        JMenuBar bar = new JMenuBar();

        /* -------- Modèles -------- */
        JMenu modeles = new JMenu("Modèles");

        /* CRUD dialogs already provided */
        JMenuItem suspects = new JMenuItem("Suspects");
        suspects.addActionListener(e -> new SuspectManagerDialog(this).setVisible(true));

        JMenuItem temoins = new JMenuItem("Témoins");
        temoins.addActionListener(e -> new WitnessManagerDialog(this).setVisible(true));

        JMenuItem preuves = new JMenuItem("Preuves");
        preuves.addActionListener(e -> new EvidenceManagerDialog(this).setVisible(true));

        /* NEW — add-case form */
        JMenuItem addCase = new JMenuItem("Nouvelle Affaire…");
        addCase.addActionListener(e -> {
            new AddCaseDialog(this).setVisible(true);
            // first component in root is the DashboardPanel → refresh its table
            ((DashboardPanel) root.getComponent(0)).refresh();
        });

        /* NEW — add-person form */
        JMenuItem addPerson = new JMenuItem("Nouvelle Personne…");
        addPerson.addActionListener(e -> new AddPersonDialog(this).setVisible(true));

        /* existing “Affaires” list is shown by switching to dashboard */
        JMenuItem affaires = new JMenuItem("Tableau des Affaires");
        affaires.addActionListener(e -> cards.show(root, "dash"));

        modeles.add(affaires);
        modeles.add(addCase);
        modeles.add(addPerson);
        modeles.addSeparator();
        modeles.add(suspects);
        modeles.add(temoins);
        modeles.add(preuves);

        /* -------- Fonctionnalités -------- */
        JMenu fonction = new JMenu("Fonctionnalités");
        JMenuItem analyseLiens = new JMenuItem("Analyse des Liens");
        analyseLiens.addActionListener(e -> new LinkAnalysisDialog(this).setVisible(true));

        JMenuItem recherche = new JMenuItem("Recherche des Affaires");
        recherche.addActionListener(e -> cards.show(root, "search"));

        fonction.add(analyseLiens);
        fonction.add(recherche);

        /* -------- Analyse avancée -------- */
        JMenu avancee = new JMenu("Analyse Avancée");
        JMenuItem analyseSuspects = new JMenuItem("Analyse entre Suspects");
        analyseSuspects.addActionListener(e ->
                new AdvancedSuspectAnalysisDialog(this).setVisible(true));
        avancee.add(analyseSuspects);

        /* assemble bar */
        bar.add(modeles);
        bar.add(fonction);
        bar.add(avancee);

        bar.add(Box.createHorizontalGlue());
        JLabel userLabel = new JLabel("Connecté comme: " + UIManager.get("connected.user"));
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 10));
        bar.add(userLabel);

        return bar;
    }

    /* --------------------------------------------------------- */
    private void applyTheme() {
        UIManager.put("control",       new Color(0xF4F4F4));
        UIManager.put("info",          new Color(0xFFFFFF));
        UIManager.put("nimbusBase",    new Color(0x4A90E2));
        UIManager.put("nimbusBlueGrey",new Color(0xD0D0D0));
        UIManager.put("nimbusFocus",   new Color(0xFF7043));
        try {
            UIManager.setLookAndFeel(
                new javax.swing.plaf.nimbus.NimbusLookAndFeel());
        } catch (Exception ignored) {}
    }
}
