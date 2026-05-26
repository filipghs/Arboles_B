package controller;

import model.BTreeModel;
import model.IBTreeModel;
import view.IBTreeView;
import view.MainView;

/**
 * Controlador principal del visualizador de Árbol B.
 *
 * <p>Su responsabilidad es exclusivamente coordinar el ensamblaje de
 * las tres capas MVC. La lógica de cada flujo está delegada en
 * colaboradores especializados:</p>
 * <ul>
 *   <li>{@link InputHandler}      — callbacks de entrada del usuario.</li>
 *   <li>{@link ModelEventHandler} — traducción de eventos del modelo a la vista.</li>
 *   <li>{@link DemoRunner}        — ejecución del demo animado.</li>
 * </ul>
 *
 * <p>Expone dos constructores con propósitos distintos:</p>
 * <ul>
 *   <li>{@link #BTreeController(int)} — constructor de arranque: crea las
 *       clases concretas y hace visible la ventana. Permite que {@code Main}
 *       solo conozca al controlador.</li>
 *   <li>{@link #BTreeController(IBTreeModel, IBTreeView, int)} — constructor
 *       de inyección: recibe las dependencias a través de sus interfaces,
 *       facilitando las pruebas unitarias.</li>
 * </ul>
 *
 * <p><strong>Principio de Responsabilidad Única (SRP):</strong>
 * Solo coordina el ensamblaje; no contiene lógica de negocio
 * ni de presentación.</p>
 *
 * <p><strong>Principio de Inversión de Dependencias (DIP):</strong>
 * Opera internamente sobre {@link IBTreeModel} e {@link IBTreeView};
 * el acoplamiento con las clases concretas queda aislado en el
 * constructor de arranque.</p>
 * @version 1.0
 * @since   2026-I
 */
public class BTreeController {

    // ── Constructores ─────────────────────────────────────────────────────────

    /**
     * Constructor de arranque.
     *
     * <p>Instancia las clases concretas {@link BTreeModel} y {@link MainView},
     * delega en el constructor de inyección para el registro de colaboradores
     * y, finalmente, hace visible la ventana.</p>
     *
     * <p>Este es el único lugar del sistema donde se instancian clases
     * concretas, permitiendo que {@code Main} opere exclusivamente
     * con el controlador.</p>
     *
     * @param order orden del Árbol B ({@code order >= 3})
     */
    public BTreeController(int order) {
        IBTreeModel model = new BTreeModel(order);
        IBTreeView  view  = new MainView(order);
        assemble(model, view, order);
        view.display();
    }

    /**
     * Constructor de inyección de dependencias.
     *
     * <p>Recibe el modelo y la vista a través de sus interfaces y
     * delega en {@link #assemble} para registrar los colaboradores
     * y solicitar el primer renderizado.</p>
     *
     * @param model modelo del árbol B; no debe ser {@code null}
     * @param view  vista de la aplicación; no debe ser {@code null}
     * @param order orden del árbol B
     */
    public BTreeController(IBTreeModel model, IBTreeView view, int order) {
        assemble(model, view, order);
    }

    // ── Ensamblaje ────────────────────────────────────────────────────────────

    /**
     * Crea y conecta los colaboradores del controlador.
     *
     * <p>Orden de registro:</p>
     * <ol>
     *   <li>{@link InputHandler}      — registra los callbacks de usuario en la vista.</li>
     *   <li>{@link ModelEventHandler} — suscribe el observador al modelo.</li>
     * </ol>
     * <p>Tras el registro se solicita el primer renderizado del árbol.</p>
     *
     * @param model modelo del árbol B
     * @param view  vista de la aplicación
     * @param order orden del árbol B
     */
    private void assemble(IBTreeModel model, IBTreeView view, int order) {
        new InputHandler(model, view, order);
        new ModelEventHandler(model, view);
        view.updateTree(model.getRoot());
    }
}
