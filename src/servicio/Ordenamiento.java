package servicio;

import java.util.Comparator;

/**
 * Algoritmos de ordenamiento implementados a mano para poder evaluarlos.
 *
 *  - Burbuja:   O(n²)       simple, solo didáctico.
 *  - Inserción: O(n²)       buena con pocos datos o casi ordenados.
 *  - MergeSort: O(n log n)  estable y predecible. ES EL SELECCIONADO para el proyecto.
 */
public final class Ordenamiento {
    private Ordenamiento() { }

    public static <T> void burbuja(T[] a, Comparator<? super T> c) {
        for (int i = 0; i < a.length - 1; i++) {
            boolean huboCambio = false;
            for (int j = 0; j < a.length - 1 - i; j++) {
                if (c.compare(a[j], a[j + 1]) > 0) {
                    T tmp = a[j]; a[j] = a[j + 1]; a[j + 1] = tmp;
                    huboCambio = true;
                }
            }
            if (!huboCambio) break;   // ya está ordenado
        }
    }

    public static <T> void insercion(T[] a, Comparator<? super T> c) {
        for (int i = 1; i < a.length; i++) {
            T actual = a[i];
            int j = i - 1;
            while (j >= 0 && c.compare(a[j], actual) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = actual;
        }
    }

    public static <T> void mergeSort(T[] a, Comparator<? super T> c) {
        if (a.length < 2) return;
        T[] auxiliar = a.clone();
        mezclaRecursiva(a, auxiliar, 0, a.length - 1, c);
    }

    private static <T> void mezclaRecursiva(T[] a, T[] aux, int ini, int fin, Comparator<? super T> c) {
        if (ini >= fin) return;
        int medio = (ini + fin) >>> 1;
        mezclaRecursiva(a, aux, ini, medio, c);
        mezclaRecursiva(a, aux, medio + 1, fin, c);

        System.arraycopy(a, ini, aux, ini, fin - ini + 1);
        int i = ini, j = medio + 1, k = ini;
        while (i <= medio && j <= fin) {
            if (c.compare(aux[i], aux[j]) <= 0) a[k++] = aux[i++];
            else a[k++] = aux[j++];
        }
        while (i <= medio) a[k++] = aux[i++];
        while (j <= fin) a[k++] = aux[j++];
    }
}
