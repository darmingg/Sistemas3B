package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * TDA Lista enlazada simple.
 * Se usa en el proyecto para el catálogo de productos, el carrito y los detalles de una venta.
 *
 * Complejidades: agregar al final O(1), agregar al inicio O(1),
 * obtener/eliminar por posición O(n), buscar O(n).
 */
public class ListaEnlazada<T> implements Iterable<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamano;

    /** Agrega un elemento al final de la lista. */
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }
        tamano++;
    }

    /** Agrega un elemento al inicio de la lista. */
    public void agregarAlInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        if (cola == null) cola = nuevo;
        tamano++;
    }

    /** Devuelve el elemento en la posición indicada (0 = primero). */
    public T obtener(int posicion) {
        validarPosicion(posicion);
        return nodoEn(posicion).getDato();
    }

    /** Elimina y devuelve el elemento en la posición indicada. */
    public T eliminarEn(int posicion) {
        validarPosicion(posicion);
        if (posicion == 0) {
            T dato = cabeza.getDato();
            cabeza = cabeza.getSiguiente();
            if (cabeza == null) cola = null;
            tamano--;
            return dato;
        }
        Nodo<T> anterior = nodoEn(posicion - 1);
        Nodo<T> objetivo = anterior.getSiguiente();
        anterior.setSiguiente(objetivo.getSiguiente());
        if (objetivo == cola) cola = anterior;
        tamano--;
        return objetivo.getDato();
    }

    /** Elimina la primera aparición del elemento (usa equals). Devuelve true si lo encontró. */
    public boolean eliminarElemento(T dato) {
        int posicion = indiceDe(dato);
        if (posicion < 0) return false;
        eliminarEn(posicion);
        return true;
    }

    /** Devuelve la posición del elemento o -1 si no existe. */
    public int indiceDe(T dato) {
        int i = 0;
        for (T actual : this) {
            if (actual == null ? dato == null : actual.equals(dato)) return i;
            i++;
        }
        return -1;
    }

    public int tamano() { return tamano; }

    public boolean estaVacia() { return tamano == 0; }

    public void limpiar() {
        cabeza = null;
        cola = null;
        tamano = 0;
    }

    /** Copia los elementos a un arreglo (necesario para ordenar y buscar). */
    @SuppressWarnings("unchecked")
    public T[] aArreglo(T[] destino) {
        if (destino.length < tamano) {
            destino = (T[]) java.lang.reflect.Array.newInstance(
                    destino.getClass().getComponentType(), tamano);
        }
        int i = 0;
        for (T dato : this) destino[i++] = dato;
        return destino;
    }

    private Nodo<T> nodoEn(int posicion) {
        Nodo<T> actual = cabeza;
        for (int i = 0; i < posicion; i++) actual = actual.getSiguiente();
        return actual;
    }

    private void validarPosicion(int posicion) {
        if (posicion < 0 || posicion >= tamano) {
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cabeza;

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
