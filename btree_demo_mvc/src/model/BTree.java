package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de un Árbol B de orden {@code m}.
 *
 * <h2>Propiedades invariantes</h2>
 * <ul>
 *   <li>Cada nodo tiene como máximo {@code m - 1} claves y {@code m} hijos.</li>
 *   <li>Cada nodo interno (excepto la raíz) tiene al menos
 *       {@code t - 1} claves, donde {@code t = ceil(m/2)}.</li>
 *   <li>La raíz tiene al menos 1 clave (salvo árbol vacío).</li>
 *   <li>Todas las hojas están al mismo nivel.</li>
 *   <li>Las claves de cada nodo están ordenadas de menor a mayor.</li>
 * </ul>
 *
 * <p><strong>Principio de Responsabilidad Única (SRP):</strong>
 * Esta clase es una <em>estructura de datos pura</em>. Su única responsabilidad
 * es implementar las operaciones del Árbol B. No conoce la vista, el
 * controlador ni el modelo observable {@link BTreeModel}.</p>
 *
 * @version 1.0
 * @since   2026-I
 */
public class BTree {

    // ════════════════════════════════════════════════════════════════════════
    // Nodo del árbol
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Nodo de un Árbol B.
     *
     * <p>Almacena una lista ordenada de claves y, si no es hoja,
     * referencias a sus nodos hijo. Se expone como clase estática pública
     * para que {@code TreePanel} pueda recorrer el árbol al dibujarlo.</p>
     */
    public static class Node {

        /** Lista ordenada de claves almacenadas en este nodo. */
        public List<Integer> keys     = new ArrayList<>();

        /** Lista de nodos hijo (vacía si {@link #leaf} es {@code true}). */
        public List<Node>    children = new ArrayList<>();

        /** {@code true} si el nodo es una hoja (no tiene hijos). */
        public boolean       leaf     = true;

        /**
         * Devuelve el número de claves almacenadas en este nodo.
         *
         * @return tamaño de la lista {@link #keys}
         */
        public int keyCount() { return keys.size(); }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Estado interno
    // ════════════════════════════════════════════════════════════════════════

    /** Orden del árbol: número máximo de hijos por nodo. */
    private final int  m;

    /**
     * Grado mínimo: {@code t = ceil(m/2)}.
     * Todo nodo interno (salvo la raíz) debe tener al menos {@code t - 1} claves.
     */
    private final int  t;

    /** Nodo raíz; nunca {@code null}. */
    private       Node root;

    /** Número total de claves almacenadas en el árbol. */
    private       int  size;

    // ════════════════════════════════════════════════════════════════════════
    // Constructor
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Crea un Árbol B vacío de orden {@code m}.
     *
     * @param m orden del árbol ({@code m >= 3})
     * @throws IllegalArgumentException si {@code m < 3}
     */
    public BTree(int m) {
        if (m < 3) throw new IllegalArgumentException(
                "El orden del Árbol B debe ser >= 3. Recibido: " + m);
        this.m    = m;
        this.t    = (int) Math.ceil(m / 2.0);
        this.root = new Node();
    }

    // ════════════════════════════════════════════════════════════════════════
    // BÚSQUEDA
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Determina si la clave existe en el árbol.
     *
     * <p>Complejidad temporal: {@code O(log_t(n) * log(m))}.</p>
     *
     * @param key clave a buscar
     * @return {@code true} si la clave existe; {@code false} en caso contrario
     */
    public boolean search(int key) {
        return searchNode(root, key);
    }

    /**
     * Búsqueda recursiva desde el nodo {@code n}.
     * Avanza linealmente hasta una clave >= key y desciende al hijo apropiado.
     *
     * @param n   nodo desde el que se busca
     * @param key clave objetivo
     * @return {@code true} si la clave es encontrada en el subárbol
     */
    private boolean searchNode(Node n, int key) {
        int i = 0;
        while (i < n.keyCount() && key > n.keys.get(i)) i++;
        if (i < n.keyCount() && key == n.keys.get(i)) return true;
        if (n.leaf) return false;
        return searchNode(n.children.get(i), key);
    }

    // ════════════════════════════════════════════════════════════════════════
    // INSERCIÓN
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Inserta la clave en el árbol si no existe previamente.
     *
     * <p>Si la raíz está llena ({@code keyCount() == m - 1}) se divide
     * antes de insertar, incrementando la altura del árbol en uno.
     * El árbol B crece hacia arriba.</p>
     *
     * @param key clave a insertar
     * @return {@code true} si fue insertada; {@code false} si ya existía
     */
    public boolean insert(int key) {
        if (search(key)) return false;

        if (root.keyCount() == m - 1) {
            Node newRoot = new Node();
            newRoot.leaf = false;
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }

        insertNonFull(root, key);
        size++;
        return true;
    }

    /**
     * Inserta la clave en el subárbol cuya raíz es {@code n},
     * garantizando que {@code n} no está lleno antes de la llamada.
     *
     * @param n   nodo receptor (no lleno al momento de la llamada)
     * @param key clave a insertar
     */
    private void insertNonFull(Node n, int key) {
        int i = n.keyCount() - 1;

        if (n.leaf) {
            n.keys.add(null);
            while (i >= 0 && key < n.keys.get(i)) {
                n.keys.set(i + 1, n.keys.get(i));
                i--;
            }
            n.keys.set(i + 1, key);
        } else {
            while (i >= 0 && key < n.keys.get(i)) i--;
            i++;
            if (n.children.get(i).keyCount() == m - 1) {
                splitChild(n, i);
                if (key > n.keys.get(i)) i++;
            }
            insertNonFull(n.children.get(i), key);
        }
    }

    /**
     * Divide el {@code i}-ésimo hijo del nodo {@code parent}.
     *
     * <p>El hijo lleno se parte en dos:
     * las primeras {@code t-1} claves quedan en el nodo izquierdo,
     * las últimas {@code t-1} van al nuevo nodo derecho,
     * y la clave mediana asciende al padre.</p>
     *
     * @param parent nodo padre del hijo que se dividirá
     * @param i      índice del hijo lleno dentro de {@code parent.children}
     */
    private void splitChild(Node parent, int i) {
        Node full  = parent.children.get(i);
        Node right = new Node();
        right.leaf = full.leaf;

        int median = full.keys.get(t - 1);

        right.keys.addAll(full.keys.subList(t, full.keyCount()));
        if (!full.leaf)
            right.children.addAll(full.children.subList(t, full.children.size()));

        full.keys.subList(t - 1, full.keyCount()).clear();
        if (!full.leaf)
            full.children.subList(t, full.children.size()).clear();

        parent.keys.add(i, median);
        parent.children.add(i + 1, right);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ELIMINACIÓN
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Elimina la clave del árbol si existe.
     *
     * <p>Si la raíz queda vacía tras la eliminación y tiene un único hijo,
     * ese hijo se convierte en la nueva raíz (el árbol reduce su altura).</p>
     *
     * @param key clave a eliminar
     * @return {@code true} si fue eliminada; {@code false} si no existía
     */
    public boolean delete(int key) {
        if (!search(key)) return false;
        deleteNode(root, key);
        if (root.keyCount() == 0 && !root.leaf) root = root.children.get(0);
        size--;
        return true;
    }

    /**
     * Elimina la clave del subárbol con raíz {@code n}.
     *
     * <p>Implementa los tres casos del algoritmo:</p>
     * <ol>
     *   <li><strong>Caso 1 (hoja):</strong> eliminación directa.</li>
     *   <li><strong>Caso 2 (nodo interno):</strong>
     *     <ul>
     *       <li>2a: hijo izquierdo tiene >= {@code t} claves → reemplaza por predecesor.</li>
     *       <li>2b: hijo derecho tiene >= {@code t} claves → reemplaza por sucesor.</li>
     *       <li>2c: ambos con {@code t-1} → fusiona y elimina recursivamente.</li>
     *     </ul>
     *   </li>
     *   <li><strong>Caso 3 (descenso):</strong> garantiza >= {@code t} claves
     *       en el hijo antes de descender (rotación o fusión).</li>
     * </ol>
     *
     * @param n   nodo actual del recorrido
     * @param key clave a eliminar
     */
    private void deleteNode(Node n, int key) {
        int     i         = indexOf(n, key);
        boolean keyIsHere = (i < n.keyCount() && n.keys.get(i) == key);

        if (keyIsHere) {
            if (n.leaf) {
                // Caso 1: la clave está en una hoja
                n.keys.remove(i);
            } else {
                Node left  = n.children.get(i);
                Node right = n.children.get(i + 1);

                if (left.keyCount() >= t) {
                    // Caso 2a: reemplaza por predecesor en orden
                    int pred = inorderPredecessor(left);
                    n.keys.set(i, pred);
                    deleteNode(left, pred);
                } else if (right.keyCount() >= t) {
                    // Caso 2b: reemplaza por sucesor en orden
                    int succ = inorderSuccessor(right);
                    n.keys.set(i, succ);
                    deleteNode(right, succ);
                } else {
                    // Caso 2c: fusión izquierdo + clave + derecho
                    merge(n, i);
                    deleteNode(left, key);
                }
            }
        } else {
            if (n.leaf) return;
            // Caso 3: descenso garantizando >= t claves en el hijo
            ensureMinKeys(n, i);
            if (i > n.keyCount()) i--;
            deleteNode(n.children.get(i), key);
        }
    }

    /**
     * Garantiza que el {@code i}-ésimo hijo de {@code n} tenga al menos
     * {@code t} claves antes de descender hacia él.
     * Aplica rotación derecha, rotación izquierda o fusión según disponibilidad.
     *
     * @param n nodo padre cuyo hijo {@code i} está deficiente
     * @param i índice del hijo al que se quiere descender
     */
    private void ensureMinKeys(Node n, int i) {
        Node child = n.children.get(i);
        if (child.keyCount() >= t) return;

        boolean leftRich  = i > 0           && n.children.get(i - 1).keyCount() >= t;
        boolean rightRich = i < n.keyCount() && n.children.get(i + 1).keyCount() >= t;

        if      (leftRich)  rotateRight(n, i);
        else if (rightRich) rotateLeft(n, i);
        else if (i > 0)     merge(n, i - 1);
        else                merge(n, i);
    }

    /**
     * Rotación hacia la derecha: la mayor clave del hermano izquierdo
     * sube al padre y la clave separadora del padre baja al hijo deficiente.
     *
     * @param parent nodo padre
     * @param i      índice del hijo deficiente
     */
    private void rotateRight(Node parent, int i) {
        Node child   = parent.children.get(i);
        Node sibling = parent.children.get(i - 1);
        child.keys.add(0, parent.keys.get(i - 1));
        parent.keys.set(i - 1, sibling.keys.remove(sibling.keyCount() - 1));
        if (!sibling.leaf)
            child.children.add(0, sibling.children.remove(sibling.children.size() - 1));
    }

    /**
     * Rotación hacia la izquierda: la menor clave del hermano derecho
     * sube al padre y la clave separadora del padre baja al hijo deficiente.
     *
     * @param parent nodo padre
     * @param i      índice del hijo deficiente
     */
    private void rotateLeft(Node parent, int i) {
        Node child   = parent.children.get(i);
        Node sibling = parent.children.get(i + 1);
        child.keys.add(parent.keys.get(i));
        parent.keys.set(i, sibling.keys.remove(0));
        if (!sibling.leaf)
            child.children.add(sibling.children.remove(0));
    }

    /**
     * Fusiona el {@code i}-ésimo y {@code (i+1)}-ésimo hijos del padre,
     * jalando la clave separadora hacia el nodo fusionado.
     * El padre pierde una clave y un hijo.
     *
     * @param parent nodo padre
     * @param i      índice del hijo izquierdo en la fusión
     */
    private void merge(Node parent, int i) {
        Node left  = parent.children.get(i);
        Node right = parent.children.get(i + 1);
        left.keys.add(parent.keys.remove(i));
        left.keys.addAll(right.keys);
        left.children.addAll(right.children);
        parent.children.remove(i + 1);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Utilidades privadas
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Primer índice {@code i} tal que {@code n.keys[i] >= key},
     * o {@code n.keyCount()} si {@code key} supera todas las claves.
     *
     * @param n   nodo en el que buscar
     * @param key clave de referencia
     * @return índice de la primera clave >= key
     */
    private int indexOf(Node n, int key) {
        int i = 0;
        while (i < n.keyCount() && key > n.keys.get(i)) i++;
        return i;
    }

    /**
     * Mayor clave del subárbol {@code n} (predecesor en orden).
     *
     * @param n subárbol del hijo izquierdo
     * @return predecesor en orden de la clave a eliminar
     */
    private int inorderPredecessor(Node n) {
        while (!n.leaf) n = n.children.get(n.keyCount());
        return n.keys.get(n.keyCount() - 1);
    }

    /**
     * Menor clave del subárbol {@code n} (sucesor en orden).
     *
     * @param n subárbol del hijo derecho
     * @return sucesor en orden de la clave a eliminar
     */
    private int inorderSuccessor(Node n) {
        while (!n.leaf) n = n.children.get(0);
        return n.keys.get(0);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Getters
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Devuelve la raíz del árbol.
     *
     * @return nodo raíz; nunca {@code null}
     */
    public Node getRoot()  { return root; }

    /**
     * Devuelve el número total de claves almacenadas.
     *
     * @return cantidad de claves ({@code >= 0})
     */
    public int  getSize()  { return size; }

    /**
     * Devuelve el orden del árbol B.
     *
     * @return {@code m}, número máximo de hijos por nodo
     */
    public int  getOrder() { return m; }
}
