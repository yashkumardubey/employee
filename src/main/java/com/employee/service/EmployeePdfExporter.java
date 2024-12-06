/*
 * package com.employee.service; import java.awt.Color; import
 * java.io.IOException; import java.util.Date; import java.util.List; import
 * java.text.DateFormat; import java.text.SimpleDateFormat;
 * 
 * import com.employee.entity.Employee; import com.lowagie.text.*; import
 * com.lowagie.text.pdf.*;
 * 
 * import jakarta.servlet.http.HttpServletResponse; public class
 * EmployeePdfExporter {
 * 
 * 
 * private List<Employee> employees;
 * 
 * public EmployeePdfExporter(List<Employee> employees) { this.employees =
 * employees; }
 * 
 * private void writeTableHeader(PdfPTable table) { PdfPCell cell = new
 * PdfPCell(); cell.setBackgroundColor(Color.BLUE); cell.setPadding(5);
 * 
 * Font font = FontFactory.getFont(FontFactory.HELVETICA);
 * font.setColor(Color.WHITE);
 * 
 * cell.setPhrase(new Phrase("Emp Id", font)); table.addCell(cell);
 * 
 * cell.setPhrase(new Phrase("First Name", font)); table.addCell(cell);
 * 
 * cell.setPhrase(new Phrase("Full Name", font)); table.addCell(cell);
 * 
 * cell.setPhrase(new Phrase("DOB", font)); table.addCell(cell);
 * 
 * cell.setPhrase(new Phrase("DOJ", font)); table.addCell(cell);
 * 
 * cell.setPhrase(new Phrase("Salary", font)); table.addCell(cell);
 * 
 * cell.setPhrase(new Phrase("Reports To", font)); table.addCell(cell);
 * 
 * cell.setPhrase(new Phrase("Dept Id", font)); table.addCell(cell);
 * 
 * cell.setPhrase(new Phrase("Rank Id", font)); table.addCell(cell);
 * 
 * cell.setPhrase(new Phrase("Create Date", font)); table.addCell(cell);
 * 
 * cell.setPhrase(new Phrase("Update Date", font)); table.addCell(cell); }
 * 
 * private void writeTableData(PdfPTable table) { for (Employee employee :
 * employees) { table.addCell(employee.getEmpid().toString());
 * table.addCell(employee.getFname());
 * table.addCell(employee.getDob().toString());
 * table.addCell(employee.getDoj().toString()); if(employee.getReportsto()!=
 * null){ table.addCell(employee.getReportsto().toString()); } else {
 * table.addCell("-"); } if(employee.getDeptid() != null) {
 * table.addCell(employee.getDeptid().toString()); } else { table.addCell("-");
 * } table.addCell(employee.getRankid().toString()); //
 * table.addCell(employee.getCreate_date().toString());
 * //table.addCell(employee.getUpdate_date().toString()); } }
 * 
 * public void export(HttpServletResponse response) throws DocumentException,
 * IOException { response.setContentType("application/pdf"); DateFormat
 * dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss"); String
 * currentDateTime = dateFormatter.format(new Date()); String headerKey =
 * "Content-Disposition"; String headerValue =
 * "attachment; filename=Employees_pdf" + currentDateTime + ".pdf";
 * response.setHeader(headerKey, headerValue); Document document = new
 * Document(PageSize.A4); PdfWriter.getInstance(document,
 * response.getOutputStream());
 * 
 * document.open(); Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
 * font.setSize(18); font.setColor(Color.BLUE);
 * 
 * Paragraph p = new Paragraph("List of Employees", font);
 * p.setAlignment(Paragraph.ALIGN_CENTER);
 * 
 * document.add(p);
 * 
 * PdfPTable table = new PdfPTable(11); table.setWidthPercentage(100f);
 * //table.setWidths(new float[] {1.5f, 3.0f, 4.5f, 4.0f, 4.0f, 2.2f, 1.5f,
 * 1.5f, 1.5f, 7.5f, 7.5f}); table.setWidths(new float[] {2.3f, 3.4f, 3.5f,
 * 2.5f, 2.5f, 3.0f, 3.0f, 2.3f, 2.3f, 3.5f, 3.5f}); table.setSpacingBefore(10);
 * 
 * writeTableHeader(table); writeTableData(table);
 * 
 * document.add(table);
 * 
 * document.close();
 * 
 * } }
 * 
 * 
 * import java.io.IOException; import java.text.DateFormat; import
 * java.text.SimpleDateFormat; import java.util.Date; import java.util.List;
 * import org.apache.pdfbox.pdmodel.PDDocument; import
 * org.apache.pdfbox.pdmodel.PDPage; import
 * org.apache.pdfbox.pdmodel.PDPageContentStream; import
 * org.apache.pdfbox.pdmodel.font.PDType1Font; import
 * jakarta.servlet.http.HttpServletResponse; import
 * com.employee.entity.Employee;
 * 
 * public class EmployeePdfExporter {
 * 
 * private List<Employee> employees;
 * 
 * public EmployeePdfExporter(List<Employee> employees) { this.employees =
 * employees; }
 * 
 * public void export(HttpServletResponse response) throws IOException {
 * response.setContentType("application/pdf"); DateFormat dateFormatter = new
 * SimpleDateFormat("yyyy-MM-dd_HH:mm:ss"); String currentDateTime =
 * dateFormatter.format(new Date()); String headerKey = "Content-Disposition";
 * String headerValue = "attachment; filename=Employees_pdf_" + currentDateTime
 * + ".pdf"; response.setHeader(headerKey, headerValue);
 * 
 * try (PDDocument document = new PDDocument()) { PDPage page = new PDPage();
 * document.addPage(page);
 * 
 * try (PDPageContentStream contentStream = new PDPageContentStream(document,
 * page)) { contentStream.beginText();
 * contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
 * contentStream.newLineAtOffset(100, 700);
 * contentStream.showText("List of Employees"); contentStream.endText();
 * 
 * int y = 650; // Starting y-coordinate for content for (Employee employee :
 * employees) { contentStream.beginText();
 * contentStream.setFont(PDType1Font.HELVETICA, 10);
 * contentStream.newLineAtOffset(100, y); contentStream.showText("Emp Id: " +
 * employee.getEmpid() + ", First Name: " + employee.getFname());
 * contentStream.endText(); y -= 20; // Adjust y-coordinate for the next row } }
 * 
 * document.save(response.getOutputStream()); } } }
 */