package org.w2fc.geoportal.ws;

import org.w2fc.geoportal.ws.exception.MissingParameterException;
import org.w2fc.geoportal.ws.model.GeometryParameter;

/**
 * @author Yevhen
 */
public class CreateGeoObjectValidator {

    public void validate(GeometryParameter params){
        if(params.getGuid() == null || params.getGuid().equals(""))
            throw new MissingParameterException("\"guid\" parameter is required");

        if(params.getExtSysId() == null || params.getExtSysId().equals(""))
            throw new MissingParameterException("\"extSysId\" parameter is required");

        if(params.getName() == null || params.getName().equals(""))
            throw new MissingParameterException("\"name\" parameter is required");

        if(params.getLayerId() == null)
            throw new MissingParameterException("\"layerId\" parameter is required");
    }
}
