package com.mycompany.proyectofinal.ivangonzalezhernandez.comun;

public class ProductoAlimento extends Producto {
    private static final long serialVersionUID = 1L;

    public ProductoAlimento() { this.tipo = "ALIMENTO"; }

    public ProductoAlimento(int id, String nombre, double precio, int cantidad) {
        super(id, nombre, precio, cantidad, "ALIMENTO");
    }

    @Override
    public double calcularPrecioFinal() {
        return precio * 1.05; // 5% impuesto
    }
}
