package server.client;

import common.configuration.Conf;
import common.file.FilePathHelper;
import common.message.SimpleMessage;
import lombok.Getter;
import lombok.Setter;
import common.command.Command;
import server.ChatServer;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientThread implements Runnable {

    @Getter
    @Setter
    private String name;
    @Getter
    private Socket socket;
    private ChatServer server;
    private Room room;

    public ClientThread(ChatServer server, Socket socket, Room room) {
        this.server = server;
        this.socket = socket;
        this.room = room;
    }

    private void handleMessages(DataInputStream inputStream) {
        while (true) {
            try {
                String message = inputStream.readUTF();
                if (Command.isCommand(message)) {
                    handleCommand(message);
                } else {
                    broadcast(message);
                }
                if (Command.LEAVE_CHAT.equalsCommand(message)) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String message) {
        String messageDate = new SimpleDateFormat(Conf.MESSAGE_DATETIME_FORMAT).format(new Date());
        String outputMessage = messageDate + " [" + name + "]: " + message;
        try {
            Files.write(Paths.get(room.getConversationFile().getPath()), (outputMessage + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        room.getParticipants()
                .forEach(p -> {
                    try {
                        DataOutputStream outputStream = new DataOutputStream(p.getSocket().getOutputStream());
                        if (p.equals(this)) {
                            outputStream.writeUTF( messageDate + " {me}: " + message);
                        } else {
                            outputStream.writeUTF(outputMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void handleCommand(String message) {
        Map<String, String> commandAndParam = Command.splitToCommandAndParam(message);
        String command = commandAndParam.keySet().stream().findFirst().orElse(null);
        String param = commandAndParam.get(command);
        if (Command.HELP.equalsCommand(command)) {
            SimpleMessage.directMessage(this.socket, Command.getAllCommands());
        } else if (Command.NEW_ROOM.equalsCommand(command)) {
            this.server.newRoom(param);
        } else if (Command.JOIN_ROOM.equalsCommand(command)) {
            joinRoom(param);
        } else if (Command.ROOM_LIST.equalsCommand(command)) {
            sendRoomList();
        } else if (Command.LEAVE_CHAT.equalsCommand(command)) {
            leaveChat();
        } else if (Command.AVAILABLE_FILES.equalsCommand(command)) {
            sendFileList();
        } else {
            SimpleMessage.serverMessage(this.socket, "command: '" + command + "' doesn't exist. Type '" + Command.HELP.getCommandString() + "' to get list of commands");
        }
    }

    private void sendFileList() {
        File[] files = new File(FilePathHelper.createDirByRoomName(room.getName())).listFiles();
        String fileList = "Files:\n";
        if (files != null) {
            fileList += Arrays.stream(files)
                    .map(File::getName)
                    .collect(Collectors.joining("\n"));
        }
        SimpleMessage.directMessage(socket, fileList);
    }

    private void leaveChat() {
        try {
            room.getParticipants().remove(this);
            socket.close();
            server.closeClientThread(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRoomList() {
        StringBuilder sb = new StringBuilder();
        server.getRooms()
                .forEach((key, value) -> sb.append("id: ").append(key).append(", name: ").append(value.getName()).append("\n"));
        SimpleMessage.directMessage(this.socket, sb.toString());
    }

    private void joinRoom(String roomId) {
        try {
            room.getParticipants().remove(this);
            Room joiningRoom = server.getRooms().get(Integer.valueOf(roomId));
            this.room = joiningRoom;
            joiningRoom.getParticipants().add(this);
            SimpleMessage.serverMessage(this.socket, "joined " + room.toString());
            SimpleMessage.directMessage(socket, Command.CLIENT_UPDATE_ROOM_NAME.getCommandString() + " " + room.getName()); // updates roomName in ChatClient
        } catch (NullPointerException e) {
            SimpleMessage.serverMessage(this.socket, "room doesn't exist");
        } catch (NumberFormatException e) {
            SimpleMessage.serverMessage(this.socket, "wrong room id");
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("odpalam watek clienta i pisze wiadomosc");
            new DataOutputStream(socket.getOutputStream()).writeUTF("Witaj użytkowniku! Podaj swoje imię i zatwierdź. Po zalogowaniu zostaniesz dołączony do czatu ogólnego.\n");
            System.out.println("czekam na odpowiedz");
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            System.out.println("czytam odpowiedz");
            this.name = inputStream.readUTF();
            this.room.getParticipants().add(this);
            System.out.println("wysylam wiadmosc powitalna");
            SimpleMessage.serverMessage(socket, "Welcome " + name + "! Type: '" + Command.HELP.getCommandString() + "' to get command list.");
            handleMessages(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
