package server.client;

import common.configuration.Conf;
import common.file.FilePathHelper;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Room {

    @Getter
    private Integer id;
    @Getter
    private String name;
    @Getter
    private Set<ClientThread> participants = new HashSet<>();
    @Getter
    private File conversationFile;

    public Room(Integer id, String name) {
        this.id = id;
        this.name = name;
        try {
            this.conversationFile = createChatFolderAndHistoryFile(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createChatFolderAndHistoryFile(String roomName) throws IOException {
        File directory = new File(FilePathHelper.createDirByRoomName(roomName));
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File history = new File(directory + Conf.PATH_SLASH + Conf.CONVERSATION_FILE_NAME + Conf.CONVERSATION_FILE_EXTENSION);
        history.createNewFile();
        return history;
    }

    @Override
    public String toString() {
        return "room id: " + id + ", name: " + name;
    }


}
