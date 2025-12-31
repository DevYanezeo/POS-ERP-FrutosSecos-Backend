package com.erp.p03.controllers.dto;

public class DashboardSummaryDTO {
    private Integer ventasHoy;
    private Double porcentajeVentasAyer;
    private Integer productosVendidos;
    private Integer productosUnicos;
    private Integer ventaPromedio;
    private Integer transaccionesHoy;
    private Integer stockTotal;
    private Integer alertasStock;

    // Getters and Setters
    public Integer getVentasHoy() {
        return ventasHoy;
    }

    public void setVentasHoy(Integer ventasHoy) {
        this.ventasHoy = ventasHoy;
    }

    public Double getPorcentajeVentasAyer() {
        return porcentajeVentasAyer;
    }

    public void setPorcentajeVentasAyer(Double porcentajeVentasAyer) {
        this.porcentajeVentasAyer = porcentajeVentasAyer;
    }

    public Integer getProductosVendidos() {
        return productosVendidos;
    }

    public void setProductosVendidos(Integer productosVendidos) {
        this.productosVendidos = productosVendidos;
    }

    public Integer getProductosUnicos() {
        return productosUnicos;
    }

    public void setProductosUnicos(Integer productosUnicos) {
        this.productosUnicos = productosUnicos;
    }

    public Integer getVentaPromedio() {
        return ventaPromedio;
    }

    public void setVentaPromedio(Integer ventaPromedio) {
        this.ventaPromedio = ventaPromedio;
    }

    public Integer getTransaccionesHoy() {
        return transaccionesHoy;
    }

    public void setTransaccionesHoy(Integer transaccionesHoy) {
        this.transaccionesHoy = transaccionesHoy;
    }

    public Integer getStockTotal() {
        return stockTotal;
    }

    public void setStockTotal(Integer stockTotal) {
        this.stockTotal = stockTotal;
    }

    public Integer getAlertasStock() {
        return alertasStock;
    }

    public void setAlertasStock(Integer alertasStock) {
        this.alertasStock = alertasStock;
    }
}
