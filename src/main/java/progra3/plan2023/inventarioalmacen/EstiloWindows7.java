package progra3.plan2023.inventarioalmacen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Clase auxiliar de apariencia (solo visual).
 * Aplica un estilo inspirado en Windows 7 / Aero a los componentes Swing.
 * No contiene logica del inventario ni toca ningun evento.
 * 
 * o eso espero... ¿Sera que el robles reyes leera esto?
 * 
 * aunque antes de agregar el estilo de win7 ya era todo funcional q yo sepa.
 */
final class EstiloWindows7 {

    // Colores base
    static final Color FONDO_VENTANA = new Color(233, 239, 247);
    static final Color FONDO_PANEL = new Color(245, 248, 252);
    static final Color BORDE_PANEL = new Color(176, 192, 212);
    static final Color TITULO_PANEL = new Color(30, 57, 91);
    static final Color BORDE_CAMPO = new Color(171, 173, 179);
    static final Color BORDE_CAMPO_FOCO = new Color(61, 123, 173);
    static final Color BORDE_TABLA = new Color(130, 135, 144);

    // Paletas de botones: {arriba claro, arriba, abajo, abajo claro, borde}
    static final Color[] NEUTRO = {
        new Color(250, 250, 250), new Color(238, 238, 238),
        new Color(221, 221, 221), new Color(207, 207, 207),
        new Color(112, 112, 112)
    };
    static final Color[] VERDE = {
        new Color(158, 218, 158), new Color(112, 190, 112),
        new Color(58, 150, 66), new Color(88, 176, 92),
        new Color(40, 112, 46)
    };
    static final Color[] AZUL = {
        new Color(162, 202, 240), new Color(114, 172, 226),
        new Color(52, 118, 192), new Color(86, 148, 214),
        new Color(36, 84, 146)
    };
    static final Color[] ROJO = {
        new Color(240, 160, 160), new Color(222, 112, 112),
        new Color(184, 44, 44), new Color(208, 80, 80),
        new Color(132, 32, 32)
    };

    private EstiloWindows7() {
    }

    // ------------------------------------------------------------------
    // Fuente
    // ------------------------------------------------------------------

    // Segoe UI (Windows 7); si no existe se usa Tahoma o la fuente por defecto
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

    // ------------------------------------------------------------------
    // Paneles y etiquetas
    // ------------------------------------------------------------------

    // Conserva el titulo que ya tiene el panel y solo cambia su aspecto 
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

    static void etiqueta(JLabel etiqueta) {
        etiqueta.setFont(fuente(Font.PLAIN, 12));
        etiqueta.setForeground(Color.BLACK);
    }

    // ------------------------------------------------------------------
    // Campos de texto y lista desplegable
    // ------------------------------------------------------------------

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

    private static Border bordeCampo(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4));
    }

    static void lista(javax.swing.JComboBox<?> combo) {
        combo.setFont(fuente(Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
    }

    // ------------------------------------------------------------------
    // Botones
    // ------------------------------------------------------------------

    // Cambia solo el dibujo del boton; los ActionListener no se tocan
    static void boton(JButton boton, Color[] paleta) {
        boton.setUI(new BotonAero(paleta, paleta != NEUTRO));
        boton.setFont(fuente(paleta != NEUTRO ? Font.BOLD : Font.PLAIN, 12));
        boton.setFocusPainted(true);
    }

    private static final class BotonAero extends BasicButtonUI {

        private final Color[] paleta;
        private final boolean colorido;

        BotonAero(Color[] paleta, boolean colorido) {
            this.paleta = paleta;
            this.colorido = colorido;
        }

        @Override
        protected void installDefaults(AbstractButton b) {
            super.installDefaults(b);
            b.setOpaque(false);
            b.setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12));
            b.setRolloverEnabled(true);
            b.setForeground(colorido ? Color.WHITE : Color.BLACK);
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            Dimension d = super.getPreferredSize(c);
            return new Dimension(d.width, Math.max(d.height, 27));
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = c.getWidth();
            int h = c.getHeight();
            boolean activo = b.isEnabled();
            boolean presionado = activo && b.getModel().isArmed() && b.getModel().isPressed();
            boolean encima = activo && b.getModel().isRollover();

            Color c1 = paleta[0];
            Color c2 = paleta[1];
            Color c3 = paleta[2];
            Color c4 = paleta[3];
            Color borde = paleta[4];

            if (!activo) {
                c1 = new Color(246, 246, 246);
                c2 = new Color(240, 240, 240);
                c3 = new Color(232, 232, 232);
                c4 = new Color(226, 226, 226);
                borde = new Color(173, 178, 181);
            } else if (presionado) {
                c1 = oscurecer(c3, 0.10f);
                c2 = oscurecer(c3, 0.04f);
                c3 = oscurecer(c3, 0.08f);
                c4 = oscurecer(c4, 0.12f);
                borde = oscurecer(borde, 0.15f);
                if (!colorido) {
                    c1 = new Color(196, 229, 246);
                    c2 = new Color(196, 229, 246);
                    c3 = new Color(152, 209, 239);
                    c4 = new Color(104, 179, 219);
                    borde = new Color(44, 98, 139);
                }
            } else if (encima) {
                if (colorido) {
                    c1 = aclarar(c1, 0.25f);
                    c2 = aclarar(c2, 0.22f);
                    c3 = aclarar(c3, 0.15f);
                    c4 = aclarar(c4, 0.18f);
                } else {
                    c1 = new Color(234, 246, 253);
                    c2 = new Color(217, 240, 252);
                    c3 = new Color(190, 230, 253);
                    c4 = new Color(167, 217, 245);
                    borde = new Color(60, 127, 177);
                }
            }

            RoundRectangle2D forma = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1.5f, h - 1.5f, 8, 8);

            // Mitad superior (clara) y mitad inferior (mas intensa), como Aero
            int mitad = h / 2;
            g2.setClip(forma);
            g2.setPaint(new GradientPaint(0, 0, c1, 0, mitad, c2));
            g2.fillRect(0, 0, w, mitad);
            g2.setPaint(new GradientPaint(0, mitad, c3, 0, h, c4));
            g2.fillRect(0, mitad, w, h - mitad);

            // Brillo de cristal en la parte superior (translucido)
            if (activo) {
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 120),
                        0, mitad, new Color(255, 255, 255, 30)));
                g2.fillRect(0, 0, w, mitad);
            }
            g2.setClip(null);

            // Borde exterior fino
            g2.setColor(borde);
            g2.draw(forma);

            // Linea interior clara (sensacion de profundidad)
            if (activo && !presionado) {
                g2.setColor(new Color(255, 255, 255, 150));
                g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, w - 3.5f, h - 3.5f, 6, 6));
            }

            g2.dispose();

            // Texto e indicador de foco (BasicButtonUI)
            super.paint(g, c);
        }

        @Override
        protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
            AbstractButton b = (AbstractButton) c;
            FontMetrics fm = g.getFontMetrics(b.getFont());
            int mnemonico = b.getDisplayedMnemonicIndex();
            int x = textRect.x + getTextShiftOffset();
            int y = textRect.y + fm.getAscent() + getTextShiftOffset();

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (!b.isEnabled()) {
                g2.setColor(new Color(131, 131, 131));
            } else {
                if (colorido) {
                    // Sombra suave para que el texto blanco se lea bien
                    g2.setColor(new Color(0, 0, 0, 110));
                    javax.swing.plaf.basic.BasicGraphicsUtils.drawStringUnderlineCharAt(
                            g2, text, mnemonico, x, y + 1);
                }
                g2.setColor(b.getForeground());
            }
            javax.swing.plaf.basic.BasicGraphicsUtils.drawStringUnderlineCharAt(
                    g2, text, mnemonico, x, y);
        }

        @Override
        protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
                Rectangle textRect, Rectangle iconRect) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(colorido ? new Color(255, 255, 255, 170) : new Color(90, 90, 90, 150));
            g2.setStroke(new java.awt.BasicStroke(1f, java.awt.BasicStroke.CAP_BUTT,
                    java.awt.BasicStroke.JOIN_MITER, 1f, new float[]{1f, 2f}, 0f));
            g2.drawRect(4, 4, b.getWidth() - 9, b.getHeight() - 9);
            g2.dispose();
        }

        private static Color aclarar(Color c, float f) {
            return new Color(
                    Math.min(255, Math.round(c.getRed() + (255 - c.getRed()) * f)),
                    Math.min(255, Math.round(c.getGreen() + (255 - c.getGreen()) * f)),
                    Math.min(255, Math.round(c.getBlue() + (255 - c.getBlue()) * f)));
        }

        private static Color oscurecer(Color c, float f) {
            return new Color(
                    Math.round(c.getRed() * (1 - f)),
                    Math.round(c.getGreen() * (1 - f)),
                    Math.round(c.getBlue() * (1 - f)));
        }
    }

    // ------------------------------------------------------------------
    // Tabla
    // ------------------------------------------------------------------

    static void tabla(JTable tabla, javax.swing.JScrollPane scroll) {
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

        // Filas alternas muy suaves; la seleccion usa los colores de la tabla
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {
                Component c = super.getTableCellRendererComponent(
                        t, valor, seleccionada, false, fila, columna);
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

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setFont(fuente(Font.PLAIN, 12));
        encabezado.setDefaultRenderer(new EncabezadoClaro());

        scroll.setBorder(BorderFactory.createLineBorder(BORDE_TABLA, 1));
        scroll.getViewport().setBackground(Color.WHITE);
    }

    // Encabezado claro con degradado suave y separadores finos
    private static final class EncabezadoClaro extends JLabel implements TableCellRenderer {

        EncabezadoClaro() {
            setOpaque(false);
            setForeground(new Color(30, 40, 60));
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor,
                boolean seleccionada, boolean foco, int fila, int columna) {
            setFont(t.getTableHeader().getFont());
            setText(valor == null ? "" : valor.toString());
            return this;
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, Math.max(d.height, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255),
                    0, h, new Color(228, 234, 242)));
            g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(213, 213, 213));
            g2.drawLine(0, h - 1, w, h - 1);
            g2.setColor(new Color(224, 224, 224));
            g2.drawLine(w - 1, 3, w - 1, h - 4);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
