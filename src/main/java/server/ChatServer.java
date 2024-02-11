package server;

import common.configuration.Conf;
import common.logger.Logg;
import common.message.SimpleMessage;
import lombok.Getter;
import server.client.ClientThread;
import server.client.Room;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer implements Runnable {

    @Getter
    private ServerSocket serverSocket;
    @Getter
    private HashMap<Integer, Room> rooms = new HashMap<>();
    private int maxRoomId;
    private final Map<ClientThread, Thread> clients = new ConcurrentHashMap<>();


    public ChatServer(int port) {
        try {
            Room mainRoom = new Room(Conf.MAIN_ROOM_ID, Conf.MAIN_ROOM_NAME);
            rooms.put(mainRoom.getId(), mainRoom);
            maxRoomId = 0;
            serverSocket = new ServerSocket(port);
            Logg.info("Chat server connected on port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Room mainRoom = rooms.get(Conf.MAIN_ROOM_ID);
                ClientThread clientThread = new ClientThread(this, socket, mainRoom);
                Thread newClient = new Thread(clientThread);
                clients.put(clientThread, newClient);
                newClient.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void newRoom(String roomName) {
        maxRoomId++;
        Room newRoom = new Room(maxRoomId, roomName);
        rooms.put(maxRoomId, newRoom);
        rooms.get(Conf.MAIN_ROOM_ID).getParticipants()
                .forEach(p -> SimpleMessage.serverMessage(p.getSocket(), "there is a new room: " + newRoom));
    }

    public void closeClientThread(ClientThread clientThread) {
        Thread thread = clients.get(clientThread);
        clients.remove(clientThread);
        thread.interrupt();
    }

}
