package server.file;

import common.logger.Logg;
import lombok.AllArgsConstructor;

import java.io.*;
import java.net.Socket;

@AllArgsConstructor
public class FileDownloader implements Runnable {

    private Socket socket;

    public void receiveFile() throws IOException {
        FileOutputStream fileOutputStream = null;
        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        ) {
            Logg.info("Downloading file...");
            String fileName = dataInputStream.readUTF();
            fileOutputStream = new FileOutputStream(fileName);
            byte[] buffer = new byte[2048];
            int read = 0;
            while ((read = dataInputStream.read(buffer, 0, buffer.length)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            socket.close();
            Logg.info("Download finished.");
        }
    }

    @Override
    public void run() {
        try {
            receiveFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
