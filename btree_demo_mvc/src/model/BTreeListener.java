package model;

/**
 * Interfaz de observador del patrón <em>Observer</em> aplicado al modelo.
 *
 * <p><strong>Principio Abierto/Cerrado (OCP):</strong>
 * Cualquier componente puede suscribirse al {@link BTreeModel} sin
 * necesidad de modificarlo. El modelo está <em>cerrado</em> a la
 * modificación y <em>abierto</em> a la extensión mediante esta interfaz.</p>
 *
 * <p><strong>Principio de Segregación de Interfaces (ISP):</strong>
 * La interfaz es mínima; declara un único método, evitando forzar
 * implementaciones vacías en los suscriptores.</p>
 *
 * <p>Al ser {@code @FunctionalInterface} puede usarse con lambdas:</p>
 * <pre>{@code
 * model.addListener(event -> System.out.println(event.message));
 * }</pre>
 *
 * @version 1.0
 * @since   2026-I
 * @see     TreeEvent
 * @see     BTreeModel
 */
@FunctionalInterface
public interface BTreeListener {

    /**
     * Invocado cada vez que el modelo modifica su estado.
     *
     * @param event objeto que describe el cambio producido; nunca {@code null}
     */
    void onEvent(TreeEvent event);
}
