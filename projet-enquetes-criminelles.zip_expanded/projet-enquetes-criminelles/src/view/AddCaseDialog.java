package view;

import dao.CaseDAO;
import model.CaseStatus;
import model.CrimeCase;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/** Clean two‑column form for creating a CrimeCase (ID, title, desc, etc.). */
public class AddCaseDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTextField idField   = new JTextField(15);
    private final JTextField titleField= new JTextField(30);
    private final JTextArea  descArea  = new JTextArea(4,30);
    private final JComboBox<CaseStatus> statusBox = new JComboBox<>(CaseStatus.values());
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 12);
    private final JTextField locationField = new JTextField(30);
    private final JTextField investigatorsField = new JTextField("INV01;INV02", 30);
    private JTextField latitudeField;
    private JTextField longitudeField;


    private final CaseDAO dao = new CaseDAO();

    public AddCaseDialog(JFrame parent) {
        super(parent,"Ajouter une Affaire",true);
        setSize(520,420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10,10));
        getContentPane().setBackground(new Color(0xF4F6F7));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xEAEDED));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.weightx= 0;

        int row = 0;
        row = addRow(form,row,"ID:",idField);
        row = addRow(form,row,"Titre:",titleField);

        JLabel dLab = new JLabel("Description:");
        c.gridx=0; c.gridy=row; c.gridwidth=1; form.add(dLab,c);
        c.gridx=1; c.gridy=row; c.weightx=1; c.weighty=1; c.fill=GridBagConstraints.BOTH;
        descArea.setLineWrap(true); descArea.setWrapStyleWord(true);
        form.add(new JScrollPane(descArea),c);
        row++; c.weighty=0; c.fill=GridBagConstraints.HORIZONTAL;

        row = addRow(form,row,"Statut:",statusBox);
        row = addRow(form,row,"Date (YYYY-MM-DD):",dateField);
        row = addRow(form,row,"Lieu:",locationField);
        row = addRow(form,row,"Investigateurs (IDs séparés par ;) :",investigatorsField);

        // Initialisation et ajout des champs latitude et longitude
        latitudeField = new JTextField(15);
        longitudeField = new JTextField(15);
        row = addRow(form,row,"Latitude:",latitudeField);
        row = addRow(form,row,"Longitude:",longitudeField);

        JButton save = new JButton("Enregistrer");
        save.setBackground(new Color(0x3498DB));
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        save.setFont(new Font("SansSerif", Font.BOLD, 12));
        save.addActionListener(e->saveCase());

        add(form ,BorderLayout.CENTER);
        add(save ,BorderLayout.SOUTH);
    }

    private int addRow(JPanel p,int row,String label,Component field){
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(5,5,5,5);
        c.anchor=GridBagConstraints.EAST;
        c.gridx=0; c.gridy=row;
        p.add(new JLabel(label),c);
        c.gridx=1; c.gridy=row; c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL;
        p.add(field,c);
        return row+1;
    }

    private void saveCase(){
        try{
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            CaseStatus st = (CaseStatus) statusBox.getSelectedItem();
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            String loc = locationField.getText().trim();
            List<String> inv = Arrays.stream(investigatorsField.getText().split(";"))
                                     .map(String::trim)
                                     .toList();
            double latitude = Double.parseDouble(latitudeField.getText().trim());
            double longitude = Double.parseDouble(longitudeField.getText().trim());

            if (id.isBlank() || title.isBlank()) {
                JOptionPane.showMessageDialog(this, "ID et Titre sont requis");
                return;
            }

            CrimeCase cc = new CrimeCase(id, title, desc, st, date, loc, inv, latitude, longitude);
            dao.save(cc);
            dispose();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
