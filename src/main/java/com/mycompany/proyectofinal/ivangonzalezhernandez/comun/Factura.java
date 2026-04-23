package com.mycompany.proyectofinal.ivangonzalezhernandez.comun;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Factura implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numeroFactura;
    private Cliente cliente;
    private LocalDate fecha;
    private List<DetalleFactura> detalles;
    private double total;

    public Factura() {
        this.detalles = new ArrayList<>();
        this.fecha = LocalDate.now();
    }

    public Factura(Cliente cliente) {
        this();
        this.cliente = cliente;
    }

    public void agregarDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
        total += detalle.getSubtotal();
    }

    public String generarTextoFactura() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("         FACTURA #").append(numeroFactura).append("\n");
        sb.append("========================================\n");
        sb.append("Fecha: ").append(fecha).append("\n");
        sb.append("Cliente: ").append(cliente.getNombre()).append("\n");
        sb.append("Correo: ").append(cliente.getCorreo()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("PRODUCTOS:\n");
        for (DetalleFactura d : detalles) {
            sb.append("  ").append(d.toString()).append("\n");
        }
        sb.append("----------------------------------------\n");
        sb.append("TOTAL: $").append(String.format("%.2f", total)).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    public int getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(int n) { this.numeroFactura = n; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente c) { this.cliente = c; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate f) { this.fecha = f; }
    public List<DetalleFactura> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleFactura> d) { this.detalles = d; }
    public double getTotal() { return total; }
    public void setTotal(double t) { this.total = t; }
}
