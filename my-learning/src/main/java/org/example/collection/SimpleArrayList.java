package org.example.collection;

import java.util.Arrays;

public class SimpleArrayList implements SimpleList {

    private String[] data;
    private int currentIndex;
    private int size;

    public SimpleArrayList() {
        this.size = 1;
        this.currentIndex = 0;
        this.data = new String[size];
    }

    @Override
    public boolean add(final String value) {
        data[currentIndex] = value;

        currentIndex++;
        if (currentIndex == size) {
            grow();
        }
        return true;
    }

    private void grow() {
        size <<= 1;
        data = Arrays.copyOf(data, size);
    }

    @Override
    public void add(final int index, final String value) {
        if (currentIndex <= index) {
            throw new IndexOutOfBoundsException();
        }

        for (int i = currentIndex; i > index; i--) {
            data[i] = data[i - 1];
        }

        data[index] = value;

        currentIndex++;
        if (currentIndex == size) {
            grow();
        }
    }

    @Override
    public String set(final int index, final String value) {
        if (currentIndex < index) {
            throw new IndexOutOfBoundsException();
        }
        final String prevData = data[index];
        data[index] = value;
        return prevData;
    }

    @Override
    public String get(final int index) {
        if (currentIndex < index) {
            throw new IndexOutOfBoundsException();
        }
        return data[index];
    }

    @Override
    public boolean contains(final String value) {
        return Arrays.stream(data)
                .filter(d -> value.equals(d))
                .findAny()
                .isPresent();
    }

    @Override
    public int indexOf(final String value) {
        for (int i = 0; i < currentIndex; i++) {
            if (data[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return currentIndex;
    }

    @Override
    public boolean isEmpty() {
        return currentIndex == 0;
    }

    @Override
    public boolean remove(final String value) {
        final int valueIndex = indexOf(value);

        if (valueIndex == -1) {
            return false;
        }

        for (int i = valueIndex; i < currentIndex; i++) {
            data[i] = data[i + 1];
        }

        currentIndex--;
        if (size << 1 > currentIndex) {
            atrophy();
        }

        return true;
    }

    private void atrophy() {
        size >>= 1;
        data = Arrays.copyOf(data, size);
    }

    @Override
    public String remove(final int index) {
        if (currentIndex <= index) {
            throw new IndexOutOfBoundsException();
        }

        final String prevData = data[index];

        for (int i = index; i < currentIndex; i++) {
            data[i] = data[i + 1];
        }

        currentIndex--;
        if (size << 1 > currentIndex) {
            atrophy();
        }

        return prevData;
    }

    @Override
    public void clear() {
        currentIndex = 0;
        size = 1;
        data = new String[size];
    }
}
