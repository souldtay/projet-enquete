package view;

import dao.PersonDAO;
import model.Person;
import model.PersonRole;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.*;

/** Dialog: list + add person *with* photo chooser – photo saved as photos/<id>.jpg */
public class AddPersonDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final PersonDAO dao = new PersonDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Nom","Prénom","Rôle","Notes"},0);
    private final JTable table = new JTable(model);

    private final JLabel photoLabel = new JLabel();

    private final JTextField idField       = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JTextField firstNameField= new JTextField();
    private final JComboBox<PersonRole> roleBox = new JComboBox<>(PersonRole.values());
    private final JTextArea  notesArea     = new JTextArea(3,20);
    private final JTextField photoPathField = new JTextField();
    private File selectedPhotoFile = null;

    public AddPersonDialog(JFrame parent) {
        super(parent,"Gestion des Personnes",true);
        setSize(800,570);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10,10));
        getContentPane().setBackground(new Color(0xF9F9F9));

        refreshTable();
        table.setRowHeight(22);
        table.getSelectionModel().addListSelectionListener(tableSelectionListener());

        JScrollPane tableScroll = new JScrollPane(table);
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        photoLabel.setPreferredSize(new Dimension(200,200));
        JPanel preview = new JPanel(new BorderLayout());
        preview.setBackground(new Color(0xEBF5FB));
        preview.add(photoLabel,BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, preview);
        split.setResizeWeight(0.7);
        add(split,BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(0xE8F8F5));
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(4,4,4,4); c.fill=GridBagConstraints.HORIZONTAL;

        int row=0;
        addRow(form,c,row++,"ID:",idField);
        addRow(form,c,row++,"Nom:",lastNameField);
        addRow(form,c,row++,"Prénom:",firstNameField);
        addRow(form,c,row++,"Rôle:",roleBox);
        addRow(form,c,row++,"Notes:",new JScrollPane(notesArea));

        JButton choose = new JButton("Choisir Photo…");
        choose.setBackground(new Color(0x85C1E9));
        choose.setForeground(Color.WHITE);
        choose.setFocusPainted(false);
        choose.setFont(new Font("SansSerif", Font.BOLD, 11));
        choose.addActionListener(e->choosePhoto());

        addRow(form,c,row++,"Photo:",photoPathField);
        c.gridx=2; c.gridy=row-1; c.weightx=0; form.add(choose,c);

        JButton save = new JButton("Enregistrer");
        save.setBackground(new Color(0x2ECC71));
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        save.setFont(new Font("SansSerif", Font.BOLD, 12));
        save.addActionListener(e->save());

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(new Color(0xD5F5E3));
        south.add(form,BorderLayout.CENTER);
        south.add(save,BorderLayout.SOUTH);
        add(south,BorderLayout.SOUTH);
    }

    private void addRow(JPanel p,GridBagConstraints c,int row,String lab,Component field){
        c.gridx=0; c.gridy=row; c.weightx=0;
        p.add(new JLabel(lab),c);
        c.gridx=1; c.gridy=row; c.weightx=1;
        p.add(field,c);
    }

    private void choosePhoto(){
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            selectedPhotoFile = fc.getSelectedFile();
            photoPathField.setText(selectedPhotoFile.getAbsolutePath());
        }
    }

    private void save(){
        try{
            String id = idField.getText().trim();
            String ln = lastNameField.getText().trim();
            String fn = firstNameField.getText().trim();
            PersonRole role = (PersonRole) roleBox.getSelectedItem();
            String notes = notesArea.getText().trim();
            if(id.isBlank()||ln.isBlank()){
                JOptionPane.showMessageDialog(this,"ID et Nom sont requis");
                return;
            }
            Person p = new Person(id,ln,fn,role,notes);
            dao.save(p);
            if(selectedPhotoFile!=null){
                Path dest = Path.of("photos",id+".jpg");
                Files.createDirectories(dest.getParent());
                Files.copy(selectedPhotoFile.toPath(),dest,StandardCopyOption.REPLACE_EXISTING);
            }
            clearForm();
            refreshTable();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Erreur : "+ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void refreshTable(){
        model.setRowCount(0);
        dao.findAll().forEach(p ->
            model.addRow(new Object[]{p.getId(),p.getLastName(),p.getFirstName(),p.getRole(),p.getNotes()})
        );
    }

    private void clearForm(){
        idField.setText(""); lastNameField.setText(""); firstNameField.setText("");
        notesArea.setText(""); photoPathField.setText(""); selectedPhotoFile=null;
        roleBox.setSelectedIndex(0);
    }

    private ListSelectionListener tableSelectionListener(){
        return e -> {
            if(e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if(row<0) {photoLabel.setIcon(null); return;}            
            String id=(String)model.getValueAt(row,0);
            Path imgPath = Path.of("photos",id+".jpg");
            if(Files.exists(imgPath)){
                try{
                    BufferedImage img = ImageIO.read(imgPath.toFile());
                    Image scaled = img.getScaledInstance(200,200,Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(scaled));
                }catch(Exception ex){photoLabel.setIcon(null);}            
            }else photoLabel.setIcon(null);
        };
    }
}
