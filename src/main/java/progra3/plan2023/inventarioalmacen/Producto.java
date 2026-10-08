/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra3.plan2023.inventarioalmacen;

/**
 * Representa un articulo del inventario.
 */
public class Producto {

    private String nombre;
    private String marca;
    private String tamano;
    private String peso;
    private int cantidad;

    public Producto(String nombre, String marca, String tamano, String peso, int cantidad) {
        this.nombre = nombre;
        this.marca = marca;
        this.tamano = tamano;
        this.peso = peso;
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMarca() {
        return marca;
    }

    public String getTamano() {
        return tamano;
    }

    public String getPeso() {
        return peso;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
