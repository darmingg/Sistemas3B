package servicio;

import estructuras.Cola;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import modelo.DetalleVenta;
import modelo.Producto;
import modelo.Venta;

/**
 * Módulo de punto de venta.
 *  - Carrito:  ListaEnlazada de detalles.
 *  - Historial de ventas: Pila (la última venta queda arriba).
 *  - Clientes en espera:  Cola (se atiende en orden de llegada).
 */
public class PuntoVenta {
    private final Inventario inventario;
    private final ListaEnlazada<DetalleVenta> carrito = new ListaEnlazada<>();
    private final Pila<Venta> historial = new Pila<>();
    private final Cola<String> clientesEnEspera = new Cola<>();
    private int siguienteFolio = 1;

    public PuntoVenta(Inventario inventario) {
        if (inventario == null) throw new IllegalArgumentException("El inventario es obligatorio");
        this.inventario = inventario;
    }

    // ---------- Carrito ----------
    public void agregarAlCarrito(String codigo, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        Producto producto = inventario.buscarPorCodigo(codigo);
        if (producto == null) throw new IllegalArgumentException("Producto no encontrado: " + codigo);

        DetalleVenta existente = buscarEnCarrito(producto.getCodigoBarras());
        int yaEnCarrito = existente == null ? 0 : existente.getCantidad();
        if (yaEnCarrito + cantidad > producto.getStock()) {
            throw new IllegalStateException("Existencias insuficientes de '" + producto.getNombre()
                    + "' (disponibles: " + producto.getStock() + ", en carrito: " + yaEnCarrito + ")");
        }
        if (existente != null) existente.sumarCantidad(cantidad);
        else carrito.agregar(new DetalleVenta(producto, cantidad));
    }

    /** Quita la última línea agregada al carrito. */
    public DetalleVenta quitarUltimoDelCarrito() {
        if (carrito.estaVacia()) throw new IllegalStateException("El carrito está vacío");
        return carrito.eliminarEn(carrito.tamano() - 1);
    }

    public void cancelarVenta() { carrito.limpiar(); }

    public double totalCarrito() {
        double total = 0;
        for (DetalleVenta d : carrito) total += d.getSubtotal();
        return total;
    }

    public ListaEnlazada<DetalleVenta> getCarrito() { return carrito; }

    // ---------- Confirmación ----------
    /** Confirma la venta: descuenta existencias, la guarda en el historial y vacía el carrito. */
    public Venta confirmarVenta() {
        if (carrito.estaVacia()) throw new IllegalStateException("El carrito está vacío");
        for (DetalleVenta d : carrito) {
            inventario.descontarStock(d.getCodigoBarras(), d.getCantidad());
        }
        Venta venta = new Venta(siguienteFolio++, carrito);
        historial.apilar(venta);
        carrito.limpiar();
        return venta;
    }

    // ---------- Historial (Pila) ----------
    public Pila<Venta> getHistorial() { return historial; }

    public double totalVendido() {
        double total = 0;
        for (Venta v : historial) total += v.getTotal();
        return total;
    }

    // ---------- Fila de clientes (Cola) ----------
    public void ponerClienteEnEspera(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }
        clientesEnEspera.encolar(nombre.trim());
    }

    public String atenderSiguienteCliente() {
        if (clientesEnEspera.estaVacia()) throw new IllegalStateException("No hay clientes en espera");
        return clientesEnEspera.desencolar();
    }

    public Cola<String> getClientesEnEspera() { return clientesEnEspera; }

    private DetalleVenta buscarEnCarrito(String codigo) {
        for (DetalleVenta d : carrito) {
            if (d.getCodigoBarras().equals(codigo)) return d;
        }
        return null;
    }
}
