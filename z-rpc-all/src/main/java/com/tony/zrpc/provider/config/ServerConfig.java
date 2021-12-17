package com.tony.zrpc.provider.config;

/**
 * @author herrhu
 * @date 2021/12/17 19:44
 **/
public class ServerConfig {
    private int port;

    private String host;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "port=" + port +
                ", host='" + host + '\'' +
                '}';
    }
}
