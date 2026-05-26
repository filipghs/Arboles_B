package view;

/**
 * Categorías semánticas de los mensajes de estado de la vista.
 *
 * <p><strong>Principio de Inversión de Dependencias (DIP):</strong>
 * El controlador usa esta enumeración para indicar el <em>tipo semántico</em>
 * del mensaje sin conocer colores, fuentes ni detalles gráficos de Swing.
 * La vista decide cómo renderizar cada categoría visualmente.</p>
 *
 * <p>Cambiar el tema visual (p.ej. Dracula → modo claro) solo requiere
 * modificar el mapa de colores en {@link MainView}, sin tocar el controlador.</p>
 *
 * @version 1.0
 * @since   2026-I
 * @see     IBTreeView#setStatus(String, StatusType)
 */
public enum StatusType {

    /** Operación exitosa — en Dracula se representa en verde. */
    SUCCESS,

    /** Error o elemento no encontrado — se representa en rojo. */
    ERROR,

    /** Advertencia (p.ej. clave duplicada) — se representa en naranja. */
    WARNING,

    /** Mensaje informativo general (p.ej. demo completado) — en cian. */
    INFO,

    /** Acción especial (p.ej. árbol limpiado) — se representa en púrpura. */
    SPECIAL
}
