package common.message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SimpleMessage {

    public static void directMessage(Socket socket, String message) {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(message);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void serverMessage(Socket socket, String message) {
        directMessage(socket, "[server]: " + message);
    }

}
