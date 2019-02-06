package ru.spbhse.test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to store elements as List does it
 * If there is only one element stores only it
 * If there are <= 5 elements stores it in a 5 elements array
 * Otherwise stores it in ArrayList
 */
@SuppressWarnings("unchecked") // wrong cast is impossible
public class SmartList<E> extends AbstractList<E> implements List<E> {
    private int size;
    private Object data;
    private final int MAX_ARRAY_SIZE = 5;

    /** Creates List with 0 elements */
    public SmartList() {}

    /** Creates list with elements of collection */
    public SmartList(Collection<? extends E> collection) {
        addAll(collection);
    }

    /** Adds element to the list */
    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        if (size == 0) {
            data = element;
        } else if (size == 1) {
            E previousElement = (E) data;
            data = new Object[MAX_ARRAY_SIZE];
            var dataArray = (E[]) data;
            dataArray[0] = previousElement;
            arrayAdd(index, element);
        } else if (size < MAX_ARRAY_SIZE) {
            arrayAdd(index, element);
        } else if (size == MAX_ARRAY_SIZE) {
            Object[] previousArray = (E[]) data;
            data = new ArrayList<E>();
            for (Object e : previousArray) {
                ((ArrayList<E>)data).add((E) e);
            }
            ((ArrayList<E>)data).add(index, element);
        } else {
            ((ArrayList<E>)data).add(index, element);
        }

        ++size;
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Removes element from list
     * @return removed element
     */
    @Override
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        E toReturn = get(index);

        if (size == 1) {
            data = null;
        } else if (size == 2) {
            data = get(1 - index);
        } else if (size <= 5) {
            removeFromArray(index);
        } else if (size == 6) {
            ((ArrayList<E>) data).remove(index);
            ArrayList<E> previousList = (ArrayList<E>) data;
            data = new Object[MAX_ARRAY_SIZE];
            int position = 0;
            for (E element : previousList) {
                arrayAdd(position++, element);
            }
        } else {
            ((ArrayList<E>) data).remove(index);
        }

        --size;

        return toReturn;
    }

    /** Removes element by index if current state is array */
    private void removeFromArray(int index) {
        var dataArray = (E[]) data;
        for (int i = index; i < MAX_ARRAY_SIZE - 1; i++) {
            dataArray[i] = dataArray[i + 1];
        }
    }

    /**
     * Sets new value to element by index
     * @return previous value
     */
    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        E toReturn;

        if (size == 1) {
            toReturn = (E) data;
            data = element;
        } else if (size <= 5) {
            toReturn = ((E[]) data)[index];
            ((E[]) data)[index] = element;
        } else {
            toReturn = ((ArrayList<E>)data).set(index, element);
        }

        return toReturn;
    }

    /** Adds element to list if current state is array */
    private void arrayAdd(int index, E element) {
        var dataArray = (E[]) data;
        for (int i = MAX_ARRAY_SIZE - 1; i > index; i--) {
            dataArray[i] = dataArray[i - 1];
        }
        dataArray[index] = element;
    }

    /** Returns element by index */
    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        if (size == 1) {
            return (E) data;
        } else if (size <= 5) {
            return ((E[]) data)[index];
        } else {
            return ((ArrayList<E>) data).get(index);
        }
    }
}
