package bigbade.pingwars.util;

public class SimpleLogger {
    public void info(Object info) {
        System.out.println("[INFO] PingWars: " + info.toString());
    }

    public void error(String error) {
        System.out.println("[ERROR] PingWars: " + error);
    }

    public void error(String error, Throwable thrown) {
        System.out.println("[ERROR] PingWars: " + error);
        System.out.println("[ERROR] Error: " + thrown.getMessage());
        thrown.printStackTrace();
    }
}
