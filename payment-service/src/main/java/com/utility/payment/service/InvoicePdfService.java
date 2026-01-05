package com.utility.payment.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.utility.payment.exception.ApiException;
import com.utility.payment.model.Invoice;
import com.utility.payment.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoicePdfService {

	private final InvoiceRepository invoiceRepository;

	public byte[] generateInvoicePdf(String paymentId) {

		Invoice invoice = invoiceRepository.findByPaymentId(paymentId)
				.orElseThrow(() -> new ApiException("Invoice not found", HttpStatus.NOT_FOUND));

		try {
			Document document = new Document(PageSize.A4, 40, 40, 40, 40);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, out);

			document.open();

			Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
			Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
			Font textFont = new Font(Font.HELVETICA, 11);
			Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

			Paragraph title = new Paragraph("UTILITY BILL INVOICE", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			document.add(new Paragraph(" "));
			document.add(new LineSeparator());
			document.add(new Paragraph(" "));

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

			PdfPTable infoTable = new PdfPTable(2);
			infoTable.setWidthPercentage(100);
			infoTable.setWidths(new float[] { 3f, 3f });

			infoTable.addCell(infoCell("Invoice Number:", boldFont));
			infoTable.addCell(infoCell(invoice.getInvoiceNumber(), textFont));

			infoTable.addCell(infoCell("Invoice Date:", boldFont));
			infoTable.addCell(infoCell(invoice.getInvoiceDate().format(formatter), textFont));

			infoTable.addCell(infoCell("Billing Period:", boldFont));
			infoTable.addCell(
					infoCell(Month.of(invoice.getBillingMonth()).name() + " " + invoice.getBillingYear(), textFont));

			infoTable.addCell(infoCell("Bill ID:", boldFont));
			infoTable.addCell(infoCell(invoice.getBillId(), textFont));

			infoTable.addCell(infoCell("Payment ID:", boldFont));
			infoTable.addCell(infoCell(invoice.getPaymentId(), textFont));

			infoTable.addCell(infoCell("Consumer ID:", boldFont));
			infoTable.addCell(infoCell(invoice.getConsumerId(), textFont));

			document.add(infoTable);
			document.add(new Paragraph(" "));

			PdfPTable amountTable = new PdfPTable(2);
			amountTable.setWidthPercentage(100);
			amountTable.setWidths(new float[] { 4f, 2f });

			addHeader(amountTable, "Description");
			addHeader(amountTable, "Amount (₹)");

			addAmountRow(amountTable, "Energy Charge", invoice.getEnergyCharge());
			addAmountRow(amountTable, "Tax", invoice.getTax());
			addAmountRow(amountTable, "Penalty", invoice.getPenalty());

			PdfPCell totalLabel = new PdfPCell(new Phrase("Total Amount", boldFont));
			totalLabel.setPadding(8);

			PdfPCell totalValue = new PdfPCell(new Phrase(String.format("₹ %.2f", invoice.getTotalAmount()), boldFont));
			totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
			totalValue.setPadding(8);

			amountTable.addCell(totalLabel);
			amountTable.addCell(totalValue);

			document.add(amountTable);

			document.add(new Paragraph(" "));
			document.add(new LineSeparator());
			document.add(new Paragraph(" "));
			Paragraph footer = new Paragraph("This is a system generated invoice. No signature required.", textFont);
			footer.setAlignment(Element.ALIGN_CENTER);
			document.add(footer);

			document.close();
			return out.toByteArray();

		} catch (Exception e) {
			throw new ApiException("Failed to generate invoice PDF", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private PdfPCell infoCell(String text, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setPadding(6);
		return cell;
	}

	private void addHeader(PdfPTable table, String text) {
		Font font = new Font(Font.HELVETICA, 12, Font.BOLD);
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(8);
		cell.setBackgroundColor(new Color(230, 230, 230));
		table.addCell(cell);
	}

	private void addAmountRow(PdfPTable table, String label, double value) {
		PdfPCell labelCell = new PdfPCell(new Phrase(label));
		labelCell.setPadding(8);

		PdfPCell valueCell = new PdfPCell(new Phrase(String.format("₹ %.2f", value)));
		valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		valueCell.setPadding(8);

		table.addCell(labelCell);
		table.addCell(valueCell);
	}
}