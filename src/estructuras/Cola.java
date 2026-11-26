package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * TDA Cola (FIFO: el primero en entrar es el primero en salir).
 * Se usa para la fila de clientes en espera de ser atendidos en caja.
 * Todas las operaciones principales son O(1).
 */
public class Cola<T> implements Iterable<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamano;

    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (fin == null) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamano++;
    }

    public T desencolar() {
        if (estaVacia()) throw new NoSuchElementException("La cola está vacía");
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) fin = null;
        tamano--;
        return dato;
    }

    public T verFrente() {
        if (estaVacia()) throw new NoSuchElementException("La cola está vacía");
        return frente.getDato();
    }

    public boolean estaVacia() { return frente == null; }

    public int tamano() { return tamano; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = frente;

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
