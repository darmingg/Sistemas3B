# Sistema 3B - Inventario y Punto de Venta

**Proyecto final de Estructura de Datos**

| | |
|---|---|
| **Institución** | Instituto Tecnológico Superior de Teziutlán |
| **Carrera** | Ingeniería en Sistemas Computacionales |
| **Materia** | Estructura de Datos |
| **Grupo** | B |
| **Docente** | Román Gutiérrez Marcos |
| **Equipo** | 5 |
| **Cliente** | Tiendas 3B |

## Integrantes

| Integrante | Rol |
|---|---|
| Carlos Sánchez Rodríguez | Líder / Lógica |
| Darío Marcelo González | Desarrollador |
| Juan Jafett Bautista Zaragoza | Programador |
| Brenda Ortiz Hernández | Pruebas / Documentación |

## Objetivo

Desarrollar un sistema de inventario y punto de venta para Tiendas 3B que aplique
estructuras de datos propias (lista enlazada, pila y cola) y algoritmos de búsqueda
y ordenamiento en un caso de uso real.

## Alcances

- Registro y catalogación de productos
- Búsqueda rápida por código de barras
- Interfaz gráfica sencilla para el personal
- Alertas de mercancía baja
- Reportes
- Punto de venta básico que descuenta existencias

## Estructura del proyecto

    src/
    ├── app/          Main (consola) y MainGUI (interfaz gráfica)
    ├── estructuras/  Nodo, ListaEnlazada, Pila, Cola
    ├── modelo/       Producto, Venta, DetalleVenta
    ├── servicio/     Inventario, PuntoVenta, Busqueda, Ordenamiento, RepositorioCsv
    └── pruebas/      PruebasModulos, EvaluacionAlgoritmos

## Estructuras de datos y algoritmos

| Componente | Archivo | Uso en el sistema |
|---|---|---|
| Lista enlazada | `ListaEnlazada.java` | [Ej. almacenar el catálogo de productos] |
| Pila | `Pila.java` | [Ej. deshacer la última operación] |
| Cola | `Cola.java` | [Ej. cola de ventas pendientes] |
| Búsqueda | `Busqueda.java` | [Algoritmo(s) usado(s)] |
| Ordenamiento | `Ordenamiento.java` | [Algoritmo(s) usado(s)] |

## Persistencia

Los datos se guardan en archivos CSV mediante `RepositorioCsv.java`.
Formato: [columnas del CSV de productos].

## Requisitos

- Java JDK [26.0.2]

## Compilación y ejecución

    javac -d out src/app/*.java src/estructuras/*.java src/modelo/*.java src/servicio/*.java
    java -cp out app.Main

Interfaz gráfica:

    java -cp out app.MainGUI

## Pruebas

    javac -d out src/pruebas/*.java
    java -cp out pruebas.PruebasModulos
    java -cp out pruebas.EvaluacionAlgoritmos

## Etapas del proyecto

Requerimientos → Diseño → Desarrollo → Pruebas → Producto final

## Entrega

Fecha de entrega: [25/11/2026]
