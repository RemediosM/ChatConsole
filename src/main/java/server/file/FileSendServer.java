package server.file;

import common.configuration.Conf;
import common.logger.Logg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileSendServer implements Runnable {

    private ServerSocket serverSocket;

    public FileSendServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            Logg.info("File server connected on port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(new FileSender(socket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
