package uk.gov.dvla.osg.rpd.json;

import java.lang.reflect.Type;

import com.google.gson.*;

import uk.gov.dvla.osg.vault.data.VaultStock;

public final class EmptyStringAsNullTypeAdapter<T> implements JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        
        try {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject stockTotals = jsonObject.get("stockTotals").getAsJsonObject();
            if (stockTotals.get("test").isJsonPrimitive()) {
                jsonObject.get("stockTotals").getAsJsonObject().remove("test");
            }
            return (T)new Gson().fromJson(jsonObject , VaultStock.class); // default deserialization
        } catch (IllegalStateException e) {
            return null;
        }
    }
}