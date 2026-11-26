package pruebas;

import estructuras.Cola;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import modelo.Producto;
import modelo.Venta;
import servicio.Busqueda;
import servicio.Inventario;
import servicio.Ordenamiento;
import servicio.PuntoVenta;
import servicio.RepositorioCsv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Pruebas por módulo, sin librerías externas (se ejecutan con "java pruebas.PruebasModulos").
 * Cada prueba imprime OK o FALLO; si algo falla el programa termina con código 1.
 */
public class PruebasModulos {
    private static int aprobadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) throws Exception {
        pruebasListaEnlazada();
        pruebasPila();
        pruebasCola();
        pruebasProducto();
        pruebasOrdenamiento();
        pruebasBusqueda();
        pruebasInventario();
        pruebasPuntoVenta();
        pruebasPersistencia();

        System.out.println("\nResultado: " + aprobadas + " aprobadas, " + fallidas + " fallidas.");
        if (fallidas > 0) System.exit(1);
    }

    // ---------- Módulo: ListaEnlazada ----------
    private static void pruebasListaEnlazada() {
        titulo("ListaEnlazada");
        ListaEnlazada<String> l = new ListaEnlazada<>();
        verificar("nueva lista está vacía", l.estaVacia());
        l.agregar("B"); l.agregar("C"); l.agregarAlInicio("A");
        verificar("tamaño 3", l.tamano() == 3);
        verificar("orden A,B,C", l.obtener(0).equals("A") && l.obtener(1).equals("B") && l.obtener(2).equals("C"));
        verificar("eliminarEn(1) devuelve B", l.eliminarEn(1).equals("B"));
        verificar("eliminar último y seguir agregando", l.eliminarEn(1).equals("C") && l.tamano() == 1);
        l.agregar("D");
        verificar("agregar tras eliminar el último", l.obtener(1).equals("D"));
        verificar("indiceDe encuentra D", l.indiceDe("D") == 1);
        verificar("eliminarElemento inexistente = false", !l.eliminarElemento("Z"));
        verificarExcepcion("obtener fuera de rango", () -> l.obtener(9), IndexOutOfBoundsException.class);
    }

    // ---------- Módulo: Pila ----------
    private static void pruebasPila() {
        titulo("Pila");
        Pila<Integer> p = new Pila<>();
        p.apilar(1); p.apilar(2); p.apilar(3);
        verificar("verTope = 3", p.verTope() == 3);
        verificar("desapilar sale en orden LIFO", p.desapilar() == 3 && p.desapilar() == 2);
        verificar("tamaño 1", p.tamano() == 1);
        p.desapilar();
        verificar("pila vacía", p.estaVacia());
        verificarExcepcion("desapilar pila vacía", p::desapilar, java.util.NoSuchElementException.class);
    }

    // ---------- Módulo: Cola ----------
    private static void pruebasCola() {
        titulo("Cola");
        Cola<String> c = new Cola<>();
        c.encolar("Ana"); c.encolar("Luis"); c.encolar("Marta");
        verificar("verFrente = Ana", c.verFrente().equals("Ana"));
        verificar("desencolar sale en orden FIFO", c.desencolar().equals("Ana") && c.desencolar().equals("Luis"));
        c.desencolar();
        verificar("cola vacía", c.estaVacia());
        c.encolar("Nuevo");
        verificar("se puede reutilizar tras vaciarse", c.verFrente().equals("Nuevo"));
        c.desencolar();
        verificarExcepcion("desencolar cola vacía", c::desencolar, java.util.NoSuchElementException.class);
    }

    // ---------- Módulo: Producto ----------
    private static void pruebasProducto() {
        titulo("Producto");
        Producto p = new Producto("100", "Leche", "Lácteos", 25.5, 10, 4);
        verificar("datos guardados", p.getNombre().equals("Leche") && p.getStock() == 10);
        p.reducirStock(3);
        verificar("reducirStock", p.getStock() == 7);
        p.aumentarStock(2);
        verificar("aumentarStock", p.getStock() == 9);
        verificar("stock 9 > mínimo 4: sin alerta", !p.tieneStockBajo());
        p.reducirStock(5);
        verificar("stock 4 = mínimo: alerta", p.tieneStockBajo());
        verificarExcepcion("reducir más del stock", () -> p.reducirStock(99), IllegalStateException.class);
        verificarExcepcion("precio negativo", () -> new Producto("1", "X", "Y", -1, 1, 1), IllegalArgumentException.class);
        verificarExcepcion("nombre vacío", () -> new Producto("1", " ", "Y", 1, 1, 1), IllegalArgumentException.class);
        Producto copia = Producto.desdeCsv(p.aCsv());
        verificar("ida y vuelta CSV", copia.getCodigoBarras().equals("100") && copia.getStock() == p.getStock());
    }

    // ---------- Módulo: Ordenamiento ----------
    private static void pruebasOrdenamiento() {
        titulo("Ordenamiento");
        Comparator<Integer> asc = Integer::compare;
        Integer[] base = {5, 3, 9, 1, 7, 3, 0};
        Integer[] a = base.clone(), b = base.clone(), c = base.clone();
        Ordenamiento.burbuja(a, asc);
        Ordenamiento.insercion(b, asc);
        Ordenamiento.mergeSort(c, asc);
        Integer[] esperado = {0, 1, 3, 3, 5, 7, 9};
        verificar("burbuja", java.util.Arrays.equals(a, esperado));
        verificar("inserción", java.util.Arrays.equals(b, esperado));
        verificar("mergeSort", java.util.Arrays.equals(c, esperado));
        Integer[] vacio = {};
        Ordenamiento.mergeSort(vacio, asc);
        verificar("arreglo vacío no falla", vacio.length == 0);
    }

    // ---------- Módulo: Búsqueda ----------
    private static void pruebasBusqueda() {
        titulo("Búsqueda");
        Integer[] a = {1, 3, 5, 7, 9, 11};
        verificar("binaria encuentra 7", Busqueda.binaria(a, 7, Integer::compare) == 3);
        verificar("binaria encuentra extremos", Busqueda.binaria(a, 1, Integer::compare) == 0
                && Busqueda.binaria(a, 11, Integer::compare) == 5);
        verificar("binaria no encuentra 4", Busqueda.binaria(a, 4, Integer::compare) == -1);
        verificar("lineal encuentra 9", Busqueda.lineal(a, 9, Integer::compare) == 4);
        verificar("lineal no encuentra 100", Busqueda.lineal(a, 100, Integer::compare) == -1);
    }

    // ---------- Módulo: Inventario ----------
    private static void pruebasInventario() {
        titulo("Inventario");
        Inventario inv = crearInventario();
        verificar("3 productos", inv.totalProductos() == 3);
        verificar("buscar existente", inv.buscarPorCodigo("200").getNombre().equals("Galletas"));
        verificar("buscar inexistente = null", inv.buscarPorCodigo("999") == null);
        verificarExcepcion("código repetido", () -> inv.registrar(new Producto("200", "Otro", "X", 1, 1, 1)),
                IllegalArgumentException.class);
        Producto[] porNombre = inv.listarPorNombre();
        verificar("orden alfabético", porNombre[0].getNombre().equals("Galletas")
                && porNombre[2].getNombre().equals("Refresco"));
        verificar("alerta: solo Galletas (3 <= 5)", inv.productosConStockBajo().tamano() == 1
                && inv.productosConStockBajo().obtener(0).getCodigoBarras().equals("200"));
        inv.reabastecer("200", 20);
        verificar("reabastecer quita la alerta", inv.productosConStockBajo().estaVacia());
        verificar("eliminar existente", inv.eliminar("100") && inv.buscarPorCodigo("100") == null);
        verificar("eliminar inexistente = false", !inv.eliminar("100"));
        verificar("el índice sigue válido tras eliminar", inv.buscarPorCodigo("300") != null);
    }

    // ---------- Módulo: PuntoVenta ----------
    private static void pruebasPuntoVenta() {
        titulo("PuntoVenta");
        Inventario inv = crearInventario();
        PuntoVenta caja = new PuntoVenta(inv);
        caja.agregarAlCarrito("100", 2);          // Refresco 10.00
        caja.agregarAlCarrito("100", 1);          // se suma a la misma línea
        caja.agregarAlCarrito("300", 1);          // Leche 20.00
        verificar("carrito con 2 líneas", caja.getCarrito().tamano() == 2);
        verificar("total parcial = 50", Math.abs(caja.totalCarrito() - 50.0) < 0.001);
        verificarExcepcion("más que el stock", () -> caja.agregarAlCarrito("200", 10), IllegalStateException.class);
        verificarExcepcion("producto inexistente", () -> caja.agregarAlCarrito("999", 1), IllegalArgumentException.class);
        verificarExcepcion("cantidad cero", () -> caja.agregarAlCarrito("100", 0), IllegalArgumentException.class);

        Venta v = caja.confirmarVenta();
        verificar("folio 1 y total 50", v.getFolio() == 1 && Math.abs(v.getTotal() - 50.0) < 0.001);
        verificar("descuenta stock (20 -> 17)", inv.buscarPorCodigo("100").getStock() == 17);
        verificar("carrito vacío tras vender", caja.getCarrito().estaVacia());
        verificar("ticket contiene el total", v.generarTicket().contains("TOTAL: $50.00"));
        verificarExcepcion("confirmar carrito vacío", caja::confirmarVenta, IllegalStateException.class);

        caja.agregarAlCarrito("300", 2);
        Venta v2 = caja.confirmarVenta();
        verificar("historial: la última venta está arriba", caja.getHistorial().verTope() == v2
                && caja.getHistorial().tamano() == 2);
        verificar("total vendido = 90", Math.abs(caja.totalVendido() - 90.0) < 0.001);

        caja.agregarAlCarrito("100", 1);
        caja.agregarAlCarrito("300", 1);
        caja.quitarUltimoDelCarrito();
        verificar("deshacer quita la última línea", caja.getCarrito().tamano() == 1);

        caja.ponerClienteEnEspera("Ana");
        caja.ponerClienteEnEspera("Luis");
        verificar("la fila atiende por orden de llegada", caja.atenderSiguienteCliente().equals("Ana"));
    }

    // ---------- Módulo: Persistencia ----------
    private static void pruebasPersistencia() throws Exception {
        titulo("Persistencia CSV");
        Path temporal = Files.createTempFile("inventario3b", ".csv");
        RepositorioCsv repo = new RepositorioCsv(temporal);
        repo.guardar(crearInventario());
        Inventario cargado = new Inventario();
        verificar("carga 3 productos", repo.cargar(cargado) == 3);
        verificar("datos íntegros", cargado.buscarPorCodigo("300").getPrecio() == 20.0);
        Files.deleteIfExists(temporal);
        verificar("archivo inexistente carga 0", new RepositorioCsv(temporal).cargar(new Inventario()) == 0);
    }

    // ---------- Utilidades de prueba ----------
    private static Inventario crearInventario() {
        Inventario inv = new Inventario();
        inv.registrar(new Producto("100", "Refresco", "Bebidas", 10.0, 20, 5));
        inv.registrar(new Producto("200", "Galletas", "Abarrotes", 8.0, 3, 5));
        inv.registrar(new Producto("300", "Leche", "Lácteos", 20.0, 15, 4));
        return inv;
    }

    private static void titulo(String modulo) {
        System.out.println("\n[" + modulo + "]");
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            aprobadas++;
            System.out.println("  OK    " + descripcion);
        } else {
            fallidas++;
            System.out.println("  FALLO " + descripcion);
        }
    }

    private static void verificarExcepcion(String descripcion, Runnable accion, Class<? extends Throwable> tipo) {
        try {
            accion.run();
            verificar(descripcion + " (debía lanzar " + tipo.getSimpleName() + ")", false);
        } catch (Throwable t) {
            verificar(descripcion, tipo.isInstance(t));
        }
    }
}
