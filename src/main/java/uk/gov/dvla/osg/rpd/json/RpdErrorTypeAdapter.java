package uk.gov.dvla.osg.rpd.json;

import java.lang.reflect.Type;

import com.google.gson.*;

import uk.gov.dvla.osg.rpd.error.RpdErrorResponse;

public class RpdErrorTypeAdapter<T> implements JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            // the generalErrors node is always an array of one object, so we extract the object and pass it to 
            // Gson to continue processing.
            JsonObject innerObject = jsonElement.getAsJsonObject()
                                        .get("generalErrors").getAsJsonArray()
                                        .get(0).getAsJsonObject();
            return (T)new Gson().fromJson(innerObject, RpdErrorResponse.class); // default deserialization
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
