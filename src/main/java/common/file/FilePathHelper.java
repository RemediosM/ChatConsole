package common.file;

import common.configuration.Conf;

public class FilePathHelper {

    public static String getFileName(String file) {
        if (file.contains("\\")) {
            return file.substring(file.lastIndexOf("\\"));
        }
        return file;
    }

    public static String createDirByRoomName(String roomName) {
        return Conf.FILES_MAIN_FOLDER + Conf.PATH_SLASH + roomName;
    }

    public static String createFullPath(String roomName, String fileName) {
        return Conf.FILES_MAIN_FOLDER + Conf.PATH_SLASH + roomName + Conf.PATH_SLASH + fileName;
    }


}
