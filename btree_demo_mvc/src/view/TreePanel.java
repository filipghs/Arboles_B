package view;

import model.BTree.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Componente Swing que dibuja el Árbol B de forma gráfica y recursiva.
 *
 * <p>Recalcula el layout del árbol en cada repintado, asignando a cada
 * subárbol un ancho proporcional a su contenido. Los nodos se representan
 * como rectángulos redondeados con sus claves y aristas hacia los hijos.</p>
 *
 * <p><strong>Principio de Responsabilidad Única (SRP):</strong>
 * Este panel tiene una única responsabilidad: <em>dibujar</em> el árbol.
 * No realiza operaciones sobre la estructura de datos y no conoce
 * al controlador.</p>
 *
 * @version 1.0
 * @since   2026-I
 */
public class TreePanel extends JPanel {

    // ── Paleta de colores (tema Dracula) ──────────────────────────────────────

    /** Color de fondo del panel. */
    private static final Color BG      = new Color(40,  42,  54);

    /** Color de fondo de cada nodo. */
    private static final Color NODE_BG = new Color(68,  71,  90);

    /** Color del borde de cada nodo. */
    private static final Color NODE_BD = new Color(98, 114, 164);

    /** Color del texto de las claves (modo normal). */
    private static final Color CYAN    = new Color(139, 233, 253);

    /** Color del texto y borde cuando una clave está resaltada. */
    private static final Color ORANGE  = new Color(255, 184,   0);

    /** Color de las aristas entre nodos. */
    private static final Color EDGE    = new Color(98, 114, 164);

    // ── Constantes de layout ──────────────────────────────────────────────────

    /** Ancho en píxeles de cada celda (una por clave) dentro de un nodo. */
    private static final int CELL_WIDTH  = 46;

    /** Alto en píxeles de cada nodo. */
    private static final int CELL_HEIGHT = 36;

    /** Separación vertical en píxeles entre niveles del árbol. */
    private static final int LEVEL_GAP   = 82;

    /** Padding mínimo horizontal en píxeles entre subárboles adyacentes. */
    private static final int H_PAD       = 20;

    // ── Estado ────────────────────────────────────────────────────────────────

    /** Raíz del árbol a dibujar; {@code null} indica árbol vacío. */
    private Node    root;

    /** Clave a resaltar visualmente; {@code null} indica sin resaltado. */
    private Integer highlight;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Crea el panel con fondo oscuro y tamaño preferido predeterminado.
     */
    public TreePanel() {
        setBackground(BG);
        setPreferredSize(new Dimension(900, 400));
    }

    // ── API pública ───────────────────────────────────────────────────────────

    /**
     * Actualiza la raíz del árbol y solicita un repintado.
     * Debe llamarse desde el Event Dispatch Thread (EDT).
     *
     * @param root nueva raíz; {@code null} muestra el mensaje de árbol vacío
     */
    public void setRoot(Node root) {
        this.root = root;
        repaint();
    }

    /**
     * Establece la clave a resaltar y solicita un repintado.
     * Debe llamarse desde el EDT.
     *
     * @param key clave a resaltar; {@code null} quita el resaltado actual
     */
    public void setHighlight(Integer key) {
        this.highlight = key;
        repaint();
    }

    // ── Pintado principal ─────────────────────────────────────────────────────

    /**
     * Punto de entrada del ciclo de pintado de Swing.
     * Activa el antialiasing y delega en los métodos de layout recursivo.
     *
     * @param g contexto gráfico proporcionado por Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (root == null || root.keyCount() == 0) {
            drawEmptyMessage(g2);
        } else {
            int totalWidth = subtreeWidth(root);
            int startX     = Math.max((getWidth() - totalWidth) / 2, 10);
            drawSubtree(g2, root, startX, 28, totalWidth);
        }

        g2.dispose();
    }

    // ── Layout recursivo ──────────────────────────────────────────────────────

    /**
     * Calcula el ancho total en píxeles necesario para el subárbol {@code n},
     * como el máximo entre el ancho del nodo raíz (con padding) y la suma
     * de los anchos de todos sus hijos.
     *
     * @param n raíz del subárbol
     * @return ancho necesario en píxeles
     */
    private int subtreeWidth(Node n) {
        int nodeW = nodeWidth(n) + H_PAD;
        if (n.leaf) return nodeW;
        int childrenW = 0;
        for (Node child : n.children) childrenW += subtreeWidth(child);
        return Math.max(nodeW, childrenW);
    }

    /**
     * Ancho en píxeles del rectángulo que representa el nodo {@code n}.
     *
     * @param n nodo a medir
     * @return {@code n.keyCount() * CELL_WIDTH + 2}
     */
    private int nodeWidth(Node n) {
        return n.keyCount() * CELL_WIDTH + 2;
    }

    /**
     * Dibuja el subárbol con raíz {@code n} dentro del área asignada,
     * de forma recursiva en profundidad (preorden).
     *
     * @param g2 contexto gráfico
     * @param n  raíz del subárbol a dibujar
     * @param x  coordenada x izquierda del área asignada
     * @param y  coordenada y superior del nodo
     * @param w  ancho total del área asignada
     * @return arreglo {@code [cx, cy]} con el centro del nodo dibujado
     */
    private int[] drawSubtree(Graphics2D g2, Node n, int x, int y, int w) {
        int nw  = nodeWidth(n);
        int nx  = x + (w - nw) / 2;
        int cxc = nx + nw / 2;

        paintNode(g2, n, nx, y);

        if (!n.leaf) {
            int[] childWidths = new int[n.children.size()];
            for (int i = 0; i < n.children.size(); i++)
                childWidths[i] = subtreeWidth(n.children.get(i));

            int childY = y + CELL_HEIGHT + LEVEL_GAP;
            int curX   = x;

            for (int i = 0; i < n.children.size(); i++) {
                int[] childCenter = drawSubtree(g2, n.children.get(i),
                        curX, childY, childWidths[i]);

                int px = nx + i * CELL_WIDTH + CELL_WIDTH / 2;
                if (i == 0)                         px = nx + 8;
                if (i == n.children.size() - 1)     px = nx + nw - 8;

                g2.setColor(EDGE);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
                g2.drawLine(px, y + CELL_HEIGHT, childCenter[0], childCenter[1]);

                curX += childWidths[i];
            }
        }

        return new int[]{ cxc, y + CELL_HEIGHT / 2 };
    }

    /**
     * Dibuja el rectángulo de un nodo con sus claves, separadores internos
     * y el resaltado opcional sobre la clave de interés.
     *
     * @param g2 contexto gráfico
     * @param n  nodo a dibujar
     * @param x  coordenada x izquierda del nodo
     * @param y  coordenada y superior del nodo
     */
    private void paintNode(Graphics2D g2, Node n, int x, int y) {
        int nw = nodeWidth(n);

        // Fondo y borde del nodo
        g2.setColor(NODE_BG);
        g2.fill(new RoundRectangle2D.Float(x, y, nw, CELL_HEIGHT, 10, 10));
        g2.setColor(NODE_BD);
        g2.setStroke(new BasicStroke(1.6f));
        g2.draw(new RoundRectangle2D.Float(x, y, nw, CELL_HEIGHT, 10, 10));

        Font        font = new Font("Monospaced", Font.BOLD, 15);
        FontMetrics fm   = g2.getFontMetrics(font);
        g2.setFont(font);

        for (int i = 0; i < n.keyCount(); i++) {
            int     key       = n.keys.get(i);
            int     cellX     = x + i * CELL_WIDTH + 1;
            boolean isHighlit = (highlight != null && key == highlight);

            if (isHighlit) {
                g2.setColor(new Color(255, 184, 0, 85));
                g2.fillRoundRect(cellX, y + 2, CELL_WIDTH - 1, CELL_HEIGHT - 4, 8, 8);
                g2.setColor(ORANGE);
                g2.setStroke(new BasicStroke(2.2f));
                g2.drawRoundRect(cellX, y + 2, CELL_WIDTH - 1, CELL_HEIGHT - 4, 8, 8);
                g2.setStroke(new BasicStroke(1.6f));
            }

            String keyStr = String.valueOf(key);
            g2.setColor(isHighlit ? ORANGE : CYAN);
            g2.drawString(keyStr,
                    cellX + (CELL_WIDTH - fm.stringWidth(keyStr)) / 2,
                    y + (CELL_HEIGHT + fm.getAscent() - fm.getDescent()) / 2);

            if (i < n.keyCount() - 1) {
                g2.setColor(NODE_BD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(cellX + CELL_WIDTH, y + 5,
                        cellX + CELL_WIDTH, y + CELL_HEIGHT - 5);
                g2.setStroke(new BasicStroke(1.6f));
            }
        }
    }

    /**
     * Dibuja un mensaje centrado cuando el árbol no tiene claves.
     *
     * @param g2 contexto gráfico
     */
    private void drawEmptyMessage(Graphics2D g2) {
        g2.setColor(NODE_BD);
        g2.setFont(new Font("SansSerif", Font.ITALIC, 15));
        String      msg = "Árbol vacío — presiona \"Demo\" o inserta un número.";
        FontMetrics fm  = g2.getFontMetrics();
        g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
    }
}