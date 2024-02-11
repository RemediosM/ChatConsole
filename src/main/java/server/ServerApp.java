package server;

import common.configuration.Conf;
import server.file.FileDownloadServer;
import server.file.FileSendServer;

public class ServerApp {
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(Conf.CHAT_SERVER_PORT);
        FileSendServer fileSendServer = new FileSendServer(Conf.FILE_SENDER_SERVER_PORT);
        FileDownloadServer fileDownloadServer = new FileDownloadServer(Conf.FILE_DOWNLOAD_SERVER_PORT);
        new Thread(chatServer).start();
        new Thread(fileSendServer).start();
        new Thread(fileDownloadServer).start();
    }

}
