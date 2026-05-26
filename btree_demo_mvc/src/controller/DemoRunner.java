package controller;

import model.IBTreeModel;
import view.IBTreeView;
import view.StatusType;

import javax.swing.*;
import java.util.List;

/**
 * Colaborador del controlador responsable de ejecutar el demo animado.
 *
 * <p>Inserta una secuencia predefinida de claves con una pausa entre
 * cada una, usando un {@link SwingWorker} para garantizar la seguridad
 * de hilos de Swing: el hilo de fondo solo duerme y publica valores,
 * mientras que {@code process()} los inserta en el modelo desde el EDT.</p>
 *
 * <p><strong>Principio de Responsabilidad Única (SRP):</strong>
 * Su única razón de cambio es la lógica y los valores del demo animado.</p>
 *
 * @version 1.0
 * @since   2026-I
 * @see     BTreeController
 * @see     InputHandler
 */
class DemoRunner {

    /** Modelo sobre el que se insertan los valores del demo. */
    private final IBTreeModel model;

    /** Vista que refleja cada inserción en tiempo real. */
    private final IBTreeView  view;

    /** Orden del árbol B, necesario para reiniciarlo al inicio del demo. */
    private final int         order;

    /**
     * Valores insertados durante el demo.
     * Producen splits frecuentes en un árbol B de orden 4.
     */
    private static final int[] DEMO_VALUES = {
            10, 20, 5, 6, 12, 30, 7, 17, 3, 25, 40, 15, 35, 50, 1, 8
    };

    /** Pausa en milisegundos entre cada inserción del demo. */
    private static final int DEMO_DELAY_MS = 480;

    /**
     * Construye el ejecutor del demo.
     *
     * @param model modelo del árbol B; no debe ser {@code null}
     * @param view  vista de la aplicación; no debe ser {@code null}
     * @param order orden del árbol B
     */
    DemoRunner(IBTreeModel model, IBTreeView view, int order) {
        this.model = model;
        this.view  = view;
        this.order = order;
    }

    /**
     * Inicia el demo animado.
     *
     * <p>Limpia el árbol, elimina cualquier resaltado y lanza
     * un {@link SwingWorker} que inserta los valores de
     * {@link #DEMO_VALUES} uno a uno con una pausa de
     * {@value #DEMO_DELAY_MS}&nbsp;ms entre cada inserción.</p>
     */
    void run() {
        model.clear(order);
        view.highlight(null);

        new SwingWorker<Void, Integer>() {

            /**
             * Hilo de fondo: publica cada valor con pausa intermedia.
             *
             * @return {@code null}
             * @throws InterruptedException si el hilo es interrumpido
             */
            @Override
            protected Void doInBackground() throws InterruptedException {
                for (int value : DEMO_VALUES) {
                    publish(value);
                    Thread.sleep(DEMO_DELAY_MS);
                }
                return null;
            }

            /**
             * EDT: inserta en el modelo cada valor publicado.
             *
             * @param chunks valores publicados desde el hilo de fondo
             */
            @Override
            protected void process(List<Integer> chunks) {
                for (int value : chunks) model.insert(value);
            }

            /** EDT: muestra el mensaje de finalización del demo. */
            @Override
            protected void done() {
                view.setStatus(
                        "Demo listo — " + model.getSize()
                                + " claves. ¡Prueba buscar o eliminar!",
                        StatusType.INFO
                );
            }

        }.execute();
    }
}
