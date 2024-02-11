package common.logger;

public class Logg {

    private static final String SERVER = "[server ";
    private static final String INFO = "info]: ";
    private static final String WARN = "warning]: ";
    private static final String ERROR = "error]: ";

    public static void info(String message) {
        System.out.println(SERVER + INFO + message);
    }

    public static void warn(String message) {
        System.out.println(SERVER + WARN + message);
    }

    public static void error(String message) {
        System.out.println(SERVER + ERROR + message);
    }

}
