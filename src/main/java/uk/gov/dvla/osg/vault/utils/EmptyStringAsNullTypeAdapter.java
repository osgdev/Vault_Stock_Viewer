package uk.gov.dvla.osg.vault.utils;

import java.lang.reflect.Type;

import com.google.gson.*;

import uk.gov.dvla.osg.vault.data.VaultStock;

public final class EmptyStringAsNullTypeAdapter<T> implements JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        
        try {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("stockTotals").getAsJsonObject().get("test").getAsString().isEmpty()) {
                jsonObject.get("stockTotals").getAsJsonObject().remove("test");
            }
            return (T)new Gson().fromJson(jsonObject , VaultStock.class); // default deserialization
        } catch (IllegalStateException e) {
            return null;
        }
    }
}