package view;

import model.Person;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Dialog to show full Person details including photo.
 */
public class PersonDetailsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JLabel lblName = new JLabel();
    private final JLabel lblRole = new JLabel();
    private final JLabel lblLocation = new JLabel();
    private final JLabel lblAge = new JLabel();
    private final JLabel picLabel = new JLabel();

    public PersonDetailsDialog(JFrame parent, Person p) {
        super(parent, "Détails de la Personne: " + p.getId(), true);
        setLayout(new BorderLayout(10, 10));
        setSize(400, 250);
        setLocationRelativeTo(parent);

        // Font styling
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        lblName.setFont(font);
        lblRole.setFont(font);
        lblAge.setFont(font);
        lblLocation.setFont(font);

        // Panel for text info
        JPanel info = new JPanel(new GridLayout(4, 1, 5, 5));
        info.setBorder(BorderFactory.createTitledBorder("Informations"));
        info.add(lblName);
        info.add(lblRole);
        info.add(lblAge);
        info.add(lblLocation);
        add(info, BorderLayout.CENTER);

        // Picture handling
        picLabel.setPreferredSize(new Dimension(120, 120));
        picLabel.setHorizontalAlignment(JLabel.CENTER);
        loadPhoto(p.getId());

        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setBorder(BorderFactory.createTitledBorder("Photo"));
        westPanel.add(picLabel, BorderLayout.CENTER);
        add(westPanel, BorderLayout.WEST);

        // Load person data
        lblName.setText("Nom: " + p.getFirstName() + " " + p.getLastName());
        lblRole.setText("Rôle: " + p.getRole());
        lblAge.setText("Age: " + p.getNotes().replaceAll(".*age=(\\d+).*", "$1"));
        lblLocation.setText("Localisation: " + p.getNotes().replaceAll(".*loc=([^;]+).*", "$1"));

        getContentPane().setBackground(new Color(245, 245, 255));
    }

    private void loadPhoto(String id) {
        try {
            File photoFile = new File("photos/" + id + ".jpg");
            if (photoFile.exists()) {
                BufferedImage img = ImageIO.read(photoFile);
                Image scaled = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                picLabel.setIcon(new ImageIcon(scaled));
            } else {
                picLabel.setIcon(new ImageIcon(new BufferedImage(120, 120, BufferedImage.TYPE_INT_RGB)));
                picLabel.setText("No photo");
            }
        } catch (Exception e) {
            picLabel.setText("Error loading photo");
            e.printStackTrace();
        }
    }
}
