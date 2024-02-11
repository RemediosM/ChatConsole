package server.file;

import common.configuration.Conf;
import common.logger.Logg;
import lombok.AllArgsConstructor;

import java.io.*;
import java.net.Socket;

@AllArgsConstructor
public class FileSender implements Runnable {

    private Socket socket;

    public void sendFile() throws IOException {
        try (DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
             DataInputStream  dataInputStream = new DataInputStream(socket.getInputStream())
        ) {
            Logg.info("Sending file...");
            String filePath = dataInputStream.readUTF();
            File file = new File(filePath);
            byte[] fileBytes = new byte[(int) file.length()];
            try (InputStream fileInputStream = new FileInputStream(file)) {
                outputStream.writeBoolean(true);
                fileInputStream.read(fileBytes, 0, fileBytes.length);
                outputStream.write(fileBytes, 0, fileBytes.length);
            } catch (FileNotFoundException e) {
                outputStream.writeBoolean(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
            Logg.info("Sending finished");
        }
    }

    @Override
    public void run() {
        try {
            sendFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
