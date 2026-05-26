package controller;

import model.IBTreeModel;
import view.IBTreeView;
import view.StatusType;

import java.util.Optional;

/**
 * Colaborador del controlador responsable de los callbacks de entrada.
 *
 * <p>Registra en la vista los manejadores para cada acción del usuario
 * (insertar, buscar, eliminar, limpiar y demo) y centraliza la
 * validación del campo de texto en {@link #parseInput()}.</p>
 *
 * <p><strong>Principio de Responsabilidad Única (SRP):</strong>
 * Su única razón de cambio es la forma en que se procesan las
 * acciones del usuario sobre el árbol.</p>
 *
 * @version 1.0
 * @since   2026-I
 * @see     BTreeController
 * @see     DemoRunner
 */
class InputHandler {

    /** Modelo; operado siempre a través de su interfaz (DIP). */
    private final IBTreeModel model;

    /** Vista; operada siempre a través de su interfaz (DIP). */
    private final IBTreeView  view;

    /** Orden del árbol B, necesario para reiniciarlo en la acción «Limpiar». */
    private final int         order;

    /**
     * Construye el manejador de entrada y registra inmediatamente
     * todos los callbacks en la vista.
     *
     * @param model modelo del árbol B; no debe ser {@code null}
     * @param view  vista de la aplicación; no debe ser {@code null}
     * @param order orden del árbol B
     */
    InputHandler(IBTreeModel model, IBTreeView view, int order) {
        this.model = model;
        this.view  = view;
        this.order = order;
        registerCallbacks();
    }

    /**
     * Registra en la vista un manejador por cada acción del usuario.
     * La acción «Demo» delega en un {@link DemoRunner} dedicado.
     */
    private void registerCallbacks() {
        view.onInsert(() -> parseInput().ifPresent(model::insert));
        view.onSearch(() -> parseInput().ifPresent(model::search));
        view.onDelete(() -> parseInput().ifPresent(model::delete));
        view.onClear(this::handleClear);
        view.onDemo(new DemoRunner(model, view, order)::run);
    }

    /**
     * Reinicia el árbol y elimina cualquier resaltado de la vista.
     */
    private void handleClear() {
        model.clear(order);
        view.highlight(null);
    }

    /**
     * Intenta parsear el texto ingresado en la vista como entero.
     *
     * <p>Si el texto no es un entero válido, muestra un mensaje de
     * error en la vista y devuelve {@link Optional#empty()} para
     * detener la cadena de llamada al modelo.</p>
     *
     * @return {@code Optional} con el entero parseado, o vacío si es inválido
     */
    private Optional<Integer> parseInput() {
        try {
            int value = Integer.parseInt(view.getInput());
            view.clearInput();
            return Optional.of(value);
        } catch (NumberFormatException e) {
            view.setStatus("✘  Ingresa un número entero válido.", StatusType.ERROR);
            return Optional.empty();
        }
    }
}
