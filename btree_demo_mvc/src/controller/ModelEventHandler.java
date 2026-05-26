package controller;

import model.IBTreeModel;
import model.TreeEvent;
import view.IBTreeView;
import view.StatusType;

/**
 * Colaborador del controlador responsable de observar el modelo
 * y reflejar sus eventos en la vista.
 *
 * <p>Se suscribe al modelo como observador (patrón <em>Observer</em>)
 * y traduce cada {@link TreeEvent} en las llamadas apropiadas sobre
 * la vista: resaltado de clave, mensaje de estado y redibujado del árbol.</p>
 *
 * <p><strong>Principio de Responsabilidad Única (SRP):</strong>
 * Su única razón de cambio es la forma en que los eventos del modelo
 * se traducen a actualizaciones de la vista.</p>
 *
 * <p><strong>Principio Abierto/Cerrado (OCP):</strong>
 * Nuevos tipos de evento se añaden al switch en {@link #handleEvent}
 * sin modificar el resto del sistema.</p>
 *
 * @version 1.0
 * @since   2026-I
 * @see     BTreeController
 */
class ModelEventHandler {

    /** Modelo; necesario para obtener la raíz tras cada evento. */
    private final IBTreeModel model;

    /** Vista; operada siempre a través de su interfaz (DIP). */
    private final IBTreeView  view;

    /**
     * Construye el manejador y se suscribe inmediatamente al modelo.
     *
     * @param model modelo del árbol B; no debe ser {@code null}
     * @param view  vista de la aplicación; no debe ser {@code null}
     */
    ModelEventHandler(IBTreeModel model, IBTreeView view) {
        this.model = model;
        this.view  = view;
        model.addListener(this::handleEvent);
    }

    /**
     * Traduce un evento del modelo en actualizaciones concretas de la vista.
     *
     * <p>Cada tipo de evento determina si se resalta una clave y qué
     * categoría de mensaje se muestra. Al finalizar siempre solicita
     * el redibujado del árbol.</p>
     *
     * @param event evento emitido por el modelo; nunca {@code null}
     */
    private void handleEvent(TreeEvent event) {
        switch (event.type) {
            case INSERTED         -> { view.highlight(event.key); view.setStatus(event.message, StatusType.SUCCESS); }
            case ALREADY_EXISTS   -> {                            view.setStatus(event.message, StatusType.WARNING); }
            case FOUND            -> { view.highlight(event.key); view.setStatus(event.message, StatusType.SUCCESS); }
            case NOT_FOUND        -> { view.highlight(null);      view.setStatus(event.message, StatusType.ERROR);   }
            case DELETED          -> { view.highlight(null);      view.setStatus(event.message, StatusType.SUCCESS); }
            case NOT_FOUND_DELETE -> {                            view.setStatus(event.message, StatusType.ERROR);   }
            case CLEARED          -> {                            view.setStatus(event.message, StatusType.SPECIAL); }
        }
        view.updateTree(model.getRoot());
    }
}