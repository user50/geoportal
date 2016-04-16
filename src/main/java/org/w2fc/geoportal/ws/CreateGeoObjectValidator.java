package org.w2fc.geoportal.ws;

import org.w2fc.geoportal.ws.exception.MissingParameterException;
import org.w2fc.geoportal.ws.exception.MissingParameterExtSysId;
import org.w2fc.geoportal.ws.exception.MissingParameterGuid;
import org.w2fc.geoportal.ws.exception.MissingParameterName;
import org.w2fc.geoportal.ws.model.GeometryParameter;

/**
 * @author Yevhen
 */
public class CreateGeoObjectValidator {

    public void validateCreate(GeometryParameter params){
        if(params.getGuid() == null || params.getGuid().equals(""))
            throw new MissingParameterGuid("\"guid\" parameter is required");

        if(params.getExtSysId() == null || params.getExtSysId().equals(""))
            throw new MissingParameterExtSysId("\"extSysId\" parameter is required");

        if(params.getName() == null || params.getName().equals(""))
            throw new MissingParameterName("\"name\" parameter is required");

        if(params.getLayerIds() == null)
            throw new MissingParameterException("\"layers\" parameter is required");
    }

    public void validateUpdate(GeometryParameter params){
        if(params.getGuid() == null || params.getGuid().equals(""))
            throw new MissingParameterGuid("\"guid\" parameter is required");

        if(params.getExtSysId() == null || params.getExtSysId().equals(""))
            throw new MissingParameterExtSysId("\"extSysId\" parameter is required");
    }
}
