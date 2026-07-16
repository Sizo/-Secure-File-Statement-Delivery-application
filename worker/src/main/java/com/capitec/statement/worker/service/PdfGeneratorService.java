package com.capitec.statement.worker.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

@Service
public class PdfGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(PdfGeneratorService.class);

    public byte[] generatePdf(String customerId, String accountNumber, String statementPeriod) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Customer Statement");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 660);
                contentStream.setLeading(14.5f);
                
                contentStream.showText("Customer ID: " + customerId);
                contentStream.newLine();
                contentStream.showText("Account Number: " + accountNumber);
                contentStream.newLine();
                contentStream.showText("Statement Period: " + statementPeriod);
                contentStream.newLine();
                contentStream.showText("Generated At: " + Instant.now().toString());
                contentStream.newLine();
                contentStream.newLine();
                
                contentStream.showText("Transaction Data:");
                contentStream.newLine();
                contentStream.showText("2023-01-01 - Deposit - $1000.00");
                contentStream.newLine();
                contentStream.showText("2023-01-05 - Withdrawal - $250.00");
                contentStream.newLine();
                contentStream.showText("2023-01-15 - Fee - $15.00");
                contentStream.newLine();
                
                contentStream.newLineAtOffset(0, -300);
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 10);
                contentStream.showText("Capitec Bank Ltd - Secure File Statement Delivery");
                
                contentStream.endText();
            }

            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Failed to generate PDF for customer {}, account {}, period {}", customerId, accountNumber, statementPeriod, e);
            throw new RuntimeException("PDF Generation Failed", e);
        }
    }
}