package org.w2fc.geoportal.ws.error;

import org.w2fc.geoportal.ws.exception.*;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodeProvider {

    private final int DEFAULT_ERROR_CODE = 11;

    private Map<Class , ErrorCode>  codeMap = new HashMap<Class, ErrorCode>();

    public ErrorCodeProvider() {
        codeMap.put(MissingParameterExtSysId.class, new ErrorCode(1, MissingParameterExtSysId.class, "Не задан параметр extSysId"));
        codeMap.put(MissingParameterName.class, new ErrorCode(2, MissingParameterName.class, "Не задан параметр name"));
        codeMap.put(MissingParameterGuid.class, new ErrorCode(3, MissingParameterGuid.class, "Не задан параметр guid"));
        codeMap.put(UnableToAddToLayerException.class, new ErrorCode(5, UnableToAddToLayerException.class, "Указанный слой не существует"));
        codeMap.put(UnableToParseCoordinates.class, new ErrorCode(6, UnableToParseCoordinates.class, "Неверно введены координаты"));
        codeMap.put(SRSDoesNotExistsException.class, new ErrorCode(7, SRSDoesNotExistsException.class, "Указаная система координат не существует"));
        codeMap.put(OutOfPermissionAreaException.class, new ErrorCode(8, OutOfPermissionAreaException.class, "Запрещено создание/редакт/удаление объектов вне полигона контроля прав."));
        codeMap.put(GeoObjectNotFoundException.class, new ErrorCode(10, GeoObjectNotFoundException.class, "В указанной внешней системе extSysId не найден объект с указанным guid"));
    }

    public int getCode(Throwable exception)
    {
        Class aClass = exception.getClass();
        ErrorCode errorCode = codeMap.get(aClass);

        if (errorCode == null)
            return DEFAULT_ERROR_CODE;

        return errorCode.code;
    }

    public String getMessage(Throwable exception)
    {
        Class aClass = exception.getClass();
        ErrorCode errorCode = codeMap.get(aClass);

        if (errorCode == null)
            return exception.getClass() + " : " + exception.getMessage();

        return errorCode.message;
    }

    private class ErrorCode{
        int code;
        Class clazz;
        String message;

        public ErrorCode(int code, Class clazz, String message) {
            this.code = code;
            this.clazz = clazz;
            this.message = message;
        }
    }
}
