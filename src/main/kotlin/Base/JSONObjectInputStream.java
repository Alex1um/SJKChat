package Base;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JSONObjectInputStream extends ObjectInputStream {
    private final Gson gson = new GsonBuilder().create();

    private ObjectInputStream inputStream;
    public JSONObjectInputStream(InputStream in) throws IOException {
        super();
        inputStream = new ObjectInputStream(in);
    }

    protected JSONObjectInputStream() throws IOException, SecurityException {
        super();
    }

    @Override
    protected Object readObjectOverride() throws IOException, ClassNotFoundException {
        String json = inputStream.readUTF();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> map = gson.fromJson(json, type);
        String className = (String) map.get("@class");
        try {
            Class<?> cls = Class.forName(className);
            Object obj = gson.fromJson(json, cls);
            return obj;
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(className + " not found", e);
        }
    }
}