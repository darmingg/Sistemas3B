package modelo;

import java.util.Comparator;

/**
 * Clase de dominio: un producto del catálogo de Tiendas 3B.
 * Atributos privados y validaciones dentro de la clase (encapsulamiento).
 * El código de barras es la identidad del producto y no puede cambiar.
 */
public class Producto {
    public static final Comparator<Producto> POR_CODIGO =
            (a, b) -> a.codigoBarras.compareTo(b.codigoBarras);
    public static final Comparator<Producto> POR_NOMBRE =
            (a, b) -> a.nombre.compareToIgnoreCase(b.nombre);

    private final String codigoBarras;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;
    private int stockMinimo;

    public Producto(String codigoBarras, String nombre, String categoria,
                    double precio, int stock, int stockMinimo) {
        this.codigoBarras = limpiarTexto(codigoBarras, "El código de barras");
        setNombre(nombre);
        setCategoria(categoria);
        setPrecio(precio);
        setStock(stock);
        setStockMinimo(stockMinimo);
    }

    // ---------- Getters ----------
    public String getCodigoBarras() { return codigoBarras; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public int getStockMinimo() { return stockMinimo; }

    // ---------- Setters con validación ----------
    public void setNombre(String nombre) {
        this.nombre = limpiarTexto(nombre, "El nombre");
    }

    public void setCategoria(String categoria) {
        this.categoria = limpiarTexto(categoria, "La categoría");
    }

    public void setPrecio(double precio) {
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo");
        this.precio = precio;
    }

    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo");
        this.stock = stock;
    }

    public void setStockMinimo(int stockMinimo) {
        if (stockMinimo < 0) throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        this.stockMinimo = stockMinimo;
    }

    // ---------- Comportamiento ----------
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        stock += cantidad;
    }

    public void reducirStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        if (cantidad > stock) {
            throw new IllegalStateException("Existencias insuficientes de '" + nombre
                    + "' (disponibles: " + stock + ")");
        }
        stock -= cantidad;
    }

    /** Un producto tiene stock bajo cuando sus existencias son iguales o menores al mínimo. */
    public boolean tieneStockBajo() {
        return stock <= stockMinimo;
    }

    // ---------- Persistencia (formato CSV) ----------
    public String aCsv() {
        return String.join(",", codigoBarras, nombre, categoria,
                String.valueOf(precio), String.valueOf(stock), String.valueOf(stockMinimo));
    }

    public static Producto desdeCsv(String linea) {
        String[] c = linea.split(",", -1);
        if (c.length != 6) throw new IllegalArgumentException("Línea CSV inválida: " + linea);
        return new Producto(c[0], c[1], c[2],
                Double.parseDouble(c[3]), Integer.parseInt(c[4]), Integer.parseInt(c[5]));
    }

    /** Quita espacios y comas (para no romper el CSV) y valida que no quede vacío. */
    private static String limpiarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío");
        }
        return texto.replace(",", " ").trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto)) return false;
        return codigoBarras.equals(((Producto) o).codigoBarras);
    }

    @Override
    public int hashCode() { return codigoBarras.hashCode(); }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "%-14s %-24s %-12s $%8.2f  stock: %4d (mín. %d)",
                codigoBarras, nombre, categoria, precio, stock, stockMinimo);
    }
}
