package view;

import model.BTree;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Ventana principal de la aplicación — implementación Swing de {@link IBTreeView}.
 *
 * <p>Construye todos los componentes gráficos y expone la API definida en
 * {@link IBTreeView} para que el controlador interactúe con la interfaz
 * sin conocer ningún detalle de Swing.</p>
 *
 * <p><strong>Principio de Responsabilidad Única (SRP):</strong>
 * Solo construye y gestiona la interfaz gráfica; no contiene lógica de negocio.</p>
 *
 * <p><strong>Principio de Sustitución de Liskov (LSP):</strong>
 * Como implementación de {@link IBTreeView}, puede sustituirse por cualquier
 * otra implementación sin que el controlador lo note.</p>
 *
 * <p><strong>Principio de Inversión de Dependencias (DIP):</strong>
 * El controlador solo referencia {@link IBTreeView}; esta clase concreta
 * es invisible para él.</p>
 *
 * @version 1.0
 * @since   2026-I
 * @see     IBTreeView
 * @see     TreePanel
 */
public class MainView extends JFrame implements IBTreeView {

    // ── Paleta de colores (tema Dracula) ──────────────────────────────────────

    private static final Color BG      = new Color(40,  42,  54);
    private static final Color SURFACE = new Color(55,  58,  71);
    private static final Color FG      = new Color(248, 248, 242);
    private static final Color CYAN    = new Color(139, 233, 253);
    private static final Color GREEN   = new Color( 80, 250, 123);
    private static final Color RED     = new Color(255,  85,  85);
    private static final Color ORANGE  = new Color(255, 184,   0);
    private static final Color PURPLE  = new Color(189, 147, 249);
    private static final Color BORDER  = new Color( 98, 114, 164);

    /**
     * Mapa que relaciona cada {@link StatusType} con su color de presentación.
     * Centraliza todas las decisiones de color en un único lugar de la vista.
     */
    private static final Map<StatusType, Color> STATUS_COLORS = Map.of(
            StatusType.SUCCESS, GREEN,
            StatusType.ERROR,   RED,
            StatusType.WARNING, ORANGE,
            StatusType.INFO,    CYAN,
            StatusType.SPECIAL, PURPLE
    );

    // ── Componentes gráficos ──────────────────────────────────────────────────

    /** Panel que dibuja el árbol B. */
    private final TreePanel  treePanel = new TreePanel();

    /** Campo de texto para ingresar claves. */
    private final JTextField input     = buildInputField();

    /** Etiqueta de estado en la parte inferior de la ventana. */
    private final JLabel     lblStatus = buildStatusLabel();

    private final JButton btnInsert = buildButton("Insertar",    GREEN);
    private final JButton btnSearch = buildButton("Buscar",      ORANGE);
    private final JButton btnDelete = buildButton("Eliminar",    RED);
    private final JButton btnClear  = buildButton("Limpiar",     PURPLE);
    private final JButton btnDemo   = buildButton("Demo rápido", CYAN);

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Construye la ventana principal.
     *
     * @param order orden del árbol B (solo para el título informativo)
     */
    public MainView(int order) {
        super("Árbol B  —  Demo MVC  [orden " + order + "]");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildHeader(order), BorderLayout.NORTH);
        add(buildCenter(),      BorderLayout.CENTER);
        add(buildBottom(),      BorderLayout.SOUTH);
    }

    // ── Construcción de paneles ───────────────────────────────────────────────

    /**
     * Construye el panel de encabezado con el título de la aplicación.
     *
     * @param order orden del árbol B para el texto informativo
     * @return panel de encabezado configurado
     */
    private JPanel buildHeader(int order) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(30, 31, 41));
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel title = new JLabel(
                "Árbol B — Demostración MVC   (orden " + order
                        + ",  máx. " + (order - 1) + " claves por nodo)"
        );
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(CYAN);
        panel.add(title);
        return panel;
    }

    /**
     * Construye el panel central con el área de visualización del árbol.
     *
     * @return scroll pane que envuelve al {@link TreePanel}
     */
    private JScrollPane buildCenter() {
        JScrollPane sp = new JScrollPane(treePanel);
        sp.setBackground(BG);
        sp.getViewport().setBackground(BG);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return sp;
    }

    /**
     * Construye el panel inferior con controles y etiqueta de estado.
     *
     * @return panel inferior configurado
     */
    private JPanel buildBottom() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(SURFACE);
        outer.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        controls.setBackground(SURFACE);
        controls.add(buildLabel("Clave:"));
        controls.add(input);
        controls.add(btnInsert);
        controls.add(btnSearch);
        controls.add(btnDelete);
        controls.add(buildVerticalSeparator());
        controls.add(btnClear);
        controls.add(btnDemo);

        lblStatus.setBorder(new EmptyBorder(6, 0, 0, 0));
        lblStatus.setHorizontalAlignment(JLabel.CENTER);

        outer.add(controls,  BorderLayout.CENTER);
        outer.add(lblStatus, BorderLayout.SOUTH);
        return outer;
    }

    // ── Implementación: callbacks ─────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * También registra la tecla Enter en el campo de texto.
     */
    @Override
    public void onInsert(Runnable handler) {
        btnInsert.addActionListener(e -> handler.run());
        input.addActionListener(e -> handler.run());
    }

    /** {@inheritDoc} */
    @Override
    public void onSearch(Runnable handler) { btnSearch.addActionListener(e -> handler.run()); }

    /** {@inheritDoc} */
    @Override
    public void onDelete(Runnable handler) { btnDelete.addActionListener(e -> handler.run()); }

    /** {@inheritDoc} */
    @Override
    public void onClear(Runnable handler)  { btnClear .addActionListener(e -> handler.run()); }

    /** {@inheritDoc} */
    @Override
    public void onDemo(Runnable handler)   { btnDemo  .addActionListener(e -> handler.run()); }

    // ── Implementación: entrada ───────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public String getInput() { return input.getText().trim(); }

    /** {@inheritDoc} */
    @Override
    public void clearInput() { input.setText(""); input.requestFocus(); }

    // ── Implementación: actualización ────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public void updateTree(BTree.Node root) { treePanel.setRoot(root); }

    /** {@inheritDoc} */
    @Override
    public void highlight(Integer key) { treePanel.setHighlight(key); }

    /**
     * {@inheritDoc}
     * El color se obtiene de {@link #STATUS_COLORS}; blanco como fallback.
     */
    @Override
    public void setStatus(String message, StatusType type) {
        lblStatus.setText(message);
        lblStatus.setForeground(STATUS_COLORS.getOrDefault(type, Color.WHITE));
    }

    /** {@inheritDoc} */
    @Override
    public void display() {
        setVisible(true);
    }

    // ── Métodos auxiliares de construcción ────────────────────────────────────

    /**
     * Crea el campo de texto de entrada con el estilo visual de la aplicación.
     *
     * @return campo de texto estilizado
     */
    private static JTextField buildInputField() {
        JTextField field = new JTextField(6);
        field.setBackground(new Color(68, 71, 90));
        field.setForeground(FG);
        field.setCaretColor(CYAN);
        field.setFont(new Font("Monospaced", Font.BOLD, 17));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return field;
    }

    /**
     * Crea un botón estilizado con texto y color de fondo indicados.
     *
     * @param text  texto visible en el botón
     * @param color color de fondo
     * @return botón configurado
     */
    private static JButton buildButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(new Color(40, 42, 54));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(7, 16, 7, 16));
        return button;
    }

    /**
     * Crea la etiqueta de estado con el estilo visual de la aplicación.
     *
     * @return etiqueta de estado con texto vacío inicial
     */
    private static JLabel buildStatusLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(FG);
        return label;
    }

    /**
     * Crea una etiqueta de texto con el estilo visual de la aplicación.
     *
     * @param text texto de la etiqueta
     * @return etiqueta configurada
     */
    private static JLabel buildLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(FG);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }

    /**
     * Crea un separador vertical decorativo para el panel de controles.
     *
     * @return separador vertical estilizado
     */
    private static JSeparator buildVerticalSeparator() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 30));
        sep.setForeground(BORDER);
        return sep;
    }
}
