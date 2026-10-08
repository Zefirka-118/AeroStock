# Documentación técnica — `EstiloWindows7.java`

## Índice

1. [Introducción](#1-introducción)
2. [Arquitectura de la clase](#2-arquitectura-de-la-clase)
3. [Personalización de botones](#3-personalización-de-botones)
4. [Renderizado](#4-renderizado)
5. [Tablas](#5-tablas)
6. [Campos de texto, paneles y otros componentes](#6-campos-de-texto-paneles-y-otros-componentes)
7. [Eventos y comportamiento](#7-eventos-y-comportamiento)
8. [Integración con InventarioAlmacen.java](#8-integración-con-inventarioalmacenjava)
9. [Flujo de ejecución](#9-flujo-de-ejecución)
10. [Conceptos de Java/Swing involucrados](#10-conceptos-de-javaswing-involucrados)
11. [Decisiones de diseño](#11-decisiones-de-diseño)
12. [Limitaciones y posibles mejoras](#12-limitaciones-y-posibles-mejoras)
13. [¿Cómo explicaría este archivo frente a un profesor?](#13-cómo-explicaría-este-archivo-frente-a-un-profesor)

---

## 1. Introducción

### Qué es `EstiloWindows7.java`

Es una clase auxiliar (`final class EstiloWindows7`), sin modificador `public`, que agrupa un conjunto de **métodos estáticos** cuyo único propósito es cambiar la **apariencia visual** de los componentes Swing que ya existen en `InventarioAlmacen.java`. No crea pantallas nuevas, no contiene datos del inventario y no define ningún flujo de la aplicación: recibe componentes ya construidos (botones, paneles, tablas, campos de texto) y les cambia color, fuente, bordes y, en el caso de los botones, la forma en que se dibujan.

La propia clase lo deja explícito en su comentario inicial:

```java
/**
 * Clase auxiliar de apariencia (solo visual).
 * Aplica un estilo inspirado en Windows 7 / Aero a los componentes Swing.
 * No contiene logica del inventario ni toca ningun evento.
 */
```

### Qué problema intenta resolver

Por defecto, los componentes de Swing usan el *Look and Feel* del sistema operativo (o `Metal` si no se configura ninguno), lo cual da una interfaz funcional pero visualmente genérica. `EstiloWindows7.java` resuelve el problema de que la aplicación "se viera plana" dándole una identidad visual propia, inspirada en el estilo **Aero** de Windows 7: botones con degradado tipo cristal, paneles con bordes suaves y títulos de color, y una tabla con encabezado degradado y filas alternadas.

### Por qué está separado del resto de la lógica de la aplicación

Está en su propio archivo, con un constructor privado y vacío (`private EstiloWindows7() {}`) que impide instanciarla, porque **no es un objeto de la aplicación**, es un conjunto de utilidades. Separarla cumple dos propósitos:

1. **No contaminar el código generado por el GUI Builder de NetBeans.** El método `initComponents()` de `InventarioAlmacen.java` está marcado como autogenerado y no debe tocarse a mano. Poner el estilo en una clase aparte evita mezclar código generado con código escrito manualmente.
2. **Separar responsabilidades.** Si mañana se quisiera cambiar el estilo visual completo (por ejemplo, a un tema oscuro), bastaría con reescribir `EstiloWindows7.java` sin tocar ni una línea de la lógica de productos, movimientos o archivos.

### Qué partes de la interfaz modifica

Según los métodos que expone, actúa sobre:

- Paneles (`JPanel`) — vía `panel(...)`
- Etiquetas (`JLabel`) — vía `etiqueta(...)`
- Campos de texto (`JTextComponent`, que cubre `JTextField` y similares) — vía `campo(...)`
- Listas desplegables (`JComboBox`) — vía `lista(...)`
- Botones (`JButton`) — vía `boton(...)`
- Tablas y su encabezado (`JTable`, `JScrollPane`, `JTableHeader`) — vía `tabla(...)`
- El fondo general de la ventana (constante `FONDO_VENTANA`, usada directamente desde `InventarioAlmacen.java`)

No toca `JFrame`, `JOptionPane`, ni ningún otro componente que no aparezca en la lista anterior.

---

## 2. Arquitectura de la clase

### Estructura general

`EstiloWindows7` es una clase de **nivel de paquete** (sin `public`), **`final`** (no puede heredarse) con:

- **Constantes estáticas** de color (`FONDO_VENTANA`, `BORDE_PANEL`, paletas `NEUTRO`, `VERDE`, `AZUL`, `ROJO`, etc.).
- **Métodos estáticos** que reciben un componente Swing y lo modifican "en sitio" (no devuelven nada nuevo, mutan el objeto recibido): `panel`, `etiqueta`, `campo`, `lista`, `boton`, `tabla`, `fuente`.
- **Dos clases internas privadas** (`private static final class`):
  - `BotonAero`, que extiende `BasicButtonUI`.
  - `EncabezadoClaro`, que extiende `JLabel` e implementa `TableCellRenderer`.

No implementa ninguna interfaz a nivel de la clase principal; toda la relación con el framework Swing ocurre a través de esas dos clases internas, que sí heredan de clases del propio Swing.

### Herencia, interfaces y clases internas

| Elemento | Tipo de relación | Clase/interfaz base |
|---|---|---|
| `EstiloWindows7` | Ninguna (clase utilitaria) | — |
| `BotonAero` (interna) | Herencia | `javax.swing.plaf.basic.BasicButtonUI` |
| `EncabezadoClaro` (interna) | Herencia + implementación de interfaz | `javax.swing.JLabel` + `javax.swing.table.TableCellRenderer` |

`BotonAero` **sobrescribe** varios métodos heredados de `BasicButtonUI`:

```java
protected void installDefaults(AbstractButton b)
public Dimension getPreferredSize(JComponent c)
public void paint(Graphics g, JComponent c)
protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text)
protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect)
```

`EncabezadoClaro` sobrescribe métodos heredados de `JLabel`:

```java
public Dimension getPreferredSize()
protected void paintComponent(Graphics g)
```

y además implementa el único método exigido por la interfaz `TableCellRenderer`:

```java
public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionada, boolean foco, int fila, int columna)
```

### Relaciones entre sus componentes

El flujo interno es:

1. Los métodos públicos de paquete (`panel`, `etiqueta`, `campo`, `lista`, `tabla`, `boton`) son el **punto de entrada**; `InventarioAlmacen.java` solo llama a estos.
2. `boton(...)` delega el verdadero trabajo de dibujo a la clase interna `BotonAero`, instalándola como la UI del botón con `boton.setUI(new BotonAero(...))`.
3. `tabla(...)` delega el dibujo del encabezado a `EncabezadoClaro`, instalándola con `encabezado.setDefaultRenderer(new EncabezadoClaro())`.
4. Todos los métodos de color y fuente comparten las constantes definidas al inicio de la clase (`FONDO_PANEL`, `BORDE_CAMPO`, las paletas de botones, etc.), de modo que todos los componentes quedan visualmente coherentes entre sí.

---

## 3. Personalización de botones

Esta es la parte más elaborada del archivo. Se hace en dos niveles: el método público `boton(...)` que prepara el botón, y la clase interna `BotonAero` que se encarga de dibujarlo.

### UI personalizada

Swing dibuja cada componente a través de un objeto "delegado de UI" (`ComponentUI`). Para un `JButton`, ese delegado normalmente es `BasicButtonUI` (o una variante dependiente del Look and Feel activo). El método `boton` reemplaza ese delegado por uno propio:

```java
static void boton(JButton boton, Color[] paleta) {
    boton.setUI(new BotonAero(paleta, paleta != NEUTRO));
    boton.setFont(fuente(paleta != NEUTRO ? Font.BOLD : Font.PLAIN, 12));
    boton.setFocusPainted(true);
}
```

Aquí ocurren tres cosas:

1. `boton.setUI(...)` sustituye por completo la forma en que Swing pinta ese botón específico, sin cambiar el Look and Feel global de la aplicación (solo afecta a ese `JButton`).
2. `paleta != NEUTRO` es una comparación **por referencia** (no de contenido): compara si el arreglo de colores que se pasó es literalmente la misma constante `NEUTRO`. Si no lo es (es decir, si se usó `VERDE`, `AZUL` o `ROJO`), el botón se considera "colorido" y se le pone negrita.
3. `setFocusPainted(true)` le dice a Swing que sí debe invocar el método de pintado de foco cuando el botón tiene el foco del teclado (ver `paintFocus` más abajo).

### Pintado/renderizado

Todo el dibujo ocurre dentro de `BotonAero.paint(Graphics g, JComponent c)`, que sobrescribe el método que Swing invoca automáticamente cada vez que el botón necesita redibujarse (al mostrarse, al pasar el mouse, al hacer clic, al perder el foco, etc.). El método:

1. Obtiene el estado lógico del botón.
2. Elige una paleta de colores según ese estado.
3. Dibuja el fondo con dos degradados (gradientes).
4. Dibuja un brillo translúcido encima.
5. Dibuja el borde.
6. Llama a `super.paint(g, c)` para que el texto y el icono se dibujen con el mecanismo normal de `BasicButtonUI` (que internamente llama a `paintText`, sobrescrito más abajo).

### Gradientes

Se usan dos `GradientPaint`, uno para la mitad superior del botón y otro para la mitad inferior, simulando el efecto "cristal" típico de Aero (claro arriba, más intenso abajo):

```java
int mitad = h / 2;
g2.setClip(forma);
g2.setPaint(new GradientPaint(0, 0, c1, 0, mitad, c2));
g2.fillRect(0, 0, w, mitad);
g2.setPaint(new GradientPaint(0, mitad, c3, 0, h, c4));
g2.fillRect(0, mitad, w, h - mitad);
```

`c1`, `c2`, `c3`, `c4` son los primeros cuatro colores de la paleta (`{arriba claro, arriba, abajo, abajo claro, borde}`), y cambian según el estado del botón (ver más abajo).

### Transparencia

El archivo no usa `AlphaComposite`. La transparencia se logra con el **canal alfa de `Color`**, construyendo colores con un cuarto parámetro (0–255) que indica opacidad:

```java
g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 120),
        0, mitad, new Color(255, 255, 255, 30)));
g2.fillRect(0, 0, w, mitad);
```

Esto dibuja un degradado blanco semitransparente (de alfa 120 a alfa 30, sobre 255) encima del fondo ya pintado, simulando el "brillo de cristal" de la mitad superior. También se usa alfa en la línea interior clara (`new Color(255, 255, 255, 150)`) y en el indicador de foco punteado (`new Color(255, 255, 255, 170)` o `new Color(90, 90, 90, 150)`).

### Bordes

La forma del botón no es un rectángulo normal sino una **esquina redondeada**, construida con `RoundRectangle2D`:

```java
RoundRectangle2D forma = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1.5f, h - 1.5f, 8, 8);
```

Esa misma forma se usa dos veces: primero como **recorte** (`g2.setClip(forma)`) para que los degradados de relleno no se salgan de las esquinas redondeadas, y después como **contorno** (`g2.draw(forma)`) para dibujar la línea exterior del borde con el color `borde` de la paleta activa. Adicionalmente, si el botón está activo y no está presionado, se dibuja una segunda línea, ligeramente más pequeña y semitransparente, para dar sensación de profundidad:

```java
if (activo && !presionado) {
    g2.setColor(new Color(255, 255, 255, 150));
    g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, w - 3.5f, h - 3.5f, 6, 6));
}
```

### Estados normal, hover, pressed, disabled

El método `paint` calcula tres variables booleanas a partir del `ButtonModel` del botón (el objeto interno de Swing que guarda si está presionado, si el mouse está encima, si está habilitado, etc.):

```java
boolean activo = b.isEnabled();
boolean presionado = activo && b.getModel().isArmed() && b.getModel().isPressed();
boolean encima = activo && b.getModel().isRollover();
```

- `isEnabled()` indica si el botón está habilitado (si está deshabilitado, ningún otro estado importa).
- `isArmed() && isPressed()` indica que el botón está siendo presionado con el mouse **dentro** de su área (si se arrastra el mouse fuera mientras se mantiene presionado, `isArmed()` se vuelve falso).
- `isRollover()` indica que el mouse está encima del botón, sin necesidad de clic.

### Cómo se determina qué aspecto debe tener en cada estado

A partir de esas tres variables, se seleccionan los cinco colores de trabajo (`c1`–`c4`, `borde`) mediante una cadena de `if / else if`:

```java
if (!activo) {
    // tonos grises fijos, independientes de la paleta elegida
} else if (presionado) {
    // los colores "abajo" de la paleta, oscurecidos; o un azul fijo si es botón NEUTRO
} else if (encima) {
    // la paleta aclarada; o un azul claro fijo si es botón NEUTRO
}
// si ninguna condición se cumple, se usan los colores originales de la paleta (estado normal)
```

Dos detalles importantes:

- **Deshabilitado** siempre usa los mismos grises fijos (`new Color(246, 246, 246)`, etc.), sin importar qué paleta tenga el botón.
- **Presionado y hover en botones "neutros"** (paleta `NEUTRO`, la variable `colorido` es `false`) no usan un oscurecido/aclarado de sus propios colores, sino un **azul fijo** (`new Color(196, 229, 246)` para presionado, `new Color(234, 246, 253)` para hover), imitando el resaltado celeste característico de los botones del sistema en Windows 7.

Los métodos `aclarar` y `oscurecer` son funciones auxiliares privadas que interpolan cada canal RGB hacia blanco o hacia negro según un factor `f` entre 0 y 1:

```java
private static Color aclarar(Color c, float f) {
    return new Color(
            Math.min(255, Math.round(c.getRed() + (255 - c.getRed()) * f)),
            ...
}
```

---

## 4. Renderizado

Esta sección detalla, paso a paso, qué ocurre dentro de `BotonAero.paint(Graphics g, JComponent c)` cada vez que Swing necesita dibujar un botón.

### 1. Obtener un contexto de dibujo seguro

```java
Graphics2D g2 = (Graphics2D) g.create();
```

`g.create()` crea una **copia** del contexto gráfico. Esto es una práctica estándar en Swing: cualquier cambio que se le haga a `g2` (color, clip, stroke) no afecta al `Graphics` original que Swing seguirá usando para otros componentes. Al final del método se llama a `g2.dispose()` para liberar esa copia.

### 2. Activar antialiasing

```java
g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
```

`RenderingHints` le indica al motor gráfico de Java **cómo** dibujar, sin cambiar **qué** se dibuja. Con antialiasing activado, los bordes curvos de `RoundRectangle2D` se ven suaves en lugar de dentados. Más adelante, en `paintText`, se activa también `KEY_TEXT_ANTIALIASING` para que el texto se vea nítido.

### 3. Leer el estado y elegir colores

Como se explicó en la sección 3, se consultan `isEnabled()`, `isArmed()`, `isPressed()`, `isRollover()` y se eligen los cinco colores de trabajo.

### 4. Definir la forma del botón

```java
RoundRectangle2D forma = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1.5f, h - 1.5f, 8, 8);
```

### 5. Pintar el relleno con `Paint` y `GradientPaint`

`Graphics2D.setPaint(Paint)` es más general que `setColor(Color)`: acepta cualquier implementación de la interfaz `Paint`, y `GradientPaint` es una de ellas (describe una transición lineal entre dos colores, entre dos puntos). El código fija el **recorte** (`setClip`) a la forma redondeada antes de rellenar, para que el degradado no dibuje fuera de las esquinas curvas, y luego restablece el recorte a `null`.

### 6. Pintar el brillo translúcido

Un segundo `GradientPaint`, esta vez con colores `Color(255,255,255, alfa)`, se dibuja encima de la mitad superior ya pintada, solo si el botón está activo. Esto simula el reflejo de luz del estilo Aero.

### 7. Dibujar los bordes

Primero el borde exterior (`g2.draw(forma)`), después, si corresponde, la línea interior clara.

### 8. Liberar el contexto y delegar el resto del pintado

```java
g2.dispose();
super.paint(g, c);
```

La llamada a `super.paint(g, c)` es clave: `BasicButtonUI.paint` es responsable de invocar internamente `paintIcon`, `paintText` y `paintFocus` en el orden correcto, con las coordenadas ya calculadas (tamaño del texto, posición del icono, etc.). Como `BotonAero` sobrescribe `paintText` y `paintFocus`, esos dos pasos terminan ejecutando el código personalizado, pero `paintIcon` (que este botón no usa, porque no tiene iconos) seguiría el comportamiento por defecto si existiera.

### `paintText`: texto con sombra

```java
protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
    ...
    if (colorido) {
        g2.setColor(new Color(0, 0, 0, 110));
        javax.swing.plaf.basic.BasicGraphicsUtils.drawStringUnderlineCharAt(g2, text, mnemonico, x, y + 1);
    }
    g2.setColor(b.getForeground());
    javax.swing.plaf.basic.BasicGraphicsUtils.drawStringUnderlineCharAt(g2, text, mnemonico, x, y);
}
```

Si el botón es "colorido" (paleta `VERDE`, `AZUL` o `ROJO`), primero dibuja el texto en negro semitransparente desplazado 1 píxel hacia abajo (una sombra simple), y después dibuja el texto real encima con el color de primer plano del botón (blanco, fijado en `installDefaults`). Esto mejora la legibilidad del texto blanco sobre fondos de color medio. `BasicGraphicsUtils.drawStringUnderlineCharAt` es el método estándar de Swing para dibujar texto respetando el subrayado del mnemónico (atajo de teclado tipo Alt+letra), aunque en este proyecto no se usan mnemónicos explícitos.

### `paintFocus`: indicador de foco

```java
protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setColor(colorido ? new Color(255, 255, 255, 170) : new Color(90, 90, 90, 150));
    g2.setStroke(new java.awt.BasicStroke(1f, ..., new float[]{1f, 2f}, 0f));
    g2.drawRect(4, 4, b.getWidth() - 9, b.getHeight() - 9);
    g2.dispose();
}
```

Este método solo se invoca (desde `BasicButtonUI`) cuando el botón tiene el foco del teclado **y** `isFocusPainted()` es verdadero (recordar que `boton(...)` llamó a `setFocusPainted(true)`). Dibuja un rectángulo punteado (`BasicStroke` con patrón de guiones) ligeramente hacia adentro del borde del botón.

### Resumen del ciclo de pintado de un botón

```
Swing decide que el botón debe redibujarse
        │
        ▼
BasicButtonUI.update(Graphics, JComponent)   ← heredado, no sobrescrito
        │
        ▼
BotonAero.paint(Graphics, JComponent)        ← sobrescrito
   ├─ lee estado (enabled / pressed / rollover)
   ├─ calcula colores según estado
   ├─ dibuja relleno con GradientPaint (2 mitades)
   ├─ dibuja brillo translúcido (si está activo)
   ├─ dibuja bordes (RoundRectangle2D)
   └─ super.paint(g, c)  → BasicButtonUI reparte el resto:
           ├─ paintIcon   (comportamiento por defecto, sin icono en este proyecto)
           ├─ BotonAero.paintText    ← sobrescrito (texto + sombra)
           └─ BotonAero.paintFocus   ← sobrescrito (rectángulo punteado)
```

---

## 5. Tablas

El método `tabla(JTable tabla, JScrollPane scroll)` personaliza tres aspectos distintos de la tabla, todos verificables en el código:

### Propiedades generales

```java
tabla.setFont(fuente(Font.PLAIN, 12));
tabla.setRowHeight(22);
tabla.setBackground(Color.WHITE);
tabla.setForeground(Color.BLACK);
tabla.setShowHorizontalLines(true);
tabla.setShowVerticalLines(false);
tabla.setGridColor(new Color(235, 235, 235));
tabla.setSelectionBackground(new Color(203, 232, 246));
tabla.setSelectionForeground(Color.BLACK);
tabla.setFillsViewportHeight(true);
```

Estas son propiedades estándar de `JTable` (sin UI personalizada ni clases nuevas): tipografía, alto de fila, colores de fondo/texto, qué líneas de cuadrícula se muestran y de qué color, y los colores que usa la fila seleccionada.

### Celdas: filas alternadas

Se reemplaza el renderer por defecto de las celdas (`DefaultTableCellRenderer`) con una subclase anónima:

```java
tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable t, Object valor,
            boolean seleccionada, boolean foco, int fila, int columna) {
        Component c = super.getTableCellRendererComponent(t, valor, seleccionada, false, fila, columna);
        if (seleccionada) {
            c.setBackground(t.getSelectionBackground());
            c.setForeground(t.getSelectionForeground());
        } else {
            c.setBackground(fila % 2 == 0 ? Color.WHITE : new Color(244, 247, 252));
            c.setForeground(Color.BLACK);
        }
        if (c instanceof JComponent) {
            ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        }
        return c;
    }
});
```

Este patrón (clase anónima que extiende `DefaultTableCellRenderer`) es el mecanismo estándar de Swing para personalizar cómo se ve cada celda. `getTableCellRendererComponent` se invoca una vez por cada celda visible, cada vez que la tabla se redibuja, y Swing usa el componente devuelto únicamente para pintarlo (no es un componente "vivo" con el que se interactúe directamente). La lógica es simple: si la fila está seleccionada usa los colores de selección de la tabla; si no, alterna entre blanco y un azul grisáceo muy claro según si el número de fila es par o impar (`fila % 2 == 0`). Nótese que el parámetro `foco` recibido se ignora deliberadamente al llamar a `super.getTableCellRendererComponent(..., false, ...)`, lo que evita que Swing dibuje el borde punteado de foco por defecto en la celda activa.

### Encabezado: `EncabezadoClaro`

```java
JTableHeader encabezado = tabla.getTableHeader();
encabezado.setFont(fuente(Font.PLAIN, 12));
encabezado.setDefaultRenderer(new EncabezadoClaro());
```

`EncabezadoClaro` es una clase interna que extiende `JLabel` e implementa `TableCellRenderer`. A diferencia del renderer de celdas (que reutiliza un `DefaultTableCellRenderer`), este construye su propio componente desde cero porque necesita **dibujar un degradado**, algo que `JLabel` no hace de forma nativa. Para eso:

1. En el constructor, se marca como no opaco (`setOpaque(false)`) porque el propio `paintComponent` se encargará de pintar el fondo.
2. `getTableCellRendererComponent` simplemente actualiza el texto y la fuente, y devuelve la instancia (`this`) — se reutiliza el mismo objeto `EncabezadoClaro` para cada columna del encabezado, cambiándole el texto en cada llamada.
3. `paintComponent(Graphics g)` sobrescribe el pintado para dibujar un degradado vertical blanco → gris azulado, y dos líneas finas (una horizontal abajo, otra vertical a la derecha, como separador de columnas), antes de llamar a `super.paintComponent(g)` para que el texto se dibuje con el mecanismo normal de `JLabel`.

### Borde del `JScrollPane`

```java
scroll.setBorder(BorderFactory.createLineBorder(BORDE_TABLA, 1));
scroll.getViewport().setBackground(Color.WHITE);
```

Esto no modifica la tabla en sí, sino el contenedor con scroll que la envuelve: le pone un borde de un píxel y fija el color de fondo del área visible cuando la tabla es más pequeña que el `viewport`.

---

## 6. Campos de texto, paneles y otros componentes

### Paneles (`panel`)

```java
static void panel(JPanel panel) {
    String titulo = "";
    Border actual = panel.getBorder();
    if (actual instanceof TitledBorder) {
        titulo = ((TitledBorder) actual).getTitle();
    }
    TitledBorder borde = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDE_PANEL, 1, true), titulo);
    borde.setTitleFont(fuente(Font.BOLD, 12));
    borde.setTitleColor(TITULO_PANEL);
    panel.setBorder(borde);
    panel.setBackground(FONDO_PANEL);
}
```

Este método es el único que **lee** una propiedad existente del componente antes de modificarlo: comprueba si el panel ya tenía un `TitledBorder` (el borde con título que NetBeans genera, por ejemplo "Producto", "Movimiento", "Inventario") y, de ser así, **conserva el texto del título**, pero reemplaza el borde completo por uno con línea redondeada (`createLineBorder(..., true)`, donde `true` activa esquinas redondeadas) y tipografía/color propios.

### Etiquetas (`etiqueta`)

```java
static void etiqueta(JLabel etiqueta) {
    etiqueta.setFont(fuente(Font.PLAIN, 12));
    etiqueta.setForeground(Color.BLACK);
}
```

Cambio simple de fuente y color de texto, sin dibujo personalizado.

### Campos de texto (`campo`)

```java
static void campo(final javax.swing.text.JTextComponent campo) {
    campo.setFont(fuente(Font.PLAIN, 12));
    campo.setBackground(Color.WHITE);
    campo.setBorder(bordeCampo(BORDE_CAMPO));
    campo.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            campo.setBorder(bordeCampo(BORDE_CAMPO_FOCO));
        }
        @Override
        public void focusLost(FocusEvent e) {
            campo.setBorder(bordeCampo(BORDE_CAMPO));
        }
    });
}
```

El parámetro es del tipo `javax.swing.text.JTextComponent`, la superclase común de `JTextField` y otros campos de texto de Swing, lo que permite que este método funcione para cualquier campo de texto del formulario (nombre, marca, cantidad, etc.) sin necesidad de un método distinto por cada uno. El borde se construye con un método privado auxiliar:

```java
private static Border bordeCampo(Color color) {
    return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(2, 4, 2, 4));
}
```

Es un **borde compuesto**: una línea de color por fuera, y un margen vacío por dentro para que el texto no quede pegado al borde.

### Listas desplegables (`lista`)

```java
static void lista(javax.swing.JComboBox<?> combo) {
    combo.setFont(fuente(Font.PLAIN, 12));
    combo.setBackground(Color.WHITE);
}
```

Es el método más simple del archivo: solo ajusta fuente y color de fondo del `JComboBox` (usado para seleccionar el producto en la sección de movimientos). No reemplaza su UI ni su renderer de elementos.

### Fuente (`fuente`)

```java
static Font fuente(int estilo, int tamano) {
    String[] disponibles = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
    String[] preferidas = {"Segoe UI", "Tahoma"};
    for (String preferida : preferidas) {
        for (String nombre : disponibles) {
            if (nombre.equalsIgnoreCase(preferida)) {
                return new Font(nombre, estilo, tamano);
            }
        }
    }
    return new Font(Font.DIALOG, estilo, tamano);
}
```

Este método es usado por **todos** los demás métodos de estilo. Consulta las fuentes instaladas en el sistema operativo donde corre el programa y busca, en orden de preferencia, "Segoe UI" (la fuente nativa de Windows 7/Vista en adelante) y, si no existe, "Tahoma" (la fuente de versiones anteriores de Windows). Si ninguna de las dos está disponible —por ejemplo, en Linux o macOS— cae de vuelta a la fuente genérica de diálogo del sistema (`Font.DIALOG`). Esto hace que la aplicación se vea más fiel al estilo Windows 7 cuando efectivamente se ejecuta en Windows, sin romperse en otros sistemas operativos.

---

## 7. Eventos y comportamiento

`EstiloWindows7.java` usa eventos únicamente para **cambiar apariencia**, nunca para lógica de negocio. Los mecanismos presentes son:

### `FocusListener` en los campos de texto

```java
campo.addFocusListener(new FocusAdapter() {
    @Override
    public void focusGained(FocusEvent e) { campo.setBorder(bordeCampo(BORDE_CAMPO_FOCO)); }
    @Override
    public void focusLost(FocusEvent e) { campo.setBorder(bordeCampo(BORDE_CAMPO)); }
});
```

`FocusAdapter` es una clase de conveniencia de Swing que implementa `FocusListener` con métodos vacíos, para que solo sea necesario sobrescribir los que interesan (`focusGained` y `focusLost`), sin tener que implementar métodos innecesarios de la interfaz completa. Cuando el usuario hace clic en un campo (o navega a él con Tab), el borde cambia de gris a azul; al salir del campo, vuelve a gris. Este listener se agrega **además de** cualquier otro listener que el campo ya tuviera; no reemplaza ni interfiere con la lógica de validación de `InventarioAlmacen.java`.

### Consulta del `ButtonModel` (no es un listener, pero sí lectura de estado en tiempo real)

Dentro de `BotonAero.paint`, el código no se "suscribe" a eventos de mouse directamente: en vez de eso, **lee el estado actual** del modelo del botón (`b.getModel()`) cada vez que Swing invoca `paint`. Swing ya se encarga, internamente, de escuchar los eventos de mouse y teclado sobre el botón y de disparar un nuevo `paint` cuando el estado cambia (por ejemplo, al entrar el mouse, `BasicButtonListener` —una clase interna del propio `BasicButtonUI`, no definida en este archivo— marca `isRollover()` como verdadero y solicita un repintado). `BotonAero` no instala ningún `MouseListener` propio: aprovecha el que ya instala `BasicButtonUI` por defecto al llamar a `installUI` (heredado, no sobrescrito en este archivo).

### Ningún listener relacionado con datos

No hay `ActionListener`, `ItemListener`, ni `TableModelListener` en este archivo. Todos los `ActionListener` que responden a clics de botones (`btnAgregarActionPerformed`, etc.) están definidos en `InventarioAlmacen.java`, y `EstiloWindows7.boton(...)` no los toca ni los reemplaza — solo reemplaza la **UI** del botón (`setUI`), que es un concepto distinto del listener de acción.

---

## 8. Integración con `InventarioAlmacen.java`

La integración ocurre en un único método de `InventarioAlmacen.java`, `aplicarEstiloWindows7()`, que se invoca una sola vez desde el constructor, **después** de que el formulario ya fue construido:

```java
public InventarioAlmacen() {
    initComponents();

    configurarTabla();
    cargarArchivo();
    mostrarProductos();
    actualizarComboProductos();

    aplicarEstiloWindows7();
}
```

Esto es importante: el estilo se aplica **al final**, sobre componentes que ya existen y ya tienen sus datos y sus `ActionListener` conectados (esos los crea `initComponents()`, generado por el GUI Builder). `aplicarEstiloWindows7()` solo cambia su apariencia.

Ejemplos reales de cada tipo de llamada, tomados directamente del código:

```java
private void aplicarEstiloWindows7() {
    getContentPane().setBackground(EstiloWindows7.FONDO_VENTANA);

    EstiloWindows7.panel(pnlProducto);
    EstiloWindows7.panel(pnlMovimiento);
    EstiloWindows7.panel(pnlInventario);

    EstiloWindows7.etiqueta(lblNombre);
    // ... una llamada por cada JLabel del formulario

    EstiloWindows7.campo(txtNombre);
    // ... una llamada por cada JTextField

    EstiloWindows7.lista(cmbProducto);

    EstiloWindows7.boton(btnAgregar, EstiloWindows7.VERDE);
    EstiloWindows7.boton(btnLimpiar, EstiloWindows7.AZUL);
    EstiloWindows7.boton(btnEliminar, EstiloWindows7.ROJO);
    EstiloWindows7.boton(btnModificar, EstiloWindows7.NEUTRO);
    EstiloWindows7.boton(btnBuscar, EstiloWindows7.NEUTRO);
    EstiloWindows7.boton(btnEntrada, EstiloWindows7.NEUTRO);
    EstiloWindows7.boton(btnSalida, EstiloWindows7.NEUTRO);

    EstiloWindows7.tabla(tblInventario, jScrollPane1);
}
```

Dos observaciones directamente verificables en el código:

- `getContentPane().setBackground(...)` es la **única** línea de `aplicarEstiloWindows7()` que no llama a un método de `EstiloWindows7`, sino que usa directamente la constante pública `FONDO_VENTANA`. Es decir, `InventarioAlmacen.java` también puede leer las constantes de color de `EstiloWindows7` sin pasar por un método.
- Cada botón recibe una paleta distinta según su función: `VERDE` para la acción de agregar (positiva), `ROJO` para eliminar (destructiva), `AZUL` para limpiar, y `NEUTRO` para el resto (modificar, buscar, entrada, salida). Esta asociación color–función la decide `InventarioAlmacen.java`, no `EstiloWindows7.java` — la clase de estilo solo ofrece las paletas como constantes, pero no impone qué botón debe usar cuál.

Como consecuencia de este diseño, `EstiloWindows7` nunca accede a `productos`, `movimientos`, ni a ningún campo de datos de `InventarioAlmacen`; la única comunicación entre ambas clases es "aquí tienes un componente Swing, cámbiale el aspecto".

---

## 9. Flujo de ejecución

Desde que se crea un componente hasta que aparece en pantalla con su estilo, ocurre lo siguiente (siguiendo el caso concreto del botón `btnAgregar`):

1. **Construcción del componente.** Dentro de `initComponents()` (código generado por NetBeans, no mostrado en este documento), se ejecuta algo equivalente a `btnAgregar = new javax.swing.JButton();`, se le asigna texto, y se le agrega su `ActionListener` que invoca `btnAgregarActionPerformed`. En este punto, el botón usa la UI por defecto del Look and Feel activo.

2. **El formulario se arma.** Todos los componentes (paneles, campos, tabla, botones) se agregan a sus contenedores y el layout los posiciona.

3. **Carga de datos.** `configurarTabla()`, `cargarArchivo()`, `mostrarProductos()` y `actualizarComboProductos()` llenan la tabla y el combo con información real, sin tocar apariencia.

4. **Aplicación del estilo.** Se ejecuta `aplicarEstiloWindows7()`, que llama a `EstiloWindows7.boton(btnAgregar, EstiloWindows7.VERDE)`. Dentro de ese método:
   - Se crea una instancia de `BotonAero` con la paleta verde.
   - `btnAgregar.setUI(nuevaInstancia)` reemplaza el delegado de pintado. Internamente, Swing invoca `uninstallUI` sobre la UI anterior y `installUI` sobre la nueva, lo que a su vez llama a `installDefaults` (sobrescrito en `BotonAero`), que marca el botón como no opaco, le pone un borde vacío y color de texto blanco.
   - Se ajusta la fuente a negrita (porque `VERDE != NEUTRO`).

5. **Se hace visible la ventana.** En `main(...)`, `new InventarioAlmacen().setVisible(true)` dispara el primer ciclo de pintado de toda la ventana.

6. **Primer pintado.** Swing recorre el árbol de componentes y, al llegar a `btnAgregar`, en vez de usar el pintado por defecto, invoca los métodos de `BotonAero` descritos en la sección 4: se calcula el estado (normal, porque nada lo está presionando ni el mouse está encima), se eligen los colores verdes de la paleta, se dibujan los degradados, el brillo y el borde, y finalmente se dibuja el texto "Agregar" en blanco con sombra.

7. **Repintados posteriores.** Cada vez que el usuario mueve el mouse sobre el botón, hace clic, o el botón gana/pierde el foco, Swing marca el componente como "sucio" y vuelve a invocar `paint`, repitiendo el paso 6 pero con un estado distinto (hover, pressed, etc.), lo que produce el efecto interactivo descrito en la sección 3.

Este mismo flujo general (crear → cargar datos → aplicar estilo → pintar → repintar en cada interacción) aplica igual para los paneles, campos, lista y tabla, con la diferencia de que esos componentes no reemplazan su UI completa: solo cambian propiedades (color, fuente, borde) que Swing usa dentro de su propio mecanismo de pintado estándar.

---

## 10. Conceptos de Java/Swing involucrados

- **Look and Feel (LaF):** es el conjunto de reglas visuales que usa Swing para dibujar todos sus componentes. `InventarioAlmacen.java` activa el LaF del sistema operativo en `main(...)` (`UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())`). `EstiloWindows7` no cambia el LaF global; solo sobrescribe la UI de componentes puntuales (los botones), por eso el resto de la interfaz sigue respondiendo al LaF del sistema.

- **UI delegates (`ComponentUI`):** cada componente Swing delega su pintado, medición y comportamiento a un objeto "UI" asociado, que el LaF activo proporciona por defecto. `JButton` usa un objeto que extiende `ButtonUI` (en el LaF básico, `BasicButtonUI`). Reemplazar ese objeto con `setUI(...)` es la forma soportada por Swing de personalizar un componente sin tener que reescribirlo desde cero.

- **`BasicButtonUI`:** es la implementación de referencia (del paquete `javax.swing.plaf.basic`) de la UI de un botón. Provee el ciclo de pintado estándar (`paint` → `paintIcon` + `paintText` + `paintFocus`) y el manejo de eventos de mouse/teclado básico. `BotonAero` hereda de ella para no tener que reimplementar esa lógica de eventos, y solo sobrescribe los métodos de dibujo.

- **Painting (pintado) en Swing:** Swing no dibuja "a demanda inmediata"; cuando algo cambia (estado, tamaño, contenido), el componente se marca para repintarse y, en el siguiente ciclo del sistema de eventos, Swing invoca los métodos `paint`/`paintComponent` correspondientes. El desarrollador no llama a estos métodos directamente.

- **`Graphics` y `Graphics2D`:** `Graphics` es la clase base de AWT para dibujar (líneas, formas, texto). `Graphics2D` la extiende y agrega capacidades más avanzadas: formas geométricas de precisión decimal (`RoundRectangle2D`), rellenos complejos (`Paint`, incluido `GradientPaint`), trazos configurables (`Stroke`, usado en `paintFocus` para la línea punteada) y pistas de calidad de render (`RenderingHints`). Todo el dibujo personalizado de este archivo se hace mediante un *cast* explícito de `Graphics` a `Graphics2D`.

- **Herencia:** `BotonAero extends BasicButtonUI` y `EncabezadoClaro extends JLabel` son los dos únicos usos de herencia de clase en el archivo.

- **Polimorfismo:** cuando Swing llama a `boton.getUI().paint(g, boton)` (internamente), no "sabe" que está invocando el `paint` de `BotonAero` en lugar del de `BasicButtonUI`; simplemente invoca el método a través de la referencia de tipo `ButtonUI`/`BasicButtonUI`, y en tiempo de ejecución se ejecuta la versión sobrescrita. Lo mismo ocurre cuando `JTable` invoca `getTableCellRendererComponent` sobre cualquier objeto que implemente `TableCellRenderer`, sin necesidad de saber si es un `DefaultTableCellRenderer` o un `EncabezadoClaro`.

- **Clases internas (`private static final class`):** `BotonAero` y `EncabezadoClaro` están definidas dentro de `EstiloWindows7` y marcadas como `private`, por lo que no son visibles ni utilizables fuera de este archivo. Ser `static` significa que no guardan una referencia implícita a una instancia de `EstiloWindows7` (coherente con que `EstiloWindows7` nunca se instancia).

- **Listeners:** patrón de Java/Swing en el que un objeto se "suscribe" a notificaciones de otro. En este archivo, el único listener definido explícitamente es el `FocusAdapter` anónimo dentro de `campo(...)`.

- **Componentes Swing involucrados:** `JPanel`, `JLabel`, `JTextComponent`/`JTextField`, `JComboBox`, `JButton`, `JTable`, `JScrollPane`, `JTableHeader` — todos reciben personalización directa o indirecta en este archivo.

---

## 11. Decisiones de diseño

A partir de evidencia concreta en el código, las decisiones que permiten mantener la apariencia separada de la lógica son:

1. **Constructor privado y clase `final`.** `private EstiloWindows7() {}` impide crear instancias y `final` impide heredar de ella. Refuerza que es una colección de utilidades sin estado propio (no hay atributos de instancia, todo es `static`), no un objeto que participe en la lógica de la aplicación.

2. **Los métodos reciben y modifican componentes ya existentes, nunca los crean.** Todas las firmas (`panel(JPanel panel)`, `campo(JTextComponent campo)`, `boton(JButton boton, Color[] paleta)`, `tabla(JTable tabla, JScrollPane scroll)`) toman como parámetro un componente que `InventarioAlmacen.java` ya construyó. `EstiloWindows7` nunca instancia un `JButton` ni decide cuántos hay o qué texto llevan — eso lo controla el GUI Builder y la propia clase principal.

3. **`aplicarEstiloWindows7()` se llama al final del constructor**, después de `initComponents()`, de la carga del archivo y de la configuración de la tabla. Esto garantiza que el estilo sea la última capa aplicada y que nunca interfiera con que los datos se carguen o con que los listeners se conecten.

4. **Ningún método de `EstiloWindows7` reemplaza o elimina un `ActionListener`.** `setUI(...)` solo afecta el delegado de pintado del botón, no sus listeners de acción; estos siguen siendo los que `initComponents()` conectó.

5. **El método `panel(...)` preserva el título existente del `TitledBorder`.** En vez de recibir el texto del título como parámetro (lo que acoplaría a `EstiloWindows7` con el contenido de cada panel), lo **lee** del borde actual del panel antes de reemplazarlo. Así, `EstiloWindows7` no necesita saber qué paneles existen ni cómo se llaman.

6. **Las paletas de color (`VERDE`, `ROJO`, `AZUL`, `NEUTRO`) son constantes públicas de la clase, no decisiones tomadas dentro de `EstiloWindows7`.** Es `InventarioAlmacen.java` quien decide, en `aplicarEstiloWindows7()`, qué paleta usa cada botón según su función (agregar = verde, eliminar = rojo, etc.). `EstiloWindows7` ofrece las opciones pero no impone la asociación semántica.

---

## 12. Limitaciones y posibles mejoras

*(Esta sección es análisis, no indica que se haya modificado el código fuente.)*

1. **`fuente(...)` recalcula la lista de fuentes del sistema en cada llamada.** `GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()` es una operación relativamente costosa, y se invoca una vez por cada componente estilizado (paneles, etiquetas, campos, lista, botones) cada vez que se construye la ventana. Podría calcularse una sola vez y guardarse en una constante estática de la clase.

2. **Las paletas de botones son arreglos de `Color` sin tipo propio.** `static final Color[] VERDE = {...}` funciona, pero no documenta qué significa cada posición del arreglo salvo por el comentario (`{arriba claro, arriba, abajo, abajo claro, borde}`). Un `record` o una pequeña clase `PaletaBoton` con campos nombrados (`claroArriba`, `arriba`, `abajo`, `claroAbajo`, `borde`) sería más seguro: evitaría errores de índice y haría el código autoexplicativo.

3. **La comparación `paleta != NEUTRO` depende de identidad de referencia.** Es correcta mientras `NEUTRO` sea siempre la misma constante compartida (y en este proyecto lo es, porque es `static final`), pero es un patrón frágil: si en el futuro alguien creara un nuevo arreglo de `Color` con los mismos valores que `NEUTRO` sin usar la constante, `boton(...)` lo trataría como "colorido" (negrita, texto blanco) aunque visualmente sea gris. Un enfoque más explícito sería pasar un booleano `colorido` directamente, o un enum.

4. **Los colores de estado "deshabilitado" y los de hover/pressed para botones neutros están repetidos como literales dentro de `paint(...)`,** en vez de definirse como constantes con nombre al inicio de la clase (como sí ocurre con `NEUTRO`, `VERDE`, etc.). Esto hace que si se quisiera ajustar, por ejemplo, el azul de "hover neutro", haya que buscarlo dentro del método de pintado en lugar de un solo lugar centralizado.

5. **No hay manejo de `AlphaComposite` ni de offscreen buffering explícito.** El archivo logra transparencia únicamente con el canal alfa de `Color`, lo cual es suficiente para este caso, pero significa que no se compone con imágenes ni con otros buffers; todo el "cristal" es aproximado mediante degradados y colores semitransparentes superpuestos, no transparencia real de la ventana.

6. **El renderer de celdas de la tabla se define como clase anónima dentro de `tabla(...)`,** mientras que el del encabezado (`EncabezadoClaro`) es una clase interna nombrada. Ambos cumplen el mismo propósito (renderizar celdas), por lo que la inconsistencia de estilo (anónima vs. nombrada) no es un error, pero sí una asimetría que podría unificarse por legibilidad.

7. **`EncabezadoClaro` se reutiliza como una única instancia para todas las columnas.** Esto es válido porque Swing solo necesita el componente para pintarlo y luego lo descarta, pero quien no conozca ese patrón de "renderer reutilizado" podría pensar erróneamente que hace falta una instancia por columna.

8. **No se valida qué ocurre si `boton(...)` recibe una paleta con menos de 5 colores.** El método asume siempre `paleta[0]` a `paleta[4]`; un arreglo más corto lanzaría `ArrayIndexOutOfBoundsException` en tiempo de ejecución. Como las paletas están controladas dentro de la misma clase (`NEUTRO`, `VERDE`, `AZUL`, `ROJO`), el riesgo es bajo en la práctica, pero no hay una verificación explícita.

---

## 13. ¿Cómo explicaría este archivo frente a un profesor?

"`EstiloWindows7.java` es la clase que separé exclusivamente para la parte visual del proyecto. La idea es que ni la lógica del inventario ni el código que genera NetBeans en `initComponents()` se mezclen con el código que decide cómo se ve cada componente.

Lo primero que hice fue fijar un pequeño conjunto de colores y fuentes como constantes, para que todo el programa compartiera la misma paleta. Luego escribí un método estático por cada tipo de componente del formulario —paneles, etiquetas, campos de texto, el combo y la tabla— que simplemente recibe ese componente y le cambia propiedades como el color de fondo, la fuente o el borde. Esos son los casos más sencillos.

El caso más elaborado es el de los botones. Ahí no me bastaba con cambiar una propiedad: quería que tuvieran ese efecto de 'cristal' tipo Aero, con degradado y un brillo arriba, y que reaccionaran visualmente cuando el mouse pasa encima o cuando se presionan. Para lograr eso no modifiqué el `JButton` directamente, sino que le reemplacé su **UI delegate** —el objeto que Swing usa internamente para pintarlo— con `setUI(...)`. Creé una clase interna, `BotonAero`, que hereda de `BasicButtonUI`, que es la clase que Swing usa por defecto para dibujar botones, y sobrescribí su método `paint`.

Dentro de `paint` lo que hago es: primero reviso el estado del botón a través de su `ButtonModel` —si está habilitado, si está presionado, si el mouse está encima—, y según ese estado elijo una paleta de colores distinta. Después, con `Graphics2D`, dibujo dos rectángulos con esquinas redondeadas usando `GradientPaint`, uno para la mitad de arriba del botón y otro para la de abajo, que es justamente lo que da el efecto de volumen. Encima de eso dibujo otro degradado, pero usando colores blancos con transparencia —el cuarto parámetro del `Color`, el canal alfa— para simular el brillo. Y al final dejo que `BasicButtonUI`, con `super.paint`, termine de dibujar el texto y el indicador de foco, que también sobrescribí para que el texto tenga una pequeña sombra y se note el foco con un rectángulo punteado.

Lo importante de toda esta clase es que en ningún momento toco los `ActionListener` ni los datos del inventario: `EstiloWindows7` solo recibe componentes que ya existen y ya funcionan, y únicamente les cambia el aspecto. Por eso la aplico al final del constructor de `InventarioAlmacen`, después de cargar los datos del archivo y de que todos los eventos ya estén conectados. Si mañana quisiera quitar el estilo Windows 7 por completo, me bastaría con dejar de llamar a `aplicarEstiloWindows7()`, y toda la lógica del inventario seguiría funcionando exactamente igual."
