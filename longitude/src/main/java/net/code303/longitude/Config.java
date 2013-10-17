package net.code303.longitude;

public class Config {
    private String server = "http://4tress.dyndns.org";
    private int port = 8000;
    private String user = "Toni";
    private int updateInterval = 15; // Minutes

    private static Config instance = new Config();

    //Constructor
    private Config()
    {
        // initialize the list of license plates
        this.init();
    }

    // Implements the singleton pattern
    public static Config  getInstance() {
        return instance;
    }

    private void init() {

    }


    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }
}
