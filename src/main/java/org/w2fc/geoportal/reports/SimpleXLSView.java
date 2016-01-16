package org.w2fc.geoportal.reports;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

@Component("SimpleXLSView")
public class SimpleXLSView extends AbstractExcelView{

public static final String XLS_CALLBACK_IMPLEMENTATION_KEY = "XlsCallback.Implementation";

    
    
    public static interface XlsCallback{
        public void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
                HttpServletResponse response) throws Exception;
    }
    
    @Override
    protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ((XlsCallback) model.get(XLS_CALLBACK_IMPLEMENTATION_KEY)).buildExcelDocument(model, workbook, request, response);        
    }

}
