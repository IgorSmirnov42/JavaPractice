package ru.spbhse.treeset;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Data structure that implements MyTreeSet interface (see MyTreeSet.java)
 * Implemented using Splay Tree (read more here: https://en.wikipedia.org/wiki/Splay_tree)
 */
public class TreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {

    /*
    Not static because need to know about E
    Cannot be generic and static, because have to call methods that are in TreeSet class
    If I move safeSetChild inside (to make class static) I wouldn't be able to call from TreeSet
        like safeSetChild(a, b) (because function is not static), so I would have to have fictive node
    */
    private class SplayTreeNode {
        private SplayTreeNode parent;
        private SplayTreeNode left;
        private SplayTreeNode right;
        final private E value;

        private SplayTreeNode(E value) {
            this.value = value;
        }

        private boolean isLeftSon() {
            return parent.left == this;
        }

        private boolean hasParent() {
            return parent != null;
        }

        private void leftRotate() {
            var previousParent = parent;
            safeSetChild(parent.parent, this, parent.hasParent() && parent.isLeftSon());

            safeSetChild(previousParent, right, true);
            safeSetChild(this, previousParent, false);
        }

        private void rightRotate() {
            var previousParent = parent;
            safeSetChild(parent.parent, this, parent.hasParent() && parent.isLeftSon());

            safeSetChild(previousParent, left, false);
            safeSetChild(this, previousParent, true);
        }

        private void zig() {
            if (isLeftSon()) {
                leftRotate();
            } else {
                rightRotate();
            }
        }

        private void zigZig() {
            parent.zig();
            zig();
        }

        private void zigZag() {
            zig();
            zig();
        }

        private void splay() {
            while (parent != null) {
                if (!parent.hasParent()) {
                    zig();
                } else if (isLeftSon() == parent.isLeftSon()) {
                    zigZig();
                } else {
                    zigZag();
                }
            }
        }

        private SplayTreeNode first() {
            if (left == null) {
                splay();
                return this;
            }
            return left.first();
        }

        private SplayTreeNode last() {
            if (right == null) {
                splay();
                return this;
            }
            return right.last();
        }

        private SplayTreeNode previous() {
            if (left != null) {
                return left.last();
            }
            if (parent != null) {
                SplayTreeNode parentSaved = parent;
                parent.splay();
                return parentSaved;
            }
            return null;
        }
    }

    /**
     * Creates an edge between parent and child node
     * Checks if some of them are nulls
     */
    private void safeSetChild(SplayTreeNode parentNode, SplayTreeNode childNode, boolean leftSon) {
        if (parentNode != null) {
            if (leftSon) {
                parentNode.left = childNode;
            } else {
                parentNode.right = childNode;
            }
        }

        if (childNode != null) {
            childNode.parent = parentNode;
        }
    }

    private void safeDeleteParent(SplayTreeNode childNode) {
        if (childNode == null) {
            return;
        }

        if (childNode.hasParent()) {
            if (childNode.isLeftSon()) {
                childNode.parent.left = null;
            } else {
                childNode.parent.right = null;
            }
        }
        childNode.parent = null;
    }

    /**
     * Creates new SplayTree that consists of union of values from left and right trees
     * NB! All keys in left tree must be less then keys in right
     * Amortized complexity O(log n)
     */
    private SplayTreeNode merge(SplayTreeNode leftTree, SplayTreeNode rightTree) {
        safeDeleteParent(leftTree);
        safeDeleteParent(rightTree);

        if (leftTree == null) {
            return rightTree;
        }
        if (rightTree == null) {
            return leftTree;
        }
        SplayTreeNode newRoot = leftTree.last();
        safeSetChild(newRoot, rightTree, false);
        return newRoot;
    }

    private SplayTreeNode rootNode;
    final private Comparator<? super E> comparator;
    private int size;

    public TreeSet() {
        comparator = null;
    }

    public TreeSet(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    /**
     * TODO
     * @throws ClassCastException if E is not comparable and set was constructed without comparator
     *         or Object cannot be casted to E and comparator was given
     */
    //@SuppressWarnings("unchecked")
    private int compareElements(Object a, E b) {

        if (comparator != null) {
            return comparator.compare((E) a, b);
        }

        Comparable<? super E> aComparable = (Comparable<? super E>) a;
        return aComparable.compareTo(b);
    }

    /**
     * Returns node appropriated for element if it is contained in set
     * Otherwise returns previous or next node
     * Returns null only if set is empty
     * Amortized complexity O(log n)
     */
    private SplayTreeNode nearElement(Object element) {
        // TODO : element not null
        SplayTreeNode previousNode = null; //
        SplayTreeNode currentNode = rootNode;
        int compareResult;
        while (currentNode != null &&
                (compareResult = compareElements(element, currentNode.value)) != 0) {
            previousNode = currentNode;
            if (compareResult > 0) {
                currentNode = currentNode.right;
            }
            else {
                currentNode = currentNode.left;
            }
        }

        if (currentNode == null) {
            currentNode = previousNode;
        }

        if (currentNode != null) {
            currentNode.splay();
            rootNode = currentNode;
        }

        return currentNode;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    /**
     * Returns number of elements stored in set
     * Complexity O(1)
     */
    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return null;
    }

    @Override
    public TreeSet<E> descendingSet() {
        return null;
    }

    /**
     * Amortized complexity O(log n)
     */
    @Override
    public boolean contains(Object element) {
        SplayTreeNode foundNode = nearElement(element);
        if (foundNode == null) {
            return false;
        }
        return compareElements(element, foundNode.value) == 0;
    }

    /**
     * Adds element to set
     * Amortized complexity O(log n)
     * @return true if element was successfully added, false if it has already been in set
     */
    @Override
    public boolean add(E element) {
        if (contains(element)) {
            return false;
        }

        SplayTreeNode newNode = new SplayTreeNode(element);
        ++size;

        if (rootNode == null) {
            rootNode = newNode;
            return true;
        }

        // element is least element => newNode becomes new root
        if (compareElements(rootNode.first().value, element) > 0) {
            rootNode.splay();
            safeSetChild(newNode, rootNode, false);
            rootNode = newNode;
            return true;
        }

        rootNode.splay();

        SplayTreeNode foundNode = nearElement(element);

        // Previous node is guaranteed to exist
        if (compareElements(element, foundNode.value) < 0) {
            foundNode = foundNode.previous();
        }

        safeSetChild(newNode, foundNode.right, false);
        safeSetChild(foundNode, newNode, false);
        rootNode = foundNode;

        return true;
    }

    /**
     * Amortized complexity O(log n)
     */
    @Override
    public boolean remove(Object element) {
        if (!contains(element)) {
            return false;
        }

        --size;
        SplayTreeNode foundNode = nearElement(element);

        // After nearElement() this node is already root => we have to merge root's children
        rootNode = merge(foundNode.left, foundNode.right);
        return true;
    }

    /**
     * Returns the least element in set
     * If set is empty returns null
     * Amortized complexity O(log n)
     */
    @Override
    public E first() {
        if (rootNode == null) {
            return null;
        }
        return rootNode.first().value;
    }

    /**
     * Returns the greatest element in set.
     * Amortized complexity O(log n)
     */
    @Override
    public E last() {
        if (rootNode == null) {
            return null;
        }
        return rootNode.last().value;
    }

    /**
     * Returns the largest element in set that is lower than given
     * If there is no such element returns null
     * Amortized complexity O(log n)
     */
    @Override
    public E lower(E e) {
        if (rootNode == null) {
            return null;
        }

        SplayTreeNode nearNode = nearElement(e);
        int compareResult = compareElements(e, nearNode.value);

        if (compareResult > 0) {
            return nearNode.value;
        }

        if (nearNode.left == null) {
            return null;
        }

        rootNode = nearNode.left.last();
        return rootNode.value;
    }

    /**
     * Returns the largest element in set that is not more than given
     * If there is no such element returns null
     * Amortized complexity O(log n)
     */
    @Override
    public E floor(E e) {
        if (contains(e)) {
            return e;
        }
        return lower(e);
    }

    /**
     * Returns the smallest element in set that is not less than given
     * If there is no such element returns null
     * Amortized complexity O(log n)
     */
    @Override
    public E ceiling(E e) {
        if (contains(e)) {
            return e;
        }
        return higher(e);
    }

    /**
     * Returns the smallest element in set that is higher than given
     * If there is no such element returns null
     * Amortized complexity O(log n)
     */
    @Override
    public E higher(E e) {
        if (rootNode == null) {
            return null;
        }

        SplayTreeNode nearNode = nearElement(e);
        int compareResult = compareElements(e, nearNode.value);

        if (compareResult < 0) {
            return nearNode.value;
        }

        if (nearNode.right == null) {
            return null;
        }

        rootNode = nearNode.right.first();
        return rootNode.value;
    }
}
