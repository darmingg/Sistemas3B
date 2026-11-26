package modelo;

/**
 * Una línea de la venta. Guarda una "foto" del nombre y precio al momento de vender,
 * así el ticket no cambia aunque después se modifique el producto.
 */
public class DetalleVenta {
    private final String codigoBarras;
    private final String nombre;
    private final double precioUnitario;
    private int cantidad;

    public DetalleVenta(Producto producto, int cantidad) {
        if (producto == null) throw new IllegalArgumentException("El producto es obligatorio");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        this.codigoBarras = producto.getCodigoBarras();
        this.nombre = producto.getNombre();
        this.precioUnitario = producto.getPrecio();
        this.cantidad = cantidad;
    }

    public void sumarCantidad(int extra) {
        if (extra <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        cantidad += extra;
    }

    public String getCodigoBarras() { return codigoBarras; }
    public String getNombre() { return nombre; }
    public double getPrecioUnitario() { return precioUnitario; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return precioUnitario * cantidad; }
}
