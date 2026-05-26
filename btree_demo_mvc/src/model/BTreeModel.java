package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo MVC del visualizador de Árbol B.
 *
 * <p>Encapsula el {@link BTree} (estructura de datos pura) y notifica
 * a todos los observadores registrados cada vez que el estado cambia.
 * La vista y el controlador <strong>nunca</strong> manipulan el
 * {@link BTree} directamente.</p>
 *
 * <p><strong>Principio de Responsabilidad Única (SRP):</strong>
 * Gestiona el estado del árbol y notifica a los observadores.
 * No conoce la vista ni el controlador.</p>
 *
 * <p><strong>Principio Abierto/Cerrado (OCP):</strong>
 * Nuevos observadores pueden registrarse sin modificar esta clase.</p>
 *
 * <p><strong>Principio de Inversión de Dependencias (DIP):</strong>
 * Implementa {@link IBTreeModel}; el controlador depende de la interfaz,
 * no de esta clase concreta.</p>
 *
 * <p>Implementa el patrón de diseño <em>Observer</em> a través de
 * {@link BTreeListener}.</p>
 *
 * @version 1.0
 * @since   2026-I
 * @see     BTree
 * @see     IBTreeModel
 * @see     BTreeListener
 */
public class BTreeModel implements IBTreeModel {

    // ── Estado interno ───────────────────────────────────────────────────────

    /**
     * Árbol B encapsulado.
     * Solo esta clase lo manipula directamente.
     */
    private BTree tree;

    /**
     * Lista de observadores registrados (patrón Observer).
     * Se notifican en el orden en que fueron añadidos.
     */
    private final List<BTreeListener> listeners = new ArrayList<>();

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Crea un modelo con un Árbol B vacío de orden {@code order}.
     *
     * @param order orden {@code m} del árbol B ({@code order >= 3})
     */
    public BTreeModel(int order) {
        this.tree = new BTree(order);
    }

    // ── Patrón Observer ──────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(BTreeListener listener) {
        if (listener == null) throw new IllegalArgumentException("El listener no puede ser null.");
        listeners.add(listener);
    }

    /**
     * Distribuye el evento a todos los observadores registrados.
     *
     * @param event evento a distribuir
     */
    private void emit(TreeEvent event) {
        listeners.forEach(l -> l.onEvent(event));
    }

    // ── Operaciones del modelo ───────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * @implNote Delega en {@link BTree#insert(int)} y emite
     *           {@link EventType#INSERTED} o {@link EventType#ALREADY_EXISTS}.
     */
    @Override
    public void insert(int key) {
        if (tree.insert(key)) {
            emit(new TreeEvent(EventType.INSERTED, key,
                    "✔  " + key + " insertado. Total: " + tree.getSize()));
        } else {
            emit(new TreeEvent(EventType.ALREADY_EXISTS, key,
                    "⚠  " + key + " ya existe en el árbol."));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Delega en {@link BTree#search(int)} y emite
     *           {@link EventType#FOUND} o {@link EventType#NOT_FOUND}.
     */
    @Override
    public void search(int key) {
        if (tree.search(key)) {
            emit(new TreeEvent(EventType.FOUND, key,
                    "✔  " + key + " encontrado en el árbol."));
        } else {
            emit(new TreeEvent(EventType.NOT_FOUND, key,
                    "✘  " + key + " NO está en el árbol."));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Delega en {@link BTree#delete(int)} y emite
     *           {@link EventType#DELETED} o {@link EventType#NOT_FOUND_DELETE}.
     */
    @Override
    public void delete(int key) {
        if (tree.delete(key)) {
            emit(new TreeEvent(EventType.DELETED, key,
                    "✔  " + key + " eliminado. Total: " + tree.getSize()));
        } else {
            emit(new TreeEvent(EventType.NOT_FOUND_DELETE, key,
                    "✘  " + key + " NO está en el árbol."));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Crea un nuevo {@link BTree}; el anterior es liberado por el GC.
     */
    @Override
    public void clear(int order) {
        this.tree = new BTree(order);
        emit(new TreeEvent(EventType.CLEARED, -1, "Árbol limpiado."));
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public BTree.Node getRoot()  { return tree.getRoot();  }

    /** {@inheritDoc} */
    @Override
    public int getSize()         { return tree.getSize();  }

    /** {@inheritDoc} */
    @Override
    public int getOrder()        { return tree.getOrder(); }
}
