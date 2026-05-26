package model;

/**
 * Tipos de eventos que el {@link BTreeModel} emite a sus observadores.
 *
 * <p><strong>Principio de Responsabilidad Única (SRP):</strong>
 * Esta enumeración tiene una sola razón de cambio: el vocabulario de
 * eventos del dominio. No mezcla lógica de negocio, presentación
 * ni persistencia.</p>
 *
 * @version 1.0
 * @since   2026-I
 */
public enum EventType {

    /** La clave fue insertada correctamente en el árbol. */
    INSERTED,

    /** La clave ya existía; no se realizó ninguna inserción. */
    ALREADY_EXISTS,

    /** La clave fue hallada durante una operación de búsqueda. */
    FOUND,

    /** La clave no existe en el árbol (operación de búsqueda). */
    NOT_FOUND,

    /** La clave fue eliminada correctamente del árbol. */
    DELETED,

    /** La clave no existe en el árbol (intento de eliminación). */
    NOT_FOUND_DELETE,

    /** El árbol fue reiniciado y vaciado completamente. */
    CLEARED
}