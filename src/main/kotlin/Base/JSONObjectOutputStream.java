package Base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class JSONObjectOutputStream extends ObjectOutputStream {
    private final Gson gson = new GsonBuilder().create();
    private ObjectOutputStream outputStream;
    public JSONObjectOutputStream(OutputStream out) throws IOException {
        super();
        outputStream = new ObjectOutputStream(out);
    }

    protected JSONObjectOutputStream() throws IOException, SecurityException {
        super();
    }

    @Override
    protected void writeObjectOverride(Object obj) throws IOException {
        String json = gson.toJson(obj, obj.getClass());
        json = addTypeInformationToJson(json, obj.getClass().getName());
        outputStream.writeUTF(json);
        outputStream.flush();
    }

    private String addTypeInformationToJson(String json, String className) {
        StringBuilder stringBuilder = new StringBuilder(json);
        stringBuilder.insert(1, "@class:\"" + className + "\",");
        return stringBuilder.toString();
    }
}
