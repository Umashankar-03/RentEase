package com.RentEase.service;

import com.RentEase.entity.Booking;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PDFService {

    public String generateBookingDetailsPdf(Booking booking) {

        Document document = new Document();
        try {
            String fileName = generatePdfFileName(booking);



            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Create a table with 2 columns to hold the company name and logo
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new int[]{2, 1}); // Adjust the width ratio (2:1)

            // Add the company name to the left cell
            Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
            PdfPCell companyNameCell = new PdfPCell(new Phrase("RentEase", companyFont));
            companyNameCell.setBorder(Rectangle.NO_BORDER);
            companyNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(companyNameCell);

            // Load the logo from the resources folder
            ClassPathResource logoResource = new ClassPathResource("images/logo.png");
            InputStream logoInputStream = logoResource.getInputStream();
            byte[] logoBytes = toByteArray(logoInputStream);
            Image logo = Image.getInstance(logoBytes);
            logo.scaleToFit(100, 100); // Adjust the size of the logo as needed

            // Add the logo to the right cell
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(logoCell);

            // Add the header table to the document
            document.add(headerTable);

            document.add(new Paragraph(" ")); // Add a blank line for spacing

            // Adding title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
            Paragraph title = new Paragraph("Booking Details ", titleFont);

            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            // Create a table with 2 columns
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            // Adding table headers
            addTableHeader(table, "Field");
            addTableHeader(table, "Value");

           // Adding booking details to the table
            addTableRow(table , "Guest Name", booking.getGuestName());
            addTableRow(table ,"Property Name ", booking.getProperty().getPropertyName() );
            addTableRow(table,"Mobile Number", booking.getMobileNumber().toString());
            addTableRow(table ,  "Total Nights " , booking.getTotalNights().toString());
            addTableRow(table , "Booking Date ", booking.getBookingDate().toString());
            addTableRow(table,"Check-in Date", booking.getCheckInDate().toString());
            addTableRow(table, "Check-out Date", booking.getCheckOutDate().toString());
            addTableRow(table , "Check-In Time", booking.getCheckInTime()+" "+booking.getMeridian().getTimePeriod().toString());
//            addTableRow(table , "Meridian", booking.getMeridian().getTimePeriod());
            addTableRow(table , "Price", booking.getProperty().getNightlyPrice().toString());
            addTableRow(table , "Total Price", booking.getTotalPrice().toString());
            addTableRow(table , "Final Price with Tax", booking.getFinalPriceWithTax().toString());

            document.add(table);

            return fileName;

        } catch (Exception e) {
            e.printStackTrace();

        }finally {
            if (document != null){
                document.close();
            }
        }

        return null;

    }

    private String generatePdfFileName(Booking booking) {
        String date = new SimpleDateFormat("yyymmdd").format(new Date());
        return "G:\\AirBnb Project\\Bookingpdf\\Booking_" + date + "_" + booking.getId() + ".pdf";
    }


    private void addTableHeader(PdfPTable table, String headerTitle) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setPhrase(new Phrase(headerTitle, headerFont));
        table.addCell(header);
    }

    private void addTableRow(PdfPTable table, String key, String value) {
        Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        table.addCell(new PdfPCell(new Phrase(key, rowFont)));
        table.addCell(new PdfPCell(new Phrase(value, rowFont)));
    }

    private byte[] toByteArray(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
