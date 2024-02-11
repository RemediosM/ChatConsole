package server.file;

import common.configuration.Conf;
import common.logger.Logg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileDownloadServer implements Runnable {

    private ServerSocket fileSocket;

    public FileDownloadServer(int port) {
        try {
            fileSocket = new ServerSocket(port);
            Logg.info("File server connected on port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket socket = fileSocket.accept();
                new Thread(new FileDownloader(socket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
