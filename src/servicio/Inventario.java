package servicio;

import estructuras.ListaEnlazada;
import modelo.Producto;

/**
 * Módulo de inventario: catálogo de productos, búsqueda rápida y alertas de stock bajo.
 *
 * Los productos viven en una ListaEnlazada. Para buscar rápido por código de barras se
 * mantiene un "índice": un arreglo ordenado por código (MergeSort) sobre el que se aplica
 * búsqueda binaria. El índice se reconstruye solo cuando el catálogo cambia.
 */
public class Inventario {
    private final ListaEnlazada<Producto> productos = new ListaEnlazada<>();
    private Producto[] indice;   // null = hay que reconstruirlo

    /** Registra un producto nuevo. Rechaza códigos de barras repetidos. */
    public void registrar(Producto producto) {
        if (producto == null) throw new IllegalArgumentException("El producto es obligatorio");
        for (Producto p : productos) {
            if (p.getCodigoBarras().equals(producto.getCodigoBarras())) {
                throw new IllegalArgumentException(
                        "Ya existe un producto con el código " + producto.getCodigoBarras());
            }
        }
        productos.agregar(producto);
        indice = null;
    }

    /** Búsqueda rápida (binaria) por código de barras. Devuelve null si no existe. */
    public Producto buscarPorCodigo(String codigo) {
        if (codigo == null) return null;
        Producto[] idx = obtenerIndice();
        int posicion = Busqueda.binaria(idx, codigo.trim(),
                (p, clave) -> p.getCodigoBarras().compareTo(clave));
        return posicion >= 0 ? idx[posicion] : null;
    }

    public boolean eliminar(String codigo) {
        Producto producto = buscarPorCodigo(codigo);
        if (producto == null) return false;
        productos.eliminarElemento(producto);
        indice = null;
        return true;
    }

    /** Descuenta existencias (lo usa el punto de venta al confirmar una venta). */
    public void descontarStock(String codigo, int cantidad) {
        Producto producto = buscarPorCodigo(codigo);
        if (producto == null) throw new IllegalArgumentException("Producto no encontrado: " + codigo);
        producto.reducirStock(cantidad);
    }

    /** Suma existencias (entrada de mercancía). */
    public void reabastecer(String codigo, int cantidad) {
        Producto producto = buscarPorCodigo(codigo);
        if (producto == null) throw new IllegalArgumentException("Producto no encontrado: " + codigo);
        producto.aumentarStock(cantidad);
    }

    /** Productos ordenados alfabéticamente por nombre (MergeSort). */
    public Producto[] listarPorNombre() {
        Producto[] copia = productos.aArreglo(new Producto[0]);
        Ordenamiento.mergeSort(copia, Producto.POR_NOMBRE);
        return copia;
    }

    /** Alerta de mercancía baja: productos con stock igual o menor a su mínimo. */
    public ListaEnlazada<Producto> productosConStockBajo() {
        ListaEnlazada<Producto> bajos = new ListaEnlazada<>();
        for (Producto p : productos) {
            if (p.tieneStockBajo()) bajos.agregar(p);
        }
        return bajos;
    }

    /** Valor total del inventario (precio × existencias). */
    public double valorTotal() {
        double total = 0;
        for (Producto p : productos) total += p.getPrecio() * p.getStock();
        return total;
    }

    public int totalProductos() { return productos.tamano(); }

    public Producto[] obtenerTodos() { return productos.aArreglo(new Producto[0]); }

    private Producto[] obtenerIndice() {
        if (indice == null) {
            indice = productos.aArreglo(new Producto[0]);
            Ordenamiento.mergeSort(indice, Producto.POR_CODIGO);
        }
        return indice;
    }
}
