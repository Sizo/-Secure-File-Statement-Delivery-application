package com.capitec.statement.worker.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfGeneratorServiceTest {

    private final PdfGeneratorService pdfGeneratorService = new PdfGeneratorService();

    @Test
    void testGeneratePdf() {
        byte[] pdf = pdfGeneratorService.generatePdf("cust-123", "acc-456", "2023-01");

        assertTrue(pdf.length > 0);
        // PDF magic number is %PDF
        assertTrue(new String(pdf).startsWith("%PDF"));
    }
}