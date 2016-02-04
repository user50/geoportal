package org.w2fc.geoportal.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.Ignore;
import org.junit.Test;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.domain.OperationStatus;
import org.w2fc.geoportal.ws.aspect.Message;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@Ignore
public class SOAPReportGeneratorTest {

    @Test
    public void testFillDocument() throws Exception {
        Document document = new Document();

        PdfWriter.getInstance(document,
                new FileOutputStream("Report.pdf"));

        document.open();

        Map<String, String> map = new HashMap<String, String>();
        map.put("1", "dsfd");
        map.put("2", "dsdfgfd");
        map.put("3", "ddfgdfsfd");
        Message message = new Message();
        message.setFails(map);

        String ms = new ObjectMapper().writeValueAsString(message);
        OperationStatus operationStatus = new OperationStatus("21", 1L, OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), 1L, ms);

        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("1", "dsfd");
        map1.put("2", "dsdfgfd");
        map1.put("3", "ddfgdfsfd");
        Message message1 = new Message();
        message1.setFails(map1);

        String ms1 = new ObjectMapper().writeValueAsString(message);
        OperationStatus operationStatus1 = new OperationStatus("21", 1L, OperationStatus.Action.CREATE, OperationStatus.Status.FAILURE, new Date(), 1L, ms1);


        new SOAPReportGenerator().fillDocument(document, Arrays.asList(operationStatus, operationStatus1), new GeoUser(), null);

        document.close();
    }
}