import controller.BTreeController;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la aplicación «Visualizador de Árbol B».
 *
 * @version 1.0
 * @since   2026-I
 * @see     BTreeController
 */
public class Main {

    /**
     * Orden del Árbol B.
     * Con {@code ORDER = 4}: máximo 3 claves y 4 hijos por nodo.
     */
    private static final int ORDER = 4;

    /**
     * Punto de entrada de la aplicación.
     *
     * <p>La construcción se delega dentro de
     * {@link SwingUtilities#invokeLater} para garantizar que el
     * Event Dispatch Thread (EDT) gestione los componentes gráficos
     * desde el primer instante, cumpliendo con el modelo de hilos de Swing.</p>
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BTreeController(ORDER));
    }
}
