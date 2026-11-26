package pruebas;

import modelo.Producto;
import servicio.Busqueda;
import servicio.Ordenamiento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Evaluación para justificar la selección del método de ordenamiento y de búsqueda.
 * Mide tiempos reales con distintos tamaños de catálogo y muestra una tabla comparativa.
 */
public class EvaluacionAlgoritmos {
    private static final int[] TAMANOS = {1000, 5000, 20000};
    private static final int BUSQUEDAS = 2000;

    public static void main(String[] args) {
        System.out.println("=== ORDENAMIENTO por código de barras (ms) ===");
        System.out.printf("%-10s %12s %12s %12s%n", "Productos", "Burbuja", "Inserción", "MergeSort");
        for (int n : TAMANOS) {
            Producto[] datos = generar(n);
            double burbuja = medirOrdenamiento(datos, 0);
            double insercion = medirOrdenamiento(datos, 1);
            double merge = medirOrdenamiento(datos, 2);
            System.out.printf("%-10d %12.2f %12.2f %12.2f%n", n, burbuja, insercion, merge);
        }

        System.out.println("\n=== BÚSQUEDA de " + BUSQUEDAS + " códigos (ms) ===");
        System.out.printf("%-10s %12s %12s%n", "Productos", "Lineal", "Binaria");
        Random azar = new Random(7);
        for (int n : TAMANOS) {
            Producto[] desordenado = generar(n);
            Producto[] ordenado = desordenado.clone();
            Ordenamiento.mergeSort(ordenado, Producto.POR_CODIGO);

            String[] claves = new String[BUSQUEDAS];
            for (int i = 0; i < BUSQUEDAS; i++) {
                claves[i] = desordenado[azar.nextInt(n)].getCodigoBarras();
            }

            long t0 = System.nanoTime();
            for (String k : claves) Busqueda.lineal(desordenado, k, (p, c) -> p.getCodigoBarras().compareTo(c));
            double lineal = (System.nanoTime() - t0) / 1e6;

            t0 = System.nanoTime();
            for (String k : claves) Busqueda.binaria(ordenado, k, (p, c) -> p.getCodigoBarras().compareTo(c));
            double binaria = (System.nanoTime() - t0) / 1e6;

            System.out.printf("%-10d %12.2f %12.2f%n", n, lineal, binaria);
        }

        System.out.println("\nConclusión: se selecciona MergeSort (O(n log n)) para ordenar y búsqueda binaria"
                + " (O(log n)) para localizar productos por código de barras.");
    }

    /** algoritmo: 0 = burbuja, 1 = inserción, 2 = mergeSort. Trabaja sobre una copia. */
    private static double medirOrdenamiento(Producto[] original, int algoritmo) {
        Producto[] copia = original.clone();
        long inicio = System.nanoTime();
        if (algoritmo == 0) Ordenamiento.burbuja(copia, Producto.POR_CODIGO);
        else if (algoritmo == 1) Ordenamiento.insercion(copia, Producto.POR_CODIGO);
        else Ordenamiento.mergeSort(copia, Producto.POR_CODIGO);
        return (System.nanoTime() - inicio) / 1e6;
    }

    /** Genera n productos con códigos únicos de 13 dígitos en orden aleatorio. */
    private static Producto[] generar(int n) {
        List<Integer> numeros = new ArrayList<>();
        for (int i = 0; i < n; i++) numeros.add(i);
        Collections.shuffle(numeros, new Random(42));
        Producto[] productos = new Producto[n];
        for (int i = 0; i < n; i++) {
            productos[i] = new Producto(String.format("750%010d", numeros.get(i)),
                    "Producto " + i, "General", 10 + (i % 50), 10, 3);
        }
        return productos;
    }
}
