package modelo;

import estructuras.ListaEnlazada;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Una venta confirmada: folio, fecha y lista de detalles. Es inmutable una vez creada. */
public class Venta {
    private final int folio;
    private final LocalDateTime fecha;
    private final ListaEnlazada<DetalleVenta> detalles = new ListaEnlazada<>();

    public Venta(int folio, ListaEnlazada<DetalleVenta> detalles) {
        if (detalles == null || detalles.estaVacia()) {
            throw new IllegalArgumentException("Una venta necesita al menos un producto");
        }
        this.folio = folio;
        this.fecha = LocalDateTime.now();
        for (DetalleVenta d : detalles) this.detalles.agregar(d);   // copia propia
    }

    public int getFolio() { return folio; }
    public LocalDateTime getFecha() { return fecha; }

    public double getTotal() {
        double total = 0;
        for (DetalleVenta d : detalles) total += d.getSubtotal();
        return total;
    }

    public int getTotalArticulos() {
        int total = 0;
        for (DetalleVenta d : detalles) total += d.getCantidad();
        return total;
    }

    /** Genera el texto del ticket de compra. */
    public String generarTicket() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== TIENDAS 3B ==========\n");
        sb.append("Folio: ").append(folio).append('\n');
        sb.append("Fecha: ").append(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append('\n');
        sb.append("--------------------------------\n");
        for (DetalleVenta d : detalles) {
            sb.append(String.format(Locale.US, "%-18s %3d x $%7.2f = $%8.2f%n",
                    d.getNombre(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal()));
        }
        sb.append("--------------------------------\n");
        sb.append(String.format(Locale.US, "TOTAL: $%.2f (%d artículos)%n", getTotal(), getTotalArticulos()));
        sb.append("¡Gracias por su compra!\n");
        return sb.toString();
    }
}
