package com.employee.service;

import java.io.IOException;
import java.util.List;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.employee.entity.Employee;

import jakarta.servlet.http.HttpServletResponse;



public class EmployeeExcelExporter {
    private XSSFWorkbook workbook;
    private Sheet sheet;
    private List<Employee> listEmployees;

    public EmployeeExcelExporter(List<Employee> listEmployees) {
        this.listEmployees = listEmployees;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Employees");

        Row headerRow = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(headerRow, 0, "Emp Id", style);
        createCell(headerRow, 1, "First Name", style);
        createCell(headerRow, 2, "Full Name", style);
        createCell(headerRow, 3, "DOB", style);
        createCell(headerRow, 4, "DOJ", style);
        createCell(headerRow, 5, "Salary", style);
        createCell(headerRow, 6, "Reports To", style);
        createCell(headerRow, 7, "Dept Id", style);
        createCell(headerRow, 8, "Rank Id", style);
        createCell(headerRow, 9, "Create Date", style);
        createCell(headerRow, 10, "Update Date", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Employee employee : listEmployees) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, employee.getEmpid(), style);
            createCell(row, columnCount++, employee.getFname(), style);
            createCell(row, columnCount++, employee.getDob().toString(), style);
            createCell(row, columnCount++, employee.getDoj().toString(), style);
            createCell(row, columnCount++, employee.getReportsto() != null ? employee.getReportsto().toString() : "-", style);
            createCell(row, columnCount++, employee.getDeptid() != null ? employee.getDeptid().toString() : "-", style);
            createCell(row, columnCount++, employee.getRankid().toString(), style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=employees_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}