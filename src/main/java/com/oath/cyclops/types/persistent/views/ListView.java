package com.oath.cyclops.types.persistent.views;

import com.oath.cyclops.types.persistent.PersistentIndexed;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface ListView<T> extends List<T>
{

  @Override
  @Deprecated
  boolean add(T t);

  @Override
  @Deprecated
  boolean remove(Object o);

  @Override
  @Deprecated
  boolean addAll(Collection<? extends T> c);

  @Override
  @Deprecated
  boolean addAll(int index, Collection<? extends T> c);

  @Override
  @Deprecated
  boolean removeAll(Collection<?> c);

  @Override
  @Deprecated
  boolean retainAll(Collection<?> c);

  @Override
  @Deprecated
  default void replaceAll(UnaryOperator<T> operator) {

  }

  @Override
  @Deprecated
  default void sort(Comparator<? super T> c) {

  }

  @Override
  @Deprecated
  default boolean removeIf(Predicate<? super T> filter) {
    return false;
  }

  @Override
  void clear();

  @AllArgsConstructor
  public static class Impl<T> extends AbstractList<T> implements ListView<T>{
    private final PersistentIndexed<T> host;

    @Override
    public int size() {
      return host.size();
    }

    @Override
    public boolean isEmpty() {
      return host.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
      return host.containsValue((T)o);
    }

    @Override
    public Iterator<T> iterator() {
      return host.iterator();
    }

    @Override
    public Object[] toArray() {
      return host.stream().toArray();
    }



    @Override
    public boolean add(T t) {
      return false;
    }

    @Override
    public boolean remove(Object o) {
      return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {

      for(Object n : c){
        if(!contains(n))
          return false;
      }
      return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
      return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
      return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public T get(int index) {
      return host.getOrElse(index,null);
    }

    @Override
    public T set(int index, T element) {
      return element;
    }

    @Override
    public void add(int index, T element) {

    }

    @Override
    public T remove(int index) {
      return get(index);
    }






  }
}
