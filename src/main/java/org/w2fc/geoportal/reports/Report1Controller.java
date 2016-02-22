package org.w2fc.geoportal.reports;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.domain.OperationStatus;
import org.w2fc.geoportal.utils.ServiceRegistry;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;


@Controller
@RequestMapping(value = "/report")
public class Report1Controller {
    
    @Autowired
    ServiceRegistry serviceRegistry;

    
    @RequestMapping(value = "/layers.pdf")
    public String listAsPdf(@RequestParam(required = false) final List<Long> ids, Model model) {
        
        model.addAttribute(SimplePDFView.PDF_CALLBACK_IMPLEMENTATION_KEY, new SimplePDFView.PdfCallback() {

            @Override
            public void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                    HttpServletRequest request, HttpServletResponse response) throws Exception {

                String appRoot = request.getSession().getServletContext().getRealPath("/");

                BaseFont baseFont = BaseFont.createFont(appRoot + "/WEB-INF/jasper/fonts/arial.ttf",
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                BaseFont baseBoldFont = BaseFont.createFont(appRoot + "/WEB-INF/jasper/fonts/arialbd.ttf",
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font font12 = new Font(baseFont, 12);
                
                Paragraph title1 = new Paragraph(
                        "Слои проекта\n \"Геопортал Ярославской области\"", 
                        new Font(baseBoldFont, 20));
                title1.setSpacingAfter(30);
                title1.setAlignment("center");
                
                Chapter chapter1 = new Chapter(title1, 1);
                chapter1.setNumberDepth(0);
                
                 List<GeoLayer> resList = serviceRegistry.getLayerDao().listTreeLayers(serviceRegistry.getUserDao().getCurrentGeoUser().getId());
                 
                 
                 for (int i = 0; i < resList.size(); i++) {
                     GeoLayer res = serviceRegistry.getLayerDao().get(resList.get(i).getId());
                     Section section2 = null;
                     PdfPTable t1 = new PdfPTable(2);

                     Paragraph title11 = new Paragraph(res.getName(), new Font(baseBoldFont, 14));
                     section2 = chapter1.addSection(title11);
                     section2.add(t1);

                     t1.setHeaderRows(1);
                     t1.setWidths(new int[]{1, 2});
                     t1.setSpacingBefore(25);
                     t1.setSpacingAfter(25);

                     PdfPCell cell1 = getTableCell("Название", new Font(baseBoldFont, 12));
                     cell1.setBorderWidth(2);
                     cell1.setMinimumHeight(30);
                     cell1.setVerticalAlignment(Element.ALIGN_CENTER);
                     t1.addCell(cell1);
                     PdfPCell cell2 = getTableCell("Значение", new Font(baseBoldFont, 12));
                     cell2.setBorderWidth(2);

                     t1.addCell(cell2);

                     t1.addCell(getTableCell("Идентификатор", font12));
                     t1.addCell(getTableCell(res.getId().toString(), font12));
                     
                     t1.addCell(getTableCell("Название слоя", font12));
                     t1.addCell(getTableCell(res.getName(), font12));
                     
                     t1.addCell(getTableCell("Источник данных", font12));
                     t1.addCell(getTableCell(res.getMetadata().getSourceSpatialData(), font12));
                     
                     t1.addCell(getTableCell("Ссылка источник данных", font12));
                     t1.addCell(getTableCell(res.getMetadata().getSourceSpatialData(), font12));
                     
                     t1.addCell(getTableCell("Период обновления", font12));
                     t1.addCell(getTableCell(res.getMetadata().getUpdateFrequency(), font12));
                     
                     t1.addCell(getTableCell("Владелец", font12));
                     t1.addCell(getTableCell(res.getMetadata().getOwnerName(), font12));
                     
                     t1.addCell(getTableCell("Эл.почта владельца", font12));
                     t1.addCell(getTableCell(res.getMetadata().getOwnerEmail(), font12));
                     
                     t1.addCell(getTableCell("Комментарий", font12));
                     t1.addCell(getTableCell(res.getMetadata().getDescSpatialData(), font12));
                     
                     
//                     StringBuffer sb = new StringBuffer();
//                     List<PortalLayerTag> tags = res.getLayerTags();
//                     for (int j = 0; j < tags.size(); j++) {
//                        sb.append(tags.get(j).getKey()).append(" : ").append(tags.get(j).getValue()).append("\n ");
//                    }
//                     
//                     t1.addCell(getTableCell("Набор тегов", font12));
//                     t1.addCell(getTableCell(sb.toString(), font12));
                 }
                
                document.add(chapter1);
            }
        
            private PdfPCell getTableCell(String string, Font font){
                PdfPCell c1 = new PdfPCell(new Phrase(string, font));
                c1.setPaddingBottom(4f);
                return c1;
            }
        });
        
        return "SimplePDFView";
    }

    @RequestMapping(value = "/{pid}/integrational-service-report.pdf")
    public String pdfReport(@PathVariable final String pid, Model model) {

        model.addAttribute(SimplePDFView.PDF_CALLBACK_IMPLEMENTATION_KEY, new SimplePDFView.PdfCallback() {

            @Override
            public void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {

                String appRoot = request.getSession().getServletContext().getRealPath("/");

                BaseFont baseFont = BaseFont.createFont(appRoot + "/WEB-INF/jasper/fonts/arial.ttf",
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                BaseFont baseBoldFont = BaseFont.createFont(appRoot + "/WEB-INF/jasper/fonts/arialbd.ttf",
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

                List<OperationStatus> operationStatuses = serviceRegistry.getOperationStatusRepository().get(pid);

                if (operationStatuses == null || operationStatuses.isEmpty())
                {
                    Paragraph title1 = new Paragraph(
                            "Нет данных",
                            new Font(baseBoldFont, 15));
                    title1.setSpacingAfter(30);
                    title1.setAlignment("center");

                    document.add(title1);

                    return;
                }

                List<Date> dates = new ArrayList<Date>();
                for (OperationStatus operationStatus : operationStatuses) {
                    dates.add(operationStatus.getDate());
                }
                Collections.sort(dates);

                Paragraph title1 = new Paragraph(
                        "Отчет о результатах использования \n интеграционного сервиса \n" +
                                "для запроса/сессии \n " + pid + " \n" + SOAPReportGenerator.formatter.format(dates.get(0)),
                        new Font(baseBoldFont, 15));
                title1.setSpacingAfter(30);
                title1.setAlignment("center");

                document.add(title1);

                new SOAPReportGenerator().fillDocument(document, operationStatuses, baseFont);
            }
        });

        return "SimplePDFView";
    }


}
