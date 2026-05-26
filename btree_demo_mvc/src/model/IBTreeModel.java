package model;

/**
 * Contrato de alto nivel que expone las operaciones del modelo del Árbol B.
 *
 * <p><strong>Principio de Inversión de Dependencias (DIP):</strong>
 * Él {@code BTreeController} depende de <em>esta</em> abstracción, nunca
 * de la clase concreta {@link BTreeModel}. Permite sustituir la
 * implementación (p.ej. Árbol B+) sin tocar el controlador.</p>
 *
 * <p><strong>Principio de Segregación de Interfaces (ISP):</strong>
 * Expone únicamente las operaciones que el controlador necesita,
 * sin imponer métodos internos de la implementación.</p>
 *
 * <p><strong>Principio de Sustitución de Liskov (LSP):</strong>
 * Toda clase que implemente esta interfaz debe poder reemplazar a
 * {@link BTreeModel} sin alterar el comportamiento observable del sistema.</p>
 *
 * @version 1.0
 * @since   2026-I
 * @see     BTreeModel
 */
public interface IBTreeModel {

    /**
     * Registra un observador que será notificado ante cualquier cambio de estado.
     *
     * @param listener observador a registrar; no debe ser {@code null}
     */
    void addListener(BTreeListener listener);

    /**
     * Intenta insertar la clave en el árbol.
     * Emite {@link EventType#INSERTED} o {@link EventType#ALREADY_EXISTS}.
     *
     * @param key clave entera a insertar
     */
    void insert(int key);

    /**
     * Busca la clave en el árbol.
     * Emite {@link EventType#FOUND} o {@link EventType#NOT_FOUND}.
     *
     * @param key clave a buscar
     */
    void search(int key);

    /**
     * Intenta eliminar la clave del árbol.
     * Emite {@link EventType#DELETED} o {@link EventType#NOT_FOUND_DELETE}.
     *
     * @param key clave a eliminar
     */
    void delete(int key);

    /**
     * Descarta el árbol actual y crea uno nuevo vacío.
     * Emite {@link EventType#CLEARED}.
     *
     * @param order orden {@code m} del nuevo Árbol B ({@code m >= 3})
     */
    void clear(int order);

    /**
     * Devuelve la raíz del árbol para su renderizado en la vista.
     *
     * @return nodo raíz; nunca {@code null}
     */
    BTree.Node getRoot();

    /**
     * Devuelve el número total de claves almacenadas.
     *
     * @return cantidad de claves ({@code >= 0})
     */
    int getSize();

    /**
     * Devuelve el orden del Árbol B.
     *
     * @return {@code m}, número máximo de hijos por nodo
     */
    int getOrder();
}
