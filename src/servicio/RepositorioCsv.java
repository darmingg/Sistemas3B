package servicio;

import modelo.Producto;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Guarda y carga el inventario en un archivo de texto CSV para no perder datos al cerrar. */
public class RepositorioCsv {
    private final Path archivo;

    public RepositorioCsv(Path archivo) {
        this.archivo = archivo;
    }

    public void guardar(Inventario inventario) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Producto p : inventario.obtenerTodos()) lineas.add(p.aCsv());
        if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());
        Files.write(archivo, lineas, StandardCharsets.UTF_8);
    }

    /** Carga los productos del archivo al inventario. Devuelve cuántos cargó. */
    public int cargar(Inventario inventario) throws IOException {
        if (!Files.exists(archivo)) return 0;
        int cargados = 0;
        for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
            if (linea.trim().isEmpty()) continue;
            inventario.registrar(Producto.desdeCsv(linea));
            cargados++;
        }
        return cargados;
    }
}
