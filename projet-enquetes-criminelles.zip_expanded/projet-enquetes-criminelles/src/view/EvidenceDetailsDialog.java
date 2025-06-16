package view;

import model.Evidence;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Simple two-pane dialog: photo on the left, description on the right.
 * Looks for a JPEG named photos/{evidenceId}.jpg.
 */
public class EvidenceDetailsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public EvidenceDetailsDialog(JFrame parent, Evidence ev) {
        super(parent, "Evidence " + ev.getId(), true);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(0xFDFEFE));

        // description
        JTextArea desc = new JTextArea(ev.getDescription(), 5, 30);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        desc.setBackground(new Color(0xFDFEFE));
        desc.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(desc), BorderLayout.CENTER);

        // picture
        JLabel pic = new JLabel();
        File imgFile = new File("photos/" + ev.getId() + ".jpg");
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            pic.setIcon(new ImageIcon(scaled));
        } else {
            pic.setText("No Image");
            pic.setHorizontalAlignment(SwingConstants.CENTER);
        }
        pic.setPreferredSize(new Dimension(140, 140));
        pic.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(pic, BorderLayout.WEST);

        pack();
        setLocationRelativeTo(parent);
    }
}
