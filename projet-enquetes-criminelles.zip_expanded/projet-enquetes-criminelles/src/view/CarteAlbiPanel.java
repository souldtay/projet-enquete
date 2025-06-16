package view;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.CrimeCase;

public class CarteAlbiPanel extends JPanel {

    private final JMapViewer mapViewer;

    public CarteAlbiPanel() {
        setLayout(new BorderLayout());

        mapViewer = new JMapViewer();
        mapViewer.setDisplayPosition(new Coordinate(43.9290, 2.1480), 14); // Albi

        add(mapViewer, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder("Carte d'Albi"));
    }

    /**
     * Affiche toutes les affaires en bleu
     */
    public void showCases(List<CrimeCase> cases) {
        mapViewer.removeAllMapMarkers();

        for (CrimeCase c : cases) {
        	if (c.getLatitude() != 0.0 && c.getLongitude() != 0.0) {
                Coordinate coord = new Coordinate(c.getLatitude(), c.getLongitude());
                MapMarkerDot marker = new MapMarkerDot(c.getTitle(), coord);
                marker.setBackColor(Color.BLUE);
                mapViewer.addMapMarker(marker);
            }
        }

        repaint();
    }

    /**
     * Affiche toutes les affaires, et met en rouge l'affaire sélectionnée (centrée)
     */
    public void highlightAndShowCases(List<CrimeCase> allCases, CrimeCase selectedCase) {
        mapViewer.removeAllMapMarkers();
        Coordinate zoomTarget = null;

        for (CrimeCase c : allCases) {
            // Ignore les affaires sans coordonnées valides (latitude=0 et longitude=0)
            if (c.getLatitude() == 0.0 && c.getLongitude() == 0.0) continue;

            Coordinate coord = new Coordinate(c.getLatitude(), c.getLongitude());
            MapMarkerDot marker = new MapMarkerDot(c.getTitle(), coord);

            if (selectedCase != null && c.getId().equals(selectedCase.getId())) {
                marker.setBackColor(Color.RED);  // Affaire sélectionnée
                zoomTarget = coord;
            } else {
                marker.setBackColor(Color.BLUE); // Les autres affaires
            }

            mapViewer.addMapMarker(marker);
        }

        if (zoomTarget != null) {
            mapViewer.setDisplayPosition(zoomTarget, 16); // Zoom sur l'affaire sélectionnée
        }

        repaint();
    }

    public void centrerSurAffaire(CrimeCase affaire) {
        if (affaire != null && affaire.getLatitude() != 0.0 && affaire.getLongitude() != 0.0) {
            Coordinate coord = new Coordinate(affaire.getLatitude(), affaire.getLongitude());
            mapViewer.setDisplayPosition(coord, 15); // Zoom sur l'affaire
        }
    }

}
