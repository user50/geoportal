package org.w2fc.geoportal.ws.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * @author Yevhen
 */
public class DateDeserializer extends JsonDeserializer<Date> {

    private DateAdapter dateAdapter = new DateAdapter();

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String date = jsonParser.getText();
        try {
            return dateAdapter.unmarshal(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse string to Date");
        }
    }
}
