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
import java.util.Date;
import java.util.List;

/**
 * @author Yevhen
 */
public class SOAPReportGenerator {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy hh:mm:ss");

    private ObjectMapper objectMapper = new ObjectMapper();

    public Document fillDocument(Document document, List<OperationStatus> actionStatusList) throws DocumentException, IOException {

        Paragraph title = new Paragraph(Font.BOLD, "Report");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph paragraph = new Paragraph("Success operations");
        paragraph.setSpacingBefore(50);
        paragraph.setSpacingAfter(30);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(2); // 2 columns.
        PdfPCell head1 = new PdfPCell(new Paragraph("Action"));
        head1.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell head2 = new PdfPCell(new Paragraph("Number of success"));
        head2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(head1);
        table.addCell(head2);

        for(OperationStatus.Action action : OperationStatus.Action.values()){
            PdfPCell actionCell = new  PdfPCell(new Paragraph(action.toString()));
            PdfPCell numberOfAction = new PdfPCell(new Paragraph(String.valueOf(number(actionStatusList, action))));
            table.addCell(actionCell);
            table.addCell(numberOfAction);
        }
        document.add(table);

        paragraph = new Paragraph("Failure operations");
        paragraph.setSpacingBefore(50);
        paragraph.setSpacingAfter(30);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table2 = new PdfPTable(4);
        head1 = new PdfPCell(new Paragraph("geoObjectId"));
        head1.setHorizontalAlignment(Element.ALIGN_CENTER);
        head2 = new PdfPCell(new Paragraph("Action"));
        head2.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell head3 = new PdfPCell(new Paragraph("Message"));
        PdfPCell head4 = new PdfPCell(new Paragraph("Date"));
        table2.addCell(head1);
        table2.addCell(head2);
        table2.addCell(head3);
        table2.addCell(head4);

        for(OperationStatus actionStatus : actionStatusList){
            if(actionStatus.getStatus()== OperationStatus.Status.FAILURE){
                PdfPCell id = new PdfPCell(new Paragraph(actionStatus.getGuid().toString()));
                PdfPCell action = new PdfPCell(new Paragraph(actionStatus.getAction().toString()));
                PdfPCell message = new PdfPCell(new Paragraph(actionStatus.getMessage()));
                PdfPCell date = new PdfPCell(new Paragraph(formatter.format(actionStatus.getDate())));
                table2.addCell(id);
                table2.addCell(action);
                table2.addCell(message);
                table2.addCell(date);
            }
        }
        document.add(table2);

        return document;
    }

    private int number(List<OperationStatus> actionStatusList, OperationStatus.Action action){
        int numberOfAction = 0;
        for(OperationStatus actionStatus : actionStatusList){
            if(actionStatus.getStatus()== OperationStatus.Status.SUCCESS && actionStatus.getAction() == action)
                numberOfAction +=1;
        }
        return numberOfAction;
    }

    private void addForRequest(Document document, OperationStatus operationStatus) throws DocumentException, IOException {
        Message message = objectMapper.readValue(operationStatus.getMessage(), Message.class);

        Paragraph paragraph = new Paragraph("Request " + operationStatus.getAction().name() + " - " + formatter.format(operationStatus.getDate()) + " - layer : Test");
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
