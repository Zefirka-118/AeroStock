# Sistema de Inventario — Almacén

> **Español / English**
> Este documento está disponible en los dos idiomas. La versión en español aparece primero; la versión en inglés está más abajo, de forma separada.
> This document is available in both languages. The Spanish version comes first; the English version is further below, as a separate section.

---

## 🇪🇸 Español

### Descripción

Aplicación de escritorio en **Java Swing** para gestionar el inventario de un almacén (refrescos, botanas y pan dulce). Permite dar de alta, modificar, eliminar y buscar productos, así como registrar entradas y salidas de stock. Toda la información se guarda en un archivo de texto plano (`inventario.txt`), sin bases de datos ni dependencias externas.

Fue desarrollada como proyecto individual para la materia **Programación III** (Interfaces Gráficas con Java).

### Características

- **Gestión de productos**: alta, modificación, eliminación y búsqueda (con navegación cíclica entre coincidencias).
- **Movimientos de almacén**: registro de entradas y salidas, cada uno con ID automático, fecha/hora y responsable.
- **Validaciones**: campos obligatorios, cantidades numéricas no negativas, stock suficiente antes de una salida, y restricción del carácter `|` (usado como separador en el archivo).
- **Persistencia simple**: el archivo `inventario.txt` se crea automáticamente la primera vez, con 45 productos iniciales (refrescos, Sabritas y productos Marinela).
- **Interfaz personalizada**: estilo visual inspirado en Windows 7 / Aero —degradados, bordes suaves, brillo translúcido y estados de interacción (normal, hover, presionado, deshabilitado)— aplicado sobre los componentes estándar de Swing sin tocar su lógica.

### Estructura del proyecto

```
InventarioAlmacen_Progra3/
├── pom.xml
└── src/main/java/progra3/plan2023/inventarioalmacen/
    ├── InventarioAlmacen.java   # Ventana principal: lógica, eventos y persistencia
    ├── InventarioAlmacen.form   # Diseño de la interfaz (NetBeans GUI Builder)
    ├── EstiloWindows7.java      # Apariencia visual (solo estilo, no lógica)
    ├── Producto.java            # Modelo de producto
    └── Movimiento.java          # Modelo de entrada/salida
```

La separación es intencional: `EstiloWindows7` únicamente cambia el dibujo de los componentes (colores, bordes, fuentes); no interfiere con los `ActionListener` ni con los datos.

### Requisitos

- **JDK 18 o superior**
- **Maven** (opcional, solo si se quiere compilar por línea de comandos; también se puede abrir y ejecutar directamente desde NetBeans)

### Cómo ejecutarlo

**Opción 1 — NetBeans**
1. Abrir el proyecto desde `Archivo → Abrir Proyecto`.
2. Ejecutar con el botón ▶ (Run Project).

**Opción 2 — Línea de comandos**
```bash
# Compilar
mvn clean package

# Ejecutar
java -cp target/classes progra3.plan2023.inventarioalmacen.InventarioAlmacen
```

El archivo `inventario.txt` se genera en el directorio desde donde se ejecuta el programa.

### Formato del archivo de datos

```
PRODUCTOS
nombre|marca|tamaño|peso|cantidad

MOVIMIENTOS
id|tipo|producto|cantidad|persona|fecha y hora
```

### Comentarios personales

Al terminar el trabajo, la aplicación ya era funcional, pero visualmente me parecía bastante sencilla. Por ello, decidí investigar un poco sobre diferentes formas de personalizar la interfaz de usuario en Java Swing.

A partir de eso, experimenté con algunos efectos visuales y estilos para los botones, intentando darle una apariencia inspirada en Windows 7 y su estilo Aero, especialmente mediante transparencias, degradados, bordes y cambios visuales al interactuar con los botones.

La intención no era recrear exactamente la interfaz de Windows 7, sino utilizar algunos elementos de ese estilo para hacer que la aplicación se sintiera menos genérica y más personalizada.

Una parte importante de este proyecto fue experimentar con aspectos de Java Swing que no había utilizado anteriormente. Además de trabajar con la lógica del inventario, tuve que investigar cómo modificar componentes visuales y crear comportamientos personalizados para algunos elementos de la interfaz.

El proyecto también me permitió entender mejor la separación entre la lógica de la aplicación y los elementos encargados de la presentación visual.

---

## 🇬🇧 English

### Description

A **Java Swing** desktop application for managing a small store's warehouse inventory (sodas, snacks, and sweet bread). It supports adding, editing, deleting, and searching products, as well as recording stock entries and exits. All data is stored in a plain text file (`inventario.txt`), with no database or external dependencies.

It was built as an individual project for the **Programming III** course (Graphical Interfaces with Java).

### Features

- **Product management**: add, edit, delete, and search (with cyclic navigation between matches).
- **Warehouse movements**: records stock entries and exits, each with an automatic ID, timestamp, and the person responsible.
- **Validation**: required fields, non-negative numeric quantities, sufficient stock before an exit, and a restriction on the `|` character (used as the field separator in the data file).
- **Simple persistence**: the `inventario.txt` file is created automatically on first run, pre-loaded with 45 starter products (sodas, Sabritas snacks, and Marinela pastries).
- **Custom UI**: a visual style inspired by Windows 7 / Aero — gradients, soft borders, translucent highlights, and interaction states (normal, hover, pressed, disabled) — layered on top of standard Swing components without touching their underlying logic.

### Project structure

```
InventarioAlmacen_Progra3/
├── pom.xml
└── src/main/java/progra3/plan2023/inventarioalmacen/
    ├── InventarioAlmacen.java   # Main window: logic, events, and persistence
    ├── InventarioAlmacen.form   # Interface layout (NetBeans GUI Builder)
    ├── EstiloWindows7.java      # Visual appearance only (no business logic)
    ├── Producto.java            # Product model
    └── Movimiento.java          # Entry/exit model
```

The separation is intentional: `EstiloWindows7` only changes how components are drawn (colors, borders, fonts); it does not interfere with the `ActionListener`s or the underlying data.

### Requirements

- **JDK 18 or newer**
- **Maven** (optional — only needed to build from the command line; the project can also be opened and run directly from NetBeans)

### How to run it

**Option 1 — NetBeans**
1. Open the project via `File → Open Project`.
2. Run it with the ▶ button (Run Project).

**Option 2 — Command line**
```bash
# Build
mvn clean package

# Run
java -cp target/classes progra3.plan2023.inventarioalmacen.InventarioAlmacen
```

The `inventario.txt` file is generated in the directory the program is run from.

### Data file format

```
PRODUCTOS
name|brand|size|weight|quantity

MOVIMIENTOS
id|type|product|quantity|person|date and time
```

### Personal notes

By the time I finished the assignment, the application was already functional, but visually it felt pretty plain to me. Because of that, I decided to look into different ways of customizing the user interface in Java Swing.

From there, I experimented with some visual effects and button styles, trying to give it a look inspired by Windows 7 and its Aero style, mainly through transparency, gradients, borders, and visual changes when interacting with the buttons.

The goal wasn't to recreate the Windows 7 interface exactly, but to use some elements of that style to make the application feel less generic and more personalized.

An important part of this project was experimenting with aspects of Java Swing I hadn't used before. Besides working on the inventory logic, I had to research how to modify visual components and create custom behavior for some interface elements.

The project also helped me better understand the separation between application logic and the elements responsible for visual presentation.
