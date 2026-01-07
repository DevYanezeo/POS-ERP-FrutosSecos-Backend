package com.erp.p03.services;

import com.erp.p03.controllers.dto.FinanceSummaryDTO;
import com.erp.p03.controllers.dto.ProductLossDTO;
import com.erp.p03.controllers.dto.ProductSalesDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReporteExcelService {

    public byte[] generarReporteExcel(String titulo,
            FinanceSummaryDTO resumen,
            List<ProductSalesDTO> topVentas,
            List<ProductLossDTO> topPerdidas) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("$#,##0"));

            // HOJA 1: Resumen Financiero
            Sheet sheetResumen = workbook.createSheet("Resumen Financiero");
            createResumenSheet(sheetResumen, titulo, resumen, headerStyle, currencyStyle);

            // HOJA 2: Productos Más Vendidos
            if (topVentas != null && !topVentas.isEmpty()) {
                Sheet sheetVentas = workbook.createSheet("Más Vendidos");
                createVentasSheet(sheetVentas, topVentas, headerStyle, currencyStyle);
            }

            // HOJA 3: Pérdidas por Vencimiento
            if (topPerdidas != null && !topPerdidas.isEmpty()) {
                Sheet sheetPerdidas = workbook.createSheet("Pérdidas (Vencimiento)");
                createPerdidasSheet(sheetPerdidas, topPerdidas, headerStyle, currencyStyle);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createResumenSheet(Sheet sheet, String titulo, FinanceSummaryDTO resumen, CellStyle headerStyle,
            CellStyle currencyStyle) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(titulo);
        titleCell.setCellStyle(headerStyle);

        String[] headers = { "Concepto", "Monto" };
        Row headerRow = sheet.createRow(2);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 3;
        addRow(sheet, rowNum++, "Total Ingresos", resumen.getTotalIngresos(), currencyStyle);
        addRow(sheet, rowNum++, "Costo Productos Vendidos", resumen.getTotalCostoProductos(), currencyStyle);
        addRow(sheet, rowNum++, "Gastos Operacionales", resumen.getGastosOperacionales(), currencyStyle);
        addRow(sheet, rowNum++, "Gastos Adquisición (No Venta)", resumen.getGastosAdquisicion(), currencyStyle);
        sheet.createRow(rowNum++); // Spacer
        addRow(sheet, rowNum++, "Utilidad Bruta", resumen.getUtilidadBruta(), currencyStyle);
        addRow(sheet, rowNum++, "Utilidad Neta", resumen.getUtilidadNeta(), currencyStyle);

        Row marginRow = sheet.createRow(rowNum++);
        marginRow.createCell(0).setCellValue("Margen %");
        marginRow.createCell(1).setCellValue(resumen.getMargenPorcentaje() + "%");

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void addRow(Sheet sheet, int rowNum, String label, Long value, CellStyle currencyStyle) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        Cell valCell = row.createCell(1);
        valCell.setCellValue(value != null ? value : 0);
        valCell.setCellStyle(currencyStyle);
    }

    private void createVentasSheet(Sheet sheet, List<ProductSalesDTO> ventas, CellStyle headerStyle,
            CellStyle currencyStyle) {
        String[] headers = { "Ranking", "Producto", "Cantidad Vendida", "Total Ingresos" };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        int rank = 1;
        for (ProductSalesDTO dto : ventas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rank++);
            row.createCell(1).setCellValue(dto.getNombre());
            row.createCell(2).setCellValue(dto.getTotalCantidad());
            Cell incomeCell = row.createCell(3);
            incomeCell.setCellValue(dto.getTotalSubtotal());
            incomeCell.setCellStyle(currencyStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createPerdidasSheet(Sheet sheet, List<ProductLossDTO> perdidas, CellStyle headerStyle,
            CellStyle currencyStyle) {
        String[] headers = { "Producto", "Cantidad Perdida", "Monto Perdida" };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (ProductLossDTO dto : perdidas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getNombre());
            row.createCell(1).setCellValue(dto.getTotalCantidadPerdida());
            Cell lossCell = row.createCell(2);
            lossCell.setCellValue(dto.getTotalPerdida());
            lossCell.setCellStyle(currencyStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
