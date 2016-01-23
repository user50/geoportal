package org.w2fc.geoportal.ws.http;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.w2fc.geoportal.ws.exception.AccessDeniedException;
import org.w2fc.geoportal.ws.exception.GeoPortalException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ErrorHandlerController
{
    @ExceptionHandler(value = AccessDeniedException.class)
    public void accessDenied(HttpServletResponse response, AccessDeniedException e){
        response.setStatus(401);
    }
}
