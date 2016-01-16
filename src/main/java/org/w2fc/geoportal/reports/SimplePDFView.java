package org.w2fc.geoportal.reports;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

@Component("SimplePDFView")
public class SimplePDFView extends AbstractPdfView {
    
    public static final String PDF_CALLBACK_IMPLEMENTATION_KEY = "PdfCallback.Implementation";

    
    
    public static interface PdfCallback{
        public void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                HttpServletRequest request, HttpServletResponse response) throws Exception;
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ((PdfCallback) model.get(PDF_CALLBACK_IMPLEMENTATION_KEY)).buildPdfDocument(model, document, writer, request, response);
        
    }

//    protected Document newDocument() {
//        return new Document(PageSize.A2.rotate());
//    }
}
