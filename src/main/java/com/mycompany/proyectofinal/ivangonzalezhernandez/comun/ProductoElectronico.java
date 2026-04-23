package com.mycompany.proyectofinal.ivangonzalezhernandez.comun;

public class ProductoElectronico extends Producto {
    private static final long serialVersionUID = 1L;

    public ProductoElectronico() { this.tipo = "ELECTRONICO"; }

    public ProductoElectronico(int id, String nombre, double precio, int cantidad) {
        super(id, nombre, precio, cantidad, "ELECTRONICO");
    }

    @Override
    public double calcularPrecioFinal() {
        return precio * 1.13; // 13% impuesto
    }
}
