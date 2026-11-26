package servicio;

import java.util.function.ToIntBiFunction;

/**
 * Algoritmos de búsqueda.
 *
 *  - Lineal:  O(n)       funciona con datos sin ordenar.
 *  - Binaria: O(log n)   requiere arreglo ordenado. ES LA SELECCIONADA para buscar por código de barras.
 *
 * El comparador recibe (elemento, clave) y devuelve: 0 si coinciden,
 * negativo si el elemento es menor que la clave y positivo si es mayor.
 */
public final class Busqueda {
    private Busqueda() { }

    /** Devuelve la posición del elemento o -1 si no existe. */
    public static <T, K> int lineal(T[] a, K clave, ToIntBiFunction<T, K> comparador) {
        for (int i = 0; i < a.length; i++) {
            if (comparador.applyAsInt(a[i], clave) == 0) return i;
        }
        return -1;
    }

    /** El arreglo DEBE estar ordenado con el mismo criterio del comparador. */
    public static <T, K> int binaria(T[] a, K clave, ToIntBiFunction<T, K> comparador) {
        int bajo = 0, alto = a.length - 1;
        while (bajo <= alto) {
            int medio = (bajo + alto) >>> 1;
            int resultado = comparador.applyAsInt(a[medio], clave);
            if (resultado == 0) return medio;
            if (resultado < 0) bajo = medio + 1;
            else alto = medio - 1;
        }
        return -1;
    }
}
