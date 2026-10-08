/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra3.plan2023.inventarioalmacen;

/**
 * Representa una entrada o una salida de almacen.
 * El tipo es el texto "ENTRADA" o "SALIDA".
 */
public class Movimiento {

    private final int id;
    private final String tipo;
    private final String producto;
    private final int cantidad;
    private final String persona;
    private final String fechaHora;

    public Movimiento(int id, String tipo, String producto, int cantidad,
            String persona, String fechaHora) {
        this.id = id;
        this.tipo = tipo;
        this.producto = producto;
        this.cantidad = cantidad;
        this.persona = persona;
        this.fechaHora = fechaHora;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getPersona() {
        return persona;
    }

    public String getFechaHora() {
        return fechaHora;
    }
}
