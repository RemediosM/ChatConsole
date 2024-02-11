package common.command;

import common.configuration.Conf;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public enum Command {
    HELP( "help", "", "get all commands", true),
    LEAVE_CHAT("q", "", "leave the chat", true),
    NEW_ROOM("new_room", "roomName", "create new room named roomName", true),
    ROOM_LIST("room_list", "", "list of rooms", true),
    JOIN_ROOM("join_room", "roomId", "join to room with id = roomId", true),
    SEND_FILE("send_file", "filePath", "send file which path = filePath", true),
    DOWNLOAD_FILE("download_file", "fileName", "download file with name = fileName", true),
    AVAILABLE_FILES("available_files", "", "list of files available to download", true),

    CLIENT_UPDATE_ROOM_NAME("update_room_name", "roomName", "", false);

    @Getter
    private final String commandString;
    @Getter
    private final String paramName;
    @Getter
    private final String description;
    @Getter
    private final boolean isPublic;

    Command(String commandString, String paramName, String description, boolean isPublic) {
        this.commandString = Conf.COMMAND_IDENTIFIER + commandString;
        this.paramName = paramName;
        this.description = description;
        this.isPublic = isPublic;
    }

    public static boolean isCommand(String message) {
        return message.length() >= 2 && Conf.COMMAND_IDENTIFIER.equals(message.substring(0, 2));
    }

    public boolean equalsCommand(String command) {
        return this.getCommandString().equals(command);
    }

    public static Map<String, String> splitToCommandAndParam(String message) {
        String command = "";
        String param = "";
        if (message.contains(" ")) {
            String[] commandParts = message.split(" ", 2);
            command = commandParts[0];
            param = commandParts[1];
        } else {
            command = message;
        }
        return Collections.singletonMap(command, param);
    }

    public static String getAllCommands() {
        return "Availble commands: \n" + Arrays.stream(Command.values())
                .filter(Command::isPublic)
                .map(v -> v + "\n")
                .collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return commandString
                + (paramName.equals("") ? "" : " " + paramName + " ")
                + " - "
                + description;
    }

}
