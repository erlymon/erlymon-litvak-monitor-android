package org.erlymon.litvak.core.model.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by sergey on 6/22/16.
 */
public class UserSerializer implements JsonSerializer<User> {
    @Override
    public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.addProperty("id", src.getId());
        result.addProperty("login", src.getLogin());
        result.addProperty("password", src.getPassword());
        return result;
    }
}
