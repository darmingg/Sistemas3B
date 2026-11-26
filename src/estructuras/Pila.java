package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * TDA Pila (LIFO: el último en entrar es el primero en salir).
 * Se usa para el historial de ventas: la venta más reciente queda en el tope.
 * Todas las operaciones principales son O(1).
 */
public class Pila<T> implements Iterable<T> {
    private Nodo<T> tope;
    private int tamano;

    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamano++;
    }

    public T desapilar() {
        if (estaVacia()) throw new NoSuchElementException("La pila está vacía");
        T dato = tope.getDato();
        tope = tope.getSiguiente();
        tamano--;
        return dato;
    }

    public T verTope() {
        if (estaVacia()) throw new NoSuchElementException("La pila está vacía");
        return tope.getDato();
    }

    public boolean estaVacia() { return tope == null; }

    public int tamano() { return tamano; }

    /** Recorre desde el tope (más reciente) hasta el fondo. */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = tope;

            @Override
            public boolean hasNext() { return actual != null; }

            @Override
            public T next() {
                if (actual == null) throw new NoSuchElementException();
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}
