package uk.gov.dvla.osg.rpd.json;

import java.lang.reflect.Type;

import com.google.gson.*;

import uk.gov.dvla.osg.rpd.error.RpdErrorResponse;

public class RpdErrorTypeAdapter<T> implements JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject innerObject = jsonElement.getAsJsonObject()
                                        .get("generalErrors").getAsJsonArray()
                                        .get(0).getAsJsonObject();
            return (T)new Gson().fromJson(innerObject, RpdErrorResponse.class); // default deserialization
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
