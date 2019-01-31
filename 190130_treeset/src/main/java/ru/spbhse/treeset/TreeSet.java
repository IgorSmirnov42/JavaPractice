package ru.spbhse.treeset;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Data structure that implements MyTreeSet interface (see MyTreeSet.java)
 * Implemented using Splay Tree
 */
public class TreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {

    private static class SplayTreeNode<E> {
        private SplayTreeNode<E> parent;
        private SplayTreeNode<E> left;
        private SplayTreeNode<E> right;
        private E value;

        private boolean isLeftSon() {
            return parent.left == this;
        }

        private boolean hasParent() {
            return parent != null;
        }

        private static void safeSetSon(SplayTreeNode parentNode, SplayTreeNode childNode, boolean leftSon) {
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

        private void leftRotate() {
            var previousParent = parent;
            if (parent.hasParent()) {
                safeSetSon(parent.parent, this, parent.isLeftSon());
            }

            safeSetSon(previousParent, right, true);
            safeSetSon(this, previousParent, false);
        }

        private void rightRotate() {
            var previousParent = parent;
            if (parent.hasParent()) {
                safeSetSon(parent.parent, this, parent.isLeftSon());
            }

            safeSetSon(previousParent, left, false);
            safeSetSon(this, previousParent, true);
        }

        private void zig() {
            if (isLeftSon()) {
                leftRotate();
            } else {
                rightRotate();
            }
        }

        private void zigzig() {
            parent.zig();
            zig();
        }

        private void zigzag() {
            zig();
            zig();
        }

        private void splay() {
            while (parent != null) {
                if (!parent.hasParent()) {
                    zig();
                } else if (isLeftSon() == parent.isLeftSon()) {
                    zigzig();
                } else {
                    zigzag();
                }
            }
        }

        private SplayTreeNode<E> first() {
            if (left == null) {
                splay();
                return this;
            }
            return left.first();
        }

        private SplayTreeNode<E> last() {
            if (right == null) {
                splay();
                return this;
            }
            return right.last();
        }
    }

    private SplayTreeNode<E> rootNode;
    private Comparator<? super E> comparator;

    /**
     * TODO
     * @throws ClassCastException if E is not comparable and set was constructed without comparator
     */
    private int compareElements(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }

        Comparable<? super E> aComparable = (Comparable<? super E>) a;

        return aComparable.compareTo(b);
    }

    /**
     * Returns node appropriated for element if it is contained in set
     * Otherwise returns previous or next node
     * Returns null only if set is empty
     */
    private SplayTreeNode<E> nearElement(E element) {
        // TODO : element not null
        SplayTreeNode<E> previousNode = null; //
        SplayTreeNode<E> currentNode = rootNode;
        int compareResult;
        while (currentNode != null &&
                (compareResult = compareElements(currentNode.value, element)) != 0) {
            previousNode = currentNode;
            if (compareResult < 0) {
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

    public Iterator<E> iterator() {
        return null;
    }

    public int size() {
        return 0;
    }

    public Iterator<E> descendingIterator() {
        return null;
    }

    public MyTreeSet<E> descendingSet() {
        return null;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    /**
     * Returns the least element in set. O(log n) time
     * If set is empty returns null
     */
    public E first() {
        return null;
    }

    /** Returns the greatest element in set. O(log n) time */
    @Override
    public E last() {
        return null;
    }

    /**
     * Returns the largest element in set that is lower than given
     * If there is no such element returns null
     * O(log n) time
     */
    public E lower(E e) {
        if (rootNode == null) {
            return null;
        }

        SplayTreeNode<E> nearNode = nearElement(e);
        int compareResult = compareElements(nearNode.value, e);

        if (compareResult < 0) {
            return nearNode.value;
        }

        if (nearNode.left == null) {
            return null;
        }

        return nearNode.left.last().value;
    }

    /**
     * Returns the largest element in set that is not more than given
     * If there is no such element returns null
     * O(log n) time
     */
    public E floor(E e) {
        if (rootNode == null) {
            return null;
        }

        SplayTreeNode<E> nearNode = nearElement(e);
        int compareResult = compareElements(nearNode.value, e);

        if (compareResult <= 0) {
            return nearNode.value;
        }

        if (nearNode.left == null) {
            return null;
        }

        return nearNode.left.last().value;
    }

    /**
     * Returns the smallest element in set that is not less than given
     * If there is no such element returns null
     * O(log n) time
     */
    public E ceiling(E e) {
        if (rootNode == null) {
            return null;
        }

        SplayTreeNode<E> nearNode = nearElement(e);
        int compareResult = compareElements(nearNode.value, e);

        if (compareResult >= 0) {
            return nearNode.value;
        }

        if (nearNode.right == null) {
            return null;
        }

        return nearNode.right.last().value;
    }

    /**
     * Returns the smallest element in set that is higher than given
     * If there is no such element returns null
     * O(log n) time
     */
    public E higher(E e) {
        if (rootNode == null) {
            return null;
        }

        SplayTreeNode<E> nearNode = nearElement(e);
        int compareResult = compareElements(nearNode.value, e);

        if (compareResult > 0) {
            return nearNode.value;
        }

        if (nearNode.right == null) {
            return null;
        }

        return nearNode.right.last().value;
    }
}
