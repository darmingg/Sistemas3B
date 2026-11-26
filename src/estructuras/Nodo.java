package estructuras;

/**
 * Nodo genérico de una estructura enlazada.
 * Solo es visible dentro del paquete "estructuras" (encapsulamiento).
 */
class Nodo<T> {
    private T dato;
    private Nodo<T> siguiente;

    Nodo(T dato) {
        this.dato = dato;
    }

    T getDato() { return dato; }
    Nodo<T> getSiguiente() { return siguiente; }
    void setSiguiente(Nodo<T> siguiente) { this.siguiente = siguiente; }
}
