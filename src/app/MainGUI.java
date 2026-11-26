package app;

import estructuras.ListaEnlazada;
import modelo.DetalleVenta;
import modelo.Producto;
import modelo.Venta;
import servicio.Inventario;
import servicio.PuntoVenta;
import servicio.RepositorioCsv;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Interfaz gráfica del sistema usando ventanas JOptionPane.
 * Solo pide datos y muestra resultados; la lógica vive en el paquete "servicio".
 */
public class MainGUI {
    private static final String TITULO = "Sistema 3B - Inventario y Punto de Venta";

    private static final String[] OPCIONES = {
        "1. Registrar producto",
        "2. Buscar producto por código de barras",
        "3. Ver catálogo (orden alfabético)",
        "4. Reabastecer producto",
        "5. Eliminar producto",
        "6. Alertas de mercancía baja",
        "7. Realizar venta",
        "8. Historial de ventas",
        "9. Fila de clientes en espera",
        "10. Reporte de inventario",
        "0. Guardar y salir"
    };

    private static final Inventario inventario = new Inventario();
    private static final PuntoVenta caja = new PuntoVenta(inventario);
    private static final RepositorioCsv repositorio = new RepositorioCsv(Paths.get("datos", "inventario.csv"));

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
            // si falla se usa el aspecto por defecto de Java
        }
        SwingUtilities.invokeLater(MainGUI::iniciar);
    }

    private static void iniciar() {
        cargarDatos();
        while (true) {
            Object seleccion = JOptionPane.showInputDialog(null, "¿Qué deseas hacer?", TITULO,
                    JOptionPane.PLAIN_MESSAGE, null, OPCIONES, OPCIONES[0]);
            if (seleccion == null || seleccion.equals(OPCIONES[OPCIONES.length - 1])) break;
            try {
                ejecutar((String) seleccion);
            } catch (NumberFormatException e) {
                error("Revisa que los campos numéricos tengan un número válido.");
            } catch (RuntimeException e) {
                error(e.getMessage());   // errores controlados: el sistema no se cierra
            }
        }
        guardarDatos();
        System.exit(0);
    }

    private static void ejecutar(String opcion) {
        int numero = Integer.parseInt(opcion.substring(0, opcion.indexOf('.')));
        switch (numero) {
            case 1: registrarProducto(); break;
            case 2: buscarProducto(); break;
            case 3: verCatalogo(); break;
            case 4: reabastecer(); break;
            case 5: eliminarProducto(); break;
            case 6: mostrarAlertas(true); break;
            case 7: realizarVenta(); break;
            case 8: verHistorial(); break;
            case 9: menuClientes(); break;
            case 10: verReporte(); break;
            default: break;
        }
    }

    // ---------- Inventario ----------
    private static void registrarProducto() {
        JTextField codigo = new JTextField();
        JTextField nombre = new JTextField();
        JTextField categoria = new JTextField();
        JTextField precio = new JTextField();
        JTextField stock = new JTextField();
        JTextField minimo = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Código de barras:")); panel.add(codigo);
        panel.add(new JLabel("Nombre:")); panel.add(nombre);
        panel.add(new JLabel("Categoría:")); panel.add(categoria);
        panel.add(new JLabel("Precio:")); panel.add(precio);
        panel.add(new JLabel("Existencias:")); panel.add(stock);
        panel.add(new JLabel("Stock mínimo:")); panel.add(minimo);

        int r = JOptionPane.showConfirmDialog(null, panel, "Registrar producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        inventario.registrar(new Producto(codigo.getText(), nombre.getText(), categoria.getText(),
                Double.parseDouble(precio.getText().trim()),
                Integer.parseInt(stock.getText().trim()),
                Integer.parseInt(minimo.getText().trim())));
        info("Producto registrado correctamente.");
    }

    private static void buscarProducto() {
        String codigo = pedirTexto("Código de barras del producto:", "Buscar producto");
        if (codigo == null) return;
        Producto p = inventario.buscarPorCodigo(codigo);
        if (p == null) {
            info("No se encontró ningún producto con ese código.");
            return;
        }
        String estado = p.tieneStockBajo() ? "\n⚠ MERCANCÍA BAJA" : "";
        info("Código: " + p.getCodigoBarras()
                + "\nNombre: " + p.getNombre()
                + "\nCategoría: " + p.getCategoria()
                + "\nPrecio: $" + String.format("%.2f", p.getPrecio())
                + "\nExistencias: " + p.getStock() + " (mínimo " + p.getStockMinimo() + ")" + estado);
    }

    private static void verCatalogo() {
        Producto[] lista = inventario.listarPorNombre();
        if (lista.length == 0) {
            info("El catálogo está vacío.");
            return;
        }
        mostrarTabla("Catálogo de productos", columnasProducto(), filasProducto(lista),
                lista.length + " productos registrados");
    }

    private static void reabastecer() {
        JTextField codigo = new JTextField();
        JTextField cantidad = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Código de barras:")); panel.add(codigo);
        panel.add(new JLabel("Cantidad que entra:")); panel.add(cantidad);

        int r = JOptionPane.showConfirmDialog(null, panel, "Reabastecer producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        inventario.reabastecer(codigo.getText().trim(), Integer.parseInt(cantidad.getText().trim()));
        Producto p = inventario.buscarPorCodigo(codigo.getText());
        info("Existencias actualizadas: " + p.getNombre() + " ahora tiene " + p.getStock() + ".");
    }

    private static void eliminarProducto() {
        String codigo = pedirTexto("Código de barras del producto a eliminar:", "Eliminar producto");
        if (codigo == null) return;
        Producto p = inventario.buscarPorCodigo(codigo);
        if (p == null) {
            info("No se encontró ningún producto con ese código.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(null, "¿Eliminar '" + p.getNombre() + "' del catálogo?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            inventario.eliminar(codigo);
            info("Producto eliminado.");
        }
    }

    /** Muestra los productos con stock bajo. Si avisarSiNoHay es false, no muestra nada cuando no hay alertas. */
    private static void mostrarAlertas(boolean avisarSiNoHay) {
        ListaEnlazada<Producto> bajos = inventario.productosConStockBajo();
        if (bajos.estaVacia()) {
            if (avisarSiNoHay) info("Sin alertas: todo el inventario está por encima del mínimo.");
            return;
        }
        Producto[] arreglo = bajos.aArreglo(new Producto[0]);
        mostrarTabla("⚠ Alertas de mercancía baja", columnasProducto(), filasProducto(arreglo),
                arreglo.length + " producto(s) por reabastecer");
    }

    // ---------- Punto de venta ----------
    private static void realizarVenta() {
        String[] botones = {"Agregar", "Deshacer último", "Terminar"};
        while (true) {
            JTextField codigo = new JTextField();
            JTextField cantidad = new JTextField("1");

            JPanel campos = new JPanel(new GridLayout(0, 2, 6, 6));
            campos.add(new JLabel("Código de barras:")); campos.add(codigo);
            campos.add(new JLabel("Cantidad:")); campos.add(cantidad);

            JTextArea resumen = areaTexto(resumenCarrito(), 12, 38);
            JPanel panel = new JPanel(new BorderLayout(0, 8));
            panel.add(campos, BorderLayout.NORTH);
            panel.add(new JScrollPane(resumen), BorderLayout.CENTER);

            int r = JOptionPane.showOptionDialog(null, panel, "Venta en curso", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, botones, botones[0]);

            if (r == 0) {
                try {
                    caja.agregarAlCarrito(codigo.getText(), Integer.parseInt(cantidad.getText().trim()));
                } catch (NumberFormatException e) {
                    error("La cantidad debe ser un número entero.");
                } catch (RuntimeException e) {
                    error(e.getMessage());
                }
            } else if (r == 1) {
                try {
                    caja.quitarUltimoDelCarrito();
                } catch (RuntimeException e) {
                    error(e.getMessage());
                }
            } else {
                break;   // Terminar o cerrar la ventana
            }
        }

        if (caja.getCarrito().estaVacia()) return;

        int r = JOptionPane.showConfirmDialog(null,
                resumenCarrito() + "\n¿Confirmar la venta?", "Confirmar venta",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            Venta venta = caja.confirmarVenta();
            JOptionPane.showMessageDialog(null, new JScrollPane(areaTexto(venta.generarTicket(), 12, 34)),
                    "Ticket de venta", JOptionPane.PLAIN_MESSAGE);
            mostrarAlertas(false);
        } else {
            caja.cancelarVenta();
            info("Venta cancelada.");
        }
    }

    private static String resumenCarrito() {
        if (caja.getCarrito().estaVacia()) return "(carrito vacío)";
        StringBuilder sb = new StringBuilder();
        for (DetalleVenta d : caja.getCarrito()) {
            sb.append(String.format("%-20s %3d x $%7.2f = $%8.2f%n",
                    d.getNombre(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal()));
        }
        sb.append(String.format("%nTOTAL: $%.2f%n", caja.totalCarrito()));
        return sb.toString();
    }

    private static void verHistorial() {
        if (caja.getHistorial().estaVacia()) {
            info("Aún no hay ventas registradas.");
            return;
        }
        Object[][] filas = new Object[caja.getHistorial().tamano()][];
        int i = 0;
        for (Venta v : caja.getHistorial()) {   // la más reciente primero (Pila)
            filas[i++] = new Object[]{v.getFolio(), v.getTotalArticulos(), String.format("$%.2f", v.getTotal())};
        }
        mostrarTabla("Historial de ventas (más reciente primero)",
                new String[]{"Folio", "Artículos", "Total"}, filas,
                String.format("Total vendido: $%.2f", caja.totalVendido()));
    }

    private static void menuClientes() {
        String[] botones = {"Agregar a la fila", "Atender siguiente", "Ver fila", "Cerrar"};
        int r = JOptionPane.showOptionDialog(null,
                "Clientes en espera: " + caja.getClientesEnEspera().tamano(), "Fila de clientes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, botones, botones[0]);
        if (r == 0) {
            String nombre = pedirTexto("Nombre del cliente:", "Fila de clientes");
            if (nombre != null) {
                caja.ponerClienteEnEspera(nombre);
                info(nombre.trim() + " quedó en la fila.");
            }
        } else if (r == 1) {
            info("Atendiendo a: " + caja.atenderSiguienteCliente());
        } else if (r == 2) {
            if (caja.getClientesEnEspera().estaVacia()) {
                info("No hay clientes en espera.");
                return;
            }
            StringBuilder sb = new StringBuilder();
            int turno = 1;
            for (String c : caja.getClientesEnEspera()) sb.append(turno++).append(". ").append(c).append('\n');
            info(sb.toString());
        }
    }

    private static void verReporte() {
        info("Productos registrados: " + inventario.totalProductos()
                + String.format("%nValor del inventario: $%.2f", inventario.valorTotal())
                + "\nProductos con alerta: " + inventario.productosConStockBajo().tamano()
                + String.format("%nVentas de esta sesión: $%.2f", caja.totalVendido()));
    }

    // ---------- Persistencia ----------
    private static void cargarDatos() {
        try {
            if (repositorio.cargar(inventario) == 0) {
                inventario.registrar(new Producto("7501000111111", "Refresco 600ml", "Bebidas", 18.50, 40, 10));
                inventario.registrar(new Producto("7501000222222", "Galletas Marías", "Abarrotes", 15.00, 8, 10));
                inventario.registrar(new Producto("7501000333333", "Leche entera 1L", "Lácteos", 27.00, 25, 12));
                inventario.registrar(new Producto("7501000444444", "Pan de caja", "Panadería", 42.00, 5, 6));
                inventario.registrar(new Producto("7501000555555", "Jabón de tocador", "Limpieza", 12.00, 30, 8));
                info("Se cargaron productos de ejemplo.\nCódigo para probar: 7501000111111");
            }
        } catch (IOException | RuntimeException e) {
            error("No se pudo leer el inventario guardado: " + e.getMessage());
        }
    }

    private static void guardarDatos() {
        try {
            repositorio.guardar(inventario);
        } catch (IOException e) {
            error("No se pudo guardar el inventario: " + e.getMessage());
        }
    }

    // ---------- Utilidades de ventanas ----------
    private static String pedirTexto(String mensaje, String titulo) {
        String texto = JOptionPane.showInputDialog(null, mensaje, titulo, JOptionPane.QUESTION_MESSAGE);
        return (texto == null || texto.trim().isEmpty()) ? null : texto.trim();
    }

    private static void info(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, TITULO, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void error(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static JTextArea areaTexto(String texto, int filas, int columnas) {
        JTextArea area = new JTextArea(texto, filas, columnas);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setEditable(false);
        return area;
    }

    private static String[] columnasProducto() {
        return new String[]{"Código", "Nombre", "Categoría", "Precio", "Existencias", "Mínimo", "Estado"};
    }

    private static Object[][] filasProducto(Producto[] productos) {
        Object[][] filas = new Object[productos.length][];
        for (int i = 0; i < productos.length; i++) {
            Producto p = productos[i];
            filas[i] = new Object[]{p.getCodigoBarras(), p.getNombre(), p.getCategoria(),
                    String.format("$%.2f", p.getPrecio()), p.getStock(), p.getStockMinimo(),
                    p.tieneStockBajo() ? "BAJO" : "OK"};
        }
        return filas;
    }

    private static void mostrarTabla(String titulo, String[] columnas, Object[][] filas, String pie) {
        DefaultTableModel modelo = new DefaultTableModel(filas, columnas) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(680, 260));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(scroll, BorderLayout.CENTER);
        JLabel etiqueta = new JLabel(pie);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panel.add(etiqueta, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(null, panel, titulo, JOptionPane.PLAIN_MESSAGE);
    }
}
