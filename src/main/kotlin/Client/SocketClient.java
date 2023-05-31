package Client;

import Base.JSONObjectInputStream;
import Base.JSONObjectOutputStream;
import Base.Messages.InitialMessage;
import Server.Config;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    Socket socket;
    private ObjectInputStream messageReader;
    private ObjectOutputStream messageWriter;
    public SocketClient(String address, String rawPort, String nickname) throws IOException {
        int port = Integer.parseInt(rawPort);
        socket = new Socket(address, port);
        if (Config.INSTANCE.getSERILIZATION().equals("json")) {
            messageWriter = new JSONObjectOutputStream(socket.getOutputStream());
            messageReader = new JSONObjectInputStream(socket.getInputStream());
        } else {
            messageWriter = new ObjectOutputStream(socket.getOutputStream());
            messageReader = new ObjectInputStream(socket.getInputStream());
        }
        send(new InitialMessage(nickname));
    }

    public Object input() throws IOException, ClassNotFoundException {
        return messageReader.readObject();
    }

    public void send(Serializable obj) throws IOException {
        messageWriter.writeObject(obj);
    }
}
