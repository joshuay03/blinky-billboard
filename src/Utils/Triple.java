package Utils;

import java.util.*;

@SuppressWarnings("ALL")
public class Triple<T0, T1, T2> implements List {
    public final T0 t0;
    public final T1 t1;
    public final T2 t2;
    private final Triple<T0, T1, T2> root;

    public Triple(T0 first, T1 second, T2 third){
        this.root = this;
        assert first != null; assert second != null; assert third != null;
        this.t0 = first; this.t1 = second; this.t2 = third;
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return(t0.equals(o) || t1.equals(o) || t2.equals(o));
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {
            int current = 0;
            @Override
            public boolean hasNext() {
                return (current < 2);
            }

            @Override
            public Object next() {
                if (current == 0) {return t1;}
                else if (current == 1) {return t2;}
                else throw new NoSuchElementException();
            }
        };
    }

    @Override
    public T0[] toArray() {
        if (t0.getClass().equals(t1.getClass()) && t1.getClass().equals(t2.getClass()))
            return (T0[]) new Object[]{t0, (T0) t1, (T0) t2};
        else return null;
    }

    @Override
    public Object[] toArray(Object[] objects) {
        try{
            return objects.getClass().cast(this.toArray());
        }
        catch (NullPointerException e){return null;}
    }

    @Override
    public boolean add(Object o) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean addAll(Collection collection) {
        return false;
    }

    @Override
    public boolean addAll(int i, Collection collection) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public Object get(int i) {
        if (i == 0) return t0;
        if (i == 1) return t1;
        if (i == 2) return t2;
        return null;
    }

    @Override
    public Object set(int i, Object o) {
        return null;
    }

    @Override
    public void add(int i, Object o) {

    }

    @Override
    public Object remove(int i) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public ListIterator listIterator(int i) {
        int current = i;
        return new ListIterator() {
            @Override
            public boolean hasNext() {
                return root.iterator().hasNext();
            }

            @Override
            public Object next() {
                return root.iterator().next();
            }

            @Override
            public boolean hasPrevious() {
                return i > 0;
            }

            @Override
            public Object previous() {
                if (current == 2) return t1;
                if (current == 1) return t0;
                else return null;
            }

            @Override
            public int nextIndex() {
                return (current + 1) % root.size();
            }

            @Override
            public int previousIndex() {
                return (current -1) % root.size();
            }

            @Override
            public void remove() {

            }

            @Override
            public void set(Object o) {

            }

            @Override
            public void add(Object o) {

            }
        };
    }

    @Override
    public List subList(int i, int i1) {
        return null;
    }

    @Override
    public boolean retainAll(Collection collection) {
        return false;
    }

    @Override
    public boolean removeAll(Collection collection) {
        return false;
    }

    @Override
    public boolean containsAll(Collection collection) {
        if (collection.size() >= 3){
            return collection.contains(this.t0) && collection.contains(this.t1) && collection.contains(this.t2);
        }
        return false;
    }
}
