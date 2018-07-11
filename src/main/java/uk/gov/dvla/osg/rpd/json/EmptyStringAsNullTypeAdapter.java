package uk.gov.dvla.osg.rpd.json;

import java.lang.reflect.Type;

import com.google.gson.*;

import uk.gov.dvla.osg.vault.data.VaultStock;

/**
 * The Class EmptyStringAsNullTypeAdapter.
 *
 * @param <T> the generic type
 */
public final class EmptyStringAsNullTypeAdapter<T> implements JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        
        try {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            // Extract the stockTotals node
            JsonObject stockTotals = jsonObject.get("stockTotals").getAsJsonObject();
            // If test contains just an empty string then remove it from the json and continue processing
            if (stockTotals.get("test").isJsonPrimitive()) {
                jsonObject.get("stockTotals").getAsJsonObject().remove("test");
            }
            return (T)new Gson().fromJson(jsonObject , VaultStock.class); // default deserialization
        } catch (IllegalStateException e) {
            return null;
        }
    }
}