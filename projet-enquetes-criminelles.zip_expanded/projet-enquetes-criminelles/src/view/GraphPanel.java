package view;

import dao.EvidenceDAO;
import model.Evidence;
import util.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Draws the person-evidence graph and lets the user click an
 * evidence node to open its detail dialog.
 */
public class GraphPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private Graph g = new Graph();
    /** node → screen position (re-built on every repaint) */
    private final Map<String, Point> nodePositions = new HashMap<>();

    public GraphPanel() {
        setBackground(new Color(0xF0F8FF)); // Light background for clarity
        setBorder(BorderFactory.createTitledBorder("Relation Graph"));

        /* --- click handling ------------------------------------------------ */
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                nodePositions.forEach((id, p) -> {
                    if (p.distance(e.getPoint()) < 15) {       // hit-test
                        // Only evidence nodes open a dialog for now
                        if (id.startsWith("E")) {
                            EvidenceDAO edao = new EvidenceDAO();
                            edao.find(ev -> ev.getId().equals(id))
                                .ifPresent(ev -> {
                                    Window w = SwingUtilities.getWindowAncestor(GraphPanel.this);
                                    new EvidenceDetailsDialog(
                                            w instanceof JFrame f ? f : null, ev
                                    ).setVisible(true);
                                });
                        }
                    }
                });
            }
        });
    }

    /* ------------------- API ------------------- */
    public void setGraph(Graph g) {
        this.g = g;
        repaint();
    }

    /* ------------------- paint ------------------- */
    @Override
    protected void paintComponent(Graphics g2) {
        super.paintComponent(g2);
        Graphics2D g = (Graphics2D) g2;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        List<String> nodes = this.g.nodes().stream().toList();
        int n = nodes.size();
        if (n == 0) return;

        /* ---- lay out nodes on a circle ---- */
        nodePositions.clear();
        for (int i = 0; i < n; i++) {
            double a = 2 * Math.PI * i / n;
            int x = (int) (w / 2 + 0.35 * w * Math.cos(a));
            int y = (int) (h / 2 + 0.35 * h * Math.sin(a));
            nodePositions.put(nodes.get(i), new Point(x, y));
        }

        /* ---- draw edges ---- */
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(0x888888));
        this.g.edges().forEach(e -> {
            Point a = nodePositions.get(e[0]), b = nodePositions.get(e[1]);
            g.drawLine(a.x, a.y, b.x, b.y);
        });

        /* ---- draw nodes + labels ---- */
        Font font = new Font("SansSerif", Font.BOLD, 12);
        g.setFont(font);
        nodes.forEach(id -> {
            Point p = nodePositions.get(id);
            Color fill;
            if (id.startsWith("E"))       fill = new Color(0x4FC3F7); // evidence = light-blue
            else if (id.startsWith("W"))  fill = new Color(0xA5D6A7); // witness  = green
            else                          fill = new Color(0xFFB74D); // suspect  = orange

            g.setColor(fill);
            g.fillOval(p.x - 14, p.y - 14, 28, 28);
            g.setColor(Color.BLACK);
            g.drawOval(p.x - 14, p.y - 14, 28, 28);
            g.drawString(id, p.x + 18, p.y + 4);
        });
    }
}
