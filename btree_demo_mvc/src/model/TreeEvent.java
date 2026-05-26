package model;

/**
 * Objeto de Transferencia de Datos (DTO) <em>inmutable</em> que encapsula
 * la información de un evento producido por el {@link BTreeModel}.
 *
 * <p><strong>Principio de Responsabilidad Única (SRP):</strong>
 * Esta clase solo transporta datos entre capas; no contiene lógica
 * de negocio, validación ni presentación.</p>
 *
 * <p>La inmutabilidad (todos los campos son {@code final}) garantiza
 * que los observadores reciban un estado consistente y no puedan
 * modificarlo accidentalmente.</p>
 *
 * @version 1.0
 * @since   2026-I
 * @see     EventType
 * @see     BTreeListener
 */
public final class TreeEvent {

    /** Tipo de evento que describe la operación realizada. */
    public final EventType type;

    /**
     * Clave involucrada en la operación.
     * Toma el valor {@code -1} cuando no aplica (p.ej. {@link EventType#CLEARED}).
     */
    public final int key;

    /** Mensaje descriptivo listo para mostrar al usuario. */
    public final String message;

    /**
     * Construye un evento completamente inicializado.
     *
     * @param type    tipo del evento; no debe ser {@code null}
     * @param key     clave involucrada, o {@code -1} si no aplica
     * @param message mensaje para el usuario; no debe ser {@code null}
     */
    public TreeEvent(EventType type, int key, String message) {
        this.type    = type;
        this.key     = key;
        this.message = message;
    }

    /**
     * Representación textual del evento, útil para depuración.
     *
     * @return cadena con tipo, clave y mensaje
     */
    @Override
    public String toString() {
        return String.format("TreeEvent{type=%s, key=%d, message='%s'}",
                type, key, message);
    }
}