package org.w2fc.geoportal.ws.http;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.w2fc.geoportal.ws.exception.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class ErrorHandlerController
{
    @ExceptionHandler(value = AccessDeniedException.class)
    public void accessDenied(HttpServletResponse response, AccessDeniedException e){
        response.setStatus(401);
    }

    @ExceptionHandler(value = GeoObjectNotFoundException.class)
    public void geoObjectNotFoundException(HttpServletResponse response, GeoObjectNotFoundException e){
        response.setStatus(404);
    }

    @ExceptionHandler(value = GeocodeException.class)
    public void geocodeException(HttpServletResponse response, GeocodeException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(e.getMessage());
        response.getWriter().flush();
        response.getWriter().close();
    }

    @ExceptionHandler(value = InvalidGeometryException.class)
    public void invalidGeometryException(HttpServletResponse response, InvalidGeometryException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(e.getMessage());
        response.getWriter().flush();
        response.getWriter().close();
    }
}
