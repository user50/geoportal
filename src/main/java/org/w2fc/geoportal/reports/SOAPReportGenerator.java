package org.w2fc.geoportal.reports;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.w2fc.geoportal.domain.OperationStatus;
import org.w2fc.geoportal.ws.aspect.Message;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Yevhen
 */
public class SOAPReportGenerator {

    private SimpleDateFormat format = new SimpleDateFormat("dd:MM:yyyy hh:mm:ss");

    private ObjectMapper objectMapper = new ObjectMapper();

    public Document fillDocument(Document document, List<OperationStatus> list) throws DocumentException, IOException {

        Paragraph title = new Paragraph(Font.BOLD, "Report");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        for (OperationStatus operationStatus : list) {
            addForRequest(document, operationStatus);
        }
        return document;
    }

    private void addForRequest(Document document, OperationStatus operationStatus) throws DocumentException, IOException {
        Message message = objectMapper.readValue(operationStatus.getMessage(), Message.class);

        Paragraph paragraph = new Paragraph("Request " + operationStatus.getAction().name() + " - " + format.format(operationStatus.getDate()) + " - layer : Test");
        paragraph.setSpacingBefore(10);
        paragraph.setSpacingAfter(5);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraph);

        Paragraph paragraph1 = new Paragraph("Success " + message.getSuccessions().size() );
        paragraph1.setSpacingBefore(5);
        paragraph1.setSpacingAfter(10);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraph1);

        PdfPTable table = new PdfPTable(2);
        PdfPCell headGuid = new PdfPCell(new Paragraph("GUID"));
        headGuid.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell headMessage = new PdfPCell(new Paragraph("Message"));
        headMessage.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(headGuid);
        table.addCell(headMessage);

        for (String guid : message.getFails().keySet()) {
            PdfPCell guidCell = new PdfPCell(new Paragraph(guid));
            PdfPCell messageCell = new PdfPCell(new Paragraph(message.getFails().get(guid)));

            table.addCell(guidCell);
            table.addCell(messageCell);

        }
        document.add(table);
    }
}
