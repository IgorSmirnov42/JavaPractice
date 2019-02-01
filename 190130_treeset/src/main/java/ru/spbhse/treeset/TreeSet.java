package ru.spbhse.treeset;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Data structure that implements MyTreeSet interface (see MyTreeSet.java)
 * Implemented using Splay Tree
 */
public class TreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {

    public static void main(String[] args) {
        var testWithoutComparator = new TreeSet<>();
        testWithoutComparator.add(1337);
        testWithoutComparator.add(42);
        testWithoutComparator.add(239);
    }

    private static class SplayTreeNode<E> {
        private SplayTreeNode<E> parent;
        private SplayTreeNode<E> left;
        private SplayTreeNode<E> right;
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

        private static SplayTreeNode merge(SplayTreeNode leftTree, SplayTreeNode rightTree) {
            if (leftTree == null) {
                return rightTree;
            }
            if (rightTree == null) {
                return leftTree;
            }
            SplayTreeNode newRoot = leftTree.last();
            safeSetSon(newRoot, rightTree, false);
            return newRoot;
        }

        private void leftRotate() {
            var previousParent = parent;
            safeSetSon(parent.parent, this, parent.hasParent() && parent.isLeftSon());

            safeSetSon(previousParent, right, true);
            safeSetSon(this, previousParent, false);
        }

        private void rightRotate() {
            var previousParent = parent;
            safeSetSon(parent.parent, this, parent.hasParent() && parent.isLeftSon());

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

        private SplayTreeNode<E> previous() {
            if (left != null) {
                return left.last();
            }
            if (parent != null) {
                SplayTreeNode<E> parentSaved = parent;
                parent.splay();
                return parentSaved;
            }
            return null;
        }
    }

    private SplayTreeNode<E> rootNode;
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
     */
    private SplayTreeNode<E> nearElement(Object element) {
        // TODO : element not null
        SplayTreeNode<E> previousNode = null; //
        SplayTreeNode<E> currentNode = rootNode;
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

    @Override
    public boolean contains(Object element) {
        SplayTreeNode<E> foundNode = nearElement(element);
        if (foundNode == null) {
            return false;
        }
        return compareElements(element, foundNode.value) == 0;
    }

    @Override
    public boolean add(E element) {
        if (contains(element)) {
            return false;
        }

        SplayTreeNode<E> newNode = new SplayTreeNode<>(element);
        ++size;

        if (rootNode == null) {
            rootNode = newNode;
            return true;
        }

        // element is least element => newNode becomes new root
        if (compareElements(rootNode.first().value, element) > 0) {
            rootNode.splay();
            SplayTreeNode.safeSetSon(newNode, rootNode, false);
            rootNode = newNode;
            return true;
        }

        rootNode.splay();

        SplayTreeNode<E> foundNode = nearElement(element);

        // Previous node is guaranteed to exist
        if (compareElements(element, foundNode.value) < 0) {
            foundNode = foundNode.previous();
        }

        SplayTreeNode.safeSetSon(newNode, foundNode.right, false);
        SplayTreeNode.safeSetSon(foundNode, newNode, false);
        rootNode = foundNode;

        return true;
    }

    /**
     * Returns the least element in set. O(log n) time
     * If set is empty returns null
     */
    @Override
    public E first() {
        if (rootNode == null) {
            return null;
        }
        return rootNode.first().value;
    }

    /** Returns the greatest element in set. O(log n) time */
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
     * O(log n) time
     */
    @Override
    public E lower(E e) {
        if (rootNode == null) {
            return null;
        }

        SplayTreeNode<E> nearNode = nearElement(e);
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
     * O(log n) time
     */
    @Override
    public E floor(E e) {
        if (rootNode == null) {
            return null;
        }

        SplayTreeNode<E> nearNode = nearElement(e);
        int compareResult = compareElements(e, nearNode.value);

        if (compareResult >= 0) {
            return nearNode.value;
        }

        if (nearNode.left == null) {
            return null;
        }

        rootNode = nearNode.left.last();
        return rootNode.value;
    }

    /**
     * Returns the smallest element in set that is not less than given
     * If there is no such element returns null
     * O(log n) time
     */
    @Override
    public E ceiling(E e) {
        if (rootNode == null) {
            return null;
        }

        SplayTreeNode<E> nearNode = nearElement(e);
        int compareResult = compareElements(e, nearNode.value);

        if (compareResult <= 0) {
            return nearNode.value;
        }

        if (nearNode.right == null) {
            return null;
        }

        rootNode = nearNode.right.first();
        return rootNode.value;
    }

    /**
     * Returns the smallest element in set that is higher than given
     * If there is no such element returns null
     * O(log n) time
     */
    @Override
    public E higher(E e) {
        if (rootNode == null) {
            return null;
        }

        SplayTreeNode<E> nearNode = nearElement(e);
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
