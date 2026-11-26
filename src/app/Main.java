package app;

import estructuras.ListaEnlazada;
import modelo.DetalleVenta;
import modelo.Producto;
import modelo.Venta;
import servicio.Inventario;
import servicio.PuntoVenta;
import servicio.RepositorioCsv;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

/**
 * Interfaz de consola para el personal de Tiendas 3B.
 * Solo se encarga de pedir datos y mostrar resultados; la lógica vive en el paquete "servicio".
 */
public class Main {
    private static final Scanner entrada = new Scanner(System.in);
    private static final Inventario inventario = new Inventario();
    private static final PuntoVenta caja = new PuntoVenta(inventario);
    private static final RepositorioCsv repositorio = new RepositorioCsv(Paths.get("datos", "inventario.csv"));

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        cargarDatos();

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opción: ");
            try {
                ejecutar(opcion);
            } catch (RuntimeException e) {
                System.out.println("  ✖ " + e.getMessage());   // errores controlados, el sistema no se cierra
            }
        } while (opcion != 0);

        guardarDatos();
        System.out.println("Hasta pronto.");
    }

    private static void mostrarMenu() {
        System.out.println("\n===== SISTEMA 3B: INVENTARIO Y PUNTO DE VENTA =====");
        System.out.println(" 1. Registrar producto");
        System.out.println(" 2. Buscar producto por código de barras");
        System.out.println(" 3. Listar catálogo (orden alfabético)");
        System.out.println(" 4. Reabastecer producto");
        System.out.println(" 5. Eliminar producto");
        System.out.println(" 6. Alertas de mercancía baja");
        System.out.println(" 7. Realizar venta");
        System.out.println(" 8. Historial de ventas");
        System.out.println(" 9. Fila de clientes en espera");
        System.out.println("10. Reporte de inventario");
        System.out.println(" 0. Guardar y salir");
    }

    private static void ejecutar(int opcion) {
        switch (opcion) {
            case 1: registrarProducto(); break;
            case 2: buscarProducto(); break;
            case 3: listarCatalogo(); break;
            case 4: reabastecer(); break;
            case 5: eliminarProducto(); break;
            case 6: mostrarAlertas(); break;
            case 7: realizarVenta(); break;
            case 8: mostrarHistorial(); break;
            case 9: menuClientes(); break;
            case 10: mostrarReporte(); break;
            case 0: break;
            default: System.out.println("  Opción no válida.");
        }
    }

    // ---------- Inventario ----------
    private static void registrarProducto() {
        String codigo = leerTexto("Código de barras: ");
        String nombre = leerTexto("Nombre: ");
        String categoria = leerTexto("Categoría: ");
        double precio = leerDecimal("Precio: ");
        int stock = leerEntero("Existencias: ");
        int minimo = leerEntero("Stock mínimo: ");
        inventario.registrar(new Producto(codigo, nombre, categoria, precio, stock, minimo));
        System.out.println("  ✔ Producto registrado.");
    }

    private static void buscarProducto() {
        Producto p = inventario.buscarPorCodigo(leerTexto("Código de barras: "));
        System.out.println(p == null ? "  No se encontró el producto." : "  " + p);
    }

    private static void listarCatalogo() {
        Producto[] lista = inventario.listarPorNombre();
        if (lista.length == 0) System.out.println("  El catálogo está vacío.");
        for (Producto p : lista) System.out.println("  " + p);
    }

    private static void reabastecer() {
        inventario.reabastecer(leerTexto("Código de barras: "), leerEntero("Cantidad que entra: "));
        System.out.println("  ✔ Existencias actualizadas.");
    }

    private static void eliminarProducto() {
        boolean eliminado = inventario.eliminar(leerTexto("Código de barras: "));
        System.out.println(eliminado ? "  ✔ Producto eliminado." : "  No se encontró el producto.");
    }

    private static void mostrarAlertas() {
        ListaEnlazada<Producto> bajos = inventario.productosConStockBajo();
        if (bajos.estaVacia()) {
            System.out.println("  Sin alertas: todo el inventario está por encima del mínimo.");
            return;
        }
        System.out.println("  ⚠ Mercancía baja (" + bajos.tamano() + "):");
        for (Producto p : bajos) System.out.println("  " + p);
    }

    // ---------- Punto de venta ----------
    private static void realizarVenta() {
        System.out.println("  (escribe el código; deja vacío para terminar. 'u' deshace el último)");
        while (true) {
            String codigo = leerTexto("  Código: ", true);
            if (codigo.isEmpty()) break;
            try {
                if (codigo.equalsIgnoreCase("u")) {
                    DetalleVenta quitado = caja.quitarUltimoDelCarrito();
                    System.out.println("  Se quitó: " + quitado.getNombre());
                } else {
                    caja.agregarAlCarrito(codigo, leerEntero("  Cantidad: "));
                }
                System.out.printf("  Total parcial: $%.2f%n", caja.totalCarrito());
            } catch (RuntimeException e) {
                System.out.println("  ✖ " + e.getMessage());
            }
        }
        if (caja.getCarrito().estaVacia()) {
            System.out.println("  Venta sin productos.");
            return;
        }
        String respuesta = leerTexto("¿Confirmar venta por $" + String.format("%.2f", caja.totalCarrito()) + "? (s/n): ");
        if (respuesta.equalsIgnoreCase("s")) {
            Venta venta = caja.confirmarVenta();
            System.out.println();
            System.out.println(venta.generarTicket());
            mostrarAlertas();
        } else {
            caja.cancelarVenta();
            System.out.println("  Venta cancelada.");
        }
    }

    private static void mostrarHistorial() {
        if (caja.getHistorial().estaVacia()) {
            System.out.println("  Aún no hay ventas.");
            return;
        }
        System.out.println("  Ventas (la más reciente primero):");
        for (Venta v : caja.getHistorial()) {
            System.out.printf("  Folio %d | %d artículos | $%.2f%n", v.getFolio(), v.getTotalArticulos(), v.getTotal());
        }
        System.out.printf("  Total vendido: $%.2f%n", caja.totalVendido());
    }

    private static void menuClientes() {
        System.out.println("  1. Agregar cliente a la fila   2. Atender siguiente   3. Ver fila");
        int op = leerEntero("  Opción: ");
        if (op == 1) {
            caja.ponerClienteEnEspera(leerTexto("  Nombre del cliente: "));
            System.out.println("  ✔ Cliente en espera.");
        } else if (op == 2) {
            System.out.println("  Atendiendo a: " + caja.atenderSiguienteCliente());
        } else if (op == 3) {
            if (caja.getClientesEnEspera().estaVacia()) System.out.println("  No hay clientes en espera.");
            int turno = 1;
            for (String c : caja.getClientesEnEspera()) System.out.println("  " + turno++ + ". " + c);
        }
    }

    private static void mostrarReporte() {
        System.out.println("  Productos registrados: " + inventario.totalProductos());
        System.out.printf("  Valor del inventario:  $%.2f%n", inventario.valorTotal());
        System.out.println("  Productos con alerta:  " + inventario.productosConStockBajo().tamano());
        System.out.printf("  Ventas de esta sesión: $%.2f%n", caja.totalVendido());
    }

    // ---------- Persistencia ----------
    private static void cargarDatos() {
        try {
            int cargados = repositorio.cargar(inventario);
            if (cargados == 0) {
                cargarDatosDeEjemplo();
                System.out.println("Se cargaron productos de ejemplo.");
            } else {
                System.out.println("Se cargaron " + cargados + " productos.");
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("No se pudo leer el inventario guardado: " + e.getMessage());
        }
    }

    private static void guardarDatos() {
        try {
            repositorio.guardar(inventario);
            System.out.println("Inventario guardado.");
        } catch (IOException e) {
            System.out.println("No se pudo guardar el inventario: " + e.getMessage());
        }
    }

    private static void cargarDatosDeEjemplo() {
        inventario.registrar(new Producto("7501000111111", "Refresco 600ml", "Bebidas", 18.50, 40, 10));
        inventario.registrar(new Producto("7501000222222", "Galletas Marías", "Abarrotes", 15.00, 8, 10));
        inventario.registrar(new Producto("7501000333333", "Leche entera 1L", "Lácteos", 27.00, 25, 12));
        inventario.registrar(new Producto("7501000444444", "Pan de caja", "Panadería", 42.00, 5, 6));
        inventario.registrar(new Producto("7501000555555", "Jabón de tocador", "Limpieza", 12.00, 30, 8));
    }

    // ---------- Lectura segura de datos ----------
    private static String leerTexto(String mensaje) { return leerTexto(mensaje, false); }

    private static String leerTexto(String mensaje, boolean permitirVacio) {
        System.out.print(mensaje);
        String texto = entrada.nextLine().trim();
        return texto;
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(entrada.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Escribe un número entero válido.");
            }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(entrada.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Escribe un número válido (ejemplo: 18.50).");
            }
        }
    }
}
