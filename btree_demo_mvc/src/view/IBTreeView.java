package view;

import model.BTree;

/**
 * Contrato de alto nivel de la vista para el visualizador de Árbol B.
 *
 * <p><strong>Principio de Inversión de Dependencias (DIP):</strong>
 * El {@code BTreeController} depende de <em>esta</em> abstracción, nunca
 * de la clase concreta {@link MainView}. Cambiar la tecnología de la
 * interfaz gráfica (Swing → JavaFX, consola, web) solo requiere crear
 * una nueva implementación; el controlador no necesita modificarse.</p>
 *
 * <p><strong>Principio de Segregación de Interfaces (ISP):</strong>
 * Los métodos se agrupan en tres responsabilidades bien delimitadas:
 * registro de callbacks, lectura de entrada y actualización de la presentación.</p>
 *
 * <p><strong>Principio de Sustitución de Liskov (LSP):</strong>
 * Cualquier implementación puede reemplazar a {@link MainView}
 * sin alterar el comportamiento del controlador.</p>
 *
 * @version 1.0
 * @since   2026-I
 * @see     MainView
 * @see     StatusType
 */
public interface IBTreeView {

    // ── Registro de callbacks ─────────────────────────────────────────────────

    /**
     * Registra el manejador que se ejecuta cuando el usuario solicita
     * una inserción (botón o tecla Enter).
     *
     * @param handler acción a invocar; no debe ser {@code null}
     */
    void onInsert(Runnable handler);

    /**
     * Registra el manejador que se ejecuta cuando el usuario solicita
     * una búsqueda.
     *
     * @param handler acción a invocar; no debe ser {@code null}
     */
    void onSearch(Runnable handler);

    /**
     * Registra el manejador que se ejecuta cuando el usuario solicita
     * la eliminación de una clave.
     *
     * @param handler acción a invocar; no debe ser {@code null}
     */
    void onDelete(Runnable handler);

    /**
     * Registra el manejador que se ejecuta cuando el usuario limpia el árbol.
     *
     * @param handler acción a invocar; no debe ser {@code null}
     */
    void onClear(Runnable handler);

    /**
     * Registra el manejador que se ejecuta cuando el usuario inicia el demo.
     *
     * @param handler acción a invocar; no debe ser {@code null}
     */
    void onDemo(Runnable handler);

    // ── Lectura de entrada ───────────────────────────────────────────────────

    /**
     * Obtiene el texto ingresado por el usuario, sin espacios en los extremos.
     *
     * @return texto de entrada; nunca {@code null}
     */
    String getInput();

    /**
     * Borra el campo de entrada y devuelve el foco al mismo.
     */
    void clearInput();

    // ── Actualización de la presentación ─────────────────────────────────────

    /**
     * Reemplaza el árbol visualizado por el que tiene la raíz indicada.
     *
     * @param root nueva raíz del árbol B a dibujar
     */
    void updateTree(BTree.Node root);

    /**
     * Resalta visualmente la clave especificada en el árbol dibujado.
     *
     * @param key clave a resaltar; {@code null} elimina cualquier resaltado
     */
    void highlight(Integer key);

    /**
     * Muestra un mensaje de estado con el estilo del tipo semántico dado.
     *
     * @param message texto del mensaje; no debe ser {@code null}
     * @param type    categoría semántica del mensaje
     */
    void setStatus(String message, StatusType type);

    /** Hace visible la ventana principal de la aplicación. */
    void display();
}
