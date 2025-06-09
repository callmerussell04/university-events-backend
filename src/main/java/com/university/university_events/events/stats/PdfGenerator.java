package com.university.university_events.events.stats;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.university.university_events.core.utils.Formatter;
import com.university.university_events.events.stats.EventStatisticsDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class PdfGenerator {

    private static Font FONT_HEADING;
    private static Font FONT_NORMAL;

    static {
        try {
            BaseFont baseFont = BaseFont.createFont("fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            FONT_HEADING = new Font(baseFont, 10, Font.BOLD);
            FONT_NORMAL = new Font(baseFont, 8, Font.NORMAL);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            // Fallback to default font if custom font fails
            FONT_HEADING = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            FONT_NORMAL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
        }
    }
    
    public static byte[] generateEventStatisticsPdf(List<EventStatisticsDto> statistics, Date startDate, Date endDate) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Заголовок
            Paragraph title = new Paragraph("Отчет по мероприятиям за период: " +
                    Formatter.format(startDate) + " - " + Formatter.format(endDate), FONT_HEADING);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Общая статистика
            Paragraph totalEvents = new Paragraph("Общее количество мероприятий: " + statistics.size(), FONT_NORMAL);
            totalEvents.setSpacingAfter(10);
            document.add(totalEvents);

            // Таблица с данными
            PdfPTable table = new PdfPTable(8); 
            table.setWidthPercentage(100);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);
            table.setWidths(new float[]{1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1f, 1f});

            addTableCell(table, "Название", FONT_HEADING);
            addTableCell(table, "Статус", FONT_HEADING);
            addTableCell(table, "Начало", FONT_HEADING);
            addTableCell(table, "Окончание", FONT_HEADING);
            addTableCell(table, "Организатор", FONT_HEADING);
            addTableCell(table, "Место", FONT_HEADING);
            addTableCell(table, "Пригл.", FONT_HEADING);
            addTableCell(table, "Посетили", FONT_HEADING);
            

            // Данные
            for (EventStatisticsDto stat : statistics) {
                addTableCell(table, stat.getName(), FONT_NORMAL);
                addTableCell(table, stat.getStatus(), FONT_NORMAL);
                addTableCell(table, stat.getStartDateTime(), FONT_NORMAL);
                addTableCell(table, stat.getEndDateTime(), FONT_NORMAL);
                addTableCell(table, stat.getOrganizer(), FONT_NORMAL);
                addTableCell(table, stat.getLocationName(), FONT_NORMAL);
                addTableCell(table, String.valueOf(stat.getInvitedCount()), FONT_NORMAL);
                addTableCell(table, String.valueOf(stat.getAttendedCount()), FONT_NORMAL);
            }
            document.add(table);

            document.close();
        } catch (DocumentException e) {
            throw new DocumentException("Ошибка при генерации PDF: " + e.getMessage(), e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }

    private static void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
